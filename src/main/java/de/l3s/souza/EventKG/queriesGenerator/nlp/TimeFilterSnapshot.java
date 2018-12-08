package de.l3s.souza.EventKG.queriesGenerator.nlp;

public class TimeFilterSnapshot {

	private String period;
	private String beginTimeStamp;
	private String eventDecision;
	private String endTimeStamp;
	private String beginYearTimeStamp;
	private String yearSelected;
	private String last;
	
	public TimeFilterSnapshot(String period, String beginTimeStamp, String eventDecision, String endTimeStamp,
			String beginYearTimeStamp, String yearSelected,String last) {
		
		this.period = period;
		this.beginTimeStamp = beginTimeStamp;
		this.eventDecision = eventDecision;
		this.endTimeStamp = endTimeStamp;
		this.beginYearTimeStamp = beginYearTimeStamp;
		this.yearSelected = yearSelected;
		this.last = last;
		
	}
	
	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public String getPeriod() {
		return period;
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getBeginTimeStamp() {
		return beginTimeStamp;
	}
	public void setBeginTimeStamp(String beginTimeStamp) {
		this.beginTimeStamp = beginTimeStamp;
	}
	public String getEventDecision() {
		return eventDecision;
	}
	public void setEventDecision(String eventDecision) {
		this.eventDecision = eventDecision;
	}
	public String getEndTimeStamp() {
		return endTimeStamp;
	}
	public void setEndTimeStamp(String endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}
	public String getBeginYearTimeStamp() {
		return beginYearTimeStamp;
	}
	public void setBeginYearTimeStamp(String beginYearTimeStamp) {
		this.beginYearTimeStamp = beginYearTimeStamp;
	}
	public String getYearSelected() {
		return yearSelected;
	}
	public void setYearSelected(String yearSelected) {
		this.yearSelected = yearSelected;
	}
	
}
