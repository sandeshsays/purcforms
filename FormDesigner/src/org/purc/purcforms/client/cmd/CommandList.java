package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.view.DesignGroupView;


/**
 * 
 * @author danielkayiwa
 *
 */
public class CommandList implements ICommand {
	
	private DesignGroupView view;
	private List<ICommand> commands = new ArrayList<ICommand>();
	
	public CommandList(DesignGroupView view){
		this.view = view;
	}
	
	//Added only for view.clearSelection() which the single command does not do.
	public CommandList(DesignGroupView view, ICommand command){
		this.view = view;
		add(command);
	}
	
	public String getName(){
		return commands.get(0).getName() + (commands.size() > 1 ? "s" : "");
	}
	
	public void undo(){
		view.clearSelection();
		
		for(ICommand command : commands)
			command.undo();
	}
	
	public void redo(){
		view.clearSelection();
		
		for(ICommand command : commands)
			command.redo();
	}
	
	public void add(ICommand command){
		commands.add(command);
	}
	
	public boolean isWidgetCommand(){
		return commands.get(0).isWidgetCommand();
	}
	
	public int size(){
		return commands.size();
	}
}
