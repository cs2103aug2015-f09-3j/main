package application.utils;

//@@author A0125975U-reused
import java.io.BufferedReader;
//@@author  A0125975U
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Event.ExtendedProperties;

import application.controller.GoogleCalendarManager;
import application.model.Task;

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

	// @@author A0125975U
	/**
	 * This function maps Task to Event Object.
	 * 
	 * @param task
	 *            : toDoo task model.
	 * @return Google Calendar Event object.
	 */
	public static Event mapTaskToEvent(Task task) {
		Event event = new Event().setSummary(task.getTextContent()).setLocation(task.getPlace_argument())
				.setDescription(task.getPriority_argument());
		Map<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("type", task.getType_argument());

		if (!task.getgCalId().equals("")) {
			event.setId(task.getgCalId());
		}

		EventDateTime start = new EventDateTime();
		if (task.getStart_date() != null) {
			start.setDateTime(new DateTime(task.getStart_date()));
		}

		EventDateTime end = new EventDateTime();
		if (task.getEnd_date() != null) {
			end.setDateTime(new DateTime(task.getEnd_date()));

		}

		EventDateTime endMinus1Hr = new EventDateTime();
		endMinus1Hr.setDateTime(new DateTime(new Date(task.getEnd_date().getTime() - 3600000)));

		if (task.getStart_date() == null) {
			event.setStart(endMinus1Hr);
			event.setEnd(end);
			hashMap.put("has_start_date", "no");
			ExtendedProperties prop = new ExtendedProperties();
			prop.setShared(hashMap);
			event.setExtendedProperties(prop);

		} else {
			event.setStart(start);
			event.setEnd(end);
			hashMap.put("has_start_date", "yes");
			ExtendedProperties prop = new ExtendedProperties();
			prop.setShared(hashMap);
			event.setExtendedProperties(prop);
		}

		return event;
	}

	/**
	 * This function maps Event Object to Task Object
	 * 
	 * @param event
	 *            : Google Calendar Event Object
	 */
	public static Task mapEventToTask(Event event) {

		Task tmpTask = new Task();

		tmpTask.setTextContent(event.getSummary());

		if (event.getExtendedProperties() != null && event.getExtendedProperties().getShared() != null) {
			tmpTask.setType_argument(event.getExtendedProperties().getShared().get("type"));
		}

		tmpTask.setPriority_argument(event.getDescription());

		tmpTask.setPlace_argument(event.getLocation());
		tmpTask.setLastServerUpdate(event.getUpdated().getValue());
		tmpTask.setgCalId(event.getId());
		String has_start_date = null;
		ExtendedProperties prop = event.getExtendedProperties();
		Map<String, String> hash = null;
		if (prop != null) {
			hash = prop.getShared();
		}

		if (hash != null) {
			has_start_date = hash.get("has_start_date");
		}

		if (has_start_date == null) {
			has_start_date = "yes";
		}

		if (event.getStart() != null && has_start_date.equals("yes")) {
			DateTime dt = event.getStart().getDate();
			if (dt == null) {
				dt = event.getStart().getDateTime();
			}
			tmpTask.setStart_date(new Date(dt.getValue()));
		} else {
			tmpTask.setStart_date(null);
		}

		if (event.getEnd() != null) {
			DateTime dt = event.getEnd().getDate();
			if (dt == null) {
				dt = event.getEnd().getDateTime();
			}
			tmpTask.setEnd_date(new Date(dt.getValue()));
		}

		return tmpTask;
	}

	/**
	 * This function take in a arraylist of records, and store the data into the
	 * file to the records.
	 * 
	 * @param records
	 *            : the place to be store in.
	 * @param file
	 *            : the file to be read from.
	 */
	public static void getRecordsFromFile(ArrayList<String> records, File file) {
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String aDataRow = "";

		try {
			while ((aDataRow = myReader.readLine()) != null) {
				records.add(aDataRow);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			myReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function will add the eventId to its deletion records.
	 * 
	 * @param eventId
	 *            : Google Calendar Event Id.
	 */
	public static void addToOfflineDeletionRecords(String eventId) {
		if (eventId != null) {
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
			} catch (NullPointerException e) {

			}
		}

	}

	// @@author A0125975U-reused
	/**
	 * Disclaimer : this method is from :
	 * http://www.journaldev.com/833/how-to-delete-a-directoryfolder-in-java-
	 * recursion
	 * 
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
