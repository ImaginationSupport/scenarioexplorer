package com.imaginationsupport.data;

import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.List;

public class Indicator implements ApiObject
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Sensitivity = "sensitivity";
		public static final String Specificity = "specificity";
		public static final String Path = "path";
	}

    @Embedded
    private List<CNode> path;

    private double sensitivity=0.0;
    private double specificity =0.0;

    public Indicator(){
        path=new ArrayList<>();
    }

    public Indicator(List<CNode> path, double sensitivity, double specificity){
        this.path=path;
        this.sensitivity=sensitivity;
        this.specificity =specificity;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(double specificity) {
        this.specificity = specificity;
    }

    public void addToPath(CNode c){
        if (path==null) {
            path = new ArrayList<>();
        }
        path.add(c);
    }

    public List<CNode> getPath(){
        return path;
    }

    public int getPathCount(){
        return path.size();
    }

    public CNode getPathByIndex(int index) throws GeneralScenarioExplorerException
	{
        try {
            return path.get(index);
        } catch (Exception e) {
            throw new GeneralScenarioExplorerException("Error getPathByIndex (list has "+path.size()+", index was "+index+"): "+e.getMessage());
        }
    }

	@Override
	public JSONObject toJSON()
	{
		final JSONObject json = new JSONObject();

		// make sure sensitivity is a valid number (it can be NaN)
		if( Double.isFinite( this.sensitivity ) )
		{
			JsonHelper.put( json, JsonKeys.Sensitivity, this.sensitivity );
		}
		else
		{
			JsonHelper.putNull( json, JsonKeys.Sensitivity );
		}

		// make sure specificity is a valid number (it can be NaN)
		if( Double.isFinite( this.specificity ) )
		{
			JsonHelper.put( json, JsonKeys.Specificity, this.specificity );
		}
		else
		{
			JsonHelper.putNull( json, JsonKeys.Specificity );
		}

		final JSONArray jsonPath = new JSONArray();
		for( final CNode node : this.path )
		{
			final JSONObject pathEntry = new JSONObject();

			JsonHelper.put( pathEntry, "id", node.getCeId() );
			JsonHelper.put( pathEntry, "outcome", node.getOutcome() );

			jsonPath.put( pathEntry );
		}

		JsonHelper.put( json, JsonKeys.Path, jsonPath );

		return json;
	}

    public void setPath(List<CNode> newPath) {
        this.path=newPath;
    }
}

