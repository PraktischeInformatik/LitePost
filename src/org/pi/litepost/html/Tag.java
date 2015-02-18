package org.pi.litepost.html;

import java.util.HashMap;
import java.util.Map;

public class Tag {
	private String name;
	public Map<String, String> attributes = new HashMap<>();;

	Tag(String name) {
		this.name = name;
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
		
		return sb.append(">").toString(); 
	}
	
	public void addAttribute(String name) {
		addAttribute(name, null);
	}
	
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
}
