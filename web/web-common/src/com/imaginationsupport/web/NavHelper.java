package com.imaginationsupport.web;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.View;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NavHelper
{
	private static final SortedSet< NavEntry > NAV_ENTRIES = new TreeSet<>();

	private static final String PROJECT_NAME_TEMPLATE_MARKER = "__project_name__";
	private static final String VIEW_NAME_TEMPLATE_MARKER = "__view_name__";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	static
	{
		// index
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_INDEX,
			"Home",
			new String[]{},
			null ) );

		// create project
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_BASIC,
			"Create Project",
			new String[]{},
			SiteJspNames.PAGE_URI_INDEX ) );

		// project details
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_PROJECT,
			"Project: " + PROJECT_NAME_TEMPLATE_MARKER,
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_INDEX ) );

		// update project
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_BASIC,
			"Details",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_FEATURES,
			"Features",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_TIMELINE_EVENTS,
			"Timeline Events",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );
//		NAV_ENTRIES.add( new NavEntry(
//			PAGE_URI_CREATE_UPDATE_PROJECT_HISTORICAL_DATASETS,
//			"Historical Datasets",
//			new String[]{ ApiStrings.JsonKeys.Project },
//			PAGE_URI_PROJECT ) );
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_ACCESS,
			"Access",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );

		// delete project
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_DELETE_PROJECT,
			"Delete",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );

		// create view
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_VIEW,
			"Create View",
			new String[]{ ApiStrings.JsonKeys.Project },
			SiteJspNames.PAGE_URI_PROJECT ) );

		// view
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_VIEW,
			"View: " + VIEW_NAME_TEMPLATE_MARKER,
			new String[]{ ApiStrings.JsonKeys.Project, ApiStrings.JsonKeys.View },
			SiteJspNames.PAGE_URI_PROJECT ) );

		// update view
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_VIEW,
			"Basic Details",
			new String[]{ ApiStrings.JsonKeys.Project, ApiStrings.JsonKeys.View },
			SiteJspNames.PAGE_URI_VIEW ) );

		// edit timeline event (return to view)
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_PROJECT_TIMELINE_EVENTS,
			"Edit Timeline Event",
			new String[]{ ApiStrings.JsonKeys.Project, ApiStrings.JsonKeys.View, ApiStrings.JsonKeys.TimelineEvent },
			SiteJspNames.PAGE_URI_VIEW ) );

		// new conditioning event (return to view)
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_CONDITIONING_EVENTS,
			"New Conditioning Event",
			new String[]{ ApiStrings.JsonKeys.Project, ApiStrings.JsonKeys.View },
			SiteJspNames.PAGE_URI_VIEW ) );

		// edit conditioning event (return to view)
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_CREATE_UPDATE_CONDITIONING_EVENTS,
			"Edit Conditioning Event",
			new String[]{ ApiStrings.JsonKeys.Project, ApiStrings.JsonKeys.View, ApiStrings.JsonKeys.ConditioningEvent },
			SiteJspNames.PAGE_URI_VIEW ) );

		// preferences
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_PREFERENCES,
			"Preferences",
			new String[]{},
			SiteJspNames.PAGE_URI_INDEX ) );

		// about
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_ABOUT,
			"About",
			new String[]{},
			SiteJspNames.PAGE_URI_INDEX ) );

		// help root
		NAV_ENTRIES.add( new NavEntry(
			SiteJspNames.PAGE_URI_HELP_ROOT,
			"Help",
			new String[]{},
			SiteJspNames.PAGE_URI_INDEX ) );

		// debugging...
//		LOGGER.debug( "Nav entries:" );
//		for( final NavEntry entry : NAV_ENTRIES )
//		{
//			LOGGER.debug( String.format( "%-30s | %-20s", entry.getUri(), entry.getPrettyName() ) );
//		}
	}

	/**
	 * Private Constructor since this class should never be instantiated!
	 */
	private NavHelper()
	{
		return;
	}

	private static class NavEntry implements Comparable< NavEntry >
	{
		final private String mUri;
		final private String mPrettyName;
		final private Set< String > mParameterNames;
		final private String mParentUri;

		NavEntry( final String uri, final String prettyName, final String[] parameterNames, final String parentUri )
		{
			mUri = uri;
			mPrettyName = prettyName;
			mParameterNames = new HashSet<>( Arrays.asList( parameterNames ) );
			mParentUri = parentUri;

			return;
		}

		public String getUri()
		{
			return mUri;
		}

		String getPrettyName()
		{
			return mPrettyName;
		}

		Set< String > getParameterNames()
		{
			return mParameterNames;
		}

		String getParentUri()
		{
			return mParentUri;
		}

		@Override
		public int hashCode()
		{
			return new HashCodeBuilder( 17, 31 )
				.append( mUri )
				.append( mParameterNames )
				.toHashCode();
		}

		@Override
		public boolean equals( final Object otherRaw )
		{
			if( !( otherRaw instanceof NavEntry ) )
			{
				return false;
			}
			if( otherRaw == this )
			{
				return true;
			}

			final NavEntry other = (NavEntry)otherRaw;
			return new EqualsBuilder()
				.append( mUri, other.mUri )
				.append( mParameterNames, other.mParameterNames )
				.isEquals();
		}

		@SuppressWarnings( "NullableProblems" )
		@Override
		public int compareTo( final NavEntry other )
		{
			if( other == null )
			{
				return 0;
			}

			return mUri.equals( other.mUri )
				? 0 - Integer.compare( mParameterNames.size(), other.mParameterNames.size() )
				: mUri.compareTo( other.mUri );
		}
	}

	public static class BreadCrumb
	{
		final String mPrettyName;
		final String mUri;
		final boolean mActive;

		BreadCrumb( final String prettyName, final String uri, final boolean active )
		{
			mPrettyName = prettyName;
			mUri = uri;
			mActive = active;

			return;
		}

		public String getPrettyName()
		{
			return mPrettyName;
		}

		public String getUri()
		{
			return mUri;
		}

		public boolean isActive()
		{
			return mActive;
		}
	}

	public static List< BreadCrumb > getBreadCrumbs( final HttpServletRequest request ) throws GeneralScenarioExplorerException
	{
		return getBreadCrumbs( request.getServletPath(), request.getQueryString() );
	}

	private static List< BreadCrumb > getBreadCrumbs( final String jspServletPath, final String queryString ) throws GeneralScenarioExplorerException
	{
		if( jspServletPath == null || jspServletPath.isEmpty() )
		{
			return Collections.singletonList( new BreadCrumb( "Home", SiteJspNames.PAGE_URI_INDEX, true ) );
		}

		try
		{
			// find the nav entry
			final String jsp = jspServletPath.substring( 1 );

//			LOGGER.trace( String.format( "uri: [%s]", jspServletPath ) );
//			LOGGER.trace( String.format( "query string: [%s]", queryString ) );

			final Map< String, String > parameters = new HashMap<>();
			final URI uriToParse = queryString == null
				? new URI( jspServletPath )
				: new URI( jspServletPath + "?" + queryString );
			for( final NameValuePair entry : URLEncodedUtils.parse( uriToParse, StandardCharsets.UTF_8 ) )
			{
//				LOGGER.trace( String.format( "uri parameter: [%s]=[%s]", entry.getName(), entry.getValue() ) );
				parameters.put( entry.getName(), entry.getValue() );
			}

			final NavEntry navEntry = findEntry( jsp, parameters.keySet() );
			if( navEntry != null )
			{

				return getBreadCrumbsWalker( navEntry, parameters );
			}
			else
			{
				return Collections.singletonList( new BreadCrumb( "Home", SiteJspNames.PAGE_URI_INDEX, false ) );
			}
		}
		catch( final URISyntaxException e )
		{
			LOGGER.error( "Error parsing URI: ", e );
			throw new GeneralScenarioExplorerException( "Error parsing URI!", e );
		}
	}

	private static List< BreadCrumb > getBreadCrumbsWalker( final NavEntry entry, final Map< String, String > parameters )
	{
		final List< BreadCrumb > breadcrumbs = new ArrayList<>();

		breadcrumbs.add( new BreadCrumb( fillInTemplateParameters( entry.getPrettyName(), parameters ), generateBreadCrumbUri( entry, parameters ), true ) );

		String workingEntryUri = entry.getParentUri();
		while( workingEntryUri != null )
		{
			final NavEntry workingEntry = findEntry( workingEntryUri, parameters.keySet() );
			if( workingEntry == null )
			{
				LOGGER.error( String.format( "Could not find nav entry: %s", workingEntryUri ) );
				return breadcrumbs;
			}

			breadcrumbs.add(
				0,
				new BreadCrumb(
					fillInTemplateParameters( workingEntry.getPrettyName(), parameters ),
					generateBreadCrumbUri( workingEntry, parameters ),
					false ) );

			workingEntryUri = workingEntry.getParentUri();
		}

		return breadcrumbs;
	}

	private static NavEntry findEntry( final String uri, final Set< String > uriParameterNames )
	{
		LOGGER.trace( "===========================================================================" );
		LOGGER.trace( String.format( "Looking for: %-40s | %-30s (%d)", uri, String.join( ",", uriParameterNames ), uriParameterNames.size() ) );

		for( final NavEntry navEntry : NAV_ENTRIES )
		{
			final boolean match = navEntry.getUri().equals( uri )
				&& navEntry.getParameterNames().size() <= uriParameterNames.size()
				&& uriParameterNames.containsAll( navEntry.getParameterNames() );

			LOGGER.trace( String.format(
				"Looking at:  %-40s | %-30s (%d) | %-30s | %s",
				navEntry.getUri(),
				String.join( ",", navEntry.getParameterNames() ),
				navEntry.getParameterNames().size(),
				navEntry.getPrettyName(),
				match ? "match!" : "" ) );

			if( match )
			{
				LOGGER.trace( "" );
				return navEntry;
			}
		}

		return null;
	}

	private static String fillInTemplateParameters( final String template, final Map< String, String > parameters )
	{
		String working = template;
		try( final API api = new API() )
		{
			if( parameters.containsKey( ApiStrings.JsonKeys.Project ) )
			{
				final Project project = api.findProject( new ObjectId( parameters.get( ApiStrings.JsonKeys.Project ) ), true );

				working = working.replaceAll( PROJECT_NAME_TEMPLATE_MARKER, project.getName() );
			}

			if( parameters.containsKey( ApiStrings.JsonKeys.View ) )
			{
				final View view = api.findView(
					new ObjectId( parameters.get( ApiStrings.JsonKeys.Project ) ),
					new ObjectId( parameters.get( ApiStrings.JsonKeys.View ) ),
					true );

				working = working.replaceAll( VIEW_NAME_TEMPLATE_MARKER, view.getLabel() );
			}
		}
		catch( final InvalidDataException e )
		{
			LOGGER.error( "Error filling in bread crumb parameters", e );
			working = working.replaceAll( PROJECT_NAME_TEMPLATE_MARKER, "Unknown" );
		}

		return working;
	}

	private static String generateBreadCrumbUri( final NavEntry entry, final Map< String, String > parameters )
	{
		final List< NameValuePair > queryStringEntries = new ArrayList<>();
		for( final String parameter : entry.getParameterNames() )
		{
			if( parameters.containsKey( parameter ) )
			{
				queryStringEntries.add( new BasicNameValuePair( parameter, parameters.get( parameter ) ) );
			}
			else
			{
				LOGGER.error( String.format( "Missing required parameter \"%s\" for URI: %s = %s", parameter, entry.getPrettyName(), entry.getUri() ) );
			}
		}

		return entry.getUri()
			+ ( queryStringEntries.isEmpty() ? "" : ( "?" + URLEncodedUtils.format( queryStringEntries, StandardCharsets.UTF_8 ) ) );
	}
}
