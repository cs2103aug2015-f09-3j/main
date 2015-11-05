package application.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//@@LimYouLiang A0125975U
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import application.model.Task;
import application.utils.GoogleCalendarUtility;
import application.utils.TokenManager;

/**
 * @author YL Lim
 *
 */
public class GoogleCalendarManager {

	private static GoogleCalendarManager instance;

	/** Default File name for deletion offline record **/
	public static final String DELETION_FILE_NAME = "deletionCache.txt";

	/** Application name. */
	private static final String APPLICATION_NAME = "toDoo";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/toDoo");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart. */
	private static final List<String> SCOPES = Arrays
			.asList(new String[] { CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR });

	com.google.api.services.calendar.Calendar service;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	private Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = this.getClass().getResourceAsStream("client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Calendar client service.
	 *
	 * @return an authorized Calendar client service
	 * @throws IOException
	 */
	private com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
		Credential credential = authorize();
		return new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	private GoogleCalendarManager() {
		try {
			service = getCalendarService();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Pre-Condition: Internet is up and quickAddMsg is not null. This function
	 * will call the google quickadd api and return the event created. If
	 * Successful, it will return the Task, otherwise it will return null.
	 * 
	 * @param quickAddMsg
	 *            : quickAdd message
	 * @return Task if successful in creation, or null if fail to create task.
	 */

	public Integer quickAddToGCal(String quickAddMsg) {

		Event createdEvent;
		try {
			createdEvent = service.events().quickAdd("primary", quickAddMsg).execute();
			Task task = this.convertEventToTask(createdEvent);
			int googleAddSuccess = DataManager.getInstance().addNewTask(task);
			return googleAddSuccess;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void syncOfflineDeletionRecords() {
		ArrayList<String> records = getListOfRecordsFromFile();

		if (records.size() == 0) {
			return;
		}

		try {
			for (String eventId : records) {
				service.events().delete("primary", eventId).execute();
			}
		} catch (IOException e) {
			return;
		}

		// If success, clear the deletion file.
		File file = new File(DELETION_FILE_NAME);
		assert file.exists() == true;
		file.delete();

	}

	/**
	 *
	 */
	private ArrayList<String> getListOfRecordsFromFile() {
		ArrayList<String> records = new ArrayList<String>();

		File file = new File(DELETION_FILE_NAME);

		if (!file.exists()) {
			return records;
		}

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

		return records;
	}

	/**
	 *
	 */
	private List<Event> getCalendarEvents(String syncToken) {

		DateTime now = new DateTime(System.currentTimeMillis());

		Calendar.Events.List request = null;
		try {
			request = service.events().list("primary");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (syncToken != null && !syncToken.equals("")) {
			request.setSyncToken(syncToken);
		} else {
			request.setTimeMin(now);
		}
		Events events = null;
		String pageToken = null;
		List<Event> items = null;
		do {
			request.setPageToken(pageToken);
			try {
				events = request.execute();
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == 410) {
					// A 410 status code, "Gone", indicates that the sync token
					// is invalid.
					System.out.println("Invalid sync token, clearing event store and re-syncing.");
					TokenManager.getInstance().clearToken();

				} else {
					return null;
				}
			} catch (IOException e) {

				e.printStackTrace();

				if (e instanceof TokenResponseException) {
					System.out.println("Renewing");
					TokenManager.getInstance().clearToken();
					GoogleCalendarUtility.recursiveDelete(DATA_STORE_DIR);
					try {
						service = getCalendarService();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}

			}

			items = events.getItems();
			if (items.size() == 0) {
				System.out.println("No upcoming events found.");
			} else {
				System.out.println("Upcoming events");
				for (Event event : items) {
					try {
						DateTime start = event.getStart().getDateTime();
						if (start == null) {
							start = event.getStart().getDate();
						}
						System.out.printf("%s (%s)\n", event.getSummary(), start);
					} catch (Exception e) {
						LogManager.getInstance().log(e.toString());
					}
				}
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);

		TokenManager.getInstance().setToken(events.getNextSyncToken());

		return items;

	}

	public void performSync() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				performUpSync();
				performDownSync();
				syncOfflineDeletionRecords();
			}
		}).run();
	}

	/**
	 *
	 */
	private void performDownSync() {
		List<Event> lists = null;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		String lastToken = TokenManager.getInstance().getLastToken();
		lists = getCalendarEvents(lastToken);

		for (Event event : lists) {
			if (!event.getStatus().equals("cancelled")) {
				taskArr.add(convertEventToTask(event));
			} else {
				DataManager.getInstance().deleteTaskByGCalId(event.getId());
			}
		}

		// Compare the downloaded and local lastUpate. depending which one is
		// latest.
		// if server lastUpdate > local , update local
		// if local lastUpdate > server , update server

		for (Task task : taskArr) {

			Task localTask = DataManager.getInstance().findTaskByGCalId(task.getgCalId());
			if (localTask == null) {
				// does not exist in local storage. its a new task. update local
				DataManager.getInstance().addNewTask(task);
			} else {
				// compare lastUpdate here
				if (localTask.getLastServerUpdate() > task.getLastServerUpdate()) {
					// update server
					Event event = mapTaskToEvent(localTask);
					updateServer(event);

				} else {
					// update local
					DataManager.getInstance().updateTask(task);
				}

			}

		}
	}

	public void removeTaskFromServer(String eventId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					service.events().delete("primary", eventId).execute();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).run();

	}

	/**
	 * @param event
	 */
	private void updateServer(Event event) {
		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(24 * 60),
				new EventReminder().setMethod("popup").setMinutes(10), };
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);

		try {
			service.events().update("primary", event.getId(), event).execute();
			// event = service.events().update("primary", event).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param event
	 */
	private Task convertEventToTask(Event event) {

		Task tmpTask = new Task();

		tmpTask.setTextContent(event.getSummary());

		try {
			tmpTask.setType_argument(event.getExtendedProperties().getShared().get("type"));
		} catch (NullPointerException e) {
			// Its normal to have null pointer exception here. google self
			// created event won't have this instance.
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
	 *
	 */
	private void performUpSync() {

		performNewTaskSync();
		performUpdatedTaskSync();
	}

	/**
	 *
	 */
	private void performUpdatedTaskSync() {
		ArrayList<Task> lists = DataManager.getInstance().getListOfModifiedTask();
		HashMap<Task, Long> lastServerUpdateMap = new HashMap<Task, Long>();

		for (Task task : lists) {

			Event event = mapTaskToEvent(task);

			try {
				event = service.events().update("primary", task.getgCalId(), event).execute();
				lastServerUpdateMap.put(task, event.getUpdated().getValue());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		DataManager.getInstance().updateServerUpdateTime(lastServerUpdateMap);

	}

	/**
	 *
	 */
	private void performNewTaskSync() {
		ArrayList<Task> lists = DataManager.getInstance().getListOfUnSyncNonFloatingTasks();
		HashMap<Task, String> hashmap = new HashMap<Task, String>();
		for (Task task : lists) {

			Event event = mapTaskToEvent(task);

			EventReminder[] reminderOverrides = new EventReminder[] {
					new EventReminder().setMethod("email").setMinutes(24 * 60),
					new EventReminder().setMethod("popup").setMinutes(10), };
			Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
					.setOverrides(Arrays.asList(reminderOverrides));
			event.setReminders(reminders);

			try {
				event = service.events().insert("primary", event).execute();
				hashmap.put(task, event.getId());
				// task.setgCalId(event.getId());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		DataManager.getInstance().updateGCalId(hashmap);
	}

	/**
	 * @param task
	 * @return
	 */
	private Event mapTaskToEvent(Task task) {
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

		// event.setStart(start);
		// event.setEnd(end);

		if (task.getStart_date() == null) {
			// event.setEndTimeUnspecified(true);
			event.setStart(endMinus1Hr);
			event.setEnd(end);

			hashMap.put("has_start_date", "no");
			// event.setUnknownKeys(map);
			ExtendedProperties prop = new ExtendedProperties();
			prop.setShared(hashMap);

			event.setExtendedProperties(prop);
			// event.set("has_start_date", false);

		} else {

			event.setStart(start);
			event.setEnd(end);
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put("has_start_date", 1);
			// event.setUnknownKeys(map);

			hashMap.put("has_start_date", "yes");
			// event.setUnknownKeys(map);
			ExtendedProperties prop = new ExtendedProperties();
			prop.setShared(hashMap);

			event.setExtendedProperties(prop);

			// event.set("has_start_date", true);
		}

		return event;
	}

	public static GoogleCalendarManager getInstance() {
		if (instance == null) {
			instance = new GoogleCalendarManager();

		}

		return instance;
	}

}
