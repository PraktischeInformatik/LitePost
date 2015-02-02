package org.pi.litepost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HomeController {
	public static Response getHome(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("home", data));
	}
	
	public static Response getCalendar(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("calendar", data));
	}
	
	public static Response getAllEvents(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("allevents", data));
	}
}
