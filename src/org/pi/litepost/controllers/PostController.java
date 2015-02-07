package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.applicationLogic.Post;
import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class PostController {
	public static Response getAll(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		ArrayList<Post> posts = new ArrayList<>();
		try {
			posts = model.getPostManager().getAll();
		} catch (DatabaseCriticalErrorException | SQLException e) {
			return Router.error(e);
		}
		data.put("posts", posts);
		return new Response(View.make("post.all", data));
	} 
	
	public static Response getSingle(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) { 
		int postId = 0;
		try {
			postId = Integer.parseInt(args.getOrDefault("post_id", ""));
		}catch(NumberFormatException e) {
			return new Response(Status.NOT_FOUND, "text/html", View.make("404", data));
		}
		
		Post post = null;
		try {
			post = model.getPostManager().getById(postId);
		} catch (DatabaseCriticalErrorException | SQLException e) {
			return Router.error(e);
		}
		data.put("post", post);
		return new Response(View.make("post.single", data));
	}
	
	public static Response getNew(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		data.put("title", "");
		data.put("content", "");
		data.put("contact", "");
		data.put("error", false);
		return new Response(View.make("post.new", data));
	}
	
	public static Response postNew(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		
		String title = View.sanitizeStrict(params.getOrDefault("title", ""));
		String content = View.sanitizePostContent(params.getOrDefault("content", ""));
		String contact = View.sanitizeStrict(params.getOrDefault("contact", ""));
		if(title.equals("") || content.equals("") || contact.equals("")) {
			data.put("title", title);
			data.put("content", content);
			data.put("contact", contact);
			data.put("error", true);
			return new Response(View.make("post.new", data));
		}
		
		try {
			model.getPostManager().insert(title, content, contact);
		} catch (DatabaseCriticalErrorException | SQLException e) {
			Router.error(e);
		}
		return Router.redirectTo("allPosts");
	}
	
	public static Response commentPost(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
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
		} catch (SQLException | DatabaseCriticalErrorException e) {
			return Router.error(e);
		}
		
		return Router.redirectTo("singlePost", string_post_id);
	}
}
