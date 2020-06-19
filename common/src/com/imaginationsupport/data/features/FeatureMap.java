package com.imaginationsupport.data.features;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.Uid;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import com.imaginationsupport.plugins.Projector;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.PostLoad;
import org.mongodb.morphia.annotations.PrePersist;

import java.util.Set;

@RestApiObjectInfo( definitionName = "Feature", tagName = RestApiHandlerInfo.CategoryNames.Feature, description = "Scenario Explorer Feature" )
public class FeatureMap implements ApiObject, NotificationSource, Comparable< FeatureMap >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Id = "id";
		public static final String Label = "name";
		public static final String Description = "description";
		public static final String FeatureType = "featureType";
		public static final String Config = "config";
		public static final String ProjectorId = "projectorId";
		public static final String ProjectorConfig = "projectorConfig";
	}

	@RestApiFieldInfo( jsonField = JsonKeys.Id, description = "The unique id" )
	private String uid;

	@RestApiFieldInfo( jsonField = JsonKeys.Label, description = "The name of the feature" )
	private String label;

	@RestApiFieldInfo( jsonField = JsonKeys.Description, description = "The description of the feature" )
	private String description;

	//FeatureType derived data
	@NotSaved
	private FeatureType type=null;

	@RestApiFieldInfo( jsonField = JsonKeys.FeatureType, description = "The unique id of the feature type" )
	private String typeName = null;

	@NotSaved
	@RestApiFieldInfo( jsonField = JsonKeys.Config, description = "The JSON config for the feature" )
	private JSONObject typeConfig = null;

	private String typeConfigSaved=null;

	//Projector derived data
	@NotSaved
	private Projector projector=null;

	@RestApiFieldInfo( jsonField = JsonKeys.ProjectorId, description = "The unique id of the projector" )
	private String projectorName = null;

	@NotSaved
	@RestApiFieldInfo( jsonField = JsonKeys.ProjectorConfig, description = "The JSON config of the feature" )
	private JSONObject projectorConfig = null;

	private String projectorConfigSaved=null;

	public FeatureMap (FeatureType type, String label, String description, Class<? extends Projector> projector ) throws ClassNotFoundException, InstantiationException, IllegalAccessException,
		GeneralScenarioExplorerException
	{
		this.uid=Uid.getUid();
		this.label=label;
		this.description=description;
		setType(type);
		if (projector!=null){
			this.projectorName=projector.getName();
			this.projector=getProjectorInstance();
		}
	}

	public FeatureMap (FeatureType type, String label, String description){
		this.uid=Uid.getUid();
		this.label=label;
		this.description=description;
		setType(type);
		this.projectorName=null;
		this.projector=null;
	}

	public FeatureMap( final JSONObject source ) throws InvalidDataException
	{
		this.uid = JsonHelper.getOptionalParameterString( source, JsonKeys.Id );
		if( uid == null )
		{
			uid = Uid.getUid();
		}

		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );

		setType( PlugInManager.getInstance().getFeatureType( JsonHelper.getRequiredParameterString( source, JsonKeys.FeatureType ) ) );

		this.typeConfig = JsonHelper.getRequiredParameterJSONObject( source, JsonKeys.Config );
		getType().setConfig( this.typeConfig );

		final String projectorId = JsonHelper.getOptionalParameterString( source, JsonKeys.ProjectorId );
		if( projectorId == null )
		{
			this.projector = null;
			this.projectorName = null;
			this.projectorConfig = null;
		}
		else
		{
			this.projector = PlugInManager.getInstance().getProjector( projectorId );
			this.projectorName = this.projector.getName();
			this.projectorConfig = JsonHelper.getRequiredParameterJSONObject( source, JsonKeys.ProjectorConfig );
			this.projector.setConfig( this.projectorConfig );
		}

		return;
	}

	public FeatureMap(){
	}

	public String getUid(){
		return uid;
	}

	public Projector getProjector() {
		return projector;
	}

	public String getProjectorName(){
		return projectorName;
	}

	public void setProjector(Class<Projector> projector) {
		if (projector==null)
			this.projectorName="";
		else
			this.projectorName = projector.getCanonicalName();
	}

	public void setType(FeatureType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label=label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description=description;
	}

	public FeatureType getType() {
		return type;
	}

	public Feature getDefaultFeature() throws InvalidDataException
	{
		return new Feature(this);
	}

	public String getFeatureTypeConfig() throws InvalidDataException
	{
		return type.getConfig().toString();
	}

	public void setFeatureTypeConfig( String config ) throws InvalidDataException
	{
		type.setConfig( JsonHelper.parseObject( config ) );
	}

	public JSONObject getProjectorConfig() throws InvalidDataException
	{
		return projector.getConfig();
	}

	public void setProjectorConfig( String config ) throws InvalidDataException
	{
		if( projector != null )
		{
			projector.setConfig( JsonHelper.parseObject( config ) );
		}
	}

	@PrePersist
	public void dehydrate() throws InvalidDataException
	{
		typeName = type.getClass().getCanonicalName();

		typeConfig = type.getConfig();
		typeConfigSaved = typeConfig.toString();

		if( projector == null )
		{
			projectorName = null;
			projectorConfig = null;
			projectorConfigSaved=null;
		}
		else
		{
			projectorName = projector.getClass().getCanonicalName();
			projectorConfig = projector.getConfig();
			projectorConfigSaved=projectorConfig.toString();
		}
	}

	@PostLoad
	public void rehydrate()
	{
		try
		{
			type = getFeatureTypeInstance();

			typeConfig = JsonHelper.parseObject( typeConfigSaved );
			type.setConfig( typeConfig );

			projector = getProjectorInstance();
			if( projector != null )
			{
				projectorConfig = JsonHelper.parseObject( projectorConfigSaved );
				projector.setConfig( projectorConfig );
			}
		}
		catch( final ClassNotFoundException | InstantiationException | IllegalAccessException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			e.printStackTrace();
		}
	}

	private Projector getProjectorInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, GeneralScenarioExplorerException
	{
		if (projectorName==null || projectorName.isEmpty())
			return null;
		Object x=this.getClass().getClassLoader().loadClass(projectorName).newInstance();
		if (!(x instanceof Projector)){
			throw new GeneralScenarioExplorerException("Projector class ("+projectorName+") in Database does not resolve to a Projector.");
		}
		if( projectorConfig != null )
		{
			( (Projector)x ).setConfig( projectorConfig );
		}
		return (Projector) x;
	}

	private FeatureType getFeatureTypeInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, GeneralScenarioExplorerException
	{
		if (typeName==null || typeName.isEmpty())
			return null;
		Object x=this.getClass().getClassLoader().loadClass(typeName).newInstance();
		if (!(x instanceof FeatureType)){
			throw new GeneralScenarioExplorerException("FeatureType class ("+typeName+") in Database does not resolve to a FeatureType Class.");
		}
		if( typeConfig != null )
		{
			( (FeatureType)x ).setConfig( typeConfig );
		}
		return (FeatureType) x;
	}

	@Override
	public JSONObject toJSON()
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Id, uid );
		JsonHelper.put( json, JsonKeys.Label, label );
		JsonHelper.put( json, JsonKeys.Description, description );
		JsonHelper.put( json, JsonKeys.FeatureType, type.getId() );
		JsonHelper.put( json, JsonKeys.ProjectorId, projector == null ? null : projector.getId() );
		JsonHelper.put( json, JsonKeys.Config, typeConfig );
		JsonHelper.put( json, JsonKeys.ProjectorConfig, projectorConfig );

		return json;
	}

	@Override
	public Set< Notification > generateNotifications() throws GeneralScenarioExplorerException, InvalidDataException
	{
		try
		{
			final FeatureType featureType = getFeatureTypeInstance();

			if( featureType == null )
			{
				throw new GeneralScenarioExplorerException( "Feature type is not set!" );
			}
			else
			{
				return featureType.generateNotifications();
			}
		}
		catch( final ClassNotFoundException | InstantiationException | IllegalAccessException e )
		{
			throw new GeneralScenarioExplorerException( "Error getting feature type instance!", e );
		}
	}

	@Override
	@SuppressWarnings( "NullableProblems" )
	public int compareTo( final FeatureMap other )
	{
		return label.toLowerCase().compareTo( other.label.toLowerCase() );
	}
}
