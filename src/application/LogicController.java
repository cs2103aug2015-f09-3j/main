package application;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This is a singleton class
 *
 * @author youlianglim
 *
 */
public class LogicController {

	private static final String ADDED_SUCCESS = "Added ";
	private static final String LISTED_ALL_SUCCESS = "Results: ";
	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
	private static final String EMPTY_FILE = "There are no tasks to delete";
	private static final String DELETE_SUCCESS = "Successfully deleted: ";
	private static final String EDIT_SUCCESS = "Successfully edited: ";
	private static final String SET_DONE_SUCCESS = "Done task: ";
	private static LogicController instance;

	public static LogicController getInstance(){

		if(instance == null){
			instance = new LogicController();
		}
		return instance;
	}

	// @nghuiyirebecca
		public String onCommandProcess(String command){

			Command cmd = Parser.getInstance().parseCommand(command);
			int cmdType = cmd.getType();

	        switch (cmdType) {
	            case 1:  DataManager.getInstance().addNewTask(cmd);
	            		 return ADDED_SUCCESS + cmd.getTextContent();

	            case 2:  printList(DataManager.getInstance().listAll(cmd.getTextContent()));
	            		 return command;

	            case 3:  DataManager.getInstance().changeStorageLocation(cmd);
	            		 return CHANGED_STORAGE_LOCATION_SUCCESS + cmd.getTextContent();

	            case 4:  int deleteSuccess= DataManager.getInstance().removeTask(cmd);
	            		 if (deleteSuccess == -1){
	            			 return EMPTY_FILE;
	            		 } else {
	            			 return DELETE_SUCCESS + cmd.getTextContent();
	            		 }

	            case 5:  Command prevCommand = determinePrevCommand();
	            		 int undoCommand = determineUndoCommand(prevCommand);
	            		 break;

	            case 6:  int editSuccess = DataManager.getInstance().editTask(cmd);
	            		 if (editSuccess == -1){
	            			 return EMPTY_FILE;
	            		 } else {
	            			 return EDIT_SUCCESS + cmd.getTextContent();
	            		 }

	            case 7:  int setDoneSuccess = DataManager.getInstance().setDoneToTask(cmd);
	            		 if (setDoneSuccess == -1){
	            			 return EMPTY_FILE;
	            		 } else {
	            			 return SET_DONE_SUCCESS + cmd.getTextContent();
	            		 }
	            default: return "testing-lc";
	        }

			return "testing-lc";
		}


		private int determineUndoCommand(Command prevCommand) {
			if (prevCommand.getType() == Command.ADD_COMMAND_TYPE){
				return Command.DELETE_COMMAND_TYPE;
			} else if (prevCommand.getType() == Command.DELETE_COMMAND_TYPE) {
				return Command.ADD_COMMAND_TYPE;
			} else if (prevCommand.getType() == Command.EDIT_COMMAND_TYPE) {
				return Command.EDIT_COMMAND_TYPE;
			} else if (prevCommand.getType() == Command.CHANGE_STORAGE_COMMAND_TYPE) {
				return Command.CHANGE_STORAGE_COMMAND_TYPE;
			} else if (prevCommand.getType() == Command.DONE_COMMAND_TYPE) {
				return Command.UNDONE_COMMAND_TYPE;
			} else if (prevCommand.getType() == Command.UNDONE_COMMAND_TYPE) {
				return Command.DONE_COMMAND_TYPE;
			}
		return 0;
	}

		private static void printList(ArrayList<String> listAll) {
			System.out.println(LISTED_ALL_SUCCESS);
			for (int i=0; i<listAll.size(); i++) {
	            System.out.println(i + ". " + listAll.get(i));
	        }
		}

		public static int chooseLine(ArrayList<String> possibleItems) {
			printList(possibleItems);
			DataManager.getInstance().savePossibleItems(possibleItems);
			return -1;
		}

		public static void newStorageLocation(Command cmd){
			// TODO
		}

}
