package org.purc.purcforms.client.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.purc.purcforms.client.util.FormUtil;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;


/**
 * Definition for group of questions. Basically this is just a specialized collection
 * of a set of questions, together with reference to their parent question.
 * 
 * @author daniel
 *
 */
public class GroupQtnsDef implements Serializable {
	
	/** A list of questions (QuestionDef objects) on a group questions row. */
	private Vector questions;
	
	/** Reference to the parent question. */
	private QuestionDef qtnDef;
	
	/**
	 * Creates a new repeat questions definition object.
	 */
	public GroupQtnsDef() {
		 
	}
	
	/** Copy Constructor. */
	public GroupQtnsDef(GroupQtnsDef groupQtnsDef) {
		//setQtnDef(new QuestionDef(groupQtnsDef.getQtnDef()));
		setQtnDef(groupQtnsDef.getQtnDef());
		copyQuestions(groupQtnsDef.getQuestions());
	}
	
	public GroupQtnsDef(QuestionDef qtnDef) {
		setQtnDef(qtnDef);
	}
	
	public GroupQtnsDef(QuestionDef qtnDef,Vector questions) {
		this(qtnDef);
		setQuestions(questions);
	}
	
	public QuestionDef getQtnDef() {
		return qtnDef;
	}

	public void setQtnDef(QuestionDef qtnDef) {
		this.qtnDef = qtnDef;
	}

	public Vector getQuestions() {
		return questions;
	}
	
	public int size(){
		if(questions == null)
			return 0;
		return questions.size();
	}

	public void addQuestion(QuestionDef qtn){
		addQuestion(qtn, null);
	}
	
	public void addQuestion(QuestionDef qtn, QuestionDef refQtn){
		if(questions == null)
			questions = new Vector();
		
		//qtn.setId((byte)(questions.size()+1)); id should be set somewhere else
		if(refQtn == null)
			questions.addElement(qtn);
		else
			questions.add(questions.indexOf(refQtn) + 1, qtn);
	}
	
	public boolean removeQuestion(QuestionDef qtnDef, FormDef formDef){
		if(qtnDef.getControlNode() != null && qtnDef.getControlNode().getParentNode() != null)
			qtnDef.getControlNode().getParentNode().removeChild(qtnDef.getControlNode());
		if(qtnDef.getDataNode() != null && qtnDef.getDataNode().getParentNode() != null)
			qtnDef.getDataNode().getParentNode().removeChild(qtnDef.getDataNode());
		if(qtnDef.getBindNode() != null && qtnDef.getBindNode().getParentNode() != null)
			qtnDef.getBindNode().getParentNode().removeChild(qtnDef.getBindNode());
		
		if(formDef != null)
			formDef.removeQtnFromRules(qtnDef);
		
		if (questions == null)
			return false;
		
		return questions.removeElement(qtnDef);
	}
	
	public void setQuestions(Vector questions) {
		this.questions = questions;
	}
	
	public String getText(){
		if(qtnDef != null)
			return qtnDef.getText();
		return null;
	}
	
	public QuestionDef getQuestion(int id){
		if(questions == null)
			return null;
		
		for(int i=0; i<getQuestions().size(); i++){
			QuestionDef def = (QuestionDef)getQuestions().elementAt(i);
			if(def.getId() == id)
				return def;
		}
		
		return null;
	}
	
	public QuestionDef getQuestionAt(int index){
		return (QuestionDef)questions.elementAt(index);
	}
	
	public int getQuestionsCount(){
		if(questions == null)
			return 0;
		return questions.size();
	}
	
	protected void copyQuestions(Vector questions){
		if(questions == null)
			return;
		
		this.questions = new Vector();
		for(int i=0; i<questions.size(); i++)
			this.questions.addElement(new QuestionDef((QuestionDef)questions.elementAt(i),qtnDef));
	}
	
	public void moveQuestionUp(QuestionDef questionDef){		
		PageDef.moveQuestionUp(questions, questionDef);
	}
	
	public void moveQuestionDown(QuestionDef questionDef){		
		PageDef.moveQuestionDown(questions, questionDef);
	}
	
	public void updateDoc(Document doc, Element xformsNode, FormDef formDef, Element formNode, Element modelNode,Element groupNode, boolean withData, String orgFormVarName){
		if(questions == null)
			return;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.elementAt(i);
			questionDef.updateDoc(doc,xformsNode,formDef,qtnDef.getDataNode(),modelNode,qtnDef.getControlNode(), true /*false*/ ,withData,orgFormVarName, qtnDef.getBinding());
		}
	}
	
	/**
	 * Gets a question identified by a variable name.
	 * 
	 * @param varName - the string identifier of the question. 
	 * @return the question reference.
	 */
	public QuestionDef getQuestion(String varName){
		if(varName == null || questions == null)
			return null;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef def = (QuestionDef)questions.elementAt(i);
			if(def.getBinding().equals(varName) || ("/" + def.getBinding()).equals(varName))
				return def;
			
			if(def.isGroupQtnsDef() && def.getGroupQtnsDef() != null){
				def = def.getGroupQtnsDef().getQuestion(varName);
				if(def != null)
					return def;
			}
		}
		
		//only do this if the above fails
		for(int i=0; i<questions.size(); i++){
			QuestionDef def = (QuestionDef)questions.elementAt(i);
			if((qtnDef.getBinding() + "/" + varName).equals(def.getBinding()) )
				return def;
		}
		
		//only do this if the above fails
		for(int i=0; i<questions.size(); i++){
			QuestionDef def = (QuestionDef)questions.elementAt(i);
			if((qtnDef.getBinding() + "/" + def.getBinding()).equals(varName) )
				return def;
		}
		
		return null;
	}
	
	public QuestionDef getQuestionWithText(String text){
		if(text == null || questions == null)
			return null;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.elementAt(i);
			if(questionDef.getText().equals(text))
				return questionDef;
		}
		return null;
	}
	
	public void refresh(GroupQtnsDef groupQtnsDef){
		Vector questions2 = groupQtnsDef.getQuestions();
		if(questions == null || questions2 == null)
			return;
		
		Vector<QuestionDef> orderedQtns = new Vector<QuestionDef>();
		Vector<QuestionDef> missingQtns = new Vector<QuestionDef>();
		
		for(int index = 0; index < questions2.size(); index++){
			QuestionDef qtn = (QuestionDef)questions2.get(index);
			QuestionDef questionDef = getQuestion(qtn.getBinding());
			if(questionDef == null){
				missingQtns.add(qtn);
				continue; //Possibly this question was deleted on server
			}
			
			questionDef.refresh(qtn);
			
			if (FormUtil.maintainOrderingOnRefresh()) {
				orderedQtns.add(questionDef); //add the question in the order it was before the refresh.
	
				//Only move up or down if question really exists.
				if(questions.indexOf(questionDef) >= 0) {
					
					//Preserve the previous question ordering even in the xforms document nodes.
					int newIndex = ((List)questions).indexOf(questionDef);
					
					int tempIndex = index - missingQtns.size();
					if(newIndex < ((List)questions).size()){
						if(tempIndex != newIndex){
							if(newIndex < tempIndex){
								while(newIndex < tempIndex){
									moveQuestionDown(questionDef);
									newIndex++;
								}
							}
							else{
								while(newIndex > tempIndex){
									moveQuestionUp(questionDef);
									newIndex--;
								}
							}
						}
					}
				}
			}
		}
		
		if (FormUtil.maintainOrderingOnRefresh()) {
			//now add the new questions which have just been added by refresh.
			int count = questions.size();
			for(int index = 0; index < count; index++){
				QuestionDef questionDef = getQuestionAt(index);
				if(groupQtnsDef.getQuestion(questionDef.getBinding()) == null)
					orderedQtns.add(questionDef);
			}
			
			//Now add the missing questions. Possibly they were added by user and not existing in the
			//original server side form.
			for(int index = 0; index < missingQtns.size(); index++){
				QuestionDef qtnDef = missingQtns.get(index);
				orderedQtns.add(new QuestionDef(qtnDef, this));
				orderedQtns.get(orderedQtns.size() - 1).setId(orderedQtns.size() + index + 1);
			}
			
			questions = orderedQtns;
		}
		else {
			//Now add the missing questions. Possibly they were added by user and not existing in the
			//original server side form.
			for(int index = 0; index < missingQtns.size(); index++){
				QuestionDef qtnDef = missingQtns.get(index);
				questions.add(new QuestionDef(qtnDef, this));
				((QuestionDef)questions.get(questions.size() - 1)).setId(questions.size() + index + 1);
			}
		}
	}
	
	
	/**
	 * Updates the xforms instance data nodes referenced by this 
 	 * repeat questions definition and its children.
	 * 
	 * @param parentDataNode the parent data node for this repeat questions definition.
	 */
	public void updateDataNodes(Element parentDataNode){
		if(questions == null)
			return;

		for(int i=0; i<questions.size(); i++)
			((QuestionDef)questions.elementAt(i)).updateDataNodes(parentDataNode);
	}
	
	public void buildLanguageNodes(String parentXpath,com.google.gwt.xml.client.Document doc, Element parentXformNode, Element parentLangNode, Map<String, String> changedXpaths){
		if(questions == null)
			return;

		for(int i=0; i<questions.size(); i++)
			((QuestionDef)questions.elementAt(i)).buildLanguageNodes(parentXpath,doc,parentXformNode,parentLangNode, changedXpaths);
	}
	
	public void removeBindNodes(){
		if(questions == null)
			return;
		
		for(int i=0; i<questions.size(); i++){
			QuestionDef questionDef = (QuestionDef)questions.elementAt(i);
			if(questionDef.getBindNode() != null && questionDef.getBindNode().getParentNode() != null) {
				questionDef.getBindNode().getParentNode().removeChild(questionDef.getBindNode());
			}
		}
	}
	
	public boolean isSubForm() {
		return qtnDef.getDataType() == QuestionDef.QTN_TYPE_SUBFORM;
	}
}