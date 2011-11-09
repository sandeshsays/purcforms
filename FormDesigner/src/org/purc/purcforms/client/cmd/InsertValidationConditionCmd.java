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
public class InsertValidationConditionCmd extends DeleteValidationConditionCmd {
	
	public InsertValidationConditionCmd(ValidationRule validationRule, ConditionWidget widget, ValidationRulesView view, TreeItem item, FormsTreeView formsTreeView, boolean clearAction){
		super(validationRule, widget, validationRule.getConditionCount(), view, item, formsTreeView);
	}
	
	public String getName(){
		return "Insert Validation Rule Condition";
	}

	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteCondition(widget, validationRule, false);
			}
		});
	}

	public void redo(){	
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addCondition(widget, index, validationRule, false);
			}
		});
	}
}