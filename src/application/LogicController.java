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
	            		 return ADDED_SUCCESS + command;
	            case 2:  printList(DataManager.getInstance().listAll(cmd.getTextContent()));
	            		 return command;
	            case 3:  DataManager.getInstance().changeStorageLocation(cmd);
	            		 return CHANGED_STORAGE_LOCATION_SUCCESS;
	            default: ;
	                     break;
	        }

			return "testing";
		}

		private static void printList(ArrayList<String> listAll) {
			System.out.println(LISTED_ALL_SUCCESS);
			for (int i=0; i<listAll.size(); i++) {
	            System.out.println(i + ". " + listAll.get(i));
	        }
		}

		public static int chooseLine(ArrayList<String> possibleItems) {
			printList(possibleItems);
			// TODO Auto-generated method stub
			return 0;
		}

		public static void newStorageLocation(Command cmd){
			// TODO
		}




}
