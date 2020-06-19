package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TestApiFeatureMap extends ApiTestCaseBase
{
	private static final String VALID_LABEL = "test feature map";

	private static final String VALID_DESCRIPTION = "my description";

	@Test
	public void testToJson() throws InvalidDataException
	{
		showMethodHeader();

		for( final FeatureType featureType : PlugInManager.getInstance().getFeatureTypes() )
		{
			final FeatureMap testFeatureMap = new FeatureMap( featureType, VALID_LABEL, VALID_DESCRIPTION );

			final JSONObject jsonMin = testFeatureMap.toJSON();

			LOGGER.debug( "min: " + jsonMin.toString( 4 ) );

			verifyStringParam( jsonMin, FeatureMap.JsonKeys.Id, testFeatureMap.getUid() );
			verifyStringParam( jsonMin, FeatureMap.JsonKeys.Label, VALID_LABEL );
			verifyStringParam( jsonMin, FeatureMap.JsonKeys.Description, VALID_DESCRIPTION );
			verifyStringParam( jsonMin, FeatureMap.JsonKeys.FeatureType, testFeatureMap.getType().getId() );
//			verifyObjectIdParam( jsonMin, FeatureMap.JsonKeys.Projector, testFeatureMap.getProjector() ); // TODO finish!

			// check for any other keys
			final Set< String > knownKeys = new HashSet<>();
			knownKeys.add( FeatureMap.JsonKeys.Id );
			knownKeys.add( FeatureMap.JsonKeys.Label );
			knownKeys.add( FeatureMap.JsonKeys.Description );
			knownKeys.add( FeatureMap.JsonKeys.FeatureType );
			knownKeys.add( FeatureMap.JsonKeys.Config );
			knownKeys.add( FeatureMap.JsonKeys.ProjectorId );
			knownKeys.add( FeatureMap.JsonKeys.ProjectorConfig );
			verifyNoExtraEntries( jsonMin, knownKeys );

			final JSONObject jsonFull = testFeatureMap.toJSON();

			LOGGER.debug( "full: " + jsonFull.toString( 4 ) );

			verifySameObject( jsonFull, jsonMin );
		}

		return;
	}
}
