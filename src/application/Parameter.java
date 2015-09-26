package application;

public class Parameter {
	
	public static final String PRIORITY_ARGUMENT = "p";
	
	public static final Integer PRIORITY_ARGUMENT_TYPE = 1;
	
	private Integer type;
	private String argument;
	
	public Parameter(Integer type, String argument){
		this.type = type;
		this.argument = argument;
	}
	
	public String getParaArg(){
		return argument;
	}
	
	public Integer getParaType(){
		return type;
	}
	

}
