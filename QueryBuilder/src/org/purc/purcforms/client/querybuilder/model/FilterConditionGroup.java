package org.purc.purcforms.client.querybuilder.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author daniel
 *
 */
public class FilterConditionGroup extends FilterConditionRow {

	
	/** Operator for combining more than one condition. (And, Or) only these two for now. */
	private String conditionsOperator;
	
	private boolean selected;
	
	private List<FilterConditionRow> conditions = new ArrayList<FilterConditionRow>();
	
	
	public FilterConditionGroup(){
		
	}

	public String getConditionsOperator() {
		return conditionsOperator;
	}


	public void setConditionsOperator(String conditionsOperator) {
		this.conditionsOperator = conditionsOperator;
	}

	public List<FilterConditionRow> getConditions() {
		return conditions;
	}

	public void setConditions(List<FilterConditionRow> conditions) {
		this.conditions = conditions;
	}
	
	public int getConditionCount(){
		return conditions.size();
	}
	
	public void addCondition(FilterConditionRow condition){
		conditions.add(condition);
	}
	
	public FilterConditionRow getConditionAt(int index){
		return conditions.get(index);
	}
	
    /**
     * @return the selected
     */
    public boolean isSelected() {
    	return selected;
    }
	
    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
}
