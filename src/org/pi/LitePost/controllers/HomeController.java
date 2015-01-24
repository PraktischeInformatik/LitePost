package org.pi.LitePost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.LitePost.View;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class HomeController {
	public static Response getHome(IHTTPSession session, Map<String, String> routeArgs, HashMap<String, Object> data) {
		return new Response(View.make("home", data));
	}
}
