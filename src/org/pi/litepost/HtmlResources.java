package org.pi.litepost;

import java.util.ArrayList;

public class HtmlResources {
	private ArrayList<String> scriptsHeader = new ArrayList<>();
	private ArrayList<String> scriptsFooter = new ArrayList<>();
	private ArrayList<String> styles = new ArrayList<>();
	
	public HtmlResources() {
		addStyle("/public/css/style.css");
	}
	
	public void addStyle(String uri) {	
		styles.add(uri);
	}
	
	public void addScriptHeader(String uri) {	
		scriptsHeader.add(uri);
	}
	
	public void addScriptFooter(String uri) {	
		scriptsFooter.add(uri);
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
