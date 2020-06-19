package com.imaginationsupport.data.tree;

import com.imaginationsupport.Database;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * Conditioning event (and outcome selected) node
 */
@RestApiObjectInfo(
	definitionName = "ConditioningEventNode",
	tagName = RestApiHandlerInfo.CategoryNames.None,
	description = "View Tree Conditioning Event Node" )
@RestApiRequestObjectPseudoFields( value = {
	@RestApiRequestObjectPseudoFieldInfo( name = CNode.JsonKeys.Outcome, rawType = int.class, description = "The outcome index that was used" ),
	@RestApiRequestObjectPseudoFieldInfo( name = CNode.JsonKeys.Label, rawType = String.class, description = "The name of the node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = CNode.JsonKeys.Description, rawType = String.class, description = "The description of the node" )
} )
public class CNode extends TreeNode
{
	public static class JsonKeys extends TreeNode.JsonKeys
	{
		public static final String Outcome = "outcome";
		public static final String Label = "name";
		public static final String Description = "description";
	}

	@NotSaved
	public static final String PREFIX = "C";

	@NotSaved
	public static final String PREFIX_CE = "CE";

	private ObjectId id;
	private int outcome;

	public CNode()
	{
	}

	public CNode( String in )
	{
		rehydrate( in );
	}

	public CNode( ConditioningEvent ce, int outcome )
	{
		this.id = ce.getId();
		this.outcome = outcome;
	}

	public ConditioningEvent getConditioningEvent()
	{
		return Database.get( ConditioningEvent.class, id );
	}

	public void setConditioningEvent( ConditioningEvent ce )
	{
		this.id = ce.getId();
	}

	public ObjectId getCeId()
	{
		return id;
	}

	public void setCeId( ObjectId ceId )
	{
		this.id = ceId;
	}

	public int getOutcome()
	{
		return outcome;
	}

	public void setOutcome( int outcome )
	{
		this.outcome = outcome;
	}

	@Override
	public String dehydrate()
	{
		return PREFIX + TreeNode.CFS + id.toString() + TreeNode.CFS + outcome;
	}

	@Override
	public void rehydrate( String in )
	{
		String[] s = in.split( TreeNode.CFS );
		id = new ObjectId( s[ 1 ] );
		outcome = Integer.parseInt( s[ 2 ] );
	}

	@Override
	public boolean isType( String in )
	{
		return in.startsWith( PREFIX );
	}

	@Override
	public ObjectId getDBId()
	{
		return id;
	}

	@Override
	public String getTreeId()
	{
//		return getDBId().toHexString()+":"+outcome;
		return getCeTreeId() + ":" + outcome;
	}

	protected String getCeTreeId()
	{
//		return getDBId().toHexString();
		return getParent().getTreeId() + "-" + getDBId().toHexString();
	}

	@Override
	public boolean equals( TreeNode other )
	{
		if( other instanceof CNode )
		{
			if( id.equals( ( (CNode)other ).id ) && outcome == ( (CNode)other ).outcome )
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return PREFIX + ": " + id + " (outcome=" + outcome + ")";
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.toJSON();

		JsonHelper.put( json, JsonKeys.Parent, getCeTreeId() ); // overwrite the id

		final ConditioningEvent conditioningEvent = Database.get( ConditioningEvent.class, id );
		JsonHelper.put( json, JsonKeys.Outcome, outcome );
		JsonHelper.put( json, JsonKeys.Label, conditioningEvent.outcome( outcome ).getLabel() );
		JsonHelper.put( json, JsonKeys.Description, conditioningEvent.outcome( outcome ).getDescription() );

		return json;
	}

	JSONObject ceToJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.toJSON();

		JsonHelper.put( json, JsonKeys.Id, getCeTreeId() ); // overwrite the id
		JsonHelper.put( json, JsonKeys.Type, PREFIX_CE ); // overwrite the type
		JsonHelper.put( json, JsonKeys.Parent, getParent().getTreeId() ); // overwrite the type

		final ConditioningEvent conditioningEvent = Database.get( ConditioningEvent.class, id );
		JsonHelper.put( json, JsonKeys.Label, conditioningEvent.getLabel() );
		JsonHelper.put( json, JsonKeys.Description, conditioningEvent.getDescription() );

		return json;
	}
}
