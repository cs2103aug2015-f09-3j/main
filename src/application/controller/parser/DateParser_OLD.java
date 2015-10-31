package application.controller.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateParser_OLD {

	public static final int ONE_WEEK_IN_MS = 86400 * 7 * 1000;
	ArrayList<String> listsOfDateFormat;
	private static DateParser_OLD instance;

	private DateParser_OLD() {
		listsOfDateFormat = new ArrayList<String>();
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_1);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_2);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_3);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_4);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_5);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_6);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_7);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_8);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_9);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_10);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_11);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_12);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_13);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_14);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_15);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_16);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_17);

		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_100);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_101);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_102);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_103);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_104);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_105);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_106);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_107);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_108);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_109);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_110);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_111);

	}

	static DateParser_OLD getInstance() {
		if (instance == null) {
			instance = new DateParser_OLD();
		}
		return instance;
	}

	public Date parseDate(String dateStr) {

		DateFormat df1;
		Date date = null;
		int count = 1;
		boolean isNextWeek = false;

		if (dateStr.toUpperCase().contains("NEXT")) {

			isNextWeek = true;

			dateStr = dateStr.replaceAll("(?i)next", "").trim();
			
		}

		for (String type : this.listsOfDateFormat) {

			df1 = new SimpleDateFormat(type);
			df1.setLenient(false);

			String tmpDate = "";
			if (count <= 17) {
				tmpDate = dateStr + " " + Calendar.getInstance().get(Calendar.YEAR);
			} else {
				tmpDate = dateStr;
			}

			if (count == 2 || count == 4 || count == 8 || count == 10 || count == 14) {
				tmpDate += " " + (Calendar.getInstance().get(Calendar.MONTH) + 1) + " "
						+ Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);

			} else if (count == 15 || count == 16 || count == 17) {
				tmpDate += " " + (Calendar.getInstance().get(Calendar.MONTH) + 1) + " "
						+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			}

			// 3 is the max char difference allowance.
			if (tmpDate.length() <= type.length() + 3) {
				try {

					date = df1.parse(tmpDate);

					break;
				} catch (ParseException e) {
					// continue parsing
					// LogManager.getInstance().log("Parse exception at date
					// parsing");
				}
			}
			count++;

		}
		
		//Modified the current set date to next week of that date
		if(isNextWeek){
			long plusOneWeekTime = date.getTime() + ONE_WEEK_IN_MS;
			date = new Date(plusOneWeekTime);		
		}
		
		return date;
	}

}
