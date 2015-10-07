package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TasksFormatter {

	public static final String DATE_FORMAT_TYPE_1 = "dd/MM/yyyy HH:mma";
	public static final int PLAIN_VIEW_TYPE = 1;
	public static final int DETAIL_VIEW_TYPE = 2;
	public static final int TIMELINE_VIEW_TYPE = 3;
	public static final int TYPE_VIEW_TYPE = 4;
	public static final int PRIORITY_VIEW_TYPE = 5;
	public static final int PLACE_VIEW_TYPE = 6;
	
	private static final int DETAIL_DESCRIPTION_COUNT = 35;
	private static final int DETAIL_START_DATE_COUNT = 25;
	private static final int DETAIL_END_DATE_COUNT = 25;
	private static final int DETAIL_LOCATION_COUNT = 25;
	private static final int DETAIL_TYPE_COUNT = 15;
	private static final int DETAIL_PRIORITY_COUNT = 20;
	

	private static final String OUTPUT_FORMAT_HEADER = "%-30s %-20s %-20s %-20s %-15s %-15s";
	private static final String OUTPUT_FORMAT = "%-3d %-30s %-20s %-20s %-20s %-15s %-15s";


	private static final String DETAIL_VIEW_HEADER = "    " + String.format(OUTPUT_FORMAT_HEADER, "Description", "Start Date", "End Date", "Location", "Type", "Priority");
	
	/**
	 * @param lists
	 *            : The lists of Task to be displayed
	 * @param typeOfFormatting
	 *            : the type of formatting format.
	 * @return the formatted text in String format.
	 */
	public static String format(ArrayList<Task> lists, int typeOfFormatting) {
		StringBuilder sb = new StringBuilder();
		DateFormat df1 = new SimpleDateFormat(DATE_FORMAT_TYPE_1);
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

			break;

		case PRIORITY_VIEW_TYPE:

			break;

		case PLACE_VIEW_TYPE:

			break;

		}

		return sb.toString(); //stub
	}


	
	private static String replaceWithDotIfTooLong(String string, int limit){
		
		if(string.length() > (limit * 0.7)){
			return string.substring(0, (int)(limit * 0.7)) + "..";
			
		}		
		return string;
	}

	
}
