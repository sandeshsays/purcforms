package org.purc.purcforms.client.querybuilder.controller;

import org.purc.purcforms.client.querybuilder.widget.ConditionActionHyperlink;
import org.purc.purcforms.client.querybuilder.widget.ConditionWidget;

import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author daniel
 *
 */
public interface ConditionController {

	public ConditionWidget addCondition(Widget sender, boolean select);
	public ConditionActionHyperlink addBracket(Widget sender, String operator, boolean addCondition, boolean select);
	public void deleteCondition(Widget sender,ConditionWidget conditionWidget);
}
