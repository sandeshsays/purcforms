package org.purc.purcforms.client.view;

import java.util.HashMap;
import java.util.Map;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.skiprule.FieldWidget;
import org.purc.purcforms.client.xforms.XformConstants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TreeItem;


public class OtherPropertiesView extends Composite implements ItemSelectionListener {

	/** Table used for organizing widgets in a table format. */
	private FlexTable table = new FlexTable();
	
	/** Widget for displaying the exclusive option. */
	private ListBox lbExclusiveOption = new ListBox(false);
	
	/** The field selection widget. */
	private FieldWidget fieldWidget;
	
	private IFormChangeListener formChangeListener;
	private Object formItem;
	private Object prevFormItem;
	private QuestionDef questionDef;
	private QuestionDef exclusiveQtnDef;
	private QuestionDef prevOptionQuestionDef;
	OptionDef prevOptionDef;
	
	private Label exclusiveOptionLabel;
	private Label exclusiveQuestionLabel;
	
	public OtherPropertiesView(){
		
		fieldWidget = new FieldWidget(this);
		fieldWidget.setSameTypesOnly(true);
		
		exclusiveOptionLabel = new Label(LocaleText.get("exclusiveOption"));
		table.setWidget(0, 0, exclusiveOptionLabel);
		table.setWidget(0, 1, lbExclusiveOption);
		
		exclusiveQuestionLabel = new Label(LocaleText.get("exclusiveQuestion"));
		table.setWidget(1, 0, exclusiveQuestionLabel);
		table.setWidget(1, 1, fieldWidget);
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		
		lbExclusiveOption.setWidth("100%");
		
		FormUtil.maximizeWidget(table);
		initWidget(table);
	}
	
	@Override
    public void onItemSelected(Object sender, Object item, boolean userAction) {
		if (sender == fieldWidget){
			QuestionDef oldValue = exclusiveQtnDef;
			exclusiveQtnDef = (QuestionDef)item;
			
			updateExclusiveQuestion(Context.getFormDef());
		}
    }

	@Override
    public void onStartItemSelection(Object sender) {
		if (sender != fieldWidget) {
			fieldWidget.stopSelection();
		}
    }
	
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
	}
	
	public void onFormItemSelected(Object formItem, TreeItem treeItem) {
		 this.prevFormItem = this.formItem;
		 this.formItem = formItem;
		
		 //save previous displayed value
		 FormDef formDef = Context.getFormDef();
		 saveExclusiveOptions(formDef);
		 saveExclusiveOption();
		 
		 if (formItem instanceof OptionDef) {
			 OptionDef optnDef = (OptionDef)formItem;
			 QuestionDef qtnDef = optnDef.getParent();
			 
			 //If switched to different question, load
			 if (qtnDef != prevOptionQuestionDef) {
				 this.lbExclusiveOption.setVisible(true);
				 this.exclusiveOptionLabel.setVisible(true);
				 this.lbExclusiveOption.clear();
				 
				 prevOptionQuestionDef = qtnDef;
	
				 Object exclusiveQuestion = Context.getFormDef().getExtentendProperty(qtnDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
				 
				 if (exclusiveQuestion != null) {
					 QuestionDef questionDef = Context.getFormDef().getQuestion(exclusiveQuestion.toString());
					 if (questionDef != null) {
						 loadOptions(questionDef);
					 }
					 else
						 Window.alert("Failed to find Exclusive Question: " + exclusiveQuestion);
				 }
			 }
			 
			 //set current value to display
			 if (lbExclusiveOption.getItemCount() > 0) {
				 lbExclusiveOption.setSelectedIndex(0);
				 Object map = formDef.getExtentendProperty(qtnDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
				 if (map != null) {
					 String binding = ((Map<String, String>)map).get(optnDef.getBinding());
					 if (binding != null) {
						 for (int index = 0; index < lbExclusiveOption.getItemCount(); index++)  {
							 if (binding.equals(lbExclusiveOption.getValue(index))) {
								 lbExclusiveOption.setSelectedIndex(index);
								 break;
							 }
						 }
					 }
				 }
			 }
			 
			 prevOptionDef = optnDef;
		 }
		 else {
			 prevOptionDef = null;
			 prevOptionQuestionDef = null;
		 }
		 
		 
		 if (formItem instanceof QuestionDef) {
			 setQuestionDef((QuestionDef)formItem);
		 }
	}
	
	private void loadOptions(QuestionDef questionDef) {
		this.lbExclusiveOption.clear();
		this.lbExclusiveOption.addItem("");
		
		for (int index = 0; index < questionDef.getOptionCount(); index++) {
			OptionDef optionDef = questionDef.getOptionAt(index);
			this.lbExclusiveOption.addItem(optionDef.getText());
			this.lbExclusiveOption.setValue(index + 1, optionDef.getBinding());
		}
	}
	
	public void updateOtherProperties() {
		
		saveExclusiveOptions(Context.getFormDef());
		
		if (questionDef == null){
			return;
		}
		
		saveExclusiveOption();
	}
	
	private void saveExclusiveOption() {
		if (questionDef == null)
			return;
		
		if (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
			if (prevFormItem instanceof QuestionDef) {
				FormDef formDef = questionDef.getParentFormDef();
				
				//update exclusive option
				int index = this.lbExclusiveOption.getSelectedIndex();
				if (index > 0) {
					formDef.setExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION, lbExclusiveOption.getValue(index));
				}
				else {
					formDef.removeExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
				}
				
				prevFormItem = null;
			}
		}
	}
	
	private void updateExclusiveQuestion(FormDef formDef) {
		if (exclusiveQtnDef != null) {
			formDef.setExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION, exclusiveQtnDef.getBinding());
			formDef.setExtentendProperty(exclusiveQtnDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION, questionDef.getBinding());
		}
		else {
			formDef.removeExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
			formDef.removeExtentendProperty(exclusiveQtnDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
		}
	}
	
	private void setQuestionDef(QuestionDef questionDef){
		clearProperties();
		
		this.questionDef = questionDef;
		
		fieldWidget.setQuestion(questionDef);
		fieldWidget.selectFormDef(Context.getFormDef());
		
		if (questionDef != null  && questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
			
			//set exclusive option
			Object exclusiveOption = questionDef.getParentFormDef().getExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
			boolean exclusiveOptionSet = false;
			
			this.lbExclusiveOption.addItem("");
			
			for (int index = 0; index < questionDef.getOptionCount(); index++) {
				OptionDef optionDef = questionDef.getOptionAt(index);
				this.lbExclusiveOption.addItem(optionDef.getText());
				this.lbExclusiveOption.setValue(index + 1, optionDef.getBinding());
				
				if (!exclusiveOptionSet && exclusiveOption != null) {
					if (exclusiveOption.equals(optionDef.getBinding())) {
						this.lbExclusiveOption.setSelectedIndex(index + 1);
						exclusiveOptionSet = true;
					}
				}
			}
			
			//set exclusive question
			Object exclusiveQuestion = questionDef.getParentFormDef().getExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
			if (exclusiveQuestion != null) {
				QuestionDef qtnDef = Context.getFormDef().getQuestion(exclusiveQuestion.toString());
				if (qtnDef != null) {
					fieldWidget.selectQuestion(qtnDef);
				}
			}
		}
		else {
			this.lbExclusiveOption.setVisible(false);
			this.exclusiveOptionLabel.setVisible(false);
			this.fieldWidget.setVisible(false);
			this.exclusiveQuestionLabel.setVisible(false);
		}
	}
	
	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		//this.enabled = enabled;

		if (!isExclusiveOptionSelected()) {
			this.lbExclusiveOption.setVisible(enabled);
			this.exclusiveOptionLabel.setVisible(enabled);
		}
		
		this.fieldWidget.setVisible(enabled);
		this.exclusiveQuestionLabel.setVisible(enabled);

		if(!enabled) {
			clearProperties();
		}
	}
	
	private void clearProperties(){
		if (questionDef != null) {
			updateOtherProperties();
			questionDef = null;
		}
		
		exclusiveQtnDef = null;
		
		this.fieldWidget.clearSelection();
		
		if (!isExclusiveOptionSelected()) {
			this.lbExclusiveOption.clear();
		}
	}
	
	private boolean isExclusiveOptionSelected() {
		return (formItem instanceof OptionDef && ((OptionDef)formItem).getParent().getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE);
	}
	
	private void saveExclusiveOptions(FormDef formDef) {
		if (prevOptionQuestionDef != null) {
			Object exclusiveQuestion = formDef.getExtentendProperty(prevOptionQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
			QuestionDef otherQuestionDef = null;
			if (exclusiveQuestion != null) {
				otherQuestionDef = formDef.getQuestion(exclusiveQuestion.toString());
			}
			
			Object map = formDef.getExtentendProperty(prevOptionQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
			int index = lbExclusiveOption.getSelectedIndex();
			if (index > 0) {
				if (map == null) {
					map = new HashMap<String, String>();
				}
				((Map)map).put(prevOptionDef.getBinding(), lbExclusiveOption.getValue(index));
				formDef.setExtentendProperty(prevOptionQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS, map);
				
				
				//Now do the set for the other opposite question
				map = formDef.getExtentendProperty(otherQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
				if (map == null) {
					map = new HashMap<String, String>();
				}
				((Map)map).put(lbExclusiveOption.getValue(index), prevOptionDef.getBinding());
				formDef.setExtentendProperty(otherQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS, map);
			}
			else {
				if (map != null) {
					((Map)map).remove(prevOptionDef.getBinding());
					if (((Map)map).size() == 0) {
						formDef.removeExtentendProperty(prevOptionQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
						formDef.removeExtentendProperty(otherQuestionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
					}
				}
			}
		}
	}
}
