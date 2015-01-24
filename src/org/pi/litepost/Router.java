package org.pi.litepost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Router {
	private static ArrayList<Route> routes = new ArrayList<>();
	private static Pattern makePattern = Pattern.compile("\\{\\w+\\}");

	public static boolean add(String name, Method method, String route, Handler handler) {
		Matcher matcher = makePattern.matcher(route);
		ArrayList<String> argNames = new ArrayList<>();
		while (matcher.find()) {
			argNames.add(route.substring(matcher.start() + 1, matcher.end() - 1));
		}
		Pattern pattern = Pattern.compile(matcher.replaceAll("(\\\\w+)"));
		routes.add(new Route(name, method, route, pattern, argNames, handler));
		return true;
	}

	public static Route getHandler(IHTTPSession session) {
		String uri = session.getUri();
		Method method = session.getMethod();
		for (Route route : routes) {
			Matcher matcher = route.pattern.matcher(uri);
			if (method.equals(route.method) && matcher.matches()) {
				return route;
			}
		}
		return null;
	}

	public static HashMap<String, String> getRouteParams(String uri, Route route) {
		HashMap<String, String> args = new HashMap<>();
		Matcher matcher = route.pattern.matcher(uri);
		if (matcher.matches()) {
			for (int i = 0; i < matcher.groupCount(); i++) {
				args.put(route.argNames.get(i), matcher.group(i + 1)); 
			}
		}
		return args;
	}

	public static Response redirectTo(String location, Map<String, String> args) {
		Response resp = new Response(Status.REDIRECT, "text/plain", "you are being redirected");
		for (Route route : routes) {
			if (route.name.equals(location)) {
				String uri = route.getRoute();
				for(String arg : args.values()) {
					uri.replace("{" + arg + "}", args.get(arg));
				}
				resp.addHeader("location", uri);
				return resp;
			}
		}
		resp.addHeader("location", location);
		return resp;
	}
	
	public static Response error(Exception e) {
		HashMap<String, Object> data = new HashMap<>();
		if(App.config.getProperty("Debug").equalsIgnoreCase("true")) {
			data.put("classname", e.getClass().getName());
			data.put("message", e.getMessage());
			data.put("trace",  e.getStackTrace());
			return new Response(View.make("error.debug", data));
		}else {
			data.put("message", e.getMessage());
			return new Response(View.make("error.production", data));
		}
	}

	public static class Route {
		private String name;
		private Method method;
		private String route;
		private Pattern pattern;
		private ArrayList<String> argNames;
		private Handler handler;
		
		public ArrayList<String> getArgNames() {
			return argNames;
		}
		public String getName() {
			return name;
		}
		public Method getMethod() {
			return method;
		}
		public String getRoute() {
			return this.route;
		}
		public Pattern getPattern() {
			return pattern;
		}

		public Handler getHandler() {
			return handler;
		}

		public Route(String name, Method method, String route, Pattern pattern, ArrayList<String> argNames, Handler handler) {
			super();
			this.name = name;
			this.method = method;
			this.route = route;
			this.pattern = pattern;
			this.handler = handler;
			this.argNames = argNames;
		}
	}
}