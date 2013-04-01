package org.purc.purcforms.client.widget.grid;

import org.purc.purcforms.client.PurcConstants;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

public class GridLine extends HTML {
	
	public GridLine() {
		DOM.setStyleAttribute(getElement(), "borderStyle", "solid");
		DOM.setStyleAttribute(getElement(), "borderWidth", "1" + PurcConstants.UNITS);
		DOM.setStyleAttribute(getElement(), "borderBottomStyle", "none");
		DOM.setStyleAttribute(getElement(), "borderRightStyle", "none");
	}
}
