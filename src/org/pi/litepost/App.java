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
import org.pi.litepost.controllers.*;
import org.pi.litepost.html.Validator;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD{

	public static Properties config;
	public static String HOSTNAME;

	public App(int port) {
		super(port);
		
		Router.add("upload", Method.GET, "/public/upload/{filename}", FileController::getUploadedFile);
		Router.add("public", Method.GET, "/public/{filename}", FileController::getFile);
		Router.add("home", Method.GET, "/", HomeController::getHome);
		
		//configuration
		Router.add("setupPage", Method.GET, "/setup", ConfigController::getSetup);
		Router.add("setupPost", Method.POST, "/setup", ConfigController::postSetup);
		
		//Login & registration
		Router.add("loginPage", Method.GET, "/login", LoginController::getLogin);
		Router.add("loginPageRedirect", Method.GET, "/login/{redirect}", LoginController::getLogin);
		Router.add("loginPost", Method.POST, "/login", LoginController::postLogin);
		Router.add("loginPostRedirect", Method.POST, "/login/{redirect}", LoginController::postLogin);
		Router.add("logout", Method.GET, "/logout", LoginController::logout);
		Router.add("registrationPage", Method.GET, "/register", LoginController::getRegistration);
		Router.add("registrationPost", Method.POST, "/register", LoginController::postRegistration);
		Router.add("emailVerification", Method.GET, "/verify/{verification_token}", LoginController::verifyEmail);
		Router.add("lostPasswordPage", Method.GET, "/lostpassword", LoginController::getLostPassword);
		Router.add("lostPasswordPost", Method.POST, "/lostpassword", LoginController::postLostPassword);
		Router.add("resetPasswordPage", Method.GET, "/resetpassword/{reset_token}", LoginController::getResetPassword);
		Router.add("resetPasswordPost", Method.POST, "/resetpassword/{reset_token}", LoginController::postResetPassword);
		
		//user
		Router.add("profilePage", Method.GET, "/profile", UserController::getProfile);
		Router.add("messagesPage", Method.GET, "/messages", UserController::getMessages);
		
		//posts
		Router.add("allPosts", Method.GET, "/posts", PostController::getAll);
		Router.add("newPost", Method.GET, "/posts/new", PostController::getNew);
		Router.add("insertPost", Method.POST, "/posts/new", PostController::postNew);
		Router.add("singlePost", Method.GET, "/post/{post_id}", PostController::getSingle);
		Router.add("commentPost", Method.POST, "/post/{post_id}/comment", PostController::commentPost);
		
		//Events
		Router.add("dailyOverview", Method.GET, "/events/{year}/{month}/{day}", EventController::getDailyOverview);
		Router.add("calendar", Method.GET, "/events/{year}/{month}", EventController::getCalendar);
		Router.add("calendarToday", Method.GET, "/events", EventController::redirectCalendar);
		Router.add("overview", Method.GET, "/events/overview", EventController::getOverview);
		
	}
	
	@Override public Response serve(IHTTPSession session) {
		System.out.println(String.format("%s %s", session.getMethod(), session.getUri()));
		
		//performance improvement: /public/.* needs no model. bypass everything
		if(session.getUri().startsWith("/public/")) {
			Route route = Router.getHandler(session);
			if(route != null) {
				HashMap<String, String> args = Router.getRouteParams(session.getUri(), route);
				HashMap<String, String> files = new HashMap<>();
				return route.getHandler().handle(session, args, files, new ViewContext(), null);
			}
		}
		
		//first time configuration
		if(!config.getProperty("litepost.configured").equals("true")) {
			if(!session.getUri().equals(Router.linkTo("setupPage"))) {
				return Router.redirectTo("setupPage");
			}
		}
		
		// standard setup & route handling
		ViewContext viewContext = new ViewContext();
		try (Model model = new Model()){
			model.init();
			model.getSessionManager().resumeSession(session.getCookies());
			model.getSessionManager().cleanSessions();
			
			String username = model.getSessionManager().get("username");
			if(username != null)
			{
				viewContext.put("username", username);
			}
			
			Route route = Router.getHandler(session);
			viewContext.setValidator(new Validator(model.getSessionManager()));
			Response resp = null;
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
		defaultProps.put("litepost.serverhost", "127.0.0.1:8080");
		defaultProps.put("litepost.public.folder", "public");
		defaultProps.put("litepost.public.uploadfolder", "public/upload");
		defaultProps.put("litepost.debug", "false");
		defaultProps.put("litepost.configured", "false");
		
		String generalFilePath = "res" + File.separatorChar + "config.properties";
		try {
			InputStream inStream = new FileInputStream(generalFilePath);
			generalProps.load(inStream);
		} catch (Exception e) {
			System.out.println("Could not load config file");
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
		try{
			Seeder.seed();
		}catch(Exception e){
			System.out.println(e);
		}
		
		Properties p = new Properties();
		p.setProperty("resource.loader", "file");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        File f = new File("templates");
        p.setProperty("file.resource.loader.path", f.getAbsolutePath());
        
		Velocity.init(p);
		
		loadConfig();
		
		File file = new File((String) config.get("litepost.public.uploadfolder"));
		if(!file.exists() || !file.isDirectory())
		{
			if (!file.mkdirs())
			{
				System.out.println("could not create Upload folder");
				System.exit(-1);
			}
		}
		
		
		String hostname = (String) config.get("litepost.serverhost");
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
		
		System.out.println(String.format("Server listening on %s. Hit Enter to stop.", hostname));
		
		try {
			System.in.read();
		} catch (Throwable ignored) {}
		
		app.stop();
		System.out.println("Server stopped.");
	}
}
