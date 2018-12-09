package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;
import de.l3s.souza.EventKG.queriesGenerator.nlp.NlpConversion;
import de.l3s.souza.EventKG.queriesGenerator.nlp.TimeFilterSnapshot;

public class TimeStampUtils {

	private static PropertyUtils propertyUtils;
	private static String queryName;
	private static String filterTime;
	private TimeFilterSnapshot timeFilterSnapshot;
	
	public TimeStampUtils( PropertyUtils propertyUtils) {
		
		queryName = "";
		
		this.propertyUtils = propertyUtils;
		filterTime = "";
	}
	
	
	public static String getQueryName() {
		return queryName;
	}


	public static void setQueryName(String queryName) {
		TimeStampUtils.queryName = queryName;
	}


	public String getTimeStampEvent(String type, String eventAttributes) throws IOException
	{
		String timestamp = "";
		
		if (type.contains("begin"))
		{
			timestamp = propertyUtils.getValueFromDocument (eventAttributes,"sem:hasBeginTimeStamp", "dbpedia_en");
			if (timestamp.isEmpty())
			timestamp = propertyUtils.getValueFromDocument (eventAttributes,"sem:hasBeginTimeStamp", " ");

		}
		if (type.contains("end"))
		{
			timestamp = propertyUtils.getValueFromDocument (eventAttributes,"sem:hasEndTimeStamp", "dbpedia_en");
			if (timestamp.isEmpty())
			timestamp = propertyUtils.getValueFromDocument (eventAttributes,"sem:hasEndTimeStamp", " ");

		}
		return timestamp;
	
	}
	
	public String getYearTimeStamp (String timestamp)
	{
		
		return (timestamp.substring(1,5));
	}
	
	public String getFilter (String periodSelected, String beginTimeStampPred, String EventDecisionSelected, String 
			startTimeVar, String endTimeVar, String beginTimeStampValue, String endTimeStampValue
			, String beginYearTimeStamp, String last, String yearSelected, String eventAttrib)
	{
		queryName = "filter in ";
		
		try {
			if (!beginTimeStampPred.isEmpty())
			{
				
				switch (periodSelected)
				{
					case "sameStartTime":
					{
						if (!EventDecisionSelected.contains("specific"))
						queryName = queryName + startTimeVar.replaceAll("StartTime", "") + ": happened at the day " + beginTimeStampValue;
						filterTime = "FILTER ("+startTimeVar + " = "+beginTimeStampValue + ")";
						break;
					}
					case "sameRange":
					{
						if (!EventDecisionSelected.contains("specific"))
						{	
							if (!endTimeStampValue.isEmpty())
								queryName = queryName + startTimeVar.replaceAll("StartTime", "") + ": happened from  " + beginTimeStampValue + " and  " + endTimeStampValue;
							else
								queryName = queryName + startTimeVar.replaceAll("StartTime", "") + ": in  " + beginTimeStampValue;

						}
						
						if (!endTimeStampValue.isEmpty())
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + " && "+startTimeVar + " < " + endTimeStampValue+")\n";
						else
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + ")\n";
						
						if (!endTimeStampValue.isEmpty())
							filterTime = filterTime + "FILTER ("+endTimeVar + " >= "+beginTimeStampValue + " && "+endTimeVar + " < " 
						+ endTimeStampValue+")\n";
						break;
					}
					
					case "before":
					{
						if (!EventDecisionSelected.contains("specific"))
						queryName = queryName + startTimeVar.replaceAll("StartTime", "") +": happened before  " + beginTimeStampValue ;

						if (!endTimeStampValue.isEmpty())
							filterTime = "FILTER ("+startTimeVar + " < "+beginTimeStampValue + " && "+endTimeVar + " < " + beginTimeStampValue+")\n";
						else
							filterTime = "FILTER ("+startTimeVar + " < "+beginTimeStampValue + ")\n";

						break;
					}
					
					case "after":
					{
						
						if (!endTimeStampValue.isEmpty())
						{
							filterTime = "FILTER ("+startTimeVar + " > "+endTimeStampValue + ")\n";
							if (!EventDecisionSelected.contains("specific"))
								queryName = queryName + startTimeVar.replaceAll("StartTime", "") +": after  " + endTimeStampValue ;
						}	
						else
						{
							filterTime = "FILTER ("+startTimeVar + " > "+beginTimeStampValue + ")\n";
							if (!EventDecisionSelected.contains("specific"))
							queryName = queryName + startTimeVar.replaceAll("StartTime", "") +" has started after  " + beginTimeStampValue ;
						}
						break;
					}
					
					case "last":
					{
						int beginYear = Integer.parseInt(beginYearTimeStamp);
						int diff = Integer.parseInt(last);
						int pastYear = beginYear - diff;
						
						if (!EventDecisionSelected.contains("specific"))
						 queryName = queryName + startTimeVar.replaceAll("StartTime", "") +" last " + diff +" years ago" ;
						beginTimeStampValue = "\""+pastYear+"-01-01\"^^xsd:date";
						endTimeStampValue = "\""+beginYear+"-01-01\"^^xsd:date";
						if (!endTimeStampValue.isEmpty())
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + " && "+startTimeVar + " < " + endTimeStampValue+")\n";
						else
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + ")\n";

						if (eventAttrib.contains("sem:hasEndTimeStamp"))
							filterTime = filterTime + "FILTER ("+endTimeVar + " >= "+beginTimeStampValue + " && "+endTimeVar + " < " 
						+ endTimeStampValue+")\n";
						break;
					}
					
					
					case "year":
					{
						int beginYear = Integer.parseInt(yearSelected);
						int endYear = beginYear + 1;
						
						if (!EventDecisionSelected.contains("specific"))
							queryName = queryName + startTimeVar.replaceAll("StartTime", "") +" happened in " + beginYear ;
						beginTimeStampValue = "\""+beginYear+"-01-01\"^^xsd:date";
						endTimeStampValue = "\""+endYear+"-01-01\"^^xsd:date";
						
						if (!endTimeStampValue.isEmpty())
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + " && "+startTimeVar + " < " + endTimeStampValue+")\n";
						else
							filterTime = "FILTER ("+startTimeVar + " >= "+beginTimeStampValue + ")\n";
						
						if (eventAttrib.contains("sem:hasEndTimeStamp"))
							filterTime = filterTime + "FILTER ("+endTimeVar + " >= "+beginTimeStampValue + " && "+endTimeVar + " < " 
						+ endTimeStampValue+")\n";
						break;
					}
				}
			}
			}
			catch (Exception e) {
				filterTime = "";		
				}
		 
		return filterTime;
		
		
	}
	
	public boolean isYearString (String str)
	{
		 String text = "";
		    text = str.replaceAll("[^0-9]", "#"); //simple solution for replacing all non digits. 
		    String[] arr = text.split("#");

		    boolean hasYear = false;
		  
		    for(String s : arr){
		        if(s.matches("^[0-9]{4}$")){
		            hasYear = true;
		            break;
		           
		        }
		    }
		    
		return hasYear;
	}
	
	
}
