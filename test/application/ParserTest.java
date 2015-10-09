package application;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.ArrayList;

import org.junit.Test;

public class ParserTest {

	@Test
	public void test() {
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		Task task1 = new Task("Buy Milk YOYOYOY");
		task1.setEnd_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setStart_date(DateFormat.getDateInstance().getCalendar().getTime());
		task1.setPriority_argument("high");
		task1.setPlace_argument("bukit panjang");
		task1.setType_argument("Meeting");
		tasks.add(task1);
		tasks.add(task1);
		assert(true);
		
	}

}
