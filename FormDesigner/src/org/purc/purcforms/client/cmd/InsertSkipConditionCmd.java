package org.purc.purcforms.client.cmd;

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
public class InsertSkipConditionCmd extends DeleteSkipConditionCmd {
	
	public InsertSkipConditionCmd(SkipRule skipRule, ConditionWidget widget, SkipRulesView view, TreeItem item, FormsTreeView formsTreeView, boolean clearAction){
		super(skipRule, widget, skipRule.getConditionCount(), view, item, formsTreeView);
	}
	
	public String getName(){
		return "Insert Skip Rule Condition";
	}

	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteCondition(widget, skipRule, false);
			}
		});
	}

	public void redo(){	
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addCondition(widget, index, skipRule, false);
			}
		});
	}
}
