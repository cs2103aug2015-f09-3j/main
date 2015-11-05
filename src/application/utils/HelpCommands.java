package application.utils;

//@@nghuiyirebecca A0130876B
public class HelpCommands {
	private static final String GEN_INFO3 = "When connected to the internet, it will automatically sync with your Google Calendar.";
	private static final String GEN_INFO_2 = "Below are commands essential to the use of toDoo. \n";
	private static final String GEN_INFO_1 = "Welcome to the help page of toDoo! \n";
	private static final String TYPE_VIEW_INST = "View the tasks by respective types.";
	private static final String PLACE_VIEW_INST = "View the tasks sorted into different locations.";
	private static final String PRIORITY_VIEW_INST = "View the tasks that are sorted by priority.";
	private static final String TIMELINE_VIEW_INST = "View the tasks by end date.";
	private static final String DETAIL_VIEW_INST = "To view all the details of a task, incuding sdate, edate, priority, type & place";
	private static final String LIST_VIEW_INST = "There are a few ways to view the tasks. \n Type list followed by the different keywords: \n";
	private static final String PRIORITY_DESC = "Add a priority level by typing in /p followed by priority level.";
	private static final String EDATE_DESC = "Add an end date to a task by typing /edate followed by a date and time.";
	private static final String SDATE_DESC = "Add a start date to a task by typing /edate followed by a date and time.";
	private static final String TYPE_DESC = "Define a type of task by typing in /t followed by the type of task.";
	private static final String LOCATION_DESC = "Add a location to a task by typing in /place followed by the location.";
	private static final String PARAMETERS_DESC = "List of optional parameters to add when adding tasks.";
	private static final String SEARCH_DESC = "To search for a task, enter keyword followed by the search term.";
	private static final String DONE_DESC = "After completeing a task, enter keyword followed by task name.";
	private static final String EDIT_DESC = "Edit a task by typing in keyword, task name and parameter to change";
	private static final String UNDO_DESC = "Undo the last command entered.";
	private static final String CHANGE_STORAGE_LOCATION_DESC = "Change the storage location by typing in keyword followed by new location.";
	private static final String LIST_TO_NOTE = "Type \"done\" to see tasks that are done. By default, list views all undone tasks.";
	private static final String LIST_DESC = "To see all tasks that are still undone.";
	private static final String DELETE_TASK_DESC = "Delete a task by typing keyword followed by the task to delete.";
	private static final String ADD_TASK_DESC = "Add a task by typing in keyword followed by a new task with optional parameters.";
	private static final String GOOGLE_ADD_TASK_DESC = "Add a task directly to Google Calendar with optional parameters";
	private static final String EMPTY_STRING = "";

	//private static final int DESC_LIMIT = 50;
	private static final String OUTPUT_FORMAT_HEADER = "%-25s %-20s %-50s";
	private static final String OUTPUT_FORMAT = "%-3s %-25s %-20s %-50s";
	private static final String LIST_FORMAT = "%-3s %-15s %-50s";

	private static final String HELP_HEADER = "LIST OF COMMANDS: ";
	private static final String HELP_TABLE_HEADER = "    " + String.format(OUTPUT_FORMAT_HEADER, "Command", "Key", "Description");
	private static final String PARAMETER_HEADER = "LIST OF PARAMETERS: ";
	private static final String PARAMETER_TABLE_HEADER = "    " + String.format(OUTPUT_FORMAT_HEADER, "Parameter", "Key", "Description");



	public static String displayHelp(){
		StringBuilder sb = new StringBuilder();

		displayGeneralInformation(sb);
		sb.append("\n \n");
		displayCommands(sb);
		sb.append("\n \n");
		displayParameters(sb);
		sb.append("\n \n");
		displayListView(sb);
		return sb.toString();
	}

	private static void displayGeneralInformation(StringBuilder sb) {
		sb.append(GEN_INFO_1);
		sb.append(GEN_INFO_2);
		sb.append(GEN_INFO3);
	}

	private static void displayListView(StringBuilder sb) {
		int count = 1;
		sb.append(LIST_VIEW_INST);
		sb.append(String.format(LIST_FORMAT, "   ", "Key", "Description"));
		sb.append("\n");
		sb.append(String.format(LIST_FORMAT, count++, "detail", DETAIL_VIEW_INST));
		sb.append("\n");
		sb.append(String.format(LIST_FORMAT, count++, "timeline", TIMELINE_VIEW_INST));
		sb.append("\n");
		sb.append(String.format(LIST_FORMAT, count++, "place", PLACE_VIEW_INST));
		sb.append("\n");
		sb.append(String.format(LIST_FORMAT, count++, "priority", PRIORITY_VIEW_INST));
		sb.append("\n");
		sb.append(String.format(LIST_FORMAT, count++, "type", TYPE_VIEW_INST));
		sb.append("\n");
	}

	private static void displayParameters(StringBuilder sb) {
		int count = 1;
		sb.append(PARAMETER_HEADER);
		sb.append("\n");
		sb.append(PARAMETERS_DESC);
		sb.append("\n");
		sb.append(PARAMETER_TABLE_HEADER);
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Location", "\\place", LOCATION_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Type", "\\t", TYPE_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Start date", "\\sdate", SDATE_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "End date", "\\edate", EDATE_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Priority", "\\p", PRIORITY_DESC));
		sb.append("\n");
	}

	private static void displayCommands(StringBuilder sb) {
		int count = 1;
		sb.append(HELP_HEADER);
		sb.append("\n");
		sb.append(HELP_TABLE_HEADER);
		sb.append("\n");

		sb.append(String.format(OUTPUT_FORMAT, count++, "Add", "add / +", ADD_TASK_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Google Calendarr add", "googleAdd / ga", GOOGLE_ADD_TASK_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Delete", "delete / -", DELETE_TASK_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "List", "list / ls", LIST_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, LIST_TO_NOTE));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Change Storage Location", "changeStorage / cs", CHANGE_STORAGE_LOCATION_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Undo", "undo / <", UNDO_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Edit", "edit / et", EDIT_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Done task", "done / ok", DONE_DESC));
		sb.append("\n");
		sb.append(String.format(OUTPUT_FORMAT, count++, "Search", "search / s", SEARCH_DESC));
		sb.append("\n");
	}


}
