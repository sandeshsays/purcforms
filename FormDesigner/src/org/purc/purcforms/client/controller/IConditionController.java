package org.purc.purcforms.client.controller;

import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;


/**
 * This interface is implemented by those classes that want to listen to events which
 * happen when the user manipulates conditions for validation and skip rules.
 * 
 * @author daniel
 *
 */
public interface IConditionController {

	/**
	 * Called to add a new condition.
	 */
	public void addCondition();
	
	/**
	 * Called to add a bracket for grouping of related conditions.
	 */
	public void addBracket();
	
	/**
	 * Called to delete a condition.
	 * 
	 * @param conditionWidget the widget for the condition to delete.
	 */
	public void deleteCondition(ConditionWidget conditionWidget);
	
	public void onConditionOperatorChanged(ConditionWidget conditionWidget, int oldOperator);
	public void onConditionValue1Changed(ConditionWidget conditionWidget, String oldValue);
	public void onConditionValue2Changed(ConditionWidget conditionWidget, String oldValue);
	public void onConditionQuestionChanged(ConditionWidget conditionWidget, QuestionDef oldQuestionDef);
	public void onConditionQtnValueToggleChanged(ConditionWidget conditionWidget, boolean oldValue);
	public void onConditionFunctionChanged(ConditionWidget conditionWidget, int oldFunction);
}
