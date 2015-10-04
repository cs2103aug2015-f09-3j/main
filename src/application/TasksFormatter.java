package application;

import java.text.DateFormat;
import java.util.ArrayList;

public class TasksFormatter {

	public static final int PLAIN_VIEW_TYPE = 1;
	public static final int DETAIL_VIEW_TYPE = 2;
	public static final int TIMELINE_VIEW_TYPE = 3;
	public static final int TYPE_VIEW_TYPE = 4;
	public static final int PRIORITY_VIEW_TYPE = 5;
	public static final int PLACE_VIEW_TYPE = 6;
	
	private static final int DETAIL_DESCRIPTION_COUNT = 51;
	private static final int DETAIL_START_DATE_COUNT = 18;
	private static final int DETAIL_END_DATE_COUNT = 16;
	private static final int DETAIL_LOCATION_COUNT = 16;
	private static final int DETAIL_TYPE_COUNT = 12;
	private static final int DETAIL_PRIORITY_COUNT = 16;
	
	private static final String DETAIL_VIEW_HEADER = "Description\t\t\t\t\tStart Date\tEnd Date\tLocation\tType\tPriority\n\n";
	/**
	 * @param lists
	 *            : The lists of Task to be displayed
	 * @param typeOfFormatting
	 *            : the type of formatting format.
	 * @return the formatted text in String format.
	 */
	public static String format(ArrayList<Task> lists, int typeOfFormatting) {
		StringBuilder sb = new StringBuilder();
		switch (typeOfFormatting) {

		case PLAIN_VIEW_TYPE:
			int i = 1;
			for(Task task: lists){
				sb.append(String.format("%d. %s\n", i++, task.getTextContent()));
			}
			break;

		case DETAIL_VIEW_TYPE:
			
			sb.append(DETAIL_VIEW_HEADER);
			for(int c=0; c < DETAIL_VIEW_HEADER.length() + countNumOfTab(DETAIL_VIEW_HEADER); c++){
				sb.append("-");
			}
			sb.append("\n");
			for(Task task: lists){
				String s_date = DateFormat.getDateInstance().format(task.getStart_date());
				String e_date =  DateFormat.getDateInstance().format(task.getEnd_date());
				appendWithSpaceOffset(sb, task.getTextContent(), DETAIL_DESCRIPTION_COUNT);
				appendWithSpaceOffset(sb, s_date, DETAIL_START_DATE_COUNT);
				appendWithSpaceOffset(sb, e_date, DETAIL_END_DATE_COUNT);
				appendWithSpaceOffset(sb, task.getPlace_argument(), DETAIL_LOCATION_COUNT);
				appendWithSpaceOffset(sb, task.getType_argument(),  DETAIL_TYPE_COUNT);
				appendWithSpaceOffset(sb, task.getPriority_argument(),  DETAIL_PRIORITY_COUNT);
			}
			
			break;

		case TIMELINE_VIEW_TYPE:

			break;

		case TYPE_VIEW_TYPE:

			break;

		case PRIORITY_VIEW_TYPE:

			break;

		case PLACE_VIEW_TYPE:

			break;

		}

		return null; //stub
	}
	//PreCond : str must not be null
	public static void appendWithSpaceOffset(StringBuilder sb, String str, int totalSpace) {
		sb.append(str + getNoOfSpace(totalSpace - str.length()));
	}
	
	private static String getNoOfSpace(int n){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<n; i++){
			sb.append(" ");
		}
		return sb.toString();
	}

	private static int countNumOfTab(String str){
		int count = 0;
		
		for(int i=0; i<str.length(); i++){
			if(str.charAt(i) == '\t'){
				count++;
			}	
		}
		return count;
		
	}
	
}
