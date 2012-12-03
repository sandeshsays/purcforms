package org.purc.purcforms.client.widget.grid;

import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.LeftPanel.Images;
import org.purc.purcforms.client.controller.IWidgetPopupMenuListener;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DesignGroupWidget;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwt.user.client.ui.Widget;
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
	
	public void resizeGrid(int widthChange, int heightChange, int width, int height){
		if(!DragContext.controlKeyPressed) {
			((GridPanel)selectedPanel).resizeGrid(widthChange, heightChange, width, height);
		}
		else
			((GridPanel)selectedPanel).resizeGridWithCtrlPressed(widthChange, heightChange, width, height);
	}
	
	public void moveLine(int xChange, int yChange, int newLeft, int newTop){	
		if(!((GridPanel)selectedPanel).isResizeLinesToFit())
			return;
		
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
}