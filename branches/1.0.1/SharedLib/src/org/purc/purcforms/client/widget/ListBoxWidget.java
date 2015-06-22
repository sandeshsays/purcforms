package org.purc.purcforms.client.widget;

import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ListBox;


/**
 * This class is only to enable us have list boxes that can be locked
 * 
 * @author daniel
 *
 */
public class ListBoxWidget extends ListBox{
	
	/** 
	 * This allows us keep track of the selected index such that we can restore it
	 * whenever the user tries to change the selected value of a locked list box.
	 * We had to do this because for now we have not been successful at disabling 
	 * mouse clicks on locked list boxes.
	 */
	private int selectedIndex = -1;
	
	/**
	 * Creates a new instance of the list box widget.
	 * 
	 * @param isMultipleSelect set to true if you want to allow multiple selection.
	 */
	public ListBoxWidget(boolean isMultipleSelect){
		super(isMultipleSelect);
	    sinkEvents(Event.getTypeInt(ChangeEvent.getType().getName()) | Event.ONKEYPRESS);
	}

	
	@Override
	public void onBrowserEvent(Event event){
		if(DOM.eventGetType(event) == Event.ONCHANGE){
			if(getParent().getParent() instanceof RuntimeWidgetWrapper &&
					((RuntimeWidgetWrapper)getParent().getParent()).isLocked()){
				super.setSelectedIndex(selectedIndex);
				return;
			}
			else if(getParent().getParent() instanceof RuntimeWidgetWrapper) {
				int index = getSelectedIndex();
				if (index != -1) {
					String value = getValue(index);
					if (value.contains("?target=xformentry&formId=")) {
						FormUtil.setAfterSubmitUrlSuffix(value);
					}
				}
			}
		}
		else if (DOM.eventGetType(event) == Event.ONKEYPRESS) {
			int code = event.getCharCode();
			if (code > 0) {
				String s = String.valueOf((char)code);
				int count = getItemCount();
				for (int index = 0; index < count; index++) {
					String value = getValue(index);
					if (value.startsWith(s)) {
						setSelectedIndex(index);
						break;
					}
				}
			}
		}

		super.onBrowserEvent(event);
	}
	
	
	/**
	 * @see com.google.gwt.user.client.ui.ListBox#setSelectedIndex(int)
	 */
	public void setSelectedIndex(int index) {
		 selectedIndex = index;
		 super.setSelectedIndex(index);
	}
}
