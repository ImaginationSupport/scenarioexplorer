package com.imaginationsupport.data;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.bson.types.ObjectId;

import com.imaginationsupport.data.features.FeatureMap;

public class TimeSeries {
	
	public static final String CSV_HEADER_TIME="Date";
	public static final DateTimeFormatter df=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	public static final String CSV_HEADER_CASE="Case"; // future use to separate multiple time series for training
	
	private List<State> states=null;
	private Project project=null;
	private List<FeatureMap> fm= new ArrayList<>();
	
	public TimeSeries(Project project, ObjectId terminatingState, boolean includeHistoricData) {
		this.project=project;
		fm.addAll(project.getFeatureMaps());
	}
	
	public TimeSeries(Project project) {
		this.project=project;
		fm.addAll(project.getFeatureMaps());
	}
	
	
	public void importCsv(String filename) throws IOException {
		Reader in = new FileReader("path/to/file.csv");
		//Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		for(CSVRecord r: records) {
			LocalDateTime t=LocalDateTime.parse(r.get(CSV_HEADER_TIME),df);
			State s=new State(project, df.format(t), "", t,t, null);
			states.add(s);
		}
		Collections.sort(states);
	}
	
	
	public void save() {
		for (State s: states) {
			s.save();
		}
	}
	
	public void exportToCSV(String fileName, boolean includeHeader) throws IOException {
		FileWriter f=new FileWriter(fileName);
		if(includeHeader) {
			for (FeatureMap map:fm) {
				
			}
		}
	}
	
	public void exportToArff(String fileName) {
		
	}
	
	private void appendData(FileWriter writer) {
		for(State s: states) {
			for(FeatureMap f: fm) {
				
			}
		}
	}
	
}
