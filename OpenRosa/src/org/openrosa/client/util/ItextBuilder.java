package org.openrosa.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.openrosa.client.Context;
import org.openrosa.client.model.FormDef;
import org.openrosa.client.model.ItextModel;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XmlUtil;
import org.purc.purcforms.client.xpath.XPathExpression;

import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;


/**
 * Builds an itext block in an xforms document.
 * 
 * @author daniel
 *
 */
public class ItextBuilder {

	public static void updateItextBlock(Document doc, FormDef formDef, ListStore<ItextModel> list, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){
		/*List<Locale> locales = Context.getLocales();
		if(locales == null)
			return;

		for(Locale locale : locales)
			updateItextBlock(doc,formDef,list,locale);*/

		updateItextBlock(doc,formDef,list,Context.getLocale(),formAttrMap,itextMap);
	}


	public static void updateItextBlock(Document doc, FormDef formDef, ListStore<ItextModel> list, Locale locale, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){

		Element modelNode = XmlUtil.getNode(doc.getDocumentElement(),"model");
		assert(modelNode != null); //we must have a model in an xform.
		Element itextNode = XmlUtil.getNode(modelNode,"itext");
		if(itextNode == null){
			itextNode = doc.createElement("itext");
			modelNode.appendChild(itextNode);
		}
		else{
			NodeList translations = itextNode.getElementsByTagName("translation");

			for(int index = 0; index < translations.getLength(); index++){
				Node node = translations.item(index);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				if(((Element)node).getAttribute("lang").equalsIgnoreCase(locale.getName())){
					itextNode.removeChild(node);
					break;
				}
			}
		}

		Element translationNode = doc.createElement("translation");
		translationNode.setAttribute("lang", locale.getName());
		itextNode.appendChild(translationNode);

		//Map for detecting duplicates in itext. eg if id yes=Yes , we should not have information more than once.
		HashMap<String,String> duplicatesMap = new HashMap<String, String>();
		
		Element rootNode = formDef.getLanguageNode();
		NodeList nodes = rootNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node node = nodes.item(index);
			if(node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			/*Element textNode = doc.createElement("text");
			translationNode.appendChild(textNode);*/

			String value = ((Element)node).getAttribute("value");
			String id = FormDesignerUtil.getXmlTagName(value);
			/*textNode.setAttribute("id", id);

			Element valueNode = doc.createElement("value");
			textNode.appendChild(valueNode);

			valueNode.appendChild(doc.createTextNode(value));*/

			String xpath = ((Element)node).getAttribute("xpath");
			Vector result = new XPathExpression(doc, xpath).getResult();
			if(result != null && result.size() > 0){
				Element targetNode = (Element)result.get(0);

				int pos = xpath.lastIndexOf('@');
				if(pos > 0 && xpath.indexOf('=',pos) < 0){
					//String attributeName = xpath.substring(pos + 1, xpath.indexOf(']',pos));
					//targetNode.setAttribute(attributeName, value);
					
					//xpath = FormUtil.getNodePath(formDef.getPageAt(0).getGroupNode());
					
					NodeList titles = doc.getElementsByTagName("title");
					if(titles != null && titles.getLength() > 0){
						xpath = FormUtil.getNodePath(titles.item(0));
						((Element)titles.item(0)).setAttribute("ref", "jr:itext('" + id + "')");
					}
					else
						continue;
				}
				else{
					targetNode.setAttribute("ref", "jr:itext('" + id + "')");

					//remove text.
					removeAllChildNodes(targetNode);
				}

				assert(result.size() == 1); //each xpath expression should point to not more than one node.

				/*ItextModel itextModel = new ItextModel();
				itextModel.set("xpath", xpath);
				itextModel.set("id", id);
				itextModel.set(localeKey, value);
				list.add(itextModel);*/

				//Skip the steps below if we have already processed this itext id.
				if(duplicatesMap.containsKey(id))
					continue;
				else
					duplicatesMap.put(id, id);
				
				addTextNode(doc,translationNode, list,xpath,id,value,locale.getKey(),formAttrMap,itextMap);
			}
			else if(index == 0){
				//NodeList titles = doc.getElementsByTagName("title");
				//if(titles != null && titles.getLength() > 0)
				//	addTextNode(doc,translationNode, list,FormUtil.getNodePath(titles.item(0)),id,value,locale.getKey());
			}
		}
	}

	/**
	 * Creates a new itext node with its text value and a gxt grid model item for a given 
	 * xforms documet's translation node.
	 * 
	 * @param doc the xfrorms document.
	 * @param translationNode the translation node.
	 * @param list the gxt grid model list.
	 * @param xpath the xpath expression pointing to the node text that this itext represents.
	 * @param id the itext id.
	 * @param value the itext value of the given id.
	 * @param localeKey the locale key
	 */
	private static void addTextNode(Document doc, Element translationNode, ListStore<ItextModel> list, String xpath, String id, String value, String localeKey, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){
		if(value.trim().length() == 0)
			return;
		
		Element textNode = doc.createElement("text");
		translationNode.appendChild(textNode);

		//String value = ((Element)node).getAttribute("value");
		//String id = FormDesignerUtil.getXmlTagName(value);
		textNode.setAttribute("id", id);

		Element valueNode = doc.createElement("value");
		textNode.appendChild(valueNode);

		valueNode.appendChild(doc.createTextNode(value));

		String fullId = id;
		String formAttr = formAttrMap.get(id);
		if(formAttr != null)
			fullId = id + ";" + formAttr;
		
		ItextModel itextModel = itextMap.get(fullId);
		if(itextModel == null){
			itextModel = new ItextModel();
			itextMap.put(fullId, itextModel);
		}
		
		itextModel.set("xpath", xpath);
		itextModel.set("id", fullId);
		itextModel.set(localeKey, value);
		list.add(itextModel);
	}

	
	/**
	 * Removes all child nodes for a give  node.
	 * 
	 * @param node the node whose child nodes to remove.
	 */
	private static void removeAllChildNodes(Element node){
		while(node.getChildNodes().getLength() > 0)
			node.removeChild(node.getChildNodes().item(0));
	}


	/**
	 * Updates an xforms document itext block with values as edited by the user from a grid.
	 * 
	 * @param doc the xforms document.
	 * @param formDef the form definition object.
	 * @param list the gxt grid itext model.
	 */
	public static void updateItextBlock(Document doc, FormDef formDef, List<ItextModel> list, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){
		List<Locale> locales = Context.getLocales();
		if(locales == null)
			return;

		for(Locale locale : locales)
			updateItextBlock(doc,formDef,list,locale,formAttrMap,itextMap);
	}

	
	/**
	 * Updates an xforms document itext block with values as edited by the user from a grid for a given locale.
	 * 
	 * @param doc the xforms document.
	 * @param formDef the form definition object.
	 * @param list the gxt grid itext model.
	 * @param locale the locale.
	 */
	private static void updateItextBlock(Document doc, FormDef formDef, List<ItextModel> list, Locale locale, HashMap<String,String> formAttrMap, HashMap<String,ItextModel> itextMap){

		Element modelNode = XmlUtil.getNode(doc.getDocumentElement(),"model");
		assert(modelNode != null); //we must have a model in an xform.
		Element itextNode = XmlUtil.getNode(modelNode,"itext");
		if(itextNode == null){
			itextNode = doc.createElement("itext");
			modelNode.appendChild(itextNode);
		}
		else{
			NodeList translations = itextNode.getElementsByTagName("translation");

			for(int index = 0; index < translations.getLength(); index++){
				Node node = translations.item(index);
				if(node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				if(((Element)node).getAttribute("lang").equalsIgnoreCase(locale.getName())){
					itextNode.removeChild(node);
					break;
				}
			}
		}

		//Map for detecting duplicates in itext. eg if id yes=Yes , we should not have information more than once.
		HashMap<String,String> duplicatesMap = new HashMap<String, String>();

		Element translationNode = doc.createElement("translation");
		translationNode.setAttribute("lang", locale.getName());
		itextNode.appendChild(translationNode);

		//we shold also have alist of those that were duplicates
		for(ItextModel itext : list){
			
			String id = itext.get("id");
			String value = itext.get(locale.getKey());
			
			//Do not create a duplicate itext element if we have already processed this itext id.
			if(!duplicatesMap.containsKey(id)){
				Element textNode = doc.createElement("text");
				translationNode.appendChild(textNode);

				textNode.setAttribute("id", id);
				
				Element valueNode = doc.createElement("value");
				textNode.appendChild(valueNode);
				valueNode.appendChild(doc.createTextNode(value));
			}
			else
				duplicatesMap.put(id, id);

			String xpath = (String)itext.get("xpath");
			Vector result = new XPathExpression(doc, xpath).getResult();
			if(result != null && result.size() > 0){
				Element targetNode = (Element)result.get(0);
				targetNode.setAttribute("ref", "jr:itext('" + id + "')");
				assert(result.size() == 1); //each xpath expression should point to not more than one node.

				if(locale.getKey().equalsIgnoreCase(Context.getDefaultLocale().getKey()))
					XmlUtil.setTextNodeValue(targetNode, value);
			}
		}
	}
}
