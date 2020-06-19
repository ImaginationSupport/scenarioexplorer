package com.imaginationsupport.views;

import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.data.StateGroup;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.*;

@Entity( value = "View" )
public class SQView extends View
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends View.JsonKeys
	{
		public static final String FeatureMap = "feature";
		public static final String StateGroup = "groupings";
	}

    private String feature=null;
    @NotSaved
    private FeatureMap featureMap=null;

    @Embedded
    private List<StateGroup> groupings=null;

    public SQView(){}

    public SQView(ObjectId projectId, String label, String description, FeatureMap featureMap) {
        super(projectId, label, description);
        this.feature=featureMap.getUid();
        this.featureMap=featureMap;
        groupings= new ArrayList<>();
    }

    public SQView(ObjectId projectId, String label, String description, String featureUid){
        super(projectId,label,description);
        this.feature=featureUid;
        groupings= new ArrayList<>();
    }

	public SQView( final JSONObject source, final Collection< FeatureMap > featureMaps ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		super( source );

		final JSONObject config = JsonHelper.getRequiredParameterJSONObject( source, JsonKeys.Config );
		final String featureMapName = JsonHelper.getRequiredParameterString( config, JsonKeys.FeatureMap );

		for( final FeatureMap featureMap : featureMaps )
		{
			if( featureMap.getUid().equals( featureMapName ) )
			{
				this.feature = featureMap.getUid();
				this.featureMap = featureMap;

				return;
			}
		}

		throw new GeneralScenarioExplorerException( String.format( "Unknown feature: %s", featureMapName ) );
	}

    @Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.toJSON();

		final JSONObject config = new JSONObject();
		JsonHelper.put( config, JsonKeys.FeatureMap, this.feature );

		JsonHelper.put( json, JsonKeys.Config, config );

		return json;
	}

	@Override
	public JSONObject getStatsJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject stats = new JSONObject();

		JsonHelper.put( stats, JsonKeys.StateGroup, groupings );

		return stats;
	}

    public void addStateGroup(StateGroup group){
        groupings.add(group);
        markModified();
    }

    public List<StateGroup> getStateGroupings(){
        return groupings;
    }

    public void setStateGroupings(List<StateGroup> groupings){
        this.groupings=groupings;
        markModified();
    }

    public StateGroup getStateGroupByIndex(int index) throws GeneralScenarioExplorerException
	{
       try {
           return groupings.get(index);
       } catch (Exception e) {
           throw new GeneralScenarioExplorerException("Error getStateGroupByIndex (list has "+groupings.size()+", index was "+index+"): "+e.getMessage());
       }
    }

    public FeatureMap getFeatureMap() throws InvalidDataException
	{
        if(featureMap==null){
            featureMap=ProjectManager.getInstance().getFeature(project,feature);
        }
        return featureMap;
    }

	@Override
	public Set< Notification > generateNotifications()
	{
		return Collections.emptySet();
	}
}
