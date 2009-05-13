package org.purc.purcforms.client.controller;


/**
 * Interface for listening to form events during form design.
 * 
 * @author daniel
 *
 */
public interface IFormSelectionListener {

	/**
	 * Called when a form item (form,page,question or question option) is selected.
	 * 
	 * @param formItem the item which has been selected.
	 */
	public void onFormItemSelected(Object formItem);
}