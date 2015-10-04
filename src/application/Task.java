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
		
	

}
