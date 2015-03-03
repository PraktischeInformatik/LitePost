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
	protected Clock clock = Clock.systemDefaultZone();

	public void setModel(Model model) {
		this.model = model;
	}
	
	public void setClock(Clock clock) {
		this.clock = clock;
	}
	
	public void init() {}

}
