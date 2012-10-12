package org.purc.purcforms.client.widget;

import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Widget for tree items which gives them a context menu.
 * 
 * @author daniel
 *
 */
public class TreeItemWidget extends Composite implements HasAllMouseHandlers, HasAllTouchHandlers {

	/** Popup panel for the context menu. */
	private PopupPanel popup;

	/** Listener for form action events. */
	private IFormActionListener formActionListener;


	/**
	 * Creates a new tree item.
	 * 
	 * @param imageProto the item image.
	 * @param caption the time caption or text.
	 * @param popup the pop up panel for context menu.
	 * @param formActionListener listener to form action events.
	 */
	public TreeItemWidget(ImageResource imageProto, String caption, PopupPanel popup, IFormActionListener formActionListener){

		this.popup = popup;
		this.formActionListener = formActionListener;

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);

		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(FormUtil.createImage(imageProto));
		//HTML headerText = new HTML(caption); //Replaced with Label due to bug which surfaces when we have text including things like "<sdsff>"
		Widget headerText = new Label(caption);
		hPanel.add(headerText);
		hPanel.setStyleName("gwt-noWrap");
		initWidget(hPanel);

		DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) /*| Event.ONMOUSEDOWN */| Event.ONKEYDOWN  | Event.MOUSEEVENTS);
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if( (event.getButton() & Event.BUTTON_RIGHT) != 0 /*&& !Context.isStructureReadOnly()*/){	  
				
				int ypos = event.getClientY();
				if(Window.getClientHeight() - ypos < 350)
					ypos = event.getClientY() - 350;
					
				FormDesignerUtil.disableContextMenu(popup.getElement());
				FormDesignerUtil.disableContextMenu(getElement());
				popup.setPopupPosition(event.getClientX(), ypos);
				popup.show();
			}
			else {
				super.onBrowserEvent(event);
			}
		}
		else if(DOM.eventGetType(event) == Event.ONKEYDOWN){
			if(event.getKeyCode() == KeyCodes.KEY_DELETE)
				formActionListener.deleteSelectedItem();
		}
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
}
