package org.pi.litepost;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Error {
	private final Throwable t;
	
	public Error(Throwable t) {
		this.t = t;
	}
	
	public String getMessage() {
		return t.getMessage();
	}
	
	public String getClassName() {
		return t.getClass().getName();
	}
	
	public StackTraceElement[] getStackTrace() {
		return t.getStackTrace();
	}
	
	public Error getCause() {
		return new Error(t.getCause());
	}
	
	public String getSource(StackTraceElement ste) {
		Class<?> cl;
		try {
			cl = Class.forName(ste.getClassName());
		} catch (ClassNotFoundException e1) {
			return "";
		}
		String path = "src" + File.separatorChar + cl.getPackage().getName().replace('.', File.separatorChar);
		String filename = path + File.separatorChar + ste.getFileName();
		try {	
			return String.join("\n", Files.readAllLines(new File(filename).toPath()));
		} catch (IOException e) {
			return "";
		}
	}
}
