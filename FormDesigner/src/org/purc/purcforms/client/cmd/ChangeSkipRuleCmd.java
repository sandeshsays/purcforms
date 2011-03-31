package org.purc.purcforms.client.cmd;

import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.SkipRulesView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeSkipRuleCmd implements ICommand {

	public static final byte PROPERTY_ACTION = 1;
	public static final byte PROPERTY_CONDITIONS_OPERATOR = 2;
	public static final byte PROPERTY_TARGETS = 3;
	public static final byte PROPERTY_MAKE_REQUIRED = 4;

	protected SkipRulesView view;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected SkipRule skipRule;

	private Object oldValue;
	private byte property;


	public ChangeSkipRuleCmd(byte property, Object oldValue, SkipRule skipRule, SkipRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.skipRule = skipRule;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
	}

	public String getName(){
		return "Change Skipe Rule " + getFieldName() + " Property";
	}

	public boolean isWidgetCommand(){
		return false;
	}

	public void undo(){
		formsTreeView.setSelectedItem(item);

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				setProperyValue(oldValue);
			}
		});
	}

	public void redo(){
		formsTreeView.setSelectedItem(item);

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				setProperyValue(oldValue);
			}
		});
	}

	private void setProperyValue(Object value){
		Object field = item.getUserObject();

		switch(property){
		case PROPERTY_ACTION:
			setAction(field, value);
			break;
		case PROPERTY_CONDITIONS_OPERATOR:
			setConditionOperator(field, value);
			break;
		case PROPERTY_TARGETS:
			setTargets(field, value);
			break;
		case PROPERTY_MAKE_REQUIRED:
			setMakeRequired(field, value);
			break;
		}
	}

	private void setAction(Object field, Object value){
		oldValue = skipRule.getAction();
		skipRule.setAction((Integer)value);
		view.setAction(skipRule);
	}

	private void setMakeRequired(Object field, Object value){
		oldValue = skipRule.getAction();
		skipRule.setAction((Integer)value);
		view.setAction(skipRule);
	}

	private void setConditionOperator(Object field, Object value){
		oldValue = skipRule.getConditionsOperator();
		skipRule.setConditionsOperator((Integer)value);
		view.setCondionsOperator(skipRule);
	}

	private void setTargets(Object field, Object value){
		for(int index = 0; index < skipRule.getActionTargets().size(); index++){
			Integer qtnId = (Integer)skipRule.getActionTargets().get(index);

			QuestionDef qtnDef = Context.getFormDef().getQuestion(qtnId);
			if(qtnDef == null)
				continue; //Ignore the question for which we are editing the skip rule.

			skipRule.removeActionTargetXformData(qtnDef);
		}
		
		oldValue = skipRule.getActionTargets();
		skipRule.setActionTargets((Vector)value);
		view.setActionTargets(skipRule);
	}

	private String getFieldName(){
		switch(property){
		case PROPERTY_ACTION:
			return "Action";
		case PROPERTY_CONDITIONS_OPERATOR:
			return "Conditions Operator";
		case PROPERTY_TARGETS:
			return "Other Questions";
		case PROPERTY_MAKE_REQUIRED:
			return "Make Required";
		}

		return "";
	}
}
