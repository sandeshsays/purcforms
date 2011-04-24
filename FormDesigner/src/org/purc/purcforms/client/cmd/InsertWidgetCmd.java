package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.xml.client.Node;

public class InsertWidgetCmd extends DeleteWidgetCmd {
	
	public InsertWidgetCmd(DesignWidgetWrapper widget, Node layoutNode, DesignGroupView view){
		super(widget, layoutNode, view);
		widget.refreshPosition();
	}
	
	public String getName(){
		return "Insert Widget";
	}
	
	public void undo(){
		view.deleteWidget(widget, panel);
	}
	
	public void redo(){
		view.insertWidget(widget, panel);
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
		}
	}
}
