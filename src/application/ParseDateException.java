package application;

@SuppressWarnings("serial")
public class ParseDateException extends Exception {

	String date;
	
	public ParseDateException(String date){
		this.date = date;
	}
	
	
	@Override
	public String getMessage() {
		return "Unable to parse \"" + date + "\"";
	}

}
