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

package edu.cmu.cs.stage3.alice.authoringtool.util;

/**
 * @author Jason Pratt
 */
public class ElementPopupUtilities {
	// preferences
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage() );

	protected static Class[] elementPopupRunnableParams = new Class[] { edu.cmu.cs.stage3.alice.core.Element.class };
	private static Runnable emptyRunnable = new Runnable() {
		public void run() {}
	};

	public static void createAndShowElementPopupMenu( edu.cmu.cs.stage3.alice.core.Element element, java.util.Vector structure, java.awt.Component component, int x, int y ) {
		javax.swing.JPopupMenu popup = makeElementPopupMenu( element, structure );
		popup.show( component, x, y );
		PopupMenuUtilities.ensurePopupIsOnScreen( popup );
	}

	/**
	 * @deprecated use makeElementPopupMenu
	 */
	public static javax.swing.JPopupMenu makeElementPopup( edu.cmu.cs.stage3.alice.core.Element element, java.util.Vector structure ) {
		return makeElementPopupMenu( element, structure );
	}
	public static javax.swing.JPopupMenu makeElementPopupMenu( edu.cmu.cs.stage3.alice.core.Element element, java.util.Vector structure ) {
		if( (element != null) && (structure != null) ) {
			Object[] initArgs = new Object[] { element };
			substituteRunnables( initArgs, structure );
			return PopupMenuUtilities.makePopupMenu( structure );
		} else {
			return null;
		}
	}

	public static void substituteRunnables( Object[] initArgs, java.util.Vector structure ) {
		for( java.util.ListIterator iter = structure.listIterator(); iter.hasNext(); ) {
			Object o = iter.next();
			if( o instanceof Class && ElementPopupRunnable.class.isAssignableFrom( (Class)o ) ) {
				try {
					ElementPopupRunnable r = (ElementPopupRunnable)((Class)o).getConstructor( elementPopupRunnableParams ).newInstance( initArgs );
					edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair( r.getDefaultLabel(), r );
					iter.set( newPair );
				} catch( NoSuchMethodException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
				} catch( IllegalAccessException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
				} catch( InstantiationException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
				} catch( java.lang.reflect.InvocationTargetException e ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
				}
			} else if( o instanceof ElementPopupRunnable ) {
				ElementPopupRunnable r = (ElementPopupRunnable)o;
				edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair( r.getDefaultLabel(), r );
				iter.set( newPair );
			} else if( o instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair pair = (edu.cmu.cs.stage3.util.StringObjectPair)o;
				if( (pair.getObject() instanceof Class) && ElementPopupRunnable.class.isAssignableFrom( (Class)pair.getObject() ) ) {
					try {
						edu.cmu.cs.stage3.util.StringObjectPair newPair = new edu.cmu.cs.stage3.util.StringObjectPair( pair.getString(), ((Class)pair.getObject()).getConstructor( elementPopupRunnableParams ).newInstance( initArgs ) );
						iter.set( newPair );
//						pair.setObject( ((Class)pair.getObject()).getConstructor( elementPopupRunnableParams ).newInstance( initArgs ) );
					} catch( NoSuchMethodException e ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
					} catch( IllegalAccessException e ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
					} catch( InstantiationException e ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
					} catch( java.lang.reflect.InvocationTargetException e ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error building popup.", e );
					}
				} else if( pair.getObject() instanceof java.util.Vector ) {
					substituteRunnables( initArgs, (java.util.Vector)pair.getObject() );
				}
			}
		}
	}

	public static java.util.Vector makeCoerceToStructure( final edu.cmu.cs.stage3.alice.core.Element element ) {
		if( (element != null) && element.isCoercionSupported() ) {
			java.util.Vector structure = new java.util.Vector();
			java.util.Vector subStructure = new java.util.Vector();

			Class[] classes = element.getSupportedCoercionClasses();
			if( classes != null ) {
				for( int i = 0; i < classes.length; i++ ) {
					final Class c = classes[i];
					if (element instanceof edu.cmu.cs.stage3.alice.core.response.TurnAnimation){
						edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = (edu.cmu.cs.stage3.alice.core.response.TurnAnimation)element;
						if (turnAnimation.direction.get() == edu.cmu.cs.stage3.alice.core.Direction.FORWARD || turnAnimation.direction.get() == edu.cmu.cs.stage3.alice.core.Direction.BACKWARD){
							if (edu.cmu.cs.stage3.alice.core.response.RollAnimation.class.isAssignableFrom(c)){
								continue;
							}
						}
					}
					String repr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( c );
					Runnable runnable = new Runnable() {
						public void run() {
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();
							element.coerceTo( c );
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
						}
					};
					subStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( repr, runnable ) );
				}
				if (subStructure.size() > 0){
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "change to", subStructure ) );
					return structure;
				}
				else{
					return null;
				}
			}
		}

		return null;
	}

	public static java.util.Vector getDefaultStructure( edu.cmu.cs.stage3.alice.core.Element element ) {
		return getDefaultStructure( element, true, null, null, null );
	}

	public static java.util.Vector getDefaultStructure( edu.cmu.cs.stage3.alice.core.Element element, boolean elementEnabled, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, javax.swing.JTree jtree, javax.swing.tree.TreePath treePath ) {
		if( element instanceof edu.cmu.cs.stage3.alice.core.Response ) {
			return getDefaultResponseStructure( (edu.cmu.cs.stage3.alice.core.Response)element );
		} else if( element instanceof edu.cmu.cs.stage3.alice.core.Question ) {
			return getDefaultQuestionStructure( (edu.cmu.cs.stage3.alice.core.Question)element );
		} else if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.characterCriterion.accept( element ) ) {
			return getDefaultCharacterStructure( element, elementEnabled, authoringTool, jtree, treePath );
		} else if( element instanceof edu.cmu.cs.stage3.alice.core.World ) {
			return getDefaultWorldStructure( (edu.cmu.cs.stage3.alice.core.World)element );
		} else if( element instanceof edu.cmu.cs.stage3.alice.core.Group ) {
			return getDefaultGroupStructure( (edu.cmu.cs.stage3.alice.core.Group)element, jtree, treePath );
		} else {
			return getDefaultElementStructure( element, jtree, treePath );
		}
	}

	public static java.util.Vector getDefaultCharacterStructure( edu.cmu.cs.stage3.alice.core.Element element, boolean elementEnabled, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool, javax.swing.JTree jtree, javax.swing.tree.TreePath treePath ) {
		java.util.Vector popupStructure = new java.util.Vector();
		popupStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element ), null ) );
		popupStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "separator", javax.swing.JSeparator.class ) );
//		popupStructure.add( MakeCopyRunnable.class );
//		popupStructure.add( MakeSharedCopyRunnable.class );
		if( elementEnabled ) {
			popupStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "methods", edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makeDefaultOneShotStructure( element ) ) );
		}
		if( (jtree != null) && (treePath != null) ) {
			Runnable renameRunnable = new RenameRunnable( element, jtree, treePath );
			popupStructure.add( renameRunnable );
		}
		if( (element instanceof edu.cmu.cs.stage3.alice.core.Sandbox) && authoringToolConfig.getValue( "enableScripting" ).equalsIgnoreCase( "true" ) ) {
			popupStructure.add( edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.EditScriptRunnable.class );
		}
//		popupStructure.add( PrintStatisticsRunnable.class );
		
		if( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.characterCriterion.accept( element ) ) {
			if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				if (!(element instanceof edu.cmu.cs.stage3.alice.core.Camera )){
					popupStructure.add( GetAGoodLookAtRunnable.class );
					popupStructure.add( StorePoseRunnable.class );
				}
			}
			//popupStructure.add( EditCharacterRunnable.class );
			popupStructure.add( DeleteRunnable.class );
			popupStructure.add( SaveCharacterRunnable.class );
			//if( authoringTool != null ) {
			//	Runnable setScopeRunnable = new SetElementScopeRunnable( element, authoringTool );
			//	popupStructure.add( setScopeRunnable );
			//}
		}
		else{
			popupStructure.add( DeleteRunnable.class );
		}
		//TODO: get this in for BVW
//		java.util.Vector copyOverStructure = new java.util.Vector();
//		copyOverStructure.add( CopyOverFromCharacterLoadRunnable.class );
//		copyOverStructure.add( CopyOverFromImportLoadRunnable.class );
//		popupStructure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "copy over", copyOverStructure ) );


		return popupStructure;
	}

	public static java.util.Vector getDefaultWorldStructure( edu.cmu.cs.stage3.alice.core.World world ) {
		java.util.Vector popupStructure =  new java.util.Vector();
		popupStructure.add( EditScriptRunnable.class );
		return popupStructure;
	}

	public static java.util.Vector getDefaultResponseStructure( edu.cmu.cs.stage3.alice.core.Response response ) {
		java.util.Vector structure =  new java.util.Vector();
		structure.add( MakeCopyRunnable.class );
		structure.add( DeleteRunnable.class );
		structure.add( ToggleCommentingRunnable.class );
		java.util.Vector coerceToStructure = makeCoerceToStructure( response );
		if( coerceToStructure != null ) {
			structure.addAll( coerceToStructure );
		}

		return structure;
	}

	public static java.util.Vector getDefaultQuestionStructure( edu.cmu.cs.stage3.alice.core.Question question ) {
		java.util.Vector structure =  new java.util.Vector();
		//structure.add( MakeCopyRunnable.class );
		structure.add( DeleteRunnable.class );
	//	structure.add( ToggleCommentingRunnable.class );
		java.util.Vector coerceToStructure = makeCoerceToStructure( question );
		if( coerceToStructure != null ) {
			structure.addAll( coerceToStructure );
		}

		return structure;
	}

	public static java.util.Vector getDefaultGroupStructure( edu.cmu.cs.stage3.alice.core.Group group, javax.swing.JTree jtree, javax.swing.tree.TreePath treePath ) {
		java.util.Vector structure =  new java.util.Vector();

		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( group ), emptyRunnable ) );
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "separator", javax.swing.JSeparator.class ) );
		structure.add( SortGroupAlphabeticallyRunnable.class );
		if( (jtree != null) && (treePath != null) ) {
			Runnable renameRunnable = new RenameRunnable( group, jtree, treePath );
			structure.add( renameRunnable );
		}
		structure.add( DeleteRunnable.class );

		return structure;
	}

	public static java.util.Vector getDefaultElementStructure( edu.cmu.cs.stage3.alice.core.Element element, javax.swing.JTree jtree, javax.swing.tree.TreePath treePath ) {
		java.util.Vector structure =  new java.util.Vector();
		if( (jtree != null) && (treePath != null) ) {
			Runnable renameRunnable = new RenameRunnable( element, jtree, treePath );
			structure.add( renameRunnable );
		}
		structure.add( DeleteRunnable.class );

		return structure;
	}

	public static abstract class ElementPopupRunnable implements Runnable {
		protected edu.cmu.cs.stage3.alice.core.Element element;

		protected ElementPopupRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this.element = element;
		}

		public edu.cmu.cs.stage3.alice.core.Element getElement() {
			return element;
		}

		public abstract String getDefaultLabel();
	}

	//TODO: there are issues with Models whose world and scenegraph trees don't match
	public static class DeleteRunnable extends ElementPopupRunnable {
		public final static edu.cmu.cs.stage3.util.Criterion namedHeadCriterion = new edu.cmu.cs.stage3.util.Criterion() {
			public boolean accept( Object o ) {
				if( o instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					if( "head".equalsIgnoreCase( ((edu.cmu.cs.stage3.alice.core.Transformable)o).name.getStringValue() ) ) {
						return true;
					}
				}
				return false;
			}
		};
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public DeleteRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
			this.authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
		}

		public DeleteRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "delete";
		}

		public void run() {
			if( element instanceof edu.cmu.cs.stage3.alice.core.Camera ) {
				String message = "The Camera is a critical part of the World.  Very bad things can happen if you delete the Camera.\nAre you sure you want to delete it?";
				int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog( message, "Delete Camera?", javax.swing.JOptionPane.YES_NO_OPTION );
				if( result != javax.swing.JOptionPane.YES_OPTION ) {
					return;
				}
			} else if( element instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight ) {
				if( element.getRoot().getDescendants( edu.cmu.cs.stage3.alice.core.light.DirectionalLight.class ).length == 1 ) {
					String message = "You are about to delete the last directional light in the World.  If you do this, everything will probably become very dark.\nAre you sure you want to delete it?";
					int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog( message, "Delete Light?", javax.swing.JOptionPane.YES_NO_OPTION );
					if( result != javax.swing.JOptionPane.YES_OPTION ) {
						return;
					}
				}
			}


			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = element.getRoot().getPropertyReferencesTo( element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true );

			if( references.length > 0 ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.garbageCollectIfPossible( references );
				references = element.getRoot().getPropertyReferencesTo( element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true );
			}

			if( references.length > 0 ) {
				for( int i=0; i<references.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Element refReferenceI = references[ i ].getReference();
					edu.cmu.cs.stage3.alice.core.Property refPropertyI = references[ i ].getProperty();
					edu.cmu.cs.stage3.alice.core.Element refOwnerI = refPropertyI.getOwner();
					if( references[ i ].getProperty().isAlsoKnownAs( edu.cmu.cs.stage3.alice.core.Sandbox.class, "textureMaps" ) ) {
						if( refOwnerI instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
							refReferenceI.setParent( refOwnerI );
						}
					}
					//if( references[ i ].getProperty().isAlsoKnownAs( edu.cmu.cs.stage3.alice.core.Sandbox.class, "geometries" ) ) {
					//	System.err.println( "geometries: " + references[ i ] );
					//}
					if( references[ i ].getProperty().isAlsoKnownAs( edu.cmu.cs.stage3.alice.core.Model.class, "geometry" ) ) {
						if( refOwnerI instanceof edu.cmu.cs.stage3.alice.core.Model ) {
							refReferenceI.setParent( refOwnerI );
						}
					}
				}
				references = element.getRoot().getPropertyReferencesTo( element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, true, true );
			}
			if( references.length > 0 ) {
				edu.cmu.cs.stage3.alice.authoringtool.dialog.DeleteContentPane.showDeleteDialog( this, authoringTool );

//				String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element, true ) + " cannot be deleted, because the following references are being made to it or its parts:";
//				javax.swing.JList list = new javax.swing.JList( references );
//				list.setCellRenderer( new edu.cmu.cs.stage3.alice.authoringtool.util.PropertyReferenceListCellRenderer() );
//				javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane( list );
//				list.setVisibleRowCount( 4 );
//				Object[] message = new Object[] { text, scrollPane };
//				edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog( message, "Cannot delete " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element, true ), javax.swing.JOptionPane.ERROR_MESSAGE );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

				if ( element instanceof edu.cmu.cs.stage3.alice.core.Group ){
					for (int i = 0; i < element.getChildCount(); i++){
						if( element.getChildAt(i) instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
							if( element.getChildAt(i) instanceof edu.cmu.cs.stage3.alice.core.Model ) {
								edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)element.getChildAt(i);
								if( model.vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame ) {
									edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = calculateAffectedProperties( model );
									authoringTool.performOneShot( createDestroyResponse( model ), createDestroyUndoResponse( model ), affectedProperties );
								} else {
									model.vehicle.set( null );
								}
							} else {
								((edu.cmu.cs.stage3.alice.core.Transformable)element).vehicle.set( null );
							}
						}
					}
				}

				if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					if( element instanceof edu.cmu.cs.stage3.alice.core.Model ) {
						edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)element;
						if( model.vehicle.get() instanceof edu.cmu.cs.stage3.alice.core.ReferenceFrame ) {
							edu.cmu.cs.stage3.alice.core.Property[] affectedProperties = calculateAffectedProperties( model );
							authoringTool.performOneShot( createDestroyResponse( model ), createDestroyUndoResponse( model ), affectedProperties );
						} else {
							model.vehicle.set( null );
						}
					} else {
						((edu.cmu.cs.stage3.alice.core.Transformable)element).vehicle.set( null );
					}
				}
				edu.cmu.cs.stage3.alice.core.Element parent = element.getParent();
				if( parent != null ) {
					//is this too liberal?
					edu.cmu.cs.stage3.alice.core.Property[] properties = parent.getProperties();
					for( int i = 0; i < properties.length; i++ ) {
						if( properties[i].get() == element ) {
							properties[i].set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultValueForClass( properties[i].getValueClass() ) );
						} else if( properties[i] instanceof edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty ) {
							edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = (edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty)properties[i];
							int j=0;
							while( j<oap.size() ) {
								if( oap.get( j ) == element ) {
									oap.remove( j );
								} else {
									j++;
								}
							}
						}
					}
//					element.removeFromParent();
					parent.removeChild( element );
				}

				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
			}
		}

		protected edu.cmu.cs.stage3.alice.core.Response createDestroyResponse( edu.cmu.cs.stage3.alice.core.Model model ) {
			edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = new edu.cmu.cs.stage3.alice.core.response.TurnAnimation();
			turnAnimation.subject.set( model );
			turnAnimation.direction.set( edu.cmu.cs.stage3.alice.core.Direction.LEFT );
			turnAnimation.amount.set( new Double( 10.0 ) );
			turnAnimation.style.set( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY );
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			opacityAnimation.element.set( model );
			opacityAnimation.propertyName.set( "opacity" );
			opacityAnimation.value.set( new Double( 0.0 ) );
			opacityAnimation.howMuch.set( edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
			opacityAnimation.style.set( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY );
			edu.cmu.cs.stage3.alice.core.response.DoTogether doTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			doTogether.componentResponses.add( turnAnimation );
			doTogether.componentResponses.add( opacityAnimation );
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set( model );
			vehicleAnimation.propertyName.set( "vehicle" );
			vehicleAnimation.value.set( null );
			vehicleAnimation.duration.set( new Double( 0.0 ) );
			vehicleAnimation.howMuch.set( edu.cmu.cs.stage3.util.HowMuch.INSTANCE );
			edu.cmu.cs.stage3.alice.core.response.Wait wait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			wait.duration.set( new Double( .2 ) );
			edu.cmu.cs.stage3.alice.core.response.DoInOrder doInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			doInOrder.componentResponses.add( wait );
			edu.cmu.cs.stage3.alice.core.Element[] heads = model.search( namedHeadCriterion );
			if( (heads != null) && (heads.length > 0) ) {
				edu.cmu.cs.stage3.alice.core.Element head = heads[0];
				if( head instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					edu.cmu.cs.stage3.alice.core.Camera camera = authoringTool.getCurrentCamera();
					if( camera != null ) {
						edu.cmu.cs.stage3.alice.core.response.PointAtAnimation pointAt = new edu.cmu.cs.stage3.alice.core.response.PointAtAnimation();
						pointAt.subject.set( head );
						pointAt.target.set( camera );
						pointAt.duration.set( new Double( .5 ) );
						doInOrder.componentResponses.add( pointAt );
						edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
						wait2.duration.set( new Double( .4 ) );
						doInOrder.componentResponses.add( wait2 );
					}
				}
			}
			doInOrder.componentResponses.add( doTogether );
			doInOrder.componentResponses.add( vehicleAnimation );
			return doInOrder;
		}

		protected edu.cmu.cs.stage3.alice.core.Response createDestroyUndoResponse( edu.cmu.cs.stage3.alice.core.Model model ) {
			edu.cmu.cs.stage3.alice.core.response.TurnAnimation turnAnimation = new edu.cmu.cs.stage3.alice.core.response.TurnAnimation();
			turnAnimation.subject.set( model );
			turnAnimation.direction.set( edu.cmu.cs.stage3.alice.core.Direction.RIGHT );
			turnAnimation.amount.set( new Double( 5.0 ) );
			turnAnimation.style.set( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY );
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation opacityAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			opacityAnimation.element.set( model );
			opacityAnimation.propertyName.set( "opacity" );
			opacityAnimation.value.set( model.opacity.get() );
			opacityAnimation.howMuch.set( edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS ); // won't work correctly if children have different opacities
			opacityAnimation.style.set( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY );
			opacityAnimation.duration.set( new Double( .8 ) );
			edu.cmu.cs.stage3.alice.core.response.DoTogether doTogether = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
			doTogether.componentResponses.add( turnAnimation );
			doTogether.componentResponses.add( opacityAnimation );
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation vehicleAnimation = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			vehicleAnimation.element.set( model );
			vehicleAnimation.propertyName.set( "vehicle" );
			vehicleAnimation.value.set( model.vehicle.get() );
			vehicleAnimation.duration.set( new Double( 0.0 ) );
			vehicleAnimation.howMuch.set( edu.cmu.cs.stage3.util.HowMuch.INSTANCE );
			edu.cmu.cs.stage3.alice.core.response.DoInOrder doInOrder = new edu.cmu.cs.stage3.alice.core.response.DoInOrder();
			doInOrder.componentResponses.add( vehicleAnimation );
			doInOrder.componentResponses.add( doTogether );
			edu.cmu.cs.stage3.alice.core.Element[] heads = model.search( namedHeadCriterion );
			if( (heads != null) && (heads.length > 0) ) {
				edu.cmu.cs.stage3.alice.core.Element head = heads[0];
				if( head instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					edu.cmu.cs.stage3.alice.core.response.Wait wait2 = new edu.cmu.cs.stage3.alice.core.response.Wait();
					wait2.duration.set( new Double( .4 ) );
					doInOrder.componentResponses.add( wait2 );
					edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation povAnimation = new edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation();
					povAnimation.subject.set( head );
					povAnimation.pointOfView.set( ((edu.cmu.cs.stage3.alice.core.Transformable)head).getPointOfView() );
					povAnimation.duration.set( new Double( .5 ) );
					doInOrder.componentResponses.add( povAnimation );
				}
			}
			return doInOrder;
		}

		protected edu.cmu.cs.stage3.alice.core.Property[] calculateAffectedProperties( edu.cmu.cs.stage3.alice.core.Model model ) {
			java.util.Vector properties = new java.util.Vector();
			properties.add( model.localTransformation );
			properties.add( model.vehicle );
			edu.cmu.cs.stage3.alice.core.Element[] descendants = model.getDescendants(); //TODO: getDescendants( HowMuch )
			for( int i = 0; i < descendants.length; i++ ) {
				if( descendants[i] instanceof edu.cmu.cs.stage3.alice.core.Model ) {
					properties.add( ((edu.cmu.cs.stage3.alice.core.Model)descendants[i]).opacity );
					properties.add( ((edu.cmu.cs.stage3.alice.core.Model)descendants[i]).localTransformation );  //HACK: for handling the head-look;  should specific to just the head
				}
			}
			return (edu.cmu.cs.stage3.alice.core.Property[])properties.toArray( new edu.cmu.cs.stage3.alice.core.Property[0] );
		}
	}

	public static class RenameRunnable extends ElementPopupRunnable {
		private javax.swing.JTree jtree;
		private javax.swing.tree.TreePath treePath;

		public RenameRunnable( edu.cmu.cs.stage3.alice.core.Element element, javax.swing.JTree jtree, javax.swing.tree.TreePath treePath ) {
			super( element );
			this.jtree = jtree;
			this.treePath = treePath;
		}

		
		public String getDefaultLabel() {
			return "rename";
		}

		public void run() {
			jtree.startEditingAtPath( treePath );
		}
	}

	public static class MakeCopyRunnable extends ElementPopupRunnable {
		public MakeCopyRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
		}

		
		public String getDefaultLabel() {
			return "make copy";
		}

		public void run() {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

			String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( element.name.getStringValue(), element.getParent() );

			// should createCopyNamed handle this?
			if( element.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CompositeResponse ) {
				int index = ((edu.cmu.cs.stage3.alice.core.response.CompositeResponse)element.getParent()).componentResponses.indexOf( element );
				edu.cmu.cs.stage3.alice.core.Element copy = element.HACK_createCopy( name, element.getParent(), index + 1, null, null );
				((edu.cmu.cs.stage3.alice.core.response.CompositeResponse)element.getParent()).componentResponses.add(index+1, copy);
			} else if( element.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Composite ) {
				int index = ((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)element.getParent()).components.indexOf( element );
				edu.cmu.cs.stage3.alice.core.Element copy = element.HACK_createCopy( name, element.getParent(), index + 1, null, null );
				((edu.cmu.cs.stage3.alice.core.question.userdefined.Composite)element.getParent()).components.add(index+1, copy);
			} else {
				edu.cmu.cs.stage3.alice.core.Element copy = element.createCopyNamed( name );
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty( copy, copy.getParent() );
			}

			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
		}
	}

	public static class MakeSharedCopyRunnable extends ElementPopupRunnable {
		protected Class[] classesToShare = {
			edu.cmu.cs.stage3.alice.core.Geometry.class,
			edu.cmu.cs.stage3.alice.core.Sound.class,
			edu.cmu.cs.stage3.alice.core.TextureMap.class
		};

		public MakeSharedCopyRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
		}

		
		public String getDefaultLabel() {
			return "make shared copy";
		}

		public void run() {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().startCompound();

			String name = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( element.name.getStringValue(), element.getParent() );
			edu.cmu.cs.stage3.alice.core.Element copy = element.createCopyNamed( name, classesToShare );
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.addElementToAppropriateProperty( copy, copy.getParent() );

			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getUndoRedoStack().stopCompound();
		}
	}

	public static class PrintStatisticsRunnable extends ElementPopupRunnable {
		public PrintStatisticsRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
		}

		
		public String getDefaultLabel() {
			return "print statistics";
		}

		public void run() {
			edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter itaCounter = new edu.cmu.cs.stage3.alice.core.util.IndexedTriangleArrayCounter();
			edu.cmu.cs.stage3.alice.core.util.TextureMapCounter textureMapCounter = new edu.cmu.cs.stage3.alice.core.util.TextureMapCounter();

			element.visit( itaCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );
			element.visit( textureMapCounter, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS );

			System.out.println( "Statistics for " + edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element ) + ":" );
			System.out.println( "  object count: " + itaCounter.getShownIndexedTriangleArrayCount() );
			System.out.println( "    face count: " + itaCounter.getShownIndexCount() / 3 );
			System.out.println( "  vertex count: " + itaCounter.getShownVertexCount() );
			System.out.println( " texture count: " + textureMapCounter.getTextureMapCount() );
			System.out.println( "texture memory: " + textureMapCounter.getTextureMapMemoryCount() + " bytes" );
		}
	}

	public static class SaveCharacterRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public SaveCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
		}

		public SaveCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "save object...";
		}

		public void run() {
			authoringTool.saveCharacter( element );
//			final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
//				public Object construct() {
//					return new Integer( authoringTool.saveCharacter( element ) );
//				}
//			};
//			worker.start();
		}
	}

	/*
	public static class EditCharacterRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public EditCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
		}

		public EditCharacterRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		public String getDefaultLabel() {
			return "edit character...";
		}

		public void run() {
			authoringTool.editCharacter( (edu.cmu.cs.stage3.alice.core.Transformable)element );
		}
	}
	*/

	public static class ToggleCommentingRunnable extends ElementPopupRunnable {
		public ToggleCommentingRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
			if( ! (element instanceof edu.cmu.cs.stage3.alice.core.Code) ) {
				throw new IllegalArgumentException( "ToggleCommentRunnable only accepts Responses or User-Defined Questions; found: " + element );
			}
		}

		
		public String getDefaultLabel() {
			edu.cmu.cs.stage3.alice.core.Code code = (edu.cmu.cs.stage3.alice.core.Code)element;
			if( code.isCommentedOut.booleanValue() ) {
				return "enable";
			} else {
				return "disable";
			}
		}

		public void run() {
			edu.cmu.cs.stage3.alice.core.Code code = (edu.cmu.cs.stage3.alice.core.Code)element;
			code.isCommentedOut.set( code.isCommentedOut.booleanValue() ? Boolean.FALSE : Boolean.TRUE );
		}
	}

	public static class SetElementScopeRunnable	extends ElementPopupRunnable {
		private edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public SetElementScopeRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "switch to this element's scope";
		}

		public void run() {
			authoringTool.setElementScope( element );
		}
	}

	public static class StorePoseRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public StorePoseRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
		}

		public StorePoseRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "capture pose";
		}

		public void run() {
			if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable)element; 
				edu.cmu.cs.stage3.alice.core.Pose pose = edu.cmu.cs.stage3.alice.core.Pose.manufacturePose( transformable, transformable );
				pose.name.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( "pose", element ) );
				element.addChild( pose );
				transformable.poses.add( pose );
			}
		}
	}

	public static class EditScriptRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public EditScriptRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
		}

		public EditScriptRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "edit script";
		}

		public void run() {
			if( element instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
				authoringTool.editObject( ((edu.cmu.cs.stage3.alice.core.Sandbox)element).script );
			}
		}
	}

//	public static class CopyOverFromImportLoadRunnable extends ElementPopupRunnable {
//		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
//
//		public CopyOverFromImportLoadRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
//			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
//		}
//
//		public CopyOverFromImportLoadRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
//			super( element );
//			this.authoringTool = authoringTool;
//		}
//
//		public String getDefaultLabel() {
//			return "from ASE";
//		}
//
//		public void run() {
//			authoringTool.copyOverFromImportLoad( element );
////			final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
////				public Object construct() {
////					authoringTool.copyOverFromImportLoad( element );
////					return null;
////				}
////			};
////			worker.start();
//		}
//	}
//
//	public static class CopyOverFromCharacterLoadRunnable extends ElementPopupRunnable {
//		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;
//
//		public CopyOverFromCharacterLoadRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
//			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
//		}
//
//		public CopyOverFromCharacterLoadRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
//			super( element );
//			this.authoringTool = authoringTool;
//		}
//
//		public String getDefaultLabel() {
//			return "from object";
//		}
//
//		public void run() {
//			final edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
//				public Object construct() {
//					authoringTool.copyOverFromCharacterLoad( element );
//					return null;
//				}
//			};
//			worker.start();
//		}
//	}

	public static class GetAGoodLookAtRunnable extends ElementPopupRunnable {
		protected edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool;

		public GetAGoodLookAtRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			this( element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack() );
		}

		public GetAGoodLookAtRunnable( edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool ) {
			super( element );
			this.authoringTool = authoringTool;
		}

		
		public String getDefaultLabel() {
			return "Camera get a good look at this";
		}

		public void run() {
			if( authoringTool.getCurrentCamera() instanceof edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera ) {
				if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
					authoringTool.getAGoodLookAt( (edu.cmu.cs.stage3.alice.core.Transformable)element, (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera)authoringTool.getCurrentCamera() );
				} else {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Can't get a good look: element is not a Transformable", null );
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Can't get a good look: camera is not symmetric perspective", null );
			}
		}
	}

	public static class SortGroupAlphabeticallyRunnable extends ElementPopupRunnable {
		protected java.util.Comparator sorter = new java.util.Comparator() {
			public int compare( Object o1, Object o2 ) {
				if( (o1 instanceof edu.cmu.cs.stage3.alice.core.Element) && (o2 instanceof edu.cmu.cs.stage3.alice.core.Element) ) {
					String name1 = ((edu.cmu.cs.stage3.alice.core.Element)o1).name.getStringValue();
					String name2 = ((edu.cmu.cs.stage3.alice.core.Element)o2).name.getStringValue();
					return name1.compareTo( name2 );
				} else {
					return 0;
				}
			}
		};

		public SortGroupAlphabeticallyRunnable( edu.cmu.cs.stage3.alice.core.Element element ) {
			super( element );
		}

		
		public String getDefaultLabel() {
			return "sort alphabetically";
		}

		public void run() {
			if( element instanceof edu.cmu.cs.stage3.alice.core.Group ) {
				edu.cmu.cs.stage3.alice.core.Group group = (edu.cmu.cs.stage3.alice.core.Group)element;
				Object[] values = group.values.getArrayValue();
				java.util.Arrays.sort( values, sorter );
				group.values.clear(); //HACK; shouldn't have to do this
				group.values.set( values );
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to sort " + element + " alphabetically because it is not a Group.", null );
			}
		}
	}
}