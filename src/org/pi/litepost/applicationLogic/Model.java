package org.pi.litepost.applicationLogic;

import java.io.File;
import java.sql.SQLException;

import org.pi.litepost.App;
import org.pi.litepost.databaseAccess.DatabaseConnector;
import org.pi.litepost.databaseAccess.DatabaseQueryManager;

/**
 * Model which initializes all Managers
 * 
 * @author Julia Moos
 *
 */
public class Model implements AutoCloseable{
	private SessionManager sessionManager;
	private UserManager userManager;
	private MessageManager messageManager;
	private PostManager postManager;
	private CalenderManager calenderManager;
	private CommentManager commentManager;
	private DatabaseConnector dbConnector;
	private DatabaseQueryManager dbQueryManager;
	private MailManager mailManager;

	public Model() {
		sessionManager = new SessionManager();
		sessionManager.setModel(this);
		
		userManager = new UserManager();
		userManager.setModel(this);
		
		messageManager = new MessageManager();
		messageManager.setModel(this);
		
		postManager = new PostManager();
		postManager.setModel(this);
		
		commentManager = new CommentManager();
		commentManager.setModel(this);
		
		mailManager = new MailManager();
		mailManager.setModel(this);
		
		calenderManager = new CalenderManager();
		
		String defaultDbpath = "res" + File.separatorChar + "litepost.db";
		dbConnector = new DatabaseConnector((String) App.config.getOrDefault("liteposrt.dbpath", defaultDbpath));
		dbQueryManager = new DatabaseQueryManager(this.dbConnector);
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public PostManager getPostManager() {
		return postManager;
	}

	public CalenderManager getCalenderManager() {
		return calenderManager;
	}

	public CommentManager getCommentManager() {
		return commentManager;
	}
	
	public MailManager getMailManager() {
		return mailManager;
	}

	public DatabaseQueryManager getQueryManager() {
		return dbQueryManager;
	}
	
	public void init() throws ClassNotFoundException, SQLException {
		dbConnector.connect();
		sessionManager.init();
		userManager.init();
		messageManager.init();
		postManager.init();
		calenderManager.init();
		commentManager.init();
		mailManager.init();
	}
	
	public void close() throws SQLException {
		dbConnector.close();
	}

}
