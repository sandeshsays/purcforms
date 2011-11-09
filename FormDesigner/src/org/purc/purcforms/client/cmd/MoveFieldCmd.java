package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;


/**
 * Moves a field up or down.
 * 
 * @author danielkayiwa
 *
 */
public class MoveFieldCmd implements ICommand {

	private FormsTreeView view;
	private TreeItem item;
	private boolean moveUp;


	public MoveFieldCmd(TreeItem item, boolean moveUp, FormsTreeView view){
		this.item = item;
		this.view = view;
		this.moveUp = moveUp;
	}

	public String getName(){
		return "Move Field " + (moveUp ? "Up" : "Down");
	}

	public void undo(){	
		view.setSelectedItem(item);
		
		if(moveUp)
			view.moveItemDown(false);
		else
			view.moveItemUp(false);
	}

	public void redo(){
		view.setSelectedItem(item);
		
		if(moveUp)
			view.moveItemUp(false);
		else
			view.moveItemDown(false);
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
