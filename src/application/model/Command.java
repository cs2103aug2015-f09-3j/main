package application.model;

// @@LimYouLiang A0125975U
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
	public static final String SEARCH_COMMAND = "search:s";
	public static final String LIST_TODAY_COMMAND = "today:now";
	public static final String LIST_NEXT_COMMAND = "next:nxt";
	public static final String HELP_COMMAND = "help:?";
	public static final String SCHEDULE_COMMAND = "schedule:sd";
	public static final String GOOGLE_ADD_COMMAND = "googleAdd:ga";

	public static final int ADD_COMMAND_TYPE = 1;
	public static final int LIST_COMMAND_TYPE = 2;
	public static final int CHANGE_STORAGE_COMMAND_TYPE = 3;
	public static final int DELETE_COMMAND_TYPE = 4;
	public static final int UNDO_COMMAND_TYPE = 5;
	public static final int EDIT_COMMAND_TYPE = 6;
	public static final int DONE_COMMAND_TYPE = 7;
	public static final int UNDONE_COMMAND_TYPE = 8;
	public static final int SEARCH_COMMAND_TYPE = 9;
	public static final int LIST_TODAY_COMMAND_TYPE = 10;
	public static final int LIST_NEXT_COMMAND_TYPE = 11;
	public static final int HELP_COMMAND_TYPE = 12;
	public static final int SCHEDULE_COMMAND_TYPE = 13;
	public static final int GOOGLE_ADD_COMMAND_TYPE = 14;

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

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Command) {
			Command cmd = (Command) obj;
			if (this.parameters.containsAll(cmd.getParameter()) && cmd.getParameter().containsAll(this.parameters)
					&& this.textContent.equals(cmd.getTextContent()) && this.type == cmd.type) {
				return true;
			} else {
				return false;
			}
		}

		return super.equals(obj);
	}

}
