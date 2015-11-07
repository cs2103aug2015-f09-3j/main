package application.controller;

import java.io.IOException;

import com.google.api.services.calendar.model.Event;

import application.model.Task;
import application.utils.GoogleCalendarUtility;

public class GoogleCalendarManagerInterface {
	
	private static GoogleCalendarManagerInterface instance;
	
	// @@Google
	com.google.api.services.calendar.Calendar service;
	
	private GoogleCalendarManagerInterface(){
		service = GoogleCalendarManager.getInstance().getService();
	}
	
	public static GoogleCalendarManagerInterface getInstance(){
		if(instance == null){
			instance = new GoogleCalendarManagerInterface();
		}
		
		return instance;
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
			Task task = GoogleCalendarUtility.mapEventToTask(createdEvent);
			int googleAddSuccess = DataManager.getInstance().addNewTask(task);
			return googleAddSuccess;
		} catch (IOException e) {
			LogManager.getInstance().log(this.getClass().getName(), e.toString());
		}
		return null;
	}
	
	/**
	 * remove events from Gooogle Calendar by eventId
	 * 
	 * @param eventId
	 *            : Google Calendar Event Id
	 */
	public void removeTaskFromServer(String eventId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					service.events().delete("primary", eventId).execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	} 


}
