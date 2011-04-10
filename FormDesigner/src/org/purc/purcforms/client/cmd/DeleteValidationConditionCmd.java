package org.purc.purcforms.client.cmd;

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
public class DeleteValidationConditionCmd implements ICommand {

	protected ValidationRulesView view;
	protected ConditionWidget widget;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected ValidationRule validationRule;
	protected int index;
	
	public DeleteValidationConditionCmd(ValidationRule validationRule, ConditionWidget widget, int index, ValidationRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.validationRule = validationRule;
		this.widget = widget;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
		this.index = index;
	}
	
	public String getName(){
		return "Delete Validation Rule Condition";
	}
	
	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addCondition(widget, index, validationRule, false);
			}
		});
	}
	
	public void redo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteCondition(widget, validationRule, false);
			}
		});
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
