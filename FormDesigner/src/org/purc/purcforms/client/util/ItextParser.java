package org.purc.purcforms.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.purc.purcforms.client.Context;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.xforms.XformConstants;
import org.purc.purcforms.client.xforms.XformUtil;
import org.purc.purcforms.client.xforms.XmlUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * Parses an xforms document and puts text in all nodes referencing an itext block for a given language.
 * The first language in the itext block is taken to be the current or default one.
 * 
 * @author daniel
 *
 */
public class ItextParser {

	/**
	 * A map of locale doc's xform node keyed by the locale key.
	 */
	private static HashMap<String, Element> localeXformNodeMap = new HashMap<String, Element>();

	private static HashMap<String, HashMap<String, String>> defaultTextMap = new HashMap<String, HashMap<String, String>>();


	/**
	 * Parses an xform and sets the text of various nodes based on the current a locale
	 * as represented by their itext ids. The translation element with the "default" attribute (default="") OR the first locale in the itext block
	 * (if no default attribute is found) is the one taken as the default.
	 * 
	 * @param xml the xforms xml.
	 * @param list the itext model which can be displayed in a gxt grid.
	 * @return the document where all itext refs are filled with text for a given locale.
	 */
	public static Document parse(String xml, int formId){
		localeXformNodeMap.clear();
		defaultTextMap.clear();

		Document doc = XmlUtil.getDocument(xml);
		
		Element modelNode = XformUtil.getModelNode(doc.getDocumentElement());
		if(modelNode != null){
			String prefix = modelNode.getPrefix();
			if(prefix != null && prefix.trim().length() > 0)
				XformConstants.updatePrefixConstants(prefix);
		}

		//Check if we have an itext block in this xform.
		NodeList nodes = doc.getElementsByTagName("itext");
		if(nodes == null || nodes.getLength() == 0)
			return doc;

		//Check if we have any translations in this itext block.
		nodes = ((Element)nodes.item(0)).getElementsByTagName("translation");
		if(nodes == null || nodes.getLength() == 0)
			return doc;

		List<Locale> locales = new ArrayList<Locale>(); //New list of locales as it comes form the parsed xform.
		HashMap<String,String> defaultText = null; //Map of default id and itext (for multiple values of the itext node) for the default language.

		HashMap<String, String> textMap = null;
		
		//Map of each locale key and map of its id and itext translations.
		for(int index = 0; index < nodes.getLength(); index++){
			Element translationNode = (Element)nodes.item(index);
			String lang = translationNode.getAttribute("lang");
			String langName = translationNode.getAttribute("lang-name");
			if(langName == null || langName.trim().length() == 0)
				langName = lang;

			HashMap<String, String> defText = new HashMap<String,String>();
			fillItextMap(translationNode,defText);

			if( (((Element)nodes.item(index)).getAttribute("default") != null || (index == 0 && Context.getLocale() == null)) 
					||(defaultText == null && Context.getLocale() != null && Context.getLocale().getKey().equals(lang))){
				defaultText = defText;
				Context.setLocale(new Locale(lang, langName));
				Context.setDefaultLocale(Context.getLocale());
			}

			defaultTextMap.put(lang, defText);

			//create a new locale object for the current translation.
			locales.add(new Locale(lang, langName));
			
			textMap = defText;
		}

		Context.setLocales(locales);

		//create a hash table of locale xform nodes keyed by locale.
		for(Locale locale : locales){
			Document localeDoc = XMLParser.createDocument();
			Element node = localeDoc.createElement(LanguageUtil.NODE_NAME_LANGUAGE_TEXT);
			node.setAttribute("lang", locale.getKey());
			localeDoc.appendChild(node);

			Element localeXformNode = localeDoc.createElement(LanguageUtil.NODE_NAME_XFORM);
			node.appendChild(localeXformNode);

			addLayoutLocaleText(doc, locale, localeXformNode);
			localeXformNodeMap.put(locale.getKey(), localeXformNode);
		}
			
		if(defaultText == null)
			defaultText = textMap;
		
		translateNodes("label", doc, defaultText);
		translateNodes("hint", doc, defaultText);
		translateNodes("title", doc, defaultText);
		translateNodes("bind", doc, defaultText);

		Context.getLanguageText().clear();


		HashMap<String,String> map = new HashMap<String,String>();
		for(Locale locale : locales){
			Element localeXformNode = localeXformNodeMap.get(locale.getKey());
			map.put(locale.getKey(), localeXformNode.getOwnerDocument().toString());
		}

		Context.getLanguageText().put(formId, map);

		return doc;
	}


	/**
	 * Fills a map of id and itext for a given locale as represented by a given translation node.
	 * 
	 * @param translationNode the translation node.
	 * @param itext the itext map.
	 */
	private static void fillItextMap(Element translationNode, HashMap<String,String> defaultText){
		NodeList nodes = translationNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node textNode = nodes.item(index);
			if(textNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			//itext.put(((Element)textNode).getAttribute("id"), getValueText(textNode));
			setValueText(((Element)textNode).getAttribute("id"), textNode, defaultText);
		}

	}

	/**
	 * Sets the text value of a node.
	 * 
	 * @param textNode the node.
	 * @return the text value.
	 */
	private static void setValueText(String id, Node textNode, HashMap<String,String> defaultText){
		String defaultValue = null, longValue = null, shortValue = null;

		NodeList nodes = textNode.getChildNodes();
		for(int index = 0; index < nodes.getLength(); index++){
			Node valueNode = nodes.item(index);
			if(valueNode.getNodeType() != Node.ELEMENT_NODE)
				continue;

			String form = ((Element)valueNode).getAttribute("form");
			String text = XmlUtil.getTextValue(valueNode);
			if(text != null){
				if(form == null)
					defaultValue = text;
				else if(form.equalsIgnoreCase("long"))
					longValue = text;
				else if(form != null && form.equalsIgnoreCase("short"))
					shortValue = text;
				else
					defaultValue = text;
			}
		}

		if(longValue != null)
			defaultValue = longValue;
		else if(shortValue != null)
			defaultValue = shortValue;

		defaultText.put(id, defaultValue);
	}


	/**
	 * For a given xforms document, fills the text of all nodes having a given name with their 
	 * corresponding text based on the itext id in the ref attribute.
	 * 
	 * @param name the name of the nodes to look for.
	 * @param doc the xforms document.
	 * @param itext the id to itext map.
	 * @param list the itext model as required by gxt grids.
	 */
	private static void translateNodes(String name, Document doc, HashMap<String,String> itext){
		NodeList nodes = doc.getElementsByTagName(name);
		if(nodes == null || nodes.getLength() == 0)
			return;

		//Map for detecting duplicates in itext. eg if id yes=Yes , we should not have information more than once.
		HashMap<String,String> duplicatesMap = new HashMap<String, String>();

		for(int index = 0; index < nodes.getLength(); index++){
			Element node = (Element)nodes.item(index);

			String id = getItextId(node);
			if(id == null || id.trim().length() == 0)
				continue;

			String text = itext.get(id);

			if(isBindNode(node))
				node.setAttribute("jr:constraintMsg", text); //We do not have itext in non jr formats
			else{
				//If the text node does not already exist, add it, else just update itx text.
				if(!XmlUtil.setTextNodeValue(node, text))
					node.appendChild(doc.createTextNode(text));
			}



			//............................................................................
			//Skip the steps below if we have already processed this itext id.
			if(duplicatesMap.containsKey(id))
				continue;
			else
				duplicatesMap.put(id, id);

			Element parentNode = (Element)node.getParentNode();
			String idname = "bind";
			String ref = parentNode.getAttribute("ref");
			if(ref != null)
				idname = "ref";
			else
				ref = parentNode.getAttribute("bind");

			if(ref == null){
				ref = parentNode.getAttribute("id");
				if(ref != null)
					idname = "id";
			}

			String xpath = FormUtil.getNodePath(parentNode) + "[@" + idname + "='" + ref + "']" + "/" + name;
			if(ref == null)
				xpath = FormUtil.getNodePath(parentNode) + "/" + name;
			
			if(isBindNode(node)){
				xpath += "[@"+XformConstants.ATTRIBUTE_NAME_ID+"='"+ node.getAttribute(XformConstants.ATTRIBUTE_NAME_ID) +"']";
				xpath += "[@"+"jr:constraintMsg"+"]";
			}

			for(Locale locale : Context.getLocales()){
				Element localeXformNode = localeXformNodeMap.get(locale.getKey());
				Element textNode = localeXformNode.getOwnerDocument().createElement("text");
				textNode.setAttribute("xpath", xpath);
				textNode.setAttribute("value", (String)defaultTextMap.get(locale.getKey()).get(id));
				localeXformNode.appendChild(textNode);
			}
			//..........................................................................
		}
	}

	public static String getItextId(Element node) {		
		//Check if node has a ref attribute.
		String ref = node.getAttribute("ref");
		if(ref == null){
			if(isBindNode(node))
				ref = node.getAttribute("jr:constraintMsg" /*XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE*/); //We do not have itext in non jr formats
			
			if(ref == null)
				return null;
		}

		//Check if node has jr:itext value in the ref attribute value.
		int pos = ref.indexOf("jr:itext('");
		if(pos < 0)
			return null;

		//Get the itext id which starts at the 11th character.
		return ref.substring(10,ref.lastIndexOf("'"));
	}
	
	public static boolean isBindNode(Element node){
		return ( node.getNodeName().equalsIgnoreCase(XformConstants.NODE_NAME_BIND_MINUS_PREFIX) ||
				node.getNodeName().equalsIgnoreCase(XformConstants.NODE_NAME_BIND) );
	}
	
	private static void addLayoutLocaleText(Document doc, Locale locale, Element localeXformNode){
		NodeList nodes = doc.getElementsByTagName("LanguageText");
		if(nodes == null || nodes.getLength() == 0)
			return;
		
		for(int index = 0; index < nodes.getLength(); index++){
			Element node = (Element)nodes.item(index);
			if(locale.getKey().equalsIgnoreCase(node.getAttribute("lang"))){
				addLayoutLocaleNode(localeXformNode.getOwnerDocument(), node);
				break;
			}
		}
	}
	
	private static void addLayoutLocaleNode(Document doc, Element langTextNode){
		NodeList nodes = langTextNode.getElementsByTagName("Form");
		if(nodes == null || nodes.getLength() == 0)
			return;
		
		Node node = nodes.item(0).cloneNode(true);
		doc.importNode(node, true);
		doc.getDocumentElement().appendChild(node);
	}
}
