package org.pi.litepost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HomeController {
	public static Response getHome(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data, Model model) {
		try {
			model.getPostManager().insert("test", "testing", "1234", 0);
		} catch (DatabaseCriticalErrorException e) {
			return Router.error(e);
		}
		try {
			data.put("posts", model.getPostManager().getAll());
		} catch (Exception e) {
			return Router.error(e);
		}
		return new Response(View.make("home", data));
	}
	
	public static Response getLogin(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data, Model model) {
		return new Response(View.make("login", data));
	}
	
	public static Response getCalendar(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data, Model model) {
		return new Response(View.make("calendar", data));
	}
	
	public static Response getAllEvents(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data, Model model) {
		return new Response(View.make("allevents", data));
	}
}
