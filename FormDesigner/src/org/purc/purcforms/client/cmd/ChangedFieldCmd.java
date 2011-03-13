package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.view.FormsTreeView;
import org.purc.purcforms.client.view.PropertiesView;

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


	public ChangedFieldCmd(TreeItem item, byte property, String oldValue, FormsTreeView view){
		this.item = item;
		this.view = view;
		this.property = property;
		this.oldValue = oldValue;
	}

	public String getName(){
		return "Change Field " + getFieldName() + " Property";
	}

	public void undo(){	
		setProperyValue(oldValue);
	}

	public void redo(){
		setProperyValue(oldValue);
	}

	public boolean isWidgetCommand(){
		return false;
	}

	private void setProperyValue(String value){
		Object field = item.getUserObject();

		switch(property){
		case PROPERTY_TEXT:
			setTextValue(field, value);
			break;
		case PROPERTY_HELP_TEXT:
			setHelpTextValue(field, value);
			break;
		case PROPERTY_TYPE:
			setTypeValue(field, value);
			break;
		case PROPERTY_BINDING:
			setBindingValue(field, value);
			break;
		case PROPERTY_VISIBLE:
			setVisibleValue(field, value);
			break;
		case PROPERTY_ENABLED:
			setEnabledValue(field, value);
			break;
		case PROPERTY_LOCKED:
			setLockedValue(field, value);
			break;
		case PROPERTY_REQUIRED:
			setRequiredValue(field, value);
			break;
		case PROPERTY_FORM_KEY:
			setFormKeyValue(field, value);
			break;
		case PROPERTY_DEFAULT_VALUE:
			setDefaultValue(field, value);
			break;
		case PROPERTY_DESCRIPTION_TEMPLATE:
			setDescriptionTemplateValue(field, value);
			break;
		}

		view.setSelectedItem(item);
	}

	private void setTextValue(Object field, String value){
		if(field instanceof FormDef){
			oldValue = ((FormDef)field).getName();
			((FormDef)field).setName(value);
		}
		else if(field instanceof PageDef){
			oldValue = ((PageDef)field).getName();
			((PageDef)field).setName(value);
		}
		else if(field instanceof QuestionDef){
			oldValue = ((QuestionDef)field).getText();
			((QuestionDef)field).setText(value);
		}
		else if(field instanceof OptionDef){
			oldValue = ((OptionDef)field).getText();
			((OptionDef)field).setText(value);
		}

		setObjBinding(field, oldValue, value);
	}

	private void setBindingValue(Object field, String value){
		if(field instanceof FormDef){
			oldValue = ((FormDef)field).getBinding();
			((FormDef)field).setBinding(value);
		}
		else if(field instanceof QuestionDef){
			oldValue = ((QuestionDef)field).getBinding();
			((QuestionDef)field).setBinding(value);
		}
		else if(field instanceof OptionDef){
			oldValue = ((OptionDef)field).getBinding();
			((OptionDef)field).setBinding(value);
		}
	}

	private void setHelpTextValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = ((QuestionDef)field).getHelpText();
			((QuestionDef)field).setHelpText(value);
		}
	}

	private void setFormKeyValue(Object field, String value){
		if(field instanceof FormDef){
			oldValue = ((FormDef)field).getFormKey();
			((FormDef)field).setFormKey(value);
		}
	}

	private void setDescriptionTemplateValue(Object field, String value){
		if(field instanceof FormDef){
			oldValue = ((FormDef)field).getDescriptionTemplate();
			((FormDef)field).setDescriptionTemplate(value);
		}
	}

	private void setDefaultValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = ((QuestionDef)field).getDefaultValue();
			((QuestionDef)field).setDefaultValue(value);
		}
	}

	private void setVisibleValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = String.valueOf(((QuestionDef)field).isVisible());
			((QuestionDef)field).setVisible(Boolean.parseBoolean(value));
		}
	}

	private void setEnabledValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = String.valueOf(((QuestionDef)field).isEnabled());
			((QuestionDef)field).setEnabled(Boolean.parseBoolean(value));
		}
	}

	private void setLockedValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = String.valueOf(((QuestionDef)field).isLocked());
			((QuestionDef)field).setLocked(Boolean.parseBoolean(value));
		}
	}

	private void setRequiredValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = String.valueOf(((QuestionDef)field).isRequired());
			((QuestionDef)field).setRequired(Boolean.parseBoolean(value));
		}
	}

	private void setTypeValue(Object field, String value){
		if(field instanceof QuestionDef){
			oldValue = String.valueOf(((QuestionDef)field).getDataType());
			QuestionDef questionDef = (QuestionDef)field;
			questionDef.setDataType(Integer.parseInt(value));
		}
	}

	private String getFieldName(){
		switch(property){
		case PROPERTY_TEXT:
			return LocaleText.get("text");
		case PROPERTY_HELP_TEXT:
			return LocaleText.get("helpText");
		case PROPERTY_TYPE:
			return LocaleText.get("type");
		case PROPERTY_BINDING:
			return LocaleText.get("binding");
		case PROPERTY_VISIBLE:
			return LocaleText.get("visible");
		case PROPERTY_ENABLED:
			return LocaleText.get("enabled");
		case PROPERTY_LOCKED:
			return LocaleText.get("locked");
		case PROPERTY_REQUIRED:
			return LocaleText.get("required");
		case PROPERTY_DEFAULT_VALUE:
			return LocaleText.get("defaultValue");
		case PROPERTY_CALCULATION:
			return LocaleText.get("calculation");
		case PROPERTY_FORM_KEY:
			return LocaleText.get("formKey");
		case PROPERTY_DESCRIPTION_TEMPLATE:
			return LocaleText.get("descriptionTemplate");
		case PROPERTY_MAKE_REQUIRED:
			return "Make Required";
		case PROPERTY_ENABLE:
			return "Enable";
		case PROPERTY_SHOW:
			return "Show";
		case PROPERTY_ERROR_MESSAGE:
			return LocaleText.get("errorMessage");
		case PROPERTY_CONDITION_QUESTION:
			return LocaleText.get("Condition Question");
		default:
			return "";
		}
	}


	private void setObjBinding(Object propertiesObj, String orgText, String currentText){

		if(orgText == null || (propertiesObj instanceof OptionDef))
			return;

		String orgTextDefBinding = FormDesignerUtil.getXmlTagName(PropertiesView.getTextWithoutDecTemplate(orgText));

		String text = PropertiesView.getTextWithoutDecTemplate(currentText.trim());
		String name = FormDesignerUtil.getXmlTagName(text);
		if(propertiesObj instanceof FormDef && ((FormDef)propertiesObj).getBinding().equals(orgTextDefBinding)){
			((FormDef)propertiesObj).setBinding(name);
		}
		else if(propertiesObj instanceof QuestionDef && ((QuestionDef)propertiesObj).getBinding().equals(orgTextDefBinding)){
			((QuestionDef)propertiesObj).setBinding(name);
		}
		else if(propertiesObj instanceof OptionDef && ((OptionDef)propertiesObj).getBinding().equals(orgTextDefBinding)){
			((OptionDef)propertiesObj).setBinding(name);
		}
	}
}
