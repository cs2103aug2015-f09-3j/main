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
	
	public static final Integer ADD_COMMAND_TYPE = 1;
	public static final Integer LIST_COMMAND_TYPE = 2;
	public static final Integer CHANGE_STORAGE_COMMAND_TYPE = 3;
	public static final Integer DELETE_COMMAND_TYPE = 4;
	public static final Integer UNDO_COMMAND_TYPE = 5;
	public static final Integer EDIT_COMMAND_TYPE = 6;
	public static final Integer DONE_COMMAND_TYPE = 7;
	
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
