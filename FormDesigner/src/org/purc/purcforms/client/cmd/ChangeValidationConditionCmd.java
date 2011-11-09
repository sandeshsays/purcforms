package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.ValidationRule;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.ValidationRulesView;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeValidationConditionCmd implements ICommand {

	public static final byte PROPERTY_LENGTH_VALUE = 1;
	public static final byte PROPERTY_OPERATOR = 2;
	public static final byte PROPERTY_VALUE1 = 3;
	public static final byte PROPERTY_VALUE2 = 4;
	public static final byte PROPERTY_QTN_VALUE_TOGGLE = 5;
	
	protected ValidationRulesView view;
	protected ConditionWidget widget;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected ValidationRule validationRule;

	protected Object oldValue;
	protected byte property;
	
	
	public ChangeValidationConditionCmd(byte property, Object oldValue, ValidationRule validationRule, ConditionWidget widget, ValidationRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.validationRule = validationRule;
		this.widget = widget;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
	}
	
	public String getName(){
		return "Change Skipe Rule Condition " + getFieldName() + " Property";
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
		case PROPERTY_LENGTH_VALUE:
			setLengthValue(field, value);
			break;
		case PROPERTY_OPERATOR:
			setOperator(field, value);
			break;
		case PROPERTY_VALUE1:
			setValue1(field, value);
			break;
		case PROPERTY_VALUE2:
			setValue2(field, value);
			break;
		case PROPERTY_QTN_VALUE_TOGGLE:
			setQtnToggleValue(field, value);
			break;
		}
	}
	
	private void setLengthValue(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = condition.getFunction();
		
		condition.setFunction((Integer)value);
		widget.setFunction(condition.getFunction());
		view.setConditionFunction(validationRule, condition);
	}
	
	private void setOperator(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = condition.getOperator();
		
		condition.setOperator((Integer)value);
		widget.setOperator(condition.getOperator());
		view.setCondionOperator(validationRule, condition);
	}

	private void setValue1(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = condition.getValue();
		
		condition.setValue((String)value);
		widget.setValue((String)value);
		view.setConditionValue(validationRule, condition);
	}
	
	private void setValue2(Object field, Object value){
		setValue1(field, value); //TODO Just for now since between operator is not implemented.
	}

	private void setQtnToggleValue(Object field, Object value){
		oldValue = !(Boolean)value;
		
		widget.setQtnToggleValue((Boolean)value);
		view.setQtnToggleValue(validationRule, widget.getExistingCondition(), (Boolean)value);
	}
	
	private String getFieldName(){
		switch(property){
		case PROPERTY_LENGTH_VALUE:
			return "Length/Value";
		case PROPERTY_OPERATOR:
			return "Operator";
		case PROPERTY_VALUE1:
			return "Value";
		case PROPERTY_VALUE2:
			return "Second Value";
		case PROPERTY_QTN_VALUE_TOGGLE:
			return LocaleText.get("question") + " value toggle";
		}
		
		return "";
	}
}
