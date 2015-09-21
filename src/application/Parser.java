package application;
/**
 * This is a singleton class
 * @author youlianglim
 * 
 */
public class Parser {
	
	
	private static Parser instance;
	
	public static Parser getInstance(){
		if(instance == null){
			instance = new Parser();
			
		}		
		return instance;
	}
	
	public Command parseCommand(String command){
		//Need to parse add, see all, change, delete, undo, edit, done, prioritise command
		
		
		return null;
	}

	
	
	

}
