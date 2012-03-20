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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason Pratt
 */
public class Importing {
	List<Importer> importers = new ArrayList<Importer>();

	public Importing() {
		init();
	}

	private void init() {
		Class<? extends Importer>[] importerClasses = AuthoringToolResources.getImporterClasses();
		for (Class<? extends Importer> importerClasse : importerClasses) {
			try {
				importers.add((Importer) importerClasse.newInstance());
			} catch (Throwable t) {
				AuthoringTool.showErrorDialog("Error creating importer of type " + importerClasse, t);
			}
		}
	}

	public List<Importer> getImporters() {
		return importers;
	}
}