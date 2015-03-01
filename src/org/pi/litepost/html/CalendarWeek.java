package org.pi.litepost.html;

import java.util.ArrayList;

public class CalendarWeek {
	ArrayList<CalendarDay> days = new ArrayList<>();
	
	public void addDay(CalendarDay day) {
		days.add(day);
	}
	
	public ArrayList<CalendarDay> getDays() {
		return days;
	}
}
