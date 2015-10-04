package application;

import java.util.ArrayList;

public class Task {
	
	private String textContent;
	
	private boolean isDone;
	private String priority_argument;
	private String type_argument;
	private String date_argument;
	private String time_argument;
	private String place_argument;
	
	
	
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


	public String getDate_argument() {
		return date_argument;
	}


	public void setDate_argument(String date_argument) {
		this.date_argument = date_argument;
	}


	public String getTime_argument() {
		return time_argument;
	}


	public void setTime_argument(String time_argument) {
		this.time_argument = time_argument;
	}


	public String getPlace_argument() {
		return place_argument;
	}


	public void setPlace_argument(String place_argument) {
		this.place_argument = place_argument;
	}
	
	
		
	

}
