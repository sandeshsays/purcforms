package org.purc.purcforms.client.querybuilder.controller;

import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author daniel
 *
 */
public interface AggregateFunctionListener {

	public void onSum(Widget sender);
	public void onAverage(Widget sender);
	public void onMinimum(Widget sender);
	public void onMaximum(Widget sender);
	public void onCount(Widget sender);
	
	public void onPivotSum(Widget sender);
	public void onPivotAverage(Widget sender);
	public void onPivotMinimum(Widget sender);
	public void onPivotMaximum(Widget sender);
	public void onPivotCount(Widget sender);
}
