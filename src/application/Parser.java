package application;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This is a singleton class
 *
 * @author youlianglim
 *
 */
public class Parser {

	private static Parser instance;

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

	

	ArrayList<String> listsOfDateFormat;

	public static Parser getInstance() {
		if (instance == null) {
			instance = new Parser();

		}
		return instance;
	}

	public Parser() {
		super();
		listsOfDateFormat = new ArrayList<String>();
		listsOfDateFormat.add(DATE_FORMAT_TYPE_1);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_2);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_3);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_4);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_5);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_6);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_7);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_8);

		listsOfDateFormat.add(DATE_FORMAT_TYPE_100);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_101);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_102);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_103);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_104);
		listsOfDateFormat.add(DATE_FORMAT_TYPE_105);
	}

	public Command parseCommand(String command) throws InvalidCommandException {
		// Need to parse add, see all, change, delete, undo, edit, done,
		// prioritise command

		String cmd, text = "";

		ArrayList<Parameter> parameters = new ArrayList<Parameter>();

		int indexOfFirstSpace = 0;
		int indexOfFirstInvertedSlash = 0;
		boolean passFirstSpace = false;

		// check and find the first space and slash.
		for (int i = 0; i < command.length(); i++) {
			if (command.charAt(i) == ' ' && !passFirstSpace) {
				indexOfFirstSpace = i;
				passFirstSpace = true;
			} else if (command.charAt(i) == '\\') {
				indexOfFirstInvertedSlash = i;
				break;
			}
		}

		// check if is a list command with no parameter. e.g list
		if (isCommandType(Command.LIST_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (indexOfFirstSpace == 0) {
			return null;
		}

		cmd = command.substring(0, indexOfFirstSpace);
		int cmdType = mapCommandType(cmd);

		if (cmdType == -1) {
			throw new InvalidCommandException(command);
		}

		if (indexOfFirstSpace < command.length()) {
			if (isValidCommandWithParameter(indexOfFirstSpace, indexOfFirstInvertedSlash)) {
				text = command.substring(indexOfFirstSpace + 1, indexOfFirstInvertedSlash);
				String parameterStr = command.substring(indexOfFirstInvertedSlash, command.length());
				String[] parameterArr = parameterStr.split(" ");
				extractParameter(parameters, parameterArr);
			} else {

				text = command.substring(indexOfFirstSpace + 1, command.length());
			}
		}
		return new Command(cmdType, text.trim(), parameters);
	}

	/**
	 * @param indexOfFirstSpace
	 * @param indexOfFirstInvertedSlash
	 * @return
	 */
	private boolean isValidCommandWithParameter(int indexOfFirstSpace, int indexOfFirstInvertedSlash) {
		return indexOfFirstInvertedSlash > 0 && indexOfFirstInvertedSlash > indexOfFirstSpace;
	}

	/**
	 * This function will convert raw parameter and add it into arraylist of
	 * parameter.
	 * 
	 * @param parameters
	 *            : arraylist to store extracted parameter
	 * @param parameterArr
	 *            : raw parameter string to convert into parameter instance.
	 */
	private void extractParameter(ArrayList<Parameter> parameters, String[] parameterArr) {
		for (int i = 0; i < parameterArr.length; i++) {
			if (parameterArr[i].charAt(0) == '\\') {
				// TODO : Check whether if the arguments is in correct
				// format.
				String para = parameterArr[i].substring(1, parameterArr[i].length());
				// If the next parameter charAt(0) is not \\, then it going to
				// concat together
				// with this paragraph

				String paraContent = "";

				for (int c = i + 1; c < parameterArr.length; c++) {
					if (parameterArr[c].charAt(0) != '\\') {
						paraContent += parameterArr[c].substring(0, parameterArr[c].length()) + " ";
					} else {
						break;
					}

				}

				int paraType = mapParameterType(para);
				if (paraType != -1) {
					parameters.add(new Parameter(paraType, paraContent.trim()));
				}
			}
		}
	}

	public Date parseDate(String dateStr) {

		DateFormat df1;
		Date date = null;
		int count = 1;

		for (String type : this.listsOfDateFormat) {

			df1 = new SimpleDateFormat(type);
			df1.setLenient(false);
			
			String tmpDate = "";
			if (count <= 8) {
				tmpDate = dateStr + " " + Calendar.getInstance().get(Calendar.YEAR);
			}else{
				tmpDate = dateStr;
			}
			
			if (count == 2 || count == 4 || count == 8) {
				tmpDate += " " + (Calendar.getInstance().get(Calendar.MONTH) + 1) + " "
						+ Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);

			}

			// 3 is the max char difference allowance.
			if (tmpDate.length() <= type.length() + 3) {
				try {

					System.out.println("count :" + count + "\nmsg:" + tmpDate);

					date = df1.parse(tmpDate);

					break;
				} catch (ParseException e) {
					// continue parsing
				}
			}
			count++;
			
		}
		return date;
	}

	private Integer mapCommandType(String cmd) {

		if (isCommandType(Command.ADD_COMMAND, cmd)) {
			return Command.ADD_COMMAND_TYPE;
		} else if (isCommandType(Command.CHANGE_STORAGE_COMMAND, cmd)) {
			return Command.CHANGE_STORAGE_COMMAND_TYPE;
		} else if (isCommandType(Command.DELETE_COMMAND, cmd)) {
			return Command.DELETE_COMMAND_TYPE;
		} else if (isCommandType(Command.DONE_COMMAND, cmd)) {
			return Command.DONE_COMMAND_TYPE;
		} else if (isCommandType(Command.EDIT_COMMAND, cmd)) {
			return Command.EDIT_COMMAND_TYPE;
		} else if (isCommandType(Command.LIST_COMMAND, cmd)) {
			return Command.LIST_COMMAND_TYPE;
		} else if (isCommandType(Command.UNDO_COMMAND, cmd)) {
			return Command.UNDO_COMMAND_TYPE;
		} else {
			return -1;
		}

	}

	private boolean isCommandType(String cmdDefinition, String cmd) {

		String[] cmdDef = cmdDefinition.split(":");
		for (int i = 0; i < cmdDef.length; i++) {
			if (cmdDef[i].equals(cmd)) {
				return true;
			}
		}
		return false;

	}

	private Integer mapParameterType(String parameterStr) {

		switch (parameterStr) {

		case Parameter.PRIORITY_ARGUMENT:
			return Parameter.PRIORITY_ARGUMENT_TYPE;

		case Parameter.START_DATE_ARGUMENT:
			return Parameter.START_DATE_ARGUMENT_TYPE;

		case Parameter.PLACE_ARGUMENT:
			return Parameter.PLACE_ARGUMENT_TYPE;
		case Parameter.END_DATE_ARGUMENT:
			return Parameter.END_DATE_ARGUMENT_TYPE;
		case Parameter.TYPE_ARGUMENT:
			return Parameter.TYPE_ARGUMENT_TYPE;

		default:
			return -1;

		}

	}

	public Task convertAddCommandtoTask(Command cmd) {

		Task task = new Task(cmd.getTextContent());

		ArrayList<Parameter> lists = cmd.getParameter();

		DateFormat df1 = new SimpleDateFormat(Parser.DATE_FORMAT_TYPE_1);

		for (Parameter para : lists) {
			if (para.getParaType() == Parameter.START_DATE_ARGUMENT_TYPE) {

				task.setStart_date(parseDate(para.getParaArg()));

			} else if (para.getParaType() == Parameter.END_DATE_ARGUMENT_TYPE) {

				task.setEnd_date(parseDate(para.getParaArg()));

			} else if (para.getParaType() == Parameter.PLACE_ARGUMENT_TYPE) {
				task.setPlace_argument(para.getParaArg());
			} else if (para.getParaType() == Parameter.PRIORITY_ARGUMENT_TYPE) {
				task.setPriority_argument(para.getParaArg());
			} else if (para.getParaType() == Parameter.TYPE_ARGUMENT_TYPE) {
				task.setType_argument(para.getParaArg());
			}
		}

		return task;

	}

}
