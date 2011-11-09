package org.purc.purcforms.client.cmd;

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
public class InsertDynamicListChildCmd extends DeleteDynamicListChildCmd {
	
	public InsertDynamicListChildCmd(OptionDef optionDef, int index, DynamicListsView view, TreeItem item, FormsTreeView formsTreeView){
		super(optionDef, index, view, item, formsTreeView);
	}
	
	public String getName(){
		return "Insert Dynamic List Option";
	}

	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteOption(optionDef);
			}
		});
	}

	public void redo(){	
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addOption(optionDef, index);
			}
		});
	}
}
