package org.pi.litepost;

import java.util.HashMap;

import org.pi.litepost.html.Validator;

public class ViewContext extends HashMap<String, Object> {

	private static final long serialVersionUID = -3995592594852053341L;
	private Validator validator;
	
	public ViewContext() {
		super();
	}
	
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	
	public Validator validator() {
		return validator;
	}
}
