package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * 
 * @author danielkayiwa
 *
 */
public class DeleteFieldCmd implements ICommand {

	private FormsTreeView view;
	private TreeItem item;
	private TreeItem parentItem;
	private int index;


	public DeleteFieldCmd(TreeItem item, TreeItem parentItem, int index, FormsTreeView view){
		this.item = item;
		this.parentItem = parentItem;
		this.view = view;
		this.index = index;
	}

	public String getName(){
		return "Delete Field";
	}

	public void undo(){
		TreeItem inserAfterItem = null;
		if(parentItem != null){
			inserAfterItem = parentItem.getChild(index);
			if(inserAfterItem != null)
				parentItem.insertItem(item, inserAfterItem);
			else
				parentItem.addItem(item);	
		}
		else
			view.addRootItem(item);
		
		view.addFormDefItem(item.getUserObject(), (inserAfterItem != null ? inserAfterItem.getUserObject() : null), parentItem);
		view.setSelectedItem(item);
		
		//if(index == 0 && parentItem != null)
		//	view.moveItemUp();
		
		//add xforms nodes
		//add to validation rules
		//add to skip rules
		//add to dynamic lists
	}

	public void redo(){
		view.deleteItem(item, parentItem);
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
