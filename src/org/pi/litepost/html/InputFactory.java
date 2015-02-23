package org.pi.litepost.html;

public class InputFactory {
	Validator validator;
	
	public InputFactory(Validator validator) {
		this.validator = validator;
	}
	
	public Select getSelect() {
		return new Select(validator);
	}
	
	public Textarea getTextarea() {
		return new Textarea(validator);
	}
	
	public Input getSubmit() {
		Input input = new Input("submit", validator);
		input.class_("button");
		return input;
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
	
	public String value(String name) {
		if(validator != null) {
			return validator.value(name);
		} else {
			return "";
		} 
	}
}
