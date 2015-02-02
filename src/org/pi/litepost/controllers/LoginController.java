package org.pi.litepost.controllers;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoginController {
	public static Response getLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.login", data));
	}
	
	public static Response postLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		try {
			model.getUserManager().login(params.get("username"), params.get("password"));
		} catch (Exception e) {
			data.put("loginFailed", true);
			return new Response(View.make("user.login", data));
		}
		return Router.redirectTo("profile");
	}
}
