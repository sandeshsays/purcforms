package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;


public class DeleteRowCmd implements ICommand {

	private int ypos;
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private DesignWidgetWrapper line;
	
	public DeleteRowCmd(int ypos, int totalDisplacement, GridDesignGroupWidget table) {
		this.ypos = ypos;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Delete Row";
	}

	public void undo(){
		table.setResizeLinesToFit(false);
		
		table.moveHorizontalLinesAndText(ypos, totalDisplacement);
		table.resizeVerticalLinesAndTable(ypos, totalDisplacement);
		table.add(line);
		
		table.setResizeLinesToFit(true);
	}

	public void redo(){
		table.setResizeLinesToFit(false);
		
		line.storePosition();
		table.remove(line);
		table.moveHorizontalLinesAndText(ypos, -totalDisplacement);
		table.resizeVerticalLinesAndTable(ypos, -totalDisplacement);
		
		table.setResizeLinesToFit(true);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void setLine(DesignWidgetWrapper line) {
		this.line = line;
	}
}