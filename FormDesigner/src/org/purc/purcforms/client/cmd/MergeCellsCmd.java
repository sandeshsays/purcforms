package org.purc.purcforms.client.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;

public class MergeCellsCmd implements ICommand {
	
	private GridDesignGroupWidget table;
	private List<DesignWidgetWrapper> addedLines = new ArrayList<DesignWidgetWrapper>();
	private List<DesignWidgetWrapper> removedLines = new ArrayList<DesignWidgetWrapper>();
	private Map<DesignWidgetWrapper, Integer> resizedVerticalLines = new HashMap<DesignWidgetWrapper, Integer>();
	private Map<DesignWidgetWrapper, Integer> resizedHorizontalLines = new HashMap<DesignWidgetWrapper, Integer>();
	
	public MergeCellsCmd(GridDesignGroupWidget table) {
		this.table = table;
	}
	
	public String getName(){
		return "Merge Cells";
	}

	public void undo(){
		for(DesignWidgetWrapper line : removedLines) {
			table.add(line);
		}
		
		for(DesignWidgetWrapper line : addedLines) {
			line.storePosition();
			table.remove(line);
		}
		
		resizeLines();
	}

	public void redo(){
		for(DesignWidgetWrapper line : addedLines) {
			table.add(line);
		}
		
		for(DesignWidgetWrapper line : removedLines) {
			line.storePosition();
			table.remove(line);
		}
		
		resizeLines();
	}
	
	private void resizeLines() {
		for(DesignWidgetWrapper line : resizedVerticalLines.keySet()) {
			int height =  resizedVerticalLines.get(line);
			resizedVerticalLines.put(line, line.getHeightInt());
			line.setHeightInt(height);
		}

		for(DesignWidgetWrapper line : resizedHorizontalLines.keySet()) {
			int width = resizedHorizontalLines.get(line);
			resizedHorizontalLines.put(line, line.getWidthInt());
			line.setWidthInt(width);
		}
	}
	
	public boolean isWidgetCommand(){
		return true;
	}
	
	public void addLine(DesignWidgetWrapper line) {
		addedLines.add(line);
	}
	
	public void removeLine(DesignWidgetWrapper line) {
		removedLines.add(line);
	}
	
	public void addResizedVerticalLine(DesignWidgetWrapper line, Integer change) {
		resizedVerticalLines.put(line, change);
	}
	
	public void addResizedHorizontalLine(DesignWidgetWrapper line, Integer change) {
		resizedHorizontalLines.put(line, change);
	}
}
