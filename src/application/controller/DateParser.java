package application.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateParser{
	
	
		
		
	ArrayList<String> listsOfDateFormat;
	private static DateParser instance;
	private DateParser(){
		listsOfDateFormat = new ArrayList<String>();
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_1);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_2);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_3);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_4);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_5);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_6);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_7);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_8);

		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_100);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_101);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_102);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_103);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_104);
		listsOfDateFormat.add(ParserFacade.DATE_FORMAT_TYPE_105);
	}
	
	public static DateParser getInstance(){
		if (instance == null) {
			instance = new DateParser();
		}
		return instance;
	}
	
	
	public Date parseDate(String dateStr) {

		DateFormat df1;
		Date date = null;
		int count = 1;

		for (String type : this.listsOfDateFormat) {

			df1 = new SimpleDateFormat(type);
			df1.setLenient(false);
			
			String tmpDate = "";
			if (count <= 8) {
				tmpDate = dateStr + " " + Calendar.getInstance().get(Calendar.YEAR);
			}else{
				tmpDate = dateStr;
			}
			
			if (count == 2 || count == 4 || count == 8) {
				tmpDate += " " + (Calendar.getInstance().get(Calendar.MONTH) + 1) + " "
						+ Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);

			}

			// 3 is the max char difference allowance.
			if (tmpDate.length() <= type.length() + 3) {
				try {

					date = df1.parse(tmpDate);

					break;
				} catch (ParseException e) {
					// continue parsing
					LogManager.getInstance().log("Parse exception at date parsing");
				}
			}
			count++;
			
		}
		return date;
	}
	

}
