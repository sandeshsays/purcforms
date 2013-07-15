package org.purc.purcforms.client.xforms;

import java.util.List;
import java.util.Map;

import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.GroupQtnsDef;
import org.purc.purcforms.client.model.OptionDef;
import org.purc.purcforms.client.model.QuestionDef;
import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/**
 * Builds xforms UI elements like input, select, select1, etc. 
 * from question definition objects of the form model.
 * 
 * @author daniel
 *
 */
public class UiElementBuilder {

	/**
	 * All methods in this class are static and hence we expect no external
	 * Instantiation of this class.
	 */
	private UiElementBuilder(){

	}
	
	private static String setBindNodeProperties(Element bindNode, QuestionDef qtn, String parentBinding, FormDef formDef){
		String nodeset = qtn.getBinding();
		String id = qtn.getBinding();
		if(!(id.contains("/") && qtn.getBindNode() != null)){
			id = XformBuilderUtil.getBindIdFromVariableName(qtn.getBinding(),false);
			
			if(parentBinding != null && !qtn.getBinding().contains("/"))
				nodeset = "/" + formDef.getBinding() + "/" + parentBinding + "/" + qtn.getBinding();
			
			if(!nodeset.startsWith("/"))
				nodeset = "/" + nodeset;
			
			if(!nodeset.startsWith("/" + formDef.getBinding() + "/"))
				nodeset = "/" + formDef.getBinding() + "/" + qtn.getBinding();
		}
		else{
			id = qtn.getBindNode().getAttribute(XformConstants.ATTRIBUTE_NAME_ID);
			nodeset = qtn.getBindNode().getAttribute(XformConstants.ATTRIBUTE_NAME_NODESET);
			
			//Without this, converting from purcforms to JR format may produce attributes
			//like bind=""
			if((id == null || id.trim().length() == 0) && qtn.getControlNode() != null){
				id = qtn.getControlNode().getAttribute(XformConstants.ATTRIBUTE_NAME_BIND);
				nodeset = "/" + formDef.getBinding() + "/" + qtn.getBinding();
			}
			else if((id == null || id.trim().length() == 0) && qtn.getControlNode() == null){
				id = FormUtil.getXmlTagName(qtn.getBinding());
				nodeset = "/" + formDef.getBinding() + "/" + qtn.getBinding();
			}
		}
		
		bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, id);
		bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_NODESET, nodeset);

		if(!qtn.isGroupQtnsDef())
			bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_TYPE, XformBuilderUtil.getXmlType(qtn.getDataType(),bindNode));	
		if(qtn.isRequired())
			bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_REQUIRED, XformConstants.XPATH_VALUE_TRUE);
		if(!qtn.isEnabled())
			bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_READONLY, XformConstants.XPATH_VALUE_TRUE);
		if(qtn.isLocked())
			bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_LOCKED, XformConstants.XPATH_VALUE_TRUE);
		if(!qtn.isVisible())
			bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_VISIBLE, XformConstants.XPATH_VALUE_FALSE);
		
		//Add extended properties if any for exclusive option
		if (qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
			Object exclusiveOption = formDef.getExtentendProperty(qtn, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
			if(exclusiveOption != null) {
				bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION, exclusiveOption.toString());
			}
			
			Object exclusiveQuestion = formDef.getExtentendProperty(qtn, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION);
			if(exclusiveQuestion != null) {
				bindNode.setAttribute(XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_QUESTION, exclusiveQuestion.toString());
			}
		}
				
		return id;
	}
	
	
	/**
	 * Converts a question definition object to xforms.
	 * 
	 * @param qtn the question definition object.
	 * @param doc the xforms document.
	 * @param xformsNode the root node of the xforms document.
	 * @param formDef the form definition object to which the question belongs.
	 * @param formNode the xforms instance data node.
	 * @param modelNode the xforms model node.
	 * @param groupNode the xforms group node to which the question belongs.
	 */
	public static void fromQuestionDef2Xform(QuestionDef qtn, Document doc, Element xformsNode, FormDef formDef, Element formNode, Element modelNode,Element groupNode, boolean rebuild){
		Element dataNode = qtn.getDataNode();
		if(dataNode == null || rebuild){
			dataNode =  XformBuilderUtil.fromVariableName2Node(doc,qtn.getBinding(),formDef,formNode);
			if(qtn.getDefaultValue() != null && qtn.getDefaultValue().trim().length() > 0)
				dataNode.appendChild(doc.createTextNode(qtn.getDefaultValue()));
			qtn.setDataNode(dataNode);
		}

		String bindAttributeName = XformConstants.ATTRIBUTE_NAME_REF;
		Element bindNode = qtn.getBindNode();
		String id = qtn.getBinding();
		if(bindNode == null || rebuild){
			bindNode =  doc.createElement(XformConstants.NODE_NAME_BIND);
			if(!groupNode.getNodeName().equals(XformConstants.NODE_NAME_REPEAT)){
				modelNode.appendChild(bindNode);
				qtn.setBindNode(bindNode);
			}	
			
			id = setBindNodeProperties(bindNode, qtn, null, formDef);
		}
		else{
			if(id != null && !(id.contains("/")))
				id = XformBuilderUtil.getBindIdFromVariableName(qtn.getBinding(),false);
			else
				id = qtn.getBindNode().getAttribute(XformConstants.ATTRIBUTE_NAME_ID);
		}
			
		
		if(!groupNode.getNodeName().equals(XformConstants.NODE_NAME_REPEAT)){
			bindAttributeName = XformConstants.ATTRIBUTE_NAME_BIND;
		}
		

		Element uiNode =  getXformUIElement(doc,qtn,bindAttributeName, false, id);
		if(groupNode != null) //Some forms may not be in groups
			groupNode.appendChild(uiNode);
		else
			xformsNode.appendChild(uiNode);

		qtn.setControlNode(uiNode);

		Element labelNode =  doc.createElement(XformConstants.NODE_NAME_LABEL);
		labelNode.appendChild(doc.createTextNode(qtn.getText()));
		uiNode.appendChild(labelNode);
		qtn.setLabelNode(labelNode);

		addHelpTextNode(qtn,doc,uiNode,null);

		if(!qtn.isGroupQtnsDef()){
			if(qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
				qtn.setFirstOptionNode(ItemsetBuilder.createDynamicOptionDefNode(doc,uiNode));
			else{
				List options = qtn.getOptions();
				if(options != null && options.size() > 0){
					
					Map<String, String> extendedOptionsMap = null;
					if (qtn.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
						extendedOptionsMap = (Map<String, String>)formDef.getExtentendProperty(qtn, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
					}
					
					for(int j=0; j<options.size(); j++){
						OptionDef optionDef = (OptionDef)options.get(j);
						Element itemNode = fromOptionDef2Xform(formDef, qtn, optionDef, doc, uiNode, (extendedOptionsMap != null ? extendedOptionsMap.get(optionDef.getBinding()) : null));	
						if(j == 0)
							qtn.setFirstOptionNode(itemNode);
					}
				}
			}
		}
		else if(qtn.getDataType() == QuestionDef.QTN_TYPE_REPEAT) {
			Element repeatNode =  doc.createElement(XformConstants.NODE_NAME_REPEAT);
			repeatNode.setAttribute(XformConstants.ATTRIBUTE_NAME_BIND, id);
			uiNode.appendChild(repeatNode);
			qtn.setControlNode(repeatNode);

			GroupQtnsDef rptQtns = qtn.getGroupQtnsDef();
			for(int j=0; j<rptQtns.size(); j++)
				createQuestion(qtn, rptQtns.getQuestionAt(j), repeatNode, dataNode, modelNode, formDef, doc);
		}
		else if(qtn.getDataType() == QuestionDef.QTN_TYPE_GROUP) {
			qtn.setControlNode(uiNode);

			GroupQtnsDef grpQtns = qtn.getGroupQtnsDef();
			for(int j=0; j<grpQtns.size(); j++)
				createQuestion(qtn, grpQtns.getQuestionAt(j), uiNode, dataNode, modelNode, formDef, doc);
		}
	}


	/**
	 * Creates an xforms ui node for a child question of a parent repeat question type.
	 * 
	 * @param parentQtn is the parent question.
	 * @param qtnDef the child question definition object.
	 * @param parentControlNode the ui node of the parent repeat question.
	 * @param parentDataNode the data node of the parent repeat question.
	 * @param modelNode the model node
	 * @param doc the xforms document.
	 */
	private static void createQuestion(QuestionDef parentQtn, QuestionDef qtnDef, Element parentControlNode, Element parentDataNode, Element modelNode, FormDef formDef, Document doc){
		String name = qtnDef.getBinding();
		
		//TODO Doesnt this introduce a bug?
		int pos = qtnDef.getBinding().lastIndexOf('/');
		if(pos > 0)
			name = qtnDef.getBinding().substring(pos + 1);

		//TODO Should do this for all invalid characters in node names.
		name = name.replace("/", "");
		name = name.replace("\\", "");
		name = name.replace(" ", "");

		Element dataNode =  doc.createElement(name);
		if(qtnDef.getDefaultValue() != null && qtnDef.getDefaultValue().trim().length() > 0)
			dataNode.appendChild(doc.createTextNode(qtnDef.getDefaultValue()));
		parentDataNode.appendChild(dataNode);
		qtnDef.setDataNode(dataNode);

		
		//.....................
		Element bindNode =  doc.createElement(XformConstants.NODE_NAME_BIND);
		modelNode.appendChild(bindNode);
		String id = setBindNodeProperties(bindNode, qtnDef, parentQtn.getBinding(), formDef);
		
		Element inputNode =  getXformUIElement(doc,qtnDef,XformConstants.ATTRIBUTE_NAME_BIND, true, id);
		
		parentControlNode.appendChild(inputNode);
		qtnDef.setControlNode(inputNode);
		qtnDef.setBindNode(bindNode);

		Element labelNode =  doc.createElement(XformConstants.NODE_NAME_LABEL);
		labelNode.appendChild(doc.createTextNode(qtnDef.getText()));
		inputNode.appendChild(labelNode);
		qtnDef.setLabelNode(labelNode);

		addHelpTextNode(qtnDef,doc,inputNode,null);

		if(!qtnDef.isGroupQtnsDef()){
			List options = qtnDef.getOptions();
			if(options != null && options.size() > 0){
				
				Map<String, String> extendedOptionsMap = null;
				if (qtnDef.getDataType() == QuestionDef.QTN_TYPE_LIST_MULTIPLE) {
					extendedOptionsMap = (Map<String, String>)formDef.getExtentendProperty(qtnDef, XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTIONS);
				}
				
				for(int index=0; index<options.size(); index++){
					OptionDef optionDef = (OptionDef)options.get(index);
					Element itemNode = fromOptionDef2Xform(formDef, qtnDef, optionDef, doc, inputNode, (extendedOptionsMap != null ? extendedOptionsMap.get(optionDef.getBinding()) : null));	
					if(index == 0)
						qtnDef.setFirstOptionNode(itemNode);
				}
			}
		}
		else if(qtnDef.getDataType() == QuestionDef.QTN_TYPE_REPEAT) {
			Element repeatNode =  doc.createElement(XformConstants.NODE_NAME_REPEAT);
			repeatNode.setAttribute(XformConstants.ATTRIBUTE_NAME_BIND, id);
			inputNode.appendChild(repeatNode);
			qtnDef.setControlNode(repeatNode);

			GroupQtnsDef rptQtns = qtnDef.getGroupQtnsDef();
			for(int j=0; j<rptQtns.size(); j++)
				createQuestion(qtnDef, rptQtns.getQuestionAt(j), repeatNode, dataNode, modelNode, formDef, doc);
		}
		else if(qtnDef.getDataType() == QuestionDef.QTN_TYPE_GROUP) {
			GroupQtnsDef grpQtns = qtnDef.getGroupQtnsDef();
			for(int j=0; j<grpQtns.size(); j++)
				createQuestion(qtnDef, grpQtns.getQuestionAt(j), inputNode, dataNode, modelNode, formDef, doc);
		}
	}


	/**
	 * Gets the xforms ui node for a given question definition object.
	 * 
	 * @param doc the xforms document.
	 * @param qtnDef the question definition object.
	 * @param bindAttributeName the attribute name for binding. Could be "bind" or "ref".
	 * @param isRepeatKid set to true if this question is a child of another repeat question type.
	 * @return the xforms ui node.
	 */
	private static Element getXformUIElement(Document doc, QuestionDef qtnDef, String bindAttributeName, boolean isRepeatKid, String id){

		String name = XformConstants.NODE_NAME_INPUT;

		int type = qtnDef.getDataType();
		if(type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC)
			name = XformConstants.NODE_NAME_SELECT1;
		else if(type == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
			name = XformConstants.NODE_NAME_SELECT;
		else if(qtnDef.isGroupQtnsDef())
			name = XformConstants.NODE_NAME_GROUP;
		else if(type == QuestionDef.QTN_TYPE_IMAGE || type == QuestionDef.QTN_TYPE_AUDIO || type == QuestionDef.QTN_TYPE_VIDEO)
			name = XformConstants.NODE_NAME_UPLOAD;

		if(id == null)
			id = XformBuilderUtil.getBindIdFromVariableName(qtnDef.getBinding(), isRepeatKid);
		
		Element node = doc.createElement(name);
		if(!qtnDef.isGroupQtnsDef())
			node.setAttribute(bindAttributeName, id);
		else
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, qtnDef.getBinding());

		//Make ODK happy.
		setMediaType(node, type);
		
		//if(type == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE || type == QuestionDef.QTN_TYPE_LIST_MULTIPLE)
		//	node.setAttribute("selection", "closed");

		return node;
	}
	
	public static void setMediaType(Element node, int type){
		String mediatype = null;
		if(type == QuestionDef.QTN_TYPE_IMAGE)
			mediatype = XformConstants.ATTRIBUTE_VALUE_IMAGE;
		else if(type == QuestionDef.QTN_TYPE_AUDIO)
			mediatype = XformConstants.ATTRIBUTE_VALUE_AUDIO;
		else if(type == QuestionDef.QTN_TYPE_VIDEO)
			mediatype = XformConstants.ATTRIBUTE_VALUE_VIDEO;
		
		if(mediatype != null)
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_MEDIATYPE, mediatype + "/*");
	}


	/**
	 * Converts an option definition object to xforms.
	 * 
	 * @param optionDef the option definition object.
	 * @param doc the xforms document.
	 * @param uiNode the xforms ui node of the question to which this option belongs.
	 * @return the item node of the option definition object.
	 */
	public static Element fromOptionDef2Xform(FormDef formDef, QuestionDef questionDef, OptionDef optionDef, Document doc, Element uiNode, String extendedOption){
		Element itemNode =  doc.createElement(XformConstants.NODE_NAME_ITEM);
		itemNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, optionDef.getBinding());

		Element node =  doc.createElement(XformConstants.NODE_NAME_LABEL);
		node.appendChild(doc.createTextNode(optionDef.getText()));
		itemNode.appendChild(node);
		optionDef.setLabelNode(node);

		node =  doc.createElement(XformConstants.NODE_NAME_VALUE);
		node.appendChild(doc.createTextNode(optionDef.getBinding()));
		itemNode.appendChild(node);
		optionDef.setValueNode(node);

		uiNode.appendChild(itemNode);
		optionDef.setControlNode(itemNode);
		
		if (extendedOption != null)
			itemNode.setAttribute(XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION, extendedOption);
		else
			itemNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_EXCLUSIVE_OPTION);
		
		return itemNode;
	}


	/**
	 * Sets the xforms help text or hint node for a question.
	 * 
	 * @param qtn the question definition object.
	 * @param doc the xforms document.
	 * @param inputNode the xforms ui node.
	 * @param firstOptionNode the first option node if a single or multiple select question type.
	 */
	public static void addHelpTextNode(QuestionDef qtn, Document doc, Element inputNode, Element firstOptionNode){
		String helpText = qtn.getHelpText();
		if(helpText != null && helpText.length() > 0){
			Element hintNode =  doc.createElement(XformConstants.NODE_NAME_HINT);
			hintNode.appendChild(doc.createTextNode(helpText));
			if(firstOptionNode == null)
				inputNode.appendChild(hintNode);
			else
				inputNode.insertBefore(hintNode, firstOptionNode);
			qtn.setHintNode(hintNode);
		}
	}
}
