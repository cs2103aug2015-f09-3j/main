package application.controller;

//@@author  A0130876B

import java.util.ArrayList;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Task;
import application.utils.GoogleCalendarUtility;
import application.utils.HelpCommands;
import application.utils.TasksFormatter;

public class CommandManager {
	private static final String NO_INTERNET_CONNECTION = "This feature does not work in offline mode, please check your internet connection.";
	private static final String DONE = "done";
	private static final String PLEASE_ENTER_TEXT = "Please enter text";
	private static final String NUMBER_FORMAT_EXCEPTION = "Number format exception in CommandManager";
	private static final String PREVIOUS_COMMAND_UNDONE = "Previous command undone";
	private static final String NO_PREVIOUS_COMMAND = "no previous command";
	private static final String MUL_MATCH_MSG = "There is more than one match, please choose from the following tasks.";
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = "\n";
	private static final int ZERO_INT = 0; 
	private static final String ADDED_SUCCESS = "Added ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS_FILE_EXIST = "Existing file in this directory so contents it it will be used";
	private static final String EMPTY_FILE = "There are no tasks to delete";
	private static final String DELETE_SUCCESS = "Successfully deleted: ";
	private static final String EDIT_SUCCESS = "Successfully edited: ";
	private static final String SET_DONE_SUCCESS = "Done task: ";
	private static final String SEARCH_RESULTS_NULL = "There are no tasks matching your search.";
	private static final String TASK_ALREADY_EXISTS = "The exact same task already exists in system.";
	private static final String EMPTY_FILE_EDIT = "There are no tasks to edit";
	private static final String WRONG_LINE_NUM	= "Wrong line number entered.";
	private static final String WRONG_DIRECTORY = "Wrong directory entered.";
	private static final String EMPTY_FILE_DONE = "There are no undone tasks that match your keyword.";
	private static final String SET_UNDONE_SUCCESS = "Successfully set undone:";
	private static final String SHOW_DONE_TASKS = "Tasks that are done: \n";

	private static ArrayList<Task> history = new ArrayList<Task>();
	private static int prevCommandType;
	private static ArrayList<Task> multipleMatchList = new ArrayList<Task>();

	/**
	 * This function executes the command that the user has input
	 * @param cmd : command to be executed
	 * @return : output to be shown to the user
	 */
	public static String executeCommand(Command cmd){
		assert cmd != null;
		int cmdType = cmd.getType();
		try{
			switch (cmdType) {
			    case Command.ADD_COMMAND_TYPE:
			    	return executeAddCommand(cmd);

			    case Command.LIST_COMMAND_TYPE:
			    	return executeListCommand(cmd);

			    case Command.CHANGE_STORAGE_COMMAND_TYPE:
			    	return executeChangeStorageCommand(cmd);

			    case Command.DELETE_COMMAND_TYPE:
			    	return executeDeleteCommand(cmd);

			    case Command.UNDO_COMMAND_TYPE:
			    	return executeUndoCommand();

			    case Command.EDIT_COMMAND_TYPE:
			    	return executeEditCommand(cmd);

			    case Command.DONE_COMMAND_TYPE:
			    	return executeSetDoneCommand(cmd);

			    case Command.UNDONE_COMMAND_TYPE:
			    	return executeUndoneCommand(cmd);

			    case Command.SEARCH_COMMAND_TYPE:
			    	return executeSearchCommand(cmd);

			    case Command.LIST_TODAY_COMMAND_TYPE:
			    	return executeTodayCommand(cmd);

			    case Command.HELP_COMMAND_TYPE:
			    	return executeHelpCommand();

			    case Command.SCHEDULE_COMMAND_TYPE:
			    	return executeScheduleCommand(cmd);

			    case Command.GOOGLE_ADD_COMMAND_TYPE:
			    	return executeGoogleQuickAddCommand(cmd);

			    default: return "testing-lc";
			}
		} catch (NullPointerException e) {
			LogManager.getInstance().log(cmd.getTextContent(), e.toString());
		}
		return PLEASE_ENTER_TEXT;
	}


	/**
	 * This function executes the Google Quick Add feature
	 * @param cmd : command to be executed
	 * @return : output to be shown to the user
	 */
	private static String executeGoogleQuickAddCommand(Command cmd) {
		Integer googleAddSuccess;
		if(GoogleCalendarUtility.hasInternetConnection()){
			googleAddSuccess= GoogleCalendarManagerInterface.getInstance().quickAddToGCal(cmd.getTextContent());
		}else{
			return NO_INTERNET_CONNECTION;
		}
		if(googleAddSuccess == DataManager.TASK_ADDED){
			return ADDED_SUCCESS + cmd.getTextContent();
		}else{
			return TASK_ALREADY_EXISTS;
		}
	}

	/**
	 * This function lets the user see his schedule
	 * @param cmd : command to be executed
	 * @return : output to be shown to the user
	 */
	private static String executeScheduleCommand(Command cmd) {
		ArrayList<Task> schedule = DataManager.getInstance().listAll(cmd);
		String sched = TasksFormatter.format(schedule, TasksFormatter.TIMELINE_VIEW_TYPE);
		return sched;
	}

	private static String executeHelpCommand() {
		String help = HelpCommands.displayHelp();
		return help;
	}

	/**
	 * This function lets the user see all his tasks for today
	 * @param cmd : command to be executed
	 * @return : today's tasks to be show to the user
	 */
	private static String executeTodayCommand(Command cmd) {
		ArrayList<Task> listToday = DataManager.getInstance().listToday(cmd);
		String today = TasksFormatter.format(listToday, TasksFormatter.TIMELINE_VIEW_TYPE);
		return today;
	}

	/**
	 * This function lets the user search for tasks with the keyword he has entered
	 * @param cmd : command with text content being the word to be searched in all tasks
	 * @return : string of tasks that matches the keyword
	 */
	private static String executeSearchCommand(Command cmd) {
		ArrayList<Task> searchResults = DataManager.getInstance().searchTasks(cmd);
		if (searchResults.size() == 0){
			return SEARCH_RESULTS_NULL;
		} else {
			String results = TasksFormatter.format(searchResults, TasksFormatter.DETAIL_VIEW_TYPE);
		return results;
		}
	}

	/**
	 * This function lets the user set undone to a task already done
	 * @param cmd : command with text content being the task to set undone
	 * @return : success of setting undone
	 */
	private static String executeUndoneCommand(Command cmd) {
		int setUndoneSuccess = ZERO_INT;
		if (isInteger(cmd)){
			setUndoneSuccess = DataManager.getInstance().setUndoneToTask(Integer.parseInt(cmd.getTextContent()));
		} else {
			setUndoneSuccess= DataManager.getInstance().setUndoneToTask(cmd);
		}
		if (setUndoneSuccess == DataManager.TASK_NOT_FOUND){
			return EMPTY_FILE_DONE;
		} else if (setUndoneSuccess == DataManager.MULTIPLE_MATCHES){
			String multipleTasksToSetUndone = TasksFormatter.format(multipleMatchList, TasksFormatter.DETAIL_VIEW_TYPE);
		    return MUL_MATCH_MSG + NEW_LINE + multipleTasksToSetUndone;
		} else if(setUndoneSuccess == DataManager.TASK_SET_TO_DONE){
		    return SET_UNDONE_SUCCESS + cmd.getTextContent();
		} else if(setUndoneSuccess == DataManager.TASK_UNDONE){
			return SET_UNDONE_SUCCESS + cmd.getTextContent();
		}else{
			return WRONG_LINE_NUM;
		}
	}

	/**
	 * This function lets the user set done to an undone task
	 * @param cmd : command with text content being the task to set done
	 * @return : success of setting done
	 */
	private static String executeSetDoneCommand(Command cmd) {
		int setDoneSuccess = ZERO_INT;
		if (isInteger(cmd) && prevCommandType == Command.DONE_COMMAND_TYPE){
			setDoneSuccess = DataManager.getInstance().setDoneToTask(Integer.parseInt(cmd.getTextContent()));
		} else {
			setDoneSuccess= DataManager.getInstance().setDoneToTask(cmd);
		}
		if (setDoneSuccess == DataManager.TASK_NOT_FOUND){
			return EMPTY_FILE_DONE;
		} else if (setDoneSuccess == DataManager.MULTIPLE_MATCHES){
			String multipleTasksToSetDone = TasksFormatter.format(multipleMatchList, TasksFormatter.DETAIL_VIEW_TYPE);
			prevCommandType = Command.DONE_COMMAND_TYPE;
		    return MUL_MATCH_MSG + NEW_LINE + multipleTasksToSetDone;
		} else if(setDoneSuccess == DataManager.TASK_SET_TO_DONE){
		    return SET_DONE_SUCCESS + cmd.getTextContent();
		}else{
			return WRONG_LINE_NUM;
		}
	}

	/**
	 * This function lets the user edit a task
	 * @param cmd : command with text content being the task to edit
	 * @return : output to show to the user if successful/failed to edit task
	 */
	private static String executeEditCommand(Command cmd) {
		int editSuccess = ZERO_INT;
		if (isInteger(cmd) && prevCommandType == Command.EDIT_COMMAND_TYPE){
			editSuccess = DataManager.getInstance().editTask(Integer.parseInt(cmd.getTextContent()));
		} else {
			editSuccess= DataManager.getInstance().editTask(cmd);
		}
		if (editSuccess == DataManager.TASK_NOT_FOUND){
			return EMPTY_FILE_EDIT;
		} else if (editSuccess == DataManager.MULTIPLE_MATCHES){
			String multipleTasksToEdit = TasksFormatter.format(multipleMatchList, TasksFormatter.DETAIL_VIEW_TYPE);
			prevCommandType = Command.EDIT_COMMAND_TYPE;
		    return MUL_MATCH_MSG + NEW_LINE + multipleTasksToEdit;
		} else if (editSuccess == DataManager.TASK_UPDATED){
		    return EDIT_SUCCESS + cmd.getTextContent();
		}else{
			return WRONG_LINE_NUM;
		}
	}

	/**
	 * This function lets the user undo the previous command
	 * @param cmd : command with no text content
	 * @return : output to show to the user if successful/failed to undo the previous command
	 */
	private static String executeUndoCommand() {
		prevCommandType = Command.UNDO_COMMAND_TYPE;
		if(DataManager.getInstance().undoPrevCommand() == DataManager.NO_PREV_COMMAND){
			return NO_PREVIOUS_COMMAND;
		} else {
			return PREVIOUS_COMMAND_UNDONE;
		}
	}
	/**
	 * This function lets the user delete a task that exists in the system.
	 * @param cmd : command with text content as the task to delete
	 * @return : output to show to the user if successful/failed to delete the task
	 */
	private static String executeDeleteCommand(Command cmd) {
		int deleteSuccess = ZERO_INT;
		if (isInteger(cmd) && prevCommandType == Command.DELETE_COMMAND_TYPE){
			deleteSuccess = DataManager.getInstance().removeTask(Integer.parseInt(cmd.getTextContent()));
		} else {
			deleteSuccess= DataManager.getInstance().removeTask(cmd);
		}
		if (deleteSuccess == DataManager.TASK_NOT_FOUND){
			return EMPTY_FILE;
		} else if (deleteSuccess == DataManager.MULTIPLE_MATCHES){
				String multipleTasks = TasksFormatter.format(multipleMatchList, TasksFormatter.DETAIL_VIEW_TYPE);
		    	prevCommandType = Command.DELETE_COMMAND_TYPE;
		      	return MUL_MATCH_MSG + NEW_LINE + NEW_LINE + multipleTasks;
		     	} else if(deleteSuccess == DataManager.TASK_REMOVED){
		    		return DELETE_SUCCESS + cmd.getTextContent();
		    	  }else{
		    		  return WRONG_LINE_NUM;
		    	  }
	}
	/**
	 * This function lets the user change the storage location
	 * @param cmd : command with text content as new storage location
	 * @return : output to show to the user if successful/failed change the storage location
	 */
	private static String executeChangeStorageCommand(Command cmd) {
		prevCommandType = Command.CHANGE_STORAGE_COMMAND_TYPE;
		int changePathSuccess = ZERO_INT;
		changePathSuccess = DataManager.getInstance().changeStorageLocation(cmd);
		if (changePathSuccess == LocalStorage.CHANGE_PATH_SUCCESS_FILE_NONEXIST){
			return CHANGED_STORAGE_LOCATION_SUCCESS + cmd.getTextContent();
		}else if(changePathSuccess == LocalStorage.CHANGE_PATH_SUCCESS_FILE_EXIST){
			return CHANGED_STORAGE_LOCATION_SUCCESS_FILE_EXIST;
		}else{
			return WRONG_DIRECTORY;
		}
	}
	/**
	 * This function lets the user view tasks in a certain format
	 * @param cmd : command with text content as either a keyword to list or type of list format
	 * @return : output to show to the user the list of tasks that match the keyword or type of list
	 */
	private static String executeListCommand(Command cmd) {
		prevCommandType = Command.LIST_COMMAND_TYPE;
		history.clear();
		String msg = EMPTY_STRING;
		ArrayList<Task> allTasks = new ArrayList<Task>();
		if (cmd.getTextContent().equals(DONE)){
			allTasks = DataManager.getInstance().checkIfDone();
			msg = SHOW_DONE_TASKS + TasksFormatter.format(allTasks, TasksFormatter.DETAIL_VIEW_TYPE);
		}else{
			allTasks = DataManager.getInstance().listAll(cmd);
			if (cmd.getTextContent() != EMPTY_STRING){
				if (isInteger(cmd)){
					int limit = Integer.parseInt(cmd.getTextContent());
					limitNumberOfTasks(allTasks, limit);
				} else {
					int type=determineViewType(cmd);
					switch (type){
						case TasksFormatter.PLAIN_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.PLAIN_VIEW_TYPE);
							break;
						case TasksFormatter.DETAIL_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.DETAIL_VIEW_TYPE);
							break;
						case TasksFormatter.TYPE_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.TYPE_VIEW_TYPE);
							break;
						case TasksFormatter.PRIORITY_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.PRIORITY_VIEW_TYPE);
							break;
						case TasksFormatter.PLACE_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.PLACE_VIEW_TYPE);
							break;
						case TasksFormatter.FLOATING_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.FLOATING_VIEW_TYPE);
							break;
						case TasksFormatter.TIMELINE_VIEW_TYPE:
							msg = TasksFormatter.format(allTasks, TasksFormatter.TIMELINE_VIEW_TYPE);
							break;
					}
				}
			}
		}
		if (msg.equals(EMPTY_STRING)){
			msg = TasksFormatter.format(allTasks, TasksFormatter.DETAIL_VIEW_TYPE);
		}
		if (cmd.getTextContent() != EMPTY_STRING && isInteger(cmd)){
			for (int i=0; i<history.size(); i++){
				allTasks.add(history.get(i));
			}
		}
		return msg;
	}

	/**
	 * This function adds a new task to the list
	 * @param cmd : command with text content as task to be added
	 * @return : output to be shown to the user
	 */
	private static String executeAddCommand(Command cmd) {
		Task taskToAdd = ParserFacade.getInstance().convertAddCommandtoTask(cmd);
		if(DataManager.getInstance().addNewTask(taskToAdd) == DataManager.TASK_ADDED){
			return ADDED_SUCCESS + taskToAdd.toString();
		}else{
			return TASK_ALREADY_EXISTS;
		}
	}

	/**
	 * This function checks which view type the user wants to view the tasks
	 * @param cmd : command which includes the view type
	 * @return : type of view the user wants to view the tasks
	 */
	private static int determineViewType(Command cmd) {
		assert cmd.getType() != null;
		if (cmd.getTextContent().equals(TasksFormatter.PLAIN_VIEW)){
			return TasksFormatter.PLAIN_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.DETAIL_VIEW)){
			return TasksFormatter.DETAIL_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.TYPE_VIEW)){
			return TasksFormatter.TYPE_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.TIMELINE_VIEW)){
			return TasksFormatter.TIMELINE_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.PRIORITY_VIEW)){
			return TasksFormatter.PRIORITY_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.PLACE_VIEW)){
			return TasksFormatter.PLACE_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.FLOATING_VIEW)){
			return TasksFormatter.FLOATING_VIEW_TYPE;
		} else if (cmd.getTextContent().equals(TasksFormatter.TIMELINE_VIEW)){
			return TasksFormatter.TIMELINE_VIEW_TYPE;
		}
		return TasksFormatter.DETAIL_VIEW_TYPE;
	}

	/**
	 * This function checks if the content of the command is an integer
	 * @param cmd : command with text content
	 * @return : true if content of command is an integer, false otherwise
	 * @exception : NumberFormatExeception if text content is not an integer.
	 */
	private static boolean isInteger(Command cmd) {
		boolean isInt = false;
		assert cmd.getTextContent() != null;
		try {
			Integer.parseInt(cmd.getTextContent());
			isInt = true;
		} catch (NumberFormatException e) {
			LogManager.getInstance().log(NUMBER_FORMAT_EXCEPTION, e.getMessage());
		}
		return isInt;
	}

	/**
	 * This function checks if there are duplicate tasks in the history
	 * @param task: the task to be compared to tasks in history
	 * @return : true if the tasks exists in history, false otherwise
	 */
	private static boolean isDuplicateTask(Task task) {
		for (int j=0; j<history.size(); j++){
			if (history.get(j) == task){
				return true;
			}
		}
		return false;
	}

	/**
	 * This function limits the number of tasks to view, as given by the user
	 * @param allTasks: ArrayList of Tasks of all tasks in system
	 * @param limit: the number of tasks to view
	 */
	private static void limitNumberOfTasks(ArrayList<Task> allTasks, int limit) {
		boolean flag = false;
		assert allTasks.size() != 0;
		if (allTasks.size() > limit){
			for (int i = allTasks.size()-1; i>=limit; i--){
				flag = isDuplicateTask(allTasks.get(i));
				if (flag==false){
					history.add(allTasks.remove(i));
				} else {
					allTasks.remove(i);
				}
				flag = false;
			}
		}
	}

	public static void setMultipleMatchList(ArrayList<Task> list){
		multipleMatchList = list;
	}
}