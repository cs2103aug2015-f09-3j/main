package application.exception;

//@@author  A0125975U
@SuppressWarnings("serial")
public class InvalidCommandException extends Exception {

	String msg = "";

	public InvalidCommandException(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		
		return "Invalid command : \"" + msg + "\"";
	}

}
