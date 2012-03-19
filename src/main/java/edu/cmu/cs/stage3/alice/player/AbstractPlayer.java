/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.player;

import edu.cmu.cs.stage3.alice.core.RenderTarget;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public abstract class AbstractPlayer {
	private edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory m_drtf;
	private edu.cmu.cs.stage3.alice.core.Clock m_clock = newClock();
	private long m_when0 = System.currentTimeMillis();
	private edu.cmu.cs.stage3.alice.core.World m_world = null;
	private boolean m_isGoodToSchedule = false;

	public AbstractPlayer(Class<?> rendererClass) {
		m_drtf = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory(rendererClass);
		edu.cmu.cs.stage3.scheduler.Scheduler scheduler = new edu.cmu.cs.stage3.scheduler.AbstractScheduler() {

			@Override
			protected void handleCaughtThowable(Runnable source, Throwable t) {
				markEachFrameRunnableForRemoval(source);
				t.printStackTrace();
			}
		};
		scheduler.addEachFrameRunnable(new Runnable() {
			@Override
			public void run() {
				AbstractPlayer.this.schedule();
			}
		});
		edu.cmu.cs.stage3.scheduler.SchedulerThread schedulerThread = new edu.cmu.cs.stage3.scheduler.SchedulerThread(scheduler);
		// schedulerThread.setSleepMillis( 1 );
		// schedulerThread.setPriority( Thread.MAX_PRIORITY );
		schedulerThread.start();
	}
	public AbstractPlayer() {
		this(null);
	}

	protected edu.cmu.cs.stage3.alice.core.Clock newClock() {
		return new edu.cmu.cs.stage3.alice.core.clock.DefaultClock();
	}

	protected abstract void handleRenderTarget(edu.cmu.cs.stage3.alice.core.RenderTarget renderTarget);
	protected abstract boolean isPreserveAndRestoreRequired();

	public void loadWorld(edu.cmu.cs.stage3.io.DirectoryTreeLoader loader, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws java.io.IOException {
		try {
			m_world = (edu.cmu.cs.stage3.alice.core.World) edu.cmu.cs.stage3.alice.core.Element.load(loader, null, progressObserver);
			m_world.setRenderTargetFactory(m_drtf);
			m_world.setClock(m_clock);
			m_clock.setWorld(m_world);
			edu.cmu.cs.stage3.alice.core.RenderTarget[] renderTargets = (edu.cmu.cs.stage3.alice.core.RenderTarget[]) m_world.getDescendants(edu.cmu.cs.stage3.alice.core.RenderTarget.class);
			for (RenderTarget renderTarget : renderTargets) {
				handleRenderTarget(renderTarget);
			}
		} catch (edu.cmu.cs.stage3.progress.ProgressCancelException pce) {
			throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper(pce, loader.toString());
		} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
			System.err.println("could not load: " + loader + ".  was unable to resolve the following references.");
			for (PropertyReference propertyReference : propertyReferences) {
				System.err.println("\t" + propertyReference);
			}
			// throw new edu.cmu.cs.stage3.alice.core.ExceptionWrapper( upre,
			// file.toString() );
		}
	}

	public void loadWorld(java.io.InputStream ios, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws java.io.IOException {
		edu.cmu.cs.stage3.io.DirectoryTreeLoader loader = new edu.cmu.cs.stage3.io.ZipTreeLoader();
		loader.open(ios);
		loadWorld(loader, progressObserver);
	}

	public void loadWorld(java.net.URL url, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws java.io.IOException {
		edu.cmu.cs.stage3.io.DirectoryTreeLoader loader = new edu.cmu.cs.stage3.io.ZipTreeLoader();
		loader.open(url);
		loadWorld(loader, progressObserver);
	}

	public void loadWorld(java.io.File file, edu.cmu.cs.stage3.progress.ProgressObserver progressObserver) throws java.io.IOException {
		edu.cmu.cs.stage3.io.DirectoryTreeLoader loader = new edu.cmu.cs.stage3.io.ZipFileTreeLoader();
		loader.open(file);
		loadWorld(loader, progressObserver);
	}

	public void unloadWorld() {
		if (m_world != null) {
			stopWorldIfNecessary();
			m_world.release();
			m_world = null;
		}
	}
	public void startWorld() {
		if (isPreserveAndRestoreRequired()) {
			m_world.preserve();
		}
		m_clock.start();
		m_isGoodToSchedule = true;
	}

	public void pauseWorld() {
		m_clock.pause();
	}
	public void resumeWorld() {
		m_clock.resume();
	}

	public void stopWorld() {
		m_isGoodToSchedule = false;
		m_clock.stop();
		if (isPreserveAndRestoreRequired()) {
			m_world.restore();
		}
	}

	public void stopWorldIfNecessary() {
		if (m_world != null && m_world.isRunning()) {
			stopWorld();
		}
	}

	@SuppressWarnings("unused")
	private double getTime() {
		return (System.currentTimeMillis() - m_when0) * 0.001;
	}

	public void schedule() {
		if (m_isGoodToSchedule) {
			m_clock.schedule();
		}
	}
}
