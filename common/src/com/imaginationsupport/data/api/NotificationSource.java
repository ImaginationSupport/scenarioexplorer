package com.imaginationsupport.data.api;

import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.util.Set;

public interface NotificationSource
{
	Set< Notification > generateNotifications() throws InvalidDataException, GeneralScenarioExplorerException;
}
