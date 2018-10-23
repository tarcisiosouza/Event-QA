package de.l3s.souza.EventKG.queriesGenerator;

import java.io.IOException;

public class TimeStampUtils {

	private static PropertyUtils propertyUtils;
	private static String queryName;
	private static String filterTime;
	
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
		queryName = "";
		try {
			if (!beginTimeStampPred.isEmpty())
			{
				switch (periodSelected)
				{
					case "sameStartTime":
					{
						if (!EventDecisionSelected.contains("specific"))
						queryName = queryName + " at the day " + beginTimeStampValue;
						filterTime = "FILTER ("+startTimeVar + " = "+beginTimeStampValue + ")";
						break;
					}
					case "sameRange":
					{
						if (!EventDecisionSelected.contains("specific"))
						{	
							if (!endTimeStampValue.isEmpty())
								queryName = queryName + " from  " + beginTimeStampValue + " and  " + endTimeStampValue;
							else
								queryName = queryName + " in  " + beginTimeStampValue;

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
						queryName = queryName + " before  " + beginTimeStampValue ;

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
								queryName = queryName + " after  " + endTimeStampValue ;
						}	
						else
						{
							filterTime = "FILTER ("+startTimeVar + " > "+beginTimeStampValue + ")\n";
							if (!EventDecisionSelected.contains("specific"))
							queryName = queryName + " has started after  " + beginTimeStampValue ;
						}
						break;
					}
					
					case "last":
					{
						int beginYear = Integer.parseInt(beginYearTimeStamp);
						int diff = Integer.parseInt(last);
						int pastYear = beginYear - diff;
						
						if (!EventDecisionSelected.contains("specific"))
						 queryName = queryName + " last " + diff +" years ago" ;
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
							queryName = queryName + " in " + beginYear ;
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
	
	
}
