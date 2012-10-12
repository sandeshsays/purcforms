
package org.purc.purcforms.client.widget.grid;

import com.google.gwt.user.client.ui.Widget;

/**
 * To store in order to restore the position of a widget
 * 
 * @author Ken Kahn
 *
 */
public class LocatedWidget {

    private Widget widget;
    private int absoluteLeft;
    private int absoluteTop;

    public LocatedWidget(Widget widget, int absoluteLeft, int absoluteTop) {
	this.widget = widget;
	this.absoluteLeft = absoluteLeft;
	this.absoluteTop = absoluteTop;
	
    }

    public Widget getWidget() {
        return widget;
    }

    public int getAbsoluteLeft() {
        return absoluteLeft;
    }

    public int getAbsoluteTop() {
        return absoluteTop;
    }

}
