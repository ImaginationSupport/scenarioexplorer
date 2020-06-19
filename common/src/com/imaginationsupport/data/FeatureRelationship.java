package com.imaginationsupport.data;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.util.ArrayList;
import java.util.List;

public enum FeatureRelationship {
	GT(">", false),
	GTEQ(">=", false),
	EQ("=", true),
	NEQ("!=", true),
	LTEQ("<=", false),
	LT("<", false);
	private final String label;
	private final boolean enumeration;

	FeatureRelationship(String label, boolean enumeration){
		this.label=label;
		this.enumeration=false;
	}
	public String getLabel() {return label;}
	public boolean getEnumeration() {return enumeration;}

	public static List<String> continuous(){
		List<String> out=new ArrayList<>();
		for (FeatureRelationship t: FeatureRelationship.class.getEnumConstants()){
			out.add(t.label);
		}
		return out;
	}

	public static List<String> enumerated(){
		List<String> out=new ArrayList<>();
		for (FeatureRelationship t: FeatureRelationship.class.getEnumConstants()){
			if(t.enumeration){
				out.add(t.label);
			}
		}
		return out;
	}

	public static FeatureRelationship fromLabel(final String label ) throws GeneralScenarioExplorerException
	{
		if( label == null ) {
			throw new GeneralScenarioExplorerException( "NumericRelationship label cannot be null!" );
		}

		switch( label ) {
			case ">":
				return GT;
			case ">=":
				return GTEQ;
			case "=":
				return EQ;
			case "!=":
				return NEQ;
			case "<=":
				return LTEQ;
			case "<":
				return LT;
			default:
				throw new GeneralScenarioExplorerException( String.format( "Unknown NumericRelationship label: %s", label ) );
		}
	}
}
