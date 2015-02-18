package org.pi.litepost.html;


public class Input extends Tag{
	Validator validator = null;
	Input(String type, Validator validator) {
		super("input");
		attr("type", type);
		this.validator = validator;
	}
	
	public Input attr(String name) {
		return attr(name, null);
	}
	
	public Input attr(String name, String value) {
		attributes.put(name, value);
		return this;
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
	
	public Input id(String value) {
		return attr("id", value);
	}
	
	public Input _class(String value) {
		return attr("class", value);
	}
	
	public Input name(String value) {
		return attr("name", value);
	}
	
	public Input placeholder(String value) {
		return attr("placeholder", value);
	}
	
	public Input value(String value) {
		return attr("value", value);
	}
	
	public Input checked() {
		return attr("checked");
	}
}
