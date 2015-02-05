package org.pi.litepost.applicationLogic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

import fi.iki.elonen.NanoHTTPD.Cookie;
import fi.iki.elonen.NanoHTTPD.CookieHandler;

public class SessionManager extends Manager {

	private CookieHandler cookies;
	private String sessionId;
	
	public void resumeSession(CookieHandler cookieHandler) throws DatabaseCriticalErrorException {
		this.cookies = cookieHandler;
		this.sessionId = cookies.read("sessionId"); 
		if(sessionId != null) {
			extendSession(sessionId);
		}
		
	}
	
	public void cleanSessions() throws DatabaseCriticalErrorException, SQLException {
		ArrayList<String> expiredSessions = new ArrayList<>();
		ResultSet rs = model.getQueryManager().executeQuery("getAllSessions");
		while(rs.next()) {
			Date d = rs.getDate("expiration");
			if(d.before(Calendar.getInstance().getTime())) {
				expiredSessions.add(rs.getString("session_id"));
			}
		}
		for(String s: expiredSessions) {
			model.getQueryManager().executeQuery("removeSession", s);
		}
	}
	
	public void startSession() throws DatabaseCriticalErrorException {
		String newSessionId = createSessionid();
		extendSession(newSessionId);
		Cookie cookie = new Cookie("sessionId", sessionId, 30);
		LocalDateTime expiration = LocalDateTime.now().plusDays(30);
		model.getQueryManager().executeQuery("startSession", sessionId, expiration);
		cookies.set(cookie);
	}
	public void extendSession(String sessionId) throws DatabaseCriticalErrorException {
		Cookie cookie = new Cookie("sessionId", sessionId, 30);
		LocalDateTime expiration = LocalDateTime.now().plusDays(30);
		model.getQueryManager().executeQuery("updateSessionVar", sessionId, "expiration", expiration);
		cookies.set(cookie);
	}
	
	private String createSessionid() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	public void endSession() throws DatabaseCriticalErrorException {
		if (sessionId != null) {
			model.getQueryManager().executeQuery("endSession", sessionId);
		}
	}
	
	public String get(String key) throws DatabaseCriticalErrorException, SQLException {
		if(sessionId != null) {
			ResultSet rs = model.getQueryManager().executeQuery("getSessionVar", sessionId, key);
			if(rs.next()) {
				return rs.getString("value");
			}
		}
		return null;
	}
	
	public void set(String key, String value) throws SQLException, DatabaseCriticalErrorException {
		if(exists(key)) {
			model.getQueryManager().executeQuery("updateSessionVar", value, sessionId, key);
		} else {
			model.getQueryManager().executeQuery("setSessionVar", sessionId, key, value);
		}
	}
	
	public boolean exists(String key) throws DatabaseCriticalErrorException, SQLException {
		if(sessionId != null) {
			ResultSet rs = model.getQueryManager().executeQuery("sessionKeyExists", sessionId, key);
			return rs.next() && rs.getInt(0) != 0;
		}
		return false;
	}

}
