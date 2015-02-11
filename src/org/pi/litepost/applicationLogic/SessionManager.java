package org.pi.litepost.applicationLogic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Calendar;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

import fi.iki.elonen.NanoHTTPD.Cookie;
import fi.iki.elonen.NanoHTTPD.CookieHandler;

public class SessionManager extends Manager {

	private CookieHandler cookies;
	private String sessionId;
	private static final DateTimeFormatter COOKIE_TIME_FORMAT = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'");

	public void resumeSession(CookieHandler cookieHandler) throws DatabaseCriticalErrorException, SQLException {
		this.cookies = cookieHandler;
		this.sessionId = cookies.read("sessionId");
		initSession();
	}
	
	public void cleanSessions() throws DatabaseCriticalErrorException, SQLException {
		ArrayList<String> expiredSessions = new ArrayList<>();
		ResultSet rs = model.getQueryManager().executeQuery("getAllSessions");
		while(rs.next()) {
			TemporalAccessor ta = COOKIE_TIME_FORMAT.parse(rs.getString("value"));
			if(LocalDateTime.from(ta).isBefore(LocalDateTime.now())) {
				expiredSessions.add(rs.getString("session_id"));
			}
		}
		for(String s: expiredSessions) {
			model.getQueryManager().executeQuery("removeSession", s);
		}
	}
	
	public void initSession() throws DatabaseCriticalErrorException, SQLException {
		initSession(null);
	}

	public void initSession(TemporalAmount duration) throws DatabaseCriticalErrorException, SQLException {
		Cookie cookie;
		String expiration;
		String sessionOnly;
		boolean newSession = this.sessionId == null;
		this.sessionId = this.sessionId == null ? createSessionid(): this.sessionId;
		if(duration == null){
			sessionOnly = "true";
			expiration = COOKIE_TIME_FORMAT.format(LocalDateTime.now().plus(Duration.ofMinutes(15)));
			cookie = Cookie.sessionCookie("sessionId", sessionId);
		} else {
			sessionOnly = "false";
			expiration = COOKIE_TIME_FORMAT.format(LocalDateTime.now().plus(duration));
			cookie = new Cookie("sessionId", sessionId, expiration);
		}
		if(newSession) {
			model.getQueryManager().executeQuery("setSessionVar", sessionId, "expiration", expiration);
			model.getQueryManager().executeQuery("setSessionVar", sessionId, "session_only", sessionOnly);
			cookies.set(cookie);
		} else {
			if(duration != null || isSessionOnly()) {
				model.getQueryManager().executeQuery("updateSessionVar", sessionId, "expiration", expiration);
				model.getQueryManager().executeQuery("updateSessionVar", sessionId, "session_only", sessionOnly);
				cookies.set(cookie);
			}
		}
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
		return get(sessionId, key);
	}
	
	public void set(String key, String value) throws SQLException, DatabaseCriticalErrorException {
		set(sessionId, key, value);
	}
	
	public boolean exists(String key) throws DatabaseCriticalErrorException, SQLException {
		return exists(sessionId, key);
	}
	
	public TemporalAccessor getExpiration() throws DatabaseCriticalErrorException, SQLException {
		return getExpiration(sessionId);
	}
	
	public boolean isSessionOnly() throws DatabaseCriticalErrorException, SQLException {
		return isSessionOnly(sessionId);
	}
	
	private String get(String sessionid, String key) throws DatabaseCriticalErrorException, SQLException {
		if(sessionId != null) {
			ResultSet rs = model.getQueryManager().executeQuery("getSessionVar", sessionId, key);
			if(rs.next()) {
				return rs.getString("value");
			}
		}
		return null;
	}
	
	private void set(String sessionid, String key, String value) throws SQLException, DatabaseCriticalErrorException {
		if(exists(key)) {
			model.getQueryManager().executeQuery("updateSessionVar", value, sessionId, key);
		} else {
			model.getQueryManager().executeQuery("setSessionVar", sessionId, key, value);
		}
	}
	
	private boolean exists(String sessionid, String key) throws DatabaseCriticalErrorException, SQLException {
		if(sessionId != null) {
			ResultSet rs = model.getQueryManager().executeQuery("sessionKeyExists", sessionId, key);
			return rs.next() && rs.getInt(1) != 0;
		}
		return false;
	}
	
	private TemporalAccessor getExpiration(String sessionid) throws DatabaseCriticalErrorException, SQLException {
		return COOKIE_TIME_FORMAT.parse(get(sessionid, "expiration"));
	}
	
	private boolean isSessionOnly(String sessionid) throws DatabaseCriticalErrorException, SQLException {
		String sessionOnly = get(sessionid, "session_only");
		return sessionOnly != null && sessionOnly.equals("true");
	}
	
	

}
