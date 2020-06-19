package com.imaginationsupport.data;

import com.imaginationsupport.Database;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

public abstract class Persistent
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Id = "id";
		public static final String Type = "type";
	}

	public Persistent()
	{
	}

	public Persistent( final JSONObject source ) throws InvalidDataException
	{
		this._id = JsonHelper.getRequiredParameterObjectId( source, JsonKeys.Id, true );

		return;
	}

	@Id
	@RestApiFieldInfo( jsonField = "id", description = "The unique id", isRequired = false )
	private ObjectId _id;

	public ObjectId getId() {
		return _id;
	}

	public void setId(ObjectId id) {
		this._id = id;
		markModified();
	}

	@NotSaved
	private boolean modified=false;

	public boolean isModified(){
		return modified;
	}

	protected void markModified(){
		modified=true;
	}

	public void save(){
		if(modified)
			Database.save(this);
		modified=false;
	}

	protected JSONObject getBaseJson()
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Id, _id );

		return json;
	}
}
