package org.purc.purcforms.client.cmd;

import java.util.List;


/**
 * 
 * @author danielkayiwa
 *
 */
public class CommandList implements ICommand {

	private List<ICommand> commands;
	
	
	public String getName(){
		return "List";
	}
	
	public void undo(){
		
	}
	
	public void redo(){
		
	}
}
