package org.purc.purcforms.client.cmd;

import org.purc.purcforms.client.view.DesignGroupView;
import org.purc.purcforms.client.view.DesignSurfaceView;
import org.purc.purcforms.client.widget.DesignWidgetWrapper;


/**
 * 
 * @author danielkayiwa
 *
 */
public class ChangeViewCmd extends ChangeWidgetCmd{


	public ChangeViewCmd(byte property, String oldValue, DesignGroupView view){
		this.view = view;
		this.property = property;
		this.oldValue = oldValue;
		this.panel = view.getPanel();
	}

	public String getName(){
		return "Change View " + getFieldName() + " Property";
	}

	public void undo(){	
		execute();
	}

	public void redo(){
		execute();
	}

	public boolean isWidgetCommand(){
		return true;
	}

	private void execute(){
		//view.clearSelection(); //Do not clear the view's selected widget.
		setProperyValue(oldValue);
	}

	private void setProperyValue(String value){

		switch(property){
		case PROPERTY_WIDTH:
			oldValue = view.getWidth();
			if(view instanceof DesignSurfaceView)
				((DesignSurfaceView)view).setWidth(value);
			else	
				view.setWidth(value);
			break;
		case PROPERTY_HEIGHT:
			oldValue = view.getHeight();
			if(view instanceof DesignSurfaceView)
				((DesignSurfaceView)view).setHeight(value);
			else
				view.setHeight(value);
			break;
		case PROPERTY_BACKGROUND_COLOR:
			oldValue = view.getBackgroundColor();
			view.setBackgroundColor(value);
			break;
		case PROPERTY_BORDER_STYLE:
			oldValue = ((DesignWidgetWrapper)view.getParent().getParent()).getBorderStyle();
			((DesignWidgetWrapper)view.getParent().getParent()).setBorderStyle(value);
			break;
		case PROPERTY_BORDER_WIDTH:
			oldValue = ((DesignWidgetWrapper)view.getParent().getParent()).getBorderWidth();
			((DesignWidgetWrapper)view.getParent().getParent()).setBorderWidth(value);
			break;
		case PROPERTY_BORDER_COLOR:
			oldValue = ((DesignWidgetWrapper)view.getParent().getParent()).getBorderColor();
			((DesignWidgetWrapper)view.getParent().getParent()).setBorderColor(value);
			break;
		}
	}
}
