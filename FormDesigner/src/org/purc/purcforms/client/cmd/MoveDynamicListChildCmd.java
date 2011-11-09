package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.view.DynamicListsView;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.PropertiesView;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class MoveDynamicListChildCmd implements ICommand {

	private FormsTreeView formsTreeView;
	private TreeItem item;
	private boolean moveUp;
	private DynamicListsView view;
	private OptionDef optionDef;
	PropertiesView propertiesView;


	public MoveDynamicListChildCmd(TreeItem item, boolean moveUp, OptionDef optionDef, DynamicListsView view, FormsTreeView formsTreeView, PropertiesView propertiesView){
		this.item = item;
		this.view = view;
		this.formsTreeView = formsTreeView;
		this.moveUp = moveUp;
		this.optionDef = optionDef;
		this.propertiesView = propertiesView;
	}

	public String getName(){
		return "Move Dynamic List Option " + (moveUp ? "Up" : "Down");
	}

	public void undo(){	
		formsTreeView.setSelectedItem(item);
		moveOption(true);
	}

	public void redo(){
		formsTreeView.setSelectedItem(item);
		moveOption(false);
	}
	
	private void moveOption(final boolean undo){
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				
				propertiesView.selectDynamicListsTab();
				
				if(moveUp){
					if(undo)
						view.moveOptionItemDown(optionDef, false);
					else
						view.moveOptionItemUp(optionDef, false);
				}
				else{
					if(undo)
						view.moveOptionItemUp(optionDef, false);
					else
						view.moveOptionItemDown(optionDef, false);
				}
			}
		});
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
