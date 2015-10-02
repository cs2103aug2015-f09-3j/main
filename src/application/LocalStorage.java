package application;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

public class LocalStorage {
	private static final String TEST_TXT = "test.txt";
	/*
	 * below is for testing purpose(to be removed) Assume that APIs similar to
	 * CE2 functionality provided.
	 */
	private static File file;
	private static ArrayList<String> list;
	private static ArrayList<String> listBackUp;

	public LocalStorage() {
		file = new File(TEST_TXT);
		list = readFile();
	}

	public ArrayList<String> readFile() {
		ArrayList<String> textList = new ArrayList<String>();
		BufferedReader reader = null;
		String text = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			text = reader.readLine();
			if (text == null) {
				System.out.println();
				System.out.println(file.getName() + " is empty");
				textList = null;
			} else {
				while (text != null) {
					textList.add(text);
					text = reader.readLine();
				}
			}
			return textList;
		} catch (IOException ex) {
			ex.printStackTrace();
			textList = null;
			return textList;
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void writeTask(String details) {
		list.add(details);
		saveToFile();
	}

	public void sort() {
		Collections.sort(list);
		saveToFile();
	}

	public int numberOfTasks() {
		return list.size();
	}

	public boolean delete(String task) {
		boolean result= list.remove(task);
		saveToFile();
		return result;
	}

	public void clear() {
		list.clear();
		saveToFile();
	}

	public ArrayList<String> search(String textContent) {
		ArrayList<String> tasks = new ArrayList<String>();
		if (textContent == null || textContent.trim().length() == 0) {
			tasks.add("Invalid search. Please enter keywords");
		}
		int numOfFoundedtasks = 0;
		for (int i = 0; i < numberOfTasks(); i++) {
			if (list.get(i).contains(textContent)) {
				tasks.add(list.get(i));
				numOfFoundedtasks += 1;
			}
		}
		return tasks;
	}

	public int changePath(String textContent) {
		listBackUp = list;
		return 1;
	}

	private static void saveToFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (String elem : list) {
				bw.write(elem);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
		}
	}
}
