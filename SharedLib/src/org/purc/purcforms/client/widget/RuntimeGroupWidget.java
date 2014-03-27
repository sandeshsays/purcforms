package org.purc.purcforms.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.purc.purcforms.client.controller.OpenFileDialogEventListener;
import org.purc.purcforms.client.controller.QuestionChangeListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Condition;
import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.GroupQtnsDef;
import org.purc.purcforms.client.model.ModelConstants;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.model.RepeatQtnsDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.model.ValidationRule;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.FormRunnerView;
import org.purc.purcforms.client.view.OpenFileDialog;
import org.purc.purcforms.client.view.FormRunnerView.Images;
import org.purc.purcforms.client.widget.grid.HorizontalGridLine;
import org.purc.purcforms.client.widget.grid.VerticalGridLine;
import org.purc.purcforms.client.xforms.XformConstants;
import org.purc.purcforms.client.xforms.XmlUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

/**
 * 
 * @author daniel
 *
 */
public class RuntimeGroupWidget extends Composite implements OpenFileDialogEventListener,QuestionChangeListener{

	private final Images images;
	private GroupQtnsDef groupQtnsDef;
	private HashMap<String,RuntimeWidgetWrapper> widgetMap = new HashMap<String,RuntimeWidgetWrapper>();
	private EditListener editListener;
	private WidgetListener widgetListener;
	private EnabledChangeListener enabledListener;
	private FlexTable table;
	private List<RuntimeWidgetWrapper> buttons = new ArrayList<RuntimeWidgetWrapper>();
	private List<RuntimeWidgetWrapper> widgets = new ArrayList<RuntimeWidgetWrapper>();
	private VerticalPanel verticalPanel = new VerticalPanel();
	private List<Element> dataNodes = new ArrayList<Element>();
	private AbsolutePanel selectedPanel = new AbsolutePanel();
	private boolean isRepeated = false;
	private Image image;
	private HTML html;
	private FormDef formDef;
	private Button btnAdd;
	private RuntimeWidgetWrapper firstInvalidWidget;

	protected HashMap<QuestionDef,List<Label>> labelMap = new HashMap<QuestionDef,List<Label>>();;
	protected HashMap<Label,String> labelText = new HashMap<Label,String>();
	protected HashMap<Label,String> labelReplaceText = new HashMap<Label,String>();

	protected HashMap<QuestionDef,List<CheckBox>> checkBoxGroupMap = new HashMap<QuestionDef,List<CheckBox>>();

	protected HashMap<QuestionDef,List<RuntimeWidgetWrapper>> calcWidgetMap = new HashMap<QuestionDef,List<RuntimeWidgetWrapper>>();

	protected HashMap<String, RuntimeWidgetWrapper> widgetBindingMap = new HashMap<String, RuntimeWidgetWrapper>();
	protected List<HashMap<String, Object>> records = new ArrayList<HashMap<String, Object>>();
	protected int currentRecordIndex = 0;
	
	private Button btnFirstRecord;
	private Button btnPrevRecord;
	private Button btnNextRecord;
	private Button btnLastRecord;
	private Label lblRecordNavigation;
	
	private Element repeatDataNodeClone;
	
	/**
	 * A map of filtered single select dynamic questions and their corresponding 
	 * non label widgets. Only questions of single select dynamic which have the
	 * widget filter property set are put in this list
	 */
	protected HashMap<QuestionDef,RuntimeWidgetWrapper> filtDynOptWidgetMap = new HashMap<QuestionDef,RuntimeWidgetWrapper>();

	protected HashMap<PushButton, List<FormDef>> repeatRowFormMap = new HashMap<PushButton, List<FormDef>>();

	private boolean readOnly = false;

	public RuntimeGroupWidget(Images images,FormDef formDef,GroupQtnsDef groupQtnsDef,EditListener editListener, WidgetListener widgetListener, boolean isRepeated, EnabledChangeListener enabledListener){
		this.images = images;
		this.formDef = formDef;
		this.groupQtnsDef = groupQtnsDef;
		this.editListener = editListener;
		this.widgetListener = widgetListener;
		this.isRepeated = isRepeated;
		this.enabledListener = enabledListener;

		if(isRepeated){
			table = new FlexTable();
			FormUtil.maximizeWidget(table);		
			verticalPanel.add(table);
			initWidget(verticalPanel);
		}
		else{
			//FormUtil.maximizeWidget(selectedPanel);	
			initWidget(selectedPanel);
		}
		
		if (groupQtnsDef.getQtnDef().getDataType() == QuestionDef.QTN_TYPE_SUBFORM) {
			records.add(new HashMap<String, Object>());
			repeatDataNodeClone = (Element)groupQtnsDef.getQtnDef().getDataNode().cloneNode(true);
		}
		//setupEventListeners();

		//table.setStyleName("cw-FlexTable");
		this.addStyleName("purcforms-repeat-border");
	}

	//TODO The code below needs great refactoring together with PreviewView
	private RuntimeWidgetWrapper getParentWrapper(Widget widget, Element node, String parentBinding){
		RuntimeWidgetWrapper parentWrapper = widgetMap.get(parentBinding);
		if(parentWrapper == null){
			QuestionDef qtn = null;
			if(groupQtnsDef != null)
				qtn = groupQtnsDef.getQuestion(parentBinding);
			else
				qtn = formDef.getQuestion(parentBinding);

			if(qtn != null){
				parentWrapper = new RuntimeWidgetWrapper(widget, images.error(),editListener, widgetListener, enabledListener);
				parentWrapper.setQuestionDef(qtn,true);
				widgetMap.put(parentBinding, parentWrapper);
				//addWidget(parentWrapper); //Misplaces first widget (with tabindex > 0) of a group (CheckBox and RadioButtons)

				qtn.addChangeListener(this);
				List<CheckBox> list = new ArrayList<CheckBox>();
				list.add((CheckBox)widget);
				checkBoxGroupMap.put(qtn, list);
			}
		}	 
		else
			checkBoxGroupMap.get(parentWrapper.getQuestionDef()).add((CheckBox)widget);

		return parentWrapper;
	}

	public void loadWidgets(FormDef formDef,NodeList nodes, List<RuntimeWidgetWrapper> externalSourceWidgets,
			HashMap<QuestionDef,List<QuestionDef>> calcQtnMappings, HashMap<QuestionDef,List<RuntimeWidgetWrapper>> calcWidgetMap,
			HashMap<QuestionDef,RuntimeWidgetWrapper> filtDynOptWidgetMap){

		HashMap<Integer,RuntimeWidgetWrapper> widgetMap = new HashMap<Integer,RuntimeWidgetWrapper>();
		HashMap<Integer,RuntimeWidgetWrapper> labelWidgetMap = new HashMap<Integer,RuntimeWidgetWrapper>();
		
		int maxTabIndex = 0;

		for(int i=0; i<nodes.getLength(); i++){
			if(nodes.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			try{
				Element node = (Element)nodes.item(i);
				int index = loadWidget(formDef,node,widgetMap,externalSourceWidgets,calcQtnMappings, calcWidgetMap, filtDynOptWidgetMap, labelWidgetMap);
				if(index > maxTabIndex)
					maxTabIndex = index;
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}

		//Adding label headers
		if(this.isRepeated){
			int col = 0;
			Set<Integer> keys = labelWidgetMap.keySet();	
			Object[] keyArray = keys.toArray();
			Arrays.sort(keyArray);
			for(Object key : keyArray){
				table.setWidget(0, col++, labelWidgetMap.get(key));
			}
		}
		
		//We are adding widgets to the panel according to the tab index.
		for(int index = 0; index <= maxTabIndex; index++){
			RuntimeWidgetWrapper widget = widgetMap.get(new Integer(index));
			if(widget != null)
				addWidget(widget);
		}

		if(isRepeated){

			DeferredCommand.addCommand(new Command() {
				public void execute() {
					if(widgets.size() == 0)
						return;
					
					RuntimeWidgetWrapper widget = widgets.get(0);
					if(!(widget.getQuestionDef() == null || widget.getQuestionDef().getDataNode() == null)){
						/*Element dataNode = (Element)widget.getQuestionDef().getDataNode().getParentNode();
						Element parent = (Element)dataNode.getParentNode();
						NodeList nodeList = parent.getElementsByTagName(dataNode.getNodeName());*/

						Element repeatDataNode = getParentNode(widget.getQuestionDef().getDataNode(),(widget.getWrappedWidget() instanceof CheckBox) ? widget.getParentBinding() : widget.getBinding(), ((QuestionDef)widget.getQuestionDef().getParent()).getBinding());
						Element parent = (Element)repeatDataNode.getParentNode();
						NodeList nodeList = parent.getElementsByTagName(repeatDataNode.getNodeName());

						RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)getParent().getParent();
						int y = getHeightInt();

						for(int index = 1; index < nodeList.getLength(); index++)
							addNewRow((Element)nodeList.item(index));

						editListener.onRowAdded(wrapper,getHeightInt()-y);

					}
				}
			});	
		}

		//Now add the button and label widgets, if any.
		if(isRepeated){
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSpacing(5);
			for(int index = 0; index < buttons.size(); index++)
				panel.add(buttons.get(index));
			verticalPanel.add(panel);

			addDeleteButton(table.getRowCount() - 1);

			FormUtil.maximizeWidget(panel);
		}
		else{
			for(int index = 0; index < buttons.size(); index++){
				RuntimeWidgetWrapper widget = buttons.get(index);
				selectedPanel.add(widget);
				FormUtil.setWidgetPosition(widget,widget.getLeft(),widget.getTop());
				//FormUtil.setWidgetPosition(selectedPanel,widget,widget.getLeft(),widget.getTop());
			}
		}
	}

	private PushButton addDeleteButton(int row){
		if(row == -1)
			return null;
		
		PushButton btn = new PushButton(LocaleText.get("deleteItem"));
		btn.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				removeRow((Widget)event.getSource());
			}
		});
		table.setWidget(row, widgets.size(), btn);

		return btn;
	}


	private void removeRow(Widget sender){
		if (!Window.confirm(LocaleText.get("removeRowPrompt")))
			return;
		
		if(table.getRowCount() == 1){//There should be atleast one row{
			clearValue();
			return;
		}

		int rowStartIndex = 1;
		if(((RuntimeWidgetWrapper)table.getWidget(0, 0)).getWrappedWidget() instanceof Label)
			rowStartIndex = 2;
		
		for(int row = rowStartIndex; row < table.getRowCount(); row++){
			if(sender == table.getWidget(row, widgets.size())){

				RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)getParent().getParent();
				int y = getHeightInt();

				table.removeRow(row);
				Element node = dataNodes.get(row-rowStartIndex);
				node.getParentNode().removeChild(node);
				dataNodes.remove(node);
				if(btnAdd != null)
					btnAdd.setEnabled(true);

				editListener.onRowRemoved(wrapper, y-getHeightInt());

				RuntimeWidgetWrapper parent = (RuntimeWidgetWrapper)getParent().getParent();
				ValidationRule validationRule = parent.getValidationRule();
				if(validationRule != null){
					parent.getQuestionDef().setAnswer(table.getRowCount()+"");
					
					//Add error message
					if(getParent().getParent() instanceof RuntimeWidgetWrapper)
						((RuntimeWidgetWrapper)getParent().getParent()).isValid(true);
				}

				List<FormDef> forms = repeatRowFormMap.get(sender);
				if(forms != null){
					for(FormDef formDef : forms)
						((FormRunnerView)editListener).removeRepeatQtnFormDef(formDef);
				}
			}
		}
	}

	private int loadWidget(FormDef formDef, Element node,HashMap<Integer,RuntimeWidgetWrapper> widgets, List<RuntimeWidgetWrapper> externalSourceWidgets,
			HashMap<QuestionDef,List<QuestionDef>> calcQtnMappings,HashMap<QuestionDef,List<RuntimeWidgetWrapper>> calcWidgetMap,
			HashMap<QuestionDef,RuntimeWidgetWrapper> filtDynOptWidgetMap, HashMap<Integer,RuntimeWidgetWrapper> labelWidgetMap){

		RuntimeWidgetWrapper parentWrapper = null;

		String s = node.getAttribute(WidgetEx.WIDGET_PROPERTY_WIDGETTYPE);
		int tabIndex = (node.getAttribute(WidgetEx.WIDGET_PROPERTY_TABINDEX) != null ? Integer.parseInt(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TABINDEX)) : 0);

		QuestionDef questionDef = null;
		String binding = node.getAttribute(WidgetEx.WIDGET_PROPERTY_BINDING);
		String parentBinding = node.getAttribute(WidgetEx.WIDGET_PROPERTY_PARENTBINDING);

		if(isRepeated){
			if(binding != null && binding.trim().length() > 0 && groupQtnsDef != null){
				questionDef = groupQtnsDef.getQuestion(binding);
				if(questionDef != null)
					questionDef.setAnswer(questionDef.getDefaultValue()); //Just incase we are refreshing and had already set the answer
			}
		}
		else{
			if(binding != null && binding.trim().length() > 0){
				questionDef = formDef.getQuestion(binding);
				if(questionDef != null)
					questionDef.setAnswer(questionDef.getDefaultValue()); //Just incase we are refreshing and had already set the answer
			}
		}

		RuntimeWidgetWrapper wrapper = null;
		boolean wrapperSet = false;
		Widget widget = null;
		if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_RADIOBUTTON)){
			/*widget = new RadioButton(parentBinding,node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
			parentWrapper = getParentWrapper(widget,node);
			((RadioButton)widget).setTabIndex(tabIndex);*/

			widget = new RadioButtonWidget(parentBinding,node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));

			if(widgetMap.get(parentBinding) == null)
				wrapperSet = true;

			parentWrapper = getParentWrapper(widget,node,parentBinding);
			((RadioButton)widget).setTabIndex(tabIndex);

			if(wrapperSet){
				wrapper = parentWrapper;
				questionDef = formDef.getQuestion(parentBinding);
			}
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_CHECKBOX)){
			/*widget = new CheckBox(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
			parentWrapper = getParentWrapper(widget,node);
			((CheckBox)widget).setTabIndex(tabIndex);*/

			widget = new CheckBoxWidget(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
			if(widgetMap.get(parentBinding) == null)
				wrapperSet = true;

			parentWrapper = getParentWrapper(widget,node,parentBinding);
			((CheckBox)widget).setTabIndex(tabIndex);

			String defaultValue = parentWrapper.getQuestionDef().getDefaultValue();
			if(defaultValue != null && defaultValue.contains(binding))
				((CheckBox)widget).setValue(true);

			if(wrapperSet){
				wrapper = parentWrapper;
				questionDef = formDef.getQuestion(parentBinding);
			}
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_BUTTON)){
			widget = new Button(node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT));
			((Button)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LISTBOX)){
			widget = new ListBoxWidget(false);
			((ListBox)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TEXTAREA)){
			widget = new TextArea();
			((TextArea)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_DATEPICKER)){
			widget = new DatePickerWidget();
			((DatePickerEx)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_DATETIME)){
			widget = new DateTimeWidget();
			((DateTimeWidget)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TIME)){
			widget = new TimeWidget();
			((TimeWidget)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TEXTBOX)){
			widget = new TextBox();
			if(questionDef != null && (questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC 
					|| questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL))
				FormUtil.allowNumericOnly((TextBox)widget,questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL);
			((TextBox)widget).setTabIndex(tabIndex);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_HORIZONTAL_LINE)){
			widget = new HorizontalGridLine(0);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_VERTICAL_LINE)){
			widget = new VerticalGridLine(0);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LABEL)){
			String text = node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT);
			if(text == null) 
				text = "";
			widget = new Label(text);

			int pos1 = text.indexOf("${");
			int pos2 = text.indexOf("}$");
			if(pos1 > -1 && pos2 > -1 && (pos2 > pos1)){
				String varname = text.substring(pos1+2,pos2);
				labelText.put((Label)widget, text);
				labelReplaceText.put((Label)widget, "${"+varname+"}$");

				((Label)widget).setText(text.replace("${"+varname+"}$", ""));
				if(varname.startsWith("/"+ formDef.getBinding()+"/"))
					varname = varname.substring(("/"+ formDef.getBinding()+"/").length(),varname.length());

				QuestionDef qtnDef = formDef.getQuestion(varname);
				List<Label> labels = labelMap.get(qtnDef);
				if(labels == null){
					labels = new ArrayList<Label>();
					labelMap.put(qtnDef, labels);
				}
				labels.add((Label)widget);
			}
			
			if ("recordNavigationLabel".equals(binding)) {
				lblRecordNavigation = (Label)widget;
			}
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_IMAGE)){
			widget = new Image();
			String xpath = binding;
			if(!xpath.startsWith(formDef.getBinding()))
				xpath = "/" + formDef.getBinding() + "/" + binding;
			((Image)widget).setUrl(URL.encode(FormUtil.getMultimediaUrl()+"?formId="+formDef.getId()+"&xpath="+xpath+"&time="+ new java.util.Date().getTime()));
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_LOGO)){
			widget = new Image();
			((Image)widget).setUrl(URL.encode(FormUtil.getHostPageBaseURL() + node.getAttribute(WidgetEx.WIDGET_PROPERTY_EXTERNALSOURCE)));
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_VIDEO_AUDIO) && questionDef != null){
			widget = new HTML();
			String xpath = binding;
			if(!xpath.startsWith(formDef.getBinding()))
				xpath = "/" + formDef.getBinding() + "/" + binding;

			String extension = "";//.3gp ".mpeg";
			String contentType = "&contentType=video/3gpp";
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_AUDIO)
				contentType = "&contentType=audio/3gpp"; //"&contentType=audio/x-wav";
			//extension = ".wav";

			contentType += "&name="+questionDef.getBinding()+".3gp";

			((HTML)widget).setHTML("<a href=" + URL.encode(FormUtil.getMultimediaUrl()+extension + "?formId="+formDef.getId()+"&xpath="+xpath+contentType+"&time="+ new java.util.Date().getTime()) + ">"+node.getAttribute(WidgetEx.WIDGET_PROPERTY_TEXT)+"</a>");

			String answer = questionDef.getAnswer();
			if(answer == null || answer.trim().length() == 0 )
				((HTML)widget).setVisible(false);
		}
		else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_GROUPBOX)||s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_REPEATSECTION)
				|| s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_TABLE) ){
			GroupQtnsDef groupQtnsDef = null;
			if(questionDef != null && questionDef.isGroupQtnsDef())
				groupQtnsDef = questionDef.getGroupQtnsDef();

			boolean repeated = false;
			String value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_REPEATED);
			if(value != null && value.trim().length() > 0)
				repeated = (value.equals(WidgetEx.REPEATED_TRUE_VALUE));

			widget = new RuntimeGroupWidget(images, formDef, groupQtnsDef, editListener, widgetListener, repeated, enabledListener);
			((RuntimeGroupWidget)widget).loadWidgets(formDef,node.getChildNodes(),externalSourceWidgets,calcQtnMappings,calcWidgetMap,filtDynOptWidgetMap);
			copyLabelMap(((RuntimeGroupWidget)widget).getLabelMap());
			copyLabelText(((RuntimeGroupWidget)widget).getLabelText());
			copyLabelReplaceText(((RuntimeGroupWidget)widget).getLabelReplaceText());
			copyCheckBoxGroupMap(((RuntimeGroupWidget)widget).getCheckBoxGroupMap());
			copyCalcWidgetMap(((RuntimeGroupWidget)widget).getCalcWidgetMap());
			copyFiltDynOptWidgetMap(((RuntimeGroupWidget)widget).getFiltDynOptWidgetMap());
		}
		/*else if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_REPEATSECTION)){
			//Not dealing with nested repeats
			//widget = new RunTimeGroupWidget();
			//((RunTimeGroupWidget)widget).setTabIndex(tabIndex);
		}*/
		else
			return tabIndex;

		if(!wrapperSet){
			wrapper = new RuntimeWidgetWrapper(widget, images.error(), editListener, widgetListener, enabledListener);

			if(parentWrapper != null){ //Check box or radio button
				if(!parentWrapper.getQuestionDef().isVisible())
					wrapper.setVisible(false);
				if(!parentWrapper.getQuestionDef().isEnabled())
					wrapper.setEnabled(false);
				if(parentWrapper.getQuestionDef().isLocked())
					wrapper.setLocked(true);
			}
		}

		//RuntimeWidgetWrapper wrapper = new RuntimeWidgetWrapper(widget,images.error(),editListener);
		boolean loadWidget = true;

		String value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_HELPTEXT);
		if(value != null && value.trim().length() > 0)
			wrapper.setTitle(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_WIDTH);
		if(value != null && value.trim().length() > 0)
			wrapper.setWidth(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_HEIGHT);
		if(value != null && value.trim().length() > 0)
			wrapper.setHeight(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_EXTERNALSOURCE);
		if(value != null && value.trim().length() > 0)
			wrapper.setExternalSource(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_DISPLAYFIELD);
		if(value != null && value.trim().length() > 0)
			wrapper.setDisplayField(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_FILTERFIELD);
		if(value != null && value.trim().length() > 0)
			wrapper.setFilterField(value);

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_ID);
		if(value != null && value.trim().length() > 0)
			wrapper.setId(value);
		
		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_CLASS);
		if(value != null && value.trim().length() > 0)
			wrapper.setCls(value);

		if(s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_VIDEO_AUDIO) || s.equalsIgnoreCase(WidgetEx.WIDGET_TYPE_IMAGE)){
			if(binding != null && binding.trim().length() > 0){
				questionDef = formDef.getQuestion(binding);
				if(questionDef != null)
					questionDef.setAnswer(questionDef.getDefaultValue()); //Just incase we are refreshing and had already set the answer
			}
		}

		if(questionDef != null){
			if(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
				questionDef.setOptions(null); //may have been set by the preview
				//if(wrapper.getWrappedWidget() instanceof ListBox || wrapper.getWrappedWidget() instanceof TextBox)
				if(wrapper.getFilterField() != null && wrapper.getFilterField().trim().length() > 0)
					filtDynOptWidgetMap.put(questionDef, wrapper);
			}

			wrapper.setQuestionDef(questionDef,false);
			ValidationRule validationRule = formDef.getValidationRule(questionDef);
			wrapper.setValidationRule(validationRule);
		}

		if(parentBinding != null)
			wrapper.setParentBinding(parentBinding);

		if(binding != null)
			wrapper.setBinding(binding);

		if(parentWrapper != null)
			parentWrapper.addChildWidget(wrapper);


		/*value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_VALUEFIELD);
		if(value != null && value.trim().length() > 0){
			wrapper.setValueField(value);

			if(externalSourceWidgets != null && wrapper.getExternalSource() != null && wrapper.getDisplayField() != null
					&& (wrapper.getWrappedWidget() instanceof TextBox || wrapper.getWrappedWidget() instanceof ListBox)
					&& questionDef != null
					&& (wrapper.getQuestionDef().getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE
							||wrapper.getQuestionDef().getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)){
				externalSourceWidgets.add(wrapper);
				loadWidget = false;
			}
		}*/

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_VALUEFIELD);
		if(value != null && value.trim().length() > 0){
			wrapper.setValueField(value);

			if(externalSourceWidgets != null && wrapper.getExternalSource() != null && wrapper.getDisplayField() != null
					&& (wrapper.getWrappedWidget() instanceof TextBox || wrapper.getWrappedWidget() instanceof ListBox)
					&& questionDef != null){

				if(!(questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE
						||questionDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE)){
					questionDef.setDataType(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE);
				}

				externalSourceWidgets.add(wrapper);
				loadWidget = false;

				wrapper.addSuggestBoxChangeEvent();
			}
		}

		if(loadWidget)
			wrapper.loadQuestion();

		wrapper.setExternalSourceDisplayValue();

		value = node.getAttribute(WidgetEx.WIDGET_PROPERTY_HEIGHT);
		if(value != null && value.trim().length() > 0)
			wrapper.setHeight(value);

		String left = node.getAttribute(WidgetEx.WIDGET_PROPERTY_LEFT);
		if(left != null && left.trim().length() > 0)
			wrapper.setLeft(left);

		String top = node.getAttribute(WidgetEx.WIDGET_PROPERTY_TOP);
		if(top != null && top.trim().length() > 0)
			wrapper.setTop(top);

		//if(wrapper.getWrappedWidget() instanceof Label)
		WidgetEx.loadLabelProperties(node,wrapper);

		wrapper.setTabIndex(tabIndex);
		//wrapper.setParentBinding(parentBinding);

		if(tabIndex > 0 && !(wrapper.getWrappedWidget() instanceof Button))
			widgets.put(new Integer(tabIndex), wrapper);
		else if (wrapper.getWrappedWidget() instanceof Label && this.isRepeated)
			labelWidgetMap.put(new Integer(wrapper.getLeftInt()), wrapper);
		else
			addWidget(wrapper);

		if(wrapperSet)
			;//FormUtil.setWidgetPosition(wrapper,left,top);

		if(widget instanceof Button && binding != null){
			//wrapper.setParentBinding(parentBinding);

			if(binding.equals("addnew")||binding.equals("remove") || binding.equals("submit") ||
					binding.equals("browse")||binding.equals("clear")||binding.equals("cancel") ||
					binding.equals("nextRecord")||binding.equals("prevRecord") ||
					binding.equals("firstRecord")||binding.equals("lastRecord") ||
					binding.equals("newRecord")||binding.equals("deleteRecord") ||
					binding.equals("search") || binding.equals("nextPage")||binding.equals("prevPage")){
				((Button)widget).addClickHandler(new ClickHandler(){
					public void onClick(ClickEvent event){
						execute((Widget)event.getSource());
					}
				});
				
				if(binding.equals("addnew")) {
					btnAdd = (Button)widget;
				}
				else if(binding.equals("firstRecord")) {
					btnFirstRecord = (Button)widget;
					btnFirstRecord.setEnabled(false);
				}
				else if(binding.equals("prevRecord")) {
					btnPrevRecord = (Button)widget;
					btnPrevRecord.setEnabled(false);
				}
				else if(binding.equals("nextRecord")) {
					btnNextRecord = (Button)widget;
					btnNextRecord.setEnabled(false);
				}
				else if(binding.equals("lastRecord")) {
					btnLastRecord = (Button)widget;
					btnLastRecord.setEnabled(false);
				}
			}
		}

		if(wrapper.isEditable() && questionDef != null)
			FormRunnerView.updateCalcWidgetMapping(wrapper, calcQtnMappings, calcWidgetMap);

		return tabIndex;
	}

	/**
	 * Just adds the first row. Other runtime rows are added using addNewRow
	 * @param wrapper
	 */
	private void addWidget(RuntimeWidgetWrapper wrapper){
		String binding = wrapper.getBinding();
		if(wrapper.getWrappedWidget() instanceof Button && 
				!("browse".equals(binding) || "clear".equals(binding))){

			//Ensure that the Add New and Remove buttons are displayed according to tab index
			if(buttons.size() == 0 || (buttons.get(0).getTabIndex() <= wrapper.getTabIndex()))
				buttons.add(wrapper);
			else{
				RuntimeWidgetWrapper w = buttons.remove(0);
				buttons.add(wrapper);
				buttons.add(w);
			}
			return;
		}

		if(isRepeated){
			//widgets.add(new RuntimeWidgetWrapper(wrapper));
			widgets.add(wrapper);

			int row = 0 , col = 0;
			if(table.getRowCount() > 0){
				if(((RuntimeWidgetWrapper)table.getWidget(0, 0)).getWrappedWidget() instanceof Label){
					row = 1;
					if(table.getRowCount() == 1)
						col = 0;
					else
						col = table.getCellCount(row);
				}
				else{
					col = table.getCellCount(row);
				}
			}

			table.setWidget(row, col, wrapper);
		}
		else{
			selectedPanel.add(wrapper);
			FormUtil.setWidgetPosition(wrapper,wrapper.getLeft(),wrapper.getTop());
			//FormUtil.setWidgetPosition(selectedPanel,wrapper,wrapper.getLeft(),wrapper.getTop());
		}
	}

	private void execute(Widget sender){
		String binding = ((RuntimeWidgetWrapper)sender.getParent().getParent()).getBinding();

		if(binding.equalsIgnoreCase("search")){
			RuntimeWidgetWrapper wrapper = getCurrentMultimediWrapper(sender);
			if(wrapper != null && wrapper.getExternalSource() != null)
				;//FormUtil.searchExternal(wrapper.getExternalSource(),sender.getElement(),wrapper.getWrappedWidget().getElement(),null);
		}
		else if(binding.equalsIgnoreCase("submit"))
			((FormRunnerView)getParent().getParent().getParent().getParent().getParent().getParent().getParent()).onSubmit();
		else if(binding.equalsIgnoreCase("cancel"))
			((FormRunnerView)getParent().getParent().getParent().getParent().getParent().getParent().getParent()).onCancel();
		else if(binding.equalsIgnoreCase("nextPage"))
			((FormRunnerView)getParent().getParent().getParent().getParent().getParent().getParent().getParent()).nextPage();
		else if(binding.equalsIgnoreCase("prevPage"))
			((FormRunnerView)getParent().getParent().getParent().getParent().getParent().getParent().getParent()).prevPage();
		else if(groupQtnsDef != null && groupQtnsDef instanceof RepeatQtnsDef){
			if (readOnly)
				return;
			
			if(binding.equalsIgnoreCase("addnew")){
				RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)getParent().getParent();
				int y = getHeightInt();

				addNewRow(sender);

				editListener.onRowAdded(wrapper,getHeightInt()-y);
			}
			else if(binding.equalsIgnoreCase("remove")){
				if(table.getRowCount() > 1){//There should be atleast one row{
					RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)getParent().getParent();
					int y = getHeightInt();

					table.removeRow(table.getRowCount()-1);
					Element node = dataNodes.get(dataNodes.size() - 1);
					node.getParentNode().removeChild(node);
					dataNodes.remove(node);
					if(btnAdd != null)
						btnAdd.setEnabled(true);

					editListener.onRowRemoved(wrapper,y-getHeightInt());
				}

				RuntimeWidgetWrapper parent = (RuntimeWidgetWrapper)getParent().getParent();
				ValidationRule validationRule = parent.getValidationRule();
				if(validationRule != null)
					parent.getQuestionDef().setAnswer(table.getRowCount()+"");
			}
			else {
				executeRecordOperation(binding);
			}
		}
		else{
			if (readOnly)
				return;
			
			if(binding.equalsIgnoreCase("clear")){
				RuntimeWidgetWrapper wrapper = getCurrentMultimediWrapper(sender);
				if(wrapper == null)
					return;

				if(wrapper.getWrappedWidget() instanceof Image && (((Image)wrapper.getWrappedWidget()).getUrl() == null ||
						((Image)wrapper.getWrappedWidget()).getUrl().trim().length() == 0))
					return;
				if(wrapper.getWrappedWidget() instanceof HTML && !wrapper.getWrappedWidget().isVisible())
					return;

				if(!Window.confirm(LocaleText.get("deleteItemPrompt")))
					return;

				QuestionDef questionDef = wrapper.getQuestionDef();
				if(questionDef != null)
					questionDef.setAnswer(null);

				if(wrapper.getWrappedWidget() instanceof Image){
					image = (Image)wrapper.getWrappedWidget();
					image.setUrl(null);
					html = null;
				}
				else if(wrapper.getWrappedWidget() instanceof Label)
					((Label)wrapper.getWrappedWidget()).setText(LocaleText.get("noSelection"));
				else{
					html = (HTML)wrapper.getWrappedWidget();
					html.setHTML(LocaleText.get("clickToPlay"));
					html.setVisible(false);
					image = null;
				}
				return;
			}
			else if(binding.equalsIgnoreCase("browse")){
				RuntimeWidgetWrapper wrapper = getCurrentMultimediWrapper(sender);
				if(wrapper == null)
					return;

				if(wrapper.getWrappedWidget() instanceof Image)
					image = (Image)wrapper.getWrappedWidget();
				else
					html = (HTML)wrapper.getWrappedWidget();

				String xpath = wrapper.getBinding();
				if(!xpath.startsWith(formDef.getBinding()))
					xpath = "/" + formDef.getBinding() + "/" + wrapper.getBinding();

				String contentType = "&contentType=video/3gpp";
				contentType += "&name="+wrapper.getQuestionDef().getBinding()+".3gp";

				//TODO What if the multimedia url suffix already has a ?
				String url = FormUtil.getMultimediaUrl()+"?formId="+formDef.getId()+"&xpath="+xpath+contentType+"&time="+ new java.util.Date().getTime();
				OpenFileDialog dlg = new OpenFileDialog(this,url);
				dlg.center();
			}
		}
	}

	/*private Image getCurrentImage(Widget sender){			
		RuntimeWidgetWrapper wrapper = getCurrentMultimediWrapper(sender);
		if(wrapper != null)
			return (Image)wrapper.getWrappedWidget();

		return null;
	}*/

	private RuntimeWidgetWrapper getCurrentMultimediWrapper(Widget sender){
		RuntimeWidgetWrapper button = (RuntimeWidgetWrapper)sender.getParent().getParent();
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
			Widget wrappedWidget = widget.getWrappedWidget();
			if(wrappedWidget instanceof Image || wrappedWidget instanceof HTML /*|| wrappedWidget instanceof Label*/){
				String binding  = widget.getBinding();
				if(binding != null && binding.equalsIgnoreCase(button.getParentBinding()))
					return widget;
			}
		}
		return null;
	}

	private void addNewRow(Widget sender){
		HashMap<String,RuntimeWidgetWrapper> widgetMap = new HashMap<String,RuntimeWidgetWrapper>();

		RuntimeWidgetWrapper firstWidget = null;
		Element newRepeatDataNode = null;
		String parentRptBinding = null;
		int row = table.getRowCount();

		List<Integer> qtnIds = new ArrayList<Integer>();
		List<QuestionDef> qtns = new ArrayList<QuestionDef>();
		
		FormDef copyFormDef = new FormDef(formDef);
		List<RuntimeWidgetWrapper> copyWidgets = new ArrayList<RuntimeWidgetWrapper>();
		
		for(int index = 0; index < widgets.size(); index++){
			RuntimeWidgetWrapper mainWidget = widgets.get(index);
			RuntimeWidgetWrapper copyWidget = getPreparedWidget(mainWidget,false);

			if(mainWidget.getQuestionDef() == null && (mainWidget.getWrappedWidget() instanceof CheckBox)){
				parentRptBinding = ((QuestionDef)widgets.get(0).getQuestionDef().getParent()).getBinding();
				copyWidget.setQuestionDef(new QuestionDef(widgets.get(0).questionDef, widgets.get(0).questionDef.getParent()), false);
			}
			else
				parentRptBinding = ((QuestionDef)mainWidget.getQuestionDef().getParent()).getBinding();

			//table.setWidget(row, index, copyWidget);

			if(index == 0){
				Element dataNode = mainWidget.getQuestionDef().getDataNode();
				if(dataNode == null){
					Window.alert(LocaleText.get("repeatChildDataNodeNotFound"));
					return; //possibly form not yet saved
				}

				Element repeatDataNode = getParentNode(dataNode,(mainWidget.getWrappedWidget() instanceof CheckBox) ? mainWidget.getParentBinding() : mainWidget.getBinding(), parentRptBinding);
				newRepeatDataNode = (Element)repeatDataNode.cloneNode(true);
				repeatDataNode.getParentNode().appendChild(newRepeatDataNode);
				//workonDefaults(newRepeatDataNode);
				dataNodes.add(newRepeatDataNode);

				firstWidget = copyWidget;
			}

			table.setWidget(row, index, copyWidget);

			setDataNode(copyWidget,newRepeatDataNode,copyWidget.getBinding(),false, parentRptBinding);

			//For now we do not allow default values for repeat kids to simplify implementation.
			Element node = copyWidget.getQuestionDef().getDataNode();
			if(!(hasDefaultValue(node))){
				copyWidget.getQuestionDef().setDefaultValue(null);
			}

			//Loading widget from here instead of in getPreparedWidget because setDataNode may clear default values			
			copyWidget.loadQuestion();

			if(copyWidget.getWrappedWidget() instanceof RadioButton)
				((RadioButton)copyWidget.getWrappedWidget()).setName(((RadioButton)copyWidget.getWrappedWidget()).getName()+row);

			if(copyWidget.getWrappedWidget() instanceof CheckBox){
				RuntimeWidgetWrapper widget = widgetMap.get(copyWidget.getParentBinding());
				if(widget == null){
					widget = copyWidget;
					widgetMap.put(copyWidget.getParentBinding(), widget);
				}
				widget.addChildWidget(copyWidget);
			}

			copyWidget.getQuestionDef().addChangeListener(copyWidget);
			qtnIds.add(copyWidget.getQuestionDef().getId());
			qtns.add(copyWidget.getQuestionDef());
			
			copyFormDef.removeQuestion(copyFormDef.getQuestion(copyWidget.getQuestionDef().getId()));
			copyFormDef.addQuestion(copyWidget.getQuestionDef());
			copyWidgets.add(copyWidget);
		}

		PushButton deleteButton = addDeleteButton(row);
		copySkipRules(qtnIds, qtns, deleteButton);
		copyCalculations(copyWidgets, copyFormDef);

		btnAdd = (Button)sender;
		RuntimeWidgetWrapper parent = (RuntimeWidgetWrapper)getParent().getParent();
		ValidationRule validationRule = parent.getValidationRule();
		if(validationRule != null){
			row++;
			parent.getQuestionDef().setAnswer(row+"");
			if(validationRule.getMaxValue(formDef) == row){
				((Button)sender).setEnabled(false);
				
				//Remove error message.
				if(getParent().getParent() instanceof RuntimeWidgetWrapper)
					((RuntimeWidgetWrapper)getParent().getParent()).isValid(true);
			}
		}

		if(firstWidget != null)
			firstWidget.setFocus();

		//byte maxRows = repeatQtnsDef.getMaxRows();
		//if(maxRows > 0 && row == maxRows)
		//	((Button)sender).setEnabled(false);
	}

	private void addNewRow(Element dataNode){
		dataNodes.add(dataNode);

		List<Integer> qtnIds = new ArrayList<Integer>();
		List<QuestionDef> qtns = new ArrayList<QuestionDef>();
		
		FormDef copyFormDef = new FormDef(formDef);
		List<RuntimeWidgetWrapper> copyWidgets = new ArrayList<RuntimeWidgetWrapper>();
		
		int row = table.getRowCount();
		for(int index = 0; index < widgets.size(); index++){
			RuntimeWidgetWrapper mainWidget = widgets.get(index);
			RuntimeWidgetWrapper copyWidget = getPreparedWidget(mainWidget,false);

			table.setWidget(row, index, copyWidget);

			setDataNode(copyWidget,dataNode,copyWidget.getBinding(),true, ((QuestionDef)mainWidget.getQuestionDef().getParent()).getBinding());
			
			//For now we do not allow default values for repeat kids to simplify implementation.
			Element node = copyWidget.getQuestionDef().getDataNode();
			if(!(hasDefaultValue(node))){
				copyWidget.getQuestionDef().setDefaultValue(null);
			}

			//Loading widget from here instead of in getPreparedWidget because setDataNode may clear default values			
			//copyWidget.loadQuestion(); commented out because it is called in setDataNode above

			if(copyWidget.getWrappedWidget() instanceof RadioButton)
				((RadioButton)copyWidget.getWrappedWidget()).setName(((RadioButton)copyWidget.getWrappedWidget()).getName()+row);

			if(copyWidget.getWrappedWidget() instanceof CheckBox){
				RuntimeWidgetWrapper widget = widgetMap.get(copyWidget.getParentBinding());
				if(widget == null){
					widget = copyWidget;
					widgetMap.put(copyWidget.getParentBinding(), widget);
				}
				widget.addChildWidget(copyWidget);
			}
			
			copyWidget.getQuestionDef().addChangeListener(copyWidget);
			qtnIds.add(copyWidget.getQuestionDef().getId());
			qtns.add(copyWidget.getQuestionDef());
			
			copyFormDef.removeQuestion(copyFormDef.getQuestion(copyWidget.getQuestionDef().getId()));
			copyFormDef.addQuestion(copyWidget.getQuestionDef());
			copyWidgets.add(copyWidget);
		}

		PushButton deleteButton = addDeleteButton(row);
		copySkipRules(qtnIds, qtns, deleteButton);
		copyCalculations(copyWidgets, copyFormDef);
	}

	private Element getParentNode(Node node, String binding, String parentBinding){	
		String name = binding;
		if(parentBinding != null && binding.startsWith(parentBinding) && binding.indexOf('/', parentBinding.length() + 1) > 0)
			name = binding.substring(parentBinding.length() + 1, binding.indexOf('/', parentBinding.length() + 1));
		else{
			int pos = binding.indexOf('/');
			if(pos > 0){
				name = binding.substring(0, pos);
				int pos2 = binding.lastIndexOf('/');
				if(pos != pos2)
					return (Element)node.getParentNode(); //name = binding.substring(pos+1, pos2);
			}
		}

		return getParentNodeWithName(node,name);
	}

	private Element getParentNodeWithName(Node node, String name){
		Element parentNode = (Element)node.getParentNode();
		String nodeName = node.getNodeName();
		if(nodeName.startsWith("xf:")){//caters for xforms from other conversions
			nodeName = nodeName.substring(3);
		}
		if(nodeName.equalsIgnoreCase(name))
			return parentNode;
		else if(name.contains("/")) //TODO This needs to be well tested such that we do not introduce bugs.
			return parentNode;
		return getParentNodeWithName(parentNode,name);
	}

	private void setDataNode(RuntimeWidgetWrapper widget, Element parentNode, String binding, boolean loadQtn, String parentBinding){
		if(widget.getQuestionDef() == null)
			return; //for checkboxes, only the first may have reference to the parent questiondef

		String name = null;
		int pos = 0;
		if(parentBinding != null && binding.startsWith(parentBinding) && binding.indexOf('/', parentBinding.length() + 1) > 0){
			name = binding.substring(parentBinding.length() + 1);
			String s = name.substring(0, name.indexOf('/'));
			parentNode = XmlUtil.getNode(parentNode, s);
		}
		else{
			name = (widget.getWrappedWidget() instanceof CheckBox) ? widget.getParentBinding() : binding;
			pos = name.indexOf('/');
			if(pos > 0){
				int pos2 = name.lastIndexOf('/');
				if(pos != pos2){
					name = name.substring(pos2+1);
					pos = -1;
				}
				else
					name = name.substring(0, pos);
			}
		}

		NodeList nodes = parentNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node child = nodes.item(index);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if(name.contains("/"))
				name = name.substring(name.lastIndexOf('/') + 1);
			if(child.getNodeName().equals(name) /*||
					(child.getParentNode().getNodeName() + "/"+ child.getNodeName()).equals(widget.getBinding())*/){
				if(pos > 0)
					setDataNode(widget,(Element)child,binding.substring(pos+1),loadQtn, parentBinding);
				else{
					widget.getQuestionDef().setDataNode((Element)child);
					if(loadQtn){
						widget.getQuestionDef().setDefaultValue(XmlUtil.getTextValue((Element)child));
						widget.loadQuestion();
					}
					else{
						parentNode.setAttribute("new", XformConstants.XPATH_VALUE_TRUE);
						((Element)child).setAttribute("new", XformConstants.XPATH_VALUE_TRUE);
						if(XformConstants.XPATH_VALUE_FALSE.equals(((Element)child).getAttribute("default")))
							widget.getQuestionDef().setDefaultValue(null);
					}
					widget.setExternalSourceDisplayValue();
				}

				break;
			}
		}
		
		if (widget.getWrappedWidget() instanceof RuntimeGroupWidget) {
			((RuntimeGroupWidget)widget.getWrappedWidget()).setDataNodes(widget.getQuestionDef().getDataNode(), loadQtn, binding);
		}
	}
	
	private void setDataNodes(Element parentDataNode, boolean loadQtn, String parentBinding) {
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++) {
			RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
			setDataNode(widget, parentDataNode, widget.getBinding(), loadQtn, parentBinding);
		}
	}

	private RuntimeWidgetWrapper getPreparedWidget(RuntimeWidgetWrapper w, boolean loadQtn){
		RuntimeWidgetWrapper widget = new RuntimeWidgetWrapper(w);

		if(loadQtn)
			widget.loadQuestion();

		QuestionDef questionDef = widget.getQuestionDef();
		if(questionDef != null && (questionDef.getDataType() == QuestionDef.QTN_TYPE_NUMERIC 
				|| questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL))
			FormUtil.allowNumericOnly((TextBox)widget.getWrappedWidget(),questionDef.getDataType() == QuestionDef.QTN_TYPE_DECIMAL);

		widget.refreshSize();
		return widget;
	}

	public void setEnabled(boolean enabled){
		if(isRepeated){
			HorizontalPanel panel = (HorizontalPanel)verticalPanel.getWidget(1);
			for(int index = 0; index < panel.getWidgetCount(); index++)
				((RuntimeWidgetWrapper)panel.getWidget(index)).setEnabled(enabled);

			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++)
					((RuntimeWidgetWrapper)table.getWidget(row, col)).setEnabled(enabled);
			}
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).setEnabled(enabled);
			}
		}
	}

	public void setLocked(boolean locked){
		if(isRepeated){
			HorizontalPanel panel = (HorizontalPanel)verticalPanel.getWidget(1);
			for(int index = 0; index < panel.getWidgetCount(); index++)
				((RuntimeWidgetWrapper)panel.getWidget(index)).setLocked(locked);

			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++)
					((RuntimeWidgetWrapper)table.getWidget(row, col)).setLocked(locked);
			}	
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).setLocked(locked);
			}
		}
	}
	
	public void setReadOnlyEx(boolean readOnly){
		this.readOnly = readOnly;
	}
	
	public void setReadOnly(boolean readOnly){
		this.readOnly = readOnly;
		
		if(isRepeated){
			HorizontalPanel panel = (HorizontalPanel)verticalPanel.getWidget(1);
			for(int index = 0; index < panel.getWidgetCount(); index++)
				((RuntimeWidgetWrapper)panel.getWidget(index)).setReadOnly(readOnly);

			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++)
					((RuntimeWidgetWrapper)table.getWidget(row, col)).setReadOnly(readOnly);
			}	
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).setReadOnly(readOnly);
			}
		}
	}

	public void saveValue(FormDef formDef){
		if(isRepeated){
			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++) {
					RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)table.getWidget(row, col);
					widget.saveValue(formDef);
					/*QuestionDef qtnDef = widget.getQuestionDef();
					if (qtnDef == null)
						continue;
					
					Element dataNode = qtnDef.getDataNode();
					if (dataNode == null)
						continue;
					
					String answer = qtnDef.getAnswer();
					if (answer == null || answer.trim().length() == 0) {
						widget.clearDataNodeValue(formDef);
					}*/
				}
			}
		}
		else{
			for (Element node : dataNodes) {
				node.getParentNode().removeChild(node);
			}
			dataNodes.clear();
			
			if (groupQtnsDef != null && groupQtnsDef.isSubForm() && records.size() > 1) {
				saveAllRecordValues(groupQtnsDef.getQtnDef().getDataNode());
			}
			else {
				saveValues();
			}
		}

		if(groupQtnsDef != null & isRepeated)
			groupQtnsDef.getQtnDef().setAnswer(getRowCount()+"");
	}
	
	private void saveValues() {
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
			((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).saveValue(formDef);
		}
	}

	public int getRowCount(){
		int rows = 0;

		for(int row = 0; row < table.getRowCount(); row++){
			boolean answerFound = false;
			for(int col = 0; col < table.getCellCount(row)-1; col++){
				if(((RuntimeWidgetWrapper)table.getWidget(row, col)).isAnswered()){
					answerFound = true;
					break;
				}
			}

			if(answerFound)
				rows++;
		}

		return rows;
	}

	public void onSetFileContents(String contents) {
		if(contents != null && contents.trim().length() > 0){
			contents = contents.replace("<pre>", "");
			contents = contents.replace("</pre>", "");
			RuntimeWidgetWrapper widgetWrapper = null;

			if(image != null)
				widgetWrapper = (RuntimeWidgetWrapper)image.getParent().getParent();
			else
				widgetWrapper = (RuntimeWidgetWrapper)html.getParent().getParent();

			String xpath = widgetWrapper.getBinding();
			if(!xpath.startsWith(formDef.getBinding()))
				xpath = "/" + formDef.getBinding() + "/" + widgetWrapper.getBinding();

			if(image != null)
				image.setUrl(FormUtil.getMultimediaUrl()+"?action=recentbinary&time="+ new java.util.Date().getTime()+"&formId="+formDef.getId()+"&xpath="+xpath);
			else{
				String extension = "";//.3gp ".mpeg";
				String contentType = "&contentType=video/3gpp";
				if(widgetWrapper.getQuestionDef().getDataType() == QuestionDef.QTN_TYPE_AUDIO)
					contentType = "&contentType=audio/3gpp"; //"&contentType=audio/x-wav";
				//extension = ".wav";

				contentType += "&name="+widgetWrapper.getQuestionDef().getBinding()+".3gp";

				html.setVisible(true);
				html.setHTML("<a href=" + URL.encode(FormUtil.getMultimediaUrl()+extension + "?formId="+formDef.getId()+"&xpath="+xpath+contentType+"&time="+ new java.util.Date().getTime()) + ">"+html.getText()+"</a>");				
			}

			widgetWrapper.getQuestionDef().setAnswer(contents);
		}
	}

	public void clearValue(){
		clearInputValues();
		
		if (!isRepeated) {
			if (groupQtnsDef != null && groupQtnsDef.isSubForm()) {
				records = new ArrayList<HashMap<String, Object>>();
				records.add(new HashMap<String, Object>());
				currentRecordIndex = 0;
				setNavigationButtonStatus();
			}
		}
	}
	
	public void clearInputValues(){
		if(isRepeated){
			while(table.getRowCount() > 1)
				table.removeRow(1);

			for(int col = 0; col < table.getCellCount(0)-1; col++) {
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)table.getWidget(0, col);
				widget.clearValue();
				//widget.clearDataNodeValue(formDef);
			}

			//TODO Causes an infinite loop for repeat questions having skip logic that refers
			//     to non repeat children.
			//((FormRunnerView)editListener).fireSkipRules();
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++) {
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				widget.clearValue();
			}
		}
	}

	public boolean isValid(boolean fireValueChanged){
		firstInvalidWidget = null;

		if(isRepeated){
			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++){
					Widget widget = table.getWidget(row, col);
					if (widget != null) {
						boolean valid = ((RuntimeWidgetWrapper)widget).isValid(fireValueChanged);
						if(!valid){
							firstInvalidWidget = (RuntimeWidgetWrapper)table.getWidget(row, col);
							return false;
						}
					}
				}
			}
			return true;
		}
		else{
			boolean valid = true;
			for(int index=0; index<selectedPanel.getWidgetCount(); index++){
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if(!widget.isValid(fireValueChanged)){
					valid = false;
					if(firstInvalidWidget == null && widget.isFocusable())
						firstInvalidWidget = widget.getInvalidWidget();
				}

				if(fireValueChanged && widget.getQuestionDef() != null)
					editListener.onValueChanged(widget);
			}
			return valid;
		}
	}

	public RuntimeWidgetWrapper getInvalidWidget(){
		if(firstInvalidWidget == null)
			return (RuntimeWidgetWrapper)getParent().getParent();
		return firstInvalidWidget;
	}

	public boolean setFocus(){
		if(isRepeated){
			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++){
					RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)table.getWidget(row, col);
					if(widget.isFocusable()){
						if(widget.setFocus())
							return true;
					}
				}
			}
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.isFocusable()){
					if(widget.setFocus())
						return true;
				}
			}
		}

		return false;
	}

	public boolean onMoveToNextWidget(Widget widget) {
		int index = selectedPanel.getWidgetIndex(widget);

		if(index == -1){
			//Handle tabbing for repeats within the flex table
			if(isRepeated){
				boolean found = false;
				for(int row = 0; row < table.getRowCount(); row++){
					for(int col = 0; col < table.getCellCount(row); col++){
						if(found){
							Widget curWidget = table.getWidget(row, col);
							if(curWidget instanceof RuntimeWidgetWrapper && ((RuntimeWidgetWrapper)curWidget).setFocus())
								return true;
						}

						if(table.getWidget(row, col) == widget)
							found = true;
					}
				}
			}

			return false;
		}

		return moveToNextWidget(index);
	}

	public boolean onMoveToPrevWidget(Widget widget){
		int index = selectedPanel.getWidgetIndex(widget);
		
		if(index == -1){
			//Handle tabbing for repeats within the flex table
			if(isRepeated){
				boolean found = false;
				for(int row = table.getRowCount() - 1; row >= 0; row--){
					for(int col = table.getCellCount(row) - 1; col >= 0 ; col--){
						if(found){
							Widget curWidget = table.getWidget(row, col);
							if(curWidget instanceof RuntimeWidgetWrapper && ((RuntimeWidgetWrapper)curWidget).setFocus())
								return true;
						}

						if(table.getWidget(row, col) == widget)
							found = true;
					}
				}
			}

			return false;
		}
		
		
		while(--index > 0){
			if(((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).setFocus())
				return true;
		}

		return false;
	}

	protected boolean moveToNextWidget(int index){
		while(++index < selectedPanel.getWidgetCount())
			if(((RuntimeWidgetWrapper)selectedPanel.getWidget(index)).setFocus()){
				return true;
			}

		return false;
	}

	public HashMap<QuestionDef,List<Label>> getLabelMap(){
		return labelMap;
	}

	public HashMap<QuestionDef,List<RuntimeWidgetWrapper>> getCalcWidgetMap(){
		return calcWidgetMap;
	}

	public HashMap<QuestionDef,RuntimeWidgetWrapper> getFiltDynOptWidgetMap(){
		return filtDynOptWidgetMap;
	}

	public HashMap<Label,String> getLabelText(){
		return labelText;
	}

	public HashMap<Label,String> getLabelReplaceText(){
		return labelReplaceText;
	}

	public HashMap<QuestionDef,List<CheckBox>> getCheckBoxGroupMap(){
		return checkBoxGroupMap;
	}

	public void onEnabledChanged(QuestionDef sender,boolean enabled){
		List<CheckBox> list = checkBoxGroupMap.get(sender);
		if(list == null)
			return;

		for(CheckBox checkBox : list){
			checkBox.setEnabled(enabled);
			if(!enabled)
				checkBox.setValue(false);
		}
	}

	public void onVisibleChanged(QuestionDef sender,boolean visible){
		List<CheckBox> list = checkBoxGroupMap.get(sender);
		if(list == null)
			return;

		for(CheckBox checkBox : list){
			checkBox.setVisible(visible);
			if(!visible)
				checkBox.setValue(false);
		}
	}

	public void onRequiredChanged(QuestionDef sender,boolean required){

	}

	public void onLockedChanged(QuestionDef sender,boolean locked){

	}
	
	public void onReadOnlyChanged(QuestionDef sender,boolean readOnly){

	}

	public void onBindingChanged(QuestionDef sender,String newValue){

	}

	public void onDataTypeChanged(QuestionDef sender,int dataType){

	}

	public void onOptionsChanged(QuestionDef sender,List<OptionDef> optionList){

	}

	public int getHeightInt(){
		return getElement().getOffsetHeight();
	}

	/*private void workonDefaults(Node repeatDataNode){
		NodeList nodes = repeatDataNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node child = nodes.item(index);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;

			if(XformConstants.XPATH_VALUE_FALSE.equals(((Element)child).getAttribute("default")))
				XmlUtil.setTextValue((Element)child, "");

			workonDefaults(child);
		}
	}*/

	public void copySkipRules(List<Integer> qtnIds, List<QuestionDef> qtns, PushButton deleteButton){
		List<FormDef> forms = new ArrayList<FormDef>();

		Vector rules = formDef.getSkipRules();
		if(rules != null){
			for(int i=0; i<rules.size(); i++){
				SkipRule rule = (SkipRule)rules.elementAt(i);

				for(int k = 0; k < rule.getConditionCount(); k++){
					Condition condition = rule.getConditionAt(k);
					if(qtnIds.contains(condition.getQuestionId())){
						SkipRule skipRule = new SkipRule(rule);	
						FormDef formDef = new FormDef();
						formDef.addSkipRule(skipRule);
						forms.add(formDef);

						for(QuestionDef qtn : qtns)
							formDef.addQuestion(qtn);

						skipRule.fire(formDef);

						break;
					}
				}
			}
		}

		for(FormDef form : forms)
			((FormRunnerView)editListener).addRepeatQtnFormDef(form);

		if(forms.size() > 0)
			repeatRowFormMap.put(deleteButton, forms);
	}
	
	private void copyCalculations(List<RuntimeWidgetWrapper> widgets, FormDef formDef){
		HashMap<QuestionDef,List<QuestionDef>> calcQtnMappings = FormRunnerView.getCalcQtnMappings(formDef);

		for(RuntimeWidgetWrapper widget : widgets){
			if(widget.isEditable()){
				FormRunnerView.updateCalcWidgetMapping(widget, calcQtnMappings, calcWidgetMap);
			}
		}
	}

	public boolean isAnyWidgetVisible(){
		if(isRepeated){
			for(int row = 0; row < table.getRowCount(); row++){
				for(int col = 0; col < table.getCellCount(row)-1; col++){
					RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)table.getWidget(row, col);
					if(widget.isVisible() && widget.isFocusable()){
						return true;
					}
				}
			}
		}
		else{
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if(widget.isVisible() && widget.isFocusable()){
					return true;
				}
			}
		}

		return false;
	}

	public int getHeaderHeight(){
		if(isRepeated)
			return 0;

		RuntimeWidgetWrapper headerLabel = (RuntimeWidgetWrapper)selectedPanel.getWidget(0);
		return headerLabel.getHeightInt();
	}

	public void onWidgetHidden(RuntimeWidgetWrapper widget, int decrement){
		Widget parent = getParent().getParent();
		if(parent instanceof RuntimeGroupWidget){
			RuntimeGroupWidget groupWidget = (RuntimeGroupWidget)parent;
			RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)groupWidget.getParent().getParent();
			groupWidget.onWidgetHidden(wrapper, decrement);
		}

		int bottomYpos = widget.getTopInt();

		if(!isRepeated){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				RuntimeWidgetWrapper currentWidget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if(currentWidget == widget)
					continue;

				int top = currentWidget.getTopInt();
				if(top >= bottomYpos)
					currentWidget.setTopInt(top - decrement);
			}
		}
	}

	public void onWidgetShown(RuntimeWidgetWrapper widget, int increment){
		Widget parent = getParent().getParent();
		if(parent instanceof RuntimeGroupWidget){
			RuntimeGroupWidget groupWidget = (RuntimeGroupWidget)parent;
			RuntimeWidgetWrapper wrapper = (RuntimeWidgetWrapper)groupWidget.getParent().getParent();
			groupWidget.onWidgetShown(wrapper, increment);
		}

		int bottomYpos = widget.getTopInt();

		if(!isRepeated){
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++){
				RuntimeWidgetWrapper currentWidget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if(currentWidget == widget)
					continue;

				int top = currentWidget.getTopInt();
				if(top >= bottomYpos)
					currentWidget.setTopInt(top + increment);
			}
		}
	}
	
	public void onValidationFailed(ValidationRule validationRule){
		if(btnAdd != null && hasEqualOperator(validationRule))
			btnAdd.setEnabled(validationRule.getMaxValue(formDef) > table.getRowCount());
	}
	
	public void onValidationPassed(ValidationRule validationRule){
		if(btnAdd != null && hasEqualOperator(validationRule))
			btnAdd.setEnabled(false);
	}
	
	private boolean hasEqualOperator(ValidationRule validationRule){
		return validationRule.getConditionAt(0).getOperator() == ModelConstants.OPERATOR_EQUAL;
	}
	
	private boolean hasDefaultValue(Element node) {
		return hasDefaultWithFalseValue(node) /*|| hasDefaultWithFalseValue((Element)node.getParentNode())*/;
	}
	
	private boolean hasDefaultWithFalseValue(Element node){
		if(node != null && "false()".equals(node.getAttribute("default")))
			return true;
		
		return false;
	}
	
	@Override
	public void onBrowserEvent(Event event) {
		//if(locked || readOnly){
			event.preventDefault();
			event.stopPropagation();
		//}

		/*if(widget instanceof RadioButton && DOM.eventGetType(event) == Event.ONMOUSEUP){
			if(((RadioButton)widget).getValue() == true){
				event.stopPropagation();
				event.preventDefault();
				((RadioButton)widget).setValue(false);
				return;
			}
		}*/
	}
	
	
	//TODO refactor these duplicate copy methods below
	/**
	 * Copies from a given label map to our class level one.
	 * 
	 * @param labelMap the label map to copy from.
	 */
	private void copyLabelMap(HashMap<QuestionDef,List<Label>> labelMap){
		Iterator<Entry<QuestionDef,List<Label>>> iterator = labelMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<QuestionDef,List<Label>> entry = iterator.next();

			List<Label> labels = this.labelMap.get(entry.getKey());
			if(labels == null)
				this.labelMap.put(entry.getKey(), entry.getValue());
			else
				labels.addAll(entry.getValue());
		}
	}

	private void copyCalcWidgetMap(HashMap<QuestionDef,List<RuntimeWidgetWrapper>> calcWidgetMap){
		Iterator<Entry<QuestionDef,List<RuntimeWidgetWrapper>>> iterator = calcWidgetMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<QuestionDef,List<RuntimeWidgetWrapper>> entry = iterator.next();

			List<RuntimeWidgetWrapper> widgets = this.calcWidgetMap.get(entry.getKey());
			if(widgets == null)
				this.calcWidgetMap.put(entry.getKey(), entry.getValue());
			else
				widgets.addAll(entry.getValue());
		}
	}

	private void copyFiltDynOptWidgetMap(HashMap<QuestionDef,RuntimeWidgetWrapper> filtDynOptWidgetMap){
		Iterator<Entry<QuestionDef,RuntimeWidgetWrapper>> iterator = filtDynOptWidgetMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<QuestionDef,RuntimeWidgetWrapper> entry = iterator.next();
			this.filtDynOptWidgetMap.put(entry.getKey(), entry.getValue()); //TODO Can it affect more than one.
		}
	}

	/**
	 * Copies from a given label text map to our class level one.
	 * 
	 * @param labelText the label text map to copy from.
	 */
	private void copyLabelText(HashMap<Label,String> labelText){
		Iterator<Entry<Label,String>> iterator = labelText.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Label,String> entry = iterator.next();
			this.labelText.put(entry.getKey(), entry.getValue());
		}
	}


	/**
	 * Copies from a given label replace text map to our class level one.
	 * 
	 * @param labelReplaceText the label replace text map to copy from.
	 */
	private void copyLabelReplaceText(HashMap<Label,String> labelReplaceText){
		Iterator<Entry<Label,String>> iterator = labelReplaceText.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Label,String> entry = iterator.next();
			this.labelReplaceText.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Copies from a given check box group map to our class level one.
	 * 
	 * @param labelMap the check box group map to copy from.
	 */
	private void copyCheckBoxGroupMap(HashMap<QuestionDef,List<CheckBox>> labelMap){
		Iterator<Entry<QuestionDef,List<CheckBox>>> iterator = labelMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<QuestionDef,List<CheckBox>> entry = iterator.next();

			List<CheckBox> checkboxes = this.checkBoxGroupMap.get(entry.getKey());
			if(checkboxes == null)
				this.checkBoxGroupMap.put(entry.getKey(), entry.getValue());
			else
				checkboxes.addAll(entry.getValue());
		}
	}
	
	protected void executeRecordOperation(String binding) {
		if (binding.equalsIgnoreCase("firstRecord")) {
			if (!isValid(true)) {
				getInvalidWidget().setFocus();
				return;
			}
			
			saveCurrentRecordValues();
			currentRecordIndex = 0;
			loadRecordValues();
			setNavigationButtonStatus();
		}
		else if (binding.equalsIgnoreCase("prevRecord")) {
			if (!isValid(true)) {
				getInvalidWidget().setFocus();
				return;
			}
			
			saveCurrentRecordValues();
			currentRecordIndex--;
			loadRecordValues();
			setNavigationButtonStatus();
		}
		else if (binding.equalsIgnoreCase("nextRecord")) {
			if (!isValid(true)) {
				getInvalidWidget().setFocus();
				return;
			}
			
			saveCurrentRecordValues();
			currentRecordIndex++;
			loadRecordValues();
			setNavigationButtonStatus();
		}
		else if (binding.equalsIgnoreCase("lastRecord")) {
			if (!isValid(true)) {
				getInvalidWidget().setFocus();
				return;
			}
			
			saveCurrentRecordValues();
			currentRecordIndex = records.size() - 1;
			loadRecordValues();
			setNavigationButtonStatus();
		}
		else if (binding.equalsIgnoreCase("newRecord")) {
			if (!isValid(true)) {
				getInvalidWidget().setFocus();
				return;
			}
			
			saveCurrentRecordValues();
			clearInputValues();
			setFocus();
			records.add(new HashMap<String, Object>());
			currentRecordIndex = records.size() - 1;
			setNavigationButtonStatus();
		}
		else if (binding.equalsIgnoreCase("deleteRecord")) {
			if (Window.confirm("Do you really want to delete this record?")) {
				records.remove(currentRecordIndex);
				if (currentRecordIndex == 0) {
					if (records.size() == 0) {
						records.add(new HashMap<String, Object>());
					}
				}
				else  {
					--currentRecordIndex;
				}
				
				clearInputValues();
				loadRecordValues();
				setNavigationButtonStatus();
			}
		}
	}
	
	private void buildWidgetBindingMap() {
		if (widgetBindingMap.size() == 0) {
			for(int index = 0; index < selectedPanel.getWidgetCount(); index++) {
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
				if (widget.isEditable() || widget.getWrappedWidget() instanceof RuntimeGroupWidget) {
					String binding = widget.getBinding();
					widgetBindingMap.put(binding, widget);
				}
			}
		}
	}
	protected void loadRecordValues() {
		buildWidgetBindingMap();
		
		HashMap<String, Object> map = records.get(currentRecordIndex);
		for (Entry<String, Object> entry : map.entrySet()) {
			RuntimeWidgetWrapper widget = widgetBindingMap.get(entry.getKey());
			if (widget.getWrappedWidget() instanceof RuntimeGroupWidget) {
				((RuntimeGroupWidget)widget.getWrappedWidget()).setRecords((List<HashMap<String, Object>>)entry.getValue());
				//((RuntimeGroupWidget)widget.getWrappedWidget()).loadRecordValues();
			} else {
				widget.setAnswer(entry.getValue() != null ? entry.getValue().toString() : null);
			}
		}
	}
	
	protected void saveCurrentRecordValues() {
		for(int index = 0; index < selectedPanel.getWidgetCount(); index++) {
			RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(index);
			/*if (!widget.isEditable() && !(widget.getWrappedWidget() instanceof RuntimeGroupWidget))
				continue;
			
			if (widget.getWrappedWidget() instanceof RuntimeGroupWidget)
				((RuntimeGroupWidget)widget.getWrappedWidget()).saveCurrentRecordValues();
			else*/
				widget.saveValue(formDef);
		}
		
		buildWidgetBindingMap();
		
		HashMap<String, Object> map = records.get(currentRecordIndex);
		for (int index = 0; index < groupQtnsDef.getQuestionsCount(); index++) {
			QuestionDef qtnDef = groupQtnsDef.getQuestionAt(index);
			
			if (qtnDef.getDataType() == QuestionDef.QTN_TYPE_SUBFORM) {
				RuntimeWidgetWrapper widget = widgetBindingMap.get(qtnDef.getBinding());
				map.put(qtnDef.getBinding(), ((RuntimeGroupWidget)widget.getWrappedWidget()).getRecords());
			} 
			else {
				map.put(qtnDef.getBinding(), qtnDef.getAnswer());
			}
		}
	}
	
	protected void saveAllRecordValues(Element paramRepeatDataNode) {
		if (groupQtnsDef.getQtnDef().getDataNode() == null) {
			Window.alert(LocaleText.get("repeatChildDataNodeNotFound"));
			return; //possibly form not yet saved
		}
		
		saveCurrentRecordValues(); //just in case we have just added new or changed existing record
		
		int prevIndex = currentRecordIndex;
		
		currentRecordIndex = 0;
		loadRecordValues();
		saveValues();
		
		String parentBinding = groupQtnsDef.getQtnDef().getBinding();
		for (int index = 1; index < records.size(); index++) {
			currentRecordIndex = index;
			loadRecordValues();
			
			Element repeatDataNode = paramRepeatDataNode; //groupQtnsDef.getQtnDef().getDataNode();
			Element newRepeatDataNode = (Element)repeatDataNodeClone.cloneNode(true);
			repeatDataNode.getParentNode().appendChild(newRepeatDataNode);
			dataNodes.add(newRepeatDataNode);
			
			for(int i = 0; i < selectedPanel.getWidgetCount(); i++) {
				RuntimeWidgetWrapper widget = (RuntimeWidgetWrapper)selectedPanel.getWidget(i);
				if (!(widget.isEditable() || widget.getWrappedWidget() instanceof RuntimeGroupWidget)) {
					continue;
				}
				
				QuestionDef qtnDef = widget.getQuestionDef();
				widget.setQuestionDef(new QuestionDef(qtnDef, qtnDef.getParent()), false);
				setDataNode(widget, newRepeatDataNode, widget.getBinding(), false, parentBinding);
				
				if (widget.getWrappedWidget() instanceof RuntimeGroupWidget) {
					((RuntimeGroupWidget)widget.getWrappedWidget()).saveAllRecordValues(widget.getQuestionDef().getDataNode());
				}
				else {
					widget.saveValue(formDef);
				}
				
				widget.setQuestionDef(qtnDef, false);
			}
		}
		
		currentRecordIndex = prevIndex;
		loadRecordValues();
	}
	
	private void setNavigationButtonStatus() {
		if (btnFirstRecord != null) {
			btnFirstRecord.setEnabled(currentRecordIndex != 0);
		}
		
		if (btnPrevRecord != null) {
			btnPrevRecord.setEnabled(currentRecordIndex != 0);
		}
		
		if (btnNextRecord != null) {
			btnNextRecord.setEnabled(currentRecordIndex != records.size() - 1);
		}
		
		if (btnLastRecord != null) {
			btnLastRecord.setEnabled(currentRecordIndex != records.size() - 1);
		}
		
		if (lblRecordNavigation != null) {
			lblRecordNavigation.setText(currentRecordIndex + 1 + " of " + records.size());
		}
	}
	
	public void setRecords(List<HashMap<String, Object>> records) {
		this.records = records;
		this.currentRecordIndex = 0;
		loadRecordValues();
		setNavigationButtonStatus();
	}
	
	public List<HashMap<String, Object>> getRecords() {
		saveCurrentRecordValues();
		return records;
	}
}