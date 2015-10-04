package application;

import java.util.ArrayList;

public class TasksFormatter {

	public static final int PLAIN_VIEW_TYPE = 1;
	public static final int DETAIL_VIEW_TYPE = 2;
	public static final int TIMELINE_VIEW_TYPE = 3;
	public static final int TYPE_VIEW_TYPE = 4;
	public static final int PRIORITY_VIEW_TYPE = 5;
	public static final int PLACE_VIEW_TYPE = 6;

	/**
	 * @param lists
	 *            : The lists of Task to be displayed
	 * @param typeOfFormatting
	 *            : the type of formatting format.
	 * @return the formatted text in String format.
	 */
	public static String format(ArrayList<Task> lists, int typeOfFormatting) {

		switch (typeOfFormatting) {

		case PLAIN_VIEW_TYPE:

			break;

		case DETAIL_VIEW_TYPE:

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

}
