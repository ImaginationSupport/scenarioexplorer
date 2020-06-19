package com.imaginationsupport;

import com.imaginationsupport.plugins.*;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.plugins.effects.*;
import com.imaginationsupport.plugins.features.*;
import com.imaginationsupport.plugins.preconditions.*;
import com.imaginationsupport.plugins.projectors.*;

import java.util.*;

//
// PLUG-INS include: FeatureTypes and Projectors
//

public class PlugInManager {
	/*
	 * Singleton
	 * Feature Manager is a singleton class
	 */
	private static PlugInManager instance=null;

	protected PlugInManager(){
	}

	public static PlugInManager getInstance(){
		if(instance==null){
			instance=new PlugInManager();
			instance.loadFeatureTypes();
			instance.loadProjectors();
			instance.loadPreconditions();
			instance.loadEffects();
		}
		return instance;
	}
	
	private Hashtable<String,FeatureType> featureTypes=new Hashtable<String,FeatureType>();
	
	private Hashtable<String,Projector> projectors=new Hashtable<String,Projector>();
	
	private Hashtable<String,Precondition> preconditions=new Hashtable<String,Precondition>();

	private Hashtable<String,Effect> effects=new Hashtable<String,Effect>();
	
	// TODO: Dynamically load these from jar files using ServiceLoader interface
	
	private void loadFeatureTypes(){
		featureTypes.put(IntegerFeature.class.getCanonicalName(), new IntegerFeature());
		featureTypes.put(DecimalFeature.class.getCanonicalName(), new DecimalFeature());
		featureTypes.put(TextFeature.class.getCanonicalName(), new TextFeature());
		featureTypes.put(ProbabilityFeature.class.getCanonicalName(), new ProbabilityFeature());
		featureTypes.put(EnumerationFeature.class.getCanonicalName(), new EnumerationFeature());
		featureTypes.put(BooleanFeature.class.getCanonicalName(), new BooleanFeature());
	}
	
	private void loadProjectors(){
		projectors.put(RandomProjector.class.getCanonicalName(), new RandomProjector());
		projectors.put(CompoundingRate.class.getCanonicalName(), new CompoundingRate());
		projectors.put(JavaScriptProjector.class.getCanonicalName(), new JavaScriptProjector());
	}
	
	private void loadPreconditions() {
		preconditions.put(TimelineEventPrecondition.class.getCanonicalName(), new TimelineEventPrecondition());
		preconditions.put(FeaturePrecondition.class.getCanonicalName(), new FeaturePrecondition());
		preconditions.put(OnHold.class.getCanonicalName(), new OnHold());
	}
	
	private void loadEffects() {
		effects.put(FeatureSetEffect.class.getCanonicalName(), new FeatureSetEffect());
		effects.put(ErrorEffect.class.getCanonicalName(), new ErrorEffect());
	}
	
	// ========================= FeatureTypes =========================
	
	public SortedSet<String> getFeatureTypeIds(){
		final TreeSet< String > ret = new TreeSet<>();
		ret.addAll(featureTypes.keySet());
		return ret;
	}

	public FeatureType getFeatureType(String id){
		return featureTypes.get(id);
	}

	public SortedSet<FeatureType> getFeatureTypes() {
		final TreeSet< FeatureType > ret = new TreeSet<>( new FeatureTypeSortComparator() );
		ret.addAll( featureTypes.values() );
		return ret;
	}

	class FeatureTypeSortComparator implements Comparator<FeatureType>
	{
		@Override
		public int compare(FeatureType a, FeatureType b) {
			return a.getName().equals(b.getName())
					? a.getId().compareTo(b.getId())
					: a.getName().compareTo( b.getName() );			
		}
	}
	
	// ========================= Projectors =========================
	
	public SortedSet<String> getProjectorIds(){
		final TreeSet< String > ret = new TreeSet<>();
		ret.addAll(projectors.keySet());
		return ret;
	}
	
	public Projector getProjector(String id) {
		return projectors.get(id);
	}
	
	public SortedSet<Projector> getProjectors() {
		final TreeSet< Projector > ret = new TreeSet<>( new ProjectorSortComparator() );
		ret.addAll( projectors.values() );
		return ret;
	}

	class ProjectorSortComparator implements Comparator<Projector>{
		@Override
		public int compare(Projector a, Projector b) {
			return a.getName().compareTo(b.getName());
		}
	}

	// ========================= Preconditions =========================

	public SortedSet<String> getPreconditionIds(){
		final TreeSet< String > ret = new TreeSet<>();
		ret.addAll(preconditions.keySet());
		return ret;
	}
	
	public Precondition getPrecondition(String id) {
		return preconditions.get(id);
	}
	
	public SortedSet< Precondition > getPreconditions() {
		final TreeSet< Precondition > ret = new TreeSet<>( new PreconditionSortComparator() );
		ret.addAll( preconditions.values() );
		return ret;
	}
	
	class PreconditionSortComparator implements Comparator<Precondition>{
		@Override
		public int compare(Precondition a, Precondition b) {
			return a.getLabel().equals(b.getLabel())
					? a.getId().compareTo(b.getId())
					: a.getLabel().compareTo( b.getLabel() );	

		}
	}
	
	// ========================= Effects ==============================

	public SortedSet<String> getEffectsIds(){
		final TreeSet< String > ret = new TreeSet<>();
		ret.addAll(preconditions.keySet());
		return ret;
	}
	
	public Effect getEffect(String id) {
		return effects.get(id);
	}
	
	public SortedSet< Effect > getEffects() {
		final TreeSet< Effect > ret = new TreeSet<>( new EffectSortComparator() );
		ret.addAll( effects.values() );
		return ret;
	}
	
	class EffectSortComparator implements Comparator<Effect>{
		@Override
		public int compare(Effect a, Effect b) {
			return a.getLabel().equals(b.getLabel())
					? a.getId().compareTo(b.getId())
					: a.getLabel().compareTo( b.getLabel() );	
		}
	}
	
}
