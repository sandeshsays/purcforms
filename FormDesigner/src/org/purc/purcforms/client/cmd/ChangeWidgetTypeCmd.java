package org.purc.purcforms.client.cmd;

import java.util.List;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
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
	private Widget wrappedWidget;
	
	public ChangeWidgetTypeCmd(DesignWidgetWrapper widget, Node layoutNode, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.panel = view.getPanel();
		
		if(layoutNode != null)
			this.parentLayoutNode = layoutNode.getParentNode();
	}
	
	public ChangeWidgetTypeCmd(DesignWidgetWrapper widget, Node layoutNode, List<DesignWidgetWrapper> widgets, DesignGroupView view){
		this(widget, layoutNode, view);
		this.widgets = widgets;
	}
	
	public ChangeWidgetTypeCmd(DesignWidgetWrapper widget, Node layoutNode, Widget wrappedWidget, DesignGroupView view){
		this(widget, layoutNode, view);
		this.wrappedWidget = wrappedWidget;
	}
	
	public String getName(){
		return "Change Widget";
	}
	
	public void undo(){
		if (wrappedWidget == null) {
			for(DesignWidgetWrapper wrapper: widgets){
				view.deleteWidget(wrapper, panel);
			}
				
			view.insertWidget(widget, panel);
		}
		else {
			Widget w = widget.getWrappedWidget();
			widget.setWrappedWidget(wrappedWidget);
			wrappedWidget = w;
		}
		
		if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.appendChild(widget.getLayoutNode());
			widget.setLayoutNode(widget.getLayoutNode());
		}
	}
	
	public void redo(){
		if (wrappedWidget == null) {
			view.deleteWidget(widget, panel);
			
			for(DesignWidgetWrapper wrapper: widgets){
				view.insertWidget(wrapper, panel);
			}
		}
		else {
			Widget w = widget.getWrappedWidget();
			widget.setWrappedWidget(wrappedWidget);
			wrappedWidget = w;
		}
		
		//Commented out because it causes problems
		/*if(widget.getLayoutNode() != null){
			if(parentLayoutNode == null)
				parentLayoutNode = widget.getLayoutNode().getParentNode();
			
			parentLayoutNode.removeChild(widget.getLayoutNode());
			widget.setLayoutNode(null);
		}*/
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}
