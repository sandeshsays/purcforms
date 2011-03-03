package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;


/**
 * Command for changing property of a form field.
 * 
 * @author danielkayiwa
 *
 */
public class ChangedFieldCmd implements ICommand {

	public static final byte PROPERTY_TEXT = 1;
	public static final byte PROPERTY_HELP_TEXT = 2;
	public static final byte PROPERTY_TYPE = 3;
	public static final byte PROPERTY_BINDING = 4;
	public static final byte PROPERTY_VISIBLE = 5;
	public static final byte PROPERTY_ENABLED = 6;
	public static final byte PROPERTY_LOCKED = 7;
	public static final byte PROPERTY_REQUIRED = 8;
	public static final byte PROPERTY_DEFAULT_VALUE = 9;
	public static final byte PROPERTY_CALCULATION = 10;
	public static final byte PROPERTY_FORM_KEY = 11;
	public static final byte PROPERTY_DESCRIPTION_TEMPLATE = 12;
	
	public static final byte PROPERTY_MAKE_REQUIRED = 13;
	public static final byte PROPERTY_ENABLE = 14;
	public static final byte PROPERTY_SHOW = 15;
	
	public static final byte PROPERTY_ERROR_MESSAGE = 16;
	
	public static final byte PROPERTY_CONDITION_QUESTION = 17;
	public static final byte PROPERTY_CONDITION_OPERATOR = 18;
	public static final byte PROPERTY_CONDITION_VALUE = 19;
	public static final byte PROPERTY_VALIDATION_VALUE_LENGTH = 20;

	
	private FormsTreeView view;
	private TreeItem item;
	private byte property;
	private String oldValue;

	public ChangedFieldCmd(TreeItem item, byte property, FormsTreeView view){
		this.item = item;
		this.view = view;
		this.property = property;
	}

	public String getName(){
		return "Change Field " + " Property";
	}

	public void undo(){	
		
		
		view.selectItem(item);
	}

	public void redo(){
		
		
		view.selectItem(item);
	}
	
	public boolean isWidgetCommand(){
		return false;
	}
}
