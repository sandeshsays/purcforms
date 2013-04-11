package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> addedLines = new ArrayList<DesignWidgetWrapper>();
	private Map<DesignWidgetWrapper, Integer> resizedLines = new HashMap<DesignWidgetWrapper, Integer>();
	private Map<DesignWidgetWrapper, Integer> movedLines = new HashMap<DesignWidgetWrapper, Integer>();
	
	public AddColumnsCmd(int xpos, int totalDisplacement, GridDesignGroupWidget table) {
		this.xpos = xpos;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Add Columns";
	}

	public void undo(){
		//remove all the newly added lines.
		for(DesignWidgetWrapper line : addedLines) {
			line.storePosition();
			table.remove(line);
		}
		
		table.moveVerticalLinesAndText(xpos, -totalDisplacement);
		
		//table.resizeHorizontalLinesAndTable(xpos, -totalDisplacement);
		table.resizeHorizontalLines(resizedLines, movedLines, -totalDisplacement);
		
		table.onColumnsRemoved(totalDisplacement);
	}

	public void redo(){
		table.moveVerticalLinesAndText(xpos, totalDisplacement);
		
		//table.resizeHorizontalLinesAndTable(xpos, totalDisplacement);
		table.resizeHorizontalLines(resizedLines, movedLines, totalDisplacement);
		
		//add the previously added lines.
		for(DesignWidgetWrapper line : addedLines) {
			table.add(line);
		}
		
		table.onColumnsAdded(totalDisplacement);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void addline(DesignWidgetWrapper line) {
		addedLines.add(line);
	}
	
	public void addResizedLine(DesignWidgetWrapper line, Integer change) {
		resizedLines.put(line, change);
	}

    public Map<DesignWidgetWrapper, Integer> getResizedLines() {
    	return resizedLines;
    }
    
    public void addMovedLine(DesignWidgetWrapper line, Integer top) {
    	movedLines.put(line, top);
	}

    public Map<DesignWidgetWrapper, Integer> getMovedLines() {
    	return movedLines;
    }
}
