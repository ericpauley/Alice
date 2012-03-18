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

package edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor;

import edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public abstract class CompositeComponentElementPanel extends edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener,  javax.swing.event.PopupMenuListener{

    protected final boolean USE_DEPTH = false;

    protected static java.util.Vector timers = new java.util.Vector();
    protected static boolean shouldReact = true;

    public static final int LEFT_INDENT = 15;
    public static final int RIGHT_INDENT = 5;
    public static final int STRUT_SIZE = 8;
    public static final int SCROLL_SIZE = 8;
    public static int SCROLL_AMOUNT = 3;
    public static final int SCROLL_DELTA = 1;
    public static final int SCROLL_START = 3;
    public static final int MAX_SCROLL = 10;
    public edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty componentElements;
    public CompositeComponentOwner m_owner;

    protected boolean HACK_started = false;

    protected javax.swing.JScrollPane topLevelScrollPane;
    protected boolean inserting = false;
    protected boolean insertingElement = false;
    private boolean invalidEvent = false;
    protected InsertPanel insertPanel;
    protected java.awt.GridBagLayout panelLayout;
    protected java.awt.GridBagConstraints panelConstraints;
    protected java.awt.Component glue;
    protected java.awt.Component strut;
    protected java.awt.Insets insets;
    protected int dropPanelPosition = -2;

    protected static java.awt.Color dndFeedBackColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor("dndHighlight2");

    protected static CompositeComponentElementPanel s_currentComponentPanel;
    protected static java.awt.Component s_componentPanelMoved;
    protected int dropPanelLocation = -2;  //-2 means it isn't anywhere
    protected int m_depth = -1;
    protected int lineLocation = -1;
    protected int oldLineLocation = -1;
    protected int insertLocation = -1;
    protected static boolean showDropPanel = false;
    protected boolean shouldDrawLine = false;
    protected java.awt.event.ContainerAdapter containerAdapter;

    //HACK: is there a better way to do this?
    protected javax.swing.Timer HACK_timer;
    protected javax.swing.Timer lingerTimer;
    protected javax.swing.Timer insertTimer;

    protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

    public CompositeComponentElementPanel(){
        insets = new java.awt.Insets(3,2,0,2);
        panelLayout = new java.awt.GridBagLayout();
        panelConstraints = new java.awt.GridBagConstraints(0,0,1,1,1,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.HORIZONTAL, insets, 0,0 );
        this.setOpaque(true);
        this.setLayout(panelLayout);
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,LEFT_INDENT,0,RIGHT_INDENT));
        glue = javax.swing.Box.createVerticalGlue();
        strut = javax.swing.Box.createVerticalStrut(STRUT_SIZE);
        insertPanel = new InsertPanel();
        HACK_timer = new javax.swing.Timer(200, new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent e){
                removeDropPanelFromCurrentComponent();
            }
        });
        HACK_timer.setRepeats(false);
        s_currentComponentPanel = this;
    }

    protected boolean isInverted(){
        return false;
    }


    protected void startListening() {
        if (componentElements != null){
            componentElements.addObjectArrayPropertyListener(this);
        }
    }

    protected void stopListening() {
        if (componentElements != null){
            componentElements.removeObjectArrayPropertyListener(this);
        }
    }

    protected void setTopLevelScrollPanel(){
        java.awt.Container currentOwner = (java.awt.Container)m_owner;
        while (!(currentOwner instanceof MainCompositeElementPanel) && currentOwner != null){
            currentOwner = currentOwner.getParent();
        }
        if (currentOwner != null){
            topLevelScrollPane = ((MainCompositeElementPanel)currentOwner).scrollPane;
        }
    }

    public void set(edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty elements, CompositeComponentOwner owner, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
        clean();
        authoringTool = authoringToolIn;
        componentElements = elements;
        clean();
        m_owner = owner;
        updateGUI();
        startListening();
    }

    public void setAuthoringTool(edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringToolIn) {
        authoringTool = authoringToolIn;
    }

    public void setStrut(int size){
        strut = javax.swing.Box.createVerticalStrut(size);
        clean();
        updateGUI();
        wakeUp();
    }

    public void goToSleep() {
        stopListening();
        HACK_timer.stop();
    }

    public void wakeUp() {
        startListening();
    }

    public void clean() {
        goToSleep();
    }

    protected void removeAllListening(){
    }

    public void die() {
        clean();
        removeAll();
        this.removeContainerListener(containerAdapter);
    }

    public CompositeComponentOwner getOwner(){
        return m_owner;
    }

    public edu.cmu.cs.stage3.alice.core.Element getElement(){
        return componentElements.getOwner();
    }

    protected static void stopAllTimers(){
        for (int i=0; i<timers.size(); i++){
            javax.swing.Timer t = (javax.swing.Timer)timers.elementAt(i);
            t.stop();
        }
        timers.removeAllElements();
    }

    protected abstract java.awt.Component makeGUI(edu.cmu.cs.stage3.alice.core.Element currentElement);

    protected void updateGUI(){
        if (componentElements != null){
            if (componentElements.size() > 0){
                this.removeAll();
                resetGUI();
                for (int i=0; i<componentElements.size(); i++){
                    edu.cmu.cs.stage3.alice.core.Element currentElement = (edu.cmu.cs.stage3.alice.core.Element)componentElements.getArrayValue()[i];
                    java.awt.Component toAdd = makeGUI(currentElement);
                    if (toAdd != null){
                        addElementPanel(toAdd, i);
                    }
                }
            }
            else{
                addDropTrough();
            }
        }
        else{
            if (m_owner != null){
                m_owner.setEnabled(false);
            }
        }
        this.revalidate();
        this.repaint();
    }

    protected void resetGUI(){
        if (this.getComponentCount() < 2){
            this.removeAll();
            this.add(strut, new java.awt.GridBagConstraints(0,1,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0 ));
            this.add(glue, new java.awt.GridBagConstraints(0,2,1,1,1,1,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0 ));
        }
    }

    protected void addDropTrough(){
        if (componentElements != null && componentsIsEmpty()){
            this.removeAll();
            panelConstraints.gridy = 0;
            this.add(insertPanel,panelConstraints, 0);
            this.add(strut, new java.awt.GridBagConstraints(0,1,1,1,0,0,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0 ));
            this.add(glue, new java.awt.GridBagConstraints(0,2,1,1,1,1,java.awt.GridBagConstraints.NORTHWEST,java.awt.GridBagConstraints.NONE, new java.awt.Insets(0,0,0,0), 0,0 ));
        }
    }

    public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent propertyEvent ) {
    	invalidEvent = false;
    	if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_INSERTED){
            if (componentElements.get(propertyEvent.getNewIndex()) == propertyEvent.getItem()){
                invalidEvent = true;
            }
        }
        if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_SHIFTED){
            if (componentElements.get(propertyEvent.getNewIndex()) == propertyEvent.getItem()){
                invalidEvent = true;
            }
            if (componentElements.get(propertyEvent.getOldIndex()) != propertyEvent.getItem()){
                invalidEvent = true;
            }
        }
        if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_REMOVED){
            if (componentElements.get(propertyEvent.getOldIndex()) != propertyEvent.getItem()){
                invalidEvent = true;
            }
        }

    }

    protected int getElementComponentCount(){
        int count = 0;
        for (int i=0; i< this.getComponentCount(); i++){
            if (this.getComponent(i) instanceof CompositeElementPanel || this.getComponent(i) instanceof ComponentElementPanel){
                count++;
            }
        }
        return count;
    }

    protected boolean checkGUI(){
        java.awt.Component c[] = this.getComponents();
        edu.cmu.cs.stage3.alice.core.Element elements[] = (edu.cmu.cs.stage3.alice.core.Element[])componentElements.get();
        int elementCount = getElementComponentCount();
        boolean aOkay = (elements.length == elementCount);
        if (aOkay){
            //Loops through the components and makes sure that component[i] == componentElement[i]
            for (int i=0; i<elements.length; i++){
                if (c[i] instanceof CompositeElementPanel){
                    if (i < elements.length){
                        if (((CompositeElementPanel)c[i]).getElement() != elements[i]){
                            aOkay = false;
                            break;
                        }
                    }
                    else{
                        aOkay = false;
                        break;
                    }
                }
                if (c[i] instanceof ComponentElementPanel){
                    if (i < elements.length){
                        if (((ComponentElementPanel)c[i]).getElement() != elements[i]){
                            aOkay = false;
                            break;
                        }
                    }
                    else{
                        aOkay = false;
                        break;
                    }
                }
            }
        }
        return aOkay;
    }

    public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent propertyEvent ) {
        if (shouldReact && !invalidEvent){
            boolean successful = true;
            edu.cmu.cs.stage3.alice.core.Element eventElement = (edu.cmu.cs.stage3.alice.core.Element)propertyEvent.getItem();
            int index = propertyEvent.getNewIndex();
            if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_INSERTED){
                java.awt.Component toInsert = s_componentPanelMoved;
                s_componentPanelMoved = null;
                boolean isCorrectPanel = false;
                if (toInsert == null){
                    toInsert = makeGUI(eventElement);
                    isCorrectPanel = true;
                }
                if (toInsert instanceof CompositeElementPanel && !isCorrectPanel){
                    if (((CompositeElementPanel)toInsert).getElement() == eventElement){
                        isCorrectPanel = true;
                    }
                }
                else if (toInsert instanceof ComponentElementPanel && !isCorrectPanel){
                    if (((ComponentElementPanel)toInsert).getElement() == eventElement){
                        isCorrectPanel = true;
                    }
                }
                if (!isCorrectPanel){
                    toInsert = makeGUI(eventElement);
                }
                if (toInsert != null){
                    addElementPanel(toInsert, index);
                }
                else{
                    successful = false;
                }
            }
            if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_REMOVED){
                s_componentPanelMoved = getComponent(eventElement);
                if (s_componentPanelMoved != null){
                    this.removeContainerListener( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener() ); // prevent the object being removed from being cleaned
                    this.remove(s_componentPanelMoved);
                    this.addContainerListener( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener() );
                }
                else{
                    successful = false;
                }
            }
            if (propertyEvent.getChangeType() == ObjectArrayPropertyEvent.ITEM_SHIFTED){
                s_componentPanelMoved = null;
                java.awt.Component c = getComponent(eventElement);
                if (c != null){
                    this.removeContainerListener( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener() ); // prevent the object being removed from being cleaned
                    this.remove(c);
                    addElementPanel(c, index);
                    this.addContainerListener( edu.cmu.cs.stage3.alice.authoringtool.util.GUIElementContainerListener.getStaticListener() ); // prevent the object being removed from being cleaned
                }
                else{
                    successful = false;
                }
            }
            successful = (successful && checkGUI());
            if (successful){
                this.revalidate();
                this.repaint();
            }
            else{
                clean();
                updateGUI();
                wakeUp();
            }
        }
    }


    public void addElementPanel(java.awt.Component toAdd, int position){
        if (this.getComponentCount()!= 0 && this.getComponent(0) == insertPanel){
            this.remove(insertPanel);
        }
        if (position > this.getElementComponentCount() || position < 0){
            position = this.getElementComponentCount();
            panelConstraints.gridy = position;
        }
        this.add(toAdd,panelConstraints, position);
        restoreLayout();
        this.doLayout();
        if (toAdd instanceof javax.swing.JComponent){
        	if (topLevelScrollPane != null){
	        	java.awt.Rectangle rect = toAdd.getBounds();
	        	int horizValue = topLevelScrollPane.getHorizontalScrollBar().getValue();
	            ((javax.swing.JComponent)toAdd).scrollRectToVisible(rect);
	            topLevelScrollPane.getHorizontalScrollBar().setValue(horizValue);
        	}
        	else{
        		((javax.swing.JComponent)toAdd).scrollRectToVisible(toAdd.getBounds());
        	}
        }
        this.repaint();
        this.revalidate();
    }

    protected java.awt.Component getComponent(edu.cmu.cs.stage3.alice.core.Element element){
        for (int i=0; i<this.getComponentCount(); i++){
            if (this.getComponent(i) instanceof ComponentElementPanel){
                ComponentElementPanel c = (ComponentElementPanel)this.getComponent(i);
                if (c.getElement() == element){
                    return c;
                }
            }
            else if (this.getComponent(i) instanceof CompositeElementPanel){
                CompositeElementPanel c = (CompositeElementPanel)this.getComponent(i);
                if (c.getElement() == element){
                    return c;
                }
            }
        }
        return null;
    }

    protected void bumpDown(int position){
        for (int i=position; i<this.getComponentCount(); i++){
            panelConstraints.gridy = i+1;
            panelLayout.setConstraints(this.getComponent(i), panelConstraints);
        }
    }

    
	public void setBackground(java.awt.Color color){
        super.setBackground(color);
        if (insertPanel != null){
            insertPanel.setBackground(color);
        }
    }

    protected void restoreLayout(){
        for (int i=0; i<this.getComponentCount(); i++){
            java.awt.Component current = this.getComponent(i);
            java.awt.GridBagConstraints c = panelLayout.getConstraints(current);
            if (c.gridy != i){
                c.gridy = i;
                panelLayout.setConstraints(current, c);
            }
        }
    }

    
	public void remove(java.awt.Component c){
        super.remove(c);
        if (c instanceof CompositeElementPanel || c instanceof ComponentElementPanel){
            if (componentElements != null && componentsIsEmpty()){
                addDropTrough();
                return;
            }
        }
        restoreLayout();
    }

    public void removeDropPanel(){
        if (this.componentsIsEmpty()){
            this.insertPanel.setHighlight(false);
            insertPanel.setHighlight(false);
        }
    }

    public java.awt.Point convertToDropPanelSpace(java.awt.Point p){
        if (insertPanel.isShowing()){
            return javax.swing.SwingUtilities.convertPoint(this, p, insertPanel);
        }
        else{
            return null;
        }
    }

    public InsertPanel getDropPanel(){
        return insertPanel;
    }

    public void setBorder(int top, int left, int bottom, int right){
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    public void setEmptyString(String emptyString){
        insertPanel.m_doNothingLabel = emptyString;
        insertPanel.m_label.setText(insertPanel.m_doNothingLabel);

    }


/////////////////////////////////// drop handling

    
	public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
        if (!m_owner.isExpanded()){
            if (m_owner.getParent() instanceof CompositeComponentElementPanel){
                ((CompositeComponentElementPanel)m_owner.getParent()).dragEnter(dtde);
                return;
            }
        }
        stopAllTimers(); //TIMER HACK
		edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.variableReferenceFlavor);
        dtde.acceptDrag(dtde.getDropAction());
    }

    
	public abstract void dragOver( java.awt.dnd.DropTargetDragEvent dtde );

    //We're passed in a non-CompositeComponentElementPanel component
    protected java.awt.Component getContainingComponentPanel(java.awt.Component c){
        if (c == m_owner.getGrip()){
            return c;
        }
        if (c == null){
            return null;
        }
        if (c.getParent() instanceof CompositeComponentElementPanel){
            return c;
        }
        if (c.getParent() == null){
            return null;
        }
        return getContainingComponentPanel(c.getParent());
    }

    protected int getElementCount(){
        return componentElements.size();
    }

    public edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty getComponentProperty(){
        return componentElements;
    }

    protected boolean checkDropLocation(int i, edu.cmu.cs.stage3.alice.core.Element toDrop){
        return true;  //need to get this working to check valid drop spaces that are on same level
    }

    protected int getInsertLocation(java.awt.Point panelSpacePoint){
        int position = -1;
        for (int i=0; i<componentElements.size(); i++){ //calculate
            java.awt.Component c = this.getComponent(i);
            int point = c.getY() + (int)(c.getHeight()/2.0);
            if (panelSpacePoint.getY() < point){
                position = i;
                break;
            }
        }
        return position;
    }


    private static java.awt.Container s_prevContainer;
    private static java.awt.Rectangle s_prevRect;
    private static void drawRect( java.awt.Container container, java.awt.Color color, java.awt.Rectangle rect ) {
        java.awt.Graphics g = container.getGraphics();
        if( g != null ) {
            if( color != null ) {
                g.setColor( color );
                g.fillRect( rect.x, rect.y, rect.width, rect.height );
                g.dispose();
            } 
        } 
    }

    private static void drawFeedback( java.awt.Container container, java.awt.Rectangle rect ) {
        if( container != null && container.equals( s_prevContainer ) ) {
            if( rect != null && rect.equals( s_prevRect ) ) {
                drawRect( container, dndFeedBackColor, rect );
                return;
            }
        }
        if( s_prevContainer != null ) {
            drawRect( s_prevContainer, s_prevContainer.getBackground(), s_prevRect );
            if (s_prevContainer instanceof CompositeComponentElementPanel){
                ((CompositeComponentElementPanel)s_prevContainer).shouldDrawLine = false;
            }
        }
        if( container != null ) {
            drawRect( container, dndFeedBackColor, rect );
            if (container instanceof CompositeComponentElementPanel){
                ((CompositeComponentElementPanel)container).shouldDrawLine = true;
            }
        }
        s_prevContainer = container;
        s_prevRect = rect;
    }

    protected void paintLine(java.awt.Container toPaint, int location){
        if (location == -1 || location >= toPaint.getComponentCount()){
            int lastSpot = toPaint.getComponentCount() - 1;
            for (int i = lastSpot; i>=0; i--){
                if (toPaint.getComponent(i) instanceof edu.cmu.cs.stage3.alice.authoringtool.util.GroupingPanel){
                    lastSpot = i;
                    break;
                }
                lastSpot = i;
            }
            if (lastSpot < 0){
                lastSpot = 0;
            }
            java.awt.Rectangle bounds = toPaint.getComponent(lastSpot).getBounds();
            lineLocation = bounds.y + bounds.height;
        }
        else{
            java.awt.Rectangle bounds = toPaint.getComponent(location).getBounds();
            lineLocation = bounds.y -2;
        }
        java.awt.Rectangle toPaintBounds = toPaint.getBounds();
        drawFeedback( toPaint, new java.awt.Rectangle( 2, lineLocation, toPaintBounds.width-4, 2 ) );
    }

    
	public void paint(java.awt.Graphics g){
        super.paint(g);
        if (shouldDrawLine){
            paintLine(this, insertLocation);
        }
    }

    protected boolean componentsIsEmpty(){
        return (componentElements.size() == 0);
    }

    protected void scrollIt(java.awt.Point hoverPoint){
        java.awt.Dimension scrollSpace = topLevelScrollPane.getSize();
        int currentValue = topLevelScrollPane.getVerticalScrollBar().getValue();
        int maxValue = topLevelScrollPane.getVerticalScrollBar().getMaximum();
        int minValue = topLevelScrollPane.getVerticalScrollBar().getMinimum();
        int amountToScroll = SCROLL_AMOUNT;
        if (hoverPoint.y < SCROLL_SIZE){
            if ( currentValue > minValue){
                if ((currentValue-amountToScroll) >= minValue){
                    currentValue -= amountToScroll;
                }
                else{
                    currentValue = minValue;
                }
                topLevelScrollPane.getVerticalScrollBar().setValue(currentValue);
                SCROLL_AMOUNT += SCROLL_DELTA;
                if (SCROLL_AMOUNT > MAX_SCROLL){
                    SCROLL_AMOUNT = MAX_SCROLL;
                }
            }
        }
        else if (hoverPoint.y > (scrollSpace.getHeight()-SCROLL_SIZE)){
            if ( currentValue < maxValue){
                if ((currentValue+amountToScroll) <= maxValue){
                    currentValue += amountToScroll;
                }
                else{
                    currentValue = maxValue;
                }
                topLevelScrollPane.getVerticalScrollBar().setValue(currentValue);
                SCROLL_AMOUNT += SCROLL_DELTA;
                if (SCROLL_AMOUNT > MAX_SCROLL){
                    SCROLL_AMOUNT = MAX_SCROLL;
                }
            }
        }
        else{
            SCROLL_AMOUNT = SCROLL_START;
        }

    }

    protected boolean isMyParent(edu.cmu.cs.stage3.alice.core.Element currentParent, edu.cmu.cs.stage3.alice.core.Element toSearchFor){
        if (currentParent == toSearchFor){
            return true;
        }
        if (currentParent == null){
            return false;
        }
        return isMyParent(currentParent.getParent(), toSearchFor);
    }

    //componentSizesAndPositions vector has already been initialized to current conditions
    //mouse is over a location and this figures out where the drop should occur
    //s_currentComponentPanel is the CompositeComponentPanel of the current DropPanel
    protected void insertDropPanel(java.awt.dnd.DropTargetDragEvent dtde){
        java.awt.Component dropComponent = dtde.getDropTargetContext().getComponent();
        java.awt.datatransfer.Transferable currentTransferable = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentTransferable();
        edu.cmu.cs.stage3.alice.core.Element currentElement = null;
        java.awt.Point mainSpacePoint;

        if (topLevelScrollPane == null){
            setTopLevelScrollPanel();
        }

        if (this != s_currentComponentPanel){  //check if moved to a new CompositeComponentElementPanel
            s_currentComponentPanel.removeDropPanel();
            s_currentComponentPanel = this;
        }
        java.awt.Point panelSpacePoint = dtde.getLocation();
        if (topLevelScrollPane != null){
            mainSpacePoint = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), panelSpacePoint, topLevelScrollPane);
            scrollIt(mainSpacePoint);
        }
        if (dropComponent != this){ // sets dropComponent to the basic element that the mouse is over (insertPanel, ComponentElementPanel, CompositeElementPanel
            dropComponent = getContainingComponentPanel(dropComponent);
            panelSpacePoint = javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this);
        }
        if (componentsIsEmpty()){
            drawFeedback(null,null);
            this.getDropPanel().setHighlight(true);
            return;
        }
        if (currentTransferable != null){
            try{
                currentElement = (edu.cmu.cs.stage3.alice.core.Element)currentTransferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
            }catch (Exception e){
            }
        }
        else{
            insertLocation = -1;
            return;
        }
        //dropComponent is now set to this panel or the element in this panel the mouse is over
        int position = getInsertLocation(panelSpacePoint);
        if (checkDropLocation(position, currentElement)){
            insertLocation = position;
            this.paintLine(this, insertLocation);
        }

    }


    //There is an insertPanel somewhere in this responsePanel
    public void removeDropPanelFromCurrentComponent(){
        shouldDrawLine = false;
        drawFeedback( null, null );
        dropPanelLocation = -2;
        if (s_currentComponentPanel != null){
            s_currentComponentPanel.removeDropPanel();
        }
    }

    protected void unhook(edu.cmu.cs.stage3.alice.core.Element element){
        element.removeFromParent();
    }

    protected void addToElement(edu.cmu.cs.stage3.alice.core.Element toAdd, edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty toAddTo, int location){
        String newName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild(toAdd.name.getStringValue(), toAddTo.getOwner());
        toAdd.name.set(newName);
        toAdd.setParent(toAddTo.getOwner());
        toAddTo.add(location, toAdd);
    }

    public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e){
        inserting = false;
        removeDropPanelFromCurrentComponent();
    }

    public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e){
        inserting = false;
        removeDropPanelFromCurrentComponent();
    }

    public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e){
    }

    //Searches through toCheck to see if the response represented by this handler is located within toCheck
    protected boolean isValidDrop(edu.cmu.cs.stage3.alice.core.Element current, edu.cmu.cs.stage3.alice.core.Element toCheck){
        if (toCheck == current){
            return false;
        }
        if (current.getParent() != null){
            return isValidDrop(current.getParent(), toCheck);
        }
        return true;
    }

    
	public abstract void drop( final java.awt.dnd.DropTargetDropEvent dtde );

    protected int getLastElementLocation(){
        return componentElements.size();
    }

    protected boolean isRecursive(edu.cmu.cs.stage3.alice.core.Element toDrop){
        edu.cmu.cs.stage3.alice.core.Element potentialParent = null;
        if (toDrop instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse){
            potentialParent = ((edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)toDrop).userDefinedResponse.getUserDefinedResponseValue();
        }
        else if (toDrop instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion){
            potentialParent = ((edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)toDrop).userDefinedQuestion.getUserDefinedQuestionValue();
        }
        if (potentialParent != null){
            return isMyParent(componentElements.getOwner(), potentialParent);
        }
        return false;
    }

    protected boolean canFindOwner(edu.cmu.cs.stage3.alice.core.Element lookingFor, edu.cmu.cs.stage3.alice.core.Element current){
        if (current == null){
            return false;
        }
        if (lookingFor == current){
            return true;
        }
        else{
            return canFindOwner(lookingFor, current.getParent());
        }
    }

    protected boolean checkLoop(edu.cmu.cs.stage3.alice.core.Element e){
        if (e instanceof edu.cmu.cs.stage3.alice.core.Variable){
            edu.cmu.cs.stage3.alice.core.Variable v = (edu.cmu.cs.stage3.alice.core.Variable)e;
            if (v.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.ForEach || v.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.ForEach){
                if (canFindOwner(v.getParent(),this.componentElements.getOwner())){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }

    protected void performDrop(edu.cmu.cs.stage3.alice.core.Element toDrop, java.awt.dnd.DropTargetDropEvent dtde){
        if (isRecursive(toDrop)){
            Object[] options = {"Yes, I understand what I am doing.",
                    "No, I made this call accidentally.",};
            int recursionReturn = edu.cmu.cs.stage3.swing.DialogManager.showOptionDialog(
                     "The code you have just dropped in creates a \"recursive method call\". We recommend that you understand\n"
                    +"what recursion is before making a call like this.  Are you sure you want to do this?", "Recursion Warning", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, options, options[1]);
            if (recursionReturn != 0){
                return;
            }
        }
        if (authoringTool != null){
            authoringTool.getUndoRedoStack().startCompound();
        }

        int dropPanelLocationTemp = getInsertLocation(javax.swing.SwingUtilities.convertPoint(dtde.getDropTargetContext().getComponent(), dtde.getLocation(), this));
        if (isInverted()){
            if (dropPanelLocationTemp == -1){
                dropPanelLocationTemp = 0;
            }
            else{
                dropPanelLocationTemp = componentElements.size() - dropPanelLocationTemp;
            }
        }
        inserting = false;
        boolean isCopy = false;
        java.awt.Component sourceComponent = edu.cmu.cs.stage3.alice.authoringtool.util.DnDManager.getCurrentDragComponent();
        removeDropPanelFromCurrentComponent();
        if (((dtde.getDropAction()& java.awt.dnd.DnDConstants.ACTION_COPY) > 0)){
			//toDrop = (edu.cmu.cs.stage3.alice.core.Element)toDrop.createCopyNamed(null, null, dropPanelLocationTemp, null, null);
			toDrop = toDrop.HACK_createCopy(null, null, dropPanelLocationTemp, null, componentElements.getOwner() );
            isCopy = true;
        }
        insertingElement = true;
        boolean alreadyDone = false;
        if (!isCopy && componentElements.contains(toDrop)){

            int oldIndex = componentElements.indexOf(toDrop);
            if (dropPanelLocationTemp <= -1 || dropPanelLocationTemp >= getLastElementLocation()){
                if (componentElements.size() == 0){
                    dropPanelLocationTemp = 0;
                }
                else{
                    dropPanelLocationTemp = getLastElementLocation()-1;
                }
            }
            else{
                if (dropPanelLocationTemp > oldIndex){
                    dropPanelLocationTemp--;
                }
            }
            int dif = Math.abs(oldIndex - dropPanelLocationTemp);
            if (dif > 0){
                componentElements.shift(oldIndex, dropPanelLocationTemp);
            }
            alreadyDone = true;
        }
        if (!alreadyDone){
            unhook(toDrop);
            if (dropPanelLocationTemp >= -1 && dropPanelLocationTemp <= getLastElementLocation()){
                addToElement(toDrop, componentElements, dropPanelLocationTemp);
            }
            else{
                addToElement(toDrop, componentElements, -1);
            }
        }
        insertingElement = false;
        if (authoringTool != null){
            authoringTool.getUndoRedoStack().stopCompound();
        }
    }

    
	public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
        dtde.acceptDrag( dtde.getDropAction() );
    }

    
	public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
        HACK_timer.restart(); //TIMER HACK
        HACK_timer.start();  //TIMER HACK
        timers.add(HACK_timer);
    }

}
