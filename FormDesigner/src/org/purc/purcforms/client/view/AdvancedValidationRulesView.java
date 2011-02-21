package org.purc.purcforms.client.view;

import org.purc.purcforms.client.controller.IConditionController;
import org.purc.purcforms.client.controller.ItemSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.AdvancedValidationRule;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.skiprule.ConditionWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * 
 * @author Victor Kakama
 * @author ctumwebaze@gmail.com
 * 
 */
public class AdvancedValidationRulesView extends Composite implements SelectionHandler<TreeItem>, IConditionController,
	ItemSelectionListener, ClickHandler {

    /** expression grouping for the questions */
    public static String EXPRESSION_ELEMENT_GROUP_QUESTIONS = "Questions";
    public static String EXPRESSION_ELEMENT_GROUP_COMMON_FUNCTIONS = "Common Functions";
    public static String EXPRESSION_ELEMENT_GROUP_OPERATORS = "Operators";
    public static String EXPRESSION_ELEMENT_GROUP_ARITHMETIC_OPERATORS = "Arithmetic";
    public static String EXPRESSION_ELEMENT_GROUP_COMPARISON_OPERATORS = "Comparison";
    public static String EXPRESSION_ELEMENT_GROUP_LOGICAL_OPERATORS = "Logical";
    public static String EXPRESSION_ELEMENT_GROUP_OTHERS_OPERATORS = "Others";

    /*
     * string functions
     */
    public static String EXPRESSION_ELEMENT_GROUP_FUNCTIONS_STRING = "String";
    public static String EXPRESSION_ELEMENT_STRING_FUNCTION = "string()";
    public static String EXPRESSION_ELEMENT_COMPARE_FUNCTION = "compare()";
    public static String EXPRESSION_ELEMENT_CONCAT_FUNCTION = "concat()";
    public static String EXPRESSION_ELEMENT_SUBSTRING_FUNCTION = "substring";
    public static String EXPRESSION_ELEMENT_STRING_LENGTH_FUNCTION = "string-length()";
    public static String EXPRESSION_ELEMENT_NORMALIZE_SPACE_FUNCTION = "normalize-space()";
    public static String EXPRESSION_ELEMENT_UPPER_CASE_FUNCTION = "upper-case()";
    public static String EXPRESSION_ELEMENT_LOWER_CASE_FUNCTION = "lower-case()";
    public static String EXPRESSION_ELEMENT_CONTAINS_FUNCTION = "contains()";
    public static String EXPRESSION_ELEMENT_STARTS_WITH_FUNCTION = "starts-with()";
    public static String EXPRESSION_ELEMENT_ENDS_WITH_FUNCTION = "ends-with()";

    /*
     * date & time functions
     */
    public static String EXPRESSION_ELEMENT_GROUP_DATE_AND_TIME_FUNCTIONS = "Date & Time";
    public static String EXPRESSION_ELEMENT_DATETIME_FUNCTION = "datetime()";
    public static String EXPRESSION_ELEMENT_YEAR_FROM_DATETIME_FUNCTION = "year-from-dateTime()";
    public static String EXPRESSION_ELEMENT_MONTH_FROM_DATETIME_FUNCTION = "month-from-dateTime()";
    public static String EXPRESSION_ELEMENT_DAY_FROM_DATETIME_FUNCTION = "day-from-dateTime()";
    public static String EXPRESSION_ELEMENT_HOURS_FROM_DATETIME_FUNCTION = "hours-from-dateTime()";
    public static String EXPRESSION_ELEMENT_MINUTES_FROM_DATETIME_FUNCTION = "minutes-from-dateTime()";
    public static String EXPRESSION_ELEMENT_SECONDS_FROM_DATETIME_FUNCTION = "seconds-from-dateTime()";
    public static String EXPRESSION_ELEMENT_YEAR_FROM_DATE_FUNCTION = "year-from-date()";
    public static String EXPRESSION_ELEMENT_MONTH_FROM_DATE_FUNCTION = "month-from-date()";
    public static String EXPRESSION_ELEMENT_DAY_FROM_DATE_FUNCTION = "day-from-date()";
    public static String EXPRESSION_ELEMENT_HOURS_FROM_TIME_FUNCTION = "hours-from-time()";
    public static String EXPRESSION_ELEMENT_MINUTES_FROM_TIME_FUNCTION = "minutes-from-time()";
    public static String EXPRESSION_ELEMENT_SECONDS_FROM_TIME_FUNCTION = "seconds-from-time()";

    /*
     * numeric functions
     */
    public static String EXPRESSION_ELEMENT_GROUP_NUMERIC_FUNCTIONS = "Numeric";
    public static String EXPRESSION_ELEMENT_NUMBER_FUNCTION = "number()";
    public static String EXPRESSION_ELEMENT_ABS_FUNCTION = "abs()";
    public static String EXPRESSION_ELEMENT_ROUND_FUNCTION = "round()";
    public static String EXPRESSION_ELEMENT_FLOOR_FUNCTION = "floor()";
    public static String EXPRESSION_ELEMENT_CEILING_FUNCTION = "ceiling()";
    public static String EXPRESSION_ELEMENT_ROUND_HALF_TO_EVEN_FUNCTION = "round-half-to-even()";

    /*
     * aggregate functions
     */
    public static String EXPRESSION_ELEMENT_GROUP_AGGREGATE_FUNCTIONS = "Aggregate";
    public static String EXPRESSION_ELEMENT_COUNT_FUNCTION = "count()";
    public static String EXPRESSION_ELEMENT_AVG_FUNCTION = "avg()";
    public static String EXPRESSION_ELEMENT_MAX_FUNCTION = "max()";
    public static String EXPRESSION_ELEMENT_MIN_FUNCTION = "min()";
    public static String EXPRESSION_ELEMENT_SUM_FUNCTION = "sum()";

    /*
     * operators
     */
    public static String EXPRESSION_ELEMENT_PLUS_OPERATOR = "+";
    public static String EXPRESSION_ELEMENT_MINUS_OPERATOR = "-";
    public static String EXPRESSION_ELEMENT_DIVISION_OPERATOR = "div";
    public static String EXPRESSION_ELEMENT_MODULUS_OPERATOR = "mod";
    public static String EXPRESSION_ELEMENT_MULTIPLICATION_OPERATOR = "*";
    public static String EXPRESSION_ELEMENT_ASSIGNMENT_OPERATOR = "=";
    public static String EXPRESSION_ELEMENT_EQUALITY_OPERATOR = "==";
    public static String EXPRESSION_ELEMENT_GREATER_THAN_OPERATOR = ">";
    public static String EXPRESSION_ELEMENT_LESS_THAN_OPERATOR = "<";
    public static String EXPRESSION_ELEMENT_NOT_EQUAL_OPERATOR = "!=";
    public static String EXPRESSION_ELEMENT_GREATER_THAN_OR_EQUAL_TO_OPERATOR = ">=";
    public static String EXPRESSION_ELEMENT_LESS_THAN_OR_EQUAL_TO_OPERATOR = "<=";
    public static String EXPRESSION_ELEMENT_BOOLEAN_AND_OPERATOR = "and";
    public static String EXPRESSION_ELEMENT_BOOLEAN_OR_OPERATOR = "or";

    // TODO add other string constants for the xpath expressions

    /** The main or root widget. */
    private VerticalPanel verticalPanel = new VerticalPanel();

    /** The widget horizontal spacing in horizontal panels. */
    private static final int HORIZONTAL_SPACING = 5;

    /** The widget vertical spacing in vertical panels. */
    @SuppressWarnings("unused")
    private static final int VERTICAL_SPACING = 0;

    /* text box for add an expression */
    private TextArea expressionEditor = new TextArea();

    /** stores value for the selected expression element group */
    @SuppressWarnings("unused")
    private String selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_QUESTIONS;

    private Button undoBtn = new Button(LocaleText.get("undo"));
    private Button pasteBtn = new Button(LocaleText.get("paste"));
    private Button okBtn = new Button(LocaleText.get("ok"));
    private Button cancelBtn = new Button(LocaleText.get("cancel"));
    private Button helpBtn = new Button(LocaleText.get("help"));
    protected ListBox expressionElementList = new ListBox(true);
    private HTML descriptionLabel = new HTML("Description");
    private Label actionLabel = new Label();
    private TextBox txtErrorMessage = new TextBox();
    Tree expressionElementGroupTree = null;

    /** The form definition object that this validation rule belongs to. */
    private FormDef formDef;

    /**
     * The question definition object which is the target of the validation
     * rule.
     */
    private QuestionDef questionDef;

    /** The validation rule expression. */
    private AdvancedValidationRule advancedValidationRule;

    public AdvancedValidationRulesView() {
	setupWidgets();

    }

    /**
     * Sets up the widgets.
     */
    private void setupWidgets() {

	expressionElementList.setHeight("150px");

	/*
	 * action label
	 */
	FormUtil.maximizeWidget(actionLabel);
	verticalPanel.add(actionLabel);

	/*
	 * expression editor
	 */
	HorizontalPanel horizontalPanel = new HorizontalPanel();
	horizontalPanel.setSpacing(HORIZONTAL_SPACING);
	horizontalPanel.setWidth("100%");
	FormUtil.maximizeWidget(expressionEditor);
	horizontalPanel.add(expressionEditor);
	verticalPanel.add(horizontalPanel);

	/*
	 * error message
	 */
	txtErrorMessage.setWidth("300px");
	Grid errorMessageGrid = new Grid(1, 2);
	errorMessageGrid.setWidget(0, 0, new Label(LocaleText.get("errorMessage")));
	errorMessageGrid.setWidget(0, 1, txtErrorMessage);
	errorMessageGrid.getCellFormatter().setWidth(0, 0, "100px");
	FormUtil.maximizeWidget(errorMessageGrid);
	verticalPanel.add(errorMessageGrid);

	FlexTable buttonTable = new FlexTable();

	/*
	 * undo button
	 */
	// TODO uncomment the undo and paste button when ready for this
	// functionality
	// undoBtn.addClickHandler(this);
	// buttonTable.setWidget(0, 1, undoBtn);

	/*
	 * paste button
	 */
	// pasteBtn.addClickHandler(this);
	// buttonTable.setWidget(0, 2, pasteBtn);

	/*
	 * expression element grouping
	 */
	expressionElementGroupTree = createExpressionElementGroup();
	ScrollPanel spanel = new ScrollPanel(expressionElementGroupTree);
	spanel.setSize("100%", "150px");
	spanel.setStyleName("block");
	buttonTable.setWidget(1, 0, spanel);

	/*
	 * expression elements
	 */
	buttonTable.setWidget(1, 1, expressionElementList);
	// descriptionLabel.setStyleName("validation-description-label");
	descriptionLabel.setStyleName("block");
	buttonTable.setWidget(1, 2, descriptionLabel);

	okBtn.addClickHandler(this);
	cancelBtn.addClickHandler(this);
	helpBtn.addClickHandler(this);
	buttonTable.setWidget(2, 1, okBtn);
	buttonTable.setWidget(2, 2, helpBtn);
	expressionElementList.setWidth("100%");

	FormUtil.maximizeWidget(buttonTable);
	verticalPanel.add(buttonTable);
	FlexCellFormatter cellFormatter = buttonTable.getFlexCellFormatter();
	cellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
	cellFormatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
	cellFormatter.setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_LEFT);
	cellFormatter.setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
	cellFormatter.setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_RIGHT);
	// cellFormatter.setStyleName(1, 2, "validation-description-label");
	// cellFormatter.setStyleName(1, 0, "validation-description-label");
	cellFormatter.setWidth(0, 0, "40%");
	cellFormatter.setWidth(0, 1, "30%");

	expressionElementList.addClickHandler(new ClickHandler() {
	    @Override
	    public void onClick(ClickEvent event) {

		String token = expressionElementList.getValue(expressionElementList.getSelectedIndex());
		addTokenToExpressionEditor(token);
	    }
	});

	expressionElementList.addChangeHandler(new ChangeHandler() {

	    @Override
	    public void onChange(ChangeEvent event) {
		handleExpressionElementListChange();
	    }

	});

	expressionElementList.addKeyPressHandler(new KeyPressHandler() {

	    @Override
	    public void onKeyPress(KeyPressEvent event) {
		if (event.getCharCode() == KeyCodes.KEY_ENTER) {
		    String token = expressionElementList.getValue(expressionElementList.getSelectedIndex());
		    addTokenToExpressionEditor(token);
		}
	    }
	});
	initWidget(verticalPanel);
    }

    public void undoAction() {
	expressionEditor.setText("");
    }

    public void pasteAction() {
	expressionEditor.setText("=($value (fieldz) < $sum ($value (fieldx), $value (fieldy)) ");
    }

    public void okAction() {
	// descriptionLabel.setText(expressionEditor.getText());
	commitValidationRuleToQuestion();
    }

    public QuestionDef getQuestionDef() {
	return questionDef;
    }

    public void setQuestionDef(QuestionDef questionDef) {
	prepareValidationViewFormQuestionChange();
	if (questionDef != null) {
	    formDef = questionDef.getParentFormDef();
	    actionLabel.setText(LocaleText.get("question") + ": " + questionDef.getDisplayText() + " "
		    + LocaleText.get("isValidWhen"));
	} else
	    actionLabel.setText(LocaleText.get("question") + ": ");

	this.questionDef = questionDef;
	advancedValidationRule = questionDef.getAdvancedValidationRule();
	if (advancedValidationRule != null) {
	    expressionEditor.setText(advancedValidationRule.getValidationExpression());
	    txtErrorMessage.setText(advancedValidationRule.getErrorMessage());
	}

	if (expressionElementGroupTree.getItemCount() > 0) {
	    /*
	     * we are assuming that the Questiond tree item is the first element
	     * if the order of the items is reversed or changed, it important
	     * that this section of the code is edited.
	     */
	    TreeItem questionsTreeItem = expressionElementGroupTree.getItem(0);
	    if (questionsTreeItem != null)
		expressionElementGroupTree.setSelectedItem(questionsTreeItem);

	}
    }

    /**
     * clears the advanced validation condition from the user interface and if
     * the current question definition is not null, it updates the validation
     * expression for the question
     */
    private void prepareValidationViewFormQuestionChange() {
	if (this.questionDef != null) {
	    commitValidationRuleToQuestion();
	}

	this.questionDef = null;
	actionLabel.setText(LocaleText.get("question") + ": ");
	txtErrorMessage.setText(null);
	expressionEditor.setText(null);
    }

    private void commitValidationRuleToQuestion() {
	if (this.advancedValidationRule == null)
	    this.advancedValidationRule = new AdvancedValidationRule();

	this.advancedValidationRule.setValidationExpression(this.expressionEditor.getText());
	this.advancedValidationRule.setErrorMessage(this.txtErrorMessage.getText());
	this.questionDef.setAdvancedValidationRule(this.advancedValidationRule);
    }

    public void cancelAction() {
	System.out.println("cancel==================");
    }

    public void helpAction() {
	System.out.println("help==================");
    }

    public void addQuestions(PageDef pageDef) {
	// questionElements = pageDef.getQuestions();
	/*
	 * check whether question node is selected on the tree and update
	 * accordingly
	 */
	/*
	 * if (questions.isSelected()) { exprElements("Questions"); }
	 */
    }

    private void addTokenToExpressionEditor(String token) {
	int cursorPosition = (expressionEditor.getText() != null && expressionEditor.getText().trim().length() > 0) ? expressionEditor
		.getCursorPos()
		: 0;
	StringBuilder builder = new StringBuilder(expressionEditor.getText());

	if (cursorPosition <= 0)
	    builder.append(token);
	else
	    builder.insert(cursorPosition, token);
	expressionEditor.setText(builder.toString());
	cursorPosition = cursorPosition + (token != null ? token.length() : 0);

	if (cursorPosition <= builder.toString().length())
	    expressionEditor.setCursorPos(cursorPosition);
    }

    public void updateValidationRule() {
	commitValidationRuleToQuestion();
    }

    @Override
    public void addBracket() {
	// TODO Auto-generated method stub

    }

    @Override
    public void addCondition() {
	// adds the condition to the question

    }

    @Override
    public void deleteCondition(ConditionWidget conditionWidget) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onItemSelected(Object sender, Object item) {
	System.out.println("Something has been selected=====================");
    }

    @Override
    public void onStartItemSelection(Object sender) {

    }

    @Override
    public void onClick(ClickEvent event) {
	Object sender = event.getSource();
	if (sender == undoBtn) {
	    undoAction();
	} else if (sender == pasteBtn) {
	    pasteAction();
	} else if (sender == okBtn) {
	    okAction();
	} else if (sender == cancelBtn) {
	    cancelAction();
	} else if (sender == helpBtn) {
	    helpAction();
	}

    }

    /**
     * creates and returns the expression element group tree
     * 
     * @return
     */
    public Tree createExpressionElementGroup() {
	Tree expressionElementTree = new Tree();
	expressionElementTree.addSelectionHandler(this);
	expressionElementTree.ensureSelectedItemVisible();

	/*
	 * QUESTION ELEMENT GROUP
	 */
	expressionElementTree.addItem(new TreeItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_QUESTIONS));

	/*
	 * OPERATOR ELEMENT GROUP
	 */
	TreeItem operatorGroup = new TreeItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_OPERATORS);
	operatorGroup.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_ARITHMETIC_OPERATORS);
	operatorGroup.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_COMPARISON_OPERATORS);
	operatorGroup.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_LOGICAL_OPERATORS);
	expressionElementTree.addItem(operatorGroup);

	/*
	 * COMMON FUNCTION GROUP
	 */
	TreeItem commonFunctions = new TreeItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_COMMON_FUNCTIONS);
	commonFunctions.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_FUNCTIONS_STRING);
	commonFunctions.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_DATE_AND_TIME_FUNCTIONS);
	commonFunctions.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_NUMERIC_FUNCTIONS);
	expressionElementTree.addItem(commonFunctions);
	commonFunctions.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_AGGREGATE_FUNCTIONS);

	return expressionElementTree;
    }

    public void exprElements(String selected) {
	if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_QUESTIONS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_QUESTIONS;
	    expressionElementList.clear();
	    if (formDef != null && formDef.getPages() != null) {
		for (PageDef pageDef : formDef.getPages()) {
		    if (pageDef != null && pageDef.getQuestions() != null) {
			for (QuestionDef question : pageDef.getQuestions()) {
			    if (question.getBinding().equalsIgnoreCase(this.getQuestionDef().getBinding())) {
				expressionElementList.addItem(question.getText(), ".");
			    } else {
				expressionElementList
					.addItem(question.getText(), ("'" + question.getParentFormDef().getBinding()
						+ "/" + question.getBinding() + "'"));
			    }
			}
		    }
		}
	    }

	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_ARITHMETIC_OPERATORS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_ARITHMETIC_OPERATORS;
	    setArithmeticExpressionElements();
	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_COMPARISON_OPERATORS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_COMPARISON_OPERATORS;
	    setComparisonExpressionElements();
	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_LOGICAL_OPERATORS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_LOGICAL_OPERATORS;
	    setLogicalExpressionElements();
	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_FUNCTIONS_STRING)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_FUNCTIONS_STRING;
	    setStringFunctionsExpressionElements();
	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_DATE_AND_TIME_FUNCTIONS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_DATE_AND_TIME_FUNCTIONS;
	    setDateTimeFunctionsExpressionElements();
	} else if (selected.equals(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_NUMERIC_FUNCTIONS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_NUMERIC_FUNCTIONS;
	    setNumericFunctionsExpressionElements();
	} else if (selected.equalsIgnoreCase(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_AGGREGATE_FUNCTIONS)) {

	    this.selectedExpressionElementGroup = AdvancedValidationRulesView.EXPRESSION_ELEMENT_GROUP_AGGREGATE_FUNCTIONS;
	    setAggregateFunctionsExpressionElement();
	}
    }

    private void setAggregateFunctionsExpressionElement() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_COUNT_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_AVG_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MAX_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MIN_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_SUM_FUNCTION);
    }

    private void setNumericFunctionsExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_NUMBER_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_ABS_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_CEILING_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_FLOOR_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_ROUND_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_ROUND_HALF_TO_EVEN_FUNCTION);
    }

    private void setDateTimeFunctionsExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_YEAR_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MONTH_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_DAY_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_HOURS_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MINUTES_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_SECONDS_FROM_DATETIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_YEAR_FROM_DATE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MONTH_FROM_DATE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_DAY_FROM_DATE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_HOURS_FROM_TIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MINUTES_FROM_TIME_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_SECONDS_FROM_TIME_FUNCTION);
    }

    private void setStringFunctionsExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_STRING_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_COMPARE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_CONCAT_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_SUBSTRING_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_STRING_LENGTH_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_NORMALIZE_SPACE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_UPPER_CASE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_LOWER_CASE_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_CONTAINS_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_STARTS_WITH_FUNCTION);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_ENDS_WITH_FUNCTION);
    }

    private void setLogicalExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_BOOLEAN_AND_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_BOOLEAN_OR_OPERATOR);
    }

    private void setComparisonExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_ASSIGNMENT_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_NOT_EQUAL_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GREATER_THAN_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_GREATER_THAN_OR_EQUAL_TO_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_LESS_THAN_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_LESS_THAN_OR_EQUAL_TO_OPERATOR);
    }

    private void setArithmeticExpressionElements() {
	expressionElementList.clear();
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_PLUS_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MINUS_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MULTIPLICATION_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_DIVISION_OPERATOR);
	expressionElementList.addItem(AdvancedValidationRulesView.EXPRESSION_ELEMENT_MODULUS_OPERATOR);
    }

    // construct an expression
    public void buildRule(String expression) {
	StringBuilder sb = new StringBuilder();
	sb.append("");
    }

    // receive events on the tree item
    @Override
    public void onSelection(SelectionEvent<TreeItem> event) {
	TreeItem itm = event.getSelectedItem();
	exprElements(itm.getText().trim());
    }

    private void handleExpressionElementListChange() {
	if (expressionElementList.getSelectedIndex() > -1) {
	    String selectedItemValue = expressionElementList.getValue(expressionElementList.getSelectedIndex());
	    if (selectedItemValue.equalsIgnoreCase(EXPRESSION_ELEMENT_ABS_FUNCTION)) {
		this.descriptionLabel.setHTML("Returns the absolute value of the argument \r\n"
			+ "Example: abs(3.14)<br/>" + "Result: 3.14<br/><br/>" + "Example: abs(-3.14)<br/>"
			+ "Result: 3.14");
	    } else {
		this.descriptionLabel.setHTML(null);
	    }
	}
    }
}