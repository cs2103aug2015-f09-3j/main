package application.utils;
//@@LimYouLiang A0125975U
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	/**
	 * This function will add the eventId to its deletion records.
	 * @param eventId : Google Calendar Event Id.
	 */
	public static void addToOfflineDeletionRecords(String eventId) {

		try {

			File file = new File(GoogleCalendarManager.DELETION_FILE_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bw = new BufferedWriter(fileWritter);
			bw.write(eventId);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	/**
	 * Disclaimer : this method is from : http://www.journaldev.com/833/how-to-delete-a-directoryfolder-in-java-recursion
	 * @param file
	 */
	public static void recursiveDelete(File file) {
		// to end the recursive loop
		if (!file.exists())
			return;

		// if directory, go inside and call recursively
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				// call recursively
				recursiveDelete(f);
			}
		}
		// call delete to delete files and empty directory
		file.delete();
	}

}
