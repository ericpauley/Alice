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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

import java.util.Map;
import edu.cmu.cs.stage3.alice.authoringtool.*;
import edu.cmu.cs.stage3.alice.core.Element;
import java.io.InputStream;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

public class ScenegraphImporter extends AbstractImporter {

    public ScenegraphImporter() {
    }
    
	public Map getExtensionMap() {
		java.util.HashMap map = new java.util.HashMap();
		map.put( "ASG", "Alice SceneGraph" );
		return map;
    }

	
	protected Element load(InputStream is, String ext) throws java.io.IOException {
        edu.cmu.cs.stage3.alice.scenegraph.Component sgSrc = edu.cmu.cs.stage3.alice.scenegraph.io.XML.load( is );
        return edu.cmu.cs.stage3.alice.core.util.ScenegraphConverter.convert( (edu.cmu.cs.stage3.alice.scenegraph.Container)sgSrc );
    }
}