package com.imaginationsupport.data;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.util.ArrayList;
import java.util.List;

public enum TemporalRelationship {
	// These are from Allen's Interval Algebra
	PRECEEDES("preceeds",true),
	MEETS("meets", false),
	OVERLAPS("overlaps", false),
	FINISHEDBY("finished by",false),
	CONTAINS("contains", false),
	STARTS("starts", true),
	EQUALS("equals", false),
	STARTEDBY("started by", false),
	DURING ("during", true),
	FINISHES("finishes", true),
	OVERLAPPEDBY("overlapped by",false),
	METBY("met by", false),
	PRECEDEDBY("preceded by", true);

	private final String label;
	private final boolean point;
	
	TemporalRelationship(String label, boolean point){
		this.label=label;
		this.point=point;
	}
	public String getLabel() {return label;}
	public boolean getPoint() {return point;}
	
	public static List<String> intervalToInterval(){
		List<String> out=new ArrayList<>();
		for (TemporalRelationship t: TemporalRelationship.class.getEnumConstants()){
			out.add(t.label);
		}
		return out;
	}
	
	public static List<String> pointToInterval(){
		List<String> out=new ArrayList<>();
		for (TemporalRelationship t: TemporalRelationship.class.getEnumConstants()){
			if(t.point){
				out.add(t.label);
			}
		}
		return out;
	}

	public static TemporalRelationship fromLabel( final String label ) throws GeneralScenarioExplorerException
	{
		if( label == null )
		{
			throw new GeneralScenarioExplorerException( "TemporalRelationship label cannot be null!" );
		}

		switch( label )
		{
			case "preceeds":
				return PRECEEDES;
			case "meets":
				return MEETS;
			case "overlaps":
				return OVERLAPS;
			case "finished by":
				return FINISHEDBY;
			case "contains":
				return CONTAINS;
			case "starts":
				return STARTS;
			case "equals":
				return EQUALS;
			case "started by":
				return STARTEDBY;
			case "during":
				return DURING;
			case "finishes":
				return FINISHES;
			case "overlapped by":
				return OVERLAPPEDBY;
			case "met by":
				return METBY;
			case "preceded by":
				return PRECEDEDBY;
			default:
				throw new GeneralScenarioExplorerException( String.format( "Unknown TemporalRelationship label: %s", label ) );
		}
	}
}
