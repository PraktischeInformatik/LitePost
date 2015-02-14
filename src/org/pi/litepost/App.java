package org.pi.litepost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.pi.litepost.Router.Route;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.controllers.FileController;
import org.pi.litepost.controllers.HomeController;
import org.pi.litepost.controllers.LoginController;
import org.pi.litepost.controllers.PostController;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD{

	public static Properties config;

	public App(int port) {
		super(port);
		
		Router.add("upload", Method.GET, "/public/upload/{filename}", FileController::getUploadedFile);
		Router.add("public", Method.GET, "/public/{filename}", FileController::getFile);
		Router.add("home", Method.GET, "/", HomeController::getHome);
		Router.add("calendar", Method.GET, "/calendar", HomeController::getCalendar);
		Router.add("allevents", Method.GET, "/allevents", HomeController::getAllEvents);
		Router.add("daysight", Method.GET, "/daysight", HomeController::getDaySight);
		
		
		//user
		Router.add("loginPage", Method.GET, "/login", LoginController::getLogin);
		Router.add("loginPost", Method.POST, "/login", LoginController::postLogin);
		Router.add("logout", Method.GET, "/logout", LoginController::logout);
		Router.add("registrationPage", Method.GET, "/register", LoginController::getRegistration);
		Router.add("registrationPost", Method.POST, "/register", LoginController::postRegistration);
		Router.add("emailVerification", Method.GET, "/verify/{verification_token}", LoginController::verifyEmail);
		
		//posts
		Router.add("allPosts", Method.GET, "/posts", PostController::getAll);
		Router.add("newPost", Method.GET, "/posts/new", PostController::getNew);
		Router.add("insertPost", Method.POST, "/posts/new", PostController::postNew);
		Router.add("singlePost", Method.GET, "/post/{post_id}", PostController::getSingle);
		Router.add("commentPost", Method.POST, "/comments/new", PostController::commentPost);
		
	}
	
	@Override public Response serve(IHTTPSession session) {
		System.out.println(String.format("%s %s", session.getMethod(), session.getUri()));
		
		//performance improvement: /public/.* needs no model. bypass everything
		if(session.getUri().startsWith("/public/")) {
			Route route = Router.getHandler(session);
			if(route != null) {
				HashMap<String, String> args = Router.getRouteParams(session.getUri(), route);
				HashMap<String, String> files = new HashMap<>();
				return route.getHandler().handle(session, args, files, new HashMap<>(), null);
			}
		}
		
		// standard setup & route handling
		HashMap<String, Object> viewContext = new HashMap<>();
		// Router is always available to Views
		viewContext.put("Router", Router.class);
		try (Model model = new Model()){
			model.init();
			model.getSessionManager().resumeSession(session.getCookies());
			model.getSessionManager().cleanSessions();
			
			Route route = Router.getHandler(session);
			Response resp = null;
			viewContext.put("Resources", new HtmlResources(model.getSessionManager()));
			if (route != null) {
				HashMap<String, String> args = Router.getRouteParams(session.getUri(), route);
				HashMap<String, String> files = new HashMap<>();
				if (Method.PUT.equals(session.getMethod()) || Method.POST.equals(session.getMethod())) {
					try {
						session.parseBody(files);
					} catch (Exception e) {
						return Router.error(e,viewContext);
					}
				}
				resp = route.getHandler().handle(session, args, files, viewContext, model);
			}else {
				resp = new Response(Response.Status.NOT_FOUND, "text/html", View.make("404", viewContext));
			}
			return resp;
		} catch (SQLException | ClassNotFoundException e) {
			return Router.error(e, viewContext);
		} 
	}
	
	public static void loadConfig() {
		config = new Properties();
		Properties defaultProps = new Properties();
		Properties generalProps = new Properties();
		Properties machineProps = new Properties();
		
		defaultProps.put("litepost.serverport", "8080");
		defaultProps.put("litepost.public.folder", "public");
		defaultProps.put("litepost.public.uploadfolder", "public/upload");
		defaultProps.put("litepost.debug", "false");
		
		String generalFilePath = "res" + File.separatorChar + "config.properties";
		try {
			InputStream inStream = new FileInputStream(generalFilePath);
			generalProps.load(inStream);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			String computername = InetAddress.getLocalHost().getHostName();
			String machineFilePath = "res" + 
					File.separatorChar + 
					"machine" + 
					File.separatorChar +
					computername + 
					".properties";
			InputStream inStream = new FileInputStream(machineFilePath);
			machineProps.load(inStream);
		} catch (Exception e1) {}
		
		config.putAll(defaultProps);
		config.putAll(generalProps);
		config.putAll(machineProps);
	}
	
	public static void main(String[] args) {
		Properties p = new Properties();
		p.setProperty("resource.loader", "file");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        File f = new File("templates");
        p.setProperty("file.resource.loader.path", f.getAbsolutePath());
        
		Velocity.init(p);
		
		loadConfig();
		
		String serverport = (String) config.get("litepost.serverport");
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
