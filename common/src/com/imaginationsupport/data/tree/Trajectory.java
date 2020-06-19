package com.imaginationsupport.data.tree;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.State;

public class Trajectory implements Comparable<Trajectory> {
	
	@NotSaved
	public final static String IFS = ",";
	@NotSaved
	public final static String CFS = ":";
			
	private ObjectId leaf=null;
	private String path=null;
	
	@NotSaved
	protected List<TreeNode> nodes=null;
		
	public Trajectory(){
	}
	
	public Trajectory(Trajectory other){
		this.leaf=other.leaf;
		this.path=other.path;
		if(other.nodes!=null){
			this.nodes=new ArrayList<>(other.nodes.size()+1);
			this.nodes.addAll(other.nodes);
		}
	}
	
	public List<TreeNode> getNodes() {
		check();
		if (nodes.isEmpty() && path!=null)
			rehydrate();
		return nodes;
	}

	public void set(List<TreeNode> nodes) {
		this.nodes = nodes;
	}
	
	public void add(TreeNode node){
		check();
		nodes.add(node);
	}

	public void add(ConditioningEvent ce, int outcome){
		check();
		nodes.add(new CNode(ce,outcome));
	}
	
	public void add(State s){
		check();
		nodes.add(new SNode(s));
	}
	
	private void check(){
		if (nodes==null) nodes=new ArrayList<>();
	}
	
	@PrePersist
	public void dehydrate(){
		if (nodes==null || nodes.isEmpty()){
			leaf=null;
			path=null;
		} else {
			leaf=nodes.get(nodes.size()-1).getDBId();
			StringBuilder sb=new StringBuilder(dehydrateNode(nodes.get(0)));
			for (int i=1;i<nodes.size();i++){
				sb.append(IFS);
				sb.append(dehydrateNode(nodes.get(i)));
			}
			path=sb.toString();
		}
	}
	
	private String dehydrateNode(TreeNode node){
		return node.dehydrate();
	}
	
	@PostLoad
	public void rehydrate(){
		nodes=new ArrayList<>();
		if(path!=null){
			String[] sp=path.split(IFS);
			for (String s: sp){
				nodes.add(rehydrateNode(s));
			}
		}
	}
	
	private TreeNode rehydrateNode(String fragment){
		if(fragment.startsWith(CNode.PREFIX)){
			return new CNode(fragment);
		}
		if(fragment.startsWith(SNode.PREFIX)){
			return new SNode(fragment);
		}
		if(fragment.startsWith(ANode.PREFIX)){
			return new ANode(fragment);
		}
		return null;
	}

	public ObjectId getLeaf(){
		return leaf;
	}

	@Override
	public int compareTo(Trajectory o) {
		return leaf.compareTo(o.leaf);
	}
}
