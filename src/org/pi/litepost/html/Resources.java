package org.pi.litepost.html;

import java.util.ArrayList;

import org.pi.litepost.Router;

public class Resources {
	private ArrayList<String> scriptsHeader = new ArrayList<>();
	private ArrayList<String> scriptsFooter = new ArrayList<>();
	private ArrayList<String> styles = new ArrayList<>();
	
	public Resources() {
		addStyle("css/style.css");
		addScriptFooter("js/script.js");
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
