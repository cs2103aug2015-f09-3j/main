package application.controller.parser;

import java.util.Date;

import application.exception.InvalidCommandException;
import application.model.Command;
import application.model.Task;

public class ParserFacade {

	private static ParserFacade instance;

	// From 1 to 99, after and on yyyy are all virtual, user does not type them.
	// It is used to faciliate parsing.
	public static final String DATE_FORMAT_TYPE_1 = "dd/MM ha yyyy";
	public static final String DATE_FORMAT_TYPE_2 = "EEEEE ha yyyy M W";
	public static final String DATE_FORMAT_TYPE_3 = "dd MMM yyyy";
	public static final String DATE_FORMAT_TYPE_4 = "EEEEE yyyy M W";
	public static final String DATE_FORMAT_TYPE_5 = "dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_6 = "dd/MM hh:mma yyyy";
	public static final String DATE_FORMAT_TYPE_7 = "dd/MM HH:mm yyyy";
	public static final String DATE_FORMAT_TYPE_8 = "EEEEE hh:mma yyyy M W";
	
	
	
	public static final String DATE_FORMAT_TYPE_9 = "ha dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_10 = "ha EEEEE yyyy M W";
	public static final String DATE_FORMAT_TYPE_11 = "yyyy EEEEE M W";
	public static final String DATE_FORMAT_TYPE_12 = "hh:mma dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_13 = "HH:mm dd/MM yyyy";
	public static final String DATE_FORMAT_TYPE_14 = "hh:mma EEEEE yyyy M W";
	
	public static final String DATE_FORMAT_TYPE_15 = "ha yyyy M dd"; 
	public static final String DATE_FORMAT_TYPE_16 = "HH:mm yyyy M dd";
	public static final String DATE_FORMAT_TYPE_17 = "hh:mma yyyy M dd";

	// From 100 onward, it is normal parsing, user type as expected.
	public static final String DATE_FORMAT_TYPE_100 = "dd/MM/yy HH:mm";
	public static final String DATE_FORMAT_TYPE_101 = "dd/MM/yy hh:mma";
	public static final String DATE_FORMAT_TYPE_102 = "dd.MM.yy HH:mm";
	public static final String DATE_FORMAT_TYPE_103 = "dd.MM.yy hh:mma";
	public static final String DATE_FORMAT_TYPE_104 = "dd/MM/yy ha";
	public static final String DATE_FORMAT_TYPE_105 = "dd.MM.yy ha";
	
	public static final String DATE_FORMAT_TYPE_106 = "HH:mm dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_107 = "hh:mma dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_108 = "HH:mm dd.MM.yy";
	public static final String DATE_FORMAT_TYPE_109 = "hh:mma dd.MM.yy";
	public static final String DATE_FORMAT_TYPE_110 = "ha dd/MM/yy";
	public static final String DATE_FORMAT_TYPE_111 = "ha dd.MM.yy";

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
	
	public Command parseCommand(String command) throws InvalidCommandException {
		return CommandParser.getInstance().parseCommand(command);
	}
	
	public Task convertAddCommandtoTask(Command cmd) {
		return CommandParser.getInstance().convertAddCommandtoTask(cmd);
	}
	

}
