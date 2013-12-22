package org.purc.purcforms.client.querybuilder.controller;

import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.querybuilder.view.QueryBuilderView;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.LoginDialog;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;


/**
 * 
 * @author daniel
 *
 */
public class QueryBuilderController {

	//These are constants to remember the current action during the login call back
	//such that we know which action to execute.
	/** No current action. */
	private static final byte CA_NONE = 0;
		
	/** Action for loading a query definition. */
	private static final byte CA_LOAD = 1;

	/** Action for loading results. */
	private static final byte CA_LOAD_RESULTS = 2;
	
	private static final byte CA_LOAD_QUERYLIST = 3;
	
	private static final byte CA_EXPORT_EXCEL = 4;
	
	private static final byte CA_SAVE_QUERY = 5;
	
	private static final byte CA_LOAD_QUERY = 6;
	
	/** The current action by the time to try to authenticate the user at the server. */
	private static byte currentAction = CA_NONE;
	
	private static QueryBuilderView view;
	private static Integer formId;
	private static String queryId;
	
	/** Static self reference such that the static login call back can have
	 *  a reference to proceed with the current action.
	 */
	private static QueryBuilderController controller;
	
	/**
	 * The dialog box used to log on the server when the user's session expires on the server.
	 */
	private static LoginDialog loginDlg = new LoginDialog();
	
	public QueryBuilderController(QueryBuilderView view) {
		QueryBuilderController.view = view;
		controller = this;
	}
	
	/**
	 * Checks if the query designer is in offline mode.
	 * 
	 * @return true if in offline mode, else false.
	 */
	public boolean isOfflineMode(){
		return formId == null;
	}
	
	public void loadResults() {
		if(isOfflineMode())
			getResults();
		else{
			currentAction = CA_LOAD_RESULTS;
			FormUtil.isAuthenticated();
		}
	}
	
	public void loadQueryList() {
		if(isOfflineMode())
			getQueryList();
		else{
			currentAction = CA_LOAD_QUERYLIST;
			FormUtil.isAuthenticated();
		}
	}
	
	public void exportExcel() {
		if(isOfflineMode())
			doExportExcel();
		else{
			currentAction = CA_EXPORT_EXCEL;
			FormUtil.isAuthenticated();
		}
	}
	
	public void saveQuery(String queryId) {
		this.queryId = queryId;
		
		if(isOfflineMode())
			doSaveQuery();
		else{
			currentAction = CA_SAVE_QUERY;
			FormUtil.isAuthenticated();
		}
	}
	
	/**
	 * Loads or opens a query with a given id.
	 * 
	 * @param qryId the query id.
	 */
	public void loadQuery(String qryId){
		queryId = qryId;

		if(isOfflineMode())
			getQuery();
		else{
			currentAction = CA_LOAD_QUERY;
			FormUtil.isAuthenticated();
		}
	}
	
	public void load(Integer frmId){
		formId = frmId;
		
		if(isOfflineMode())
			doLoad();
		else{
			currentAction = CA_LOAD;
			FormUtil.isAuthenticated();
		}
	}
	
	/**
	 * This is called from the server after an attempt to authenticate the current
	 * user before they can submit form data.
	 * 
	 * @param authenticated has a value of true if the server has successfully authenticated the user, else false.
	 */
	private static void authenticationCallback(boolean authenticated) {	

		//If user has passed authentication, just go on with whatever they wanted to do
		//else just redisplay the login dialog and let them enter correct
		//user name and password.
		if(authenticated){	
			loginDlg.hide();
			
			if(currentAction == CA_LOAD)
				controller.doLoad();
			else if(currentAction == CA_LOAD_RESULTS)
				controller.getResults();
			else if(currentAction == CA_LOAD_QUERYLIST)
				controller.getQueryList();
			else if(currentAction == CA_LOAD_QUERY)
				controller.getQuery();
			else if(currentAction == CA_SAVE_QUERY) {
				controller.doSaveQuery();
			}
			else if(currentAction == CA_EXPORT_EXCEL)
				controller.doExportExcel();

			currentAction = CA_NONE;
		}
		else
			loginDlg.center();
	}
	
	private static void getResults() {
		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getFormDefDownloadUrlSuffix();
				url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
				
				try{
					builder.sendRequest(view.getSql(), new RequestCallback(){
						public void onResponseReceived(Request request, Response response){

							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							String html = response.getText();
							if(html == null || html.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}

							view.setResults(html);
							FormUtil.dlg.hide();
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private static void doLoad() {
		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getFormDefDownloadUrlSuffix();
				url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
				
				try{
					builder.sendRequest(null, new RequestCallback(){
						public void onResponseReceived(Request request, Response response){

							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							String xml = response.getText();
							if(xml == null || xml.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}
							
							String xformsXml = null;
							int pos = xml.indexOf(PurcConstants.PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR);
							if(pos > -1) {
								xformsXml = xml.substring(0, pos);
								String queryDefXml = xml.substring(pos+PurcConstants.PURCFORMS_FORMDEF_LAYOUT_XML_SEPARATOR.length(), xml.length());
								view.setQueryDef(queryDefXml);
							}
							else {
								xformsXml = xml;
							}

							view.setXform(xformsXml);
							view.load();
							
							FormUtil.dlg.hide();
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private static void getQuery() {
		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getOpenQueryUrlSuffix();
				url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
				url += "&queryId=" + queryId;
				url += "&action=query";
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
				
				try{
					builder.sendRequest(null, new RequestCallback(){
						public void onResponseReceived(Request request, Response response){

							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							String xml = response.getText();
							if(xml == null || xml.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}
							
							FormUtil.dlg.hide();
							
							view.setQueryDef(xml);
							view.parseQueryDef();
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private static void doExportExcel() {
		
		DeferredCommand.addCommand(new Command(){
			public void execute() {
				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getExportExcelUrlSuffix();
				url += "sql=" + view.getSql();
				url = FormUtil.appendRandomParameter(url);
				Window.Location.replace(URL.encode(url));
			}
		});
	}
	
	private static void doSaveQuery() {
		FormUtil.dlg.setText(LocaleText.get("savingQuery"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getSaveQueryUrlSuffix();
				url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
				url += "&queryId=" + queryId;
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
				
				try{
					builder.sendRequest(view.getQueryDef(), new RequestCallback(){
						public void onResponseReceived(Request request, Response response){

							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							String queryId = response.getText();
							if(queryId == null || queryId.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}

							FormUtil.dlg.hide();
							
							Window.alert(LocaleText.get("querySaveSuccess"));
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
	
	private static void getQueryList() {
		FormUtil.dlg.setText(LocaleText.get("loading"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {

				String url = FormUtil.getHostPageBaseURL();
				url += FormUtil.getOpenQueryUrlSuffix();
				url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
				url += "&action=queryList";
				url = FormUtil.appendRandomParameter(url);

				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
				
				try{
					builder.sendRequest(view.getQueryDef(), new RequestCallback(){
						public void onResponseReceived(Request request, Response response){

							if(response.getStatusCode() != Response.SC_OK){
								FormUtil.displayReponseError(response);
								return;
							}

							String xml = response.getText();
							if(xml == null || xml.length() == 0){
								FormUtil.dlg.hide();
								Window.alert(LocaleText.get("noDataFound"));
								return;
							}
							
							FormUtil.dlg.hide();
							
							view.openQueryList(xml);
						}

						public void onError(Request request, Throwable exception){
							FormUtil.displayException(exception);
						}
					});
				}
				catch(RequestException ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}
}
