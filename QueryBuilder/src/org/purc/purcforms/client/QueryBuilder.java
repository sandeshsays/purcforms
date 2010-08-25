package org.purc.purcforms.client;

import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class QueryBuilder implements EntryPoint {

	private QueryBuilderWidget queryBuilder;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		FormUtil.setupUncaughtExceptionHandler();	
		
		RootPanel rootPanel = RootPanel.get("querybuilder");
		if(rootPanel == null)
			return;
		
		queryBuilder = new QueryBuilderWidget();

		rootPanel.add(queryBuilder);

		queryBuilder.setWidth("100%");
		queryBuilder.setHeight("100%");
	}
}
