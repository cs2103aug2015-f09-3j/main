package application;

import java.util.ArrayList;

public class LocalStorage {
	/*	below is for testing purpose(to be removed)
	 	Assume that APIs similar to CE2 functionality provided.
	*/
	public Integer writeTask(String details){
		return 1;
	}
	public Integer sort(){
		return 1;
	}
	
	public ArrayList<String> readFile(){
		ArrayList<String> testList = new ArrayList<String>();
		testList.add("Hello world");
		testList.add("Test case 1 " + '\\' + "p " + '\\' +"t");
		return testList;
	}
}
