package org.pi.litepost.controllers;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

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
			return new Response(Status.NOT_FOUND, "text/html", View.make("404", context));
		}
		
		Post post = null;
		try {
			post = model.getPostManager().getById(postId);
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
		Map<String, String> params = session.getParms();
		
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("hasTitle", View::sanitizeStrict, s -> s.length() > 0, "title")
			.validateSingle("hasContent", View::sanitizePostContent, s -> s.length() > 0, "content")
			.validateSingle("hasContact", View::sanitizeStrict, s -> s.length() > 0, "contact");
		
		if(!validator.validate(session.getParms())) {
			context.setValidator(validator);
			return new Response(View.make("post.new", context));
		}
		
		String filename = params.get("image");
		if(filename != null) {
			String filepath = files.get("image");
			File input = new File(filepath);
			String uploadfolder = (String) App.config.get("litepost.public.uploadfolder");
			File output = new File(uploadfolder + File.separatorChar + filename);
			if(input.exists()) {
				try {
					Files.copy(input, output);
				} catch (IOException e) {
					return Router.error(e, context);
				}
			}
		}
		
		try {
			String title = validator.value("username");
			String content = validator.value("content");
			String contact = validator.value("contact");
			model.getPostManager().insert(title, content, contact);
			ResultSet rs = model.getQueryManager().executeQuery("getLastId", "Posts"); 
			model.getPostManager().addImage(Router.linkTo("upload", filename), rs.getInt(1));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("allPosts");
	}
	
	public static Response commentPost(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Map<String, String> params = session.getParms();
		
		String string_post_id = params.get("post_id");
		String string_parent_id = params.getOrDefault("parent_id", "0");
		String content = View.sanitizeStrict(params.getOrDefault("content", ""));
		if(content.equals("")) {
			return Router.redirectTo("singlePost", string_post_id);
		}
		
		int post_id = Integer.parseInt(string_post_id);
		int parent_id = Integer.parseInt(string_parent_id);
		
		try {
			model.getCommentManager().insert(content, parent_id, post_id);
		} catch (SQLException  e) {
			return Router.error(e, context);
		}
		
		return Router.redirectTo("singlePost", string_post_id);
	}
}
