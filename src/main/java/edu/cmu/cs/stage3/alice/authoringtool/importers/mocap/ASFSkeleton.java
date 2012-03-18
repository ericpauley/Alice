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

package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import edu.cmu.cs.stage3.alice.core.Model;
import java.util.Vector;
import java.util.Hashtable;
import edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse;
import edu.cmu.cs.stage3.alice.core.response.DoTogether;
import edu.cmu.cs.stage3.alice.core.Pose;

public class ASFSkeleton {
    public Vector bones;
    public Hashtable bones_dict;
    public double lengthscale;
    public double anglescale;

    public ASFSkeleton() {
        bones = new Vector();
        bones_dict = new Hashtable();
    }

    public ASFBone getRoot() {
        return ((ASFBone)bones_dict.get("root"));
    }

    public Model buildBones() {
        return getRoot().buildBone(null);
    }

    public void setBasePose(Model mod) {
        getRoot().setBasePose(mod);
    }

    public void addFrames() {
        getRoot().addFrames();
    }

    public UserDefinedResponse buildAnim() {
        UserDefinedResponse anim = new UserDefinedResponse();
        DoTogether partAnims = new DoTogether();
        anim.addChild(partAnims);
        anim.componentResponses.add(partAnims);

        getRoot().buildAnim(partAnims);
        return anim;
    }

    public Pose[] buildPoses() {
        Pose[] poseList = new Pose[2];
        poseList[0]=new Pose();
        poseList[1]=new Pose();

        poseList[0].poseMap.set(new java.util.Hashtable());
        poseList[1].poseMap.set(new java.util.Hashtable());

        getRoot().buildPoses(getRoot().realMod ,poseList[0],poseList[1]);
        return poseList;
    }
}

