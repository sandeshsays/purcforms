package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;


public class DeleteColumnCmd implements ICommand {

	private int xpos;
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private DesignWidgetWrapper line;
	
	public DeleteColumnCmd(int xpos, int totalDisplacement, GridDesignGroupWidget table) {
		this.xpos = xpos;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Delete Column";
	}

	public void undo(){
		table.moveVerticalLinesAndText(xpos, totalDisplacement);
		table.resizeHorizontalLinesAndTable(xpos, totalDisplacement);
		table.add(line);
	}

	public void redo(){
		line.storePosition();
		table.remove(line);
		table.moveVerticalLinesAndText(xpos, -totalDisplacement);
		table.resizeHorizontalLinesAndTable(xpos, -totalDisplacement);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void setLine(DesignWidgetWrapper line) {
		this.line = line;
	}
}
