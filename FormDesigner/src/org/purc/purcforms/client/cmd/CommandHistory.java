package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.util.FormUtil;

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
	
	private List<ICommand> cmds = new ArrayList<ICommand>();
	private Stack<ICommand> undoCmds = new Stack<ICommand>();
	private Stack<ICommand> redoCmds = new Stack<ICommand>();
	private PushButton btnUndo;
	private PushButton btnRedo;
	
	public CommandHistory(){
		try{
			undoBufferSize = Integer.parseInt(FormUtil.getUndoRedoBufferSize());
		}
		catch(Exception ex){}
	}
	
	public void add(ICommand command){
		undoCmds.add(command);
		//cmds.add(command);
		
		//Trim the undo buffer size such that we do not run out of memory.
		/*if(cmds.size() > undoBufferSize){
			ICommand cmd = cmds.get(0);
			if(!undoCmds.remove(cmd)){
				redoCmds.remove(cmd);
			}
			cmds.remove(0);
		}*/
		
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
		//cmds.add(command);
				
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
		//cmds.add(command);
		
		btnRedo.setEnabled(canRedo());
		btnRedo.setTitle(getRedoCommandName());
		
		btnUndo.setEnabled(true);
		btnUndo.setTitle("Undo " + command.getName());
		
		//if(undoCmds.size() > undoBufferSize)
		//	undoCmds.remove(undoCmds.size() - 1);
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
		cmds.clear();
		
		btnUndo.setEnabled(false);
		btnRedo.setEnabled(false);
		
		btnUndo.setTitle("Undo");
		btnRedo.setTitle("Redo");
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
		if(command.isWidgetCommand()){
			//Context.getLeftPanel().selectWidgetProperties();
			Context.getCenterPanel().selectDesignSurface(false);
		}
		else{
			Context.getLeftPanel().selectFormFields();
			Context.getCenterPanel().selectPropertiesTab();
		}
	}
}
