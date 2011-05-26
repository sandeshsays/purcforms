package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignSurfaceView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.xml.client.Node;


public class DeleteTabCmd implements ICommand {

	protected DesignSurfaceView view;
	protected DesignWidgetWrapper widget;
	protected AbsolutePanel panel;
	protected Node parentLayoutNode;
	protected String name;
	protected int index;
	
	public DeleteTabCmd(DesignWidgetWrapper widget, int index, String name, Node layoutNode, DesignSurfaceView view){
		this.widget = widget;
		this.view = view;
		this.index = index;
		this.name = name;
		this.panel = view.getPanel();
		
		if(layoutNode != null)
			this.parentLayoutNode = layoutNode.getParentNode();
	}
	
	public String getName(){
		return "Delete Tab";
	}
	
	public void undo(){
		view.clearSelection();
		view.addNewTab(name, index, false);
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
		}
	}
	
	public void redo(){
		view.deleteTab(index);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}

}
