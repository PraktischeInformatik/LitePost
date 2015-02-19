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
		Pattern pattern = Pattern.compile(matcher.replaceAll("(.+)"));
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
	
	private static Route getRoute(String location) {
		for (Route route : routes) {
			if (route.name.equals(location)) {
				return route;
			}
		}
		return null;
	}

	public static Response redirectTo(String location) {
		return redirectTo(location, new HashMap<>());
	}
	
	public static Response redirectTo(String location, Object... args) {
		Route route = getRoute(location);
		if(route != null) {
			return redirectTo(route, args);
		}
		return redirectToUrl(location);
	}
	
	public static Response redirectTo(String location, Map<String, String> args) {
		Route route = getRoute(location);
		if(route != null) {
			String[] argsList = new String[route.argNames.size()];
			int i = 0;
			for(String argName : route.argNames) {
				argsList[i] = args.getOrDefault(argName, "");
				i++;
			}
			return redirectTo(route, argsList);
		}
		return redirectToUrl(location);
	}
	
	private static Response redirectTo(Route route, Object[] args) {
		String uri = route.getRoute();
		for(int i = 0; i < args.length; i++) {
			uri = uri.replace("{" + route.argNames.get(i) + "}", args[i].toString());
		}
		return redirectToUrl(uri);
	}
	
	private static Response redirectToUrl(String url) {
		Response resp = new Response(Status.SEE_OTHER, "text/plain", "you are being redirected");
		resp.addHeader("location", url);
		return resp;
	}
	
	
	public static String linkTo(String location) {
		return linkTo(location, new HashMap<>());
	}
	
	public static String linkTo(String location, Object... args) {
		Route route = getRoute(location);
		if(route != null) {
			return linkTo(route, args);
		}
		return location;
	}
	
	public static String linkTo(String location, Map<String, String> args) {
		Route route = getRoute(location);
		if(route != null) {
			String[] argsList = new String[route.argNames.size()];
			int i = 0;
			for(String argName : route.argNames) {
				argsList[i] = args.getOrDefault(argName, "");
				i++;
			}
			return linkTo(route, argsList);
		}
		return location;
	}
	
	private static String linkTo(Route route, Object[] args) {
		String uri = route.getRoute();
		for(int i = 0; i < args.length; i++) {
			uri = uri.replace("{" + route.argNames.get(i) + "}", args[i].toString());
		}
		return uri;
	}
	
	public static Response error(Exception e, ViewContext context) {
		if(App.config.getProperty("litepost.debug").equalsIgnoreCase("true")) {
			context.put("error", new Error(e));
			return new Response(Status.INTERNAL_ERROR, "text/html", View.make("error.debug", context));
		}else {
			context.put("message", e.getMessage());
			return new Response(Status.INTERNAL_ERROR, "text/html", View.make("error.production", context));
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