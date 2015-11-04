package application.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import application.controller.GoogleCalendarManager;

public class GoogleCalendarUtility {
	
	
	public static boolean hasInternetConnection() {
		try {
			URL urlObj = new URL("http://www.google.com");
			URLConnection connection = urlObj.openConnection();
			connection.connect();
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	public static void addToOfflineDeletionRecords(String eventId) {

		try {
			
			
			
			
			File file = new File(GoogleCalendarManager.DELETION_FILE_NAME);
			if(!file.exists()){
    			file.createNewFile();
    		}
			
			FileWriter fileWritter = new FileWriter(file.getName(),true);
			BufferedWriter bw = new BufferedWriter(fileWritter);
			bw.write(eventId);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
