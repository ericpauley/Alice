package edu.cmu.cs.stage3.scheduler;

class SchedulerEvent extends java.awt.event.InvocationEvent {
	public static final int ID = java.awt.AWTEvent.RESERVED_ID_MAX + 11235;
	public SchedulerEvent(edu.cmu.cs.stage3.scheduler.SchedulerThread schedulerThread) {
		super(schedulerThread, ID, schedulerThread.getScheduler(), null, false);
	}
}

public class SchedulerThread extends Thread {
	private Scheduler m_scheduler;
	private java.awt.EventQueue m_eventQueue;
	private SchedulerEvent m_schedulerEvent;
	private boolean m_continue;
	private long m_sleepMillis = 15;
	public SchedulerThread(Scheduler scheduler) {
		m_scheduler = scheduler;
		m_eventQueue = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue();
		m_schedulerEvent = new SchedulerEvent(this);
	}
	public Scheduler getScheduler() {
		return m_scheduler;
	}
	public void markToStop() {
		m_continue = false;
	}
	public long getSleepMillis() {
		return m_sleepMillis;
	}
	public void setSleepMillis(long sleepMillis) {
		m_sleepMillis = sleepMillis;
	}

	@Override
	public void run() {
		m_continue = true;
		do {
			try {
				java.awt.AWTEvent e = m_eventQueue.peekEvent(m_schedulerEvent.getID());
				// e = null;
				if (e == null) {
					m_eventQueue.postEvent(m_schedulerEvent);
					sleep(m_sleepMillis);
				} else {
					sleep(1);
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				break;
			}
		} while (m_continue);
	}
}
