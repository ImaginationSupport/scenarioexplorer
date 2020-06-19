package com.imaginationsupport.data.api;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.json.JSONObject;

public interface ApiObject
{
	JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException;
}
