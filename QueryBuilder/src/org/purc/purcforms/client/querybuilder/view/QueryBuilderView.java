package org.purc.purcforms.client.querybuilder.view;

import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener;
import org.purc.purcforms.client.querybuilder.controller.QueryBuilderController;
import org.purc.purcforms.client.querybuilder.model.KeyValue;
import org.purc.purcforms.client.querybuilder.sql.SqlBuilder;
import org.purc.purcforms.client.querybuilder.sql.XmlBuilder;
import org.purc.purcforms.client.querybuilder.util.QueryBuilderUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XformParser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;


/**
 * 
 * @author daniel
 *
 */
public class QueryBuilderView  extends Composite implements SelectionHandler<Integer>,ResizeHandler, ItemSelectionListener {

	private int selectedTabIndex;
	private int xformsXmlIndex = 0;
	private int filterConditionsIndex = 1;
	private int displayFieldsIndex = 2;
	private int queryDefXmlIndex = 3;
	private int sqlIndex = 4;
	private int resultsIndex = 5;
	
	private DecoratedTabPanel tabs = new DecoratedTabPanel();
	private TextArea txtXform = new TextArea();
	private TextArea txtDefXml= new TextArea();
	private TextArea txtSql = new TextArea();
	private HTML htmlResults = new HTML();
	
	private FilterConditionsView filterConditionsView = new FilterConditionsView();
	private DisplayFieldsView displayFieldsView = new DisplayFieldsView();
	
	private QueryBuilderController controller;
	
	private PopupPanel popup;
	
	private SaveQueryDialog saveQueryDialog;
	private String queryId;
	private String queryName;
	private String displayAs = "Report Listing";
	
	private boolean rebuildSql = true;
	
	public interface Images extends ClientBundle {
		ImageResource newquery();
		ImageResource open();
		ImageResource save();
		ImageResource saveas();
		ImageResource delete();
		ImageResource displayas();
		ImageResource spreadsheet();
		ImageResource pdf();
		ImageResource barchart();
		ImageResource piechart();
		ImageResource linechart();
		ImageResource reportlist();
	}
	
	public static final Images images = (Images) GWT.create(Images.class);
	
	public QueryBuilderView(){
		
		txtXform.setWidth("100%");
		txtXform.setHeight("100%");
		tabs.setWidth("100%");
		//tabs.setHeight("100%");
		
		if (QueryBuilderUtil.showXformsXml())
			tabs.add(txtXform,"XForms Source");
		else {
			filterConditionsIndex -= 1;
			displayFieldsIndex -= 1;
			queryDefXmlIndex -= 1;
			sqlIndex -= 1;
			resultsIndex -= 1;
		}
		
		tabs.add(filterConditionsView,"Filter Conditions");
		tabs.add(displayFieldsView,"Display Fields");
		
		if (QueryBuilderUtil.showDefinitionXml())
			tabs.add(txtDefXml,"Definition XML");
		else {
			sqlIndex -= 1;
			resultsIndex -= 1;
		}
		
		if (QueryBuilderUtil.showSql())
			tabs.add(txtSql,"SQL");
		else 
			resultsIndex -= 1;
		
		if (QueryBuilderUtil.showResults()) {
			tabs.add(htmlResults, "Results");
			htmlResults.setWidth("100%");
			htmlResults.setHeight("100%");
		}
		
		tabs.addSelectionHandler(this);
		initWidget(tabs);
		
		tabs.selectTab(filterConditionsIndex);
		
		Window.addResizeHandler(this);

		//		This is needed for IE
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onWindowResized(Window.getClientWidth(), Window.getClientHeight());
			}
		});
		
		txtXform.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				parseXform();
			}
		});
		
		txtDefXml.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				parseQueryDef();
			}
		});
		
		popup = new PopupPanel(true,true);
		MenuBar menuBar = new MenuBar(true);
		
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.newquery(), LocaleText.get("newItem")), true, new Command(){
			public void execute() {popup.hide(); onQueryDeleted();}});
		
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.open(),LocaleText.get("open")),true,new Command(){
			public void execute() {popup.hide(); openQuery();}});

		menuBar.addSeparator();
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.save(),LocaleText.get("save")),true,new Command(){
			public void execute() {popup.hide(); saveQuery();}});
		
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.saveas(),LocaleText.get("saveAs")),true,new Command(){
			public void execute() {popup.hide(); saveAsQuery();}});
		
		menuBar.addSeparator();
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.delete(),LocaleText.get("deleteItem")),true,new Command(){
			public void execute() {popup.hide(); deleteQuery();}});
		
		
		//display as menu items
		menuBar.addSeparator();
		MenuBar displayAsMenu = new MenuBar(true);
		displayAsMenu.addItem(QueryBuilderUtil.createHeaderHTML(images.reportlist(), LocaleText.get("reportListing")),true,new Command(){
			public void execute() {popup.hide(); displayAs = "Report Listing"; showResults();}});
		
		displayAsMenu.addItem(QueryBuilderUtil.createHeaderHTML(images.linechart(), LocaleText.get("lineGraph")),true,new Command(){
			public void execute() {popup.hide(); displayAs = "Line Chart"; showResults();}});
		
		displayAsMenu.addItem(QueryBuilderUtil.createHeaderHTML(images.barchart(), LocaleText.get("barGraph")),true,new Command(){
			public void execute() {popup.hide(); displayAs = "Bar Chart"; showResults();}});
		
		displayAsMenu.addItem(QueryBuilderUtil.createHeaderHTML(images.piechart(), LocaleText.get("pieChart")),true,new Command(){
			public void execute() {popup.hide(); displayAs = "Pie Chart"; showResults();}});
		
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.displayas(),LocaleText.get("displayAs")), true, displayAsMenu);

		
		menuBar.addSeparator();
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.spreadsheet(),LocaleText.get("exportSpreadSheet")),true,new Command(){
			public void execute() {popup.hide(); exportSpreadSheet();}});
		menuBar.addItem(QueryBuilderUtil.createHeaderHTML(images.pdf(),LocaleText.get("exportPdf")),true,new Command(){
			public void execute() {popup.hide(); exportPdf();}});
		
		menuBar.addSeparator();
		menuBar.addItem(LocaleText.get("close"), new Command(){
			public void execute() {close();}});

		popup.setWidget(menuBar);
		
		DOM.sinkEvents(getElement(),DOM.getEventsSunk(getElement()) | Event.ONMOUSEDOWN);
		
		txtXform.setText(FormUtil.formatXml(getTestXform()));
		parseXform();
		
		//txtDefXml.setText(getTestQueryDef());
		//parseQueryDef();
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		int type = DOM.eventGetType(event);

		switch (type) {
		case Event.ONMOUSEDOWN:
			
			QueryBuilderUtil.enableContextMenu(getElement());
			
			if( (event.getButton() & Event.BUTTON_RIGHT) != 0){
				//if("gwt-purcforms".equals(event.getTarget().getClassName())){
					
					int ypos = event.getClientY();
					if(Window.getClientHeight() - ypos < 100)
						ypos = event.getClientY() - 100;
					
					int xpos = event.getClientX();
					if(Window.getClientWidth() - xpos < 110)
						xpos = event.getClientX() - 110;
					
					QueryBuilderUtil.disableContextMenu(popup.getElement());
					QueryBuilderUtil.disableContextMenu(getElement());
					popup.setPopupPosition(xpos, ypos);
					popup.show();
				//}
			}
			break;
		}	
	}

	public void setController(QueryBuilderController controller) {
		this.controller = controller;
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(SelectionEvent)
	 */
	public void onSelection(SelectionEvent<Integer> event){
		final int prevSelectionIndex = selectedTabIndex;
		selectedTabIndex = event.getSelectedItem();
		
		rebuildSql = true;
		
		FormUtil.dlg.setText("Building " + (selectedTabIndex == queryDefXmlIndex ? "Query Definition" : "SQL")); //LocaleText.get("???????")
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					if(selectedTabIndex == queryDefXmlIndex && QueryBuilderUtil.showDefinitionXml())
						buildQueryDef();
					else if(selectedTabIndex == sqlIndex && QueryBuilderUtil.showSql())
						buildSql();
					else if(selectedTabIndex == resultsIndex && QueryBuilderUtil.showResults()) {
						rebuildSql = prevSelectionIndex != sqlIndex;
						showResults();
					}

					FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}	
			}
		});
	}
	
	public void onWindowResized(int width, int height) {
		txtXform.setHeight(height-50+PurcConstants.UNITS);
		txtDefXml.setHeight(height-50+PurcConstants.UNITS);
		txtSql.setHeight(height-50+PurcConstants.UNITS);
	} 
	
	private void parseXform(){
		FormUtil.dlg.setText("Parsing Xform"); //LocaleText.get("???????")
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					FormDef formDef = null;
					String xml = txtXform.getText().trim();
					if(xml.length() > 0) {
						formDef = XformParser.fromXform2FormDef(xml);

						//remove questions which are not filled by the user
						//such could be automatically filled
						for (int pageNo = 0; pageNo < formDef.getPageCount(); pageNo++) {
							PageDef pageDef = formDef.getPageAt(pageNo);
							for (int index = 0; index < pageDef.getQuestionCount(); index++) {
								QuestionDef qtnDef = pageDef.getQuestionAt(index);
								if (!qtnDef.isVisible()) {
									pageDef.removeQuestion(qtnDef, formDef);
									index--;
								}
							}
						}
					}

					filterConditionsView.setFormDef(formDef);
					displayFieldsView.setFormDef(formDef);
					
					FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}	
			}
		});
	}
	
	public void parseQueryDef(){
		FormUtil.dlg.setText("Parsing Query Definition"); //LocaleText.get("???????")
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					String xml = txtDefXml.getText().trim();
					if(xml.length() > 0){
						filterConditionsView.loadQueryDef(xml);
						displayFieldsView.loadQueryDef(xml);

						if(selectedTabIndex == resultsIndex && QueryBuilderUtil.showResults()) {
							showResults();
						}
					}
					
					FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}	
			}
		});
	}
	
	private String getTestXform(){
		return "<xf:xforms xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> " +
			" <xf:model> " +
			"   <xf:instance id=\"newform1\"> " +
			"     <newform1 name=\"New Form1\" id=\"1\"> " +
			"       <question1/> " +
			"       <question2/> " +
			"     </newform1> " +
			"   </xf:instance> " +
			"   <xf:bind id=\"question1\" nodeset=\"/newform1/question1\" type=\"xsd:string\"/> " +
			"   <xf:bind id=\"question2\" nodeset=\"/newform1/question2\" type=\"xsd:string\"/> " +
			"   <xf:bind id=\"question3\" nodeset=\"/newform1/question3\" type=\"xsd:int\"/> " +
			"   <xf:bind id=\"question4\" nodeset=\"/newform1/question4\" type=\"xsd:date\"/> " +
			" </xf:model> " +
			" <xf:group id=\"1\"> " +
			"   <xf:label>Page1</xf:label> " +
			"  <xf:select1 bind=\"question1\"> " +
			"    <xf:label>Question1</xf:label> " +
			"	<item id=\"option1\"> " +
		    "    <label>Option1</label> " +
		    "    <value>option1</value> " +
		    "  </item> " +
		    "  <item id=\"option2\"> " +
		    "    <label>Option2</label> " +
		    "    <value>option2</value> " +
		    "  </item> " +
		    "  <item id=\"option3\"> " +
		    "    <label>Option3</label> " +
		    "    <value>option3</value> " +
		    "  </item> " +
			"  </xf:select1> " +
			"  <xf:input bind=\"question2\"> " +
			"    <xf:label>Question2</xf:label> " +
			"  </xf:input> " +
			"  <xf:input bind=\"question3\"> " +
			"    <xf:label>Question3</xf:label> " +
			"  </xf:input> " +
			"  <xf:input bind=\"question4\"> " +
			"    <xf:label>Question4</xf:label> " +
			"  </xf:input> " +
			" </xf:group> " +
			" </xf:xforms>";
	}
	
	private String getTestQueryDef(){
		return "<querydef> "+
			  " <FilterConditions> "+
			  " <group operator=\"all\"> "+
			  " <group operator=\"all\"> "+
			  "     <condition field=\"question1\" operator=\"7\" value=\"option1,option2\"/> "+
			  "   </group> "+
			  "   <group operator=\"all\"> "+
			  "     <condition field=\"question1\" operator=\"1\" value=\"bbbb\"/> "+
			  "   </group> "+
			  " </group> "+
			  " </FilterConditions> " +
			  " <DisplayFields> " +
			  " 	<Field name=\"question1\" text=\"Last Name\"/> " +
			  " 	<Field name=\"question2\" text=\"First Name\"/> " +
			  " 	<Field name=\"question3\" text=\"Weight\" AggFunc=\"SUM\" /> " +
			  " 	<Field name=\"question4\" text=\"Date of Birth\"/> " +
			  " </DisplayFields> " +
			  " <SortFields> " +
			  " 	<Field name=\"question1\" sortOrder=\"1\"/> " +
			  " 	<Field name=\"question2\" sortOrder=\"2\"/> " +
			  " </SortFields> " +
			  " </querydef>";
	}
	
	private void buildSql(){
		txtSql.setText(SqlBuilder.buildSql(filterConditionsView.getFormDef(),displayFieldsView.getDisplayFields(),filterConditionsView.getFilterConditionRows(),displayFieldsView.getSortFields()));
	}
	
	private void buildQueryDef(){
		txtDefXml.setText(/*FormUtil.formatXml(FormUtil.formatXml(*/XmlBuilder.buildXml(filterConditionsView.getFormDef(),filterConditionsView.getFilterConditionRows(),displayFieldsView.getDisplayFields(),displayFieldsView.getSortFields(), getSql())/*))*/);
	}
	
	private void showResults() {
		controller.loadResults(queryId, displayAs);
	}
	
	public String getQueryDef(){
		buildQueryDef();
		return txtDefXml.getText();
	}
	
	public String getSql(){
		if (rebuildSql) {
			buildSql();
		}
		return txtSql.getText();
	}
	
	public void setXform(String xml){
		txtXform.setText(xml);
		//parseXform();
	}
	
	public void setQueryDef(String xml){
		txtDefXml.setText(xml);
		//parseQueryDef();
	}
	
	public void setSql(String sql){
		txtSql.setText(sql);
	}
	
	public void setResults(String html) {
		htmlResults.setHTML(html);
	}
	
	public void load(){
		parseXform();
		parseQueryDef();
	}
	
	public void onResize(ResizeEvent event){
		onWindowResized(Window.getClientWidth(), Window.getClientHeight());
	}
	
	public void hideDebugTabs(){
		tabs.remove(0);
		tabs.remove(2);
		tabs.remove(2);
	}
	
	/**
	 * Loads a form from the server into the query builder.
	 * 
	 * @param formId the form identifier.
	 */
	public void load(int formId){
		if(formId != -1) {
			controller.load(formId);
		}
	}
	
	public void openQuery() {
		controller.loadQueryList();
	}
	
	public void saveQuery() {
		if (queryId == null) {
			saveAsQuery();
		}
		else {
			controller.saveQuery(queryId);
		}
	}
	
	public void saveAsQuery() {
		String url = FormUtil.getHostPageBaseURL();
		url += FormUtil.getSaveQueryUrlSuffix();
		url += FormUtil.getFormIdName() + "=" + FormUtil.getFormId();
		url = FormUtil.appendRandomParameter(url);
		saveQueryDialog = new SaveQueryDialog(url, getQueryDef(), "New Query", this);
		saveQueryDialog.center();
	}
	
	public void deleteQuery() {
		if (queryName == null) {
			Window.alert("Please first open the query to delete");
			return;
		}
		
		if (!Window.confirm("Do you really want to delete this query: " + queryName + "?"))
			return;
		
		controller.deleteQuery(queryId);
	}
	
	public void displayAs() {
		
	}
	
	public void exportSpreadSheet() {
		controller.exportExcel(queryId, queryName);
	}
	
	public void exportPdf() {
		controller.exportPdf(queryId, displayAs, queryName);
	}
	
	public void openQueryList(String xml) {
		OpenQueryDialog dialog = new OpenQueryDialog(this);
		dialog.loadQueryList(xml);
		dialog.center();
	}
	
	public void onQueryDeleted() {
		queryId =  null;
		queryName = null;
		txtXform.setText("");
		txtDefXml.setText("");
		txtSql.setText("");
		htmlResults.setHTML("");
		filterConditionsView.clearConditions();
		displayFieldsView.clearFields();
		
		Window.setTitle(QueryBuilderUtil.getTitle());
	}

	/**
     * @see org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener#onItemSelected(java.lang.Object, java.lang.Object)
     */
    @Override
    public void onItemSelected(Object sender, Object item) {
    	queryId = ((KeyValue)item).getKey();
    	queryName = ((KeyValue)item).getValue();
    	
   		Window.setTitle(QueryBuilderUtil.getTitle() + " - [" + queryName + "]");

    	if (saveQueryDialog == sender) {
    		Window.alert(LocaleText.get("querySaveSuccess"));
    	}
    	else {
     		controller.loadQuery(queryId);
    	}
    }

	/**
     * @see org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener#onStartItemSelection(java.lang.Object)
     */
    @Override
    public void onStartItemSelection(Object sender) {
	    
    }
    
    public void close() {
    	String url = FormUtil.getCloseUrl();
		if(url != null && url.trim().length() > 0)
			Window.Location.replace(url);
    }
}
