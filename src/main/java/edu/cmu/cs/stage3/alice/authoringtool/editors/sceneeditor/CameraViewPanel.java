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

package edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.scenegraph.Camera;

/**
 * @author Jason Pratt
 * @author Clifton Forlines
 */
public class CameraViewPanel extends JPanel implements edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener, edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorListener {

	public final static int NAVIGATOR_TAB = 0;
	public final static int MANIPULATOR_TAB = 1;

	public final static int SINGLE_VIEW_MODE = 0;
	public final static int QUAD_VIEW_MODE = 1;

	public final static int MORE_CONTROLS = 0;
	public final static int FEWER_CONTROLS = 1;

	public final static String NONE_DUMMY = "<None>";

	protected edu.cmu.cs.stage3.alice.core.World world;
	protected SceneEditor sceneEditor;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTarget = null;
	protected edu.cmu.cs.stage3.alice.core.Camera renderCamera = null;
	protected edu.cmu.cs.stage3.math.Matrix44 originalCameraPOV;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator rtmm;
	// protected
	// edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetOrbitManipulator
	// rtom;
	protected edu.cmu.cs.stage3.alice.core.Transformable pickedTransformable = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable selectedTransformable = null;
	protected edu.cmu.cs.stage3.alice.core.Transformable blankTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected java.util.HashMap resizeTable = new java.util.HashMap();
	// protected java.util.HashMap povMap = new java.util.HashMap();
	// protected
	// edu.cmu.cs.stage3.alice.authoringtool.util.FilteringElementTreeModel
	// povTreeModel = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.FilteringElementTreeModel();
	// protected edu.cmu.cs.stage3.alice.core.Transformable povTransformable;
	edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator guiNavigator;
	edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer galleryViewer;
	protected double minimumViewingAngle;
	protected javax.swing.ButtonGroup singleQuadGroup = new javax.swing.ButtonGroup();
	protected javax.swing.ButtonGroup mouseModeGroup = new javax.swing.ButtonGroup();
	protected int viewMode = -1;
	protected edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator boundingBoxDecorator = new edu.cmu.cs.stage3.alice.core.decorator.BoundingBoxDecorator();
	protected edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator pivotDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator();
	protected boolean showDroppingFeedback = false;
	protected javax.vecmath.Vector3d droppingFeedbackDimensions;
	protected javax.vecmath.Vector3d droppingFeedbackLocation;
	protected edu.cmu.cs.stage3.alice.core.decorator.BoxDecorator dropFeedbackDecorator = new edu.cmu.cs.stage3.alice.core.decorator.BoxDecorator();
	protected edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator dropFeedbackPivotDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator();
	protected edu.cmu.cs.stage3.alice.core.Transformable pickFeedbackTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.core.Transformable boundingBoxFeedbackTransformable;
	protected edu.cmu.cs.stage3.math.Plane pickingPlane = new edu.cmu.cs.stage3.math.Plane(new javax.vecmath.Vector3d(0, 0, 0), new javax.vecmath.Vector3d(0, 1, 0));
	protected edu.cmu.cs.stage3.alice.core.Element ground;
	protected boolean successFullVisualDrop = false;

	// Stuff for mouse manipulation while dragging stuff in
	// protected edu.cmu.cs.stage3.alice.core.Transformable helper = new
	// edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable helper = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected edu.cmu.cs.stage3.alice.scenegraph.Camera sgCamera = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgCameraTransformable = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Scene sgScene = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.Transformable sgIdentity = new edu.cmu.cs.stage3.alice.scenegraph.Transformable();
	protected java.awt.Point originalMousePoint = new java.awt.Point();
	protected boolean firstTimeKeyIsDown = true;
	protected javax.vecmath.Vector3d tempVec = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d zeroVec = new javax.vecmath.Vector3d(0.0, 0.0, 0.0);
	protected javax.vecmath.Vector4d tempVec4 = new javax.vecmath.Vector4d();
	protected javax.vecmath.Vector3d cameraForward = new javax.vecmath.Vector3d();
	protected javax.vecmath.Vector3d cameraUp = new javax.vecmath.Vector3d();
	protected edu.cmu.cs.stage3.math.Matrix44 oldTransformation;
	protected java.awt.Cursor invisibleCursor = java.awt.Toolkit.getDefaultToolkit().createCustomCursor(java.awt.Toolkit.getDefaultToolkit().getImage(""), new java.awt.Point(0, 0), "invisible cursor");
	protected java.awt.Cursor savedCursor;

	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode defaultMoveMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode moveUpDownMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode turnLeftRightMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode turnForwardBackwardMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode tumbleMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode resizeMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode copyMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode orthoScrollMode;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode orthoZoomInMode;
	// protected
	// edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode
	// orthoZoomOutMode;

	ManipulatorModeButton defaultMoveModeButton;
	ManipulatorModeButton moveUpDownModeButton;
	ManipulatorModeButton turnLeftRightModeButton;
	ManipulatorModeButton turnForwardBackwardModeButton;
	ManipulatorModeButton tumbleModeButton;
	ManipulatorModeButton resizeModeButton;
	ManipulatorModeButton copyModeButton;
	ManipulatorModeButton orthoScrollModeButton;
	ManipulatorModeButton orthoZoomInModeButton;
	// ManipulatorModeButton orthoZoomOutModeButton;

	protected ManipulatorModeButton singleViewButtonSave;
	protected ManipulatorModeButton quadViewButtonSave;

	// quad view stuff
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTargetFromTheRight = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTargetFromTheTop = null;
	protected edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget renderTargetFromTheFront = null;

	protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheRight = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
	protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheTop = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();
	protected edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera cameraFromTheFront = new edu.cmu.cs.stage3.alice.core.camera.OrthographicCamera();

	protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheRight = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheTop = new edu.cmu.cs.stage3.alice.core.Transformable();
	protected edu.cmu.cs.stage3.alice.core.Transformable camTransformableFromTheFront = new edu.cmu.cs.stage3.alice.core.Transformable();

	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator rtmmFromTheRight;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator rtmmFromTheTop;
	protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator rtmmFromTheFront;

	protected javax.swing.JPanel quadPanel = new javax.swing.JPanel();

	protected String fromRightText = "from the right";
	protected String fromTopText = "from the top";
	protected String fromFrontText = "from the front";

	protected String selectedButtonText = "Mouse Controls";
	protected java.awt.Color defaultBorderColor = new java.awt.Color(102, 102, 153);
	protected java.awt.Color highlightBorderColor = new java.awt.Color(102, 153, 102);

	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage());
	protected java.awt.Color viewLabelColor = new java.awt.Color(255, 255, 255, 128);
	private int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
	protected java.awt.Font viewLabelFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, (int) (18 * fontSize / 12.0));
	protected boolean originalTileDraggingOption;

	protected javax.swing.ListCellRenderer enumerableComboRenderer = new javax.swing.DefaultListCellRenderer() {

		@Override
		public java.awt.Component getListCellRendererComponent(javax.swing.JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof edu.cmu.cs.stage3.util.Enumerable) {
				value = ((edu.cmu.cs.stage3.util.Enumerable) value).getRepr();
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	};

	protected java.awt.dnd.DropTargetListener renderTargetDropTargetListener = new java.awt.dnd.DropTargetListener() {
		private edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter imageImporter = new edu.cmu.cs.stage3.alice.authoringtool.importers.ImageImporter();
		private boolean checkDrag(java.awt.dnd.DropTargetDragEvent dtde) {
			boolean toReturn = false;
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
				toReturn = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
				toReturn = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor)) {
				toReturn = true;
			} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
				try {
					java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
					if (transferable instanceof edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable) {
						dtde.rejectDrag();
						return false;
					}
					edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype prototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
					if (edu.cmu.cs.stage3.alice.core.response.CompositeResponse.class.isAssignableFrom(prototype.getElementClass()) || edu.cmu.cs.stage3.alice.core.response.Print.class.isAssignableFrom(prototype.getElementClass()) || edu.cmu.cs.stage3.alice.core.response.Comment.class.isAssignableFrom(prototype.getElementClass())) {
						dtde.rejectDrag();
						return false;
					} else {
						toReturn = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// } else if(
				// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
				// df )) { //Works in 1.4, but not in 1.3_0_10 or 1.3_0_11
				// // System.out.println("here");
				// toReturn = true;
				// }else if( dtde.getDropAction() ==
				// java.awt.dnd.DnDConstants.ACTION_NONE ) { // HACK for
				// UniformResourceLocators //Doesn't work in 1.30_10 or 1.3_0_11
				// dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_LINK ); //
				// HACK for UniformResourceLocators
				// return true;
			} else {
				dtde.rejectDrag();
				return false;
			}
			if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_COPY) > 0) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_COPY);
			} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_MOVE) > 0) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
			} else if ((dtde.getDropAction() & java.awt.dnd.DnDConstants.ACTION_LINK) > 0) {
				dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_LINK);
			}
			return toReturn;
		}

		private void startDraggingFromGallery() {
			edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject galleryObject = getGalleryObject(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent());
			startDraggingFromGallery(galleryObject);
		}

		private void startDraggingFromGallery(edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject galleryObject) {
			if (galleryObject != null) {
				authoringToolConfig.setValue("gui.pickUpTiles", "false");
				javax.vecmath.Vector3d dimensions = galleryObject.getBoundingBox();
				// System.out.println("dimensions: "+dimensions);
				if (!showDroppingFeedback) {
					showDroppingFeedback = true;
					dropFeedbackDecorator.setIsShowing(true);
					dropFeedbackPivotDecorator.setIsShowing(true);
				}
				dropFeedbackDecorator.setWidth(dimensions.x);
				dropFeedbackDecorator.setHeight(dimensions.y);
				dropFeedbackDecorator.setDepth(dimensions.z);
				edu.cmu.cs.stage3.math.Box boundingBox = new edu.cmu.cs.stage3.math.Box();
				boundingBox.setMinimum(new javax.vecmath.Vector3d(0, 0, 0));
				boundingBox.setMaximum(new javax.vecmath.Vector3d(dimensions.x / 2, dimensions.y, dimensions.z / 2));
				dropFeedbackPivotDecorator.setOverrideBoundingBox(boundingBox);

				java.awt.Rectangle renderRect = renderPanel.getBounds();
				java.awt.Point correctPoint = renderPanel.getLocationOnScreen();
				originalMousePoint.x = correctPoint.x + renderRect.width / 2;
				originalMousePoint.y = correctPoint.y + renderRect.height / 2;

				// Turn and raise initialization stuff
				sgCamera = renderTarget.getCameras()[0]; // TODO: handle
															// multiple
															// viewports?
				sgCameraTransformable = (edu.cmu.cs.stage3.alice.scenegraph.Transformable) sgCamera.getParent();
				sgScene = (edu.cmu.cs.stage3.alice.scenegraph.Scene) sgCamera.getRoot();

				oldTransformation = new edu.cmu.cs.stage3.math.Matrix44(boundingBoxFeedbackTransformable.getSceneGraphTransformable().getLocalTransformation());
				// DEBUG System.out.println( "picked: " + sgPickedTransformable
				// );
				helper.setParent(sgScene);
				sgIdentity.setParent(sgScene);
			}
		}

		private void stopDraggingFromGallery() {
			if (originalTileDraggingOption) {
				authoringToolConfig.setValue("gui.pickUpTiles", "true");
			} else {
				authoringToolConfig.setValue("gui.pickUpTiles", "false");
			}
			showDroppingFeedback = false;
			dropFeedbackDecorator.setIsShowing(false);
			dropFeedbackPivotDecorator.setIsShowing(false);
			firstTimeKeyIsDown = true;

		}

		@Override
		public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);

			originalTileDraggingOption = authoringToolConfig.getValue("gui.pickUpTiles").equalsIgnoreCase("true");

			if (authoringToolConfig.getValue("showObjectLoadFeedback").equalsIgnoreCase("true") && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor) || edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor))) {
				// try{
				// startDraggingFromGallery();
				// } catch (Exception e){
				// dropFeedbackDecorator.setIsShowing(false);
				// successFullVisualDrop = false;
				// }
			} else {
				successFullVisualDrop = false;
			}
		}

		@Override
		public void dragExit(java.awt.dnd.DropTargetEvent dte) {
			stopDraggingFromGallery();
		}

		private edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject getGalleryObject(java.awt.Component sourceComponent) {
			while (sourceComponent != null && !(sourceComponent instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject)) {
				sourceComponent = sourceComponent.getParent();
			}
			if (sourceComponent instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) {
				return (edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) sourceComponent;
			} else {
				return null;
			}
		}

		private void turnAndRaiseManipulator(int keyMode, java.awt.Point point) {
			double deltaFactor;
			int dx = point.x - originalMousePoint.x;
			int dy = point.y - originalMousePoint.y;
			edu.cmu.cs.stage3.alice.scenegraph.Transformable sgPickedTransformable = boundingBoxFeedbackTransformable.getSceneGraphTransformable();
			if (sgCamera instanceof edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera) {
				edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera orthoCamera = (edu.cmu.cs.stage3.alice.scenegraph.OrthographicCamera) sgCamera;
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight(); // TODO:
																							// should
																							// be
																							// viewport,
																							// but
																							// not
																							// working
																							// right
																							// now
				double nearClipHeightInWorld = orthoCamera.getPlane()[3] - orthoCamera.getPlane()[1];
				deltaFactor = nearClipHeightInWorld / nearClipHeightInScreen;
			} else {
				double projectionMatrix11 = renderTarget.getProjectionMatrix(sgCamera).getElement(1, 1);
				double nearClipDist = sgCamera.getNearClippingPlaneDistance();
				double nearClipHeightInWorld = 2 * (nearClipDist / projectionMatrix11);
				double nearClipHeightInScreen = renderTarget.getAWTComponent().getHeight(); // TODO:
																							// should
																							// be
																							// viewport,
																							// but
																							// not
																							// working
																							// right
																							// now
				double pixelHeight = nearClipHeightInWorld / nearClipHeightInScreen;
				double objectDist = sgPickedTransformable.getPosition(sgCameraTransformable).getLength();
				deltaFactor = objectDist * pixelHeight / nearClipDist;
			}

			boolean shiftDown = keyMode >= 2;
			boolean controlDown = keyMode == 1 || keyMode == 3;

			// if( mode == GROUND_PLANE_MODE ) {
			if (true) {
				if (controlDown) {
					if (shiftDown) {
						helper.setTransformation(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgCameraTransformable);
						helper.setPosition(zeroVec, sgPickedTransformable);
						sgPickedTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getXAxis(), -dy * .01, helper);
						sgPickedTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx * .01, sgPickedTransformable);
					} else {
						helper.setTransformation(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgScene);
						helper.setPosition(zeroVec, sgPickedTransformable);
						sgPickedTransformable.rotate(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), -dx * .01, helper);
					}
				} else if (shiftDown) {
					helper.setTransformation(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(), sgScene);
					helper.setPosition(zeroVec, sgPickedTransformable);
					tempVec.x = 0.0;
					tempVec.y = -dy * deltaFactor;
					tempVec.z = 0.0;
					sgPickedTransformable.translate(tempVec, helper);
				} else {
					javax.vecmath.Matrix4d cameraTransformation = sgCameraTransformable.getAbsoluteTransformation();
					cameraUp.x = cameraTransformation.m10;
					cameraUp.y = cameraTransformation.m11;
					cameraUp.z = cameraTransformation.m12;
					cameraForward.x = cameraTransformation.m20;
					cameraForward.y = cameraTransformation.m21;
					cameraForward.z = cameraTransformation.m22;

					helper.setPosition(zeroVec, sgPickedTransformable);
					if (Math.abs(cameraForward.y) < Math.abs(cameraUp.y)) { // if
																			// we're
																			// looking
																			// mostly
																			// level
						cameraForward.y = 0.0;
						helper.setOrientation(cameraForward, cameraUp, sgScene);
						// System.out.println( "helper.setOrientation( " +
						// cameraForward + ", " + cameraUp + ", " + sgScene +
						// " );" );
					} else { // if we're looking mostly up or down
						cameraUp.y = 0.0;
						cameraForward.negate();
						helper.setOrientation(cameraUp, cameraForward, sgScene);
						// System.out.println( "helper.setOrientation( " +
						// cameraUp + ", " + cameraForward + ", " + sgScene +
						// " );" );
					}

					tempVec.x = dx * deltaFactor;
					tempVec.y = 0.0;
					tempVec.z = -dy * deltaFactor;
					sgPickedTransformable.translate(tempVec, helper);
					// System.out.println( "helper: " +
					// helper.getAbsoluteTransformation() );
					// System.out.println( "cameraTransformation: " +
					// cameraTransformation );
					// System.out.println( "cameraTransformable: " +
					// sgCameraTransformable );
				}
				// } else if( mode == CAMERA_PLANE_MODE ) {
			} /*
			 * else if( false ) { if( controlDown ) { if( shiftDown ) { //TODO?
			 * } else { helper.setTransformation(
			 * edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d(),
			 * sgCameraTransformable ); helper.setPosition( zeroVec,
			 * sgPickedTransformable ); sgPickedTransformable.rotate(
			 * edu.cmu.cs.stage3.math.MathUtilities.getZAxis(), -dx*.01, helper
			 * ); } } else if( shiftDown ) { int bigdx = point.x -
			 * originalMousePoint.x; int bigdy = point.y - originalMousePoint.y;
			 * sgPickedTransformable.setLocalTransformation( oldTransformation
			 * ); if( Math.abs( bigdx ) > Math.abs( bigdy ) ) { tempVec.x =
			 * bigdx*deltaFactor; tempVec.y = 0.0; } else { tempVec.x = 0.0;
			 * tempVec.y = -bigdy*deltaFactor; } tempVec.z = 0.0;
			 * sgPickedTransformable.translate( tempVec, sgCameraTransformable
			 * ); } else { tempVec.x = dx*deltaFactor; tempVec.y =
			 * -dy*deltaFactor; tempVec.z = 0.0;
			 * sgPickedTransformable.translate( tempVec, sgCameraTransformable
			 * ); } }
			 */
		}

		private void placeMouseOnBoundingBoxBottom() {
			javax.vecmath.Vector3d xyzInCamera = boundingBoxFeedbackTransformable.transformTo(new javax.vecmath.Vector3d(0, 0, 0), renderCamera);
			javax.vecmath.Vector3d xyzInViewport = renderTarget.transformFromCameraToViewport(xyzInCamera, renderCamera.getSceneGraphCamera());
			java.awt.Rectangle rect = renderTarget.getActualViewport(renderCamera.getSceneGraphCamera());
			java.awt.Point newPoint = new java.awt.Point((int) xyzInViewport.x + rect.x + renderPanel.getLocationOnScreen().x, (int) xyzInViewport.y + rect.y + renderPanel.getLocationOnScreen().y);
			edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation(newPoint);
			pickingPlane = new edu.cmu.cs.stage3.math.Plane(boundingBoxFeedbackTransformable.getPosition(world), new javax.vecmath.Vector3d(0, 1, 0));
			boundingBoxFeedbackTransformable.setPositionRightNow(0.0, 0.0, 0.0);
		}

		@Override
		public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);
			if (authoringToolConfig.getValue("showObjectLoadFeedback").equalsIgnoreCase("true") && (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor) || edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor))) {
				edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject galleryObject = getGalleryObject(edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent());
				if (galleryObject != null) {
					int keyMode = 0;
					if (edu.cmu.cs.stage3.awt.AWTUtilities.isKeyPressed(java.awt.event.KeyEvent.VK_CONTROL)) {
						keyMode += 1;
					}
					if (edu.cmu.cs.stage3.awt.AWTUtilities.isKeyPressed(java.awt.event.KeyEvent.VK_SHIFT)) {
						keyMode += 2;
					}
					if (keyMode > 0) { // Shift or Control is being pressed, so
										// do other mode
						if (firstTimeKeyIsDown) {
							firstTimeKeyIsDown = false;
							edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation(originalMousePoint);
							edu.cmu.cs.stage3.awt.AWTUtilities.setIsCursorShowing(false);
							// System.out.println("hiding");

						} else {
							turnAndRaiseManipulator(keyMode, edu.cmu.cs.stage3.awt.AWTUtilities.getCursorLocation());
							edu.cmu.cs.stage3.awt.AWTUtilities.setCursorLocation(originalMousePoint);
						}
					} else {
						if (!firstTimeKeyIsDown) {
							placeMouseOnBoundingBoxBottom();
							edu.cmu.cs.stage3.awt.AWTUtilities.setIsCursorShowing(true);
							// System.out.println("showing");
							firstTimeKeyIsDown = true;
						}

						if (ground != null) {
							successFullVisualDrop = true;
							if (!showDroppingFeedback) {
								startDraggingFromGallery(galleryObject);
							}
							java.awt.Point myPoint2 = edu.cmu.cs.stage3.awt.AWTUtilities.getCursorLocation();
							java.awt.Point panelLocation = renderPanel.getLocationOnScreen();
							java.awt.Rectangle rect = renderTarget.getActualViewport(renderCamera.getSceneGraphCamera());
							if (myPoint2 != null && rect != null && panelLocation != null) {
								myPoint2.x -= rect.x + panelLocation.x;
								myPoint2.y -= rect.y + panelLocation.y;
							}
							try {
								edu.cmu.cs.stage3.math.Ray ray = renderTarget.getRayAtPixel(renderCamera.getSceneGraphCamera(), myPoint2.x, myPoint2.y);
								edu.cmu.cs.stage3.math.Matrix44 cameraToGround = renderCamera.getTransformation((edu.cmu.cs.stage3.alice.core.ReferenceFrame) ground);
								ray.transform(cameraToGround);
								double intersection = pickingPlane.intersect(ray);
								javax.vecmath.Vector3d intersectionPoint = ray.getPoint(intersection);
								javax.vecmath.Vector3d oldLocation = pickFeedbackTransformable.getPosition();
								pickFeedbackTransformable.setPositionRightNow(intersectionPoint);
								javax.vecmath.Vector3d pointInCameraSpace = boundingBoxFeedbackTransformable.getPosition(renderCamera);
								if (pointInCameraSpace.z < 0) {
									pickFeedbackTransformable.setPositionRightNow(oldLocation);
									successFullVisualDrop = false;
									stopDraggingFromGallery();
								}
							} catch (Exception e) {
								successFullVisualDrop = false;
								stopDraggingFromGallery();
							}
						} else {
							successFullVisualDrop = false;
							stopDraggingFromGallery();
						}
					}
				}
			}

		}

		@Override
		public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
			checkDrag(dtde);
		}

		@Override
		public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
			boolean visualDrop = successFullVisualDrop;
			edu.cmu.cs.stage3.math.Matrix44 dropPoint = null;
			if (visualDrop && ground != null) {
				// edu.cmu.cs.stage3.math.Matrix44 groundToWorld =
				// ((edu.cmu.cs.stage3.alice.core.Model)ground).getTransformation(world);
				// edu.cmu.cs.stage3.math.Matrix44 currentTransform =
				// pickFeedbackTransformable.getTransformation(world);
				dropPoint = boundingBoxFeedbackTransformable.getTransformation(world);
				boundingBoxFeedbackTransformable.setLocalTransformationRightNow(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
			}
			edu.cmu.cs.stage3.awt.AWTUtilities.setIsCursorShowing(true);
			// System.out.println("showing");
			stopDraggingFromGallery();
			pickingPlane = new edu.cmu.cs.stage3.math.Plane(new javax.vecmath.Vector3d(0, 0, 0), new javax.vecmath.Vector3d(0, 1, 0));
			try {
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
					java.util.Map imageImporterExtensionMap = imageImporter.getExtensionMap();
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					java.util.List fileList = (java.util.List) transferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
					if (!fileList.isEmpty()) {
						for (java.util.Iterator iter = fileList.iterator(); iter.hasNext();) {
							final java.io.File file = (java.io.File) iter.next();
							if (file.exists() && file.canRead() && !file.isDirectory()) {
								// String pathName = file.getAbsolutePath();
								String fileName = file.getName();
								final String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
								if (imageImporterExtensionMap.get(extension.toUpperCase()) != null) { // make
																										// a
																										// billboard
																										// if
																										// it's
																										// an
																										// image
									String elementName = extension.length() > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
									edu.cmu.cs.stage3.alice.core.Element element = imageImporter.load(file);
									if (element instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
										elementName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild(elementName, world);
										element.name.set(elementName);
										edu.cmu.cs.stage3.alice.core.Billboard billboard = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.makeBillboard((edu.cmu.cs.stage3.alice.core.TextureMap) element, true);
										world.addChild(billboard);
										edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty(billboard, world);
										billboard.vehicle.set(world);
										renderTarget.markDirty();
									} else {
										edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(file.getAbsolutePath() + " did not produce a TextureMap when loaded.", null, false);
										continue;
									}
								} else if (extension.equalsIgnoreCase(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.CHARACTER_EXTENSION)) {
									if (visualDrop && dropPoint != null) {
										authoringTool.loadAndAddCharacter(file, dropPoint);
									} else {
										authoringTool.loadAndAddCharacter(file);
									}
									// final
									// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
									// worker = new
									// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker()
									// {
									// public Object construct() {
									// return new Integer(
									// authoringTool.loadCharacter( file ) );
									// }
									// };
									// worker.start();
								} else if (authoringTool.isImportable(extension)) {
									authoringTool.importElement(file, world, null, false);
								} else {
									// TODO: make this a message dialog
									edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Alice does not know how to open this file: \n" + file.getAbsolutePath());
									continue;
								}
							} else {
								edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("This file does not exist, cannot be read, or is a directory.\n" + file.getAbsolutePath());
								continue;
							}
						}
					} else {
						dtde.getDropTargetContext().dropComplete(false);
						return;
					}
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor);
					java.util.Vector popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeDefaultOneShotStructure(transformable);
					edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, dtde.getDropTargetContext().getComponent(), dtde.getLocation().x, dtde.getLocation().y);
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor);
					if (responsePrototype.getDesiredProperties().length > 0) {
						java.util.Vector popupStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure(responsePrototype, edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.oneShotFactory, authoringTool.getWorld());
						edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.createAndShowPopupMenu(popupStructure, dtde.getDropTargetContext().getComponent(), dtde.getLocation().x, dtde.getLocation().y);
					} else {
						((Runnable) edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.oneShotFactory.createItem(responsePrototype)).run();
					}
					dtde.dropComplete(true);
				} else if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor)) {
					dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
					java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
					java.net.URL url = (java.net.URL) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.URLTransferable.urlFlavor);
					String extension = url.getPath().substring(url.getPath().lastIndexOf('.') + 1).toUpperCase();
					if (url.getPath().toLowerCase().endsWith("." + edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.CHARACTER_EXTENSION)) {
						if (visualDrop && dropPoint != null) {
							authoringTool.loadAndAddCharacter(url, dropPoint);
						} else {
							authoringTool.loadAndAddCharacter(url);
						}
					} else if (authoringTool.isImportable(extension)) {
						authoringTool.importElement(url, authoringTool.getWorld());
					}

					// final
					// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
					// worker = new
					// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker()
					// {
					// public Object construct() {
					// if( url.getPath().toLowerCase().endsWith( "." +
					// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.CHARACTER_EXTENSION
					// ) ) {
					// return new Integer( authoringTool.loadCharacter( url ) );
					// } else if( authoringTool.isImportable( extension ) ) {
					// return authoringTool.importElement( url,
					// authoringTool.getWorld() );
					// }
					// return null;
					// }
					// };
					// worker.start();
					dtde.dropComplete(true);
					// } else if( dtde.getDropAction() ==
					// java.awt.dnd.DnDConstants.ACTION_NONE ){ //Doesn't work
					// in 1.3_0_10 or 1.3_0_11
					// // else if(
					// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde,
					// df) ) { // HACK for UniformResourceLocators
					// dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_LINK );
					// // HACK for UniformResourceLocators
					// java.awt.datatransfer.Transferable transferable =
					// dtde.getTransferable();
					// java.awt.datatransfer.DataFlavor[] flavors =
					// transferable.getTransferDataFlavors();
					// java.io.BufferedReader reader = null;
					// if (flavors != null && flavors.length > 0){
					// System.out.println("here!");
					// reader = new java.io.BufferedReader(
					// flavors[0].getReaderForText( transferable ) );
					// } else{
					// System.out.println("here 2!");
					// reader = new java.io.BufferedReader( df.getReaderForText(
					// transferable ) );
					// }
					// java.net.URL url = new java.net.URL( reader.readLine() );
					// String extension = url.getPath().substring(
					// url.getPath().lastIndexOf( '.' ) + 1 ).toUpperCase();
					// if( url.getPath().toLowerCase().endsWith( "." +
					// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.CHARACTER_EXTENSION
					// ) ) {
					// authoringTool.loadAndAddCharacter( url );
					// } else if( authoringTool.isImportable( extension ) ) {
					// authoringTool.importElement( url,
					// authoringTool.getWorld() );
					// }
					//
					// // final
					// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker
					// worker = new
					// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker()
					// {
					// // public Object construct() {
					// // if( url.getPath().toLowerCase().endsWith( "." +
					// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.CHARACTER_EXTENSION
					// ) ) {
					// // return new Integer( authoringTool.loadCharacter( url )
					// );
					// // } else if( authoringTool.isImportable( extension ) ) {
					// // return authoringTool.importElement( url,
					// authoringTool.getWorld() );
					// // }
					// // return null;
					// // }
					// // };
					// // worker.start();
					// dtde.dropComplete( true );
				} else {
					dtde.rejectDrop();
					dtde.dropComplete(false);
				}
			} catch (java.awt.datatransfer.UnsupportedFlavorException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: bad flavor", e);
			} catch (java.io.IOException e) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Drop didn't work: IOException", e);
			}
		}
	};
	java.awt.dnd.DropTarget renderTargetDropTarget;

	java.awt.dnd.DropTargetListener doNothingDropTargetListener = new java.awt.dnd.DropTargetListener() {
		@Override
		public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) {
		}
		@Override
		public void dragExit(java.awt.dnd.DropTargetEvent dte) {
		}
		@Override
		public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {
		}
		@Override
		public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {
		}
		@Override
		public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
		}
	};

	protected java.awt.event.MouseListener renderTargetMouseListener = new edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {

		@Override
		public void singleClickResponse(java.awt.event.MouseEvent ev) {
			edu.cmu.cs.stage3.alice.core.Transformable picked = pickedTransformable;
			edu.cmu.cs.stage3.alice.core.Element selectedElement = authoringTool.getSelectedElement();
			if (selectedElement == picked) {
				// do nothing
			} else if (affectSubpartsCheckBox.isSelected()) {
				authoringTool.setSelectedElement(picked);
			} else if (selectedElement != null && selectedElement.isDescendantOf(picked)) {
				authoringTool.setSelectedElement(picked);
			} else {
				edu.cmu.cs.stage3.alice.core.Element parent = null;
				while (picked != null && !picked.doEventsStopAscending() && picked.getParent() != selectedElement) {
					parent = picked.getParent();
					if (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
						picked = (edu.cmu.cs.stage3.alice.core.Transformable) parent;
					} else {
						picked = null;
					}
				}
				if (picked != null) {
					authoringTool.setSelectedElement(picked);
				} else {
					authoringTool.setSelectedElement(parent);
				}
			}
		}

	};

	// dummy objects
	edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener dummyGroupListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		@Override
		public void objectArrayPropertyChanging(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}
		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			CameraViewPanel.this.updateMoveCameraCombo();
		}
	};

	edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener potentialDummyGroupListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		@Override
		public void objectArrayPropertyChanging(edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
		}
		@Override
		public void objectArrayPropertyChanged(final edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev) {
			edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.hasDummyObjectGroup(world)) {
				edu.cmu.cs.stage3.alice.core.Group dummyGroup = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDummyObjectGroup(world);
				dummyGroup.values.addObjectArrayPropertyListener(dummyGroupListener);
				world.groups.removeObjectArrayPropertyListener(this);
			}
		}
	};

	protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
	protected AuthoringToolListener authoringToolListener = new AuthoringToolListener();

	// ///////////////
	// Constructor
	// ///////////////

	public CameraViewPanel(SceneEditor sceneEditor) {
		this.sceneEditor = sceneEditor; // TODO: remove this dependency
		jbInit();
		guiInit();

		if (boundingBoxFeedbackTransformable == null) {
			boundingBoxFeedbackTransformable = new edu.cmu.cs.stage3.alice.core.Transformable();
		}
		boundingBoxFeedbackTransformable.setLocalTransformationRightNow(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
		boundingBoxFeedbackTransformable.setParent(pickFeedbackTransformable);
		boundingBoxFeedbackTransformable.vehicle.set(pickFeedbackTransformable);
		dropFeedbackDecorator.setReferenceFrame(boundingBoxFeedbackTransformable);
		dropFeedbackPivotDecorator.setTransformable(boundingBoxFeedbackTransformable);
		// povInit();
	}

	private void guiInit() {
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			controlScrollPane.setPreferredSize(new java.awt.Dimension(255 + (fontSize - 12) * 14, 0)); // Aik
																										// Min
		} else {
			controlScrollPane.setPreferredSize(new java.awt.Dimension(305 + (fontSize - 12) * 14, 0));
		}
		// build the navigator
		guiNavigator = new edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator();
		guiNavigator.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		navPanel.add(guiNavigator, new java.awt.GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 0, 0), 0, 0));
		navPanel.setOpaque(true);

		// build the gallery viewer
		try {
			galleryViewer = new edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryViewer();
			galleryPanel.removeAll();
			galleryPanel.add(galleryViewer, java.awt.BorderLayout.CENTER);
		} catch (Throwable t) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Unable to create the gallery viewer", t);
		}

		// aspect ratio controls
		String[] defaultAspectRatios = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultAspectRatios();
		for (String defaultAspectRatio : defaultAspectRatios) {
			aspectRatioComboBox.addItem(defaultAspectRatio);
		}
		lensAngleSlider.setMinimum(1);
		lensAngleSlider.setMaximum(999);

		aspectRatioComboBox.addActionListener(new AspectRatioComboBoxListener());
		lensAngleSlider.addChangeListener(new LensAngleSliderListener());

		// single/quad
		singleQuadGroup.add(singleViewButton);
		singleQuadGroup.add(quadViewButton);
		singleViewButton.setSelected(true);

		quadPanel.setBackground(java.awt.Color.black);
		quadPanel.setLayout(new java.awt.GridLayout(2, 2, 1, 1));

		// dummy objects
		moveCameraCombo.setRenderer(new edu.cmu.cs.stage3.alice.authoringtool.util.ElementListCellRenderer(edu.cmu.cs.stage3.alice.authoringtool.util.ElementListCellRenderer.JUST_NAME));

		// icons
		cameraDummyButton.setIcon(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForString("dummyAtCamera"));
		objectDummyButton.setIcon(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForString("dummyAtObject"));

		// tooltips
		singleViewButton.setToolTipText("<html><font face=arial size=-1>Switch to a Single View of the World.</font></html>");
		quadViewButton.setToolTipText("<html><font face=arial size=-1>Switch to a Quad View of the World.<p><p>The Quad View shows the world from<p>the Right, Top, Front, and regular view.</font></html>");
		affectSubpartsCheckBox.setToolTipText("<html><font face=arial size=-1>Affect Subparts.<p><p>When selected, the mouse will<p>manipulate the part of an Object<p>clicked on instead of the whole Object.</font></html>");
		aspectRatioComboBox.setToolTipText("<html><font face=arial size=-1>Change the Aspect Ratio of the World.<p><p>The Aspect Ratio is a measure of the width<p>of the window that we look at the world through<p>versus its height.</font></html>");
		aspectRatioLabel.setToolTipText("<html><font face=arial size=-1>The Aspect Ratio is a measure of the width<p>of the window that we look at the world through<p>versus its height.</font></html>");
		lensAngleSlider.setToolTipText("<html><font face=arial size=-1>Change the Lens Angle of the Camera.<p><p>A wider lens angle will let<p>you see more of the world.</font></html>");
		lensAngleLabel.setToolTipText("<html><font face=arial size=-1>A wider lens angle will let<p>you see more of the world.</font></html>");
		cameraDummyButton.setToolTipText("<html><font face=arial size=-1>Create a New Dummy Object<p>at the Current Position of the Camera.</font></html>");
		objectDummyButton.setToolTipText("<html><font face=arial size=-1>Create a New Dummy Object<p>at the Current Position of the Selected Object.</font></html>");
		moveCameraCombo.setToolTipText("<html><font face=arial size=-1>Move the Camera to a Dummy<p>Object's Point of View.</font></html>");
		moveCameraLabel.setToolTipText(moveCameraCombo.getToolTipText());

		setVisibleControls(FEWER_CONTROLS);
	}

	// private void povInit() {
	// java.util.LinkedList inclusionList = new java.util.LinkedList();
	// inclusionList.add( new edu.cmu.cs.stage3.util.Criterion() {
	// public boolean accept( Object o ) {
	// return (o instanceof edu.cmu.cs.stage3.alice.core.Variable) &&
	// javax.vecmath.Matrix4d.class.isAssignableFrom(
	// (Class)((edu.cmu.cs.stage3.alice.core.Variable)o).valueClass.getValue()
	// );
	// }
	// } );
	// povTreeModel.setInclusionList( inclusionList );
	// povTreeModel.setRoot( new edu.cmu.cs.stage3.alice.core.Transformable() );
	// //HACK
	//
	// setPovTransformable( new edu.cmu.cs.stage3.alice.core.Transformable() );
	// //HACK
	// povTree.setModel( povTreeModel );
	// povTree.addTreeSelectionListener( new
	// javax.swing.event.TreeSelectionListener() {
	// public void valueChanged( javax.swing.event.TreeSelectionEvent ev ) {
	// javax.swing.tree.TreePath path = ev.getNewLeadSelectionPath();
	// if( (path != null) && (povTransformable != null) ) {
	// Object o = path.getLastPathComponent();
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation ani = new
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation inverseAni =
	// new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
	// ani.pointOfView.set(
	// (edu.cmu.cs.stage3.math.Matrix44)((edu.cmu.cs.stage3.alice.core.Variable)o).getValue()
	// );
	// ani.subject.set( povTransformable );
	// inverseAni.pointOfView.set( povTransformable.getLocalTransformation() );
	// inverseAni.subject.set( povTransformable );
	// authoringTool.performOneShot( ani, inverseAni, new
	// edu.cmu.cs.stage3.alice.core.Property[]{povTransformable.localTransformation}
	// );
	// }
	// }
	// } );
	// povTree.addMouseListener( povTreeMouseListener );
	// povTree.setCellRenderer( new
	// edu.cmu.cs.stage3.alice.authoringtool.util.DefaultElementTreeCellRenderer()
	// );
	// ((javax.swing.tree.DefaultTreeCellRenderer)povTree.getCellRenderer()).setLeafIcon(
	// null );
	// javax.swing.JTextField textField = new javax.swing.JTextField();
	// textField.setFont( povTree.getFont() );
	// //povTree.setCellEditor( new javax.swing.tree.DefaultTreeCellEditor(
	// povTree,
	// (javax.swing.tree.DefaultTreeCellRenderer)povTree.getCellRenderer() ) {
	// povTree.setCellEditor( new javax.swing.DefaultCellEditor( textField ) {
	// public java.awt.Component getTreeCellEditorComponent( javax.swing.JTree
	// jtree, Object value, boolean isSelected, boolean expanded, boolean leaf,
	// int row ) {
	// return super.getTreeCellEditorComponent( jtree,
	// ((edu.cmu.cs.stage3.alice.core.Element)value).name.getStringValue(),
	// isSelected, expanded, leaf, row );
	// }
	// public boolean shouldSelectCell( java.util.EventObject ev ) {
	// return false;
	// }
	// } );
	// povTree.setEditable( true );
	// }

	public void renderInit(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		this.authoringTool = authoringTool;
		renderTarget = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
		if (renderTarget != null) {
			renderTarget.setName("perspective");
			rtmm = new edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator(renderTarget);
			rtmm.addRenderTargetPickManipulatorListener(this);
			rtmm.setMode(defaultMoveMode);
			// rtom = new
			// edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetOrbitManipulator(
			// renderTarget, authoringTool.getUndoRedoStack(),
			// authoringTool.getOneShotScheduler() );
			// rtom.addRenderTargetPickManipulatorListener( this );
			// rtom.setEnabled( false );
			renderPanel.add(renderTarget.getAWTComponent(), java.awt.BorderLayout.CENTER);
			renderTarget.addRenderTargetListener(this);
			renderTargetDropTarget = new java.awt.dnd.DropTarget(renderTarget.getAWTComponent(), renderTargetDropTargetListener);
			renderTarget.getAWTComponent().setDropTarget(renderTargetDropTarget);
			renderTarget.getAWTComponent().addMouseListener(renderTargetMouseListener);

			// quad view
			renderTargetFromTheRight = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
			renderTargetFromTheTop = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();
			renderTargetFromTheFront = authoringTool.getRenderTargetFactory().createOnscreenRenderTarget();

			renderTargetFromTheRight.setName("right");
			renderTargetFromTheTop.setName("top");
			renderTargetFromTheFront.setName("front");

			renderTargetFromTheRight.addRenderTargetListener(this);
			renderTargetFromTheTop.addRenderTargetListener(this);
			renderTargetFromTheFront.addRenderTargetListener(this);

			renderTargetFromTheRight.addCamera(cameraFromTheRight.getSceneGraphCamera());
			renderTargetFromTheTop.addCamera(cameraFromTheTop.getSceneGraphCamera());
			renderTargetFromTheFront.addCamera(cameraFromTheFront.getSceneGraphCamera());

			rtmmFromTheRight = new edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator(renderTargetFromTheRight);
			rtmmFromTheTop = new edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator(renderTargetFromTheTop);
			rtmmFromTheFront = new edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetMultiManipulator(renderTargetFromTheFront);

			rtmmFromTheRight.setMode(defaultMoveMode);
			rtmmFromTheTop.setMode(defaultMoveMode);
			rtmmFromTheFront.setMode(defaultMoveMode);

			cameraFromTheRight.vehicle.set(camTransformableFromTheRight);
			cameraFromTheTop.vehicle.set(camTransformableFromTheTop);
			cameraFromTheFront.vehicle.set(camTransformableFromTheFront);

			cameraFromTheRight.minimumX.set(new Double(Double.NaN)); // Aik Min
																		// -
																		// move
																		// camera
																		// away
																		// in
																		// quad
																		// view
			cameraFromTheRight.minimumY.set(new Double(-3.0));
			cameraFromTheRight.maximumX.set(new Double(Double.NaN));
			cameraFromTheRight.maximumY.set(new Double(3.0));
			cameraFromTheTop.minimumX.set(new Double(Double.NaN));
			cameraFromTheTop.minimumY.set(new Double(-3.0));
			cameraFromTheTop.maximumX.set(new Double(Double.NaN));
			cameraFromTheTop.maximumY.set(new Double(3.0));
			cameraFromTheFront.minimumX.set(new Double(Double.NaN));
			cameraFromTheFront.minimumY.set(new Double(-3.0));
			cameraFromTheFront.maximumX.set(new Double(Double.NaN));
			cameraFromTheFront.maximumY.set(new Double(3.0));

			cameraFromTheRight.nearClippingPlaneDistance.set(new Double(.1));
			cameraFromTheTop.nearClippingPlaneDistance.set(new Double(.1));
			cameraFromTheFront.nearClippingPlaneDistance.set(new Double(.1));

			cameraFromTheRight.farClippingPlaneDistance.set(new Double(1000.0));
			cameraFromTheTop.farClippingPlaneDistance.set(new Double(1000.0));
			cameraFromTheFront.farClippingPlaneDistance.set(new Double(1000.0));

			edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44();
			m.rotateY(-Math.PI / 2.0);
			m.setPosition(new javax.vecmath.Vector3d(500.0, 0.75, 0.0));
			camTransformableFromTheRight.localTransformation.set(m);
			m = new edu.cmu.cs.stage3.math.Matrix44();
			m.rotateY(Math.PI);
			m.rotateX(-Math.PI / 2.0);
			m.setPosition(new javax.vecmath.Vector3d(0.0, 500.0, 0.0));
			camTransformableFromTheTop.localTransformation.set(m);
			m = new edu.cmu.cs.stage3.math.Matrix44();
			m.rotateY(Math.PI);
			m.setPosition(new javax.vecmath.Vector3d(0.0, 0.75, 500.0));
			camTransformableFromTheFront.localTransformation.set(m);

			singleViewButton.setSelected(true);
			setViewMode(SINGLE_VIEW_MODE);

		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("CameraViewPanel unable to create renderTarget", null);
		}
	}

	// ///////////////////
	// public methods
	// ///////////////////

	public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool) {
		defaultMoveMode = new edu.cmu.cs.stage3.alice.authoringtool.util.DefaultMoveMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		moveUpDownMode = new edu.cmu.cs.stage3.alice.authoringtool.util.RaiseLowerMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		turnLeftRightMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TurnLeftRightMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		turnForwardBackwardMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TurnForwardBackwardMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		tumbleMode = new edu.cmu.cs.stage3.alice.authoringtool.util.TumbleMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		resizeMode = new edu.cmu.cs.stage3.alice.authoringtool.util.ResizeMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		copyMode = new edu.cmu.cs.stage3.alice.authoringtool.util.CopyMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		orthoScrollMode = new edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicScrollMode(authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		// orthoZoomInMode = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode(
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode.ZOOM_IN,
		// authoringTool );
		// orthoZoomOutMode = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode(
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode.ZOOM_OUT,
		// authoringTool );
		orthoZoomInMode = new edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode(authoringTool, authoringTool.getUndoRedoStack(), authoringTool.getOneShotScheduler());
		// orthoZoomOutMode = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode(
		// edu.cmu.cs.stage3.alice.authoringtool.util.OrthographicZoomMode.ZOOM_OUT,
		// authoringTool, authoringTool.getUndoRedoStack(),
		// authoringTool.getOneShotScheduler() );

		defaultMoveModeButton = new ManipulatorModeButton(defaultMoveMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/defaultMoveMode"), "Move Objects Freely", "<html><font face=arial size=-1>Move Objects Freely with the Mouse.<p>&nbsp;<p><b>shift</b>: move up/down<p><b>control</b>: turn left/right<p><b>shift/control</b>: tumble</font></html>");
		moveUpDownModeButton = new ManipulatorModeButton(moveUpDownMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/moveUpDownMode"), "Move Objects Up and Down", "<html><font face=arial size=-1>Move Objects Up and Down with the Mouse.</font></html>");
		turnLeftRightModeButton = new ManipulatorModeButton(turnLeftRightMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/turnLeftRightMode"), "Turn Objects Left and Right", "<html><font face=arial size=-1>Turn Objects Left and Right with the Mouse.</font></html>");
		turnForwardBackwardModeButton = new ManipulatorModeButton(turnForwardBackwardMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/turnForwardBackwardMode"), "Turn Objects Forwards and Backwards", "<html><font face=arial size=-1>Turn Objects Forwards and Backwards with the Mouse.</font></html>");
		tumbleModeButton = new ManipulatorModeButton(tumbleMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/tumbleMode"), "Tumble Objects", "<html><font face=arial size=-1>Tumble Objects with the Mouse.</font></html>");
		resizeModeButton = new ManipulatorModeButton(resizeMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/resizeMode"), "Resize Objects", "<html><font face=arial size=-1>Resize Objects with the Mouse<p>&nbsp;<p><b>shift</b>: resize more slowly</font></html>");
		copyModeButton = new ManipulatorModeButton(copyMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/copyMode"), "Copy Objects", "<html><font face=arial size=-1>Copy Objects with the Mouse.</font></html>");
		orthoScrollModeButton = new ManipulatorModeButton(orthoScrollMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/orthoScrollMode"), "Scroll View", "<html><font face=arial size=-1>Scroll View with the Mouse.<p>&nbsp;<p><b>shift</b>: scroll more slowly<p><b>control</b>: scroll more quickly<p><b>shift+control</b>: scroll much more quickly</font></html>");
		orthoZoomInModeButton = new ManipulatorModeButton(orthoZoomInMode, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue("manipulatorModes/zoomInMode"), "Zoom View In and Out", "<html><font face=arial size=-1>Zoom View In and Out with the Mouse.<p>&nbsp;<p><b>shift</b>: zoom more slowly</font></html>");
		// orthoZoomOutModeButton = new ManipulatorModeButton( orthoZoomOutMode,
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue(
		// "manipulatorModes/zoomOutMode" ), "zoom out" );

		// mode cursors
		java.awt.Dimension bestSize = java.awt.Toolkit.getDefaultToolkit().getBestCursorSize(22, 22);
		java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(bestSize.width, bestSize.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString("manipulatorModes/orthoScrollMode"), 0, 0, 22, 22, null);
		orthoScrollMode.setPreferredCursor(java.awt.Toolkit.getDefaultToolkit().createCustomCursor(bi, new java.awt.Point(11, 11), "orthoScrollMode"));

		bestSize = java.awt.Toolkit.getDefaultToolkit().getBestCursorSize(22, 22);
		bi = new java.awt.image.BufferedImage(bestSize.width, bestSize.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString("manipulatorModes/zoomInMode"), 0, 0, 22, 22, null);
		orthoZoomInMode.setPreferredCursor(java.awt.Toolkit.getDefaultToolkit().createCustomCursor(bi, new java.awt.Point(7, 7), "zoomInMode"));

		// bestSize = java.awt.Toolkit.getDefaultToolkit().getBestCursorSize(
		// 22, 22 );
		// bi = new java.awt.image.BufferedImage( bestSize.width,
		// bestSize.height, java.awt.image.BufferedImage.TYPE_INT_ARGB );
		// bi.getGraphics().drawImage(
		// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getImageForString(
		// "manipulatorModes/zoomOutMode" ), 0, 0, 22, 22, null );
		// orthoZoomOutMode.setPreferredCursor(
		// java.awt.Toolkit.getDefaultToolkit().createCustomCursor( bi, new
		// java.awt.Point( 7, 7 ), "zoomOutMode" ) );

		singleViewButtonSave = quadViewButtonSave = defaultMoveModeButton;

		authoringTool.addElementSelectionListener(new edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener() {
			@Override
			public void elementSelected(edu.cmu.cs.stage3.alice.core.Element element) {
				if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
					selectedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) element;
				} else {
					selectedTransformable = null;
				}
				renderTarget.markDirty();
			}
		});
		guiNavigator.setAuthoringTool(authoringTool);

		if (galleryViewer != null) {
			galleryViewer.setAuthoringTool(authoringTool);
		}

		authoringTool.addAuthoringToolStateListener(authoringToolListener);
	}

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		if (this.viewMode != viewMode) {
			for (java.util.Enumeration enum0 = mouseModeGroup.getElements(); enum0.hasMoreElements();) {
				ManipulatorModeButton b = (ManipulatorModeButton) enum0.nextElement();
				if (b.isSelected()) {
					if (viewMode == SINGLE_VIEW_MODE) {
						singleViewButtonSave = b;
					} else if (viewMode == QUAD_VIEW_MODE) {
						quadViewButtonSave = b;
					}
				}
			}

			this.viewMode = viewMode;

			superRenderPanel.removeAll();
			quadPanel.removeAll();
			mouseModePanel.removeAll();
			for (java.util.Enumeration enum0 = mouseModeGroup.getElements(); enum0.hasMoreElements();) {
				ManipulatorModeButton b = (ManipulatorModeButton) enum0.nextElement();
				b.setSelected(false);
				mouseModeGroup.remove(b);
			}
			if (viewMode == SINGLE_VIEW_MODE) {
				mouseModePanel.add(defaultMoveModeButton, new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(moveUpDownModeButton, new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(turnLeftRightModeButton, new java.awt.GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(turnForwardBackwardModeButton, new java.awt.GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(tumbleModeButton, new java.awt.GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(resizeModeButton, new java.awt.GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(copyModeButton, new java.awt.GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				// mouseModePanel.add( javax.swing.Box.createGlue(), new
				// java.awt.GridBagConstraints( 7, 0, 1, 1, 1.0, 0.0,
				// java.awt.GridBagConstraints.WEST,
				// java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(
				// 0, 0, 2, 2 ), 0, 0 ) );

				mouseModeGroup.add(defaultMoveModeButton);
				mouseModeGroup.add(moveUpDownModeButton);
				mouseModeGroup.add(turnLeftRightModeButton);
				mouseModeGroup.add(turnForwardBackwardModeButton);
				mouseModeGroup.add(tumbleModeButton);
				mouseModeGroup.add(resizeModeButton);
				mouseModeGroup.add(copyModeButton);

				// singleViewButtonSave.setSelected( true );
				defaultMoveModeButton.setSelected(true);
				selectedButtonText = defaultMoveModeButton.titleText;
				guiNavigator.setImageSize(edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.LARGE_IMAGES);
				superRenderPanel.add(renderAndNavPanel, java.awt.BorderLayout.CENTER);
			} else if (viewMode == QUAD_VIEW_MODE) {
				mouseModePanel.add(defaultMoveModeButton, new java.awt.GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(turnLeftRightModeButton, new java.awt.GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(turnForwardBackwardModeButton, new java.awt.GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(tumbleModeButton, new java.awt.GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(resizeModeButton, new java.awt.GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(copyModeButton, new java.awt.GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				// mouseModePanel.add( javax.swing.Box.createGlue(), new
				// java.awt.GridBagConstraints( 6, 0, 1, 1, 1.0, 0.0,
				// java.awt.GridBagConstraints.WEST,
				// java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(
				// 0, 0, 2, 2 ), 0, 0 ) );
				mouseModePanel.add(orthoScrollModeButton, new java.awt.GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				mouseModePanel.add(orthoZoomInModeButton, new java.awt.GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0, 0, 2, 2), 0, 0));
				// mouseModePanel.add( orthoZoomOutModeButton, new
				// java.awt.GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0,
				// java.awt.GridBagConstraints.WEST,
				// java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0,
				// 2, 2 ), 0, 0 ) );
				// mouseModePanel.add( javax.swing.Box.createGlue(), new
				// java.awt.GridBagConstraints( 5, 1, 1, 1, 1.0, 0.0,
				// java.awt.GridBagConstraints.WEST,
				// java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(
				// 0, 0, 2, 2 ), 0, 0 ) );

				mouseModeGroup.add(defaultMoveModeButton);
				mouseModeGroup.add(turnLeftRightModeButton);
				mouseModeGroup.add(turnForwardBackwardModeButton);
				mouseModeGroup.add(tumbleModeButton);
				mouseModeGroup.add(resizeModeButton);
				mouseModeGroup.add(copyModeButton);
				mouseModeGroup.add(orthoScrollModeButton);
				mouseModeGroup.add(orthoZoomInModeButton);
				// mouseModeGroup.add( orthoZoomOutModeButton );

				// quadViewButtonSave.setSelected( true );
				defaultMoveModeButton.setSelected(true);
				selectedButtonText = defaultMoveModeButton.titleText;

				quadPanel.add(renderAndNavPanel);
				quadPanel.add(renderTargetFromTheTop.getAWTComponent());
				quadPanel.add(renderTargetFromTheRight.getAWTComponent());
				quadPanel.add(renderTargetFromTheFront.getAWTComponent());

				guiNavigator.setImageSize(edu.cmu.cs.stage3.alice.authoringtool.util.GuiNavigator.SMALL_IMAGES);
				superRenderPanel.add(quadPanel, java.awt.BorderLayout.CENTER);
			}

			if (affectSubpartsCheckBox.isSelected()) {
				affectSubpartsCheckBox.doClick();
			}
			mouseInfoBorder.setTitle(selectedButtonText);
			mouseInfoBorder.setTitleColor(defaultBorderColor);
			mouseModePanel.repaint();
			// this.add( superRenderPanel, java.awt.BorderLayout.CENTER );
			// //HACK: this should not be necessary. I don't know what's going
			// on.

			setTargetsDirty();
			revalidate();
			repaint();
		}
	}

	public void setTargetsDirty() {
		renderTarget.markDirty();
		renderTargetFromTheTop.markDirty();
		renderTargetFromTheRight.markDirty();
		renderTargetFromTheFront.markDirty();
	}

	private boolean HACK_isInvokedFromSetWorld = false;
	public void setWorld(edu.cmu.cs.stage3.alice.core.World world) {
		edu.cmu.cs.stage3.alice.scenegraph.Camera[] sgCameras = renderTarget.getCameras();
		for (Camera sgCamera2 : sgCameras) {
			renderTarget.removeCamera(sgCamera2);
		}

		this.world = world;
		if (world != null) {
			ground = world.getChildNamedIgnoreCase("ground");
		}
		if (this.world != null) {
			edu.cmu.cs.stage3.alice.core.Camera[] cameras = (edu.cmu.cs.stage3.alice.core.Camera[]) world.getDescendants(edu.cmu.cs.stage3.alice.core.Camera.class);
			if (cameras.length > 0) {
				renderCamera = cameras[0];
				renderTarget.addCamera(renderCamera.getSceneGraphCamera());
				originalCameraPOV = renderCamera.getTransformation(world);
				// setPovTransformable( renderCamera );
				guiNavigator.setObjectToNavigate(renderCamera);

				// aspect ratio and lens angle
				if (renderCamera instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
					edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera cam = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) renderCamera;
					Number hAngle = cam.horizontalViewingAngle.getNumberValue();
					Number vAngle = cam.verticalViewingAngle.getNumberValue();
					Boolean letterboxed = cam.isLetterboxedAsOpposedToDistorted.getBooleanValue();
					if (letterboxed == null) {
						cam.isLetterboxedAsOpposedToDistorted.set(Boolean.TRUE);
						letterboxed = cam.isLetterboxedAsOpposedToDistorted.getBooleanValue();
					}

					if (hAngle == null && vAngle != null) {
						cam.horizontalViewingAngle.set(new Double(4.0 / 3.0 * vAngle.doubleValue()));
						hAngle = cam.horizontalViewingAngle.getNumberValue();
					} else if (hAngle != null && vAngle == null) {
						cam.verticalViewingAngle.set(new Double(3.0 / 4.0 * hAngle.doubleValue()));
						vAngle = cam.verticalViewingAngle.getNumberValue();
					} else if (hAngle == null && vAngle == null) {
						cam.verticalViewingAngle.set(new Double(.5));
						vAngle = cam.verticalViewingAngle.getNumberValue();
						cam.horizontalViewingAngle.set(new Double(4.0 / 3.0 * vAngle.doubleValue()));
						hAngle = cam.horizontalViewingAngle.getNumberValue();
					}

					double aspectRatio = hAngle.doubleValue() / vAngle.doubleValue();
					String aspectRatioString = (String) cam.data.get("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString");
					// System.out.println( "aspectRatioString: " +
					// aspectRatioString );

					if (aspectRatioString == null) {
						aspectRatioString = "4/3";
					}

					Double parsedAspectRatio = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble(aspectRatioString);
					if (parsedAspectRatio == null) {
						parsedAspectRatio = new Double(0.0);
					}
					if (aspectRatio != parsedAspectRatio.doubleValue()) {
						// System.out.println( "aspectRatio: " + aspectRatio );
						// System.out.println( "parsedAspectRatio: " +
						// parsedAspectRatio );
						aspectRatioString = Double.toString(aspectRatio);
						cam.data.put("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString", aspectRatioString);
					}

					int lensAngle; // between 1 and 1000
					if (aspectRatio >= 1.0) {
						lensAngle = (int) (hAngle.doubleValue() * 1000.0 / Math.PI);
					} else {
						lensAngle = (int) (vAngle.doubleValue() * 1000.0 / Math.PI);
					}

					CameraViewPanel.this.HACK_isInvokedFromSetWorld = true;

					aspectRatioComboBox.setSelectedItem(aspectRatioString);

					try {
						lensAngleSlider.setValue(lensAngle);
					} finally {
						CameraViewPanel.this.HACK_isInvokedFromSetWorld = false;
					}
				}
			}

			// quad view
			camTransformableFromTheRight.vehicle.set(world);
			camTransformableFromTheTop.vehicle.set(world);
			camTransformableFromTheFront.vehicle.set(world);

			// drop feedback transformable
			pickFeedbackTransformable.vehicle.set(ground);
		} else {
			renderCamera = null;
		}
	}

	public java.awt.Dimension getRenderSize() {
		java.awt.Dimension size = renderPanel.getSize();
		// size.height -= navPanel.getHeight();
		return size;
	}

	public void activate() {
		if (viewMode == SINGLE_VIEW_MODE) {
			if (!superRenderPanel.isAncestorOf(renderAndNavPanel)) {
				superRenderPanel.add(renderAndNavPanel, java.awt.BorderLayout.CENTER);
			}
		} else if (viewMode == QUAD_VIEW_MODE) {
			if (!superRenderPanel.isAncestorOf(quadPanel)) {
				superRenderPanel.add(quadPanel, java.awt.BorderLayout.CENTER);
			}
		}
	}

	public void deactivate() {
		superRenderPanel.removeAll();
	}

	public void setVisibleControls(int howMuch) {
		if (howMuch == MORE_CONTROLS) {
			controlPanel.add(aspectPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			controlPanel.add(jSeparator2, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			controlPanel.add(markerPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			controlPanel.add(jSeparator3, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			moreFewerControlsButton.setText("fewer controls  <<");
		} else if (howMuch == FEWER_CONTROLS) {
			controlPanel.remove(aspectPanel);
			controlPanel.remove(jSeparator2);
			controlPanel.remove(markerPanel);
			controlPanel.remove(jSeparator3);
			moreFewerControlsButton.setText("more controls  >>");
		}
	}

	// ////////////////////
	// internal methods
	// ////////////////////

	protected void dropDummy(edu.cmu.cs.stage3.alice.core.ReferenceFrame referenceFrame) {
		edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		edu.cmu.cs.stage3.alice.core.Group dummyGroup = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDummyObjectGroup(world);
		edu.cmu.cs.stage3.alice.core.Dummy dummy = new edu.cmu.cs.stage3.alice.core.Dummy();
		dummy.name.set(edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild("Dummy", dummyGroup));
		dummyGroup.addChild(dummy);
		dummyGroup.values.add(dummy);
		dummy.vehicle.set(world);
		dummy.setPointOfViewRightNow(referenceFrame);
	}

	protected void updateMoveCameraCombo() {
		moveCameraCombo.removeAllItems();
		moveCameraCombo.addItem(NONE_DUMMY);

		edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		edu.cmu.cs.stage3.alice.core.Group dummyGroup = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDummyObjectGroup(world);
		Object[] dummies = dummyGroup.values.getArrayValue();
		for (Object dummie : dummies) {
			moveCameraCombo.addItem(dummie);
			if (renderCamera != null && dummie instanceof edu.cmu.cs.stage3.alice.core.Dummy) {
				javax.vecmath.Matrix4d m = renderCamera.getTransformation((edu.cmu.cs.stage3.alice.core.Dummy) dummie);
				if (m.equals(edu.cmu.cs.stage3.math.MathUtilities.getIdentityMatrix4d())) {
					moveCameraCombo.setSelectedItem(dummie);
				}
			}
		}
	}

	// public void setPovTransformable(
	// edu.cmu.cs.stage3.alice.core.Transformable trans ) {
	// povTransformable = trans;
	//
	// if( povTransformable != null ) {
	// povTreeModel.setRoot( povTransformable );
	// titledBorder3.setTitle(
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(
	// povTransformable, true ) + "'s Point Of Views" );
	// } else {
	// povTreeModel.setRoot( new edu.cmu.cs.stage3.alice.core.Transformable() );
	// // HACK
	// titledBorder3.setTitle( "Point Of Views" );
	// }
	// }
	//
	// protected void addCurrentPOVToVariables() {
	// if( povTransformable != null ) {
	// edu.cmu.cs.stage3.alice.core.Variable newPov = new
	// edu.cmu.cs.stage3.alice.core.Variable();
	// newPov.valueClass.set( edu.cmu.cs.stage3.math.Matrix44.class );
	// newPov.value.set( povTransformable.getLocalTransformation() );
	// newPov.name.set(
	// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild(
	// "PointOfView", povTransformable ) );
	// povTransformable.addChild( newPov );
	// povTransformable.variables.add( newPov );
	// }
	// }

	// ///////////////////////////////////////
	// Mouse event handling
	// ///////////////////////////////////////

	// protected final java.awt.event.MouseListener povTreeMouseListener = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter() {
	// protected void popupResponse( java.awt.event.MouseEvent e ) {
	// javax.swing.JTree tree = (javax.swing.JTree)e.getSource();
	// javax.swing.tree.TreePath path = tree.getPathForLocation( e.getX(),
	// e.getY() );
	// if( path != null ) {
	// Object node = path.getLastPathComponent();
	// if( node instanceof edu.cmu.cs.stage3.alice.core.Element ) {
	// javax.swing.JPopupMenu popup = createPopup(
	// (edu.cmu.cs.stage3.alice.core.Element)node, path );
	// popup.show( e.getComponent(), e.getX(), e.getY() );
	// edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen(
	// popup );
	// }
	// }
	// }
	//
	// private javax.swing.JPopupMenu createPopup( final
	// edu.cmu.cs.stage3.alice.core.Element element, javax.swing.tree.TreePath
	// path ) {
	// java.util.Vector popupStructure = new java.util.Vector();
	// Runnable renameRunnable = new
	// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.RenameRunnable(
	// element, povTree, path );
	// popupStructure.add( renameRunnable );
	// popupStructure.add(
	// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable.class
	// );
	//
	// return
	// edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.makeElementPopupMenu(
	// element, popupStructure );
	// }
	// };

	// /////////////////////////////
	// RenderListener Interface
	// /////////////////////////////

	private boolean oldBoundingBoxValue = false;
	@Override
	public void cleared(edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent ev) {
		setTargetsDirty();
		if (selectedTransformable != null) {
			oldBoundingBoxValue = selectedTransformable.isBoundingBoxShowing.booleanValue();
			// selectedTransformable.isBoundingBoxShowing.set( true );
			if (!oldBoundingBoxValue) {
				boundingBoxDecorator.setReferenceFrame(selectedTransformable);
				boundingBoxDecorator.setIsShowing(true);
				pivotDecorator.setTransformable(selectedTransformable);
				pivotDecorator.setIsShowing(true);
				// selectedTransformable.HACK_showBoundingBoxRightNow();
			}
		}

		if (world != null) {
			edu.cmu.cs.stage3.alice.core.Element[] dummies = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.Dummy.class));
			for (Element dummie : dummies) {
				edu.cmu.cs.stage3.alice.core.Dummy dummy = (edu.cmu.cs.stage3.alice.core.Dummy) dummie;
				dummy.getSceneGraphVisual().setIsShowing(true);
			}
		}
	}

	@Override
	public void rendered(edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent ev) {
		setTargetsDirty();
		if (selectedTransformable != null) {
			selectedTransformable.isBoundingBoxShowing.set(oldBoundingBoxValue);
			if (!oldBoundingBoxValue) {
				boundingBoxDecorator.setIsShowing(false);
				boundingBoxDecorator.setReferenceFrame(null);
				pivotDecorator.setIsShowing(false);
				pivotDecorator.setTransformable(null);
				// selectedTransformable.HACK_hideBoundingBoxRightNow();
			}
		}

		if (world != null) {
			edu.cmu.cs.stage3.alice.core.Element[] dummies = world.search(new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion(edu.cmu.cs.stage3.alice.core.Dummy.class));
			for (Element dummie : dummies) {
				edu.cmu.cs.stage3.alice.core.Dummy dummy = (edu.cmu.cs.stage3.alice.core.Dummy) dummie;
				dummy.getSceneGraphVisual().setIsShowing(false);
			}
		}

		edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTarget evRenderTarget = ev.getRenderTarget();
		String text = null;
		if (evRenderTarget == renderTargetFromTheRight) {
			text = fromRightText;
		} else if (evRenderTarget == renderTargetFromTheTop) {
			text = fromTopText;
		} else if (evRenderTarget == renderTargetFromTheFront) {
			text = fromFrontText;
		}
		if (text != null) {
			java.awt.Graphics g = evRenderTarget.getOffscreenGraphics();
			g.setColor(viewLabelColor);
			g.setFont(viewLabelFont);
			g.drawString(text, 12, 18);
			g.dispose();
		}
	}

	public void swapped(edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetEvent renderTargetEvent) {
		setTargetsDirty();
	}

	// //////////////////////////////////////////////////////
	// RenderTargetPickManipulatorListener Interface
	// //////////////////////////////////////////////////////

	@Override
	public void prePick(edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev) {
	}
	@Override
	public void postPick(edu.cmu.cs.stage3.alice.authoringtool.util.event.RenderTargetPickManipulatorEvent ev) {
		edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = ev.getPickInfo();
		if (pickInfo != null && pickInfo.getCount() > 0 && pickInfo.getVisualAt(0) != null && pickInfo.getVisualAt(0).getBonus() != null) {
			Object bonus = pickInfo.getVisualAt(0).getBonus();
			if (bonus instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				pickedTransformable = (edu.cmu.cs.stage3.alice.core.Transformable) bonus;
			} else {
				pickedTransformable = null;
			}
		} else {
			pickedTransformable = null;
		}
	}

	/*
	 * public void postPick( edu.cmu.cs.stage3.alice.authoringtool.util.event.
	 * RenderTargetPickManipulatorEvent ev ) {
	 * edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo =
	 * ev.getPickInfo(); if( (pickInfo != null) && (pickInfo.getCount() > 0) &&
	 * (pickInfo.getVisual( 0 ) != null) && (pickInfo.getVisual( 0 ).getBonus()
	 * != null) ) { Object o = pickInfo.getVisual( 0 ).getBonus(); if( o
	 * instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
	 * edu.cmu.cs.stage3.alice.core.Transformable bonus =
	 * (edu.cmu.cs.stage3.alice.core.Transformable)o;
	 * edu.cmu.cs.stage3.alice.core.Element selectedElement =
	 * authoringTool.getSelectedElement(); if( selectedElement == bonus ) { //
	 * do nothing } else if( affectSubpartsCheckBox.isSelected() ) {
	 * authoringTool.setSelectedElement( bonus ); } else if( (selectedElement !=
	 * null) && selectedElement.isDescendantOf( bonus ) ) {
	 * authoringTool.setSelectedElement( bonus ); } else { while( (bonus !=
	 * null) && (! bonus.doEventsStopAscending()) && (bonus.getParent() !=
	 * selectedElement) ) { edu.cmu.cs.stage3.alice.core.Element parent =
	 * bonus.getParent(); if( parent instanceof
	 * edu.cmu.cs.stage3.alice.core.Transformable ) { bonus =
	 * (edu.cmu.cs.stage3.alice.core.Transformable)parent; } else { bonus =
	 * null; authoringTool.setSelectedElement( parent ); } } pickedTransformable
	 * = bonus; if( bonus != null ) { authoringTool.setSelectedElement( bonus );
	 * } } } else { pickedTransformable = null; } } else { pickedTransformable =
	 * null; } }
	 */

	// ////////////////////////
	// GUI Classes
	// ////////////////////////

	class ManipulatorModeButton extends javax.swing.JToggleButton implements java.awt.event.MouseListener {
		protected edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode mode;
		protected javax.swing.border.Border selectedBorder = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED);
		protected javax.swing.border.Border unselectedBorder = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED);
		protected String titleText;

		public ManipulatorModeButton(edu.cmu.cs.stage3.alice.authoringtool.util.RenderTargetManipulatorMode mode, javax.swing.Icon icon, String titleText, String toolTip) {
			super(icon);
			this.mode = mode;
			setToolTipText(toolTip);
			this.titleText = titleText;

			if (icon != null) {
				java.awt.Dimension d = new java.awt.Dimension(icon.getIconWidth() + 4, icon.getIconHeight() + 4);
				setMinimumSize(d);
				setMaximumSize(d);
				setPreferredSize(d);
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("no icon found for mode: " + mode.getClass().getName(), null);
			}

			setBorder(unselectedBorder);
			setOpaque(false);
			addMouseListener(this);
			addChangeListener(new javax.swing.event.ChangeListener() {
				@Override
				public void stateChanged(javax.swing.event.ChangeEvent ev) {
					if (ManipulatorModeButton.this.isSelected()) {
						rtmm.setMode(ManipulatorModeButton.this.mode);
						rtmmFromTheRight.setMode(ManipulatorModeButton.this.mode);
						rtmmFromTheTop.setMode(ManipulatorModeButton.this.mode);
						rtmmFromTheFront.setMode(ManipulatorModeButton.this.mode);
						setBorder(selectedBorder);
						selectedButtonText = ManipulatorModeButton.this.titleText;
					} else {
						setBorder(unselectedBorder);
					}
				}
			});
		}

		@Override
		public void doClick() {
			super.doClick();
			selectedButtonText = ManipulatorModeButton.this.titleText;
			mouseInfoBorder.setTitle(selectedButtonText);
			mouseInfoBorder.setTitleColor(defaultBorderColor);
			mouseModePanel.repaint();

		}

		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
		}

		@Override
		public void mouseEntered(java.awt.event.MouseEvent e) {
			mouseInfoBorder.setTitle(titleText);
			mouseInfoBorder.setTitleColor(highlightBorderColor);
			mouseModePanel.repaint();
		}
		@Override
		public void mouseExited(java.awt.event.MouseEvent e) {
			mouseInfoBorder.setTitle(selectedButtonText);
			mouseInfoBorder.setTitleColor(defaultBorderColor);
			mouseModePanel.repaint();
		}
		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent e) {
		}

	}

	class AspectRatioComboBoxListener implements java.awt.event.ActionListener {
		@Override
		public void actionPerformed(java.awt.event.ActionEvent ev) {
			if (renderCamera instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
				edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera cam = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) renderCamera;
				String aspectRatioString = (String) aspectRatioComboBox.getSelectedItem();
				Double parsedAspectRatio = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.parseDouble(aspectRatioString);
				if (parsedAspectRatio == null) { // badly formatted string
					aspectRatioComboBox.setSelectedItem(cam.data.get("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString"));
				} else {
					if (HACK_isInvokedFromSetWorld) {
						// pass
					} else {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							cam.data.put("edu.cmu.cs.stage3.alice.authoringtool.editors.sceneeditor.aspectRatioString", aspectRatioString);
							double newAspectRatio = parsedAspectRatio.doubleValue();
							double lensAngle = lensAngleSlider.getValue() * Math.PI / 1000.0;
							double hAngle, vAngle;
							if (newAspectRatio < 1.0) {
								vAngle = lensAngle;
								hAngle = vAngle * newAspectRatio;
							} else {
								hAngle = lensAngle;
								vAngle = hAngle / newAspectRatio;
							}
							cam.horizontalViewingAngle.set(new Double(hAngle));
							cam.verticalViewingAngle.set(new Double(vAngle));
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		}
	}

	class LensAngleSliderListener implements javax.swing.event.ChangeListener {
		private Object previousHorizontalAngle;
		private Object previousVerticalAngle;
		public boolean duringChange = false;
		@Override
		public void stateChanged(javax.swing.event.ChangeEvent ev) {
			if (renderCamera instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) {
				edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera cam = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera) renderCamera;

				if (!duringChange) {
					duringChange = true;
					previousHorizontalAngle = cam.horizontalViewingAngle.get();
					previousVerticalAngle = cam.verticalViewingAngle.get();
				}
				authoringTool.getUndoRedoStack().setIsListening(false);

				double aspectRatio = cam.horizontalViewingAngle.doubleValue() / cam.verticalViewingAngle.doubleValue();
				// int sliderValue = lensAngleSlider.getValue();
				double lensAngle = lensAngleSlider.getValue() * Math.PI / 1000.0;
				double hAngle, vAngle;
				if (aspectRatio < 1.0) {
					vAngle = lensAngle;
					hAngle = vAngle * aspectRatio;
				} else {
					hAngle = lensAngle;
					vAngle = hAngle / aspectRatio;
				}
				cam.horizontalViewingAngle.set(new Double(hAngle));
				cam.verticalViewingAngle.set(new Double(vAngle));

				authoringTool.getUndoRedoStack().setIsListening(true);
				if (!lensAngleSlider.getValueIsAdjusting()) {
					duringChange = false;
					if (HACK_isInvokedFromSetWorld) {
						// pass
					} else {
						authoringTool.getUndoRedoStack().startCompound();
						authoringTool.getUndoRedoStack().push(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(cam.horizontalViewingAngle, previousHorizontalAngle, cam.horizontalViewingAngle.get()));
						authoringTool.getUndoRedoStack().push(new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyUndoableRedoable(cam.verticalViewingAngle, previousVerticalAngle, cam.verticalViewingAngle.get()));
						authoringTool.getUndoRedoStack().stopCompound();
					}
				}
			}
		}
	}

	class AuthoringToolListener implements edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateListener {
		@Override
		public void worldUnLoading(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
			if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.hasDummyObjectGroup(world)) {
				edu.cmu.cs.stage3.alice.core.Group dummyGroup = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDummyObjectGroup(world);
				dummyGroup.values.removeObjectArrayPropertyListener(dummyGroupListener);
			}
		}

		@Override
		public void worldLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
			if (world != null) {
				if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.hasDummyObjectGroup(world)) {
					edu.cmu.cs.stage3.alice.core.Group dummyGroup = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDummyObjectGroup(world);
					dummyGroup.values.addObjectArrayPropertyListener(dummyGroupListener);
				} else {
					world.groups.addObjectArrayPropertyListener(potentialDummyGroupListener);
				}
			}
		}

		@Override
		public void stateChanged(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void stateChanging(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldLoading(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldStarting(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldStopping(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldPausing(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldUnLoaded(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldSaving(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldStarted(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldStopped(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldPaused(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
		@Override
		public void worldSaved(edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev) {
			setTargetsDirty();
		}
	}

	// /////////////////
	// GUI Callbacks
	// /////////////////

	void moreFewerControlsButton_actionPerformed(ActionEvent e) {
		if (aspectPanel.getParent() != null) {
			setVisibleControls(FEWER_CONTROLS);
		} else {
			setVisibleControls(MORE_CONTROLS);
		}
	}

	// void btnStandUp_actionPerformed(ActionEvent e) {
	// edu.cmu.cs.stage3.alice.core.response.StandUpAnimation ani = new
	// edu.cmu.cs.stage3.alice.core.response.StandUpAnimation();
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation inverseAni =
	// new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
	// ani.asSeenBy.set(world);
	// ani.transformable.set( renderCamera );
	// inverseAni.transformable.set( renderCamera );
	// inverseAni.asSeenBy.set(world);
	// inverseAni.pointOfView.set( renderCamera.localTransformation.get() );
	// authoringTool.performOneShot( ani, inverseAni, new
	// edu.cmu.cs.stage3.alice.core.Property[]{renderCamera.localTransformation}
	// );
	// }
	//
	// void btnEyeHeight_actionPerformed(ActionEvent e) {
	// double xPos = renderCamera.getPosition(world).x;
	// double zPos = renderCamera.getPosition(world).z;
	// edu.cmu.cs.stage3.alice.core.response.PositionAnimation ani = new
	// edu.cmu.cs.stage3.alice.core.response.PositionAnimation();
	// edu.cmu.cs.stage3.alice.core.response.PositionAnimation inverseAni = new
	// edu.cmu.cs.stage3.alice.core.response.PositionAnimation();
	// ani.asSeenBy.set(world);
	// ani.transformable.set( renderCamera );
	// ani.position.set( new javax.vecmath.Vector3d( xPos, 1.6, zPos ) );
	// inverseAni.transformable.set( renderCamera );
	// inverseAni.asSeenBy.set( world );
	// inverseAni.position.set( renderCamera.getPosition( world ) );
	// authoringTool.performOneShot( ani, inverseAni, new
	// edu.cmu.cs.stage3.alice.core.Property[] {
	// renderCamera.localTransformation } );
	// }
	//
	// void btnResetCamera_actionPerformed(ActionEvent e) {
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation ani = new
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
	// edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation inverseAni =
	// new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
	// ani.pointOfView.set( originalCameraPOV );
	// ani.transformable.set( renderCamera );
	// inverseAni.pointOfView.set( renderCamera.getLocalTransformation() );
	// inverseAni.transformable.set( renderCamera );
	// authoringTool.performOneShot( ani, inverseAni, new
	// edu.cmu.cs.stage3.alice.core.Property[]{renderCamera.localTransformation}
	// );
	// }

	void singleViewButton_actionPerformed(ActionEvent e) {
		setViewMode(SINGLE_VIEW_MODE);
	}

	void quadViewButton_actionPerformed(ActionEvent e) {
		setViewMode(QUAD_VIEW_MODE);
	}

	void affectSubpartsCheckBox_actionPerformed(ActionEvent e) {
		boolean ascendTreeEnabled = !affectSubpartsCheckBox.isSelected();
		rtmm.setAscendTreeEnabled(ascendTreeEnabled);
		// rtom.setAscendTreeEnabled( ascendTreeEnabled );
		if (ascendTreeEnabled) {
			defaultMoveModeButton.doClick();
		} else {
			tumbleModeButton.doClick();
		}
	}

	void cameraDummyButton_actionPerformed(ActionEvent e) {
		if (renderCamera != null) {
			dropDummy(renderCamera);
		}
	}

	void objectDummyButton_actionPerformed(ActionEvent e) {
		edu.cmu.cs.stage3.alice.core.Element selectedElement = authoringTool.getSelectedElement();
		if (selectedElement != null && selectedElement instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame) {
			dropDummy((edu.cmu.cs.stage3.alice.core.ReferenceFrame) selectedElement);
		}
	}

	void moveCameraCombo_actionPerformed(ActionEvent e) {
		Object selected = moveCameraCombo.getSelectedItem();
		if (selected instanceof edu.cmu.cs.stage3.alice.core.Dummy) {
			edu.cmu.cs.stage3.alice.core.Dummy dummy = (edu.cmu.cs.stage3.alice.core.Dummy) selected;
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation anim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoAnim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
			anim.pointOfView.set(edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d());
			anim.subject.set(renderCamera);
			anim.asSeenBy.set(dummy);
			undoAnim.pointOfView.set(renderCamera.getLocalTransformation());
			undoAnim.subject.set(renderCamera);
			authoringTool.performOneShot(anim, undoAnim, new edu.cmu.cs.stage3.alice.core.Property[]{renderCamera.localTransformation});
		}
	}

	// /////////////////
	// Autogenerated
	// /////////////////

	TitledBorder titledBorder1;
	FlowLayout flowLayout4 = new FlowLayout();
	BorderLayout borderLayout1 = new BorderLayout();
	ButtonGroup btnGrpTheMouse = new ButtonGroup();
	Border border1;
	TitledBorder titledBorder2;
	Border border2;
	TitledBorder titledBorder3;
	Border border3;
	Border border4;
	Border border5;
	JPanel galleryPanel = new JPanel();
	JLabel placeHolderLabel = new JLabel();
	Component component2;
	BorderLayout borderLayout2 = new BorderLayout();
	JPanel superRenderPanel = new JPanel();
	JPanel navPanel = new JPanel();
	JPanel renderPanel = new JPanel();
	BorderLayout borderLayout5 = new BorderLayout();
	JPanel renderAndNavPanel = new JPanel();
	BorderLayout borderLayout3 = new BorderLayout();
	BorderLayout borderLayout4 = new BorderLayout();
	Border border6;
	javax.swing.border.TitledBorder mouseInfoBorder = new javax.swing.border.TitledBorder("Mouse Controls");
	JScrollPane controlScrollPane = new JScrollPane();
	GridBagLayout gridBagLayout = new GridBagLayout();
	JSlider lensAngleSlider = new JSlider();
	JPanel aspectPanel = new JPanel();
	JLabel moveCameraLabel = new JLabel();
	JSeparator jSeparator3 = new JSeparator();
	JSeparator jSeparator2 = new JSeparator();
	JSeparator jSeparator1 = new JSeparator();
	JRadioButton quadViewButton = new JRadioButton();
	JPanel mouseModePanel = new JPanel();
	JComboBox aspectRatioComboBox = new JComboBox();
	JPanel mousePanel = new JPanel();
	JPanel markerPanel = new JPanel();
	JButton objectDummyButton = new JButton();
	JCheckBox affectSubpartsCheckBox = new JCheckBox();
	JRadioButton singleViewButton = new JRadioButton();
	JLabel aspectRatioLabel = new JLabel();
	JComboBox moveCameraCombo = new JComboBox();
	JPanel controlPanel = new JPanel();
	JButton cameraDummyButton = new JButton();
	Component component1;
	JLabel lensAngleLabel = new JLabel();

	private JButton moreFewerControlsButton = new JButton();

	private void jbInit() {
		mouseInfoBorder.setTitleFont(new java.awt.Font("Dialog", 0, 11));
		border1 = BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164));
		titledBorder2 = new TitledBorder(BorderFactory.createEmptyBorder(), "");
		border2 = BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164));
		titledBorder3 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white, new Color(165, 164, 164)), "Point Of View");
		border3 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border4 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		border5 = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		component2 = Box.createVerticalStrut(150);
		border6 = BorderFactory.createEmptyBorder(0, 0, 1, 1);
		component1 = Box.createGlue();
		flowLayout4.setHgap(0);
		flowLayout4.setVgap(0);
		placeHolderLabel.setFont(new java.awt.Font("Dialog", 0, (int) (24 * fontSize / 12.0)));
		placeHolderLabel.setForeground(Color.gray);
		placeHolderLabel.setText("The Gallery will go here.");
		galleryPanel.setLayout(borderLayout2);
		navPanel.setLayout(gridBagLayout);
		navPanel.setBackground(Color.white);
		renderPanel.setLayout(borderLayout5);
		renderPanel.setBackground(Color.black);
		renderAndNavPanel.setLayout(borderLayout3);
		renderAndNavPanel.setBackground(Color.black);
		superRenderPanel.setLayout(borderLayout4);
		superRenderPanel.setBackground(Color.black);
		lensAngleSlider.setOpaque(false);
		lensAngleSlider.setPreferredSize(new Dimension(150, 16));
		lensAngleSlider.setMinimumSize(new Dimension(10, 16));
		aspectPanel.setLayout(gridBagLayout);
		aspectPanel.setBorder(border4);
		aspectPanel.setOpaque(false);
		moveCameraLabel.setText("move camera to dummy:");
		jSeparator3.setForeground(Color.gray);
		jSeparator2.setForeground(Color.gray);
		jSeparator1.setForeground(Color.gray);
		quadViewButton.setOpaque(false);
		quadViewButton.setText("quad view");
		quadViewButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				quadViewButton_actionPerformed(e);
			}
		});
		mouseModePanel.setLayout(gridBagLayout);
		mouseModePanel.setOpaque(false);
		mouseModePanel.setBorder(mouseInfoBorder);
		aspectRatioComboBox.setMinimumSize(new Dimension(80, (int) (20 * fontSize / 12.0)));
		aspectRatioComboBox.setPreferredSize(new Dimension(80, (int) (20 * fontSize / 12.0)));
		aspectRatioComboBox.setEditable(true);
		mousePanel.setLayout(gridBagLayout);
		mousePanel.setBackground(new Color(236, 235, 235));
		mousePanel.setBorder(border3);
		markerPanel.setLayout(gridBagLayout);
		markerPanel.setBorder(border5);
		markerPanel.setOpaque(false);
		objectDummyButton.setHorizontalAlignment(SwingConstants.LEFT);
		objectDummyButton.setMargin(new Insets(2, 8, 2, 8));
		objectDummyButton.setText("drop dummy at selected object");
		objectDummyButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				objectDummyButton_actionPerformed(e);
			}
		});
		affectSubpartsCheckBox.setOpaque(false);
		affectSubpartsCheckBox.setText("affect subparts");
		affectSubpartsCheckBox.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				affectSubpartsCheckBox_actionPerformed(e);
			}
		});
		singleViewButton.setOpaque(false);
		singleViewButton.setText("single view");
		singleViewButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				singleViewButton_actionPerformed(e);
			}
		});
		aspectRatioLabel.setText("aspect ratio:");
		moveCameraCombo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveCameraCombo_actionPerformed(e);
			}
		});
		controlPanel.setLayout(gridBagLayout);
		controlPanel.setBackground(new Color(236, 235, 235));
		cameraDummyButton.setHorizontalAlignment(SwingConstants.LEFT);
		cameraDummyButton.setMargin(new Insets(2, 8, 2, 8));
		cameraDummyButton.setText("drop dummy at camera");
		cameraDummyButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cameraDummyButton_actionPerformed(e);
			}
		});
		lensAngleLabel.setText("lens angle:");
		controlScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		moreFewerControlsButton.setText("more controls  >>");
		moreFewerControlsButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moreFewerControlsButton_actionPerformed(e);
			}
		});

		galleryPanel.add(placeHolderLabel, BorderLayout.NORTH);
		galleryPanel.add(component2, BorderLayout.CENTER);
		titledBorder1 = new TitledBorder("");
		renderAndNavPanel.add(renderPanel, BorderLayout.CENTER);
		renderAndNavPanel.add(navPanel, BorderLayout.SOUTH);
		controlScrollPane.getViewport().add(controlPanel, null);
		markerPanel.add(cameraDummyButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		markerPanel.add(objectDummyButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 0), 0, 0));
		markerPanel.add(moveCameraLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 2, 0), 0, 0));
		markerPanel.add(moveCameraCombo, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(mousePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(markerPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		mousePanel.add(affectSubpartsCheckBox, new GridBagConstraints(0, 8, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 16, 0, 0), 0, 0));
		mousePanel.add(singleViewButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mousePanel.add(quadViewButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		mousePanel.add(mouseModePanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 16, 0, 16), 0, 0));
		controlPanel.add(aspectPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		aspectPanel.add(aspectRatioLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		aspectPanel.add(aspectRatioComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 6, 4, 10), 0, 0));
		aspectPanel.add(lensAngleLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
		aspectPanel.add(lensAngleSlider, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 4, 4), 0, 0));
		controlPanel.add(component1, new GridBagConstraints(0, 8, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(jSeparator1, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(jSeparator2, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(jSeparator3, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		controlPanel.add(moreFewerControlsButton, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 4, 0, 0), 0, 0));
		superRenderPanel.add(renderAndNavPanel, BorderLayout.CENTER);

		setLayout(borderLayout1);
		this.add(controlScrollPane, BorderLayout.EAST);
		this.add(galleryPanel, BorderLayout.SOUTH);
		this.add(superRenderPanel, BorderLayout.CENTER);
	}
}