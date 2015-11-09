package application.utils;
//@@author A0130876B
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import application.controller.LogManager;
import application.controller.parser.ParserFacade;
import application.model.Task;

public class TasksFormatter {

	private static final String TASKS_FORMATTER_LOG = "TasksFormatter log";
	private static final String LOCATION_HEADER = "LOCATION: ";
	private static final String PRIORITY_HEADER = "PRIORITY: ";
	private static final String FULLSTOP = ". ";
	private static final String TYPE_HEADER = "TYPE: ";
	private static final String OUTSTANDING_TASKS_INFO = "Outstanding tasks (no due date): \n";
	private static final String DUE = " DUE:   ";
	private static final String NEW_LINE = "\n";
	private static final String TIMELINE_INST = "SCHEDULE \n \n";
	private static final String NOT_APPLICABLE = "NOT APPLICABLE.";
	public static final int PLAIN_VIEW_TYPE = 1;
	public static final int DETAIL_VIEW_TYPE = 2;
	public static final int TIMELINE_VIEW_TYPE = 3;
	public static final int TYPE_VIEW_TYPE = 4;
	public static final int PRIORITY_VIEW_TYPE = 5;
	public static final int PLACE_VIEW_TYPE = 6;
	public static final int FLOATING_VIEW_TYPE = 7;

	public static final String PLAIN_VIEW = "plain";
	public static final String DETAIL_VIEW = "detail";
	public static final String TIMELINE_VIEW = "schedule";
	public static final String TYPE_VIEW = "type";
	public static final String PRIORITY_VIEW = "priority";
	public static final String PLACE_VIEW = "place";
	public static final String FLOATING_VIEW = "floating";

	private static final int DETAIL_DESCRIPTION_COUNT = 35;
	private static final int DETAIL_LOCATION_COUNT = 25;
	private static final int DETAIL_TYPE_COUNT = 15;
	private static final int DETAIL_PRIORITY_COUNT = 20;


	private static final String OUTPUT_FORMAT_HEADER = "%-30s %-20s %-20s %-20s %-15s %-15s";
	private static final String OUTPUT_FORMAT = "%-3d %-30s %-20s %-20s %-20s %-15s %-15s";

	private static final String DETAIL_VIEW_HEADER = "    " + String.format(OUTPUT_FORMAT_HEADER, "Description", "Start Date", "End Date", "Location", "Type", "Priority");
	private static final String EMPTY_STRING = "";


	/**
	 * @param lists
	 *            : The lists of Task to be displayed
	 * @param typeOfFormatting
	 *            : the type of formatting format.
	 * @return the formatted text in String format.
	 */
	public static String format(ArrayList<Task> lists, int typeOfFormatting) {
			StringBuilder sb = new StringBuilder();
			DateFormat df1 = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_101);
			DateFormat df2 = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_2);
			DateFormat df3 = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_200);
		try {
			switch (typeOfFormatting) {

			case PLAIN_VIEW_TYPE:
				showPlainView(lists, sb);
				break;

			case DETAIL_VIEW_TYPE:
				showDetailView(lists, sb, df1);
				break;

			case TIMELINE_VIEW_TYPE:
				showTimelineView(lists, sb, df2, df3);
				break;

			case TYPE_VIEW_TYPE:
				showTypeView(lists, sb);

				break;

			case PRIORITY_VIEW_TYPE:
				showPriorityView(lists, sb);
				break;

			case PLACE_VIEW_TYPE:
				showPlaceView(lists, sb);
				break;

			case FLOATING_VIEW_TYPE:
				showFloatingView(lists, sb);
			}

		} catch (NullPointerException e){
			LogManager.getInstance().log(TASKS_FORMATTER_LOG, e.toString());
		}
		return sb.toString();
	}

	//return a String of floating tasks
	private static void showFloatingView(ArrayList<Task> lists, StringBuilder sb) {
		boolean isFloat = false;
		int noOfFloatingTasks = 1;
		for (Task task:lists){
			if (task.getEnd_date() == null){
				isFloat = true;
			}
			if (isFloat){
				sb.append(noOfFloatingTasks + FULLSTOP + task.getTextContent() + NEW_LINE);
				noOfFloatingTasks++;
			}
		}
	}

	//return a String of tasks listed by location
	private static void showPlaceView(ArrayList<Task> lists, StringBuilder sb) {
		ArrayList<String> locationNames = namesOfLocation(lists);
		String nameOfLocation = EMPTY_STRING;
		for (String location:locationNames){
			if (location.equals(EMPTY_STRING)){
				nameOfLocation = NOT_APPLICABLE;
			} else {
				nameOfLocation = location;
			}
			sb.append(LOCATION_HEADER+nameOfLocation+NEW_LINE);
			int counter = 1;
			for (Task task:lists){
				if (location.equals(task.getPlace_argument())){
					sb.append(counter+FULLSTOP+ task.getTextContent() +NEW_LINE);
					counter++;
				}
			}
			counter = 0;
			sb.append(NEW_LINE);
		}
	}

	//return a string of tasks listed by priority
	private static void showPriorityView(ArrayList<Task> lists, StringBuilder sb) {
		ArrayList<String> namesOfPriorityLevel = namesOfPriorityLevels(lists);
		for (String priority:namesOfPriorityLevel){
			sb.append(PRIORITY_HEADER+priority+NEW_LINE);
			int counter = 1;
			for (Task task:lists){
				if (priority.equals(task.getPriority_argument())){
					sb.append(counter+FULLSTOP+ task.getTextContent() +NEW_LINE);
					counter++;
				}
			}
			counter = 0;
			sb.append(NEW_LINE);
		}
	}

	//return a string of tasks listed by type of task
	private static void showTypeView(ArrayList<Task> lists, StringBuilder sb) {
		ArrayList<String> namesOfTypes = namesOfTypes(lists);
		for (String type:namesOfTypes){
			sb.append(TYPE_HEADER+type+NEW_LINE);
			int counter = 1;
			for (Task task:lists){
				if (type.equals(task.getType_argument())){
					sb.append(counter+FULLSTOP+ task.getTextContent() +NEW_LINE);
					counter++;
				}
			}
			counter = 0;
			sb.append(NEW_LINE);
		}
	}

	//return a string of tasks by its date/time
	private static void showTimelineView(ArrayList<Task> lists, StringBuilder sb, DateFormat df2, DateFormat df3) {
		sb.append(TIMELINE_INST);
		ArrayList<Task> sortedByEDate = new ArrayList<Task>();
		ArrayList<Task> floating = new ArrayList<Task>();
		for (Task task:lists){
			if (task.getEnd_date()!=null){
				sortedByEDate.add(task);
			} else {
				floating.add(task);
			}
		}
		ArrayList<Date> allDates = new ArrayList<Date>();
		getAllPossibleEDates(sortedByEDate, allDates);
		for (Date date:allDates){
			String thisDate = df2.format(date);
			sb.append(thisDate);
			sb.append(NEW_LINE);
			listAllTaskOnDate(sb, df2, df3, sortedByEDate, thisDate);
			sb.append("\n");
		}
		if (floating.size()>0){
			sb.append(NEW_LINE + OUTSTANDING_TASKS_INFO);
			int counter = 1;
			for (Task task:floating){
				sb.append(counter + FULLSTOP + task.getTextContent() + NEW_LINE);
				counter++;
			}
		}
	}
	/**
	 * This function adds to StringBuilder the tasks with due date on thisDate
	 * @param sb - StringBuilder to add string
	 * @param df2, df3: Different date formats for addition to sb
	 * @param sortedByEDate: ArrayList<Task> that contains all tasks with end dates
	 * @param thisDate: the date on which the tasks to list are on.
	 */
	private static void listAllTaskOnDate(StringBuilder sb, DateFormat df2, DateFormat df3,
			ArrayList<Task> sortedByEDate, String thisDate) {
		for (Task task:sortedByEDate){
			if (thisDate.equals(df2.format(task.getEnd_date()))){
				if (task.getStart_date()!=null){
					String start_time = df3.format(task.getStart_date());
					String end_time = df3.format(task.getEnd_date());
					sb.append("[" + start_time + " - " + end_time + "]   ");
					sb.append(task.getTextContent() + NEW_LINE);
				} else {
					String time = df3.format(task.getEnd_date());
					sb.append(time+DUE);
					sb.append(task.getTextContent() + NEW_LINE);
				}
			}
		}
	}

	/**
	 * This function compiles an ArrayList<Date> based on the number of different endDates/
	 * @param ArrayList<Task> sortedByEDate: ArrayList<Task> with end dates
	 * @param ArrayList<Date> allDates: ArrayList<Date> of different end dates
	 */
	private static void getAllPossibleEDates(ArrayList<Task> sortedByEDate, ArrayList<Date> allDates) {
		int numDates = 1;
		if (sortedByEDate.size() == 1){
			allDates.add(sortedByEDate.get(0).getEnd_date());
		} else if (sortedByEDate.size() > 1){
				//allDates.add(sortedByEDate.get(0).getEnd_date());
				for (int a=1; a<sortedByEDate.size()-1; a++){
					allDates.add(sortedByEDate.get(a).getEnd_date());
					for (int b=a+1; b<sortedByEDate.size(); b++){
						if (isSameEdate(sortedByEDate.get(a).getEnd_date(), sortedByEDate.get(b).getEnd_date())){
							allDates.remove(sortedByEDate.get(a).getEnd_date());
						} else {
							numDates++;
						}
					}
				}
				if (numDates == 1) {
					allDates.add(sortedByEDate.get(0).getEnd_date());
				}
			}
	}

	//returns a string with all the relevant details
	private static void showDetailView(ArrayList<Task> lists, StringBuilder sb, DateFormat df1) {
		sb.append(DETAIL_VIEW_HEADER);

		sb.append("\n");
		int count = 1;
		for(Task task: lists){

			String s_date = "";
			String e_date = "";

			if(task.getStart_date() != null){
				s_date = df1.format(task.getStart_date());
				//s_date = DateFormat.getDateInstance().format(task.getStart_date());
			}
			if(task.getEnd_date() != null){
				e_date = df1.format(task.getEnd_date());
				//e_date =  DateFormat.getDateInstance().format(task.getEnd_date());
			}
			sb.append(String.format(OUTPUT_FORMAT, count++, replaceWithDotIfTooLong(task.getTextContent(),DETAIL_DESCRIPTION_COUNT), s_date, e_date, replaceWithDotIfTooLong(task.getPlace_argument(),DETAIL_LOCATION_COUNT)
					,replaceWithDotIfTooLong(task.getType_argument(),DETAIL_TYPE_COUNT),replaceWithDotIfTooLong(task.getPriority_argument(),DETAIL_PRIORITY_COUNT)));


			sb.append("\n");
		}
	}

	//returns a string of tasks
	private static void showPlainView(ArrayList<Task> lists, StringBuilder sb) {
		int i = 1;
		for(Task task: lists){
			sb.append(String.format("%d. %s" + NEW_LINE, i++, task.getTextContent()));
		}
	}

	/**
	 * This function checks if the two end dates entered are the same date
	 * @param Date d1, d2:
	 *            : The two dates to be compared
	 * @return boolean if the two dates are the same
	 */
	private static boolean isSameEdate(Date d1, Date d2) {
		assert d1 != null;
		assert d2 != null;
		DateFormat df = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_2);
		String d1Str = df.format(d1);
		String d2Str = df.format(d2);
		if (d2Str.equals(d1Str)){
			return true;
		}
		return false;
	}

	/**
	 * This function compiles a list of locations the tasks are located at
	 * @param lists
	 *            : ArrayList of tasks to be checked
	 * @return ArrayList of String of locations the tasks are at
	 */
	private static ArrayList<String> namesOfLocation(ArrayList<Task> lists) {
		boolean isAdded = false;
		ArrayList<String> locationNames = new ArrayList<String>();
		for(Task task: lists){
			String locationOfTask = task.getPlace_argument();
			for (String inList:locationNames){
				if (inList.equals(locationOfTask)){
					isAdded = true;
				}
			}
			if (isAdded == false){
				locationNames.add(locationOfTask);
			}
			isAdded = false;
		}
		return locationNames;
	}

	/**
	 * This function compiles the different priority levels the tasks are at
	 * @param lists
	 *            : ArrayList of tasks to be checked
	 * @return ArrayList of String of the different priority levels
	 */
	private static ArrayList<String> namesOfPriorityLevels(ArrayList<Task> lists) {
		boolean isAdded = false;
		ArrayList<String> namesOfPriorityLevels = new ArrayList<String>();
		for(Task task: lists){
			String priorityOfTask = task.getPriority_argument();
			for (String inList:namesOfPriorityLevels){
				if (inList.equals(priorityOfTask)){
					isAdded = true;
				}
			}
			if (isAdded == false){
				namesOfPriorityLevels.add(priorityOfTask);
			}
			isAdded = false;
		}
		return namesOfPriorityLevels;
	}

	/**
	 * This function compiles a list of different types of tasks
	 * @param lists
	 *            : ArrayList of tasks to be checked
	 * @return ArrayList of String of type of tasks
	 */
	private static ArrayList<String> namesOfTypes(ArrayList<Task> list){
		boolean isAdded = false;
		ArrayList<String> namesOfTypes = new ArrayList<String>();
		for(Task task: list){
			String typeOfTask = task.getType_argument();
			for (String inList:namesOfTypes){
				if (inList.equals(typeOfTask)){
					isAdded = true;
				}
			}
			if (isAdded == false){
				namesOfTypes.add(typeOfTask);
			}
			isAdded = false;
		}
		return namesOfTypes;
	}
	//@@author  A0125975U
	private static String replaceWithDotIfTooLong(String string, int limit){

		if(string.length() > (limit * 0.7)){
			return string.substring(0, (int)(limit * 0.7)) + "..";

		}
		return string;
	}


}
