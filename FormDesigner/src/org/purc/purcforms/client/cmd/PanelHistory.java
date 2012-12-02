package org.purc.purcforms.client.cmd;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class PanelHistory {
	
	private AbsolutePanel panel;
	private PanelHistory panelHistory;
	private int samePanelCount = 1;
	
	public PanelHistory() {
		
	}
	
	public PanelHistory(PanelHistory panelHistory, AbsolutePanel panel) {
		setPanelHistory(panelHistory);
		setPanel(panel);
	}

	public AbsolutePanel getPanel() {
		return panel;
	}

	public void setPanel(AbsolutePanel panel) {
		this.panel = panel;
	}

	public PanelHistory getPanelHistory() {
		return panelHistory;
	}

	public void setPanelHistory(PanelHistory panelHistory) {
		this.panelHistory = panelHistory;
	}

	public int getSamePanelCount() {
		return samePanelCount;
	}

	public void incrementSamePanelCount() {
		this.samePanelCount++;
	}
	
	public void decrementSamePanelCount() {
		this.samePanelCount--;
	}
}
