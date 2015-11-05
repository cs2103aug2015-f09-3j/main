package application;
//@@author LimQiWen A0125980B
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.Gson;

import application.controller.DataManager;
import application.model.Command;
import application.model.LocalStorage;
import application.model.Parameter;
import application.model.Task;

public class DataManagerTest {
	
	LocalStorage file;
	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	ArrayList<Task> test = new ArrayList<Task>();
	Command cmd1 = new Command(3,"test.txt",parameters);
	Command cmd2 = new Command(2,"",parameters);
	Task task1 = new Task("go buy milk");
	Task task2 = new Task("go to school");
	Gson gson = new Gson();
	String filepath;
	@Test
	public void testList1() {
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader("filePath.txt"));
			filepath = br.readLine();
			br.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		if (filepath == null){
			filepath = "toDoo.txt";
		}
		file = new LocalStorage(filepath);
		task2.setPlace_argument("nus");
		
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		
		parameters.clear();
		cmd2.setParameter(parameters);
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd2));
	}
	
	/* This is a boundary case for one parameter passed in to the listAll(Command cmd) method */
	@Test
	public void testList2(){
		task2.setPlace_argument("nus");
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader("filePath.txt"));
			filepath = br.readLine();
			br.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		if (filepath == null){
			filepath = "toDoo.txt";
		}
		file = new LocalStorage(filepath);
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		parameters.clear();
		parameters.add(new Parameter(5,"nus"));
		cmd2.setParameter(parameters);
		test.clear();
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd2));
	}
	
	/* This is a boundary case for multiple parameters passed in to the listAll(Command cmd) method */
	@Test
	public void testList3(){
		task2.setPlace_argument("nus");
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader("filePath.txt"));
			filepath = br.readLine();
			br.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		if (filepath == null){
			filepath = "toDoo.txt";
		}
		file = new LocalStorage(filepath);
		file.clear();
		ArrayList<String> list = new ArrayList<String>();
		list.add(gson.toJson(task1));
		list.add(gson.toJson(task2));
		file.saveToFile(list);
		
		parameters.clear();
		parameters.add(new Parameter(2, "normal"));
		parameters.add(new Parameter(1, "normal"));
		cmd2.setParameter(parameters);
		test.clear();
		test.add(task1);
		test.add(task2);
		assertEquals(test,DataManager.getInstance().listAll(cmd2));
	}
}
