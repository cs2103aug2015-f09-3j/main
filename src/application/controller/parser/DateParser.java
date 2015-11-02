package application.controller.parser;

//@@LimYouLiang A0125975U
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class DateParser {

	private static DateParser instance;

	private DateParser() {

	}

	static DateParser getInstance() {
		if (instance == null) {
			instance = new DateParser();
		}
		return instance;
	}

	public Date parseDate(String dateStr) {

		if (dateStr.contains("/") || dateStr.contains(".")) {
			List<Date> lists = new ArrayList<Date>();
			lists.add(UKDateParser.getInstance().parseDate(dateStr));

			return UKDateParser.getInstance().parseDate(dateStr);

		} else {

			List<Date> lists = null;
			Parser parser = new Parser();
			List<DateGroup> groups = parser.parse(dateStr);
			for (DateGroup group : groups) {
				lists = group.getDates();
			}
			return lists.get(0);

		}

	}

	public List<Date> parseMultipleDate(String dateStr) {

		List<Date> lists = null;
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(dateStr);
		for (DateGroup group : groups) {
			lists = group.getDates();
		}
		return lists;

	}

	public boolean containMultipleDate(String dateStr) {

		List<Date> lists = null;
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(dateStr);
		for (DateGroup group : groups) {
			lists = group.getDates();
		}
		return lists.size() > 1;

	}

}
