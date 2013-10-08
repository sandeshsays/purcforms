package org.purc.purcforms.client.querybuilder;

import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.querybuilder.util.QueryBuilderUtil;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class QueryBuilder implements EntryPoint {

	private QueryBuilderWidget queryBuilder;
	
	/**
	 * This is the GWT entry point method.
	 */
	public void onModuleLoad() {

		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		publishJS();

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onModuleLoadDeffered();
			}
		});		
	}		
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoadDeffered() {
		FormUtil.setupUncaughtExceptionHandler();	
		
		RootPanel rootPanel = RootPanel.get("querybuilder");
		if(rootPanel == null) {
			FormUtil.dlg.hide();
			return;
		}
		
		QueryBuilderUtil.retrieveUserDivParameters();
		
		queryBuilder = new QueryBuilderWidget();

		rootPanel.add(queryBuilder);

		queryBuilder.setWidth("100%");
		queryBuilder.setHeight("100%");
		
		//If a form id has been specified in the html host page, load the form
		//with that id in the designer.
		String s = FormUtil.getFormId();
		if(s != null) {
			queryBuilder.loadQuery(getQueryId(s));
		}
	}
	
	private Integer getQueryId(String sId){
		try{
			return Integer.parseInt(sId);
		}
		catch(Exception ex){
			return 1;
		}
	}
	
	// Set up the JS-callable signature as a global JS function.
	private native void publishJS() /*-{
   		$wnd.authenticationCallback = @org.purc.purcforms.client.querybuilder.controller.QueryBuilderController::authenticationCallback(Z);
	}-*/;
}
