package application.controller.parser;

//@@LimYouLiang A0125975U
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

	/**
	 * This function parses a string containing a single date.
	 * @param dateStr : date in string format.
	 * @return Date object
	 */
	public Date parseDate(String dateStr) {

		if (isUKFormat(dateStr)) {

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

	/**
	 * This function check if the date is in UK format.
	 * @param dateStr : date in string object.
	 * @return true if the date is in uk format, else false.
	 */
	private boolean isUKFormat(String dateStr) {
		return dateStr.contains("/") || dateStr.contains(".");
	}

	
	/**
	 * This function parse a string containing multiple date. For e.g "today to next wednesday"
	 * @param dateStr : date in string object.
	 * @return : a list of date object. Contains only two object.
	 */
	public List<Date> parseMultipleDate(String dateStr) {

		List<Date> lists = null;
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(dateStr);
		for (DateGroup group : groups) {
			lists = group.getDates();
		}
		return lists;

	}

	/**
	 * This function checks if the date contain multiple date.
	 * @param dateStr : date in string object.
	 * @return return true if contains multiple date, else false.
	 */
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
