package application.controller;


import java.util.ArrayList;
import java.util.Stack;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.Task;
import application.utils.HelpCommands;
import application.utils.TasksFormatter;

public class CommandManager {

	private static final String INVALID_COMMAND = "Invalid command";
	private static final int TASK_VIEW_LIMIT = 10;
	private static final String ADDED_SUCCESS = "Added ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
	private static final String EMPTY_FILE = "There are no tasks to delete";
	private static final String DELETE_SUCCESS = "Successfully deleted: ";
	private static final String EDIT_SUCCESS = "Successfully edited: ";
	private static final String SET_DONE_SUCCESS = "Done task: ";
	private static final String SEARCH_RESULTS_NULL = "There are no tasks matching your search.";
	private static ArrayList<Task> history = new ArrayList<Task>();

	public static String executeCommand(Command cmd){
		assert cmd != null;
		int cmdType = cmd.getType();

		switch (cmdType) {
		    case Command.ADD_COMMAND_TYPE:
		    	Task taskToAdd = ParserFacade.getInstance().convertAddCommandtoTask(cmd);
		    	DataManager.getInstance().addNewTask(taskToAdd);
		    	return ADDED_SUCCESS + taskToAdd.toString();

		    case Command.LIST_COMMAND_TYPE:
		    	history.clear();
		    	ArrayList<Task> allTasks = DataManager.getInstance().listAll(cmd);
		    	limitNumberOfTasks(allTasks);
		    	String msg = TasksFormatter.format(allTasks, TasksFormatter.DETAIL_VIEW_TYPE);
		    	//String msg = printList(DataManager.getInstance().listAll(cmd.getTextContent()));
		    	for (int i=0; i<history.size(); i++){
		    		allTasks.add(history.get(i));
		    	}
		    	return msg;

		    case Command.LIST_NEXT_COMMAND_TYPE:
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
		    	}

		    case Command.CHANGE_STORAGE_COMMAND_TYPE:
		    	DataManager.getInstance().changeStorageLocation(cmd);
		    	return CHANGED_STORAGE_LOCATION_SUCCESS + cmd.getTextContent();

		    case Command.DELETE_COMMAND_TYPE:
		    	int deleteSuccess= DataManager.getInstance().removeTask(cmd);
				if (deleteSuccess == -1){
					return EMPTY_FILE;
				} else if (deleteSuccess == -2){
			      		   return "";
		         	   } else {
		        		   return DELETE_SUCCESS + cmd.getTextContent();
		        	   }

		    case Command.UNDO_COMMAND_TYPE:
		    	DataManager.undoPrevCommand();
				break;

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
		return "testing";
	}


	private static boolean checkDuplicateTask(Task task) {
		for (int j=0; j<history.size(); j++){
			if (history.get(j) == task){
				return true;
			}
		}
		return false;
	}


	private static void limitNumberOfTasks(ArrayList<Task> allTasks) {
		boolean flag = false;
		if (allTasks.size() > TASK_VIEW_LIMIT){
			for (int i = allTasks.size()-1; i>=TASK_VIEW_LIMIT; i--){
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