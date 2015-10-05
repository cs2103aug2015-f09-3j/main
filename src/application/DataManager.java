package application;

import java.util.ArrayList;
/*
 * @author: Lim Qi Wen
 */
public class DataManager {
	// Integer return values => status codes to be defined

	private LocalStorage file;

	public static DataManager instance = null;

	private DataManager() {
		file = new LocalStorage();
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public String addNewTask(Task taskToAdd) {
		String details = taskToAdd.getTextContent();	
		//TODO : Need a better way to store the task, recommended to use a library google gson
		// to convert the Task into a text-based json file.
		for (int i = 0; i < taskToAdd.getParameter().size(); i++) {
			details = details + " " + '\\' + taskToAdd.getParameter().get(i).getParaArg();
		}
		file.writeTask(details); 
		file.sort();
		return details;
	}

	public ArrayList<String> listAll(String para) {
		ArrayList<String> taskList = new ArrayList<String>();
		taskList = file.readFile();
		String taskDetails;
		for (int i = 0; i < taskList.size(); i++) {
			taskDetails = taskList.get(i);
			// TODO filter list by types of task
			if (taskDetails.contains("" + '\\')) {
				taskDetails = taskDetails.substring(0, taskDetails.indexOf('\\')).trim();
				taskList.remove(i);
				taskList.add(i, taskDetails);
			}
		}
		return taskList;
	}

	public Integer removeTask(Command cmd) {
		ArrayList<String> possibleItems = new ArrayList<String>();
		possibleItems = file.search(cmd.getTextContent());
		if (possibleItems.size() == 1) {
			if(file.delete(possibleItems.get(0)))
				return 0;
			else
				return -1;
		} else if (possibleItems.size() <= 0) {
			return -1;
		} else {
			int lineToDelete = LogicController.getInstance().chooseLine(possibleItems);
			file.delete(possibleItems.get(lineToDelete - 1));
			return 0;
		}
	}

	public Integer changeStorageLocation(Command cmd) {
		return file.changePath(cmd.getTextContent());
	}

	public Integer editTask(Command cmd) {
		ArrayList<String> possibleItems = new ArrayList<String>();
		possibleItems = file.search(cmd.getTextContent());
		if (possibleItems.size() == 1) {
			//TODO
			return 0;
		}else if (possibleItems.size() <= 0) {
			return -1;
		} else {
			int lineToEdit = LogicController.getInstance().chooseLine(possibleItems);
			//TODO
			return 0;
		}
	}

	public Integer setDoneToTask(Command cmd){
		ArrayList<String> possibleItems = new ArrayList<String>();
		ArrayList<String> listOfTasks = new ArrayList<String>();
		possibleItems = file.search(cmd.getTextContent());
		listOfTasks = file.readFile();
		if(possibleItems.size() == 1){
			for(int i=0; i<listOfTasks.size();i++){
				if(listOfTasks.get(i).equals(possibleItems.get(0))){
					listOfTasks.add(i,listOfTasks.remove(i).concat("-done"));
					break;
				}
			}
			file.clear();
			for(int j=0; j<listOfTasks.size(); j++){
				file.writeTask(listOfTasks.get(j));
			}
			return 0;
		}else if(possibleItems.size()<=0){
			return -1;
		}else{
			int lineToSetDone = LogicController.getInstance().chooseLine(possibleItems);
			for(int i=0; i<listOfTasks.size();i++){
				if(listOfTasks.get(i).equals(possibleItems.get(lineToSetDone-1))){
					listOfTasks.add(i,listOfTasks.remove(i).concat("-done"));
					break;
				}
			}
			file.clear();
			for(int j=0; j<listOfTasks.size(); j++){
				file.writeTask(listOfTasks.get(j));
			}
			return 0;
		}
	}

	public void savePossibleItems(ArrayList<String> toSave){
		//TODO
	}
}
