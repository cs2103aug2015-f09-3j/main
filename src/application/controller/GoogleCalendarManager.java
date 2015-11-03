package application.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import application.model.Task;

public class GoogleCalendarManager {

	private static GoogleCalendarManager instance;

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
	private static final List<String> SCOPES = Arrays.asList(new String[]{CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR});

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
	public Credential authorize() throws IOException {
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
	public com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
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
	 * 
	 */
	public List<Event> getCalendarEvents() { 
		try {

			// List the next 10 events from the primary calendar.
			DateTime now = new DateTime(System.currentTimeMillis());
			Events events = service.events().list("primary").setMaxResults(10).setTimeMin(now).setOrderBy("startTime")
					.setSingleEvents(true).execute();
			List<Event> items = events.getItems();
			if (items.size() == 0) {
				System.out.println("No upcoming events found.");
			} else {
				System.out.println("Upcoming events");
				for (Event event : items) {
					DateTime start = event.getStart().getDateTime();
					if (start == null) {
						start = event.getStart().getDate();
					}
					System.out.printf("%s (%s)\n", event.getSummary(), start);
				}
			}

			return items;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void performSync(){
		
		performUpSync();
		//TODO: performDownSync
		List<Event> lists = getCalendarEvents();
		
		
		
		
		
		
		
		
	}

	/**
	 * 
	 */
	private void performUpSync() {
		ArrayList<Task> lists = DataManager.getInstance().getListOfTasksToUpload();
		HashMap<Task, String> hashmap = new HashMap<Task, String>();
		for(Task task : lists){
			
			Event event = mapTaskToEvent(task);
			
			EventReminder[] reminderOverrides = new EventReminder[] {
				    new EventReminder().setMethod("email").setMinutes(24 * 60),
				    new EventReminder().setMethod("popup").setMinutes(10),
				};
				Event.Reminders reminders = new Event.Reminders()
				    .setUseDefault(false)
				    .setOverrides(Arrays.asList(reminderOverrides));
				event.setReminders(reminders);
				

				try {
					event = service.events().insert("primary", event).execute();
					hashmap.put(task, event.getId());
					//task.setgCalId(event.getId());
					
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
		Event event = new Event()
				.setSummary(task.getTextContent())
				.setLocation(task.getPlace_argument())
				.setDescription(task.getPriority_argument())
				.setKind(task.getType_argument())
				;
				
				

		EventDateTime start = new EventDateTime();
		if(task.getStart_date() != null){
		start.setDateTime(new DateTime(task.getStart_date()));
		}
				
		EventDateTime end = new EventDateTime();
		if(task.getEnd_date() != null){
			end.setDateTime(new DateTime(task.getEnd_date()));
			
		}

		EventDateTime endMinus1Hr = new EventDateTime();
		endMinus1Hr.setDateTime(new DateTime(new Date(task.getEnd_date().getTime() - 3600000)));
		
		//event.setStart(start);
		//event.setEnd(end);
		
		if(task.getStart_date() == null){ 
			//event.setEndTimeUnspecified(true);
			event.setStart(endMinus1Hr);
			event.setEnd(end);
			event.set("has_start_date", false);
			
		}else{
			
			event.setStart(start);
			event.setEnd(end);
			event.set("has_start_date", true);
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
