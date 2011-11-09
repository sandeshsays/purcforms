package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;


/**
 * 
 * @author danielkayiwa
 *
 */
public class MoveWidgetCmd implements ICommand {

	private DesignGroupView view;
	private DesignWidgetWrapper widget;
	protected AbsolutePanel panel;
	private int x = 0;
	private int y = 0;
	
	
	public MoveWidgetCmd(DesignWidgetWrapper widget, int x, int y, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.panel = view.getPanel();
		this.x = x;
		this.y = y;
	}
	
	public String getName(){
		return "Move Widget";
	}
	
	public void undo(){
		widget.setLeftInt(widget.getLeftInt() + x);
		widget.setTopInt(widget.getTopInt() + y);
		
		view.selectWidget(widget, panel);
	}
	
	public void redo(){
		widget.setLeftInt(widget.getLeftInt() - x);
		widget.setTopInt(widget.getTopInt() - y);
		
		view.selectWidget(widget, panel);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
}
