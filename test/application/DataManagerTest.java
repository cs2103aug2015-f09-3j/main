package application;

import org.junit.Test;

import application.controller.DataManager;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Parameter;
import application.model.Task;

import static org.junit.Assert.*;

import java.util.ArrayList;
import com.google.gson.*;

public class DataManagerTest {
	
	LocalStorage file = new LocalStorage("test.txt");
	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	ArrayList<Task> test = new ArrayList<Task>();
	Command cmd = new Command(2,"",parameters);
	Task task1 = new Task("go buy milk");
	Task task2 = new Task("go to school");
	Gson gson = new Gson();

	@Test
	public void testList1() {
		task2.setPlace_argument("nus");
		
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		
		parameters.clear();
		cmd.setParameter(parameters);
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
	
	/* This is a boundary case for one parameter passed in to the listAll(Command cmd) method */
	@Test
	public void testList2(){
		task2.setPlace_argument("nus");
		
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		parameters.clear();
		parameters.add(new Parameter(5,"nus"));
		cmd.setParameter(parameters);
		test.clear();
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
	
	/* This is a boundary case for multiple parameters passed in to the listAll(Command cmd) method */
	@Test
	public void testList3(){
		task2.setPlace_argument("nus");
		
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		parameters.clear();
		parameters.add(new Parameter(2, "normal"));
		parameters.add(new Parameter(1, "normal"));
		cmd.setParameter(parameters);
		test.clear();
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd));
	}
}
