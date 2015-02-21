package org.pi.litepost.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.function.Predicate;

import org.pi.litepost.App;
import org.pi.litepost.Router;
import org.pi.litepost.View;
import org.pi.litepost.ViewContext;
import org.pi.litepost.applicationLogic.Model;
import org.pi.litepost.html.Validator;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class ConfigController {
	
	public static Response getSetup(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		context.put("serverhost", session.getHeaders().get("host"));
		return new Response(View.make("setup.firstrun", context));
	}
	
	public static Response postSetup(IHTTPSession session, Map<String, String> args, Map<String, String> files, ViewContext context, Model model) {
		Predicate<String[]> sslValidation = ss -> 
			ss[0].equalsIgnoreCase("true") ?
				!ss[1].isEmpty() && !ss[2].isEmpty() :
				true;
		
		Validator validator = new Validator(model.getSessionManager())
			.validateSingle("validCsrfToken", model.getSessionManager()::validateToken, "csrf_token")
			.validateSingle("validServerPort", s -> s.matches("[0-9]*"), "serverport")
			.validateSingle("validMailSystemaddress", model.getMailManager()::validEmail, "systemmail")
			.validateExists("hasPublicFolder", "publicfolder")
			.validateExists("hasUploadFolder", "uploadfolder")
			.validateMultiple("SSLNeedsUsernameAndPassword", sslValidation, "usessl", "mailusername", "mailpassword")
			.validateExists("hasMailHost", "mailhost")
			.validateExists("hasMailPort", "mailport");
		
		if(!validator.validate(session.getParms())) {
			context.setValidator(validator);
			return new Response(View.make("setup.firstrun", context));
		}
		App.config.put("litepost.serverport" , validator.value("serverport"));
		App.config.put("litepost.serverhost" , validator.value("serverhost"));
		App.config.put("litepost.public.folder" , validator.value("publicfolder"));
		App.config.put("litepost.public.uploadfolder" , validator.value("uploadfolder"));
		App.config.put("litepost.mail.usessl" , validator.value("usessl"));
		App.config.put("litepost.mail.username" , validator.value("mailusername"));
		App.config.put("litepost.mail.password" , validator.value("mailpassword"));
		App.config.put("litepost.mail.smtp.host" , validator.value("mailhost"));
		App.config.put("litepost.mail.smtp.port" , validator.value("mailport"));
		App.config.put("litepost.mail.systemmail" , validator.value("systemmail"));
		App.config.put("litepost.debug" , "false");
		App.config.put("litepost.configured" , "true");
		
		String generalFilePath = "res" + File.separatorChar + "config.properties";
		
		OutputStream out;
		try {
			out = new FileOutputStream(generalFilePath);
			App.config.store(out, null);
		} catch (IOException e) {
			return Router.error(e, context);
		}
		return Router.redirectTo("allPosts");
	}
}
