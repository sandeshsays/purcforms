package org.purc.purcforms.client.model;

import java.util.List;

/**
 * 
 * @author ctumwebaze
 * 
 */
public class FunctionDef {

    public static final int FUNCTION_AVG = 1;
    public static final int FUNCTION_MAX = 2;
    public static final int FUNCTION_MIN = 3;
    public static final int FUNCTION_SUM = 4;
    public static final int FUNCTION_COUNT = 5;

    /**
     * list of functional parameters. it can contain question definitions,
     * literal values and functions definitions as well
     */
    private List<Object> functionParameters;
    private int function = 0;

    /**
     * default constructor
     */
    public FunctionDef() {

    }

    /**
     * @return the function
     */
    public int getFunction() {
	return function;
    }

    /**
     * @param function
     *            the function to set
     */
    public void setFunction(int function) {
	this.function = function;
    }

    /**
     * @return the functionParameters
     */
    public List<Object> getFunctionParameters() {
	return functionParameters;
    }

    /**
     * @param functionParameters
     *            the functionParameters to set
     */
    public void setFunctionParameters(List<Object> functionParameters) {
	this.functionParameters = functionParameters;
    }

    public Object evaluate() {
	if (getFunction() == FunctionDef.FUNCTION_SUM)
	    return evaluateSum(functionParameters);
	else if (getFunction() == FunctionDef.FUNCTION_MIN)
	    return evaluateMin(functionParameters);
	else if (getFunction() == FunctionDef.FUNCTION_MAX)
	    return evaluateMax(functionParameters);
	else if (getFunction() == FunctionDef.FUNCTION_AVG)
	    return evaluateAvg(functionParameters);
	else if (getFunction() == FunctionDef.FUNCTION_COUNT)
	    return evaluateCount(functionParameters);

	return null;
    }

    private Object evaluateCount(List<Object> functionParameters2) {
	// TODO Auto-generated method stub
	return null;
    }

    private Object evaluateAvg(List<Object> functionParameters2) {
	// TODO Auto-generated method stub
	return null;
    }

    private Object evaluateMax(List<Object> functionParameters2) {
	// TODO Auto-generated method stub
	return null;
    }

    private Object evaluateMin(List<Object> functionParameters2) {
	// TODO Auto-generated method stub
	return null;
    }

    private Object evaluateSum(List<Object> functionParameters) {
	double val = 0;
	for (Object fxnParameter : functionParameters) {
	    if (fxnParameter instanceof QuestionDef) {
		QuestionDef qDef = ((QuestionDef) fxnParameter);
		switch (qDef.getDataType()) {
		case QuestionDef.QTN_TYPE_NUMERIC:
		case QuestionDef.QTN_TYPE_DECIMAL:
		    val += Double.parseDouble(qDef.getAnswer());
		    break;
		}
	    }else if(fxnParameter instanceof FunctionDef){
		val += (Double)((FunctionDef)fxnParameter).evaluate();
	    }else{
		val += (Double)fxnParameter;
	    }
	}
	
	return val;
    }

}
