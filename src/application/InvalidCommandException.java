package application;

@SuppressWarnings("serial")
public class InvalidCommandException extends Exception {

	String msg = "";
	
	public InvalidCommandException(String msg){
		this.msg = msg;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "Invalid command : \"" + msg + "\"";
	}


}
