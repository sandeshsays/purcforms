package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.google.gwt.user.client.ui.AbsolutePanel;


/**
 * Command for changing property of a widget.
 * 
 * @author danielkayiwa
 *
 */
public class ChangeWidgetCmd implements ICommand {

	public static final byte PROPERTY_TEXT = 1;
	public static final byte PROPERTY_TOOLTIP = 2;
	public static final byte PROPERTY_BINDING = 3;
	public static final byte PROPERTY_CHILD_BINDING = 4;
	public static final byte PROPERTY_WIDTH = 5;
	public static final byte PROPERTY_HEIGHT = 6;
	public static final byte PROPERTY_ENABLED = 7;
	public static final byte PROPERTY_VISIBLE = 8;
	public static final byte PROPERTY_LEFT = 9;
	public static final byte PROPERTY_TOP = 10;
	public static final byte PROPERTY_TAB_INDEX = 11;
	public static final byte PROPERTY_REPEAT = 12;
	public static final byte PROPERTY_EXTERNAL_SOURCE = 13;
	public static final byte PROPERTY_DISPLAY_FIELD = 14;
	public static final byte PROPERTY_VALUE_FIELD = 15;
	public static final byte PROPERTY_FILTER_FIELD = 16;

	public static final byte PROPERTY_FONT_FAMILY = 17;
	public static final byte PROPERTY_FORE_COLOR = 18;
	public static final byte PROPERTY_FONT_WEIGHT = 19;
	public static final byte PROPERTY_FONT_STYLE = 20;
	public static final byte PROPERTY_FONT_SIZE = 21;

	public static final byte PROPERTY_TEXT_DECORATION = 22;
	public static final byte PROPERTY_TEXT_ALIGN = 23;
	public static final byte PROPERTY_BACKGROUND_COLOR = 24;
	public static final byte PROPERTY_BORDER_STYLE = 25;
	public static final byte PROPERTY_BORDER_WIDTH = 26;
	public static final byte PROPERTY_BORDER_COLOR = 27;
	public static final byte PROPERTY_ID = 28;

	protected DesignGroupView view;
	protected DesignWidgetWrapper widget;
	protected AbsolutePanel panel;
	protected byte property;
	protected String oldValue;

	protected ChangeWidgetCmd(){
		
	}
	
	public ChangeWidgetCmd(DesignWidgetWrapper widget, byte property, String oldValue, DesignGroupView view){
		this.widget = widget;
		this.view = view;
		this.property = property;
		this.oldValue = oldValue;
		this.panel = view.getPanel();
	}

	public String getName(){
		return "Change Widget " + getFieldName() + " Property";
	}

	public void undo(){	
		view.clearSelection();
		setProperyValue(oldValue);
	}

	public void redo(){
		view.clearSelection();
		setProperyValue(oldValue);
	}

	public boolean isWidgetCommand(){
		return true;
	}

	protected String getFieldName(){
		switch(property){
		case PROPERTY_TEXT:
			return LocaleText.get("text");
		case PROPERTY_TOOLTIP:
			return LocaleText.get("toolTip");
		case PROPERTY_BINDING:
			return LocaleText.get("binding");
		case PROPERTY_CHILD_BINDING:
			return LocaleText.get("childBinding");
		case PROPERTY_WIDTH:
			return LocaleText.get("width");
		case PROPERTY_HEIGHT:
			return LocaleText.get("height");
		case PROPERTY_ENABLED:
			return LocaleText.get("enabled");
		case PROPERTY_VISIBLE:
			return LocaleText.get("visible");
		case PROPERTY_LEFT:
			return LocaleText.get("left");
		case PROPERTY_TOP:
			return LocaleText.get("top");
		case PROPERTY_TAB_INDEX:
			return LocaleText.get("tabIndex");
		case PROPERTY_REPEAT:
			return LocaleText.get("repeat");
		case PROPERTY_EXTERNAL_SOURCE:
			return "externalSource";
		case PROPERTY_DISPLAY_FIELD:
			return "displayField";
		case PROPERTY_VALUE_FIELD:
			return "valueField";
		case PROPERTY_FILTER_FIELD:
			return LocaleText.get("filterField");
		case PROPERTY_FONT_FAMILY:
			return LocaleText.get("fontFamily");	
		case PROPERTY_FORE_COLOR:
			return LocaleText.get("foreColor");
		case PROPERTY_FONT_WEIGHT:
			return LocaleText.get("fontWeight");
		case PROPERTY_FONT_STYLE:
			return LocaleText.get("fontStyle");
		case PROPERTY_FONT_SIZE:
			return LocaleText.get("fontSize");
		case PROPERTY_TEXT_DECORATION:
			return LocaleText.get("textDecoration");
		case PROPERTY_TEXT_ALIGN:
			return LocaleText.get("textAlign");
		case PROPERTY_BACKGROUND_COLOR:
			return LocaleText.get("backgroundColor");
		case PROPERTY_BORDER_STYLE:
			return LocaleText.get("borderStyle");
		case PROPERTY_BORDER_WIDTH:
			return LocaleText.get("borderWidth");
		case PROPERTY_BORDER_COLOR:
			return LocaleText.get("borderColor");
		case PROPERTY_ID:
			return LocaleText.get("id");
		default:
			return "";
		}
	}

	private void setProperyValue(String value){

		switch(property){
		case PROPERTY_TEXT:
			oldValue = widget.getText();
			widget.setText(value);
			break;
		case PROPERTY_TOOLTIP:
			oldValue = widget.getTitle();
			widget.setTitle(value);
			break;
		case PROPERTY_BINDING:
			if(widget.hasParentBinding()){
				oldValue = widget.getParentBinding();
				widget.setParentBinding(value);
			}
			else{
				oldValue = widget.getBinding();
				widget.setBinding(value);
			}
			break;
		case PROPERTY_CHILD_BINDING:
			oldValue = widget.getBinding();
			widget.setBinding(value);
			break;
		case PROPERTY_WIDTH:
			oldValue = widget.getWidth();
			widget.setWidth(value);
			break;
		case PROPERTY_HEIGHT:
			oldValue = widget.getHeight();
			widget.setHeight(value);
			break;
		case PROPERTY_VISIBLE:
			oldValue = String.valueOf(widget.isVisible());
			widget.setVisible(Boolean.parseBoolean(value));
			break;
		case PROPERTY_LEFT:
			oldValue = widget.getLeft();
			widget.setLeft(value);
			break;
		case PROPERTY_TOP:
			oldValue = widget.getTop();
			widget.setTop(value);
			break;
		case PROPERTY_TAB_INDEX:
			oldValue = String.valueOf(widget.getTabIndex());
			widget.setTabIndex(Integer.parseInt(value));
			break;
		case PROPERTY_REPEAT:
			oldValue = String.valueOf(widget.isRepeated());
			widget.setRepeated(Boolean.parseBoolean(value));
			break;
		case PROPERTY_EXTERNAL_SOURCE:
			oldValue = widget.getExternalSource();
			widget.setExternalSource(value);
			break;
		case PROPERTY_DISPLAY_FIELD:
			oldValue = widget.getDisplayField();
			widget.setDisplayField(value);
			break;
		case PROPERTY_VALUE_FIELD:
			oldValue = widget.getValueField();
			widget.setValueField(value);
			break;
		case PROPERTY_FILTER_FIELD:
			oldValue = widget.getFilterField();
			widget.setFilterField(value);
			break;
		case PROPERTY_FONT_FAMILY:
			oldValue = widget.getFontFamily();
			widget.setFontFamily(value);
			break;
		case PROPERTY_FORE_COLOR:
			oldValue = widget.getForeColor();
			widget.setForeColor(value);
			break;
		case PROPERTY_FONT_WEIGHT:
			oldValue = widget.getFontWeight();
			widget.setFontWeight(value);
			break;
		case PROPERTY_FONT_STYLE:
			oldValue = widget.getFontStyle();
			widget.setFontStyle(value);
			break;
		case PROPERTY_FONT_SIZE:
			oldValue = widget.getFontSize();
			widget.setFontSize(value);
			break;
		case PROPERTY_TEXT_DECORATION:
			oldValue = widget.getTextDecoration();
			widget.setTextDecoration(value);
			break;
		case PROPERTY_TEXT_ALIGN:
			oldValue = widget.getTextAlign();
			widget.setTextAlign(value);
			break;
		case PROPERTY_BACKGROUND_COLOR:
			oldValue = widget.getBackgroundColor();
			widget.setBackgroundColor(value);
			break;
		case PROPERTY_BORDER_STYLE:
			oldValue = widget.getBorderStyle();
			widget.setBorderStyle(value);
			break;
		case PROPERTY_BORDER_WIDTH:
			oldValue = widget.getBorderWidth();
			widget.setBorderWidth(value);
			break;
		case PROPERTY_BORDER_COLOR:
			oldValue = widget.getBorderColor();
			widget.setBorderColor(value);
			break;
		case PROPERTY_ID:
			oldValue = widget.getId();
			widget.setId(value);
			break;
		}

		view.onWidgetSelected(widget, panel, false);
	}
}
