package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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


	@Override
	public boolean equals(Object obj) {

		if(obj instanceof Task){
			
			Task taskCmp = (Task)obj;
			if(this.isDone == taskCmp.isDone 
					&& this.place_argument.equals(taskCmp.getPlace_argument()) && this.priority_argument.equals(taskCmp.getPriority_argument())
					 && this.textContent.equals(taskCmp.getTextContent())
					&& this.type_argument.equals(taskCmp.getType_argument())){
				
				if(this.start_date != null && taskCmp.getStart_date() != null){
					if(this.start_date.equals(taskCmp.getStart_date())){
						return true;
					}
					else
					{
						return false;
					}
				}
				
				if(this.end_date != null && taskCmp.getEnd_date() != null){
					if(this.end_date.equals(taskCmp.getEnd_date())){
						return true;
					}else{
						return false;
					}
				}
				
				return true;
			}
			else{
				return false;
			}
			
			
		
		}

		return super.equals(obj);
	}
	

	public String getEDateInStr(String type){
		
		DateFormat df1 = new SimpleDateFormat(type);
		String dateStr = df1.format(this.getEnd_date());
		return dateStr;
	}
	
	public String getSDateInStr(String type){
		
		DateFormat df1 = new SimpleDateFormat(type);
		String dateStr = df1.format(this.getStart_date());
		return dateStr;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("Description: " + this.textContent);
		if(!this.type_argument.equals("")){
			sb.append(" Type: " + this.type_argument);
		}
		if(!this.priority_argument.equals("")){
			sb.append(" Priority: " + this.priority_argument);
		}
		
		if(!this.place_argument.equals("")){
			sb.append(" Location: " + this.place_argument);
		}
		
		if(this.start_date!= null){
			sb.append(" Start Date: " + this.getSDateInStr(Parser.DATE_FORMAT_TYPE_1));
			
		}
		
		if(this.end_date!= null){
			sb.append(" End date : " + this.getEDateInStr(Parser.DATE_FORMAT_TYPE_1));
		}
		
		return sb.toString();
	}
	
	

	
}
