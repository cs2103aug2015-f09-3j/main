package application;
//@@author LimYouLiang A0125975U
import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import application.controller.parser.ParserFacade;
import application.controller.parser.UKDateParser;
import application.exception.InvalidCommandException;
import application.model.Command;
import application.model.Parameter;
import application.model.Task;

public class ParserTest {
	


	@Test
	public void testParseCommand1() throws InvalidCommandException {

		Command cmd = ParserFacade.getInstance().parseCommand(
				"add buy milk for mom \\p high \\t personal \\sdate 15/1/2015 13:00pm \\edate 13/2/2015 12:00pm \\place clementi ntuc").get(0);
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
	public void testParseCommand2() throws InvalidCommandException {

		Command cmd = ParserFacade.getInstance().parseCommand("list").get(0);
		ArrayList<Parameter> paras = new ArrayList<Parameter>();

		Command cmdCmp = new Command(Command.LIST_COMMAND_TYPE, "", paras);

		assertEquals(cmd, cmdCmp);

	}

	@Test
	public void testParseCommand3() throws InvalidCommandException { 

		Command cmd = ParserFacade.getInstance().parseCommand("edit lala world").get(0);
		ArrayList<Parameter> paras = new ArrayList<Parameter>();

		Command cmdCmp = new Command(Command.EDIT_COMMAND_TYPE, "lala world", paras);

		assertEquals(cmd, cmdCmp);

	}
	
	@Test
	public void testSmartParseCommand1() throws InvalidCommandException{
		
		Command cmd = ParserFacade.getInstance().parseCommand(
				"add buy milk for mom @ 13/2/2015 12:00pm \\p high \\t personal").get(0);
		ArrayList<Parameter> paras = new ArrayList<Parameter>();
		paras.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, "13/2/2015 12:00pm"));
		paras.add(new Parameter(Parameter.PRIORITY_ARGUMENT_TYPE, "high"));
		paras.add(new Parameter(Parameter.TYPE_ARGUMENT_TYPE, "personal"));
		Command cmdCmp = new Command(Command.ADD_COMMAND_TYPE, "buy milk for mom", paras);

		assertEquals(cmd, cmdCmp);    
		
	}

	//Boundary/Equivalence Testing, testing it now with zero parameter.
	@Test
	public void testConvertAddCommandtoTask1() {

		Command cmd = new Command(Command.ADD_COMMAND_TYPE, "test 123", new ArrayList<Parameter>());

		Task taskCmp = new Task("test 123");

		Task tmpTask; 
		tmpTask = ParserFacade.getInstance().convertAddCommandtoTask(cmd);

		assertEquals(tmpTask, taskCmp);

	}
	
	//Boundary/Equivalence Testing, Testing it with one parameter.
	@Test
	public void testConvertAddCommandtoTask2() throws ParseException {

		DateFormat df1 = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_100);
		ArrayList<Parameter> paras = new ArrayList<Parameter>();
		paras.add(new Parameter(Parameter.START_DATE_ARGUMENT_TYPE, "13/12/2015 9:00am"));
		//paras.add(new Parameter(Parameter.END_DATE_ARGUMENT_TYPE, "14/12/2015 13:00pm"));
		//paras.add(new Parameter(Parameter.PLACE_ARGUMENT_TYPE, "bpp"));
		//paras.add(new Parameter(Parameter.PRIORITY_ARGUMENT_TYPE, "high"));
		//paras.add(new Parameter(Parameter.TYPE_ARGUMENT_TYPE, "meeting"));

		Command cmd = new Command(Command.ADD_COMMAND_TYPE, "test 123", paras);

		Task taskCmp = new Task("test 123");
		//taskCmp.setEnd_date(df1.parse("14/12/2015 13:00pm"));
		taskCmp.setStart_date(df1.parse("13/12/2015 9:00am"));
		//taskCmp.setPlace_argument("bpp");
		//taskCmp.setPriority_argument("high");
		//taskCmp.setType_argument("meeting");
		Task tmpTask;
		tmpTask = ParserFacade.getInstance().convertAddCommandtoTask(cmd);
		assertEquals(tmpTask, taskCmp);

	}

	
	//Equivalence testing involved from testParseDate1() to testParseDate105()
	//For e.g : parsing 1/2 2am is enough, parsing 3/2 3am will most likely to pass. 
	@Test
	public void testParseDate1() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("1/2 2am");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 2);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}



	
	

	@Test
	public void testParseDate5() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("5/6");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 5);
		cal.set(Calendar.MONTH, 5);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}

	@Test
	public void testParseDate6() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("25/12 11:30pm");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 25);
		cal.set(Calendar.MONTH, 11); 
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}

	@Test
	public void testParseDate7() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("3/8 15:30");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 3);
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}



	
	@Test
	public void testParseDate10() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("next tuesday 3:45pm");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);

		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 45);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(new Date(cal.getTime().getTime() + UKDateParser.ONE_WEEK_IN_MS), date);

	}

	@Test
	public void testParseDate100() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("11/11/2016 15:30");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 11);
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}

	@Test
	public void testParseDate100_1() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("11/11/16 15:30");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 11);
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}

	@Test
	public void testParseDate101() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("11/11/2016 3:30am");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 11);
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}

	


	@Test
	public void testParseDate104() throws ParseException {
		Date date = ParserFacade.getInstance().parseDate("11/11/16 3am");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 11);
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.HOUR_OF_DAY, 3);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(cal.getTime(), date);

	}


}
