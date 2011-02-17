package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;

public class InsertWidgetCmd  implements ICommand {

	private DesignWidgetWrapper widget;
	
	
	public String getName(){
		return "Insert Widget";
	}
	
	public void undo(){
		
	}
	
	public void redo(){
		
	}
}
