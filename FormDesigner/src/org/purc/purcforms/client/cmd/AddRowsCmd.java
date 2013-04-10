package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> addedLines = new ArrayList<DesignWidgetWrapper>();
	private Map<DesignWidgetWrapper, Integer> resizedLines = new HashMap<DesignWidgetWrapper, Integer>();
	private Map<DesignWidgetWrapper, Integer> movedLines = new HashMap<DesignWidgetWrapper, Integer>();
	
	public AddRowsCmd(int ypos, int totalDisplacement, GridDesignGroupWidget table) {
		this.ypos = ypos;
		this.totalDisplacement = totalDisplacement;
		this.table = table;
	}
	
	public String getName(){
		return "Add Rows";
	}

	public void undo(){
		//remove all the newly added lines.
		for(DesignWidgetWrapper line : addedLines) {
			line.storePosition();
			table.remove(line);
		}
		
		table.moveHorizontalLinesAndText(ypos, -totalDisplacement);
		
		//table.resizeVerticalLinesAndTable(ypos, -totalDisplacement);
		table.resizeVerticalLines(resizedLines, movedLines, -totalDisplacement);
	}

	public void redo(){
		table.moveHorizontalLinesAndText(ypos, totalDisplacement);
		
		//table.resizeVerticalLinesAndTable(ypos, totalDisplacement);
		table.resizeVerticalLines(resizedLines, movedLines, totalDisplacement);
		
		//add the previously added lines.
		for(DesignWidgetWrapper line : addedLines) {
			table.add(line);
		}
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
    
    public void addMovedLine(DesignWidgetWrapper line, Integer left) {
    	movedLines.put(line, left);
	}

    public Map<DesignWidgetWrapper, Integer> getMovedLines() {
    	return movedLines;
    }
}