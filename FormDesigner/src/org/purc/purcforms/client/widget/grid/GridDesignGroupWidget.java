package org.purc.purcforms.client.widget.grid;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.LeftPanel.Images;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.cmd.AddColumnsCmd;
import org.purc.purcforms.client.cmd.AddRowsCmd;
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
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addRowsBelow")), true, new Command(){
			public void execute() {popup.hide(); addRows(true);}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addRowsAbove")), true, new Command(){
			public void execute() {popup.hide(); addRows(false);}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addColumnsRight")), true, new Command(){
			public void execute() {popup.hide(); addColumns(true);}});
		
		tableMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(), LocaleText.get("addColumnsLeft")), true, new Command(){
			public void execute() {popup.hide(); addColumns(false);}});
		
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
		String columns = Window.prompt(LocaleText.get("numberOfRowsPrompt"), "1");
		addRows(y - getAbsoluteTop(), FormDesignerUtil.convertToInt(columns), below);
	}
	
	public void addRows(int ypos, int rows, boolean below) {
		//get biggest possible distance between two horizontal lines
		int topDiff = getHeightInt();
		
		//get biggest possible distance between two horizontal lines
		int bottomDiff = topDiff;
		boolean topLineFound = false;
		boolean bottomLineFound = false;
		
		WidgetCollection horizontalLines = ((GridPanel)selectedPanel).getHorizontalLines();
		DesignWidgetWrapper startLine = null;
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
				bottomLineFound = true;
			}
		}
		
		if(!topLineFound) {
			int tableTop = ((DesignWidgetWrapper)getParent().getParent()).getTopInt();
			topDiff = ypos - tableTop;
			if(below) {
				if(horizontalLines.size() == 0)
					return;
				
				startLine = (DesignWidgetWrapper)horizontalLines.get(0);
				startLine.setTopInt(tableTop);
				startLine.setLeftInt(((DesignWidgetWrapper)getParent().getParent()).getLeftInt());
				startLine.setWidthInt(getWidthInt());
			}
		}
		
		if(!bottomLineFound) {
			int tableBottom = ((DesignWidgetWrapper)getParent().getParent()).getTopInt() + getHeightInt() ;
			bottomDiff = (tableBottom - ypos);
			if(!below) {
				if(horizontalLines.size() == 0)
					return;
				
				startLine = (DesignWidgetWrapper)horizontalLines.get(0);
				startLine.setTopInt(tableBottom);
				startLine.setLeftInt(((DesignWidgetWrapper)getParent().getParent()).getLeftInt());
				startLine.setWidthInt(getWidthInt());
			}
		}
		
		if(startLine == null)
			return;
		
		int size = topDiff + bottomDiff;
		int top = startLine.getTopInt();
		int totalDisplacement = size * rows;
		
		moveHorizontalLinesAndText(top, totalDisplacement);
		
		AddRowsCmd addRowsCmd = new AddRowsCmd(top, below, totalDisplacement, this);
		
		//now add the rows
		int width = startLine.getWidthInt();
		x = startLine.getAbsoluteLeft();
		y = startLine.getTopInt() + getAbsoluteTop();
		
		for(int i = 0; i < rows; i++) {
			y += size;
			HorizontalGridLine line = new HorizontalGridLine(width);
			DesignWidgetWrapper wrapper = addNewWidget(line, false);
			wrapper.setWidthInt(width);
			wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
			
			addRowsCmd.addline(wrapper);
		}
		
		resizeVerticalLinesAndTable(ypos, totalDisplacement);
		
		Context.getCommandHistory().add(addRowsCmd);
	}
	
	public void addColumns(boolean right) {
		String columns = Window.prompt(LocaleText.get("numberOfColumnsPrompt"), "1");
		addColumns(x - getAbsoluteLeft(), FormDesignerUtil.convertToInt(columns), right);
	}
	
	public void addColumns(int xpos, int columns, boolean right) {
		//get biggest possible distance between two vertical lines
		int leftDiff = getWidthInt();
		
		//get biggest possible distance between two horizontal lines
		int rightDiff = leftDiff;
		boolean leftLineFound = false;
		boolean rightLineFound = false;
		
		WidgetCollection verticalLines = ((GridPanel)selectedPanel).getVerticalLines();
		DesignWidgetWrapper startLine = null;
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
				rightLineFound = true;
			}
		}
		
		if(!leftLineFound) {
			int tableLeft = ((DesignWidgetWrapper)getParent().getParent()).getLeftInt();
			leftDiff = xpos - tableLeft;
			if(right) {
				if(verticalLines.size() == 0)
					return;
				
				startLine = (DesignWidgetWrapper)verticalLines.get(0);
				startLine.setLeftInt(tableLeft);
				startLine.setTopInt(((DesignWidgetWrapper)getParent().getParent()).getTopInt());
				startLine.setHeightInt(getHeightInt());
			}
		}
		
		if(!rightLineFound) {
			int tableRight = ((DesignWidgetWrapper)getParent().getParent()).getLeftInt() + getWidthInt() ;
			rightDiff = (tableRight - xpos);
			if(!right) {
				if(verticalLines.size() == 0)
					return;
				
				startLine = (DesignWidgetWrapper)verticalLines.get(0);
				startLine.setLeftInt(tableRight);
				startLine.setTopInt(((DesignWidgetWrapper)getParent().getParent()).getTopInt());
				startLine.setHeightInt(getHeightInt());
			}
		}
		
		if(startLine == null)
			return;
		
		int size = leftDiff + rightDiff;
		int left = startLine.getLeftInt();
		int totalDisplacement = size * columns;
		
		moveVerticalLinesAndText(left, totalDisplacement);
		
		AddColumnsCmd addColumnsCmd = new AddColumnsCmd(left, right, totalDisplacement, this);
		
		//now add the columns
		int height = startLine.getHeightInt();
		x = startLine.getAbsoluteLeft();
		y = startLine.getTopInt();
		
		for(int i = 0; i < columns; i++) {
			x += size;
			VerticalGridLine line = new VerticalGridLine(height);
			DesignWidgetWrapper wrapper = addNewWidget(line, false);
			wrapper.setHeight(height);
			wrapper.setBorderColor(FormUtil.getDefaultGroupBoxHeaderBgColor());
			
			addColumnsCmd.addline(wrapper);
		}
		
		resizeHorizontalLinesAndTable(xpos, totalDisplacement);
		
		Context.getCommandHistory().add(addColumnsCmd);
	}
	
	public void moveVerticalLinesAndText(int left, int totalDisplacement) {
		//move all lines on the right of the mouse position
		WidgetCollection verticalLines = ((GridPanel)selectedPanel).getVerticalLines();
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
	
	public void resizeHorizontalLinesAndTable(int xpos, int totalDisplacement) {
		//now expand the horizontal lines
		WidgetCollection horizontalLines = ((GridPanel)selectedPanel).getHorizontalLines();
		for(Widget w : horizontalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int width = widget.getWidthInt();
			if((widget.getLeftInt() + width) > xpos) {
				widget.setWidthInt(width + totalDisplacement);
			}
		}
		
		setWidth(getWidthInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public void moveHorizontalLinesAndText(int top, int totalDisplacement) {
		//move all lines below the mouse position
		WidgetCollection horizontalLines = ((GridPanel)selectedPanel).getHorizontalLines();
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
	
	public void resizeVerticalLinesAndTable(int ypos, int totalDisplacement) {
		//now expand the vertical lines
		WidgetCollection verticalLines = ((GridPanel)selectedPanel).getVerticalLines();
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int height = widget.getHeightInt();
			if((widget.getTopInt() + height) > ypos) {
				widget.setHeightInt(height + totalDisplacement);
			}
		}
		
		setHeight(getHeightInt() + totalDisplacement + PurcConstants.UNITS);
	}
	
	public void add(DesignWidgetWrapper line) {
		selectedPanel.add(line);
		selectedPanel.setWidgetPosition(line, line.getLeftInt(), line.getTopInt());
	}
}