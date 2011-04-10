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
public class DeleteDynamicListChildCmd implements ICommand {

	protected DynamicListsView view;
	protected FormsTreeView formsTreeView;
	protected TreeItem item;
	protected OptionDef optionDef;
	protected int index;
	
	public DeleteDynamicListChildCmd(OptionDef optionDef, int index, DynamicListsView view, TreeItem item, FormsTreeView formsTreeView){
		this.optionDef = optionDef;
		this.view = view;
		this.item = item;
		this.formsTreeView = formsTreeView;
		this.index = index;
	}
	
	public String getName(){
		return "Delete Dynamic List Option";
	}
	
	public void undo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.addOption(optionDef, index);
			}
		});
	}
	
	public void redo(){
		formsTreeView.setSelectedItem(item);
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				view.deleteOption(optionDef);
			}
		});
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
