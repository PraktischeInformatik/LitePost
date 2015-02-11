package org.pi.litepost;

import java.util.ArrayList;

import org.pi.litepost.applicationLogic.SessionManager;

public class HtmlResources {
	private ArrayList<String> scriptsHeader = new ArrayList<>();
	private ArrayList<String> scriptsFooter = new ArrayList<>();
	private ArrayList<String> styles = new ArrayList<>();
	private SessionManager sessionManager;
	
	public HtmlResources(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
		addStyle("css/style.css");
	}
	
	public void addStyle(String uri) {	
		styles.add(Router.linkTo("public", uri));
	}
	
	public void addScriptHeader(String uri) {	
		scriptsHeader.add(Router.linkTo("public", uri));
	}
	
	public void addScriptFooter(String uri) {	
		scriptsFooter.add(Router.linkTo("public", uri));
	}
	
	public String csrfToken() {
		String format = "<input type=\"hidden\" class=\"csrf-token\" name=\"csrf_token\" value=\"%s\">";
		return String.format(format, sessionManager.csrfToken());
	}
	
	public ArrayList<String> getStyles() {
		return styles;
	}
	
	public ArrayList<String> getScriptsHeader() {
		return scriptsHeader;
	}
	
	public ArrayList<String> getScriptsFooter() {
		return scriptsFooter;
	}
}
