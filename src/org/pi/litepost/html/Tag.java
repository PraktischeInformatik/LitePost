package org.pi.litepost.html;

import java.util.HashMap;
import java.util.Map;

public abstract class Tag<T extends Tag<?>> {
	protected String name;
	protected Map<String, String> attributes = new HashMap<>();
	protected String content;
	protected boolean closeTag; 
	
	Tag(boolean closeTag) {
		this.name = getClass().getSimpleName().toLowerCase();
		this.closeTag = closeTag;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<");
		sb.append(name);
		for(String attr : attributes.keySet()) {
			sb.append(" ")
			  .append(attr);
			String value = attributes.get(attr);
			if(value != null) {
				sb.append("=\"")
				  .append(value)
				  .append("\"");
			}
		}
		sb.append(">");
		
		if(content != null) {
			sb.append(content);
		}
		if(closeTag) {
			sb.append("</")
			  .append(name)
			  .append(">");
		}
		
		return sb.toString(); 
	}
	

	public Tag<T> attr(String name) {
		return attr(name, null);
	}
	
	public Tag<T> attr(String name, String value) {
		attributes.put(name, value);
		return this;
	}
	
	public Tag<T> id(String value) {
		return attr("id", value);
	}
	
	public Tag<T> get(String value) {
		
		return class_(value);
	}
	
	public Tag<T> class_(String value) {
		if(!attributes.containsKey("class")) {
			attr("class", value);
		}else {
			attr("class", attributes.get("class") + " " + value);
		}
		return this;
	}
	
	public Tag<T> style(String style) {
		return attr("style", style);
	}
	
	public Tag<T> data(String name, String value) {
		return attr("data-" + name, value);
	}
	
	public Tag<T> content(String content) {
		this.content = content;
		return this;
	}
}
