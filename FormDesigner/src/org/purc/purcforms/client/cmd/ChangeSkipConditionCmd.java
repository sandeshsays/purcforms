package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
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
	public static final byte PROPERTY_VALUE = 3;
	public static final byte PROPERTY_QTN_VALUE_TOGGLE = 4;
	
	protected SkipRulesView view;
	protected ConditionWidget widget;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected SkipRule skipRule;
	protected int index;
	
	private String oldValue;
	private byte property;
	
	
	public ChangeSkipConditionCmd(byte property, String oldValue, SkipRule skipRule, ConditionWidget widget, int index, SkipRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.skipRule = skipRule;
		this.widget = widget;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
		this.index = index;
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
	
	private void setProperyValue(String value){
		Object field = item.getUserObject();

		switch(property){
		case PROPERTY_QUESTION:
			setQuestion(field, value);
			break;
		case PROPERTY_OPERATOR:
			setOperator(field, value);
			break;
		case PROPERTY_VALUE:
			setValue(field, value);
			break;
		case PROPERTY_QTN_VALUE_TOGGLE:
			setQtnToggleValue(field, value);
			break;
		}
	}
	
	private void setQuestion(Object field, String value){
		
	}
	
	private void setOperator(Object field, String value){
		
	}

	private void setValue(Object field, String value){
		
	}

	private void setQtnToggleValue(Object field, String value){
		
	}
	
	private String getFieldName(){
		switch(property){
		case PROPERTY_QUESTION:
			return LocaleText.get("question");
		case PROPERTY_OPERATOR:
			return "Operator";
		case PROPERTY_VALUE:
			return "Value";
		case PROPERTY_QTN_VALUE_TOGGLE:
			return LocaleText.get("question") + " value toggle";
		}
		
		return "";
	}
}
