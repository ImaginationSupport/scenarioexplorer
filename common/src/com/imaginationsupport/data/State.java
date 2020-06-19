package com.imaginationsupport.data;

import com.imaginationsupport.Database;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Projector;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.PostLoad;

import java.time.LocalDateTime;
import java.util.Hashtable;

@Entity
@RestApiObjectInfo( definitionName = "State", tagName = RestApiHandlerInfo.CategoryNames.State, description = "Scenario Explorer State" )
public class State extends Persistent implements ApiObject, Comparable< State >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		private static final String Label = "name";
		private static final String Description = "description";
		private static final String Start = "start";
		private static final String End = "end";
		private static final String Features = "features";
	}

	@NotSaved
	public Project projectObject=null;
	
	@Embedded
	public ObjectId project;

	@NotSaved
	public static final String PROJECT_ID_FIELD="project";

	@RestApiFieldInfo(description = "The date and time of the start of the state")
	public LocalDateTime start;

	@RestApiFieldInfo(description = "The date and time of the end of the state")
	public LocalDateTime end;

	@RestApiFieldInfo(description = "The name of the state")
	public String label;

	@RestApiFieldInfo(description = "The description of the state")
	public String description;

	// TODO need to annotate feature values!

	public double probability;
	public boolean isRange=false;
	public boolean isHistoric=false;
	
	@Embedded
	public ObjectId previous;
	
	@Embedded
	public Hashtable<String,Feature> features= new Hashtable<>();

	@NotSaved
	private static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();
	
	public State(){
	}
	
	public State(Project project, String label, String description, LocalDateTime start, LocalDateTime end, State previous){
		this.project=project.getId();
		this.projectObject=project;
		this.label=label;
		this.description=description;
		this.start=start;
		this.end=end;
		if(previous!=null){
			this.previous=previous.getId();
			populate(project,previous);
		} else {
			populate(project);
		}
		markModified();
	}

	public State( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );

		return;
	}

	
	public Feature getFeature(String featureUid) throws DatastoreException{
		if(features.containsKey(featureUid)){
			return features.get(featureUid);
		} else {
			throw new DatastoreException("Request for feature ("+featureUid+") not in project state.");
		}
	}
	
	public void updateFeature(Feature feature) throws DatastoreException{	
		if(feature==null) throw new DatastoreException("Attempting to store a null feature.");
		if(features.containsKey(feature.getUid())){
			features.remove(feature.getUid());
		} 
		features.put(feature.getUid(), feature);
		markModified();
	}
		
	protected void moveDates(LocalDateTime  start, LocalDateTime  end){
		this.start=start;
		this.end=end;
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

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
		markModified();
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
		markModified();
	}

	public boolean isRange() {
		return isRange;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}

	public void populate(Project project, State previous) {
		if (previous==null) populate(project);

		this.projectObject=project;
		this.probability=previous.getProbability();
		for (FeatureMap map: project.getFeatureMaps()){
			try{
				//NOTE: this block is duplicated in PropagateFeatures.populate...
				Projector projector=map.getProjector();
				Feature f=new Feature(map);
				if (projector!=null){ // if we have a projector then project it
					f.setValue(projector.project(map, previous.getFeature(map.getUid()).getValue(),previous, this));
				} else { // if no projector we just copy the previous value
					f.setValue(previous.getFeature(map.getUid()).getValue());
				}
				updateFeature(f);
			} catch (final DatastoreException | InvalidDataException | GeneralScenarioExplorerException e) {
				System.err.println("ERROR: Failed to project feature in new state ("+map.getUid()+":"+map.getLabel()+": "+e); // TODO should not log to STDOUT...
				e.printStackTrace();
			}
		}
		markModified();
	}
	
	public void populate(Project project) {
		this.projectObject=project;
		this.probability=1.0;
		for (FeatureMap map: project.getFeatureMaps()){
			try{
				updateFeature(map.getDefaultFeature());
			} catch (DatastoreException | InvalidDataException e) {
				System.err.println("ERROR: Failed to project feature in new state ("+map.getUid()+":"+map.getLabel()+": "+e);
				e.printStackTrace();
			}
		}
		markModified();
	}
	
	@PostLoad
	public void reattachFeatures(){
		if(projectObject==null) projectObject=Database.get(Project.class, project);
		for (FeatureMap map: projectObject.getFeatureMaps()){
			Feature f;
			try {
				f = getFeature(map.getUid());
				f.setMap(map);
			} catch (DatastoreException e) {
				// This is an acceptable state in cases where we are adding a new feature
			}	
		}
	}

	public void setProject(Project project) {
		this.projectObject=project;
		this.project=project.getId();
	}

	public ObjectId getProjectId() {
		return project;
	}

	public Project getProject() {
		if (projectObject==null){
			projectObject=Database.get(Project.class,project);
		}
		return projectObject;
	}

	public void removeFeature(FeatureMap map) {
		if(features.containsKey(map.getUid())) {
			features.remove(map.getUid());
			markModified();	
		}
	}
	
	public void setHistoric(boolean historic) {
		isHistoric=historic;
		markModified();
	}
	
	public boolean isHistoric() {
		return isHistoric;
	}


	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
		markModified();
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JsonKeys.Label, label );
		JsonHelper.put( json, JsonKeys.Description, description );
		JsonHelper.put( json, JsonKeys.Start, start );
		JsonHelper.put( json, JsonKeys.End, end );

		final JSONObject featuresJSON = new JSONObject();
		for( final String key : features.keySet() )
		{
			final Feature feature = features.get( key );

			if( feature.getMap() == null )
			{
				// TODO this should never actually get hit, but it does -- I *think* it's when a feature is edited and the id gets changed somehow
				LOGGER.error( String.format( "Feature %s does not exist!", key ) );
			}
			else
			{
				JsonHelper.put( featuresJSON, key, features.get( key ).getValue() );
			}
		}
		JsonHelper.put( json, JsonKeys.Features, featuresJSON );

		return json;
	}

	@Override
	public int compareTo( final State other )
	{
		return start.equals( other.start )
			   ? getId().compareTo( other.getId())
			   : start.compareTo( other.start );
	}
}
