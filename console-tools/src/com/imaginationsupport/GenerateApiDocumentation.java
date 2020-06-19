package com.imaginationsupport;

import com.imaginationsupport.annotations.*;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.*;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.ReflectionHelper;
import com.imaginationsupport.helpers.YamlHelper;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.FeatureType;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.views.View;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.api.RequestHandler;
import com.imaginationsupport.web.api.data.DashboardData;
import com.imaginationsupport.web.api.request.AvailableRequestInfo;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public class GenerateApiDocumentation
{
	private static final String ARG_USE_V2 = "-v2";
	private static final String ARG_USE_V3 = "-v3";

	private static final String INFO_DESCRIPTION = "[description]";
	private static final String INFO_VERSION = "[version]";
	private static final String INFO_TITLE = "Scenario Explorer API";

	private static final String OBJECT_ID_PATTERN = "^[0-9a-f]{24}$";

	private static final String[] API_OBJECTS_PACKAGES = new String[]{
		"com.imaginationsupport.data",
		"com.imaginationsupport.data.features",
		"com.imaginationsupport.data.tree",
		"com.imaginationsupport.data.api",
		"com.imaginationsupport.data.plugins",
		"com.imaginationsupport.views",
		"com.imaginationsupport.web.api.data",
		"com.imaginationsupport.plugins"
	};

	private static final Logger LOGGER = ConsoleUtil.getConsoleLogger();

	private static final int INVALID_CATEGORY_ID = 1000;

	public static void main( final String[] args )
	{
		try
		{
			if( args.length != 2 )
			{
				showUsage();
				System.exit( 1 );
			}
			else if( args[ 0 ].equals( ARG_USE_V2 ) )
			{
				final String outputFilename = args[ 1 ];

				if( outputFilename.equals( "-" ) )
				{
					LOGGER.info( "Generating Swagger v2..." );
					System.out.println( generateV2() );
				}
				else
				{
					LOGGER.info( String.format( "Generating Swagger v2: %s", outputFilename ) );
					try( final PrintWriter file = new PrintWriter( outputFilename ) )
					{
						file.println( String.format( "# compiled: %s", ImaginationSupportUtil.formatDateTime( LocalDateTime.now() ) ) );
						file.println( generateV2() );
					}
				}

				LOGGER.info( "done." );
			}
			else if( args[ 0 ].equals( ARG_USE_V3 ) )
			{
				final String outputFilename = args[ 1 ];

				if( outputFilename.equals( "-" ) )
				{
					LOGGER.info( "Generating OpenAPI v3..." );
					System.out.println( generateV3() );
				}
				else
				{
					LOGGER.info( String.format( "Generating OpenAPI v3: %s", outputFilename ) );

					try( final PrintWriter file = new PrintWriter( outputFilename ) )
					{
						file.println( String.format( "# compiled: %s", ImaginationSupportUtil.formatDateTime( LocalDateTime.now() ) ) );
						file.println( generateV3() );
					}
				}

				LOGGER.info( "done." );
			}
			else
			{
				showUsage();
				System.exit( 1 );
			}
		}
		catch( final Exception e )
		{
			LOGGER.fatal( ImaginationSupportUtil.formatStackTrace( e ) );
			System.exit( 1 );
		}

		return;
	}

	private static void showUsage()
	{
		System.out.println( "Usage:" );
		System.out.println( String.format( "  %-16sGenerate the Swagger Version 2 format", ARG_USE_V2 + " filename" ) );
		System.out.println( String.format( "  %-16sGenerate the OpenAPI Version 3 format", ARG_USE_V3 + " filename" ) );

		return;
	}

	private static Set< Class< ? > > getApiObjects() throws GeneralScenarioExplorerException
	{
		final SortedSet< Class< ? > > apiObjects = new TreeSet<>( new ReflectionHelper.AlphabeticalApiObjectSorter() );

		final Set< Class< ? > > classesToCheck = new HashSet<>();
		LOGGER.info( "Scanning packages for API objects:" );
		for( final String packageName : API_OBJECTS_PACKAGES )
		{
			final Set< Class< ? > > classesFound = ReflectionHelper.listClassesInPackage( true, packageName );

			LOGGER.info( String.format( "    %d classes found in %-50s", classesFound.size(), packageName ) );

			for( final Class< ? > classFound : classesFound )
			{
				LOGGER.debug( "            " + classFound.getCanonicalName() );
			}

			classesToCheck.addAll( classesFound );
		}

		LOGGER.info( "Searching for RestApiObjectInfo annotations..." );
		for( final Class< ? > classToCheck : classesToCheck )
		{
			final RestApiObjectInfo annotation = classToCheck.getAnnotation( RestApiObjectInfo.class );
			if( annotation != null )
			{
				LOGGER.info( String.format( "    %s = %s", classToCheck.getSimpleName(), annotation.description() ) );
				apiObjects.add( classToCheck );
			}
		}

		return apiObjects;
	}

	private static String generateV2() throws GeneralScenarioExplorerException
	{
		final Set< Class< ? > > apiObjects = getApiObjects();

		final YamlHelper.YamlObject swaggerConfig = new YamlHelper.YamlObject();

		swaggerConfig.add( "swagger", "2.0" );

		swaggerConfig.add( "info", generateV2Info() );

//		swaggerConfig.add( "host", HOST );
//		swaggerConfig.add( "basePath", BASE_PATH );

		swaggerConfig.add( "tags", generateV2Tags( apiObjects ) );

		final YamlHelper.YamlArray schemes = new YamlHelper.YamlArray();
		schemes.add( "https" );
		swaggerConfig.add( "schemes", schemes );

		swaggerConfig.add( "paths", generatePaths() );

//		swaggerConfig.add( "securityDefinitions",);
		// TODO finish!

		swaggerConfig.add( "definitions", generateV2Definitions( apiObjects ) );

		return swaggerConfig.toString();
	}

	private static YamlHelper.YamlObject generateV2Info()
	{
		final YamlHelper.YamlObject info = new YamlHelper.YamlObject();

		info.add( "description", INFO_DESCRIPTION );
		info.add( "version", INFO_VERSION );
		info.add( "title", INFO_TITLE );
//		info.add( "termsOfService", INFO_ );

//		final YamlHelper.YamlObject contact = new YamlHelper.YamlObject();
//		contact.add( "email", INFO_CONTACT_EMAIL );
//		info.add( "contact", contact );

//		info.add( "license",  );
//		info.add( "name", INFO_ );
//		info.add( "url", INFO_ );

		return info;
	}

	private static YamlHelper.YamlArray generateV2Tags( final Set< Class< ? > > apiObjects ) throws GeneralScenarioExplorerException
	{
		final SortedSet< Class< ? > > sortedApiObjects = new TreeSet<>( new ApiObjectSorter() );
		sortedApiObjects.addAll( apiObjects );

		final YamlHelper.YamlArray tags = new YamlHelper.YamlArray();

		for( final Class< ? > apiObjectClass : sortedApiObjects )
		{
			final YamlHelper.YamlObject tag = generateTag( apiObjectClass );
			if( tag != null )
			{
				tags.add( tag );
			}
		}

		return tags;
	}

	private static YamlHelper.YamlObject generateTag( final Class< ? > tagClass ) throws GeneralScenarioExplorerException
	{
		if( tagClass == null )
		{
			throw new NullPointerException( "Tag class cannot be null!" );
		}

		final YamlHelper.YamlObject tag = new YamlHelper.YamlObject();

		final RestApiObjectInfo objectInfo = findClassAnnotation( tagClass, true );

		final String tagName = categoryNameToTagName( objectInfo.tagName() );
		if( tagName == null )
		{
			return null;
		}

		tag.add( "name", tagName );
		tag.add( "description", objectInfo.description() );

		return tag;
	}

	private static YamlHelper.YamlObject generateV2Definitions( final Set< Class< ? > > apiObjects ) throws GeneralScenarioExplorerException
	{
		final YamlHelper.YamlObject definitions = new YamlHelper.YamlObject();

		final SortedSet< Class< ? > > sortedApiObjects = new TreeSet<>( new ApiObjectSorter() );
		sortedApiObjects.addAll( apiObjects );

		for( final Class< ? > apiObjectClass : sortedApiObjects )
		{
			final RestApiObjectInfo objectInfo = findClassAnnotation( apiObjectClass, true );

			final YamlHelper.YamlObject definition = new YamlHelper.YamlObject( new DefinitionFieldSorter() );

			final YamlHelper.YamlArray required = new YamlHelper.YamlArray();
			final YamlHelper.YamlObject properties = new YamlHelper.YamlObject();

			generateV2Definition( definition, required, properties, apiObjectClass, false );

			definition.add( "type", "object" );

			definition.add( "properties", properties );

			if( !required.isEmpty() )
			{
				definition.add( "required", required );
			}

			if( !objectInfo.discriminator().equals( RestApiObjectInfo.NO_DISCRIMINATOR ) )
			{
				definition.add( "discriminator", objectInfo.discriminator() );
			}

			final String definitionName = objectInfo.definitionName().trim();
			if( definitionName.isEmpty() )
			{
				throw new GeneralScenarioExplorerException( String.format( "Definition name cannot be empty: %s", apiObjectClass.getSimpleName() ) );
			}
			else if( Character.isLowerCase( definitionName.charAt( 0 ) ) )
			{
				throw new GeneralScenarioExplorerException( String.format(
					"Definition name must start with a capital letter: %s = %s",
					apiObjectClass.getSimpleName(),
					definitionName ) );
			}

//			LOGGER.info( String.format( "%-30s | %-30s | %s", apiObjectClass.getSimpleName(), objectInfo.definitionName(), definitionName ) );

			definitions.add( definitionName, definition );
		}

		LOGGER.info( String.format( "Generated %d definitions", definitions.size() ) );

		return definitions;
	}

	private static void generateV2Definition(
		final YamlHelper.YamlObject definition,
		final YamlHelper.YamlArray required,
		final YamlHelper.YamlObject properties,
		final Class< ? > definitionClass,
		final boolean isSuperClass ) throws GeneralScenarioExplorerException
	{
		assert definitionClass != null;

		if( definitionClass == Object.class )
		{
			return;
		}

		if( isSuperClass )
		{
			final RestApiObjectInfo objectInfo = findClassAnnotation( definitionClass, false );
			if( objectInfo != null )
			{
				final YamlHelper.YamlArray allOf = new YamlHelper.YamlArray();

				addRef( allOf, definitionClass.getSimpleName() ); // TODO use simple name?
				allOf.add( "properties", properties );
				// TODO this will fail if more than two levels of inheritance

				definition.add( "allOf", allOf );
			}
		}
		else
		{
			// add all the real fields
			for( final Field field : getClassFields( definitionClass ) )
			{
				final RestApiFieldInfo fieldAnnotation = findFieldAnnotation( field );
				if( fieldAnnotation != null )
				{
					final String fieldName = fieldAnnotation.jsonField().equals( RestApiFieldInfo.USE_NAME_OF_MEMBER_VARIABLE )
						? field.getName()
						: fieldAnnotation.jsonField();

					properties.add( fieldName, generateFieldDefinitionDetails( fieldAnnotation.description(), field.getType(), field.getGenericType() ) );

					if( fieldAnnotation.isRequired() )
					{
						required.add( fieldName );
					}
				}
			}

			// add all the pseudo fields
			final RestApiRequestObjectPseudoFields pseudoFieldAnnotations = definitionClass.getAnnotation( RestApiRequestObjectPseudoFields.class );
			if( pseudoFieldAnnotations != null )
			{
				for( final RestApiRequestObjectPseudoFieldInfo pseudoFieldAnnotation : pseudoFieldAnnotations.value() )
				{
					final Class< ? > innerType = pseudoFieldAnnotation.genericInnerType().equals( void.class )
						? null
						: pseudoFieldAnnotation.genericInnerType();

					properties.add( pseudoFieldAnnotation.name(), generateFieldDefinitionDetails( pseudoFieldAnnotation.description(), pseudoFieldAnnotation.rawType(), innerType ) );

					if( pseudoFieldAnnotation.isRequired() )
					{
						required.add( pseudoFieldAnnotation.name() );
					}
				}
			}
		}

		generateV2Definition( definition, required, properties, definitionClass.getSuperclass(), true );

		return;
	}

	private static YamlHelper.YamlObject generateFieldDefinitionDetails(
		final String description,
		final Class< ? > rawType,
		final Type genericType ) throws GeneralScenarioExplorerException
	{
		final Class< ? > innerType;
		if( rawType.isAssignableFrom( Set.class ) )
		{
			if( !( genericType instanceof ParameterizedType ) )
			{
				throw new GeneralScenarioExplorerException( String.format( "%s is not a ParameterizedType", rawType.getTypeName() ) );
			}

			final ParameterizedType parameterizedType = (ParameterizedType)genericType;
			if( parameterizedType.getActualTypeArguments().length != 1 )
			{
				throw new GeneralScenarioExplorerException( "Expected only 1 ParameterizedType item!" );
			}

			innerType = (Class< ? >)parameterizedType.getActualTypeArguments()[ 0 ];
		}
		else if( rawType.isAssignableFrom( Hashtable.class ) )
		{
			if( !( genericType instanceof ParameterizedType ) )
			{
				throw new GeneralScenarioExplorerException( "Invalid HashTable type -- Must be Generic Type!" );
			}

			final ParameterizedType parameterizedType = (ParameterizedType)genericType;
			final Type[] typeArguments = parameterizedType.getActualTypeArguments();
			if( typeArguments.length != 2 )
			{
				throw new GeneralScenarioExplorerException( String.format( "Invalid HashTable type -- Must have two arguments but found %d!", typeArguments.length ) );
			}

			innerType = (Class< ? >)typeArguments[ 1 ];
		}
		else
		{
			innerType = null;
		}

		return generateFieldDefinitionDetails( description, rawType, innerType );
	}

	private static YamlHelper.YamlObject generateFieldDefinitionDetails(
		final String description,
		final Class< ? > rawType,
		final Class< ? > innerType ) throws GeneralScenarioExplorerException
	{
		final YamlHelper.YamlObject details = new YamlHelper.YamlObject();

		if( description != null && !description.trim().isEmpty() )
		{
			details.add( "description", description.trim() );
		}

		if( rawType.equals( String.class ) )
		{
			details.add( "type", "string" );
		}
		else if( rawType.equals( boolean.class ) || rawType.equals( Boolean.class ) )
		{
			details.add( "type", "boolean" );
		}
		else if( rawType.equals( int.class ) || rawType.equals( Integer.class ) )
		{
			details.add( "type", "integer" );
		}
		else if( rawType.equals( LocalDateTime.class ) )
		{
			details.add( "type", "string" );
			details.add( "format", "date-time" );
			details.add( "example", "2018-01-31 13:58:12" );
		}
		else if( rawType.equals( ObjectId.class ) )
		{
			details.add( "type", "string" );
			details.add( "pattern", OBJECT_ID_PATTERN );
		}
		else if( rawType.equals( SNode.class ) )
		{
			assert false; // TODO finish!
		}
		else if( rawType.isAssignableFrom( Set.class ) )
		{
			if( innerType == null )
			{
				throw new GeneralScenarioExplorerException( "Inner type cannot be null!" );
			}

			final YamlHelper.YamlObject itemsDetails = new YamlHelper.YamlObject();
			if( String.class.isAssignableFrom( innerType ) )
			{
				itemsDetails.add( "type", "string" );
			}
			else if( ObjectId.class.isAssignableFrom( innerType ) )
			{
				itemsDetails.add( "type", "string" );
				itemsDetails.add( "pattern", OBJECT_ID_PATTERN );
			}
			else if( Project.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "Project" );
			}
			else if( ProjectTemplate.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "ProjectTemplate" );
			}
			else if( View.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "View" );
			}
			else if( TreeNode.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "TreeNode" );
			}
			else if( Notification.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "Notification" );
			}
			else if( FeatureMap.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "Feature" );
			}
			else if( TimelineEvent.class.isAssignableFrom( innerType ) )
			{
				addRef( itemsDetails, "TimelineEvent" );
			}
			else
			{
				throw new GeneralScenarioExplorerException( String.format( "Unable to handle ParameterizedType inner type: %s", innerType.getSimpleName() ) );
			}

			details.add( "type", "array" );
			details.add( "items", itemsDetails );
		}
		else if( rawType.equals( Hashtable.class ) )
		{
			if( FeatureMap.class.isAssignableFrom( innerType ) )
			{
				final YamlHelper.YamlObject itemsDetails = new YamlHelper.YamlObject();
				addRef( itemsDetails, "Feature" );

				details.add( "type", "array" );
				details.add( "items", itemsDetails );
			}
			else
			{
				throw new GeneralScenarioExplorerException( String.format( "Invalid HashTable type -- Unknown value type: %s", innerType.getTypeName() ) );
			}
		}
		else if( ApiObject.class.isAssignableFrom( rawType ) )
		{
			addRef( details, rawType.getSimpleName() );
		}
		else if( rawType.equals( JSONObject.class ) )
		{
			details.add( "type", "object" );
		}
		else if( rawType.equals( JSONArray.class ) )
		{
			final YamlHelper.YamlObject itemsDetails = new YamlHelper.YamlObject();

			if( ApiObject.class.isAssignableFrom( innerType ) )
			{
				// TODO this shouldn't use the simple name, it should load the annotation and use that name!

				final RestApiObjectInfo innerTypeAnnotation = findClassAnnotation( innerType, true );

				addRef( itemsDetails, innerTypeAnnotation.definitionName() );
			}
			else
			{
				throw new GeneralScenarioExplorerException( String.format( "Unable to handle array type: %s", innerType.getSimpleName() ) );
			}

			details.add( "type", "array" );
			details.add( "items", itemsDetails );
		}
		else if( Enum.class.isAssignableFrom( rawType ) )
		{
			details.add( "type", "string" );
		}
		else
		{
			throw new GeneralScenarioExplorerException( String.format( "Unable to describe field type: %s", rawType.getSimpleName() ) );
		}

		return details;
	}

	private static YamlHelper.YamlObject generatePaths() throws GeneralScenarioExplorerException
	{
		final Map< String, Map< String, YamlHelper.YamlObject > > pathEntries = new TreeMap<>( new RequestMethodHandlerSorter() );
		for( final AvailableRequestInfo availableRequestSetup : RequestHandler.getAvailableRequests() )
		{
			final String path = "/" + String.join( "/", availableRequestSetup.getUriParts() );

			if( !pathEntries.containsKey( path ) )
			{
				pathEntries.put( path, new TreeMap<>( new HttpMethodSorter() ) );
			}

			pathEntries.get( path ).put( availableRequestSetup.getMethod().toString().toLowerCase(), generatePathEntry( availableRequestSetup ) );
		}

		// now that they are grouped, generate the entries
		int totalApiCalls = 0;
		final YamlHelper.YamlObject paths = new YamlHelper.YamlObject();
		for( final String path : pathEntries.keySet() )
		{
			final YamlHelper.YamlObject pathMethods = new YamlHelper.YamlObject();

			for( final String pathMethod : pathEntries.get( path ).keySet() )
			{
				pathMethods.add( pathMethod, pathEntries.get( path ).get( pathMethod ) );
			}

			paths.add( path, pathMethods );
			totalApiCalls += pathMethods.size();
		}

		LOGGER.info( String.format( "Generated %d paths (%s total API calls)", pathEntries.size(), totalApiCalls ) );

		return paths;
	}

	private static YamlHelper.YamlObject generatePathEntry( final AvailableRequestInfo availableRequestSetup ) throws GeneralScenarioExplorerException
	{
		final List< RestApiRequestParameterInfo > parameterAnnotations = availableRequestSetup.getParameters();

		final YamlHelper.YamlObject pathMethod = new YamlHelper.YamlObject();

		final YamlHelper.YamlArray consumes = new YamlHelper.YamlArray();
		consumes.add( availableRequestSetup.getInputMimeType() );

		final YamlHelper.YamlArray produces = new YamlHelper.YamlArray();

		switch( availableRequestSetup.getResponseSchemaType() )
		{
			case JsonArray:
			case JsonObject:
				produces.add( "application/json" );
				break;

			case JavascriptSource:
				produces.add( "application/javascript" );
				break;
		}

		final YamlHelper.YamlArray tags = new YamlHelper.YamlArray();
		tags.add( categoryNameToTagName( availableRequestSetup.getCategory() ) );

		final YamlHelper.YamlArray parameters = new YamlHelper.YamlArray();
		if( parameterAnnotations != null )
		{
			for( final RestApiRequestParameterInfo parameterInfoAnnotation : parameterAnnotations )
			{
				parameters.add( generateParameterEntry( parameterInfoAnnotation ) );
			}
		}

		final YamlHelper.YamlObject responses = new YamlHelper.YamlObject();
		responses.add( 200, generateResponse( "success", availableRequestSetup.getResponseSchemaType(), availableRequestSetup.getResponseSchemaDefinition() ) );
		if( parameterAnnotations != null )
		{
			for( final RestApiRequestParameterInfo parameterInfoAnnotation : parameterAnnotations )
			{
				responses.add( 400, generateResponse( String.format( "Invalid parameter: %s", parameterInfoAnnotation.name() ), null, null ) );
				responses.add( 404, generateResponse( String.format( "Parameter \"%s\" not found", parameterInfoAnnotation.name() ), null, null ) );
			}
		}
		// TODO add responses for bad parameters

		pathMethod.add( "tags", tags );
		pathMethod.add( "summary", availableRequestSetup.getApiSummary() );
		pathMethod.add( "description", availableRequestSetup.getApiDescription() );
		pathMethod.add( "operationId", availableRequestSetup.getApiAction().toString() );
		pathMethod.add( "consumes", consumes );
		pathMethod.add( "produces", produces );
		pathMethod.add( "parameters", parameters );
		pathMethod.add( "responses", responses );
//		pathMethod.add( "security", "" );
		// TODO finish!

		return pathMethod;
	}

	private static YamlHelper.YamlObject generateParameterEntry( final RestApiRequestParameterInfo parameterInfoAnnotation ) throws GeneralScenarioExplorerException
	{
		final YamlHelper.YamlObject parameterInfo = new YamlHelper.YamlObject();

		switch( parameterInfoAnnotation.in() )
		{
			case Path:
				parameterInfo.add( "name", parameterInfoAnnotation.name() );
				parameterInfo.add( "in", "path" );

				switch( parameterInfoAnnotation.type() )
				{

					case String:
						parameterInfo.add( "type", "string" );
						break;

					case ObjectId:
						parameterInfo.add( "type", "string" );
						parameterInfo.add( "pattern", OBJECT_ID_PATTERN );
						break;

					case User:
					case Project:
						parameterInfo.add( "type", parameterInfoAnnotation.type().toString() );
						break;

					default:
						throw new GeneralScenarioExplorerException( String.format( "Unknown parameter type: %s", parameterInfoAnnotation.type() ) );
				}
				break;

			case FormData:
				final YamlHelper.YamlObject schema = new YamlHelper.YamlObject();
				addRef( schema, parameterInfoAnnotation.type().toString() );

				parameterInfo.add( "in", "body" );
				parameterInfo.add( "name", "body" );
				parameterInfo.add( "schema", schema );
				break;

			default:
				throw new GeneralScenarioExplorerException( String.format( "Unknown parameter location: %s", parameterInfoAnnotation.in() ) );
		}

		parameterInfo.add( "description", parameterInfoAnnotation.description() );
		parameterInfo.add( "required", parameterInfoAnnotation.required() );

		return parameterInfo;
	}

	private static YamlHelper.YamlObject generateResponse(
		final String description,
		final RestApiRequestInfo.SchemaType schemaType,
		final RestApiRequestInfo.SchemaDefinition schemaDefinition ) throws GeneralScenarioExplorerException
	{
		final YamlHelper.YamlObject response = new YamlHelper.YamlObject();

		response.add( "description", description );

		if( schemaType != null )
		{
			final YamlHelper.YamlObject schema = new YamlHelper.YamlObject();

			switch( schemaType )
			{
				case JsonObject:
					if( schemaDefinition == RestApiRequestInfo.SchemaDefinition.GenericObject )
					{
						schema.add( "type", "object" );
					}
					else
					{
						addRef( schema, schemaDefinition.toString() );
					}
					break;

				case JsonArray:
					final YamlHelper.YamlObject items = new YamlHelper.YamlObject();
					addRef( items, schemaDefinition.toString() );

					schema.add( "type", "array" );
					schema.add( "items", items );
					break;

				case JavascriptSource:
					schema.add( "type", "string" );
//					schema.add( "example", "... javascript source code ..." );
					break;

				default:
					throw new GeneralScenarioExplorerException( String.format( "Unknown response schema type: %s", schemaType ) );
			}

			response.add( "schema", schema );
		}

		return response;
	}

	private static String generateV3()
	{
		return "..."; // TODO finish!
	}

	private static Set< Field > getClassFields( final Class< ? > classToLookIn )
	{
		final Set< Field > fields = new TreeSet<>( new FieldSorter() );

		fields.addAll( Arrays.asList( classToLookIn.getDeclaredFields() ) );

		if( classToLookIn.getSuperclass() != null )
		{
			fields.addAll( getClassFields( classToLookIn.getSuperclass() ) );
		}

		return fields;
	}

	private static RestApiObjectInfo findClassAnnotation( final Class< ? > classToLookIn, boolean throwExceptionOnFailure ) throws GeneralScenarioExplorerException
	{
		final RestApiObjectInfo annotationFound = classToLookIn.getAnnotation( RestApiObjectInfo.class );

		if( annotationFound == null && throwExceptionOnFailure )
		{
			throw new GeneralScenarioExplorerException( String.format( "Could not find RestApiObjectInfo for class %s", classToLookIn.getSimpleName() ) );
		}

		return annotationFound;
	}

	private static RestApiFieldInfo findFieldAnnotation( final Field fieldToLookIn )
	{
		return fieldToLookIn.getAnnotation( RestApiFieldInfo.class );
	}

	/**
	 * Sorts based on the http methods:  get < post < put < delete < everything else
	 */
	private static class HttpMethodSorter implements Comparator< String >
	{
		public int compare( final String a, final String b )
		{
			return convertToInt( a ).compareTo( convertToInt( b ) );
		}

		/**
		 * Helper function to convert the string version to the int version
		 *
		 * @param stringVersion the string version
		 *
		 * @return the int version
		 */
		private static Integer convertToInt( final String stringVersion )
		{
			if( stringVersion == null )
			{
				return 5;
			}
			else if( stringVersion.toLowerCase().equals( RestApiRequestInfo.HttpMethod.Get.toString().toLowerCase() ) )
			{
				return 1;
			}
			else if( stringVersion.toLowerCase().equals( RestApiRequestInfo.HttpMethod.Post.toString().toLowerCase() ) )
			{
				return 2;
			}
			else if( stringVersion.toLowerCase().equals( RestApiRequestInfo.HttpMethod.Put.toString().toLowerCase() ) )
			{
				return 3;
			}
			else if( stringVersion.toLowerCase().equals( RestApiRequestInfo.HttpMethod.Delete.toString().toLowerCase() ) )
			{
				return 4;
			}
			else
			{
				return 5;
			}
		}
	}

	private static class RequestMethodHandlerSorter implements Comparator< String >
	{
		private static final Map< String, Integer > mCategories = new HashMap<>();

		static
		{
			mCategories.put( ApiStrings.RestApiStrings.User, 0 );
			mCategories.put( ApiStrings.RestApiStrings.Project, 1 );
			mCategories.put( ApiStrings.RestApiStrings.ProjectTemplate, 2 );
			mCategories.put( ApiStrings.RestApiStrings.View, 3 );
			mCategories.put( ApiStrings.RestApiStrings.Feature, 4 );
			mCategories.put( ApiStrings.RestApiStrings.TimelineEvent, 5 );
			mCategories.put( ApiStrings.RestApiStrings.ConditioningEvent, 6 );
			mCategories.put( ApiStrings.RestApiStrings.State, 7 );
			mCategories.put( ApiStrings.RestApiStrings.FeatureType, 8 );
			mCategories.put( ApiStrings.RestApiStrings.Precondition, 9 );
			mCategories.put( ApiStrings.RestApiStrings.OutcomeEffect, 10 );
			mCategories.put( ApiStrings.RestApiStrings.Projector, 11 );

			mCategories.put( ApiStrings.RestApiStrings.Dashboard, 100 );
			mCategories.put( ApiStrings.RestApiStrings.Tree, 101 );
			mCategories.put( ApiStrings.RestApiStrings.Stats, 102 );
			mCategories.put( ApiStrings.RestApiStrings.Export, 103 );
			mCategories.put( ApiStrings.RestApiStrings.Import, 104 );
			mCategories.put( ApiStrings.RestApiStrings.Clone, 105 );
			mCategories.put( ApiStrings.RestApiStrings.FromTemplate, 106 );

			mCategories.put( ApiStrings.RestApiStrings.Src, 200 );
		}

		@Override
		public int compare( final String pathA, final String pathB )
		{
			final String[] splitA = pathA.split( "/" );
			final String[] splitB = pathB.split( "/" );

			int splitIndex = 1;
			while( splitIndex < splitA.length && splitIndex < splitB.length )
			{
				final int categoryA = splitA[ splitIndex ].startsWith( "{" ) && splitA[ splitIndex ].endsWith( "}" )
					? INVALID_CATEGORY_ID - 1
					: mCategories.getOrDefault( splitA[ splitIndex ], INVALID_CATEGORY_ID );
				final int categoryB = splitB[ splitIndex ].startsWith( "{" ) && splitB[ splitIndex ].endsWith( "}" )
					? INVALID_CATEGORY_ID - 1
					: mCategories.getOrDefault( splitB[ splitIndex ], INVALID_CATEGORY_ID );

				if( categoryA == INVALID_CATEGORY_ID )
				{
					LOGGER.error( String.format( "Unknown category in path %s at index %d: %s", pathA, splitIndex, splitA[ splitIndex ] ) );
				}
				if( categoryB == INVALID_CATEGORY_ID )
				{
					LOGGER.error( String.format( "Unknown category in path %s at index %d: %s", pathB, splitIndex, splitB[ splitIndex ] ) );
				}

				assert categoryA != INVALID_CATEGORY_ID;
				assert categoryB != INVALID_CATEGORY_ID;

				if( categoryA != categoryB )
				{
					return Integer.compare( categoryA, categoryB );
				}

				splitIndex++;
			}

			return pathA.compareTo( pathB );
		}
	}

	private static class ApiObjectSorter implements Comparator< Class< ? > >
	{
		@Override
		public int compare( final Class< ? > classA, final Class< ? > classB )
		{
			return Integer.compare( getImportance( classA ), getImportance( classB ) );
		}

		private static int getImportance( final Class< ? > classToCheck )
		{
			if( classToCheck.equals( User.class ) )
			{
				return 0;
			}
			else if( classToCheck.equals( Project.class ) )
			{
				return 1;
			}
			else if( classToCheck.equals( ProjectTemplate.class ) )
			{
				return 2;
			}
			else if( classToCheck.equals( View.class ) )
			{
				return 3;
			}
			else if( classToCheck.equals( Tree.class ) )
			{
				return 4;
			}
			else if( classToCheck.equals( TreeNode.class ) )
			{
				return 5;
			}
			else if( classToCheck.equals( SNode.class ) )
			{
				return 6;
			}
			else if( classToCheck.equals( CNode.class ) )
			{
				return 7;
			}
			else if( classToCheck.equals( ANode.class ) )
			{
				return 8;
			}
			else if( classToCheck.equals( FeatureMap.class ) )
			{
				return 9;
			}
			else if( classToCheck.equals( TimelineEvent.class ) )
			{
				return 10;
			}
			else if( classToCheck.equals( ConditioningEvent.class ) )
			{
				return 11;
			}
			else if( classToCheck.equals( State.class ) )
			{
				return 12;
			}
			else if( classToCheck.equals( Plugin.class ) )
			{
				return 20;
			}
			else if( classToCheck.equals( FeatureType.class ) )
			{
				return 21;
			}
			else if( classToCheck.equals( Precondition.class ) )
			{
				return 22;
			}
			else if( classToCheck.equals( Effect.class ) )
			{
				return 23;
			}
			else if( classToCheck.equals( Projector.class ) )
			{
				return 24;
			}
			else if( classToCheck.equals( DashboardData.class ) )
			{
				return 50;
			}
			else if( classToCheck.equals( ProjectBackup.class ) )
			{
				return 51;
			}
			else if( classToCheck.equals( Notification.class ) )
			{
				return 52;
			}
			else
			{
				LOGGER.error( String.format( "Unknown API object class to sort: %s", classToCheck.getSimpleName() ) );
				assert false;

				return 99;
			}
		}
	}

	/**
	 * Sorts based on the field name
	 */
	private static class FieldSorter implements Comparator< Field >
	{
		@Override
		public int compare( final Field a, final Field b )
		{
			return a.getName().toLowerCase().compareTo( b.getName().toLowerCase() );
		}
	}

	/**
	 * Sorts based on a few specific ones first, then the rest alphabetically
	 */
	private static class DefinitionFieldSorter implements Comparator< String >
	{
		@Override
		public int compare( final String a, final String b )
		{
			final int rankA = getRank( a );
			final int rankB = getRank( b );

			if( rankA == rankB )
			{
				return a.compareTo( b );
			}
			else
			{
				return Integer.compare( rankA, rankB );
			}
		}

		static int getRank( final String value )
		{
			if( value.equals( "id" ) )
			{
				return 0;
			}
			else if( value.equals( "name" ) )
			{
				return 0;
			}
			else
			{
				return 99;
			}
		}
	}

	private static String categoryNameToTagName( final RestApiHandlerInfo.CategoryNames categoryName ) throws GeneralScenarioExplorerException
	{
		switch( categoryName )
		{
			case None:
				return null;

			case User:
				return "users";

			case Project:
				return "projects";

			case View:
				return "views";

			case TimelineEvent:
				return "timeline events";

			case Feature:
				return "features";

			case ConditioningEvent:
				return "conditioning events";

			case State:
				return "states";

			case ProjectTemplate:
				return "projects templates";

			case Dashboard:
				return "dashboard";

			case Plugin:
				return "plugins";

			default:
				assert false;
				throw new GeneralScenarioExplorerException( String.format( "Unknown category: %s", categoryName ) );
		}
	}

	private static void addRef( final YamlHelper.YamlObject owner, final String refName )
	{
		owner.add( "$ref", String.format( "#/definitions/%s", refName ) );

		return;
	}

	private static void addRef( final YamlHelper.YamlArray owner, final String refName )
	{
		owner.add( "$ref", String.format( "#/definitions/%s", refName ) );

		return;
	}
}
