package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.xml.client.Node;


public class DeleteWidgetCmd implements ICommand {

	protected DesignGroupView view;
	protected DesignWidgetWrapper widget;
	protected Node parentLayoutNode;
	
	public DeleteWidgetCmd(DesignWidgetWrapper widget, Node layoutNode, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		
		if(layoutNode != null)
			this.parentLayoutNode = layoutNode.getParentNode();
	}
	
	public String getName(){
		return "Delete Widget";
	}
	
	public void undo(){
		view.insertWidget(widget);
		
		if(widget.getLayoutNode() != null)
			parentLayoutNode.appendChild(widget.getLayoutNode());
	}
	
	public void redo(){
		view.deleteWidget(widget);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}
