package org.purc.purcforms.client.querybuilder.widget;

import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.controller.AggregateFunctionListener;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author daniel
 *
 */
public class AggregateFunctionHyperlink extends Hyperlink {

	public static final String FUNC_TEXT_SUM = "Sum"; //LocaleText.get("???");
	public static final String FUNC_TEXT_AVG = "Average";
	public static final String FUNC_TEXT_MIN = "Minimum";
	public static final String FUNC_TEXT_MAX = "Maximum";
	public static final String FUNC_TEXT_COUNT = "Count";
	
	public static final String FUNC_VALUE_SUM = "SUM"; //LocaleText.get("???");
	public static final String FUNC_VALUE_AVG = "AVG";
	public static final String FUNC_VALUE_MIN = "MIN";
	public static final String FUNC_VALUE_MAX = "MAX";
	public static final String FUNC_VALUE_COUNT = "COUNT";
	
	public static final String FUNC_TEXT_PIVOT_SUM = "Pivot Sum"; //LocaleText.get("???");
	public static final String FUNC_TEXT_PIVOT_AVG = "Pivot Average";
	public static final String FUNC_TEXT_PIVOT_MIN = "Pivot Minimum";
	public static final String FUNC_TEXT_PIVOT_MAX = "Pivot Maximum";
	public static final String FUNC_TEXT_PIVOT_COUNT = "Pivot Count";
	
	public static final String FUNC_VALUE_PIVOT_SUM = "PIVOT_SUM"; //LocaleText.get("???");
	public static final String FUNC_VALUE_PIVOT_AVG = "PIVOT_AVG";
	public static final String FUNC_VALUE_PIVOT_MIN = "PIVOT_MIN";
	public static final String FUNC_VALUE_PIVOT_MAX = "PIVOT_MAX";
	public static final String FUNC_VALUE_PIVOT_COUNT = "PIVOT_COUNT";

	private PopupPanel popup;
	private AggregateFunctionListener actionListener;
	private QuestionDef questionDef;


	public AggregateFunctionHyperlink(String text, String targetHistoryToken ,AggregateFunctionListener actionListener){
		super(text,targetHistoryToken);
		this.actionListener = actionListener;
		DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONMOUSEDOWN );
	}

	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			setupPopup();
			popup.setPopupPosition(event.getClientX(), event.getClientY());
			popup.show();
		}
	}

	private void setupPopup(){
		if(questionDef == null)
			return;

		popup = new PopupPanel(true,true);

		MenuBar menuBar = new MenuBar(true);

		final Widget w = this;

		//LocaleText.get("???")
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL){

			menuBar.addItem(FUNC_TEXT_SUM,true, new Command(){
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_SUM); actionListener.onSum(w);}});

			menuBar.addItem(FUNC_TEXT_AVG,true, new Command(){
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_AVG); actionListener.onAverage(w);}});
		
			menuBar.addSeparator();
		}

		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_DATE ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_TIME ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_DATE_TIME){

			menuBar.addItem(FUNC_TEXT_MIN,true, new Command(){ //LocaleText.get("???")
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_MIN); actionListener.onMinimum(w);}});

			menuBar.addItem(FUNC_TEXT_MAX,true, new Command(){ //LocaleText.get("???")
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_MAX); actionListener.onMaximum(w);}});
			
			menuBar.addSeparator();
		}

		menuBar.addItem(FUNC_TEXT_COUNT,true, new Command(){ //LocaleText.get("???")
			public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_COUNT); actionListener.onCount(w);}});

		
		//cross tab
		//LocaleText.get("???")
		
		if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE ||
				questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE){

			menuBar.addSeparator();
			
			/*menuBar.addItem(FUNC_TEXT_PIVOT_SUM,true, new Command(){
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_PIVOT_SUM); actionListener.onPivotSum(w);}});

			menuBar.addItem(FUNC_TEXT_PIVOT_AVG,true, new Command(){
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_PIVOT_AVG); actionListener.onPivotAverage(w);}});

			menuBar.addSeparator();
			menuBar.addItem(FUNC_TEXT_PIVOT_MIN,true, new Command(){ //LocaleText.get("???")
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_PIVOT_MIN); actionListener.onPivotMinimum(w);}});

			menuBar.addItem(FUNC_TEXT_PIVOT_MAX,true, new Command(){ //LocaleText.get("???")
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_PIVOT_MAX); actionListener.onPivotMaximum(w);}});
			
			menuBar.addSeparator();*/

			menuBar.addItem(FUNC_TEXT_PIVOT_COUNT,true, new Command(){ //LocaleText.get("???")
				public void execute() {popup.hide(); ((Hyperlink)w).setText(FUNC_TEXT_PIVOT_COUNT); actionListener.onPivotCount(w);}});
		}

				
		popup.setWidget(menuBar);
	}

	public void setQuestionDef(QuestionDef questionDef){
		this.questionDef = questionDef;
	}
	
	public String getAggregateFunction(){
		String text = getText();
		
		if(text.equals(FUNC_TEXT_SUM))
			return FUNC_VALUE_SUM;
		else if(text.equals(FUNC_TEXT_AVG))
			return FUNC_VALUE_AVG;
		else if(text.equals(FUNC_TEXT_MIN))
			return FUNC_VALUE_MIN;
		else if(text.equals(FUNC_TEXT_MAX))
			return FUNC_VALUE_MAX;
		if(text.equals(FUNC_TEXT_PIVOT_SUM))
			return FUNC_VALUE_PIVOT_SUM;
		else if(text.equals(FUNC_TEXT_PIVOT_AVG))
			return FUNC_VALUE_PIVOT_AVG;
		else if(text.equals(FUNC_TEXT_PIVOT_MIN))
			return FUNC_VALUE_PIVOT_MIN;
		else if(text.equals(FUNC_TEXT_PIVOT_MAX))
			return FUNC_VALUE_PIVOT_MAX;
		else if(text.equals(FUNC_TEXT_PIVOT_COUNT)) {
			return FUNC_VALUE_PIVOT_COUNT;
		}
		else
			return FUNC_VALUE_COUNT;
	}
	
	public void setAggregateFunction(String aggFunc){
		if(aggFunc.equals(FUNC_VALUE_SUM))
			aggFunc = FUNC_TEXT_SUM;
		else if(aggFunc.equals(FUNC_VALUE_AVG))
			aggFunc = FUNC_TEXT_AVG;
		else if(aggFunc.equals(FUNC_VALUE_MIN))
			aggFunc = FUNC_TEXT_MIN;
		else if(aggFunc.equals(FUNC_VALUE_MAX))
			aggFunc = FUNC_TEXT_MAX;
		if(aggFunc.equals(FUNC_VALUE_PIVOT_SUM))
			aggFunc = FUNC_TEXT_PIVOT_SUM;
		else if(aggFunc.equals(FUNC_VALUE_PIVOT_AVG))
			aggFunc = FUNC_TEXT_PIVOT_AVG;
		else if(aggFunc.equals(FUNC_VALUE_PIVOT_MIN))
			aggFunc = FUNC_TEXT_PIVOT_MIN;
		else if(aggFunc.equals(FUNC_VALUE_PIVOT_MAX))
			aggFunc = FUNC_TEXT_PIVOT_MAX;
		else if(aggFunc.equals(FUNC_VALUE_PIVOT_COUNT))
			aggFunc = FUNC_TEXT_PIVOT_COUNT;
		else
			aggFunc = FUNC_TEXT_COUNT;
		
		setText(aggFunc);
	}
}
