package com.imaginationsupport.data.tree;

import com.imaginationsupport.Database;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.State;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.NotSaved;

import java.time.LocalDateTime;

/**
 * State node
 */
@RestApiObjectInfo(
	definitionName = "StateNode",
	tagName = RestApiHandlerInfo.CategoryNames.None,
	description = "View Tree State Node" )
@RestApiRequestObjectPseudoFields( value = {
	@RestApiRequestObjectPseudoFieldInfo( name = SNode.JsonKeys.Label, rawType = String.class,description = "The name of the node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = SNode.JsonKeys.Start, rawType = LocalDateTime.class,description = "The date and time of the start of the node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = SNode.JsonKeys.End, rawType = LocalDateTime.class,description = "The data and time of the end of the node" )
} )
public class SNode extends TreeNode
{
	public static class JsonKeys extends TreeNode.JsonKeys
	{
		public static final String Label = "name";
		public static final String Start = "start";
		public static final String End = "end";
	}

	@NotSaved
	public static final String PREFIX = "S";

	private ObjectId id;

	public SNode() {}

	public SNode(String in){
		rehydrate(in);
	}

	public SNode(State s){
		this.id=s.getId();
	}

	public State getState() {
		return Database.get(State.class, id);
	}

	public void setState(State s) {
		this.id=s.getId();
	}

	public ObjectId getStateId() {
		return id;
	}

	public void setStateId(ObjectId id) {
		this.id = id;
	}


	@Override
	public String dehydrate() {
		return PREFIX+TreeNode.CFS+id.toString();
	}

	@Override
	public void rehydrate(String in) {
		String[] s=in.split(TreeNode.CFS);
		id=new ObjectId(s[1]);
	}

	@Override
	public boolean isType(String in) {
		return in.startsWith(PREFIX);
	}

	@Override
	public ObjectId getDBId() {
		return id;
	}

	@Override
	public String getTreeId() {
		return getDBId().toHexString();
	}

	@Override
	public boolean equals(TreeNode other) {
		if(other instanceof SNode){
			if(id.equals(((SNode)other).id))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return PREFIX+": "+id;
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.toJSON();

		final State state = getState();
		JsonHelper.put( json, JsonKeys.Start, state.getStart() );
		JsonHelper.put( json, JsonKeys.End, state.getEnd() );
		JsonHelper.put( json, JsonKeys.Label, state.getLabel() );

		return json;
	}
}
