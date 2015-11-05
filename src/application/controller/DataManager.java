package application.controller;

//@@LimQiWen A0125980B

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Stack;

import com.google.gson.Gson;

import application.controller.parser.ParserFacade;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Parameter;
import application.model.Task;

import application.utils.GoogleCalendarUtility;

public class DataManager {
	public static final Integer WRONG_LINE_NUM = -5;
	public static final Integer NO_PREV_COMMAND = -4;
	public static final Integer TASK_ALREADY_EXISTS = -3;
	public static final Integer MULTIPLE_MATCHES = -2;
	public static final Integer TASK_NOT_FOUND = -1;
	public static final Integer TASK_REMOVED = 1;
	public static final Integer TASK_ADDED = 2;
	public static final Integer TASK_SET_TO_DONE = 3;
	public static final Integer TASK_UPDATED = 4;
	public static final Integer PREV_COMMAND_UNDONE = 5;
	public static final Integer TASK_UNDONE = 6;
	public static final Integer MAX_HISTORY = 10;

	private static Data data;
	private ArrayList<Parameter> paraList;
	private Stack<Operation> history;
	public static DataManager instance = null;
	

	private DataManager() {
		data = new Data();
		paraList = null;
		history = new Stack<Operation>();
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	// @@LimYouLiang A0125975U
	public void switchToTestingMode(String filePath) {
		data = new Data(filePath);
		paraList = null;
	}

	/**
	 * This function return a list of suitable task for its first sync.
	 * 
	 * @return
	 */
	public ArrayList<Task> getListOfUnSyncNonFloatingTasks() {
		

		ArrayList<Task> unSyncTasks = new ArrayList<Task>();

		for (Task task : data.getTaskList()) {
			if (task.isUnSyncedTask() && task.isNonFloatingTask()) {
				unSyncTasks.add(task);
			}
		}

		return unSyncTasks;

	}

	/**
	 * This function will return a list of modified gCal Sync-ed task.
	 * 
	 * @return a list of Tasks that have been modified locally since last server
	 *         update.
	 */
	public ArrayList<Task> getListOfModifiedTask() {

		ArrayList<Task> filteredList = new ArrayList<Task>();

		for (Task task : data.getTaskList()) {
			if (task.isModified()) {
				filteredList.add(task);
			}
		}

		return filteredList;
	}

	/**
	 * This function finds local task by its GCalId.
	 * 
	 * @param gCalId
	 * @return return tasks with that GCalId, If Any. Otherwise, return null.
	 */
	public Task findTaskByGCalId(String gCalId) {

		for (Task task : data.getTaskList()) {
			if (task.compareGCalId(gCalId)) {
				return task;
			}
		}
		return null;
	}

	/**
	 * This function deletes task by its GCalId, If Any.
	 * 
	 * @param gCalId
	 */
	public void deleteTaskByGCalId(String gCalId) {
		Task task = findTaskByGCalId(gCalId);
		if (task == null) {
			return;
		}
		int indexToDelete = data.getTaskList().indexOf(task);
		data.getTaskList().remove(indexToDelete);

		data.updateStorage();

	}

	/**
	 * This function takes in HashMap of Task and String, It takes the
	 * value(GCalId) and update its key(Task).
	 * 
	 * @param lists
	 *            : HashMap of key(Task) and value(String)(GCalId) pair.
	 * 
	 */
	public void updateGCalId(HashMap<Task, String> lists) {

		for (Task task : lists.keySet()) {
			int indexToUpdate = data.getTaskList().indexOf(task);
			data.getTaskList().get(indexToUpdate).setgCalId(lists.get(task));
			data.getTaskList().get(indexToUpdate).setLastServerUpdate(System.currentTimeMillis());
		}
		data.updateStorage();

	}

	/**
	 * This function takes in HashMap of Task and Long, It takes the
	 * value(LastServerUpdateTime) and update its key(Task).
	 * 
	 * @param :
	 *            HashMap of key(Task) and value(Long)(lastServerUpdate) pair.
	 */
	public void updateServerUpdateTime(HashMap<Task, Long> lists) {

		for (Task task : lists.keySet()) {
			int indexToUpdate = data.getTaskList().indexOf(task);
			data.getTaskList().get(indexToUpdate).setLastServerUpdate(lists.get(task));
		}

		data.updateStorage();

	}

	/**
	 * This function updates the local storage with the updated remote task.
	 * 
	 * @param remoteTask
	 *            : latest remote task from google calendar api.
	 */
	public void updateTask(Task remoteTask) {

		Task localTask = this.findTaskByGCalId(remoteTask.getgCalId());
		assert localTask != null; // localTask will be found.
		int indexToUpdate = data.getTaskList().indexOf(localTask);
		data.getTaskList().remove(indexToUpdate);
		data.getTaskList().add(remoteTask);
		data.updateStorage();

	}

	// @@LimQiWen A0125980B

	public Integer addNewTask(Task taskToAdd) {
		data.clearSearchList();
		if (data.getTaskList().contains(taskToAdd)) {
			return TASK_ALREADY_EXISTS;
		} else {
			data.addToData(taskToAdd);
			history.push(new Operation((taskToAdd),null,Op.ADD));
			return TASK_ADDED;
		}
	}

	public ArrayList<Task> listAll(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> filteredList = new ArrayList<Task>();
		if (cmd.getParameter().size() == 0) {
			for (Task task : data.getTaskList()) {
				if (!task.isDone()) {
					filteredList.add(task);
				}
			}
		} else {
			ArrayList<Parameter> parameter = new ArrayList<Parameter>();
			parameter = cmd.getParameter();
			filterListByParameter(filteredList, parameter);
		}
		sort(filteredList);
		return filteredList;
	}

	public ArrayList<Task> checkIfDone() {
		data.clearSearchList();
		ArrayList<Task> list = new ArrayList<Task>();
		for (Task task : data.getTaskList()) {
			if (task.isDone()) {
				list.add(task);
			}
		}
		return list;
	}

	private void filterListByParameter(ArrayList<Task> filteredList, ArrayList<Parameter> parameter) {
		for (Parameter para : parameter) {
			switch (para.getParaType()) {
			case Parameter.PRIORITY_ARGUMENT_TYPE:
				for (Task task : data.getTaskList()) {
					if (task.getPriority_argument().equals(para.getParaArg())) {
						if (!filteredList.contains(task)) {
							filteredList.add(task);
						}
					}
				}
				break;
			case Parameter.TYPE_ARGUMENT_TYPE:
				for (Task task : data.getTaskList()) {
					if (task.getType_argument().equals(para.getParaArg())) {
						if (!filteredList.contains(task)) {
							filteredList.add(task);
						}
					}
				}
				break;
			case Parameter.START_DATE_ARGUMENT_TYPE:
				for (Task task : data.getTaskList()) {
					if(para.getParaArg().equals("")){
						if(task.getStart_date() == null){
							if (!filteredList.contains(task)) {
								filteredList.add(task);
							}
						}
					}else{
						if (task.getStart_date() != null) {
							if (task.getStart_date().equals(ParserFacade.getInstance().
									parseDate(para.getParaArg()))) {
								if (!filteredList.contains(task)) {
									filteredList.add(task);
								}
							}
						}
					}
				}
				break;
			case Parameter.END_DATE_ARGUMENT_TYPE:
				for (Task task : data.getTaskList()) {
					if(para.getParaArg().equals("")){
						if(task.getEnd_date() == null){
							if (!filteredList.contains(task)) {
								filteredList.add(task);
							}
						}
					}else{
						if (task.isNonFloatingTask()) {
							if (task.getEnd_date().equals(ParserFacade.getInstance().
									parseDate(para.getParaArg()))) {
								if (!filteredList.contains(task)) {
									filteredList.add(task);
								}
							}
						}
					}
				}
				break;
			default:
				for (Task task : data.getTaskList()) {
					if (task.getPlace_argument().equals(para.getParaArg())) {
						if (!filteredList.contains(task)) {
							filteredList.add(task);
						}
					}
				}
				break;
			}
		}
	}

	public Integer removeTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			data.removeFromData(searchList.get(0));
			history.push(new Operation(searchList.get(0),null,Op.DELETE));
			if (searchList.get(0).getgCalId() != null && !searchList.get(0).getgCalId().equals("")) {
				if (GoogleCalendarUtility.hasInternetConnection()) {
					GoogleCalendarManager.getInstance().removeTaskFromServer(searchList.get(0).getgCalId());
				} else {
					GoogleCalendarUtility.addToOfflineDeletionRecords(searchList.get(0).getgCalId());
				}
			}
			return TASK_REMOVED;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}

	}

	public Integer removeTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.removeFromData(data.getSearchList().get(lineNum - 1));
		history.push(new Operation(data.getSearchList().get(lineNum - 1),null,Op.DELETE));
		if (data.getSearchList().get(lineNum - 1).getgCalId() != null
				&& !data.getSearchList().get(lineNum - 1).getgCalId().equals("")) {
			if (GoogleCalendarUtility.hasInternetConnection()) {
				GoogleCalendarManager.getInstance()
						.removeTaskFromServer(data.getSearchList().get(lineNum - 1).getgCalId());
			} else {
				GoogleCalendarUtility.addToOfflineDeletionRecords(data.getSearchList().get(lineNum - 1).getgCalId());
			}
		}
		return TASK_REMOVED;
	}

	public Integer editTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		paraList = cmd.getParameter();
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			ArrayList<Task> taskList = data.getTaskList();
			Task newTask, prevTask;
			prevTask = copyTask(searchList.get(0));
			for (Parameter para : paraList) {
				switch (para.getParaType()) {
				case Parameter.PRIORITY_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(0))).setPriority_argument(para.getParaArg());
					break;
				case Parameter.TYPE_ARGUMENT_TYPE:
					taskList.get(taskList.indexOf(searchList.get(0))).setType_argument(para.getParaArg());
					break;
				case Parameter.START_DATE_ARGUMENT_TYPE:
					if (para.getParaArg().equals("")) {
						taskList.get(taskList.indexOf(searchList.get(0))).setStart_date(null);
					} else {
						taskList.get(taskList.indexOf(searchList.get(0)))
								.setStart_date(ParserFacade.getInstance().parseDate(para.getParaArg()));
					}
					break;
				case Parameter.END_DATE_ARGUMENT_TYPE:
					if (para.getParaArg().equals("")) {
						taskList.get(taskList.indexOf(searchList.get(0))).setEnd_date(null);
					} else {
						taskList.get(taskList.indexOf(searchList.get(0)))
								.setEnd_date(ParserFacade.getInstance().parseDate(para.getParaArg()));
					}
					break;
				default:
					taskList.get(taskList.indexOf(searchList.get(0))).setPlace_argument(para.getParaArg());
					break;
				}
			}
			newTask = searchList.get(0);
			history.push(new Operation(newTask, prevTask, Op.EDIT));
			taskList.get(taskList.indexOf(searchList.get(0))).setLastLocalUpdate(System.currentTimeMillis());
			data.updateStorage();
			return TASK_UPDATED;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}

	public Integer editTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		ArrayList<Task> taskList = data.getTaskList();
		ArrayList<Task> searchList = data.getSearchList();
		Task newTask, prevTask;
		prevTask = copyTask(searchList.get(lineNum-1));
		for (Parameter para : paraList) {
			switch (para.getParaType()) {
			case Parameter.PRIORITY_ARGUMENT_TYPE:
				taskList.get(taskList.indexOf(searchList.get(lineNum - 1))).setPriority_argument(para.getParaArg());
				break;
			case Parameter.TYPE_ARGUMENT_TYPE:
				taskList.get(taskList.indexOf(searchList.get(lineNum - 1))).setType_argument(para.getParaArg());
				break;
			case Parameter.START_DATE_ARGUMENT_TYPE:
				taskList.get(taskList.indexOf(searchList.get(lineNum - 1)))
						.setStart_date(ParserFacade.getInstance().parseDate(para.getParaArg()));
				break;
			case Parameter.END_DATE_ARGUMENT_TYPE:
				taskList.get(taskList.indexOf(searchList.get(lineNum - 1)))
						.setEnd_date(ParserFacade.getInstance().parseDate(para.getParaArg()));
				break;
			default:
				taskList.get(taskList.indexOf(searchList.get(lineNum - 1))).setPlace_argument(para.getParaArg());
				break;
			}
		}
		newTask = searchList.get(lineNum - 1);
		history.push(new Operation(newTask, prevTask, Op.EDIT));
		data.updateStorage();
		return TASK_UPDATED;
	}

	public Integer setDoneToTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		ArrayList<Task> taskList = data.getTaskList();
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			int index = taskList.indexOf(searchList.get(0));
			taskList.get(index).setDone(true);
			history.push(new Operation(searchList.get(0),null, Op.SETDONE));//TODO
			data.updateStorage();
			return TASK_SET_TO_DONE;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}

	public Integer setDoneToTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.getSearchList().get(lineNum - 1).setDone(true);
		history.push(new Operation(data.getSearchList().get(lineNum - 1),null, Op.SETDONE)); //TODO
		data.updateStorage();
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		data.clearSearchList();
		return data.changeFileLocation(cmd.getTextContent());
	}

	public Integer undoPrevCommand() {
		data.clearSearchList();
		if(history.empty()){
			return NO_PREV_COMMAND;
		}else{
			Operation oper = history.pop();
			oper.undo(data);
			return PREV_COMMAND_UNDONE;
		}
	}

	private ArrayList<Task> searchTasksForMatches(Command cmd) {
		ArrayList<Task> searchList = new ArrayList<Task>();
		for (int i = 0; i < data.getTaskList().size(); i++) {
			if (data.getTaskList().get(i).getTextContent().contains(cmd.getTextContent())) {
				searchList.add(data.getTaskList().get(i));
			}
		}
		return searchList;
	}

	private void sort(ArrayList<Task> list) {
		Collections.sort(list);
	}

	@SuppressWarnings("deprecation")
	public ArrayList<Task> listToday(Command cmd) {
		Date today = new Date();
		ArrayList<Task> tasksDueToday = new ArrayList<Task>();

		for (Task task : data.getTaskList()) {
			if (!task.isDone()) {
				if (task.isNonFloatingTask()) {
					if (task.getEnd_date().getYear() == today.getYear()) {
						if (task.getEnd_date().getMonth() == today.getMonth()) {
							if (task.getEnd_date().getDate() == today.getDate()) {
								tasksDueToday.add(task);
							}
						}
					}
					if (tasksDueToday.size() > 10) {
						break;
					}
				}
			}
		}
		return tasksDueToday;
	}

	public ArrayList<Task> searchTasks(Command cmd) {
		return searchTasksForMatches(cmd);
	}

	public int setUndoneToTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.getSearchList().get(lineNum - 1).setDone(false);
		history.push(new Operation(data.getSearchList().get(lineNum - 1), null, Op.UNDONE)); //TODO
		data.updateStorage();
		return TASK_UNDONE;
	}

	public int setUndoneToTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		ArrayList<Task> taskList = data.getTaskList();
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			int index = taskList.indexOf(searchList.get(0));
			taskList.get(index).setDone(false);
			history.push(new Operation(searchList.get(0), null, Op.UNDONE)); //TODO
			data.updateStorage();
			return TASK_UNDONE;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}
	
	
	private Task copyTask(Task task1) {
		Task task2 = new Task();
		task2 = new Task(task1.getTextContent());
		task2.setDone(task1.isDone());
		task2.setPriority_argument(new String(task1.getPriority_argument()));
		task2.setType_argument(new String(task1.getType_argument()));
		task2.setPlace_argument(new String(task1.getPlace_argument()));
		task2.setStart_date(task1.getStart_date());
		task2.setEnd_date(task1.getEnd_date());
		task2.setgCalId(task1.getgCalId());
		task2.setLastServerUpdate(task1.getLastServerUpdate());
		task2.setLastLocalUpdate(task1.getLastLocalUpdate());
		return task2;
	}

}

class Data {
	private ArrayList<Task> taskList;
	private ArrayList<Task> searchList;
	private StorageInterface storageIO;
	private Gson gson;

	public Data() {
		storageIO = new StorageInterface();
		gson = new Gson();
		taskList = initializeTaskList();
		searchList = new ArrayList<Task>();
	}

	public Data(String testingFilePath) {
		storageIO = new StorageInterface(testingFilePath);
		gson = new Gson();
		taskList = initializeTaskList();
		searchList = new ArrayList<Task>();
	}

	public ArrayList<Task> getTaskList() {
		return taskList;
	}

	public ArrayList<Task> getSearchList() {
		return searchList;
	}

	public void saveToSearchList(ArrayList<Task> list) {
		searchList = list;
	}

	public void clearSearchList() {
		searchList.clear();
	}

	public void addToData(Task taskToAdd) {
		taskList.add(taskToAdd);
		sort();
		updateStorage();
	}

	public void updateStorage() {
		storageIO.saveToStorage(tasksToStrings());
	}

	public void removeFromData(Task taskToRemove) {
		taskList.remove(taskToRemove);
		updateStorage();
	}

	public void removeFromData(int lineNum) {
		taskList.remove(lineNum);
		updateStorage();
	}

	public Integer changeFileLocation(String location) {
		return storageIO.changeFilePath(location);
	}

	private ArrayList<Task> initializeTaskList() {
		ArrayList<String> listString = storageIO.readFromStorage();
		ArrayList<Task> listTask = new ArrayList<Task>();
		for (int i = 0; i < listString.size(); i++) {
			listTask.add(gson.fromJson(listString.get(i), Task.class));

		}

		return listTask;
	}

	private void sort() {
		Collections.sort(taskList);
	}

	private ArrayList<String> tasksToStrings() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (int i = 0; i < taskList.size(); i++) {
			taskStrings.add(gson.toJson(taskList.get(i)));
		}
		return taskStrings;
	}
	
}

class StorageInterface {
	private LocalStorage file;
	private File filePath;
	public static final String FILE_PATH_TXT = "filePath.txt";
	public static String DEFAULT_FILE = "toDoo.txt";

	public StorageInterface() {
		filePath = new File(FILE_PATH_TXT);
		try {
			filePath.createNewFile();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		file = new LocalStorage(determineFilePath());
	}

	public StorageInterface(String testFilePath) {
		assert new File(testFilePath).exists() == true;
		DEFAULT_FILE = "testingTestController.txt";
		filePath = new File(testFilePath);
		try {
			filePath.createNewFile();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		file = new LocalStorage(determineFilePath());
	}

	public ArrayList<String> readFromStorage() {
		return file.readFile();
	}

	public void saveToStorage(ArrayList<String> list) {
		file.clear();
		file.saveToFile(list);
	}

	public Integer changeFilePath(String newPath) {
		BufferedWriter wr = null;
		BufferedReader br = null;
		String oldPath = null;
		int success = 0;
		try {
			br = new BufferedReader(new FileReader(filePath));
			oldPath = br.readLine();
			wr = new BufferedWriter(new FileWriter(filePath, false));
			wr.close();
			wr = new BufferedWriter(new FileWriter(filePath, true));
			wr.append(newPath);
			br.close();
			wr.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			success = LocalStorage.WRONG_DIRECTORY;
		}
		if (success == 0) {
			success = file.changePath(newPath);
		}
		if (success == LocalStorage.WRONG_DIRECTORY) {
			try {
				if (oldPath == null) {
					oldPath = DEFAULT_FILE;
				}
				wr = new BufferedWriter(new FileWriter(filePath, false));
				wr.close();
				wr = new BufferedWriter(new FileWriter(filePath, true));
				wr.append(oldPath);
				wr.close();
				return LocalStorage.WRONG_DIRECTORY;
			} catch (IOException ex) {
				ex.printStackTrace();
				return LocalStorage.WRONG_DIRECTORY;
			}
		} else {
			return LocalStorage.CHANGE_PATH_SUCCESS;
		}
	}

	private String determineFilePath() {
		String text = null;
		BufferedReader br = null;
		BufferedWriter wr = null;
		boolean success = true;
		File tempfile;
		try {
			br = new BufferedReader(new FileReader(filePath));
			wr = new BufferedWriter(new FileWriter(filePath, true));
			text = br.readLine();
			if (text == null) {
				wr.append(DEFAULT_FILE);
				text = DEFAULT_FILE;
				wr.close();
				br.close();
			} else {
				tempfile = new File(text);
				tempfile.createNewFile();
			}
		} catch (IOException ex) {
			success = false;
		}
		if (success == false) {
			try {
				wr = new BufferedWriter(new FileWriter(filePath, false));
				wr.close();
				wr = new BufferedWriter(new FileWriter(filePath, true));
				wr.append(DEFAULT_FILE);
				wr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				text = DEFAULT_FILE;
			}
		}
		return text;
	}

}

class Operation {
	private Task task;
	private Task prevTask;
	private Op op;
	

	public Operation(Task task, Task prevTask, Op op){
		this.task = task;
		this.prevTask = prevTask;
		this.op = op;
	}
	

	public void undo(Data data){
		int index;
		switch(op){
		case ADD:
			data.removeFromData(task);
			break;
		case DELETE:
			data.addToData(task);
			break;
		case EDIT:
			data.removeFromData(task);
			data.addToData(prevTask);
			break;
		case SETDONE:
			index = data.getTaskList().indexOf(task);
			data.getTaskList().get(index).setDone(false);
			break;
		default:
			index = data.getTaskList().indexOf(task);
			data.getTaskList().get(index).setDone(true);
		}
	}	
}

enum Op {
    ADD, DELETE, EDIT, SETDONE, UNDONE
}