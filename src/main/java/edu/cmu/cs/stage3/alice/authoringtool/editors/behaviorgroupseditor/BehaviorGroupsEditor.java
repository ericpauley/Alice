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

package edu.cmu.cs.stage3.alice.authoringtool.editors.behaviorgroupseditor;

import java.awt.Color;

import javax.swing.ScrollPaneConstants;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author David Culyba
 * @version 1.0
 */

public class BehaviorGroupsEditor extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor, edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {

    //protected java.util.Vector behaviorGroups = new java.util.Vector();
    protected edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel m_containingPanel;
    protected BehaviorGroupEditor worldEditor;
    protected java.awt.GridBagLayout containingPanelLayout;
    protected javax.swing.JPanel m_header;
    protected javax.swing.JScrollPane scrollPane;
    protected java.awt.Component glue;
    protected final String BEHAVIOR_NAME = "Events";
    protected static final String DEFAULT_NAME = "event";
    protected static final int SPACE = 8;
    protected static final int OBJECT = 1;
    protected static final int BEHAVIOR = 2;
    protected static final int BAD = -1;
    protected int editorCount = 0;
    protected int counter = 0;
    protected final java.awt.Color BACKGROUND_COLOR = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("behaviorBackground");;
    protected edu.cmu.cs.stage3.alice.core.World world;
    protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
    protected javax.swing.JButton newBehaviorButton;
    protected javax.swing.JPopupMenu behaviorMenu;
    protected java.util.Vector behaviorRunnables = new java.util.Vector();
    protected javax.swing.JLabel menuLabel = new javax.swing.JLabel();
    protected java.util.Vector allSandboxes = new java.util.Vector();
    protected boolean paintDropPotential = false;
    protected boolean beingDroppedOn = false;



    protected java.awt.Color dndHighlightColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight" );
    protected java.awt.Color dndHighlightColor2 = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "dndHighlight2" );
    protected DropPotentialFeedbackListener dropPotentialFeedbackListener = new DropPotentialFeedbackListener();

    public final javax.swing.AbstractAction newBehaviorAction = new javax.swing.AbstractAction(){
        public void actionPerformed(java.awt.event.ActionEvent e){
            setRunnables(world, "");
            createNewBehavior(e);
        }
    };

    protected class DropPotentialFeedbackListener implements edu.cmu.cs.stage3.alice.authoringtool.util.event.DnDManagerListener {
        private void doCheck() {
            java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
            try{
                int type = checkTransferable( transferable );
                boolean transferableHasPotential = (type != BAD);
                if( BehaviorGroupsEditor.this.paintDropPotential != transferableHasPotential ) {
                    BehaviorGroupsEditor.this.paintDropPotential = transferableHasPotential;
                    BehaviorGroupsEditor.this.repaint();
                }
            }
            catch (Exception e){
            }
        }

        public void dragGestureRecognized( java.awt.dnd.DragGestureEvent dge ) {
            // do nothing for the gesture, wait until dragStarted
        }

        public void dragStarted() {
            doCheck();
        }

        public void dragEnter( java.awt.dnd.DragSourceDragEvent dsde ) {
            doCheck();
        }

        public void dragExit( java.awt.dnd.DragSourceEvent dse ) {
            doCheck();
        }

        public void dragOver( java.awt.dnd.DragSourceDragEvent dsde ) {
            //don't check here
        }

        public void dropActionChanged( java.awt.dnd.DragSourceDragEvent dsde ) {
            doCheck();
        }

        public void dragDropEnd( java.awt.dnd.DragSourceDropEvent dsde ) {
            BehaviorGroupsEditor.this.paintDropPotential = false;
            BehaviorGroupsEditor.this.beingDroppedOn = false;
            BehaviorGroupsEditor.this.repaint();
        }
    }

	public java.awt.Rectangle getScrollPaneVisibleRect(){
		return scrollPane.getVisibleRect();
	}
	
	public javax.swing.JComponent getScrollPane(){
			return scrollPane;
	}
	
	public javax.swing.JComponent getContainingPanel(){
		return m_containingPanel;
	}
	
    protected class BehaviorMenuItem extends javax.swing.JMenuItem{
        private edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel visibleComponent;
        private javax.swing.JPanel internalComponent;

        public BehaviorMenuItem(String s){
            super();
            visibleComponent = new edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel();
            internalComponent = new javax.swing.JPanel();
            visibleComponent.setLayout(new java.awt.GridBagLayout());
            BasicBehaviorPanel.buildLabel(internalComponent, s);
            internalComponent.setOpaque(false);
            visibleComponent.setOpaque(true);
            visibleComponent.add(internalComponent, new java.awt.GridBagConstraints(1,0,1,1,0,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0));
            visibleComponent.add(javax.swing.Box.createHorizontalGlue(), new java.awt.GridBagConstraints(2,0,1,1,1,1,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
            visibleComponent.setSize(visibleComponent.getPreferredSize());

            java.awt.Dimension dim = visibleComponent.getPreferredSize();
            internalComponent.doLayout();
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(dim.width, dim.height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            // visibleComponent.print(image.createGraphics());
            javax.swing.JLabel l = new javax.swing.JLabel("TEST");

            l.print(image.createGraphics());
            this.setIcon(new javax.swing.ImageIcon(image));
        }

        
		public void menuSelectionChanged(boolean isIncluded){
            super.menuSelectionChanged(isIncluded);
            if (isIncluded){
                internalComponent.setBackground(java.awt.Color.white);
            }
            else{
                internalComponent.setBackground(java.awt.Color.black);
            }
        }
    }

    public static void setName(edu.cmu.cs.stage3.alice.core.Behavior toSet, edu.cmu.cs.stage3.alice.core.Element parent){
        String newName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild(DEFAULT_NAME, parent);
        toSet.name.set(newName);
    }

    public BehaviorGroupsEditor(){
        authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
        behaviorMenu = new javax.swing.JPopupMenu();
        Class[] behaviors = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getBehaviorClasses();
        java.util.Vector structure = new java.util.Vector();
        edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.addListener( dropPotentialFeedbackListener );
        for( int i = 0; i < behaviors.length; i++ ) {
            Class currentBehavior = behaviors[i];
            String behaviorName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue(currentBehavior);
            if (behaviorName == null){
                behaviorName = "No Name";
            }
            CreateNewBehaviorRunnable runnable = new CreateNewBehaviorRunnable( currentBehavior, world );
            behaviorRunnables.add(runnable);
            structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( behaviorName,  runnable) );
        }
        if( structure.size() > 0 ) {
            behaviorMenu = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePopupMenu( structure );
        } else {
            behaviorMenu = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeDisabledPopup( "None Available" );
        }
        behaviorMenu.add(menuLabel, 0);
        initGUI();
        refreshGUI();
    }

    public BehaviorGroupsEditor(edu.cmu.cs.stage3.alice.core.World theWorld) {
        this();
        this.setObject(theWorld);
    }

    protected BehaviorGroupEditor getEditor(edu.cmu.cs.stage3.alice.core.Sandbox toCheck){
        for (int i=0; i<m_containingPanel.getComponentCount(); i++){
            if (m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor){
                BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
                if (bge.getElement() == toCheck){
                    return bge;
                }
            }
        }
        return null;
    }
    
    public java.util.Vector getEditors(){
		java.util.Vector toReturn = new java.util.Vector();
		for (int i=0; i<m_containingPanel.getComponentCount(); i++){
			if (m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor){
				BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
				java.util.Vector behaviorsComponents = bge.getBehaviorComponents();
				for (int j=0; j<behaviorsComponents.size(); j++){
					toReturn.add(behaviorsComponents.get(j));
				}
			}
		}
		return toReturn;
    }

    protected boolean checkGUI(){
        int total = 1;
        for (int i=0; i<world.sandboxes.size(); i++){
            if (((edu.cmu.cs.stage3.alice.core.Sandbox)world.sandboxes.get(i)).behaviors.size() > 0){
                total++;
            }
        }
        for (int i=0; i<world.groups.size(); i++){
            edu.cmu.cs.stage3.alice.core.Group currentGroup = (edu.cmu.cs.stage3.alice.core.Group)world.groups.get(i);
            for (int j=0; j<currentGroup.size(); j++){
                if ((edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j) instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
                    if (((edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j)).behaviors.size() > 0){
                        total++;
                    }
                }
            }
        }
        int count = 0;
        for (int i=0; i<m_containingPanel.getComponentCount(); i++){
            if (m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor){
                BehaviorGroupEditor bge = (BehaviorGroupEditor)m_containingPanel.getComponent(i);
                if (bge.getElement() == world){
                    count++;
                }
                else if (world.sandboxes.contains(bge.getElement())){
                    if (((edu.cmu.cs.stage3.alice.core.Sandbox)bge.getElement()).behaviors.size() <= 0){
                        return false;
                    }
                    else{
                        count++;
                    }
                }
                else{
                    boolean found = false;
                    for (int j=0; j<world.groups.size(); j++){
                        edu.cmu.cs.stage3.alice.core.Group currentGroup = (edu.cmu.cs.stage3.alice.core.Group)world.groups.get(j);
                        if (currentGroup.contains(bge.getElement())){
                            if (((edu.cmu.cs.stage3.alice.core.Sandbox)bge.getElement()).behaviors.size() <= 0){
                                return false;
                            }
                            else{
                                count++;
                                found = true;
                            }
                        }
                    }
                    if (!found){
                        return false;
                    }
                }
            }
        }
        if (count != total){
            return false;
        }
        return true;
    }

    protected void resetConstraints(){
        for (int i=0; i<m_containingPanel.getComponentCount(); i++){
            java.awt.Component c = m_containingPanel.getComponent(i);
            java.awt.GridBagConstraints constraints = containingPanelLayout.getConstraints(c);
            if (c != null){
                constraints.gridy = i;
                containingPanelLayout.setConstraints(c, constraints);
            }
        }
        m_containingPanel.revalidate();
        m_containingPanel.repaint();
    }

    public void editorRemoved(BehaviorGroupEditor toRemove){
        if (getEditor((edu.cmu.cs.stage3.alice.core.Sandbox)toRemove.getElement()) != null){
            editorCount--;
            if (editorCount == 1){
                worldEditor.removeLabel();
            }
            m_containingPanel.remove(toRemove);
            if (!checkGUI()){
                refreshGUI();
            }
            else{
                resetConstraints();
            }
        }
    }

    public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ){
    }

    public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
        if (ev.getItem() instanceof edu.cmu.cs.stage3.alice.core.Behavior){
            edu.cmu.cs.stage3.alice.core.Sandbox behaviorOwner = (edu.cmu.cs.stage3.alice.core.Sandbox)ev.getObjectArrayProperty().getOwner();
            if (behaviorOwner.behaviors.size() == 0){
                BehaviorGroupEditor toRemove = getEditor(behaviorOwner);
                if (toRemove != null){
                    editorRemoved(toRemove);
                }
            }
            else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED && behaviorOwner.behaviors.size() == 1){
                addEditor(behaviorOwner);
            }
        }
        else if (ev.getItem() instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
            edu.cmu.cs.stage3.alice.core.Sandbox child = (edu.cmu.cs.stage3.alice.core.Sandbox)ev.getItem();
            if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED){
                addEditor(child);
            }
            else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED){
                BehaviorGroupEditor toRemove = getEditor(child);
                if (toRemove != null){
                    editorRemoved(toRemove);
                }
                child.behaviors.removeObjectArrayPropertyListener(this);
            }
        }
        else if (ev.getItem() instanceof edu.cmu.cs.stage3.alice.core.Group){
            edu.cmu.cs.stage3.alice.core.Group child = (edu.cmu.cs.stage3.alice.core.Group)ev.getItem();
            if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED){
                child.values.addObjectArrayPropertyListener(this);
                for (int j=0; j<child.size(); j++){
                    if (child.getChildAt(j) instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
                        edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)child.getChildAt(j);
                        addEditor(current);
                    }
                }
            }
            else if (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_REMOVED){
                child.values.removeObjectArrayPropertyListener(this);
                for (int j=0; j<child.size(); j++){
                    if ((edu.cmu.cs.stage3.alice.core.Sandbox)child.getChildAt(j) instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
                        edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)child.getChildAt(j);
                        current.behaviors.removeObjectArrayPropertyListener(this);
                        BehaviorGroupEditor toRemove = getEditor(current);
                        if (toRemove != null){
                            editorRemoved(toRemove);
                        }
                    }
                }
            }
        }
    }

    protected void createNewBehavior(java.awt.event.ActionEvent e){
        behaviorMenu.show( newBehaviorButton, 0, newBehaviorButton.getHeight());
        edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen( behaviorMenu );
    }

    protected void addEditor(edu.cmu.cs.stage3.alice.core.Sandbox toAdd){
        edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty currentGroup = toAdd.behaviors;
        if (getEditor(toAdd) == null && currentGroup != null){
            toAdd.behaviors.addObjectArrayPropertyListener(this);
            if (currentGroup.size() > 0){
                editorCount++;
                if (editorCount > 1){
                    worldEditor.addLabel();
                }
                BehaviorGroupEditor editor = new BehaviorGroupEditor();
                editor.set(currentGroup.getOwner(), authoringTool);
                editor.setDropTarget( new java.awt.dnd.DropTarget( editor, editor ));
                m_containingPanel.remove(glue);
                m_containingPanel.add(editor, new java.awt.GridBagConstraints(0,m_containingPanel.getComponentCount(),1,1,0,0,java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(5,4,SPACE-5,2), 0,0));
                m_containingPanel.add(glue, new java.awt.GridBagConstraints(0,m_containingPanel.getComponentCount(),1,1,1,1,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
                if (!checkGUI()){
                    refreshGUI();
                }
                else{
                    resetConstraints();
                    this.revalidate();
                    this.repaint();
                }
            }
        }
    }

    class CreateNewBehaviorRunnable implements Runnable {
        Class behaviorClass;
        edu.cmu.cs.stage3.alice.core.Sandbox owner;

        public CreateNewBehaviorRunnable( Class behaviorClass, edu.cmu.cs.stage3.alice.core.Sandbox owner ) {
            this.behaviorClass = behaviorClass;
            this.owner = owner;
        }

        public void setOwner(edu.cmu.cs.stage3.alice.core.Sandbox owner){
            this.owner = owner;
        }

        public void run() {
            try {
				if (authoringTool != null){
					authoringTool.getUndoRedoStack().startCompound();
				}
                Object instance = behaviorClass.newInstance();
                if( instance instanceof edu.cmu.cs.stage3.alice.core.Behavior ) {
                    edu.cmu.cs.stage3.alice.core.Behavior behavior = (edu.cmu.cs.stage3.alice.core.Behavior)instance;
                    setName(behavior, owner);
                    behavior.setParent( owner );
                    owner.behaviors.add(0, behavior);
                    behavior.manufactureDetails();
                    if (behavior instanceof edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior){
						((edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior)behavior).subject.set(authoringTool.getCurrentCamera());
                    }
                }
				if (authoringTool != null){
					authoringTool.getUndoRedoStack().stopCompound();
				}
            } catch( Throwable t ) {
                edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Failed to create new event", t );
            }
        }
    }

    
	public void release(){
        super.release();
        if (world != null){
            world.sandboxes.removeObjectArrayPropertyListener( this );
            world.groups.removeObjectArrayPropertyListener( this );
            for (int i=0; i<world.sandboxes.size(); i++){
                edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)world.sandboxes.get(i);
                current.behaviors.removeObjectArrayPropertyListener(this);
            }
            for (int i=0; i<world.groups.size(); i++){
                edu.cmu.cs.stage3.alice.core.Group currentGroup = (edu.cmu.cs.stage3.alice.core.Group)world.groups.get(i);
                currentGroup.values.removeObjectArrayPropertyListener(this);
                for (int j=0; j<currentGroup.size(); j++){
                    if ((edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j) instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
                        edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j);
                        current.behaviors.removeObjectArrayPropertyListener(this);
                    }
                }
            }
        }
    }

    public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool){
        this.authoringTool = authoringTool;
        for (int i=0; i<m_containingPanel.getComponentCount(); i++){
            if (m_containingPanel.getComponent(i) instanceof BehaviorGroupEditor){
                ((BehaviorGroupEditor)m_containingPanel.getComponent(i)).setAuthoringTool(authoringTool);
            }
        }
    }

    protected void setRunnables(edu.cmu.cs.stage3.alice.core.Sandbox s, String label){
        for (int i=0; i<behaviorRunnables.size(); i++){
            ((CreateNewBehaviorRunnable)behaviorRunnables.get(i)).setOwner(s);
        }
        menuLabel.setText(label);
    }

    public void setObject(edu.cmu.cs.stage3.alice.core.World theWorld){
        release();
        world = theWorld;
        //newBehaviorDialog.set(theWorld);
        if (world != null){
            setRunnables(world, "");
            world.sandboxes.addObjectArrayPropertyListener(this);
            world.groups.addObjectArrayPropertyListener(this);
            for (int i=0; i<world.sandboxes.size(); i++){
                edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)world.sandboxes.get(i);
                current.behaviors.addObjectArrayPropertyListener(this);
            }
            for (int i=0; i<world.groups.size(); i++){
                edu.cmu.cs.stage3.alice.core.Group currentGroup = (edu.cmu.cs.stage3.alice.core.Group)world.groups.get(i);
                currentGroup.values.addObjectArrayPropertyListener(this);
                for (int j=0; j<currentGroup.size(); j++){
                    if ((edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j) instanceof edu.cmu.cs.stage3.alice.core.Sandbox){
                        edu.cmu.cs.stage3.alice.core.Sandbox current = (edu.cmu.cs.stage3.alice.core.Sandbox)currentGroup.getChildAt(j);
                        if (current.behaviors != null){
                            current.behaviors.addObjectArrayPropertyListener(this);
                        }
                    }
                }
            }
        }
        refreshGUI();
    }

    public Object getObject(){
        return world;
    }

    public javax.swing.JComponent getJComponent(){
        return this;
    }

    private void removeAllElements(javax.swing.JPanel container){
        for (int i=0; i<container.getComponentCount(); i++){
            if (container.getComponent(i) instanceof BehaviorGroupEditor){
                ((BehaviorGroupEditor)container.getComponent(i)).release();
            }
        }
        container.removeAll();
    }

    protected void initGUI(){
        this.setMinimumSize(new java.awt.Dimension(0,0));
        this.setLayout(new java.awt.BorderLayout());
        this.setBackground(BACKGROUND_COLOR);

        this.setBorder(null);
        // this.setDropTarget(new java.awt.dnd.DropTarget( this, this));
        scrollPane = new javax.swing.JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        this.add(scrollPane , java.awt.BorderLayout.CENTER);

        m_containingPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel(){
            
			public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
                BehaviorGroupsEditor.this.dragEnter(dtde);
            }
            
			public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
                BehaviorGroupsEditor.this.dragExit(dte);
            }

            
			public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
                BehaviorGroupsEditor.this.dragOver(dtde);
            }

            
			public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
                BehaviorGroupsEditor.this.drop(dtde);
            }

            
			public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
                BehaviorGroupsEditor.this.dropActionChanged(dtde);
            }
        };
        String toolTipText = "<html><body>"+
                             "<p>Events</p>"+
                             "<p>Events run Methods when certain things happen</p>"+
                             "<p>(like when the mouse is clicked on an Object or</p>"+
                             "<p>when a certain key is pressed)</p></body></html>";
        containingPanelLayout = new java.awt.GridBagLayout();
        m_containingPanel.setLayout(containingPanelLayout);
        m_containingPanel.setBorder(null);
        m_containingPanel.setBackground(BACKGROUND_COLOR);
        m_containingPanel.setToolTipText(toolTipText);
        scrollPane.setViewportView(m_containingPanel);

        newBehaviorButton = new javax.swing.JButton("create new event");
        newBehaviorButton.setToolTipText("Display Menu of New Event Types");
        newBehaviorButton.setMargin(new java.awt.Insets(2,2,2,2));
        newBehaviorButton.addActionListener(newBehaviorAction);
        newBehaviorButton.setBackground(new java.awt.Color( 240, 240, 255 ));
        newBehaviorButton.setDropTarget(new java.awt.dnd.DropTarget( newBehaviorButton, this));

        m_header = new javax.swing.JPanel();
        m_header.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,2,2));
        m_header.setBackground(BACKGROUND_COLOR);
        m_header.setBorder(javax.swing.BorderFactory.createMatteBorder(0,0,1,0,java.awt.Color.gray));
        m_header.setToolTipText(toolTipText);
        javax.swing.JLabel behaviorLabel = new javax.swing.JLabel(BEHAVIOR_NAME);
        int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
        java.awt.Font behaviorFont = new java.awt.Font("Helvetica", java.awt.Font.BOLD, (int)(16*fontSize/12.0));
        behaviorLabel.setFont(behaviorFont);
        behaviorLabel.setDropTarget(new java.awt.dnd.DropTarget( behaviorLabel, this));
        m_header.add(behaviorLabel);
        m_header.add(javax.swing.Box.createHorizontalStrut(4));
        m_header.add(newBehaviorButton);
        m_header.setDropTarget(new java.awt.dnd.DropTarget( m_header, this));
        this.add(m_header, java.awt.BorderLayout.NORTH);

        glue = javax.swing.Box.createVerticalGlue();
        this.setToolTipText(toolTipText);
    }

    protected synchronized void refreshGUI(){
        if (world != null){
            this.removeAll();
            this.add(m_header, java.awt.BorderLayout.NORTH);
            this.add(scrollPane , java.awt.BorderLayout.CENTER);
            removeAllElements(m_containingPanel);
            int count = 0;
            worldEditor = new BehaviorGroupEditor();
            worldEditor.set(world, authoringTool);
            worldEditor.setEmptyString(" No events");
            m_containingPanel.add(worldEditor, new java.awt.GridBagConstraints(0,count,1,1,1,0,java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(5,4,SPACE-5,2), 0,0));
            BehaviorGroupEditor editor = null;
            for (int i=0; i<world.sandboxes.size(); i++){
                edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty currentGroup = ((edu.cmu.cs.stage3.alice.core.Sandbox)world.sandboxes.get(i)).behaviors;
                if (currentGroup != null){
                    if (currentGroup.size() > 0){
                        count++;
                        editor = new BehaviorGroupEditor();
                        editor.set(currentGroup.getOwner(), authoringTool);
             //           editor.setDropTarget( new java.awt.dnd.DropTarget( editor, editor ));
                        m_containingPanel.add(editor, new java.awt.GridBagConstraints(0,count,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets(5,4,SPACE-5,2), 0,0));

                    }
                }
            }
            if (count == 0){
               worldEditor.removeLabel();
            }
            m_containingPanel.add(glue, new java.awt.GridBagConstraints(0,count+1,1,1,1,1,java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.BOTH, new java.awt.Insets(0,0,0,0), 0,0));
            editorCount = count+1;
        }
        this.revalidate();
        this.repaint();
        //printComponents(this);
    }

    protected void printComponents(java.awt.Container c){
        System.out.println(c+"n\n");
        for (int i=0; i<c.getComponentCount(); i++){
            if (c.getComponent(i) instanceof java.awt.Container){
                printComponents((java.awt.Container)c.getComponent(i));
            }
            else{
                System.out.println(c.getComponent(i));
            }
        }
    }

    protected static int checkDragEvent( java.awt.dnd.DropTargetDragEvent dtde ) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException{
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor) ) {
            java.awt.datatransfer.Transferable transferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
            edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor );
            Class valueClass = copyFactory.getValueClass();
            if (edu.cmu.cs.stage3.alice.core.Behavior.class.isAssignableFrom(valueClass)){
                return BEHAVIOR;
            }
            else{
                return BAD;
            }
        }
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor ) ){
            return OBJECT;
        }
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.behaviorReferenceFlavor ) ){
            return BEHAVIOR;
        }
        return BAD;
    }

    protected static int checkTransferable( java.awt.datatransfer.Transferable transferable ) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException{
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor) ) {
            edu.cmu.cs.stage3.alice.core.CopyFactory copyFactory = (edu.cmu.cs.stage3.alice.core.CopyFactory)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CopyFactoryTransferable.copyFactoryFlavor );
            Class valueClass = copyFactory.getValueClass();
            if (edu.cmu.cs.stage3.alice.core.Behavior.class.isAssignableFrom(valueClass)){
                return BEHAVIOR;
            }
            else{
                return BAD;
            }
        }
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor ) ){
            return OBJECT;
        }
        if (edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.behaviorReferenceFlavor ) ){
            return BEHAVIOR;
        }
        return BAD;
    }

    protected java.awt.Component getTopComponent(java.awt.Component c){
        if (c instanceof BehaviorGroupEditor || c == this || c == m_containingPanel || c == null){
            return c;
        }
        else {
            return getTopComponent(c.getParent());
        }
    }

    private java.awt.Component getPrimaryComponent(java.awt.Component c){
        if (c == null){
            return c;
        }
        if (c.getParent() == m_containingPanel){
            return c;
        }
        return getPrimaryComponent(c.getParent());
    }

    protected BehaviorGroupEditor getEditor(java.awt.Point p){
        int numSpots = editorCount*2;
        int[] spots = new int[numSpots];
        for( int i = 0; i < editorCount; i++ ) {
            java.awt.Component c = m_containingPanel.getComponent( i );
            spots[i*2] = c.getBounds().y; //top
            spots[i*2+1] = c.getBounds().y + c.getBounds().height; // bottom
        }
        int closestSpot = -1;
        int minDist = Integer.MAX_VALUE;
        for( int i = 0; i < numSpots; i++ ) {
            int d = Math.abs( p.y - spots[i]);
            if( d < minDist ) {
                minDist = d;
                closestSpot = i;
            }
        }
        return (BehaviorGroupEditor)m_containingPanel.getComponent( (closestSpot/2) );
    }

    protected void paintLineInEditor(java.awt.dnd.DropTargetDragEvent dtde){
        BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
        //java.awt.Point p2 = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), currentEditor);
        currentEditor.dragOver(dtde);
    }



    ///////////////////////////////////////////////
    // DropTargetListener interface
    ///////////////////////////////////////////////

    
	public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
        try{
            int type = checkDragEvent(dtde);
            if (type != BAD && type != BEHAVIOR){
                dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
                counter++;
                beingDroppedOn = true;
                repaint();
            }
            else{
                beingDroppedOn = false;
                repaint();
                dtde.rejectDrag();
            }
        }
        catch (Exception e){
            dtde.rejectDrag();
            beingDroppedOn = false;
            repaint();
        }
    }

    
	public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
        if( beingDroppedOn ) {
            beingDroppedOn = false;
            repaint();
        } else {
            super.dragExit( dte );
        }
    }

    
	public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
        try{
            int type = checkDragEvent(dtde);
            if ( type == BAD){
                dtde.rejectDrag();
                if (beingDroppedOn){
                    beingDroppedOn = false;
                    repaint();
                }
            }
            else if ( type == OBJECT ) {
                dtde.acceptDrag(java.awt.dnd.DnDConstants.ACTION_MOVE);
                if (!beingDroppedOn){
                    beingDroppedOn = true;
                    repaint();
                }
            }else if (type == BEHAVIOR){
                java.awt.Component primary = getPrimaryComponent(dtde.getDropTargetContext().getComponent());
                if (!(primary instanceof BehaviorGroupEditor)){
                    BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
                    if (currentEditor != null){
                        currentEditor.componentElementPanel.dragOver(dtde);
                    }
                }
            }
        }
        catch (Exception e){

            dtde.rejectDrag();
            beingDroppedOn = false;
            repaint();
        }
    }

    
	public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
        java.awt.datatransfer.Transferable transferable = dtde.getTransferable();
        boolean dropSuccess = true;
        try{
            int type = checkTransferable(transferable);
            if( type == BAD) {
                dtde.rejectDrop();
                dropSuccess = false;
            }
            else if (type == OBJECT) {
                dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_MOVE);
                edu.cmu.cs.stage3.alice.core.Transformable droppedElement = (edu.cmu.cs.stage3.alice.core.Transformable)dtde.getTransferable().getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.transformableReferenceFlavor );
                setRunnables(droppedElement, "New "+droppedElement.name.get()+" event");
                behaviorMenu.show( dtde.getDropTargetContext().getComponent(), (int)dtde.getLocation().getX(), (int)dtde.getLocation().getY() );
                edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.ensurePopupIsOnScreen( behaviorMenu );
            }
            else if (type == BEHAVIOR){
                java.awt.Component primary = getPrimaryComponent(dtde.getDropTargetContext().getComponent());
                if (!(primary instanceof BehaviorGroupEditor)){
                    BehaviorGroupEditor currentEditor = getEditor(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), m_containingPanel));
                    if (currentEditor != null){
                        currentEditor.componentElementPanel.drop(dtde);
                    }
                }
            }
        }
        catch (Exception e){
            edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "The drop failed.", e);
            dropSuccess = false;
        }
        beingDroppedOn = false;
        paintDropPotential = false;
        repaint();
        dtde.dropComplete(dropSuccess);
    }

    
	public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
    }

    
	public void paintForeground( java.awt.Graphics g ) {
        super.paintForeground( g );
        java.awt.Point p = javax.swing.SwingUtilities.convertPoint(m_containingPanel, m_containingPanel.getLocation(), this);
        if( beingDroppedOn ) {
            java.awt.Dimension size = m_containingPanel.getSize();
            g.setColor( dndHighlightColor2 );
            g.drawRect( p.x, p.y, size.width - 1, size.height - 1 );
            g.drawRect( p.x+1, p.y+1, size.width - 3, size.height - 3 );
        } else if( paintDropPotential ) {
            java.awt.Dimension size = m_containingPanel.getSize();
            g.setColor( dndHighlightColor );
            g.drawRect( p.x, p.y, size.width - 1, size.height - 1 );
            g.drawRect( p.x+1, p.y+1, size.width - 3, size.height - 3 );
        }
    }


    public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    }
    public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    }

    public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    }
    public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
        refreshGUI();
    }
    public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    }
    public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    }
    public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
        refreshGUI();
    }


}