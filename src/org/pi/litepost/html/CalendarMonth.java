package org.pi.litepost.html;

import java.util.ArrayList;

public class CalendarMonth {
	ArrayList<CalendarWeek> weeks = new ArrayList<>();
	
	public void appendWeek() {
		weeks.add(new CalendarWeek());
	}
	
	public void appendDay(CalendarDay day) {
		weeks.get(weeks.size() - 1).addDay(day);
	}
	
	public ArrayList<CalendarWeek> getWeeks() {
		return weeks;
	}
}
