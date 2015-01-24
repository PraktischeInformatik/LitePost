package org.pi.LitePost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.pi.LitePost.Router.Route;
import org.pi.LitePost.controllers.FileController;
import org.pi.LitePost.controllers.HomeController;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD{

	public static Properties config;

	public App(int port) {
		super(port);
		
		Router.add("files", Method.GET, "/public/.*", FileController::getFile);
		Router.add("home", Method.GET, "/", HomeController::getHome)
	}
	
	@Override public Response serve(IHTTPSession session) {
		System.out.println(String.format("%s %s", session.getMethod(), session.getUri()));
		Route route = Router.getHandler(session);
		if (route != null) {
			HashMap<String, String> args = Router.getRouteParams(session.getUri(), route);
			return route.getHandler().handle(session, args, new HashMap<>());
		}else {
			return new Response(Response.Status.NOT_FOUND, "text/plain", "404 - Resource not found");
		}
	}

	public static void main(String[] args) {
		Velocity.init();
		
		InputStream inStream = null;
		try {
			inStream = new FileInputStream("res" + File.separatorChar + "config.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		config = new Properties();
		try {
			config.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		String serverport = (String) config.getOrDefault("Serverport", "8080");
		int port = 8080;
		try {
			port = Integer.parseInt(serverport);
		}catch(NumberFormatException e) {
			System.out.println("Error reading serverport from configuration.");
			System.out.println("Falling back to default port: " + port);
		}
		
		App app = new App(port);
		try {
			app.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println(String.format("Server startet on port %d. Hit Enter to stop.", port));
		
		try {
			System.in.read();
		} catch (Throwable ignored) {}
		
		app.stop();
		System.out.println("Server stopped.");
	}
}
