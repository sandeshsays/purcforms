package org.purc.purcforms.client.controller;

import org.purc.purcforms.client.model.FormDef;


/**
 * 
 * @author daniel
 *
 */
public interface ICenterPanel {

	void commitChanges();
	String getJavaScriptSource();
	String getCSS();
	FormDef getFormDef();
}
