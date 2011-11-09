package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.xml.client.Node;


/**
 * 
 * @author danielkayiwa
 *
 */
public class GroupWidgetsCmd implements ICommand {

	protected DesignGroupView view;
	protected DesignWidgetWrapper widget;
	protected AbsolutePanel panel;
	protected Node parentLayoutNode;
	protected ICommand deleteWidgetsCmd;
	protected String left;
	protected String top;
	
	public GroupWidgetsCmd(DesignWidgetWrapper widget, Node layoutNode, ICommand deleteWidgetsCmd, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.deleteWidgetsCmd = deleteWidgetsCmd;
		this.panel = view.getPanel();
		
		if(layoutNode != null)
			this.parentLayoutNode = layoutNode.getParentNode();
		
		widget.refreshPosition();
		
		left = widget.getLeft();
		top = widget.getTop();
	}
	
	public String getName(){
		return "Group Widgets";
	}
	
	public void undo(){
		view.deleteWidget(widget, panel);
		deleteWidgetsCmd.undo();
	}
	
	public void redo(){
		widget.setLeft(left);
		widget.setTop(top);
		
		deleteWidgetsCmd.redo();
		view.insertWidget(widget, panel);
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
		}
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}
