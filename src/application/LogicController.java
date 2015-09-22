package application;
/**
 * This is a singleton class
 * 
 * @author youlianglim
 *
 */
public class LogicController {
	
	private static LogicController instance;
	
	public static LogicController getInstance(){
		
		if(instance == null){
			instance = new LogicController();
		}
		return instance;
	}
	
	public String onCommandProcess(String command){
		//Implement your logic here. Call parser etc etc.
		
		Command cmd = Parser.getInstance().parseCommand(command);
		
		return "testing";
	}
	
	

}
