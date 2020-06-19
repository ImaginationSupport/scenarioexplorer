package com.imaginationsupport.data.tree;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.NotSaved;

/**
 * Aggregate node
 */
public class ANode extends TreeNode {
	
	@NotSaved
	public static final String PREFIX = "A";
	
	private ObjectId id;
	
	public ANode() {}
	
	public ANode(String in){
		rehydrate(in);
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
		if(other instanceof ANode){
			if(id.equals(((ANode)other).id))
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
		return super.toJSON();
	}
}

