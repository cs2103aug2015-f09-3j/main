package application.controller;


import java.util.ArrayList;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.Task;
import application.utils.HelpCommands;
import application.utils.TasksFormatter;

public class CommandManager {

	private static final String MUL_MATCH_MSG = "There is more than one match, please choose from the following tasks.";
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = "\n";
	private static final String INVALID_COMMAND = "Invalid command";
	private static final int TASK_VIEW_LIMIT = 10;
	private static final int ZERO_INT = 0;
	private static final String ADDED_SUCCESS = "Added ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
	private static final String EMPTY_FILE = "There are no tasks to delete";
	private static final String DELETE_SUCCESS = "Successfully deleted: ";
	private static final String EDIT_SUCCESS = "Successfully edited: ";
	private static final String SET_DONE_SUCCESS = "Done task: ";
	private static final String SEARCH_RESULTS_NULL = "There are no tasks matching your search.";
	private static final String TASK_ALREADY_EXISTS = "The exact same task already exists in system.";
	private static ArrayList<Task> history = new ArrayList<Task>();

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
		    	ArrayList<Task> allTasks = DataManager.getInstance().listAll(cmd);
		    	if (cmd.getTextContent() != EMPTY_STRING){
		    		int limit = Integer.parseInt(cmd.getTextContent());
		    		limitNumberOfTasks(allTasks, limit);
		    	}
		    	String msg = TasksFormatter.format(allTasks, TasksFormatter.DETAIL_VIEW_TYPE);
		    	if (cmd.getTextContent() != EMPTY_STRING){
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
		    	DataManager.getInstance().changeStorageLocation(cmd);
		    	return CHANGED_STORAGE_LOCATION_SUCCESS + cmd.getTextContent();

		    case Command.DELETE_COMMAND_TYPE:
		    	int deleteSuccess = ZERO_INT;
		    	if (isInteger(cmd)){
		    		deleteSuccess = DataManager.getInstance().removeTask(Integer.parseInt(cmd.getTextContent()));
		    	} else {
			    	deleteSuccess= DataManager.getInstance().removeTask(cmd);
		    	}
				if (deleteSuccess == DataManager.TASK_NOT_FOUND){
					return EMPTY_FILE;
				} else if (deleteSuccess == DataManager.MULTIPLE_MATCHES){
						String multipleTasks = TasksFormatter.format(Data.getSearchList(), TasksFormatter.DETAIL_VIEW_TYPE);
				      	return MUL_MATCH_MSG + NEW_LINE + multipleTasks;
			         	} else {
			        		return DELETE_SUCCESS + cmd.getTextContent();
			        	  }


		    case Command.UNDO_COMMAND_TYPE:
		    	if(DataManager.undoPrevCommand() == DataManager.NO_PREV_COMMAND)
		    		return "no previous command";
		    	else
		    		return "previous command undone";

		    case Command.EDIT_COMMAND_TYPE:
		    	int editSuccess = DataManager.getInstance().editTask(cmd);
				if (editSuccess == -1){
				    return EMPTY_FILE;
				} else {
		  			 return EDIT_SUCCESS + cmd.getTextContent();
				}

		    case Command.DONE_COMMAND_TYPE:
		    	int setDoneSuccess = DataManager.getInstance().setDoneToTask(cmd);
		    	if (setDoneSuccess == -1){
		    		return EMPTY_FILE;
		    	} else {
		    		return SET_DONE_SUCCESS + cmd.getTextContent();
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
		    	String today = TasksFormatter.format(DataManager.getInstance().listToday(cmd), TasksFormatter.DETAIL_VIEW_TYPE);
		    	return today;

		    case Command.HELP_COMMAND_TYPE:
		    	String help = HelpCommands.displayHelp();
		    	return help;

		    default: return "testing-lc";
		}
		//return "testing";
	}


	private static boolean isInteger(Command cmd) {
		boolean isInt = false;
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
}