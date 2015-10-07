package application;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import com.google.gson.*;

import com.google.gson.Gson;

public class LocalStorage {
	private static final String TEST_TXT = "test.txt";
	/*
	 * below is for testing purpose(to be removed) Assume that APIs similar to
	 * CE2 functionality provided.
	 */
	private static File file;

	public LocalStorage() {
		file = new File(TEST_TXT);
	}

	public ArrayList<String> readFile() {
		ArrayList<String> textLine = new ArrayList<String>();
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
        String aDataRow = "";
        try {
			while ((aDataRow = myReader.readLine()) != null) 
			{
			    textLine.add(aDataRow);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			myReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return textLine;
	}

	/*public void writeTask(String details) {
		list.add(details);
		saveToFile();
	}

	public void sort() {
		Collections.sort(list);
		saveToFile();
	}

	private int numberOfTasks() {
		return list.size();
	}

	public boolean delete(String task) {
		boolean result= list.remove(task);
		saveToFile();
		return result;
	}*/

	public void clear() {
		saveToFile(new ArrayList<String>());
	}

	/*public ArrayList<String> search(String textContent) {
		ArrayList<String> tasks = new ArrayList<String>();
		if (textContent == null || textContent.trim().length() == 0) {
			tasks.add("Invalid search. Please enter keywords");
		}
		for (int i = 0; i < numberOfTasks(); i++) {
			if (list.get(i).contains(textContent)) {
				tasks.add(list.get(i));
			}
		}
		return tasks;
	}*/

	public int changePath(String textContent) { 
		return 1;
	}

	public void saveToFile(ArrayList<String> list) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (String elem : list) {;
				bw.append(elem);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
		}
	}
}
