package application;
//@@nghuiyirebecca A0130876B
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import application.controller.DataManager;
import application.controller.LogicController;
import application.controller.parser.ParserFacade;
import application.exception.InvalidCommandException;
import application.model.LocalStorage;
import application.model.Task;
import application.utils.TasksFormatter;

public class LogicControllerTest {
	LocalStorage file;
	String curFilePath;

	
	public void testAll() throws InvalidCommandException {
		DataManager.getInstance().switchToTestingMode("testingMode.txt");
		testListAll();
		testListDone();
		testLimit();
		testAdd();
		testAddRepeat();
		testDeleteFullName();
		testDeletePartialName();
		cleanUp();

	}

	public void cleanUp() {
		try {
			File file = new File("testingTestController.txt");
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/* This is a boundary case for the listAll method */
	public void testListAll() throws InvalidCommandException {
		LogicController.onCommandProcess("add meeting with prof");
		LogicController.onCommandProcess("add 2021 homework i cri");
		LogicController.onCommandProcess("add free lunch at engin");
		LogicController.onCommandProcess("add llao llao with tenyee");
		String cmd = "list";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = TasksFormatter.format(
				DataManager.getInstance().listAll(ParserFacade.getInstance().parseCommand(cmd).get(0)),
				TasksFormatter.DETAIL_VIEW_TYPE) + "\n";
		assertEquals(expectedResult, result);
	}
	
	//@@ZhangLei A0093966L
	/* This is a boundary case for the list done tasks */
	public void testListDone() throws InvalidCommandException {
		LogicController.onCommandProcess("add meeting with prof");
		LogicController.onCommandProcess("add 2021 homework i cri");
		LogicController.onCommandProcess("add free lunch at engin");
		LogicController.onCommandProcess("add llao llao with tenyee");
		LogicController.onCommandProcess("ok meeting with prof");
		LogicController.onCommandProcess("done llao llao with tenyee");
		String cmd = "list done";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Tasks that are done: \n" + 
				TasksFormatter.format(DataManager.getInstance().checkIfDone(), TasksFormatter.DETAIL_VIEW_TYPE) + "\n";
		assertEquals(expectedResult, result);
	}
	
	//@@ZhangLei A0093966L
	/* This is a boundary case for the list tasks in specific location */
	@Test
	public void testListPlace() throws InvalidCommandException {
		LogicController.onCommandProcess("add meeting with prof \\place SOC");
		LogicController.onCommandProcess("add 2021 homework i cri \\place SOC");
		LogicController.onCommandProcess("add llao llao with tenyee \\place KR MRT");
		String cmd = "list \\place SOC";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = TasksFormatter.format(
				DataManager.getInstance().listAll(ParserFacade.getInstance().parseCommand(cmd).get(0)),
				TasksFormatter.DETAIL_VIEW_TYPE) + "\n";
		assertEquals(expectedResult, result);
	}

	/* This is a boundary case for the limitNumberOfTasks method */
	public void testLimit() throws InvalidCommandException {
		ArrayList<Task> testLimit = new ArrayList<Task>();
		testLimit.add(new Task("meeting with prof"));
		testLimit.add(new Task("2021 homework i cri"));
		String command = "list 2";
		String result = LogicController.onCommandProcess(command);
		String expectedResult = TasksFormatter.format(testLimit, TasksFormatter.DETAIL_VIEW_TYPE) + "\n";
		assertEquals(expectedResult, result);
	}

	public void testAdd() throws InvalidCommandException {
		String cmd = "add cs2103 v0.2 \\p high \\t school \\sdate 23/10/2015 9:00am \\place soc";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Added Description: cs2103 v0.2 Type: school Priority: high Location: soc Start Date: 23/10 09AM 2015"
				+ "\n";
		assertEquals(expectedResult, result);
	}
	
	//@@ZhangLei A0093966L
	/* This is a boundary case for adding the same task */
	public void testAddRepeat() throws InvalidCommandException {
		String cmd1 = "add dinner with porpor \\p high \\t personal \\sdate 24/10/2015 6:00pm \\place home";
		String cmd2 = "add dinner with porpor \\t personal \\p high \\sdate 24/10/2015 6:00pm \\place home";
		LogicController.onCommandProcess(cmd1);
		String result = LogicController.onCommandProcess(cmd2);
		String expectedResult = "The exact same task already exists in system.\n";
		assertEquals(expectedResult, result);
	}

	/*
	 * This is a boundary case for the delete method as deleting task that is
	 * named in full
	 */
	public void testDeleteFullName() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete EE2020 homework";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: EE2020 homework" + "\n";
		assertEquals(expectedResult, result);
	}

	/*
	 * This is a boundary case for the delete method as deleting task that is
	 * partially named
	 */
	public void testDeletePartialName() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete 2020";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: 2020" + "\n";
		assertEquals(expectedResult, result);
	}
	
	
}
