package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;

/**
 * Command for adding table rows.
 * 
 * @author danielkayiwa
 *
 */
public class AddRowsCmd implements ICommand {

	private int ypos;
	private boolean below;
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> lines = new ArrayList<DesignWidgetWrapper>();
	
	public AddRowsCmd(int ypos, boolean below, int totalDisplacement, GridDesignGroupWidget table) {
		this.ypos = ypos;
		this.below = below;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Add Rows";
	}

	public void undo(){
		//remove all the newly added lines.
		for(DesignWidgetWrapper line : lines) {
			line.storePosition();
			table.remove(line);
		}
		
		table.moveHorizontalLinesAndText(ypos, -totalDisplacement);
		table.resizeVerticalLinesAndTable(ypos, -totalDisplacement);
	}

	public void redo(){
		table.moveHorizontalLinesAndText(ypos, totalDisplacement);
		table.resizeVerticalLinesAndTable(ypos, totalDisplacement);
		
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