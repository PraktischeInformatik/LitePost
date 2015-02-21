package org.pi.litepost;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.owasp.html.Handler;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.HtmlSanitizer;
import org.owasp.html.HtmlStreamRenderer;
import org.owasp.html.PolicyFactory;
import org.pi.litepost.html.FormFactory;
import org.pi.litepost.html.InputFactory;
import org.pi.litepost.html.Resources;

import com.google.common.base.Throwables;

public class View {
	private static PolicyFactory POST_CONTENT_POLICY;
	private static PolicyFactory STRICT_POLICY;
	static {
		POST_CONTENT_POLICY = new HtmlPolicyBuilder()
			.allowElements("h2", "p", "b", "u", "i", "a")
			.allowUrlProtocols("https")
			.allowAttributes("href").onElements("a")
			.requireRelNofollowOnLinks().toFactory();
		
		STRICT_POLICY = new HtmlPolicyBuilder().toFactory();
	}
	
	public static String make(String template, HashMap<String, Object> data) {
		VelocityContext context = new VelocityContext();
		importContext(context, data);
		StringWriter writer = new StringWriter();
		getTemplate(template).merge(context, writer);
		return writer.toString();
	}
	
	public static void importContext(VelocityContext context, HashMap<String, Object> data) {
		for(String key : data.keySet()) {
			context.put(key, data.get(key));
		}
		
		//load defaults
		if(data instanceof ViewContext) {
			ViewContext viewContext = (ViewContext) data;
			context.put("Input", new InputFactory(viewContext.validator()));
		} else {
			context.put("Input", new InputFactory(null));
		}
		context.put("Form", new FormFactory());
		context.put("Resources", new Resources());
		context.put("Router", Router.class);
	}
	
	private static Template getTemplate(String templateName) {
		String path = File.separatorChar + templateName.replace('.', File.separatorChar) + ".vm"; 
		return Velocity.getTemplate(path, "utf-8");
	}
	
	private static HtmlStreamRenderer getRenderer(StringBuilder sb) {
		return HtmlStreamRenderer.create(
		        sb,
		        // Receives notifications on a failure to write to the output.
		        new Handler<IOException>() {
		          public void handle(IOException ex) {
		            Throwables.propagate(ex);  // System.out suppresses IOExceptions
		          }
		        },
		        // Our HTML parser is very lenient, but this receives notifications on
		        // truly bizarre inputs.
		        new Handler<String>() {
		          public void handle(String x) {
		            throw new AssertionError(x);
		          }
		        });
	}
	
	public static String sanitizePostContent(String string) {
		StringBuilder sb = new StringBuilder();
		HtmlSanitizer.sanitize(string, POST_CONTENT_POLICY.apply(getRenderer(sb)));
		return sb.toString();
	}
	
	public static String sanitizeStrict(String string) {
		StringBuilder sb = new StringBuilder();
		HtmlSanitizer.sanitize(string, STRICT_POLICY.apply(getRenderer(sb)));
		return sb.toString();
	}
	
}
