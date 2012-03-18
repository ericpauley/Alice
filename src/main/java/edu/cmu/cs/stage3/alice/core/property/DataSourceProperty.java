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

package edu.cmu.cs.stage3.alice.core.property;

import edu.cmu.cs.stage3.alice.core.Element;

public class DataSourceProperty extends ObjectProperty {

	public DataSourceProperty(Element owner, String name,
			edu.cmu.cs.stage3.media.DataSource defaultValue) {
		super(owner, name, defaultValue,
				edu.cmu.cs.stage3.media.DataSource.class);
	}

	public edu.cmu.cs.stage3.media.DataSource getDataSourceValue() {
		return (edu.cmu.cs.stage3.media.DataSource) getValue();
	}

	private String getFilename() {
		edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue();
		return getOwner().name.getStringValue() + '.'
				+ dataSourceValue.getExtension();
	}

	
	protected void decodeObject(org.w3c.dom.Element node,
			edu.cmu.cs.stage3.io.DirectoryTreeLoader loader,
			java.util.Vector referencesToBeResolved, double version)
			throws java.io.IOException {
		m_associatedFileKey = null;
		
		String filename = getFilename(getNodeText(node));
		
		java.io.InputStream is = loader.readFile(filename);
		set(edu.cmu.cs.stage3.media.Manager.createDataSource(is,
				edu.cmu.cs.stage3.io.FileUtilities.getExtension(filename)));
		loader.closeCurrentFile();

		try {
			String durationHintText = node.getAttribute( "durationHint" );
			if( durationHintText != null ) {
				double durationHint = Double.parseDouble( durationHintText );
				getDataSourceValue().setDurationHint( durationHint );
			}
		} catch( Throwable t ) {
			//pass
		}
		
		try {
            m_associatedFileKey = loader.getKeepKey( filename );
        } catch( edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse ) {
            m_associatedFileKey = null;
        }
    }

	
	protected void encodeObject(org.w3c.dom.Document document,
			org.w3c.dom.Element node,
			edu.cmu.cs.stage3.io.DirectoryTreeStorer storer,
			edu.cmu.cs.stage3.alice.core.ReferenceGenerator referenceGenerator)
			throws java.io.IOException {
		edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue();
		if (dataSourceValue != null) {
			double duration = dataSourceValue
					.getDuration(edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY);
			if (Double.isNaN(duration)) {
				// pass
			} else {
				node.setAttribute( "durationHint", Double.toString( duration ) );
			}

			String filename = getFilename();
			Object associatedFileAbsolutePath;
			try {
				associatedFileAbsolutePath = storer.getKeepKey(filename);
			} catch (edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
				associatedFileAbsolutePath = null;
			}
			if (m_associatedFileKey == null
					|| !m_associatedFileKey.equals(associatedFileAbsolutePath)) {
				m_associatedFileKey = null;
				java.io.OutputStream os = storer.createFile(filename,
						dataSourceValue.isCompressionWorthwhile());
				java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(
						os);
				bos.write(dataSourceValue.getData());
				bos.flush();
				storer.closeCurrentFile();
				m_associatedFileKey = associatedFileAbsolutePath;
			} else {
				try {
					storer.keepFile(filename);
				} catch (edu.cmu.cs.stage3.io.KeepFileNotSupportedException kfnse) {
					kfnse.printStackTrace();
				} catch (edu.cmu.cs.stage3.io.KeepFileDoesNotExistException kfdne) {
					kfdne.printStackTrace();
				}
			}
			node.appendChild(document.createTextNode("java.io.File[" + filename
					+ "]"));
		}
	}

	
	public void keepAnyAssociatedFiles(
			edu.cmu.cs.stage3.io.DirectoryTreeStorer storer)
			throws edu.cmu.cs.stage3.io.KeepFileNotSupportedException,
			edu.cmu.cs.stage3.io.KeepFileDoesNotExistException {
		super.keepAnyAssociatedFiles(storer);
		edu.cmu.cs.stage3.media.DataSource dataSourceValue = getDataSourceValue(); // todo:
																					// handle
																					// variable
		if (dataSourceValue != null) {
			storer.keepFile(getFilename());
		}
	}
}
