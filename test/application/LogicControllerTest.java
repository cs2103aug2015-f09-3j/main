package application;

import static org.junit.Assert.*;

import org.junit.Test;

import application.controller.LogicController;
import application.exception.InvalidCommandException;

public class LogicControllerTest {

	@Test
	public void testAdd1() throws InvalidCommandException {
		String cmd = "add cs2103 v0.2 \\p high \\t school \\sdate 23/10/2015 9am \\place soc";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Added Description: cs2103 v0.2 Type: school Priority: high Location: soc Start Date: 23/10 9AM 2015";
		assertEquals(result, expectedResult);
	}

	@Test
	public void testAdd2() throws InvalidCommandException {
		String cmd = "add dinner with porpor \\p high \\t personal \\sdate 24/10/2015 6pm \\place home";
		String result = LogicController.onCommandProcess(cmd);
		String expectedResult = "Added Description: dinner with porpor Type: personal Priority: high Location: home Start Date: 24/10 6PM 2015";
		assertEquals(result, expectedResult);
	}

	@Test
	//Delete with full name of the task given
	public void testDelete1() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete EE2020 homework";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: EE2020 homework";
		assertEquals(result, expectedResult);
	}

	@Test
	//Delete with partial name of the task given
	public void testDelete2() throws InvalidCommandException {
		String cmd = "add EE2020 homework \\edate 28/10/2015 \\p high";
		String delete = "delete homework";
		LogicController.onCommandProcess(cmd);
		String result = LogicController.onCommandProcess(delete);
		String expectedResult = "Successfully deleted: homework";
		assertEquals(result, expectedResult);
	}

}

