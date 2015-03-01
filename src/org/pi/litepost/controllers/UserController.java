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
import org.pi.litepost.applicationLogic.Message;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.applicationLogic.User;
import org.pi.litepost.html.Validator;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class UserController {
	
 	public static Response getProfile(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		User user = null;
		try {
			user = model.getUserManager().getCurrent();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		if(user == null) {
			try {
				String redirect = session.getUri();
				redirect = URLEncoder.encode(redirect, "UTF-8");
				return Router.redirectTo("loginPageRedirect", redirect);
			} catch (UnsupportedEncodingException e) {
				return Router.error(e, context);
			}
		}
		context.put("user", user);
		return new Response(View.make("user.profile", context));
	}
	
	public static Response getMessages(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		User user = null;
		try {
			user = model.getUserManager().getCurrent();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		if(user == null) {
			try {
				String redirect = session.getUri();
				redirect = URLEncoder.encode(redirect, "UTF-8");
				return Router.redirectTo("loginPageRedirect", redirect);
			} catch (UnsupportedEncodingException e) {
				return Router.error(e, context);
			}
		}
		
		try {
			ArrayList<Message> sentMessages = model.getMessageManager().getFromUser(user);
			ArrayList<Message> receivedMessages = model.getMessageManager().getToUser(user);
			context.put("receivedMessages", receivedMessages);
			context.put("sentMessages", sentMessages);
			return new Response(View.make("user.messages", context));
		} catch (SQLException e) {
			return Router.error(e, context);
		}
	}
	
	public static Response getSendMessage(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		User user = null;
		try {
			user = model.getUserManager().getCurrent();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		if(user == null) {
			try {
				String redirect = session.getUri();
				redirect = URLEncoder.encode(redirect, "UTF-8");
				return Router.redirectTo("loginPageRedirect", redirect);
			} catch (UnsupportedEncodingException e) {
				return Router.error(e, context);
			}
		}
		return new Response(View.make("user.newmessage", context));
	}
	
	public static Response postSendMessages(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
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
}
