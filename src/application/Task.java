package application;

import java.util.Date;

public class Task {
	
	private String textContent;
	
	private boolean isDone = false;
	private String priority_argument = "";
	private String type_argument = "";
	private Date start_date;
	private Date end_date;
	private String place_argument = "";
	
	
	
	public Task(String textContent) {
		super();
		this.textContent = textContent;
	}


	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}


	public String getTextContent() {
		return textContent;
	}


	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}


	public String getPriority_argument() {
		return priority_argument;
	}


	public void setPriority_argument(String priority_argument) {
		this.priority_argument = priority_argument;
	}


	public String getType_argument() {
		return type_argument;
	}


	public void setType_argument(String type_argument) {
		this.type_argument = type_argument;
	}




	public Date getStart_date() {
		return start_date;
	}


	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}


	public Date getEnd_date() {
		return end_date;
	}


	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}


	public String getPlace_argument() {
		return place_argument;
	}


	public void setPlace_argument(String place_argument) {
		this.place_argument = place_argument;
	}
	
	
		
	

}
