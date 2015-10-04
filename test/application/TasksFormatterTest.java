package application;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.ArrayList;

import org.junit.Test;

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
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Buy Milk YOYOYOY");
		task1.setEnd_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setStart_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setPriority_argument("high");
		task1.setPlace_argument("bukit panjang");
		task1.setType_argument("Meeting");
		tasks.add(task1);
		tasks.add(task1);
		
		
		assertEquals("Description                    Start Date           End Date             Location             Type            Priority       \nBuy Milk YOYOYOY               Oct 4, 1935          Oct 4, 1935          bukit panjang        Meeting         high           \nBuy Milk YOYOYOY               Oct 4, 1935          Oct 4, 1935          bukit panjang        Meeting         high", TasksFormatter.format(tasks, TasksFormatter.DETAIL_VIEW_TYPE).trim());
	}
	

}
