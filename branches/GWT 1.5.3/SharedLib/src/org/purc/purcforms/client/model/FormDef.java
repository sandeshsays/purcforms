package org.purc.purcforms.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XformConstants;
import org.purc.purcforms.client.xforms.XformUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Definition of a form. This has some meta data about the form definition and  
 * a collection of pages together with question branching or skipping rules.
 * A form is sent as defined in one language. For instance, those using
 * Swahili would get forms in that language, etc. We don't support runtime
 * changing of a form language in order to have a more efficient implementation
 * as a trade off for more flexibility which may not be used most of the times.
 * 
 * @author Daniel Kayiwa
 *
 */
public class FormDef implements Serializable{

	/** A collection of page definitions (PageDef objects). */
	private Vector pages;

	//TODO May not need to serialize this property for smaller pay load. Then we may just rely on the id.
	//afterall it is not even guaranteed to be unique.
	/** The string unique identifier of the form definition. */
	private String variableName = ModelConstants.EMPTY_STRING;

	/** The display name of the form. */
	private String name = ModelConstants.EMPTY_STRING;

	/** The numeric unique identifier of the form definition. */
	private int id = ModelConstants.NULL_ID;

	/** The collection of rules (SkipRule objects) for this form. */
	private Vector skipRules;

	/** The collection of rules (ValidationRule objects) for this form. */
	private Vector validationRules;

	/** A string constistig for form fields that describe its data. eg description-template="${/data/question1}$ Market" */
	private String descriptionTemplate =  ModelConstants.EMPTY_STRING;

	/** A mapping of dynamic lists keyed by the id of the question whose values
	 *  determine possible values of another question as specified in the DynamicOptionDef object.
	 */
	private HashMap<Integer,DynamicOptionDef>  dynamicOptions;


	/** The xforms document.(for easy syncing between the object model and actual xforms document. */
	private Document doc;
	
	/** 
	 * The data node of the xform that this form represents.
	 * This is the node immediately under the instace node.
	 */
	private Element dataNode;
	
	/** The top level node of the xform that this form represents. */
	private Element xformsNode;
	
	/** The model node of the xform that this form represents. */
	private Element modelNode;

	/** The layout xml for this form. */
	private String layoutXml;
	
	/** The xforms xml for this form. */
	private String xformXml;
	
	/** The language xml for this form. */
	private String languageXml;

	/** 
	 * Flag to determine if we can change the form structure.
	 * For a read only form, we can only change the Text and Help Text.
	 * 
	 */
	private boolean readOnly = false;


	/** Constructs a form definition object. */
	public FormDef() {

	}

	/**
	 * Creates a new copy of the form from an existing one.
	 * 
	 * @param formDef the form to copy from.
	 */
	public FormDef(FormDef formDef) {
		this(formDef,true);
	}

	/**
	 * Creates a new copy of the form from an existing one, with a flag which
	 * tells whether we should copy the validation rules too.
	 * 
	 * @param formDef the form to copy from.
	 * @param copyValidationRules set to true if you also want to copy the validation rules, else false.
	 */
	public FormDef(FormDef formDef, boolean copyValidationRules) {
		setId(formDef.getId());
		setName(formDef.getName());

		//I just don't think we need this in addition to the id
		setVariableName(formDef.getVariableName());

		setDescriptionTemplate(formDef.getDescriptionTemplate());
		copyPages(formDef.getPages());
		copySkipRules(formDef.getSkipRules());

		//This is a temporary fix for an infinite recursion that happens when validation
		//rule copy constructor tries to set a formdef using the FormDef copy constructor.
		if(copyValidationRules)
			copyValidationRules(formDef.getValidationRules());

		copyDynamicOptions(formDef.getDynamicOptions());
	}

	/**
	 * Constructs a form definition object from these parameters.
	 * 
	 * @param name - the numeric unique identifier of the form definition.
	 * @param name - the display name of the form.
	 * @param variableName - the string unique identifier of the form definition.
	 * @param pages - collection of page definitions.
	 * @param rules - collection of branching rules.
	 */
	public FormDef(int id, String name, String variableName,Vector pages, Vector skipRules, Vector validationRules, HashMap<Integer,DynamicOptionDef> dynamicOptions, String descTemplate) {
		setId(id);
		setName(name);

		//I just don't think we need this in addition to the id
		setVariableName(variableName);

		setPages(pages);
		setSkipRules(skipRules);
		setValidationRules(validationRules);
		setDynamicOptions(dynamicOptions);
		setDescriptionTemplate((descTemplate == null) ? ModelConstants.EMPTY_STRING : descTemplate);
	}

	/**
	 * Adds a new page to the form.
	 * 
	 * @param pageDef the page to add.
	 */
	public void addPage(PageDef pageDef){
		if(pages == null)
			pages = new Vector();

		pages.add(pageDef);
		pageDef.setParent(this);
	}

	/**
	 * Adds a default page to the form.
	 */
	public void addPage(){
		if(pages == null)
			pages = new Vector();

		int pageno = pages.size() + 1;
		pages.add(new PageDef("Page"+pageno,pageno,this));
	}

	/**
	 * Sets the name of the last page in the form.
	 * 
	 * @param name the name to set.
	 * @return the last page in the form whose name has been set.
	 */
	public PageDef setPageName(String name){
		PageDef pageDef = ((PageDef)pages.elementAt(pages.size()-1));
		pageDef.setName(name);
		return pageDef;
	}

	public void setPageLabelNode(Element labelNode){
		((PageDef)pages.elementAt(pages.size()-1)).setLabelNode(labelNode);
	}

	public void setPageGroupNode(Element groupNode){
		((PageDef)pages.elementAt(pages.size()-1)).setGroupNode(groupNode);
	}

	/**
	 * Gets the list of pages that the form has.
	 * 
	 * @return the page list
	 */
	public Vector getPages() {
		return pages;
	}

	public PageDef getPageAt(int index) {
		if(pages == null)
			return null;
		return (PageDef)pages.elementAt(index);
	}

	public SkipRule getSkipRuleAt(int index) {
		if(skipRules == null)
			return null;
		return (SkipRule)skipRules.elementAt(index);
	}

	public ValidationRule getValidationRuleAt(int index) {
		if(validationRules == null)
			return null;
		return (ValidationRule)validationRules.elementAt(index);
	}

	public void setPages(Vector pages) {
		this.pages = pages;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//I just don't think we need this in addition to the id
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Vector getSkipRules() {
		return skipRules;
	}

	public void setSkipRules(Vector skipRules) {
		this.skipRules = skipRules;
	}

	public Vector getValidationRules() {
		return validationRules;
	}

	public void setValidationRules(Vector validationRules) {
		this.validationRules = validationRules;
	}

	public HashMap<Integer,DynamicOptionDef> getDynamicOptions() {
		return dynamicOptions;
	}

	public void setDynamicOptions(HashMap<Integer,DynamicOptionDef> dynamicOptions) {
		this.dynamicOptions = dynamicOptions;
	}

	public String getDescriptionTemplate() {
		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;
	}

	public String getLayoutXml() {
		return layoutXml;
	}

	public void setLayoutXml(String layout) {
		this.layoutXml = layout;
	}

	public String getLanguageXml() {
		return languageXml;
	}

	public void setLanguageXml(String languageXml) {
		this.languageXml = languageXml;
	}

	public String getXformXml() {
		return xformXml;
	}

	public void setXformXml(String xform) {
		this.xformXml = xform;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the first skip rule which has a given question as one of its targets.
	 * 
	 * @param questionDef the question.
	 * @return the skip rule.
	 */
	public SkipRule getSkipRule(QuestionDef questionDef){
		if(skipRules == null)
			return null;

		for(int i=0; i<skipRules.size(); i++){
			SkipRule rule = (SkipRule)skipRules.elementAt(i);
			Vector targets = rule.getActionTargets();
			for(int j=0; j<targets.size(); j++){
				if(((Integer)targets.elementAt(j)).intValue() == questionDef.getId())
					return rule;
			}
		}

		return null;
	}

	/**
	 * Gets the validation rule for a given question.
	 * 
	 * @param questionDef the question.
	 * @return the validation rule.
	 */
	public ValidationRule getValidationRule(QuestionDef questionDef){
		if(validationRules == null)
			return null;

		for(int i=0; i<validationRules.size(); i++){
			ValidationRule rule = (ValidationRule)validationRules.elementAt(i);
			if(questionDef.getId() == rule.getQuestionId())
				return rule;
		}

		return null;
	}

	/**
	 * Updates the xforms document with the current changes in the form.
	 * 
	 * @param withData set to true if you want question answers to also be saved as part of the xform.
	 */
	public void updateDoc(boolean withData){
		dataNode.setAttribute(XformConstants.ATTRIBUTE_NAME_NAME, name);

		//TODO Check that this comment out does not introduce bugs
		//We do not want a refreshed xform to overwrite existing formDef id
		//If ones want to change the id, he should load the xform as a new form with that id
		/*String val = dataNode.getAttribute(XformConstants.ATTRIBUTE_NAME_ID);
		if(val == null || val.trim().length() == 0)
			dataNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, String.valueOf(id));
		else
			setId(Integer.parseInt(val));*/

		//TODO Check this with the above
		dataNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID,String.valueOf(id));

		String orgVarName = dataNode.getNodeName();
		if(!orgVarName.equalsIgnoreCase(variableName)){
			dataNode = XformUtil.renameNode(dataNode,variableName);
			updateDataNodes();
			((Element)dataNode.getParentNode()).setAttribute(XformConstants.ATTRIBUTE_NAME_ID, variableName);
		}

		if(dataNode != null){
			if(descriptionTemplate == null || descriptionTemplate.trim().length() == 0)
				dataNode.removeAttribute(XformConstants.ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE);
			else
				dataNode.setAttribute(XformConstants.ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE, descriptionTemplate);
		}

		if(pages != null){
			for(int i=0; i<pages.size(); i++){
				PageDef pageDef = (PageDef)pages.elementAt(i);
				pageDef.updateDoc(doc,xformsNode,this,dataNode,modelNode,withData,orgVarName);
			}
		}

		if(skipRules != null){
			for(int i=0; i<skipRules.size(); i++){
				SkipRule skipRule = (SkipRule)skipRules.elementAt(i);
				skipRule.updateDoc(this);
			}
		}

		if(validationRules != null){
			for(int i=0; i<validationRules.size(); i++){
				ValidationRule validationRule = (ValidationRule)validationRules.elementAt(i);
				validationRule.updateDoc(this);
			}
		}

		if(dynamicOptions != null){
			Iterator<Entry<Integer,DynamicOptionDef>> iterator = dynamicOptions.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer,DynamicOptionDef> entry = iterator.next();
				DynamicOptionDef dynamicOptionDef = entry.getValue();
				QuestionDef questionDef = getQuestion(entry.getKey());
				if(questionDef == null)
					continue;

				dynamicOptionDef.updateDoc(this,questionDef);
			}
		}
	}

	private void updateDataNodes(){
		if(pages == null)
			return;

		for(int i=0; i<pages.size(); i++)
			((PageDef)pages.elementAt(i)).updateDataNodes(dataNode);
	}

	public String toString() {
		return getName();
	}

	/**
	 * Gets a question identified by a variable name.
	 * 
	 * @param varName - the string identifier of the question. 
	 * @return the question reference.
	 */
	public QuestionDef getQuestion(String varName){
		if(varName == null || pages == null)
			return null;

		for(int i=0; i<getPages().size(); i++){
			QuestionDef def = ((PageDef)getPages().elementAt(i)).getQuestion(varName);
			if(def != null)
				return def;
		}

		return null;
	}

	/**
	 * Gets a question identified by an id
	 * 
	 * @param id - the numeric identifier of the question. 
	 * @return the question reference.
	 */
	public QuestionDef getQuestion(int id){		
		if(pages == null)
			return null;

		for(int i=0; i<getPages().size(); i++){
			QuestionDef def = ((PageDef)getPages().elementAt(i)).getQuestion(id);
			if(def != null)
				return def;
		}

		return null;
	}

	/**
	 * Gets a numeric question identifier for a given question variable name.
	 * 
	 * @param varName - the string identifier of the question. 
	 * @return the numeric question identifier.
	 */
	public int getQuestionId(String varName){
		QuestionDef qtn = getQuestion(varName);
		if(qtn != null)
			return qtn.getId();

		return ModelConstants.NULL_ID;
	}

	/**
	 * Adds a new question to the form.
	 * 
	 * @param qtn the new question to add.
	 */
	public void addQuestion(QuestionDef qtn){
		if(pages == null){
			pages = new Vector();
			PageDef page = new PageDef(/*this.getVariableName()*/"Page1",Integer.parseInt("1"),null,this);
			pages.addElement(page);
		}

		((PageDef)pages.elementAt(pages.size()-1)).addQuestion(qtn);

		qtn.setParent(pages.elementAt(pages.size()-1));
	}

	/**
	 * Copies a given list of pages into this form.
	 * 
	 * @param pages the pages to copy.
	 */
	private void copyPages(Vector pages){
		if(pages != null){
			this.pages =  new Vector();
			for(int i=0; i<pages.size(); i++) //Should have atleast one page is why we are not checking for nulls.
				this.pages.addElement(new PageDef((PageDef)pages.elementAt(i),this));
		}
	}

	/**
	 * Copies a given list of skip rules into this form.
	 * 
	 * @param rules the skip rules.
	 */
	private void copySkipRules(Vector rules){
		if(rules != null)
		{
			this.skipRules =  new Vector();
			for(int i=0; i<rules.size(); i++)
				this.skipRules.addElement(new SkipRule((SkipRule)rules.elementAt(i)));
		}
	}

	/**
	 * Copies a given list of validation rules into this form.
	 * 
	 * @param rules the validation rules.
	 */
	private void copyValidationRules(Vector rules){
		if(rules != null)
		{
			this.validationRules =  new Vector();
			for(int i=0; i<rules.size(); i++)
				this.validationRules.addElement(new ValidationRule((ValidationRule)rules.elementAt(i)));
		}
	}

	private void copyDynamicOptions(HashMap<Integer,DynamicOptionDef> options){
		if(options != null)
		{
			dynamicOptions =  new HashMap<Integer,DynamicOptionDef>();

			Iterator<Entry<Integer,DynamicOptionDef>> iterator = options.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer,DynamicOptionDef> entry = iterator.next();
				DynamicOptionDef dynamicOptionDef = entry.getValue();
				QuestionDef questionDef = getQuestion(dynamicOptionDef.getQuestionId());
				if(questionDef == null)
					return;
				dynamicOptions.put(new Integer(entry.getKey()), new DynamicOptionDef(dynamicOptionDef,questionDef));
			}
		}
	}

	/*private void copyDynamicOptions(HashMap<Integer,DynamicOptionDef>){

	}*/

	/**
	 * Removes a page from the form.
	 * 
	 * @param pageFef the page to remove.
	 */
	public void removePage(PageDef pageDef){
		/*for(int i=0; i<pages.size(); i++){
			((PageDef)pages.elementAt(i)).removeAllQuestions();
		}*/

		pageDef.removeAllQuestions(this);

		if(pageDef.getGroupNode() != null)
			pageDef.getGroupNode().getParentNode().removeChild(pageDef.getGroupNode());

		pages.removeElement(pageDef);
	}

	/**
	 * Sets the xforms document represented by this form.
	 * @param doc
	 */
	public void setDoc(Document doc){
		this.doc = doc;
	}

	/**
	 * Gets the xforms document represented by this form.
	 * @return
	 */
	public Document getDoc(){
		return doc;
	}

	/**
	 * @return the dataNode
	 */
	public Element getDataNode() {
		return dataNode;
	}

	/**
	 * @param dataNode the dataNode to set
	 */
	public void setDataNode(Element dataNode) {
		this.dataNode = dataNode;
	}

	/**
	 * @return the xformsNode
	 */
	public Element getXformsNode() {
		return xformsNode;
	}

	/**
	 * @param xformsNode the xformsNode to set
	 */
	public void setXformsNode(Element xformsNode) {
		this.xformsNode = xformsNode;
	}

	/**
	 * @return the modelNode
	 */
	public Element getModelNode() {
		return modelNode;
	}

	/**
	 * @param modelNode the modelNode to set
	 */
	public void setModelNode(Element modelNode) {
		this.modelNode = modelNode;
	}

	
	/**
	 * Moves a page one position up in the form.
	 * 
	 * @param pageDef the page to move.
	 */
	public void movePageUp(PageDef pageDef){
		int index = pages.indexOf(pageDef);

		pages.remove(pageDef);

		if(pageDef.getGroupNode() != null)
			xformsNode.removeChild(pageDef.getGroupNode());

		PageDef currentPageDef;
		List list = new ArrayList();

		while(pages.size() >= index){
			currentPageDef = (PageDef)pages.elementAt(index-1);
			list.add(currentPageDef);
			pages.remove(currentPageDef);
		}

		pages.add(pageDef);
		for(int i=0; i<list.size(); i++){
			if(i == 0){
				PageDef pgDef = (PageDef)list.get(i);
				if(pgDef.getGroupNode() != null)
					xformsNode.insertBefore(pageDef.getGroupNode(), pgDef.getGroupNode());
			}
			pages.add(list.get(i));
		}
	}

	/**
	 * Moves a page one position down in the form.
	 * 
	 * @param pageDef the page to move.
	 */
	public void movePageDown(PageDef pageDef){
		int index = pages.indexOf(pageDef);	

		pages.remove(pageDef);

		if(pageDef.getGroupNode() != null)
			xformsNode.removeChild(pageDef.getGroupNode());

		PageDef currentItem; // = parent.getChild(index - 1);
		List list = new ArrayList();

		while(pages.size() > 0 && pages.size() > index){
			currentItem = (PageDef)pages.elementAt(index);
			list.add(currentItem);
			pages.remove(currentItem);
		}

		for(int i=0; i<list.size(); i++){
			if(i == 1){
				pages.add(pageDef); //Add after the first item.

				PageDef pgDef = (PageDef)list.get(i);
				if(pgDef.getGroupNode() != null)
					xformsNode.insertBefore(pageDef.getGroupNode(), pgDef.getGroupNode());
			}
			pages.add(list.get(i));
		}

		if(list.size() == 1){
			pages.add(pageDef);

			if(pageDef.getGroupNode() != null)
				xformsNode.appendChild(pageDef.getGroupNode());
		}
	}

	/**
	 * Removes a question from the form.
	 * 
	 * @param qtnDef the question to remove.
	 * @return true if the question has been found and removed, else false.
	 */
	public boolean removeQuestion(QuestionDef qtnDef){
		for(int i=0; i<pages.size(); i++){
			if(((PageDef)pages.elementAt(i)).removeQuestion(qtnDef,this))
				return true;
		}
		return false;
	}

	/**
	 * Removes a question for the validation rules list.
	 * 
	 * @param questionDef the question to remove.
	 */
	private void removeQtnFromValidationRules(QuestionDef questionDef){
		for(int index = 0; index < this.getValidationRuleCount(); index++){
			ValidationRule validationRule = getValidationRuleAt(index);
			validationRule.removeQuestion(questionDef);
			if(validationRule.getConditionCount() == 0){
				removeValidationRule(validationRule);
				index++;
			}
		}
	}

	/**
	 * Removes a question from skip rules list.
	 * 
	 * @param questionDef the question to remove.
	 */
	private void removeQtnFromSkipRules(QuestionDef questionDef){
		for(int index = 0; index < getSkipRuleCount(); index++){
			SkipRule skipRule = getSkipRuleAt(index);
			skipRule.removeQuestion(questionDef);
			if(skipRule.getActionTargetCount() == 0 || skipRule.getConditionCount() == 0){
				removeSkipRule(skipRule);
				index++;
			}
		}
	}

	/**
	 * Removes a question from the validation rules which are referencing it.
	 * 
	 * @param qtnDef the question to remove.
	 */
	public void removeQtnFromRules(QuestionDef qtnDef){
		removeQtnFromValidationRules(qtnDef);
		removeQtnFromSkipRules(qtnDef);
	}
	
	/**
	 * Check if a question is referenced by any dynamic selection list relationship
	 * and if so, removes the relationship.
	 * 
	 * @param questionDef the question to check.
	 */
	public void removeQtnFromDynamicLists(QuestionDef questionDef){
		if(dynamicOptions != null){
			
			Object[] keys = dynamicOptions.keySet().toArray();
			for(int index = 0; index < keys.length; index++){
				Integer questionId = (Integer)keys[index];
				DynamicOptionDef dynamicOptionDef = dynamicOptions.get(questionId);
				
				//Check if the deleted question is the parent of a dynamic selection
				//list relationship. And if so, delete the relationship.
				if(questionId.intValue() == questionDef.getId()){
					dynamicOptions.remove(questionId);
					removeDynamicInstanceNode(dynamicOptionDef);
					continue;
				}

				//Check if the deleted question is the child of a dynamic selection
				//list relationship. And if so, delete the relationship.
				if(dynamicOptionDef.getQuestionId() == questionDef.getId()){
					dynamicOptions.remove(questionId);
					removeDynamicInstanceNode(dynamicOptionDef);
					continue;
				}
				
				dynamicOptionDef.updateDoc(this,questionDef);
			}
		}
	}
	
	/**
	 * Removes the instance node referenced by a dynamic selection list object.
	 * 
	 * @param dynamicOptionDef the dynamic selection list object.
	 */
	private static void removeDynamicInstanceNode(DynamicOptionDef dynamicOptionDef){
		//dataNode points to <dynamiclist>
		//dataNode.getParentNode() points to <xf:instance id="theid">
		//dataNode.getParentNode().getParentNode() points to <xf:model>
		
		Element dataNode = dynamicOptionDef.getDataNode();
		if(dataNode != null && dataNode.getParentNode() != null
				&& dataNode.getParentNode().getParentNode() != null){
			
			dataNode.getParentNode().getParentNode().removeChild(dataNode.getParentNode());	
		}
	}

	/**
	 * Gets the number of pages in the form.
	 * 
	 * @return the number of pages.
	 */
	public int getPageCount(){
		if(pages == null)
			return 0;
		return pages.size();
	}

	/**
	 * Gets the number of skip rules in the form.
	 * 
	 * @return the number of skip rules.
	 */
	public int getSkipRuleCount(){
		if(skipRules == null)
			return 0;
		return skipRules.size();
	}

	/**
	 * Gets the number of validation rules in the form.
	 * 
	 * @return the number of validation rules.
	 */
	public int getValidationRuleCount(){
		if(validationRules == null)
			return 0;
		return validationRules.size();
	}

	/**
	 * Removes a question from its page to some other page.
	 * 
	 * @param qtn the question to move.
	 * @param pageNo the new page number where to take the question.
	 */
	public void moveQuestion2Page(QuestionDef qtn, int pageNo, FormDef formDef){
		if(pages.size() < pageNo)
			pages.add(new PageDef(formDef));
		
		for(int i=0; i<pages.size(); i++){
			PageDef page = (PageDef)pages.elementAt(i);
			if(page.contains(qtn)){
				if(i == pageNo-1)
					return;
				page.getQuestions().removeElement(qtn);
				((PageDef)pages.elementAt(pageNo-1)).addQuestion(qtn);
				return;
			}
		}
	}

	/**
	 * Gets questions with given display text.
	 * 
	 * @param text the display text to look for.
	 * @return the question of found, else null.
	 */
	public QuestionDef getQuestionWithText(String text){
		for(int i=0; i<pages.size(); i++){
			QuestionDef questionDef = ((PageDef)pages.elementAt(i)).getQuestionWithText(text);
			if(questionDef != null)
				return questionDef;
		}
		return null;
	}
	

	/**
	 * Checks if the form has a particular skip rule.
	 * 
	 * @param skipRule the skip rule to check.
	 * @return true if the skip rule has been found, else false.
	 */
	public boolean containsSkipRule(SkipRule skipRule){
		if(skipRules == null)
			return false;
		return skipRules.contains(skipRule);
	}

	
	/**
	 * Checks if a form has a particular validation rule.
	 * 
	 * @param validationRule the validation rule to check.
	 * @return true if the validation rule has been found, else false.
	 */
	public boolean containsValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			return false;
		return validationRules.contains(validationRule);
	}

	/**
	 * Adds a new skip rule to the form.
	 * 
	 * @param skipRule the new skip rule to add.
	 */
	public void addSkipRule(SkipRule skipRule){
		if(skipRules == null)
			skipRules = new Vector();
		skipRules.addElement(skipRule);
	}

	/**
	 * Adds a new validation rule to the form.
	 * 
	 * @param validationRule the new validation rule to add.
	 */
	public void addValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			validationRules = new Vector();
		validationRules.addElement(validationRule);
	}

	/**
	 * Removes a skip rule from the form.
	 * 
	 * @param skipRule the skip rule to remove.
	 * @return true if the skip rule has been found and removed, else false.
	 */
	public boolean removeSkipRule(SkipRule skipRule){
		if(skipRules == null)
			return false;

		boolean ret = skipRules.remove(skipRule);
		if(dataNode != null){
			for(int index = 0; index < skipRule.getActionTargetCount(); index++){
				QuestionDef questionDef = getQuestion(skipRule.getActionTargetAt(index));
				if(questionDef != null && questionDef.getDataNode() != null){
					questionDef.getDataNode().removeAttribute(XformConstants.ATTRIBUTE_NAME_RELEVANT);
					questionDef.getDataNode().removeAttribute(XformConstants.ATTRIBUTE_NAME_ACTION);
				}
			}
		}
		return ret;
	}

	/**
	 * Removes a validation rule from the form.
	 * 
	 * @param validationRule the validation rule to remove.
	 * @return true if the validation rule has been found and removed.
	 */
	public boolean removeValidationRule(ValidationRule validationRule){
		if(validationRules == null)
			return false;

		boolean ret = validationRules.remove(validationRule);
		if(dataNode != null){
			QuestionDef questionDef = getQuestion(validationRule.getQuestionId());
			if(questionDef != null && questionDef.getBindNode() != null){
				questionDef.getBindNode().removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT);
				questionDef.getBindNode().removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE);
			}
		}
		return ret;
	}

	public void setDynamicOptionDef(Integer questionId, DynamicOptionDef dynamicOptionDef){
		
		//The parent or child question may have been deleted.
		if(getQuestion(questionId) == null || getQuestion(dynamicOptionDef.getQuestionId()) == null)
			return;
		
		if(dynamicOptions == null)
			dynamicOptions = new HashMap<Integer,DynamicOptionDef>();
		
		dynamicOptions.put(questionId, dynamicOptionDef);
	}

	public DynamicOptionDef getDynamicOptions(Integer questionId){
		if(dynamicOptions == null)
			return null;
		return dynamicOptions.get(questionId);
	}

	public DynamicOptionDef getChildDynamicOptions(Integer questionId){
		if(dynamicOptions == null)
			return null;

		Iterator<Entry<Integer,DynamicOptionDef>> iterator = dynamicOptions.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Integer,DynamicOptionDef> entry = iterator.next();
			DynamicOptionDef dynamicOptionDef = entry.getValue();
			if(dynamicOptionDef.getQuestionId() == questionId)
				return dynamicOptionDef;
		}
		return null;
	}

	public QuestionDef getDynamicOptionsParent(Integer questionId){
		if(dynamicOptions == null)
			return null;

		Iterator<Entry<Integer,DynamicOptionDef>> iterator = dynamicOptions.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Integer,DynamicOptionDef> entry = iterator.next();
			DynamicOptionDef dynamicOptionDef = entry.getValue();
			if(dynamicOptionDef.getQuestionId() == questionId)
				return getQuestion(entry.getKey());
		}
		return null;
	}

	public OptionDef getDynamicOptionDef(Integer questionId, int id){
		if(dynamicOptions == null)
			return null;

		Iterator<Entry<Integer,DynamicOptionDef>> iterator = dynamicOptions.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Integer,DynamicOptionDef> entry = iterator.next();
			DynamicOptionDef dynamicOptionDef = entry.getValue();
			if(dynamicOptionDef.getQuestionId() == questionId)
				return dynamicOptionDef.getOptionWithId(id);
		}
		return null;
	}

	/**
	 * Removes a dynamic selection list relationship referenced by a given question.
	 * 
	 * @param questionId the question to check from dynamic selection lists.
	 */
	public void removeDynamicOptions(Integer questionId){
		if(dynamicOptions != null){
			DynamicOptionDef dynamciOptionDef = dynamicOptions.get(questionId);
			if(dynamciOptionDef == null)
				return;
			
			removeDynamicInstanceNode(dynamciOptionDef);
			
			dynamicOptions.remove(questionId);
		}
	}

	/**
	 * Updates this formDef (as the main from the refresh source) with the parameter one
	 * 
	 * @param formDef the old formDef to copy from.
	 */
	public void refresh(FormDef formDef){
		this.id = formDef.getId();

		if(variableName.equals(formDef.getVariableName()))
			name = formDef.getName();

		for(int index = 0; index < formDef.getPageCount(); index++)
			refresh((PageDef)formDef.getPageAt(index));

		//Clear existing skip rules if any. Already existing skip rules will always
		//overwrite those from the refresh source.
		skipRules = new Vector();
		for(int index = 0; index < formDef.getSkipRuleCount(); index++)
			formDef.getSkipRuleAt(index).refresh(this, formDef);
		
		//Clear existing validation rules if any. Already existing validation rules 
		//will always overwrite those from the refresh source.
		validationRules = new Vector();
		for(int index = 0; index < formDef.getValidationRuleCount(); index++)
			formDef.getValidationRuleAt(index).refresh(this, formDef);
		
		//If we already had dynamic options, they will always overwrite all 
		//from the refresh source.
		//TODO May need to do a smarter refresh by only overwriting those that have
		//come from the server and then leave the rest.
		if(formDef.getDynamicOptions() != null){
			dynamicOptions =  new HashMap<Integer,DynamicOptionDef>();

			Iterator<Entry<Integer,DynamicOptionDef>> iterator = formDef.getDynamicOptions().entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Integer,DynamicOptionDef> entry = iterator.next();
				
				QuestionDef oldParentQtnDef = formDef.getQuestion(entry.getKey());
				if(oldParentQtnDef == null)
					continue; //How can this be missing in the original formdef???
				
				QuestionDef newParentQtnDef = getQuestion(oldParentQtnDef.getVariableName());
				if(newParentQtnDef == null)
					continue; //My be deleted by refresh source.
				
				DynamicOptionDef oldDynOptionDef = entry.getValue();
				QuestionDef oldChildQtnDef = formDef.getQuestion(oldDynOptionDef.getQuestionId());
				if(oldChildQtnDef == null)
					return; //can this be lost in the old formdef????
				
				QuestionDef newChildQtnDef = getQuestion(oldChildQtnDef.getVariableName());
				if(newChildQtnDef == null)
					continue; //possibly deleted by refresh sourced (eg server).
				
				DynamicOptionDef newDynOptionDef = new DynamicOptionDef();
				newDynOptionDef.setQuestionId(newChildQtnDef.getId());
				newDynOptionDef.refresh(this, formDef, newDynOptionDef, oldDynOptionDef,newParentQtnDef,oldParentQtnDef,newChildQtnDef,oldChildQtnDef);
				dynamicOptions.put(new Integer(newParentQtnDef.getId()),newDynOptionDef);
			}
		}
	}

	private void refresh(PageDef pageDef){
		for(int index = 0; index < pages.size(); index++)
			((PageDef)pages.get(index)).refresh(pageDef);
	}

	/**
	 * Gets the total number of questions contained in the form.
	 * 
	 * @return the number of questions.
	 */
	public int getQuestionCount(){
		if(pages == null)
			return 0;

		int count = 0;
		for(int index = 0; index < pages.size(); index++)
			count += getPageAt(index).getQuestionCount();

		return count;
	}

	public void updateRuleConditionValue(String origValue, String newValue){
		for(int index = 0; index < getSkipRuleCount(); index++)
			getSkipRuleAt(index).updateConditionValue(origValue, newValue);

		for(int index = 0; index < getValidationRuleCount(); index++)
			getValidationRuleAt(index).updateConditionValue(origValue, newValue);
	}

	public Element getLanguageNode() {
		com.google.gwt.xml.client.Document doc = XMLParser.createDocument();
		Element rootNode = doc.createElement("xform");
		rootNode.setAttribute(XformConstants.ATTRIBUTE_NAME_ID, id+"");
		doc.appendChild(rootNode);

		if(dataNode != null){
			Element node = doc.createElement(XformConstants.NODE_NAME_TEXT);
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_XPATH, FormUtil.getNodePath(dataNode)+"[@name]");
			node.setAttribute(XformConstants.ATTRIBUTE_NAME_VALUE, name);
			rootNode.appendChild(node);

			if(pages != null){
				for(int index = 0; index < pages.size(); index++)
					((PageDef)pages.elementAt(index)).buildLanguageNodes(doc, rootNode);
			}

			if(validationRules != null){
				for(int index = 0; index < validationRules.size(); index++)
					((ValidationRule)validationRules.elementAt(index)).buildLanguageNodes(this, rootNode);
			}

			if(dynamicOptions != null){
				Iterator<Entry<Integer,DynamicOptionDef>> iterator = dynamicOptions.entrySet().iterator();
				while(iterator.hasNext())
					iterator.next().getValue().buildLanguageNodes(this, rootNode);
			}

			/*XPathExpression xpls = new XPathExpression(this.doc, "xforms/model/instance/newform1"); //"/xforms/model/instance/newform1"
			Vector result = xpls.getResult();
			if(result.size() > 0)
				System.out.println(result.get(0));*/
		}

		return rootNode;
	}

	/**
	 * Gets the form to which a particular item (PageDef,QuestionDef,OptionDef) belongs.
	 * 
	 * @param formItem the item.
	 * @return the form.
	 */
	public static FormDef getFormDef(Object formItem){
		if(formItem instanceof FormDef)
			return (FormDef)formItem;
		else if(formItem instanceof PageDef)
			return ((PageDef)formItem).getParent();
		else if(formItem instanceof QuestionDef){
			Object item = ((QuestionDef)formItem).getParent();
			return getFormDef(item);
		}
		else if(formItem instanceof OptionDef){
			Object item = ((OptionDef)formItem).getParent();
			return getFormDef(item);
		}

		return null;
	}

	/**
	 * Removes all question change event listeners.
	 */
	public void clearChangeListeners(){
		if(pages == null)
			return;

		for(int i=0; i<pages.size(); i++)
			((PageDef)pages.elementAt(i)).clearChangeListeners();
	}
}