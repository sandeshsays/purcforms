package org.purc.purcforms.client.widget.grid;

import java.util.ArrayList;
import java.util.List;

import org.purc.purcforms.client.widget.DesignWidgetWrapper;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class GridPanel extends AbsolutePanel {

	private int width;
	private int height;
	private int gridVertSize;
	private int gridHorzSize;
	private PickupDragController gridConstrainedDragController;
	private PickupDragController unconstrainedDragController;

	public GridPanel(int gridVertSize, int gridHorzSize, int width, int height,
			boolean dragAndDropEnabled) {
		super();
		this.width = width;
		this.height = height;
		/*
		 * this.expresserCanvas = expresserCanvas; this.model = model;
		 * eventManager = new EventManager(this);
		 */
		setGridSize(gridVertSize, gridHorzSize);
		setPixelSize(width, height);
		if (dragAndDropEnabled) {
			enableDragAndDrop();
		}
	}

	private void enableDragAndDrop() {
		gridConstrainedDragController = new PickupDragController(this, true);
		GridConstrainedSelectionStyleDropController gridConstrainedDropController = new GridConstrainedSelectionStyleDropController(
				this, gridVertSize, gridVertSize);
		gridConstrainedDragController
				.registerDropController(gridConstrainedDropController);
		gridConstrainedDragController.setBehaviorMultipleSelection(true);
		// dragController.setBehaviorConstrainedToBoundaryPanel(true);
		unconstrainedDragController = new PickupDragController(this, true);
		// ignore small movements to enable popup menus
		gridConstrainedDragController.setBehaviorDragStartSensitivity(4);
		unconstrainedDragController.setBehaviorDragStartSensitivity(4);
	}

	public void recomputePixelSize() {
		setPixelSize(width, height);
	}

	public void setGridSize(int gridVertSize, int gridHorzSize) {
		if (this.gridVertSize == gridVertSize) {
			return;
		}
		
		this.gridVertSize = gridVertSize;
		this.gridHorzSize = gridHorzSize;
		
		drawGrid();
	}
	
	public void resizeGrid(int width, int height){
		this.width = width;
		this.height = height;
		
		drawGrid();
	}
	
	public void drawGrid(){
		int widgetCount = getWidgetCount();
		// count backwards since removing widgets as it runs
		List<LocatedWidget> widgetsToPutBack = new ArrayList<LocatedWidget>();
		for (int i = widgetCount - 1; i >= 0; i--) {
			Widget widget = getWidget(i);
			/*
			 * if (widget instanceof ShapeView) { ((ShapeView)
			 * widget).setGridSize(gridSize); }
			 */// ????????????????????????
			if (!(widget instanceof VerticalGridLine || widget instanceof HorizontalGridLine)) {
				widgetsToPutBack.add(new LocatedWidget(widget, widget
						.getAbsoluteLeft(), widget.getAbsoluteTop()));
			}
			if (widget instanceof GridLine) {
				widget.removeFromParent();
			}
			// temporarily remove every one
			// widget.removeFromParent();
		}
		setPixelSize(width, height);
		//if (gridSize >= 10) {
			addGridLines();
		//}
		// grid lines are always underneath other widgets
		int canvasAbsoluteLeft = getAbsoluteLeft();
		int canvasAbsoluteTop = getAbsoluteTop();
		// put them back in the right z-order
		int size = widgetsToPutBack.size();
		for (int i = size - 1; i >= 0; i--) {
			LocatedWidget locatedWidget = widgetsToPutBack.get(i);
			add(locatedWidget.getWidget(), locatedWidget.getAbsoluteLeft()
					- canvasAbsoluteLeft, locatedWidget.getAbsoluteTop()
					- canvasAbsoluteTop);
		}
	}

	protected void addGridLines() {
		// draw the grid lines at gridSize-1 so that the tiles themselves
		// can be from 0, 0 and have a size of gridSize-1
		// int bottomLeftBlank = expresserCanvas.getRuleAreaHeight();
		// int visibleHeight = height-bottomLeftBlank;
		// if (visibleHeight < 1) {
		// return;
		// }
		clear();
		
		for (int i = gridVertSize - 1; i < width; i += gridVertSize) {
			add(new VerticalGridLine(height), i, 0);
		}
		for (int j = gridHorzSize - 1; j < height; j += gridHorzSize) {
			add(new HorizontalGridLine(width), 0, j);
		}
	}

	/**
	 * called when canvas dimensions changed adjusts the length of existing grid
	 * lines and removes or adds grid lines as needed
	 * 
	 * @param deltaHeight
	 * @param deltaWidth
	 */
	protected void adjustGridLines(int newWidth, int newHeight) {
		// this draws grid lines under the rule area -- not a problem
		// and then when resized those lines are there
		// int bottomLeftBlank = expresserCanvas.getRuleAreaHeight();
		// int visibleHeight = newHeight-bottomLeftBlank;
		// if (visibleHeight < 1) {
		// return;
		// }
		// int maxLeft = newWidth+gridSize-1;
		// int maxTop = newHeight+gridSize-1;
		int widgetCount = getWidgetCount();
		// count backwards since may remove some widgets here
		for (int i = widgetCount - 1; i >= 0; i--) {
			Widget widget = getWidget(i);
			if (widget instanceof VerticalGridLine) {
				if (getWidgetLeft(widget) >= newWidth) {
					remove(widget);
				} else {
					widget.setHeight(newHeight + "px");
				}
			}
			if (widget instanceof HorizontalGridLine) {
				if (getWidgetTop(widget) >= newHeight) {
					remove(widget);
				} else {
					widget.setWidth(newWidth + "px");
				}
			}
		}
		// add any additional grid lines needed
		int firstX = (width / gridVertSize) * gridVertSize + gridVertSize - 1;
		for (int i = firstX; i <= newWidth; i += gridVertSize) {
			add(new VerticalGridLine(newHeight), i, 0);
		}
		int firstY = (height / gridVertSize) * gridVertSize + gridVertSize - 1;
		for (int j = firstY; j <= newHeight; j += gridVertSize) {
			add(new HorizontalGridLine(newWidth), 0, j);
		}
	}

	protected int nearestGridX(int x) {
		return gridVertSize * (int) Math.round(((double) x) / gridVertSize);
	}

	protected int nearestGridY(int y) {
		return gridVertSize * (int) Math.round(((double) y) / gridVertSize);
	}

	public PickupDragController getGridConstrainedDragController() {
		return gridConstrainedDragController;
	}

	public int getGridSize() {
		return gridVertSize;
	}

	private boolean centerInside(int widgetLeft, int widgetTop,
			int widgetWidth, int widgetHeight, int rectangleLeft,
			int rectangleTop, int rectangleWidth, int rectangleHeight) {
		int widgetX = widgetLeft + widgetWidth / 2;
		int widgetY = widgetTop + widgetHeight / 2;
		int rectangleRight = rectangleLeft + rectangleWidth;
		int rectangleBottom = rectangleTop + rectangleHeight;
		return widgetX > rectangleLeft && widgetX < rectangleRight
				&& widgetY > rectangleTop && widgetY < rectangleBottom;
	}

	public void clearSelection() {
		gridConstrainedDragController.clearSelection();
	}

	public boolean containsShapeAt(int x, int y) {
		int widgetCount = getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			Widget widget = getWidget(i);

			/*
			 * if (widget instanceof DesignWidgetWrapper) { if
			 * (((DesignWidgetWrapper) widget).contains(x, y)) { return true; }
			 * }
			 */

		}
		return false;
	}

	public Widget widgetContainingPoint(int x, int y) {
		int widgetCount = getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			Widget widget = getWidget(i);
			if (widget instanceof GridLine) {
				// grid lines aren't really objects -- just a way to draw the
				// canvas
				continue;
			}
			int left = widget.getAbsoluteLeft();
			if (x < left) {
				continue;
			}
			int right = left + widget.getOffsetWidth();
			if (x > right) {
				continue;
			}
			int top = widget.getAbsoluteTop();
			if (y < top) {
				continue;
			}
			int bottom = top + widget.getOffsetHeight();
			if (y > bottom) {
				continue;
			}
			return widget;
		}
		return null;
	}

	public PickupDragController getUnconstrainedDragController() {
		return unconstrainedDragController;
	}

	public void moveAllBy(int deltaX, int deltaY) {
		int widgetCount = getWidgetCount();
		for (int i = 0; i < widgetCount; i++) {
			Widget widget = getWidget(i);
			if (widget instanceof DesignWidgetWrapper) {
				int left = getWidgetLeft(widget) + deltaX;
				int top = getWidgetTop(widget) + deltaY;
				setWidgetPosition(widget, left, top);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
