package application;

import java.util.Date;

public class Task implements Comparable<Task>{
	
	private String textContent;
	
	private boolean isDone = false;
	private String priority_argument = "";
	private String type_argument = "";
	private Date start_date = null;
	private Date end_date = null;
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
	
/*	@Override
	public String toString(){
		String format;
		if(isDone){
			format = "Task: " + textContent + "  Start Date & Time: " + start_date +
					"  End Date & Time: " + end_date + "  Type: " + type_argument + 
					"  Priority: " + priority_argument + "  Location: " + place_argument + "(DONE)";
		}else{
			format = "Task: " + textContent + "  Start Date & Time: " + start_date +
					"  End Date & Time: " + end_date + "  Type: " + type_argument + 
					"  Priority: " + priority_argument + "  Location: " + place_argument + "(NOT DONE)";
		}
		return format;
	}*/	
	
	@Override
	 public int compareTo(Task task) {
	    try{
	    	return getEnd_date().compareTo(task.getEnd_date());
	    }catch(NullPointerException ex){
	    	if (this.getEnd_date()== null){
	    		return 1;
	    	}else{
	    		return -1;
	    	}
	    }
	  }
	

}
