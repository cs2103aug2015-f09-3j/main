package application;

import java.util.ArrayList;

public class Command {

	public static final String ADD_COMMAND = "add:+";
	public static final String LIST_COMMAND = "list:ls";
	public static final String CHANGE_STORAGE_COMMAND = "changeStorage:cs";
	public static final String DELETE_COMMAND = "delete:-";
	public static final String UNDO_COMMAND = "undo:<";
	public static final String EDIT_COMMAND = "edit:et";
	public static final String DONE_COMMAND = "done:ok";
	public static final String UNDONE_COMMAND = "notdone:notok";

	public static final int ADD_COMMAND_TYPE = 1;
	public static final int LIST_COMMAND_TYPE = 2;
	public static final int CHANGE_STORAGE_COMMAND_TYPE = 3;
	public static final int DELETE_COMMAND_TYPE = 4;
	public static final int UNDO_COMMAND_TYPE = 5;
	public static final int EDIT_COMMAND_TYPE = 6;
	public static final int DONE_COMMAND_TYPE = 7;
	public static final int UNDONE_COMMAND_TYPE = 8;

	private Integer type;
	private String textContent;
	private ArrayList<Parameter> parameters;
	

	public Command(Integer type, String textContent, ArrayList<Parameter> parameter) {
		super();
		this.type = type;
		this.textContent = textContent;
		this.parameters = parameter;
	}

	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public ArrayList<Parameter> getParameter() {
		return parameters;
	}
	public void setParameter(ArrayList<Parameter> parameter) {
		this.parameters = parameter;
	}



}
