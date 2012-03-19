package edu.cmu.cs.stage3.alice.gallery.batch;

import java.io.File;

import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public abstract class Batch {
	private edu.cmu.cs.stage3.alice.core.World m_world;
	public Batch() {
		m_world = new edu.cmu.cs.stage3.alice.core.World();
		m_world.name.set("World");

		initialize(m_world);
	}
	public edu.cmu.cs.stage3.alice.core.World getWorld() {
		return m_world;
	}
	protected void initialize(edu.cmu.cs.stage3.alice.core.World world) {
	}
	public void forEachElement(java.io.File root, ElementHandler elementHandler) {
		java.io.File[] directories = root.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(java.io.File file) {
				return file.isDirectory();
			}
		});
		for (File directorie : directories) {
			forEachElement(directorie, elementHandler);
		}

		java.io.File[] files = root.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(java.io.File dir, String name) {
				return name.endsWith(".a2c");
			}
		});
		for (File fileI : files) {
			try {
				edu.cmu.cs.stage3.alice.core.Element element = edu.cmu.cs.stage3.alice.core.Element.load(fileI, m_world);
				elementHandler.handleElement(element, fileI);
				element.release();
			} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
				System.err.println(fileI);
				edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
				for (PropertyReference propertyReference : propertyReferences) {
					System.err.println("\t" + propertyReference);
				}
				upre.printStackTrace();
			} catch (Throwable t) {
				System.err.println(fileI);
				t.printStackTrace();
			}
		}
	}
}
