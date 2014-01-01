package org.purc.purcforms.client.querybuilder.model;

import java.io.Serializable;


/**
 * 
 * @author daniel
 *
 */
public class FilterConditionRow  implements Serializable {

	private boolean selected;
	
	/**
     * @return the selected
     */
    public boolean isSelected() {
    	return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
}
