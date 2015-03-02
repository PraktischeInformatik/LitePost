package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Comment;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.applicationLogic.Post;
import org.pi.litepost.applicationLogic.User;
import org.pi.litepost.exceptions.ForbiddenOperationException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class AdminController {

	public static Response getPosts(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
 		Response r;
 		if((r = UserController.mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		
 		Map<String, String> parms = session.getParms();
 		try {
			if(parms.containsKey("user_id") && parms.get("user_id").matches("[0-9]+")) {
				int userId = Integer.parseInt(parms.get("user_id"));
				ArrayList<Post> posts = model.getPostManager().getByUser(userId);
				context.put("posts", posts);
			}else if(parms.containsKey("reported")){
				ArrayList<Post> posts = model.getPostManager().getReports();
				context.put("posts", posts);
	 		} else {
				ArrayList<Post> posts = model.getPostManager().getAll();
				context.put("posts", posts);
	 		}
			ArrayList<User> users = model.getUserManager().getAll();
			context.put("users", users);
			return new Response(View.make("admin.posts", context));
		} catch (ForbiddenOperationException e) {
			return Router.forbidden(context);
		}catch (SQLException e) {
			return Router.error(e, context);
		}
 	}
	
	public static Response getComments(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
 		Response r;
 		if((r = UserController.mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		
 		Map<String, String> parms = session.getParms();
 		try {
 			ArrayList<Comment> comments;
			if(parms.containsKey("user_id") && parms.get("user_id").matches("[0-9]+")) {
				int userId = Integer.parseInt(parms.get("user_id"));
				comments = model.getCommentManager().getByUser(userId);
			}else if(parms.containsKey("reported")){
				comments = model.getCommentManager().getReports();
	 		} else {
				comments = model.getCommentManager().getAll();
	 		}
			context.put("comments", comments);
			ArrayList<User> users = model.getUserManager().getAll();
			context.put("users", users);
			return new Response(View.make("admin.comments", context));
		} catch (ForbiddenOperationException e) {
			return Router.forbidden(context);
		}catch (SQLException e) {
			return Router.error(e, context);
		}
 	}
}
