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

import java.util.Map;
import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.core.Element;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.FileInputStream;
import java.io.File;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.math.Matrix33;
import edu.cmu.cs.stage3.alice.core.Response;
import java.util.ListIterator;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.io.EStreamTokenizer;
import edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources;
import edu.cmu.cs.stage3.alice.core.Pose;



public class AMCImporter extends AbstractImporter {
    protected ASFSkeleton skel = null;
    private static final edu.cmu.cs.stage3.alice.core.World scene = new edu.cmu.cs.stage3.alice.core.World();
	protected Model applyTo = null;

    protected double fps = 30;
    protected int nativeFPS = 60;

    protected String animationName = "motionCaptureAnimation";
    protected String AMCPath = "";

    public AMCImporter() {
    }

    
	public Map getExtensionMap() {
		java.util.HashMap map = new java.util.HashMap();
		map.put( "AMC", "Acclaim Motion Capture" );
		return map;
    }

    
	public Element load( String filename ) throws java.io.IOException {
		animationName = new File( filename ).getName();
		animationName = animationName.substring( 0, animationName.lastIndexOf( '.' ) );
        if (animationName.lastIndexOf( '.' )!=-1)
            animationName = animationName.substring( animationName.lastIndexOf( '.' )+1 );

        AMCPath = new File(filename).getAbsolutePath();
        AMCPath = AMCPath.substring(0,AMCPath.indexOf(new File( filename ).getName()));
        return super.load(filename);
	}

	
	public Element load( File file ) throws java.io.IOException {
		animationName = file.getName();
		animationName = animationName.substring( 0, animationName.lastIndexOf( '.' ) );
        if (animationName.lastIndexOf( '.' )!=-1)
            animationName = animationName.substring( animationName.lastIndexOf( '.' )+1 );

        AMCPath = file.getAbsolutePath();
        AMCPath = AMCPath.substring(0,AMCPath.indexOf(file.getName()));
		return super.load(file);
	}

	
	public Element load( java.net.URL url ) throws java.io.IOException {
		String externalForm = url.toExternalForm();
		String fullName = externalForm.substring( externalForm.lastIndexOf( '/' ) + 1 );
		animationName = fullName.substring( 0 , fullName.lastIndexOf( '.' ) );
        if (animationName.lastIndexOf( '.' )!=-1)
            animationName = animationName.substring( animationName.lastIndexOf( '.' )+1 );

        return super.load(url);
	}

    
	protected Element load(InputStream is, String ext) throws java.io.IOException {
        EStreamTokenizer tokenizer;
        edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream bcfis = new edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream( is );
        java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader( bcfis ) );
        tokenizer = new EStreamTokenizer( br );

        tokenizer.commentChar('#');
        tokenizer.eolIsSignificant( false );
        tokenizer.lowerCaseMode( true );
        tokenizer.parseNumbers();
        tokenizer.wordChars( '_', '_' );
        tokenizer.wordChars( ':', ':' );

        // read the headers and stop at the :degrees section
        String ASFfilename = "";
        String ASFpath = "";
        double dt = 1.0/nativeFPS;

        while (tokenizer.ttype!=StreamTokenizer.TT_WORD || !tokenizer.sval.equals(":degrees")) {
            if (tokenizer.ttype!=StreamTokenizer.TT_WORD) {
                tokenizer.nextToken();
                continue;
            }
            if (tokenizer.sval.equals(":asf-file")) {
                tokenizer.nextToken();
                ASFfilename = tokenizer.sval;
            } else if (tokenizer.sval.equals(":asf-path")) {
                tokenizer.nextToken();
                ASFpath = tokenizer.sval;
            } else if (tokenizer.sval.equals(":samples-per-second")) {
                tokenizer.nextToken();
                nativeFPS = (int)tokenizer.nval;
                dt = 1.0/nativeFPS;
            }
            tokenizer.nextToken();
        }

        File ASFfile = new File("");

        if (!ASFfilename.equals("")) {
            ASFfile = new File(ASFfilename);
            if (!ASFfile.isFile()) {
                int i;
                int previ=0;
                for (i=ASFpath.indexOf(";"); i!=-1; i=ASFpath.indexOf(";",i+1)) {
                    String temp = ASFpath.substring(previ,i-1);
                    if (!temp.endsWith(File.separator) && !temp.equals("")) temp = temp.concat(File.separator);
                    ASFfile = new File(temp.concat(ASFfilename));
                    if (ASFfile.isFile()) break;
                    previ=i+1;
                }
                if (!ASFfile.isFile()) {
                    String temp = ASFpath.substring(previ);
                    if (!temp.endsWith(File.separator) && !temp.equals("")) temp = temp.concat(File.separator);
                    ASFfile = new File(temp.concat(ASFfilename));
                }
                if (!ASFfile.isFile()) {
                    String temp = AMCPath;
                    if (!temp.endsWith(File.separator) && !temp.equals("")) temp = temp.concat(File.separator);
                    ASFfile = new File(temp.concat(ASFfilename));
                }
            }

        }


        MocapImporterOptionsDialog optionsDialog = new MocapImporterOptionsDialog();

        if (ASFfile.isFile()) {
            optionsDialog.setASFFile(ASFfile.getPath());
            optionsDialog.setASFPath(ASFfile.getParentFile());
        } else
            optionsDialog.setASFPath(new File(AMCPath));
        optionsDialog.setNativeFPS(nativeFPS);
        optionsDialog.pack();
        optionsDialog.setVisible( true );

        if (optionsDialog.ok==false)
            return null;

        applyTo = (Model)optionsDialog.getSelectedPart();
        ASFfile = new File(optionsDialog.getASFFile());
        fps = optionsDialog.getFPS();
        nativeFPS = optionsDialog.getNativeFPS();
        dt = 1.0/nativeFPS;

        if (!ASFfile.isFile()) return null;

        InputStream ASFis = new FileInputStream(ASFfile);
        skel = (new ASFImporter()).loadSkeleton(ASFis);
        ASFis.close();

        int samplenumber = 0;

        if (applyTo==null) {
            applyTo = skel.getRoot().model;
        }
        skel.setBasePose(applyTo);

        //System.out.println("Parsing Motion...");
        tokenizer.nextToken();
        while (tokenizer.ttype!=StreamTokenizer.TT_EOF) {
            samplenumber = (int)tokenizer.nval;
            tokenizer.nextToken();

            ASFBone bone;

            while (tokenizer.ttype==StreamTokenizer.TT_WORD) {
                bone = (ASFBone)skel.bones_dict.get(tokenizer.sval);
                tokenizer.nextToken();

                bone.position = (Vector3)bone.base_position.clone();
                bone.axis = new Matrix33();

                ListIterator li2;
                li2 = bone.dof.listIterator();
                while (li2.hasNext()) {
                    Integer d = (Integer)li2.next();
                    if (d.equals(ASFBone.DOF_L)) {
                        //bone.lengthSpline.addKey(new DoubleSimpleKey((samplenumber-1)*dt,tokenizer.nval*skel.lengthscale));
                    } else if (d.equals(ASFBone.DOF_TX)) {
                        bone.position.x = tokenizer.nval*skel.lengthscale;
                    } else if (d.equals(ASFBone.DOF_TY)) {
                        bone.position.y = tokenizer.nval*skel.lengthscale;
                    } else if (d.equals(ASFBone.DOF_TZ)) {
                        bone.position.z = -tokenizer.nval*skel.lengthscale;
                    } else if (d.equals(ASFBone.DOF_RX)) {
                        bone.axis.rotateX(-tokenizer.nval*skel.anglescale);
                    } else if (d.equals(ASFBone.DOF_RY)) {
                        bone.axis.rotateY(-tokenizer.nval*skel.anglescale);
                    } else if (d.equals(ASFBone.DOF_RZ)) {
                        bone.axis.rotateZ(tokenizer.nval*skel.anglescale);
                    }
                    tokenizer.nextToken();
                }

                if ((bone.lastTime+(1.0/fps)) <= ((samplenumber-1)*dt) ) {
                    bone.lastTime = ((samplenumber-1)*dt);
                    bone.hasFrame = true;
                }
            }
            skel.addFrames();
       }


        Response anim = skel.buildAnim();
        Pose[] poses = skel.buildPoses();

        anim.name.set(AuthoringToolResources.getNameForNewChild(animationName,applyTo));
        poses[0].name.set(AuthoringToolResources.getNameForNewChild(animationName+"_startPose",applyTo));
        poses[1].name.set(AuthoringToolResources.getNameForNewChild(animationName+"_endPose",applyTo));

        applyTo.responses.add(anim);
        applyTo.misc.add(poses[0]);
        applyTo.misc.add(poses[1]);

        applyTo.addChild(anim);
        applyTo.addChild(poses[0]);
        applyTo.addChild(poses[1]);

        if (applyTo == skel.getRoot().model)
            applyTo.name.set("MocapSkeleton");

        return applyTo; // TODO: what should we return?
    }
}