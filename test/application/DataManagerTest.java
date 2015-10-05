package application;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.ArrayList;

public class DataManagerTest {
	
	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	ArrayList<String> test = new ArrayList<String>();
	@Test
	public void test() {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new Parameter(1,"p"));
		parameters.add(new Parameter(2,"t"));
		Command cmd = new Command(1,"Hello World", parameters);
		String test1 = "Hello World " + '\\' + "p " + '\\' + "t";
		DataManager testObj = DataManager.getInstance();
		assertEquals(test1,testObj.addNewTask(Parser.getInstance().convertAddCommandtoTask(cmd)));
		test.add("Hello world");
		test.add("Test case 1");
		assertEquals(test,testObj.listAll(null));
		
	}

}
