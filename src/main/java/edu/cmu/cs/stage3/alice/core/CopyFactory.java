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

import edu.cmu.cs.stage3.util.HowMuch;
import edu.cmu.cs.stage3.util.Criterion;
import edu.cmu.cs.stage3.progress.ProgressObserver;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.alice.core.reference.ObjectArrayPropertyReference;
import edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty;

class CopyReferenceGenerator extends edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceGenerator {
	private Class[] m_classesToShare;
	public CopyReferenceGenerator( Element internalRoot, Class[] classesToShare ) {
		super( internalRoot );
		m_classesToShare = classesToShare;
	}
	
	protected boolean isExternal( Element element ) {
		if( element.isAssignableToOneOf( m_classesToShare ) ) {
			return true;
		}
		return super.isExternal( element );
	}
}

class VariableCriterion implements edu.cmu.cs.stage3.util.Criterion {
	private edu.cmu.cs.stage3.util.Criterion m_wrappedCriterion;
	private String m_name;
	public VariableCriterion( String name, edu.cmu.cs.stage3.util.Criterion wrappedCriterion ) {
		m_name = name;
		m_wrappedCriterion = wrappedCriterion;
	}
	public String getName() {
		return m_name;
	}
	public edu.cmu.cs.stage3.util.Criterion getWrappedCriterion() {
		return m_wrappedCriterion;
	}
	public boolean accept( Object o ) {
		if( o instanceof Variable  ) {
			Variable variable = (Variable)o;
			if( m_name != null ) {
				return m_name.equalsIgnoreCase( variable.name.getStringValue() );
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "VariableCriterion["+m_name+"]";
	}
	
}

class CodeCopyReferenceGenerator extends CopyReferenceGenerator {
	public CodeCopyReferenceGenerator( Element internalRoot, Class[] classesToShare ) {
		super( internalRoot, classesToShare );
	}
	
	public edu.cmu.cs.stage3.util.Criterion generateReference( edu.cmu.cs.stage3.alice.core.Element element ) {
		edu.cmu.cs.stage3.util.Criterion criterion = super.generateReference( element ); 
		if( element instanceof Variable ) {
			Element parent = element.getParent();
			if( parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox ) {
				//pass
			} else {
				criterion = new VariableCriterion( element.name.getStringValue(), criterion );
			}
		}
		return criterion;
	}
}
//class CodeCopyReferenceResolver extends edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver {
//	private Element m_parentToBe;
//	public CodeCopyReferenceResolver( Element internalRoot, Element externalRoot, Element parentToBe ) {
//		super( internalRoot, externalRoot );
//		m_parentToBe = parentToBe;
//	}
//	private Element lookup( Element element, VariableCriterion variableCriterion ) {
//		System.err.println( "lookup: " + element );
//		if( element != null ) {
//			for( int i=0; i<element.getChildCount(); i++ ) {
//				Element child = element.getChildAt( i );
//				if( variableCriterion.accept( child ) ) {
//					return child;
//				}
//			}
//			return lookup( element.getParent(), variableCriterion ); 			
//		} else {
//			return null;
//		}
//	}
//	public Element resolveReference( edu.cmu.cs.stage3.util.Criterion criterion ) throws edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException {
//		if( criterion instanceof VariableCriterion ) {
//			VariableCriterion variableCriterion = (VariableCriterion)criterion;
//			Element variable = lookup( m_parentToBe, variableCriterion );
//			if( variable == null ) {
//				throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException( variableCriterion, "could not resolve variable criterion: " + variableCriterion );
//			} else {
//				return variable;
//			}
//		} else {
//			return super.resolveReference( criterion );
//		}
//	}
//}
public class CopyFactory {
	private class ElementCapsule {
		private class PropertyCapsule {
			private String m_name;
			private Object m_value;
			private PropertyCapsule( Property property, ReferenceGenerator referenceGenerator ) {
				m_name = property.getName();
				if( property instanceof ObjectArrayProperty ) {
					ObjectArrayProperty oap = (ObjectArrayProperty)property;
					if( oap.get() != null ) {
						Object[] array = new Object[ oap.size() ];
						for( int i=0; i<oap.size(); i++ ) {
							array[ i ] = getValueToTuckAway( oap.get( i ), referenceGenerator );
						}
						m_value = array;
					} else {
						m_value = null;
					}
				} else {
					m_value = getValueToTuckAway( property.get(), referenceGenerator );
				}
			}
            private Object getCopyIfPossible( Object o ) {
                if( o instanceof Cloneable ) {
                    //todo
                    //return o.clone();
                    //check for constructor that takes object of this class, too
                    return o;
                } else {
                    return o;
                }
            }
            private Object getValueToTuckAway( Object value, ReferenceGenerator referenceGenerator ) {
                if( value instanceof Element ) {
                    return referenceGenerator.generateReference( (Element)value );
                } else {
                    return getCopyIfPossible( value );
                }
            }
            private Object getValueForCopy( Property property, Object value, java.util.Vector referencesToBeResolved ) {
                if( value instanceof Criterion ) {
                    referencesToBeResolved.addElement( new PropertyReference( property, (Criterion)value ) );
                    return null;
                } else {
                    return getCopyIfPossible( value );
                }
            }
            private Object getValueForCopy( ObjectArrayProperty oap, Object value, int i, java.util.Vector referencesToBeResolved ) {
                if( value instanceof Criterion ) {
                    referencesToBeResolved.addElement( new ObjectArrayPropertyReference( oap, (Criterion)value, i, 0 ) );
                    return null;
                } else {
                    return getCopyIfPossible( value );
                }
            }

			public void set( Element element, java.util.Vector referencesToBeResolved ) {
				Property property = element.getPropertyNamed( m_name );
				if( property instanceof ObjectArrayProperty ) {
                    ObjectArrayProperty oap = (ObjectArrayProperty)property;
					if( m_value != null ) {
						Object[] src = (Object[])m_value;
						Object[] dst = (Object[])java.lang.reflect.Array.newInstance( oap.getComponentType(), src.length );
						for( int i=0; i<src.length; i++ ) {
							dst[ i ] = getValueForCopy( oap, src[ i ], i, referencesToBeResolved );
						}
						oap.set( dst );
					} else {
						oap.set( null );
					}
				} else {
                    property.set( getValueForCopy( property, m_value, referencesToBeResolved ) );
				}
			}
		}
		private Class m_cls;
		private PropertyCapsule[] m_propertyCapsules;
		private ElementCapsule[] m_childCapsules;
		private ElementCapsule( Element element, ReferenceGenerator referenceGenerator, Class[] classesToShare, HowMuch howMuch ) {
			m_cls = element.getClass();

			//todo: handle howMuch
			Element[] elementChildren = element.getChildren();
			m_childCapsules = new ElementCapsule[ elementChildren.length ];
			for( int i=0; i<m_childCapsules.length; i++ ) {
				if( elementChildren[ i ].isAssignableToOneOf( classesToShare ) ) {
					m_childCapsules[ i ] = null;
				} else {
					m_childCapsules[ i ] = new ElementCapsule( elementChildren[ i ], referenceGenerator, classesToShare, howMuch );
				}
			}

			Property[] elementProperties = element.getProperties();
			m_propertyCapsules = new PropertyCapsule[ elementProperties.length ];
			for( int i=0; i<m_propertyCapsules.length; i++ ) {
				m_propertyCapsules[ i ] = new PropertyCapsule( elementProperties[ i ], referenceGenerator );
			}
		}

		private String getName() {
			for( int i=0; i<m_propertyCapsules.length; i++ ) {
				PropertyCapsule propertyCapsule = m_propertyCapsules[ i ];
				if( propertyCapsule.m_name.equals( "name" ) ) {
					return (String)propertyCapsule.m_value;
				}
			}
			return null;
		}
		private Element internalManufacture( java.util.Vector referencesToBeResolved ) {
			Element element;
			try {
				element = (Element)m_cls.newInstance();
			} catch( Throwable t ) {
				throw new RuntimeException();
			}
			for( int i=0; i<m_childCapsules.length; i++ ) {
				if( m_childCapsules[ i ] != null ) {
					element.addChild( m_childCapsules[ i ].internalManufacture( referencesToBeResolved ) );
				}
			}
			for( int i=0; i<m_propertyCapsules.length; i++ ) {
				m_propertyCapsules[ i ].set( element, referencesToBeResolved );
			}
			return element;
		}

		private Element lookup( Element element, VariableCriterion variableCriterion ) {
			if( element != null ) {
				for( int i=0; i<element.getChildCount(); i++ ) {
					Element child = element.getChildAt( i );
					if( variableCriterion.accept( child ) ) {
						return child;
					}
				}
				return lookup( element.getParent(), variableCriterion ); 			
			} else {
				return null;
			}
		}
		//todo: update progressObserver
		private Element manufacture( ReferenceResolver referenceResolver, ProgressObserver progressObserver, Element parentToBe ) throws UnresolvablePropertyReferencesException {
			java.util.Vector referencesToBeResolved = new java.util.Vector();
			Element element = internalManufacture( referencesToBeResolved );
			java.util.Vector referencesLeftUnresolved = new java.util.Vector();
			element.setParent( parentToBe );
			try {
				if( referenceResolver instanceof edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver ) {
					edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver drr = (edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver)referenceResolver;
					if( drr.getInternalRoot() == null ) {
						drr.setInternalRoot( element );
					}
				}
				java.util.Enumeration enum0 = referencesToBeResolved.elements();
				while( enum0.hasMoreElements() ) {
					PropertyReference propertyReference = ((PropertyReference)enum0.nextElement());
					try {
						Criterion criterion = propertyReference.getCriterion();
						if( criterion instanceof VariableCriterion ) {
							VariableCriterion variableCriterion = (VariableCriterion)criterion;
							Element variable = lookup( propertyReference.getProperty().getOwner(), variableCriterion );
							if( variable != null ) {
								propertyReference.getProperty().set( variable );
							} else {
								//System.err.println("Cannot make a copy of this item. Try again using the clipboard");
								//throw new edu.cmu.cs.stage3.alice.core.UnresolvableReferenceException( variableCriterion, "could not resolve variable criterion: " + variableCriterion );
							}
						} else {
							propertyReference.resolve( referenceResolver );
						}
					} catch( UnresolvableReferenceException ure ) {
						referencesLeftUnresolved.addElement( propertyReference );
					}
				}
			} finally {
				element.setParent( null );
			}
			if( referencesLeftUnresolved.size() > 0 ) {
				PropertyReference[] propertyReferences = new PropertyReference[ referencesLeftUnresolved.size() ];
				referencesLeftUnresolved.copyInto( propertyReferences );
				StringBuffer sb = new StringBuffer();
				sb.append( "PropertyReferences: \n" );
				for( int i=0; i<propertyReferences.length; i++ ) {
					sb.append( propertyReferences[ i ] );
					sb.append( "\n" );
				}
				throw new UnresolvablePropertyReferencesException( propertyReferences, element, sb.toString() );
			}
			return element;
		}
	}
	
	private ElementCapsule m_capsule;
	private Class m_valueClass;
	private Class HACK_m_hackValueClass;
	public CopyFactory( Element element, Element internalReferenceRoot, Class[] classesToShare, HowMuch howMuch ) {
		ReferenceGenerator referenceGenerator;
		if( element instanceof Response || element instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.Component ) {
			referenceGenerator = new CodeCopyReferenceGenerator( internalReferenceRoot, classesToShare );
		} else {
			referenceGenerator = new CopyReferenceGenerator( internalReferenceRoot, classesToShare );
		}
		m_capsule = new ElementCapsule( element, referenceGenerator, classesToShare, howMuch );
		m_valueClass = element.getClass();
		HACK_m_hackValueClass = null;
		try {
			if( element instanceof Expression ) {
				HACK_m_hackValueClass = ((Expression)element).getValueClass();
			}
		} catch( Throwable t ) {
			//pass
		}
	}
	public Class getValueClass() {
		return m_valueClass;
	}
	public Class HACK_getExpressionValueClass() {
		return HACK_m_hackValueClass;
	}
	
	public Element manufactureCopy( ReferenceResolver referenceResolver, ProgressObserver progressObserver, Element parentToBe ) throws UnresolvablePropertyReferencesException {
		return m_capsule.manufacture( referenceResolver, progressObserver, parentToBe );
	}
	public Element manufactureCopy( Element externalRoot, Element internalRoot, ProgressObserver progressObserver, Element parentToBe ) throws UnresolvablePropertyReferencesException {
//		ReferenceResolver referenceResolver;
//		if( Response.class.isAssignableFrom( m_valueClass ) || edu.cmu.cs.stage3.alice.core.question.userdefined.Component.class.isAssignableFrom( m_valueClass ) ) {
//			referenceResolver = new CodeCopyReferenceResolver( internalRoot, externalRoot, parentToBe );
//		} else {
//			referenceResolver = ;
//		}
//		return manufactureCopy( referenceResolver, progressObserver, parentToBe );
		return manufactureCopy( new edu.cmu.cs.stage3.alice.core.reference.DefaultReferenceResolver( internalRoot, externalRoot ), progressObserver, parentToBe );
	}
	public Element manufactureCopy( Element externalRoot, Element internalRoot, ProgressObserver progressObserver ) throws UnresolvablePropertyReferencesException {
		return manufactureCopy( externalRoot, internalRoot, progressObserver, null );
	}
	public Element manufactureCopy( Element externalRoot, Element internalRoot ) throws UnresolvablePropertyReferencesException {
		return manufactureCopy( externalRoot, internalRoot, null );
	}
	public Element manufactureCopy( Element externalRoot ) throws UnresolvablePropertyReferencesException {
		return manufactureCopy( externalRoot, (Element)null );
	}
	
	public String toString() {
		return "edu.cmu.cs.stage3.alice.core.CopyFactory[" + m_valueClass + "]";
	}
}
