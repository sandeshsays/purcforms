package org.purc.purcforms.client.model;

import java.io.Serializable;

public class AdvancedValidationRule implements Serializable {

	/**
	 * generated serialization ID
	 */
	private static final long serialVersionUID = 5515275529397951098L;

	/**
	 * xpath expression
	 */
	private String validationExpression;

	/**
	 * error message displayed when the expression fails
	 */
	private String errorMessage;

	/**
	 * gets the xpath validation expression
	 * 
	 * @return the validationExpression
	 */
	public String getValidationExpression() {
		return validationExpression;
	}

	/**
	 * sets the given validation expression
	 * 
	 * @param validationExpression
	 *            the validationExpression to set
	 */
	public void setValidationExpression(String validationExpression) {
		this.validationExpression = validationExpression;
	}

	/**
	 * gets the error message to display when the validation expression fails
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * sets the error message to display when the validation expression fails
	 * 
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
