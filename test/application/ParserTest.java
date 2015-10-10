package application;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Test;

public class ParserTest {

	@Test
	public void testParseCommand1() {
		
		Command cmd = Parser.getInstance().parseCommand("add buy milk for mom \\p high \\t personal \\sdate 15/1/2015 13:00pm \\edate 13/2/2015 12:00pm \\place clementi ntuc");
		ArrayList<Parameter> paras = new ArrayList<Parameter>();
		paras.add(new Parameter(Parameter.START_DATE_ARGUMENT_TYPE, "15/1/2015 13:00pm"));
		paras.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, "13/2/2015 12:00pm"));
		paras.add(new Parameter(Parameter.PLACE_ARGUMENT_TYPE, "clementi ntuc"));
		paras.add(new Parameter(Parameter.PRIORITY_ARGUMENT_TYPE, "high"));
		paras.add(new Parameter(Parameter.TYPE_ARGUMENT_TYPE, "personal"));
		Command cmdCmp = new Command(Command.ADD_COMMAND_TYPE, "buy milk for mom", paras);
		
		assertEquals(cmd, cmdCmp);
		
	}
	
	@Test
	public void testParseCommand2() {
		
		Command cmd = Parser.getInstance().parseCommand("list");
		ArrayList<Parameter> paras = new ArrayList<Parameter>();

		Command cmdCmp = new Command(Command.LIST_COMMAND_TYPE, "", paras);
		
		assertEquals(cmd, cmdCmp);
		
	}
	
	@Test
	public void testParseCommand3() {
		
		Command cmd = Parser.getInstance().parseCommand("edit lala world");
		ArrayList<Parameter> paras = new ArrayList<Parameter>();

		Command cmdCmp = new Command(Command.EDIT_COMMAND_TYPE, "lala world", paras);
		
		assertEquals(cmd, cmdCmp);
		
	}


	@Test
	public void testConvertAddCommandtoTask1() {
		
		Command cmd = new Command(Command.ADD_COMMAND_TYPE, "test 123", new ArrayList<Parameter>());

		Task taskCmp = new Task("test 123");

		Task tmpTask;
		tmpTask = Parser.getInstance().convertAddCommandtoTask(cmd);

		assertEquals(tmpTask, taskCmp);

	}

	@Test
	public void testConvertAddCommandtoTask2() throws ParseException {

		DateFormat df1 = new SimpleDateFormat(TasksFormatter.DATE_FORMAT_TYPE_1);
		ArrayList<Parameter> paras = new ArrayList<Parameter>();
		paras.add(new Parameter(Parameter.START_DATE_ARGUMENT_TYPE, "13/12/2015 12:00am"));
		paras.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, "14/12/2015 13:00pm"));
		paras.add(new Parameter(Parameter.PLACE_ARGUMENT_TYPE, "bpp"));
		paras.add(new Parameter(Parameter.PRIORITY_ARGUMENT_TYPE, "high"));
		paras.add(new Parameter(Parameter.TYPE_ARGUMENT_TYPE, "meeting"));

		Command cmd = new Command(Command.ADD_COMMAND_TYPE, "test 123", paras);

		Task taskCmp = new Task("test 123");
		taskCmp.setEnd_date(df1.parse("14/12/2015 13:00pm"));
		taskCmp.setStart_date(df1.parse("13/12/2015 12:00am"));
		taskCmp.setPlace_argument("bpp");
		taskCmp.setPriority_argument("high");
		taskCmp.setType_argument("meeting");
		Task tmpTask;
		tmpTask = Parser.getInstance().convertAddCommandtoTask(cmd);
		assertEquals(tmpTask, taskCmp);

	}

}
