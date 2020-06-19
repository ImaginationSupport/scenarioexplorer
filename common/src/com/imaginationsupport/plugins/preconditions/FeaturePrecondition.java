package com.imaginationsupport.plugins.preconditions;

import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.FeatureRelationship;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Precondition;
import org.json.JSONObject;

public class FeaturePrecondition extends Precondition {

	private static final String JSON_KEY_FEATURE = "featureId";
	private static final String JSON_KEY_RELATION = "relation";
	private static final String JSON_KEY_VALUE = "value";

	private String featureUid = null;
	private FeatureRelationship relation = null;
	private String value = null;

	public FeatureRelationship getRelation() {
		return relation;
	}

	public FeaturePrecondition()
	{
		final JSONObject configJSON = new JSONObject();

		JsonHelper.putNull( configJSON, JSON_KEY_FEATURE );
		JsonHelper.put( configJSON, JSON_KEY_RELATION, FeatureRelationship.EQ.getLabel() );
		JsonHelper.putNull( configJSON, JSON_KEY_VALUE );

		setConfig( configJSON );

		return;
	}

	public FeaturePrecondition( FeatureMap map, FeatureRelationship relation, String value ) throws GeneralScenarioExplorerException
	{
		this.featureUid = map.getUid();
		this.relation = relation;
		this.value = map.getType().userToStorage( value );

		final JSONObject configJSON = new JSONObject();

		JsonHelper.put( configJSON, JSON_KEY_FEATURE, this.featureUid );
		JsonHelper.put( configJSON, JSON_KEY_RELATION, this.relation.getLabel() );
		JsonHelper.put( configJSON, JSON_KEY_VALUE, this.value );

		setConfig( configJSON );

		return;
	}

	@Override
	public void setConfig( final JSONObject config )
	{
		super.setConfig( config);

		this.featureUid = config.isNull( JSON_KEY_FEATURE )
			? null
			: config.getString( JSON_KEY_FEATURE );
		try
		{
			this.relation = config.isNull( JSON_KEY_RELATION )
				? null
				: FeatureRelationship.fromLabel( config.getString( JSON_KEY_RELATION ) );
		}
		catch( GeneralScenarioExplorerException e )
		{
			System.out.println( "Error parsing feature relationship: " + e.getMessage() );

			this.relation = FeatureRelationship.EQ;
		}

		this.value = config.isNull( JSON_KEY_VALUE )
			? null
			: config.getString( JSON_KEY_VALUE );
	}

	public String getLabel() {
		return "Feature Value";
	}

	public String getDescription() {
		return "This precondition type constrains the conditioning event based on the value of a specific feature.";
	}

	public String getValue() {
		return value;
	}

	public String getFeatureUid() {
		return featureUid;
	}

	@Override
	public String getPluginJavaScriptSourceUriPath() {
		return "/js/plugins/preconditions/featurePrecondition.js";
	}

	public boolean satisfied(State state) throws InvalidDataException, GeneralScenarioExplorerException
	{
		Feature feature = null;
		try {
			feature = state.getFeature(featureUid);
		} catch (DatastoreException e) {
			throw new GeneralScenarioExplorerException("Error in Feature Value during comparison for Precondition.");
		}

		if (!feature.getMap().getType().isContinuousVariable()) {
			String stateValue = feature.getValue();
			String compareValue = getValue();
			switch (getRelation()) {
				case EQ:
					if (stateValue.equalsIgnoreCase(compareValue)) return true;
					else return false;
				case NEQ:
					if (!stateValue.equalsIgnoreCase(compareValue)) return true;
					else return false;
				default:
					throw new GeneralScenarioExplorerException("Error in Enumerated Feature Relationship (" + stateValue + "," + compareValue + ")for Conditioning Event Precondition to Feature.");
			}
		} else {
			double stateValue = Double.parseDouble(feature.getValue());
			double compareValue = Double.parseDouble(getValue());
			switch (getRelation()) {
				case GT:
					if (stateValue > compareValue) return true;
					else return false;
				case GTEQ:
					if (stateValue >= compareValue) return true;
					else return false;
				case EQ:
					if (stateValue == compareValue) return true;
					else return false;
				case NEQ:
					if (stateValue != compareValue) return true;
					else return false;
				case LTEQ:
					if (stateValue <= compareValue) return true;
					else return false;
				case LT:
					if (stateValue < compareValue) return true;
					else return false;
				default:
					throw new GeneralScenarioExplorerException("Error in Continuous Feature Relationship (" + stateValue + "," + compareValue + ") for Conditioning Event Precondition to Feature.");
			}
		}
	}
}
