package org.purc.purcforms.client.widget.grid;

import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.LeftPanel.Images;
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
		int rightDiff = getHeightInt();
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
		for(Widget w : verticalLines) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			int currentLeft = widget.getLeftInt();
			if(currentLeft > left) {
				widget.setLeftInt(currentLeft + totalDisplacement);
			}
		}
		
		//move all test on the right hand side of the mouse position
		for(Widget w : ((GridPanel)selectedPanel).getNonLineWidgets()) {
			DesignWidgetWrapper widget = (DesignWidgetWrapper)w;
			if("100%".equals(widget.getWidth()))
				continue; //header label widget
			
			int currentLeft = widget.getLeftInt();
			if(currentLeft > left) {
				widget.setLeftInt(currentLeft + totalDisplacement);
			}
		}
		
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
		}
		
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
}