package org.pi.litepost.html;

public class Textarea extends Input {
	Textarea(Validator validator) {
		super("", validator);
		this.closeTag = true;
	}
	public Input prepopulate() {
		if(validator != null && attributes.containsKey("name")) {
			this.content = validator.value(attributes.get("name"));
		}
		return this;
	}
}