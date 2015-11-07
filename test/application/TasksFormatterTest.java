package application;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Test;

import application.controller.parser.ParserFacade;
import application.model.Task;
import application.utils.TasksFormatter;

public class TasksFormatterTest {

	@Test
	public void testFormatType1() {

		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Buy Milk YOYOYOY");
		task1.setEnd_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setStart_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setPriority_argument("high");
		task1.setPlace_argument("bukit panjang");
		task1.setType_argument("Meeting");
		tasks.add(task1);
		tasks.add(task1);

		assertEquals("1. Buy Milk YOYOYOY\n2. Buy Milk YOYOYOY\n", TasksFormatter.format(tasks, TasksFormatter.PLAIN_VIEW_TYPE));
	}

	@Test
	public void testFormatType2() {
		DateFormat df1 = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_101);
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Buy Milk YOYOYOY");
		try{
		task1.setEnd_date(df1.parse("09/10/1935 8:10PM"));
		task1.setStart_date(df1.parse("09/10/1935 8:10PM"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		task1.setPriority_argument("high");
		task1.setPlace_argument("bukit panjang");
		task1.setType_argument("Meeting");
		tasks.add(task1);
		tasks.add(task1);


		assertEquals("Description                    Start Date           End Date             "
				+ "Location             Type            Priority       \n1   Buy Milk YOYOYOY               "
				+ "09/10/35 08:10PM     09/10/35 08:10PM     bukit panjang        Meeting         high           "
				+ "\n2   Buy Milk YOYOYOY               09/10/35 08:10PM     09/10/35 08:10PM     bukit panjang        "
				+ "Meeting         high", TasksFormatter.format(tasks, TasksFormatter.DETAIL_VIEW_TYPE).trim());
	}

	//@@ A0130876B
	@Test
	public void testViewSchedule(){

		ArrayList<Task> tasks = new ArrayList<Task>();
		DateFormat format = new SimpleDateFormat(ParserFacade.DATE_FORMAT_TYPE_101);
		Task nobodinessTask = new Task("NOBODINESS DUE");
		try{
			nobodinessTask.setEnd_date(format.parse("16/11/2015 7:00PM"));
			nobodinessTask.setStart_date(format.parse("16/11/1935 6:00PM"));
			}
			catch(Exception e){
				e.printStackTrace();
			}

		nobodinessTask.setPlace_argument("bukit panjang");
		nobodinessTask.setType_argument("Meeting");
		tasks.add(nobodinessTask);
		tasks.add(nobodinessTask);

		assertEquals("SCHEDULE \n \n" + "16/11 2015\n[06:00PM - 07:00PM]   NOBODINESS DUE\n"+
				"[06:00PM - 07:00PM]   NOBODINESS DUE\n\n", TasksFormatter.format(tasks, TasksFormatter.TIMELINE_VIEW_TYPE));
	}

	@Test
	public void testViewType() throws ParseException {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Gym with wendy");
		task1.setType_argument("personal");
		tasks.add(task1);
		Task task2 = new Task("Buy a new phone");
		task2.setType_argument("personal");
		tasks.add(task2);
		Task task3 = new Task("Meeting with 2103 prof");
		task3.setType_argument("school");
		tasks.add(task3);


		assertEquals("TYPE: personal\n1. Gym with wendy\n2. Buy a new phone\n\nTYPE: school\n"
				+ "1. Meeting with 2103 prof\n\n", TasksFormatter.format(tasks, TasksFormatter.TYPE_VIEW_TYPE));
	}

	@Test
	public void testViewPriority() throws ParseException {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Take a shower");
		task1.setPriority_argument("low");
		tasks.add(task1);
		Task task2 = new Task("Talk to Darren about fundraising");
		tasks.add(task2);
		Task task3 = new Task("Get a life");
		task3.setPriority_argument("high");
		tasks.add(task3);


		assertEquals("PRIORITY: low\n1. Take a shower\n\nPRIORITY: normal\n1. Talk to Darren about fundraising\n\n"
				+ "PRIORITY: high\n1. Get a life\n\n", TasksFormatter.format(tasks, TasksFormatter.PRIORITY_VIEW_TYPE));
	}

	@Test
	public void testViewPlace() throws ParseException {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Gym with wendy");
		task1.setPlace_argument("Gym @ Bt. Gombak MRT");
		tasks.add(task1);
		Task task2 = new Task("Dinner with parents");
		task2.setPlace_argument("Lau Pat Sat");
		tasks.add(task2);
		Task task3 = new Task("Study for 2103 Finals");
		tasks.add(task3);


		assertEquals("LOCATION: Gym @ Bt. Gombak MRT\n1. Gym with wendy\n\nLOCATION: Lau Pat Sat\n1. Dinner with parents\n\n"
				+ "LOCATION: NOT APPLICABLE.\n1. Study for 2103 Finals\n\n", TasksFormatter.format(tasks, TasksFormatter.PLACE_VIEW_TYPE));

	}

	@Test
	public void testViewFloating() throws ParseException {
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Take a shower");
		tasks.add(task1);
		Task task2 = new Task("Go for a run");
		tasks.add(task2);
		Task task3 = new Task("Buy Gong Cha Peach Green Tea");
		tasks.add(task3);


		assertEquals("1. Take a shower\n2. Go for a run\n3. Buy Gong Cha Peach Green Tea\n", TasksFormatter.format(tasks, TasksFormatter.FLOATING_VIEW_TYPE));
	}
}
