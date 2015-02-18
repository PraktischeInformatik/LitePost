package org.pi.litepost.html;


public class FormFactory {
	Validator validator;
	
	public FormFactory(Validator validator) {
		this.validator = validator;
	}
	
	public Form begin(String method, String action) {
		return new Form(method, action);
	}
	
	public String end() {
		return "</form>";
	}
	
	public Input input(String type) {
		return new Input(type, validator);
	}
	
	public String csrfToken() {
		if(validator != null) {
			return validator.csrfToken().toString();
		} else {
			return "";
		}
	}
}
