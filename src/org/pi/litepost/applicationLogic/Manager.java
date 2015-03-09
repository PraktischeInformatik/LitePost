package org.pi.litepost.applicationLogic;

import java.time.Clock;

/**
 * Superclass for all Managers; sets the Model, so one Manager can use another
 * 
 * @author Julia Moos
 *
 */
public abstract class Manager {

	protected Model model;
	protected Clock clock;

	public void setModel(Model model) {
		this.model = model;
		this.clock = model.getClock();
	}
	
	public void init() {}

}
