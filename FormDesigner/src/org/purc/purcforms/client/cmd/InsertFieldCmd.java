package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.xml.client.Node;

public class InsertFieldCmd implements ICommand {

	protected FormsTreeView view;
	protected TreeItem item;
	protected TreeItem parentItem;
	protected int index;

	protected Node node1;
	protected Node node2;
	protected Node node3;

	protected InsertFieldCmd(){
		
	}
	
	public InsertFieldCmd(TreeItem item, FormsTreeView view){
		this.item = item;
		this.parentItem = item.getParentItem();
		this.view = view;
		
		if(parentItem != null)
			this.index = parentItem.getChildIndex(item);
	}

	public String getName(){
		return "Insert Field";
	}

	public void undo(){
		storeUserObjectNode();
		view.deleteItem(item, parentItem, false);
	}

	public void redo(){		
		restoreUserObjectNode();

		TreeItem inserAfterItem = null;
		if(parentItem != null){
			inserAfterItem = parentItem.getChild(index);
			if(inserAfterItem != null)
				parentItem.insertItem(item, inserAfterItem); //(index + 1, item);
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

	public boolean isWidgetCommand(){
		return false;
	}


	protected void storeUserObjectNode(){
		Object userObject = item.getUserObject();
		if(userObject instanceof PageDef){
			PageDef pageDef = (PageDef)userObject;
			if(pageDef.getGroupNode() != null && pageDef.getGroupNode().getParentNode() != null)
				node1 = pageDef.getGroupNode().getParentNode();
		}
		else if(userObject instanceof QuestionDef){
			QuestionDef questionDef = (QuestionDef)userObject;
			if(questionDef.getControlNode() != null && questionDef.getControlNode().getParentNode() != null)
				node1 = questionDef.getControlNode().getParentNode();

			if(questionDef.getDataNode() != null && questionDef.getDataNode().getParentNode() != null)
				node2 = questionDef.getDataNode().getParentNode();

			if(questionDef.getBindNode() != null && questionDef.getBindNode().getParentNode() != null)
				node3 = questionDef.getBindNode().getParentNode();
		}
	}


	protected void restoreUserObjectNode(){
		Object userObject = item.getUserObject();
		if(userObject instanceof PageDef){
			if(node1 != null)
				node1.appendChild(((PageDef)userObject).getGroupNode());
		}
		else if(userObject instanceof QuestionDef){
			QuestionDef questionDef = (QuestionDef)userObject;
			if(node1 != null)
				node1.appendChild(questionDef.getControlNode());

			if(node2 != null)
				node2.appendChild(questionDef.getDataNode());

			if(node3 != null)
				node3.appendChild(questionDef.getBindNode());
		}
		else if(userObject instanceof OptionDef){
			OptionDef optionDef = (OptionDef)userObject;
			if(optionDef.getControlNode() != null)
				optionDef.getParent().getControlNode().appendChild(optionDef.getControlNode());
		}
	}
}
