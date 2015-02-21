package org.pi.litepost.html;


public class FormFactory {
	
	public FormFactory() {}
	
	public Form begin(String method, String action) {
		return new Form(method, action);
	}
	
	public String end() {
		return "</form>";
	}
}
