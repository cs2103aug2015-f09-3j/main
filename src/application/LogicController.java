package application;

import java.util.ArrayList;

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
		public String onCommandProcess(String command) throws InvalidCommandException{

			Command cmd = Parser.getInstance().parseCommand(command);
			int cmdType = cmd.getType();

	        switch (cmdType) {
	            case Command.ADD_COMMAND_TYPE:
	            	Task taskToAdd = Parser.getInstance().convertAddCommandtoTask(cmd);
	            	DataManager.getInstance().addNewTask(taskToAdd);
	            	return ADDED_SUCCESS + taskToAdd.toString();

	            case Command.LIST_COMMAND_TYPE:
	            	String msg = TasksFormatter.format(DataManager.getInstance().listAll(cmd), TasksFormatter.DETAIL_VIEW_TYPE);
	            	//String msg = printList(DataManager.getInstance().listAll(cmd.getTextContent()));
	            	return msg;

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
	            	Command prevCommand = determinePrevCommand();
	            	int undoCommand = determineUndoCommand(prevCommand);
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

	            default: return "testing-lc";
	        }

			return "testing-lc";
		}


		private Command determinePrevCommand() {
			// TODO Auto-generated method stub
			return null;
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

		private String printList(ArrayList<String> listAll) {
			//System.out.println(LISTED_ALL_SUCCESS);
			assert listAll.size() != 0;
			StringBuilder sb = new StringBuilder();

			for (int i=0; i<listAll.size(); i++) {
	           // System.out.println(i + ". " + listAll.get(i));
				sb.append(i + ". " + listAll.get(i) + "\n");
	        }

			return sb.toString();
		}

		public int chooseLine(ArrayList<String> possibleItems) {
			printList(possibleItems);
			return -1;
		}

}
