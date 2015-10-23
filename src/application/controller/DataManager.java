package application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

import com.google.gson.*;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Parameter;
import application.model.Task;

/*
 * @author: Lim Qi Wen
 */

public class DataManager {

	public static final Integer NO_PREV_COMMAND = -4;
	public static final Integer TASK_ALREADY_EXISTS = -3;
	public static final Integer MULTIPLE_MATCHES = -2;
	public static final Integer TASK_NOT_FOUND = -1;
	public static final Integer TASK_REMOVED = 1;
	public static final Integer TASK_ADDED = 2;
	public static final Integer TASK_SET_TO_DONE = 3;
	public static final Integer TASK_UPDATED = 4;
	public static final Integer PREV_COMMAND_UNDONE = 5;
	public static final Integer MAX_HISTORY = 10;

	private static Data data;
	private ArrayList<Parameter> para;
	public static DataManager instance = null;

	private DataManager() {
		data = new Data();
		para = null;
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	public void addNewTask(Task taskToAdd) {
		data.clearSearchList();
		if(taskToAdd.getPriority_argument().equals("")){
			taskToAdd.setPriority_argument("Normal");
		}
		if(taskToAdd.getType_argument().equals("")){
			taskToAdd.setType_argument("Normal");
		}
		data.addToData(taskToAdd);
	}

	public ArrayList<Task> listAll(Command cmd) {
		if (cmd.getParameter().size() == 0){
			return data.getTaskList();
			
		}else{
			ArrayList<Task> filteredList = new ArrayList<Task>();
			ArrayList<Parameter> para = new ArrayList<Parameter>();
			para = cmd.getParameter();
			for(int i=0; i<para.size();i++){
				switch(para.get(i).getParaType()){
					case Parameter.PRIORITY_ARGUMENT_TYPE:
						for(Task task: data.getTaskList()){
							if(task.getPriority_argument().equals(para.get(i).getParaArg())){
								if(!filteredList.contains(task)){
									filteredList.add(task);
								}
							}
						}
						break;
					case Parameter.TYPE_ARGUMENT_TYPE:
						for(Task task: data.getTaskList()){
							if(task.getType_argument().equals(para.get(i).getParaArg())){
								if(!filteredList.contains(task)){
									filteredList.add(task);
								}
							}
						}
						break;
					case Parameter.START_DATE_ARGUMENT_TYPE:
						try {
							for(Task task: data.getTaskList()){
								if(task.getStart_date().equals(ParserFacade.getInstance().parseDate(para.get(i).getParaArg()))){
									if(!filteredList.contains(task)){
										filteredList.add(task);
									}
								}
							}
						}catch (NullPointerException e){
							// Do nothing, task remain the same.
						}
							break;
					case Parameter.END_DATE_ARGUMENT_TYPE:
						try {
							for(Task task: data.getTaskList()){
								if(task.getEnd_date().equals(ParserFacade.getInstance().parseDate(para.get(i).getParaArg()))){
									if(!filteredList.contains(task)){
										filteredList.add(task);
									}
								}
							}
						}catch (NullPointerException e){
							// Do nothing, task remain the same.
						}
							break;
					default:
						for(Task task: data.getTaskList()){
							if(task.getPlace_argument().equals(para.get(i).getParaArg())){
								if(!filteredList.contains(task)){
									filteredList.add(task);
								}
							}
						}
						break;
				}
			}
			sort(filteredList);
			return filteredList;
		}
	}

	public Integer removeTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:
				data.removeFromData(searchList.get(0));
				return TASK_REMOVED;
			default:
				//LogicController.getInstance().chooseLine(searchList);
				return MULTIPLE_MATCHES;
		}
	}

	public Integer removeTask(int lineNum){
		data.removeFromData(data.getSearchList().get(lineNum));
		return TASK_REMOVED;
	}

	public Integer editTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		para = cmd.getParameter();
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:
				ArrayList<Task> taskList = data.getTaskList();
				for(int i=0; i<para.size(); i++){
					switch(para.get(i).getParaType()){
						case Parameter.PRIORITY_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setPriority_argument(para.get(i).getParaArg());
							break;
						case Parameter.TYPE_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setType_argument(para.get(i).getParaArg());
							break;
						case Parameter.START_DATE_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setStart_date(
									ParserFacade.getInstance().parseDate(para.get(i).getParaArg()));
							break;
						case Parameter.END_DATE_ARGUMENT_TYPE:
							taskList.get(taskList.indexOf(searchList.get(0))).setEnd_date(
									ParserFacade.getInstance().parseDate(para.get(i).getParaArg()));
							break;
						default:
							taskList.get(taskList.indexOf(searchList.get(0))).setPlace_argument(para.get(i).getParaArg());
							break;
					}
				}
				data.updateStorage();
				return TASK_UPDATED;
			default:
				//LogicController.getInstance().chooseLine(searchList);
				return MULTIPLE_MATCHES;
		}
	}

	public Integer editTask(int lineNum){
		ArrayList<Task> taskList = data.getTaskList();
		ArrayList<Task> searchList = data.getSearchList();
		for(int i=0; i<para.size(); i++){
			switch(para.get(i).getParaType()){
				case Parameter.PRIORITY_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(lineNum))).setPriority_argument(para.get(i).getParaArg());
					break;
				case Parameter.TYPE_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(lineNum))).setType_argument(para.get(i).getParaArg());
					break;
				case Parameter.START_DATE_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(lineNum))).setStart_date(
							ParserFacade.getInstance().parseDate(para.get(i).getParaArg()));
					break;
				case Parameter.END_DATE_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(lineNum))).setEnd_date(
							ParserFacade.getInstance().parseDate(para.get(i).getParaArg()));
					break;
				default:
					taskList.get(taskList.indexOf(searchList.get(lineNum))).setPlace_argument(para.get(i).getParaArg());
					break;
			}
		}
		data.updateStorage();
		return TASK_UPDATED;
	}

	public Integer setDoneToTask(Command cmd){
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		ArrayList<Task> taskList = data.getTaskList();
		switch (searchList.size()){
			case 0:
				return TASK_NOT_FOUND;
			case 1:
				int index = taskList.indexOf(searchList.get(0));
				taskList.get(index).setDone(true);
				data.updateStorage();
				return TASK_SET_TO_DONE;
			default:
				//LogicController.getInstance().chooseLine(searchList);
				return MULTIPLE_MATCHES;
		}
	}

	public Integer setDoneToTask(int lineNum){
		int index = data.getTaskList().indexOf(data.getSearchList().get(lineNum));
		data.getTaskList().get(index).setDone(true);
		data.updateStorage();
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		return data.changeFileLocation(cmd.getTextContent());
	}

	public static Integer undoPrevCommand(){
		return data.undo();
	}

	private ArrayList<String> tasksToStrings(ArrayList<Task> list){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			taskStrings.add(list.get(i).getTextContent());
		}
		return taskStrings;
	}

	private ArrayList<Task> searchTasksForMatches(Command cmd) {
		ArrayList<Task> searchList = new ArrayList<Task>();
		for(int i=0; i<data.getTaskList().size();i++){
			if(data.getTaskList().get(i).getTextContent().contains(cmd.getTextContent())){
				searchList.add(data.getTaskList().get(i));
			}
		}
		return searchList;
	}

	private void sort(ArrayList<Task> list){
		Collections.sort(list);
	}
	
	
	@SuppressWarnings("deprecation")
	public ArrayList<Task> listToday(Command cmd) {
		Date today = new Date();
		ArrayList<Task> tasksDueToday = new ArrayList<Task>();
		Task task;

		for(int i=0; i< data.getTaskList().size(); i++){
			task = data.getTaskList().get(i);
			if(task.getEnd_date().getYear() == today.getYear()){
				if(task.getEnd_date().getMonth() == today.getMonth()){
					if(task.getEnd_date().getDate() == today.getDate()){
						tasksDueToday.add(task);
					}
				}
			}
			if(tasksDueToday.size() > 10){
				break;
			}
		}
		return tasksDueToday;
	}

	public ArrayList<Task> searchTasks(Command cmd) {
		return searchTasksForMatches(cmd);
	}
}

class Data{
	private ArrayList<Task> taskList;
	private ArrayList<Task> searchList;
	private Stack<ArrayList<Task>> history;
	private StorageInterface storageIO;
	private Gson gson;
	private int histCount;

	public Data(){
		storageIO = new StorageInterface();
		gson = new Gson();
		taskList = initializeTaskList();
		searchList = new ArrayList<Task>();
		history = new Stack<ArrayList<Task>>();
		addToHistory();
		histCount = 1;
	}


	public ArrayList<Task> getTaskList(){
		return taskList;
	}
	public ArrayList<Task> getSearchList(){
		return searchList;
	}

	public void saveToSearchList(ArrayList<Task> list){
		searchList = list;
	}
	public void clearSearchList(){
		searchList.clear();
	}

	public void addToData(Task taskToAdd){
		taskList.add(taskToAdd);
		sort();
		updateStorage();
	}
	public void updateStorage(){
		
		addToHistory();
		histCount ++;
		limitHistory();
		storageIO.saveToStorage(tasksToStrings());
	}
	public void removeFromData(Task taskToRemove){
		taskList.remove(taskToRemove);
		updateStorage();
	}
	public void removeFromData(int lineNum){
		taskList.remove(lineNum);
		updateStorage();
	}
	public Integer undo(){
		if(histCount == 1){
			return DataManager.NO_PREV_COMMAND;
		}else{
			history.pop();
			taskList = history.pop();
			histCount -= 2;
			updateStorage();
			return DataManager.PREV_COMMAND_UNDONE;
		}
	}

	public Integer changeFileLocation(String location){
		return storageIO.changeFilePath(location);
	}

	private ArrayList<Task> initializeTaskList(){
		ArrayList<String> listString = storageIO.readFromStorage();
		ArrayList<Task> listTask = new ArrayList<Task>();

		for(int i=0; i<listString.size(); i++){
			listTask.add(gson.fromJson(listString.get(i),Task.class));
		}

		return listTask;
	}
	

	private void addToHistory(){
		ArrayList<Task> list = new ArrayList<Task>();
		Task task = new Task();
		Task temp;
		for(int i=0; i<taskList.size();i++){
			temp = taskList.get(i);
			task = new Task(temp.getTextContent());
			task.setDone(temp.isDone());
			task.setPriority_argument(new String(temp.getPriority_argument()));
			task.setType_argument(new String(temp.getType_argument()));
			task.setPlace_argument(new String(temp.getPlace_argument()));	
			task.setStart_date(temp.getStart_date());
			task.setEnd_date(temp.getEnd_date());
			list.add(task);
		}
		history.push(list);
	}
	
	private void sort(){
		Collections.sort(taskList);
	}

	private ArrayList<String> tasksToStrings(){
		ArrayList<String> taskStrings = new ArrayList<String>();
		for(int i=0;i<taskList.size();i++){
			taskStrings.add(gson.toJson(taskList.get(i)));
		}
		return taskStrings;
	}

	private void limitHistory(){
		Stack<ArrayList<Task>> tempStack = new Stack<ArrayList<Task>>();
		if(histCount > DataManager.MAX_HISTORY){
			while(!history.empty()){
				tempStack.push(history.pop());
			}
			tempStack.pop();
			while(!tempStack.empty()){
				history.push(tempStack.pop());
			}
			histCount --;
		}
	}
}

class StorageInterface{
	private LocalStorage file;
	private static final String TEST_TXT = "test.txt";

	public StorageInterface(){
		file = new LocalStorage(TEST_TXT);
	}

	public ArrayList<String> readFromStorage(){
		return file.readFile();
	}

	public void saveToStorage(ArrayList<String> list){
		file.clear();
		file.saveToFile(list);
	}

	public Integer changeFilePath(String newPath){
		return file.changePath(newPath);
	}


}
