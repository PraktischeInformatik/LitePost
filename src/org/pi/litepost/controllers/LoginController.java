package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.exceptions.LoginFailedException;
import org.pi.litepost.exceptions.PasswordResetException;
import org.pi.litepost.exceptions.UserEmailNotVerifiedException;
import org.pi.litepost.exceptions.UseranameExistsException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoginController {
	public static Response getLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.login", data));
	}
	
	public static Response postLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		String csrfToken = params.getOrDefault("csrf_token", "");
		String username = params.getOrDefault("username", "");
		String password = params.getOrDefault("password", "");
		boolean remember = params.containsKey("remember");
		boolean loginFailed = false;
		if(!model.getSessionManager().validateToken(csrfToken)) {
			loginFailed = true;
			data.put("csrfValidationFailed", true);	
		}
		if(!loginFailed) {
			try {			
				model.getUserManager().login(username, password, remember);
			} catch (LoginFailedException e) {
				loginFailed = true;
			} catch (UserEmailNotVerifiedException e) {
				loginFailed = true;
				data.put("userEmailNotVerified", true);
			} catch (Exception e) {
				return Router.error(e, data);
			}
		}
		if(loginFailed) {
			data.put("loginFailed", true);
			data.put("username", username);
			data.put("remember", remember);
			return new Response(View.make("user.login", data));
		}else {
			return Router.redirectTo("profile");	
		}
	}
	
	public static Response getRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.registration", data));
	}
	
	public static Response postRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		String csrfToken = params.getOrDefault("csrf_token", "");
		String username = params.getOrDefault("username", "");
		String password = params.getOrDefault("password", "");
		String pwconfirm = params.getOrDefault("pwconfirm", "");
		String firstname = params.getOrDefault("firstname", "");
		String lastname = params.getOrDefault("lastname", "");
		String email = params.getOrDefault("email", "");
		boolean registrationFailed = false;
		if(!model.getSessionManager().validateToken(csrfToken)) {
			registrationFailed = true;
			data.put("csrfValidationFailed", true);	
		}
		if(password.equals("") || !password.equals(pwconfirm)) {
			registrationFailed = true;
			data.put("passwordMismatch", true);
		}
		if(!registrationFailed) {
			try {
				model.getUserManager().register(username, password, firstname, lastname, email);
			} catch(UseranameExistsException e) {
				registrationFailed = true;
			} catch(Exception e) {
				return Router.error(e, data);
			}
		}
		if(registrationFailed) {
			data.put("registrationFailed", true);
			data.put("username", username);
			data.put("firstname", firstname);
			data.put("lastname", lastname);
			data.put("email", email);
			return new Response(View.make("user.registration", data));
		} else {
			return Router.redirectTo("loginPage");
		}
	}
	
	public static Response verifyEmail(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		String token = args.get("verification_token");
		boolean verified = false;
		try {
			verified = model.getUserManager().verifyEmail(token);
		} catch (SQLException e) {
			return Router.error(e, data);
		}
		if(!verified) {
			return new Response(View.make("user.verificationfailed", data));
		}
		return Router.redirectTo("allPosts");
	}
	
	public static Response getLostPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.lostpassword", data));
	}
	
	public static Response postLostPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();

		String csrfToken = params.getOrDefault("csrf_token", "");
		if(!model.getSessionManager().validateToken(csrfToken)) {
			//TODO bessere fehlerbehandlung
			return Router.redirectTo("lostPasswordPage");	
		}
		
		String email = params.getOrDefault("email", "");
		if(email.equals("")) {
			return new Response(View.make("user.lostpassword", data));
		}
		try {
			model.getUserManager().sendResetPassword(email);
		} catch (Exception e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("login");
	}
	
	public static Response getResetPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		String token = args.get("reset_token");
		data.put("resetToken", token);
		return new Response(View.make("user.resetpassword", data));
	}
	
	public static Response postResetPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		Map<String, String> params = session.getParms();
		
		String csrfToken = params.getOrDefault("csrf_token", "");
		if(!model.getSessionManager().validateToken(csrfToken)) {
			//TODO bessere fehlerbehandlung
			return Router.redirectTo("resetPasswordPage");	
		}
		
		String password = params.getOrDefault("password", "");
		String pwconfirm = params.getOrDefault("pwconfirm", "");
		String token = args.get("reset_token");
		if(password.equals("") || !password.equals(pwconfirm)) {
			data.put("resetToken", token);
			data.put("passwordMismatch", true);
			return new Response(View.make("user.resetpassword", data));	
		}
		try {
			model.getUserManager().resetPassword(password, token);
		}catch(PasswordResetException e){
			return new Response(View.make("user.resetpasswordfailed", data));
		} catch (Exception e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("loginPage");
	}
	
	public static Response logout(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		try {
			model.getUserManager().logout();
		} catch (SQLException e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("posts");
	}
}
