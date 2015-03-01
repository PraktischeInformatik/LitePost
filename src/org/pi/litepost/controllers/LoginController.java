package org.pi.litepost.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Map;

import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.exceptions.EmailExistsException;
import org.pi.litepost.exceptions.LoginFailedException;
import org.pi.litepost.exceptions.PasswordResetException;
import org.pi.litepost.exceptions.UserEmailNotVerifiedException;
import org.pi.litepost.exceptions.UseranameExistsException;
import org.pi.litepost.html.Validator;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoginController {
	public static Response getLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		if(args.containsKey("redirect")) {
			context.put("redirect", args.get("redirect"));
		}
		return new Response(View.make("user.login", context));
	}
	
	public static Response postLogin(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("hasUsername", s -> !s.isEmpty() && View.sanitizeStrict(s).equals(s), "username")
			.validateExists("hasPassword", "password")
			.validateFlag("rememberPassword", "remember")
			.manual("emailVerified").manual("loginSuccessful");
		
		if(!validator.validate(session.getParms())) {
			context.setValidator(validator);
			return new Response(View.make("user.login", context));
		}
		
		try {			
			boolean remember = validator.flag("remember");
			String username = validator.value("username");
			String password = validator.value("password");
			model.getUserManager().login(username, password, remember);
		} catch (LoginFailedException | UserEmailNotVerifiedException e) {
			validator.manual("emailVerified",
					!(e instanceof UserEmailNotVerifiedException));
			validator.manual("loginSuccessful",
					!(e instanceof LoginFailedException));
			context.setValidator(validator);
			return new Response(View.make("user.login", context));
		} catch (Exception e) {
			return Router.error(e, context);
		}
		if(args.containsKey("redirect")) {
			try {
				String url = URLDecoder.decode(args.get("redirect"), "UTF-8");
				return Router.redirectTo(url);
			} catch (UnsupportedEncodingException e) {
				return Router.error(e, context);
			}
		}
		return Router.redirectTo("profilePage");
	}
	
	public static Response getRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		return new Response(View.make("user.registration", context));
	}
	
	public static Response postRegistration(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateExists("hasUsername", "username")
			.validateExists("hasFirstname", "firstname")
			.validateExists("hasLastname", "lastname")
			.validateSingle("validEmail", model.getMailManager()::validEmail, "email")
			.validateSingle("passwordMinLength", s -> s.length() > 4, "password")
			.validateMultiple("passwordsMatch", ss -> ss[0].equals(ss[1]), "password", "pwconfirm")
			.manual("usernameAvailable").manual("emailAvailable");
		
		if(!validator.validate(session.getParms())) {
			context.setValidator(validator);
			return new Response(View.make("user.registration", context));
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
			context.setValidator(validator);
			return new Response(View.make("user.registration", context));
		} catch(Exception e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("loginPage");
	}
	
	public static Response verifyEmail(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		String token = args.get("verification_token");
		boolean verified = false;
		try {
			verified = model.getUserManager().verifyEmail(token);
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		if(!verified) {
			return new Response(View.make("user.verificationfailed", context));
		}
		return Router.redirectTo("allPosts");
	}
	
	public static Response getLostPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		return new Response(View.make("user.lostpassword", context));
	}
	
	public static Response postLostPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Map<String, String> params = session.getParms();

		String csrfToken = params.getOrDefault("csrf_token", "");
		if(!model.getSessionManager().validateToken(csrfToken)) {
			//TODO bessere fehlerbehandlung
			return Router.redirectTo("lostPasswordPage");	
		}
		
		String email = params.getOrDefault("email", "");
		if(email.equals("")) {
			return new Response(View.make("user.lostpassword", context));
		}
		try {
			model.getUserManager().sendResetPassword(email);
		} catch (Exception e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("login");
	}
	
	public static Response getResetPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		String token = args.get("reset_token");
		context.put("resetToken", token);
		return new Response(View.make("user.resetpassword", context));
	}
	
	public static Response postResetPassword(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
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
			context.put("resetToken", token);
			context.put("passwordMismatch", true);
			return new Response(View.make("user.resetpassword", context));	
		}
		try {
			model.getUserManager().resetPassword(password, token);
		}catch(PasswordResetException e){
			return new Response(View.make("user.resetpasswordfailed", context));
		} catch (Exception e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("loginPage");
	}
	
	public static Response logout(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		try {
			model.getUserManager().logout();
		} catch (SQLException e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("posts");
	}
}
