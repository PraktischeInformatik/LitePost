package org.pi.litepost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.pi.litepost.Router.Route;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.controllers.FileController;
import org.pi.litepost.controllers.HomeController;
import org.pi.litepost.controllers.LoginController;
import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD{

	public static Properties config;

	public App(int port) {
		super(port);
		
		Router.add("files", Method.GET, "/public/.*", FileController::getFile);
		Router.add("home", Method.GET, "/", HomeController::getHome);
		Router.add("calendar", Method.GET, "/calendar", HomeController::getCalendar);
		Router.add("allevents", Method.GET, "/allevents", HomeController::getAllEvents);
		Router.add("daysight", Method.GET, "/daysight", HomeController::getDaySight);
		
		
		Router.add("loginPage", Method.GET, "/login", LoginController::getLogin);
		Router.add("loginPost", Method.POST, "/login", LoginController::postLogin);
	}
	
	@Override public Response serve(IHTTPSession session) {
		Model model;
		try {
			model = new Model();
			model.getSessionManager().cleanSessions();
			model.getSessionManager().resumeSession(session.getCookies());
		} catch (DatabaseCriticalErrorException | SQLException e) {
			return Router.error(e);
		}
		
		System.out.println(String.format("%s %s", session.getMethod(), session.getUri()));
		Route route = Router.getHandler(session);
		Response resp = null;
		if (route != null) {
			HashMap<String, String> args = Router.getRouteParams(session.getUri(), route);
			HashMap<String, String> files = new HashMap<>();
			if (Method.PUT.equals(session.getMethod()) || Method.POST.equals(session.getMethod())) {
				try {
					session.parseBody(files);
				} catch (Exception e) {
					return Router.error(e);
				}
			}
			resp = route.getHandler().handle(session, args, files, new HashMap<>(), model);
		}else {
			resp = new Response(Response.Status.NOT_FOUND, "text/plain", "404 - Resource not found");
		}
		try {
			model.close();
		} catch (DatabaseCriticalErrorException e) {
		}
		return resp;
	}

	public static void main(String[] args) {
		Properties p = new Properties();
		p.setProperty("resource.loader", "file");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        File f = new File("templates");
        p.setProperty("file.resource.loader.path", f.getAbsolutePath());
        
        
		Velocity.init(p);
		
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
