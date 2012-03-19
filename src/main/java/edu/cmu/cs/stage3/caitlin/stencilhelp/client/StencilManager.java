package edu.cmu.cs.stage3.caitlin.stencilhelp.client;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.cs.stage3.alice.authoringtool.util.CustomMouseAdapter;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StateCapsule;
import edu.cmu.cs.stage3.caitlin.stencilhelp.application.StencilApplication;

public class StencilManager implements MouseListener, MouseMotionListener, KeyListener, StencilClient, StencilStackChangeListener {
	StencilApplication stencilApp = null;
	StencilPanel stencilPanel = null;
	ObjectPositionManager positionManager = null;
	Vector updateShapes = null;
	Vector clearRegions = null;
	StencilParser stencilParser = null;

	Vector mouseEventListeners = new Vector();
	Vector keyEventListeners = new Vector();
	Vector stencilFocusListeners = new Vector();
	Vector layoutChangeListeners = new Vector();
	Vector stencilStackChangeListeners = new Vector();
	Vector readWriteListeners = new Vector();
	Vector stencilList = new Vector();
	int currentStencilIndex = 0;
	StencilObject focalObject = null;
	StencilMouseAdapter mouseAdapter = new StencilMouseAdapter();
	Stencil errorStencil = null;

	boolean writeEnabled = true;
	boolean stencilChanged = false;
	String worldToLoad = null;
	String nextStack = null;
	String previousStack = null;
	long lastRedrawTime = -1;

	boolean overrideMinResolution = false;

	public StencilManager(StencilApplication stencilApp) {
		this.stencilApp = stencilApp;
		stencilPanel = new StencilPanel(this);
		addMouseEventListener(stencilPanel);
		addReadWriteListener(stencilPanel);
		positionManager = new ObjectPositionManager(stencilApp);
		StencilManager.Stencil currentStencil = new StencilManager.Stencil();

		stencilList.addElement(currentStencil);
		// createDefaultStencilObjects();
		triggerRefresh();
	}

	protected void createDefaultStencilObjects() {
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);

		NavigationBar navBar = new NavigationBar(this, positionManager);
		addMouseEventListener(navBar);
		addStencilStackChangeListener(navBar);
		currentStencil.addObject(navBar);

		Menu menu = new Menu(this);
		addMouseEventListener(menu);
		addStencilFocusListener(menu);
		stencilPanel.addMessageListener(menu);
		currentStencil.addObject(menu);
	}

	protected void triggerRefresh() {
		stencilPanel.redraw();
	}

	protected void triggerRefresh(long time) {
		if (time > lastRedrawTime) {
			lastRedrawTime = time;
			stencilPanel.redraw();
		}
	}

	/* called by menu */
	// COME BACK - general purpose way to request focus for all screen objects
	protected void requestFocus(StencilObject newFocalObject) {

		if (focalObject != null && focalObject instanceof StencilFocusListener && stencilFocusListeners.contains(focalObject)) {
			((StencilFocusListener) focalObject).focusLost();
		}
		if (newFocalObject != null && newFocalObject instanceof StencilFocusListener && stencilFocusListeners.contains(focalObject)) {
			((StencilFocusListener) newFocalObject).focusGained();
		}
		// focalObject = newFocalObject;
		setNewFocalObject(newFocalObject);
	}

	// COME BACK - THIS SHOULD NOT BE THIS WAY FOREVER
	protected String showDialog(javax.swing.filechooser.FileFilter filter) {
		JFileChooser chooser = new JFileChooser();
		if (filter != null) {
			chooser.setFileFilter(filter);
		}
		int returnVal = chooser.showOpenDialog(stencilPanel);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName();
		} else {
			return null;
		}

	}

	// COME BACK - remove this
	public StencilManager.Stencil newStencil() {
		return new StencilManager.Stencil();
	}

	public StencilManager.Stencil newStencil(int stepsToGoBack) {
		Stencil newStencil = new Stencil();
		newStencil.setSteps(stepsToGoBack);
		return newStencil;
	}

	public void loadStencilsFile() {
		File loadFile = null;
		StencilFileFilter filter = new StencilManager.StencilFileFilter();

		while (loadFile == null) {
			String fileName = showDialog(filter);

			// if the user specified a filename (must be stencil file), make
			// sure it's valid
			if (fileName != null) {
				loadFile = new File(fileName);

				// check to see if the file already exists, if not we're fine
				if (!loadFile.exists()) {
					int ans = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog("Can't find " + fileName + ". Please choose another", "Can't find file", javax.swing.JOptionPane.OK_CANCEL_OPTION);

					if (ans == javax.swing.JOptionPane.CANCEL_OPTION) {
						return;
					} else {
						loadFile = null; // make them choose again
					}
				} else {} // no name conflict, we can proceed
			} else { // no filename specified, interpret this is a cancel
				return;
			}
		}

		loadStencilTutorial(loadFile);
	}

	public void saveStencilsFile() {
		File saveFile = null;

		while (saveFile == null) {
			String fileName = showDialog(null);

			// if the user specified a filename, make sure it's valid
			if (fileName != null) {
				saveFile = new File(fileName);
				// make sure this is an appropriate filename
				if (!fileName.endsWith(".stl")) {
					fileName = fileName + ".stl";
				}
				// check to see if the file already exists, if not we're fine
				if (saveFile.exists()) {
					String msg = "This file already exists, do you want to overwrite it?";
					int ans = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog(msg, "File already exists", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);

					if (ans == javax.swing.JOptionPane.YES_OPTION) {
						// we can proceed
					} else if (ans == javax.swing.JOptionPane.NO_OPTION) {
						saveFile = null;
					} else if (ans == javax.swing.JOptionPane.CANCEL_OPTION) {
						return;
					}

				} else {} // no name conflict, we can proceed
			} else { // no filename specified, interpret this is a cancel
				return;
			}
		}

		// at this point we should have a valid saveFile, create a new document

		Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException pce) {
			document = null;
		}

		if (document != null) {
			Element root = document.createElement("stencilStack");
			root.setAttribute("access", "read");
			if (worldToLoad != null) {
				root.setAttribute("world", worldToLoad);
			}
			if (nextStack != null) {
				root.setAttribute("nextStack", nextStack);
			}
			if (previousStack != null) {
				root.setAttribute("previousStack", previousStack);
			}
			for (int i = 0; i < stencilList.size(); i++) {
				StencilManager.Stencil stencil = (StencilManager.Stencil) stencilList.elementAt(i);
				stencil.write(document, root);
			}

			document.appendChild(root);
			document.getDocumentElement().normalize();

			// try {
			// ((com.sun.xml.tree.XmlDocument)document).write( new
			// PrintWriter(new BufferedWriter(new FileWriter(saveFile))) );
			// } catch (IOException ioe)
			// {System.err.println("problems creating printwriter");};
			try {
				FileWriter fileWriter = new FileWriter(saveFile);
				edu.cmu.cs.stage3.xml.Encoder.write(document, fileWriter);
				fileWriter.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void toggleLock() {
		setWriteEnabled(!writeEnabled);
	}

	/* create and destroy objects */

	public void createNewHole(Point p) {
		String id = stencilApp.getIDForPoint(p, false);
		if (id != null) {
			Hole h = new Hole(id, positionManager, stencilApp, this);
			addMouseEventListener(h);
			addLayoutChangeListener(h);
			Note n = new Note(p, new Point(30, 30), h, positionManager, this, false);
			addMouseEventListener(n);
			addKeyEventListener(n);
			StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			currentStencil.addObject(h);
			currentStencil.addObject(n);
			// focalObject = n;
			setNewFocalObject(n);
			announceLayoutChange();
			n.initializeNote(); // THIS PROBABLY WANTS TO BE DONE THROUGH NOTE
			this.triggerRefresh();
		}
	}

	public void createNewFrame(Point p) {
		String id = stencilApp.getIDForPoint(p, false);
		if (id != null) {
			Frame h = new Frame(id, positionManager);
			addLayoutChangeListener(h);
			Note n = new Note(p, new Point(30, 30), h, positionManager, this, false);
			addMouseEventListener(n);
			addKeyEventListener(n);
			StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			currentStencil.addObject(h);
			currentStencil.addObject(n);
			// focalObject = n;
			setNewFocalObject(n);
			announceLayoutChange();
			n.initializeNote(); // THIS PROBABLY WANTS TO BE DONE THROUGH NOTE
			this.triggerRefresh();
		}
	}
	public void createNewNote(Point p) {
		Note n = new Note(p, new Point(0, 0), null, positionManager, this, false);
		addMouseEventListener(n);
		addKeyEventListener(n);
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		currentStencil.addObject(n);
		announceLayoutChange();
		n.initializeNote();
		this.triggerRefresh();

		// clean this
		// focalObject = n;
		setNewFocalObject(n);
	}
	public void removeAllObjects() {
		// focalObject = null;
		setNewFocalObject(null);
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		currentStencil.removeAllObjects();
		stencilChanged = true;
	}

	/* Current Stencil Object stuff */
	protected Vector getShapesToDraw() {
		Vector shapes = new Vector();
		Stencil currentStencil = (Stencil) stencilList.elementAt(currentStencilIndex);
		Vector currentObjects = currentStencil.getObjects();
		for (int i = 0; i < currentObjects.size(); i++) {
			StencilObject screenObj = (StencilObject) currentObjects.elementAt(i);
			Vector temp = screenObj.getShapes();
			if (temp != null) {
				shapes.addAll(temp);
			}
		}
		return shapes;
	}

	/* EXPERIMENTAL - REMOVE?????? */
	// a call to this must be followed by an immediate call to getClearRegions
	public Vector getUpdateShapes() {
		updateDrawInfo();
		return updateShapes;
	}

	public Vector getClearRegions() {
		return clearRegions;
	}

	protected void updateDrawInfo() {
		updateShapes = new Vector();
		clearRegions = new Vector();
		Stencil currentStencil = (Stencil) stencilList.elementAt(currentStencilIndex);
		Vector currentObjects = currentStencil.getObjects();
		Vector unmodifiedObjects = new Vector();
		boolean overlapping = false;

		if (stencilChanged == false) {

			// add the objects and clear regions that have been modified
			for (int i = 0; i < currentObjects.size(); i++) {
				StencilObject obj = (StencilObject) currentObjects.elementAt(i);

				if (obj.isModified()) {
					Vector objShapes = obj.getShapes();
					if (objShapes != null) {
						updateShapes.addAll(objShapes);
					}
					java.awt.Rectangle clear = obj.getPreviousRectangle();
					if (clear != null) {
						clearRegions.addElement(clear);
					}
				} else {
					unmodifiedObjects.addElement(obj);
				}
			}

			// check to see if there is any overlapping
			for (int i = 0; i < unmodifiedObjects.size(); i++) {
				StencilObject obj = (StencilObject) unmodifiedObjects.elementAt(i);
				java.awt.Rectangle objRect = obj.getRectangle();
				java.awt.Rectangle prevObjRect = obj.getPreviousRectangle();
				if (prevObjRect == null) {
					prevObjRect = objRect;
				}
				if (objRect != null) {
					for (int j = 0; j < clearRegions.size(); j++) {
						java.awt.Rectangle clearRect = (java.awt.Rectangle) clearRegions.elementAt(j);
						if (clearRect.intersects(objRect) || clearRect.intersects(prevObjRect)) {
							overlapping = true;
							break;
						}
					}
				}
			}
		}

		// if overlapping - just redraw everything.
		if (overlapping || stencilChanged) {
			updateShapes = new Vector();
			clearRegions = new Vector();
			clearRegions.addElement(new java.awt.Rectangle(0, 0, stencilPanel.getWidth(), stencilPanel.getHeight()));
			for (int i = 0; i < currentObjects.size(); i++) {
				StencilObject obj = (StencilObject) currentObjects.elementAt(i);
				Vector newShapes = obj.getShapes();
				if (newShapes != null) {
					updateShapes.addAll(obj.getShapes());
				}
			}
			stencilChanged = false;
		}
	}

	/* Stencil Navigation stuff */
	public boolean hasNext() {
		boolean autoAdvancingHole = false;
		Stencil currentStencil = (Stencil) stencilList.elementAt(currentStencilIndex);
		if (currentStencil != null) {
			Vector stencilObjects = currentStencil.getObjects();
			for (int i = 0; i < stencilObjects.size(); i++) {
				StencilObject stencilObj = (StencilObject) stencilObjects.elementAt(i);
				if (stencilObj instanceof Hole) {
					if (((Hole) stencilObj).getAutoAdvance() == true && ((Hole) stencilObj).getAdvanceEvent() != Hole.ADVANCE_ON_ENTER) {
						autoAdvancingHole = true;
					}

				}
			}
		}
		if (currentStencilIndex < stencilList.size() - 1 && autoAdvancingHole == false) {
			return true;
		} else {
			return false;
		}
	}
	public boolean hasPrevious() {
		if (currentStencilIndex > 0) {
			Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			if (currentStencil.getStepsToGoBack() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public int getStencilNumber() {
		return currentStencilIndex;
	}
	public int getNumberOfStencils() {
		return stencilList.size();
	}
	public void reloadStencils() {
		loadWorld();

		// tell current stencil that it's not current
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		currentStencil.setCurrentStencil(false);
		currentStencilIndex = 0;

		// tell next stencil that it is current
		currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		currentStencil.setCurrentStencil(true);
		broadcastStencilNumberChange();

		// reset the waypoints
		stencilApp.clearWayPoints();
		stencilApp.makeWayPoint();

		stencilChanged = true;
	}

	protected boolean checkState(Stencil currentStencil) {
		if (writeEnabled) {
			// if writing enabled - get the current state so we have the "right"
			// answer
			currentStencil.setEndState(stencilApp.getCurrentState());
			return true;
		} else {
			// compare against saved "right" answer
			if (currentStencil.getEndState() != null) {
				boolean appInRightState = stencilApp.doesStateMatch(currentStencil.getEndState());
				if (!appInRightState) {
					return false;
					// TODO: set error stencil instead
				}
			}
			return true;
		}
	}

	public void showNextStencil() {
		if (currentStencilIndex < stencilList.size() - 1) {
			// tell current stencil that it's not current
			StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);

			boolean checkState = checkState(currentStencil);

			currentStencil.setCurrentStencil(false);
			currentStencilIndex++;

			// tell next stencil that it is current
			currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			currentStencil.setCurrentStencil(true);

			if (checkState == false) {
				// some kind of error has happened, handle it.
				// System.out.println("state match failed");
				currentStencil.setErrorStencil(true);
			}
			broadcastStencilNumberChange();

			stencilChanged = true;
			stencilApp.makeWayPoint();
		}
	}
	public void showPreviousStencil() {
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		if (currentStencil != null) {
			int stepsToGoBack = currentStencil.getStepsToGoBack();

			if (stepsToGoBack > 0) {
				for (int i = 0; i < stepsToGoBack; i++) {
					stencilApp.goToPreviousWayPoint();
				}

				if (currentStencilIndex > stepsToGoBack - 1) {
					currentStencil.setCurrentStencil(false);
					currentStencilIndex -= stepsToGoBack;
				}

				currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
				currentStencil.setCurrentStencil(true);
				broadcastStencilNumberChange();
				stencilChanged = true;
			}
		}
	}
	protected java.io.File getFile(String fileName) {
		// get a valid fileString to open
		int index = fileName.indexOf("/");
		int length = fileName.length();
		String fileString = null;
		if (index != -1) {
			fileString = fileName.substring(0, index) + java.io.File.separator + fileName.substring(index + 1, length);
		}

		// make a file
		java.io.File file = new java.io.File(fileString);

		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}
	public void showNextStack() {
		java.io.File nextFile = getFile(nextStack);
		if (nextFile != null) {
			loadStencilTutorial(nextFile);
		}
	}
	public void showPreviousStack() {
		java.io.File previousFile = getFile(previousStack);
		if (previousFile != null) {
			loadStencilTutorial(previousFile);
		}
	}
	public void insertNewStencil() {
		// tell current stencil that it's not current
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		boolean checkState = checkState(currentStencil);
		if (checkState == false) {
			// some kind of error has happened, handle it.
		}
		currentStencil.setCurrentStencil(false);
		currentStencilIndex++;

		// create the new stencil and hook it in
		currentStencil = new StencilManager.Stencil();
		stencilList.insertElementAt(currentStencil, currentStencilIndex);
		createDefaultStencilObjects();
		currentStencil.setCurrentStencil(true);

		broadcastStencilNumberChange();
		stencilChanged = true;
		stencilApp.makeWayPoint();
		triggerRefresh();
	}

	/* StencilClient stuff */
	@Override
	public boolean isDropAccessible(java.awt.Point p) {
		// COME BACK
		return false;
	}
	@Override
	public void update() {
		if (getIsShowing()) {
			// System.out.println("update");
			announceLayoutChange();
		}
	}
	// COME BACK TO ME!!!
	@Override
	public void stateChanged() {
	}
	@Override
	public Component getStencilComponent() {
		return stencilPanel;
	}
	@Override
	public void showStencils(boolean show) {

		stencilPanel.setIsDrawing(show); // TAKE THIS OUT WHEN DAVE CHANGES IT
		if (getIsShowing()) {
			stencilPanel.removeMouseListener(mouseAdapter);
			stencilPanel.removeMouseMotionListener(this);
			stencilPanel.addMouseListener(mouseAdapter);
			stencilPanel.addMouseMotionListener(this);
			if (writeEnabled) {
				addStencilStackChangeListener(this);
			}
			update();
		} else {
			// tell authoring tool we're done now, so it can reinstate warning
			// dialogs
			stencilApp.setVisible(false);
			stencilPanel.removeMouseListener(mouseAdapter);
			stencilPanel.removeMouseMotionListener(this);
			currentStencilIndex = 0;
			stencilList.removeAllElements();

			stencilList.addElement(this.newStencil());
		}
	}

	@Override
	public boolean getIsShowing() {
		return stencilPanel.getIsDrawing();
	}
	// COME BACK TO ME
	@Override
	public void loadStencilTutorial(java.io.File tutorialFile) {

		showStencils(true); // this will need to come out when Dave adds it

		stencilParser = new StencilParser(this, positionManager, stencilApp);
		worldToLoad = null;
		nextStack = null;
		previousStack = null;
		Vector newStencilList = stencilParser.parseFile(tutorialFile);

		// update the state of stencilmanager
		mouseEventListeners = new Vector();
		keyEventListeners = new Vector();
		stencilFocusListeners = new Vector();
		layoutChangeListeners = new Vector();
		stencilStackChangeListeners = new Vector();
		currentStencilIndex = 0;
		// focalObject = null;
		setNewFocalObject(null);
		stencilPanel.removeAllMessageListeners();

		addMouseEventListener(stencilPanel);
		stencilApp.deFocus();

		// tell everyone to update their current values
		stencilList = newStencilList;
		addLinks();
		StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
		currentStencil.setCurrentStencil(true);
		broadcastCurrentStencilChange();
		broadcastStencilNumberChange();
		stencilChanged = true;
		this.triggerRefresh();

		// showStencils(true);

		// start saving waypoints.
		stencilApp.clearWayPoints();
		stencilApp.makeWayPoint();

	}

	/* stencil stack change listener stuff */
	public void addStencilStackChangeListener(StencilStackChangeListener sscListener) {
		stencilStackChangeListeners.addElement(sscListener);
	}
	public void removeStencilStackChangeListener(StencilStackChangeListener sscListener) {
		stencilStackChangeListeners.remove(sscListener);
	}
	protected void broadcastStencilNumberChange() {
		for (int i = 0; i < stencilStackChangeListeners.size(); i++) {
			((StencilStackChangeListener) stencilStackChangeListeners.elementAt(i)).numberOfStencilsChanged(stencilList.size());
		}
	}
	protected void broadcastCurrentStencilChange() {
		for (int i = 0; i < stencilStackChangeListeners.size(); i++) {
			((StencilStackChangeListener) stencilStackChangeListeners.elementAt(i)).currentStencilChanged(currentStencilIndex);
		}
	}

	/* read write listener */
	public void addReadWriteListener(ReadWriteListener rwListener) {
		readWriteListeners.addElement(rwListener);
	}
	public void removeReadWriteListener(ReadWriteListener rwListener) {
		readWriteListeners.removeElement(rwListener);
	}

	public void setWriteEnabled(boolean enabled) {
		writeEnabled = enabled;
		// announce the change to all who care
		for (int i = 0; i < readWriteListeners.size(); i++) {
			ReadWriteListener rwL = (ReadWriteListener) readWriteListeners.elementAt(i);
			rwL.setWriteEnabled(writeEnabled);
		}
		this.triggerRefresh();
	}

	protected void loadWorld() {
		if (worldToLoad != null) {
			int index = worldToLoad.indexOf("/");
			int length = worldToLoad.length();
			String worldString = null;
			if (index != -1) {
				worldString = worldToLoad.substring(0, index) + java.io.File.separator + worldToLoad.substring(index + 1, length);
			}
			if (worldString != null) {
				stencilApp.performTask("loadWorld<" + worldString + ">");
			}
		}
	}

	public void setWorld(String worldToLoad) {
		this.worldToLoad = worldToLoad;
		loadWorld();
	}

	public void setNextAndPreviousStacks(String previousStack, String nextStack) {
		this.previousStack = previousStack;
		this.nextStack = nextStack;
	}

	protected void addLinks() {
		if (previousStack != null && stencilList.size() > 0) {
			Stencil stencil = (Stencil) stencilList.elementAt(0);
			stencil.addObject(new Link(this, positionManager, false));
		}

		if (nextStack != null && stencilList.size() > 0) {
			Stencil stencil = (Stencil) stencilList.elementAt(stencilList.size() - 1);
			stencil.addObject(new Link(this, positionManager, true));
		}
	}

	/* LayoutUpdateListener stuff */
	public void addLayoutChangeListener(LayoutChangeListener lcListener) {
		layoutChangeListeners.addElement(lcListener);
	}
	public void removeLayoutChangeListener(LayoutChangeListener lcListener) {
		layoutChangeListeners.remove(lcListener);
	}
	protected void announceLayoutChange() {
		boolean error = false;
		// System.out.println("LAYOUT CHANGE");
		for (int i = 0; i < layoutChangeListeners.size(); i++) {
			LayoutChangeListener lcListener = (LayoutChangeListener) layoutChangeListeners.elementAt(i);
			if (!(lcListener instanceof Note)) {
				error = !lcListener.layoutChanged();
			}
			if (error) {
				StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
				// System.out.println("something is missing");
				currentStencil.setErrorStencil(true);
			}
		}
		for (int i = 0; i < layoutChangeListeners.size(); i++) {
			LayoutChangeListener lcListener = (LayoutChangeListener) layoutChangeListeners.elementAt(i);
			if (lcListener instanceof Note) {
				lcListener.layoutChanged();
			}
			stencilChanged = true; // HACK
		}
		this.triggerRefresh();
	}

	/* StencilFocusListener stuff */
	public void addStencilFocusListener(StencilFocusListener sfListener) {
		stencilFocusListeners.addElement(sfListener);
	}

	public void removeStencilFocusListener(StencilFocusListener sfListener) {
		stencilFocusListeners.remove(sfListener);
	}

	/* MouseListener stuff */
	protected void setNewFocalListener(MouseEventListener meListener) {
		StencilObject newFocalObject = null;
		if (meListener instanceof StencilObject) {
			newFocalObject = (StencilObject) meListener;
		}
		// notify the past focal object and the new focal object if they are
		// registered stencilFocusListeners
		if (focalObject != newFocalObject) {
			if (focalObject != null && focalObject instanceof StencilFocusListener && stencilFocusListeners.contains(focalObject)) {
				((StencilFocusListener) focalObject).focusLost();
			}
			if (newFocalObject != null && newFocalObject instanceof StencilFocusListener && stencilFocusListeners.contains(focalObject)) {
				((StencilFocusListener) newFocalObject).focusGained();
			}
			if (focalObject instanceof Hole) {
				stencilApp.deFocus();
			}
			// focalObject = newFocalObject;
			setNewFocalObject(newFocalObject);
			// triggerRefresh();
		}
	}
	public void addMouseEventListener(MouseEventListener meListener) {
		// all the objects should have a chance at these events before
		// stencilPane
		if (meListener instanceof StencilPanel) {
			mouseEventListeners.addElement(meListener);
		} else {
			mouseEventListeners.insertElementAt(meListener, 0);
		}
	}

	public void removeMouseEventListener(MouseEventListener meListener) {
		mouseEventListeners.removeElement(meListener);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			MouseEventListener meListener = (MouseEventListener) mouseEventListeners.elementAt(i);
			if (meListener.contains(e.getPoint())) {
				setNewFocalListener(meListener);

				boolean refresh = meListener.mousePressed(e);
				if (refresh) {
					triggerRefresh(e.getWhen());
				}
				return;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (focalObject != null && focalObject instanceof MouseEventListener) {
			boolean refresh = ((MouseEventListener) focalObject).mouseReleased(e);
			if (refresh) {
				triggerRefresh(e.getWhen());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			MouseEventListener meListener = (MouseEventListener) mouseEventListeners.elementAt(i);
			if (meListener.contains(e.getPoint())) {
				setNewFocalListener(meListener);

				// send the new focal object the event to deal with
				boolean refresh = meListener.mouseClicked(e);
				if (refresh) {
					triggerRefresh(e.getWhen());
				}
				return;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// for (int i = 0; i < mouseEventListeners.size(); i++) {
		// MouseEventListener meListener = (MouseEventListener)
		// mouseEventListeners.elementAt(i);
		// if ( meListener.contains(e.getPoint()) ){
		// boolean refresh = meListener.mouseEntered(e);
		// if (refresh) triggerRefresh(e.getWhen());
		// return;
		// }
		// }
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// for (int i = 0; i < mouseEventListeners.size(); i++) {
		// MouseEventListener meListener = (MouseEventListener)
		// mouseEventListeners.elementAt(i);
		// if ( meListener.contains(e.getPoint()) ){
		// boolean refresh = meListener.mouseExited(e);
		// if (refresh) triggerRefresh(e.getWhen());
		// return;
		// }
		// }
	}

	/* Mouse MotionListener stuff */
	@Override
	public void mouseMoved(MouseEvent e) {
		for (int i = 0; i < mouseEventListeners.size(); i++) {
			MouseEventListener meListener = (MouseEventListener) mouseEventListeners.elementAt(i);
			if (meListener.contains(e.getPoint())) {
				boolean refresh = meListener.mouseMoved(e);
				if (refresh) {
					triggerRefresh(e.getWhen());
				}
				return;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (focalObject != null && focalObject instanceof MouseEventListener) {
			boolean refresh = ((MouseEventListener) focalObject).mouseDragged(e);
			if (refresh) {
				triggerRefresh(e.getWhen());
			}
		}
	}

	/* KeyListener stuff */
	public void addKeyEventListener(KeyEventListener keListener) {
		keyEventListeners.addElement(keListener);
	}
	public void removeKeyEventListener(KeyEventListener keListener) {
		keyEventListeners.remove(keListener);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		if (focalObject != null && focalObject instanceof KeyEventListener) {
			boolean refresh = ((KeyEventListener) focalObject).keyTyped(e);
			if (refresh) {
				triggerRefresh(e.getWhen());
			}
		}
	}

	// may want to check here for whether or not we've hit the enter key
	@Override
	public void keyPressed(KeyEvent e) {
		if (focalObject != null && focalObject instanceof KeyEventListener) {
			boolean refresh = ((KeyEventListener) focalObject).keyPressed(e);
			if (refresh) {
				triggerRefresh(e.getWhen());
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (focalObject != null && focalObject instanceof KeyEventListener) {
			boolean refresh = ((KeyEventListener) focalObject).keyReleased(e);
			if (refresh) {
				triggerRefresh(e.getWhen());
			}
		}
	}

	// THIS IS GOING TO BE THE PLACE FOR A DIALOG THAT GOES ALONGSIDE AUTHORING
	// MODE
	@Override
	public void numberOfStencilsChanged(int newNumberOfStencils) {
		if (writeEnabled) {
			StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			// System.out.println(" stencil number change: " + currentStencil);
		}
	}
	@Override
	public void currentStencilChanged(int selectedStencil) {
		if (writeEnabled) {
			StencilManager.Stencil currentStencil = (StencilManager.Stencil) stencilList.elementAt(currentStencilIndex);
			// System.out.println(" stencil change: " + currentStencil);
		}
	}

	protected void setNewFocalObject(StencilObject newFocalObject) {
		focalObject = newFocalObject;
		if (writeEnabled) {
			if (newFocalObject instanceof Note) {
				if (((Note) newFocalObject).scrObject != null) {
					// System.out.println( ((Note)focalObject).scrObject );
				} else {
					// System.out.println( focalObject );
				}
			}
		}
	}

	/* Stencil Class to keep track of the objects in each stencil */
	// COME BACK - make this a stand alone class
	public class Stencil {
		protected Vector stencilObjects = new Vector();
		protected StateCapsule endStateCapsule = null; // this should encode the
														// correct state for the
														// world at the *end* of
														// this step
		protected boolean error = false;
		protected int goBackSteps = 1;

		public void setSteps(int goBackSteps) {
			this.goBackSteps = goBackSteps;
		}

		public int getStepsToGoBack() {
			return goBackSteps;
		}

		public StateCapsule getEndState() {
			return endStateCapsule;
		}

		public void setEndState(StateCapsule stateCapsule) {
			endStateCapsule = stateCapsule;
		}

		public void write(Document document, Element element) {
			Element stencilElement = document.createElement("stencil");
			String stencilTitle = null;

			// figure out how to write out the statecapsule
			if (endStateCapsule != null) {
				Element stateNode = document.createElement("stateCapsule");
				CDATASection stateDataSection = document.createCDATASection(endStateCapsule.getStorableRepr());
				stateNode.appendChild(stateDataSection);
				stencilElement.appendChild(stateNode);
			}
			for (int i = 0; i < stencilObjects.size(); i++) {
				StencilObject stencilObj = (StencilObject) stencilObjects.elementAt(i);
				if (stencilObj instanceof Note) {
					((Note) stencilObj).write(document, stencilElement);
				} else if (stencilObj instanceof NavigationBar) {
					stencilTitle = ((NavigationBar) stencilObj).getTitleString();
				}
			}
			if (stencilTitle != null) {
				stencilElement.setAttribute("title", stencilTitle);
			}
			stencilElement.setAttribute("stepsToGoBack", Integer.toString(goBackSteps));
			element.appendChild(stencilElement);
		}

		public void setErrorStencil(boolean idError) {
			stencilChanged = true;
			error = idError;
			StencilManager.this.triggerRefresh(System.currentTimeMillis());
		}

		public void setCurrentStencil(boolean currentStencil) {
			if (currentStencil) {
				// this is just being shown, so no errors yet
				error = false;

				// add Everyone as listeners
				addAllListeners();
				// update the positions of objects
				for (int i = 0; i < stencilObjects.size(); i++) {
					StencilObject obj = (StencilObject) stencilObjects.elementAt(i);
					if (obj instanceof Hole) {
						boolean success = ((Hole) obj).layoutChanged();
						// if this comes back unsuccessful, then I need to check
						// again in a second
						WaitAndUpdateThread godot = new WaitAndUpdateThread((long) 1250, this, (LayoutChangeListener) obj);
						godot.start();
					} else if (obj instanceof Frame) {
						boolean success = ((Frame) obj).layoutChanged();

						// if this comes back unsuccessful, then I need to check
						// again in a second
						WaitAndUpdateThread godot = new WaitAndUpdateThread((long) 1250, this, (LayoutChangeListener) obj);
						godot.start();
					}
				}
				for (int i = 0; i < stencilObjects.size(); i++) {
					StencilObject obj = (StencilObject) stencilObjects.elementAt(i);
					// if (obj instanceof Note) (
					// (Note)obj).updatePosition(StencilManager.this.stencilApp);
					if (obj instanceof Note) {
						((Note) obj).updatePosition();
					}
				}
			} else {
				// remove everyone as listeners
				if (error && errorStencil != null) {
					errorStencil.removeAllListeners();
				}
				removeAllListeners();
			}
		}
		public void addObject(StencilObject stencilObject) {
			stencilObjects.addElement(stencilObject);
			if (stencilObject instanceof ReadWriteListener) {
				addReadWriteListener((ReadWriteListener) stencilObject);
				((ReadWriteListener) stencilObject).setWriteEnabled(writeEnabled);
			}
		}
		public void removeObject(StencilObject stencilObject) {
			stencilObjects.removeElement(stencilObject);
			if (stencilObject instanceof ReadWriteListener) {
				removeReadWriteListener((ReadWriteListener) stencilObject);
			}
		}
		protected void addAllListeners() {
			for (int i = 0; i < stencilObjects.size(); i++) {

				StencilObject obj = (StencilObject) stencilObjects.elementAt(i);
				if (obj instanceof MouseEventListener) {
					addMouseEventListener((MouseEventListener) obj);
				}
				if (obj instanceof KeyEventListener) {
					addKeyEventListener((KeyEventListener) obj);
				}
				if (obj instanceof StencilFocusListener) {
					addStencilFocusListener((StencilFocusListener) obj);
				}
				if (obj instanceof LayoutChangeListener) {
					addLayoutChangeListener((LayoutChangeListener) obj);
				}
				if (obj instanceof StencilStackChangeListener) {
					addStencilStackChangeListener((StencilStackChangeListener) obj);
				}
				if (obj instanceof StencilPanelMessageListener) {
					stencilPanel.addMessageListener((StencilPanelMessageListener) obj);
				}

			}
		}
		protected void removeAllListeners() {
			for (int i = 0; i < stencilObjects.size(); i++) {

				StencilObject obj = (StencilObject) stencilObjects.elementAt(i);
				if (obj instanceof MouseEventListener) {
					removeMouseEventListener((MouseEventListener) obj);
				}
				if (obj instanceof KeyEventListener) {
					removeKeyEventListener((KeyEventListener) obj);
				}
				if (obj instanceof StencilFocusListener) {
					removeStencilFocusListener((StencilFocusListener) obj);
				}
				if (obj instanceof LayoutChangeListener) {
					removeLayoutChangeListener((LayoutChangeListener) obj);
				}
				if (obj instanceof StencilStackChangeListener) {
					removeStencilStackChangeListener((StencilStackChangeListener) obj);
				}
				if (obj instanceof StencilPanelMessageListener) {
					stencilPanel.removeMessageListener((StencilPanelMessageListener) obj);
				}

			}
		}
		public void removeAllObjects() {
			Vector newStencilObjects = new Vector();
			for (int i = 0; i < stencilObjects.size(); i++) {
				if (stencilObjects.elementAt(i) instanceof Menu || stencilObjects.elementAt(i) instanceof NavigationBar || stencilObjects.elementAt(i) instanceof Link) {
					newStencilObjects.addElement(stencilObjects.elementAt(i));
				} else {
					StencilObject obj = (StencilObject) stencilObjects.elementAt(i);
					if (obj instanceof MouseEventListener) {
						removeMouseEventListener((MouseEventListener) obj);
					}
					if (obj instanceof KeyEventListener) {
						removeKeyEventListener((KeyEventListener) obj);
					}
					if (obj instanceof StencilFocusListener) {
						removeStencilFocusListener((StencilFocusListener) obj);
					}
					if (obj instanceof LayoutChangeListener) {
						removeLayoutChangeListener((LayoutChangeListener) obj);
					}
					if (obj instanceof StencilStackChangeListener) {
						removeStencilStackChangeListener((StencilStackChangeListener) obj);
					}
					if (obj instanceof StencilPanelMessageListener) {
						stencilPanel.removeAllMessageListeners();
					}
				}
			}
			stencilObjects = newStencilObjects;
		}
		public Vector getObjects() {
			if (error) {
				// next step is to return the error screen stencil objects.
				// System.out.println("error in getObjects");
				if (errorStencil == null) {
					errorStencil = stencilParser.getErrorStencil();
				}
				errorStencil.addAllListeners(); // make sure this works
				return errorStencil.getObjects();
			}
			return stencilObjects;
		}
	}

	protected class StencilFileFilter extends javax.swing.filechooser.FileFilter {

		@Override
		public boolean accept(File pathname) {
			if (pathname.getName().endsWith(".stl")) {
				return true;
			} else if (pathname.isDirectory()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getDescription() {
			return "Stencil Files";
		}
	}

	protected class StencilMouseAdapter extends CustomMouseAdapter {

		@Override
		protected void singleClickResponse(java.awt.event.MouseEvent ev) {
			StencilManager.this.mouseClicked(ev);
		}

		@Override
		protected void doubleClickResponse(java.awt.event.MouseEvent ev) {
			StencilManager.this.mouseClicked(ev);
		}

		@Override
		protected void mouseUpResponse(java.awt.event.MouseEvent ev) {
			StencilManager.this.mouseReleased(ev);
		}

		@Override
		protected void mouseDownResponse(java.awt.event.MouseEvent ev) {
			StencilManager.this.mousePressed(ev);
		}
	}
}