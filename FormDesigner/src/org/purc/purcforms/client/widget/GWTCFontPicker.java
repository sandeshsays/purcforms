/*
 * Copyright 2010 Manuel Carrasco Mo̱ino. (manuel_carrasco at users.sourceforge.net) 
 * http://code.google.com/p/gwtchismes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.purc.purcforms.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.RichTextArea.FontSize;

/**
 * Font picker 
 */
public class GWTCFontPicker extends PopupPanel implements ClickHandler, HasValueChangeHandlers<GWTCFontPicker> {

    private class FontCell extends HTML {
        String theFont;

        public FontCell(String font) {
            super(font);
            this.theFont = font;
            DOM.setStyleAttribute(getElement(), "backgroundColor", "#D8ECFD");
            DOM.setStyleAttribute(getElement(), "padding", "2px 4px 2px 8px");
            addMouseOverHandler(new MouseOverHandler() {
                public void onMouseOver(MouseOverEvent event) {
                    DOM.setStyleAttribute(getElement(), "backgroundColor", "#7FAAFF");
                }
            });
            addMouseOutHandler(new MouseOutHandler() {
                public void onMouseOut(MouseOutEvent event) {
                    DOM.setStyleAttribute(getElement(), "backgroundColor", "#D8ECFD");
                }
            });
        }

        public String getFont() {
            return theFont;
        }

    }

    public enum FontPickerType {
        FONT_FAMILY, FONT_SIZE
    }

    private static final String[] fontFamilies = new String[] { "Times New Roman", "Arial", "Courier New", "Georgia", "Trebuchet", "Verdana", "Comic Sans MS" };

    private static final String[] fontSizes = new String[] {"12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"}; //new String[] { "xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large" };

    private String font = "";

    ValueChangeHandler<GWTCFontPicker> changeHandler = null;

    public GWTCFontPicker(FontPickerType type) {
        super(true);
        VerticalPanel container = new VerticalPanel();
        DOM.setStyleAttribute(container.getElement(), "border", "1px solid  #7FAAFF");
        DOM.setStyleAttribute(container.getElement(), "backgroundColor", "#D8ECFD");
        DOM.setStyleAttribute(container.getElement(), "cursor", "pointer");

        String[] fonts = type == FontPickerType.FONT_SIZE ? fontSizes : fontFamilies;

        for (int i = 0; i < fonts.length; i++) {
            FontCell cell;
            if (type == FontPickerType.FONT_SIZE) {
                cell = new FontCell( fonts[i] /*"" + (i + 1)*/);
                DOM.setStyleAttribute(cell.getElement(), "fontSize", fonts[i]);
            } else {
                cell = new FontCell(fonts[i]);
                DOM.setStyleAttribute(cell.getElement(), "fontFamily", fonts[i]);
            }
            cell.addClickHandler(this);
            container.add(cell);
        }

        add(container);
        setAnimationEnabled(true);
        setStyleName("hupa-color-picker");
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<GWTCFontPicker> handler) {
        assert changeHandler == null : "Change handler is already defined";
        changeHandler = handler;
        return new HandlerRegistration() {
            public void removeHandler() {
                changeHandler = null;
            }
        };
    }

    public String getFontName() {
        return font;
    }

    public String getFontSize() {
        /*switch (Integer.valueOf(font).intValue()) {
        case 1:
            return FontSize.XX_SMALL;
        case 2:
            return FontSize.X_SMALL;
        case 4:
            return FontSize.MEDIUM;
        case 5:
            return FontSize.LARGE;
        case 6:
            return FontSize.X_LARGE;
        case 7:
            return FontSize.XX_LARGE;
        case 3:
        default:
            return FontSize.SMALL;
        }*/
    	
    	return font;
    }

    public void onClick(ClickEvent event) {
        FontCell cell = (FontCell) event.getSource();
        this.font = cell.getFont();
        if (changeHandler != null)
        	this.hide();
            changeHandler.onValueChange(new ValueChangeEvent<GWTCFontPicker>(this) {
            });
    }

}