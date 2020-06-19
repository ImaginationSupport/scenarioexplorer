package com.imaginationsupport.data;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.effects.ErrorEffect;
import com.imaginationsupport.plugins.preconditions.OnHold;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestApiObjectInfo( definitionName = "ConditioningEvent", tagName = RestApiHandlerInfo.CategoryNames.ConditioningEvent, description = "Scenario Explorer Conditioning Event" )
public class ConditioningEvent extends Persistent implements ApiObject, NotificationSource, Comparable< ConditioningEvent >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String Label = "name";
		public static final String Description = "description";
		public static final String Preconditions = "preconditions";
		public static final String Outcomes = "outcomes";
		public static final String OriginViewId = "originViewId";
	}

	public static final String DATABASE_VIEW_KEY="originView";

	/**
	 * Holds the available notification keys
	 */
	@SuppressWarnings( "WeakerAccess" )
	public abstract static class Notifications
	{
		public static final String NOT_IN_ANY_VIEW = "conditioning-event-not-in-any-view";

		public static final String PRECONDITION_ON_HOLD = "conditioning-event-precondition-on-hold";

		public static final String OUTCOME_EFFECT_ERROR = "conditioning-event-outcome-effect-error";
	}

	@Embedded
	private ObjectId project;

	@Embedded
	private ObjectId originView;

	@RestApiFieldInfo( description = "The name of the conditioning event" )
	private String label;

	@RestApiFieldInfo( description = "The description of the conditioning event" )
	private String description;

	private double pEventGivenPreconditions=1.0;

	@Embedded
//	@RestApiFieldInfo
	private List< Precondition > preconditions = new ArrayList<>();

	@Embedded
//	@RestApiFieldInfo
	private List< Outcome > outcomes = new ArrayList<>();

	/**
	 * Constructor for morphia
	 */
	@SuppressWarnings( "unused" )
	private ConditioningEvent()
	{
		return;
	}

	public ConditioningEvent(ObjectId projectId, ObjectId view, String label, String description){
		this.project=projectId;
		this.originView=view;
		this.label=label;
		this.description=description;
	}

	public ConditioningEvent( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );

		this.preconditions = new ArrayList<>();
		final JSONArray preconditionsRaw = JsonHelper.getRequiredParameterJSONArray( source, JsonKeys.Preconditions );
		for( int i = 0; i < preconditionsRaw.length(); ++i )
		{
			addPrecondition( API.deserializePrecondition( preconditionsRaw.getJSONObject( i ) ) );
		}

		this.outcomes = new ArrayList<>();
		final JSONArray outcomesRaw = JsonHelper.getRequiredParameterJSONArray( source, JsonKeys.Outcomes );
		for( int i = 0; i < outcomesRaw.length(); ++i )
		{
			addOutcome( new Outcome( outcomesRaw.getJSONObject( i ) ) );
		}

		this.originView = JsonHelper.getRequiredParameterObjectId( source, JsonKeys.OriginViewId, false );

		return;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String newLabel) {
		this.label = newLabel;
		markModified();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String newDescription) {
		this.description = newDescription;
		markModified();
	}

	public void addPrecondition(Precondition pc){
		if (preconditions==null) preconditions=new ArrayList<>();
		preconditions.add(pc);
		markModified();
	}

	public List<Precondition> getPreconditions() {
		return preconditions;
	}

	public void removePrecondition(Precondition pc) {
		if(preconditions==null || !preconditions.contains(pc)) return;
		preconditions.remove(pc);
		markModified();
	}

	public List<Outcome> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<Outcome> newOutcomes) {
		this.outcomes = newOutcomes;
		markModified();
	}

	public int addOutcome(Outcome outcome){
		if(outcomes==null) outcomes=new ArrayList<>();
		this.outcomes.add(outcome);
		markModified();
		return outcomes.size()-1;
	}

	public int outcomeCount(){
		return outcomes.size();
	}

	public Outcome outcome(int i){
		if(i>outcomes.size()) return null;
		return outcomes.get(i);
	}

	public ObjectId getProject() {
		return project;
	}

	public void setProject(ObjectId projectId) {
		this.project=projectId;
	}

	public ObjectId getOriginViewId() {
		return originView;
	}

	public void setOriginView(ObjectId newOriginView) {
		this.originView = newOriginView;
	}

	public double getPEventGivenPreconditions() {
		return pEventGivenPreconditions;
	}

	public void setPEventGivenPreconditions(double pEventGivenPreconditions) {
		this.pEventGivenPreconditions = pEventGivenPreconditions;
	}

	public boolean preconditionsSatisfied(State state) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if(preconditions==null) return true;
		for (Precondition p: preconditions){
			if(!p.satisfied(state)) return false;
		}
		return true;
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JsonKeys.Label, this.label );
		JsonHelper.put( json, JsonKeys.Description, this.description );
		JsonHelper.put( json, JsonKeys.OriginViewId, this.originView );

		JsonHelper.put( json, JsonKeys.Preconditions, this.preconditions ) ;
		JsonHelper.put( json, JsonKeys.Outcomes, this.outcomes ) ;

		return json;
	}

	@Override
	public Set< Notification > generateNotifications() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final Set< Notification > notifications = new HashSet<>();

		for( final Precondition precondition : preconditions )
		{
			if( precondition instanceof OnHold )
			{
				notifications.add( new Notification(
					Notification.Scope.ConditioningEvent,
					Notifications.PRECONDITION_ON_HOLD,
					String.format( "Conditioning event \"%s\" has a precondition on hold.", getLabel() ),
					null,
					null,
					getId(),
					null
				) );
			}
			else
			{
				notifications.addAll( precondition.generateNotifications() );
			}
		}

		for( final Outcome outcome : outcomes )
		{
			for( final Effect effect : outcome.getEffects() )
			{
				if( effect instanceof ErrorEffect )
				{
					notifications.add( new Notification(
						Notification.Scope.ConditioningEvent,
						Notifications.OUTCOME_EFFECT_ERROR,
						String.format( "Conditioning event \"%s\" outcome \"%s\" effect error.", getLabel(), outcome.getLabel() ),
						null,
						null,
						getId(),
						null
					) );
				}
			}

			notifications.addAll( outcome.generateNotifications() );
		}

		return notifications;
	}

	@Override
	public int compareTo( final ConditioningEvent other )
	{
		return this.label.equals( other.label )
			? this.getId().compareTo( other.getId() )
			: this.label.compareTo( other.label );
	}
}
