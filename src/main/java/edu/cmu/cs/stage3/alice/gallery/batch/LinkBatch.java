package edu.cmu.cs.stage3.alice.gallery.batch;

import java.io.File;

public abstract class LinkBatch {
	public void forEachLink(java.io.File dir, LinkHandler linkHandler) {
		java.io.File[] dirs = dir.listFiles(new java.io.FileFilter() {
			@Override
			public boolean accept(java.io.File file) {
				return file.isDirectory();
			}
		});
		for (File dir2 : dirs) {
			forEachLink(dir2, linkHandler);
		}

		java.io.File[] files = dir.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(java.io.File dir, String name) {
				return name.endsWith(".link");
			}
		});
		for (File fileI : files) {
			try {
				java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(fileI));
				String s = r.readLine();
				linkHandler.handleLink(fileI, s);
			} catch (java.io.IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
