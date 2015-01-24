package org.pi.LitePost;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD{

	public App(int port) {
		super(port);
	}
	
	@Override public Response serve(IHTTPSession session) {
		String html = "<html>"
				+ "<head>"
				+ "<title>Hello</title>"
				+ "</head>"
				+ "<body>"
				+ "<h2>Hello, World!</h2>"
				+ "</body>"
				+ "</html>";
		return new Response(html);
	}

	public static void main(String[] args) {
		App app = new App(8080);
		try {
			app.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		System.out.println("Server startet. Hit Enter to stop.");
		
		try {
			System.in.read();
		} catch (Throwable ignored) {}
		
		app.stop();
		System.out.println("Server stopped.");
	}
}
