package org.purc.purcforms.client.cmd;

import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.model.DynamicOptionDef;
import org.purc.purcforms.client.model.QuestionDef;
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
public class ChangeDynamicListCmd implements ICommand {

	public static final byte PROPERTY_QUESTION = 1;

	protected DynamicListsView view;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected DynamicOptionDef dynamicOptionDef;

	protected Object oldValue;
	protected byte property;


	public ChangeDynamicListCmd(byte property, Object oldValue, DynamicOptionDef dynamicOptionDef, DynamicListsView view, TreeItem item, FormsTreeView formsTreeView){
		this.property = property;
		this.oldValue = oldValue;
		this.dynamicOptionDef = dynamicOptionDef;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
	}

	public String getName(){
		return "Change Dynamic List " + getFieldName() + " Property";
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
		}
	}

	private void setQuestion(Object field, Object value){
		//oldValue = dynamicOptionDef;
		//dynamicOptionDef = (DynamicOptionDef)oldValue;
		//dynamicOptionDef = formDef.getDynamicOptions(parentQuestionDef.getId());
		//view.(dynamicOptionDef);
	}

	private String getFieldName(){
		switch(property){
		case PROPERTY_QUESTION:
			return "Question";
		}

		return "";
	}
}
