package application.controller.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.exception.InvalidCommandException;
import application.model.Command;
import application.model.Parameter;
import application.model.Task;

/**
 * This is a singleton class
 *
 * @@LimYouLiang A0125975U
 *
 */
public class CommandParser {

	private static final int ERROR_COMMAND_TYPE = -1;
	private static final String REGEX_ATSIGN = " @ | @|@ ";
	private static final String VERTICAL_DASH = "\\|";
	private static final char INVERTED_SLASH = '\\';
	private static CommandParser instance;

	static CommandParser getInstance() {
		if (instance == null) {
			instance = new CommandParser();
		}
		return instance;
	}

	public CommandParser() {
		super();

	}

	/**
	 * This function parse the input command string from the input console.
	 * @param command : string to be parsed.
	 * @return : obj of ArrayList<Command>
	 * @throws InvalidCommandException : when command is invalid.
	 */
	public ArrayList<Command> parseCommand(String command) throws InvalidCommandException {


		ArrayList<Command> cmds = new ArrayList<Command>();
		String cmd;
		String[] text;

		ArrayList<Parameter> parameters = new ArrayList<Parameter>();

		int indexOfFirstSpace = 0;
		int indexOfFirstInvertedSlash = 0;
		boolean passFirstSpace = false;

		// check and find the first space and slash.
		for (int i = 0; i < command.length(); i++) {
			if (command.charAt(i) == ' ' && !passFirstSpace) {
				indexOfFirstSpace = i;
				passFirstSpace = true;
			} else if (command.charAt(i) == INVERTED_SLASH) {
				indexOfFirstInvertedSlash = i;
				break;
			}
		}

		indexOfFirstSpace = getEquivalentIndexForCommandWithoutParameter(command, indexOfFirstSpace);

		if (indexOfFirstSpace == 0) {
			return null;
		}

		cmd = command.substring(0, indexOfFirstSpace);
		int cmdType = mapCommandType(cmd);

		if (cmdType == ERROR_COMMAND_TYPE) {
			throw new InvalidCommandException(command);
		}

		if (!command.contains(VERTICAL_DASH)) {
			performSmartParsing(command, parameters);
		}

		text = extractTextAndPerformParameterParsing(command, parameters, indexOfFirstSpace, indexOfFirstInvertedSlash);
		if (text != null) {
			for (String textEntry : text) {
				cmds.add(new Command(cmdType, trimOffDateIfAny(textEntry), parameters));

			}
		} else {
			cmds.add(new Command(cmdType, "", parameters));
		}

		return cmds;
	}

	private String trimOffDateIfAny(String str) {

		String[] strArr = str.split(REGEX_ATSIGN);
		return strArr[0].trim();

	}

	/**
	 * 
	 * This function extracts the command main text and extract its paramter.
	 * @param command : the raw command string.
	 * @param parameters : the lists to store the parsed results
	 * @param indexOfFirstSpace : index of the first space of the command.
	 * @param indexOfFirstInvertedSlash : index of the first inverted slash of the command.
	 * @return
	 */
	private String[] extractTextAndPerformParameterParsing(String command, ArrayList<Parameter> parameters,
			int indexOfFirstSpace, int indexOfFirstInvertedSlash) {
		String splitedText[] = null;
		String text;
		if (indexOfFirstSpace < command.length()) {
			if (isValidCommandWithParameter(indexOfFirstSpace, indexOfFirstInvertedSlash)) {
				text = command.substring(indexOfFirstSpace + 1, indexOfFirstInvertedSlash);

				String parameterStr = command.substring(indexOfFirstInvertedSlash, command.length());
				String[] parameterArr = parameterStr.split(" ");
				extractParameter(parameters, parameterArr);
			} else {

				text = command.substring(indexOfFirstSpace + 1, command.length());
			}
			// For multiple add and done
			splitedText = text.split(VERTICAL_DASH);
		}
		return splitedText;
	}

	/**
	 * This function performs smart parsing syntax. 
	 * @param command : the raw command string.
	 * @param parameters : the list to store the parsed results.
	 */
	private void performSmartParsing(String command, ArrayList<Parameter> parameters) {
		if (command.contains("@")) {

			String[] strArr = command.split(REGEX_ATSIGN);
			// use the first at, which the content is at [1]
			// try to find @ if there is any
			String[] strArr2;
			if (strArr.length > 1) {
				strArr2 = strArr[1].split("\\\\");
				// parameters.add(new
				// Parameter(Parameter.END_DATE_ARGUMENT_TYPE,
				// strArr2[0].trim()));
				if (ParserFacade.getInstance().containMultiDate(strArr2[0].trim())) {
					parameters.add(new Parameter(Parameter.START_END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));
				} else {
					parameters.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));
				}
			}

		}
	}

	/**
	 * This function finds the equivalent index of the first space for command without paramters(which is the space after the command).
	 * @param command : Raw command string.
	 * @return the equivalent index of the first space.
	 */
	private int getEquivalentIndexForCommandWithoutParameter(String command, int indexOfFirstSpace) {
		
		// check if is a list command with no parameter. e.g list
		if (isCommandType(Command.LIST_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (isCommandType(Command.LIST_NEXT_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (isCommandType(Command.LIST_TODAY_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (isCommandType(Command.HELP_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (isCommandType(Command.SCHEDULE_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}

		if (isCommandType(Command.UNDO_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}
		return indexOfFirstSpace;
	}

	
	/**
	 * This function convert the an add command into Task.
	 * @param cmd
	 * @return
	 */
	public Task convertAddCommandtoTask(Command cmd) {

		assert cmd != null;
		Task task = new Task(cmd.getTextContent());
		ArrayList<Parameter> lists = cmd.getParameter();

		for (Parameter para : lists) {
			if (para.getParaType() == Parameter.START_DATE_ARGUMENT_TYPE) {

				task.setStart_date(DateParser.getInstance().parseDate(para.getParaArg()));

			} else if (para.getParaType() == Parameter.START_END_DATE_ARGUMENT_TYPE) {

				List<Date> listsOfDate = DateParser.getInstance().parseMultipleDate(para.getParaArg());
				task.setStart_date(listsOfDate.get(0));
				task.setEnd_date(listsOfDate.get(1));

			} else if (para.getParaType() == Parameter.END_DATE_ARGUMENT_TYPE) {

				task.setEnd_date(DateParser.getInstance().parseDate(para.getParaArg()));

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

	/**
	 * 
	 * @param indexOfFirstSpace
	 * @param indexOfFirstInvertedSlash
	 * @return True if the command is with parameter, else false.
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
			if (parameterArr[i].charAt(0) == INVERTED_SLASH) {

				// format.
				String para = parameterArr[i].substring(1, parameterArr[i].length());
				// If the next parameter charAt(0) is not \\, then it going to
				// concat together
				// with this paragraph

				String paraContent = "";

				for (int c = i + 1; c < parameterArr.length; c++) {
					if (parameterArr[c].charAt(0) != INVERTED_SLASH) {
						paraContent += parameterArr[c].substring(0, parameterArr[c].length()) + " ";
					} else {
						break;
					}
				}
				int paraType = mapParameterType(para);
				if (paraType != ERROR_COMMAND_TYPE) {
					parameters.add(new Parameter(paraType, paraContent.trim()));
				}
			}
		}
	}

	/**
	 * Map the command and finds its form.
	 * @param cmd
	 * @return the type of command in integer form.
	 */
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
		} else if (isCommandType(Command.SEARCH_COMMAND, cmd)) {
			return Command.SEARCH_COMMAND_TYPE;
		} else if (isCommandType(Command.LIST_TODAY_COMMAND, cmd)) {
			return Command.LIST_TODAY_COMMAND_TYPE;
		} else if (isCommandType(Command.LIST_NEXT_COMMAND, cmd)) {
			return Command.LIST_NEXT_COMMAND_TYPE;
		} else if (isCommandType(Command.HELP_COMMAND, cmd)) {
			return Command.HELP_COMMAND_TYPE;
		} else if (isCommandType(Command.SCHEDULE_COMMAND, cmd)) {
			return Command.SCHEDULE_COMMAND_TYPE;
		} else if (isCommandType(Command.GOOGLE_ADD_COMMAND, cmd)) {
				return Command.GOOGLE_ADD_COMMAND_TYPE;
		} else {
			return ERROR_COMMAND_TYPE;
		}

	}

	/**
	 * 
	 * @param cmdDefinition : The definition of the command. For e.g "add:+" 
	 * @param cmd : the command to check. For E.g. add
	 * @return true if matched, otherwise false.
	 */
	private boolean isCommandType(String cmdDefinition, String cmd) {

		String[] cmdDef = cmdDefinition.split(":");
		for (int i = 0; i < cmdDef.length; i++) {
			if (cmdDef[i].equals(cmd)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * This functions maps the parameters with its type.
	 * @param parameterStr
	 * @return its type in integer form.
	 */
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
			return ERROR_COMMAND_TYPE;

		}

	}

}
