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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable;

/**
 * @author Jason Pratt
 */
public class DragFromComponent extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.event.ElementSelectionListener {
	public final static int PROPERTIES_TAB = 0;
	public final static int ANIMATIONS_TAB = 1;
	public final static int QUESTIONS_TAB = 2;
	public final static int OTHER_TAB = 3;

	protected edu.cmu.cs.stage3.alice.authoringtool.util.Configuration config = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration
	.getLocalConfiguration(edu.cmu.cs.stage3.alice.authoringtool.JAlice.class.getPackage());
	
	protected edu.cmu.cs.stage3.alice.core.Element element;
	protected edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor variableGroupEditor = new edu.cmu.cs.stage3.alice.authoringtool.editors.variablegroupeditor.VariableGroupEditor();
	protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane newResponseContentPane;
	protected edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane newQuestionContentPane;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty vars;
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty responses;
	protected ResponsesListener responsesListener = new ResponsesListener();
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty questions;
	protected QuestionsListener questionsListener = new QuestionsListener();
	protected edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty poses;
	protected PosesListener posesListener = new PosesListener();
	protected java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.HORIZONTAL, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 );
	protected java.awt.GridBagConstraints glueConstraints = new java.awt.GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.BOTH, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 );
	protected javax.swing.border.Border spacingBorder = javax.swing.BorderFactory.createEmptyBorder( 4, 0, 8, 0 );
	protected edu.cmu.cs.stage3.alice.core.event.ChildrenListener parentListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		private edu.cmu.cs.stage3.alice.core.Element parent;
		public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev ) {
			if( (ev.getChild() == DragFromComponent.this.element) && (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) ) {
				parent = DragFromComponent.this.element.getParent();
			}
		}
		public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev ) {
			if( (ev.getChild() == DragFromComponent.this.element) && (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) ) {
				DragFromComponent.this.setElement( null );
				parent.removeChildrenListener( this );
			}
		}
	};
	protected edu.cmu.cs.stage3.alice.core.event.PropertyListener nameListener = new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
		public void propertyChanging( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {}
		public void propertyChanged( edu.cmu.cs.stage3.alice.core.event.PropertyEvent ev ) {
			DragFromComponent.this.ownerLabel.setText( ev.getValue().toString() + "'s details" );
		}
	};
	protected javax.swing.JButton newAnimationButton = new javax.swing.JButton( "create new method" );
	protected javax.swing.JButton newQuestionButton = new javax.swing.JButton( "create new "+edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING );
	protected javax.swing.JButton capturePoseButton = new javax.swing.JButton( "capture pose" );
	protected edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse newlyCreatedAnimation;
	protected edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion newlyCreatedQuestion;
	protected edu.cmu.cs.stage3.alice.core.Pose newlyCreatedPose;
	protected AuthoringTool authoringTool;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel soundsPanel;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel textureMapsPanel;
	protected edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel miscPanel;

	protected java.util.HashSet panelsToClean = new java.util.HashSet();

	public DragFromComponent( AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		variableGroupEditor.setAuthoringTool( authoringTool );
		newResponseContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewResponseContentPane();
		newQuestionContentPane = new edu.cmu.cs.stage3.alice.authoringtool.dialog.NewQuestionContentPane();
		soundsPanel =  new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.SoundsPanel( authoringTool );
		textureMapsPanel =  new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.TextureMapsPanel( authoringTool );
		miscPanel = new edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ObjectArrayPropertyPanel( "Misc", authoringTool );
		jbInit();
		guiInit();
	}

	private void guiInit() {
		newAnimationButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		newAnimationButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		newAnimationButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( responses != null ) {
						newResponseContentPane.reset( responses.getOwner() );
						int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( newResponseContentPane );
						if( result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION ) {
							authoringTool.getUndoRedoStack().startCompound();
							try {
								edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse response = new edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse();
								response.name.set( newResponseContentPane.getNameValue() );
								responses.getOwner().addChild( response );
								responses.add( response );
							} finally {
								authoringTool.getUndoRedoStack().stopCompound();
							}
						}
					}
				}
			}
		);
		newQuestionButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		newQuestionButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		newQuestionButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( questions != null ) {
						newQuestionContentPane.reset( questions.getOwner() );
						int result = edu.cmu.cs.stage3.swing.DialogManager.showDialog( newQuestionContentPane );
						if( result == edu.cmu.cs.stage3.swing.ContentPane.OK_OPTION ) {
							authoringTool.getUndoRedoStack().startCompound();
							try {
								edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion question = new edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion();
								question.name.set( newQuestionContentPane.getNameValue() );
								question.valueClass.set( newQuestionContentPane.getTypeValue() );
								questions.getOwner().addChild( question );
								questions.add( question );
							} finally {
								authoringTool.getUndoRedoStack().stopCompound();
							}
						}
					}
				}
			}
		);

		capturePoseButton.setBackground( new java.awt.Color( 240, 240, 255 ) );
		capturePoseButton.setMargin( new java.awt.Insets( 2, 4, 2, 4 ) );
		capturePoseButton.addActionListener(
			new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					if( poses != null ) {
						authoringTool.getUndoRedoStack().startCompound();
						try {
							edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable)poses.getOwner(); 
							edu.cmu.cs.stage3.alice.core.Pose pose = edu.cmu.cs.stage3.alice.core.Pose.manufacturePose( transformable, transformable );
							pose.name.set( AuthoringToolResources.getNameForNewChild( "pose", poses.getOwner() ) );
							poses.getOwner().addChild( pose );
							poses.add( pose );
						} finally {
							authoringTool.getUndoRedoStack().stopCompound();
						}
					}
				}
			}
		);

		tabbedPane.setUI( new edu.cmu.cs.stage3.alice.authoringtool.util.AliceTabbedPaneUI() );
		tabbedPane.setOpaque( false );
		tabbedPane.setSelectedIndex( ANIMATIONS_TAB );

		// to make tab color match
		propertiesScrollPane.setBackground( java.awt.Color.white );
		animationsScrollPane.setBackground( java.awt.Color.white );
		questionsScrollPane.setBackground( java.awt.Color.white );
		otherScrollPane.setBackground( java.awt.Color.white );

		soundsPanel.setExpanded( false );
		textureMapsPanel.setExpanded( false );
		miscPanel.setExpanded( false );

		// tooltips
		String cappedQuestionString = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING.substring(0,1).toUpperCase()+edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING.substring(1);
		comboPanel.setToolTipText( "<html><font face=arial size=-1>This area displays the details<p>of the Selected Object.</font></html>" );
		tabbedPane.setToolTipTextAt( PROPERTIES_TAB, "<html><font face=arial size=-1>Open the Properties Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the Properties of the Selected Object.</font></html>" );
		tabbedPane.setToolTipTextAt( ANIMATIONS_TAB, "<html><font face=arial size=-1>Open the Methods Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the Methods of the Selected Object.</font></html>" );
		tabbedPane.setToolTipTextAt( QUESTIONS_TAB, "<html><font face=arial size=-1>Open the "+cappedQuestionString+"s"+" Tab<p>of the Selected Object.<p><p>Use this tab to view and edit<p>the "+cappedQuestionString+"s"+" of the Selected Object.</font></html>" );
		newAnimationButton.setToolTipText( "<html><font face=arial size=-1>Create a New Blank Method<p>and Open it for Editing.</font></html>" );
		newQuestionButton.setToolTipText( "<html><font face=arial size=-1>Create a New Blank "+cappedQuestionString+"<p>and Open it for Editing.</font></html>" );
		propertiesPanel.setToolTipText( "<html><font face=arial size=-1>Properties Tab<p><p>This tab allows you to view and edit<p>the Properties of the Selected Object.</font></html>" );
		animationsPanel.setToolTipText( "<html><font face=arial size=-1>Methods Tab<p><p>Methods are the actions that an object knows how to do.<p>Most objects come with default methods, and you can<p>create your own methods as well.</font></html>" );
		questionsPanel.setToolTipText( "<html><font face=arial size=-1>"+cappedQuestionString+"s"+" Tab<p><p>"+cappedQuestionString+"s"+" are the things that an object can<p>answer about themselves or the world.</font></html>" );
	}

	public void paintComponent( java.awt.Graphics g ) {
		super.paintComponent( g );
		g.setColor( java.awt.Color.black );
		g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
	}

	// ElementSelectionListener interface
	public void elementSelected( edu.cmu.cs.stage3.alice.core.Element element ) {
		setElement( element );
		authoringTool.hackStencilUpdate();
	}

// the swing worker was removed to fix alice locking up if you don't wait for her to 
// settle down after a world load.  as a bonus, the every now and then selection failure
// on world load also went a away.
// TODO: figure out why the worker was necessary in the first place
//     dennisc
//	public void elementSelected( final edu.cmu.cs.stage3.alice.core.Element element ) {
//		edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
//			public Object construct() {
//				setElement( element );
//				authoringTool.hackStencilUpdate();
//				return null;
//			}
//		};
//		worker.start();
//	}


	public edu.cmu.cs.stage3.alice.core.Element getElement() {
		return element;
	}

	synchronized public void setElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		vars = null;
		if( responses != null ) {
			responses.removeObjectArrayPropertyListener( responsesListener );
			responses = null;
		}
		if( questions != null ) {
			questions.removeObjectArrayPropertyListener( questionsListener );
			questions = null;
		}
		if( poses != null ) {
			poses.removeObjectArrayPropertyListener( posesListener );
			poses = null;
		}
		if( this.element != null ) {
			if( this.element.getParent() != null ) {
				this.element.getParent().removeChildrenListener( parentListener );
			}
			this.element.name.removePropertyListener( nameListener );
		}

		this.element = element;

		if( element != null ) {
			ownerLabel.setText( AuthoringToolResources.getReprForValue( element ) + "'s details" );
			if( element.getParent() != null ) {
				element.getParent().addChildrenListener( parentListener );
			}
			element.name.addPropertyListener( nameListener );
			if( element.getSandbox() == element ) { //HACK: only show user-defined stuff for sandboxes
				vars = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)element.getPropertyNamed( "variables" );
				responses = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)element.getPropertyNamed( "responses" );
				if( responses != null ) {
					responses.addObjectArrayPropertyListener( responsesListener );
				}
				questions = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)element.getPropertyNamed( "questions" );
				if( questions != null ) {
					questions.addObjectArrayPropertyListener( questionsListener );
				}
			}
			if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				poses = ((edu.cmu.cs.stage3.alice.core.Transformable)element).poses;
				if( poses != null ) {
					poses.addObjectArrayPropertyListener( posesListener );
				}
			}
		} else {
			ownerLabel.setText( "" );
		}

		int selectedIndex = tabbedPane.getSelectedIndex();
		refreshGUI();
		tabbedPane.setSelectedIndex( selectedIndex );
	}

	public void selectTab( int index ) {
		tabbedPane.setSelectedIndex( index );
	}
	
	public int getSelectedTab(){
		return tabbedPane.getSelectedIndex();
	}

	public String getKeyForComponent( java.awt.Component c ) {
		edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if( c == variableGroupEditor.getNewVariableButton() ) {
			return "newVariableButton";
		} else if( c == newAnimationButton ) {
			return "newAnimationButton";
		} else if( c == newQuestionButton ) {
			return "newQuestionButton";
		} else if( c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel ) {
			try {
				java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel)c).getTransferable();
				if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.variableReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.core.Variable v = (edu.cmu.cs.stage3.alice.core.Variable)transferable.getTransferData( ElementReferenceTransferable.variableReferenceFlavor );
					return "variable<" + v.getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.textureMapReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.core.TextureMap t = (edu.cmu.cs.stage3.alice.core.TextureMap)transferable.getTransferData( ElementReferenceTransferable.textureMapReferenceFlavor );
					return "textureMap<" + t.getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Sound.class ) ) ) {
					edu.cmu.cs.stage3.alice.core.Sound s = (edu.cmu.cs.stage3.alice.core.Sound)transferable.getTransferData( AuthoringToolResources.getReferenceFlavorForClass( edu.cmu.cs.stage3.alice.core.Sound.class ) );
					return "sound<" + s.getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, ElementReferenceTransferable.elementReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( ElementReferenceTransferable.elementReferenceFlavor );
					return "misc<" + e.getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor );
					return "property<" + p.getName() + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor );
					return "userDefinedResponse<" + p.getActualResponse().getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor );
					return "userDefinedQuestion<" + p.getActualQuestion().getKey( world ) + ">";
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor ) ) {
					edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor );
					if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( p.getElementClass() ) ) {
						return "responsePrototype<" + p.getElementClass().getName() + ">";
					} else if( edu.cmu.cs.stage3.alice.core.Question.class.isAssignableFrom( p.getElementClass() ) ) {
						return "questionPrototype<" + p.getElementClass().getName() + ">";
					} else {
						return null;
					}
				} else {
					return null;
				}
			} catch( Exception e ) {
				AuthoringTool.showErrorDialog( "Error examining DnDGroupingPanel.", e );
				return null;
			}
		} else if( c instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton ) {
			Object o = ((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton)c).getObject();
			if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element)o;
				return "editObjectButton<" + e.getKey( world ) + ">";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public java.awt.Component getComponentForKey( String key ) {
		String prefix = AuthoringToolResources.getPrefix( key );
		String spec = AuthoringToolResources.getSpecifier( key );
		edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if( key.equals( "newVariableButton" ) ) {
			return variableGroupEditor.getNewVariableButton();
		} else if( key.equals( "newAnimationButton" ) ) {
			return newAnimationButton;
		} else if( key.equals( "newQuestionButton" ) ) {
			return newQuestionButton;
		} else if( prefix.equals( "variable" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( variableGroupEditor, e );
			}
		} else if( prefix.equals( "textureMap" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( textureMapsPanel, e );
			}
		} else if( prefix.equals( "sound" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( soundsPanel, e );
			}
		} else if( prefix.equals( "misc" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( miscPanel, e );
			}
		} else if( prefix.equals( "property" ) && (spec != null) ) {
			return AuthoringToolResources.findPropertyDnDPanel( propertiesPanel, this.element, spec );
		} else if( prefix.equals( "userDefinedResponse" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Response actualResponse = (edu.cmu.cs.stage3.alice.core.Response)world.getDescendantKeyed( spec );
			if( actualResponse != null ) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel( animationsPanel, actualResponse );
			}
		} else if( prefix.equals( "userDefinedQuestion" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Question actualQuestion = (edu.cmu.cs.stage3.alice.core.Question)world.getDescendantKeyed( spec );
			if( actualQuestion != null ) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel( questionsPanel, actualQuestion );
			}
		} else if( prefix.equals( "responsePrototype" ) && (spec != null) ) {
			try {
				Class elementClass = Class.forName( spec );
				if( elementClass != null ) {
					return AuthoringToolResources.findPrototypeDnDPanel( animationsPanel, elementClass );
				}
			} catch( Exception e ) {
				AuthoringTool.showErrorDialog( "Error while looking for ProtoypeDnDPanel using class " + spec, e );
			}
		} else if( prefix.equals( "questionPrototype" ) && (spec != null) ) {
			try {
				Class elementClass = Class.forName( spec );
				if( elementClass != null ) {
					return AuthoringToolResources.findPrototypeDnDPanel( questionsPanel, elementClass );
				}
			} catch( Exception e ) {
				AuthoringTool.showErrorDialog( "Error while looking for ProtoypeDnDPanel using class " + spec, e );
			}
		} else if( prefix.equals( "editObjectButton" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				if( e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) {
					return AuthoringToolResources.findEditObjectButton( animationsPanel, e );
				} else if( e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
					return AuthoringToolResources.findEditObjectButton( questionsPanel, e );
				} else {
					return AuthoringToolResources.findEditObjectButton( propertiesPanel, e );
				}
			}
		}

		return null;
	}

	public java.awt.Component getPropertyViewComponentForKey( String key ) {
		String prefix = AuthoringToolResources.getPrefix( key );
		String spec = AuthoringToolResources.getSpecifier( key );
		edu.cmu.cs.stage3.alice.core.World world = authoringTool.getWorld();
		if( key.equals( "newVariableButton" ) ) {
			return variableGroupEditor.getNewVariableButton();
		} else if( key.equals( "newAnimationButton" ) ) {
			return newAnimationButton;
		} else if( key.equals( "newQuestionButton" ) ) {
			return newQuestionButton;
		} else if( prefix.equals( "variable" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findPropertyViewController( variableGroupEditor, e, "value" );
			}
		} else if( prefix.equals( "textureMap" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( textureMapsPanel, e );
			}
		} else if( prefix.equals( "sound" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( soundsPanel, e );
			}
		} else if( prefix.equals( "misc" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				return AuthoringToolResources.findElementDnDPanel( miscPanel, e );
			}
		} else if( prefix.equals( "property" ) && (spec != null) ) {
			return AuthoringToolResources.findPropertyViewController( propertiesPanel, this.element, spec );
		} else if( prefix.equals( "userDefinedResponse" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Response actualResponse = (edu.cmu.cs.stage3.alice.core.Response)world.getDescendantKeyed( spec );
			if( actualResponse != null ) {
				return AuthoringToolResources.findUserDefinedResponseDnDPanel( animationsPanel, actualResponse );
			}
		} else if( prefix.equals( "userDefinedQuestion" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Question actualQuestion = (edu.cmu.cs.stage3.alice.core.Question)world.getDescendantKeyed( spec );
			if( actualQuestion != null ) {
				return AuthoringToolResources.findUserDefinedQuestionDnDPanel( questionsPanel, actualQuestion );
			}
		} else if( prefix.equals( "responsePrototype" ) && (spec != null) ) {
			try {
				Class elementClass = Class.forName( spec );
				if( elementClass != null ) {
					return AuthoringToolResources.findPrototypeDnDPanel( animationsPanel, elementClass );
				}
			} catch( Exception e ) {
				AuthoringTool.showErrorDialog( "Error while looking for ProtoypeDnDPanel using class " + spec, e );
			}
		} else if( prefix.equals( "questionPrototype" ) && (spec != null) ) {
			try {
				Class elementClass = Class.forName( spec );
				if( elementClass != null ) {
					return AuthoringToolResources.findPrototypeDnDPanel( questionsPanel, elementClass );
				}
			} catch( Exception e ) {
				AuthoringTool.showErrorDialog( "Error while looking for ProtoypeDnDPanel using class " + spec, e );
			}
		} else if( prefix.equals( "editObjectButton" ) && (spec != null) ) {
			edu.cmu.cs.stage3.alice.core.Element e = world.getDescendantKeyed( spec );
			if( e != null ) {
				if( e instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) {
					return AuthoringToolResources.findEditObjectButton( animationsPanel, e );
				} else if( e instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
					return AuthoringToolResources.findEditObjectButton( questionsPanel, e );
				} else {
					return AuthoringToolResources.findEditObjectButton( propertiesPanel, e );
				}
			}
		}

		return null;
	}

	protected void cleanPanels() {
		for( java.util.Iterator iter = panelsToClean.iterator(); iter.hasNext(); ) {
			javax.swing.JPanel panel = (javax.swing.JPanel)iter.next();
			java.awt.Component[] children = panel.getComponents();
			for( int i = 0; i < children.length; i++ ) {
				if( children[i] instanceof edu.cmu.cs.stage3.alice.authoringtool.util.Releasable ) {
					((edu.cmu.cs.stage3.alice.authoringtool.util.Releasable)children[i]).release();
				}
			}
			panel.removeAll();
		}
		panelsToClean.clear();
	}

	synchronized public void refreshGUI() {
		cleanPanels();
		propertiesPanel.removeAll();
		animationsPanel.removeAll();
		questionsPanel.removeAll();
		if( element != null ) {
			constraints.gridy = 0;
			// Variable panel
			if( vars != null ) {
				variableGroupEditor.setVariableObjectArrayProperty( vars );
				propertiesPanel.add( variableGroupEditor, constraints );
				constraints.gridy++;
			}

			// poses
			if( poses != null ) {
				javax.swing.JPanel subPanel = new javax.swing.JPanel();
				subPanel.setBackground( java.awt.Color.white );
				subPanel.setLayout( new java.awt.GridBagLayout() );
				subPanel.setBorder( spacingBorder );
				panelsToClean.add( subPanel );

				int count = 0;
				Object[] poseArray = poses.getArrayValue();
				for( int i = 0; i < poseArray.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Pose pose = (edu.cmu.cs.stage3.alice.core.Pose)poseArray[i];

					javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( pose );
					if( gui != null ) {
						java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
						subPanel.add( gui, constraints );
						count++;
						if( (newlyCreatedPose == pose) && (gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel) ) {
							((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.ElementDnDPanel)gui).editName();
							newlyCreatedPose = null;
						}
					} else {
						AuthoringTool.showErrorDialog( "Unable to create gui for pose: " + pose, null );
					}
				}

				java.awt.GridBagConstraints c = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 4, 2, 2, 2 ), 0, 0 );
				subPanel.add( capturePoseButton, c );

				propertiesPanel.add( subPanel, constraints );
				constraints.gridy++;
			}

			// property panels
			java.util.Vector propertyStructure = AuthoringToolResources.getPropertyStructure( element, false );
			if( propertyStructure != null ) {
				for( java.util.Iterator iter = propertyStructure.iterator(); iter.hasNext(); ) {
					edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)iter.next();
					String groupName = sop.getString();
					java.util.Vector propertyNames = (java.util.Vector)sop.getObject();

					javax.swing.JPanel subPanel = new javax.swing.JPanel();
					javax.swing.JPanel toAdd = subPanel;
					subPanel.setBackground( java.awt.Color.white );
					subPanel.setLayout( new java.awt.GridBagLayout() );
					subPanel.setBorder( spacingBorder );
					panelsToClean.add( subPanel );
					if (groupName.compareTo("seldom used properties") == 0){
						edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel expandPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel();
						expandPanel.setTitle( "Seldom Used Properties" );
						expandPanel.setContent( subPanel );
						expandPanel.setExpanded(false);
						toAdd = expandPanel;
					}

					if( propertyNames != null ) {
						int i = 0;
						for( java.util.Iterator jter = propertyNames.iterator(); jter.hasNext(); ) {
							String name = (String)jter.next();
							final edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed( name );
							if( property != null ) {
								edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory oneShotFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
									public Object createItem( final Object o ) {
										return new Runnable() {
											public void run() {
												if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Transformable) && (property == ((edu.cmu.cs.stage3.alice.core.Transformable)property.getOwner()).vehicle) ) {
													((edu.cmu.cs.stage3.alice.core.Transformable)property.getOwner()).setVehiclePreservingAbsoluteTransformation( (edu.cmu.cs.stage3.alice.core.ReferenceFrame)o );
												} else if (property.getName().equals("localTransformation")){ //Animate and undo the point of view when set
													edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnim = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
													povAnim.subject.set( property.getOwner() );
													povAnim.pointOfView.set( o );
													povAnim.asSeenBy.set( element.getParent() );
													edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
													undoResponse.subject.set( property.getOwner() );
													undoResponse.pointOfView.set( ((edu.cmu.cs.stage3.alice.core.Transformable)property.getOwner()).localTransformation.getMatrix4dValue() );
													undoResponse.asSeenBy.set( ((edu.cmu.cs.stage3.alice.core.Transformable)property.getOwner()).vehicle.getValue() );
													edu.cmu.cs.stage3.alice.core.Property[] properties = new edu.cmu.cs.stage3.alice.core.Property[] { ((edu.cmu.cs.stage3.alice.core.Transformable)property.getOwner()).localTransformation };
													authoringTool.performOneShot( povAnim, undoResponse, properties );
												} else {
													edu.cmu.cs.stage3.alice.core.response.PropertyAnimation response = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													response.element.set( property.getOwner() );
													response.propertyName.set( property.getName() );
													response.value.set( o );
													edu.cmu.cs.stage3.alice.core.response.PropertyAnimation undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
													undoResponse.element.set( property.getOwner() );
													undoResponse.propertyName.set( property.getName() );
													undoResponse.value.set( property.getValue() );
													// this is over-reaching for some properties
													java.util.Vector pVector = new java.util.Vector();
													pVector.add( property );
													edu.cmu.cs.stage3.alice.core.Element[] descendants = property.getOwner().getDescendants();
													for( int i = 0; i < descendants.length; i++ ) {
														edu.cmu.cs.stage3.alice.core.Property p = descendants[i].getPropertyNamed( property.getName() );
														if( p != null ) {
															pVector.add( p );
														}
													}
													edu.cmu.cs.stage3.alice.core.Property[] properties = (edu.cmu.cs.stage3.alice.core.Property[])pVector.toArray( new edu.cmu.cs.stage3.alice.core.Property[0] );
													authoringTool.performOneShot( response, undoResponse, properties );
												}
											}
										};
									}
								};
								javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getPropertyGUI( property, true, false, oneShotFactory );
								if( gui != null ) {
									java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, i, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
									subPanel.add( gui, constraints );
									i++;
								} else {
									AuthoringTool.showErrorDialog( "Unable to create gui for property: " + property, null );
								}
							} else {
								AuthoringTool.showErrorDialog( "no property on " + element + " named " + name, null );
							}
						}
					}

					propertiesPanel.add( toAdd, constraints );
					constraints.gridy++;
				}
			}

			// sounds/texture/misc
			if( element instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
				edu.cmu.cs.stage3.alice.core.Sandbox sandbox = (edu.cmu.cs.stage3.alice.core.Sandbox)element;
				soundsPanel.setSounds( sandbox.sounds );
				propertiesPanel.add( soundsPanel, constraints );
				constraints.gridy++;
				textureMapsPanel.setTextureMaps( sandbox.textureMaps );
				propertiesPanel.add( textureMapsPanel, constraints );
				constraints.gridy++;
				if( sandbox.misc.size() > 0 ) {
					miscPanel.setObjectArrayProperty( sandbox.misc );
					propertiesPanel.add( miscPanel, constraints );
					constraints.gridy++;
				}
				propertiesPanel.add( javax.swing.Box.createVerticalStrut( 8 ), constraints );
				constraints.gridy++;
			}

			if( element.data.get( "modeled by" ) != null ) {
				propertiesPanel.add( new javax.swing.JLabel( "modeled by:  " + element.data.get( "modeled by" ) ), constraints );
				constraints.gridy++;
			}
			if( element.data.get( "painted by" ) != null ) {
				propertiesPanel.add( new javax.swing.JLabel( "painted by:  " + element.data.get( "painted by" ) ), constraints );
				constraints.gridy++;
			}
			if( element.data.get( "programmed by" ) != null ) {
				propertiesPanel.add( new javax.swing.JLabel( "programmed by:  " + element.data.get( "programmed by" ) ), constraints );
				constraints.gridy++;
			}
			if ( element.data.get( "modeled by" ) != null  ){
				java.text.NumberFormat formatter = new java.text.DecimalFormat("#.####");
				propertiesPanel.add( new javax.swing.JLabel( "depth:  " + formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getDepth()) ), constraints );
				constraints.gridy++;
				propertiesPanel.add( new javax.swing.JLabel( "height:  " + formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getHeight()) ), constraints );
				constraints.gridy++;
				propertiesPanel.add( new javax.swing.JLabel( "width:  " + formatter.format(((edu.cmu.cs.stage3.alice.core.Model) element).getWidth()) ), constraints );
				constraints.gridy++;
			}
			glueConstraints.gridy = constraints.gridy;
			propertiesPanel.add( javax.swing.Box.createGlue(), glueConstraints );

			constraints.gridy = 0;

			// user-defined responses
			if( responses != null ) {
				javax.swing.JPanel subPanel = new javax.swing.JPanel();
				subPanel.setBackground( java.awt.Color.white );
				subPanel.setLayout( new java.awt.GridBagLayout() );
				subPanel.setBorder( spacingBorder );
				panelsToClean.add( subPanel );

				int count = 0;
				Object[] responseArray = responses.getArrayValue();
				for( int i = 0; i < responseArray.length; i++ ) {
					Class responseClass = edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class;
					edu.cmu.cs.stage3.alice.core.Response response = (edu.cmu.cs.stage3.alice.core.Response)responseArray[i];

					if( response instanceof edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse ) {
						edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype( (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)response );
						javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( callToUserDefinedResponsePrototype );
						if( gui != null ) {
							edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton editButton = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getEditObjectButton( response, gui );
							editButton.setToolTipText( "<html><font face=arial size=-1>Open this method for editing.</font></html>" );
							javax.swing.JPanel guiPanel = new javax.swing.JPanel();
							panelsToClean.add( guiPanel );
							guiPanel.setBackground( java.awt.Color.white );
							guiPanel.setLayout( new java.awt.GridBagLayout() );
							guiPanel.add( gui, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );
							guiPanel.add( editButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.SOUTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 4, 0, 0 ), 0, 0 ) );
							java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
							subPanel.add( guiPanel, constraints );
							count++;
							if( (newlyCreatedAnimation == response) && (gui instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel) ) {
//								((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CallToUserDefinedResponsePrototypeDnDPanel)gui).editName();
								authoringTool.editObject( newlyCreatedAnimation );
								newlyCreatedAnimation = null;
							}
						} else {
							AuthoringTool.showErrorDialog( "Unable to create gui for callToUserDefinedResponsePrototype: " + callToUserDefinedResponsePrototype, null );
						}
					} else {
						AuthoringTool.showErrorDialog( "Response is not a userDefinedResponse: " + response, null );
					}
				}

				java.awt.GridBagConstraints c = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 4, 2, 2, 2 ), 0, 0 );
				subPanel.add( newAnimationButton, c );

				animationsPanel.add( subPanel, constraints );
				constraints.gridy++;
			}

			// response panels
			java.util.Vector oneShotStructure = AuthoringToolResources.getOneShotStructure( element.getClass() );
			if( oneShotStructure != null ) {
				for( java.util.Iterator iter = oneShotStructure.iterator(); iter.hasNext(); ) {
					edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)iter.next();
					String groupName = sop.getString();
					java.util.Vector responseNames = (java.util.Vector)sop.getObject();
					javax.swing.JPanel subPanel = new javax.swing.JPanel();
					javax.swing.JPanel toAdd = subPanel;
					subPanel.setBackground( java.awt.Color.white );
					subPanel.setLayout( new java.awt.GridBagLayout() );
					subPanel.setBorder( spacingBorder );
					panelsToClean.add( subPanel );

					if( responseNames != null ) {

						
						int i = 0;
						for( java.util.Iterator jter = responseNames.iterator(); jter.hasNext(); ) {
							Object item = jter.next();
							if( item instanceof String ) { // ignore hierarchy for now
								String className = (String)item;
								try {
									if( ! className.startsWith( "edu.cmu.cs.stage3.alice.core.response.PropertyAnimation" ) ) { // ignore property animations for now
										Class responseClass = Class.forName( className );
										java.util.LinkedList known = new java.util.LinkedList();
										String format = AuthoringToolResources.getFormat( responseClass );
										edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer( format );
										while( tokenizer.hasMoreTokens() ) {
											String token = tokenizer.nextToken();
											if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
												String propertyName = token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) );
												known.add( new edu.cmu.cs.stage3.util.StringObjectPair( propertyName, element ) );
											}
										}
										edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = (edu.cmu.cs.stage3.util.StringObjectPair[])known.toArray( new edu.cmu.cs.stage3.util.StringObjectPair[0] );

										String[] desiredProperties = AuthoringToolResources.getDesiredProperties( responseClass );
										edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype( responseClass, knownPropertyValues, desiredProperties );
										javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( responsePrototype );
										if( gui != null ) {
											java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, i, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
											subPanel.add( gui, constraints );
											i++;
										} else {
											AuthoringTool.showErrorDialog( "Unable to create gui for responsePrototype: " + responsePrototype, null );
										}
									}
								} catch( ClassNotFoundException e ) {
									AuthoringTool.showErrorDialog( "Error while looking for class " + className, e );
								}
							}
						}
					}

					animationsPanel.add( toAdd, constraints );
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			animationsPanel.add( javax.swing.Box.createGlue(), glueConstraints );


			// user-defined questions
			constraints.gridy = 0;
			if( questions != null ) {
				javax.swing.JPanel subPanel = new javax.swing.JPanel();
				subPanel.setBackground( java.awt.Color.white );
				subPanel.setLayout( new java.awt.GridBagLayout() );
				subPanel.setBorder( spacingBorder );
				panelsToClean.add( subPanel );

				int count = 0;
				Object[] questionsArray = questions.getArrayValue();
				for( int i = 0; i < questionsArray.length; i++ ) {
					Class questionClass = edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class;
					edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question)questionsArray[i];

					if( question instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
						edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype( (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)question );
						javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( callToUserDefinedQuestionPrototype );
						if( gui != null ) {
							edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton editButton = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getEditObjectButton( question, gui );
							editButton.setToolTipText( "<html><font face=arial size=-1>Open this question for editing.</font></html>" );
							javax.swing.JPanel guiPanel = new javax.swing.JPanel();
							panelsToClean.add( guiPanel );
							guiPanel.setBackground( java.awt.Color.white );
							guiPanel.setLayout( new java.awt.GridBagLayout() );
							guiPanel.add( gui, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.SOUTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 0, 0, 0 ), 0, 0 ) );
							guiPanel.add( editButton, new java.awt.GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.SOUTHWEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 4, 0, 0 ), 0, 0 ) );
							java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
							subPanel.add( guiPanel, constraints );
							count++;
							if( newlyCreatedQuestion == question ) {
								authoringTool.editObject( newlyCreatedQuestion );
								newlyCreatedQuestion = null;
							}
						} else {
							AuthoringTool.showErrorDialog( "Unable to create gui for callToUserDefinedQuestionPrototype: " + callToUserDefinedQuestionPrototype, null );
						}
					} else {
						throw new RuntimeException( "ERROR: question is not a userDefinedQuestion" );
					}
				}

				java.awt.GridBagConstraints c = new java.awt.GridBagConstraints( 0, count, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 4, 2, 2, 2 ), 0, 0 );
				subPanel.add( newQuestionButton, c );

				questionsPanel.add( subPanel, constraints );
				constraints.gridy++;
			}

			// question panels
			java.util.Vector questionStructure = AuthoringToolResources.getQuestionStructure( element.getClass() );
			if( questionStructure != null ) {
				for( java.util.Iterator iter = questionStructure.iterator(); iter.hasNext(); ) {
					edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)iter.next();
					String groupName = sop.getString();
					java.util.Vector questionNames = (java.util.Vector)sop.getObject();

					javax.swing.JPanel subPanel = new javax.swing.JPanel();
					subPanel.setBackground( java.awt.Color.white );
					subPanel.setLayout( new java.awt.GridBagLayout() );
					panelsToClean.add( subPanel );

					edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel expandPanel = new edu.cmu.cs.stage3.alice.authoringtool.util.ExpandablePanel();
					expandPanel.setTitle( groupName );
					expandPanel.setContent( subPanel );

					if( questionNames != null ) {
						int i = 0;
						for( java.util.Iterator jter = questionNames.iterator(); jter.hasNext(); ) {
							String className = (String)jter.next();
							try {
								Class questionClass = Class.forName( className );
								edu.cmu.cs.stage3.alice.core.Question tempQuestion = (edu.cmu.cs.stage3.alice.core.Question)questionClass.newInstance();
								java.util.LinkedList known = new java.util.LinkedList();
								String format = AuthoringToolResources.getFormat( questionClass );
								edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer( format );
								while( tokenizer.hasMoreTokens() ) {
									String token = tokenizer.nextToken();
									if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
										String propertyName = token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) );
										known.add( new edu.cmu.cs.stage3.util.StringObjectPair( propertyName, element ) );
									}
								}
								if( edu.cmu.cs.stage3.alice.core.question.PartKeyed.class.isAssignableFrom( questionClass ) ) { // special case hack
									known.add( new edu.cmu.cs.stage3.util.StringObjectPair( "key", "" ) );
								}
								edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = (edu.cmu.cs.stage3.util.StringObjectPair[])known.toArray( new edu.cmu.cs.stage3.util.StringObjectPair[0] );

								String[] desiredProperties = AuthoringToolResources.getDesiredProperties( questionClass );
								if( edu.cmu.cs.stage3.alice.core.question.PartKeyed.class.isAssignableFrom( questionClass ) ) { // special case hack
									desiredProperties = new String[0];
								}
								edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype elementPrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype( questionClass, knownPropertyValues, desiredProperties );
								javax.swing.JComponent gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( elementPrototype );
								if( gui != null ) {
									java.awt.GridBagConstraints constraints = new java.awt.GridBagConstraints( 0, i, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 );
									subPanel.add( gui, constraints );
									i++;
								} else {
									AuthoringTool.showErrorDialog( "Unable to create gui for elementPrototype: " + elementPrototype, null );
								}
							} catch( ClassNotFoundException e ) {
								AuthoringTool.showErrorDialog( "Unable to create gui for class: " + className, e );
							} catch( IllegalAccessException e ) {
								AuthoringTool.showErrorDialog( "Unable to create gui for class: " + className, e );
							} catch( InstantiationException e ) {
								AuthoringTool.showErrorDialog( "Unable to create gui for class: " + className, e );
							}
						}
					}

					questionsPanel.add( expandPanel, constraints );
					constraints.gridy++;
				}
			}
			glueConstraints.gridy = constraints.gridy;
			questionsPanel.add( javax.swing.Box.createGlue(), glueConstraints );


			// other panels
//			constraints.gridy = 0;
//			if( element instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
//				edu.cmu.cs.stage3.alice.core.Sandbox sandbox = (edu.cmu.cs.stage3.alice.core.Sandbox)element;
//				soundsPanel.setSounds( sandbox.sounds );
//				otherPanel.add( soundsPanel, constraints );
//				constraints.gridy++;
//				textureMapsPanel.setTextureMaps( sandbox.textureMaps );
//				otherPanel.add( textureMapsPanel, constraints );
//				constraints.gridy++;
//				miscPanel.setObjectArrayProperty( sandbox.misc );
//				otherPanel.add( miscPanel, constraints );
//				constraints.gridy++;
//			}
//			glueConstraints.gridy = constraints.gridy;
//			otherPanel.add( javax.swing.Box.createGlue(), glueConstraints );
		}
		revalidate();
		repaint();
	}

	public class ResponsesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
				newlyCreatedAnimation = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex( selectedIndex );
		}
	}

	public class QuestionsListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
				newlyCreatedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex( selectedIndex );
		}
	}

	public class PosesListener implements edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent ev ) {
			if( ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent.ITEM_INSERTED ) {
				newlyCreatedPose = (edu.cmu.cs.stage3.alice.core.Pose)ev.getItem();
			}
			int selectedIndex = tabbedPane.getSelectedIndex();
			refreshGUI();
			tabbedPane.setSelectedIndex( selectedIndex );
		}
	}

	////////////////////
	// Autogenerated
	////////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel propertySubPanel = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	Border border1;
	//JScrollPane propertyScrollPane = new JScrollPane();
	Border border2;
	Border border3;
	Border border4;
	Border border5;
	Border border6;
	Border border7;
	JPanel comboPanel = new JPanel();
	JLabel ownerLabel = new JLabel();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	JTabbedPane tabbedPane = new JTabbedPane();
	JScrollPane propertiesScrollPane = new JScrollPane();
	JScrollPane animationsScrollPane = new JScrollPane();
	GridBagLayout gridBagLayout2 = new GridBagLayout();
	JPanel propertiesPanel = new JPanel();
	GridBagLayout gridBagLayout3 = new GridBagLayout();
	JPanel animationsPanel = new JPanel();
	JScrollPane questionsScrollPane = new JScrollPane();
	JPanel questionsPanel = new JPanel();
	GridBagLayout gridBagLayout4 = new GridBagLayout();
	JScrollPane otherScrollPane = new JScrollPane();
	JPanel otherPanel = new JPanel();
	GridBagLayout gridBagLayout5 = new GridBagLayout();
	Border border8;
	Border border9;

	private void jbInit() {
		border1 = BorderFactory.createEmptyBorder(2,0,0,0);
		border2 = BorderFactory.createLineBorder(SystemColor.controlText,1);
		border3 = BorderFactory.createCompoundBorder(border2,border1);
		border4 = BorderFactory.createEmptyBorder(8,8,8,8);
		border5 = BorderFactory.createEmptyBorder(2,2,2,2);
		border6 = BorderFactory.createLineBorder(SystemColor.controlText,1);
		border7 = BorderFactory.createCompoundBorder(border6,border5);
		border8 = BorderFactory.createEmptyBorder();
		border9 = BorderFactory.createLineBorder(Color.black,1);
		this.setLayout(borderLayout1);
		propertySubPanel.setLayout(borderLayout2);
		propertySubPanel.setBorder(border1);
		propertySubPanel.setMinimumSize(new Dimension(0, 0));
		propertySubPanel.setOpaque(false);
		this.setBackground(new Color(204, 204, 204));
		this.setMinimumSize(new Dimension(0, 0));
		borderLayout2.setHgap(8);
		borderLayout2.setVgap(6);
		comboPanel.setLayout(gridBagLayout1);
		ownerLabel.setForeground(Color.black);
		ownerLabel.setText("owner\'s details");
		propertiesPanel.setBackground(Color.white);
		propertiesPanel.setLayout(gridBagLayout2);
		animationsPanel.setBackground(Color.white);
		animationsPanel.setLayout(gridBagLayout3);
		questionsPanel.setBackground(Color.white);
		questionsPanel.setLayout(gridBagLayout4);
		otherPanel.setBackground(Color.white);
		otherPanel.setLayout(gridBagLayout5);
		propertiesScrollPane.getViewport().setBackground(Color.white);
		propertiesScrollPane.setBorder(null);
		animationsScrollPane.getViewport().setBackground(Color.white);
		animationsScrollPane.setBorder(null);
		questionsScrollPane.getViewport().setBackground(Color.white);
		questionsScrollPane.setBorder(null);
		otherScrollPane.getViewport().setBackground(Color.white);
		otherScrollPane.setBorder(null);
		comboPanel.setOpaque(false);
		this.add(propertySubPanel, BorderLayout.CENTER);
		propertySubPanel.add(comboPanel, BorderLayout.NORTH);
		comboPanel.add(ownerLabel,  new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 8, 0, 0), 0, 0));
		propertySubPanel.add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.add(propertiesScrollPane, "properties");
		propertiesScrollPane.getViewport().add(propertiesPanel, null);
		tabbedPane.add(animationsScrollPane, "methods");
		tabbedPane.add(questionsScrollPane, edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.QUESTION_STRING+"s");
//		tabbedPane.add(otherScrollPane, "other");
		otherScrollPane.getViewport().add(otherPanel, null);
		questionsScrollPane.getViewport().add(questionsPanel, null);
		animationsScrollPane.getViewport().add(animationsPanel, null);
	}
}