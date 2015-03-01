package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Event;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.html.CalendarDay;
import org.pi.litepost.html.CalendarMonth;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import java.time.LocalDate;
import java.time.Year;
import java.time.Month;
import java.time.YearMonth;

public class EventController {
	public static Response getCalendar(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		YearMonth month = Year.parse(args.get("year")).atMonth(Month.valueOf(args.get("month").toUpperCase()));
		YearMonth next = month.plusMonths(1);
		YearMonth prev = month.minusMonths(1);
		String nextMonthLink = Router.linkTo("calendar",
				next.getYear(),
				next.getMonth().toString().toLowerCase());
		String prevMonthLink = Router.linkTo("calendar",
				prev.getYear(),
				prev.getMonth().toString().toLowerCase());
		context.put("nextMonthLink", nextMonthLink);
		context.put("prevMonthLink", prevMonthLink);
		
		String monthName = month.getMonth().toString().toLowerCase();
		String[] localizedMonthnames = {
			"Januar", "Februar", "März", "April", "Mai", "Juni",
			"Juli", "August", "September", "Oktober", "November", "Dezember"
		};
		context.put("monthName", localizedMonthnames[month.getMonthValue() - 1]);
		context.put("year", month.getYear());
		
		try {
			ArrayList<Event> events = model.getPostManager().getEvents(month);
			
			
			int offsetBefore = month.atDay(1).getDayOfWeek().getValue() - 1;
			int offsetAfter = 7 - month.atEndOfMonth().getDayOfWeek().getValue();
			int length = month.lengthOfMonth();
			
			boolean[] eventDays = new boolean[length];
			for(Event e : events) {
				int day = e.getEventDate().getDayOfMonth();
				eventDays[day - 1] = true;
			}
			
			CalendarMonth calendarMonth = new CalendarMonth();
			
			for(int i = 0; i < offsetBefore; i++) {
				if(i % 7 == 0) calendarMonth.appendWeek();
				calendarMonth.appendDay(new CalendarDay(false, true, 0, ""));
			}
			
			for(int i = 0; i < length; i++) {
				if((i + offsetBefore) % 7 == 0) calendarMonth.appendWeek();
				boolean hasEvent = eventDays[i];
				int dayNum = i + 1;
				String link = Router.linkTo("dailyOverview", month.getYear(), monthName, dayNum);
				CalendarDay day = new CalendarDay(hasEvent, false, dayNum, link);
				calendarMonth.appendDay(day);
			}
			
			for(int i = 0; i < offsetAfter; i++) {
				calendarMonth.appendDay(new CalendarDay(false, true, 0, ""));
			}
			
			context.put("month", calendarMonth);
			return new Response(View.make("event.calendar", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	} 
	
	public static Response getOverview(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		try {
			ArrayList<Event> events = model.getPostManager().getEvents();
			context.put("events", events);
			return new Response(View.make("event.overview", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	}
	
	public static Response getDailyOverview(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		LocalDate date = Year.parse(args.get("year"))
				.atMonth(Month.valueOf(args.get("month").toUpperCase()))
				.atDay(Integer.parseInt(args.get("day")));
		try {
			ArrayList<Event> events = model.getPostManager().getEvents(date);
			context.put("events", events);
			return new Response(View.make("event.overview", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	}
	
	public static Response redirectCalendar(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		LocalDate date = LocalDate.now();
		return Router.redirectTo("calendar", date.getYear(), date.getMonth().toString().toLowerCase());
	}
	
	
}
