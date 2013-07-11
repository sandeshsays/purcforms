package org.purc.purcforms.client.view;

import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XformConstants;

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
	
	private IFormChangeListener formChangeListener;
	private TreeItem treeItem;
	private QuestionDef questionDef;
	
	public OtherPropertiesView(){
		table.setWidget(0, 0, new Label(LocaleText.get("exclusiveOption")));
		table.setWidget(0, 1, lbExclusiveOption);
		
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth(0, 0, "20%");
		
		lbExclusiveOption.setWidth("100%");
		
		FormUtil.maximizeWidget(table);
		initWidget(table);
	}
	
	@Override
    public void onItemSelected(Object sender, Object item, boolean userAction) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void onStartItemSelection(Object sender) {
	    // TODO Auto-generated method stub
	    
    }
	
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
	}
	
	public void onFormItemSelected(TreeItem treeItem) {
		this.treeItem = treeItem;
	}
	
	public void updateOtherProperties() {
		if (questionDef == null){
			return;
		}
		
		if (questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
			FormDef formDef = questionDef.getParentFormDef();
			int index = this.lbExclusiveOption.getSelectedIndex();
			if (index > 0) {
				formDef.setExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION, lbExclusiveOption.getValue(index));
			}
			else {
				formDef.removeExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
			}
		}
	}
	
	public void setQuestionDef(QuestionDef questionDef){
		clearProperties();
		
		this.questionDef = questionDef;
		
		if (questionDef != null  && questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
			String exclusiveOption = questionDef.getParentFormDef().getExtentendProperty(questionDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
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
		}
		else {
			this.lbExclusiveOption.setEnabled(false);
		}
	}
	
	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		//this.enabled = enabled;

		this.lbExclusiveOption.setEnabled(enabled);

		if(!enabled) {
			clearProperties();
		}
	}
	
	private void clearProperties(){
		if (questionDef != null) {
			updateOtherProperties();
			questionDef = null;
		}
		
		this.lbExclusiveOption.clear();
	}
}
