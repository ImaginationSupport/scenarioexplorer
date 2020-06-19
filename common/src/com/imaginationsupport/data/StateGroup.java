package com.imaginationsupport.data;

import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.List;

public class StateGroup implements ApiObject
{
    @SuppressWarnings( "WeakerAccess" )
    public static class JsonKeys
    {
        public static final String Name = "name";
        public static final String Description = "description";
        public static final String Members = "members";
        public static final String Indicators = "indicators";
    }

    private String name="";
    private String description="";

    @Embedded
    private List<ObjectId> members = null;

    @Embedded
    private List<Indicator> indicators = new ArrayList<>();

    public StateGroup(){
    }

    public StateGroup(String name, String description){
        this.name=name;
        this.description=description;
    }

    public void addIndicator(Indicator i){
        if (indicators==null) {
            indicators = new ArrayList<>();
        }
        indicators.add(i);
    }

    public List<Indicator> getIndicators(){
        return indicators;
    }

    public int getIndicatorCount(){
        return indicators.size();
    }

    public Indicator getIndicatorByIndex(int index) throws GeneralScenarioExplorerException
    {
        try {
           return indicators.get(index);
        } catch (Exception e) {
            throw new GeneralScenarioExplorerException("Error getIndicatorByIndex (list has "+indicators.size()+", index was "+index+"): "+e.getMessage());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addMember(ObjectId i){
        if (members==null) {
            members = new ArrayList<>();
        }
        members.add(i);
    }

    public List<ObjectId> getMembers(){
        return members;
    }

    public int getMemberCount(){
        return members.size();
    }

    public ObjectId getMemberByIndex(int index) throws GeneralScenarioExplorerException
    {
        try {
            return members.get(index);
        } catch (Exception e) {
            throw new GeneralScenarioExplorerException("Error getMemberByIndex (list has "+members.size()+", index was "+index+"): "+e.getMessage());
        }
    }

    @Override
    public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
    {
        final JSONObject json = new JSONObject();

        JsonHelper.put( json, JsonKeys.Name, this.name );
        JsonHelper.put( json, JsonKeys.Description, this.description );

        JsonHelper.putObjectIds( json, JsonKeys.Members, this.members );

        JsonHelper.put( json, JsonKeys.Indicators, this.indicators );

        return json;
    }
}
