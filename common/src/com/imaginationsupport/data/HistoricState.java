package com.imaginationsupport.data;

import java.time.LocalDateTime;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.DatastoreException;

@Entity
public class HistoricState extends State {

	protected static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();

	@NotSaved
	private State previous=null;
	
	public HistoricState(Project project, LocalDateTime time, State previous){
		super(project,"historic","",time,time,previous);
		this.previous=previous;
		isHistoric=true;
		markModified();
	}
	
	public void setValuesCsv(CSVRecord record) throws InvalidDataException, GeneralScenarioExplorerException
	{
		for(FeatureMap fm: projectObject.getFeatureMaps()) {
			Feature f=new Feature(fm);
			try {
				String v=record.get(fm.getLabel());
				if (v==null) throw new IllegalStateException("Null value to replace");
				f.setValue(v);
			} catch (IllegalStateException | IllegalArgumentException e) {
				try {
					if(previous!=null) {
						f.setValue(previous.getFeature(fm.getUid()).getValue());
					} else {
						f.setValue(fm.getType().getDefaultValue());
					}
				} catch (DatastoreException e1) {
					LOGGER.error("Exception Caught in HistoricState setValues ("+fm.getLabel()+"): "+e1);
					e1.printStackTrace();
				}
			}
			try {
				updateFeature(f);
			} catch (DatastoreException e) {
				LOGGER.error("Exception Caught in HistoricState updateFeature ("+fm.getLabel()+"): "+e);
				e.printStackTrace();
			}
		}
		markModified();
	}
	
	public void setFeature(FeatureMap map, String value) throws DatastoreException, InvalidDataException, GeneralScenarioExplorerException
	{
		Feature f=new Feature(map);
		f.setValue(value);
		updateFeature(f);
	}
}
