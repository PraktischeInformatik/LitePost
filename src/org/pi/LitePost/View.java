package org.pi.LitePost;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class View {
	public static String make(String template, HashMap<String, Object> data) {
		VelocityContext context = new VelocityContext();
		for(String key : data.keySet()) {
			context.put(key, data.get(key));
		}
		StringWriter writer = new StringWriter();
		 getTemplate(template).merge(context, writer);
		return writer.toString();
	}
	private static Template getTemplate(String templateName) {
		String path = "templates" + File.separatorChar + templateName.replace('.', File.separatorChar) + ".vm"; 
		return Velocity.getTemplate(path);
	}
}
