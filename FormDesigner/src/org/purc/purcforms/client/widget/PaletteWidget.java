package org.purc.purcforms.client.widget;

import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.FormsTreeView.Images;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;


/**
 * This is a widget which can be put on the palette for users to drag and drop
 * on the design surface.
 * 
 * @author daniel
 *
 */
public class PaletteWidget extends Composite implements HasAllMouseHandlers, HasAllTouchHandlers{

	/** The name of the widget. e.g TextBox, CheckBox, Label,Button etc */
	private String name;

	/**
	 * Creates a new instance of the palette.
	 * 
	 * @param images the palette images.
	 * @param html the name of the widget
	 */
	public PaletteWidget(Images images, HTML html){
		name = html.getText();

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		hPanel.add(FormUtil.createImage(images.add()));
		hPanel.add(html);
		initWidget(hPanel);

		DOM.sinkEvents(getElement(),DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS);

		DOM.setStyleAttribute(getElement(), "cursor", "pointer");
	}

	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		return addDomHandler(handler, MouseWheelEvent.getType());
	}

	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}
	
	public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
		return addDomHandler(handler, TouchMoveEvent.getType());
	}
	
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}
	
	public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
		return addDomHandler(handler, TouchCancelEvent.getType());
	}

	/**
	 * Gets the name of the widget.
	 * 
	 * @return the name
	 */
	public String getName(){
		return name;
	}
}
