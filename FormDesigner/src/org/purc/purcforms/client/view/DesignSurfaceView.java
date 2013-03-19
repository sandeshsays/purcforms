package org.purc.purcforms.client.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.LeftPanel.Images;
import org.purc.purcforms.client.cmd.CommandList;
import org.purc.purcforms.client.cmd.InsertTabCmd;
import org.purc.purcforms.client.controller.DragDropListener;
import org.purc.purcforms.client.controller.FormDesignerDragController;
import org.purc.purcforms.client.controller.FormDesignerDropController;
import org.purc.purcforms.client.controller.IWidgetPopupMenuListener;
import org.purc.purcforms.client.controller.LayoutChangeListener;
import org.purc.purcforms.client.controller.WidgetSelectionListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.widget.DatePickerWidget;
import org.purc.purcforms.client.widget.DateTimeWidget;
import org.purc.purcforms.client.widget.DesignGroupWidget;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;
import org.purc.purcforms.client.widget.RadioButtonWidget;
import org.purc.purcforms.client.widget.TimeWidget;
import org.purc.purcforms.client.widget.WidgetEx;
import org.purc.purcforms.client.widget.grid.GridDesignGroupWidget;
import org.purc.purcforms.client.widget.grid.HorizontalGridLine;
import org.purc.purcforms.client.widget.grid.VerticalGridLine;
import org.purc.purcforms.client.xforms.XformConstants;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * The surface onto which to drag and drop widgets.
 * 
 * @author daniel
 *
 */
public class DesignSurfaceView extends DesignGroupView implements SelectionHandler<Integer>,DragDropListener,IWidgetPopupMenuListener{

	/** Height in pixels of the selected page. */
	private String sHeight = "100%"; //"100%";

	/** Width in pixels of the selected page. */
	private String sWidth = "100%";

	/** Listener to layout change events. */
	private LayoutChangeListener layoutChangeListener;

	/** The form being designed. */
	FormDef formDef;

	/** The layout xml document object. */
	Document doc;

	private static DesignGroupView selectedView;

	public DesignSurfaceView(){
		super(null);
	}

	/**
	 * Creates a new instance of the design surface.
	 * 
	 * @param images the images used by the design surface.
	 */
	public DesignSurfaceView(Images images){
		super(images);

		FormUtil.maximizeWidget(tabs);
		initPanel();
		tabs.selectTab(0);

		initWidget(tabs);
		tabs.addSelectionHandler(this);

		DOM.sinkEvents(getElement(),DOM.getEventsSunk(getElement()) | Event.MOUSEEVENTS | Event.KEYEVENTS);

		setupPopup();

		widgetPopup = new PopupPanel(true,true);
		MenuBar menuBar = new MenuBar(true);
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.cut(),LocaleText.get("cut")),true,new Command(){
			public void execute() {widgetPopup.hide(); cutWidgets();}});

		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.copy(),LocaleText.get("copy")),true,new Command(){
			public void execute() {widgetPopup.hide(); copyWidgets(false);}});

		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.delete(),LocaleText.get("deleteItem")),true, new Command(){
			public void execute() {widgetPopup.hide(); deleteSelectedItem();}});
		
		
		menuBar.addSeparator();	
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("lockWidgets")),true, new Command(){
			public void execute() {widgetPopup.hide(); lockWidgets();}});
		
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("unLockWidgets")),true, new Command(){
			public void execute() {widgetPopup.hide(); unLockWidgets();}});
		

		menuBar.addSeparator(); //LocaleText.get("??????")?????????
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("changeWidgetH")),true, new Command(){
			public void execute() {widgetPopup.hide(); changeWidget(false);}});

		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("changeWidgetV")),true, new Command(){
			public void execute() {widgetPopup.hide(); changeWidget(true);}});

		menuBar.addSeparator();
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("groupWidgets")),true,new Command(){
			public void execute() {widgetPopup.hide(); groupWidgets();}});

		//menuBar.addSeparator();
		//menuBar.addItem(lockWidgetsMenu);

		widgetPopup.setWidget(menuBar);

		rubberBand.addStyleName("rubberBand");

		currentWidgetSelectionListener = this;
		
		//DOM.setAttribute(getElement(), "contentEditable", "true");
	}

	/**
	 * Processes keyboard events for the design surface.
	 * 
	 * @param event the event object.
	 * @return true if processed, else false.
	 */
	public boolean handleKeyBoardEvent(Event event){
		if(!childHandleKeyDownEvent(event)) {
			if (!handleKeyDownEvent(event) && selectedView != null) {
				//ignore keyboard events from the widget properties panel
				String s = event.getEventTarget().toString();
				if(!(s.startsWith("<input ") || s.contains("HTMLInputElement"))) {
					selectedView.addLabelAndStartEditing(event);
				}
			}
		}

		return true;
	}

	/**
	 * Sets up the design surface panel for the first page.
	 */
	protected void initPanel(){
		AbsolutePanel panel = new AbsolutePanel();
		FormUtil.maximizeWidget(panel);
		tabs.add(panel,LocaleText.get("page") + "1");

		selectedPanel = panel;

		super.initPanel();

		tabDropControllers.add(tabs.getWidgetCount()-1, dropController);
		panel.setHeight(sHeight);

		//This is needed for IE
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				setHeight(getHeight());
			}
		});
	}

	/**
	 * Sets up up the context menu popup for the design surface.
	 */
	private void setupPopup(){
		popup = new PopupPanel(true,true);

		MenuBar menuBar = new MenuBar(true);

		MenuBar addControlMenu = new MenuBar(true);

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("label")),true,new Command(){
			public void execute() {popup.hide(); addNewLabel(LocaleText.get("label"),true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("textBox")),true,new Command(){
			public void execute() {popup.hide(); addNewTextBox(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("checkBox")),true,new Command(){
			public void execute() {popup.hide(); addNewCheckBox(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("radioButton")),true,new Command(){
			public void execute() {popup.hide(); addNewRadioButton(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("listBox")),true,new Command(){
			public void execute() {popup.hide(); addNewDropdownList(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("textArea")),true,new Command(){
			public void execute() {popup.hide(); addNewTextArea(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("button")),true,new Command(){
			public void execute() {popup.hide(); addNewButton(LocaleText.get("button"),null,true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("datePicker")),true,new Command(){
			public void execute() {popup.hide(); addNewDatePicker(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("dateTimeWidget")),true,new Command(){
			public void execute() {popup.hide(); addNewDateTimeWidget(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("timeWidget")),true,new Command(){
			public void execute() {popup.hide(); addNewTimeWidget(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("groupBox")),true,new Command(){
			public void execute() {popup.hide(); addNewGroupBox(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("repeatSection")),true,new Command(){
			public void execute() {popup.hide(); addNewRepeatSection(true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("picture")),true,new Command(){
			public void execute() {popup.hide(); addNewPictureSection(null,null,true);}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("videoAudio")),true,new Command(){
			public void execute() {popup.hide(); addNewVideoAudioSection(null,null,true);}});

		/*addControlMenu.addSeparator();

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Group Box"),true,new Command(){
			public void execute() {popup.hide(); addNewButton();}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Auto Complete TextBox"),true,new Command(){
			public void execute() {popup.hide(); addNewTextBox();}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Time Picker"),true,new Command(){
			public void execute() {popup.hide(); ;}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Date & Time Picker"),true,new Command(){
			public void execute() {popup.hide(); ;}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Image"),true,new Command(){
			public void execute() {popup.hide(); ;}});

		addControlMenu.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),"Attachment"),true,new Command(){
			public void execute() {popup.hide(); ;}});*/

		menuBar.addItem("     "+LocaleText.get("addWidget"),addControlMenu);
		
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("selectedFormField")),true,new Command(){
			public void execute() {popup.hide(); addSelectedFormField(true);}});

		//if(selectedDragController.isAnyWidgetSelected()){
		deleteWidgetsSeparator = menuBar.addSeparator();
		deleteWidgetsMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.delete(),LocaleText.get("deleteSelected")),true,new Command(){
			public void execute() {popup.hide(); deleteSelectedItem();}});

		groupWidgetsSeparator = menuBar.addSeparator();
		groupWidgetsMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.addchild(),LocaleText.get("groupWidgets")),true,new Command(){
			public void execute() {popup.hide(); groupWidgets();}});
		//}

		menuBar.addSeparator();	
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("newTab")),true, new Command(){
			public void execute() {
				popup.hide(); 
				selectedTabIndex++;
				DesignWidgetWrapper widget = addNewTab(LocaleText.get("page") + (selectedTabIndex + 1), selectedTabIndex, true);
				Context.getCommandHistory().add(new InsertTabCmd(widget, selectedTabIndex, widget.getText(), widget.getLayoutNode(), (DesignSurfaceView)widget.getWidgetSelectionListener()));
			}});

		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.delete(),LocaleText.get("deleteTab")),true, new Command(){
			public void execute() {
				popup.hide(); 
				String name = pageWidgets.get(selectedTabIndex).getText();
				DesignWidgetWrapper widget = deleteTab();
				if(widget != null)
					Context.getCommandHistory().add(new InsertTabCmd(widget, selectedTabIndex, name, widget.getLayoutNode(), (DesignSurfaceView)widget.getWidgetSelectionListener()));
			}});

		//if(selectedDragController.isAnyWidgetSelected()){
		cutCopySeparator = menuBar.addSeparator();
		cutMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.cut(),LocaleText.get("cut")),true,new Command(){
			public void execute() {popup.hide(); cutWidgets();}});

		copyMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.copy(),LocaleText.get("copy")),true,new Command(){
			public void execute() {popup.hide(); copyWidgets(false);}});
		//}
		//else if(clipBoardWidgets.size() > 0){
		pasteSeparator = menuBar.addSeparator();
		pasteMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.paste(),LocaleText.get("paste")),true,new Command(){
			public void execute() {popup.hide(); pasteWidgets(true);}});
		//}

		menuBar.addSeparator();	
		lockWidgetsMenu = menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("lockWidgets")),true, new Command(){
			public void execute() {popup.hide(); lockWidgets();}});
		
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("unLockWidgets")),true, new Command(){
			public void execute() {popup.hide(); unLockWidgets();}});

		menuBar.addSeparator();	
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.add(),LocaleText.get("selectAll")),true, new Command(){
			public void execute() {popup.hide(); selectAll();}});

		menuBar.addSeparator();
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.refresh(),LocaleText.get("refresh")),true,new Command(){
			public void execute() {popup.hide(); refresh();}});

		menuBar.addSeparator();
		menuBar.addItem(FormDesignerUtil.createHeaderHTML(images.open(),LocaleText.get("load")),true,new Command(){
			public void execute() {popup.hide(); load();}});

		popup.setWidget(menuBar);
	}

	/**
	 * @see com.google.gwt.event.logical.shared.SelectionHandler#onSelection(SelectionEvent)
	 */
	public void onSelection(SelectionEvent<Integer> event){
		selectedTabIndex = event.getSelectedItem();

		//selectedDragController = dragControllers.elementAt(selectedTabIndex);
		//selectedPanel = selectedDragController.getBoundaryPanel();
		selectedPanel = (AbsolutePanel)tabDropControllers.get(selectedTabIndex).getDropTarget();

		widgetSelectionListener.onWidgetSelected(getSelPageDesignWidget(),false);
	}

	/**
	 * Sets the listener to widget selection events.
	 * 
	 * @param widgetSelectionListener the listener.
	 */
	public void setWidgetSelectionListener(WidgetSelectionListener  widgetSelectionListener){
		this.widgetSelectionListener = widgetSelectionListener;
	}

	/**
	 * Gets the widgets layout xml.
	 * 
	 * @return the xml.
	 */
	public String getLayoutXml(){

		if(tabs.getWidgetCount() == 0)
			return null;

		com.google.gwt.xml.client.Document doc = XMLParser.createDocument();
		doc.appendChild(doc.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"UTF-8\""));
		Element rootNode = doc.createElement("Form");
		if(formDef != null)
			rootNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, formDef.getId()+"");
		doc.appendChild(rootNode);

		this.doc = doc;

		boolean hasWidgets = false;
		AbsolutePanel prevSelPanel = selectedPanel;
		for(int i=0; i<tabs.getWidgetCount(); i++){
			Element node = doc.createElement("Page");
			node.setAttribute(WidgetEx.WIDGET_PROPERTY_TEXT, DesignWidgetWrapper.getTabDisplayText(tabs.getTabBar().getTabHTML(i)));
			//node.setAttribute("BackgroundColor", tabs.getTabBar().getTabHTML(i));

			pageWidgets.get(i).buildLabelProperties(node);

			if(pageWidgets.get(i) != null)
				node.setAttribute(WidgetEx.WIDGET_PROPERTY_BINDING, pageWidgets.get(i).getBinding());

			rootNode.appendChild(node);
			AbsolutePanel panel = (AbsolutePanel)tabs.getWidget(i);

			selectedPanel = panel;
			node.setAttribute(WidgetEx.WIDGET_PROPERTY_WIDTH, getWidth());
			node.setAttribute(WidgetEx.WIDGET_PROPERTY_HEIGHT, getHeight());
			node.setAttribute(WidgetEx.WIDGET_PROPERTY_BACKGROUND_COLOR, getBackgroundColor());

			boolean b = buildLayoutXml(node,panel,doc);
			if(b)
				hasWidgets = true;
		}

		selectedPanel = prevSelPanel;

		if(hasWidgets)
			return FormDesignerUtil.formatXml(doc.toString());
		else if(formDef != null)
			//TODO This accidentally overwrites form layout after refresh when user has not refreshed the design surface.
			//When user clears widgets, the setting of formdef layout to null will be done immediately
			//after the delete.
			;//formDef.setLayoutXml(null);

		return null;
	}

	/**
	 * Gets the root node of the language or locale translation document for the widgets 
	 * layout on the design surface.
	 * 
	 * @return the root node.
	 */
	public Element getLanguageNode(){
		if(tabs.getWidgetCount() == 0)
			return null;

		com.google.gwt.xml.client.Document doc = XMLParser.createDocument();
		doc.appendChild(doc.createProcessingInstruction("xml", "version=\"1.0\" encoding=\"UTF-8\""));
		Element rootNode = doc.createElement("Form");
		if(formDef != null)
			rootNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, formDef.getId()+"");
		doc.appendChild(rootNode);

		//String xpath = "Form/Page/Item[@Binding='";
		//String xpath = "Form/Page/Item[@";
		for(int i=0; i<tabs.getWidgetCount(); i++){
			if(pageWidgets.get(i) == null)
				continue; //TODO Need to deal with this case where all widgets are deleted but layout and locale text remains

			String xpath = "Form/Page[@Binding='"+pageWidgets.get(i).getBinding()+"']/Item[@";

			String text = DesignWidgetWrapper.getTabDisplayText(tabs.getTabBar().getTabHTML(i));
			Element node = doc.createElement(XformConstants.NODE_NAME_TEXT);
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_XPATH, "Form/Page[@Binding='"+pageWidgets.get(i).getBinding()+"'][@Text]");
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_VALUE, text);
			rootNode.appendChild(node);

			buildLanguageNode((AbsolutePanel)tabs.getWidget(i),doc, rootNode,xpath);
		}

		return rootNode;
	}

	private void buildLanguageNode(AbsolutePanel panel,com.google.gwt.xml.client.Document doc, Element parentNode, String xpath){
		for(int i=0; i<panel.getWidgetCount(); i++){
			Widget widget = panel.getWidget(i);
			if(!(widget instanceof DesignWidgetWrapper))
				continue;
			((DesignWidgetWrapper)widget).buildLanguageXml(doc,parentNode, xpath);
		}
	}

	private boolean buildLayoutXml(Element parent, AbsolutePanel panel, com.google.gwt.xml.client.Document doc){
		for(int i=0; i<panel.getWidgetCount(); i++){
			Widget widget = panel.getWidget(i);
			if(!(widget instanceof DesignWidgetWrapper))
				continue;
			((DesignWidgetWrapper)widget).buildLayoutXml(parent, doc);
		}

		return panel.getWidgetCount() > 0;
	}

	/**
	 * Sets the layout xml for the design surface.
	 * 
	 * @param xml the layout xml.
	 * @param formDef the form whose layout xml is given.
	 * @return true if the layout xml has been loaded successfully, else false.
	 */
	public boolean setLayoutXml(String xml, FormDef formDef){
		this.formDef = formDef;
		
		PaletteView.unRegisterAllDropControllers();
		FormsTreeView.unRegisterAllDropControllers();
		FormDesignerDragController.unregisterDropControllers();
		tabs.clear();
		pageWidgets.clear();
		tabDropControllers.clear();

		if(xml == null || xml.trim().length() == 0){
			addNewTab(null, true);
			return false;
		}

		com.google.gwt.xml.client.Document doc = XMLParser.parse(xml);
		Element root = doc.getDocumentElement();
		NodeList pages = root.getChildNodes();
		for(int i=0; i<pages.getLength(); i++){
			if(pages.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element node = (Element)pages.item(i);
			DesignWidgetWrapper widget = addNewTab(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT), true);
			WidgetEx.loadLabelProperties(node, widget);

			((DesignGroupView)this).setWidth(node.getAttribute(WidgetEx.WIDGET_PROPERTY_WIDTH));
			((DesignGroupView)this).setHeight(node.getAttribute(WidgetEx.WIDGET_PROPERTY_HEIGHT));
			((DesignGroupView)this).setBackgroundColor(node.getAttribute(WidgetEx.WIDGET_PROPERTY_BACKGROUND_COLOR));

			loadPage(node.getChildNodes());
		}

		this.doc = doc;
		
		if(tabs.getWidgetCount() > 0){
			selectedTabIndex = 0;
			tabs.selectTab(selectedTabIndex);
		}		
		
		return true;
	}

	/**
	 * Loads a list of layout xml nodes for a given page on the design surface.
	 * 
	 * @param nodes the node list.
	 */
	private void loadPage(NodeList nodes){
		for(int i=0; i<nodes.getLength(); i++){
			if(nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;

			try{
				Element node = (Element)nodes.item(i);
				DesignWidgetWrapper widget = loadWidget(node,selectedDragController,selectedPanel,images,widgetPopup,this,currentWidgetSelectionListener,formDef); //TODO CHECK ???????????????
				if(widget != null && (widget.getWrappedWidget() instanceof DesignGroupWidget)){
					((DesignGroupWidget)widget.getWrappedWidget()).loadWidgets(node,formDef);
					((DesignGroupWidget)widget.getWrappedWidget()).setWidgetSelectionListener(currentWidgetSelectionListener); //TODO CHECK
					if(!widget.isRepeated())
						selectedDragController.makeDraggable(widget, ((DesignGroupWidget)widget.getWrappedWidget()).getHeaderLabel());
				}
			}
			catch(Exception ex){
				//FormUtil.displayException(ex);
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Loads a widget on the design surface.
	 * 
	 * @param node
	 * @param dragController
	 * @param panel
	 * @param images
	 * @param widgetPopup
	 * @param widgetPopupMenuListener
	 * @param widgetSelectionListener
	 * @param formDef
	 * @return
	 */
	public static DesignWidgetWrapper loadWidget(Element node,FormDesignerDragController dragController, AbsolutePanel panel, Images images, PopupPanel widgetPopup, IWidgetPopupMenuListener widgetPopupMenuListener,WidgetSelectionListener widgetSelectionListener,FormDef formDef){
		String left = node.getAttribute(WidgetEx.WIDGET_PROPERTY_LEFT);
		String top = node.getAttribute(WidgetEx.WIDGET_PROPERTY_TOP);
		String s = node.getAttribute(WidgetEx.WIDGET_PROPERTY_WIDGETTYPE);

		Widget widget = null;
		if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_RADIOBUTTON))
			widget = new RadioButtonWidget(node.getAttribute(WidgetEx.WIDGET_PROPERTY_PARENTBINDING),node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_CHECKBOX))
			widget = new CheckBox(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_BUTTON))
			widget = new Button(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LISTBOX))
			widget = new ListBox(false);
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TEXTAREA))
			widget = new TextArea();
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_IMAGE) || s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LOGO))
			widget = FormUtil.createImage(images.picture());
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_VIDEO_AUDIO))
			widget = new Hyperlink(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT),"");
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_DATEPICKER))
			widget = new DatePickerWidget();
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TIME))
			widget = new TimeWidget();
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_DATETIME))
			widget = new DateTimeWidget();
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TEXTBOX))
			widget = new TextBox();
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_HORIZONTAL_LINE))
			widget = new HorizontalGridLine(0);
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_VERTICAL_LINE))
			widget = new VerticalGridLine(0);
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LABEL))
			widget = new Label(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_GROUPBOX) || s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_REPEATSECTION))
			widget = new DesignGroupWidget(images,widgetPopupMenuListener);
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TABLE))
			widget = new GridDesignGroupWidget(images,widgetPopupMenuListener);
		else
			return null; 

		DesignWidgetWrapper wrapper = new DesignWidgetWrapper(widget,widgetPopup,widgetSelectionListener);
		wrapper.setLayoutNode(node);

		String value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_HELPTEXT);
		if(value != null && value.trim().length() > 0)
			wrapper.setTitle(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_EXTERNALSOURCE);
		if(value != null && value.trim().length() > 0)
			wrapper.setExternalSource(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_DISPLAYFIELD);
		if(value != null && value.trim().length() > 0)
			wrapper.setDisplayField(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_VALUEFIELD);
		if(value != null && value.trim().length() > 0)
			wrapper.setValueField(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_FILTERFIELD);
		if(value != null && value.trim().length() > 0)
			wrapper.setFilterField(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_WIDTH);
		if(value != null && value.trim().length() > 0)
			wrapper.setWidth(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_HEIGHT);
		if(value != null && value.trim().length() > 0)
			wrapper.setHeight(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_REPEATED);
		if(value != null && value.trim().length() > 0)
			wrapper.setRepeated(value.equals(WidgetEx.REPEATED_TRUE_VALUE));

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_ID);
		if(value != null && value.trim().length() > 0)
			wrapper.setId(value);

		String binding = node.getAttribute(WidgetEx.WIDGET_PROPERTY_BINDING);
		if(binding != null && binding.trim().length() > 0)
			wrapper.setBinding(binding);
		else
			binding = null;

		String parentBinding = node.getAttribute(WidgetEx.WIDGET_PROPERTY_PARENTBINDING);
		if(parentBinding != null && parentBinding.trim().length() > 0)
			wrapper.setParentBinding(parentBinding);
		else
			parentBinding = null;

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_TABINDEX);
		if(value != null && value.trim().length() > 0)
			wrapper.setTabIndex(Integer.parseInt(value));

		//if(wrapper.getWrappedWidget() instanceof Label)
		WidgetEx.loadLabelProperties(node,wrapper);

		if(formDef != null && binding != null && parentBinding == null){
			QuestionDef questionDef = formDef.getQuestion(binding);
			if(questionDef != null)
				wrapper.setQuestionDef(questionDef);
		}

		/*if(formDef != null && (binding != null || parentBinding != null)){
			QuestionDef questionDef = formDef.getQuestion(parentBinding != null ? parentBinding : binding);
			if(questionDef != null){
				wrapper.setQuestionDef(questionDef);
			}
		}*/

		if(!"true".equals(node.getAttribute(WidgetEx.WIDGET_PROPERTY_HEADER_LABEL))){
			dragController.makeDraggable(wrapper);

			//Without this, widgets in this box cant use Ctrl + A in edit mode and also
			//edited text is not automatically selected.
			
			//The line below has been purposely commented out because it makes label widgets get selected
			//when one drags a rubber band - a behavior that is very annoying.
			//wrapper.removeStyleName("dragdrop-handle");
		}

		panel.add(wrapper);
		FormDesignerUtil.setWidgetPosition(wrapper, left, top);

		return wrapper;
	}

	/**
	 * Deletes the selected widgets.
	 */
	public boolean deleteWidgets(){
		if(super.deleteWidgets() && doc != null){
			String layout = null;
			if(!(tabs.getTabBar().getTabCount() == 1 && (selectedPanel == null || (selectedPanel != null && selectedPanel.getWidgetCount() == 0))))
				layout = FormUtil.formatXml(doc.toString());
			layoutChangeListener.onLayoutChanged(layout);

			return true;
		}

		if(tabs.getTabBar().getTabCount() == 1 && selectedPanel != null && selectedPanel.getWidgetCount() == 0)
			layoutChangeListener.onLayoutChanged(null);

		return false;
	}

	/**
	 * Deletes the selected page tab.
	 */
	private DesignWidgetWrapper deleteTab(){
		if(tabs.getWidgetCount() == 1){
			if(formDef != null)
				formDef.setLayoutXml(null); //TODO Check if this does not bring bugs
			Window.alert(LocaleText.get("cantDeleteAllTabs"));
			return null;
		}

		if(selectedPanel.getWidgetCount() > 0){
			Window.alert(LocaleText.get("deleteAllTabWidgetsFirst"));
			return null;
		}

		if(!Window.confirm(LocaleText.get("deleteTabPrompt")))
			return null;

		return deleteTab(selectedTabIndex);
	}

	public DesignWidgetWrapper deleteTab(int index){
		selectedTabIndex = index;

		FormDesignerDropController dropController = tabDropControllers.remove(selectedTabIndex);
		
		//TODO Need to check for this has multiple drop controllers
		PaletteView.unRegisterDropController(dropController);
		FormsTreeView.unRegisterDropController(dropController);
		FormDesignerDragController.unRegisterDropController(dropController);
		
		tabDropControllers.remove(dropController);

		tabs.remove(selectedTabIndex);
		DesignWidgetWrapper widget = pageWidgets.remove(selectedTabIndex);
		if(selectedTabIndex > 0)
			selectedTabIndex -= 1;

		tabs.selectTab(selectedTabIndex);

		return widget;
	}

	/**
	 * Loads the default widget layout for a given form.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setLayout(FormDef formDef){			
		this.formDef = formDef;

		PaletteView.unRegisterAllDropControllers();
		FormsTreeView.unRegisterAllDropControllers();
		FormDesignerDragController.unregisterDropControllers();
		tabs.clear();
		pageWidgets.clear();
		tabDropControllers.clear();

		Vector pages = formDef.getPages();
		if(pages != null){
			for(int i=0; i<pages.size(); i++){
				PageDef pageDef = (PageDef)pages.get(i);
				addNewTab(pageDef.getName(), true);
				loadPage(pageDef);

				selectAll();
				format();
				clearSelection();
			}

			//TODO May need to support multiple listeners.
			layoutChangeListener.onLayoutChanged(getLayoutXml());
		}

		if(tabs.getWidgetCount() > 0){
			selectedTabIndex = 0;
			tabs.selectTab(selectedTabIndex);
		}
	}

	/**
	 * Loads the default widget layout for a given page.
	 * 
	 * @param pageDef the page definition object.
	 */
	private void loadPage(PageDef pageDef){
		loadQuestions(pageDef.getQuestions());
	}

	/**
	 * Does automatic loading of question widgets onto the design surface for a given page.
	 * 
	 * @param questions the list of questions.
	 */
	private void loadQuestions(List<QuestionDef> questions){
		loadQuestions(questions, 20, 20, 0,tabs.getTabBar().getTabCount() == 1,false, null, false);
	}

	/**
	 * Sets the current form beign designed.
	 * 
	 * @param formDef the form definition object.
	 */
	public void setFormDef(FormDef formDef){	
		if(this.formDef != formDef){
			PaletteView.unRegisterAllDropControllers();
			FormsTreeView.unRegisterAllDropControllers();
			FormDesignerDragController.unregisterDropControllers();
			tabs.clear();
			pageWidgets.clear();
			tabDropControllers.clear();
			addNewTab(null, true);
		}

		this.formDef = formDef;
	}

	/**
	 * Loads widgets for questions that are not loaded on the design surface.
	 */
	public void refresh(){
		if(formDef == null)
			return;

		FormUtil.dlg.setText(LocaleText.get("refreshingDesignSurface"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					boolean loading = false;
					if(!(tabs.getTabBar().getTabCount() == 1 && (selectedPanel == null || (selectedPanel != null && selectedPanel.getWidgetCount() == 0))))
						loadNewWidgets();
					else{
						if(formDef.getLayoutXml() != null && formDef.getLayoutXml().trim().length() > 0 && selectedPanel != null && selectedPanel.getWidgetCount() == 0){
							loading = true;
							load();
						}
						else	
							setLayout(formDef);
					}

					if(!loading)
						FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}
			}
		});
		
		
	}

	/**
	 * Checks if the design surface has any widgets.
	 * 
	 * @return true if it has, else false.
	 */
	public boolean hasWidgets(){
		//return (tabs.getTabBar().getTabCount() > 0 && selectedPanel != null && selectedPanel.getWidgetCount() > 0);
		for(int index = 0; index < tabs.getTabBar().getTabCount(); index++){
			Widget widget = tabs.getWidget(index);
			if(!(widget instanceof AbsolutePanel))
				continue;
			
			if(((AbsolutePanel)widget).getWidgetCount() > 0)
				return true;
		}
		
		return false;
	}


	/**
	 * Loads design surface widgets from layout xml of the current form.
	 */
	public void load(){
		if(formDef == null)
			return;

		//AbsolutePanel panel (AbsolutePanel)tabs.getWidget(i);
		if((selectedPanel != null && selectedPanel.getWidgetCount() > 0) || tabs.getTabBar().getTabCount() > 1){
			Window.alert(LocaleText.get("deleteAllWidgetsFirst"));
			return;
		}

		FormUtil.dlg.setText(LocaleText.get("loadingDesignSurface"));
		FormUtil.dlg.center();

		DeferredCommand.addCommand(new Command(){
			public void execute() {
				try{
					if(!setLayoutXml(formDef.getLayoutXml(), formDef))
						refresh();
					else
						FormUtil.dlg.hide();
				}
				catch(Exception ex){
					FormUtil.displayException(ex);
				}
			}
		});
	}

	/**
	 * Loads widgets that have not yet been loaded on the design surface. If if they were once
	 * loaded, have been deleted.
	 */
	private void loadNewWidgets(){

		CommandList commands = new CommandList(this);

		clearSelection();

		//Create list of bindings for widgets that are already loaded on the design surface.
		HashMap<String,String> bindings = new HashMap<String, String>();
		List<DropController> dropControllers = FormDesignerDragController.getInstance().getDropControllers();
		for(int i=0; i<dropControllers.size(); i++){
			Widget dropTarget = dropControllers.get(i).getDropTarget();
			if(!(dropTarget instanceof AbsolutePanel))
				continue;
			fillBindings((AbsolutePanel)dropTarget, bindings);
		}

		//Load the new questions onto the design surface for all pages.
		for(int index = 0; index < formDef.getPageCount(); index++){
			List<QuestionDef> newQuestions = new ArrayList<QuestionDef>();

			//Create a list of questions that have not yet been loaded on the design surface
			//for the current page.
			fillNewQuestions(formDef.getPageAt(index),newQuestions,bindings);

			//Check if this is a new page whose tab has not yet been added, and then add it
			if(tabs.getTabBar().getTabCount() < index+1)
				addNewTab(LocaleText.get("page") + (index+1), true);
			else
				tabs.getTabBar().selectTab(index);

			//Load the new questions onto the design surface for the current page.
			if(newQuestions.size() > 0){
				loadQuestions(newQuestions,getLowestWidgetYPos() + 20, 20, selectedPanel.getWidgetCount(),false, true, commands, false);
				format();
				clearSelection();
			}
		}

		if(commands.size() > 0)
			Context.getCommandHistory().add(commands);
	}

	/**
	 * Fills bindings for loaded widgets in a given panel.
	 * 
	 * @param panel the panel.
	 * @param bindings the map of bindings. Made a map instead of list for only easy of search with key.
	 */
	private void fillBindings(AbsolutePanel panel,HashMap<String,String> bindings){
		if(panel.getWidgetIndex(rubberBand) > -1)
			panel.remove(rubberBand);

		for(int index = 0; index < panel.getWidgetCount(); index++){
			DesignWidgetWrapper widget = (DesignWidgetWrapper)panel.getWidget(index);

			//When a widget is deleted, it is reloaded on refresh even if its label still exists.
			if(widget.getWrappedWidget() instanceof Label)
				continue;

			String binding = widget.getParentBinding();
			if(binding == null)
				binding = widget.getBinding();
			bindings.put(binding, binding); //Could possibly put widget as value.

			if(widget.getWrappedWidget() instanceof DesignGroupWidget)
				fillBindings(((DesignGroupWidget)widget.getWrappedWidget()).getPanel(),bindings);
		}
	}

	/**
	 * Fills questions, in a page, that are not loaded on the design surface.
	 * 
	 * @param pageDef the page.
	 * @param newQuestions a list of the new questions.
	 * @param bindings a map of bindings for questions that are aleady loaded on the design surface.
	 */
	private void fillNewQuestions(PageDef pageDef, List<QuestionDef> newQuestions, HashMap<String,String> bindings){
		for(int index = 0; index < pageDef.getQuestionCount(); index ++){
			QuestionDef questionDef = pageDef.getQuestionAt(index);
			if(!bindings.containsKey(questionDef.getBinding()))
				newQuestions.add(questionDef);
		}
	}

	/**
	 * Sets the listener to widget layout change events.
	 * 
	 * @param layoutChangeListener the listener.
	 */
	public void setLayoutChangeListener(LayoutChangeListener layoutChangeListener){
		this.layoutChangeListener = layoutChangeListener;
	}

	/**
	 * @see org.purc.purcforms.client.controller.WidgetSelectionListener#onWidgetSelected(Widget, boolean)
	 */
	public void onWidgetSelected(DesignWidgetWrapper widget, boolean multipleSel){

		boolean ctrlKey = FormDesignerUtil.getCtrlKey();
		if(!ctrlKey)
			stopLabelEdit(false);

		if(widget == null){
			selectedDragController.clearSelection(); //New and may cause bugs
			//widgetSelectionListener.onWidgetSelected(widget); //New and may cause bugs	
			return;
		}
		//if(selectedPanel.getWidgetIndex(widget) > -1)
		//	selectedDragController.toggleSelection(widget);

		if(!(widget.getWrappedWidget() instanceof TabBar)){
			//Event event = DOM.eventGetCurrentEvent(); //TODO verify that this does not introduce a bug
			//if(event != null && DOM.eventGetType(event) == Event.ONCONTEXTMENU){
			if(selectedPanel.getWidgetIndex(widget) > -1){
				if(!ctrlKey){
					if(!selectedDragController.isWidgetSelected(widget)/*selectedDragController.getSelectedWidgetCount() == 1*/)
						selectedDragController.clearSelection();
					selectedDragController.selectWidget(widget);
				}

				recursivelyClearGroupBoxSelection();
			}
			else{

				if(!(widget.getWrappedWidget() instanceof Label && "100%".equals(widget.getWidth())))
					selectedDragController.clearSelection();

				for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
					Widget wid = selectedPanel.getWidget(index);
					if(!(wid instanceof DesignWidgetWrapper))
						continue;
					if(!(((DesignWidgetWrapper)wid).getWrappedWidget() instanceof DesignGroupWidget))
						continue;

					DesignGroupWidget designGroupWidget = (DesignGroupWidget)((DesignWidgetWrapper)wid).getWrappedWidget();
					if(!designGroupWidget.containsWidget(widget))
						designGroupWidget.clearGroupBoxSelection();
				}
			}
		}

		widgetSelectionListener.onWidgetSelected(widget, multipleSel);
	}

	@Override
	protected void selectAll(){
		if(editWidget != null){
			txtEdit.selectAll();
			return; //let label editor do select all
		}

		List<Widget> widgets = selectedDragController.getSelectedWidgets();
		if(widgets != null){
			for(int index = 0; index < widgets.size(); index++){
				DesignWidgetWrapper widget = (DesignWidgetWrapper)widgets.get(index);
				if(widget.getWrappedWidget() instanceof DesignGroupWidget){
					selectedDragController.clearSelection();
					((DesignGroupWidget)widget.getWrappedWidget()).selectAll();
					//return;
				}
			}
		}

		super.selectAll();
	}

	@Override
	public void setHeight(String height) {
		if(height != null && height.trim().length() > 0 && !height.equals("100%"))
			sHeight = height;

		super.setHeight(sHeight);
	}

	@Override
	public void setWidth(String width) {
		if(width != null && width.trim().length() > 0 && !width.equals("100%"))
			sWidth = width;

		super.setWidth(sWidth);
	}

	/**
	 * Gets the html for the selected page.
	 * 
	 * @return the html text.
	 */
	public String getSelectedPageHtml(){
		if(selectedPanel == null)
			return "";

		return selectedPanel.getElement().getInnerHTML();
	}

	public void selectPanel(AbsolutePanel panel){
		int index = tabs.getWidgetIndex(panel);
		if(index == tabs.getTabBar().getSelectedTab())
			return;
		
		tabs.selectTab(index);

		//selectedDragController = dragControllers.elementAt(index);
		//selectedPanel = selectedDragController.getBoundaryPanel();
	}

	public void clearSelection(){
		FormDesignerDragController.getInstance().clearSelection();
		/*for(int i=0; i<dragControllers.size(); i++)
			dragControllers.elementAt(i).clearSelection();*/
	}
	
	protected void ensureVisible(Widget widget) {
		ScrollPanel scrollPanel = (ScrollPanel)getParent();
		//((ScrollPanel)getParent()).ensureVisible(widget);
		com.google.gwt.dom.client.Element scrollElement = scrollPanel.getElement();
		com.google.gwt.dom.client.Element widgetElement = widget.getElement();
	    int realOffsetTop = 0;
	    int realOffsetLeft = 0;
	    while (widgetElement != null && (widgetElement != scrollElement)) {
	      realOffsetTop += widgetElement.getOffsetTop();
	      realOffsetLeft += widgetElement.getOffsetLeft();
	      widgetElement = widgetElement.getOffsetParent();
	    }

	    scrollPanel.setScrollPosition(realOffsetTop - scrollElement.getOffsetHeight() / 2);
	    scrollPanel.setHorizontalScrollPosition(realOffsetLeft - scrollElement.getOffsetWidth() / 2);
	}
	
	public ScrollPanel getScrollPanel(){
		return (ScrollPanel)getParent();
	}
	
	public void selectTab(FormDesignerDropController dropController){
		tabs.selectTab(tabDropControllers.indexOf(dropController));
	}
	
	public static void setSelectedView(DesignGroupView selectedView){
		DesignSurfaceView.selectedView = selectedView;
	}
}
