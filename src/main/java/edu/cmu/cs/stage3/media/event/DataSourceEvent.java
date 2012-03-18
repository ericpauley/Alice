package edu.cmu.cs.stage3.media.event;

public class DataSourceEvent extends java.util.EventObject {
	public DataSourceEvent( edu.cmu.cs.stage3.media.DataSource source ) {
		super( source );
	}
	public edu.cmu.cs.stage3.media.DataSource getDataSource() {
		return (edu.cmu.cs.stage3.media.DataSource)getSource();
	}
}
