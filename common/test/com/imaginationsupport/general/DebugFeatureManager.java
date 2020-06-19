package com.imaginationsupport.general;


import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.plugins.FeatureType;

public class DebugFeatureManager
{

	public PlugInManager fm=null;
	
	public static void main(String[] args) {
		DebugFeatureManager t=new DebugFeatureManager();
		t.listFeatures();
	}

	public DebugFeatureManager(){
		fm=PlugInManager.getInstance();
	}
	
	public void listFeatures(){
		System.out.println("Classes extending FeatureType:");
		for(String id: fm.getFeatureTypeIds()){
			FeatureType ft=fm.getFeatureType(id);
			System.out.println(ft.getName()+"\t"+ft.getClass().getCanonicalName());
		}
	}
	
}
