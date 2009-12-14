package org.purc.purcforms.client.widget;

import org.purc.purcforms.client.controller.IFormActionListener;
import org.purc.purcforms.client.util.FormDesignerUtil;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * Widget for tree items which gives them a context menu.
 * 
 * @author daniel
 *
 */
public class TreeItemWidget extends Composite{

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
	public TreeItemWidget(AbstractImagePrototype imageProto, String caption, PopupPanel popup,IFormActionListener formActionListener){

		this.popup = popup;
		this.formActionListener = formActionListener;

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);

		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(imageProto.createImage());
		HTML headerText = new HTML(caption);
		hPanel.add(headerText);
		hPanel.setStyleName("gwt-noWrap");
		initWidget(hPanel);

		DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONMOUSEDOWN | Event.ONKEYDOWN );
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			if( (event.getButton() & Event.BUTTON_RIGHT) != 0 /*&& !Context.isStructureReadOnly()*/){	  
				popup.setPopupPosition(event.getClientX(), event.getClientY());
				FormDesignerUtil.disableContextMenu(popup.getElement());
				popup.show();
			}
		}
		else if(DOM.eventGetType(event) == Event.ONKEYDOWN){
			if(event.getKeyCode() == KeyboardListener.KEY_DELETE)
				formActionListener.deleteSelectedItem();
		}
	}
}
