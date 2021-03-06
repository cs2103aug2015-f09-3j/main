package application.controller;

//@@author  A0125980B

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
import application.utils.TokenManager;

//Class to interact with other components and manage the Data Class
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
	private int histCount;

	public static DataManager instance = null;

	private DataManager() {
		data = new Data();
		paraList = null;
		history = new Stack<Operation>();
		histCount = 0;
	}

	public static DataManager getInstance() {
		if (instance == null) {
			instance = new DataManager();
		}
		return instance;
	}

	// @@author  A0125975U
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

	//@@author  A0125980B

	/**
	 * This method takes in a task object and calls Data class to add it.
	 * 
	 * @param taskToAdd
	 * @return Integer code for result of operation
	 */
	
	public Integer addNewTask(Task taskToAdd) {
		data.clearSearchList();
		if (data.getTaskList().contains(taskToAdd)) {
			return TASK_ALREADY_EXISTS;
		} else {
			data.addToData(taskToAdd);
			updateHistory(taskToAdd, null, Op.ADD);
			return TASK_ADDED;
		}
	}
	
	/**
	 * @param cmd
	 * @return ArrayList of tasks depending on parameters in cmd
	 */

	public ArrayList<Task> listAll(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> filteredList = new ArrayList<Task>();
		determineTasksToList(cmd, filteredList);
		sort(filteredList);
		return filteredList;
	}

	/**
	 * This method checks the whether any parameters are listed in cmd. If there are no
	 * parameters, the whole list of undone tasks will be returned, otherwise the tasks will
	 * be filtered accordingly to the parameter and returned to caller.
	 * @param cmd
	 * @param filteredList
	 */
	private void determineTasksToList(Command cmd, ArrayList<Task> filteredList) {
		if (cmd.getParameter().size() == 0) {
			listWithoutParameters(filteredList);
		} else {
			listWithParameters(cmd, filteredList);
		}
	}


	private void listWithParameters(Command cmd, ArrayList<Task> filteredList) {
		ArrayList<Parameter> parameter = new ArrayList<Parameter>();
		parameter = cmd.getParameter();
		filterListByParameter(filteredList, parameter);
	}

	private void listWithoutParameters(ArrayList<Task> filteredList) {
		for (Task task : data.getTaskList()) {
			if (!task.isDone()) {
				filteredList.add(task);
			}
		}
	}
	

	private void filterListByParameter(ArrayList<Task> filteredList,
			ArrayList<Parameter> parameter) {
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
					if (para.getParaArg().equals("")) {
						if (task.getStart_date() == null) {
							if (!filteredList.contains(task)) {
								filteredList.add(task);
							}
						}
					} else {
						if (task.getStart_date() != null) {
							if (task.getStart_date().equals(ParserFacade.
									getInstance().parseDate(para.getParaArg()))) {
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
					if (para.getParaArg().equals("")) {
						if (task.getEnd_date() == null) {
							if (!filteredList.contains(task)) {
								filteredList.add(task);
							}
						}
					} else {
						if (task.isNonFloatingTask()) {
							if (task.getEnd_date().equals(ParserFacade.
									getInstance().parseDate(para.getParaArg()))) {
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

	
	/**
	 * @param filteredList
	 * @return ArrayList of done tasks
	 */

	public ArrayList<Task> checkIfDone() {
		data.clearSearchList();
		ArrayList<Task> list = new ArrayList<Task>();
		addDoneTasksToList(list);
		return list;
	}

	
	private void addDoneTasksToList(ArrayList<Task> list) {
		for (Task task : data.getTaskList()) {
			if (task.isDone()) {
				list.add(task);
			}
		}
	}

	/**
	 * This method searches the list of tasks for matches with the task name passed in by 
	 * the cmd object. If the number of matches is 0, and error code will be returned. If the 
	 * number of matches is 1, data will be called to remove the matching task. If there are
	 * more than 1 matches, the list of matching task will be passed to CommandManager.
	 * @param cmd
	 * @return Integer code for result of operation
	 */
	public Integer removeTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			data.removeFromData(searchList.get(0));
			updateHistory(searchList.get(0), null, Op.DELETE);
			syncRemovalOfTask(searchList.get(0));
			return TASK_REMOVED;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}

	}
	
	/**
	 * This method facilitates the removal of task by their line number. From the 
	 * removeTask(cmd) method, if there are multiple matching tasks of similar names, the 
	 * matching task will be returned to caller. The line number of the task is passed into this 
	 * method for removal by line number.
	 * @param lineNum
	 * @return Integer code for result of operation
	 */
	public Integer removeTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.removeFromData(data.getSearchList().get(lineNum - 1));
		updateHistory(data.getSearchList().get(lineNum - 1), null, Op.DELETE);
		syncRemovalOfTask(data.getSearchList().get(lineNum -1));
		return TASK_REMOVED;
	}
	
	/**
	 * This method removes the task if it has a valid gCalId. If Internet connection
	 * is available, it will be removed straight from google's server. Otherwise it will be
	 * stored in an offline record.
	 * @param Task
	 */
	private void syncRemovalOfTask(Task task) {
		if (task.getgCalId() != null && !task.getgCalId().equals("")) {
			if (GoogleCalendarUtility.hasInternetConnection()) {
				GoogleCalendarManagerInterface.getInstance().
				removeTaskFromServer(task.getgCalId());
			} else {
				GoogleCalendarUtility.addToOfflineDeletionRecords(task.getgCalId());
			}
		}
	}
		
	/**
	 * This method searches the list of tasks for matches with the name passed in by the cmd
	 * object. If the number of matches is 0, an error code will be returned. If the number
	 * of matches is 1, the matching task will be edited according to the parameters passed in by
	 * the cmd object. If there are multiple matches, the list of matching tasks will be passed
	 * to CommandManager.
	 * @param cmd
	 * @return Integer code for result of operation
	 */
	public Integer editTask(Command cmd) {
		assert cmd.getParameter().size() != 0; //assert that there are parameters to edit
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
				editTaskByParameter(1,taskList,searchList,para);
			}
			newTask = searchList.get(0);
			updateHistory(newTask, prevTask, Op.EDIT);
			taskList.get(taskList.indexOf(searchList.get(0))).
				setLastLocalUpdate(System.currentTimeMillis());
			data.updateStorage();
			return TASK_UPDATED;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}

	/**
	 * This method facilitates the editing of tasks by lineNum. This method can be called after
	 * calling the editTask(cmd) method and getting a list of matching tasks. The line number of
	 * the task can be passed into this method for editing.
	 * @param listNum
	 * @return Integer code for result of operation
	 */
	public Integer editTask(int lineNum) {
		
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		
		ArrayList<Task> taskList = data.getTaskList();
		ArrayList<Task> searchList = data.getSearchList();
		Task newTask, prevTask;
		
		prevTask = copyTask(searchList.get(lineNum - 1));
		for (Parameter para : paraList) {
			editTaskByParameter(lineNum, taskList, searchList, para);
		}
		newTask = searchList.get(lineNum - 1);
		updateHistory(newTask, prevTask, Op.EDIT);
		data.updateStorage();
		return TASK_UPDATED;
	}

	private void editTaskByParameter(int lineNum, ArrayList<Task> taskList, 
				ArrayList<Task> searchList,	Parameter para) {
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
	
	/**
	 * This method searches the list of tasks for matches with the name passed in by the cmd
	 * object. If the number of matches is 0, an error code will be returned. If the number
	 * of matches is 1, the matching task will be set to done. If there are multiple matches, 
	 * the list of matching tasks will be passed to CommandManager.
	 * @param cmd
	 * @return Integer code for result of operation
	 */
	public Integer setDoneToTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchUndoneTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		ArrayList<Task> taskList = data.getTaskList();
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			int index = taskList.indexOf(searchList.get(0));
			taskList.get(index).setDone(true);
			updateHistory(searchList.get(0), null, Op.SETDONE);
			data.updateStorage();
			return TASK_SET_TO_DONE;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}
	
	/**
	 * This method facilitates the setting done of tasks by lineNum. This method can be called 
	 * after calling the setDoneToTask(cmd) method and getting a list of matching tasks. 
	 * The line number of the task can be passed into this method for setting done.
	 * @param listNum
	 * @return Integer code for result of operation
	 */
	public Integer setDoneToTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.getSearchList().get(lineNum - 1).setDone(true);
		updateHistory(data.getSearchList().get(lineNum - 1), null, Op.SETDONE);
		data.updateStorage();
		return TASK_SET_TO_DONE;
	}

	public Integer changeStorageLocation(Command cmd) {
		data.clearSearchList();
		return data.changeFileLocation(cmd.getTextContent());
	}
	
	/**
	 * This method retrieves the most recent change to data and undo it.
	 */
	public Integer undoPrevCommand() {
		data.clearSearchList();
		if (history.empty()) {
			return NO_PREV_COMMAND;
		} else {
			Operation oper = history.pop();
			oper.undo(data);
			histCount--;
			return PREV_COMMAND_UNDONE;
		}
	}
	
	
	/**
	 * @return the list of tasks that have their end date on the current date.
	 * 
	 */
	public ArrayList<Task> listToday() {
		Date today = new Date();
		ArrayList<Task> tasksDueToday = new ArrayList<Task>();

		checkTasksDateIfToday(today, tasksDueToday);
		return tasksDueToday;
	}

	/**
	 * This method checks the date object in each tasks in the task list and if a task matches
	 * the current date, it will be added to a list and the list will be returned. 
	 * The maximum number of tasks in the list is 10.
	 * @param today
	 * @param tasksDueToday
	 */
	@SuppressWarnings("deprecation")
	private void checkTasksDateIfToday(Date today, ArrayList<Task> tasksDueToday) {
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
	}

	public ArrayList<Task> searchTasks(Command cmd) {
		return searchTasksForMatches(cmd);
	}

	/**
	 * This method facilitates the setting undone of tasks by lineNum. This method can be called 
	 * after calling the setUndoneToTask(cmd) method and getting a list of matching tasks. 
	 * The line number of the task can be passed into this method for setting undone.
	 * @param listNum
	 * @return Integer code for result of operation
	 */
	public int setUndoneToTask(int lineNum) {
		if (lineNum > data.getSearchList().size()) {
			return WRONG_LINE_NUM;
		}
		data.getSearchList().get(lineNum - 1).setDone(false);
		updateHistory(data.getSearchList().get(lineNum - 1), null, Op.UNDONE);
		data.updateStorage();
		return TASK_UNDONE;
	}
	
	/**
	 * This method searches the list of tasks for matches with the name passed in by the cmd
	 * object. If the number of matches is 0, an error code will be returned. If the number
	 * of matches is 1, the matching task will be set to undone. If there are multiple matches, 
	 * the list of matching tasks will be passed to CommandManager.
	 * @param cmd
	 * @return Integer code for result of operation
	 */
	public int setUndoneToTask(Command cmd) {
		data.clearSearchList();
		ArrayList<Task> searchList = searchDoneTasksForMatches(cmd);
		data.saveToSearchList(searchList);
		ArrayList<Task> taskList = data.getTaskList();
		switch (searchList.size()) {
		case 0:
			return TASK_NOT_FOUND;
		case 1:
			int index = taskList.indexOf(searchList.get(0));
			taskList.get(index).setDone(false);
			updateHistory(searchList.get(0), null, Op.UNDONE);
			data.updateStorage();
			return TASK_UNDONE;
		default:
			CommandManager.setMultipleMatchList(searchList);
			return MULTIPLE_MATCHES;
		}
	}
	
	/**
	 * This method copies the content of task1 into a new Task object and returns it.
	 * @param task1
	 * @return new Task object with same attributes as task1
	 */
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
	
	
	/**
	 * This method pushes the latest update to data  into a Stack of Operation objects. The 
	 * maximum size of the stack is 10. task1 is the task being operated on. task2 is previous
	 * task before update. oper is the type of operation.
	 * @param task1, task2, oper
	 */
	private void updateHistory(Task task1, Task task2, Op oper) {
		history.push(new Operation(task1, task2, oper));
		histCount++;
		limitHistory();
	}

	
	/**
	 * This method limits the History stack to a size of 10.
	 */
	private void limitHistory() {
		Stack<Operation> tempStack = new Stack<Operation>();
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

	private ArrayList<Task> searchTasksForMatches(Command cmd) {
		ArrayList<Task> searchList = new ArrayList<Task>();
		for (Task task: data.getTaskList()) {
			if (task.getTextContent().contains(cmd.getTextContent())) {
				searchList.add(task);
			}
		}
		return searchList;
	}
	
	private ArrayList<Task> searchDoneTasksForMatches(Command cmd) {
		ArrayList<Task> searchList = new ArrayList<Task>();
		for (Task task: data.getTaskList()) {
			if (task.getTextContent().contains(cmd.getTextContent())) {
				if(task.isDone())
					searchList.add(task);
			}
		}
		return searchList;
	}
	
	private ArrayList<Task> searchUndoneTasksForMatches(Command cmd) {
		ArrayList<Task> searchList = new ArrayList<Task>();
		for (Task task: data.getTaskList()) {
			if (task.getTextContent().contains(cmd.getTextContent())) {
				if(!task.isDone()){
					searchList.add(task);
				}
			}
		}
		return searchList;
	}

	private void sort(ArrayList<Task> list) {
		Collections.sort(list);
	}
}

//Class holding the Data to be modified
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
		int changePathResult;
		changePathResult = storageIO.changeFilePath(location);
		if(changePathResult == LocalStorage.CHANGE_PATH_SUCCESS_FILE_EXIST){
			taskList = initializeTaskList();
			TokenManager.getInstance().clearToken();
		}
		return changePathResult;	
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

//Class to interface with LocalStorage class
class StorageInterface {
	private LocalStorage file;
	private File filePath;
	
	public static final String	PATH_ERROR = "Path error";
	public static final String	RESET_TO_DEFAULT_PATH = "Resetting to default directory";
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
			// read and store the old path before clearing it
			br = new BufferedReader(new FileReader(filePath));
			oldPath = br.readLine();
			wr = new BufferedWriter(new FileWriter(filePath, false));
			wr.close();
			wr = new BufferedWriter(new FileWriter(filePath));
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
				// if new path is wrong directory, reset filePath to old path
				if (oldPath == null) {
					oldPath = DEFAULT_FILE;
				}
				wr = new BufferedWriter(new FileWriter(filePath, false));
				wr.close();
				wr = new BufferedWriter(new FileWriter(filePath));
				wr.append(oldPath);
				wr.close();
				return LocalStorage.WRONG_DIRECTORY;
			} catch (IOException ex) {
				ex.printStackTrace();
				return LocalStorage.WRONG_DIRECTORY;
			}
		} else {
			return success;
		}
	}

	private String determineFilePath() {
		String text = null;
		BufferedReader br = null;
		BufferedWriter wr = null;
		boolean success = true;
		try {
			// determine storage path from filePath.txt in project directory
			br = new BufferedReader(new FileReader(filePath));

			text = br.readLine();
			if (text == null) {
				// if filePath.txt is empty, write the default storage path into it
				wr = new BufferedWriter(new FileWriter(filePath));
				wr.append(DEFAULT_FILE);
				text = DEFAULT_FILE;
				wr.close();
				br.close();
			} else {
				// test if the path stored in filePath.txt is valid
				success = new File(text).exists();
				br.close();
			}
		} catch (IOException ex) {
			success = false;
		}
		if (success == false) {
			LogManager.getInstance().log(PATH_ERROR,RESET_TO_DEFAULT_PATH);
			text = resetFilePathToDefault();
		}
		return text;
	}

	/**
	 * @return: a string containing the default file path
	 */
	private String resetFilePathToDefault() {
		String text;
		BufferedWriter wr;
		try {
			wr = new BufferedWriter(new FileWriter(filePath, false));
			wr.close();
			wr = new BufferedWriter(new FileWriter(filePath));
			wr.append(DEFAULT_FILE);
			wr.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			text = DEFAULT_FILE;
		}
		return text;
	}

}

class Operation {
	private Task task;
	private Task prevTask;
	private Op op;

	public Operation(Task task, Task prevTask, Op op) {
		this.task = task;
		this.prevTask = prevTask;
		this.op = op;
	}

	public void undo(Data data) {
		int index;
		switch (op) {
		case ADD:
			data.removeFromData(task);
			if(task.getgCalId()!= null && !task.getgCalId().equals("")){	
				if (GoogleCalendarUtility.hasInternetConnection()) {
					GoogleCalendarManagerInterface.getInstance().
						removeTaskFromServer(task.getgCalId());
				} else {
				GoogleCalendarUtility.addToOfflineDeletionRecords(task.getgCalId());
				}	
			}
			break;
		case DELETE:
			task.setgCalId("");
			data.addToData(task);
			
			break;
		case EDIT:
			index = data.getTaskList().indexOf(task);
			Task temp = data.getTaskList().get(index);
			temp.setgCalId(prevTask.getgCalId());
			temp.setLastServerUpdate(prevTask.getLastServerUpdate());
			temp.setLastLocalUpdate(prevTask.getLastLocalUpdate());
			temp.setDone(prevTask.isDone());
			temp.setPriority_argument(prevTask.getPriority_argument());
			temp.setType_argument(prevTask.getType_argument());
			temp.setStart_date(prevTask.getStart_date());
			temp.setEnd_date(prevTask.getEnd_date());
			temp.setPlace_argument(prevTask.getPlace_argument());
			if(task.getgCalId()!= null && !task.getgCalId().equals("")){
				if(GoogleCalendarUtility.hasInternetConnection()){
					GoogleCalendarManager.getInstance().
						updateGCalEvent(GoogleCalendarUtility.mapTaskToEvent(prevTask));
				}
			}
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
