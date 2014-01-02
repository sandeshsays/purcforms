package org.purc.purcforms.client.querybuilder.sql;

import java.util.List;

import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.model.DisplayField;
import org.purc.purcforms.client.querybuilder.model.FilterCondition;
import org.purc.purcforms.client.querybuilder.model.FilterConditionGroup;
import org.purc.purcforms.client.querybuilder.model.FilterConditionRow;
import org.purc.purcforms.client.querybuilder.model.SortField;
import org.purc.purcforms.client.querybuilder.util.QueryBuilderUtil;
import org.purc.purcforms.client.querybuilder.widget.GroupHyperlink;


/**
 * 
 * @author daniel
 *
 */
public class SqlBuilder {

	private static String DATE_SEPARATOR = "'";
	private static  String LIKE_SEPARATOR = "%";

	public static String buildSql(FormDef formDef, List<DisplayField> displayFields, FilterConditionGroup filterConditionGroup, List<SortField> sortFields){
		if(formDef == null || filterConditionGroup == null)
			return null;

		String tableAlias = QueryBuilderUtil.getTableAlias();
		if (tableAlias == null) {
			tableAlias = "";
		}
		
		String sql = "SELECT " + getSelectList(displayFields) + " \r\nFROM " + formDef.getBinding() + " " + tableAlias;

		String filter = "";
		if(filterConditionGroup.getConditionCount() > 0)
			filter = getFilter(filterConditionGroup);

		if(filter.length() > 0)
			sql = sql + " \r\nWHERE " + filter;
		
		String groupByClause = getGroupByClause(displayFields);
		if(groupByClause != null)
			sql = sql + " \r\nGROUP BY " + groupByClause;

		
		String orderByClause = getOrderByClause(sortFields);
		if(orderByClause != null)
			sql = sql + " \r\nORDER BY " + orderByClause;

		return sql;
	}
	
	private static String getFieldMapping(String name) {
		String mapping = QueryBuilderUtil.getColumnMapping(name);
		if (mapping != null) {
			return mapping;
		}
		else {
			return "t." + name;
		}
	}
	
	private static String getSelectList(List<DisplayField> displayFields){
		if(displayFields == null || displayFields.size() == 0)
			return "*";
		
		String selectList = null;
		
		for(DisplayField field : displayFields){
			if(selectList == null)
				selectList = "";
			else
				selectList += ",";
			
			String aggFunc = field.getAggFunc();
			if(aggFunc != null)
				selectList += aggFunc + "(";
			
			selectList += getFieldMapping(field.getName());
			
			if(aggFunc != null)
				selectList +=")";
			
			selectList += " AS '" + field.getText()+"'";
		}
		
		return selectList;
	}
	
	private static String getGroupByClause(List<DisplayField> displayFields){
		if(displayFields == null || displayFields.size() == 0)
			return null;
		
		int aggFuncCount = 0;
		String groupByClause = null;
		for(DisplayField field : displayFields){
			if(groupByClause == null && field.getAggFunc() == null)
				groupByClause = "";
			else if(field.getAggFunc() == null)
				groupByClause += ",";
			
			if(field.getAggFunc() == null)
				groupByClause += getFieldMapping(field.getName());
			else
				aggFuncCount++;
		}
		
		if(aggFuncCount > 0 && aggFuncCount < displayFields.size())
			return groupByClause;
		return null;
	}
	
	private static String getOrderByClause(List<SortField> sortFields){
		if(sortFields == null || sortFields.size() == 0)
			return null;
		
		String orderByClause = null;
		
		for(SortField field : sortFields){
			if(orderByClause == null)
				orderByClause = "";
			else
				orderByClause += ",";
			
			orderByClause += getFieldMapping(field.getName()) + " " + (field.getSortOrder() == SortField.SORT_ASCENDING ? "ASC" : "DESC");
		}
		
		return orderByClause;
	}

	private static String getFilter(FilterConditionGroup filterGroup){

		String filter = "";
		
		if (!filterGroup.hasAnySelectedCondition())
			return filter;

		List<FilterConditionRow> rows = filterGroup.getConditions();
		for(FilterConditionRow row : rows){
			
			if (!row.isSelected())
				continue;
			
			if(filter.length() > 0) {
				if (row instanceof FilterCondition || (row instanceof FilterConditionGroup && ((FilterConditionGroup)row).hasAnySelectedCondition())) {
					filter += getSQLInnerCombiner(filterGroup.getConditionsOperator());
				}
			}
			
			if(row instanceof FilterConditionGroup)
				filter += getFilter((FilterConditionGroup)row);
			else
				filter += getFilter((FilterCondition)row);
		}
		
		if(filter.length() > 0 && filterGroup.isSelected())
			filter = getSQLOuterCombiner(filterGroup.getConditionsOperator()) + "(" + filter + ")";

		return filter;
	}

	private static String getFilter(FilterCondition condition){		
		String filter = "t." + condition.getFieldName();
		filter += getDBOperator(condition.getOperator());
		filter += getQuotedValue(condition.getFirstValue(),condition.getDataType(),condition.getOperator());
		
		if (condition.getOperator() == ModelConstants.OPERATOR_IN_LIST || condition.getOperator() == ModelConstants.OPERATOR_NOT_IN_LIST) {
			filter += ")";
		}
		
		return filter;
	}

	private static String getDBOperator(int operator)
	{
		switch(operator)
		{
		case ModelConstants.OPERATOR_EQUAL:
			return " = ";
		case ModelConstants.OPERATOR_NOT_EQUAL:
			return " <> ";
		case ModelConstants.OPERATOR_LESS:
			return " < ";
		case ModelConstants.OPERATOR_LESS_EQUAL:
			return " <= ";
		case ModelConstants.OPERATOR_GREATER:
			return " > ";
		case ModelConstants.OPERATOR_GREATER_EQUAL:
			return " >= ";
		case ModelConstants.OPERATOR_IS_NULL:
			return " IS NULL";
		case ModelConstants.OPERATOR_IS_NOT_NULL:
			return " IS NOT NULL";
		case ModelConstants.OPERATOR_IN_LIST:
			return " IN (";
		case ModelConstants.OPERATOR_NOT_IN_LIST:
			return " NOT IN (";
		case ModelConstants.OPERATOR_STARTS_WITH:
			return " LIKE ";
		case ModelConstants.OPERATOR_NOT_START_WITH:
			return " NOT LIKE ";
		case ModelConstants.OPERATOR_CONTAINS:
			return " LIKE ";
		case ModelConstants.OPERATOR_NOT_CONTAIN:
			return " NOT LIKE ";
		case ModelConstants.OPERATOR_BETWEEN:
			return " BETWEEN ";
		case ModelConstants.OPERATOR_NOT_BETWEEN:
			return " NOT BETWEEN ";
		case ModelConstants.OPERATOR_ENDS_WITH:
			return " LIKE ";
		case ModelConstants.OPERATOR_NOT_END_WITH:
			return " NOT LIKE ";
		}

		return null;
	}

	private static String getQuotedValue(String fieldVal,int dataType, int operator)
	{
		if(operator == ModelConstants.OPERATOR_IS_NULL || operator == ModelConstants.OPERATOR_IS_NOT_NULL)
			return "";
		
		switch(dataType)
		{
		case QuestionDef.QTN_TYPE_TEXT:
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE:
		case QuestionDef.QTN_TYPE_LIST_MULTIPLE:
		case QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC:
		{
			String value = null;
			if(operator == ModelConstants.OPERATOR_STARTS_WITH || operator == ModelConstants.OPERATOR_NOT_START_WITH)
				value = fieldVal + LIKE_SEPARATOR;
			if(operator == ModelConstants.OPERATOR_NOT_END_WITH || operator == ModelConstants.OPERATOR_NOT_END_WITH)
				value = LIKE_SEPARATOR + fieldVal;
			if(operator == ModelConstants.OPERATOR_CONTAINS || operator == ModelConstants.OPERATOR_NOT_CONTAIN)
				value = LIKE_SEPARATOR + fieldVal + LIKE_SEPARATOR;
			else
				value = fieldVal;
			
			if (dataType == QuestionDef.QTN_TYPE_TEXT)
				value = "'" + value + "'";
			
			return value;
		}
		case QuestionDef.QTN_TYPE_DATE:
			return DATE_SEPARATOR + fieldVal + DATE_SEPARATOR;
		default:
			return " " + fieldVal + " ";
		}
	}

	private static String getSQLInnerCombiner(String val)
	{
		if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL))
			return " AND ";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ANY))
			return " OR ";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_NONE))
			return " OR ";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_NOT_ALL))
			return " AND ";

		return null;
	}

	private static String getSQLOuterCombiner(String val)
	{
		if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ALL))
			return "";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_ANY))
			return "";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_NONE))
			return " NOT ";
		else if(val.equalsIgnoreCase(GroupHyperlink.CONDITIONS_OPERATOR_TEXT_NOT_ALL))
			return " NOT ";

		return null;
	}
}
