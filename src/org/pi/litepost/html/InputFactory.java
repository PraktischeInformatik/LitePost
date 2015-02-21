package org.pi.litepost.html;

public class InputFactory {
	Validator validator;
	
	public InputFactory(Validator validator) {
		this.validator = validator;
	}
	
	public Input get(String type) {
		return new Input(type, validator);
	}
	
	public String getCsrfToken() {
		if(validator != null) {
			return validator.csrfToken().toString();
		} else {
			return "";
		}
	}
	
	public boolean error(String name) {
		if(validator != null) {
			return !validator.valid(name);
		} else {
			return false;
		}
	}
}
