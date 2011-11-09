package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * 
 * @author danielkayiwa
 *
 */
public class DeleteFieldCmd extends InsertFieldCmd {

	public DeleteFieldCmd(TreeItem item, TreeItem parentItem, int index, FormsTreeView view){
		this.item = item;
		this.parentItem = parentItem;
		this.index = index;
		this.view = view;
		
		storeUserObjectNode();
	}

	public String getName(){
		return "Delete Field";
	}

	public void undo(){
		restoreUserObjectNode();
		
		TreeItem inserAfterItem = null;
		if(parentItem != null){
			inserAfterItem = parentItem.getChild(index);
			if(inserAfterItem != null)
				parentItem.insertItem(item, inserAfterItem); //parentItem.insertItem(index + 1, item);
			else
				parentItem.addItem(item);	
			
			view.addFormDefItem(item.getUserObject(), (inserAfterItem != null ? inserAfterItem.getUserObject() : null), parentItem);
		}
		else
			view.addRootItem(item);
		
		view.setSelectedItem(item);
		
		//if(index == 0 && parentItem != null)
		//	view.moveItemUp();
		
		//add xforms nodes
		//add to validation rules
		//add to skip rules
		//add to dynamic lists
	}

	public void redo(){
		storeUserObjectNode();
		view.deleteItem(item, parentItem, false);
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
