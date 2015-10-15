package application;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogManager {
	
	private static LogManager instance;
	
	Logger logger = Logger.getLogger("MyLog");
	FileHandler handler;
	public static LogManager getInstance(){
		
		if(instance == null){
			instance = new LogManager();
		}
		
		return instance;
		
	}
	
	private LogManager(){
		
		try {
			handler = new FileHandler("log.txt");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	        logger.addHandler(handler);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        handler.setFormatter(formatter);  
	}
	
	public void log(String msg){
		logger.info(msg);
	}
	
	
	

}
