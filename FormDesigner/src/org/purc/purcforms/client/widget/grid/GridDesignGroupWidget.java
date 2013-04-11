package org.purc.purcforms.client.widget.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.LeftPanel.Images;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.cmd.AddColumnsCmd;
import org.purc.purcforms.client.cmd.AddRowsCmd;
import org.purc.purcforms.client.cmd.DeleteColumnCmd;
import org.purc.purcforms.client.cmd.DeleteRowCmd;
import org.purc.purcforms.client.cmd.MergeCellsCmd;
import org.purc.purcforms.client.controller.IWidgetPopupMenuListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DesignGroupWidget;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.google.gwt.xml.client.Element;


/**
 * 
 * @author daniel
 *
 */
public class GridDesignGroupWidget extends DesignGroupWidget {

	/**
	 * 
	 * @param images
	 * @param widgetPopupMenuListener
	 */
	public GridDesignGroupWidget(Images images, IWidgetPopupMenuListener widgetPopupMenuListener){
		super(images, widgetPopupMenuListener);
	}
	
	public GridDesignGroupWidget(GridDesignGroupWidget designGroupWidget, Images images, IWidgetPopupMenuListener widgetPopupMenuListener){
		super(designGroupWidget, images, widgetPopupMenuListener);
		
		int count = designGroupWidget.getVerticalLineCount();
		for(int index = 0; index < count; index++){
			DesignWidgetWrapper widget = new DesignWidgetWrapper(designGroupWidget.getVerticalLineAt(index),images);

			widget.setWidgetSelectionListener(this);
			widget.setPopupPanel(widgetPopup);

			selectedPanel.add(widget);
			selectedDragController.makeDraggable(widget);
		}
		
		count = designGroupWidget.getHorizontalLineCount();
		for(int index = 0; index < count; index++){
			DesignWidgetWrapper widget = new DesignWidgetWrapper(designGroupWidget.getHorizontalLineAt(index),images);

			widget.setWidgetSelectionListener(this);
			widget.setPopupPanel(widgetPopup);

			selectedPanel.add(widget);
			selectedDragController.makeDraggable(widget);
		}
	}
	
	protected void addExtraMenu(MenuBar menuBar) {
		//begin table menu-----------
		MenuBar tableMenu = new MenuBar(true);
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addRowsAbove")), true, new Command(){
			public void execute() {popup.hide(); addRows(false);}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addRowsBelow")), true, new Command(){
			public void execute() {popup.hide(); addRows(true);}});
		
		tableMenu.addSeparator();
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addColumnsLeft")), true, new Command(){
			public void execute() {popup.hide(); addColumns(false);}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addColumnsRight")), true, new Command(){
			public void execute() {popup.hide(); addColumns(true);}});
		
		tableMenu.addSeparator();
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("deleteColumn")), true, new Command(){
			public void execute() {popup.hide(); deleteColumn();}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("deleteRow")), true, new Command(){
			public void execute() {popup.hide(); deleteRow();}});
		
		tableMenu.addSeparator();
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("mergeCells")), true, new Command(){
			public void execute() {popup.hide(); mergeCells();}});
		
		menuBar.addSeparator();
		menuBar.addItem("     " + LocaleText.get("table"), tableMenu);
		//end table menu----------
	}
	
	public void resizeGrid(int widthChange, int heightChange, int width, int height){
		if(!DragContext.controlKeyPressed) {
			((GridPanel)selectedPanel).resizeGrid(widthChange, heightChange, width, height);
		}
		else
			((GridPanel)selectedPanel).resizeGridWithCtrlPressed(widthChange, heightChange, width, height);
	}
	
	public void moveLine(int xChange, int yChange, int newLeft, int newTop){	
		//if(!((GridPanel)selectedPanel).isResizeLinesToFit())
		//	return;
		
		//check if we are to expand table below or right
		if(!DragContext.controlKeyPressed) {
			if(yChange != 0 && yChange != -1) {//horizontal line moved
				setHeight(getHeightInt() - yChange + PurcConstants.UNITS);
			}
			else if(xChange != 0 && xChange != -1) {//vertical line moved
				setWidth(getWidthInt() - xChange + PurcConstants.UNITS);
			}
			
			((GridPanel)selectedPanel).moveLine(xChange, yChange, newLeft, newTop);
		}
		else
			((GridPanel)selectedPanel).moveLineWithCtrlPressed(xChange, yChange, newLeft, newTop);
	}
	
	protected void initPanel(){
		selectedPanel = new GridPanel();
		super.initPanel();
	}
	
	public void buildLayoutXml(Element parent, com.google.gwt.xml.client.Document doc){
		super.buildLayoutXml(parent, doc);
		
		GridPanel gridPanel = (GridPanel)selectedPanel;
		
		for(int i=0; i<gridPanel.getHorizontalWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)gridPanel.getHorizontalWidget(i);
			((DesignWidgetWrapper)widget).buildLayoutXml(parent, doc);
		}
		
		for(int i=0; i<gridPanel.getVerticalWidgetCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)gridPanel.getVerticalWidget(i);
			((DesignWidgetWrapper)widget).buildLayoutXml(parent, doc);
		}
	}

	public void buildLanguageXml(com.google.gwt.xml.client.Document doc, Element parentNode, String xpath){
		super.buildLanguageXml(doc, parentNode, xpath);
		
		GridPanel gridPanel = (GridPanel)selectedPanel;
		
		for(int i=0; i<gridPanel.getHorizontalWidgetCount(); i++){
			Widget widget = gridPanel.getHorizontalWidget(i);
			((DesignWidgetWrapper)widget).buildLanguageXml(doc,parentNode, xpath);
		}
		
		for(int i=0; i<gridPanel.getVerticalWidgetCount(); i++){
			Widget widget = gridPanel.getVerticalWidget(i);
			((DesignWidgetWrapper)widget).buildLanguageXml(doc,parentNode, xpath);
		}
	}
	
	public int getWidthInt() {
		return FormUtil.convertDimensionToInt(getWidth());
	}
	
	public int getHeightInt() {
		return FormUtil.convertDimensionToInt(getHeight());
	}
	
	public int getVerticalLineCount(){
		return ((GridPanel)selectedPanel).getVerticalWidgetCount();
	}

	public DesignWidgetWrapper getVerticalLineAt(int index){
		return (DesignWidgetWrapper)((GridPanel)selectedPanel).getVerticalWidget(index);
	}
	
	public int getHorizontalLineCount(){
		return ((GridPanel)selectedPanel).getHorizontalWidgetCount();
	}

	public DesignWidgetWrapper getHorizontalLineAt(int index){
		return (DesignWidgetWrapper)((GridPanel)selectedPanel).getHorizontalWidget(index);
	}
	
	public void storePosition(){
		super.storePosition();
		
		int count = getVerticalLineCount();
		for(int index = 0; index < count; index++)
			getVerticalLineAt(index).storePosition();
		
		count = getHorizontalLineCount();
		for(int index = 0; index < count; index++)
			getHorizontalLineAt(index).storePosition();
	}
	
	public void setWidgetPosition(){
		super.setWidgetPosition();
		
		for(int i=0; i<getVerticalLineCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)getVerticalLineAt(i);
			selectedPanel.setWidgetPosition(widget, widget.getLeftInt(), widget.getTopInt());
			widget.setWidth(widget.getWidth());
			widget.setHeight(widget.getHeight());
			
			if(widget.getWrappedWidget() instanceof DesignGroupWidget)
				((DesignGroupWidget)widget.getWrappedWidget()).setWidgetPosition();
		}
		
		for(int i=0; i<getHorizontalLineCount(); i++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)getHorizontalLineAt(i);
			selectedPanel.setWidgetPosition(widget, widget.getLeftInt(), widget.getTopInt());
			widget.setWidth(widget.getWidth());
			widget.setHeight(widget.getHeight());
			
			if(widget.getWrappedWidget() instanceof DesignGroupWidget)
				((DesignGroupWidget)widget.getWrappedWidget()).setWidgetPosition();
		}
	}
	
	public void addRows(boolean below) {
		String rows = Window.prompt(LocaleText.get("numberOfRowsPrompt"), "1");
		if(rows == null || rows.trim().isEmpty()) 
			return; //possibly user selected cancel
		
		addRows(y - getAbsoluteTop(), FormDesignerUtil.convertToInt(rows), below);
	}
	
	public void addRows(int ypos, int rows, boolean below) {
		//get biggest possible distance between two horizontal lines
		int topDiff = getHeightInt();
		
		//get biggest possible distance between two horizontal lines
		int bottomDiff = topDiff;
		boolean topLineFound = false;
		boolean bottomLineFound = false;
		
		WidgetCollection horizontalLines = getHorizontalLines();
		DesignWidgetWrapper startLine = null;
		List<DesignWidgetWrapper> startLines = new ArrayList<DesignWidgetWrapper>();
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int top = widget.getTopInt();
			
			//if current line is above the mouse position
			if(top < ypos) {
				int diff = ypos - top; //how far, above, line is from mouse position
				if(diff < topDiff) { //if this is the smallest distance between line and mouse position
					topDiff = diff;
					if(!below) {//if inserting row before
						startLine = widget;
					}
				}
				else if (diff == topDiff) {
					startLines.add(widget);
				}
				
				topLineFound = true;
			}
			else if(top > ypos){ //current line below the mouse position
				int diff = top - ypos; //how far, below, line is from mouse position
				if(diff < bottomDiff) { //if this is the smallest distance between line and mouse position
					bottomDiff = diff;
					if(below) {//if inserting row after
						startLine = widget;
					}
				}
				else if (diff == bottomDiff) {
					startLines.add(widget);
				}
				
				bottomLineFound = true;
			}
		}
		
		if(!topLineFound) {
			/*int tableTop = ((DesignWidgetWrapper)getParent().getParent()).getTopInt();
			topDiff = ypos - tableTop;
			if(below) {
				if(horizontalLines.size() == 0)
					return;
				
				startLine = (DesignWidgetWrapper)horizontalLines.get(0);
				startLine.setTopInt(tableTop);
				startLine.setLeftInt(((DesignWidgetWrapper)getParent().getParent()).getLeftInt());
				startLine.setWidthInt(getWidthInt());
			}*/
			
			topDiff = ypos;
			if(!below) {
				if(horizontalLines.size() == 0)
					startLine = new DesignWidgetWrapper(new HorizontalGridLine(getHeightInt()), null, null);
				else
					startLine = new DesignWidgetWrapper((DesignWidgetWrapper)horizontalLines.get(0), null);
				
				startLine.setTopInt(getHeaderLabelHeight());
				startLine.setLeftInt(0);
				startLine.setWidthInt(getWidthInt());
			}
		}
		
		if(!bottomLineFound) {
			int tableBottom = getHeightInt(); //((DesignWidgetWrapper)getParent().getParent()).getTopInt() + getHeightInt();
			bottomDiff = tableBottom - ypos; //(ypos - (tableBottom - getAbsoluteTop())) + getHeaderLabelHeight();
			//if(!below) {
				if(horizontalLines.size() == 0) 
					startLine = new DesignWidgetWrapper(new HorizontalGridLine(getHeightInt()), null, null);
				else
					startLine = new DesignWidgetWrapper((DesignWidgetWrapper)horizontalLines.get(0), null);
				
				startLine.setTopInt(tableBottom /*+ getHeaderLabelHeight()*/);
				startLine.setLeftInt(0 /*((DesignWidgetWrapper)getParent().getParent()).getLeftInt()*/);
				startLine.setWidthInt(getWidthInt());
			//}
		}
		
		if(startLine == null)
			return;
		
		//int size = topDiff + bottomDiff;
		int size = (!topLineFound && below) ? startLine.getTopInt() : (topDiff + bottomDiff);
		if(topLineFound) size -= 1; else size -= 9;
		int top = startLine.getTopInt();
		int totalDisplacement = size * rows;
		
		//int size = (!leftLineFound && right) ? startLine.getLeftInt() : (leftDiff + rightDiff);
		
		moveHorizontalLinesAndText(top, totalDisplacement);
		
		AddRowsCmd addRowsCmd = new AddRowsCmd(top, totalDisplacement, this);
		
		//now add the rows
		int width = startLine.getWidthInt();
		x = startLine.getLeftInt() + getAbsoluteLeft(); //startLine.getAbsoluteLeft();
		y = top +  getAbsoluteTop(); //(bottomLineFound ? getAbsoluteTop() : 0);
		
		if(!bottomLineFound) {
			y -= size;
		}
		
		this.setResizeLinesToFit(false);
		for(int i = 0; i < rows; i++) {
			y += size;
			HorizontalGridLine line = new HorizontalGridLine(width);
			DesignWidgetWrapper wrapper = addNewWidget(line, false);
			wrapper.setWidthInt(width);
			wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
			
			addRowsCmd.addline(wrapper);
			
			int prevX = x;
			for (DesignWidgetWrapper ln : startLines) {
				x = ln.getLeftInt() + getAbsoluteLeft();
				
				HorizontalGridLine newLine = new HorizontalGridLine(ln.getWidthInt());
				DesignWidgetWrapper newWrapper = addNewWidget(newLine, false);
				newWrapper.setWidthInt(ln.getWidthInt());
				newWrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
				
				addRowsCmd.addline(newWrapper);
			}
			x = prevX;
		}
		this.setResizeLinesToFit(true);
		
		resizeVerticalLinesAndTable(ypos, totalDisplacement, addRowsCmd.getResizedLines(), addRowsCmd.getMovedLines());
		
		Context.getCommandHistory().add(addRowsCmd);
	}
	
	public void addColumns(boolean right) {
		String columns = Window.prompt(LocaleText.get("numberOfColumnsPrompt"), "1");
		if(columns == null || columns.trim().isEmpty()) 
			return; //possibly user selected cancel
		
		addColumns(x - getAbsoluteLeft(), FormDesignerUtil.convertToInt(columns), right);
	}
	
	public void addColumns(int xpos, int columns, boolean right) {
		//get biggest possible distance between two vertical lines
		int leftDiff = getWidthInt();
		
		//get biggest possible distance between two horizontal lines
		int rightDiff = leftDiff;
		boolean leftLineFound = false;
		boolean rightLineFound = false;
		
		WidgetCollection verticalLines = getVerticalLines();
		DesignWidgetWrapper startLine = null;
		List<DesignWidgetWrapper> startLines = new ArrayList<DesignWidgetWrapper>();
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int left = widget.getLeftInt();
			
			//if current line is on the left hand side of the mouse position
			if(left < xpos) {
				int diff = xpos - left; //how far, to the left, line is from mouse position
				if(diff < leftDiff) { //if this is the smallest distance between line and mouse position
					leftDiff = diff;
					if(!right) {//if inserting column before
						startLine = widget;
					}
				}
				else if (diff == leftDiff) {
					startLines.add(widget);
				}
				
				leftLineFound = true;
			}
			else if(left > xpos){ //current line on right hand side of mouse position
				int diff = left - xpos; //how far, to the right, line is from mouse position
				if(diff < rightDiff) { //if this is the smallest distance between line and mouse position
					rightDiff = diff;
					if(right) {//if inserting column after
						startLine = widget;
					}
				}
				else if (diff == rightDiff) {
					startLines.add(widget);
				}
				
				rightLineFound = true;
			}
		}
		
		if(!leftLineFound) {
			//int tableLeft = ((DesignWidgetWrapper)getParent().getParent()).getLeftInt();
			leftDiff = xpos; //(xpos - tableLeft);
			if(!right) {
				if(verticalLines.size() == 0)
					startLine = new DesignWidgetWrapper(new VerticalGridLine(getHeightInt()), null, null);
				else
					startLine = new DesignWidgetWrapper((DesignWidgetWrapper)verticalLines.get(0), null);
				
				startLine.setLeftInt(0 /*tableLeft*/);
				startLine.setTopInt(getHeaderLabelHeight() /*((DesignWidgetWrapper)getParent().getParent()).getTopInt()*/);
				startLine.setHeightInt(getHeightInt());
			}
		}
		
		if(!rightLineFound) {
			int tableRight = /*((DesignWidgetWrapper)getParent().getParent()).getLeftInt() +*/ getWidthInt() ;
			rightDiff = (tableRight - xpos);
			//if(!right) {
				if(verticalLines.size() == 0)
					startLine = new DesignWidgetWrapper(new VerticalGridLine(getHeightInt()), null, null);
				else
					startLine = new DesignWidgetWrapper((DesignWidgetWrapper)verticalLines.get(0), null);
				
				startLine.setLeftInt(tableRight);
				startLine.setTopInt(getHeaderLabelHeight() /*((DesignWidgetWrapper)getParent().getParent()).getTopInt()*/);
				startLine.setHeightInt(getHeightInt());
			//}
		}
		
		if(startLine == null)
			return;
		
		int size = (!leftLineFound && right) ? startLine.getLeftInt() : (leftDiff + rightDiff);
		int left = startLine.getLeftInt();
		int totalDisplacement = size * columns;
		
		moveVerticalLinesAndText(left, totalDisplacement);
		
		AddColumnsCmd addColumnsCmd = new AddColumnsCmd(left, totalDisplacement, this);
		
		//now add the columns
		int height = startLine.getHeightInt();
		//x = rightLineFound ? startLine.getAbsoluteLeft() : getWidthInt() + startLine.getLeftInt() - size;
		x = left + getAbsoluteLeft();
		y = startLine.getTopInt() + getAbsoluteTop();
		
		/*if(!leftLineFound && !right){
			y += getAbsoluteTop();
		}*/
		
		if(!rightLineFound /*&& right*/) {
			//y += getAbsoluteTop();
			x -= size;
		}
		/*else if(leftLineFound && !right) {
			x -= size;
		}*/
		
		this.setResizeLinesToFit(false);
		for(int i = 0; i < columns; i++) {
			x += size;
			VerticalGridLine line = new VerticalGridLine(height);
			DesignWidgetWrapper wrapper = addNewWidget(line, false);
			wrapper.setHeight(height);
			wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
			
			addColumnsCmd.addline(wrapper);
			
			int prevY = y;
			for (DesignWidgetWrapper ln : startLines) {
				y = ln.getTopInt() + getAbsoluteTop();
				
				VerticalGridLine newLine = new VerticalGridLine(ln.getHeightInt());
				DesignWidgetWrapper newWrapper = addNewWidget(newLine, false);
				newWrapper.setHeightInt(ln.getHeightInt());
				newWrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
				
				addColumnsCmd.addline(newWrapper);
			}
			y = prevY;
		}
		this.setResizeLinesToFit(true);
		
		resizeHorizontalLinesAndTable(xpos, totalDisplacement, addColumnsCmd.getResizedLines(), addColumnsCmd.getMovedLines());
		
		Context.getCommandHistory().add(addColumnsCmd);
	}
	
	public void moveVerticalLinesAndText(int left, int totalDisplacement) {
		//move all lines on the right of the mouse position
		WidgetCollection verticalLines = getVerticalLines();
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int currentLeft = widget.getLeftInt();
			if(currentLeft > left) {
				widget.setLeftInt(currentLeft + totalDisplacement);
			}
		}
		
		//move all text on the right hand side of the mouse position
		for(Widget w : ((GridPanel)selectedPanel).getNonLineWidgets()) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			if("100%".equals(widget.getWidth()))
				continue; //header label widget
			
			int currentLeft = widget.getLeftInt();
			if(currentLeft > left) {
				widget.setLeftInt(currentLeft + totalDisplacement);
			}
		}
	}
	
	public void resizeHorizontalLinesAndTable(int xpos, int totalDisplacement, Map<DesignWidgetWrapper, Integer> resizedLines, Map<DesignWidgetWrapper, Integer> movedLines) {
		//now expand the horizontal lines
		WidgetCollection horizontalLines = getHorizontalLines();
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int width = widget.getWidthInt();
			int left = widget.getLeftInt();
			if((left + width) > xpos) {
				if (left <= xpos) {
					resizedLines.put(widget, width);
					widget.setWidthInt(width + totalDisplacement);
				}
				else {
					movedLines.put(widget, left);
					widget.setLeftInt(left + totalDisplacement);
				}
			}
		}
		
		setWidth(getWidthInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public void moveHorizontalLinesAndText(int top, int totalDisplacement) {
		//move all lines below the mouse position
		WidgetCollection horizontalLines = getHorizontalLines();
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int currentTop = widget.getTopInt();
			if(currentTop > top) {
				widget.setTopInt(currentTop + totalDisplacement);
			}
		}
		
		//move all text below the mouse position
		for(Widget w : ((GridPanel)selectedPanel).getNonLineWidgets()) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			if("100%".equals(widget.getWidth()))
				continue; //header label widget
			
			int currentTop = widget.getTopInt();
			if(currentTop > top) {
				widget.setTopInt(currentTop + totalDisplacement);
			}
		}
	}
	
	public void resizeVerticalLinesAndTable(int ypos, int totalDisplacement, Map<DesignWidgetWrapper, Integer> resizedLines, Map<DesignWidgetWrapper, Integer> movedLines) {
		//now expand the vertical lines
		WidgetCollection verticalLines = getVerticalLines();
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int height = widget.getHeightInt();
			int top = widget.getTopInt();
			if((top + height) > ypos) {
				if (top <= ypos) {
					resizedLines.put(widget, height);
					widget.setHeightInt(height + totalDisplacement);
				}
				else {
					movedLines.put(widget, top);
					widget.setTopInt(top + totalDisplacement);
				}
			}
		}
		
		setHeight(getHeightInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public void add(DesignWidgetWrapper line) {
		selectedPanel.add(line);
		selectedPanel.setWidgetPosition(line, line.getLeftInt(), line.getTopInt());
	}
	
	public void deleteColumn() {
		int xpos = x - getAbsoluteLeft();
		
		//get biggest possible distance between two vertical lines
		int leftDiff = getWidthInt();
		
		//get biggest possible distance between two horizontal lines
		int rightDiff = leftDiff;
		boolean leftLineFound = false;
		boolean rightLineFound = false;
		
		WidgetCollection verticalLines = getVerticalLines();
		DesignWidgetWrapper rightLine = null;
		DesignWidgetWrapper leftLine = null;
		List<DesignWidgetWrapper> leftLines = new ArrayList<DesignWidgetWrapper>();
		List<DesignWidgetWrapper> rightLines = new ArrayList<DesignWidgetWrapper>();
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int left = widget.getLeftInt();
			
			//if current line is on the left hand side of the mouse position
			if(left < xpos) {
				int diff = xpos - left; //how far, to the left, line is from mouse position
				if(diff < leftDiff) { //if this is the smallest distance between line and mouse position
					leftDiff = diff;
					leftLine = widget;
				}
				else if (diff == leftDiff) {
					leftLines.add(widget);
				}
				
				leftLineFound = true;
			}
			else if(left > xpos){ //current line on right hand side of mouse position
				int diff = left - xpos; //how far, to the right, line is from mouse position
				if(diff < rightDiff) { //if this is the smallest distance between line and mouse position
					rightDiff = diff;
					rightLine = widget;
				}
				else if (diff == rightDiff) {
					rightLines.add(widget);
				}
				
				rightLineFound = true;
			}
		}
		
		int left = xpos;
		
		if(!leftLineFound) {
			int tableLeft = 0; //((DesignWidgetWrapper)getParent().getParent()).getLeftInt();
			leftDiff = xpos - tableLeft;
			left = tableLeft;
		}
		else
			left = leftLine.getLeftInt();
		
		if(!rightLineFound) {
			int tableRight = /*((DesignWidgetWrapper)getParent().getParent()).getLeftInt() +*/ getWidthInt() ;
			rightDiff = tableRight - xpos;
		}
		
		int totalDisplacement = leftDiff + rightDiff;
		DeleteColumnCmd deleteColumnCmd = new DeleteColumnCmd(left, totalDisplacement, this);
		
		if(rightLine != null) {
			rightLine.storePosition();
			remove(rightLine);
			deleteColumnCmd.deleteLine(rightLine);
			for (DesignWidgetWrapper line : rightLines) {
				line.storePosition();
				remove(line);
				deleteColumnCmd.deleteLine(line);
			}
		}
		else {
			leftLine.storePosition();
			remove(leftLine);
			deleteColumnCmd.deleteLine(leftLine);
			for (DesignWidgetWrapper line : leftLines) {
				line.storePosition();
				remove(line);
				deleteColumnCmd.deleteLine(line);
			}
		}
		
		moveVerticalLinesAndText(left, -totalDisplacement);
		resizeHorizontalLinesAndTable(left, -totalDisplacement, deleteColumnCmd.getResizedLines(), deleteColumnCmd.getMovedLines());
		
		Context.getCommandHistory().add(deleteColumnCmd);
	}
	
	public void deleteRow() {
		int ypos = y - getAbsoluteTop();
		
		//get biggest possible distance between two horizontal lines
		int topDiff = getHeightInt();
		
		//get biggest possible distance between two vertical lines
		int belowDiff = topDiff;
		boolean topLineFound = false;
		boolean belowLineFound = false;
		
		WidgetCollection horizontalLines = getHorizontalLines();
		DesignWidgetWrapper belowLine = null;
		DesignWidgetWrapper topLine = null;
		List<DesignWidgetWrapper> topLines = new ArrayList<DesignWidgetWrapper>();
		List<DesignWidgetWrapper> belowLines = new ArrayList<DesignWidgetWrapper>();
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int top = widget.getTopInt();
			
			//if current line is above the mouse position
			if(top < ypos) {
				int diff = ypos - top; //how far, to the top, line is from mouse position
				if(diff < topDiff) { //if this is the smallest distance between line and mouse position
					topDiff = diff;
					topLine = widget;
				}
				else if(diff == topDiff) {
					topLines.add(widget);
				}
				
				topLineFound = true;
			}
			else if(top > ypos){ //current line below the mouse position
				int diff = top - ypos; //how far, to the top, line is from mouse position
				if(diff < belowDiff) { //if this is the smallest distance between line and mouse position
					belowDiff = diff;
					belowLine = widget;
				}
				else if(diff == belowDiff) {
					belowLines.add(widget);
				}
				
				belowLineFound = true;
			}
		}
		
		int top = ypos;
		
		if(!topLineFound) {
			int tableTop = 0; //((DesignWidgetWrapper)getParent().getParent()).getTopInt();
			topDiff = ypos - tableTop;
			top = tableTop;
		}
		else
			top = topLine.getTopInt();
		
		if(!belowLineFound) {
			int tableBottom = /*((DesignWidgetWrapper)getParent().getParent()).getTopInt() +*/ getHeightInt() ;
			belowDiff = tableBottom - ypos;
		}
		
		int totalDisplacement = topDiff + belowDiff;
		DeleteRowCmd deleteRowCmd = new DeleteRowCmd(top, totalDisplacement, this);
		
		if(belowLine != null) {
			belowLine.storePosition();
			remove(belowLine);
			deleteRowCmd.deleteLine(belowLine);
			for (DesignWidgetWrapper line : belowLines) {
				line.storePosition();
				remove(line);
				deleteRowCmd.deleteLine(line);
			}
		}
		else {
			topLine.storePosition();
			remove(topLine);
			deleteRowCmd.deleteLine(topLine);
			for (DesignWidgetWrapper line : topLines) {
				line.storePosition();
				remove(line);
				deleteRowCmd.deleteLine(line);
			}
		}
		
		moveHorizontalLinesAndText(top, -totalDisplacement);
		resizeVerticalLinesAndTable(top, -totalDisplacement, deleteRowCmd.getResizedLines(), deleteRowCmd.getMovedLines());
		
		Context.getCommandHistory().add(deleteRowCmd);
	}
	
	public WidgetCollection getHorizontalLines(){
		return ((GridPanel)selectedPanel).getHorizontalLines();
	}
	
	public WidgetCollection getVerticalLines(){
		return ((GridPanel)selectedPanel).getVerticalLines();
	}
	
	public List<DesignWidgetWrapper> getHorizontalLinesCopy(){
		List<DesignWidgetWrapper> list = new ArrayList<DesignWidgetWrapper>();
		WidgetCollection horizontalLines = getHorizontalLines();
		for(Widget w : horizontalLines) {
			list.add((DesignWidgetWrapper)w);
		}
		
		return list;
	}
	
	public List<DesignWidgetWrapper> getVerticalLinesCopy(){
		List<DesignWidgetWrapper> list = new ArrayList<DesignWidgetWrapper>();
		WidgetCollection verticalLines = getVerticalLines();
		for(Widget w : verticalLines) {
			list.add((DesignWidgetWrapper)w);
		}
		
		return list;
	}
	
	public void setResizeLinesToFit(boolean resizeLinesToFit) {
		((GridPanel)selectedPanel).setResizeLinesToFit(resizeLinesToFit);
	}
	
	public void mergeCells() {
		MergeCellsCmd mergeCellsCmd = new MergeCellsCmd(this);
		
		mergeHorizontalLines(mergeCellsCmd);
		mergeVerticalLines(mergeCellsCmd);
		
		Context.getCommandHistory().add(mergeCellsCmd);
	}
	
	public void mergeHorizontalLines(MergeCellsCmd mergeCellsCmd) {
		int leftDiff = getWidthInt();
		int rightDiff = leftDiff;
		
		boolean leftLineFound = false;
		boolean rightLineFound = false;
		
		WidgetCollection verticalLines = getVerticalLines();
		DesignWidgetWrapper rightLine = null;
		DesignWidgetWrapper leftLine = null;
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int left = widget.getLeftInt();
			
			//if current line is on the left hand side of the mouse position
			if(left < rubberBandLeft) {
				int diff = rubberBandLeft - left; //how far, to the left, line is from mouse position
				if(diff < leftDiff) { //if this is the smallest distance between line and mouse position
					leftDiff = diff;
					leftLine = widget;
				}
				leftLineFound = true;
			}
			else if(left > rubberBandRight){ //current line on right hand side of mouse position
				int diff = left - rubberBandLeft; //how far, to the right, line is from mouse position
				if(diff < rightDiff) { //if this is the smallest distance between line and mouse position
					rightDiff = diff;
					rightLine = widget;
				}
				rightLineFound = true;
			}
		}
		
		int leftLineX = 0;
		if(leftLineFound) {
			leftLineX = leftLine.getLeftInt();
		}
		
		this.setResizeLinesToFit(false);
		List<DesignWidgetWrapper> horizontalLines = getHorizontalLinesCopy();
		for(DesignWidgetWrapper widget : horizontalLines) {
			int left = widget.getLeftInt();
			int top = widget.getTopInt();
			int right = left + widget.getWidthInt();
			if (left < rubberBandLeft && right > rubberBandLeft && top > rubberBandTop && top < rubberBandBottom) {
				
				if (!leftLineFound) {
					widget.storePosition();
					mergeCellsCmd.removeLine(widget);
					remove(widget);
				}
				else {
					int oldWidth = widget.getWidthInt();
					int newWidth = leftLineX - left;
					widget.setWidthInt(newWidth);
					mergeCellsCmd.addResizedHorizontalLine(widget, oldWidth);
				}
				
				if (!rightLineFound)
					continue;
				
				x = rightLine.getLeftInt() + getAbsoluteLeft();
				y = top + getAbsoluteTop();
				int width = right - rightLine.getLeftInt();
				HorizontalGridLine line = new HorizontalGridLine(width);
				DesignWidgetWrapper wrapper = addNewWidget(line, false);
				wrapper.setWidthInt(width);
				wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
				
				mergeCellsCmd.addLine(wrapper);
			}
		}
		this.setResizeLinesToFit(true);
	}
	
	public void mergeVerticalLines(MergeCellsCmd mergeCellsCmd) {
		int topDiff = getHeightInt();
		int bottomDiff = topDiff;
		
		boolean topLineFound = false;
		boolean bottomLineFound = false;
		
		WidgetCollection horizontalLines = getHorizontalLines();
		DesignWidgetWrapper topLine = null;
		DesignWidgetWrapper bottomLine = null;
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int top = widget.getTopInt();
			
			//if current line is above the mouse position
			if(top < rubberBandTop) {
				int diff = rubberBandTop - top; //how far, above, line is from mouse position
				if(diff < topDiff) { //if this is the smallest distance between line and mouse position
					topDiff = diff;
					topLine = widget;
				}
				topLineFound = true;
			}
			else if(top > rubberBandBottom){ //current line below mouse position
				int diff = top - rubberBandBottom; //how far, to the bottom, line is from mouse position
				if(diff < bottomDiff) { //if this is the smallest distance between line and mouse position
					bottomDiff = diff;
					bottomLine = widget;
				}
				bottomLineFound = true;
			}
		}
		
		int topLineY = 0;
		if(topLineFound) {
			topLineY = topLine.getTopInt();
		}
		
		this.setResizeLinesToFit(false);
		List<DesignWidgetWrapper> verticalLines = getVerticalLinesCopy();
		for(DesignWidgetWrapper widget : verticalLines) {
			int top = widget.getTopInt();
			int left = widget.getLeftInt();
			int bottom = top + widget.getHeightInt();
			if (top < rubberBandTop && bottom > rubberBandTop && left > rubberBandLeft && left < rubberBandRight) {
				
				if (!topLineFound) {
					widget.storePosition();
					mergeCellsCmd.removeLine(widget);
					remove(widget);
				}
				else {
					int oldHeight = widget.getHeightInt();
					int newHeight = topLineY - top;
					widget.setHeightInt(newHeight);
					mergeCellsCmd.addResizedVerticalLine(widget, oldHeight);
				}
				
				if (!bottomLineFound)
					continue;
				
				y = bottomLine.getTopInt() + getAbsoluteTop();
				x = left + getAbsoluteLeft();
				int height = bottom - bottomLine.getTopInt();
				VerticalGridLine line = new VerticalGridLine(height);
				DesignWidgetWrapper wrapper = addNewWidget(line, false);
				wrapper.setHeightInt(height);
				wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
				
				mergeCellsCmd.addLine(wrapper);
			}
		}
		this.setResizeLinesToFit(true);
	}
	
	public void resizeHorizontalLines(Map<DesignWidgetWrapper, Integer> resizedLines, Map<DesignWidgetWrapper, Integer> movedLines, int totalDisplacement) {
		for(DesignWidgetWrapper line : resizedLines.keySet()) {
			int width = resizedLines.get(line);
			resizedLines.put(line, line.getWidthInt());
			line.setWidthInt(width);
		}
		
		for(DesignWidgetWrapper line : movedLines.keySet()) {
			int left = movedLines.get(line);
			movedLines.put(line, line.getLeftInt());
			line.setLeftInt(left);
		}
		
		setWidth(getWidthInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public void resizeVerticalLines(Map<DesignWidgetWrapper, Integer> resizedLines, Map<DesignWidgetWrapper, Integer> movedLines, int totalDisplacement) {
		for(DesignWidgetWrapper line : resizedLines.keySet()) {
			int width = resizedLines.get(line);
			resizedLines.put(line, line.getHeightInt());
			line.setHeightInt(width);
		}
		
		for(DesignWidgetWrapper line : movedLines.keySet()) {
			int top = movedLines.get(line);
			movedLines.put(line, line.getTopInt());
			line.setTopInt(top);
		}
		
		setHeight(getHeightInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public int getHeaderLabelHeight() {
		return ((GridPanel)selectedPanel).getLabelHeight();
	}
}