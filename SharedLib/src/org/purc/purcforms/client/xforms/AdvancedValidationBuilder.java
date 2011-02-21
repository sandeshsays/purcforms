package org.purc.purcforms.client.xforms;

import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.PageDef;
import org.purc.purcforms.client.model.QuestionDef;

import com.google.gwt.xml.client.Element;

/**
 * Builds constraints attributes of the xform document from the advanced
 * validation rule objects
 * 
 * @author ctumwebaze@gmail.com
 * 
 */
public class AdvancedValidationBuilder {

    public static void fromAdvancedValidationRule2Xform(FormDef formDef) {
	if (formDef != null) {
	    if (formDef.getPages() != null) {
		for (PageDef pageDef : formDef.getPages()) {
		    if (pageDef != null && pageDef.getQuestions() != null) {
			for (QuestionDef questionDef : pageDef.getQuestions()) {
			    Element node = questionDef.getBindNode();
			    if (node == null)
				node = questionDef.getControlNode();

			    if (questionDef.getAdvancedValidationRule() != null) {
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT);
				node.removeAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE);

				node.setAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT, questionDef
					.getAdvancedValidationRule().getValidationExpression());
				node.setAttribute(XformConstants.ATTRIBUTE_NAME_CONSTRAINT_MESSAGE, questionDef
					.getAdvancedValidationRule().getErrorMessage());
			    }
			}
		    }
		}
	    }
	}
    }
}
