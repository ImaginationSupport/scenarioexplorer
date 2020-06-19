package com.imaginationsupport;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.imaginationsupport.data.HistoricState;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.views.View;

public class StateManager {
	/*
	 * Singleton
	 * State Manager is a singleton class
	 */
	private static StateManager instance=null;
	
	protected StateManager(){}
	
	public static StateManager getInstance(){
		if(instance==null){
			instance=new StateManager();
		}
		return instance;
	}
	
	private Database db=Database.getInstance();
	//private JobManager jm=JobManager.getInstance();
	//private PlugInManager pluginManager=PlugInManager.getInstance();
	
	protected static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();
	public static final String HISTORICDATELABEL = "Date";
	public static final DateTimeFormatter df=DateTimeFormatter.ofPattern("M/d/yyyy");


	//TODO: this is not going to scale well, keep an eye on this...
	public SortedSet<State> getStates(final ObjectId projectId, final ObjectId viewId) throws InvalidDataException
	{
		if(viewId==null) throw new InvalidDataException("Null parameter not permitted.");
		View v=Database.get(View.class, viewId);
		if (v==null) throw new InvalidDataException("No View for the DB id provided.");
		TreeSet< State > ret = new TreeSet<>();
		for (ObjectId sid: v.getTree().getStatesIds()) {
			State s=Database.get(State.class, sid);
			//System.out.println(s.getId().toHexString());
			if(s!=null) ret.add(s);
		}
		return ret;
	}
	
	public State getState(final ObjectId projectId, final ObjectId stateId) throws InvalidDataException
	{
		if(projectId==null || stateId==null) throw new InvalidDataException("Null parameter not permitted.");
		return Database.get(State.class, stateId);
	}
		
	public State updateState(final ObjectId projectId, final State updated) throws GeneralScenarioExplorerException
	{
		if(projectId==null || updated==null || updated.getId()==null) throw new GeneralScenarioExplorerException("Null parameter not permitted.");
		State s=Database.get(State.class, updated.getId());
		if(s==null) throw new GeneralScenarioExplorerException("No such State ("+updated.getId()+") in Database.");
		s.setLabel(updated.getLabel());
		s.setDescription(updated.getDescription());
		s.save();
		// FOR NOW NO OTHER CHANGES ARE ALLOWED
		return s;
	}
	
	public String importHistoricStates(final Project project, String csvPath, boolean integrate) throws IOException, InvalidDataException
	{
		String errors="";
		FileReader fileReader=new FileReader(new File(csvPath));
		CSVParser csvParser = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(fileReader);
		
		Map<String,Integer> map = csvParser.getHeaderMap();
		List<String> uids=new ArrayList<>();
		List<String> headers=new ArrayList<>();
		List<FeatureMap> maps=new ArrayList<>();
		for(String cHeader: map.keySet()) {
			if(!cHeader.equals(HISTORICDATELABEL)) {
				uids.add(getUidFromHeader(cHeader));
				maps.add(project.getFeatureMap(getUidFromHeader(cHeader)));
			} else {
				uids.add(cHeader);
				maps.add(null);
			}
			headers.add(cHeader);
		}
		
		if (!headers.get(0).equals(HISTORICDATELABEL)) {
			errors+="Error parsing CSV file: First column must be labeled '"+HISTORICDATELABEL+"'.\n";
		}
		
		int line=1;
		for(CSVRecord row: csvParser.getRecords()) {
			try {
				String d=row.get(HISTORICDATELABEL);
				LocalDate date= LocalDate.parse(d,df);
				HistoricState s=null;
				if (integrate) s=getHistoricState(project,date.atStartOfDay());
				else s=new HistoricState(project,date.atStartOfDay(),null);
				
				for(int c=1;c<headers.size();c++) {
					String value=row.get(c);
					Feature feature=null;
					if (integrate) {
						try {
							feature=s.getFeature(uids.get(c));
						} catch (DatastoreException e) {
							errors+="Warning: This Project does not have a Feature matching '"+headers.get(c)+"'.";
							continue;
						}
						if(feature!=null && !feature.getValue().equals(value)) {
							feature.setValue(value);
						}
					} else {
						s.setFeature(maps.get(c),value);
					}
				}
				s.save();
			} catch(Exception e) {
				errors+="Line "+line+": "+e+"\n";
			}
			line++;
		}
		fileReader.close();
		csvParser.close();
		if (errors.isEmpty()) errors="Success.";
		return errors;
	}
	
	private String getColHeader(FeatureMap fm) {
		return fm.getLabel()+" ["+fm.getUid()+"]";
	}
	
	private String getUidFromHeader(String header) {
		return header.substring(header.indexOf("[")+1, header.indexOf("]"));
	}
	
	public void exportHistoricStates(final Project project, String csvPath) throws GeneralScenarioExplorerException
	{
		List<HistoricState> states=getHistoricStates(project);
		Collections.sort(states);
		try {
	        FileWriter fileWriter = new FileWriter(csvPath);
			CSVPrinter csvPrinter=new CSVPrinter(fileWriter,CSVFormat.EXCEL.withFirstRecordAsHeader());
			
			//Print Header
			List<String> headerData= new ArrayList<>();
			headerData.add(HISTORICDATELABEL);
			for (FeatureMap map: project.getFeatureMaps()){
				headerData.add(getColHeader(map));
			}
			csvPrinter.printRecord(headerData);
			
			//Print Data
			for(HistoricState s: states) {
				List<String> rowData= new ArrayList<>();
				rowData.add(df.format(s.getStart()));
				for (FeatureMap map: project.getFeatureMaps()){
					try {
						headerData.add(s.getFeature(map.getUid()).getValue());
					} catch (DatastoreException | InvalidDataException e) {
						headerData.add(""); // put a blank in for missing data
					}
				}
				csvPrinter.printRecord(rowData);
			}
		
			fileWriter.flush();
			fileWriter.close();
			csvPrinter.close();
		} catch (IOException e) {
			throw new GeneralScenarioExplorerException("Unable to write out CSV content to file ("+csvPath+"): "+e);
		}
	}
	
	public List<HistoricState> getHistoricStates(Project project)
	{
		Query<HistoricState> q= db.datastore.createQuery(HistoricState.class);
		q.criteria("project").equal(project.getId());
		return q.asList();
	}
	
	public HistoricState getHistoricState(Project project, LocalDateTime date) {
		Query<HistoricState> q= db.datastore.createQuery(HistoricState.class);
		q.criteria("project").equal(project.getId());
		q.criteria("start").equal(date);
		HistoricState ret=q.get();
		if (ret==null) {
			ret=new HistoricState(project, date, null);
		}
		return ret;
	}
	
	
}
