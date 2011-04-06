package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.SkipRulesView;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeSkipConditionCmd implements ICommand {

	public static final byte PROPERTY_QUESTION = 1;
	public static final byte PROPERTY_OPERATOR = 2;
	public static final byte PROPERTY_VALUE1 = 3;
	public static final byte PROPERTY_VALUE2 = 4;
	public static final byte PROPERTY_QTN_VALUE_TOGGLE = 5;
	
	protected SkipRulesView view;
	protected ConditionWidget widget;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected SkipRule skipRule;

	protected Object oldValue;
	protected byte property;
	
	
	public ChangeSkipConditionCmd(byte property, Object oldValue, SkipRule skipRule, ConditionWidget widget, SkipRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.skipRule = skipRule;
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
		case PROPERTY_QUESTION:
			setQuestion(field, value);
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
	
	private void setQuestion(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = widget.getQuestionDef();
		
		QuestionDef questionDef = (QuestionDef)value;
		condition.setQuestionId(questionDef.getId());
		widget.setQuestionDef(questionDef);
		view.setCondionQuestion(skipRule, condition, questionDef);
	}
	
	private void setOperator(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = condition.getOperator();
		
		condition.setOperator((Integer)value);
		widget.setOperator(condition.getOperator());
		view.setCondionOperator(skipRule, condition);
	}

	private void setValue1(Object field, Object value){
		Condition condition = widget.getExistingCondition();
		oldValue = condition.getValue();
		
		condition.setValue((String)value);
		widget.setValue((String)value);
		view.setConditionValue(skipRule, condition);
	}
	
	private void setValue2(Object field, Object value){
		setValue1(field, value); //TODO Just for now since between operator is not implemented.
	}

	private void setQtnToggleValue(Object field, Object value){
		oldValue = !(Boolean)value;
		
		widget.setQtnToggleValue((Boolean)value);
		view.setQtnToggleValue(skipRule, widget.getExistingCondition(), (Boolean)value);
	}
	
	private String getFieldName(){
		switch(property){
		case PROPERTY_QUESTION:
			return LocaleText.get("question");
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
