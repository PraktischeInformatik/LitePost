package org.pi.litepost.html;

import java.util.ArrayList;


public class Input extends Tag<Input>{
	Validator validator = null;
	Input(String type, Validator validator) {
		super(false);
		attr("type", type);
		this.validator = validator;
	}
	
	public Input prepopulate() {
		if(validator != null && attributes.containsKey("name")) {
			String value = validator.value(attributes.get("name"));
			if(attributes.get("type").equalsIgnoreCase("checkbox")) {
				if(value.equals("true")) checked();
			}else {
				value(value);	
			}
		}
		return this;
	}
	
	public Input name(String value) {
		return (Input) attr("name", value);
	}
	
	public Input placeholder(String value) {
		return (Input) attr("placeholder", value);
	}
	
	public Input value(String value) {
		return (Input) attr("value", value);
	}
	
	public Input checked() {
		return (Input) attr("checked");
	}
}