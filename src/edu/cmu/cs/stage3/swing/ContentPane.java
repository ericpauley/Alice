package edu.cmu.cs.stage3.swing;

public abstract class ContentPane extends javax.swing.JPanel {
	public final static int OK_OPTION = javax.swing.JOptionPane.OK_OPTION;
	public final static int CANCEL_OPTION = javax.swing.JOptionPane.CANCEL_OPTION;
	
	protected javax.swing.JDialog m_dialog;
	public String getTitle() {
		return getClass().getName();
	}
	public boolean isReadyToDispose( int option ) {
		return true;
	}
	public void handleDispose() {
		m_dialog.dispose();
	}
	public void preDialogShow( javax.swing.JDialog dialog ) {
		m_dialog = dialog;
		m_dialog.setLocationRelativeTo( dialog.getOwner() );
	}
	public void postDialogShow( javax.swing.JDialog dialog ) {
		m_dialog = null;
	}
	
	public void addOKActionListener( java.awt.event.ActionListener l ) {
	}
	public void removeOKActionListener( java.awt.event.ActionListener l ) {
	}
	public void addCancelActionListener( java.awt.event.ActionListener l ) {
	}
	public void removeCancelActionListener( java.awt.event.ActionListener l ) {
	}
	
	public void setDialogTitle( String title ) {
		if( m_dialog != null ) {
			m_dialog.setTitle( title );
		} else {
			//todo: throw Exception?
		}
	}
	public void packDialog() {
		if( m_dialog != null ) {
			m_dialog.pack();
		} else {
			//todo: throw Exception?
		}
	}
}