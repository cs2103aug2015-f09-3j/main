package application.controller;

//@@author  A0125975U-reused
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

	// @@Google
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
	
	/**
	 * This function retrieve Event from the Events instance.
	 * 
	 * @param events
	 */
	private List<Event> retrieveEvents(Events events) {
		List<Event> items;
		items = events.getItems();
		if (items.size() == 0) {
			System.out.println("No new event, Local is already the latest.");
		} else {
			System.out.println("New/Update events found.");
			for (Event event : items) {
				try {
					DateTime start = event.getStart().getDateTime();
					if (start == null) {
						start = event.getStart().getDate();
					}
					System.out.printf("%s (%s)\n", event.getSummary(), start);
				} catch (Exception e) {
					LogManager.getInstance().log(this.getClass().getName(), e.toString());
				}
			}
		}

		return items;
	}

	
	
	//@@author A0125975U
	
	public com.google.api.services.calendar.Calendar getService(){
		return service;
	}


	public static GoogleCalendarManager getInstance() {
		if (instance == null) {
			instance = new GoogleCalendarManager();
		}

		return instance;
	}
	
	public void performSync() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				performUpSync();
				performDownSync();
				syncOfflineDeletionRecords();
			}
		}).start();
	}

	

	private GoogleCalendarManager() {
		try {
			service = getCalendarService();
		} catch (IOException e1) {
			LogManager.getInstance().log(this.getClass().getName(), e1.toString());
		}

	}

	
	/**
	 * This function syncs records of deletion that happen during offline mode.
	 * It clears its cache if successful.
	 */
	private void syncOfflineDeletionRecords() {
		ArrayList<String> records = getListOfDeletionsRecordsFromFile();

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
	 * This function get a list of deletions record from Cache.
	 */
	private ArrayList<String> getListOfDeletionsRecordsFromFile() {
		ArrayList<String> records = new ArrayList<String>();

		File file = new File(DELETION_FILE_NAME);

		if (!file.exists()) {
			return records;
		}
		GoogleCalendarUtility.getRecordsFromFile(records, file);

		return records;
	}

	

	/**
	 * This function retrieves Event from Google Calendar API based on a
	 * syncToken, if syncToken is null , It is the initial full sync.
	 * 
	 * @return : a list of events to be updated to local storage or null if it
	 *         fail.
	 */
	private List<Event> getCalendarEvents() {
		String syncToken = TokenManager.getInstance().getLastToken();
		Calendar.Events.List request = null;
		try {
			request = service.events().list("primary");
		} catch (IOException e1) {
			LogManager.getInstance().log(this.getClass().getName(), e1.toString());
			return null;
		}
		setInitialSyncOrContinuingSync(syncToken, request);
		return getEventsByIterateRequestUsingPageToken(request);
	}

	/**
	 * This function takes in a valid request object, and start retrieving the
	 * events from the google api.
	 * 
	 * @param request
	 *            : valid Calendar.Events.List instance
	 * @return list of events
	 */
	private List<Event> getEventsByIterateRequestUsingPageToken(Calendar.Events.List request) {
		List<Event> items = new ArrayList<Event>();
		String pageToken = null;
		Events events = null;
		do {

			request.setPageToken(pageToken);
			try {
				events = request.execute();
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == 410) {
					// A 410 status code, "Gone", indicates that the sync token
					// is invalid.
					LogManager.getInstance().log(this.getClass().getName(),
							"Invalid sync token, clearing event store and re-syncing.");
					TokenManager.getInstance().clearToken();

				}
			} catch (IOException e) {
				LogManager.getInstance().log(this.getClass().getName(), e.toString());
				if (e instanceof TokenResponseException) {
					renewAccountCredential();
				}

			}
			if (events != null) {
				items.addAll(retrieveEvents(events));
				pageToken = events.getNextPageToken();
			}
		} while (pageToken != null);

		TokenManager.getInstance().setToken(events.getNextSyncToken());
		return items;
	}

	/**
	 * This function sets the request obj for either initial sync or
	 * continuation sync.
	 * 
	 * @param syncToken
	 *            : null if for intial sync, otherwise the sync token from
	 *            previous sync.
	 * @param request
	 *            : valid Calendar.Events.List instance
	 */
	private void setInitialSyncOrContinuingSync(String syncToken, Calendar.Events.List request) {
		DateTime now = new DateTime(System.currentTimeMillis());
		if (syncToken != null && !syncToken.equals("")) {
			request.setSyncToken(syncToken);
		} else {
			request.setTimeMin(now);
		}
	}

	/**
	 * This function renews the account credential.
	 */
	private void renewAccountCredential() {
		System.out.println("Renewing");
		TokenManager.getInstance().clearToken();
		GoogleCalendarUtility.recursiveDelete(DATA_STORE_DIR);
		try {
			service = getCalendarService();
		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	

	
	/**
	 * 
	 */
	private void performDownSync() {
		List<Event> lists = null;
		ArrayList<Task> taskArr = new ArrayList<Task>();
		lists = getCalendarEvents();

		for (Event event : lists) {
			if (!event.getStatus().equals("cancelled")) {
				taskArr.add(GoogleCalendarUtility.mapEventToTask(event));
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
					Event event = GoogleCalendarUtility.mapTaskToEvent(localTask);
					updateGCalEvent(event);

				} else {
					// update local
					DataManager.getInstance().updateTask(task);
				}

			}

		}
	}

	

	/**
	 * This function updates the Google Calendar Event.
	 * 
	 * @param event
	 *            : Google Calendar Event
	 */
	private void updateGCalEvent(Event event) {
		EventReminder[] reminderOverrides = new EventReminder[] {
				new EventReminder().setMethod("email").setMinutes(24 * 60),
				new EventReminder().setMethod("popup").setMinutes(10) };
		Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
				.setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);

		try {
			service.events().update("primary", event.getId(), event).execute();
		} catch (IOException e) {
			LogManager.getInstance().log(this.getClass().getName(), e.toString());
		}
	}


	/**
	 * This function perform sync from local to server.
	 */
	private void performUpSync() {

		performNewTaskSync();
		performUpdatedTaskSync();
	}

	/**
	 * This function perform syncing to server for locally modified task.
	 */
	private void performUpdatedTaskSync() {
		ArrayList<Task> lists = DataManager.getInstance().getListOfModifiedTask();
		HashMap<Task, Long> lastServerUpdateMap = new HashMap<Task, Long>();

		for (Task task : lists) {

			Event event = GoogleCalendarUtility.mapTaskToEvent(task);

			try {
				event = service.events().update("primary", task.getgCalId(), event).execute();
				lastServerUpdateMap.put(task, event.getUpdated().getValue());
			} catch (IOException e) {
				LogManager.getInstance().log(this.getClass().getName(), e.toString());
			}
		}

		DataManager.getInstance().updateServerUpdateTime(lastServerUpdateMap);

	}

	/**
	 * This function syncs all new task that are yet to be sync for the first time.
	 */
	private void performNewTaskSync() {
		ArrayList<Task> lists = DataManager.getInstance().getListOfUnSyncNonFloatingTasks();
		HashMap<Task, String> hashmap = new HashMap<Task, String>();
		for (Task task : lists) {

			Event event = GoogleCalendarUtility.mapTaskToEvent(task);

			EventReminder[] reminderOverrides = new EventReminder[] {
					new EventReminder().setMethod("email").setMinutes(24 * 60),
					new EventReminder().setMethod("popup").setMinutes(10), };
			Event.Reminders reminders = new Event.Reminders().setUseDefault(false)
					.setOverrides(Arrays.asList(reminderOverrides));
			event.setReminders(reminders); 

			try {
				event = service.events().insert("primary", event).execute();
				hashmap.put(task, event.getId());
			} catch (IOException e) {
				LogManager.getInstance().log(this.getClass().getName(), e.toString());
			}

		}

		DataManager.getInstance().updateGCalId(hashmap);
	}

	

}
