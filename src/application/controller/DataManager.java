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
import application.utils.Utility;

/*
 * @author: Lim Qi Wen
 */

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
	public static DataManager instance = null;

	private DataManager() {
		data = new Data();
		paraList = null;
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

	public ArrayList<Task> getListOfTasksToUploadGCal() {

		// data.clearSearchList();
		ArrayList<Task> filteredList = new ArrayList<Task>();

		for (Task task : data.getTaskList()) {
			if (task.getgCalId().equals("") && task.getEnd_date() != null) {
				filteredList.add(task);
			}
		}

		return filteredList;

	}

	public ArrayList<Task> getListOfTaskToUpdateGCal() {

		// data.clearSearchList();
		ArrayList<Task> filteredList = new ArrayList<Task>();

		for (Task task : data.getTaskList()) {
			if (task.getgCalId() != null && !task.getgCalId().equals("") && task.getLastServerUpdate() != 0
					&& task.getLastLocalUpdate() != 0 && (task.getLastLocalUpdate() > task.getLastServerUpdate())) {
				filteredList.add(task);
			}
		}

		return filteredList;
	}

	public Task findTaskByGCalId(String gCalId) {
		// data.clearSearchList();

		for (Task task : data.getTaskList()) {
			if (task.getgCalId() != null && task.getgCalId() != "" && task.getgCalId().equals(gCalId)) {
				return task;
			}
		}

		return null;
	}

	public void deleteTaskByGCalId(String gCalId) {
		Task task = findTaskByGCalId(gCalId);
		if (task == null) {
			return;
		}
		// data.clearSearchList();
		int indexToDelete = data.getTaskList().indexOf(task);
		data.getTaskList().remove(indexToDelete);

		data.updateStorage();

	}

	public void updateGCalId(HashMap<Task, String> lists) {
		// data.clearSearchList();

		for (Task task : lists.keySet()) {
			int indexToUpdate = data.getTaskList().indexOf(task);
			data.getTaskList().get(indexToUpdate).setgCalId(lists.get(task));
			data.getTaskList().get(indexToUpdate).setLastServerUpdate(System.currentTimeMillis());
		}

		data.updateStorage();

	}

	public void updateServerUpdateTime(HashMap<Task, Long> lists) {
		// data.clearSearchList();

		for (Task task : lists.keySet()) {
			int indexToUpdate = data.getTaskList().indexOf(task);
			data.getTaskList().get(indexToUpdate).setLastServerUpdate(lists.get(task));
		}

		data.updateStorage();

	}

	public ArrayList<Task> getAllTasks() {
		// data.clearSearchList();
		return data.getTaskList();
	}

	public void updateTask(Task task) {

		// data.clearSearchList();
		Task localTask = this.findTaskByGCalId(task.getgCalId());

		int indexToUpdate = data.getTaskList().indexOf(localTask);

		data.getTaskList().remove(indexToUpdate);
		data.getTaskList().add(task);
		data.updateStorage();

	}

	// @@LimQiWen A0125980B

	public Integer addNewTask(Task taskToAdd) {
		// data.clearSearchList();
		data.clearSearchList();
		if (data.getTaskList().contains(taskToAdd)) {
			return TASK_ALREADY_EXISTS;
		} else {
			data.addToData(taskToAdd);
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
					if (task.getStart_date() != null) {
						if (task.getStart_date().equals(ParserFacade.getInstance().parseDate(para.getParaArg()))) {
							if (!filteredList.contains(task)) {
								filteredList.add(task);
							}
						}
					}
				}
				break;
			case Parameter.END_DATE_ARGUMENT_TYPE:
				for (Task task : data.getTaskList()) {
					if (task.getEnd_date() != null) {
						if (task.getEnd_date().equals(ParserFacade.getInstance().parseDate(para.getParaArg()))) {
							if (!filteredList.contains(task)) {
								filteredList.add(task);
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
			if (searchList.get(0).getgCalId() != null && !searchList.get(0).getgCalId().equals("")) {
				if (Utility.hasInternetConnection()) {
					GoogleCalendarManager.getInstance().removeTaskFromServer(searchList.get(0).getgCalId());
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
		if (data.getSearchList().get(lineNum - 1).getgCalId() != null
				&& !data.getSearchList().get(lineNum - 1).getgCalId().equals("")) {
			if (Utility.hasInternetConnection()) {
				GoogleCalendarManager.getInstance()
						.removeTaskFromServer(data.getSearchList().get(lineNum - 1).getgCalId());
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
		int index = data.getTaskList().indexOf(data.getSearchList().get(lineNum - 1));
		data.getTaskList().get(index).setDone(true);
		data.updateStorage();
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		data.clearSearchList();
		return data.changeFileLocation(cmd.getTextContent());
	}

	public static Integer undoPrevCommand() {
		data.clearSearchList();
		return data.undo();
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
				if (task.getEnd_date() != null) {
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
		int index = data.getTaskList().indexOf(data.getSearchList().get(lineNum - 1));
		data.getTaskList().get(index).setDone(false);
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
			data.updateStorage();
			return TASK_UNDONE;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}

}

class Data {
	private ArrayList<Task> taskList;
	private ArrayList<Task> searchList;
	private Stack<ArrayList<Task>> history;
	private StorageInterface storageIO;
	private Gson gson;
	private int histCount;

	public Data() {
		storageIO = new StorageInterface();
		gson = new Gson();
		taskList = initializeTaskList();
		searchList = new ArrayList<Task>();
		history = new Stack<ArrayList<Task>>();
		addToHistory();
		histCount = 1;
	}

	public Data(String testingFilePath) {
		storageIO = new StorageInterface(testingFilePath);
		gson = new Gson();
		taskList = initializeTaskList();
		searchList = new ArrayList<Task>();
		history = new Stack<ArrayList<Task>>();
		addToHistory();
		histCount = 1;
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
		addToHistory();
		histCount++;
		limitHistory();
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

	public Integer undo() {
		if (histCount == 1) {
			return DataManager.NO_PREV_COMMAND;
		} else {
			history.pop();
			taskList = history.pop();
			histCount -= 2;
			updateStorage();
			return DataManager.PREV_COMMAND_UNDONE;
		}
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

	private void addToHistory() {
		ArrayList<Task> list = new ArrayList<Task>();
		Task task = new Task();
		Task temp;
		for (int i = 0; i < taskList.size(); i++) {
			temp = taskList.get(i);
			task = new Task(temp.getTextContent());
			task.setDone(temp.isDone());
			task.setPriority_argument(new String(temp.getPriority_argument()));
			task.setType_argument(new String(temp.getType_argument()));
			task.setPlace_argument(new String(temp.getPlace_argument()));
			task.setStart_date(temp.getStart_date());
			task.setEnd_date(temp.getEnd_date());
			task.setgCalId(temp.getgCalId());
			task.setLastServerUpdate(temp.getLastServerUpdate());
			task.setLastLocalUpdate(temp.getLastLocalUpdate());
			list.add(task);
		}
		history.push(list);
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

	private void limitHistory() {
		Stack<ArrayList<Task>> tempStack = new Stack<ArrayList<Task>>();
		if (histCount > DataManager.MAX_HISTORY) {
			while (!history.empty()) {
				tempStack.push(history.pop());
			}
			tempStack.pop();
			while (!tempStack.empty()) {
				history.push(tempStack.pop());
			}
			histCount--;
		}
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
