package applicationLogic;

import databaseAccess.DatabaseConnector;
import databaseAccess.DatabaseCriticalErrorException;
import databaseAccess.DatabaseRequestManager;

/**
 * Model which initializes all Managers
 * 
 * @author Julia Moos
 *
 */
public class Model {
	private SessionManager sessionManager;
	private UserManager userManager;
	private MessageManager messageManager;
	private PostManager postManager;
	private CalenderManager calenderManager;
	private CommentManager commentManager;
	private DatabaseConnector dbConnector;
	private DatabaseQueryManager dbQueryManager;

	public Model() throws DatabaseCriticalErrorException {
		sessionManager = new SessionManager();
		sessionManager.setModel(this);
		userManager = new UserManager();
		userManager.setModel(this);
		messageManager = new MessageManager();
		messageManager.setModel(this);
		postManager = new PostManager();
		postManager.setModel(this);
		calenderManager = new CalenderManager();
		commentManager = new CommentManager();
		commentManager.setModel(this);
		dbConnector = new DatabaseConnector();
		dbQueryManager = new DatabaseQueryManager(
				this.dbConnector);

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

	public DatabaseRequestManager getDatabaseRequestManager() {
		return databaseRequestManager;
	}
}
