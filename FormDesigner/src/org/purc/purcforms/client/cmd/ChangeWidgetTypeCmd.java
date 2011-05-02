package org.purc.purcforms.client.cmd;

import java.util.List;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.xml.client.Node;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeWidgetTypeCmd implements ICommand {

	private DesignGroupView view;
	private DesignWidgetWrapper widget;
	private Node parentLayoutNode;
	private AbsolutePanel panel;
	private List<DesignWidgetWrapper> widgets;
	
	public ChangeWidgetTypeCmd(DesignWidgetWrapper widget, Node layoutNode, List<DesignWidgetWrapper> widgets, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.widgets = widgets;
		this.panel = view.getPanel();
		
		if(layoutNode != null)
			this.parentLayoutNode = layoutNode.getParentNode();
	}
	
	public String getName(){
		return "Change Widget";
	}
	
	public void undo(){
		for(DesignWidgetWrapper wrapper: widgets){
			view.deleteWidget(wrapper, panel);
		}
			
		view.insertWidget(widget, panel);
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
		}
	}
	
	public void redo(){
		view.deleteWidget(widget, panel);
		
		for(DesignWidgetWrapper wrapper: widgets){
			view.insertWidget(wrapper, panel);
		}
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}
