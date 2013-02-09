package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

public class MoveTableLineCmd extends MoveWidgetCmd{

	private boolean isCtrlPresses;
	
	public MoveTableLineCmd(DesignWidgetWrapper widget, int x, int y, DesignGroupView view, boolean isCtrlPresses){
		super(widget, x, y, view);
		this.isCtrlPresses = isCtrlPresses;
	}
	
	public String getName(){
		return "Move Table Line";
	}
	
	public void undo(){
		super.undo();
		
		if (!isCtrlPresses) {
			
		}
	}
	
	public void redo(){
		super.redo();
		
		if (!isCtrlPresses) {
			
		}
	}
}
