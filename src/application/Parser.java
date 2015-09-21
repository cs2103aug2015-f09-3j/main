package application;

import java.util.ArrayList;

/**
 * This is a singleton class
 * 
 * @author youlianglim
 * 
 */
public class Parser {

	private static Parser instance;

	public static Parser getInstance() {
		if (instance == null) {
			instance = new Parser();

		}
		return instance;
	}

	public Command parseCommand(String command) {
		// Need to parse add, see all, change, delete, undo, edit, done,
		// prioritise command

		String cmd, text;
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();

		int indexOfFirstSpace = 0;
		int indexOfFirstInvertedSlash = 0;
		boolean passFirstSpace = false;

		for (int i = 0; i < command.length(); i++) {
			if (command.charAt(i) == ' ' && !passFirstSpace) {
				indexOfFirstSpace = i;
				passFirstSpace = true;
			} else if (command.charAt(i) == '\\') {
				indexOfFirstInvertedSlash = i;
				break;
			}
		}

		if (indexOfFirstSpace == 0) {
			return null;
		}

		cmd = command.substring(0, indexOfFirstSpace);
		if (indexOfFirstInvertedSlash > 0 && indexOfFirstInvertedSlash > indexOfFirstSpace) {
			text = command.substring(indexOfFirstSpace + 1, indexOfFirstInvertedSlash);
			String parameterStr = command.substring(indexOfFirstInvertedSlash, command.length());
			String[] parameterArr = parameterStr.split(" ");
			for (int i = 0; i < parameterArr.length; i++) {
				if (parameterArr[i].charAt(0) == '\\') {
					String para = parameterArr[i].substring(1, parameterArr[i].length());
					parameters.add(new Parameter(mapParameterType(para)));
				}
			}
		} else {
			text = command.substring(indexOfFirstSpace + 1, command.length());
		}

		int cmdType = mapCommandType(cmd);

		return new Command(cmdType, text, parameters);
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

		default:
			return -1;

		}

	}

}
