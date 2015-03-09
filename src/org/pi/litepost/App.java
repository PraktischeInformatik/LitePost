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
import org.pi.litepost.controllers.AdminController;
import org.pi.litepost.controllers.ConfigController;
import org.pi.litepost.controllers.EventController;
import org.pi.litepost.controllers.FileController;
import org.pi.litepost.controllers.HomeController;
import org.pi.litepost.controllers.LoginController;
import org.pi.litepost.controllers.PostController;
import org.pi.litepost.controllers.UserController;
import org.pi.litepost.databaseAccess.DatabaseConnector;
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
		Router.add("firstrun", Method.GET, "/firstrun", ConfigController::getFirstrun);
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
		Router.add("messagesPage", Method.GET, "/profile/messages", UserController::getMessages);
		Router.add("sendMessagePage", Method.GET, "/profile/messages/send", UserController::getSendMessage);
		Router.add("sendMessagePost", Method.POST, "/profile/messages/send", UserController::postSendMessages);
		Router.add("readMessage", Method.GET, "/profile/messages/read/{message_id}", UserController::readMessage);
		Router.add("deleteMessage", Method.GET, "/profile/messages/delete/{message_id}", UserController::deleteMessage);
		Router.add("deleteUser", Method.GET, "/profile/delete/{user_id}", UserController::deleteUser);
		Router.add("myPosts", Method.GET, "/profile/posts", UserController::myPosts);
		Router.add("myComments", Method.GET, "/profile/comments", UserController::myComments);
		
		//posts
		Router.add("allPosts", Method.GET, "/posts", PostController::getAll);
		Router.add("newPost", Method.GET, "/posts/new", PostController::getNew);
		Router.add("insertPost", Method.POST, "/posts/new", PostController::postNew);
		Router.add("updatePage", Method.GET, "/posts/update/{post_id}", PostController::getUpdate);
		Router.add("updatePost", Method.POST, "/posts/update/{post_id}", PostController::postUpdate);
		Router.add("reportPost", Method.GET, "/post/report/{post_id}", PostController::reportPost);
		Router.add("unblockPost", Method.GET, "/post/unblock/{post_id}", PostController::unblockPost);
		Router.add("deletePost", Method.GET, "/post/delete/{post_id}/{return_to_admin}", PostController::deletePost);
		Router.add("deleteComment", Method.GET, "/comment/delete/{comment_id}/{return_to_admin}", PostController::deleteComment);
		Router.add("commentPost", Method.POST, "/comment/new/{post_id}", PostController::commentPost);
		Router.add("singlePost", Method.GET, "/post/{post_id}", PostController::getSingle);
		
		
		//admin
		Router.add("adminPosts", Method.GET, "/admin/posts", AdminController::getPosts);
		Router.add("adminComments", Method.GET, "/admin/comments", AdminController::getComments);
		Router.add("adminUsers", Method.GET, "/admin/users", AdminController::getUsers);
		
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
			String uri = session.getUri();
			String setup = Router.linkTo("setupPage");
			String firstrun = Router.linkTo("firstrun");
			if(!uri.equals(setup) && !uri.equals(firstrun)) {
				return Router.redirectTo("firstrun");
			}
		}
		
		// standard setup & route handling
		ViewContext viewContext = new ViewContext();
		try (Model model = new Model()){
			model.init();
			model.getSessionManager().resumeSession(session.getCookies());
			model.getSessionManager().cleanSessions();
			
			org.pi.litepost.applicationLogic.User user = model.getUserManager().getCurrent();
			if(user != null) {
				viewContext.put("currentUser", user);
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
				resp = Router.notFound(viewContext);
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
		
		defaultProps.setProperty("litepost.serverport", "8080");
		defaultProps.setProperty("litepost.serverhost", "127.0.0.1:8080");
		defaultProps.setProperty("litepost.public.folder", "public");
		defaultProps.setProperty("litepost.public.uploadfolder", "public" + File.separatorChar + "upload");
		defaultProps.setProperty("litepost.debug", "false");
		defaultProps.setProperty("litepost.configured", "false");
		defaultProps.setProperty("litepost.dbpath", "res" + File.separatorChar + "litepost.db");
		defaultProps.setProperty("litepost.mail.smtp.host", "");
		defaultProps.setProperty("litepost.mail.smtp.port", "");
		defaultProps.setProperty("litepost.mail.systemmail", "");
		defaultProps.setProperty("litepost.mail.usessl", "false");
		
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
		Properties p = new Properties();
		p.setProperty("resource.loader", "file");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        File f = new File("templates");
        p.setProperty("file.resource.loader.path", f.getAbsolutePath());
        
		Velocity.init(p);
		
		loadConfig();
		
		
		String dbpath = (String) App.config.getProperty("litepost.dbpath");
		try(DatabaseConnector dbConnector = new DatabaseConnector(dbpath)){
			if(App.config.getProperty("litepost.debug").equalsIgnoreCase("true")) {
				dbConnector.connect(Seeders.getAll());
			} else {
				dbConnector.connect(false);
			}
		}catch(Exception e){
			System.out.println("could not Connect to database");
			e.printStackTrace();
			System.exit(-1);
		}
				
		
		File file = new File(config.getProperty("litepost.public.uploadfolder"));
		if(!file.exists() || !file.isDirectory())
		{
			if (!file.mkdirs())
			{
				System.out.println("could not create Upload folder");
				System.exit(-1);
			}
		}
		
		
		String hostname = config.getProperty("litepost.serverhost");
		String serverport = config.getProperty("litepost.serverport");
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
