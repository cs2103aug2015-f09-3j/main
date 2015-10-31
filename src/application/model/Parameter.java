package application.model;

public class Parameter {

	public static final String PRIORITY_ARGUMENT = "p";
	public static final String TYPE_ARGUMENT = "t";
	public static final String START_DATE_ARGUMENT = "sdate";
	public static final String END_DATE_ARGUMENT = "edate";
	
	public static final String START_END_DATE_ARGUMENT = "date";

	public static final String PLACE_ARGUMENT = "place";

	public static final int PRIORITY_ARGUMENT_TYPE = 1;
	public static final int TYPE_ARGUMENT_TYPE = 2;
	public static final int START_DATE_ARGUMENT_TYPE = 3;
	public static final int END_DATE_ARGUMENT_TYPE = 4;
	public static final int PLACE_ARGUMENT_TYPE = 5;
	public static final int START_END_DATE_ARGUMENT_TYPE = 6;
	
	private int type;
	private String argument;

	public Parameter(int type, String argument) {
		this.type = type;
		this.argument = argument;
	}

	public String getParaArg() {
		return argument;
	}

	public int getParaType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub

		if (obj instanceof Parameter) {
			Parameter nObj = (Parameter) obj;
			if (nObj.getParaArg().equals(this.argument) && nObj.getParaType() == this.type) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(obj);
		}
	}

}
