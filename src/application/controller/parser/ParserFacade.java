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

	// From 100 onward, it is normal parsing, user type as expected.
	public static final String DATE_FORMAT_TYPE_100 = "dd/MM/yy HH:mm";
	public static final String DATE_FORMAT_TYPE_101 = "dd/MM/yy hh:mma";
	public static final String DATE_FORMAT_TYPE_102 = "dd.MM.yy HH:mm";
	public static final String DATE_FORMAT_TYPE_103 = "dd.MM.yy hh:mma";
	public static final String DATE_FORMAT_TYPE_104 = "dd/MM/yy ha";
	public static final String DATE_FORMAT_TYPE_105 = "dd.MM.yy ha";

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
