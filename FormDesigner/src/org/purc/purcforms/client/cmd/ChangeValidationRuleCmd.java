package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.ValidationRule;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.ValidationRulesView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeValidationRuleCmd implements ICommand {

	public static final byte PROPERTY_ERROR_MSG = 1;
	public static final byte PROPERTY_CONDITIONS_OPERATOR = 2;

	protected ValidationRulesView view;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected ValidationRule validationRule;

	private Object oldValue;
	private byte property;


	public ChangeValidationRuleCmd(byte property, Object oldValue, ValidationRule validationRule, ValidationRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.validationRule = validationRule;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
	}

	public String getName(){
		return "Change Validation Rule " + getFieldName() + " Property";
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
		case PROPERTY_ERROR_MSG:
			setErrorMessage(field, value);
			break;
		case PROPERTY_CONDITIONS_OPERATOR:
			setConditionOperator(field, value);
			break;
		}
	}

	private void setErrorMessage(Object field, Object value){
		oldValue = validationRule.getErrorMessage();
		validationRule.setErrorMessage((String)value);
		view.setErrorMessage(validationRule);
	}

	private void setConditionOperator(Object field, Object value){
		oldValue = validationRule.getConditionsOperator();
		validationRule.setConditionsOperator((Integer)value);
		view.setCondionsOperator(validationRule);
	}

	private String getFieldName(){
		switch(property){
		case PROPERTY_ERROR_MSG:
			return LocaleText.get("errorMessage");
		case PROPERTY_CONDITIONS_OPERATOR:
			return "Conditions Operator";
		}

		return "";
	}
}
