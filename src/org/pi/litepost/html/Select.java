package org.pi.litepost.html;

import java.util.ArrayList;

public class Select extends Input {
	ArrayList<Option> options = new ArrayList<>();
	Select(Validator validator) {
		super("", validator);
		this.closeTag = true;
	}
	
	public Select addOption(String value, String content) {
		this.options.add(new Option(value, content));
		return this;
	}
	
	public Select addTimes() {
		for(int hour = 0; hour < 24; hour++) {
			for(int minute = 0; minute <= 45; minute += 15) {
				String value = String.format("%02d%02d", hour, minute);
				String content = String.format("%02d:%02d", hour, minute);
				addOption(value, content);
			}
		}
		return this;
	}
	
	@Override
	public String toString() {
		this.content = options.stream()
				.map(Option::toString)
				.reduce((a, s) -> a + s)
				.orElse("");
		return super.toString();
	}
	
	class Option extends Tag<Option> {
		Option(String value, String content) {
			super(true);
			attr("value", value);
			this.content = content;
		}
	}
}