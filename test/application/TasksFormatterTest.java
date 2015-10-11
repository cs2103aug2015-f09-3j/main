package application;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
		DateFormat df1 = new SimpleDateFormat(Parser.DATE_FORMAT_TYPE_101);
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

		
		assertEquals("Description                    Start Date           End Date             Location             Type            Priority       \n1   Buy Milk YOYOYOY               09/10/35 08:10PM     09/10/35 08:10PM     bukit panjang        Meeting         high           \n2   Buy Milk YOYOYOY               09/10/35 08:10PM     09/10/35 08:10PM     bukit panjang        Meeting         high", TasksFormatter.format(tasks, TasksFormatter.DETAIL_VIEW_TYPE).trim());
	}
	

}
