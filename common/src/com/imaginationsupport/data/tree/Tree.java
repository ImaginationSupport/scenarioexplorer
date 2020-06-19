package com.imaginationsupport.data.tree;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.PostLoad;
import org.mongodb.morphia.annotations.PrePersist;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

@RestApiObjectInfo( definitionName = "ViewTree", tagName = RestApiHandlerInfo.CategoryNames.None, description = "View Tree" )
@RestApiRequestObjectPseudoFields( value = {
	@RestApiRequestObjectPseudoFieldInfo( name = Tree.JsonKeys.Root, rawType = ObjectId.class, description = "The unique id of the root node" ),
	@RestApiRequestObjectPseudoFieldInfo( name = Tree.JsonKeys.Nodes, rawType = Set.class, genericInnerType = TreeNode.class, description = "The set of nodes" )
} )
public class Tree implements ApiObject
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Root = "root";
		public static final String Nodes = "nodes";
	}

	@Embedded
	private List<Trajectory> trajectories = new ArrayList<>();
	
	@Embedded
	protected SNode root;
	
	@NotSaved
	protected Hashtable<ObjectId,SNode> stateIndex=new Hashtable<>();
	
	public Tree(){}
	
	public Tree(State root){
		this.root=new SNode(root);
	}
	
	public List<Trajectory> getTree() {
		return trajectories;
	}

	public void setTree(List<Trajectory> newTrajectories) {
		this.trajectories = newTrajectories;
		rehydrate();
	}
	
	public void addTrajectory(Trajectory t){
		if(trajectories==null) trajectories=new ArrayList<>();
		this.trajectories.add(t);
		rehydrateDfs(null,t);
		t.rehydrate(); // need to reset nodes
	}
	
	public List<Trajectory> getTrajectories(){
		return trajectories;
	}
	
	
	public SNode getRoot(){
		return root;
	}

	@PrePersist
	public void dehydrate(){
		if (root==null){
			trajectories=null;
		} else {
			if (trajectories==null || trajectories.isEmpty()){
				Trajectory t=new Trajectory();
				//t.put(root);
				dehydrateDfs(root,t);
			}
				
		}
	}
	
	public void forceDehydrate(){
		trajectories.clear();
		Trajectory t=new Trajectory();
		dehydrateDfs(root,t);
	}
		
	private void dehydrateDfs(TreeNode node, Trajectory t){
		Trajectory next=new Trajectory(t);
		next.add(node);
		
		if(node.children==null) { // leaf node
			if(node instanceof CNode){
				System.err.println("ERROR: DFS found and is skipping leaf node conditioning event: "+node.toString());
				return;
			}
			trajectories.add(next);
		} else { // not a leaf node
			for (TreeNode c: node.children){
				dehydrateDfs(c,next);
			}
		}
	}
	
	@PostLoad
	public void rehydrate(){
		root=null;
		stateIndex=new Hashtable<>();
		for(Trajectory t:trajectories){
			rehydrateDfs(root,t);
		}
	}
	
//	private void rehydrateDfs(TreeNode parent, Trajectory t){
//		if(t.nodes.isEmpty()) return; // nothing to do here
//
//		// initial case to set root
//		TreeNode current=t.nodes.get(0);
//		t.nodes.remove(0);
//
//		if (parent==null){
//			root=(SNode)current;
//			indexNodes(root);
//			rehydrateDfs(root,t);
//		} else {
//			TreeNode next=null;
//			// check if the current is already a child
//			if(parent.children!=null){
//				for (TreeNode child: parent.children){
//					if (current.equals(child)){
//						next=child;
//						break;
//					}
//				}
//			}
//			// add current or pass to next
//			if (next==null){ // put new node to tree
//				parent.addChild(current);
//				indexNodes(current);
//				rehydrateDfs(current,t);
//			} else {
//				rehydrateDfs(next,t);
//			}
//		}
//	}
	
	private void rehydrateDfs(TreeNode parent, Trajectory t){
		if(t.nodes.isEmpty()) return; // nothing to do here

		// initial case to set root
		TreeNode current=t.nodes.get(0);
		indexNodes(current);

		if (parent==null){
			parent=current;
			root=(SNode)parent;
		}

		// compare if we have the node here or as child
		if(parent.equals(current)){
			t.nodes.remove(0);
			rehydrateDfs(parent,t);
		} else { // check children
			boolean found=false;
			if(parent.children!=null){
				for (TreeNode next: parent.children){
					if (current.equals(next)){
						found=true;
						rehydrateDfs(next,t);
					}
				}
			}
			if (!found){ // put new node to tree
				parent.addChild(current);
				//indexNodes(c);
				rehydrateDfs(current,t);
			}
		}
	}
	
	private void indexNodes(TreeNode node) {
		if(node instanceof SNode){
			stateIndex.put(node.getDBId(),(SNode)node);
		}
	}
	
	public String toString(){
		return dfsPrint(root,0);
	}
	
	private String dfsPrint(TreeNode node, int indention){
		String out=indent(indention)+node.toString()+"\n";
		indention++;
		if(node.children!=null){
			for (TreeNode next:node.children){
				out+=dfsPrint(next,indention);
			}
		}
		return out;
	}
	
	private String indent(int depth){
		String s="";
		for(int i=0;i<depth;i++){
			s+="\t";
		}
		return s;
	}


	private void dfsJSON(final JSONArray ja, final TreeNode node) throws GeneralScenarioExplorerException
	{
		// on first instance of CNode, produce the CE node
		if(node instanceof CNode){
			if (((CNode)node).getOutcome()==0)
				ja.put(((CNode)node).ceToJSON());
		}

		ja.put(node.toJSON());
		if(node.children!=null){
			for (TreeNode next:node.children){
				dfsJSON(ja, next);
			}
		}
	}
	
	public SNode getStateNode(State state){
		return getStateNode(state.getId());
	}
	
	public SNode getStateNode(ObjectId id){
		return stateIndex.get(id);
	}

	public Set<ObjectId> getStatesIds(){
		return stateIndex.keySet();
	}
	
	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONArray nodes = new JSONArray();
		dfsJSON( nodes, root );

		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Root, root.getDBId() );
		JsonHelper.put( json, JsonKeys.Nodes, nodes );

		return json;
	}
}
