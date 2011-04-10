package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.view.DynamicListsView;
import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeDynamicListChildCmd implements ICommand {

	public static final byte PROPERTY_TEXT = 1;
	public static final byte PROPERTY_BINDING = 2;
	
	protected DynamicListsView view;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected OptionDef optionDef;

	protected String oldValue;
	protected byte property;
	
	
	public ChangeDynamicListChildCmd(byte property, String oldValue, OptionDef optionDef, DynamicListsView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.optionDef = optionDef;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
	}
	
	public String getName(){
		return "Change Dynamic List Option " + getFieldName() + " Property";
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
		case PROPERTY_TEXT:
			setText(field, value);
			break;
		case PROPERTY_BINDING:
			setBinding(field, value);
			break;
		}
	}
	
	private void setText(Object field, String value){
		oldValue = optionDef.getText();
		optionDef.setText(value);
		view.displayOptionText(optionDef, oldValue);
	}
	
	private void setBinding(Object field, String value){
		oldValue = optionDef.getBinding();
		optionDef.setBinding(value);
		view.displayOptionBinding(optionDef);
	}
	
	private String getFieldName(){
		switch(property){
		case PROPERTY_TEXT:
			return LocaleText.get("text");
		case PROPERTY_BINDING:
			return LocaleText.get("binding");
		}
		
		return "";
	}
}
