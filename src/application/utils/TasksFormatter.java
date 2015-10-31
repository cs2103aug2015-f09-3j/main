package application.utils;
//@@nghuiyirebecca A0130876B
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import application.controller.parser.ParserFacade;
import application.model.Task;

public class TasksFormatter {

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
	public static final String TIMELINE_VIEW = "timeline";
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

	private static final String TIMELINE_FORMAT_HEADER = "%-30s %-20s %-20s %-20s %-15s %-15s";
	private static final String TIMELINE_FORMAT = "%-3d %-30s %-20s %-20s %-20s %-15s %-15s";


	private static final String DETAIL_VIEW_HEADER = "    " + String.format(OUTPUT_FORMAT_HEADER, "Description", "Start Date", "End Date", "Location", "Type", "Priority");
	private static final String TIMELINE_VIEW_HEADER = "    " + String.format(TIMELINE_FORMAT_HEADER, "Description", "Start Date", "End Date", "Location", "Type", "Priority");
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
		switch (typeOfFormatting) {

		case PLAIN_VIEW_TYPE:
			int i = 1;
			for(Task task: lists){
				sb.append(String.format("%d. %s\n", i++, task.getTextContent()));
			}
			break;

		case DETAIL_VIEW_TYPE:

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

			break;

		case TIMELINE_VIEW_TYPE:
			break;

		case TYPE_VIEW_TYPE:
			ArrayList<String> namesOfTypes = namesOfTypes(lists);
			for (String type:namesOfTypes){
				sb.append("TYPE: "+type+"\n");
				int counter = 1;
				for (Task task:lists){
					if (type.equals(task.getType_argument())){
						sb.append(counter+". "+ task.getTextContent() +"\n");
						counter++;
					}
				}
				counter = 0;
				sb.append("\n");
			}

			break;

		case PRIORITY_VIEW_TYPE:
			ArrayList<String> namesOfPriorityLevel = namesOfPriorityLevels(lists);
			for (String priority:namesOfPriorityLevel){
				sb.append("PRIORITY: "+priority+"\n");
				int counter = 1;
				for (Task task:lists){
					if (priority.equals(task.getPriority_argument())){
						sb.append(counter+". "+ task.getTextContent() +"\n");
						counter++;
					}
				}
				counter = 0;
				sb.append("\n");
			}
			break;

		case PLACE_VIEW_TYPE:
			ArrayList<String> locationNames = namesOfLocation(lists);
			String nameOfLocation = EMPTY_STRING;
			for (String location:locationNames){
				if (location.equals(EMPTY_STRING)){
					nameOfLocation = NOT_APPLICABLE;
				} else {
					nameOfLocation = location;
				}
				sb.append("LOCATION: "+nameOfLocation+"\n");
				int counter = 1;
				for (Task task:lists){
					if (location.equals(task.getPlace_argument())){
						sb.append(counter+". "+ task.getTextContent() +"\n");
						counter++;
					}
				}
				counter = 0;
				sb.append("\n");
			}
			break;

		case FLOATING_VIEW_TYPE:
			boolean isFloat = false;
			int noOfFloatingTasks = 1;
			for (Task task:lists){
				if (task.getEnd_date() == null){
					isFloat = true;
				}
				if (isFloat){
					sb.append(noOfFloatingTasks + ". " + task.getTextContent() + "\n");
					noOfFloatingTasks++;
				}
			}
		}

		return sb.toString(); //stub
	}

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

	private static String replaceWithDotIfTooLong(String string, int limit){

		if(string.length() > (limit * 0.7)){
			return string.substring(0, (int)(limit * 0.7)) + "..";

		}
		return string;
	}


}
