package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;

/**
 * Command for adding table columns.
 * 
 * @author danielkayiwa
 *
 */
public class AddColumnsCmd implements ICommand {

	private int xpos;
	private boolean right;
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> lines = new ArrayList<DesignWidgetWrapper>();
	
	public AddColumnsCmd(int xpos, boolean right, int totalDisplacement, GridDesignGroupWidget table) {
		this.xpos = xpos;
		this.right = right;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Add Columns";
	}

	public void undo(){
		//remove all the newly added lines.
		for(DesignWidgetWrapper line : lines) {
			line.storePosition();
			table.remove(line);
		}
		
		table.moveVerticalLinesAndText(xpos, -totalDisplacement);
		table.resizeHorizontalLinesAndTable(xpos, -totalDisplacement);
	}

	public void redo(){
		table.moveVerticalLinesAndText(xpos, totalDisplacement);
		table.resizeHorizontalLinesAndTable(xpos, totalDisplacement);
		
		//add the previously added lines.
		for(DesignWidgetWrapper line : lines) {
			table.add(line);
		}
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void addline(DesignWidgetWrapper line) {
		lines.add(line);
	}
}
