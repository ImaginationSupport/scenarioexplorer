package com.imaginationsupport.views;

import com.imaginationsupport.Database;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;

import java.util.HashSet;
import java.util.Set;

@RestApiObjectInfo( definitionName = "View", tagName = RestApiHandlerInfo.CategoryNames.View, description = "Scenario Explorer View" )
public abstract class View extends Persistent implements ApiObject, NotificationSource, Comparable< View >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String Label = "name";
		public static final String Description = "description";
		public static final String Type = "type";
		public static final String Assigned = "assigned";
		public static final String ProjectId = "projectId";
		public static final String Config = "config";
	}

	public abstract static class ViewTypes
	{
		public static final String Unknown = "UN";
		public static final String FuturesBuilding = "FB";
		public static final String SmartQuery = "SQ";
		public static final String WhatIf = "WI";
		public static final String ExtremeState = "ES";
	}

	@Embedded
	protected ObjectId project;

	private String label="";
	private String description="";
	private String type="";

	@Embedded
	private Set<ObjectId> assigned= new HashSet<>();

	@Embedded
	private Tree tree;

	public View()
	{
		super();
	}

	public View(ObjectId projectId, String label, String description){
		this.project=projectId;
		this.label=label;
		this.description=description;
		this.type=this.getClass().getCanonicalName();
		markModified();
	}

	public View( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.project = JsonHelper.getRequiredParameterObjectId( source, JsonKeys.ProjectId );
		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );
		this.type = JsonHelper.getRequiredParameterString( source, JsonKeys.Type );

		final JSONArray assignedRaw = JsonHelper.getRequiredParameterJSONArray( source, JsonKeys.Assigned );
		for( int i = 0; i < assignedRaw.length(); ++i )
		{
			assigned.add( new ObjectId( assignedRaw.getString( i ) ) );
		}

		markModified();

		return;
	}

	public ObjectId getProject() {
		return project;
	}

	public void setProject(ObjectId projectId){
		this.project=projectId;
		markModified();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		markModified();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		markModified();
	}

	public Tree getTree() {
		return tree;
	}

	public void setTree(Tree tree) {
		this.tree = tree;
		markModified();
	}

	public String getType() {
		return type;
	}

//	public String toString(){
//		return tree.toString();
//	}

//	public JSONObject toJSON(){
//		JSONObject jo=new JSONObject();
//		jo.put("id", getId());
//		jo.put("mission", mission);
//		jo.put("label", label);
//		jo.put("description", description);
//		jo.put("root", tree.getRoot().getTreeId());
//		jo.put("tree",tree.toJSON());
//		JSONArray ja=new JSONArray();
//		for (ObjectId ceid:assigned){
//			ConditioningEvent ce=Database.get(ConditioningEvent.class,ceid);
//			ja.put(ce.toJSON());
//			//JSONObject j2=new JSONObject();
//			//j2.put("DBid",ce.toHexString());
//			//ja.put(j2);
//		}
//		jo.put("assigned",ja);
//		return jo;
//	}

	public void assign(ConditioningEvent event){
		assignConditioningEvent(event.getId());
		markModified();
	}

	public void assignConditioningEvent(ObjectId eventId){
		assigned.add(eventId);
		markModified();
	}

	public void unassign(ConditioningEvent event){
		unassignConditioningEvent(event.getId());
		markModified();
	}

	public void unassignConditioningEvent(ObjectId eventId){
		if(assigned.contains(eventId)) {
			assigned.remove(eventId);
			markModified();

			ConditioningEvent e=Database.get(ConditioningEvent.class, eventId);
			if(e!=null && e.getOriginViewId().equals(this.getId())){
				// TODO finish!
			}
		}
	}

	public Set< ObjectId > getAssigned() {
		// TODO remove this or getAssignedConditioningEventIds
		return assigned;
	}

	public boolean isAssigned(ConditioningEvent event) {
		return isAssigned(event.getId());
	}

	public boolean isAssigned(ObjectId eventId) {
		return assigned.contains(eventId);
	}

	public Set<ObjectId> getAssignedConditioningEventIds(){
		return assigned;
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		if( this instanceof FBView )
		{
			JsonHelper.put( json, JsonKeys.Type, ViewTypes.FuturesBuilding );
		}
		else if( this instanceof SQView )
		{
			JsonHelper.put( json, JsonKeys.Type, ViewTypes.SmartQuery );
		}
		else
		{
			JsonHelper.put( json, JsonKeys.Type, ViewTypes.Unknown );
		}

		JsonHelper.put( json, JsonKeys.Label, this.label );
		JsonHelper.put( json, JsonKeys.Description, this.description );
		JsonHelper.put( json, JsonKeys.ProjectId, this.project );
		JsonHelper.putObjectIds( json, JsonKeys.Assigned, this.assigned );
		JsonHelper.put( json, JsonKeys.Config, new JSONObject() );

		return json;
	}

	public static View fromJSON( final JSONObject source, final Project project ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final String rawType = JsonHelper.getRequiredParameterString( source, JsonKeys.Type );
		switch( rawType )
		{
			case ViewTypes.FuturesBuilding:
				return new FBView( source );

			case ViewTypes.SmartQuery:
				return new SQView( source, project.getFeatureMaps() );

			case ViewTypes.ExtremeState:
				throw new InvalidDataException( "Extreme State View not available" );

			case ViewTypes.WhatIf:
				throw new InvalidDataException( "What-If View not available" );

			case ViewTypes.Unknown:
			default:
				throw new InvalidDataException( String.format( "Unknown view type: %s", rawType ) );
		}
	}

	/**
	 * Gets the stats object for the view
	 *
	 * @return the stats object for the view
	 */
	public abstract JSONObject getStatsJSON() throws InvalidDataException, GeneralScenarioExplorerException;

	@Override
	public int compareTo( final View other )
	{
		return this.label.equals(other.label)
			? this.getId().compareTo(other.getId())
			: this.label.compareTo( other.label );
	}
}
