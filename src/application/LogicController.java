package application;

import java.util.ArrayList;

/**
 * This is a singleton class
 *
 * @author youlianglim
 *
 */
// @nghuiyirebecca
public class LogicController {

//	private static final String ADDED_SUCCESS = "Added ";
//	private static final String LISTED_ALL_SUCCESS = "Results: ";
//	private static final String CHANGED_STORAGE_LOCATION_SUCCESS = "Changed storage location: ";
//	private static final String EMPTY_FILE = "There are no tasks to delete";
//	private static final String DELETE_SUCCESS = "Successfully deleted: ";
//	private static final String EDIT_SUCCESS = "Successfully edited: ";
//	private static final String SET_DONE_SUCCESS = "Done task: ";
	private static LogicController instance;

	public static LogicController getInstance(){

		if(instance == null){
			instance = new LogicController();
		}
		return instance;
	}


		public String onCommandProcess(String command) throws InvalidCommandException{

			Command cmd = Parser.getInstance().parseCommand(command);

			return CommandManager.executeCommand(cmd);

		}

		/* FOR UNDO PURPOSES
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
		 */

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
