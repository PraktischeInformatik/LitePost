package org.pi.litepost.html;


public class Form extends Tag<Form>{
	Form(String method, String action) {
		super(false);
		attr("method", method);
		attr("action", action);
	}
}