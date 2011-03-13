package org.purc.purcforms.client.cmd;

import java.util.Stack;

import org.purc.purcforms.client.Context;

import com.google.gwt.user.client.ui.PushButton;


//TODO Refresh which results into a widget insert should be un doable. Refresh may also insert new tabs.
//TODO Investigate the undo buffer size which could have a bug of not stopping the user where they expect to stop
/**
 * The manager for undo and redo commands.
 * 
 * @author daniel
 *
 */
public class CommandHistory {

	private int undoBufferSize = 100;
	
	private Stack<ICommand> undoCmds = new Stack<ICommand>();
	private Stack<ICommand> redoCmds = new Stack<ICommand>();
	private PushButton btnUndo;
	private PushButton btnRedo;
	
	public void add(ICommand command){
		undoCmds.add(command);
		
		//Trim the undo buffer size such that we do not run out of memory.
		if(undoCmds.size() > undoBufferSize)
			undoCmds.remove(undoBufferSize);
		
		btnUndo.setEnabled(true);
		btnUndo.setTitle("Undo " + command.getName());
	}
	
	public void undo(){
		if(!canUndo())
			return;
		
		ICommand command  = undoCmds.pop();
		
		selectUITab(command);
		
		command.undo();
		redoCmds.push(command);
				
		btnUndo.setEnabled(canUndo());
		btnUndo.setTitle(getUndoCommandName());
		
		btnRedo.setEnabled(true);
		btnRedo.setTitle("Redo " + command.getName());
	}
	
	public void redo(){
		if(!canRedo())
			return;
		
		ICommand command  = redoCmds.pop();
		
		selectUITab(command);
		
		command.redo();
		undoCmds.push(command);
		
		btnRedo.setEnabled(canRedo());
		btnRedo.setTitle(getRedoCommandName());
		
		btnUndo.setEnabled(true);
		btnUndo.setTitle("Undo " + command.getName());
	}
	
	public boolean canUndo(){
		return undoCmds.size() > 0;
	}
	
	public boolean canRedo(){
		return redoCmds.size() > 0;
	}
	
	public String getUndoCommandName(){
		if(undoCmds.size() == 0)
			return "Undo";
		else
			return "Undo " + undoCmds.lastElement().getName();
	}
	
	public String getRedoCommandName(){
		if(redoCmds.size() == 0)
			return "Redo";
		else
			return "Redo " + redoCmds.lastElement().getName();
	}
	
	public void clear(){
		undoCmds.clear();
		redoCmds.clear();
		
		btnUndo.setEnabled(false);
		btnRedo.setEnabled(false);
	}
	
	public void setUndoBufferSize(int size){
		this.undoBufferSize = size;
	}
	
	public void setUndoButton(PushButton btnUndo){
		this.btnUndo = btnUndo;
		btnUndo.setEnabled(false);
	}
	
	public void setRedoButton(PushButton btnRedo){
		this.btnRedo = btnRedo;
		btnRedo.setEnabled(false);
	}
	
	private void selectUITab(ICommand command){
		if(command.isWidgetCommand())
			Context.getCenterPanel().selectDesignSurface();
		else
			Context.getCenterPanel().selectPropertiesTab();
	}
}
