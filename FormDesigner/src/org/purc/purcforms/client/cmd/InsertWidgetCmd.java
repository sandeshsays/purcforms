package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.xml.client.Node;

public class InsertWidgetCmd extends DeleteWidgetCmd {
	
	public InsertWidgetCmd(DesignWidgetWrapper widget, Node layoutNode, DesignGroupView view){
		super(widget, layoutNode, view);
	}
	
	public String getName(){
		return "Insert Widget";
	}
	
	public void undo(){
		view.deleteWidget(widget);
	}
	
	public void redo(){
		view.insertWidget(widget);
		
		if(widget.getLayoutNode() != null)
			parentLayoutNode.appendChild(widget.getLayoutNode());
	}
}
