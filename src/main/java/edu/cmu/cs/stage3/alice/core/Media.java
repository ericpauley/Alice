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

package edu.cmu.cs.stage3.alice.core;

import edu.cmu.cs.stage3.alice.core.property.DataSourceProperty;
import edu.cmu.cs.stage3.alice.core.property.ElementArrayProperty;
import edu.cmu.cs.stage3.alice.core.property.NumberProperty;

public class Media extends Element {
	public final DataSourceProperty dataSource = new DataSourceProperty(this, "dataSource", null);
	public final NumberProperty mediaLockCacheCountHint = new NumberProperty(this, "mediaLockCacheCountHint", new Integer(1));
	public final ElementArrayProperty markers = new ElementArrayProperty(this, "markers", null, edu.cmu.cs.stage3.alice.core.media.SoundMarker[].class);

	@Override
	protected void started(World world, double time) {
		super.started(world, time);
		edu.cmu.cs.stage3.media.DataSource dataSourceValue = dataSource.getDataSourceValue();
		// todo? wait for realized players
		int realizedPlayerCount = dataSourceValue.waitForRealizedPlayerCount(mediaLockCacheCountHint.intValue(), 0);
	}
}

// import edu.cmu.cs.stage3.alice.core.property.*;
//
// import edu.cmu.cs.stage3.io.media.MediaLock;
// import edu.cmu.cs.stage3.io.media.MediaLockObserver;
//
// import edu.cmu.cs.stage3.io.media.control.PanControl;
// import edu.cmu.cs.stage3.alice.core.media.SoundMarker;
//
// import javax.sound.sampled.*;
//
// public class Media extends Element {
// public final DataSourceProperty dataSource = new DataSourceProperty( this,
// "dataSource", null );
// public final NumberProperty mediaLockCacheCountHint = new NumberProperty(
// this, "mediaLockCacheCountHint", new Integer( 1 ) );
//
// public final ElementArrayProperty markers = new
// ElementArrayProperty(this,"markers",new SoundMarker[0],SoundMarker[].class);
//
// private java.util.Vector m_runtimeMediaLocks = new java.util.Vector();
// private java.util.Vector m_authoringMediaLocks = new java.util.Vector();
//
// private Double length = new Double(Double.NaN);
//
// private static boolean noSoundCard = false;
//
// //private boolean isMoviePlayer = false;
// //public edu.cmu.cs.stage3.alice.moviemaker.RecCodec rcCodec = null;
//
// //public Media() {
// // if (isMoviePlayer)
// // rcCodec = new edu.cmu.cs.stage3.alice.moviemaker.RecCodec();
// //}
//
// public javax.media.Player[] HACK_getPlayers() {
//
// javax.media.Player[] players = new javax.media.Player[
// m_runtimeMediaLocks.size()+m_authoringMediaLocks.size() ];
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// players[i] = ( (MediaLock) m_runtimeMediaLocks.elementAt( i ) ).getPlayer();
// }
// for( int i=0; i<m_authoringMediaLocks.size(); i++ ) {
// players[i+m_runtimeMediaLocks.size()] = ( (MediaLock)
// m_authoringMediaLocks.elementAt( i ) ).getPlayer();
// }
// return players;
// }
//
// public void dataSourceChanged( javax.media.protocol.DataSource
// dataSourceValue ) {
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_runtimeMediaLocks.elementAt( i );
// if( mediaLock.isLocked() ) {
// mediaLock.release();
// }
// mediaLock.getPlayer().deallocate();
// }
// for( int i=0; i<m_authoringMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_authoringMediaLocks.elementAt( i );
// if( mediaLock.isLocked() ) {
// mediaLock.release();
// }
// mediaLock.getPlayer().deallocate();
// }
// m_runtimeMediaLocks = new java.util.Vector();
// m_authoringMediaLocks = new java.util.Vector();
// length = new Double(Double.NaN);
// }
//
// public void addMarker(double time) {
// addMarker("marker".concat(new
// Integer(markers.getArrayValue().length+1).toString()), time);
// }
//
// public void addMarker(String name, double time) {
// addMarker(new SoundMarker(name,time));
// }
//
// public void addMarker(SoundMarker newMarker) {
// double time = newMarker.getTime();
//
// SoundMarker[] oldarry=(SoundMarker[])markers.getArrayValue();
//
// for (int i=0; i<oldarry.length; i++) {
// if (time==oldarry[i].getTime())
// return;
// else if (time<oldarry[i].getTime()) {
// markers.add(i,newMarker);
// addChild(newMarker);
// return;
// }
// }
// markers.add(oldarry.length,newMarker);
// addChild(newMarker);
// }
//
// public void removeMarker(double time) {
// SoundMarker[] oldarry=(SoundMarker[])markers.getArrayValue();
// for (int i=0; i<oldarry.length; i++) {
// if (time==oldarry[i].getTime()) {
// removeChild(oldarry[i]);
// markers.remove(i);
// return;
// }
// }
// }
//
// public void removeMarker(SoundMarker m) {
// removeChild(m);
// markers.remove(m);
// }
//
// public SoundMarker[] getMarkers() {
// SoundMarker[] oldarry=(SoundMarker[])markers.getArrayValue();
// return oldarry;
// }
//
// public SoundMarker nextMarker(double time) {
// SoundMarker[] oldarry=getMarkers();
// for (int i=0; i<oldarry.length; i++) {
// if (oldarry[i].getTime()>time)
// return oldarry[i];
// }
// return null;
// }
//
// public SoundMarker previousMarker(double time) {
// SoundMarker[] oldarry=getMarkers();
// if (oldarry.length>0) {
// if (time<=oldarry[0].getTime())
// return null;
// } else
// return null;
// for (int i=1; i<oldarry.length; i++) {
// if (oldarry[i].getTime()>=time)
// return oldarry[i-1];
// }
// return oldarry[oldarry.length-1];
// }
//
// public MediaLock getUninitializedMediaLock() {
// MediaLock mediaLock = null;
//
// javax.media.protocol.DataSource dataSourceValue =
// dataSource.getDataSourceValue();
// mediaLock = new MediaLock( dataSource.getDataSourceValue() );
// m_authoringMediaLocks.addElement( mediaLock );
//
// return mediaLock;
// }
//
// public static boolean hasSoundCard() {
// boolean bad = true;
// Mixer.Info[] mixers = AudioSystem.getMixerInfo();
// for (int i=0; i<mixers.length; i++) {
// Line.Info[] lines = AudioSystem.getMixer(mixers[i]).getSourceLineInfo();
// for (int j=0; j<lines.length; j++) {
// bad = false;
// try {
// AudioSystem.getMixer(mixers[i]).getLine(lines[j]).open();
// } catch (Exception e) {
// bad = true;
// }
// if (bad==false)
// break;
// }
// if (bad==false)
// break;
// }
//
// return !bad;
// }
//
// public void setIsMoviePlayer(boolean isMoviePlayer) {
// /*
// if (this.isMoviePlayer == isMoviePlayer)
// return;
//
// this.isMoviePlayer = isMoviePlayer;
//
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_runtimeMediaLocks.elementAt( i );
// if (mediaLock.isLocked()) {
// }
// }
// // TODO: what about medialocks that are already locked?
// m_runtimeMediaLocks.clear();
//
// if (isMoviePlayer)
// rcCodec = new edu.cmu.cs.stage3.alice.moviemaker.RecCodec();
// else
// rcCodec = null;
// */
// }
//
// private void showNoSoundCardMessageDialogIfNecessary() {
// if (!noSoundCard) {
// java.awt.Component parentComponent = null;
// World world = getWorld();
// if( world != null ) {
// RenderTarget[] renderTargets = (RenderTarget[])world.getDescendants(
// RenderTarget.class );
// if( renderTargets != null && renderTargets.length > 0 ) {
// parentComponent = renderTargets[ 0 ].getAWTComponent();
// }
// }
// edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Your world has sound, but you do not seem to have a sound card therefore the sound will not play.","No Sound Card",javax.swing.JOptionPane.WARNING_MESSAGE);
// }
// noSoundCard = true;
// }
//
// public MediaLock getMediaLock( MediaLockObserver mediaLockObserver ) {
// MediaLock mediaLock = null;
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// MediaLock ml = (MediaLock)m_runtimeMediaLocks.elementAt( i );
// if( ml.isFree() ) {
// mediaLock = ml;
// }
// }
// if( mediaLock==null ) {
// javax.media.protocol.DataSource dataSourceValue =
// dataSource.getDataSourceValue();
// mediaLock = new MediaLock( dataSource.getDataSourceValue() );
// m_runtimeMediaLocks.addElement( mediaLock );
//
// if (!hasSoundCard()) {
// showNoSoundCardMessageDialogIfNecessary();
// } else {
// mediaLock.installCodec(new edu.cmu.cs.stage3.io.media.effects.Pan());
// //if (rcCodec!=null)
// // mediaLock.installCodec(rcCodec);
// mediaLock.getPlayer().prefetch();
// }
// }
//
// if (!hasSoundCard()) {
// showNoSoundCardMessageDialogIfNecessary();
// } else {
// mediaLock.lock( mediaLockObserver );
// }
// return mediaLock;
// }
//
// protected void started( World world, double time ) {
// super.started( world, time );
// int count = mediaLockCacheCountHint.intValue();
// javax.media.protocol.DataSource dataSourceValue =
// dataSource.getDataSourceValue();
// for( int i=m_runtimeMediaLocks.size(); i<count; i++ ) {
// MediaLock mediaLock = new MediaLock( dataSourceValue );
// m_runtimeMediaLocks.addElement( mediaLock );
// if (!hasSoundCard()) {
// showNoSoundCardMessageDialogIfNecessary();
// } else {
// mediaLock.installCodec(new edu.cmu.cs.stage3.io.media.effects.Pan());
// //if (rcCodec!=null)
// // mediaLock.installCodec(rcCodec);
// }
// }
//
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_runtimeMediaLocks.elementAt( i );
// if (!hasSoundCard()) {
// showNoSoundCardMessageDialogIfNecessary();
// } else {
// mediaLock.getPlayer().prefetch();
// }
// }
// for( int i=0; i<m_authoringMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_authoringMediaLocks.elementAt( i );
// if (mediaLock.isLocked()) {
// mediaLock.getPlayer().stop();
// }
// }
// //if (rcCodec!=null)
// // rcCodec.restart(time);
// }
// protected void stopped( World world, double time ) {
// super.stopped( world, time );
// for( int i=0; i<m_runtimeMediaLocks.size(); i++ ) {
// MediaLock mediaLock = (MediaLock)m_runtimeMediaLocks.elementAt( i );
//
// if (mediaLock.isLocked()) {
// mediaLock.getPlayer().stop();
//
// mediaLock.getPlayer().setRate((float)1.0);
// PanControl panControl =
// (PanControl)mediaLock.getPlayer().getControl("edu.cmu.cs.stage3.io.media.control.PanControl");
// if( panControl!=null ) {
// panControl.setPan( 0.0 );
// }
//
// mediaLock.release();
// }
// }
// }
//
// public double getLength() {
// if (length.isNaN()) {
// MediaLock ml = getUninitializedMediaLock();
// ml.lock(null);
// if (ml.getPlayer().getDuration()==javax.media.Duration.DURATION_UNKNOWN)
// length = new Double(Double.NaN);
// else if
// (ml.getPlayer().getDuration()==javax.media.Duration.DURATION_UNBOUNDED)
// length = new Double(Double.POSITIVE_INFINITY);
// else
// length = new Double(ml.getPlayer().getDuration().getSeconds());
// }
// return length.doubleValue();
// }
// }