package application;
//@@nghuiyirebecca A0130876B
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import application.controller.DataManager;
import application.controller.LogicController;
import application.controller.parser.ParserFacade;
import application.exception.InvalidCommandException;
import application.model.LocalStorage;
import application.utils.TasksFormatter;

public class LogicControllerTest {
	LocalStorage file;
	String curFilePath;
	
	/* This is a boundary case for the listAll method */
	@Test
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
	@Test
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

	public void testAdd() throws InvalidCommandException {
		String result = LogicController.onCommandProcess("add cs2103 v0.2 \\p high \\t school \\sdate 23/10/2015 9:00am \\place soc");
		String expectedResult = "Added Description: cs2103 v0.2 Type: school Priority: high Location: soc Start Date: 23/10 09AM 2015"
				+ "\n";
		assertEquals(expectedResult, result);
	}
	
	//@@ZhangLei A0093966L
	/* This is a boundary case for adding the same task */
	@Test
	public void testAddRepeat() throws InvalidCommandException {
		LogicController.onCommandProcess("add dinner with porpor \\p high \\t personal \\sdate 24/10/2015 6:00pm \\place home");
		String result = LogicController.onCommandProcess("add dinner with porpor \\t personal \\p high \\sdate 24/10/2015 6:00pm \\place home");
		String expectedResult = "The exact same task already exists in system.\n";
		assertEquals(expectedResult, result);
	}

	/*
	 * This is a boundary case for the delete method as deleting task that is
	 * named in full
	 */
	@Test
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
	@Test
	public void testDeletePartialName() throws InvalidCommandException {
		String cmd = "add EE4240 homework \\edate 28/10/2015 \\p high";
		String delete = "delete 4240";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: 4240" + "\n";
		assertEquals(expectedResult, result);
	}
	
	/*
	 * This is a boundary case for the delete method as deleting one of the tasks having
	 * the same names
	 */
	@Test
	public void testDeleteOneFromMultipleOccurance() throws InvalidCommandException {
		String cmd1 = "add EE2021 homework \\p high";
		String cmd2 = "add EE2021 homework \\p normal";
		String delete = "delete EE2021 homework";
		LogicController.onCommandProcess(cmd1);
		LogicController.onCommandProcess(cmd2);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "There is more than one match, please choose from the following tasks.\n" + "\n" +
				"    Description                    Start Date           End Date             Location             Type            Priority       \n"+
				"1   EE2021 homework                                                                               normal          high           \n"+
				"2   EE2021 homework                                                                               normal          normal         \n"+"\n";
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testEditTask() throws InvalidCommandException {
		String cmd = "add EE2022 homework \\p high";
		String edit = "edit EE2022 homework \\p low \\place home";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(edit);
		String expectedResult = "Successfully edited: EE2022 homework\n";
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testDoneTask() throws InvalidCommandException {
		LogicController.onCommandProcess("add CS2103 quiz 12");
		LogicController.onCommandProcess("add CS1010 homework");
		LogicController.onCommandProcess("add watch movie at engin");
		String done2 = "ok CS1010 homework";
		String result = LogicController.onCommandProcess(done2);
		String expectedResult = "Done task: CS1010 homework\n";
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testUndoneTask() throws InvalidCommandException {
	}
}
