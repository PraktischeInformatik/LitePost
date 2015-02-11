package org.pi.litepost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;
import org.pi.litepost.exceptions.LoginFailedException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoginController {
	public static Response getLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.login", data));
	}
	
	public static Response postLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		String csrfToken = params.getOrDefault("csrf_token", "");
		if(!model.getSessionManager().validateToken(csrfToken)) {
			data.put("csrfValidationFailed", true);
			return new Response(View.make("user.login", data));
		}
		try {
			boolean remember = params.containsKey("remember");
			model.getUserManager().login(params.get("username"), params.get("password"), remember);
		} catch (LoginFailedException e) {
			data.put("loginFailed", true);
			return new Response(View.make("user.login", data));
		} catch (Exception e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("profile");
	}
	
	public static Response logout(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		try {
			model.getUserManager().logout();
		} catch (DatabaseCriticalErrorException e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("posts");
	}
}
