package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.model.FormDef;
import org.purc.purcforms.client.model.SkipRule;
import org.purc.purcforms.client.view.FormsTreeView;

import com.google.gwt.user.client.ui.TreeItem;


/**
 * 
 * @author danielkayiwa
 *
 */
public class DeleteSkipRuleCmd extends InsertSkipRuleCmd {

	public DeleteSkipRuleCmd(SkipRule skipRule, FormDef formDef, TreeItem item, FormsTreeView view){
		super(skipRule, formDef, item, view);
	}
	
	public String getName(){
		return "Delete Skip Rule";
	}

	public void undo(){
		formDef.addSkipRule(skipRule);
		view.setSelectedItem(item);
	}

	public void redo(){	
		formDef.removeSkipRule(skipRule);
		view.setSelectedItem(item);
	}
}
