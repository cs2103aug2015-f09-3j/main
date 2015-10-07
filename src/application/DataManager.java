package application;

import java.util.ArrayList;
import com.google.gson.*;

/*
 * @author: Lim Qi Wen
 */

public class DataManager {
	public static final Integer MULTIPLE_MATCHES = -2;
	public static final Integer TASK_NOT_FOUND = -1;
	public static final Integer TASK_REMOVED = 1;
	public static final Integer TASK_SET_TO_DONE = 2;
	
	
	private LocalStorage file;
	private Gson gson;
	private ArrayList<Task> taskList;
	private ArrayList<Task> searchList;
	public static DataManager instance = null;

	private DataManager() {
		file = new LocalStorage();
		gson = new Gson();
		taskList = initialiseTaskList();
		searchList = new ArrayList<Task>();
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public void addNewTask(Task taskToAdd) {
		searchList.clear();
		taskList.add(taskToAdd);
		sort();
		file.clear();
		file.saveToFile(tasksToStrings());
	}

	public ArrayList<String> listAll(String para) {
		searchList.clear();
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0; i<taskList.size();i++){
			list.add(taskList.get(i).getTextContent());
			//TODO filter list by parameter;
		}
		return list;
	}

	public Integer removeTask(Command cmd) {
		searchList.clear();
		searchTasksForMatches(cmd);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:	
				taskList.remove(searchList.get(0));
				file.clear();
				file.saveToFile(tasksToStrings());
				return TASK_REMOVED;
			default:
				LogicController.getInstance().chooseLine(tasksToStrings(searchList));
				return MULTIPLE_MATCHES;
		}	
	}
	
	public Integer removeTask(int lineNum){
		taskList.remove(searchList.get(lineNum));
		file.clear();
		file.saveToFile(tasksToStrings());
		return TASK_REMOVED;
	}

	public Integer editTask(Command cmd) {
		return 0;
	}

	public Integer setDoneToTask(Command cmd){
		searchList.clear();
		searchTasksForMatches(cmd);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:	
				int index = taskList.indexOf(searchList.get(0));
				taskList.get(index).setDone(true);
				file.clear();
				file.saveToFile(tasksToStrings());
				return TASK_SET_TO_DONE;
			default:
				LogicController.getInstance().chooseLine(tasksToStrings(searchList));
				return MULTIPLE_MATCHES;
		}	
	}
	
	public Integer setDoneToTask(int lineNum){
		int index = taskList.indexOf(searchList.get(lineNum));
		taskList.get(index).setDone(true);
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		return file.changePath(cmd.getTextContent());
	}
	
	private ArrayList<String> tasksToStrings(){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<taskList.size();i++){
			taskStrings.add(gson.toJson(taskList.get(i)));
		}
		return taskStrings;
	}
	
	private ArrayList<String> tasksToStrings(ArrayList<Task> list){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			taskStrings.add(list.get(i).getTextContent());
		}
		return taskStrings;
	}
	
	private void sort(){
		//TODO
	}
	
	private ArrayList<Task> initialiseTaskList(){
		ArrayList<Task> list = new ArrayList<Task>();
		ArrayList<String> stringList = file.readFile();
		for(int i=0; i<stringList.size();i++){
			list.add(gson.fromJson(stringList.get(i), Task.class));
		}
		return list;
	}

	private void searchTasksForMatches(Command cmd) {
		for(int i=0; i<taskList.size();i++){
			if(taskList.get(i).getTextContent().contains(cmd.getTextContent())){
				searchList.add(taskList.get(i));
			}
		}
	}
}

