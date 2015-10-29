package application.model;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalStorage {

	private static Logger logger = Logger.getLogger("LocalStorage");

	private static File file;
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	//change file name
	public int changePath(String newPath) {
		File newFile = new File(newPath);
		try{
			newFile.createNewFile();
		}catch(IOException ex){
			return -1;
		}
		clear(newFile);
		saveToFile(readFile(),newFile);
		fileName = newPath;
		file.delete();
		file = newFile;
		return 1;		
	}
	
	public LocalStorage(String path) {
		fileName = path;
		file = new File(fileName);
		try{
			file.createNewFile();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
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
			e.printStackTrace();
		}
        try {
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return textLine;
	}
	
	public void clear() {
		saveToFile(new ArrayList<String>(),file);
	}
	private void clear(File f) {
		saveToFile(new ArrayList<String>(),f);
	}

	private void saveToFile(ArrayList<String> list, File f) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
			for (String elem : list) {;
				bw.append(elem);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			e.printStackTrace();
		}
	}
	
	public void logging(ArrayList<String> list) {
		// log a message at INFO level
		logger.log(Level.INFO, "going to start processing");
		try{
			saveToFile(list,file);
		} catch (Exception ex) {
		//log a message at WARNING level
			logger.log(Level.WARNING, "processing error", ex);
		}
		logger.log(Level.INFO, "end of processing");
	}

}
