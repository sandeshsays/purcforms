package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignSurfaceView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.xml.client.Node;



public class InsertTabCmd extends DeleteTabCmd {
	
	public InsertTabCmd(DesignWidgetWrapper widget, int index, String name, Node layoutNode, DesignSurfaceView view){
		super(widget, index, name, layoutNode, view);
	}
	
	public String getName(){
		return "Insert Tab";
	}
	
	public void undo(){
		view.deleteTab(index);
	}
	
	public void redo(){
		view.clearSelection();
		view.addNewTab(widget.getText(), index, false);
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
		}
	}
}
