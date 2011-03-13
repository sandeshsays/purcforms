package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;

public class InsertFieldCmd implements ICommand {

	private FormsTreeView view;
	private TreeItem item;
	private TreeItem parentItem;
	private int index;


	public InsertFieldCmd(TreeItem item, TreeItem parentItem, int index, FormsTreeView view){
		this.item = item;
		this.parentItem = parentItem;
		this.view = view;
		this.index = index;
	}

	public String getName(){
		return "Insert Field";
	}

	public void undo(){
		view.deleteItem(item, parentItem);
	}

	public void redo(){
		TreeItem inserAfterItem = null;
		if(parentItem != null){
			inserAfterItem = parentItem.getChild(index);
			if(inserAfterItem != null)
				parentItem.insertItem(item, inserAfterItem); //(index + 1, item);
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
	
	public boolean isWidgetCommand(){
		return false;
	}
}
