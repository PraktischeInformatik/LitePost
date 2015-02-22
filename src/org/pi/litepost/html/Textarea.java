package org.pi.litepost.html;

public class Textarea extends Input {
	Textarea(Validator validator) {
		super("", validator);
		this.closeTag = true;
	}	
}