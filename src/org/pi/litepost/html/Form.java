package org.pi.litepost.html;


public class Form extends Tag{
	Form(String method, String action) {
		super("form");
		attr("method", method);
		attr("action", action);
	}
	
	public Form attr(String name) {
		return attr(name, null);
	}
	
	public Form attr(String name, String value) {
		attributes.put(name, value);
		return this;
	}
	
	public Form id(String value) {
		return attr("id", value);
	}
	
	public Form _class(String value) {
		return attr("class", value);
	}
	
}