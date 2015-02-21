package org.pi.litepost.applicationLogic;

/**
 * Superclass for all Managers; sets the Model, so one Manager can use another
 * 
 * @author Julia Moos
 *
 */
public abstract class Manager {

	protected Model model;

	public void setModel(Model model) {
		this.model = model;
	}

}
