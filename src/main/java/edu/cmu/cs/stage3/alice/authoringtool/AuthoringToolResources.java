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

import java.awt.Component;
import java.io.File;

import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Transformable;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;
import edu.cmu.cs.stage3.util.StringObjectPair;
import edu.cmu.cs.stage3.util.StringTypePair;

/**
 * @author Jason Pratt
 */
public class AuthoringToolResources {
	public final static long startTime = System.currentTimeMillis();
	public final static String QUESTION_STRING = "function";
	public static edu.cmu.cs.stage3.util.Criterion characterCriterion = new edu.cmu.cs.stage3.util.Criterion() {
		@Override
		public boolean accept(Object o) {
			return o instanceof edu.cmu.cs.stage3.alice.core.Sandbox;
		}
	};
	public static java.io.FileFilter resourceFileFilter = new java.io.FileFilter() {
		public String getDescription() {
			return "resource files";
		}
		@Override
		public boolean accept(java.io.File file) {
			return file.isFile() && file.canRead() && file.getName().toLowerCase().endsWith(".py");
		}
	};

	// preferences
	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());

	public static class Resources implements java.io.Serializable {
		public java.util.Vector propertyStructure;
		public java.util.Vector oneShotStructure;
		public java.util.Vector questionStructure;
		public java.util.Vector worldTreeChildrenPropertiesStructure;
		public java.util.Vector behaviorParameterPropertiesStructure;
		public java.util.HashMap nameMap = new java.util.HashMap();
		public java.util.HashMap htmlNameMap = new java.util.HashMap();
		public java.util.HashMap formatMap = new java.util.HashMap();
		public java.util.HashMap propertyValueFormatMap = new java.util.HashMap();
		public java.util.HashMap unitMap = new java.util.HashMap();
		public Class[] classesToOmitNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToIncludeNoneFor;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertyNamesToOmit;
		public edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitScriptDefinedFor;
		public java.util.Vector defaultPropertyValuesStructure;
		public edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes;
		public String[] defaultAspectRatios;
		public Class[] behaviorClasses;
		public String[] parameterizedPropertiesToOmit;
		public String[] responsePropertiesToOmit;
		public String[] oneShotGroupsToInclude;
		public String[] questionPropertiesToOmit;
		public java.util.HashMap colorMap = new java.util.HashMap();
		public java.text.DecimalFormat decimalFormatter = new java.text.DecimalFormat("#0.##");
		public java.util.HashMap stringImageMap = new java.util.HashMap();
		public java.util.HashMap stringIconMap = new java.util.HashMap();
		public java.util.HashMap disabledIconMap = new java.util.HashMap();
		public Class[] importers;
		public Class[] editors;
		public java.util.HashMap flavorMap = new java.util.HashMap();
		public java.util.HashMap keyCodesToStrings = new java.util.HashMap();
		public boolean experimentalFeaturesEnabled;
		public java.util.HashMap miscMap = new java.util.HashMap();
		public java.net.URL mainWebGalleryURL = null;
		public java.io.File mainDiskGalleryDirectory = null;
		public java.io.File mainCDGalleryDirectory = null;
	}
	protected static Resources resources;

	protected static java.io.File resourcesDirectory;
	protected static java.io.File resourcesCacheFile;
	protected static java.io.File resourcesPyFile;
	protected static java.io.FilenameFilter pyFilenameFilter = new java.io.FilenameFilter() {
		@Override
		public boolean accept(java.io.File dir, String name) {
			return name.toLowerCase().endsWith(".py");
		}
	};

	static {
		resourcesDirectory = new java.io.File(JAlice.getAliceHomeDirectory(), "resources").getAbsoluteFile();
		resourcesCacheFile = new java.io.File(resourcesDirectory, "resourcesCache.bin").getAbsoluteFile();
		resourcesPyFile = new java.io.File(resourcesDirectory, authoringToolConfig.getValue("resourceFile")).getAbsoluteFile();
		if (!resourcesPyFile.canRead()) {
			resourcesPyFile = new java.io.File(resourcesDirectory, "Alice Style.py").getAbsoluteFile();
		}
		loadResourcesPy();
		//
		// if( isResourcesCacheCurrent() ) {
		// try {
		// loadResourcesCache();
		// } catch( Throwable t ) {
		// AuthoringTool.showErrorDialog(
		// "Unable to load resources cache.  Reloading resources from " +
		// resourcesPyFile.getAbsolutePath(), t );
		// try {
		// loadResourcesPy();
		// } catch( Throwable t2 ) {
		// AuthoringTool.showErrorDialog( "Unable to load resources from " +
		// resourcesPyFile.getAbsolutePath(), t2 );
		// }
		// deleteResourcesCache();
		// }
		// } else {
		// try {
		// loadResourcesPy();
		// } catch( Throwable t ) {
		// AuthoringTool.showErrorDialog( "Unable to load resources from " +
		// resourcesPyFile.getAbsolutePath(), t );
		// }
		// saveResourcesCache();
		// }
	}

	public static boolean safeIsDataFlavorSupported(java.awt.dnd.DropTargetDragEvent dtde, java.awt.datatransfer.DataFlavor flavor) {
		try {
			boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t) {
			return false;
		}
	}

	public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(java.awt.dnd.DropTargetDropEvent dtde) {
		try {
			return dtde.getCurrentDataFlavors();
		} catch (Throwable t) {
			return null;
		}
	}

	public static java.awt.datatransfer.DataFlavor[] safeGetCurrentDataFlavors(java.awt.dnd.DropTargetDragEvent dtde) {
		try {
			return dtde.getCurrentDataFlavors();
		} catch (Throwable t) {
			return null;
		}
	}

	public static boolean safeIsDataFlavorSupported(java.awt.dnd.DropTargetDropEvent dtde, java.awt.datatransfer.DataFlavor flavor) {
		try {
			boolean toReturn = dtde.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean safeIsDataFlavorSupported(java.awt.datatransfer.Transferable transferable, java.awt.datatransfer.DataFlavor flavor) {
		try {
			boolean toReturn = transferable.isDataFlavorSupported(flavor);
			return toReturn;
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean isResourcesCacheCurrent() {
		long cacheTime = resourcesCacheFile.exists() ? resourcesCacheFile.lastModified() : 0L;
		long mostCurrentPy = getMostCurrentPyTime(resourcesDirectory, 0L);

		return cacheTime > mostCurrentPy;
	}

	private static long getMostCurrentPyTime(java.io.File directory, long mostCurrentPy) {
		java.io.File[] files = directory.listFiles();
		for (File file : files) {
			if (pyFilenameFilter.accept(directory, file.getName())) {
				mostCurrentPy = Math.max(mostCurrentPy, file.lastModified());
			} else if (file.isDirectory()) {
				mostCurrentPy = Math.max(mostCurrentPy, getMostCurrentPyTime(file, mostCurrentPy));
			}
		}

		return mostCurrentPy;
	}

	public static void loadResourcesPy() {
		resources = new Resources();
		org.python.core.PySystemState.initialize();
		org.python.core.PySystemState pySystemState = org.python.core.Py.getSystemState();
		org.python.core.__builtin__.execfile(resourcesPyFile.getAbsolutePath(), pySystemState.builtins, pySystemState.builtins);
		AuthoringToolResources.initKeyCodesToStrings();
		initWebGalleryURL();
	}

	public static void loadResourcesCache() throws Exception {
		java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.BufferedInputStream(new java.io.FileInputStream(resourcesCacheFile)));
		resources = (Resources) ois.readObject();
		ois.close();
	}

	public static void saveResourcesCache() {
		try {
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.BufferedOutputStream(new java.io.FileOutputStream(resourcesCacheFile)));
			oos.writeObject(resources);
			oos.flush();
			oos.close();
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Unable to save resources cache to " + resourcesCacheFile.getAbsolutePath(), t);
		}
	}

	public static void deleteResourcesCache() {
		try {
			resourcesCacheFile.delete();
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Unable to delete resources cache " + resourcesCacheFile.getAbsolutePath(), t);
		}
	}

	public static void setPropertyStructure(java.util.Vector propertyStructure) {
		if (propertyStructure != null) {
			for (java.util.Iterator iter = propertyStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class.forName(className);
					} catch (java.lang.ClassNotFoundException e) {
						throw new IllegalArgumentException("propertyStructure error: " + className + " is not a Class");
					}
				} else {
					throw new IllegalArgumentException("Unexpected object found in propertyStructure: " + o);
				}
			}
		}

		AuthoringToolResources.resources.propertyStructure = propertyStructure;
	}

	public static java.util.Vector getPropertyStructure(Class elementClass) {
		if (AuthoringToolResources.resources.propertyStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.propertyStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (java.util.Vector) ((edu.cmu.cs.stage3.util.StringObjectPair) o).getObject();
						}
					} catch (java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in propertyStructure: " + o, null);
				}
			}
		}
		return null;
	}

	public static java.util.Vector getPropertyStructure(edu.cmu.cs.stage3.alice.core.Element element, boolean includeLeftovers) {
		java.util.Vector structure = getPropertyStructure(element.getClass());

		if (includeLeftovers && structure != null) {
			java.util.Vector usedProperties = new java.util.Vector();
			for (java.util.Iterator iter = structure.iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.util.StringObjectPair sop = (edu.cmu.cs.stage3.util.StringObjectPair) iter.next();
				java.util.Vector propertyNames = (java.util.Vector) sop.getObject();
				if (propertyNames != null) {
					for (java.util.Iterator jter = propertyNames.iterator(); jter.hasNext();) {
						String name = (String) jter.next();
						edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(name);
						if (property != null) {
							usedProperties.add(property);
						}
					}
				}
			}

			java.util.Vector leftovers = new java.util.Vector();
			edu.cmu.cs.stage3.alice.core.Property[] properties = element.getProperties();
			for (int i = 0; i < properties.length; i++) {
				if (!usedProperties.contains(properties[i])) {
					leftovers.add(properties[i].getName());
				}
			}

			if (leftovers.size() > 0) {
				structure.add(new edu.cmu.cs.stage3.util.StringObjectPair("leftovers", leftovers));
			}
		}

		return structure;
	}

	public static void setOneShotStructure(java.util.Vector oneShotStructure) {
		// validate structure
		if (oneShotStructure != null) {
			for (java.util.Iterator iter = oneShotStructure.iterator(); iter.hasNext();) {
				Object classChunk = iter.next();
				if (classChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) classChunk).getString();
					Object groups = ((edu.cmu.cs.stage3.util.StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (groups instanceof java.util.Vector) {
						for (java.util.Iterator jter = ((java.util.Vector) groups).iterator(); jter.hasNext();) {
							Object groupChunk = jter.next();
							if (groupChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
								Object responseClasses = ((edu.cmu.cs.stage3.util.StringObjectPair) groupChunk).getObject();
								if (responseClasses instanceof java.util.Vector) {
									for (java.util.Iterator kter = ((java.util.Vector) responseClasses).iterator(); kter.hasNext();) {
										Object className2 = kter.next();
										if (className2 instanceof String || className2 instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
											// do nothing
										} else {
											throw new IllegalArgumentException("oneShotStructure error: expected String or StringObjectPair, got: " + className);
										}
									}
								}
							} else {
								throw new IllegalArgumentException("Unexpected object found in oneShotStructure: " + groupChunk);
							}
						}
					} else {
						throw new IllegalArgumentException("oneShotStructure error: expected Vector, got: " + groups);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "oneShotStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException("Unexpected object found in oneShotStructure: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.oneShotStructure = oneShotStructure;
	}

	public static java.util.Vector getOneShotStructure(Class elementClass) {
		if (AuthoringToolResources.resources.oneShotStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.oneShotStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (java.util.Vector) ((edu.cmu.cs.stage3.util.StringObjectPair) o).getObject();
						}
					} catch (java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in oneShotStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void setQuestionStructure(java.util.Vector questionStructure) {
		// validate structure
		if (questionStructure != null) {
			for (java.util.Iterator iter = questionStructure.iterator(); iter.hasNext();) {
				Object classChunk = iter.next();
				if (classChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) classChunk).getString();
					Object groups = ((edu.cmu.cs.stage3.util.StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (groups instanceof java.util.Vector) {
						for (java.util.Iterator jter = ((java.util.Vector) groups).iterator(); jter.hasNext();) {
							Object groupChunk = jter.next();
							if (groupChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
								Object questionClasses = ((edu.cmu.cs.stage3.util.StringObjectPair) groupChunk).getObject();
								if (questionClasses instanceof java.util.Vector) {
									for (java.util.Iterator kter = ((java.util.Vector) questionClasses).iterator(); kter.hasNext();) {
										Object className2 = kter.next();
										if (className2 instanceof String) {
											try {
												Class.forName((String) className2);
											} catch (ClassNotFoundException e) {
												throw new IllegalArgumentException("questionStructure error: " + className2 + " is not a Class");
											}
										} else {
											throw new IllegalArgumentException("questionStructure error: expected String, got: " + className);
										}
									}
								}
							} else {
								throw new IllegalArgumentException("Unexpected object found in questionStructure: " + groupChunk);
							}
						}
					} else {
						throw new IllegalArgumentException("questionStructure error: expected Vector, got: " + groups);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "questionStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException("Unexpected object found in questionStructure: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.questionStructure = questionStructure;
	}

	public static java.util.Vector getQuestionStructure(Class elementClass) {
		if (AuthoringToolResources.resources.questionStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.questionStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (java.util.Vector) ((edu.cmu.cs.stage3.util.StringObjectPair) o).getObject();
						}
					} catch (java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in questionStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void setDefaultPropertyValuesStructure(java.util.Vector defaultPropertyValuesStructure) {
		// validate structure
		if (defaultPropertyValuesStructure != null) {
			for (java.util.Iterator iter = defaultPropertyValuesStructure.iterator(); iter.hasNext();) {
				Object classChunk = iter.next();
				if (classChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					// String className =
					// ((edu.cmu.cs.stage3.util.StringObjectPair)classChunk).getString();
					Object properties = ((edu.cmu.cs.stage3.util.StringObjectPair) classChunk).getObject();
					// try {
					// Class c = Class.forName( className );
					if (properties instanceof java.util.Vector) {
						for (java.util.Iterator jter = ((java.util.Vector) properties).iterator(); jter.hasNext();) {
							Object propertyChunk = jter.next();
							if (propertyChunk instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
								Object values = ((edu.cmu.cs.stage3.util.StringObjectPair) propertyChunk).getObject();
								if (!(values instanceof java.util.Vector)) {
									throw new IllegalArgumentException("defaultPropertyValuesStructure error: expected Vector, got: " + values);
									// } else {
									// for( java.util.Iterator kter =
									// ((java.util.Vector)values).iterator();
									// kter.hasNext(); ) {
									// System.out.println( kter.next() );
									// }
								}
							} else {
								throw new IllegalArgumentException("defaultPropertyValuesStructure error: expected StringObjectPair, got: " + propertyChunk);
							}
						}
					} else {
						throw new IllegalArgumentException("defaultPropertyValuesStructure error: expected Vector, got: " + properties);
					}
					// } catch( java.lang.ClassNotFoundException e ) {
					// throw new IllegalArgumentException(
					// "defaultPropertyValuesStructure error: " + className +
					// " is not a Class" );
					// }
				} else {
					throw new IllegalArgumentException("defaultPropertyValuesStructure error: expected StringObjectPair, got: " + classChunk);
				}
			}
		}

		AuthoringToolResources.resources.defaultPropertyValuesStructure = defaultPropertyValuesStructure;
	}

	public static java.util.Vector getDefaultPropertyValues(Class elementClass, String propertyName) {
		if (AuthoringToolResources.resources.defaultPropertyValuesStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.defaultPropertyValuesStructure.iterator(); iter.hasNext();) {
				edu.cmu.cs.stage3.util.StringObjectPair classChunk = (edu.cmu.cs.stage3.util.StringObjectPair) iter.next();
				String className = classChunk.getString();
				try {
					Class c = Class.forName(className);
					if (c.isAssignableFrom(elementClass)) {
						java.util.Vector properties = (java.util.Vector) classChunk.getObject();
						for (java.util.Iterator jter = properties.iterator(); jter.hasNext();) {
							edu.cmu.cs.stage3.util.StringObjectPair propertyChunk = (edu.cmu.cs.stage3.util.StringObjectPair) jter.next();
							if (propertyName.equals(propertyChunk.getString())) {
								return (java.util.Vector) propertyChunk.getObject();
							}
						}
					}
				} catch (java.lang.ClassNotFoundException e) {
					AuthoringTool.showErrorDialog("Can't find class " + className, e);
				}
			}
		}

		return null;
	}

	public static void putName(Object key, String prettyName) {
		AuthoringToolResources.resources.nameMap.put(key, prettyName);
	}

	public static String getName(Object key) {
		return (String) AuthoringToolResources.resources.nameMap.get(key);
	}

	public static boolean nameMapContainsKey(Object key) {
		return AuthoringToolResources.resources.nameMap.containsKey(key);
	}

	public static void putHTMLName(Object key, String prettyName) {
		AuthoringToolResources.resources.htmlNameMap.put(key, prettyName);
	}

	public static String getHTMLName(Object key) {
		return (String) AuthoringToolResources.resources.htmlNameMap.get(key);
	}

	public static boolean htmlNameMapContainsKey(Object key) {
		return AuthoringToolResources.resources.htmlNameMap.containsKey(key);
	}

	public static void putFormat(Object key, String formatString) {
		AuthoringToolResources.resources.formatMap.put(key, formatString);
	}

	public static String getFormat(Object key) {
		return (String) AuthoringToolResources.resources.formatMap.get(key);
	}

	public static String getPlainFormat(Object key) {
		String format = (String) AuthoringToolResources.resources.formatMap.get(key);
		StringBuffer sb = new StringBuffer();
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (!token.startsWith("<<") || token.startsWith("<<<")) {
				while (token.indexOf("&lt;") > -1) {
					token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
				}
				sb.append(token);
			}
		}
		return sb.toString();
	}

	public static boolean formatMapContainsKey(Object key) {
		return AuthoringToolResources.resources.formatMap.containsKey(key);
	}

	public static void putPropertyValueFormatMap(String propertyKey, java.util.HashMap valueReprMap) {
		AuthoringToolResources.resources.propertyValueFormatMap.put(propertyKey, valueReprMap);
	}

	public static java.util.HashMap getPropertyValueFormatMap(String propertyKey) {
		return (java.util.HashMap) AuthoringToolResources.resources.propertyValueFormatMap.get(propertyKey);
	}

	public static boolean propertyValueFormatMapContainsKey(String propertyKey) {
		return AuthoringToolResources.resources.propertyValueFormatMap.containsKey(propertyKey);
	}

	public static void putUnitString(String key, String unitString) {
		AuthoringToolResources.resources.unitMap.put(key, unitString);
	}

	public static String getUnitString(String key) {
		return (String) AuthoringToolResources.resources.unitMap.get(key);
	}

	public static boolean unitMapContainsKey(String key) {
		return AuthoringToolResources.resources.unitMap.containsKey(key);
	}

	public static java.util.Set getUnitMapKeySet() {
		return AuthoringToolResources.resources.unitMap.keySet();
	}

	public static java.util.Collection getUnitMapValues() {
		return AuthoringToolResources.resources.unitMap.values();
	}

	public static void setClassesToOmitNoneFor(Class[] classesToOmitNoneFor) {
		AuthoringToolResources.resources.classesToOmitNoneFor = classesToOmitNoneFor;
	}

	// public static boolean shouldGUIOmitNone( Class valueClass ) {
	// for( int i = 0; i <
	// AuthoringToolResources.resources.classesToOmitNoneFor.length; i++ ) {
	// if(
	// AuthoringToolResources.resources.classesToOmitNoneFor[i].isAssignableFrom(
	// valueClass ) ) {
	// return true;
	// }
	// }
	// return false;
	// }

	public static void setPropertiesToOmitNoneFor(edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitNoneFor) {
		AuthoringToolResources.resources.propertiesToOmitNoneFor = propertiesToOmitNoneFor;
	}

	public static void setPropertiesToIncludeNoneFor(edu.cmu.cs.stage3.util.StringTypePair[] propertiesToIncludeNoneFor) {
		AuthoringToolResources.resources.propertiesToIncludeNoneFor = propertiesToIncludeNoneFor;
	}

	public static boolean shouldGUIOmitNone(edu.cmu.cs.stage3.alice.core.Property property) {
		return !shouldGUIIncludeNone(property);
	}

	public static boolean shouldGUIIncludeNone(edu.cmu.cs.stage3.alice.core.Property property) {
		if (AuthoringToolResources.resources.propertiesToIncludeNoneFor != null) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for (StringTypePair element : AuthoringToolResources.resources.propertiesToIncludeNoneFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean shouldGUIIncludeNone(Class elementClass, String propertyName) {
		if (AuthoringToolResources.resources.propertiesToIncludeNoneFor != null) {
			for (StringTypePair element : AuthoringToolResources.resources.propertiesToIncludeNoneFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertyNamesToOmit(edu.cmu.cs.stage3.util.StringTypePair[] propertyNamesToOmit) {
		AuthoringToolResources.resources.propertyNamesToOmit = propertyNamesToOmit;
	}

	public static boolean shouldGUIOmitPropertyName(edu.cmu.cs.stage3.alice.core.Property property) {
		if (AuthoringToolResources.resources.propertyNamesToOmit != null) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for (StringTypePair element : AuthoringToolResources.resources.propertyNamesToOmit) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static void setPropertiesToOmitScriptDefinedFor(edu.cmu.cs.stage3.util.StringTypePair[] propertiesToOmitScriptDefinedFor) {
		AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor = propertiesToOmitScriptDefinedFor;
	}

	public static boolean shouldGUIOmitScriptDefined(edu.cmu.cs.stage3.alice.core.Property property) {
		if (!authoringToolConfig.getValue("enableScripting").equalsIgnoreCase("true")) {
			return true;
		} else if (AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor != null) {
			Class elementClass = property.getOwner().getClass();
			String propertyName = property.getName();
			for (StringTypePair element : AuthoringToolResources.resources.propertiesToOmitScriptDefinedFor) {
				if (element.getType().isAssignableFrom(elementClass) && element.getString().equals(propertyName)) {
					return true;
				}
			}
		}
		return false;
	}

	// get repr in the context of a property
	public static String getReprForValue(Object value, edu.cmu.cs.stage3.alice.core.Property property) {
		return getReprForValue(value, property, null);
	}
	public static String getReprForValue(Object value, Class elementClass, String propertyName) {
		return getReprForValue(value, elementClass, propertyName, null);
	}
	public static String getReprForValue(Object value, edu.cmu.cs.stage3.alice.core.Property property, Object extraContextInfo) {
		Class elementClass = property.getOwner().getClass();
		String propertyName = property.getName();
		if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation && property.getName().equals("value")) {
			edu.cmu.cs.stage3.alice.core.response.PropertyAnimation propertyAnimation = (edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) property.getOwner();
			Object e = propertyAnimation.element.get();
			if (e instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				elementClass = ((edu.cmu.cs.stage3.alice.core.Expression) e).getValueClass();
			} else {
				Object elementValue = propertyAnimation.element.getElementValue();
				if (elementValue != null) {
					elementClass = elementValue.getClass();
				} else {
					elementClass = null;
				}
			}
			propertyName = propertyAnimation.propertyName.getStringValue();
		} else if (property.getOwner() instanceof edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment && property.getName().equals("value")) {
			edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment propertyAssignment = (edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment) property.getOwner();
			elementClass = propertyAssignment.element.getElementValue().getClass();
			propertyName = propertyAssignment.propertyName.getStringValue();
		}
		return getReprForValue(value, elementClass, propertyName, extraContextInfo);
	}
	public static String getReprForValue(Object value, Class elementClass, String propertyName, Object extraContextInfo) {
		boolean verbose = false;
		Class valueClass = null;
		try {
			valueClass = edu.cmu.cs.stage3.alice.core.Element.getValueClassForPropertyNamed(elementClass, propertyName);
		} catch (Exception e) { // a bit hackish
			valueClass = Object.class;
		}
		if (valueClass == null) { // another hack
			valueClass = Object.class;
		}
		if (elementClass == null || propertyName == null) {
			return getReprForValue(value);
		}

		if (edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse.class.isAssignableFrom(elementClass) && propertyName.equals("userDefinedResponse") || edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion.class.isAssignableFrom(elementClass) && propertyName.equals("userDefinedQuestion")) {
			verbose = true;
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Variable && ((edu.cmu.cs.stage3.alice.core.Variable) value).getParent() instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
			verbose = true;
		}

		try {
			while (edu.cmu.cs.stage3.alice.core.Element.class.isAssignableFrom(elementClass)) {
				String propertyKey = elementClass.getName() + "." + propertyName;

				String userRepr = null;
				if (extraContextInfo != null && extraContextInfo.equals("menuContext")) { // if
																							// the
																							// repr
																							// is
																							// going
																							// to
																							// be
																							// shown
																							// in
																							// a
																							// menu
					if (propertyValueFormatMapContainsKey(propertyKey + ".menuContext")) {
						propertyKey = propertyKey + ".menuContext";
					}
				} else if (extraContextInfo instanceof edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) { // if
																													// there
																													// is
																													// extra
																													// info
																													// stored
																													// in
																													// the
																													// element's
																													// data
																													// property
					edu.cmu.cs.stage3.alice.core.property.DictionaryProperty data = (edu.cmu.cs.stage3.alice.core.property.DictionaryProperty) extraContextInfo;
					if (data.getName().equals("data")) { // sanity check
						Object repr = data.get("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
						if (repr != null) {
							if (repr instanceof String) {
								if (Number.class.isAssignableFrom(valueClass) && value instanceof Double) { // if
																											// it's
																											// a
																											// number,
																											// check
																											// to
																											// make
																											// sure
																											// the
																											// string
																											// is
																											// still
																											// valid
									Double d = AuthoringToolResources.parseDouble((String) repr);
									if (d != null && d.equals(value)) {
										userRepr = (String) repr;
									} else {
										data.remove("edu.cmu.cs.stage3.alice.authoringtool.userRepr." + propertyName);
									}
								} else {
									userRepr = (String) repr;
								}
							}
						}
					}
				}

				String reprString = null;
				if (propertyValueFormatMapContainsKey(propertyKey)) {
					java.util.HashMap map = getPropertyValueFormatMap(propertyKey);
					if (map.containsKey(value)) {
						reprString = (String) map.get(value);
					} else if (value == null) { // is this right for all cases?
						reprString = null;
					} else if (map.containsKey("default")) {
						reprString = (String) map.get("default");
					}
				}

				if (reprString != null) {
					for (java.util.Iterator iter = AuthoringToolResources.resources.unitMap.keySet().iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						String unitString = getUnitString(key);
						String unitExpression = "<" + key + ">";
						while (reprString.indexOf(unitExpression) > -1) {
							StringBuffer sb = new StringBuffer(reprString);
							sb.replace(reprString.indexOf(unitExpression), reprString.indexOf(unitExpression) + unitExpression.length(), unitString);
							reprString = sb.toString();
						}
					}

					while (reprString.indexOf("<value>") > -1) {
						String valueString = userRepr != null ? userRepr : getReprForValue(value);
						StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<value>"), reprString.indexOf("<value>") + "<value>".length(), valueString);
						reprString = sb.toString();
					}
					while (reprString.indexOf("<percentValue>") > -1 && value instanceof Double) {
						double v = ((Double) value).doubleValue() * 100.0;
						String valueString = AuthoringToolResources.resources.decimalFormatter.format(v) + "%";
						StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<percentValue>"), reprString.indexOf("<percentValue>") + "<percentValue>".length(), valueString);
						reprString = sb.toString();
					}
					while (reprString.indexOf("<keyCodeValue>") > -1 && value instanceof Integer) {
						String valueString = java.awt.event.KeyEvent.getKeyText(((Integer) value).intValue());
						StringBuffer sb = new StringBuffer(reprString);
						sb.replace(reprString.indexOf("<keyCodeValue>"), reprString.indexOf("<keyCodeValue>") + "<keyCodeValue>".length(), valueString);
						reprString = sb.toString();
					}

					return reprString;
				}

				elementClass = elementClass.getSuperclass();
			}
		} catch (Throwable t) {
			AuthoringTool.showErrorDialog("Error finding repr for " + value, t);
		}
		return getReprForValue(value, verbose);
	}

	public static String getReprForValue(Object value) {
		return getReprForValue(value, false);
	}

	protected static void initWebGalleryURL() {
		java.net.URL galleryURL = null;
		try {
			galleryURL = new java.net.URL("http://www.alice.org/gallery/");
			java.io.File urlFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceHomeDirectory(), "etc/AliceWebGalleryURL.txt").getAbsoluteFile();
			if (urlFile.exists()) {
				if (urlFile.canRead()) {
					java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(urlFile));
					String urlString = null;
					while (true) {
						urlString = br.readLine();
						if (urlString == null) {
							break;
						} else if (urlString.length() > 0 && urlString.charAt(0) != '#') {
							break;
						}
					}
					br.close();

					if (urlString != null) {
						urlString = urlString.trim();
						if (urlString.length() > 0) {
							try {
								galleryURL = new java.net.URL(urlString);
							} catch (java.net.MalformedURLException badURL) {
								if (urlString.startsWith("www")) {
									urlString = "http://" + urlString;
									try {
										galleryURL = new java.net.URL(urlString);
									} catch (java.net.MalformedURLException badURLAgain) {}
								}
							}

						}
					}

				}
			}
		} catch (Throwable t) {} finally {
			if (galleryURL != null) {
				setMainWebGalleryURL(galleryURL);
			}
		}
	}

	protected static String stripUnnamedsFromName(Object value) {
		String toStrip = new String(value.toString());
		String toReturn = "";
		String toMatch = "__Unnamed";
		boolean notDone = true;
		while (notDone) {
			int nextIndex = toStrip.indexOf(toMatch);
			if (nextIndex >= 0) {
				String toAdd = toStrip.substring(0, nextIndex);
				if (toAdd != null) {
					toReturn += toAdd;
				}
				String newToStrip = toStrip.substring(nextIndex, toStrip.length());
				if (newToStrip != null) {
					toStrip = newToStrip;
				} else {
					notDone = false;
					break;
				}
				nextIndex = toStrip.indexOf(".");
				if (nextIndex >= 0) {
					newToStrip = toStrip.substring(nextIndex + 1, toStrip.length());
					if (newToStrip != null) {
						toStrip = newToStrip;
					} else {
						notDone = false;
						break;
					}
				} else {
					notDone = false;
					break;
				}
			} else {
				toReturn += toStrip;
				notDone = false;
				break;
			}
		}
		return toStrip;
	}

	public static String getReprForValue(Object value, boolean verbose) {
		if (nameMapContainsKey(value)) {
			value = getName(value);
		}
		if (formatMapContainsKey(value)) {
			value = getPlainFormat(value);
		}
		if (value instanceof Class) {
			value = ((Class) value).getName();
			if (nameMapContainsKey(value)) {
				value = getName(value);
			}
		}
		if (value instanceof edu.cmu.cs.stage3.util.Enumerable) {
			value = ((edu.cmu.cs.stage3.util.Enumerable) value).getRepr();
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.question.PropertyValue) {
			String propertyName = ((edu.cmu.cs.stage3.alice.core.question.PropertyValue) value).propertyName.getStringValue();
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) ((edu.cmu.cs.stage3.alice.core.question.PropertyValue) value).element.get();
			Class valueClass = element.getClass();
			if (element instanceof edu.cmu.cs.stage3.alice.core.Expression) {
				valueClass = ((edu.cmu.cs.stage3.alice.core.Expression) element).getValueClass();
			}
			try {
				Class declaringClass = valueClass.getField(propertyName).getDeclaringClass();
				if (declaringClass != null) {
					String key = declaringClass.getName() + "." + propertyName;
					if (nameMapContainsKey(key)) {
						propertyName = getName(key);
					}
				}
			} catch (NoSuchFieldException e) {
				AuthoringTool.showErrorDialog("Error representing PropertyValue: can't find " + propertyName + " on " + valueClass, e);
			}

			value = getReprForValue(element, false) + "." + propertyName;
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Question && formatMapContainsKey(value.getClass())) {
			String questionRepr = "";
			edu.cmu.cs.stage3.alice.core.Question question = (edu.cmu.cs.stage3.alice.core.Question) value;
			String format = getFormat(value.getClass());
			edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer formatTokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
			// int i = 0;
			while (formatTokenizer.hasMoreTokens()) {
				String token = formatTokenizer.nextToken();
				if (token.startsWith("<") && token.endsWith(">")) {
					edu.cmu.cs.stage3.alice.core.Property property = question.getPropertyNamed(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
					if (property != null) {
						questionRepr += getReprForValue(property.get(), property);
					}
				} else {
					while (token.indexOf("&lt;") > -1) {
						token = new StringBuffer(token).replace(token.indexOf("&lt;"), token.indexOf("&lt;") + 4, "<").toString();
					}
					questionRepr += token;
				}
			}

			if (questionRepr.length() > 0) {
				value = questionRepr;
			}
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
			if (verbose) {
				edu.cmu.cs.stage3.alice.core.Element ancestor = ((edu.cmu.cs.stage3.alice.core.Element) value).getSandbox();
				if (ancestor != null) {
					ancestor = ancestor.getParent();
				}
				value = ((edu.cmu.cs.stage3.alice.core.Element) value).getKey(ancestor);
				value = stripUnnamedsFromName(value);
			} else {
				value = ((edu.cmu.cs.stage3.alice.core.Element) value).name.getStringValue();
			}
		}
		if (value instanceof Number) {
			double d = ((Number) value).doubleValue();
			// if( d == -.75 ) {
			// value = "-3/4";
			// } else if( d == -.5 ) {
			// value = "-1/2";
			// } else if( d == -.25 ) {
			// value = "-1/4";
			// } else if( d == .25 ) {
			// value = "1/4";
			// } else if( d == .5 ) {
			// value = "1/2";
			// } else if( d == -.75 ) {
			// value = "3/4";
			// } else if( d == -.9 ) {
			// value = "-9/10";
			// } else if( d == -.8 ) {
			// value = "-4/5";
			// } else if( d == -.7 ) {
			// value = "-7/10";
			// } else if( d == -.6 ) {
			// value = "-3/5";
			// } else if( d == -.4 ) {
			// value = "-2/5";
			// } else if( d == -.3 ) {
			// value = "-3/10";
			// } else if( d == -.2 ) {
			// value = "-1/5";
			// } else if( d == -.1 ) {
			// value = "-1/10";
			// } else if( d == .1 ) {
			// value = "1/10";
			// } else if( d == .2 ) {
			// value = "1/5";
			// } else if( d == .3 ) {
			// value = "3/10";
			// } else if( d == .4 ) {
			// value = "2/5";
			// } else if( d == .6 ) {
			// value = "3/5";
			// } else if( d == .7 ) {
			// value = "7/10";
			// } else if( d == .8 ) {
			// value = "4/5";
			// } else if( d == .9 ) {
			// value = "9/10";
			// } else {
			value = AuthoringToolResources.resources.decimalFormatter.format(d);
			// }
		}
		// if( value instanceof edu.cmu.cs.stage3.math.Vector3 ) {
		// edu.cmu.cs.stage3.math.Vector3 vec =
		// (edu.cmu.cs.stage3.math.Vector3)value;
		// value = "Vector3( " + vec.x + ", " + vec.y + ", " + vec.z + " )";
		// }
		// if( value instanceof edu.cmu.cs.stage3.math.Matrix44 ) {
		// edu.cmu.cs.stage3.math.Matrix44 m =
		// (edu.cmu.cs.stage3.math.Matrix44)value;
		// edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
		// edu.cmu.cs.stage3.math.Quaternion quaternion =
		// m.getAxes().getQuaternion();
		// value = "position: " + decimalFormatter.format( position.x ) + ", " +
		// decimalFormatter.format( position.y ) + ", " +
		// decimalFormatter.format( position.z ) + ";  " +
		// "orientation: (" + decimalFormatter.format( quaternion.x ) + ", " +
		// decimalFormatter.format( quaternion.y ) + ", " +
		// decimalFormatter.format( quaternion.z ) + ") " +
		// decimalFormatter.format( quaternion.w );
		// }
		if (value instanceof javax.vecmath.Vector3d) {
			javax.vecmath.Vector3d vec = (javax.vecmath.Vector3d) value;
			value = "Vector3( " + AuthoringToolResources.resources.decimalFormatter.format(vec.x) + ", " + AuthoringToolResources.resources.decimalFormatter.format(vec.y) + ", " + AuthoringToolResources.resources.decimalFormatter.format(vec.z) + " )";
		}
		if (value instanceof javax.vecmath.Matrix4d) {
			edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44((javax.vecmath.Matrix4d) value);
			edu.cmu.cs.stage3.math.Vector3 position = m.getPosition();
			edu.cmu.cs.stage3.math.Quaternion quaternion = m.getAxes().getQuaternion();
			value = "position: " + AuthoringToolResources.resources.decimalFormatter.format(position.x) + ", " + AuthoringToolResources.resources.decimalFormatter.format(position.y) + ", " + AuthoringToolResources.resources.decimalFormatter.format(position.z) + ";  " + "orientation: (" + AuthoringToolResources.resources.decimalFormatter.format(quaternion.x) + ", " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.y) + ", " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.z) + ") " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.w);
		}
		if (value instanceof edu.cmu.cs.stage3.math.Quaternion) {
			edu.cmu.cs.stage3.math.Quaternion quaternion = (edu.cmu.cs.stage3.math.Quaternion) value;
			value = "(" + AuthoringToolResources.resources.decimalFormatter.format(quaternion.x) + ", " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.y) + ", " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.z) + ") " + AuthoringToolResources.resources.decimalFormatter.format(quaternion.w);
		}
		if (value instanceof edu.cmu.cs.stage3.alice.scenegraph.Color) {
			edu.cmu.cs.stage3.alice.scenegraph.Color color = (edu.cmu.cs.stage3.alice.scenegraph.Color) value;
			if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK)) {
				value = "black";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE)) {
				value = "blue";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN)) {
				value = "brown";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN)) {
				value = "cyan";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY)) {
				value = "dark gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY)) {
				value = "gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN)) {
				value = "green";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY)) {
				value = "light gray";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA)) {
				value = "magenta";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE)) {
				value = "orange";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PINK)) {
				value = "pink";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE)) {
				value = "purple";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.RED)) {
				value = "red";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE)) {
				value = "white";
			} else if (color.equals(edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW)) {
				value = "yellow";
			} else {
				value = "Color(r:" + AuthoringToolResources.resources.decimalFormatter.format(color.getRed()) + ", g:" + AuthoringToolResources.resources.decimalFormatter.format(color.getGreen()) + ", b:" + AuthoringToolResources.resources.decimalFormatter.format(color.getBlue()) + ", a:" + AuthoringToolResources.resources.decimalFormatter.format(color.getAlpha()) + ")";
			}
		}
		if (value instanceof edu.cmu.cs.stage3.alice.core.Property) {
			String simpleName = ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
			if (((edu.cmu.cs.stage3.alice.core.Property) value).getDeclaredClass() != null) {
				String key = ((edu.cmu.cs.stage3.alice.core.Property) value).getDeclaredClass().getName() + "." + ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
				if (nameMapContainsKey(key)) {
					simpleName = getName(key);
				} else {
					simpleName = ((edu.cmu.cs.stage3.alice.core.Property) value).getName();
				}
			}

			if (((edu.cmu.cs.stage3.alice.core.Property) value).getOwner() instanceof edu.cmu.cs.stage3.alice.core.Variable) {
				value = getReprForValue(((edu.cmu.cs.stage3.alice.core.Property) value).getOwner(), verbose);
			} else if (verbose && ((edu.cmu.cs.stage3.alice.core.Property) value).getOwner() != null) {
				value = getReprForValue(((edu.cmu.cs.stage3.alice.core.Property) value).getOwner()) + "." + simpleName;
			} else {
				value = simpleName;
			}
		}
		if (value == null) {
			value = "<None>";
		}

		return value.toString();
	}

	public static String getFormattedReprForValue(Object value, edu.cmu.cs.stage3.util.StringObjectPair[] knownPropertyValues) {
		String format = (String) AuthoringToolResources.resources.formatMap.get(value);
		StringBuffer sb = new StringBuffer();
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
				for (StringObjectPair knownPropertyValue : knownPropertyValues) {
					if (knownPropertyValue.getString().equals(propertyName)) {
						sb.append(AuthoringToolResources.getReprForValue(knownPropertyValue.getObject(), true));
						break;
					}
				}
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				// leave blank
			} else if (token.startsWith("<") && token.endsWith(">")) {
				String propertyName = token.substring(token.lastIndexOf("<") + 1, token.indexOf(">"));
				boolean appendedValue = false;
				for (StringObjectPair knownPropertyValue : knownPropertyValues) {
					if (knownPropertyValue.getString().equals(propertyName)) {
						sb.append(AuthoringToolResources.getReprForValue(knownPropertyValue.getObject(), true));
						appendedValue = true;
						break;
					}
				}
				if (!appendedValue) {
					sb.append(token);
				}
			} else {
				sb.append(token);
			}
		}
		return sb.toString();
	}

	public static String getNameInContext(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.core.Element context) {
		// DEBUG System.out.println( "element: " + element );
		// DEBUG System.out.println( "context: " + context );
		if (element instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			if (element.getParent() != null) {
				edu.cmu.cs.stage3.alice.core.Element variableRoot = element.getParent();
				// DEBUG System.out.println( "variableRoot: " + variableRoot );
				if (variableRoot instanceof edu.cmu.cs.stage3.alice.core.Response && (context.isDescendantOf(variableRoot) || context == variableRoot)) {
					return element.name.getStringValue();
				}
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Sound && context instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse) {
			edu.cmu.cs.stage3.alice.core.Sound sound = (edu.cmu.cs.stage3.alice.core.Sound) element;
			double t = Double.NaN;
			edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
			if (dataSourceValue != null) {
				t = dataSourceValue.getDuration(edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY);
				// t = dataSourceValue.waitForDuration( 100 );
				// if( Double.isNaN( t ) ) {
				// t = dataSourceValue.getDurationHint();
				// }
			}
			return getReprForValue(element, true) + " (" + formatTime(t) + ")";
		}

		return getReprForValue(element, true);
	}

	public static void setDefaultVariableTypes(edu.cmu.cs.stage3.util.StringTypePair[] defaultVariableTypes) {
		AuthoringToolResources.resources.defaultVariableTypes = defaultVariableTypes;
	}

	public static edu.cmu.cs.stage3.util.StringTypePair[] getDefaultVariableTypes() {
		return AuthoringToolResources.resources.defaultVariableTypes;
	}

	public static void setDefaultAspectRatios(String[] defaultAspectRatios) {
		AuthoringToolResources.resources.defaultAspectRatios = defaultAspectRatios;
	}

	public static String[] getDefaultAspectRatios() {
		return AuthoringToolResources.resources.defaultAspectRatios;
	}

	public static String[] getInitialVisibleProperties(Class elementClass) {
		java.util.LinkedList visible = new java.util.LinkedList();
		String format = AuthoringToolResources.getFormat(elementClass);
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<") && token.endsWith(">")) {
				visible.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			}
		}

		return (String[]) visible.toArray(new String[0]);
	}

	public static String[] getDesiredProperties(Class elementClass) {
		java.util.LinkedList desired = new java.util.LinkedList();
		String format = AuthoringToolResources.getFormat(elementClass);
		edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer tokenizer = new edu.cmu.cs.stage3.alice.authoringtool.util.FormatTokenizer(format);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.startsWith("<<<") && token.endsWith(">>>")) {
				// skip this one
				// should be in knownPropertyValues
			} else if (token.startsWith("<<") && token.endsWith(">>")) {
				desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			} else if (token.startsWith("<") && token.endsWith(">")) {
				desired.add(token.substring(token.lastIndexOf("<") + 1, token.indexOf(">")));
			}
		}

		return (String[]) desired.toArray(new String[0]);
	}

	public static void setBehaviorClasses(Class[] behaviorClasses) {
		AuthoringToolResources.resources.behaviorClasses = behaviorClasses;
	}

	public static Class[] getBehaviorClasses() {
		return AuthoringToolResources.resources.behaviorClasses;
	}

	public static void setParameterizedPropertiesToOmit(String[] parameterizedPropertiesToOmit) {
		AuthoringToolResources.resources.parameterizedPropertiesToOmit = parameterizedPropertiesToOmit;
	}

	public static String[] getParameterizedPropertiesToOmit() {
		return AuthoringToolResources.resources.parameterizedPropertiesToOmit;
	}

	public static void setOneShotGroupsToInclude(String[] oneShotGroupsToInclude) {
		AuthoringToolResources.resources.oneShotGroupsToInclude = oneShotGroupsToInclude;
	}

	public static String[] getOneShotGroupsToInclude() {
		return AuthoringToolResources.resources.oneShotGroupsToInclude;
	}

	public static void setBehaviorParameterPropertiesStructure(java.util.Vector behaviorParameterPropertiesStructure) {
		AuthoringToolResources.resources.behaviorParameterPropertiesStructure = behaviorParameterPropertiesStructure;
	}

	public static String[] getBehaviorParameterProperties(Class behaviorClass) {
		if (AuthoringToolResources.resources.behaviorParameterPropertiesStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.behaviorParameterPropertiesStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class c = Class.forName(className);
						if (c.isAssignableFrom(behaviorClass)) {
							return (String[]) ((edu.cmu.cs.stage3.util.StringObjectPair) o).getObject();
						}
					} catch (java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in behaviorParameterPropertiesStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void putColor(String key, java.awt.Color color) {
		AuthoringToolResources.resources.colorMap.put(key, color);
	}

	private static float[] rgbToHSL(java.awt.Color rgb) {
		float[] rgbF = rgb.getRGBColorComponents(null);
		float[] hsl = new float[3];
		float min = Math.min(rgbF[0], Math.min(rgbF[1], rgbF[2]));
		float max = Math.max(rgbF[0], Math.max(rgbF[1], rgbF[2]));
		float delta = max - min;

		hsl[2] = (max + min) / 2;

		if (delta == 0) {
			hsl[0] = 0.0f;
			hsl[1] = 0.0f;
		} else {
			if (hsl[2] < 0.5) {
				hsl[1] = delta / (max + min);
				// System.out.println("B: min: "+min+", max: "+max+", delta: "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			} else {
				hsl[1] = delta / (2 - max - min);
				// System.out.println("A: min: "+min+", max: "+max+", delta: "+delta+", H: "+hsl[0]+", S: "+hsl[1]+", L: "+hsl[2]);
			}
			float delR = ((max - rgbF[0]) / 6 + delta / 2) / delta;
			float delG = ((max - rgbF[1]) / 6 + delta / 2) / delta;
			float delB = ((max - rgbF[2]) / 6 + delta / 2) / delta;
			if (rgbF[0] == max) {
				hsl[0] = delB - delG;
			} else if (rgbF[1] == max) {
				hsl[0] = 1.0f / 3 + delR - delB;
			} else if (rgbF[2] == max) {
				hsl[0] = 2.0f / 3 + delG - delR;
			}

			if (hsl[0] < 0) {
				hsl[0] += 1;
			}
			if (hsl[0] > 1) {
				hsl[0] -= 1;
			}
		}
		// System.out.println("For RGB: "+rgb+" HSL = "+hsl[0]+", "+hsl[1]+", "+hsl[2]);
		return hsl;
	}

	private static float hueToRGB(float v1, float v2, float vH) {
		if (vH < 0) {
			vH += 1;
		}
		if (vH > 1) {
			vH -= 1;
		}
		if (6 * vH < 1) {
			return v1 + (v2 - v1) * 6 * vH;
		}
		if (2 * vH < 1) {
			return v2;
		}
		if (3 * vH < 2) {
			return v1 + (v2 - v1) * (2.0f / 3 - vH) * 6;
		}
		return v1;
	}

	private static java.awt.Color hslToRGB(float[] hsl) {
		java.awt.Color rgb = new java.awt.Color(0, 0, 0);
		if (hsl[1] == 0) {
			// System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+" RGB = "+hsl[2]+", "+hsl[2]+", "+hsl[2]);
			return new java.awt.Color(hsl[2], hsl[2], hsl[2]);
		} else {
			float var_2 = 0.0f;
			if (hsl[2] < 0.5) {
				var_2 = hsl[2] * (1 + hsl[1]);
			} else {
				var_2 = hsl[2] + hsl[1] - hsl[1] * hsl[2];
			}
			float var_1 = 2 * hsl[2] - var_2;
			float R = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0] + 1.0f / 3));
			float G = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0]));
			float B = Math.min(1.0f, hueToRGB(var_1, var_2, hsl[0] - 1.0f / 3));
			// System.out.println("For HSL: "+hsl[0]+", "+hsl[1]+", "+hsl[2]+" RGB = "+R+", "+G+", "+B);
			return new java.awt.Color(R, G, B);
		}
	}
	// static {
	// float[] hsl = rgbToHSL(java.awt.Color.white);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.black);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.red);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(java.awt.Color.green);
	// hslToRGB(hsl);
	// hsl = rgbToHSL(new java.awt.Color(100, 100, 100));
	// hslToRGB(hsl);
	// hsl = rgbToHSL(new java.awt.Color(.2f, .5f, .5f));
	// hslToRGB(hsl);
	//
	// }
	public static java.awt.Color getColor(String key) {
		java.awt.Color toReturn = (java.awt.Color) AuthoringToolResources.resources.colorMap.get(key);
		if (authoringToolConfig.getValue("enableHighContrastMode").equalsIgnoreCase("true") && !key.equalsIgnoreCase("mainFontColor") && !key.equalsIgnoreCase("objectTreeDisabledText") && !key.equalsIgnoreCase("objectTreeSelectedText") && !key.equalsIgnoreCase("disabledHTMLText") && !key.equalsIgnoreCase("disabledHTML") && !key.equalsIgnoreCase("stdErrTextColor") && !key.equalsIgnoreCase("commentForeground") && !key.equalsIgnoreCase("objectTreeSelected") && !key.equalsIgnoreCase("dndHighlight") && !key.equalsIgnoreCase("dndHighlight2") && !key.equalsIgnoreCase("dndHighlight3") && !key.equalsIgnoreCase("guiEffectsShadow") && !key.equalsIgnoreCase("guiEffectsEdge") && !key.equalsIgnoreCase("guiEffectsTroughShadow") && !key.equalsIgnoreCase("guiEffectsDisabledLine") && !key.equalsIgnoreCase("makeSceneEditorBigBackground") && !key.equalsIgnoreCase("makeSceneEditorSmallBackground") && !key.equalsIgnoreCase("objectTreeText")) {
			float[] hsl = rgbToHSL(toReturn);
			hsl[2] = Math.max(hsl[2], .95f);
			java.awt.Color convertedColor = hslToRGB(hsl);
			return new java.awt.Color(convertedColor.getRed(), convertedColor.getGreen(), convertedColor.getBlue(), toReturn.getAlpha());
		} else {
			return toReturn;
		}
	}

	public static void setMainWebGalleryURL(java.net.URL url) {
		AuthoringToolResources.resources.mainWebGalleryURL = url;
	}

	public static java.net.URL getMainWebGalleryURL() {
		return AuthoringToolResources.resources.mainWebGalleryURL;
	}

	public static void setMainDiskGalleryDirectory(java.io.File file) {
		AuthoringToolResources.resources.mainDiskGalleryDirectory = file;
	}

	public static java.io.File getMainDiskGalleryDirectory() {
		return AuthoringToolResources.resources.mainDiskGalleryDirectory;
	}

	public static void setMainCDGalleryDirectory(java.io.File file) {
		AuthoringToolResources.resources.mainCDGalleryDirectory = file;
	}

	public static java.io.File getMainCDGalleryDirectory() {
		return AuthoringToolResources.resources.mainCDGalleryDirectory;
	}

	public static void autodetectMainCDGalleryDirectory(String galleryName) {
		java.io.File[] cdRoots = edu.cmu.cs.stage3.alice.authoringtool.util.CDUtil.getCDRoots();

		for (File cdRoot : cdRoots) {
			if (cdRoot.exists() && cdRoot.canRead()) {
				java.io.File potentialDir = new java.io.File(cdRoot, galleryName);
				if (potentialDir.exists() && potentialDir.canRead()) {
					setMainCDGalleryDirectory(potentialDir);
					break;
				}
			}
		}
	}

	public static java.awt.Image getAliceSystemIconImage() {
		return getImageForString("aliceHead");
	}
	public static javax.swing.ImageIcon getAliceSystemIcon() {
		return getIconForString("aliceHead");
	}

	public static java.awt.Image getImageForString(String s) {
		if (!AuthoringToolResources.resources.stringImageMap.containsKey(s)) {
			java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
			}
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
			}
			if (resource != null) {
				java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(resource);
				AuthoringToolResources.resources.stringImageMap.put(s, image);
			} else {
				return null;
			}
		}

		return (java.awt.Image) AuthoringToolResources.resources.stringImageMap.get(s);
	}

	public static javax.swing.ImageIcon getIconForString(String s) {
		if (!AuthoringToolResources.resources.stringIconMap.containsKey(s)) {
			java.net.URL resource = AuthoringToolResources.class.getResource("images/" + s + ".gif");
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".png");
			}
			if (resource == null) {
				resource = AuthoringToolResources.class.getResource("images/" + s + ".jpg");
			}
			if (resource != null) {
				AuthoringToolResources.resources.stringIconMap.put(s, new javax.swing.ImageIcon(resource));
			} else {
				return null;
			}
		}

		return (javax.swing.ImageIcon) AuthoringToolResources.resources.stringIconMap.get(s);
	}

	static final javax.swing.ImageIcon cameraIcon = getIconForString("camera");
	static final javax.swing.ImageIcon ambientLightIcon = getIconForString("ambientLight");
	static final javax.swing.ImageIcon directionalLightIcon = getIconForString("directionalLight");
	static final javax.swing.ImageIcon pointLightIcon = getIconForString("pointLight");
	static final javax.swing.ImageIcon defaultLightIcon = getIconForString("pointLight");
	static final javax.swing.ImageIcon modelIcon = getIconForString("model");
	static final javax.swing.ImageIcon subpartIcon = getIconForString("subpart");
	static final javax.swing.ImageIcon sceneIcon = getIconForString("scene");
	static final javax.swing.ImageIcon folderIcon = getIconForString("folder");
	static final javax.swing.ImageIcon defaultIcon = getIconForString("default");

	public static javax.swing.ImageIcon getIconForValue(Object value) {
		if (value instanceof edu.cmu.cs.stage3.alice.core.Camera) { // TODO:
																	// perspective
																	// and
																	// orthographic
			return cameraIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.AmbientLight) {
			return ambientLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.DirectionalLight) {
			return directionalLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.light.PointLight) {
			return pointLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Light) {
			return defaultLightIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (((edu.cmu.cs.stage3.alice.core.Transformable) value).getParent() instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				return subpartIcon;
			} else {
				return modelIcon;
			}
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.World) {
			return sceneIcon;
		} else if (value instanceof edu.cmu.cs.stage3.alice.core.Group) {
			return folderIcon;
		} else if (value instanceof java.awt.Image) {
			return new javax.swing.ImageIcon((java.awt.Image) value);
		} else if (value instanceof String) {
			return getIconForString((String) value);
		} else if (value instanceof Integer) {
			String s = (String) AuthoringToolResources.resources.keyCodesToStrings.get(value);
			if (s != null) {
				return getIconForString("keyboardKeys/" + s);
			} else {
				return null;
			}
		} else {
			return defaultIcon;
		}
	}

	public static javax.swing.ImageIcon getDisabledIcon(javax.swing.ImageIcon inputIcon) {
		return getDisabledIcon(inputIcon, 70);
	}

	public static javax.swing.ImageIcon getDisabledIcon(javax.swing.ImageIcon inputIcon, int percentGray) {
		javax.swing.ImageIcon disabledIcon = (javax.swing.ImageIcon) AuthoringToolResources.resources.disabledIconMap.get(inputIcon);

		if (disabledIcon == null) {
			javax.swing.GrayFilter filter = new javax.swing.GrayFilter(true, percentGray);
			java.awt.image.ImageProducer producer = new java.awt.image.FilteredImageSource(inputIcon.getImage().getSource(), filter);
			java.awt.Image grayImage = java.awt.Toolkit.getDefaultToolkit().createImage(producer);
			disabledIcon = new javax.swing.ImageIcon(grayImage);
			AuthoringToolResources.resources.disabledIconMap.put(inputIcon, disabledIcon);
		}

		return disabledIcon;
	}

	public static void openURL(String urlString) throws java.io.IOException {
		if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
			String[] cmdarray = new String[3];
			cmdarray[0] = "rundll32";
			cmdarray[1] = "url.dll,FileProtocolHandler";
			cmdarray[2] = urlString;

			if (urlString.indexOf("&stacktrace") > -1) {
				try {
					java.io.File tempURL = java.io.File.createTempFile("tempURLHolder", ".url");
					tempURL = tempURL.getAbsoluteFile();
					tempURL.deleteOnExit();
					java.io.PrintWriter urlWriter = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(tempURL)));
					urlWriter.println("[InternetShortcut]");
					urlWriter.println("URL=" + urlString);
					urlWriter.flush();
					urlWriter.close();
					cmdarray[2] = tempURL.getAbsolutePath();
				} catch (Throwable t) {
					cmdarray[2] = urlString.substring(0, urlString.indexOf("&stacktrace"));
				}
			}

			Runtime.getRuntime().exec(cmdarray);

			// final Process p = Runtime.getRuntime().exec( cmdarray );
			// try {
			// p.waitFor();
			// } catch( InterruptedException e ) {
			// e.printStackTrace();
			// }
			// System.out.println( urlString.length() );
			// System.out.println( urlString );
			// System.out.println( p.exitValue() );
			// edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker worker =
			// new edu.cmu.cs.stage3.alice.authoringtool.util.SwingWorker() {
			// public Object construct() {
			// java.io.BufferedInputStream bif = new
			// java.io.BufferedInputStream( p.getErrorStream() );
			// java.io.OutputStream os =
			// AuthoringTool.getHack().getStdErrOutputComponent().getStdErrStream();
			// while( true ) {
			// try {
			// if( bif.available() > 0 ) {
			// os.write( bif.read() );
			// }
			// Thread.sleep( 1 );
			// } catch( Exception e ) {
			// e.printStackTrace();
			// break;
			// }
			// }
			// return null;
			// }
			// };
			// worker.start();

			// Runtime.getRuntime().exec(
			// "rundll32 url.dll,FileProtocolHandler " + urlString );
		} else {
			// try netscape
			try {
				String[] cmd = new String[]{"netscape", urlString};
				Runtime.getRuntime().exec(cmd);
			} catch (Throwable t) {
				String lcOSName = System.getProperty("os.name").toLowerCase();
				if (lcOSName.startsWith("mac os x")) {
					Runtime.getRuntime().exec("open " + urlString);
				}
			}
		}
	}

	// public static String cleanURLString( String urlString ) {
	// java.util.HashMap replacementMap = new java.util.HashMap();
	// replacementMap.put( "/", "%2F" );
	// replacementMap.put( " ", "%20" );
	// replacementMap.put( "~", "%7E" );
	// replacementMap.put( "&", "%26" );
	// replacementMap.put( "?", "%3F" );
	// replacementMap.put( "=", "%3D" );
	// replacementMap.put( ";", "%3B" );
	// replacementMap.put( ">", "%3E" );
	// replacementMap.put( "<", "%3C" );
	//
	// StringBuffer sb = new StringBuffer( urlString );
	// for( java.util.Iterator iter = replacementMap.keySet().iterator();
	// iter.hasNext(); ) {
	// String key = (String)iter.next();
	// String value = (String)replacementMap.get( key );
	// while( true ) {
	// int start = sb.toString().indexOf( key );
	// int end = start + key.length();
	// if( start > -1 ) {
	// sb.replace( start, end, value );
	// } else {
	// break;
	// }
	// }
	// }
	//
	// return sb.toString();
	// }

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	public static Double parseDouble(String doubleString) {
		Double number = null;
		if (doubleString.trim().equalsIgnoreCase("infinity")) {
			number = new Double(Double.POSITIVE_INFINITY);
		} else if (doubleString.trim().equalsIgnoreCase("-infinity")) {
			number = new Double(Double.NEGATIVE_INFINITY);
		} else if (doubleString.indexOf('/') > -1) {
			if (doubleString.lastIndexOf('/') == doubleString.indexOf('/')) {
				String numeratorString = doubleString.substring(0, doubleString.indexOf('/'));
				String denominatorString = doubleString.substring(doubleString.indexOf('/') + 1);
				try {
					number = new Double(Double.parseDouble(numeratorString) / Double.parseDouble(denominatorString));
				} catch (NumberFormatException e) {}
			}
		} else {
			try {
				number = Double.valueOf(doubleString);
			} catch (NumberFormatException e) {}
		}

		return number;
	}

	/**
	 * gets the the world's dummy object group, and creates it if necessary
	 */
	public static edu.cmu.cs.stage3.alice.core.Group getDummyObjectGroup(edu.cmu.cs.stage3.alice.core.World world) {
		edu.cmu.cs.stage3.alice.core.Element[] groups = world.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
		for (Element group : groups) {
			if (group.data.get("dummyObjectGroup") != null && group.data.get("dummyObjectGroup").equals("true") && world.groups.contains(group)) {
				return (edu.cmu.cs.stage3.alice.core.Group) group;
			}
		}

		edu.cmu.cs.stage3.alice.core.Group dummyObjectGroup = new edu.cmu.cs.stage3.alice.core.Group();
		dummyObjectGroup.name.set("Dummy Objects");
		dummyObjectGroup.data.put("dummyObjectGroup", "true");
		dummyObjectGroup.valueClass.set(edu.cmu.cs.stage3.alice.core.Dummy.class);
		world.addChild(dummyObjectGroup);
		world.groups.add(dummyObjectGroup);
		return dummyObjectGroup;
	}

	public static boolean hasDummyObjectGroup(edu.cmu.cs.stage3.alice.core.World world) {
		if (world != null) {
			edu.cmu.cs.stage3.alice.core.Element[] groups = world.getChildren(edu.cmu.cs.stage3.alice.core.Group.class);
			for (Element group : groups) {
				if (group.data.get("dummyObjectGroup") != null && group.data.get("dummyObjectGroup").equals("true") && world.groups.contains(group)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isMethodHookedUp(edu.cmu.cs.stage3.alice.core.Response response, edu.cmu.cs.stage3.alice.core.World world) {
		return isMethodHookedUp(response, world, new java.util.Vector());
	}

	private static boolean isMethodHookedUp(edu.cmu.cs.stage3.alice.core.Response response, edu.cmu.cs.stage3.alice.core.World world, java.util.Vector checkedMethods) {
		edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references = response.getRoot().getPropertyReferencesTo(response, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true);
		for (PropertyReference reference : references) {
			edu.cmu.cs.stage3.alice.core.Element referrer = reference.getProperty().getOwner();
			if (world.behaviors.contains(referrer)) {
				return true;
			} else if (referrer instanceof edu.cmu.cs.stage3.alice.core.Response && !checkedMethods.contains(referrer)) {
				checkedMethods.add(referrer);
				if (isMethodHookedUp((edu.cmu.cs.stage3.alice.core.Response) referrer, world, checkedMethods)) {
					return true;
				}
			}
		}

		return false;
	}

	public static edu.cmu.cs.stage3.alice.core.Response createUndoResponse(edu.cmu.cs.stage3.alice.core.Response response) {
		edu.cmu.cs.stage3.alice.core.Response undoResponse = null;

		Class responseClass = response.getClass();
		if (response instanceof edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) {
			edu.cmu.cs.stage3.alice.core.response.ResizeAnimation resizeResponse = (edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) response;
			edu.cmu.cs.stage3.alice.core.response.ResizeAnimation undoResizeResponse = new edu.cmu.cs.stage3.alice.core.response.ResizeAnimation();

			undoResizeResponse.amount.set(new Double(1.0 / resizeResponse.amount.doubleValue()));
			undoResizeResponse.asSeenBy.set(resizeResponse.asSeenBy.get());
			undoResizeResponse.dimension.set(resizeResponse.dimension.get());
			undoResizeResponse.likeRubber.set(resizeResponse.likeRubber.get());
			undoResizeResponse.subject.set(resizeResponse.subject.get());

			undoResponse = undoResizeResponse;
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) {
			try {
				undoResponse = (edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) responseClass.newInstance();
				edu.cmu.cs.stage3.alice.core.Direction direction = (edu.cmu.cs.stage3.alice.core.Direction) ((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).direction.getValue();
				edu.cmu.cs.stage3.alice.core.Direction opposite = new edu.cmu.cs.stage3.alice.core.Direction(direction.getMoveAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getMoveAxis()), direction.getTurnAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getTurnAxis()), direction.getRollAxis() == null ? null : edu.cmu.cs.stage3.math.Vector3.negate(direction.getRollAxis()));
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).subject.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).subject.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).amount.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).amount.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).direction.set(opposite);
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).asSeenBy.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).asSeenBy.get());
				((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) undoResponse).style.set(((edu.cmu.cs.stage3.alice.core.response.DirectionAmountTransformAnimation) response).style.get());
			} catch (IllegalAccessException e) {
				AuthoringTool.showErrorDialog("Error creating new response: " + responseClass, e);
			} catch (InstantiationException e) {
				AuthoringTool.showErrorDialog("Error creating new response: " + responseClass, e);
			}
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject.getValue();
			edu.cmu.cs.stage3.math.Matrix44 localTransformation = transformable.getLocalTransformation();
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).element.set(transformable);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).propertyName.set(transformable.localTransformation.getName());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).value.set(localTransformation);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).howMuch.set(edu.cmu.cs.stage3.util.HowMuch.INSTANCE);
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PropertyAnimation();
			edu.cmu.cs.stage3.alice.core.Element element = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).element.getElementValue();
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).element.set(element);
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).propertyName.set(((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName.get());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).value.set(element.getPropertyNamed(((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName.getStringValue()).getValue());
			((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) undoResponse).howMuch.set(((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).howMuch.get());
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.SayAnimation || response instanceof edu.cmu.cs.stage3.alice.core.response.ThinkAnimation || response instanceof edu.cmu.cs.stage3.alice.core.response.Wait || response instanceof edu.cmu.cs.stage3.alice.core.response.SoundResponse) {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoResponse.duration.set(new Double(0.0));
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PoseAnimation) {
			edu.cmu.cs.stage3.alice.core.response.PoseAnimation poseAnim = (edu.cmu.cs.stage3.alice.core.response.PoseAnimation) response;
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.PoseAnimation();
			edu.cmu.cs.stage3.alice.core.Transformable subject = (edu.cmu.cs.stage3.alice.core.Transformable) poseAnim.subject.get();
			edu.cmu.cs.stage3.alice.core.Pose currentPose = edu.cmu.cs.stage3.alice.core.Pose.manufacturePose(subject, subject);
			((edu.cmu.cs.stage3.alice.core.response.PoseAnimation) undoResponse).subject.set(subject);
			((edu.cmu.cs.stage3.alice.core.response.PoseAnimation) undoResponse).pose.set(currentPose);
			// TODO: handle CompositeAnimations... and everything else...
		}

		if (undoResponse != null) {
			undoResponse.duration.set(response.duration.get());
		} else {
			undoResponse = new edu.cmu.cs.stage3.alice.core.response.Wait();
			undoResponse.duration.set(new Double(0.0));
			AuthoringTool.showErrorDialog("Could not create undoResponse for " + response, null);
		}

		return undoResponse;
	}

	public static void addAffectedProperties(java.util.List affectedProperties, edu.cmu.cs.stage3.alice.core.Element element, String propertyName, edu.cmu.cs.stage3.util.HowMuch howMuch) {
		edu.cmu.cs.stage3.alice.core.Property property = element.getPropertyNamed(propertyName);
		if (property != null) {
			affectedProperties.add(property);
		}
		if (howMuch.getDescend()) {
			for (int i = 0; i < element.getChildCount(); i++) {
				edu.cmu.cs.stage3.alice.core.Element child = element.getChildAt(i);
				if (child.isFirstClass.booleanValue() && howMuch.getRespectDescendant()) {
					// respect descendant
				} else {
					addAffectedProperties(affectedProperties, child, propertyName, howMuch);
				}
			}
		}
	}

	/**
	 * this method only handles some cases. you cannot depend on it to return
	 * the correct Property array for all responses.
	 */
	public static edu.cmu.cs.stage3.alice.core.Property[] getAffectedProperties(edu.cmu.cs.stage3.alice.core.Response response) {
		edu.cmu.cs.stage3.alice.core.Property[] properties = null;

		if (response instanceof edu.cmu.cs.stage3.alice.core.response.ResizeAnimation) {
			edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject.getElementValue();
			java.util.Vector pVector = new java.util.Vector();
			pVector.add(transformable.localTransformation);
			if (transformable instanceof edu.cmu.cs.stage3.alice.core.Model) {
				pVector.add(((edu.cmu.cs.stage3.alice.core.Model) transformable).visualScale);
			}
			edu.cmu.cs.stage3.alice.core.Transformable[] descendants = (edu.cmu.cs.stage3.alice.core.Transformable[]) transformable.getDescendants(edu.cmu.cs.stage3.alice.core.Transformable.class);
			for (Transformable descendant : descendants) {
				pVector.add(descendant.localTransformation);
				if (descendant instanceof edu.cmu.cs.stage3.alice.core.Model) {
					pVector.add(((edu.cmu.cs.stage3.alice.core.Model) descendant).visualScale);
				}
			}
			properties = (edu.cmu.cs.stage3.alice.core.Property[]) pVector.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformAnimation) {
			edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformAnimation) response).subject.getElementValue();
			properties = new edu.cmu.cs.stage3.alice.core.Property[]{transformable.localTransformation};
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.TransformResponse) {
			edu.cmu.cs.stage3.alice.core.Transformable transformable = (edu.cmu.cs.stage3.alice.core.Transformable) ((edu.cmu.cs.stage3.alice.core.response.TransformResponse) response).subject.getElementValue();
			properties = new edu.cmu.cs.stage3.alice.core.Property[]{transformable.localTransformation};
		} else if (response instanceof edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) {
			edu.cmu.cs.stage3.alice.core.Element element = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).element.getElementValue();
			String propertyName = ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).propertyName.getStringValue();
			edu.cmu.cs.stage3.util.HowMuch howMuch = (edu.cmu.cs.stage3.util.HowMuch) ((edu.cmu.cs.stage3.alice.core.response.PropertyAnimation) response).howMuch.getValue();

			java.util.LinkedList propertyList = new java.util.LinkedList();
			addAffectedProperties(propertyList, element, propertyName, howMuch);
			properties = (edu.cmu.cs.stage3.alice.core.Property[]) propertyList.toArray(new edu.cmu.cs.stage3.alice.core.Property[0]);
		} // TODO: handle everything else

		if (properties == null) {
			properties = new edu.cmu.cs.stage3.alice.core.Property[0];
		}

		return properties;
	}

	public static edu.cmu.cs.stage3.alice.core.Billboard makeBillboard(edu.cmu.cs.stage3.alice.core.TextureMap textureMap, boolean makeTextureChild) {
		java.awt.image.ImageObserver sizeObserver = new java.awt.image.ImageObserver() {
			@Override
			public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
				return (infoflags & java.awt.image.ImageObserver.WIDTH & java.awt.image.ImageObserver.HEIGHT) > 0;
			}
		};

		if (textureMap != null) {
			int imageWidth = textureMap.image.getImageValue().getWidth(sizeObserver);
			int imageHeight = textureMap.image.getImageValue().getHeight(sizeObserver);
			double aspectRatio = (double) imageWidth / (double) imageHeight;
			double width, height;
			if (aspectRatio < 1.0) {
				width = 1.0;
				height = 1.0 / aspectRatio;
			} else {
				width = aspectRatio;
				height = 1.0;
			}

			edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[]{edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0f, 0.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, height, 0.0, 0.0, 0.0, 1.0, 0.0f, 1.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, height, 0.0, 0.0, 0.0, 1.0, 1.0f, 1.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0f, 0.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, 0.0, 0.0, 0.0, 0.0, -1.0, 1.0f, 0.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(-width / 2.0, height, 0.0, 0.0, 0.0, -1.0, 1.0f, 1.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, height, 0.0, 0.0, 0.0, -1.0, 0.0f, 1.0f), edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.createXYZIJKUV(width / 2.0, 0.0, 0.0, 0.0, 0.0, -1.0, 0.0f, 0.0f),};
			int[] indices = new int[]{0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7};

			edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geom = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
			geom.vertices.set(vertices);
			geom.indices.set(indices);

			edu.cmu.cs.stage3.alice.core.Billboard billboard = new edu.cmu.cs.stage3.alice.core.Billboard();
			billboard.isFirstClass.set(true);
			billboard.geometries.add(geom);
			billboard.geometry.set(geom);
			billboard.addChild(geom);

			if (makeTextureChild) {
				if (textureMap.getParent() != null) {
					textureMap.removeFromParent();
				}
				billboard.addChild(textureMap);
				billboard.textureMaps.add(textureMap);
				billboard.diffuseColorMap.set(textureMap);
				billboard.name.set(textureMap.name.getStringValue());
				textureMap.name.set(textureMap.name.getStringValue() + "_Texture");
			} else {
				billboard.name.set(textureMap.name.getStringValue() + "_Billboard");
				billboard.diffuseColorMap.set(textureMap);
			}

			return billboard;
		}

		return null;
	}

	public static void centerComponentOnScreen(java.awt.Component c) {
		java.awt.Dimension size = c.getSize();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		int x = screenSize.width / 2 - size.width / 2;
		int y = screenSize.height / 2 - size.height / 2;

		c.setLocation(x, y);
	}

	public static void ensureComponentIsOnScreen(java.awt.Component c) {
		java.awt.Point location = c.getLocation();
		java.awt.Dimension size = c.getSize();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		screenSize.height -= 28; // hack for standard Windows Task Bar

		if (!(c instanceof java.awt.Window)) {
			javax.swing.SwingUtilities.convertPointToScreen(location, c.getParent());
		}

		if (location.x < 0) {
			location.x = 0;
		} else if (location.x + size.width > screenSize.width) {
			location.x -= location.x + size.width - screenSize.width;
		}
		if (location.y < 0) {
			location.y = 0;
		} else if (location.y + size.height > screenSize.height) {
			location.y -= location.y + size.height - screenSize.height;
		}

		if (!(c instanceof java.awt.Window)) {
			javax.swing.SwingUtilities.convertPointFromScreen(location, c.getParent());
		}

		c.setLocation(location);
	}

	public static String getNameForNewChild(String baseName, edu.cmu.cs.stage3.alice.core.Element parent) {
		String name = baseName;

		if (name != null) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
		}

		if (name == null || parent == null) {
			return name;
		}

		if (parent.getChildNamedIgnoreCase(name) == null && parent.getChildNamedIgnoreCase(name + 1) == null) {
			return name;
		}

		if (baseName.length() < 1) {
			baseName = "copy";
		}

		// take baseName, strip a number off the end if necessary, and use next
		// available number after the stripped number
		int begin = baseName.length() - 1;
		int end = baseName.length();
		int endDigit = 2;
		while (begin >= 0) {
			try {
				endDigit = Integer.parseInt(baseName.substring(begin, end));
				name = baseName.substring(0, begin);
				begin--;
			} catch (NumberFormatException e) {
				break;
			}
		}
		baseName = name;
		for (int i = endDigit; i < Integer.MAX_VALUE; i++) {
			name = baseName + i;
			if (parent.getChildNamedIgnoreCase(name) == null) {
				return name;
			}
		}

		throw new RuntimeException("Unable to find a suitable new name; baseName = " + baseName + ", parent = " + parent);
	}

	/*
	 * public static String[] convertToStringArray( Object[] arr ) { String[]
	 * strings = new String[arr.length]; for( int i = 0; i < arr.length; i++ ) {
	 * strings[i] = (String)arr[i]; } return strings; }
	 */

	public static void setWorldTreeChildrenPropertiesStructure(java.util.Vector worldTreeChildrenPropertiesStructure) {
		AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure = worldTreeChildrenPropertiesStructure;
	}

	public static String[] getWorldTreeChildrenPropertiesStructure(Class elementClass) {
		if (AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure != null) {
			for (java.util.Iterator iter = AuthoringToolResources.resources.worldTreeChildrenPropertiesStructure.iterator(); iter.hasNext();) {
				Object o = iter.next();
				if (o instanceof edu.cmu.cs.stage3.util.StringObjectPair) {
					String className = ((edu.cmu.cs.stage3.util.StringObjectPair) o).getString();
					try {
						Class c = Class.forName(className);
						if (c.isAssignableFrom(elementClass)) {
							return (String[]) ((edu.cmu.cs.stage3.util.StringObjectPair) o).getObject();
						}
					} catch (java.lang.ClassNotFoundException e) {
						AuthoringTool.showErrorDialog("Can't find class " + className, e);
					}
				} else {
					AuthoringTool.showErrorDialog("Unexpected object found in worldTreeChildrenPropertiesStructure: " + o, null);
				}
			}
		}

		return null;
	}

	public static void addElementToAppropriateProperty(edu.cmu.cs.stage3.alice.core.Element element, edu.cmu.cs.stage3.alice.core.Element parent) {
		edu.cmu.cs.stage3.alice.core.property.ObjectArrayProperty oap = null;

		if (element instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.World) {
				oap = ((edu.cmu.cs.stage3.alice.core.World) parent).sandboxes;
			} else if (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				oap = ((edu.cmu.cs.stage3.alice.core.Transformable) parent).parts;
			} else if (parent instanceof edu.cmu.cs.stage3.alice.core.Group) {
				oap = ((edu.cmu.cs.stage3.alice.core.Group) parent).values;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Response) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).responses;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Behavior) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).behaviors;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Variable) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).variables;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Question) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).questions;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Sound) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).sounds;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).textureMaps;
			}
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.Pose) {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) {
				oap = ((edu.cmu.cs.stage3.alice.core.Transformable) parent).poses;
			}
		} else {
			if (parent instanceof edu.cmu.cs.stage3.alice.core.Sandbox) {
				oap = ((edu.cmu.cs.stage3.alice.core.Sandbox) parent).misc;
			}
		}

		if (oap != null) {
			if (!oap.contains(element)) {
				oap.add(element);
			}
		}
	}

	public static double getAspectRatio(edu.cmu.cs.stage3.alice.core.World world) {
		if (world != null) {
			edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[] spCameras = (edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera[]) world.getDescendants(edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera.class);
			if (spCameras.length > 0) {
				return spCameras[0].horizontalViewingAngle.doubleValue() / spCameras[0].verticalViewingAngle.doubleValue();
			}
		}
		return 0.0;
	}

	public static double getCurrentTime() {
		long timeMillis = System.currentTimeMillis() - startTime;
		return timeMillis / 1000.0;
	}

	public static void setImporterClasses(Class[] importers) {
		AuthoringToolResources.resources.importers = importers;
	}

	public static Class[] getImporterClasses() {
		return AuthoringToolResources.resources.importers;
	}

	public static void setEditorClasses(Class[] editors) {
		AuthoringToolResources.resources.editors = editors;
	}

	public static Class[] getEditorClasses() {
		return AuthoringToolResources.resources.editors;
	}

	public static void findAssignables(Class baseClass, java.util.Set result, boolean includeInterfaces) {
		if (baseClass != null) {
			if (!result.contains(baseClass)) {
				result.add(baseClass);

				if (includeInterfaces) {
					Class[] interfaces = baseClass.getInterfaces();
					for (Class interface1 : interfaces) {
						findAssignables(interface1, result, includeInterfaces);
					}
				}

				findAssignables(baseClass.getSuperclass(), result, includeInterfaces);
			}
		}
	}

	public static java.awt.datatransfer.DataFlavor getReferenceFlavorForClass(Class c) {
		if (!AuthoringToolResources.resources.flavorMap.containsKey(c)) {
			try {
				AuthoringToolResources.resources.flavorMap.put(c, new java.awt.datatransfer.DataFlavor(java.awt.datatransfer.DataFlavor.javaJVMLocalObjectMimeType + "; class=" + c.getName()));
			} catch (ClassNotFoundException e) {
				AuthoringTool.showErrorDialog("Can't find class " + c.getName(), e);
			}
		}
		return (java.awt.datatransfer.DataFlavor) AuthoringToolResources.resources.flavorMap.get(c);
	}

	public static Object getDefaultValueForClass(Class cls) {
		if (cls == Boolean.class) {
			return Boolean.TRUE;
		} else if (cls == Number.class) {
			return new Double(1);
		} else if (cls == String.class) {
			return new String("default string");
		} else if (cls == javax.vecmath.Vector3d.class) {
			return edu.cmu.cs.stage3.math.MathUtilities.createXAxis();
		} else if (cls == edu.cmu.cs.stage3.math.Vector3.class) {
			return new edu.cmu.cs.stage3.math.Vector3();
		} else if (cls == edu.cmu.cs.stage3.math.Quaternion.class) {
			return new edu.cmu.cs.stage3.math.Quaternion();
		} else if (javax.vecmath.Matrix4d.class.isAssignableFrom(cls)) {
			return new edu.cmu.cs.stage3.math.Matrix44();
		} else if (cls == java.awt.Color.class) {
			return java.awt.Color.white;
		} else if (cls == edu.cmu.cs.stage3.alice.scenegraph.Color.class) {
			return edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE;
		} else if (edu.cmu.cs.stage3.util.Enumerable.class.isAssignableFrom(cls)) {
			edu.cmu.cs.stage3.util.Enumerable[] items = edu.cmu.cs.stage3.util.Enumerable.getItems(cls);
			if (items.length > 0) {
				return items[0];
			} else {
				return null;
			}
		} else if (cls == edu.cmu.cs.stage3.alice.core.ReferenceFrame.class) {
			return AuthoringTool.getHack().getWorld();
		} else {
			return null;
		}
	}

	// public static javax.vecmath.Matrix4d getAGoodLookAtMatrix(
	// edu.cmu.cs.stage3.alice.core.Transformable transformable,
	// edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera ) {
	// if( (transformable != null) && (camera != null) ) {
	// edu.cmu.cs.stage3.alice.core.Transformable getAGoodLookDummy = new
	// edu.cmu.cs.stage3.alice.core.Transformable();
	// getAGoodLookDummy.vehicle.set( camera.vehicle.get() );
	// edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
	// double radius = bs.getRadius();
	// if( (radius == 0.0) || Double.isNaN( radius ) ) {
	// radius = 1.0;
	// }
	// double theta = Math.min( camera.horizontalViewingAngle.doubleValue(),
	// camera.verticalViewingAngle.doubleValue() );
	// double dist = radius/Math.sin( theta/2.0 );
	// double offset = dist/Math.sqrt( 3.0 );
	// javax.vecmath.Vector3d center = bs.getCenter();
	// if( center == null ) { // this should be unnecessary
	// center = transformable.getPosition();
	// }
	//
	// if( center != null ) {
	// if( (! Double.isNaN( center.x ) ) && (! Double.isNaN( center.y ) ) && (!
	// Double.isNaN( center.z ) ) && (! Double.isNaN( offset ) ) ) {
	// getAGoodLookDummy.setPositionRightNow( center.x - offset, center.y +
	// offset, center.z + offset, transformable );
	// getAGoodLookDummy.pointAtRightNow( transformable, new
	// edu.cmu.cs.stage3.math.Vector3( center ) );
	// javax.vecmath.Matrix4d result =
	// getAGoodLookDummy.getLocalTransformation();
	// getAGoodLookDummy.vehicle.set( null );
	// return result;
	// } else {
	// AuthoringTool.showErrorDialog( "bad bounding sphere center: " + center,
	// null );
	// }
	// } else {
	// AuthoringTool.showErrorDialog( "bounding sphere returned null center",
	// null );
	// }
	// }
	//
	// return null;
	// }

	public static double distanceToBackAfterGetAGoodLookAt(edu.cmu.cs.stage3.alice.core.Transformable transformable, edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera camera) {
		if (transformable != null && camera != null) {
			edu.cmu.cs.stage3.math.Sphere bs = transformable.getBoundingSphere();
			double radius = bs.getRadius();
			double theta = Math.min(camera.horizontalViewingAngle.doubleValue(), camera.verticalViewingAngle.doubleValue());
			return radius / Math.sin(theta / 2.0) + radius;
		}

		return 0.0;
	}

	public static boolean areExperimentalFeaturesEnabled() {
		return AuthoringToolResources.resources.experimentalFeaturesEnabled;
	}

	public static void setExperimentalFeaturesEnabled(boolean enabled) {
		AuthoringToolResources.resources.experimentalFeaturesEnabled = enabled;
	}

	public static void putMiscItem(Object key, Object item) {
		AuthoringToolResources.resources.miscMap.put(key, item);
	}

	public static Object getMiscItem(Object key) {
		return AuthoringToolResources.resources.miscMap.get(key);
	}

	public static void garbageCollectIfPossible(edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] references) {
		for (PropertyReference reference : references) {
			edu.cmu.cs.stage3.alice.core.Element element = reference.getProperty().getOwner();
			// if( element instanceof
			// edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse )
			// {
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] metaReferences = element.getRoot().getPropertyReferencesTo(element, edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS, false, true);
			if (metaReferences.length == 0) {
				element.getParent().removeChild(element);
			}
			// }
		}
	}

	public static String formatMemorySize(long bytes) {
		String sizeString = null;
		if (bytes < 1024) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes) + " bytes";
		} else if (bytes < 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format((double) bytes / (double) 1024) + " KB";
		} else if (bytes < 1024L * 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes / ((double) 1024L * 1024L)) + " MB";
		} else if (bytes < 1024L * 1024L * 1024L * 1024L) {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes / ((double) 1024L * 1024L * 1024L)) + " GB";
		} else {
			sizeString = AuthoringToolResources.resources.decimalFormatter.format(bytes / ((double) 1024L * 1024L * 1024L * 1024L)) + " TB";
		}
		return sizeString;
	}

	public static String formatTime(double seconds) {
		if (Double.isNaN(seconds)) {
			return "?:??";
		} else {
			java.text.DecimalFormat decFormatter = new java.text.DecimalFormat(".000");
			java.text.DecimalFormat secMinFormatter1 = new java.text.DecimalFormat("00");
			java.text.DecimalFormat secMinFormatter2 = new java.text.DecimalFormat("#0");

			double secondsFloored = (int) Math.floor(seconds);
			double decimal = seconds - secondsFloored;
			double secs = secondsFloored % 60.0;
			double minutes = (secondsFloored - secs) / 60.0 % 60.0;
			double hours = (secondsFloored - 60.0 * minutes - secs) / (60.0 * 60.0);

			String timeString = secMinFormatter1.format(secs) + decFormatter.format(decimal);
			if (hours > 0.0) {
				timeString = secMinFormatter1.format(minutes) + ":" + timeString;
				timeString = secMinFormatter2.format(hours) + ":" + timeString;
			} else {
				timeString = secMinFormatter2.format(minutes) + ":" + timeString;
			}

			return timeString;
		}
	}

	public static void printHierarchy(java.awt.Component c) {
		printHierarchy(c, 0);
	}
	private static void printHierarchy(java.awt.Component c, int level) {
		String tabs = "";
		for (int i = 0; i < level; i++) {
			tabs += "--";
		}
		System.out.println(tabs + c.getClass().getName() + "_" + c.hashCode());

		if (c instanceof java.awt.Container) {
			java.awt.Component[] children = ((java.awt.Container) c).getComponents();
			for (Component element : children) {
				printHierarchy(element, level + 1);
			}
		}
	}

	private static void initKeyCodesToStrings() {
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_0), "0");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_1), "1");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_2), "2");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_3), "3");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_4), "4");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_5), "5");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_6), "6");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_7), "7");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_8), "8");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_9), "9");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_A), "A");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_B), "B");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_C), "C");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_D), "D");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_E), "E");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_F), "F");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_G), "G");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_H), "H");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_I), "I");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_J), "J");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_K), "K");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_L), "L");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_M), "M");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_N), "N");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_O), "O");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_P), "P");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Q), "Q");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_R), "R");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_S), "S");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_T), "T");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_U), "U");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_V), "V");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_W), "W");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_X), "X");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Y), "Y");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_Z), "Z");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_ENTER), "enter");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_SPACE), "space");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_UP), "upArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_DOWN), "downArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_LEFT), "leftArrow");
		AuthoringToolResources.resources.keyCodesToStrings.put(new Integer(java.awt.event.KeyEvent.VK_RIGHT), "rightArrow");
	}

	public static void copyFile(java.io.File from, java.io.File to) throws java.io.IOException {
		if (!to.exists()) {
			to.createNewFile();
		}
		java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.io.FileInputStream(from));
		java.io.BufferedOutputStream out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(to));

		int b = in.read();
		while (b != -1) {
			out.write(b);
			b = in.read();
		}

		in.close();
		out.flush();
		out.close();
	}

	// ///////////////////////////
	// HACK code for stencils
	// ///////////////////////////

	public static String getPrefix(String token) {
		if (token.indexOf("<") > -1 && token.indexOf(">") > token.indexOf("<")) {
			return token.substring(0, token.indexOf("<"));
		} else {
			return token;
		}
	}

	public static String getSpecifier(String token) {
		if (token.indexOf("<") > -1 && token.indexOf(">") > token.indexOf("<")) {
			if (!System.getProperty("os.name").startsWith("Window")) {
				token = token.replaceAll("\\\\", java.io.File.separator);
			}
			return token.substring(token.indexOf("<") + 1, token.indexOf(">"));
		} else {
			return null;
		}
	}

	public static java.awt.Component findElementDnDPanel(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Element element) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o).getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor)) {
							edu.cmu.cs.stage3.alice.core.Element e = (edu.cmu.cs.stage3.alice.core.Element) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor);
							if (element.equals(e)) {
								return true;
							}
						}
					} catch (Exception e) {
						AuthoringTool.showErrorDialog("Error finding ElementDnDPanel.", e);
					}
				}
				return false;
			}
		};
		java.awt.Component toReturn = findComponent(root, criterion);
		if (toReturn instanceof edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) {
			return ((edu.cmu.cs.stage3.alice.authoringtool.editors.compositeeditor.MainCompositeElementPanel) toReturn).getWorkSpace();
		} else {
			return toReturn;
		}
	}

	public static java.awt.Component findPropertyDnDPanel(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Element element, final String propertyName) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o).getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor)) {
							edu.cmu.cs.stage3.alice.core.Property p = (edu.cmu.cs.stage3.alice.core.Property) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.PropertyReferenceTransferable.propertyReferenceFlavor);
							edu.cmu.cs.stage3.alice.core.Element e = p.getOwner();
							if (element.equals(e) && p.getName().equals(propertyName)) {
								return true;
							}
						}
					} catch (Exception e) {
						AuthoringTool.showErrorDialog("Error finding PropertyDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findUserDefinedResponseDnDPanel(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Response actualResponse) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o).getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor)) {
							edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor);
							if (p.getActualResponse().equals(actualResponse)) {
								return true;
							}
						}
					} catch (Exception e) {
						AuthoringTool.showErrorDialog("Error finding UserDefinedResponseDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findUserDefinedQuestionDnDPanel(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Question actualQuestion) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o).getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor)) {
							edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor);
							if (p.getActualQuestion().equals(actualQuestion)) {
								return true;
							}
						}
					} catch (Exception e) {
						AuthoringTool.showErrorDialog("Error finding UserDefinedQuestionDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findPrototypeDnDPanel(java.awt.Container root, final Class elementClass) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) {
					try {
						java.awt.datatransfer.Transferable transferable = ((edu.cmu.cs.stage3.alice.authoringtool.util.DnDGroupingPanel) o).getTransferable();
						if (transferable != null && AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor)) {
							edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype p = (edu.cmu.cs.stage3.alice.authoringtool.util.ElementPrototype) transferable.getTransferData(edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementPrototypeReferenceTransferable.elementPrototypeReferenceFlavor);
							if (p.getElementClass().equals(elementClass)) {
								return true;
							}
						}
					} catch (Exception e) {
						AuthoringTool.showErrorDialog("Error finding PrototypeDnDPanel.", e);
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findPropertyViewController(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Element element, final String propertyName) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) {
					edu.cmu.cs.stage3.alice.core.Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.PropertyViewController) o).getProperty();
					if (p.getOwner().equals(element) && p.getName().equals(propertyName)) {
						return true;
					}
				} else if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController) {
					edu.cmu.cs.stage3.alice.core.Property p = ((edu.cmu.cs.stage3.alice.authoringtool.viewcontroller.CollectionPropertyViewController) o).getProperty();
					if (p.getOwner().equals(element) && p.getName().equals(propertyName)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findButton(java.awt.Container root, final String buttonText) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof javax.swing.JButton) {
					if (((javax.swing.JButton) o).getText().equals(buttonText)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findEditObjectButton(java.awt.Container root, final edu.cmu.cs.stage3.alice.core.Element element) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) {
					if (((edu.cmu.cs.stage3.alice.authoringtool.util.EditObjectButton) o).getObject().equals(element)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findGalleryObject(java.awt.Container root, final String uniqueIdentifier) {
		edu.cmu.cs.stage3.util.Criterion criterion = new edu.cmu.cs.stage3.util.Criterion() {
			@Override
			public boolean accept(Object o) {
				if (o instanceof edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) {
					if (((edu.cmu.cs.stage3.alice.authoringtool.galleryviewer.GalleryObject) o).getUniqueIdentifier().equals(uniqueIdentifier)) {
						return true;
					}
				}
				return false;
			}
		};

		return findComponent(root, criterion);
	}

	public static java.awt.Component findComponent(java.awt.Container root, edu.cmu.cs.stage3.util.Criterion criterion) {
		if (criterion.accept(root)) {
			return root;
		}

		java.awt.Component[] children = root.getComponents();
		for (Component element : children) {
			if (element instanceof java.awt.Container) {
				java.awt.Component result = findComponent((java.awt.Container) element, criterion);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}
}