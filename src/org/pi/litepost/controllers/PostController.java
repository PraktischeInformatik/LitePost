package org.pi.litepost.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import org.pi.litepost.App;
import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.applicationLogic.Post;
import org.pi.litepost.html.Validator;

import com.google.common.io.Files;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class PostController {
	private static DateTimeFormatter FILENAME_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
	private static DateTimeFormatter EVENT_TIME_FORMAT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
	
	public static Response getAll(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		ArrayList<Post> posts = new ArrayList<>();
		try {
			posts = model.getPostManager().getAll();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		context.put("posts", posts);
		return new Response(View.make("post.all", context));
	} 
	
	public static Response getSingle(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) { 
		int postId = 0;
		try {
			postId = Integer.parseInt(args.getOrDefault("post_id", ""));
		}catch(NumberFormatException e) {
			return Router.notFound(context);
		}
		
		Post post = null;
		try {
			post = model.getPostManager().getById(postId);
			if(post == null)  {
				return Router.notFound(context);
			}
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		context.put("post", post);
		return new Response(View.make("post.single", context));
	}
	
	public static Response getNew(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		return new Response(View.make("post.new", context));
	}
	
	public static Response postNew(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		String datePattern = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";
		
		Function<String, String> saniziteContent = 
				s -> View.sanitizePostContent(
						s.replace("&quot;", "\"")
						 .replace("&amp;", "&"));
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("hasTitle", View::sanitizeStrict, s -> s.length() > 0, "title")
			.validateSingle("hasContent", saniziteContent, s -> s.length() > 0, "content")
			.validateSingle("hasContact", View::sanitizeStrict, s -> s.length() > 0, "contact")
			.validateFlag("isEvent", "is-event")
			.validateMultiple("validDateIfEvent", p -> !p[0].isEmpty() && p[1].matches(datePattern), "is-event", "date");
		context.setValidator(validator);
		
		if(!validator.validate(session.getParms())) {
			return new Response(View.make("post.new", context));
		}
		int i = 0;
		String image = "image" + i;
		Map<String, String> params = session.getParms();
		String uploadfolder = (String) App.config.get("litepost.public.uploadfolder");
		ArrayList<File> loadedImages = new ArrayList<>();
		ArrayList<String> sources = new ArrayList<>(); 
		while(params.containsKey(image)) {
			String filename = LocalDateTime.now().format(FILENAME_TIME_FORMAT);
			String imagename = params.get(image);
			if(imagename != null) {
				String extension = imagename.substring(imagename.lastIndexOf('.'));
				String outfilename = filename + extension;
				File output = new File(uploadfolder + File.separator + outfilename);
				int filenum = 1;
				while(output.exists()){
					outfilename = filename + "-" + filenum + extension;
					output = new File(uploadfolder + File.separator + outfilename);
					filenum++;
				}
				File input = new File(files.get(image));
				if(input.exists()) {
					try {
						loadedImages.add(output);
						Files.copy(input, output);
						input.delete();
						sources.add(outfilename);
					} catch (IOException e) {
						for(File f : loadedImages) {
							f.delete();
						}
						return Router.error(e, context);
					}
				}
			}
			i++;
			image = "image" + i;
		}
		
		if(validator.flag("isEvent")) {
			//LocalDateTime date = LocalDateTime.from(
			//		EVENT_TIME_FORMAT.parse(validator.value("date")));
			
		}
		
		try {
			String title = validator.value("title");
			String content = validator.value("content");
			String contact = validator.value("contact");
			model.getPostManager().insert(title, content, contact);
			ResultSet rs = model.getQueryManager().executeQuery("getLastId", "Posts");
			int postId = rs.getInt(1);
			for(String s : sources) {
				String src = Router.linkTo("upload", s);
				model.getPostManager().addImage(src, postId);
			}
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("allPosts");
	}
	
	public static Response commentPost(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("parentIdNumericOrEmpty", s -> s.matches("[0-9]*"), "parent_id")
			.validateSingle("hasContent", View::sanitizeStrict, s -> s.length() > 0, "content");
		context.setValidator(validator);
		
		int postId = 0;
		try {
			postId = Integer.parseInt(args.getOrDefault("post_id", ""));
		}catch(NumberFormatException e) {
			return new Response(Status.NOT_FOUND, "text/html", View.make("404", context));
		}
		
		if(!validator.validate(session.getParms())) {
			return getSingle(session, args, files, context, model);
		}
		
		try {
			int parentId = 0;
			try {
				parentId = Integer.parseInt(validator.value("parent_id"));
			}catch(NumberFormatException e){}
			String content = validator.value("content");
			model.getCommentManager().insert(content, parentId, postId);
		} catch (SQLException  e) {
			return Router.error(e, context);
		}
		
		return Router.redirectTo("singlePost", postId);
	}
}
