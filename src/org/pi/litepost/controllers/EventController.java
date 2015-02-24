package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Event;
import org.pi.litepost.applicationLogic.Model;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class EventController {
	public static Response getCalendar(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		return new Response(View.make("event.calendar", context));
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
}
