package application.controller;

//@@nghuiyirebecca A0130876B

import java.util.ArrayList;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Task;
import application.utils.HelpCommands;
import application.utils.TasksFormatter;

public class CommandManager {

	private static final String MUL_MATCH_MSG = "There is more than one match, please choose from the following tasks.";
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = "\n";
	private static final int ZERO_INT = 0;
	private static final String ADDED_SUCCESS = "Added ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
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

	public static String executeCommand(Command cmd){
		assert cmd != null;
		int cmdType = cmd.getType();





		switch (cmdType) {
		    case Command.ADD_COMMAND_TYPE:
		    	Task taskToAdd = ParserFacade.getInstance().convertAddCommandtoTask(cmd);
		    	if(DataManager.getInstance().addNewTask(taskToAdd) == DataManager.TASK_ADDED){
		    		return ADDED_SUCCESS + taskToAdd.toString();
		    	}else{
		    		return TASK_ALREADY_EXISTS;
		    	}
		    case Command.LIST_COMMAND_TYPE:
		    	history.clear();
		    	String msg = EMPTY_STRING;
		    	ArrayList<Task> allTasks = new ArrayList<Task>();
		    	if (cmd.getTextContent().equals("done")){
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
		    	//String msg = printList(DataManager.getInstance().listAll(cmd.getTextContent()));
		    	return msg;

		   /* case Command.LIST_NEXT_COMMAND_TYPE:
		    	if (history.size() == 0 || DataManager.getInstance().listAll(cmd).size() <= TASK_VIEW_LIMIT){
		    		history.clear();
		    		return INVALID_COMMAND;
		    	} else{
		    		//ArrayList<Task> allPossibleTasks = DataManager.getInstance().listAll(cmd);
		    		ArrayList<Task> nextTasks = history;
		    		assert nextTasks.size() != 0;
		    		limitNumberOfTasks(nextTasks);
			    	String listnext = TasksFormatter.format(nextTasks, TasksFormatter.DETAIL_VIEW_TYPE);
			    	return listnext;
		    	} */

		    case Command.CHANGE_STORAGE_COMMAND_TYPE:
		    	int changePathSuccess = ZERO_INT;
		    	changePathSuccess = DataManager.getInstance().changeStorageLocation(cmd);
		    	if (changePathSuccess == LocalStorage.CHANGE_PATH_SUCCESS){
		    		return CHANGED_STORAGE_LOCATION_SUCCESS + cmd.getTextContent();
		    	}else{
		    		return WRONG_DIRECTORY;
		    	}
		    case Command.DELETE_COMMAND_TYPE:
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


		    case Command.UNDO_COMMAND_TYPE:
		    	if(DataManager.undoPrevCommand() == DataManager.NO_PREV_COMMAND)
		    		return "no previous command";
		    	else
		    		return "Previous command undone";

		    case Command.EDIT_COMMAND_TYPE:
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

		    case Command.DONE_COMMAND_TYPE:
		    	int setDoneSuccess = ZERO_INT;
		    	if (isInteger(cmd)){
		    		setDoneSuccess = DataManager.getInstance().setDoneToTask(Integer.parseInt(cmd.getTextContent()));
		    	} else {
			    	setDoneSuccess= DataManager.getInstance().setDoneToTask(cmd);
		    	}
				if (setDoneSuccess == DataManager.TASK_NOT_FOUND){
					return EMPTY_FILE_DONE;
				} else if (setDoneSuccess == DataManager.MULTIPLE_MATCHES){
					String multipleTasksToSetDone = TasksFormatter.format(multipleMatchList, TasksFormatter.DETAIL_VIEW_TYPE);
				    return MUL_MATCH_MSG + NEW_LINE + multipleTasksToSetDone;
			    } else if(setDoneSuccess == DataManager.TASK_SET_TO_DONE){
			        return SET_DONE_SUCCESS + cmd.getTextContent();
			    }else{
			    	return WRONG_LINE_NUM;
			    }

		    case Command.UNDONE_COMMAND_TYPE:
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
			    }else{
			    	return WRONG_LINE_NUM;
			    }

		    case Command.SEARCH_COMMAND_TYPE:
		    	ArrayList<Task> searchResults = DataManager.getInstance().searchTasks(cmd);
		    	if (searchResults.size() == 0){
		    		return SEARCH_RESULTS_NULL;
		    	} else {
		    		String results = TasksFormatter.format(searchResults, TasksFormatter.DETAIL_VIEW_TYPE);
		    	return results;
		    	}

		    case Command.LIST_TODAY_COMMAND_TYPE:
		    	ArrayList<Task> listToday = DataManager.getInstance().listToday(cmd);
		    	String today = TasksFormatter.format(listToday, TasksFormatter.TIMELINE_VIEW_TYPE);
		    	return today;

		    case Command.HELP_COMMAND_TYPE:
		    	String help = HelpCommands.displayHelp();
		    	return help;

		    case Command.SCHEDULE_COMMAND_TYPE:
		    	ArrayList<Task> schedule = DataManager.getInstance().listAll(cmd);
		    	String sched = TasksFormatter.format(schedule, TasksFormatter.TIMELINE_VIEW_TYPE);
		    	return sched;

		    case Command.GOOGLE_ADD_COMMAND_TYPE:
		    	Integer googleAddSuccess= GoogleCalendarManager.getInstance().quickAddToGCal(cmd.getTextContent());
		    	if(googleAddSuccess == DataManager.TASK_ADDED){
		    		return ADDED_SUCCESS + cmd.getTextContent();
		    	}else{
		    		return TASK_ALREADY_EXISTS;
		    	}

		    default: return "testing-lc";
		}


		//return "testing";
	}

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


	private static boolean isInteger(Command cmd) {
		boolean isInt = false;
		assert cmd.getTextContent() != null;
		try {
			Integer.parseInt(cmd.getTextContent());
			isInt = true;
		}
		catch (NumberFormatException e) {

		}
		return isInt;
	}


	private static boolean checkDuplicateTask(Task task) {
		for (int j=0; j<history.size(); j++){
			if (history.get(j) == task){
				return true;
			}
		}
		return false;
	}


	private static void limitNumberOfTasks(ArrayList<Task> allTasks, int limit) {
		boolean flag = false;
		assert allTasks.size() != 0;
		if (allTasks.size() > limit){
			for (int i = allTasks.size()-1; i>=limit; i--){
				flag = checkDuplicateTask(allTasks.get(i));
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