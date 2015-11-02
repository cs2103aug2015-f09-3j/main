package application.controller.parser;

// @@LimYouLiang A0125975U
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UKDateParser {

	public static final int ONE_WEEK_IN_MS = 86400 * 7 * 1000;
	ArrayList<String> listsOfDateFormat;
	private static UKDateParser instance;

	private UKDateParser() {
		listsOfDateFormat = new ArrayList<String>();
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_1);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_2);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_3);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_4);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_5);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_6);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_7);

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

	static UKDateParser getInstance() {
		if (instance == null) {
			instance = new UKDateParser();
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
			if (count <= 7) {
				tmpDate = dateStr + " " + Calendar.getInstance().get(Calendar.YEAR);
			} else {
				tmpDate = dateStr;
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

		// Modified the current set date to next week of that date
		if (isNextWeek) {
			long plusOneWeekTime = date.getTime() + ONE_WEEK_IN_MS;
			date = new Date(plusOneWeekTime);
		}

		return date;
	}

}
