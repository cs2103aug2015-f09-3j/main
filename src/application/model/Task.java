package application.model;

// @@author LimYouLiang A0125975U
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import application.controller.parser.ParserFacade;

public class Task implements Comparable<Task> {

	private String textContent;

	private boolean isDone;
	private String priority_argument;
	private String type_argument;
	private Date start_date;
	private Date end_date;
	private String place_argument;

	private String gCalId = "";

	/**
	 * @return the gCalId
	 */
	public String getgCalId() {
		return gCalId;
	}

	/**
	 * @param gCalId
	 *            the gCalId to set
	 */
	public void setgCalId(String gCalId) {
		this.gCalId = gCalId;
	}

	/**
	 * @return the lastUpdate
	 */
	public long getLastServerUpdate() {
		return lastServerUpdate;
	}

	/**
	 * @param lastUpdate
	 *            the lastUpdate to set
	 */
	public void setLastServerUpdate(long lastUpdate) {
		this.lastServerUpdate = lastUpdate;
	}

	private long lastServerUpdate;
	private long lastLocalUpdate;

	/**
	 * @return the lastLocalUpdate
	 */
	public long getLastLocalUpdate() {
		return lastLocalUpdate;
	}

	/**
	 * @param lastLocalUpdate
	 *            the lastLocalUpdate to set
	 */
	public void setLastLocalUpdate(long lastLocalUpdate) {
		this.lastLocalUpdate = lastLocalUpdate;
	}

	public Task() {
		isDone = false;
		textContent = new String("");
		priority_argument = new String("normal");
		type_argument = new String("normal");
		place_argument = new String("");
		start_date = null;
		end_date = null;
	}

	public Task(String textContent) {
		this.textContent = textContent;
		isDone = false;
		textContent = new String("");
		priority_argument = new String("normal");
		type_argument = new String("normal");
		place_argument = new String("");
		start_date = null;
		end_date = null;
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
		if (textContent != null) {
			this.textContent = textContent;
		} else {
			this.textContent = "";
		}
	}

	public String getPriority_argument() {
		return priority_argument;
	}

	public void setPriority_argument(String priority_argument) {
		if (priority_argument != null) {
			this.priority_argument = priority_argument;
		} else {
			this.priority_argument = "normal";
		}
	}

	public String getType_argument() {
		return type_argument;
	}

	public void setType_argument(String type_argument) {
		if (type_argument != null) {
			this.type_argument = type_argument;
		} else {
			this.type_argument = "normal";
		}
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
		if (place_argument != null) {
			this.place_argument = place_argument;
		} else {
			this.place_argument = "";
		}
	}

	

	@Override
	public int compareTo(Task task) {
		try {
			return getEnd_date().compareTo(task.getEnd_date());
		} catch (NullPointerException ex) {
			if (this.getEnd_date() == null) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
	
		if (obj instanceof Task) {
			boolean isDateEq1, isDateEq2;
			Task taskCmp = (Task) obj;
			if (this.isDone == taskCmp.isDone && this.place_argument.equals(taskCmp.getPlace_argument())
					&& this.priority_argument.equals(taskCmp.getPriority_argument())
					&& this.textContent.equals(taskCmp.getTextContent())
					&& this.type_argument.equals(taskCmp.getType_argument())
					&& (this.lastServerUpdate == taskCmp.getLastServerUpdate())) {

				if (this.start_date != null && taskCmp.getStart_date() != null) {
					if (this.start_date.equals(taskCmp.getStart_date())) {
						isDateEq1 = true;
					} else {
						isDateEq1 = false;
					}
				}else{
					if (this.start_date == null && taskCmp.getStart_date() == null){
						isDateEq1 = true;
					}else{
						isDateEq1 = false;
					}
				}

				if (this.end_date != null && taskCmp.getEnd_date() != null) {
					if (this.end_date.equals(taskCmp.getEnd_date())) {
						isDateEq2 = true;
					} else {
						isDateEq2 = false;
					}
				}else{
					if (this.end_date == null && taskCmp.getEnd_date() == null){
						isDateEq2 = true;
					}else{
						isDateEq2 = false;
					}
				}

				return (isDateEq1 && isDateEq2);
			} else {
				return false;
			}

		}

		return super.equals(obj);
	}

	public String getEDateInStr(String type) {

		DateFormat df1 = new SimpleDateFormat(type);
		String dateStr = df1.format(this.getEnd_date());
		return dateStr;
	}

	public String getSDateInStr(String type) {

		DateFormat df1 = new SimpleDateFormat(type);
		String dateStr = df1.format(this.getStart_date());
		return dateStr;
	}
	
	/**
	 * @param task
	 * @return
	 */
	public boolean isModified() {
		return this.getgCalId() != null && !this.getgCalId().equals("") && this.getLastServerUpdate() != 0
				&& this.getLastLocalUpdate() != 0 && (this.getLastLocalUpdate() > this.getLastServerUpdate());
	}
	
	/**
	 * This function will determine if task is non-floating.
	 * @param task
	 * @return return true if task is non-floating else false.
	 */
	public boolean isNonFloatingTask() {
		return this.getEnd_date() != null;
	}

	/**This function will determine if task is yet to be synced.
	 * Pre-condition: task is not null.
	 * @param task
	 * @return return true if task is not yet synced, else false.
	 */
	public boolean isUnSyncedTask() {
		return this.getgCalId() != null && this.getgCalId().equals("");
	}
	
	/**
	 * This function will check if this task 
	 * @return
	 */
	private boolean isSyncedBefore() {
		return this.getgCalId() != null && !this.getgCalId().equals("");
	}

	/**
	 * This function compares if this task gCalId is the same.
	 * @param gCalId
	 * @return if gCalId is the same.
	 */
	public boolean compareGCalId(String gCalId) {
		return this.isSyncedBefore() && this.getgCalId().equals(gCalId);
	}
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Description: " + this.textContent);
		if (!this.type_argument.equals("")) {
			sb.append(" Type: " + this.type_argument);
		}
		if (!this.priority_argument.equals("")) {
			sb.append(" Priority: " + this.priority_argument);
		}

		if (!this.place_argument.equals("")) {
			sb.append(" Location: " + this.place_argument);
		}

		if (this.start_date != null) {
			sb.append(" Start Date: " + this.getSDateInStr(ParserFacade.DATE_FORMAT_TYPE_1));

		}

		if (this.end_date != null) {
			sb.append(" End date : " + this.getEDateInStr(ParserFacade.DATE_FORMAT_TYPE_1));
		}

		return sb.toString();
	}

}
