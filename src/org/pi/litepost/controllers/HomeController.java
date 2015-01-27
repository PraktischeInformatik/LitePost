package org.pi.litepost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.View;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HomeController {
	public static Response getHome(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data) {
		return new Response(View.make("home", data));
	}
	
	public static Response getLogin(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data) {
		return new Response(View.make("login", data));
	}
}
