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
public class InsertSkipRuleCmd  implements ICommand {

	protected FormsTreeView view;
	protected TreeItem item;
	protected SkipRule skipRule;
	protected FormDef formDef;
	
	
	public InsertSkipRuleCmd(SkipRule skipRule, FormDef formDef, TreeItem item, FormsTreeView view){
		this.skipRule = skipRule;
		this.formDef = formDef;
		this.item = item;
		this.view = view;
	}
	
	public String getName(){
		return "Insert Skip Rule";
	}

	public void undo(){
		formDef.removeSkipRule(skipRule);
		view.setSelectedItem(item);
	}

	public void redo(){	
		formDef.addSkipRule(skipRule);
		view.setSelectedItem(item);
	}

	public boolean isWidgetCommand(){
		return false;
	}
}
