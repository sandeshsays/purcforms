package org.purc.purcforms.client.querybuilder.widget;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.QueryBuilder;
import org.purc.purcforms.client.querybuilder.controller.ConditionController;
import org.purc.purcforms.client.querybuilder.controller.FilterRowActionListener;
import org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author daniel
 *
 */
public class ConditionWidget extends Composite implements ItemSelectionListener, FilterRowActionListener{

	private static final int HORIZONTAL_SPACING = 5;

	private FormDef formDef;
	private FieldWidget fieldWidget;
	private OperatorHyperlink operatorHyperlink;
	private ValueWidget valueWidget = new ValueWidget();
	private HorizontalPanel horizontalPanel;
	private ConditionActionHyperlink actionHyperlink;
	private CheckBox chkSelect = new CheckBox();

	private QuestionDef questionDef;
	private int operator;
	private ConditionController view;
	private Condition condition;
	private Label lbLabel = new Label(LocaleText.get("value"));

	private boolean allowFieldSelection = false;
	private int depth = 1;
	
	public ConditionWidget(FormDef formDef, ConditionController view, boolean allowFieldSelection, QuestionDef questionDef, int depth,AddConditionHyperlink addConditionHyperlink, boolean select){
		this.formDef = formDef;
		this.view = view;
		this.allowFieldSelection = allowFieldSelection;
		this.questionDef = questionDef;
		this.depth = depth;
		setupWidgets(addConditionHyperlink, select);
	}

	private void setupWidgets(AddConditionHyperlink addConditionHyperlink, boolean select){
		actionHyperlink = new ConditionActionHyperlink("<>","",true,depth,addConditionHyperlink,this);
		chkSelect.setValue(select);
		
		if(allowFieldSelection)
			fieldWidget = new FieldWidget(this);

		operatorHyperlink = new OperatorHyperlink(OperatorHyperlink.OP_TEXT_EQUAL,"",this);

		horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(chkSelect);
		horizontalPanel.setSpacing(HORIZONTAL_SPACING);
		horizontalPanel.addStyleName(QueryBuilder.CSS_QUERY_BUILDER_TABLE);
		horizontalPanel.add(actionHyperlink);

		if(allowFieldSelection)
			horizontalPanel.add(fieldWidget);
		else{
			if(questionDef.isGroupQtnsDef())
				lbLabel.setText(LocaleText.get("count"));
			horizontalPanel.add(lbLabel);
		}

		horizontalPanel.add(operatorHyperlink);
		horizontalPanel.add(valueWidget);

		initWidget(horizontalPanel);

		if(allowFieldSelection)
			fieldWidget.setFormDef(formDef);
		valueWidget.setFormDef(formDef);

		operator = ModelConstants.OPERATOR_EQUAL;
		valueWidget.setOperator(operator);
	}

	/**
	 * @see org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener#ontemSelected(java.lang.Object, java.lang.Object)
	 */
	public void onItemSelected(Object sender, Object item) {
		if(sender == fieldWidget /*fieldHyperlink*/){
			questionDef = (QuestionDef)item;
			operatorHyperlink.setDataType(questionDef.getDataType());
			valueWidget.setQuestionDef(questionDef);
			
			//We set the universal operator which is valid for all questions.
			operator = ModelConstants.OPERATOR_EQUAL;
			valueWidget.setOperator(operator);
		}
		else if(sender == operatorHyperlink){
			operator = ((Integer)item).intValue();
			valueWidget.setOperator(operator);

			if(allowFieldSelection)
				fieldWidget.stopSelection();
		}
		else if(sender == valueWidget){

		}
	}

	public void onStartItemSelection(Object sender){
		if(sender != valueWidget)
			valueWidget.stopEdit(false); //Temporary hack to turn off edits when focus goes off the edit widget

		if(allowFieldSelection && sender != fieldWidget)
			fieldWidget.stopSelection();
		/*if(sender == operatorHyperlink){
			fieldWidget.stopSelection();
			//operatorHyperlink.startSelection();
		}*/
	}

	public ConditionWidget addCondition(Widget sender, boolean select){
		return view.addCondition(sender, select);
	}

	public ConditionActionHyperlink addBracket(Widget sender, String operator, boolean addCondition, boolean select){
		return view.addBracket(sender,operator,addCondition, select);
	}

	public void deleteCurrentRow(Widget sender){
		view.deleteCondition(sender,this);
	}

	public Condition getCondition(){
		if(condition == null)
			condition = new Condition();

		condition.setQuestionId(questionDef.getId());
		condition.setOperator(operator);
		condition.setValue(valueWidget.getValue());
		condition.setValueQtnDef(valueWidget.getValueQtnDef());
		condition.setSelected(chkSelect.getValue());
		
		if(condition.getValue() == null)
			return null;

		return condition;
	}

	/*public boolean setCondition(Condition condition){
		this.condition = condition;
		questionDef = formDef.getQuestion(condition.getQuestionId());
		if(questionDef == null)
			return false;

		setQuestionDef(questionDef);

		return true;
	}*/

	public void setQuestionDef(QuestionDef questionDef){
		this.questionDef = questionDef;
		
		valueWidget.setQuestionDef(questionDef);
		operatorHyperlink.setDataType(questionDef.getDataType());
		fieldWidget.setQuestion(questionDef);
	}
	
	public void setOparator(int operator){
		this.operator = operator;
		this.operatorHyperlink.setOperator(operator);
		this.valueWidget.setOperator(operator);
	}
	
	public void setValue(String value){
		valueWidget.setValue(value);
	}
	
	public int getDepth(){
		return depth;
	}
}
