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

	            case 5:  //TODO
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
	            default: return "testing";
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
			Scanner sc = new Scanner(System.in);
			printList(possibleItems);
			Command cmd = Parser.getInstance().parseCommand(sc.nextLine());
			sc.close();
			return Integer.valueOf(cmd.getTextContent());
		}

		public static void newStorageLocation(Command cmd){
			// TODO
		}

}
