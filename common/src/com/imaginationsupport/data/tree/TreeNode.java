package com.imaginationsupport.data.tree;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.ArrayList;
import java.util.List;

@Embedded
@RestApiObjectInfo(
	definitionName = "TreeNode",
	tagName = RestApiHandlerInfo.CategoryNames.None,
	description = "View Tree Node Base",
	discriminator = TreeNode.JsonKeys.Type )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo( name = TreeNode.JsonKeys.Id, rawType = ObjectId.class, description = "The unique id of the node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = TreeNode.JsonKeys.Type, rawType = String.class, description = "The type of node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = TreeNode.JsonKeys.DbId, rawType = ObjectId.class, description = "The unique id of the datebase id" ),
	@RestApiRequestObjectPseudoFieldInfo( name = TreeNode.JsonKeys.Parent, rawType = ObjectId.class, description = "The unique id of the node's parent" )
} )
public abstract class TreeNode implements ApiObject
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		protected static final String Id = "id";
		protected static final String Type = "type";
		protected static final String DbId = "DBid";
		protected static final String Parent = "parent";
	}

	@NotSaved
	protected TreeNode parent=null;

	@NotSaved
	protected List<TreeNode> children=null;

	@NotSaved
	public static final String CFS=":";

	public abstract String dehydrate();
	public abstract void rehydrate(String in);
	public abstract boolean isType(String in);
	public abstract ObjectId getDBId();
	public abstract String getTreeId();
	public abstract boolean equals(TreeNode other);
	public abstract String toString();
//	public abstract JSONObject toJSON();

	public TreeNode getParent(){
		return parent;
	}

	public void setParent(TreeNode node){
		this.parent=node;
	}

	public List<TreeNode> getChildren(){
		return children;
	}

	public void addChild(TreeNode node){
		if(children==null) children=new ArrayList<>();
		children.add(node);
		node.setParent(this);
	}

	public void setChildren (List<TreeNode> nodes){
		children=nodes;
		for (TreeNode node: nodes){
			node.setParent(this);
		}
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Id, getTreeId() );
		JsonHelper.put( json, JsonKeys.DbId, getDBId() );
		JsonHelper.put( json, JsonKeys.Parent, parent == null ? null : parent.getTreeId() );

		if( this instanceof SNode )
		{
			JsonHelper.put( json, JsonKeys.Type, SNode.PREFIX );
		}
		else if( this instanceof CNode )
		{
			JsonHelper.put( json, JsonKeys.Type, CNode.PREFIX );
		}
		else if( this instanceof ANode )
		{
			JsonHelper.put( json, JsonKeys.Type, ANode.PREFIX );
		}
		else
		{
			throw new GeneralScenarioExplorerException( "Unknown TreeNode type: " + this.getClass().getCanonicalName() );
		}

		return json;
	}
}
