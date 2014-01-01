package org.purc.purcforms.client.querybuilder.view;

import org.purc.purcforms.client.PurcConstants;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.querybuilder.controller.ItemSelectionListener;
import org.purc.purcforms.client.querybuilder.model.KeyValue;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * Handles selection of query to open
 * 
 * @author daniel
 *
 */
public class OpenQueryDialog  extends DialogBox {

	/** Widget displaying list of all questions on the form. */
	private ListBox lbQueryList = new ListBox(true);
	
	/** Button to commit changes and close this dialog box. */
	private Button btnOk = new Button(LocaleText.get("open"));
	
	/** Button to cancel changes, if any, and close this dialog box. */
	private Button btnCancel = new Button(LocaleText.get("cancel"));
	
	/** Main or root widget for this dialog box. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	private ItemSelectionListener listener;
	
	/**
	 * Creates a new instance of skip questions dialog box.
	 */
	public OpenQueryDialog(ItemSelectionListener listener){
		this.listener = listener;
		
		lbQueryList.setWidth("250"+PurcConstants.UNITS);
		lbQueryList.setHeight("200"+PurcConstants.UNITS);
		
		setWidget(mainPanel);
		
		HorizontalPanel horzPanel = new HorizontalPanel();
		horzPanel.add(lbQueryList);
		
		lbQueryList.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				btnOk.setEnabled(true);
			}
		});

		horzPanel.setSpacing(5);
		mainPanel.add(horzPanel);

		setupOkCancelButtons();
		
		mainPanel.setSpacing(5);
		
		setText(LocaleText.get("selectQuery"));
	}	
	
	/**
	 * Sets up the Ok and Cancel buttons.
	 */
	private void setupOkCancelButtons(){
		btnOk.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
				int index = lbQueryList.getSelectedIndex();
				listener.onItemSelected(this, new KeyValue(lbQueryList.getValue(index), lbQueryList.getItemText(index)));
			}
		});
		
		lbQueryList.addDoubleClickHandler(new DoubleClickHandler() {
			public void onDoubleClick(DoubleClickEvent event) {
				int index = lbQueryList.getSelectedIndex();
				if (index >= 0) {
					hide();
					listener.onItemSelected(this, new KeyValue(lbQueryList.getValue(index), lbQueryList.getItemText(index)));
				}
			}
		});
		
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		HorizontalPanel horzPanel = new HorizontalPanel();
		
		horzPanel.add(btnOk);
		horzPanel.add(btnCancel);
		
		horzPanel.setCellHorizontalAlignment(btnOk, HasAlignment.ALIGN_CENTER);
		horzPanel.setCellHorizontalAlignment(btnCancel, HasAlignment.ALIGN_CENTER);
		FormUtil.maximizeWidget(horzPanel);
		
		btnOk.setEnabled(false);
		
		mainPanel.add(horzPanel);
	}
	
	public void loadQueryList(String xml) {
		Document doc = XMLParser.parse(xml);
		Element rootNode = doc.getDocumentElement();
		if(!rootNode.getNodeName().equalsIgnoreCase("querylist"))
			return;
		
		NodeList nodes = rootNode.getElementsByTagName("query");
		if(nodes == null || nodes.getLength() == 0)
			return;

		for(int index = 0; index < nodes.getLength(); index++){
			Element node = (Element)nodes.item(index);
			lbQueryList.addItem(node.getAttribute("name"), node.getAttribute("id"));
		}
	}
}
