package application;

import org.junit.Test;

import application.controller.DataManager;
import application.model.Command;
import application.model.Parameter;
import application.model.Task;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class DataManagerTest {
	
	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	ArrayList<Task> test = new ArrayList<Task>();
	Command cmd = new Command(2,"",parameters);
	Task task1 = new Task("go buy milk");
	Task task2 = new Task("go to school");
	
	/* This is a boundary case for no parameters passed in to the listAll(Command cmd) method" */
	@Test
	public void testList1() {
		task2.setPlace_argument("nus");
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
	
	/* This is a boundary case for one parameter passed in to the listAll(Command cmd) method */
	public void testList2(){
		parameters.add(new Parameter(5,"nus"));
		test.clear();
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
	
	/* This is a boundary case for multiple parameters passed in to the listAll(Command cmd) method */
	public void testList3(){
		parameters.clear();
		parameters.add(new Parameter(2, "normal"));
		parameters.add(new Parameter(1, "normal"));
		test.clear();
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
}
