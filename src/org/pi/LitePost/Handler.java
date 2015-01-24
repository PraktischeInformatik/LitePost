package org.pi.LitePost;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Handler {
	public Response handle(IHTTPSession session, Map<String, String> args, HashMap<String, Object> data);
}
