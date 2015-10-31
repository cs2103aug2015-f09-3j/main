package application.controller.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.ParseLocation;
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

}
