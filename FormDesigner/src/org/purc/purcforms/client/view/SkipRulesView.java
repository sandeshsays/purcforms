package org.purc.purcforms.client.view;

import java.util.List;
import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.cmd.ChangeSkipConditionCmd;
import org.purc.purcforms.client.cmd.ChangeSkipRuleCmd;
import org.purc.purcforms.client.cmd.DeleteSkipConditionCmd;
import org.purc.purcforms.client.cmd.InsertSkipConditionCmd;
import org.purc.purcforms.client.controller.IConditionController;
import org.purc.purcforms.client.controller.IFormChangeListener;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.controller.QuestionSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;
import org.purc.purcforms.client.widget.skiprule.GroupHyperlink;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget enables creation of skip rules.
 * 
 * @author daniel
 *
 */
public class SkipRulesView extends Composite implements IConditionController, QuestionSelectionListener, ItemSelectionListener{

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

	/** The form definition object that this skip rule belongs to. */
	private FormDef formDef;

	/** The question definition object which is the target of the skip rule. 
	 *  As for now, the form designer supports only one skip rule target. But the
	 *  skip rule object supports an un limited number.
	 */
	private QuestionDef questionDef;

	/** The skip rule definition object. */
	private SkipRule skipRule;

	/** Flag determining whether to enable this widget or not. */
	private boolean enabled;

	/** Widget for the skip rule action to enable a question. */
	private RadioButton rdEnable = new RadioButton("action","Enable");

	/** Widget for the skip rule action to disable a question. */
	private RadioButton rdDisable = new RadioButton("action","Disable");

	/** Widget for the skip rule action to show a question. */
	private RadioButton rdShow = new RadioButton("action","Show");

	/** Widget for the skip rule action to hide a question. */
	private RadioButton rdHide = new RadioButton("action","Hide");

	/** Widget for the skip rule action to make a question required. */
	private CheckBox chkMakeRequired = new CheckBox("Make Required");

	/** Widget for Label "for question". */
	private Label lblAction = new Label(LocaleText.get("forQuestion"));

	/** Widget for Label "and". */
	private Label lblAnd = new Label(LocaleText.get("and"));

	private IFormChangeListener formChangeListener;
	private TreeItem treeItem;

	private PropertiesView propertiesView;

	public static final int ACTION_AUTO_SET = 1 << 8;

	/**
	 * Creates a new instance of the skip logic widget.
	 */
	public SkipRulesView(PropertiesView propertiesView){
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
		actionPanel.add(rdEnable);
		actionPanel.add(rdDisable);
		actionPanel.add(rdShow);
		actionPanel.add(rdHide);
		actionPanel.add(chkMakeRequired);
		actionPanel.setSpacing(5);

		Hyperlink hyperlink = new Hyperlink(LocaleText.get("clickForOtherQuestions"),"");
		hyperlink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				showOtherQuestions();
			}
		});

		HorizontalPanel horzPanel = new HorizontalPanel();
		horzPanel.setSpacing(10);
		horzPanel.add(lblAction);
		horzPanel.add(lblAnd);
		horzPanel.add(hyperlink);

		verticalPanel.add(horzPanel);
		verticalPanel.add(actionPanel);

		horizontalPanel.add(new Label(LocaleText.get("when")));
		horizontalPanel.add(groupHyperlink);
		horizontalPanel.add(new Label(LocaleText.get("ofTheFollowingApply")));
		verticalPanel.add(horizontalPanel);

		verticalPanel.add(addConditionLink);

		addConditionLink.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				addCondition();
			}
		});

		rdEnable.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				updateMakeRequired();
				setAction();
			}
		});
		rdDisable.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				updateMakeRequired();
				setAction();
			}
		});
		rdShow.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				updateMakeRequired();
				setAction();
			}
		});
		rdHide.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				updateMakeRequired();
				setAction();
			}
		});

		chkMakeRequired.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				setAction();
			}
		});

		verticalPanel.setSpacing(VERTICAL_SPACING);
		initWidget(verticalPanel);
	}

	private void setAction(){
		if(skipRule == null){
			skipRule = new SkipRule();
			formDef.addSkipRule(skipRule);
		}

		int oldValue = skipRule.getAction();		
		skipRule.setAction(getAction());

		Context.getCommandHistory().add(new ChangeSkipRuleCmd(ChangeSkipRuleCmd.PROPERTY_ACTION, oldValue, skipRule, this, treeItem, (FormsTreeView)formChangeListener));
	}

	/**
	 * Enables the make required widget if the enable or show widget is ticked, 
	 * else disables and unticks it.
	 */
	private void updateMakeRequired(){
		chkMakeRequired.setEnabled(rdEnable.getValue() == true || rdShow.getValue() == true);
		if(!chkMakeRequired.isEnabled())
			;//chkMakeRequired.setValue(false);
	}

	/**
	 * Adds a new condition.
	 */
	public void addCondition(){
		if(!enabled)
			return;

		addCondition(new ConditionWidget(formDef, this, true, questionDef), -1, skipRule, true);
	}

	public void addCondition(ConditionWidget conditionWidget, int index, SkipRule skpRule, boolean storeHistory) {
		this.skipRule = skpRule;

		if(formDef != null && enabled){		
			
			if(!storeHistory)
				conditionWidget.stopEdit(false);
			
			verticalPanel.remove(addConditionLink);

			if(index == -1)
				verticalPanel.add(conditionWidget);
			else
				verticalPanel.insert(conditionWidget, index + 2);

			verticalPanel.add(addConditionLink);

			if(skipRule == null){

				if(!(rdEnable.getValue() == true||rdDisable.getValue() == true||rdShow.getValue() == true||rdHide.getValue() == true)){
					rdEnable.setValue(true);
					updateMakeRequired();
				}

				skipRule = new SkipRule();
				skipRule.setConditionsOperator(groupHyperlink.getConditionsOperator());

				int action = getAction();
				action |= ACTION_AUTO_SET;
				skipRule.setAction(action);

				skipRule.addActionTarget(questionDef.getId());
				formDef.addSkipRule(skipRule);
			}
			else if(!formDef.containsSkipRule(skipRule))
				formDef.addSkipRule(skipRule);

			skipRule.addCondition(conditionWidget.getCondition());
			setAction(skipRule.getAction());

			if(storeHistory)
				Context.getCommandHistory().add(new InsertSkipConditionCmd(skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener, skpRule == null));
			else
				propertiesView.selectSkipRulesTab();
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
		deleteCondition(conditionWidget, skipRule, true);
	}

	/**
	 * Deletes a condition.
	 * 
	 * @param conditionWidget the widget having the condition to delete.
	 */
	public void deleteCondition(ConditionWidget conditionWidget, SkipRule skpRule, boolean storeHistory){
		this.skipRule = skpRule;

		//if(skipRule != null){
		Condition condition = conditionWidget.getCondition();
		if(condition != null){
			if(skipRule.getConditionCount() == 1 && skipRule.getActionTargetCount() > 1)
				skipRule.removeActionTarget(questionDef);
			else
				skipRule.removeCondition(condition);
		}
		//}

		int index = verticalPanel.getWidgetIndex(conditionWidget);
		if(index > -1)
			verticalPanel.remove(index);
		else
			index = removeConditionWidget(conditionWidget.getExistingCondition());

		if(skipRule.getConditionCount() == 0){
			formDef.removeSkipRule(skipRule);

			if((skipRule.getAction() & ACTION_AUTO_SET) != 0)
				setAction(0);

			//skipRule = null;
		}

		if(storeHistory){
			Context.getCommandHistory().add(new DeleteSkipConditionCmd(skipRule, conditionWidget, index - 2, this, treeItem, (FormsTreeView)formChangeListener));
		}
		else
			propertiesView.selectSkipRulesTab();
	}

	/**
	 * Sets or updates the values of the skip rule object from the user's widget selections.
	 */
	public void updateSkipRule(){
		if(questionDef == null){
			skipRule = null;
			return;
		}

		if(skipRule == null)
			skipRule = new SkipRule();

		int conditionCount = 0;
		int count = verticalPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = verticalPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				Condition condition = ((ConditionWidget)widget).getCondition();
				if(condition != null && !skipRule.containsCondition(condition))
					skipRule.addCondition(condition);
				else if(condition != null && skipRule.containsCondition(condition))
					skipRule.updateCondition(condition);
				conditionCount++;
			}
		}

		if(skipRule.getConditions() == null || conditionCount == 0)
			skipRule = null;
		else{
			skipRule.setConditionsOperator(groupHyperlink.getConditionsOperator());

			boolean autoSetAction = ((skipRule.getAction() & ACTION_AUTO_SET) != 0);
			int action = getAction();
			if(autoSetAction)
				action |= ACTION_AUTO_SET;
			skipRule.setAction(action);

			if(!skipRule.containsActionTarget(questionDef.getId()))
				skipRule.addActionTarget(questionDef.getId());
		}
	}

	private int removeConditionWidget(Condition condition){
		/*int count = verticalPanel.getWidgetCount();
		for(int i=0; i<count; i++){
			Widget widget = verticalPanel.getWidget(i);
			if(widget instanceof ConditionWidget){
				if(condition == ((ConditionWidget)widget).getCondition()){
					verticalPanel.remove(widget);
					return i;
				}
			}
		}*/
		
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

	/**
	 * Gets the skip rule action based on the user's widget selections.
	 * 
	 * @return the skip rule action.
	 */
	private int getAction(){
		int action = 0;
		if(rdEnable.getValue() == true)
			action |= ModelConstants.ACTION_ENABLE;
		else if(rdShow.getValue() == true)
			action |= ModelConstants.ACTION_SHOW;
		else if(rdHide.getValue() == true)
			action |= ModelConstants.ACTION_HIDE;
		else if(rdDisable.getValue() == true)
			action |= ModelConstants.ACTION_DISABLE;

		if(chkMakeRequired.getValue() == true)
			action |= ModelConstants.ACTION_MAKE_MANDATORY;
		else
			action |= ModelConstants.ACTION_MAKE_OPTIONAL;

		return action;
	}

	/**
	 * Updates the widgets basing on a given skip rule action.
	 * 
	 * @param action the skip rule action.
	 */
	private void setAction(int action){
		rdEnable.setValue((action & ModelConstants.ACTION_ENABLE) != 0);
		rdDisable.setValue((action & ModelConstants.ACTION_DISABLE) != 0);
		rdShow.setValue((action & ModelConstants.ACTION_SHOW) != 0);
		rdHide.setValue((action & ModelConstants.ACTION_HIDE) != 0);
		chkMakeRequired.setValue((action & ModelConstants.ACTION_MAKE_MANDATORY) != 0);
		updateMakeRequired();
	}

	/**
	 * Sets the question definition object which is the target of the skip rule.
	 * For now we support only one target for the skip rule.
	 * 
	 * @param questionDef the question definition object.
	 */
	public void setQuestionDef(QuestionDef questionDef){
		clearConditions();

		formDef = questionDef.getParentFormDef();

		if(questionDef != null)
			lblAction.setText(LocaleText.get("forQuestion") + questionDef.getDisplayText());
		else
			lblAction.setText(LocaleText.get("forQuestion"));

		this.questionDef = questionDef;

		skipRule = formDef.getSkipRule(questionDef);

		if(skipRule != null){ 
			groupHyperlink.setCondionsOperator(skipRule.getConditionsOperator());
			setAction(skipRule.getAction());
			verticalPanel.remove(addConditionLink);
			Vector conditions = skipRule.getConditions();
			Vector lostConditions = new Vector();
			for(int i=0; i<conditions.size(); i++){
				ConditionWidget conditionWidget = new ConditionWidget(formDef,this,true,questionDef);
				if(conditionWidget.setCondition((Condition)conditions.elementAt(i)))
					verticalPanel.add(conditionWidget);
				else
					lostConditions.add((Condition)conditions.elementAt(i));
			}
			for(int i=0; i<lostConditions.size(); i++)
				skipRule.removeCondition((Condition)lostConditions.elementAt(i));

			if(skipRule.getConditionCount() == 0){
				formDef.removeSkipRule(skipRule);
				//Context.getCommandHistory().add(new DeleteSkipRuleCmd(skipRule, formDef, treeItem, (FormsTreeView)formChangeListener));
				skipRule = null;
			}

			verticalPanel.add(addConditionLink);
		}
	}

	/**
	 * Sets the form definition object to which this skip rule belongs.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){
		updateSkipRule();
		this.formDef = formDef;
		this.questionDef = null;
		clearConditions();
	}

	/**
	 * Removes all skip rule conditions.
	 */
	private void clearConditions(){
		if(questionDef != null)
			updateSkipRule();

		questionDef = null;
		lblAction.setText(LocaleText.get("forQuestion"));

		while(verticalPanel.getWidgetCount() > 4)
			verticalPanel.remove(verticalPanel.getWidget(3));

		rdEnable.setValue(false);
		rdDisable.setValue(false);
		rdShow.setValue(false);
		rdHide.setValue(false);
		chkMakeRequired.setValue(false);
		updateMakeRequired();
	}

	/**
	 * Sets whether to enable this widget or not.
	 * 
	 * @param enabled set to true to enable, else false.
	 */
	public void setEnabled(boolean enabled){
		this.enabled = enabled;

		groupHyperlink.setEnabled(enabled);

		rdEnable.setEnabled(enabled);
		rdDisable.setEnabled(enabled);
		rdShow.setEnabled(enabled);
		rdHide.setEnabled(enabled);
		chkMakeRequired.setEnabled(enabled);

		if(!enabled)
			clearConditions();
	}


	/**
	 * Shows a list of other questions that are targets of the current skip rule.
	 */
	private void showOtherQuestions(){
		if(enabled){
			SkipQtnsDialog dialog = new SkipQtnsDialog(this);
			dialog.setData(formDef,questionDef,skipRule);
			dialog.center();
		}
	}


	/**
	 * @see org.purc.purcforms.client.controller.QuestionSelectionListener#onQuestionsSelected(List)
	 */
	public void onQuestionsSelected(List<String> questions){
		if(skipRule == null)
			skipRule = new SkipRule();

		Vector<Integer> oldValue = new Vector<Integer>();
		if(skipRule.getActionTargets() != null){
			for(int index = 0; index < skipRule.getActionTargets().size(); index++){
				oldValue.add(skipRule.getActionTargetAt(index));
			}
		}
		
		//Check if we have any action targets. If we do not, just add all as new.
		List<Integer> actnTargets = skipRule.getActionTargets();
		if(actnTargets == null){
			for(String varName : questions)
				skipRule.addActionTarget(formDef.getQuestion(varName).getId());

			//return;
		}
		else{

			//Remove any de selected action targets from the skip rule.
			for(int index = 0; index < actnTargets.size(); index++){
				Integer qtnId = actnTargets.get(index);

				QuestionDef qtnDef = formDef.getQuestion(qtnId);
				if(qtnDef == questionDef)
					continue; //Ignore the question for which we are editing the skip rule.

				if(qtnDef == null || !questions.contains(qtnDef.getBinding())){
					if(qtnDef == null) //possibly question was deleted or some bug.
						actnTargets.remove(index);
					else
						skipRule.removeActionTarget(qtnDef);

					index = index - 1;
				}
			}

			//Add any newly added questions as action targets.
			for(String varName : questions){
				QuestionDef qtnDef = formDef.getQuestion(varName);
				if(!skipRule.containsActionTarget(qtnDef.getId()))
					skipRule.addActionTarget(qtnDef.getId());
			}
		}
		
		Context.getCommandHistory().add(new ChangeSkipRuleCmd(ChangeSkipRuleCmd.PROPERTY_TARGETS, oldValue, skipRule, this, treeItem, (FormsTreeView)formChangeListener));
	}

	public void setFormChangeListener(IFormChangeListener formChangeListener){
		this.formChangeListener = formChangeListener;
	}

	public void onFormItemSelected(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

	public void setAction(SkipRule skipRule){
		this.skipRule = skipRule;
		setAction(skipRule.getAction());
		
		propertiesView.selectSkipRulesTab();
	}

	public void setCondionsOperator(SkipRule skipRule){
		this.skipRule = skipRule;
		groupHyperlink.setCondionsOperator(skipRule.getConditionsOperator());
		
		propertiesView.selectSkipRulesTab();
	}

	public void setActionTargets(SkipRule skipRule){
		this.skipRule = skipRule;
		
		propertiesView.selectSkipRulesTab();
	}

	public void onItemSelected(Object sender, Object item, boolean userAction){
		if(skipRule == null){
			skipRule = new SkipRule();
			formDef.addSkipRule(skipRule);
		}

		int oldValue = skipRule.getConditionsOperator();
		skipRule.setConditionsOperator(groupHyperlink.getConditionsOperator());

		if(oldValue != skipRule.getConditionsOperator())
			Context.getCommandHistory().add(new ChangeSkipRuleCmd(ChangeSkipRuleCmd.PROPERTY_CONDITIONS_OPERATOR, oldValue, skipRule, this, treeItem, (FormsTreeView)formChangeListener));
	}

	public void onStartItemSelection(Object sender){
		assert(sender == groupHyperlink);
	}
	
	public void setCondionOperator(SkipRule skipRule, Condition condition){
		this.skipRule = skipRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setOperator(condition.getOperator());
		
		propertiesView.selectSkipRulesTab();
	}
	
	public void setCondionQuestion(SkipRule skipRule, Condition condition, QuestionDef questionDef){
		this.skipRule = skipRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setQuestionDef(questionDef);
		
		propertiesView.selectSkipRulesTab();
	}
	
	public void setConditionValue(SkipRule skipRule, Condition condition){
		this.skipRule = skipRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setValue(condition.getValue());
		
		propertiesView.selectSkipRulesTab();
	}
	
	public void setQtnToggleValue(SkipRule skipRule, Condition condition, boolean value){
		this.skipRule = skipRule;
		
		int index = getConditionIndex(condition);
		ConditionWidget widget = (ConditionWidget)verticalPanel.getWidget(index);
		widget.setQtnToggleValue(value);
		
		propertiesView.selectSkipRulesTab();
	}
	
	public void onConditionOperatorChanged(ConditionWidget conditionWidget, int oldOperator){
		Context.getCommandHistory().add(new ChangeSkipConditionCmd(ChangeSkipConditionCmd.PROPERTY_OPERATOR, oldOperator, skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));
	}
	
	public void onConditionValue1Changed(ConditionWidget conditionWidget, String oldValue){
		Context.getCommandHistory().add(new ChangeSkipConditionCmd(ChangeSkipConditionCmd.PROPERTY_VALUE1, oldValue, skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));		
	}
	
	public void onConditionValue2Changed(ConditionWidget conditionWidget, String oldValue){
		Context.getCommandHistory().add(new ChangeSkipConditionCmd(ChangeSkipConditionCmd.PROPERTY_VALUE2, oldValue, skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));
	}
	
	public void onConditionQuestionChanged(ConditionWidget conditionWidget, QuestionDef oldQuestionDef){
		Context.getCommandHistory().add(new ChangeSkipConditionCmd(ChangeSkipConditionCmd.PROPERTY_QUESTION, oldQuestionDef, skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));
	}
	
	public void onConditionQtnValueToggleChanged(ConditionWidget conditionWidget, boolean oldValue){
		Context.getCommandHistory().add(new ChangeSkipConditionCmd(ChangeSkipConditionCmd.PROPERTY_QTN_VALUE_TOGGLE, oldValue, skipRule, conditionWidget, this, treeItem, (FormsTreeView)formChangeListener));
	}
	
	public void onConditionFunctionChanged(ConditionWidget conditionWidget, int oldFunction){
		//Note implemented for skip rules.
	}
}
