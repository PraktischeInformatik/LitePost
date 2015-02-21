package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;

/**
 * CalenderManager to get the actual Date
 * 
 * @author Julia Moos
 *
 */
public class CalenderManager extends Manager {
	/**
	 * returns the actual Date
	 * 
	 * @return
	 */
	public LocalDateTime getDate() {
		return LocalDateTime.now();
	}
}
