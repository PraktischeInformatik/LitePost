package org.pi.litepost;

import java.util.HashMap;
import java.util.Map;

import org.pi.litepost.applicationLogic.Model;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface Handler {
	public Response handle(IHTTPSession session, Map<String, String> args, Map<String, String> files, HashMap<String, Object> data, Model model);
}
