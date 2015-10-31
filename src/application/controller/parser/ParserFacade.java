package application.controller.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.exception.InvalidCommandException;
import application.model.Command;
import application.model.Task;

public class ParserFacade {

	private static ParserFacade instance;

	// From 1 to 99, after and on yyyy are all virtual, user does not type them.
	// It is used to facilitate parsing.
	public static final String DATE_FORMAT_TYPE_1 = "dd/MM hha yyyy";
	public static final String DATE_FORMAT_TYPE_2 = "dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_3 = "dd/MM hh:mma yyyy";
	public static final String DATE_FORMAT_TYPE_4 = "dd/MM HH:mm yyyy";
	public static final String DATE_FORMAT_TYPE_5 = "hha dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_6 = "hh:mma dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_7 = "HH:mm dd/MM yyyy";
	


	// From 100 onward, it is normal parsing, user type as expected.
	public static final String DATE_FORMAT_TYPE_100 = "dd/MM/yy HH:mm";
	public static final String DATE_FORMAT_TYPE_101 = "dd/MM/yy hh:mma";
	public static final String DATE_FORMAT_TYPE_102 = "dd.MM.yy HH:mm";
	public static final String DATE_FORMAT_TYPE_103 = "dd.MM.yy hh:mma";
	public static final String DATE_FORMAT_TYPE_104 = "dd/MM/yy hha";
	public static final String DATE_FORMAT_TYPE_105 = "dd.MM.yy hha";
	
	public static final String DATE_FORMAT_TYPE_106 = "HH:mm dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_107 = "hh:mma dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_108 = "HH:mm dd.MM.yy";
	public static final String DATE_FORMAT_TYPE_109 = "hh:mma dd.MM.yy";
	public static final String DATE_FORMAT_TYPE_110 = "hha dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_111 = "hha dd.MM.yy";

	private ParserFacade() { 

	}

	public static ParserFacade getInstance() {
		if (instance == null) {
			instance = new ParserFacade();
		}
		return instance;
	}
	
	public Date parseDate(String dateStr) {
		return DateParser.getInstance().parseDate(dateStr);
	}
	
	public List<Date> parseMultiDate(String dateStr) {
		return DateParser.getInstance().parseMultipleDate(dateStr);
	}
	
	public boolean containMultiDate(String dateStr){
		return DateParser.getInstance().containMultipleDate(dateStr);
	}
	
	public ArrayList<Command> parseCommand(String command) throws InvalidCommandException {
		return CommandParser.getInstance().parseCommand(command);
	}
	
	public Task convertAddCommandtoTask(Command cmd) {
		return CommandParser.getInstance().convertAddCommandtoTask(cmd);
	}
	

}
