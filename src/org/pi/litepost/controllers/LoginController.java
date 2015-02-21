package org.pi.litepost.controllers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.AddressException;

import org.pi.litepost.Router;
import org.pi.litepost.Validator;
import org.pi.litepost.View;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.exceptions.EmailExistsException;
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
		
		Validator validator = new Validator()
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("hasUsername", s -> !s.isEmpty() && View.sanitizeStrict(s).equals(s), "username")
			.validateExists("hasPassword", "password")
			.validateFlag("rememberPassword", "remember")
			.manual("emailVerified").manual("loginSuccessful");
		
		if(!validator.validate(session.getParms())) {
			data.put("Validation", validator);
			return new Response(View.make("user.login", data));
		}
		
		
		try {			
			boolean remember = validator.flag("remember");
			String username = validator.value("usernmae");
			String password = validator.value("password");
			model.getUserManager().login(username, password, remember);
		} catch (LoginFailedException | UserEmailNotVerifiedException e) {
			validator.manual("emailVerified",
					!(e instanceof UserEmailNotVerifiedException));
			validator.manual("loginSuccessful",
					!(e instanceof LoginFailedException));
			data.put("Validation", validator);
			return new Response(View.make("user.login", data));
		} catch (Exception e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("profile");
	}
	
	public static Response getRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		return new Response(View.make("user.registration", data));
	}
	
	public static Response postRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model) {
		
		Validator validator = new Validator()
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateExists("hasUsername", "username")
			.validateExists("hasFirstname", "firstname")
			.validateExists("hasLastname", "lastname")
			.validateSingle("validEmail", model.getMailManager()::validEmail, "email")
			.validateSingle("passwordMinLength", s -> s.length() > 4, "password")
			.validateMultiple("passwordsMatch", ss -> ss[0].equals(ss[1]), "password", "pwconfirm")
			.manual("usernameAvailable").manual("emailAvailable");
		
		if(!validator.validate(session.getParms())) {
			data.put("Validation", validator);
			return new Response(View.make("user.registration", data));
		}

		try {
			model.getUserManager().register(
					validator.value("username"),
					validator.value("password"),
					validator.value("firstname"),
					validator.value("lastname"),
					validator.value("email"));
		} catch(UseranameExistsException | EmailExistsException e) {
			validator.manual("usernameAvailable", 
					!(e instanceof UseranameExistsException));
			validator.manual("emailAvailable", 
					!(e instanceof EmailExistsException));
			data.put("Validation", validator);
			return new Response(View.make("user.registration", data));
		} catch(Exception e) {
			return Router.error(e, data);
		}
		return Router.redirectTo("loginPage");
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
