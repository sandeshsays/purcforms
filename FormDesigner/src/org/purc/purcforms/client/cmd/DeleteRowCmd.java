package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;


public class DeleteRowCmd implements ICommand {

	private int ypos;
	private int totalDisplacement;
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> deletedLines = new ArrayList<DesignWidgetWrapper>();
	private Map<DesignWidgetWrapper, Integer> resizedLines = new HashMap<DesignWidgetWrapper, Integer>();
	private Map<DesignWidgetWrapper, Integer> movedLines = new HashMap<DesignWidgetWrapper, Integer>();

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
		
		//table.resizeVerticalLinesAndTable(ypos, totalDisplacement);
		table.resizeVerticalLines(resizedLines, movedLines, totalDisplacement);
		
		for (DesignWidgetWrapper line : deletedLines) {
			table.add(line);
		}
		
		table.onRowsAdded(totalDisplacement);
		
		table.setResizeLinesToFit(true);
	}

	public void redo(){
		table.setResizeLinesToFit(false);
		
		for (DesignWidgetWrapper line : deletedLines) {
			line.storePosition();
			table.remove(line);
		}
		
		table.moveHorizontalLinesAndText(ypos, -totalDisplacement);
		
		//table.resizeVerticalLinesAndTable(ypos, -totalDisplacement);
		table.resizeVerticalLines(resizedLines, movedLines, -totalDisplacement);
		
		table.onRowsRemoved(totalDisplacement);
		
		table.setResizeLinesToFit(true);
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void deleteLine(DesignWidgetWrapper line) {
		deletedLines.add(line);
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