package edu.cmu.cs.stage3.swing;

import javax.swing.WindowConstants;

abstract class ReturnValueTracker {
	private javax.swing.JDialog m_dialog;
	private int m_returnValue;
	private java.awt.event.WindowListener m_windowListener = new java.awt.event.WindowAdapter() {
		
		public void windowClosing( java.awt.event.WindowEvent e ) {
			ReturnValueTracker.this.onWindowClosing( e );
		}
	};
	protected abstract void onWindowClosing( java.awt.event.WindowEvent e );
	public ReturnValueTracker( javax.swing.JDialog dialog, int initialReturnValue ) {
		m_dialog = dialog;
		m_returnValue = initialReturnValue;
		m_dialog.addWindowListener( m_windowListener );
	}
	public void setReturnValue( int returnValue ) {
		m_returnValue = returnValue;
	}
	public int getReturnValue() {
		return m_returnValue;
	}
	protected javax.swing.JDialog getDialog() {
		return m_dialog;
	}

	public void removeListeners() {
		m_dialog.removeWindowListener( m_windowListener );
	}
}

public class DialogManager {
	private static java.util.Stack s_stack = new java.util.Stack();
	private static javax.swing.JDialog createModalDialog( String title ) {
		java.awt.Component parent;
		try {
			parent = (java.awt.Component)s_stack.peek();
		} catch( java.util.EmptyStackException ese ) {
			parent = new java.awt.Frame( "empty stack" );
			parent.setVisible( true );
		}
		javax.swing.JDialog dialog;
		if( parent instanceof java.awt.Dialog ) {
			dialog = new javax.swing.JDialog( (java.awt.Dialog)parent, title, true );
		} else {
			dialog = new javax.swing.JDialog( (java.awt.Frame)parent, title, true );
		}
		dialog.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		return dialog;
	}
	private static void showModalDialog( javax.swing.JDialog dialog, boolean requiresSetLocationRelativeToOwner ) {
		if( dialog.isModal() ) {
			if( requiresSetLocationRelativeToOwner ) {
				dialog.setLocationRelativeTo( dialog.getOwner() );
			}
			s_stack.push( dialog );
			try {
				dialog.setVisible( true );
			} finally {
				s_stack.pop();
			}
		} else {
			throw new RuntimeException( "DialogManager only handles *modal* dialogs" );
		}
	}
	private static void showModalDialog( javax.swing.JDialog dialog ) {
		showModalDialog( dialog, true );
	}

	public static void initialize( java.awt.Window root ) {
		s_stack.clear();
		s_stack.push( root );
	}

	public static int showDialog( ContentPane contentPane ) {
		javax.swing.JDialog dialog = createModalDialog( contentPane.getTitle() );
		dialog.getContentPane().setLayout( new java.awt.BorderLayout() );
		dialog.getContentPane().add( contentPane, java.awt.BorderLayout.CENTER );
		dialog.pack();

		class ContentPaneReturnValueTracker extends ReturnValueTracker {
			private ContentPane m_contentPane;
			private java.awt.event.ActionListener m_okListener = new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent e ) {
					if( m_contentPane.isReadyToDispose( ContentPane.OK_OPTION ) ) {
						setReturnValue( ContentPane.OK_OPTION );
						getDialog().dispose();
					}
				}
			};
			private java.awt.event.ActionListener m_cancelListener = new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent e ) {
					if( m_contentPane.isReadyToDispose( ContentPane.CANCEL_OPTION ) ) {
						setReturnValue( ContentPane.CANCEL_OPTION );
						getDialog().dispose();
					}
				}
			};

			public ContentPaneReturnValueTracker( javax.swing.JDialog dialog, ContentPane contentPane ) {
				super( dialog, ContentPane.CANCEL_OPTION );
				m_contentPane = contentPane;
				m_contentPane.addOKActionListener( m_okListener );
				m_contentPane.addCancelActionListener( m_cancelListener );
			}

			
			protected void onWindowClosing( java.awt.event.WindowEvent e ) {
				if( m_contentPane.isReadyToDispose( ContentPane.CANCEL_OPTION ) ) {
					setReturnValue( ContentPane.CANCEL_OPTION );
					m_contentPane.handleDispose();
				}
			}			
			
			public void removeListeners() {
				super.removeListeners();
				m_contentPane.removeOKActionListener( m_okListener );
				m_contentPane.removeCancelActionListener( m_cancelListener );
			}
		}

		ContentPaneReturnValueTracker returnValueTracker = new ContentPaneReturnValueTracker( dialog, contentPane );
		
		if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.isresizable == false)
			dialog.setResizable(false);
		
		contentPane.preDialogShow( dialog );
		showModalDialog( dialog, false );
		contentPane.postDialogShow( dialog );

		returnValueTracker.removeListeners();
		
		return returnValueTracker.getReturnValue();
	}
	
	public static java.awt.Color showDialog( final javax.swing.JColorChooser colorChooser, String title, java.awt.Color initialColor ) {
		class ColorTracker implements java.awt.event.ActionListener {
			java.awt.Color m_color = null;
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				m_color = colorChooser.getColor();
			}
			public java.awt.Color getColor() {
				return m_color;
			}
		}
		ColorTracker colorTracker = new ColorTracker();
		java.awt.Component parent = (java.awt.Component)s_stack.peek();
		javax.swing.JDialog dialog = javax.swing.JColorChooser.createDialog( parent, title, true, colorChooser, colorTracker, null );
		showModalDialog( dialog );
		return colorTracker.getColor();
	}

	public static int showDialog( javax.swing.JFileChooser fileChooser, String approveButtonText ) {
		if(approveButtonText != null) {
			fileChooser.setApproveButtonText(approveButtonText);
			fileChooser.setDialogType( javax.swing.JFileChooser.CUSTOM_DIALOG );
		}
		String title = fileChooser.getUI().getDialogTitle( fileChooser );
		fileChooser.getAccessibleContext().setAccessibleDescription(title);

		javax.swing.JDialog dialog = createModalDialog( title );

		java.awt.Container contentPane = dialog.getContentPane();
		contentPane.setLayout( new java.awt.BorderLayout() );
		contentPane.add( fileChooser, java.awt.BorderLayout.CENTER );
 
		//if( javax.swing.JDialog.isDefaultLookAndFeelDecorated() ) {
		//	boolean supportsWindowDecorations = javax.swing.UIManager.getLookAndFeel().getSupportsWindowDecorations();
		//	if (supportsWindowDecorations) {
		//		dialog.getRootPane().setWindowDecorationStyle( javax.swing.JRootPane.FILE_CHOOSER_DIALOG );
		//	}
		//}

		dialog.pack();

		class FileChooserReturnValueTracker extends ReturnValueTracker {
			private javax.swing.JFileChooser m_fileChooser;
			private java.awt.event.ActionListener m_actionListener = new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent e ) {
					String command = e.getActionCommand();
					if( command.equals( javax.swing.JFileChooser.APPROVE_SELECTION ) ) {
						setReturnValue( javax.swing.JFileChooser.APPROVE_OPTION );
					} else if( command.equals( javax.swing.JFileChooser.CANCEL_SELECTION ) ) {
						setReturnValue( javax.swing.JFileChooser.CANCEL_OPTION );
					}
					getDialog().dispose();
				}
			};

			public FileChooserReturnValueTracker( javax.swing.JDialog dialog, javax.swing.JFileChooser fileChooser ) {
				super( dialog, javax.swing.JFileChooser.CANCEL_OPTION );//, javax.swing.JFileChooser.ERROR_OPTION );
				m_fileChooser = fileChooser;
				m_fileChooser.addActionListener( m_actionListener );
			}
			
			protected void onWindowClosing( java.awt.event.WindowEvent e ) {
				setReturnValue( javax.swing.JFileChooser.CANCEL_OPTION );
				getDialog().dispose();
			}
			
			public void removeListeners() {
				super.removeListeners();
				m_fileChooser.removeActionListener( m_actionListener );
			}
		}

		FileChooserReturnValueTracker returnValueTracker = new FileChooserReturnValueTracker( dialog, fileChooser );

		fileChooser.rescanCurrentDirectory();
	
		showModalDialog( dialog );

		returnValueTracker.removeListeners();
		
		return returnValueTracker.getReturnValue();
	}
	public static int showOpenDialog( javax.swing.JFileChooser fileChooser ) {
		fileChooser.setDialogType( javax.swing.JFileChooser.OPEN_DIALOG );
		return showDialog( fileChooser, null );
	}
	public static int showSaveDialog( javax.swing.JFileChooser fileChooser ) {
		fileChooser.setDialogType( javax.swing.JFileChooser.SAVE_DIALOG );
		return showDialog( fileChooser, null );
	}
	
	private static String getUIManagerString( Object key ) {
		//todo: java1.4
		//java.awt.Component component = (java.awt.Component)s_stack.peek();
		//return javax.swing.UIManager.getString( key, component.getLocale() );
		if( key.equals( "OptionPane.inputDialogTitle" ) ) {
			return "Input";
		} else if( key.equals( "OptionPane.messageDialogTitle" ) ) {
			return "Message";
		} else if( key.equals( "OptionPane.titleText" ) ) {
			return "Select an Option";
		}
		return "";
	}

	public static String showInputDialog( Object message ) {
		return showInputDialog( message, getUIManagerString( "OptionPane.inputDialogTitle" ), javax.swing.JOptionPane.QUESTION_MESSAGE );
	}
	public static String showInputDialog( Object message, Object initialSelectionValue ) {
		return (String)showInputDialog( message, getUIManagerString( "OptionPane.inputDialogTitle" ), javax.swing.JOptionPane.QUESTION_MESSAGE, null, null, initialSelectionValue );
	}

	public static String showInputDialog( Object message, String title, int messageType ) {
		return (String)showInputDialog( message, title, messageType, null, null, null);
	}

	public static Object showInputDialog( Object message, String title, int messageType, javax.swing.Icon icon, Object[] selectionValues, Object initialSelectionValue ) {
		javax.swing.JOptionPane pane = new javax.swing.JOptionPane( message, messageType, javax.swing.JOptionPane.OK_CANCEL_OPTION, icon, null, null);

		pane.setWantsInput( true );
		pane.setSelectionValues( selectionValues );
		pane.setInitialSelectionValue( initialSelectionValue );

		java.awt.Component parent = (java.awt.Component)s_stack.peek();
		pane.setComponentOrientation( parent.getComponentOrientation() );

		javax.swing.JDialog dialog = pane.createDialog( parent, title );

		pane.selectInitialValue();
		
		showModalDialog( dialog );

		Object value = pane.getInputValue();

		if (value == javax.swing.JOptionPane.UNINITIALIZED_VALUE) {
			return null;
		} else {
			return value;
		}
	}

	public static void showMessageDialog( Object message ) {
		showMessageDialog( message, getUIManagerString( "OptionPane.messageDialogTitle" ), javax.swing.JOptionPane.INFORMATION_MESSAGE );
	}

	public static void showMessageDialog( Object message, String title, int messageType ) {
		showMessageDialog( message, title, messageType, null );
	}

	public static void showMessageDialog( Object message, String title, int messageType, javax.swing.Icon icon ) {
		showOptionDialog( message, title, javax.swing.JOptionPane.DEFAULT_OPTION, messageType, icon, null, null );
	}

	public static int showConfirmDialog( Object message ) {
		return showConfirmDialog( message,  getUIManagerString( "OptionPane.titleText" ), javax.swing.JOptionPane.YES_NO_CANCEL_OPTION );
	}

	public static int showConfirmDialog( Object message, String title, int optionType ) {
		return showConfirmDialog( message, title, optionType, javax.swing.JOptionPane.QUESTION_MESSAGE );
	}

	public static int showConfirmDialog( Object message, String title, int optionType, int messageType ) {
		return showConfirmDialog( message, title, optionType, messageType, null );
	}

	public static int showConfirmDialog( Object message, String title, int optionType, int messageType, javax.swing.Icon icon ) {
		return showOptionDialog( message, title, optionType, messageType, icon, null, null );
	}

	public static int showOptionDialog( Object message, String title, int optionType, int messageType, javax.swing.Icon icon, Object[] options, Object initialValue ) {
		javax.swing.JOptionPane pane = new javax.swing.JOptionPane( message, messageType, optionType, icon, options, initialValue );

		pane.setInitialValue(initialValue);
		java.awt.Component parent = null;
		if (!s_stack.isEmpty()) {
			parent = (java.awt.Component)s_stack.peek();
			pane.setComponentOrientation( parent.getComponentOrientation() );
		}
		
		javax.swing.JDialog dialog = pane.createDialog( parent, title );
		pane.selectInitialValue();
		showModalDialog( dialog );

		Object selectedValue = pane.getValue();

		if(selectedValue == null)
			return javax.swing.JOptionPane.CLOSED_OPTION;
		if(options == null) {
			if(selectedValue instanceof Integer)
				return ((Integer)selectedValue).intValue();
			return javax.swing.JOptionPane.CLOSED_OPTION;
		}
		for(int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if(options[counter].equals(selectedValue))
				return counter;
		}
		return javax.swing.JOptionPane.CLOSED_OPTION;
	}
}
