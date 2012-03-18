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

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.util.StringObjectPair;

/**
 * @author Jason Pratt
 */
public class PopupMenuUtilities {
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.class.getPackage() );

	protected static java.util.HashMap recentlyUsedValues = new java.util.HashMap();

	protected static java.util.Hashtable runnablesToActionListeners = new java.util.Hashtable();
	public final static PopupItemFactory oneShotFactory = new PopupItemFactory() {
		public Object createItem( final Object o ) {
			return new Runnable() {
				public void run() {
					if( o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype ) {
						edu.cmu.cs.stage3.alice.core.Response response = ((edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype)o).createNewResponse();
						edu.cmu.cs.stage3.alice.core.Response undoResponse = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.createUndoResponse( response );
						edu.cmu.cs.stage3.alice.core.Property[] properties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getAffectedProperties( response );
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().performOneShot( response, undoResponse, properties );
					}
				}
			};
		}
	};

	public final static javax.swing.Icon currentValueIcon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( "currentValue" );
	public final static Object NO_CURRENT_VALUE = new Object();

	protected static edu.cmu.cs.stage3.util.Criterion isNamedElement = new edu.cmu.cs.stage3.util.Criterion() {
		public boolean accept( Object o ) {
			if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				if( ((edu.cmu.cs.stage3.alice.core.Element)o).name.get() != null ) {
					return true;
				}
			}
			return false;
		}
	};
	//protected static edu.cmu.cs.stage3.util.NotCriterion isNotActualParameter = new edu.cmu.cs.stage3.util.NotCriterion( new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.ActualParameter.class ) );
	protected static edu.cmu.cs.stage3.util.Criterion isNotActualParameter = new edu.cmu.cs.stage3.util.Criterion() {
		public boolean accept( Object o ) {
			if( o instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
				edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)o;
				if( variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse ) {
					edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse callToUserDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse)variable.getParent();
					if( callToUserDefinedResponse.requiredActualParameters.contains( variable ) ) {
						return false;
					} else if( callToUserDefinedResponse.keywordActualParameters.contains( variable ) ) {
						return false;
					}
				} else if( variable.getParent() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion ) {
					edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion callToUserDefinedQuestion = (edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion)variable.getParent();
					if( callToUserDefinedQuestion.requiredActualParameters.contains( variable ) ) {
						return false;
					} else if( callToUserDefinedQuestion.keywordActualParameters.contains( variable ) ) {
						return false;
					}
				}
			}

			return true;
		}
	};

	protected static java.util.HashMap specialStringMap = new java.util.HashMap();

	static {
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled( false );  // since we mix heavy and lightweight components

		specialStringMap.put( "<keyCode>", "a key" );
		specialStringMap.put( "<keyCode>", "a key" );
		specialStringMap.put( "<mouse>", "the mouse" );
		specialStringMap.put( "<onWhat>", "something" );
		specialStringMap.put( "<condition>", "something" );
		specialStringMap.put( "<variable>", "a variable" );
		specialStringMap.put( "<arrowKeys>", "the arrow keys" );
		specialStringMap.put( "<button>", "" );
	}

	public static void addRecentlyUsedValue( Class valueClass, Object value ) {
		if( ! recentlyUsedValues.containsKey( valueClass ) ) {
			recentlyUsedValues.put( valueClass, new java.util.ArrayList() );
		}
		java.util.List recentList = (java.util.List)recentlyUsedValues.get( valueClass );
		while( recentList.contains( value ) ) {
			recentList.remove( value );
		}
		recentList.add( 0, value );
	}

	public static void clearRecentlyUsedValues() {
		recentlyUsedValues.clear();
	}

	public static void createAndShowPopupMenu( Vector structure, java.awt.Component component, int x, int y ) {
		javax.swing.JPopupMenu popup = makePopupMenu( structure );
		popup.show( component, x, y );
		ensurePopupIsOnScreen( popup );
	}

	public static JPopupMenu makePopupMenu( Vector structure ) {
		AliceMenuWithDelayedPopup menu = makeMenu( "", structure );
		if (menu != null){
			return menu.getPopupMenu();
		}
		return null;
	}

//	public static JMenu makeMenu( String title, Vector structure ) {
//		if( structure == null || structure.isEmpty() ) {
//			return null;
//		} else {
//			JMenu menu = new AliceMenu( title );
////			JMenu menu = new JMenu( title );
//			menu.setUI( new AliceMenuUI() );
//			menu.setDelay( 0 );
//
//			for( java.util.Enumeration enum0. = structure.elements(); enum0.hasMoreElements(); ) {
//				Object o = enum0.nextElement();
//				if( !(o instanceof StringObjectPair) ) {
//					throw new IllegalArgumentException( "structure must be made only of StringObjectPairs, found: " + o );
//				}
//
//				StringObjectPair pair = (StringObjectPair)o;
//				String name = pair.getString();
//
//				//hack
//				if( name != null ) {
//					for( java.util.Iterator iter = specialStringMap.keySet().iterator(); iter.hasNext(); ) {
//						String s = (String)iter.next();
//						if( name.indexOf( s ) > -1 ) {
//							StringBuffer sb = new StringBuffer( name );
//							sb.replace( name.indexOf( s ), name.indexOf( s ) + s.length(), (String)specialStringMap.get( s ) );
//							name = sb.toString();
//						}
//					}
//				}
//
//				Object content = pair.getObject();
//				if( content instanceof java.util.Vector ) {
//					JMenu submenu = makeMenu( name, (java.util.Vector)content );
//					if( submenu != null ) {
//						menu.add( submenu );
//					}
//				} else if( content instanceof java.awt.event.ActionListener ) {
//					JMenuItem menuItem = makeMenuItem( name );
//					menuItem.addActionListener( (java.awt.event.ActionListener)content );
//					menu.add( menuItem );
//				} else if( content instanceof Runnable ) {
//					JMenuItem menuItem = makeMenuItem( name );
//					java.awt.event.ActionListener listener = (java.awt.event.ActionListener)runnablesToActionListeners.get( content );
//					if( listener == null ) {
//						listener = new PopupMenuItemActionListener( (Runnable)content, menu /*MEMFIX*/ );
//						//MEMFIX runnablesToActionListeners.put( content, listener );
//					}
//					menuItem.addActionListener( listener );
//					menu.add( menuItem );
//				} else if( content == javax.swing.JSeparator.class ) {
//					menu.addSeparator();
//				} else if( content instanceof java.awt.Component ) {
//					menu.add( (java.awt.Component)content );
//				} else if( content == null ) {
//					javax.swing.JLabel label = new javax.swing.JLabel( name );
//					label.setBorder( javax.swing.BorderFactory.createEmptyBorder( 1, 4, 1, 4 ) );
//					menu.add( label );
//				}
//			}
//
//			return menu;
//		}
//	}

	public static AliceMenuWithDelayedPopup makeMenu( String title, Vector structure ) {
		if( structure == null || structure.isEmpty() ) {
			return null;
		} else {
			AliceMenuWithDelayedPopup menu = new AliceMenuWithDelayedPopup( title, structure );
			menu.setUI( new AliceMenuUI() );
			menu.setDelay( 0 );
			return menu;
		}
	}

	public static void populateDelayedMenu( AliceMenuWithDelayedPopup menu, Vector structure ) {
		for( java.util.Enumeration enum0 = structure.elements(); enum0.hasMoreElements(); ) {
			Object o = enum0.nextElement();
			if( !(o instanceof StringObjectPair) ) {
				throw new IllegalArgumentException( "structure must be made only of StringObjectPairs, found: " + o );
			}

			StringObjectPair pair = (StringObjectPair)o;
			String name = pair.getString();

			//hack
			if( name != null ) {
				for( java.util.Iterator iter = specialStringMap.keySet().iterator(); iter.hasNext(); ) {
					String s = (String)iter.next();
					if( name.indexOf( s ) > -1 ) {
						StringBuffer sb = new StringBuffer( name );
						sb.replace( name.indexOf( s ), name.indexOf( s ) + s.length(), (String)specialStringMap.get( s ) );
						name = sb.toString();
					}
				}
			}

			Object content = pair.getObject();
			if( content instanceof DelayedBindingPopupItem ) {
				content = ((DelayedBindingPopupItem)content).createItem();
			}

			javax.swing.Icon icon = null;
			if( content instanceof PopupItemWithIcon ) {
				icon = ((PopupItemWithIcon)content).getIcon();
				content = ((PopupItemWithIcon)content).getItem();
			}

			if( content instanceof java.util.Vector ) {
				JMenu submenu = makeMenu( name, (java.util.Vector)content );
				if( submenu != null ) {
					menu.add( submenu );
				}
			} else if( content instanceof java.awt.event.ActionListener ) {
				JMenuItem menuItem = makeMenuItem( name, icon );
				menuItem.addActionListener( (java.awt.event.ActionListener)content );
				menu.add( menuItem );
			} else if( content instanceof Runnable ) {
				JMenuItem menuItem = makeMenuItem( name, icon );
				java.awt.event.ActionListener listener = (java.awt.event.ActionListener)runnablesToActionListeners.get( content );
				if( listener == null ) {
					listener = new PopupMenuItemActionListener( (Runnable)content, menu /*MEMFIX*/ );
					//MEMFIX runnablesToActionListeners.put( content, listener );
				}
				menuItem.addActionListener( listener );
				menu.add( menuItem );
			} else if( content == javax.swing.JSeparator.class ) {
				menu.addSeparator();
			} else if( content instanceof java.awt.Component ) {
				menu.add( (java.awt.Component)content );
			} else if( content == null ) {
				javax.swing.JLabel label = new javax.swing.JLabel( name );
				label.setBorder( javax.swing.BorderFactory.createEmptyBorder( 1, 4, 1, 4 ) );
				menu.add( label );
			}
		}
	}


//	public static JPopupMenu makePopupMenu( edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback, Object context, Vector structure ) {
//		return makeMenu( "", callback, context, structure ).getPopupMenu();
//	}
//
//	public static JMenu makeMenu( String title, edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback, Object context, Vector structure ) {
//		if( structure == null || structure.isEmpty() ) {
//			return null;
//		} else {
//			JMenu menu = new JMenu( title );
//
//			for( java.util.Enumeration enum0. = structure.elements(); enum0.hasMoreElements(); ) {
//				Object o = enum0.nextElement();
//				if( !(o instanceof StringObjectPair) ) {
//					throw new IllegalArgumentException( "structure must be made only of StringObjectPairs" );
//				}
//
//				StringObjectPair pair = (StringObjectPair)o;
//				String name = pair.getString();
//				Object content = pair.getObject();
//				if( content instanceof java.util.Vector ) {
//					JMenu submenu = makeMenu( name, callback, context, (java.util.Vector)content );
//					if( submenu != null ) {
//						menu.add( submenu );
//					}
//				} else if( content == javax.swing.JSeparator.class ) {
//					menu.addSeparator();
//				} else {
//					JMenuItem menuItem = makeMenuItem( name );
//					java.awt.event.ActionListener listener = new PopupMenuItemCallbackActionListener( callback, context, content );
//					menuItem.addActionListener( listener );
//					menu.add( menuItem );
//				}
//			}
//
//			return menu;
//		}
//	}

	public static JMenuItem makeMenuItem( String text ) {
		return makeMenuItem( text, null );
	}
	public static JMenuItem makeMenuItem( String text, javax.swing.Icon icon ) {
		JMenuItem item;
		if( icon != null ) {
			item = new JMenuItem( text, icon );
		} else {
			item = new JMenuItem( text );
		}
		item.setUI( new AliceMenuItemUI() );
		return item;
	}

	public static boolean isStringInStructure( String s, java.util.Vector structure ) {
		for( java.util.Enumeration enum0 = structure.elements(); enum0.hasMoreElements(); ) {
			StringObjectPair pair = (StringObjectPair)enum0.nextElement();
			String string = pair.getString();
			Object content = pair.getObject();
			if( string == s ) {
				return true;
			}
			else if( content instanceof java.util.Vector ) {
				if( isStringInStructure( s, (java.util.Vector)content ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isObjectInStructure( Object o, java.util.Vector structure ) {
		for( java.util.Enumeration enum0 = structure.elements(); enum0.hasMoreElements(); ) {
			StringObjectPair pair = (StringObjectPair)enum0.nextElement();
			Object content = pair.getObject();
			if( content == o ) {
				return true;
			}
			else if( content instanceof java.util.Vector ) {
				if( isObjectInStructure( o, (java.util.Vector)content ) ) {
					return true;
				}
			}
		}
		return false;
	}

	public static java.util.Vector makeElementStructure( Element root, final edu.cmu.cs.stage3.util.Criterion criterion, final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context, final Object currentValue ) {
		DelayedBindingPopupItem delayedBindingPopupItem;
		java.util.Vector structure = new java.util.Vector();
		//System.out.println("making structure: root: "+root+", context: "+context);
		if( criterion.accept( root ) ) {
			if( root.equals( currentValue ) ) {
				structure.addElement( new StringObjectPair(  "the entire " + (String)root.name.getValue(), new PopupItemWithIcon( factory.createItem( root ), currentValueIcon ) ) );
			} else {
				structure.addElement( new StringObjectPair( "the entire " + (String)root.name.getValue(), factory.createItem( root ) ) );
			}
			if( root.getChildCount() > 0 ) {
				Element [] children = root.getChildren();
				for( int i = 0; i < children.length; i++ ) {
					final Element child = children[i];
					if( child.getChildCount() == 0 ) {
						if( criterion.accept( child ) ) {
							if( child.equals( currentValue ) ) {
								structure.addElement( new StringObjectPair( (String)child.name.getValue(), new PopupItemWithIcon( factory.createItem( child ), currentValueIcon ) ) );
							} else {
								structure.addElement( new StringObjectPair( (String)child.name.getValue(), factory.createItem( child ) ) );
							}
						}
					} else {
						if( (child.search( criterion ).length > 0) || criterion.accept( child ) ) {
							delayedBindingPopupItem = new DelayedBindingPopupItem() {
								public Object createItem() {
									java.util.Vector subStructure = makeElementStructure( child, criterion, factory, context, currentValue );
									if( (subStructure.size() == 1) && criterion.accept( child ) ) {
										if( child.equals( currentValue ) ) {
											return new PopupItemWithIcon( factory.createItem( child ), currentValueIcon );
										} else {
											return factory.createItem( child );
										}
									} else {
										return subStructure;
									}
								}
							};
							structure.addElement( new StringObjectPair( (String)child.name.getValue(), delayedBindingPopupItem ) );
						}
					}
				}

				if( structure.size() > 1 ) {
					structure.insertElementAt( new StringObjectPair( "Separator", javax.swing.JSeparator.class ), 1 );
				}
			}
		} else {
			Element[] children = root.getChildren();
			for( int i = 0; i < children.length; i++ ) {
				final Element child = children[i];
				if( child.getChildCount() == 0 ) {
					if( criterion.accept( child ) ) {
						if( child.equals( currentValue ) ) {
							structure.addElement( new StringObjectPair( (String)child.name.getValue(), new PopupItemWithIcon( factory.createItem( child ), currentValueIcon ) ) );
						} else {
							structure.addElement( new StringObjectPair( (String)child.name.getValue(), factory.createItem( child ) ) );
						}
					}
				} else {
					if( (child.search( criterion ).length > 0) || criterion.accept( child ) ) {
						delayedBindingPopupItem = new DelayedBindingPopupItem() {
							public Object createItem() {
								java.util.Vector subStructure = makeElementStructure( child, criterion, factory, context, currentValue );
								if( (subStructure.size() == 1) && criterion.accept( child ) ) {
									if( child.equals( currentValue ) ) {
										return new PopupItemWithIcon( factory.createItem( child ), currentValueIcon );
									} else {
										return factory.createItem( child );
									}
								} else {
									return subStructure;
								}
							}
						};
						structure.addElement( new StringObjectPair( (String)child.name.getValue(), delayedBindingPopupItem ) );
					}
				}
			}
		}

		return structure;
	}

	public static java.util.Vector makeFlatElementStructure( Element root, edu.cmu.cs.stage3.util.Criterion criterion, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context, Object currentValue ) {
		java.util.Vector structure = new java.util.Vector();

		edu.cmu.cs.stage3.alice.core.Element[] elements = root.search( criterion );
		for( int i = 0; i < elements.length; i++ ) {
			String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( elements[i] );
			if( context != null ) {
				text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameInContext( elements[i], context );
			}
			if( elements[i].equals( currentValue ) ) {
				structure.addElement( new StringObjectPair( text, new PopupItemWithIcon( factory.createItem( elements[i] ), currentValueIcon ) ) );
			} else {
				structure.addElement( new StringObjectPair( text, factory.createItem( elements[i] ) ) );
			}
		}

		return structure;
	}

//	public static java.util.Vector makeFlatExpressionStructure( Class valueClass, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
//		java.util.Vector structure = new java.util.Vector();
//
//		if( context != null ) {
//			edu.cmu.cs.stage3.alice.core.Expression[] expressions = context.findAccessibleExpressions( valueClass );
//			for( int i = 0; i < expressions.length; i++ ) {
//				String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( expressions[i] );
//				if( context != null ) {
//					text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameInContext( expressions[i], context );
//				}
//				if( expressions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
//					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype prototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype( (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)expressions[i] );
//					if( prototype.getDesiredProperties().length > 0 ) {
//						structure.addElement( new StringObjectPair( text, makePrototypeStructure( prototype, factory, context ) ) );
//					} else {
//						structure.addElement( new StringObjectPair( text, factory.createItem( prototype.createNewElement() ) ) );
//					}
//				} else {
//					structure.addElement( new StringObjectPair( text, factory.createItem( expressions[i] ) ) );
//				}
//			}
//		}
//
//		return structure;
//	}
	public static java.util.Vector makeFlatExpressionStructure( Class valueClass, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context, Object currentValue ) {
		return makeFlatExpressionStructure( valueClass, null, factory, context, currentValue );
	}

	public static java.util.Vector makeFlatExpressionStructure( Class valueClass, edu.cmu.cs.stage3.util.Criterion criterion, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context, Object currentValue ) {
		java.util.Vector structure = new java.util.Vector();
		if( context != null ) {
			edu.cmu.cs.stage3.alice.core.Expression[] expressions = context.findAccessibleExpressions( valueClass );
			for( int i = 0; i < expressions.length; i++ ) {
				if( (criterion == null) || criterion.accept( expressions[i] ) ) {
					String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( expressions[i] );
					if( context != null ) {
						text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameInContext( expressions[i], context );
					}
					if( expressions[i] instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion ) {
						edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype prototype = new edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype( (edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion)expressions[i] );
						if( prototype.getDesiredProperties().length > 0 ) {
							structure.addElement( new StringObjectPair( text, makePrototypeStructure( prototype, factory, context ) ) );
						} else {
							structure.addElement( new StringObjectPair( text, factory.createItem( prototype.createNewElement() ) ) );
						}
					} else {
						if( expressions[i].equals( currentValue ) ) {
							structure.addElement( new StringObjectPair( text, new PopupItemWithIcon( factory.createItem( expressions[i] ), currentValueIcon ) ) );
						} else {
							structure.addElement( new StringObjectPair( text, factory.createItem( expressions[i] ) ) );
						}
					}
				}
			}
		}

		return structure;
	}

	public static java.util.Vector makeElementAndExpressionStructure( Element root, Class valueClass, PopupItemFactory factory, boolean makeFlat, edu.cmu.cs.stage3.alice.core.Element context, Object currentValue ) {
		java.util.Vector structure;
		if( makeFlat ) {
			structure = makeFlatElementStructure( root, new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( valueClass ), factory, context, currentValue );
		} else {
			structure = makeElementStructure( root, new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( valueClass ), factory, context, currentValue );
		}

		java.util.Vector expressionStructure = makeFlatExpressionStructure( valueClass, factory, context, currentValue );
		if( (expressionStructure != null) && (expressionStructure.size() > 0) ) {
			String className = valueClass.getName();
			if( structure.size() > 0 ) {
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
			}
			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "expressions", expressionStructure ) );
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ResponsePrototype and return a Runnable
	 */
	public static java.util.Vector makeResponseStructure( final edu.cmu.cs.stage3.alice.core.Element element, final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();
		Class valueClass = element.getClass();
		if( element instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
		}

		java.util.Vector oneShotStructure = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getOneShotStructure( valueClass );
		if( (oneShotStructure != null) && (oneShotStructure.size() > 0) ) {
			boolean isFirst = true;
			String[] groupsToUse = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getOneShotGroupsToInclude();
			for (int i=0; i< oneShotStructure.size(); i++){
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)oneShotStructure.get( i ); // pull off first group, usually "common animations"
				String currentGroupName = sop.getString();
				boolean useIt = false;
				for (int j=0; j<groupsToUse.length; j++){
					if (currentGroupName.compareTo(groupsToUse[j]) == 0){
						useIt = true;
						continue;
					}
				}
				if (useIt){
					if (!isFirst){
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "separator", javax.swing.JSeparator.class ) );
					}
					else{
						isFirst = false;
					}
					java.util.Vector responseNames = (java.util.Vector)sop.getObject();
					structure.addAll( makeOneShotStructure( responseNames, element, factory, context ) );
				}
			}
			if( element instanceof edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization) {
				final edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.CollectionOfModelsVisualization)element;
					final edu.cmu.cs.stage3.alice.core.Collection collection = visualization.getItemsCollection();
					if( (collection instanceof edu.cmu.cs.stage3.alice.core.List) || (collection instanceof edu.cmu.cs.stage3.alice.core.Array) ) {
						if( (collection != null) && (collection.values.size() > 0) && edu.cmu.cs.stage3.alice.core.Model.class.isAssignableFrom( collection.valueClass.getClassValue() ) ) {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
								public Object createItem() {
									java.util.Vector subStructure = new java.util.Vector();
									edu.cmu.cs.stage3.alice.core.Question question = null;
									Object[] items = collection.values.getArrayValue();
									if (collection instanceof edu.cmu.cs.stage3.alice.core.List){
										//Item at beginning
										question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning();
										((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning)question).subject.set( visualization );
										java.util.Vector responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
										subStructure.add( new StringObjectPair( "item at the beginning", responseStructure ) );
										
										//Item at index
										for( int i = 0; i < items.length; i++ ) {
											question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex();
											((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex)question).subject.set( visualization );
											((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex)question).index.set( new Double( i ));
											responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
											subStructure.add( new StringObjectPair( "item "+i, responseStructure ) );
										}
										
										//Item at end
										question = new edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd();
										((edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd)question).subject.set( visualization );
										responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
										subStructure.add( new StringObjectPair( "item at the end", responseStructure ) );
									}
									else if( collection instanceof edu.cmu.cs.stage3.alice.core.Array ) {
										for( int i = 0; i < items.length; i++ ) {
											question = new edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex();
											((edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex)question).subject.set( visualization );
											((edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex)question).index.set( new Double( i ));
											java.util.Vector responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
											subStructure.add( new StringObjectPair( "element "+i, responseStructure ) );
										}
									}
									return subStructure;
								}
							};
							structure.add( new StringObjectPair( "item responses", delayedBindingPopupItem ) );
						}
				}
			}
			if( element instanceof edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization) {
				edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization visualization = (edu.cmu.cs.stage3.alice.core.visualization.ModelVisualization)element;
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				edu.cmu.cs.stage3.alice.core.Question question = new edu.cmu.cs.stage3.alice.core.question.visualization.model.Item();
				((edu.cmu.cs.stage3.alice.core.question.visualization.model.Item)question).subject.set( visualization );
				java.util.Vector responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
				structure.add( new StringObjectPair( "item", responseStructure ) );
			}
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ResponsePrototype and return a Runnable
	 */
	public static java.util.Vector makeExpressionResponseStructure( final edu.cmu.cs.stage3.alice.core.Expression expression, final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		// if it's a variable, allow user to set it
		if( expression instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
			edu.cmu.cs.stage3.util.StringObjectPair[] known = { new edu.cmu.cs.stage3.util.StringObjectPair( "element", expression ), new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", "value" ), new edu.cmu.cs.stage3.util.StringObjectPair( "duration", new Integer( 0 ) ) };
			String[] desired = { "value" };
			edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype rp = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype( edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, known, desired );
			java.util.Vector setValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure( rp, factory, context );
			if( (setValueStructure != null) && (! setValueStructure.isEmpty()) ) {
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "set value", setValueStructure ) );
			}
		}

		// cascade to appropriate responses
		java.util.Vector oneShotStructure = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getOneShotStructure( expression.getValueClass() );
		if( (oneShotStructure != null) && (oneShotStructure.size() > 0) ) {
			boolean isFirst = true;
			String[] groupsToUse = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getOneShotGroupsToInclude();
			for (int i=0; i< oneShotStructure.size(); i++){
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)oneShotStructure.get( i ); // pull off first group, usually "common animations"
				String currentGroupName = sop.getString();
				boolean useIt = false;
				for (int j=0; j<groupsToUse.length; j++){
					if (currentGroupName.compareTo(groupsToUse[j]) == 0){
						useIt = true;
						continue;
					}
				}
				if (useIt){
					if (!isFirst){
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "separator", javax.swing.JSeparator.class ) );
					}
					else{
						isFirst = false;
					}
					java.util.Vector responseNames = (java.util.Vector)sop.getObject();
					java.util.Vector subStructure = makeOneShotStructure( responseNames, expression, factory, context );

					if( subStructure.size() > 0 ) {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						structure.addAll( subStructure );
					}
				//	structure.addAll( makeOneShotStructure( responseNames, expression, factory, context ) );
				}
			}
			
		}

		if( expression instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
			edu.cmu.cs.stage3.alice.core.Variable variable = (edu.cmu.cs.stage3.alice.core.Variable)expression;
			if( edu.cmu.cs.stage3.alice.core.Collection.class.isAssignableFrom( expression.getValueClass() ) ) {
				final edu.cmu.cs.stage3.alice.core.Collection collection = (edu.cmu.cs.stage3.alice.core.Collection)variable.value.get();
				if( (collection instanceof edu.cmu.cs.stage3.alice.core.List) || (collection instanceof edu.cmu.cs.stage3.alice.core.Array) ) {
					if( (collection != null) && (collection.values.size() > 0) && edu.cmu.cs.stage3.alice.core.Model.class.isAssignableFrom( collection.valueClass.getClassValue() ) ) {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						DelayedBindingPopupItem delayedBindingPopupItem = new DelayedBindingPopupItem() {
							public Object createItem() {
								java.util.Vector subStructure = new java.util.Vector();
								Object[] items = collection.values.getArrayValue();
								for( int i = 0; i < items.length; i++ ) {
									edu.cmu.cs.stage3.alice.core.Question question = null;
									if( collection instanceof edu.cmu.cs.stage3.alice.core.List ) {
										question = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex)question).list.set( expression );
										((edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex)question).index.set( new Double( i ) );
									} else if( collection instanceof edu.cmu.cs.stage3.alice.core.Array ) {
										question = new edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex();
										((edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex)question).array.set( expression );
										((edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex)question).index.set( new Double( i ) );
									}
									java.util.Vector responseStructure = PopupMenuUtilities.makeResponseStructure( question, factory, context );
									subStructure.add( new StringObjectPair( "item" + i, responseStructure ) );
								}
								return subStructure;
							}
						};
						structure.add( new StringObjectPair( "item responses", delayedBindingPopupItem ) );
					}
				}
			}
		}

		return structure;
	}

	public static java.util.Vector makeOneShotStructure( java.util.Vector responseNames, edu.cmu.cs.stage3.alice.core.Element element, PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();
		if( responseNames != null ) {
			int i = 0;
			for( java.util.Iterator iter = responseNames.iterator(); iter.hasNext(); ) {
				Object item = iter.next();
				if( item instanceof String ) {
					String className = (String)item;
					try {
						if( className.startsWith( "edu.cmu.cs.stage3.alice.core.response.PropertyAnimation" ) ) {
							String propertyName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getSpecifier( className );
							if( propertyName.equals( "vehicle" ) ) {
								edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = new edu.cmu.cs.stage3.util.StringObjectPair[] {
									new edu.cmu.cs.stage3.util.StringObjectPair( "element", element ),
									new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", propertyName ),
									new edu.cmu.cs.stage3.util.StringObjectPair( "duration", new Double( 0.0 ) ),
								};
								String[] desiredProperties = new String[] { "value" };
								edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype( edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class, knownPropertyValues, desiredProperties );
								String responseName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormattedReprForValue( edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, knownPropertyValues );
								java.util.Vector subStructure = makePrototypeStructure( responsePrototype, factory, context );
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( responseName, subStructure ) );
							} else {
								edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = new edu.cmu.cs.stage3.util.StringObjectPair[] {
									new edu.cmu.cs.stage3.util.StringObjectPair( "element", element ),
									new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", propertyName )
								};
								String[] desiredProperties = new String[] { "value" };
								edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype( edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, knownPropertyValues, desiredProperties );
								String responseName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormattedReprForValue( edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class, knownPropertyValues );
								java.util.Vector subStructure = makePrototypeStructure( responsePrototype, factory, context );
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( responseName, subStructure ) );
							}
						} else {
							Class responseClass = Class.forName( className );
							java.util.LinkedList known = new java.util.LinkedList();
							String format = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormat( responseClass );
							edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer( format );
							while( tokenizer.hasMoreTokens() ) {
								String token = tokenizer.nextToken();
							//	System.out.println("token: "+token);
								if( token.startsWith( "<<<" ) && token.endsWith( ">>>" ) ) {
									String propertyName = token.substring( token.lastIndexOf( "<" ) + 1, token.indexOf( ">" ) );
									//System.out.println("property name: "+propertyName+", element "+element);
									known.add( new edu.cmu.cs.stage3.util.StringObjectPair( propertyName, element ) );
								}
							}
							edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = (edu.cmu.cs.stage3.util.StringObjectPair[])known.toArray( new edu.cmu.cs.stage3.util.StringObjectPair[0] );
							String[] desiredProperties = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDesiredProperties( responseClass );
							edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype responsePrototype = new edu.cmu.cs.stage3.alice.authoringtool.util.ResponsePrototype( responseClass, knownPropertyValues, desiredProperties );
							String responseName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getFormattedReprForValue( responseClass, knownPropertyValues );
			
							if( responsePrototype.getDesiredProperties().length > 0 ) {
								java.util.Vector subStructure = makePrototypeStructure( responsePrototype, factory, context );
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( responseName, subStructure ) );
							} else {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( responseName, factory.createItem( responsePrototype ) ) );
							}
						}
					} catch( Throwable t ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating popup item.", t );
					}
				} else if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
					try {
						String label = ((edu.cmu.cs.stage3.util.StringObjectPair)item).getString();
						java.util.Vector subResponseNames = (java.util.Vector)((edu.cmu.cs.stage3.util.StringObjectPair)item).getObject();
						java.util.Vector subStructure = makeOneShotStructure( subResponseNames, element, factory, context );
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( label, subStructure ) );
					} catch( Throwable t ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error creating popup item.", t );
					}
				}
			}
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makePropertyAssignmentForUserDefinedQuestionStructure( final edu.cmu.cs.stage3.alice.core.Property property, final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		edu.cmu.cs.stage3.util.StringObjectPair[] known = { new edu.cmu.cs.stage3.util.StringObjectPair( "element", property.getOwner() ), new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", property.getName() ) };
		String[] desired = { "value" };
		ElementPrototype ep = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment.class, known, desired );
		java.util.Vector setValueStructure = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.makePrototypeStructure( ep, factory, context );
		if( (setValueStructure != null) && (! setValueStructure.isEmpty()) ) {
			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "set value", setValueStructure ) );
		}

		return structure;
	}


	// this method is overly complex, mostly following the "Big Ball Of Mud" Pattern
	// http://www.laputan.org/mud/mud.html
	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and rerfturn a Runnable
	 */
	public static java.util.Vector makePrototypeStructure( final ElementPrototype elementPrototype, final PopupItemFactory factory, final edu.cmu.cs.stage3.alice.core.Element context){
		java.util.Vector structure = new java.util.Vector();
		final String[] desiredProperties = elementPrototype.getDesiredProperties();
		if( (desiredProperties == null) || (desiredProperties.length == 0) ) {
			structure.add( new StringObjectPair( "no properties to set on " + elementPrototype.getElementClass().getName() + "; please report this bug", factory.createItem( elementPrototype ) ) ); // this should not be reached
		} else if( desiredProperties.length > 0 ) {
			String preRepr = elementPrototype.getElementClass().getName() + "." + desiredProperties[0];
			String propertyRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( preRepr );
			if( propertyRepr.equals( preRepr ) ) {
				propertyRepr = desiredProperties[0];
			}
			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( propertyRepr, null ) );
//			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
			Class preValueClass = null;
			java.util.Vector preDefaultStructure = null;
			if( edu.cmu.cs.stage3.alice.core.response.PropertyAnimation.class.isAssignableFrom( elementPrototype.getElementClass() ) ) {
				preDefaultStructure = getDefaultValueStructureForPropertyAnimation( elementPrototype.getKnownPropertyValues() );
				preValueClass = getValueClassForPropertyAnimation( elementPrototype.getKnownPropertyValues() );
			} else if( edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment.class.isAssignableFrom( elementPrototype.getElementClass() ) ) {
				preDefaultStructure = getDefaultValueStructureForPropertyAnimation( elementPrototype.getKnownPropertyValues() );
				preValueClass = getValueClassForPropertyAnimation( elementPrototype.getKnownPropertyValues() );
			} else if( edu.cmu.cs.stage3.alice.core.question.list.ListBooleanQuestion.class.isAssignableFrom( elementPrototype.getElementClass() ) ||
					   edu.cmu.cs.stage3.alice.core.question.list.ListNumberQuestion.class.isAssignableFrom( elementPrototype.getElementClass() ) ||
					   edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion.class.isAssignableFrom( elementPrototype.getElementClass() ) ||
					   edu.cmu.cs.stage3.alice.core.response.list.ListResponse.class.isAssignableFrom( elementPrototype.getElementClass() ) )
			{
				if( desiredProperties[0].equals( "item" ) ) {
					preValueClass = getValueClassForList( elementPrototype.getKnownPropertyValues() );
					preDefaultStructure = getDefaultValueStructureForClass( preValueClass );
				} else if( desiredProperties[0].equals( "index" ) ) { // a bit hackish.
					preValueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
					preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty( elementPrototype.getKnownPropertyValues() );
				} else {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
					preDefaultStructure = getDefaultValueStructureForProperty( elementPrototype.getElementClass(), desiredProperties[0] );
				}
			} else if( edu.cmu.cs.stage3.alice.core.question.array.ArrayNumberQuestion.class.isAssignableFrom( elementPrototype.getElementClass() ) ||
					   edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion.class.isAssignableFrom( elementPrototype.getElementClass() ) ||
					   edu.cmu.cs.stage3.alice.core.response.array.ArrayResponse.class.isAssignableFrom( elementPrototype.getElementClass() ) )
			{
				if( desiredProperties[0].equals( "item" ) ) {
					preValueClass = getValueClassForArray( elementPrototype.getKnownPropertyValues() );
					preDefaultStructure = getDefaultValueStructureForClass( preValueClass );
				} else if( desiredProperties[0].equals( "index" ) ) {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
					preDefaultStructure = getDefaultValueStructureForCollectionIndexProperty( elementPrototype.getKnownPropertyValues() );
				} else {
					preValueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
					preDefaultStructure = getDefaultValueStructureForProperty( elementPrototype.getElementClass(), desiredProperties[0] );
				}
			} else if( elementPrototype instanceof CallToUserDefinedResponsePrototype ) {
				edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse actualResponse = ((CallToUserDefinedResponsePrototype)elementPrototype).getActualResponse();
				Object[] params = actualResponse.requiredFormalParameters.getArrayValue();
				for( int i = 0; i < params.length; i++ ) {
					if( ((edu.cmu.cs.stage3.alice.core.Variable)params[i]).name.getStringValue().equals( desiredProperties[0] ) ) {
						preValueClass = (Class)((edu.cmu.cs.stage3.alice.core.Variable)params[i]).valueClass.getValue();
						break;
					}
				}
				preDefaultStructure = getDefaultValueStructureForClass( preValueClass );
			} else if( elementPrototype instanceof CallToUserDefinedQuestionPrototype ) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.UserDefinedQuestion actualQuestion = ((CallToUserDefinedQuestionPrototype)elementPrototype).getActualQuestion();
				Object[] params = actualQuestion.requiredFormalParameters.getArrayValue();
				for( int i = 0; i < params.length; i++ ) {
					if( ((edu.cmu.cs.stage3.alice.core.Variable)params[i]).name.getStringValue().equals( desiredProperties[0] ) ) {
						preValueClass = (Class)((edu.cmu.cs.stage3.alice.core.Variable)params[i]).valueClass.getValue();
						break;
					}
				}
				preDefaultStructure = getDefaultValueStructureForClass( preValueClass );
			} else if (edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class.isAssignableFrom( elementPrototype.getElementClass()) ||
						edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class.isAssignableFrom( elementPrototype.getElementClass())){
				preValueClass = getValueClassForComparator(elementPrototype.knownPropertyValues);
				preDefaultStructure = getDefaultValueStructureForClass( preValueClass );
			}else {
				preValueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
				preDefaultStructure = getDefaultValueStructureForProperty( elementPrototype.getElementClass(), desiredProperties[0] );
			}

//			hack so we can use these in an inner class
			
			final Class valueClass = preValueClass;
					 
			PopupItemFactory recursiveFactory = new PopupItemFactory() {
				public Object createItem( Object o ) {
					if (!valueClass.isInstance(o)){ //Add the question here?
						if (valueClass.isAssignableFrom(javax.vecmath.Vector3d.class) && o instanceof edu.cmu.cs.stage3.alice.core.Transformable){
							edu.cmu.cs.stage3.alice.core.question.Position positionQuestion = new edu.cmu.cs.stage3.alice.core.question.Position();
							positionQuestion.subject.set(o);
							o = positionQuestion;
						} else if (valueClass.isAssignableFrom(javax.vecmath.Matrix4d.class) && o instanceof edu.cmu.cs.stage3.alice.core.Transformable){
							edu.cmu.cs.stage3.alice.core.question.PointOfView POVQuestion = new edu.cmu.cs.stage3.alice.core.question.PointOfView();
							POVQuestion.subject.set(o);
							o = POVQuestion;
						}else if (valueClass.isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class) && o instanceof edu.cmu.cs.stage3.alice.core.Transformable){
							edu.cmu.cs.stage3.alice.core.question.Quaternion quaternionQuestion = new edu.cmu.cs.stage3.alice.core.question.Quaternion();
							quaternionQuestion.subject.set(o);
							o = quaternionQuestion;
						}
					}
					if( desiredProperties.length == 1 ) { // end of the line
						//DEBUG System.out.println( "end of the line: " + desiredProperties[0] + ", " + o );
						return factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], o ) ) );
					} else { // recurse
						//DEBUG System.out.println( "recursive: " + desiredProperties[0] + ", " + o );
						return makePrototypeStructure( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], o ) ), factory, context );
					}
				}
			};
//			hack so we can use these in an inner class
			final java.util.Vector defaultStructure = processStructure( preDefaultStructure, recursiveFactory, NO_CURRENT_VALUE );
			// compute recent values
			java.util.Vector recentlyUsedStructure = new java.util.Vector();
			if( recentlyUsedValues.containsKey( preValueClass ) ) {
				java.util.List recentList = (java.util.List)recentlyUsedValues.get( preValueClass );
				int count = 0;
				for( java.util.Iterator iter = recentList.iterator(); iter.hasNext(); ) {
					if( count > Integer.parseInt( authoringToolConfig.getValue( "maxRecentlyUsedValues" ) ) ) {
						break;
					}
					Object value = iter.next();
					if( ! structureContains( preDefaultStructure, value ) ) {
						recentlyUsedStructure.add( value );
						count++;
					}
				}
			}

			

			if( ! defaultStructure.isEmpty() ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				structure.addAll( defaultStructure );
			}

			// add recent values if there are any
			if( ! recentlyUsedStructure.isEmpty() ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				addLabelsToValueStructure( recentlyUsedStructure, elementPrototype.getElementClass(), desiredProperties[0] );
				structure.addAll( processStructure( recentlyUsedStructure, recursiveFactory, NO_CURRENT_VALUE ) );
			}


			// elements
			//   create criterion; handle exceptions
			edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( valueClass );
			edu.cmu.cs.stage3.util.Criterion elementIsNamed = new edu.cmu.cs.stage3.util.Criterion() { //HACK; shouldn't have to cull unnamed elements
				public boolean accept( Object o ) {
					if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
						if( ((edu.cmu.cs.stage3.alice.core.Element)o).name.get() != null ) {
							return true;
						}
					}
					return false;
				}
			};
			InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
			edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, elementIsNamed, inAppropriateOAPCriterion } );
			Class elementClass = elementPrototype.getElementClass();
			edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues = elementPrototype.getKnownPropertyValues();
			
			//Don't get self criterion stuff!
			if( (edu.cmu.cs.stage3.alice.core.response.AbstractPointAtAnimation.class.isAssignableFrom( elementClass )  && desiredProperties[0].equals( "target" )) ||
				(edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation.class.isAssignableFrom(elementPrototype.elementClass)  && desiredProperties[0].equals( "value" )) ) {
				for( int i = 0; i < knownPropertyValues.length; i++ ) {
					String propertyName = knownPropertyValues[i].getString();
					if( propertyName.equals( "subject" ) || propertyName.equals( "element" ) ) {
						final Object transformableValue = knownPropertyValues[i].getObject();
						edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
							public boolean accept( Object o ) {
								if( o == transformableValue ) {
									return false;
								}
								return true;
							}
						};
						criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, elementIsNamed, inAppropriateOAPCriterion, notSelf } );
						break;
					}
				}
			}
			//   make structure
			if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( valueClass ) || valueClass.isAssignableFrom( edu.cmu.cs.stage3.alice.core.Element.class ) ) {
				edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search( criterion );
				if( elements.length > 0 ) {
					if( elements.length < 10 ) {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						structure.addAll( makeFlatElementStructure( context.getRoot(), criterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
					} else {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						structure.addAll( makeElementStructure( context.getRoot(), criterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
					}
				}
			}
			if (!Object.class.isAssignableFrom(valueClass)){
				//Okay, this needs to build a structure to find models, get their position and pass them to the vector3d property
				if( javax.vecmath.Vector3d.class.isAssignableFrom( valueClass ) || valueClass.isAssignableFrom(javax.vecmath.Vector3d.class)) {
					edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Model.class ), elementIsNamed, inAppropriateOAPCriterion } );
					edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search( modelCriterion );
					if( elements.length > 0 ) {
						if( elements.length < 10 ) {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeFlatElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						} else {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						}
					}
				}
				if( valueClass.isAssignableFrom(javax.vecmath.Matrix4d.class)) {
					edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Model.class ), elementIsNamed, inAppropriateOAPCriterion } );
					edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search( modelCriterion );
					if( elements.length > 0 ) {
						if( elements.length < 10 ) {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeFlatElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						} else {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						}
					}
				}
				if( valueClass.isAssignableFrom(edu.cmu.cs.stage3.math.Quaternion.class)) {
					edu.cmu.cs.stage3.util.Criterion modelCriterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Model.class ), elementIsNamed, inAppropriateOAPCriterion } );
					edu.cmu.cs.stage3.alice.core.Element[] elements = context.getRoot().search( modelCriterion );
					if( elements.length > 0 ) {
						if( elements.length < 10 ) {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeFlatElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						} else {
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							structure.addAll( makeElementStructure( context.getRoot(), modelCriterion, recursiveFactory, context, NO_CURRENT_VALUE ) );
						}
					}
				}
			}
//			else{
//				System.out.println("bleh!");
//			}
			
			// import or record sound if necessary
			final PopupItemFactory metaFactory = new PopupItemFactory() {
				public Object createItem( final Object o ) {
					return new Runnable() {
						public void run() {
							((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], o ) ) )).run();
						}
					};
				}
			};
			if( valueClass.isAssignableFrom( edu.cmu.cs.stage3.alice.core.Sound.class ) && (valueClass != Object.class) ) {
				java.io.File soundDir = new java.io.File( edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "sounds" ).getAbsoluteFile();
				if( soundDir.exists() && soundDir.isDirectory() ) {
					java.util.ArrayList sounds = new java.util.ArrayList();
					java.io.File[] fileList = soundDir.listFiles();
					for( int i = 0; i < fileList.length; i++ ) {
						if( fileList[i].isFile() && fileList[i].canRead() ) {
							sounds.add( fileList[i] );
						}
					}

					if( sounds.size() > 0 ) {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						for( java.util.Iterator iter = sounds.iterator(); iter.hasNext(); ) {
							final java.io.File soundFile = (java.io.File)iter.next();
							String name = soundFile.getName();
							name = name.substring( 0, name.lastIndexOf( '.' ) );
							Runnable importSoundRunnable = new Runnable() {
								public void run() {
									edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound)edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().importElement( soundFile, context.getSandbox() );
									if( sound != null ) {
										((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], sound ) ) )).run();
									}
//									edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().importElement( soundFile, context.getSandbox() );
//									((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], worker.get() ) ) )).run();
								}
							};
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( name, importSoundRunnable ) );
						}
					}
				}

				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				Runnable importRunnable = new Runnable() {
					public void run() {
//						PropertyPopupPostWorker postWorker = new PropertyPopupPostWorker( metaFactory );
						PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable( metaFactory );
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().setImportFileFilter( "Sound Files" );
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().importElement( null, context.getSandbox(), propertyPopupPostImportRunnable );
					}
				};
				Runnable recordRunnable = new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.core.Sound sound = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().promptUserForRecordedSound( context.getSandbox() );
						if( sound != null ) {
							((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], sound ) ) )).run();
						}
					}
				};
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "import sound file...", importRunnable ) );
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "record new sound...", recordRunnable ) );
			}

			// expressions
			java.util.Vector expressionStructure = makeFlatExpressionStructure( valueClass, recursiveFactory, context, NO_CURRENT_VALUE );
			if( (expressionStructure != null) && (expressionStructure.size() > 0) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "expressions", expressionStructure ) );
			}

			// Null
			boolean nullValid;
			if( elementPrototype instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype ) {
				nullValid = true;
			} else if( elementPrototype instanceof edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype ) {
				nullValid = true;
			} else {
				nullValid = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIIncludeNone( elementPrototype.getElementClass(), desiredProperties[0] );
//				nullValid = edu.cmu.cs.stage3.alice.core.Element.isNullValidForPropertyNamed( elementPrototype.getElementClass(), desiredProperties[0] );
			}
			if( nullValid ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				String nullRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, elementClass, desiredProperties[0], "menuContext" );
				if( desiredProperties.length == 1 ) { // end of the line
					structure.add( new StringObjectPair( nullRepr, factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], null ) ) ) ) );
				} else { // recurse
					structure.add( new StringObjectPair( nullRepr, makePrototypeStructure( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], null ) ), factory, context ) ) );
				}
			}

			// Other...
			final PopupItemFactory otherFactory = new PopupItemFactory() {
				public Object createItem( final Object o ) {
					return new Runnable() {
						public void run() {
							((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], o ) ) )).run();
						}
					};
				}
			};
			if( (desiredProperties.length == 1) && edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.isOtherDialogSupportedForClass( valueClass ) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				Runnable runnable = new Runnable() {
					public void run() {
						//TODO: context doesn't really want to be passed here... it wants to be the response that will be made...
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showOtherDialog( valueClass, null, otherFactory, context );
					}
				};
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "other...", runnable ) );
			}

			// allow user to create new list
			if( edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom( valueClass ) && (desiredProperties.length == 1) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				Runnable createNewListRunnable = new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
						edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables = context.getSandbox().variables;
						edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool.showNewVariableDialog( "Create new list", context.getRoot(), true, true );
						if( variable != null ) {
							if( variables != null ) {
								authoringTool.getUndoRedoStack().startCompound();
								try {
									variables.getOwner().addChild( variable );
									variables.add( variable );
								} finally {
									authoringTool.getUndoRedoStack().stopCompound();
								}
							}
							((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( desiredProperties[0], variable ) ) )).run();
						}
					}
				};
				structure.add( new StringObjectPair( "create new list...", createNewListRunnable ) );
			}
		}

		return structure;
	}

	/**
	 * @deprecated  use makePropertyStructure( final edu.cmu.cs.stage3.alice.core.Property property, final PopupItemFactory factory, boolean includeDefaults, boolean includeExpressions, boolean includeOther, edu.cmu.cs.stage3.alice.core.Element root )
	 */
	public static java.util.Vector makePropertyStructure( final edu.cmu.cs.stage3.alice.core.Property property, final PopupItemFactory factory, boolean includeDefaults, boolean includeExpressions, boolean includeOther ) {
		return makePropertyStructure( property, factory, includeDefaults, includeExpressions, includeOther, null );
	}
	/**
	 * the PopupItemFactory should accept a target value and return a Runnable
	 *
	 * root is used to create an Element hierarchy if needed.
	 * if root is null, property.getElement().getRoot() is used.
	 */
	public static java.util.Vector makePropertyStructure( final edu.cmu.cs.stage3.alice.core.Property property, final PopupItemFactory factory, boolean includeDefaults, boolean includeExpressions, boolean includeOther, edu.cmu.cs.stage3.alice.core.Element root ) {
		if( root == null ) {
			root = property.getOwner().getRoot();
		}
		java.util.Vector structure = new java.util.Vector();
		Class targetValueClass = edu.cmu.cs.stage3.alice.authoringtool.util.PopupMenuUtilities.getDesiredValueClass(property);
		if( edu.cmu.cs.stage3.alice.core.List.class.isAssignableFrom( targetValueClass ) ) { // lists are special
			edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent.getPropertyReferencesTo( property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false );
			if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.list.ListObjectQuestion) && (references.length > 0) ) {
				final Class itemValueClass = references[0].getProperty().getValueClass();
				edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
					public boolean accept( Object o ) {
						if( o instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
							edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)o).getValue();
							if( list != null ) {
								if( itemValueClass.isAssignableFrom( list.valueClass.getClassValue() ) ) {
									return true;
								}
							}
						}
						return false;
					}
				};
				structure = makeFlatExpressionStructure( targetValueClass, criterion, factory, property.getOwner(), property.get() );

				if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ) ) {
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					if( property.get() == null ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), new PopupItemWithIcon( factory.createItem( null ), currentValueIcon ) ) );
					} else {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), factory.createItem( null ) ) );
					}
				}
			} else { // not an anonymous list question; accept all lists
				structure = makeFlatExpressionStructure( targetValueClass, factory, property.getOwner(), property.get() );

				if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ) ) {
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					if( property.get() == null ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), new PopupItemWithIcon( factory.createItem( null ), currentValueIcon ) ) );
					} else {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), factory.createItem( null ) ) );
					}
				}

				// allow user to create new list
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				Runnable createNewListRunnable = new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool authoringTool = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack();
						edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty variables = property.getOwner().getSandbox().variables;
						edu.cmu.cs.stage3.alice.core.Variable variable = authoringTool.showNewVariableDialog( "Create new list", property.getOwner().getRoot(), true, true );
						if( variable != null ) {
							if( variables != null ) {
								variables.getOwner().addChild( variable );
								variables.add( variable );
							}
							((Runnable)factory.createItem( variable )).run();
						}
					}
				};
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "create new list...", createNewListRunnable ) );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Array.class.isAssignableFrom( targetValueClass ) ) { // arrays are special too
			edu.cmu.cs.stage3.alice.core.Element parent = property.getOwner().getParent();
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = parent.getPropertyReferencesTo( property.getOwner(), edu.cmu.cs.stage3.util.HowMuch.INSTANCE, false );
			if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.array.ArrayObjectQuestion) && (references.length > 0) ) {
				final Class itemValueClass = references[0].getProperty().getValueClass();
				edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
					public boolean accept( Object o ) {
						if( o instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
							edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array)((edu.cmu.cs.stage3.alice.core.Variable)o).getValue();
							if( array != null ) {
								if( itemValueClass.isAssignableFrom( array.valueClass.getClassValue() ) ) {
									return true;
								}
							}
						}
						return false;
					}
				};
				structure = makeFlatExpressionStructure( targetValueClass, criterion, factory, property.getOwner(), property.get() );

				if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ) ) {
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					if( property.get() == null ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), new PopupItemWithIcon( factory.createItem( null ), currentValueIcon ) ) );
					} else {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), factory.createItem( null ) ) );
					}
				}
			} else { // not an anonymous array question; accept all arrays
				structure = makeFlatExpressionStructure( targetValueClass, factory, property.getOwner(), property.get() );

				if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ) ) {
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					if( property.get() == null ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), new PopupItemWithIcon( factory.createItem( null ), currentValueIcon ) ) );
					} else {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), factory.createItem( null ) ) );
					}
				}
			}
		} else {
			if( includeDefaults ) {
				if( edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( targetValueClass ) ) {
					final edu.cmu.cs.stage3.alice.core.Element context = root;
					final edu.cmu.cs.stage3.alice.core.Property referenceProperty = property;
					PopupItemFactory userDefinedResponsePopupFactory = new PopupItemFactory() {
						public Object createItem( Object o ) {
							final edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse userDefinedResponse = (edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse)o;
							CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = new CallToUserDefinedResponsePrototype( userDefinedResponse );
							callToUserDefinedResponsePrototype.calculateDesiredProperties();
							PopupItemFactory prototypePopupFactory = new PopupItemFactory() {
								public Object createItem( Object prototype ) {
									CallToUserDefinedResponsePrototype completedPrototype = (CallToUserDefinedResponsePrototype)prototype;
									return factory.createItem( completedPrototype.createNewElement() );
								}
							};
							if( userDefinedResponse.requiredFormalParameters.size() > 0 ) {
								if (referenceProperty.getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior ||
								referenceProperty.getOwner() instanceof edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior){
									return makePrototypeStructure( callToUserDefinedResponsePrototype, prototypePopupFactory, referenceProperty.getOwner() );
								}else{
									return makePrototypeStructure( callToUserDefinedResponsePrototype, prototypePopupFactory, context );
								}
							} else {
								return prototypePopupFactory.createItem( callToUserDefinedResponsePrototype );
							}
						}
					};
					edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion criterion = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.response.UserDefinedResponse.class );
					structure.addAll( makeElementStructure( root, criterion, userDefinedResponsePopupFactory, root, property.get() ) );
				} else {
					// default and recently used values
					
					
					
					java.util.Vector defaultStructure = getDefaultValueStructureForProperty( property );
					java.util.Vector recentlyUsedStructure = new java.util.Vector();
					if( recentlyUsedValues.containsKey( targetValueClass ) ) {
						java.util.List recentList = (java.util.List)recentlyUsedValues.get( targetValueClass );
						int count = 0;
						for( java.util.Iterator iter = recentList.iterator(); iter.hasNext(); ) {
							if( count > Integer.parseInt( authoringToolConfig.getValue( "maxRecentlyUsedValues" ) ) ) {
								break;
							}
							Object value = iter.next();
							if( ! structureContains( defaultStructure, value ) ) {
								recentlyUsedStructure.add( value );
								count++;
							}
						}
					}

					// make sure current value is represented
					Object currentValue = property.get();
					java.util.Vector unlabeledDefaultValueStructure = getUnlabeledDefaultValueStructureForProperty( property.getOwner().getClass(), property.getName(), property ); // very hackish
					if( ! (unlabeledDefaultValueStructure.contains( currentValue ) || recentlyUsedStructure.contains( currentValue )) ) {
						if( ! (currentValue instanceof edu.cmu.cs.stage3.alice.core.Expression) ) {
							recentlyUsedStructure.add( 0, currentValue );
							if( recentlyUsedStructure.size() > Integer.parseInt( authoringToolConfig.getValue( "maxRecentlyUsedValues" ) ) ) {
								recentlyUsedStructure.remove( recentlyUsedStructure.size() - 1 );
							}
						}
					}

					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					structure.addAll( processStructure( defaultStructure, factory, property.get() ) );
					if( ! recentlyUsedStructure.isEmpty() && !property.getName().equalsIgnoreCase("keyCode")) {
						if( structure.size() > 0 ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						addLabelsToValueStructure( recentlyUsedStructure, property.getOwner().getClass(), property.getName() );
						structure.addAll( processStructure( recentlyUsedStructure, factory, property.get() ) );
					}

					// Elements
					if( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( targetValueClass ) || targetValueClass.isAssignableFrom( edu.cmu.cs.stage3.alice.core.Element.class ) ) {
						edu.cmu.cs.stage3.util.Criterion criterion;
						if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.Behavior) && edu.cmu.cs.stage3.alice.core.Response.class.isAssignableFrom( targetValueClass ) ) {
							criterion = new edu.cmu.cs.stage3.util.Criterion() { // object must be top-level response
								public boolean accept( Object o ) {
									if( o instanceof edu.cmu.cs.stage3.alice.core.Response ) {
										if( ! (((edu.cmu.cs.stage3.alice.core.Response)o).getParent() instanceof edu.cmu.cs.stage3.alice.core.Response) ) {
											if( ! (o instanceof edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse) ) {
												return true;
											}
										}
									}
									return false;
								}
							};
						} else if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) && property.getName().equals( "element" ) ) {
							edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation)property.getOwner();
							final String propertyName = propertyAnimation.propertyName.getStringValue();
							edu.cmu.cs.stage3.util.Criterion hasProperty = new edu.cmu.cs.stage3.util.Criterion() {
								public boolean accept( Object o ) {
									if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
										if( ((edu.cmu.cs.stage3.alice.core.Element)o).getPropertyNamed( propertyName ) != null ) {
											return true;
										}
									}
									return false;
								}
							};
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { hasProperty, isNamedElement } );
						} else if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtAnimation) && property.getName().equals( "target" ) ) {
							final Object transformableValue = property.getOwner().getPropertyNamed( "subject" ).get();
							edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								public boolean accept( Object o ) {
									if( o == transformableValue ) {
										return false;
									}
									return true;
								}
							};
							edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( targetValueClass );
							InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion } );
						} else if( (property instanceof edu.cmu.cs.stage3.alice.core.property.VehicleProperty) ) {
							final Object transformableValue = property.getOwner();
							edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								public boolean accept( Object o ) {
									if( o == transformableValue ) {
										return false;
									}
									return true;
								}
							};
							edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( targetValueClass );
							InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion } );
						}else if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PointAtConstraint) && property.getName().equals( "target" ) ) {
							final Object transformableValue = property.getOwner().getPropertyNamed( "subject" ).get();
							edu.cmu.cs.stage3.util.Criterion notSelf = new edu.cmu.cs.stage3.util.Criterion() {
								public boolean accept( Object o ) {
									if( o == transformableValue ) {
										return false;
									}
									return true;
								}
							};
							edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( targetValueClass );
							InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
							criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notSelf, isNamedElement, inAppropriateOAPCriterion } );
						} else {
							edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion instanceOf = new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( targetValueClass );
							if( edu.cmu.cs.stage3.alice.core.Expression.class.isAssignableFrom( targetValueClass ) ) {
								criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, isNamedElement } );
							} else {
								edu.cmu.cs.stage3.util.criterion.NotCriterion notExpression = new edu.cmu.cs.stage3.util.criterion.NotCriterion( new edu.cmu.cs.stage3.util.criterion.InstanceOfCriterion( edu.cmu.cs.stage3.alice.core.Expression.class ) );
								InAppropriateObjectArrayPropertyCriterion inAppropriateOAPCriterion = new InAppropriateObjectArrayPropertyCriterion();
								criterion = new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { instanceOf, notExpression, isNamedElement, inAppropriateOAPCriterion } );
							}
						}
						edu.cmu.cs.stage3.alice.core.Element[] elements = root.search( criterion );

						if( (structure.size() > 0) && (elements.length > 0) ) {
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
						}
						if( elements.length < 10 ) {
							structure.addAll( makeFlatElementStructure( root, criterion, factory, root, property.get() ) );
						} else {
							structure.addAll( makeElementStructure( root, criterion, factory, root, property.get() ) );
						}

						// import a sound if necessary
						if( targetValueClass.isAssignableFrom( edu.cmu.cs.stage3.alice.core.Sound.class ) ) {
							final edu.cmu.cs.stage3.alice.core.World world = root.getWorld();
							if( structure.size() > 0 ) {
								structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
							}
							Runnable runnable = new Runnable() {
								public void run() {
									PropertyPopupPostImportRunnable propertyPopupPostImportRunnable = new PropertyPopupPostImportRunnable( factory );
									edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().setImportFileFilter( "Sound Files" );
									edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().importElement( null, world, propertyPopupPostImportRunnable ); // should probably somehow hook the sound up to the object playing it
								}
							};
							structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "import sound file...", runnable ) );
						}
					}
				}
			}
			//TODO check out to see if shouldGUIOmitNone needs to be handled
			if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitNone( property ) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				if( property.get() == null ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), new PopupItemWithIcon( factory.createItem( null ), currentValueIcon ) ) );
				} else {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( null, property, "menuContext" ), factory.createItem( null ) ) );
				}
			}
			//TODO check out makeFlatExpressionStructure
			if( includeExpressions ) {
				
				java.util.Vector expressionStructure = makeFlatExpressionStructure( targetValueClass, factory, property.getOwner(), property.get() );
				if( (expressionStructure != null) && (expressionStructure.size() > 0) ) {
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "expressions", expressionStructure ) );
				} else if( structure.size() == 0 ) {
					javax.swing.JLabel label = new javax.swing.JLabel( "no expressions for this type" );
					label.setForeground( java.awt.Color.gray );
					label.setBorder( javax.swing.BorderFactory.createEmptyBorder( 2, 8, 2, 2 ) );
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "", label ) );
				}
			}

			if( Boolean.class.isAssignableFrom( targetValueClass ) && includeExpressions ) {
				java.util.Vector booleanLogicStructure = makeBooleanLogicStructure( property.get(), factory, property.getOwner() );
				if (booleanLogicStructure!= null){
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "logic", booleanLogicStructure ) );
				}
			}

			if( Number.class.isAssignableFrom( targetValueClass ) && includeExpressions ) {
				java.util.Vector mathStructure = makeCommonMathQuestionStructure( property.get(), factory, property.getOwner() );
				if (mathStructure != null){
					if( structure.size() > 0 ) {
						structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
					}
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "math", mathStructure ) );
				}
			}

			if( ! edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.shouldGUIOmitScriptDefined( property ) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				Runnable scriptDefinedRunnable = new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showScriptDefinedPropertyDialog( property, factory );
					}
				};
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "script-defined...", scriptDefinedRunnable ) );
			}
			//TODO: make this mroe intelligent
			// Other...
			
			if( includeOther && edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.isOtherDialogSupportedForClass( targetValueClass ) ) {
				if( structure.size() > 0 ) {
					structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
				}
				final Class finalOtherValueClass = targetValueClass;
				Runnable runnable = new Runnable() {
					public void run() {
						edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.showOtherPropertyDialog( property, factory, null, finalOtherValueClass );
					}
				};
				structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "other...", runnable ) );
			}
		}

		if( structure.size() == 0 ) {
			javax.swing.JLabel label = new javax.swing.JLabel( "nothing to choose" );
			label.setForeground( java.awt.Color.gray );
			label.setBorder( javax.swing.BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
			structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "", label ) );
		}

		return structure;
	}

	public static java.util.Vector makeListQuestionStructure( final edu.cmu.cs.stage3.alice.core.Variable listVariable, final PopupItemFactory factory, Class returnValueClass, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		edu.cmu.cs.stage3.alice.core.List list = (edu.cmu.cs.stage3.alice.core.List)listVariable.getValue();
		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory prototypeToItemFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
			public Object createItem( final Object object ) {
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
				return factory.createItem( ep.createNewElement() );
			}
		};

		if( returnValueClass.isAssignableFrom( list.valueClass.getClassValue() ) ) {
			Runnable firstItemRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning itemAtBeginning = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning();
				itemAtBeginning.list.set( listVariable );
				((Runnable)factory.createItem( itemAtBeginning )).run();
			} };
			Runnable lastItemRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd itemAtEnd = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd();
				itemAtEnd.list.set( listVariable );
				((Runnable)factory.createItem( itemAtEnd )).run();
			} };
			Runnable randomItemRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex itemAtRandomIndex = new edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex();
				itemAtRandomIndex.list.set( listVariable );
				((Runnable)factory.createItem( itemAtRandomIndex )).run();
			} };

			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "list", listVariable ) };
			String[] desired = new String[] { "index" };
			ElementPrototype ithPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex.class, known, desired );
			java.util.Vector ithStructure = makePrototypeStructure( ithPrototype, prototypeToItemFactory, context );

			structure.add( new StringObjectPair( "first item from list", firstItemRunnable ) );
			structure.add( new StringObjectPair( "last item from list", lastItemRunnable ) );
			structure.add( new StringObjectPair( "random item from list", randomItemRunnable ) );
			structure.add( new StringObjectPair( "ith item from list", ithStructure ) );
		}
		if ( edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom( list.valueClass.getClassValue() ) ){
//			public static java.util.Vector makePropertyValueStructure( final edu.cmu.cs.stage3.alice.core.Element element, Class valueClass, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
//			java.util.Vector structure = new java.util.Vector();
//	
//			Class elementClass = null;
//			if( element instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
//				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
//			} else {
//				elementClass = element.getClass();
//			}
//	
//			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "element", element ) };
//			String[] desired = new String[] { "propertyName" };
//			ElementPrototype prototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.PropertyValue.class, known, desired );
//	
//			String[] propertyNames = getPropertyNames( elementClass, valueClass );
//	
//			String prefix = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element, false ) + ".";
//			for( int i = 0; i < propertyNames.length; i++ ) {
//				if( (! propertyNames[i].equals( "visualization" )) && (! propertyNames[i].equals( "isFirstClass" )) ) { // HACK suppression
//					String propertyName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( propertyNames[i], false );
//					structure.add( new StringObjectPair( prefix + propertyName, factory.createItem( prototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", propertyName ) ) ) ) );
//				}
//			}
//	
//			return structure;
		}
		if( returnValueClass.isAssignableFrom( Boolean.class ) ) {
			Runnable isEmptyRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.list.IsEmpty isEmpty = new edu.cmu.cs.stage3.alice.core.question.list.IsEmpty();
				isEmpty.list.set( listVariable );
				((Runnable)factory.createItem( isEmpty )).run();
			} };

			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "list", listVariable ) };
			String[] desired = new String[] { "item" };
			ElementPrototype containsPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.list.Contains.class, known, desired );
			java.util.Vector containsStructure = makePrototypeStructure( containsPrototype, prototypeToItemFactory, context );

			if( structure.size() > 0 ) {
				structure.add( new StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
			}
			structure.add( new StringObjectPair( "is list empty", isEmptyRunnable ) );
			structure.add( new StringObjectPair( "list contains", containsStructure ) );
		}

		if( returnValueClass.isAssignableFrom( Number.class ) ) {
			Runnable sizeRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.list.Size size = new edu.cmu.cs.stage3.alice.core.question.list.Size();
				size.list.set( listVariable );
				((Runnable)factory.createItem( size )).run();
			} };

			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "list", listVariable ) };
			String[] desired = new String[] { "item" };

			ElementPrototype firstIndexOfItemPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem.class, known, desired );
			java.util.Vector firstIndexOfItemStructure = makePrototypeStructure( firstIndexOfItemPrototype, prototypeToItemFactory, context );

			ElementPrototype lastIndexOfItemPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem.class, known, desired );
			java.util.Vector lastIndexOfItemStructure = makePrototypeStructure( lastIndexOfItemPrototype, prototypeToItemFactory, context );

			if( structure.size() > 0 ) {
				structure.add( new StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
			}
			structure.add( new StringObjectPair( "size of list", sizeRunnable ) );
			structure.add( new StringObjectPair( "first index of", firstIndexOfItemStructure ) );
			structure.add( new StringObjectPair( "last index of", lastIndexOfItemStructure ) );
		}

		return structure;
	}

	public static java.util.Vector makeArrayQuestionStructure( final edu.cmu.cs.stage3.alice.core.Variable arrayVariable, final PopupItemFactory factory, Class returnValueClass, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		edu.cmu.cs.stage3.alice.core.Array array = (edu.cmu.cs.stage3.alice.core.Array)arrayVariable.getValue();

		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory prototypeToItemFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
			public Object createItem( final Object object ) {
				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
				return factory.createItem( ep.createNewElement() );
			}
		};

		if( returnValueClass.isAssignableFrom( array.valueClass.getClassValue() ) ) {
			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "array", arrayVariable ) };
			String[] desired = new String[] { "index" };
			ElementPrototype ithPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex.class, known, desired );
			java.util.Vector ithStructure = makePrototypeStructure( ithPrototype, prototypeToItemFactory, context );

			structure.add( new StringObjectPair( "ith item from array", ithStructure ) );
		}

		if( returnValueClass.isAssignableFrom( Number.class ) ) {
			Runnable sizeRunnable = new Runnable() { public void run() {
				edu.cmu.cs.stage3.alice.core.question.array.Size size = new edu.cmu.cs.stage3.alice.core.question.array.Size();
				size.array.set( arrayVariable );
				((Runnable)factory.createItem( size )).run();
			} };

			edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "array", arrayVariable ) };
			String[] desired = new String[] { "item" };

			if( structure.size() > 0 ) {
				structure.add( new StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
			}
			structure.add( new StringObjectPair( "size of array", sizeRunnable ) );
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makeCommonMathQuestionStructure( final Object firstOperand, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		if( firstOperand instanceof Number ) {
			//accept
		} else if( (firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression) && Number.class.isAssignableFrom( ((edu.cmu.cs.stage3.alice.core.Expression)firstOperand).getValueClass() ) ) {
			//accept
		} else if( firstOperand == null ) {
			//accept
		} else {
			throw new IllegalArgumentException( "firstOperand must represent a Number" );
		}

		String firstOperandRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( firstOperand, false );

//		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory mathFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
//			public Object createItem( final Object object ) {
//				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
//				return factory.createItem( ep.createNewElement() );
//			}
//		};

		edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "a", firstOperand ) };
		String[] desired = new String[] { "b" };

		ElementPrototype addPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberAddition.class, known, desired );
		java.util.Vector addStructure = makePrototypeStructure( addPrototype, factory, context );
		ElementPrototype subtractPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberSubtraction.class, known, desired );
		java.util.Vector subtractStructure = makePrototypeStructure( subtractPrototype, factory, context );
		ElementPrototype multiplyPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberMultiplication.class, known, desired );
		java.util.Vector multiplyStructure = makePrototypeStructure( multiplyPrototype, factory, context );
		ElementPrototype dividePrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberDivision.class, known, desired );
		java.util.Vector divideStructure = makePrototypeStructure( dividePrototype, factory, context );

		structure.add( new StringObjectPair( firstOperandRepr + " +", addStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " -", subtractStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " *", multiplyStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " /", divideStructure ) );

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makeBooleanLogicStructure( final Object firstOperand, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();
		boolean isNone = false;
		if( firstOperand instanceof Boolean ) {
			//accept
		} else if( (firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression) && Boolean.class.isAssignableFrom( ((edu.cmu.cs.stage3.alice.core.Expression)firstOperand).getValueClass() ) ) {
			//accept
		} else if( firstOperand == null ) {
			return null;
		} else {
			throw new IllegalArgumentException( "firstOperand must represent a Boolean" );
		}

		String firstOperandRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( firstOperand, false );

//		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory logicFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
//			public Object createItem( final Object object ) {
//				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
//				return factory.createItem( ep.createNewElement() );
//			}
//		};

		edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "a", firstOperand ) };
		String[] desired = new String[] { "b" };

		ElementPrototype andPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.And.class, known, desired );
		java.util.Vector andStructure = makePrototypeStructure( andPrototype, factory, context );
		ElementPrototype orPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.Or.class, known, desired );
		java.util.Vector orStructure = makePrototypeStructure( orPrototype, factory, context );
		ElementPrototype notPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.Not.class, known, new String[0] );
		Object notItem = factory.createItem( notPrototype );
		ElementPrototype equalPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired );
		java.util.Vector equalStructure = makePrototypeStructure( equalPrototype, factory, context );
		ElementPrototype notEqualPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired );
		java.util.Vector notEqualStructure = makePrototypeStructure( notEqualPrototype, factory, context );

		structure.add( new StringObjectPair( firstOperandRepr + " and", andStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " or", orStructure ) );
		structure.add( new StringObjectPair( "not " + firstOperandRepr, notItem ) );
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "Separator", javax.swing.JSeparator.class ) );
		structure.add( new StringObjectPair( firstOperandRepr + " ==", equalStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " !=", notEqualStructure ) );

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makeComparatorStructure( final Object firstOperand, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		String firstOperandRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( firstOperand, false );

//		edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory comparatorFactory = new edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory() {
//			public Object createItem( final Object object ) {
//				edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype ep = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype)object;
//				return factory.createItem( ep.createNewElement() );
//			}
//		};

		edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "a", firstOperand ) };
		String[] desired = new String[] { "b" };
		ElementPrototype equalPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.IsEqualTo.class, known, desired );
		java.util.Vector equalStructure = makePrototypeStructure( equalPrototype, factory, context );
		ElementPrototype notEqualPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo.class, known, desired );
		java.util.Vector notEqualStructure = makePrototypeStructure( notEqualPrototype, factory, context );

		structure.add( new StringObjectPair( firstOperandRepr + " ==", equalStructure ) );
		structure.add( new StringObjectPair( firstOperandRepr + " !=", notEqualStructure ) );

		if( (firstOperand instanceof Number) || ((firstOperand instanceof edu.cmu.cs.stage3.alice.core.Expression) && Number.class.isAssignableFrom( ((edu.cmu.cs.stage3.alice.core.Expression)firstOperand).getValueClass() )) ) {
//			ElementPrototype equalToPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsEqualTo.class, known, desired );
//			java.util.Vector equalToStructure = makePrototypeStructure( equalToPrototype, factory, context );
//			ElementPrototype notEqualToPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsNotEqualTo.class, known, desired );
//			java.util.Vector notEqualToStructure = makePrototypeStructure( notEqualToPrototype, factory, context );
			
			ElementPrototype lessThanPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsLessThan.class, known, desired );
			java.util.Vector lessThanStructure = makePrototypeStructure( lessThanPrototype, factory, context );
			ElementPrototype greaterThanPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThan.class, known, desired );
			java.util.Vector greaterThanStructure = makePrototypeStructure( greaterThanPrototype, factory, context );
			ElementPrototype lessThanOrEqualPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsLessThanOrEqualTo.class, known, desired );
			java.util.Vector lessThanOrEqualStructure = makePrototypeStructure( lessThanOrEqualPrototype, factory, context );
			ElementPrototype greaterThanOrEqualPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThanOrEqualTo.class, known, desired );
			java.util.Vector greaterThanOrEqualStructure = makePrototypeStructure( greaterThanOrEqualPrototype, factory, context );
			
//			structure.add( new StringObjectPair( firstOperandRepr + " ==", equalToStructure ) );
//			structure.add( new StringObjectPair( firstOperandRepr + " !=", notEqualToStructure ) );
			structure.add( new StringObjectPair( firstOperandRepr + " <", lessThanStructure ) );
			structure.add( new StringObjectPair( firstOperandRepr + " >", greaterThanStructure ) );
			structure.add( new StringObjectPair( firstOperandRepr + " <=", lessThanOrEqualStructure ) );
			structure.add( new StringObjectPair( firstOperandRepr + " >=", greaterThanOrEqualStructure ) );
		}

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makePartsOfPositionStructure( final Object position, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		if( position instanceof javax.vecmath.Vector3d ) {
			//accept
		} else if( (position instanceof edu.cmu.cs.stage3.alice.core.Expression) && javax.vecmath.Vector3d.class.isAssignableFrom( ((edu.cmu.cs.stage3.alice.core.Expression)position).getValueClass() ) ) {
			//accept
		} else if( position == null ) {
			//accept
		} else {
			throw new IllegalArgumentException( "position must represent a javax.vecmath.Vector3d" );
		}

		String positionRepr = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( position, false );

		edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "vector3", position ) };

		ElementPrototype xPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.vector3.X.class, known, new String[0] );
		Object xItem = factory.createItem( xPrototype );
		ElementPrototype yPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.vector3.Y.class, known, new String[0] );
		Object yItem = factory.createItem( yPrototype );
		ElementPrototype zPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.vector3.Z.class, known, new String[0] );
		Object zItem = factory.createItem( zPrototype );

		structure.add( new StringObjectPair( positionRepr + "'s distance right", xItem ) );
		structure.add( new StringObjectPair( positionRepr + "'s distance up", yItem ) );
		structure.add( new StringObjectPair( positionRepr + "'s distance forward", zItem ) );

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makeResponsePrintStructure( final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		final edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[0];

		Runnable textStringRunnable = new Runnable() {
			public void run() {
				ElementPrototype elementPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.response.Print.class, known, new String[] { "text" } );
				java.awt.Frame jAliceFrame = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame();
				String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Enter text to print:", "Enter Text String", javax.swing.JOptionPane.PLAIN_MESSAGE );
				if( text != null ) {
					((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( "text", text ) ) )).run();
				}
			}
		};
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "text string...", textStringRunnable ) );

		ElementPrototype elementPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.response.Print.class, known, new String[] { "object" } );
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "object", PopupMenuUtilities.makePrototypeStructure( elementPrototype, factory, context ) ) );

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makeQuestionPrintStructure( final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		final edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[0];

		Runnable textStringRunnable = new Runnable() {
			public void run() {
				ElementPrototype elementPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, known, new String[] { "text" } );
				java.awt.Frame jAliceFrame = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getJAliceFrame();
				String text = edu.cmu.cs.stage3.swing.DialogManager.showInputDialog( "Enter text to print:", "Enter Text String", javax.swing.JOptionPane.PLAIN_MESSAGE );
				if( text != null ) {
					((Runnable)factory.createItem( elementPrototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( "text", text ) ) )).run();
				}
			}
		};
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "text string...", textStringRunnable ) );

		ElementPrototype elementPrototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.userdefined.Print.class, known, new String[] { "object" } );
		structure.add( new edu.cmu.cs.stage3.util.StringObjectPair( "object", PopupMenuUtilities.makePrototypeStructure( elementPrototype, factory, context ) ) );

		return structure;
	}

	/**
	 * the PopupItemFactory should accept a completed ElementPrototype and return a Runnable
	 */
	public static java.util.Vector makePropertyValueStructure( final edu.cmu.cs.stage3.alice.core.Element element, Class valueClass, final PopupItemFactory factory, edu.cmu.cs.stage3.alice.core.Element context ) {
		java.util.Vector structure = new java.util.Vector();

		Class elementClass = null;
		if( element instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
			elementClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
		} else {
			elementClass = element.getClass();
		}

		edu.cmu.cs.stage3.util.StringObjectPair[] known = new edu.cmu.cs.stage3.util.StringObjectPair[] { new edu.cmu.cs.stage3.util.StringObjectPair( "element", element ) };
		String[] desired = new String[] { "propertyName" };
		ElementPrototype prototype = new ElementPrototype( edu.cmu.cs.stage3.alice.core.question.PropertyValue.class, known, desired );

		String[] propertyNames = getPropertyNames( elementClass, valueClass );
		String prefix = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( element, false ) + ".";
		for( int i = 0; i < propertyNames.length; i++ ) {
			if( (! propertyNames[i].equals( "visualization" )) && (! propertyNames[i].equals( "isFirstClass" )) ) { // HACK suppression
				String propertyName = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( propertyNames[i], false );
				structure.add( new StringObjectPair( prefix + propertyName, factory.createItem( prototype.createCopy( new edu.cmu.cs.stage3.util.StringObjectPair( "propertyName", propertyName ) ) ) ) );
			}
		}

		return structure;
	}

	// HACK until this method is on Element
	private static String[] getPropertyNames( Class elementClass, Class valueClass ) {
		try {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)elementClass.newInstance();
			edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
			java.util.Vector propertyNames = new java.util.Vector();
			for( int i = 0; i < properties.length; i++ ) {
				if( valueClass.isAssignableFrom( properties[i].getValueClass() ) ) {
					propertyNames.add( properties[i].getName() );
				}
			}
			return (String[])propertyNames.toArray( new String[0] );
		} catch( InstantiationException ie ) {
			return null;
		} catch( IllegalAccessException iae ) {
			return null;
		}
	}

	public static java.util.Vector getDefaultValueStructureForProperty( Class elementClass, String propertyName ) {
		return getDefaultValueStructureForProperty( elementClass, propertyName, null );
	}

	public static java.util.Vector getDefaultValueStructureForProperty( edu.cmu.cs.stage3.alice.core.Property property ) {
		return getDefaultValueStructureForProperty( property.getOwner().getClass(), property.getName(), property );
	}

	// property may be null if it is not available.  if it is available, though, it will be used to derive the value class.
	public static java.util.Vector getDefaultValueStructureForProperty( Class elementClass, String propertyName, edu.cmu.cs.stage3.alice.core.Property property ) {
		java.util.Vector structure = new java.util.Vector( getUnlabeledDefaultValueStructureForProperty( elementClass, propertyName, property ) );
		addLabelsToValueStructure( structure, elementClass, propertyName );
		return structure;
	}

	// property may be null if it is not available.  if it is available, though, it will be used to derive the value class.
	public static java.util.Vector getUnlabeledDefaultValueStructureForProperty( Class elementClass, String propertyName, edu.cmu.cs.stage3.alice.core.Property property ) {
		if( property != null ) {
			if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) && property.getName().equals( "value" ) ) {
				edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation)property.getOwner();
				elementClass = propertyAnimation.element.getElementValue().getClass();
				if (propertyAnimation.element.getElementValue()instanceof edu.cmu.cs.stage3.alice.core.Variable){
					edu.cmu.cs.stage3.alice.core.Variable var = (edu.cmu.cs.stage3.alice.core.Variable)propertyAnimation.element.getElementValue();
					elementClass = var.getValueClass();
				}
				propertyName = propertyAnimation.propertyName.getStringValue();
				
			} else if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment) && property.getName().equals( "value" ) ) {
				edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment propertyAssignment = (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment)property.getOwner();
				elementClass = propertyAssignment.element.getElementValue().getClass();
				if (propertyAssignment.element.getElementValue() instanceof edu.cmu.cs.stage3.alice.core.Variable){
					edu.cmu.cs.stage3.alice.core.Variable var = (edu.cmu.cs.stage3.alice.core.Variable)propertyAssignment.element.getElementValue();
					elementClass = var.getValueClass();
				}
				propertyName = propertyAssignment.propertyName.getStringValue();
			}
		}
		java.util.Vector structure = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDefaultPropertyValues( elementClass, propertyName );
		if( structure == null ) {
			structure = new java.util.Vector();
		}
		if( structure.size() < 1 ) {
			Class valueClass;
			if( property != null ) {
				if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) && (property.getName().equals( "a" ) || property.getName().equals( "b" )) ) {
					Object otherValue;
					if( property.getName().equals( "a" ) ) {
						otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).b.get();
					} else {
						otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).a.get();
					}

					if( otherValue instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
						valueClass = ((edu.cmu.cs.stage3.alice.core.Expression)otherValue).getValueClass();
					} else if( otherValue != null ) {
						valueClass = otherValue.getClass();
					} else {
						valueClass = property.getValueClass();
					}
				} else {
					valueClass = property.getValueClass();
				}
			} else {
				valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementClass, propertyName );
			}
			structure.addAll( getUnlabeledDefaultValueStructureForClass( valueClass ) );
		}

		return structure;
	}

	public static java.util.Vector getDefaultValueStructureForCollectionIndexProperty( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		Object collection = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "list" ) ) {
				collection = knownPropertyValues[i].getObject();
				break;
			} else if( knownPropertyValues[i].getString().equals( "array" ) ) {
				collection = knownPropertyValues[i].getObject();
				break;
			}
		}

		edu.cmu.cs.stage3.alice.core.Collection realCollection = null;
		if( collection instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
			realCollection = (edu.cmu.cs.stage3.alice.core.Collection)((edu.cmu.cs.stage3.alice.core.Variable)collection).getValue();
		} else if( collection instanceof edu.cmu.cs.stage3.alice.core.Collection ) {
			realCollection = (edu.cmu.cs.stage3.alice.core.Collection)collection;
		}

		java.util.Vector structure = new java.util.Vector();
		if( realCollection != null ) {
			int size = realCollection.values.size();
			for( int i = 0; (i < size) && (i < 10); i++ ) {
				structure.add( new StringObjectPair( Integer.toString( i ), new Double( (double)i ) ) );
			}
		}

		if( structure.size() < 1 ) {
			structure.add( new StringObjectPair( "0", new Double( 0.0 ) ) );
		}

		return structure;
	}

//	public static java.util.Vector getDefaultValueStructureForListIndexProperty( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
//		Object list = null;
//		for( int i = 0; i < knownPropertyValues.length; i++ ) {
//			if( knownPropertyValues[i].getString().equals( "list" ) ) {
//				list = knownPropertyValues[i].getObject();
//				break;
//			}
//		}
//
//		edu.cmu.cs.stage3.alice.core.List realList = null;
//		if( list instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
//			realList = (edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)list).getValue();
//		} else if( list instanceof edu.cmu.cs.stage3.alice.core.List ) {
//			realList = (edu.cmu.cs.stage3.alice.core.List)list;
//		}
//
//		java.util.Vector structure = new java.util.Vector();
//		if( realList != null ) {
//			int size = realList.values.size();
//			for( int i = 0; (i < size) && (i < 10); i++ ) {
//				structure.add( new StringObjectPair( Integer.toString( i ), new Double( (double)i ) ) );
//			}
//		}
//
//		if( structure.size() < 1 ) {
//			structure.add( new StringObjectPair( "0", new Double( 0.0 ) ) );
//		}
//
//		return structure;
//	}
//
//	public static java.util.Vector getDefaultValueStructureForArrayIndexProperty( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
//		Object array = null;
//		for( int i = 0; i < knownPropertyValues.length; i++ ) {
//			if( knownPropertyValues[i].getString().equals( "array" ) ) {
//				array = knownPropertyValues[i].getObject();
//				break;
//			}
//		}
//
//		edu.cmu.cs.stage3.alice.core.Array realArray = null;
//		if( array instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
//			realArray = (edu.cmu.cs.stage3.alice.core.Array)((edu.cmu.cs.stage3.alice.core.Variable)array).getValue();
//		} else if( array instanceof edu.cmu.cs.stage3.alice.core.Array ) {
//			realArray = (edu.cmu.cs.stage3.alice.core.Array)array;
//		}
//
//		java.util.Vector structure = new java.util.Vector();
//		if( realArray != null ) {
//			int size = realArray.values.size();
//			for( int i = 0; (i < size) && (i < 10); i++ ) {
//				structure.add( new StringObjectPair( Integer.toString( i ), new Double( (double)i ) ) );
//			}
//		}
//
//		if( structure.size() < 1 ) {
//			structure.add( new StringObjectPair( "0", new Double( 0.0 ) ) );
//		}
//
//		return structure;
//	}

	public static java.util.Vector getDefaultValueStructureForClass( Class valueClass ) {
		java.util.Vector structure = getUnlabeledDefaultValueStructureForClass( valueClass );
		addLabelsToValueStructure( structure );
		return structure;
	}

	public static java.util.Vector getUnlabeledDefaultValueStructureForClass( Class valueClass ) {
		java.util.Vector structure = new java.util.Vector();

		if( Boolean.class.isAssignableFrom( valueClass ) ) {
			structure.add( Boolean.TRUE );
			structure.add( Boolean.FALSE );
		} else if( edu.cmu.cs.stage3.alice.scenegraph.Color.class.isAssignableFrom( valueClass ) ) {
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.RED );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.PINK );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY );
			structure.add( edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY );
		} else if( Number.class.isAssignableFrom( valueClass ) ) {
			structure.add( new Double( .25 ) );
			structure.add( new Double( .5 ) );
			structure.add( new Double( 1.0 ) );
			structure.add( new Double( 2.0 ) );
		} else if( edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom( valueClass ) ) {
			edu.cmu.cs.stage3.util.Enumerable[] enumItems = edu.cmu.cs.stage3.util.Enumerable.getItems( valueClass );
			for( int i = 0; i < enumItems.length; i++ ) {
				structure.add( enumItems[i] );
			}
		} else if( edu.cmu.cs.stage3.alice.core.Style.class.isAssignableFrom( valueClass ) ) {
			structure.add( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY );
			structure.add( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY );
			structure.add( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY );
			structure.add( edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY);
		} else if( edu.cmu.cs.stage3.math.Vector3.class.isAssignableFrom( valueClass ) ) {
			structure.add( new edu.cmu.cs.stage3.math.Vector3( 0.0, 0.0, 0.0 ) );
		} else if( edu.cmu.cs.stage3.math.Vector4.class.isAssignableFrom( valueClass ) ) {
			structure.add( new edu.cmu.cs.stage3.math.Vector4( 0.0, 0.0, 0.0, 0.0 ) );
		} else if( edu.cmu.cs.stage3.math.Matrix33.class.isAssignableFrom( valueClass ) ) {
			structure.add( edu.cmu.cs.stage3.math.Matrix33.IDENTITY );
		} else if( edu.cmu.cs.stage3.math.Matrix44.class.isAssignableFrom( valueClass ) ) {
			structure.add( edu.cmu.cs.stage3.math.Matrix44.IDENTITY );
		} else if( edu.cmu.cs.stage3.math.Quaternion.class.isAssignableFrom( valueClass ) ) {
			structure.add( new edu.cmu.cs.stage3.math.Quaternion() );
		} else if( String.class.isAssignableFrom( valueClass ) ) {
			structure.add( "default string" );
		}

		return structure;
	}

	// have to special-case PropertyAnimations
	private static java.util.Vector getDefaultValueStructureForPropertyAnimation( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		java.util.Vector structure = new java.util.Vector();
		edu.cmu.cs.stage3.alice.core.Element element = null;
		String propertyName = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "element" ) ) {
				element = (edu.cmu.cs.stage3.alice.core.Element)knownPropertyValues[i].getObject();
				break;
			}
		}
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "propertyName" ) ) {
				propertyName = (String)knownPropertyValues[i].getObject();
				break;
			}
		}
		if( (element != null) && (propertyName != null) ) {
			Class elementClass = element.getClass();
			if( element instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
			}
			structure = getDefaultValueStructureForProperty( elementClass, propertyName, element.getPropertyNamed( propertyName ) );
		}

		return structure;
	}

	private static void addLabelsToValueStructure( java.util.Vector structure ) {
		for( java.util.ListIterator iter = structure.listIterator(); iter.hasNext(); ) {
			Object item = iter.next();
			if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)item;
				if( sop.getObject() instanceof java.util.Vector ) {
					addLabelsToValueStructure( (java.util.Vector)sop.getObject() );
				}
			} else if( item instanceof java.util.Vector ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unexpected Vector found while processing value structure", null );
			} else {
				String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( item );
				iter.set( new edu.cmu.cs.stage3.util.StringObjectPair( text, item ) );
			}
		}
	}

	private static void addLabelsToValueStructure( java.util.Vector structure, Class elementClass, String propertyName ) {
		for( java.util.ListIterator iter = structure.listIterator(); iter.hasNext(); ) {
			Object item = iter.next();
			if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)item;
				if( sop.getObject() instanceof java.util.Vector ) {
					addLabelsToValueStructure( (java.util.Vector)sop.getObject(), elementClass, propertyName );
				}
			} else if( item instanceof java.util.Vector ) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unexpected Vector found while processing value structure", null );
			} else {
				String text = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( item, elementClass, propertyName, "menuContext" );
				iter.set( new edu.cmu.cs.stage3.util.StringObjectPair( text, item ) );
			}
		}
	}

	private static java.util.Vector processStructure( java.util.Vector structure, edu.cmu.cs.stage3.alice.authoringtool.util.PopupItemFactory factory, Object currentValue ) {
		java.util.Vector processed = new java.util.Vector();
		for( java.util.Iterator iter = structure.iterator(); iter.hasNext(); ) {
			Object item = iter.next();
			if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)item;
				if( sop.getObject() instanceof java.util.Vector ) {
					processed.add( new edu.cmu.cs.stage3.util.StringObjectPair( sop.getString(), processStructure( (java.util.Vector)sop.getObject(), factory, currentValue ) ) );
				} else {
					if( ((currentValue == null) && (sop.getObject() == null)) || ((currentValue != null) && currentValue.equals( sop.getObject() )) ) {
						processed.add( new edu.cmu.cs.stage3.util.StringObjectPair( sop.getString(), new PopupItemWithIcon( factory.createItem( sop.getObject() ), currentValueIcon ) ) );
					} else {
						processed.add( new edu.cmu.cs.stage3.util.StringObjectPair( sop.getString(), factory.createItem( sop.getObject() ) ) );
					}
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unexpected Vector found while processing value structure", null );
			}
		}
		return processed;
	}

	public static boolean structureContains( java.util.Vector structure, Object value ) {
		for( java.util.Iterator iter = structure.iterator(); iter.hasNext(); ) {
			Object item = iter.next();
			if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)item;
				if( sop.getObject() instanceof java.util.Vector ) {
					if( structureContains( (java.util.Vector)sop.getObject(), value ) ) {
						return true;
					}
				} else if( sop.getObject() == null ) {
					if( value == null ) {
						return true;
					}
				} else {
					if( sop.getObject().equals( value ) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// have to special-case PropertyAnimations
	private static Class getValueClassForPropertyAnimation( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		Class valueClass = null;
		edu.cmu.cs.stage3.alice.core.Element element = null;
		String propertyName = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "element" ) ) {
				element = (edu.cmu.cs.stage3.alice.core.Element)knownPropertyValues[i].getObject();
				break;
			}
		}
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "propertyName" ) ) {
				propertyName = (String)knownPropertyValues[i].getObject();
				break;
			}
		}
		if( (element instanceof edu.cmu.cs.stage3.alice.core.Variable) && ("value".equals( propertyName )) ) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Variable)element).getValueClass();
		} else if( (element != null) && (propertyName != null) ) {
			Class elementClass = element.getClass();
			if( element instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression)element).getValueClass();
			}
			valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed( elementClass, propertyName );
		}

		return valueClass;
	}

	private static Class getValueClassForList( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		Class valueClass = null;
		Object list = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "list" ) ) {
				list = knownPropertyValues[i].getObject();
				break;
			}
		}
		if( list instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
			edu.cmu.cs.stage3.alice.core.List realList = (edu.cmu.cs.stage3.alice.core.List)((edu.cmu.cs.stage3.alice.core.Variable)list).getValue();
			if (realList != null){
				valueClass = realList.valueClass.getClassValue();
			}
		} else if( list instanceof edu.cmu.cs.stage3.alice.core.List ) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.List)list).valueClass.getClassValue();
		} else { // bail
			valueClass = Object.class;
		}

		return valueClass;
	}

	private static Class getValueClassForArray( edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues ) {
		Class valueClass = null;
		Object array = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "array" ) ) {
				array = knownPropertyValues[i].getObject();
				break;
			}
		}

		if( array instanceof edu.cmu.cs.stage3.alice.core.Variable ) {
			edu.cmu.cs.stage3.alice.core.Array realArray = (edu.cmu.cs.stage3.alice.core.Array)((edu.cmu.cs.stage3.alice.core.Variable)array).getValue();
			valueClass = realArray.valueClass.getClassValue();
		} else if( array instanceof edu.cmu.cs.stage3.alice.core.Array ) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Array)array).valueClass.getClassValue();
		} else { // bail
			valueClass = Object.class;
		}

		return valueClass;
	}
	
	private static Class generalizeValueClass(Class valueClass){
		Class newValueClass = valueClass;
		if (java.lang.Number.class.isAssignableFrom(valueClass)){
			newValueClass = java.lang.Number.class;
		} else if (edu.cmu.cs.stage3.alice.core.Model.class.isAssignableFrom(valueClass)){
			newValueClass = edu.cmu.cs.stage3.alice.core.Model.class;
		}
		return newValueClass;
	}
	
	private static Class getValueClassForComparator(edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues){
		Class valueClass = null;
		Object operand = null;
		for( int i = 0; i < knownPropertyValues.length; i++ ) {
			if( knownPropertyValues[i].getString().equals( "a" ) ) {
				operand = knownPropertyValues[i].getObject();
				break;
			}
		}
		if( operand instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
			valueClass = ((edu.cmu.cs.stage3.alice.core.Expression)operand).getValueClass();
		} else if( operand != null ) {
			valueClass = operand.getClass();
		} 
		return generalizeValueClass(valueClass);
	}
	
	public static Class getDesiredValueClass(edu.cmu.cs.stage3.alice.core.Property property){
		Class targetValueClass = property.getValueClass();
		if( (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion) && (property.getName().equals( "a" ) || property.getName().equals( "b" )) ) {
			Object otherValue;
			Object ourValue;
			if( property.getName().equals( "a" ) ) {
				otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).b.get();
				ourValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).a.get();
			} else {
				otherValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).a.get();
				ourValue = ((edu.cmu.cs.stage3.alice.core.question.BinaryObjectResultingInBooleanQuestion)property.getOwner()).b.get();
			}
			Class otherValueClass;
			if( otherValue instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
				otherValueClass = ((edu.cmu.cs.stage3.alice.core.Expression)otherValue).getValueClass();
			} else if( otherValue != null ) {
				otherValueClass = otherValue.getClass();
			} else {
				otherValueClass = property.getValueClass();
			}
			if( ourValue instanceof edu.cmu.cs.stage3.alice.core.Expression ) {
				targetValueClass = ((edu.cmu.cs.stage3.alice.core.Expression)ourValue).getValueClass();
			} else if( ourValue != null ) {
				targetValueClass = ourValue.getClass();
			} else {
				targetValueClass = property.getValueClass();
			}
			if (targetValueClass != otherValueClass){
				targetValueClass = otherValueClass;
			}
			targetValueClass = generalizeValueClass(targetValueClass);
		}
		return targetValueClass;
	}

	public static java.util.Vector makeDefaultOneShotStructure( edu.cmu.cs.stage3.alice.core.Element element ) {
//		return makeResponseStructure( element, oneShotFactory, edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld() );
		return makeResponseStructure( element, oneShotFactory, element.getRoot() );
	}

	public static void ensurePopupIsOnScreen( JPopupMenu popup ) {
		java.awt.Point location = popup.getLocation( null );
		java.awt.Dimension size = popup.getSize( null );
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar

		javax.swing.SwingUtilities.convertPointToScreen( location, popup );

		if( location.x < 0 ) {
			location.x = 0;
		} else if( location.x + size.width > screenSize.width ) {
			location.x -= (location.x + size.width) - screenSize.width;
		}
		if( location.y < 0 ) {
			location.y = 0;
		} else if( location.y + size.height > screenSize.height ) {
			location.y -= (location.y + size.height) - screenSize.height;
		}

		popup.setLocation( location );
	}

	public static java.awt.event.ActionListener getPopupMenuItemActionListener( Runnable runnable ) {
		return new PopupMenuItemActionListener( runnable );
	}

	public static javax.swing.JPopupMenu makeDisabledPopup( String s ) {
		javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
		javax.swing.JMenuItem item = makeMenuItem( s, null );
		item.setEnabled( false );
		popup.add( item );
		return popup;
	}

	public static edu.cmu.cs.stage3.util.Criterion getAvailableExpressionCriterion( Class valueClass, edu.cmu.cs.stage3.alice.core.Element context ) {
		//DEBUG System.out.println( "getAvailableExpressionCriterion( " + valueClass + ", " + context + " )" );
		if( context == null ) { //SERIOUS HACK
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error: null context while looking for expressions; using World", null );
			context = edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.getHack().getWorld();
		}
		edu.cmu.cs.stage3.util.Criterion isAccessible = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAccessibleFromCriterion( context );
		edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion isAssignable = new edu.cmu.cs.stage3.alice.core.criterion.ExpressionIsAssignableToCriterion( valueClass );
		return new edu.cmu.cs.stage3.util.criterion.MatchesAllCriterion( new edu.cmu.cs.stage3.util.Criterion[] { isNotActualParameter, isAccessible, isNamedElement, isAssignable } );
	}

	public static void printStructure( java.util.Vector structure ) {
		printStructure( structure, 0 );
	}

	private static void printStructure( java.util.Vector structure, int indent ) {
		String tabs = "";
		for( int i = 0; i < indent; i++ ) {
			tabs = tabs + "\t";
		}
		for( java.util.Iterator iter = structure.iterator(); iter.hasNext(); ) {
			Object item = iter.next();
			if( item instanceof edu.cmu.cs.stage3.util.StringObjectPair ) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair)item;
				if( sop.getObject() instanceof java.util.Vector ) {
					printStructure( (java.util.Vector)sop.getObject(), indent + 1 );
				} else {
					System.out.println( tabs + sop.getString() + " : " + sop.getObject() );
				}
			} else {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "unexpected object found while printing structure: " + item, null );
			}
		}
	}
}

class PopupMenuItemActionListener implements java.awt.event.ActionListener {
	protected Runnable runnable;
	protected javax.swing.JMenu menu; //MEMFIX

	public PopupMenuItemActionListener( Runnable runnable ) {
		this.runnable = runnable;
	}

	public PopupMenuItemActionListener( Runnable runnable, javax.swing.JMenu menu ) { //MEMFIX
		this.runnable = runnable;
		this.menu = menu;
	}

	public void actionPerformed( java.awt.event.ActionEvent e ) {
		try {
			runnable.run();
			if( menu != null ) { //MEMFIX
				menu.getPopupMenu().setInvoker( null ); //MEMFIX
			}
			//runnable = null; //MEMFIX
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error encountered while responding to popup menu item.", t );
		}
	}
}

class PopupMenuItemCallbackActionListener implements java.awt.event.ActionListener {
	protected edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback;
	protected Object context;
	protected Object source;

	public PopupMenuItemCallbackActionListener( edu.cmu.cs.stage3.alice.authoringtool.util.Callback callback, Object context, Object source ) {
		this.callback = callback;
		this.context = context;
		this.source = source;
	}

	public void actionPerformed( java.awt.event.ActionEvent e ) {
		callback.callback( context, source );
	}
}
