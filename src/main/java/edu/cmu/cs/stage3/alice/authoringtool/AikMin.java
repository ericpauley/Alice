package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.Font;
import javax.swing.UIManager;

public class AikMin {
	//javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	//Integer.parseInt( authoringToolConfig.getValue( "fontSize" ) )
	public static void setFontSize(int fontSize){
		Font fontType = UIManager.getFont("Menu.font");
	    String name = fontType.getName();
	    int style = fontType.getStyle();
		Font font = new Font( name, style, fontSize);
		setUI(font);
	}
	
	private static void setUI(Font font){
		UIManager.put("Button.font",  font);
		UIManager.put("CheckBox.font",  font);
		UIManager.put("CheckBoxMenuItem.acceleratorFont",  font);
		UIManager.put("CheckBoxMenuItem.font",  font);
		UIManager.put("ComboBox.font",  font);
		UIManager.put("DesktopIcon.font",  font);
		UIManager.put("EditorPane.font",  font);
		UIManager.put("FormattedTextField.font",  font);
		UIManager.put("InternalFrame.titleFont",  font);
		UIManager.put("Label.font", font);
		UIManager.put("List.font",  font);
		UIManager.put("Menu.acceleratorFont",  font);
		UIManager.put("Menu.font",  font);
		UIManager.put("MenuBar.font",  font);
		UIManager.put("MenuItem.acceleratorFont",  font);
		UIManager.put("MenuItem.font",  font);
		UIManager.put("OptionPane.buttonFont", font);
		UIManager.put("OptionPane.messageFont",  font);
		UIManager.put("PasswordField.font", font);
		UIManager.put("PopupMenu.font",  font);
		UIManager.put("ProgressBar.font",  font);
		UIManager.put("RadioButton.font",  font);
		UIManager.put("RadioButtonMenuItem.acceleratorFont",  font);
		UIManager.put("RadioButtonMenuItem.font",  font);
		UIManager.put("Spinner.font",  font);
		UIManager.put("TabbedPane.font", font);
		UIManager.put("Table.font", font);
		UIManager.put("TableHeader.font",  font);
		UIManager.put("TextArea.font", font);
		UIManager.put("TextField.font", font);
		UIManager.put("TextPane.font",  font);
		UIManager.put("TitledBorder.font",  font);
		//UIManager.put("ToggleButton.font",  new Font());
		UIManager.put("ToolBar.font",  font);
		UIManager.put("ToolTip.font",  font);
		UIManager.put("Tree.font",  font);
		UIManager.put("Viewport.font", font);
		UIManager.put("JTitledPanel.title.font", font);
	}
}
