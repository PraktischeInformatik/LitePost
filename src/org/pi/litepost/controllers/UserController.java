package org.pi.litepost.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Comment;
import org.pi.litepost.applicationLogic.Message;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.applicationLogic.Post;
import org.pi.litepost.applicationLogic.User;
import org.pi.litepost.exceptions.ForbiddenOperationException;
import org.pi.litepost.html.Validator;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class UserController {
	
	public static Response mustLogin(IHTTPSession session, Model model, ViewContext context) {
		User user = null;
		try {
			user = model.getUserManager().getCurrent();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		if(user == null) {
			try {
				String redirect = session.getUri() + '?' + session.getQueryParameterString();
				redirect = URLEncoder.encode(redirect, "UTF-8");
				return Router.redirectTo("loginPageRedirect", redirect);
			} catch (UnsupportedEncodingException e) {
				return Router.error(e, context);
			}
		}
		return null;
	}
	
 	public static Response getProfile(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		//current user is already in ViewContext by default
 		//no need to put him there again
 		//just render the view
 		return new Response(View.make("user.profile", context));
	}
	
 	public static Response getMessages(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}

		try {
			User user = model.getUserManager().getCurrent();
			ArrayList<Message> sentMessages = model.getMessageManager().getFromUser(user);
			ArrayList<Message> receivedMessages = model.getMessageManager().getToUser(user);
			context.put("receivedMessages", receivedMessages);
			context.put("sentMessages", sentMessages);
			return new Response(View.make("user.messages", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	}
 	public static Response myPosts(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
 		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		try {
			ArrayList<Post> posts = model.getPostManager().getByUser();
			context.put("posts", posts);
			return new Response(View.make("user.myposts", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
 	}
 	
 	public static Response myComments(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		try {
			ArrayList<Comment> comments = model.getCommentManager().getByUser();
			context.put("comments", comments);
			return new Response(View.make("user.mycomments", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	}
	
	public static Response getSendMessage(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}

		return new Response(View.make("user.newmessage", context));
	}
	
	public static Response postSendMessages(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}
		
		Predicate<String> receiverExists = s -> {
			try {
				return model.getUserManager().getByName(s) != null;
			}catch(SQLException e) {
				return false;
			}
		};
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("receiverExists", receiverExists, "receiver")
			.validateExists("hasSubject", "subject")
			.validateExists("hasContent", "content");
	
		if(!validator.validate(session.getParms())) {
			context.setValidator(validator);
			return new Response(View.make("user.newmessage", context));
		}
		
		try {
			String subject = validator.value("subject");
			String content = validator.value("content");
			User sender = model.getUserManager().getCurrent();
			User receiver = model.getUserManager().getByName(validator.value("receiver"));
			
			if(sender == null) {
				try {
					String redirect = session.getUri();
					redirect = URLEncoder.encode(redirect, "UTF-8");
					return Router.redirectTo("loginPageRedirect", redirect);
				} catch (UnsupportedEncodingException e) {
					return Router.error(e, context);
				}
			}
			
			model.getMessageManager().insert(sender, receiver, subject, content);
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("messagesPage");
	}
	
	public static Response readMessage(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}

		int message_id = Integer.parseInt(args.get("message_id"));
		try {
			model.getMessageManager().readMessage(message_id);
			Message msg = model.getMessageManager().getById(message_id);
			context.put("message", msg);
			return new Response(View.make("user.message", context));
		}catch(SQLException e) {
			return Router.error(e, context);
		}
	}
	
	public static Response deleteMessage(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}

		int message_id = Integer.parseInt(args.get("message_id"));
		try {			
			model.getMessageManager().deleteMessage(message_id);
			return Router.redirectTo("messagesPage");
		}catch(SQLException e) {
			return Router.error(e, context);
		} catch (ForbiddenOperationException e) {
			return Router.forbidden(context);
		}
	}
	
	public static Response deleteUser(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Response r;
 		if((r = mustLogin(session, model, context)) != null) {
 			return r;
 		}
 		boolean redirectToAdmin = false;
		try {
			User user = model.getUserManager().getCurrent();
			int userId = Integer.parseInt(args.get("user_id"));
			if(user.isAdmin() && user.getUserId() != userId) {
				redirectToAdmin = true; 
			} 
			model.getUserManager().delete(userId);
		} catch (SQLException e) {
			return Router.error(e, context);
		} catch (ForbiddenOperationException e) {
			return Router.forbidden(context);
		}
		if(redirectToAdmin) {
			return Router.redirectTo("adminUsers");
		} else {
			return Router.redirectTo("home");
		}
	}
}