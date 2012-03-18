package edu.cmu.cs.stage3.scheduler;

public abstract class AbstractScheduler implements Scheduler {
	private java.util.Vector m_eachFrameRunnables = new java.util.Vector();
	private java.util.Vector m_eachFrameRunnablesMarkedForRemoval = new java.util.Vector();
	private Runnable[] m_cachedEachFrameRunnables;
		
	public void addEachFrameRunnable( Runnable runnable ) {
		synchronized( m_eachFrameRunnables ) {
			m_eachFrameRunnables.addElement( runnable );
			m_cachedEachFrameRunnables = null;
		}
	}
	public void markEachFrameRunnableForRemoval( Runnable runnable ) {
		synchronized( m_eachFrameRunnablesMarkedForRemoval ) {
			m_eachFrameRunnablesMarkedForRemoval.addElement( runnable );
			m_cachedEachFrameRunnables = null;
		}
	}
	private Runnable[] getEachFrameRunnables() {
		if( m_cachedEachFrameRunnables == null ) {
			synchronized( m_eachFrameRunnables ) {
				synchronized( m_eachFrameRunnablesMarkedForRemoval ) {
					if( m_eachFrameRunnablesMarkedForRemoval.size() > 0 ) {
						java.util.Enumeration enum0 = m_eachFrameRunnablesMarkedForRemoval.elements();
						while( enum0.hasMoreElements() ) {
							m_eachFrameRunnables.removeElement( enum0.nextElement() );
						}
						m_eachFrameRunnablesMarkedForRemoval.clear();
					}
					m_cachedEachFrameRunnables = new Runnable[ m_eachFrameRunnables.size() ];
					m_eachFrameRunnables.copyInto( m_cachedEachFrameRunnables );
				}
			}
		}
		return m_cachedEachFrameRunnables;
	}

	protected abstract void handleCaughtThowable( Runnable source, Throwable t );
	
	public void run() {
		Runnable[] eachFrameRunnables = getEachFrameRunnables();
		for( int i=0; i<eachFrameRunnables.length; i++ ) {
			try {
				eachFrameRunnables[ i ].run();
			} catch( Throwable t ) {
				handleCaughtThowable( eachFrameRunnables[ i ], t );
			}
		}
	}
}
