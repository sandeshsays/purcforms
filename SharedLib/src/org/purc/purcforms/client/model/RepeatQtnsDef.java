package org.purc.purcforms.client.model;

import java.util.Vector;


/**
 * Definition for repeat sets of questions. Basically this is just a specialized collection
 * of a set of repeating questions, together with reference to their parent question.
 * 
 * @author daniel
 *
 */
public class RepeatQtnsDef extends GroupQtnsDef {
	
	/** The maximum number of rows that this repeat questions definition can have. */
	private byte maxRows = -1;
	
	
	/**
	 * Creates a new repeat questions definition object.
	 */
	public RepeatQtnsDef() {
		 
	}
	
	/** Copy Constructor. */
	public RepeatQtnsDef(GroupQtnsDef repeatQtnsDef) {
		//setQtnDef(new QuestionDef(repeatQtnsDef.getQtnDef()));
		setQtnDef(repeatQtnsDef.getQtnDef());
		copyQuestions(repeatQtnsDef.getQuestions());
	}
	
	public RepeatQtnsDef(QuestionDef qtnDef) {
		setQtnDef(qtnDef);
	}
	
	public RepeatQtnsDef(QuestionDef qtnDef,Vector questions) {
		this(qtnDef);
		setQuestions(questions);
	}
	
	public void setMaxRows(byte maxRows){
		this.maxRows = maxRows;
	}
	
	public byte getMaxRows(){
		return maxRows;
	}
}
