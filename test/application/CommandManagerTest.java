package application;

//@@ZhangLei A0093966L

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import org.junit.Test;
import application.exception.InvalidCommandException;
import application.model.Command;
import application.model.Parameter;
import application.controller.CommandManager;
import application.controller.DataManager;

public class CommandManagerTest {
	
	@Test
	public void testAll() throws InvalidCommandException {
		DataManager.getInstance().switchToTestingMode("testingMode.txt");
		testAddCorrectCommandWithoutParameters();
		testAddCorrectCommandWithParameters();
		testAddRepeatingCommand();
		testListCommandsWithPriority();
		cleanUp();
	}
	
	public void testAddCorrectCommandWithoutParameters() {
		ArrayList<Parameter> parameter= new ArrayList<Parameter>();
		parameter.add(new Parameter(1, "normal"));
		parameter.add(new Parameter(2, "normal"));
		Command addTaskWithoutParameters = new Command(1, "CS2103 v0.1 demo", parameter);
		assertEquals("Added Description: CS2103 v0.1 demo Type: normal Priority: normal", 
				CommandManager.executeCommand(addTaskWithoutParameters));
	}
	
	public void testAddCorrectCommandWithParameters() {
		ArrayList<Parameter> parameter= new ArrayList<Parameter>();
		parameter.add(new Parameter(1, "very high"));
		parameter.add(new Parameter(2, "school"));
		parameter.add(new Parameter(3, "06/11/2015 9am"));
		parameter.add(new Parameter(4, "06/11/2015 10am"));
		parameter.add(new Parameter(5, "SOC B1"));
		Command addTaskWithParameters = new Command(1, "CS2103 v0.2 demo", parameter);
		assertEquals("Added Description: CS2103 v0.2 demo Type: school Priority: very high Location: SOC B1", 
				CommandManager.executeCommand(addTaskWithParameters));
	}

	public void testAddRepeatingCommand() {
		Integer commandType = 1;
		ArrayList<Parameter> parameter= new ArrayList<Parameter>();
		parameter.add(new Parameter(1, "normal"));
		parameter.add(new Parameter(2, "normal"));
		Command addTaskWithoutParameters = new Command(commandType, "CS2103 v0.3 demo", parameter);
		CommandManager.executeCommand(addTaskWithoutParameters);
		assertEquals("The exact same task already exists in system.", 
				CommandManager.executeCommand(addTaskWithoutParameters));
	}
	
	public void testListCommandsWithPriority() {
		ArrayList<Parameter> parameter= new ArrayList<Parameter>();
		parameter.add(new Parameter(1, "high"));
		parameter.add(new Parameter(2, "normal"));
		Command command1 = new Command(1, "CS2103 v0.4 demo", parameter);
		CommandManager.executeCommand(command1);
		Command command2 = new Command(1, "CS2103 v0.5 submission", parameter);
		CommandManager.executeCommand(command2);
		parameter.set(1, new Parameter(1, "low"));
		Command command3 = new Command(1, "CS2103 v0.5 demo", parameter);
		CommandManager.executeCommand(command3);
		ArrayList<Parameter> list= new ArrayList<Parameter>();
		list.add(new Parameter(1, "high"));
		Command listPriority = new Command(2, "", list);
		assertEquals("    Description                    Start Date           End Date             Location             Type            Priority       \n" + 
				"1   CS2103 v0.4 demo                                                                              normal          high           \n" + 
				"2   CS2103 v0.5 submission                                                                        normal          high           \n", 
				CommandManager.executeCommand(listPriority));
	}
	
	public void cleanUp() {
		try {
			File file = new File("testingTestController.txt");
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
