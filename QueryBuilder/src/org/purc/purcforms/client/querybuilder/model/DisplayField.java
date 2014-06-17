package org.purc.purcforms.client.querybuilder.model;

import java.io.Serializable;

import org.purc.purcforms.client.model.QuestionDef;


/**
 * 
 * @author daniel
 *
 */
public class DisplayField implements Serializable {

	private String name;
	private String text;
	private String AggFunc;
	private int dataType;
	private QuestionDef questionDef;
	
	public DisplayField(){
		
	}

	public DisplayField(String name, String text, String AggFunc, int dataType, QuestionDef questionDef) {
		super();
		this.name = name;
		this.text = text;
		this.AggFunc = AggFunc;
		this.dataType = dataType;
		this.questionDef = questionDef;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAggFunc() {
		return AggFunc;
	}

	public void setAggFunc(String aggFunc) {
		AggFunc = aggFunc;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

    /**
     * @return the questionDef
     */
    public QuestionDef getQuestionDef() {
    	return questionDef;
    }
	
    /**
     * @param questionDef the questionDef to set
     */
    public void setQuestionDef(QuestionDef questionDef) {
    	this.questionDef = questionDef;
    }
}
