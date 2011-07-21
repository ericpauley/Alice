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

package edu.cmu.cs.stage3.alice.authoringtool.galleryviewer;

/**
 * @author David Culyba
 *
 */

public class WebGalleryObject extends GalleryObject {

    protected boolean needToWriteThumbnail = false;

    
	protected String getToolTipString(){
        return "<html><body><p>Object</p><p>Click to add this object to the world</p></body></html>";
    }

    
	protected void guiInit(){
        super.guiInit();
        location = "the Web";
    }

    public static java.awt.Image retrieveImage(String root, String filename, long timestamp){
        final String imageFilename = root + filename;
        boolean needToLoad = false;

        javax.swing.ImageIcon toReturn = null;
        if (imageFilename != null){
            String cacheFilename = GalleryViewer.cacheDir+GalleryViewer.reverseWebReady(filename);
            java.io.File cachedImageFile = new java.io.File(cacheFilename);
            if (cachedImageFile.exists() && cachedImageFile.canRead()){
                if (cachedImageFile.lastModified() == timestamp){
                    toReturn = new javax.swing.ImageIcon(cacheFilename);
                }
                else{
                    needToLoad = true;
                }
            }
            else{
                needToLoad = true;
            }
            if (needToLoad){
                try{
                    java.net.URL imageURL = new java.net.URL(imageFilename);
                    toReturn = new javax.swing.ImageIcon(imageURL);
                }
                catch (Exception e){
                    toReturn = null;
                }
            }
            if (toReturn.getIconHeight() < 10 || toReturn.getIconWidth() < 10){
                toReturn = null;
            }
        }
        return toReturn.getImage();
    }

    
	public void loadImage(){
        String tempFilename = null;
        if (data != null && data.imageFilename != null){
            tempFilename = rootPath + data.imageFilename;
        }
        final String imageFilename = tempFilename;
        Runnable doLoad = new Runnable() {
            public void run(){
                String cacheFilename = null;
                if (imageFilename != null){
                    cacheFilename = GalleryViewer.cacheDir+GalleryViewer.reverseWebReady(data.imageFilename);
                    java.io.File cachedImageFile = new java.io.File(cacheFilename);
                    if (cachedImageFile.exists() && cachedImageFile.canRead()){
                        if (cachedImageFile.lastModified() == data.timeStamp){
                            image = new javax.swing.ImageIcon(cacheFilename);
                        }
                        else{
                            needToWriteThumbnail = true;
                        }
                    }
                    else{
                        needToWriteThumbnail = true;
                    }
                    if (needToWriteThumbnail){
                        try{
                            java.net.URL imageURL = new java.net.URL(imageFilename);
                            image = new javax.swing.ImageIcon(imageURL);
                        }
                        catch (Exception e){
                            image = GalleryViewer.noImageIcon;
                        }
                    }
                }
                else{
                    image = GalleryViewer.noImageIcon;
                    needToWriteThumbnail = false;
                }
                if (image.getIconHeight() < 10 || image.getIconWidth() < 10){
                    image = GalleryViewer.noImageIcon;
                    needToWriteThumbnail = false;
                }
                setImage(image);
                if (needToWriteThumbnail && cacheFilename != null){
                    storeThumbnail(cacheFilename,image.getImage(), data.timeStamp);
                }
            }
        };
        Thread t = new Thread(doLoad);
        t.start();
    }

}