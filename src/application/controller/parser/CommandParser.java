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
 * @author youlianglim
 *
 */
public class CommandParser {

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

	public ArrayList<Command> parseCommand(String command) throws InvalidCommandException {
		// Need to parse add, see all, change, delete, undo, edit, done,
		// prioritise command

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
			} else if (command.charAt(i) == '\\') {
				indexOfFirstInvertedSlash = i;
				break;
			}
		}

		indexOfFirstSpace = setIndexOfFirstSpaceForCommandWithoutParameter(command, indexOfFirstSpace);

		if (indexOfFirstSpace == 0) {
			return null;
		}

		cmd = command.substring(0, indexOfFirstSpace);
		int cmdType = mapCommandType(cmd);

		if (cmdType == -1) {
			throw new InvalidCommandException(command);
		}

		if (!command.contains("\\|")) {
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
	
	private String trimOffDateIfAny(String str){
		
		String[] strArr = str.split(" @ | @|@ ");
		return strArr[0].trim();

		
	}

	/**
	 * @param command
	 * @param text
	 * @param parameters
	 * @param indexOfFirstSpace
	 * @param indexOfFirstInvertedSlash
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
			splitedText = text.split("\\|");
		}
		return splitedText;
	}

	/**
	 * @param command
	 * @param parameters
	 */
	private void performSmartParsing(String command, ArrayList<Parameter> parameters) {
		if (command.contains("@")) {

			String[] strArr = command.split(" @ | @|@ ");
			// use the first at, which the content is at [1]
			// try to find @ if there is any
			String[] strArr2;
			if (strArr.length > 1) {
				strArr2 = strArr[1].split("\\\\");		
			//	parameters.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));				
				if(ParserFacade.getInstance().containMultiDate(strArr2[0].trim())){
					parameters.add(new Parameter(Parameter.START_END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));
				}else{
					parameters.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));
				}
			} 
			
			
			
			// Find if at or @ appear first
			
			/*
			int indexAt = command.toUpperCase().indexOf("AT");
			int indexAnd = command.lastIndexOf("@");
			
			
			
			
			boolean atOnly = false, andOnly = false;

			if (indexAt == -1) {
				indexAt = Integer.MAX_VALUE;
				andOnly = true;
			}
			if (indexAnd == -1) {
				indexAnd = Integer.MAX_VALUE;
				atOnly = true;
			}

			if ((indexAt < indexAnd)) {

				String[] strArr = command.split("(?i)at");
				// use the first at, which the content is at [1]
				// try to find @ if there is any
				String[] strArr2;
				if (strArr.length > 1) {
					if (atOnly) {
						strArr2 = strArr[1].split("\\\\");
					} else {
						strArr2 = strArr[1].split(" @ | @|@ ");
					}

					parameters.add(new Parameter(Parameter.PLACE_ARGUMENT_TYPE, strArr2[0].trim()));
				} else {
					strArr2 = strArr[0].split(" @ | @|@ ");
				}
				String[] strArr3;
				// task location is at strArr2[0], time is at strArr2[1]

				if (strArr2.length > 1) {
					strArr3 = strArr2[1].split("\\\\");
					if(ParserFacade.getInstance().containMultiDate(strArr3[0].trim())){
						parameters.add(new Parameter(Parameter.START_END_DATE_ARGUMENT_TYPE, strArr3[0].trim()));
					}else{
						parameters.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, strArr3[0].trim()));
					}
				}
			} else {
				// @ appear first then at
				String[] strArr = command.split(" @ | @|@ ");
				// use the first at, which the content is at [1]
				// try to find @ if there is any
				String[] strArr2;
				if (strArr.length > 1) {
					strArr2 = strArr[1].split("\\\\");	
					parameters.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, strArr2[0].trim()));
				} 
				
			}
			
			*/
		}
	}

	/**
	 * @param command
	 * @param indexOfFirstSpace
	 * @return
	 */
	private int setIndexOfFirstSpaceForCommandWithoutParameter(String command, int indexOfFirstSpace) {
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

		if (isCommandType(Command.UNDO_COMMAND, command.trim())) {
			indexOfFirstSpace = command.length();
		}
		return indexOfFirstSpace;
	}

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

			}else if (para.getParaType() == Parameter.END_DATE_ARGUMENT_TYPE) {

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

}
