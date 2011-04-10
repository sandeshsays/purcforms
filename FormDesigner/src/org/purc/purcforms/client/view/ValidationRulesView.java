package org.purc.purcforms.client.view;

import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.cmd.ChangeValidationConditionCmd;
import org.purc.purcforms.client.cmd.ChangeValidationRuleCmd;
import org.purc.purcforms.client.cmd.DeleteValidationConditionCmd;
import org.purc.purcforms.client.cmd.InsertValidationConditionCmd;
import org.purc.purcforms.client.controller.IConditionController;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.model.ValidationRule;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;
import org.purc.purcforms.client.widget.skiprule.GroupHyperlink;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget enables creation of validation rules.
 * 
 * @author daniel
 *
 */
public class ValidationRulesView extends Composite implements IConditionController, ItemSelectionListener{

	/** The widget horizontal spacing in horizontal panels. */
	private static final int HORIZONTAL_SPACING = 5;
	
	/** The widget vertical spacing in vertical panels. */
	private static final int VERTICAL_SPACING = 0;
	
	/** The main or root widget. */
	private VerticalPanel verticalPanel = new VerticalPanel();
	
	/** Widget for adding new conditions. */
	private Hyperlink addConditionLink = new Hyperlink(LocaleText.get("clickToAddNewCondition"),"");
	
	/** Widget for grouping conditions. Has all,any, none, and not all. */
	private GroupHyperlink groupHyperlink = new GroupHyperlink(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL, "", this);
	
	/** The form definition object that this validation rule belongs to. */
	private FormDef formDef;
	
	/** The question definition object which is the target of the validation rule. */
	private QuestionDef questionDef;
	
	/** The validation rule definition object. */
	private ValidationRule validationRule;
	
	/** Flag determining whether to enable this widget or not. */
	private boolean enabled;
	
	/** Widget for the validation rule error message. */
	private TextBox txtErrorMessage = new TextBox();

	/** Widget for Label "Question: ". */
	private Label lblAction = new Label(LocaleText.get("question")+": " /*"Question: "*/);
	
	private IFormChangeListener formChangeListener;
	private TreeItem treeItem;

	private PropertiesView propertiesView;
	
	
	/**
	 * Creates a new instance of the validation rule widget.
	 */
	public ValidationRulesView(PropertiesView propertiesView){
		this.propertiesView = propertiesView;
		setupWidgets();
	}
	
	
	/**
	 * Sets up the widgets.
	 */
	private void setupWidgets(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);
		
		HorizontalPanel actionPanel = new HorizontalPanel();
		actionPanel.setWidth("100%");
		FormUtil.maximizeWidget(txtErrorMessage);
		actionPanel.add(new Label(LocaleText.get("errorMessage")));
		actionPanel.add(txtErrorMessage);
		actionPanel.setSpacing(10);
		
		verticalPanel.add(lblAction);
		verticalPanel.add(actionPanel);
		
		horizontalPanel.add(new Label(LocaleText.get("when")));
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));
		verticalPanel.add(horizontalPanel);
		
		//verticalPanel.add(new ConditionWidget(FormDefTest.getPatientFormDef(),this));
		verticalPanel.add(addConditionLink);
		
		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition();
			}
		});
		
		final ValidationRulesView view = this;
		txtErrorMessage.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				if(validationRule == null){
					validationRule = new ValidationRule(questionDef.getId(), formDef);
					validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());
					formDef.addValidationRule(validationRule);
				}

				String oldValue = validationRule.getErrorMessage();
				validationRule.setErrorMessage(txtErrorMessage.getText());

				if(!validationRule.getErrorMessage().equals(oldValue))
					Context.getCommandHistory().add(new ChangeValidationRuleCmd(ChangeValidationRuleCmd.PROPERTY_ERROR_MSG, oldValue, validationRule, view, treeItem, (FormsTreeView)formChangeListener));		
			}
		});
		
		
		verticalPanel.setSpacing(VERTICAL_SPACING);
		initWidget(verticalPanel);
	}
	
	
	/**
	 * Adds a new condition.
	 */
	public void addCondition(){
		addCondition(new ConditionWidget(formDef, this, false, questionDef), -1, validationRule, true);
	}
	
	public void addCondition(ConditionWidget conditionWidget, int index, ValidationRule valdtnRule, boolean storeHistory){
		this.validationRule = valdtnRule;
		
		if(formDef != null && enabled){
			
			if(!storeHistory)
				conditionWidget.stopEdit(false);
			
			verticalPanel.remove(addConditionLink);
			
			conditionWidget.setQuestionDef(questionDef);
			
			if(index == -1)
				verticalPanel.add(conditionWidget);
			else
				verticalPanel.insert(conditionWidget, index + 2);
			
			verticalPanel.add(addConditionLink);
			
			txtErrorMessage.setFocus(true);
			
			if(validationRule == null){
				validationRule = new ValidationRule(questionDef.getId(), formDef);
				validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());
				formDef.addValidationRule(validationRule);
			}
			else if(!formDef.containsValidationRule(validationRule))
				formDef.addValidationRule(validationRule);
			
			validationRule.addCondition(conditionWidget.getCondition());
				
			/*String text = txtErrorMessage.getText();
			if(text != null && text.trim().length() == 0){
				txtErrorMessage.setText(LocaleText.get("errorMessage"));
				//txtErrorMessage.selectAll();
				txtErrorMessage.setFocus(true);
			}*/
			
			if(storeHistory){
				validationRule.setErrorMessage(txtErrorMessage.getText());
				Context.getCommandHistory().add(new InsertValidationConditionCmd(validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener, valdtnRule == null));
			}
			else{
				txtErrorMessage.setText(validationRule.getErrorMessage());
				propertiesView.selectValidationRulesTab();
			}
		}
	}
	
	
	/**
	 * Supposed to add a bracket or nested set of related conditions which are 
	 * currently not supported.
	 */
	public void addBracket(){
		
	}
	
	
	/**
	 * Deletes a condition.
	 * 
	 * @param conditionWidget the widget having the condition to delete.
	 */
	public void deleteCondition(ConditionWidget conditionWidget){
		deleteCondition(conditionWidget, validationRule, true);
	}
	
	public void deleteCondition(ConditionWidget conditionWidget, ValidationRule valdtnRule, boolean storeHistory){
		this.validationRule = valdtnRule;
		
		if(validationRule != null)
			validationRule.removeCondition(conditionWidget.getCondition());
		
		//verticalPanel.remove(conditionWidget);
		
		int index = verticalPanel.getWidgetIndex(conditionWidget);
		if(index > -1)
			verticalPanel.remove(index);
		else
			index = removeConditionWidget(conditionWidget.getExistingCondition());
		
		if(validationRule.getConditionCount() == 0){
			formDef.removeValidationRule(validationRule);
			//txtErrorMessage.setText(null);

			//validationRule = null;
		}
		
		if(storeHistory){
			Context.getCommandHistory().add(new DeleteValidationConditionCmd(validationRule, conditionWidget, index - 2, this, treeItem, (FormsTreeView)formChangeListener));
		}
		else
			propertiesView.selectValidationRulesTab();
	}
	
	
	/**
	 * Sets or updates the values of the validation rule object from the user's widget selections.
	 */
	public void updateValidationRule(){
		if(questionDef == null){
			validationRule = null;
			return;
		}
		
		if(validationRule == null)
			validationRule = new ValidationRule(questionDef.getId(),formDef);
		
		//validationRule.setErrorMessage(txtErrorMessage.getText());
		
		int count = verticalPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = verticalPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				Condition condition = ((ConditionWidget)widget).getCondition();
				
				if(condition != null && !validationRule.containsCondition(condition) /*&& condition.getValue() != null*/)
					validationRule.addCondition(condition);
				else if(condition != null && validationRule.containsCondition(condition)){
					//if(condition.getValue() != null)
						validationRule.updateCondition(condition);
					//else
					//	validationRule.removeCondition(condition);
				}
			}
		}
		
		if(validationRule.getConditions() == null || validationRule.getConditionCount() == 0){
			formDef.removeValidationRule(validationRule);
			validationRule = null;
		}
		else
			validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());
		
		if(validationRule != null && !formDef.containsValidationRule(validationRule))
			formDef.addValidationRule(validationRule);
	}
	
	
	/**
	 * Sets the question definition object which is the target of the validation rule.
	 * 
	 * @param questionDef the question definition object.
	 */
	public void setQuestionDef(QuestionDef questionDef){
		clearConditions();
		
		formDef = questionDef.getParentFormDef();
		
		/*if(questionDef.getParent() instanceof PageDef)
			formDef = ((PageDef)questionDef.getParent()).getParent();
		else
			formDef = ((PageDef)((QuestionDef)questionDef.getParent()).getParent()).getParent();*/
		
		if(questionDef != null)
			lblAction.setText(LocaleText.get("question")+":  " + questionDef.getDisplayText() + "  "+LocaleText.get("isValidWhen"));
		else
			lblAction.setText(LocaleText.get("question")+": ");
		
		this.questionDef = questionDef;
		
		validationRule = formDef.getValidationRule(questionDef);
		if(validationRule != null){
			groupHyperlink.setCondionsOperator(validationRule.getConditionsOperator());
			txtErrorMessage.setText(validationRule.getErrorMessage());
			verticalPanel.remove(addConditionLink);
			Vector conditions = validationRule.getConditions();
			Vector lostConditions = new Vector();
			for(int i=0; i<conditions.size(); i++){
				ConditionWidget conditionWidget = new ConditionWidget(formDef,this,false,questionDef);
				if(conditionWidget.setCondition((Condition)conditions.elementAt(i)))
					verticalPanel.add(conditionWidget);
				else
					lostConditions.add((Condition)conditions.elementAt(i));
			}
			for(int i=0; i<lostConditions.size(); i++)
				validationRule.removeCondition((Condition)lostConditions.elementAt(i));
			if(validationRule.getConditionCount() == 0){
				formDef.removeValidationRule(validationRule);
				validationRule = null;
			}
			
			verticalPanel.add(addConditionLink);
		}
	}
	
	
	/**
	 * Sets the form definition object to which this validation rule belongs.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		updateValidationRule();
		this.formDef = formDef;
		this.questionDef = null;
		clearConditions();
	}
	
	
	/**
	 * Removes all validation rule conditions.
	 */
	private void clearConditions(){
		if(questionDef != null)
			updateValidationRule();
		
		questionDef = null;
		lblAction.setText(LocaleText.get("question")+": ");
		
		while(verticalPanel.getWidgetCount() > 4)
			verticalPanel.remove(verticalPanel.getWidget(3));
		
		txtErrorMessage.setText(null);
	}
	
	
	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
		this.groupHyperlink.setEnabled(enabled);
		
		txtErrorMessage.setEnabled(enabled);
		
		//One should be able to localize validation error messages.
		if(Context.inLocalizationMode())
			txtErrorMessage.setEnabled(true);
		
		if(!enabled)
			clearConditions();
	}
	
	
	/**
	 * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
	 */
	public void onWindowResized(int width, int height){
		if(width - 700 > 0)
			txtErrorMessage.setWidth(width - 700 + PurcConstants.UNITS);
	}
	
	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
	}
	
	public void onItemSelected(Object sender, Object item, boolean userAction){
		if(validationRule == null){
			validationRule = new ValidationRule(questionDef.getId(), formDef);
			validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());
			formDef.addValidationRule(validationRule);
		}

		int oldValue = validationRule.getConditionsOperator();
		validationRule.setConditionsOperator(groupHyperlink.getConditionsOperator());

		if(oldValue != validationRule.getConditionsOperator())
			Context.getCommandHistory().add(new ChangeValidationRuleCmd(ChangeValidationRuleCmd.PROPERTY_CONDITIONS_OPERATOR, oldValue, validationRule, this, treeItem, (FormsTreeView)formChangeListener));		
	}

	public void onStartItemSelection(Object sender){
		assert(sender == groupHyperlink);
	}
	
	public void onFormItemSelected(TreeItem treeItem) {
		this.treeItem = treeItem;
	}
	
	public void setErrorMessage(ValidationRule validationRule){
		this.validationRule = validationRule;
		txtErrorMessage.setText(validationRule.getErrorMessage());
		
		propertiesView.selectValidationRulesTab();
	}

	public void setCondionsOperator(ValidationRule validationRule){
		this.validationRule = validationRule;
		groupHyperlink.setCondionsOperator(validationRule.getConditionsOperator());
		
		propertiesView.selectValidationRulesTab();
	}
	
	public void setCondionOperator(ValidationRule validationRule, Condition condition){
		this.validationRule = validationRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setOperator(condition.getOperator());
		
		propertiesView.selectValidationRulesTab();
	}
	
	public void setConditionFunction(ValidationRule validationRule, Condition condition){
		this.validationRule = validationRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setFunction(condition.getFunction());
		widget.setValue(condition.getValue());
		
		propertiesView.selectValidationRulesTab();
	}
	
	public void setConditionValue(ValidationRule validationRule, Condition condition){
		this.validationRule = validationRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setValue(condition.getValue());
		
		propertiesView.selectValidationRulesTab();
	}
	
	public void setQtnToggleValue(ValidationRule validationRule, Condition condition, boolean value){
		this.validationRule = validationRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setQtnToggleValue(value);
		
		propertiesView.selectValidationRulesTab();
	}
	
	public void onConditionOperatorChanged(ConditionWidget conditionWidget, int oldOperator){
		Context.getCommandHistory().add(new ChangeValidationConditionCmd(ChangeValidationConditionCmd.PROPERTY_OPERATOR, oldOperator, validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	public void onConditionValue1Changed(ConditionWidget conditionWidget, String oldValue){
		Context.getCommandHistory().add(new ChangeValidationConditionCmd(ChangeValidationConditionCmd.PROPERTY_VALUE1, oldValue, validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	public void onConditionValue2Changed(ConditionWidget conditionWidget, String oldValue){
		Context.getCommandHistory().add(new ChangeValidationConditionCmd(ChangeValidationConditionCmd.PROPERTY_VALUE2, oldValue, validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	public void onConditionQuestionChanged(ConditionWidget conditionWidget, QuestionDef oldQuestionDef){
		//Not implemented for validation rules.
	}
	
	public void onConditionQtnValueToggleChanged(ConditionWidget conditionWidget, boolean oldValue){
		Context.getCommandHistory().add(new ChangeValidationConditionCmd(ChangeValidationConditionCmd.PROPERTY_QTN_VALUE_TOGGLE, oldValue, validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	public void onConditionFunctionChanged(ConditionWidget conditionWidget, int oldFunction){
		Context.getCommandHistory().add(new ChangeValidationConditionCmd(ChangeValidationConditionCmd.PROPERTY_LENGTH_VALUE, oldFunction, validationRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	private int removeConditionWidget(Condition condition){	
		int index =  getConditionIndex(condition);
		if(index > -1){
			verticalPanel.remove(index);
		}

		return index;
	}
	
	private int getConditionIndex(Condition condition){
		int count = verticalPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = verticalPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				if(condition == ((ConditionWidget)widget).getExistingCondition())
					return i;
			}
		}

		return -1;
	}
}
