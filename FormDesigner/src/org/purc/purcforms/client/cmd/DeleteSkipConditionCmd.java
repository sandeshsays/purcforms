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
public class DeleteSkipConditionCmd implements ICommand {

	protected SkipRulesView view;
	protected ConditionWidget widget;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected SkipRule skipRule;
	protected int index;
	
	public DeleteSkipConditionCmd(SkipRule skipRule, ConditionWidget widget, int index, SkipRulesView view, TreeItem item, FormsTreeView formsTreeView){
		this.skipRule = skipRule;
		this.widget = widget;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
		this.index = index;
	}
	
	public String getName(){
		return "Delete Skip Rule Condition";
	}
	
	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addCondition(widget, index, skipRule, false);
			}
		});
	}
	
	public void redo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteCondition(widget, skipRule, false);
			}
		});
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
