package application.controller;
//@@author LimYouLiang A0125975U
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import application.controller.parser.ParserFacade;
import wiremock.org.json.JSONException;
import wiremock.org.json.JSONObject;

public class LogManager {

	private static LogManager instance;
	private static final String LOG_FILE_NAME = "log.txt";
	private static File file;
	private int count = 1;

	public static LogManager getInstance() {

		if (instance == null) {
			instance = new LogManager();
		}

		return instance;

	}

	private LogManager() {
		
		file = new File(LOG_FILE_NAME);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				//Change file name
				file = new File(LOG_FILE_NAME + count++);
			}
		}
	}

	public void log(String tag, String msg) {
		
		try {
			SimpleDateFormat df = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_7);
			JSONObject jObj = new JSONObject();
			jObj.put("Tag", tag);
			jObj.put("Msg", msg);
			jObj.put("Time", df.format(System.currentTimeMillis()));
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bw = new BufferedWriter(fileWritter);
			bw.write(jObj.toString());
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			//Change file name
			file = new File(LOG_FILE_NAME + count++);
			
		} catch (JSONException e) {
			assert false;
		}
	}

}
