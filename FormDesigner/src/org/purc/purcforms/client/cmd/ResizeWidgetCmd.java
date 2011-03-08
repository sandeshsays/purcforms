package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ResizeWidgetCmd implements ICommand {

	private DesignGroupView view;
	private DesignWidgetWrapper widget;
	protected AbsolutePanel panel;
	private int x = 0;
	private int y = 0;
	private int width;
	private int height;
	
	
	public ResizeWidgetCmd(DesignWidgetWrapper widget, int x, int y, int width, int height, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.panel = view.getPanel();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public String getName(){
		return "Resize Widget";
	}
	
	public void undo(){
		widget.setLeftInt(widget.getLeftInt() + x);
		widget.setTopInt(widget.getTopInt() + y);
		widget.setWidthInt(widget.getWidthInt() + width);
		widget.setHeightInt(widget.getHeightInt() + height);

		view.selectWidget(widget, panel);
	}
	
	public void redo(){
		widget.setLeftInt(widget.getLeftInt() - x);
		widget.setTopInt(widget.getTopInt() - y);
		widget.setWidthInt(widget.getWidthInt() - width);
		widget.setHeightInt(widget.getHeightInt() - height);
		
		view.selectWidget(widget, panel);
	}

	public boolean isWidgetCommand(){
		return true;
	}
}
