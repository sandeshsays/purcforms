package org.purc.purcforms.client.model;

import java.util.HashMap;

import com.google.gwt.xml.client.Element;


public class SubFormRecord {
	
	private HashMap<String, Object> record = new HashMap<String, Object>();
	private Element parentDataNode;
	private HashMap<String, Element> dataNodes = new HashMap<String, Element>();
	
	public SubFormRecord() {
		
	}
	
	public SubFormRecord(Element parentDataNode) {
		setParentDataNode(parentDataNode);
	}
	
    /**
     * @return the record
     */
    public HashMap<String, Object> getRecord() {
    	return record;
    }
	
    /**
     * @param record the record to set
     */
    public void setRecord(HashMap<String, Object> record) {
    	this.record = record;
    }
	
    /**
     * @return the parentDataNode
     */
    public Element getParentDataNode() {
    	return parentDataNode;
    }
	
    /**
     * @param parentDataNode the parentDataNode to set
     */
    public void setParentDataNode(Element dataNode) {
    	this.parentDataNode = dataNode;
    }
    
    public void put(String key, Object value) {
    	record.put(key, value);
    }
    
    public Object get(String key) {
    	return record.get(key);
    }
    
    public void setDataNode(String binding, Element dataNode) {
    	dataNodes.put(binding, dataNode);
    }
    
    public Element getDataNode(String binding) {
    	return dataNodes.get(binding);
    }
}
