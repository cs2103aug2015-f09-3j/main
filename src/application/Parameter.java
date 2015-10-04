package application;

public class Parameter {

	public static final String PRIORITY_ARGUMENT = "p";
	public static final String TYPE_ARGUMENT = "t";
	public static final String DATE_ARGUMENT = "date";
	public static final String TIME_ARGUMENT = "time";
	public static final String PLACE_ARGUMENT = "place";

	public static final int PRIORITY_ARGUMENT_TYPE = 1;
	public static final int TYPE_ARGUMENT_TYPE = 2;
	public static final int DATE_ARGUMENT_TYPE = 3;
	public static final int TIME_ARGUMENT_TYPE = 4;
	public static final int PLACE_ARGUMENT_TYPE = 5;

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
