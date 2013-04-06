package org.purc.purcforms.client;

import java.util.List;

import org.purc.purcforms.client.controller.IFormDesignerListener;
import org.purc.purcforms.client.controller.ILocaleListChangeListener;
import org.purc.purcforms.client.locale.LocaleText;
import org.purc.purcforms.client.model.Locale;
import org.purc.purcforms.client.util.FormDesignerUtil;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.view.FormRunnerView;
import org.purc.purcforms.client.widget.GWTCFontPicker;
import org.purc.purcforms.client.widget.GWTCFontPicker.FontPickerType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.mogaleaf.client.common.widgets.ColorHandler;
import com.mogaleaf.client.common.widgets.SimpleColorPicker;

/**
 * This widget is the main tool bar for the form designer.
 * 
 * @author daniel
 *
 */
public class Toolbar extends Composite implements ILocaleListChangeListener, ColorHandler, ValueChangeHandler<GWTCFontPicker> {

	/**
	 * Tool bar images.
	 */
	public interface Images extends ClientBundle,FormRunnerView.Images {
		ImageResource newform();
		ImageResource open();
		ImageResource save();
		ImageResource moveup();
		ImageResource movedown();
		ImageResource add();
		ImageResource addchild();
		ImageResource delete();
		ImageResource justifyleft();
		ImageResource justifyright();
		ImageResource cut();
		ImageResource copy();
		ImageResource paste();
		ImageResource alignTop();
		ImageResource alignBottom();
		ImageResource samewidth();
		ImageResource sameheight();
		ImageResource samesize();
		ImageResource undo();
		ImageResource redo();
		ImageResource refresh();
		ImageResource find();
		ImageResource print();
		ImageResource bold();
		ImageResource italic();
		ImageResource underline();
		ImageResource font();
		ImageResource color();
		ImageResource fontsize();
	}
	 
	/** Main widget for this tool bar. */
	private HorizontalPanel panel = new HorizontalPanel();
	
	/** The tool bar buttons. */
	private PushButton btnAddNewItem;
	private PushButton btnAddNewChildItem;
	private PushButton btnDeleteItem;
	private PushButton btnMoveItemUp;
	private PushButton btnMoveItemDown;
	private PushButton btnNewForm;
	private PushButton btnOpenForm;
	private PushButton btnSaveForm;
	private PushButton btnAlignLeft;
	private PushButton btnAlignRight;
	private PushButton btnAlignTop;
	private PushButton btnAlignBottom;
	private PushButton btnSameWidth;
	private PushButton btnSameHeight;
	private PushButton btnSameSize;
	private PushButton btnCut;
	private PushButton btnCopy;
	private PushButton btnPaste;
	private PushButton btnRefresh;
	private PushButton btnUndo;
	private PushButton btnRedo;
	private PushButton btnFind;
	private PushButton btnPrint;
	private PushButton btnBold;
	private PushButton btnItalic;
	private PushButton btnUnderline;
	private PushButton btnFontFamily;
	private PushButton btnForeColor;
	private PushButton btnFontSize;
	
	/** Widget for separating tool bar buttons from each other. */
	private Label separatorWidget = new Label("  ");
	
	/** Widget to display the list of languages or locales. */
	private ListBox cbLanguages = new ListBox(false);
	
	/** The images for the tool bar icons. */
	private final Images images;
	
	/** Listener to the tool bar button click events. */
	private IFormDesignerListener controller;
	
	private SimpleColorPicker colorPicker = new SimpleColorPicker();
	private GWTCFontPicker fontFamilyPicker = new GWTCFontPicker(FontPickerType.FONT_FAMILY);
	private GWTCFontPicker fontSizePicker = new GWTCFontPicker(FontPickerType.FONT_SIZE);
	
	/**
	 * Creates a new instance of the tool bar.
	 * 
	 * @param images the images for tool bar icons.
	 * @param controller listener to the tool bar button click events.
	 */
	public Toolbar(Images images,IFormDesignerListener controller){
		this.images = images;
		this.controller = controller;
		setupToolbar();
		setupClickListeners();
		initWidget(panel);
		
		Context.addLocaleListChangeListener(this);
	}
	
	/**
	 * Sets up the tool bar.
	 */
	private void setupToolbar(){
		btnNewForm = new PushButton(FormUtil.createImage(images.newform()));
		btnOpenForm = new PushButton(FormUtil.createImage(images.open()));
		btnSaveForm = new PushButton(FormUtil.createImage(images.save()));
		btnFind = new PushButton(FormUtil.createImage(images.find()));
		btnPrint = new PushButton(FormUtil.createImage(images.print()));
		
		btnAddNewItem = new PushButton(FormUtil.createImage(images.add()));
		btnAddNewChildItem = new PushButton(FormUtil.createImage(images.addchild()));
		btnDeleteItem = new PushButton(FormUtil.createImage(images.delete()));
		btnMoveItemUp = new PushButton(FormUtil.createImage(images.moveup()));
		btnMoveItemDown = new PushButton(FormUtil.createImage(images.movedown()));
		
		btnAlignLeft = new PushButton(FormUtil.createImage(images.justifyleft()));
		btnAlignRight = new PushButton(FormUtil.createImage(images.justifyright()));
		btnAlignTop = new PushButton(FormUtil.createImage(images.alignTop()));
		btnAlignBottom = new PushButton(FormUtil.createImage(images.alignBottom()));
		btnSameWidth = new PushButton(FormUtil.createImage(images.samewidth()));
		btnSameHeight = new PushButton(FormUtil.createImage(images.sameheight()));
		btnSameSize = new PushButton(FormUtil.createImage(images.samesize()));
		
		btnCut = new PushButton(FormUtil.createImage(images.cut()));
		btnCopy = new PushButton(FormUtil.createImage(images.copy()));
		btnPaste = new PushButton(FormUtil.createImage(images.paste()));
		btnRefresh = new PushButton(FormUtil.createImage(images.refresh()));
		
		btnUndo = new PushButton(FormUtil.createImage(images.undo()));
		btnRedo = new PushButton(FormUtil.createImage(images.redo()));
		
		btnBold = new PushButton(FormUtil.createImage(images.bold()));
		btnItalic = new PushButton(FormUtil.createImage(images.italic()));
		btnUnderline = new PushButton(FormUtil.createImage(images.underline()));
		btnFontFamily = new PushButton(FormUtil.createImage(images.font()));
		btnForeColor = new PushButton(FormUtil.createImage(images.color()));
		btnFontSize = new PushButton(FormUtil.createImage(images.fontsize()));
		
		/*lbFontSize = new ListBox();
		lbFontSize.setWidth("80" + PurcConstants.UNITS);
		lbFontSize.setHeight("25" + PurcConstants.UNITS);*/
		
		btnNewForm.setTitle(LocaleText.get("newForm"));
		btnSaveForm.setTitle(LocaleText.get("save"));
		btnOpenForm.setTitle(LocaleText.get("open"));
		btnFind.setTitle(LocaleText.get("find"));
		btnPrint.setTitle(LocaleText.get("print"));
		
		btnAddNewItem.setTitle(LocaleText.get("addNew"));
		btnAddNewChildItem.setTitle(LocaleText.get("addNewChild"));
		btnDeleteItem.setTitle(LocaleText.get("deleteSelected"));
		btnMoveItemUp.setTitle(LocaleText.get("moveUp"));
		btnMoveItemDown.setTitle(LocaleText.get("moveDown"));
		
		btnCut.setTitle(LocaleText.get("cut"));
		btnCopy.setTitle(LocaleText.get("copy"));
		btnPaste.setTitle(LocaleText.get("paste"));
		btnRefresh.setTitle(LocaleText.get("refresh"));
		
		btnAlignLeft.setTitle(LocaleText.get("alignLeft"));
		btnAlignRight.setTitle(LocaleText.get("alignRight"));
		btnAlignTop.setTitle(LocaleText.get("alignTop"));
		btnAlignBottom.setTitle(LocaleText.get("alignBottom"));
		btnSameWidth.setTitle(LocaleText.get("makeSameWidth"));
		btnSameHeight.setTitle(LocaleText.get("makeSameHeight"));
		btnSameSize.setTitle(LocaleText.get("makeSameSize"));
		
		btnUndo.setTitle(LocaleText.get("undo"));
		btnRedo.setTitle(LocaleText.get("redo"));
		
		btnBold.setTitle(LocaleText.get("bold"));
		btnItalic.setTitle(LocaleText.get("italic"));
		btnUnderline.setTitle(LocaleText.get("underline"));
		btnFontFamily.setTitle(LocaleText.get("fontFamily"));
		btnForeColor.setTitle(LocaleText.get("foreColor"));
		btnFontSize.setTitle(LocaleText.get("fontSize"));
		
		if(Context.isOfflineMode())
			panel.add(btnNewForm);
		
		panel.add(btnOpenForm);
		panel.add(btnPrint);
		panel.add(btnFind);
		panel.add(btnSaveForm);
		
		panel.add(separatorWidget);
		
		panel.add(btnAddNewItem);
		panel.add(btnAddNewChildItem);
		panel.add(btnDeleteItem);
		panel.add(separatorWidget);
		panel.add(btnMoveItemUp);
		panel.add(btnMoveItemDown);
		
		panel.add(separatorWidget);
		panel.add(btnCut);
		panel.add(btnCopy);
		panel.add(btnPaste);
		
		panel.add(separatorWidget);
		//panel.add(btnRefresh);
		
		panel.add(btnFontFamily);
		panel.add(btnFontSize);
		panel.add(btnBold);
		panel.add(btnItalic);
		panel.add(btnUnderline);
		panel.add(btnForeColor);
		
		panel.add(separatorWidget);
		panel.add(btnAlignLeft);
		panel.add(btnAlignRight);
		panel.add(btnAlignTop);
		panel.add(btnAlignBottom);
		
		panel.add(separatorWidget);
		panel.add(btnSameWidth);
		panel.add(btnSameHeight);
		panel.add(btnSameSize);
		
		panel.add(separatorWidget);
		panel.add(btnUndo);
		panel.add(btnRedo);
		
		Label label = new Label(FormDesignerUtil.getTitle());
		panel.add(label);
		panel.setCellWidth(label,"100%");
		panel.setCellHorizontalAlignment(label,HasHorizontalAlignment.ALIGN_CENTER);
		
		label = new Label(LocaleText.get("language"));
		panel.add(label);
		panel.setCellHorizontalAlignment(label,HasHorizontalAlignment.ALIGN_RIGHT);
		
		populateLocales();
		
		cbLanguages.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event){
				int index = getLocaleIndex(Context.getLocale().getKey());
				ListBox listBox = (ListBox)event.getSource();
				if(!controller.changeLocale(new Locale(listBox.getValue(listBox.getSelectedIndex()),listBox.getItemText(listBox.getSelectedIndex()))))
					cbLanguages.setSelectedIndex(index);
			}
		});
		
		panel.add(cbLanguages);
		panel.setCellHorizontalAlignment(cbLanguages,HasHorizontalAlignment.ALIGN_RIGHT);
		
		//Set a 3 pixels spacing between tool bar buttons.
		panel.setSpacing(3);
		
		Context.getCommandHistory().setUndoButton(btnUndo);
		Context.getCommandHistory().setRedoButton(btnRedo);
	}
	
	/**
	 * Setup button click event handlers.
	 */
	private void setupClickListeners(){
		btnNewForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.newForm();}});
		
		btnOpenForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.openForm();}});
		
		btnSaveForm.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.saveForm();}});
		
		btnPrint.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.printForm();}});
		
		btnFind.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.find();}});
		
		btnAddNewItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewItem();}});
		
		btnAddNewChildItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.addNewChildItem();}});
		
		btnDeleteItem.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.deleteSelectedItem();}});
		
		btnMoveItemUp.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.moveItemUp();}});
		
		btnMoveItemDown.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.moveItemDown();}});
		
		btnAlignLeft.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignLeft();}});
		
		btnAlignRight.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignRight();}});
		
		btnAlignTop.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignTop();}});
		
		btnAlignBottom.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.alignBottom();}});
		
		btnCut.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.cutItem();}});
		
		btnCopy.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.copyItem();}});
		
		btnPaste.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.pasteItem();}});
		
		btnSameWidth.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameWidth();}});
		
		btnSameHeight.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameHeight();}});
		
		btnSameSize.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.makeSameSize();}});
		
		btnRefresh.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.refresh(this);}});
		
		btnUndo.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.undo();}});
		
		btnRedo.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.redo();}});
		
		btnBold.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.bold();}});
		
		btnItalic.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.italic();}});
		
		btnUnderline.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){controller.underline();}});
		
		btnForeColor.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
					colorPicker.setPopupPosition(btnForeColor.getAbsoluteLeft(), btnForeColor.getAbsoluteTop());
					colorPicker.show();
				}});
		
		btnFontFamily.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){			
					fontFamilyPicker.setPopupPosition(btnFontFamily.getAbsoluteLeft(), btnFontFamily.getAbsoluteTop());
					fontFamilyPicker.show();
				}});
		
		btnFontSize.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event){			
					fontSizePicker.setPopupPosition(btnFontSize.getAbsoluteLeft(), btnFontSize.getAbsoluteTop());
					fontSizePicker.show();
				}});
		
		colorPicker.addListner(this);
		fontFamilyPicker.addValueChangeHandler(this);
		fontSizePicker.addValueChangeHandler(this);
	}
	
	/**
	 * Populates the locale drop down with a list of locales supported by the form designer.
	 */
	public void populateLocales(){
		cbLanguages.clear();
		
		List<Locale> locales = Context.getLocales();
		if(locales == null)
			return;
		
		for(Locale locale : locales)
			cbLanguages.addItem(locale.getName(), locale.getKey());
		
		Locale locale = Context.getLocale();
		if(locale != null)
			cbLanguages.setSelectedIndex(getLocaleIndex(locale.getKey()));
	}
	
	
	private int getLocaleIndex(String localeKey){		
		List<Locale> locales = Context.getLocales();
		assert(locales != null);
		
		for(int index = 0; index < locales.size(); index++){
			Locale locale = locales.get(index);
			if(locale.getKey().equals(localeKey))
				return index;
		}
		
		return 0;
	}
	
	public void onLocaleListChanged(){
		populateLocales();
	}
	
	@Override
	public void newColorSelected(String color) {
	     controller.foreColor(color);
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<GWTCFontPicker> event) {
		GWTCFontPicker fontPicker = event.getValue();
		
		if (fontPicker == fontFamilyPicker) {
			controller.fontFamily(fontPicker.getFontName());
		}
		else if(fontPicker == fontSizePicker) {
			controller.fontSize(fontPicker.getFontSize());
		}
	}
}
