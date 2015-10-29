package application;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import application.controller.DataManager;
import application.controller.LogicController;
import application.controller.parser.ParserFacade;
import application.exception.InvalidCommandException;
import application.model.Task;
import application.utils.TasksFormatter;

public class LogicControllerTest {

	@Test
	public void testAll() throws InvalidCommandException{
		testList1();
		testLimit();
		testAdd1();
		testAdd2();
		testDelete1();
		testDelete2();
	}

	 /* This is a boundary case for the listAll method */
	public void testList1() throws InvalidCommandException {
		LogicController.onCommandProcess("add meeting with prof");
		LogicController.onCommandProcess("add 2021 homework i cri");
		LogicController.onCommandProcess("add free lunch at engin");
		LogicController.onCommandProcess("add llao llao with tenyee");
		String cmd = "list";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = TasksFormatter.format(DataManager.getInstance().listAll(ParserFacade.getInstance().parseCommand(cmd).get(0)), TasksFormatter.DETAIL_VIEW_TYPE);
		assertEquals(expectedResult, result);
	}

	/* This is a boundary case for the limitNumberOfTasks method*/
	public void testLimit() throws InvalidCommandException {
		ArrayList<Task> testLimit = new ArrayList<Task>();
		testLimit.add(new Task("meeting with prof"));
		testLimit.add(new Task("2021 homework i cri"));
		String command = "list 2";
		String result = LogicController.onCommandProcess(command);
		String expectedResult = TasksFormatter.format(testLimit, TasksFormatter.DETAIL_VIEW_TYPE);
		assertEquals(expectedResult, result);
	}


	public void testAdd1() throws InvalidCommandException {
		String cmd = "add cs2103 v0.2 \\p high \\t school \\sdate 23/10/2015 9am \\place soc";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Added Description: cs2103 v0.2 Type: school Priority: high Location: soc Start Date: 23/10 9AM 2015";
		assertEquals(expectedResult, result);
	}


	public void testAdd2() throws InvalidCommandException {
		String cmd = "add dinner with porpor \\p high \\t personal \\sdate 24/10/2015 6pm \\place home";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Added Description: dinner with porpor Type: personal Priority: high Location: home Start Date: 24/10 6PM 2015";
		assertEquals(expectedResult, result);
	}


	/* This is a boundary case for the delete method as deleting task that is named in full*/
	public void testDelete1() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete EE2020 homework";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: EE2020 homework";
		assertEquals(expectedResult, result);
	}


	/* This is a boundary case for the delete method as deleting task that is partially named*/
	public void testDelete2() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete 2020";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: 2020";
		assertEquals(expectedResult, result);
	}



}

