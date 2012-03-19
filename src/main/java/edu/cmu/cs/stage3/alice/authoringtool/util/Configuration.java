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
public final class Configuration {
	public final static int VIS_OPEN = 1;
	public final static int VIS_ADVANCED = 2;
	public final static int VIS_HIDDEN = 4;
	public final static int VIS_ALL = VIS_OPEN | VIS_ADVANCED | VIS_HIDDEN;

	// static access
	public static String getValue(Package p, String relativeKey) {
		return _getValue(p.getName() + "." + relativeKey);
	}

	public static String[] getValueList(Package p, String relativeKey) {
		return _getValueList(p.getName() + "." + relativeKey);
	}

	public static void setValue(Package p, String relativeKey, String value) {
		_setValue(p.getName() + "." + relativeKey, value);
	}

	public static void setValueList(Package p, String relativeKey, String[] values) {
		_setValueList(p.getName() + "." + relativeKey, values);
	}

	public static void addToValueList(Package p, String relativeKey, String item) {
		_addToValueList(p.getName() + "." + relativeKey, item);
	}

	public static void removeFromValueList(Package p, String relativeKey, String item) {
		_removeFromValueList(p.getName() + "." + relativeKey, item);
	}

	public static boolean isList(Package p, String relativeKey) {
		return _isList(p.getName() + "." + relativeKey);
	}

	public static boolean keyExists(Package p, String relativeKey) {
		return _keyExists(p.getName() + "." + relativeKey);
	}

	public static void deleteKey(Package p, String relativeKey) {
		_deleteKey(p.getName() + "." + relativeKey);
	}

	public static String[] getSubKeys(Package p, String relativeKey, int visibility) {
		return _getSubKeys(p.getName() + "." + relativeKey, visibility);
	}

	public static void setVisibility(Package p, String relativeKey, int visibility) {
		_setVisibility(p.getName() + "." + relativeKey, visibility);
	}

	// instance access
	private String keyPrefix;

	private Configuration(Package p) {
		keyPrefix = p.getName() + ".";
	}

	public static Configuration getLocalConfiguration(Package p) {
		return new Configuration(p);
	}

	public String getValue(String relativeKey) {
		return _getValue(keyPrefix + relativeKey);
	}

	public String[] getValueList(String relativeKey) {
		return _getValueList(keyPrefix + relativeKey);
	}

	public void setValue(String relativeKey, String value) {
		_setValue(keyPrefix + relativeKey, value);
	}

	public void setValueList(String relativeKey, String[] values) {
		_setValueList(keyPrefix + relativeKey, values);
	}

	public void addToValueList(String relativeKey, String item) {
		_addToValueList(keyPrefix + relativeKey, item);
	}

	public void removeFromValueList(String relativeKey, String item) {
		_removeFromValueList(keyPrefix + relativeKey, item);
	}

	public boolean isList(String relativeKey) {
		return _isList(keyPrefix + relativeKey);
	}

	public boolean keyExists(String relativeKey) {
		return _keyExists(keyPrefix + relativeKey);
	}

	public void deleteKey(String relativeKey) {
		_deleteKey(keyPrefix + relativeKey);
	}

	public String[] getSubKeys(String relativeKey, int visibility) {
		return _getSubKeys(keyPrefix + relativeKey, visibility);
	}

	public void setVisibility(String relativeKey, int visibility) {
		_setVisibility(keyPrefix + relativeKey, visibility);
	}

	// internals
	private static final java.io.File configLocation = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "AlicePreferences.xml").getAbsoluteFile();
	private static Key root;

	static {
		root = new Key();
		root.name = "<root>";
		root.subKeys = new java.util.HashMap();
		java.io.File aliceHasNotExitedFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
		if (aliceHasNotExitedFile.canRead()) {
			try {
				storeConfig();
			} catch (java.io.IOException e2) {
				edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Unable to create new preferences file.", e2);
			}
		} else {
			try {
				loadConfig(configLocation);
			} catch (Exception e) {

			}
		}
	}

	private static class Key {
		public String name;
		public int visibility;
		public String value;
		public java.util.ArrayList valueList;
		public java.util.HashMap subKeys;

		public Key getSubKey(String name) {
			if (subKeys != null) {
				int i = name.indexOf('.');
				if (i == -1) {
					return (Key) subKeys.get(name);
				} else {
					Key subKey = (Key) subKeys.get(name.substring(0, i));
					if (subKey != null) {
						return subKey.getSubKey(name.substring(i + 1));
					}
				}
			}
			return null;
		}

		public Key createSubKey(String name) {
			if (subKeys == null) {
				subKeys = new java.util.HashMap();
			}

			int i = name.indexOf('.');
			if (i == -1) {
				Key subKey = (Key) subKeys.get(name);
				if (subKey == null) {
					subKey = new Key();
					subKey.name = name;
					subKeys.put(name, subKey);
				}
				return subKey;
			} else {
				Key subKey = (Key) subKeys.get(name.substring(0, i));
				if (subKey == null) {
					subKey = new Key();
					subKey.name = name.substring(0, i);
					subKeys.put(subKey.name, subKey);
				}
				return subKey.createSubKey(name.substring(i + 1));
			}
		}

		public void deleteSubKey(String name) {
			if (subKeys != null) {
				int i = name.indexOf('.');
				if (i == -1) {
					subKeys.remove(name);
				} else {
					Key subKey = (Key) subKeys.get(name.substring(0, i));
					if (subKey != null) {
						subKey.deleteSubKey(name.substring(i + 1));
					}
				}
			}
		}

		@Override
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("\nname: " + name + "\n");
			s.append("visibility: " + visibility + "\n");
			s.append("value: " + value + "\n");
			s.append("valueList: " + valueList + "\n");
			s.append("subKeys: " + subKeys + "\n");
			return s.toString();
		}
	}

	// somebody is going to hate me for the underscores,
	// but I needed to avoid duplication with the instance access methods,
	// so I borrowed a convention from python.

	private static String _getValue(String keyName) {
		Key key = root.getSubKey(keyName);
		if (key != null) {
			return key.value;
		}
		return null;
	}

	private static String[] _getValueList(String keyName) {
		Key key = root.getSubKey(keyName);
		if (key != null) {
			if (key.valueList != null) {
				return (String[]) key.valueList.toArray(new String[0]);
			}
		}
		return null;
	}

	private static void _setValue(String keyName, String value) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		String oldValue = key.value;
		String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, value, oldValueList, null);

		if (key.valueList != null) {
			key.valueList = null;
		}
		key.value = value;

		fireChanged(keyName, _isList(keyName), oldValue, value, oldValueList, null);
	}

	private static void _setValueList(String keyName, String[] values) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		String oldValue = key.value;
		String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, values);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList(values == null ? 0 : values.length);
		} else {
			key.valueList.clear();
		}
		if (values != null) {
			for (String value : values) {
				key.valueList.add(value);
			}
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, values);
	}

	private static void _addToValueList(String keyName, String item) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		String oldValue = key.value;
		String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList();
		}
		if (item != null) {
			key.valueList.add(item);
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
	}

	private static void _removeFromValueList(String keyName, String item) {
		Key key = root.getSubKey(keyName);

		if (key == null) {
			key = root.createSubKey(keyName);
		}

		String oldValue = key.value;
		String[] oldValueList = _getValueList(keyName);
		fireChanging(keyName, _isList(keyName), oldValue, null, oldValueList, null);

		if (key.value != null) {
			key.value = null;
		}

		if (key.valueList == null) {
			key.valueList = new java.util.ArrayList();
		} else {
			if (item != null) {
				key.valueList.remove(item);
			}
		}

		fireChanged(keyName, _isList(keyName), oldValue, null, oldValueList, _getValueList(keyName));
	}

	private static boolean _isList(String keyName) {
		Key key = root.getSubKey(keyName);
		if (key != null) {
			return key.valueList != null;
		}
		return false;
	}

	private static boolean _keyExists(String keyName) {
		return root.getSubKey(keyName) != null;
	}

	private static void _deleteKey(String keyName) {
		root.deleteSubKey(keyName);
	}

	private static String[] _getSubKeys(String keyName, int visibility) {
		Key key = root.getSubKey(keyName);
		if (key != null) {
			java.util.ArrayList list = new java.util.ArrayList(key.subKeys.size());
			for (java.util.Iterator iter = key.subKeys.keySet().iterator(); iter.hasNext();) {
				Key subKey = (Key) iter.next();
				if ((subKey.visibility & visibility) > 0) {
					list.add(subKey.name);
				}
			}
			return (String[]) list.toArray(new String[0]);
		}
		return null;
	}

	private static void _setVisibility(String keyName, int visibility) {
		Key key = root.getSubKey(keyName);
		if (key != null) {
			key.visibility = visibility;
		}
	}

	// IO
	private static void loadConfig(java.io.File file) throws java.io.IOException {
		loadConfig(file.toURL());
	}

	private static void loadConfig(java.net.URL url) throws java.io.IOException {
		java.io.BufferedInputStream bis = new java.io.BufferedInputStream(url.openStream());
		loadConfig(bis);
		bis.close();
	}

	private static void loadConfig(java.io.InputStream is) throws java.io.IOException {
		root.subKeys = new java.util.HashMap();

		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		try {
			javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(is);

			org.w3c.dom.Element rootElement = document.getDocumentElement();
			rootElement.normalize();

			org.w3c.dom.NodeList childNodes = rootElement.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				org.w3c.dom.Node childNode = childNodes.item(i);
				if (childNode instanceof org.w3c.dom.Element) {
					org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
					String tagName = childElement.getTagName();
					if (tagName.equals("key")) {
						Key subKey = loadKey(childElement);
						if (subKey != null && subKey.name != null) {
							root.subKeys.put(subKey.name, subKey);
							// System.out.println( "loaded subKey: " + subKey );
						}
					}
				}
			}
			// } catch( org.xml.sax.SAXParseException spe ) {
			// if( spe.getException() != null ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file; line " + spe.getLineNumber() +
			// ", uri " + spe.getSystemId() + "\nmessage: " + spe.getMessage(),
			// spe.getException() );
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file; line " + spe.getLineNumber() +
			// ", uri " + spe.getSystemId() + "\nmessage: " + spe.getMessage(),
			// spe );
			// }
			// } catch( org.xml.sax.SAXException sxe ) {
			// if( sxe.getException() != null ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", sxe.getException() );
			// } else {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", sxe );
			// }
			// } catch( javax.xml.parsers.ParserConfigurationException pce ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Error parsing preferences file.", pce );
			// }
		} catch (Exception e) {
			edu.cmu.cs.stage3.swing.DialogManager.showMessageDialog("Alice had trouble reading your preferences but will continue to run normally", "Unable to load preferences", javax.swing.JOptionPane.WARNING_MESSAGE);
		}
	}

	private static Key loadKey(org.w3c.dom.Element keyElement) {
		Key key = new Key();

		String visibility = keyElement.getAttribute("visibility").trim();
		if (visibility.equals("open")) {
			key.visibility = VIS_OPEN;
		} else if (visibility.equals("advanced")) {
			key.visibility = VIS_ADVANCED;
		} else if (visibility.equals("hidden")) {
			key.visibility = VIS_HIDDEN;
		}

		java.util.HashMap map = parseSingleNode(keyElement);

		org.w3c.dom.Element nameElement = (org.w3c.dom.Element) map.get("name");
		if (nameElement != null) {
			org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(nameElement).get("text");
			if (textNode != null) {
				key.name = textNode.getData().trim();
			}
		}

		org.w3c.dom.Element valueElement = (org.w3c.dom.Element) map.get("value");
		if (valueElement != null) {
			org.w3c.dom.Element listElement = (org.w3c.dom.Element) parseSingleNode(valueElement).get("list");
			if (listElement != null) {
				key.valueList = new java.util.ArrayList();
				java.util.ArrayList items = (java.util.ArrayList) parseSingleNode(listElement).get("items");
				if (items != null) {
					for (java.util.Iterator iter = items.iterator(); iter.hasNext();) {
						org.w3c.dom.Element itemElement = (org.w3c.dom.Element) iter.next();
						if (itemElement != null) {
							org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(itemElement).get("text");
							if (textNode != null) {
								key.valueList.add(textNode.getData().trim());
							}
						}
					}
				}
			} else {
				org.w3c.dom.Text textNode = (org.w3c.dom.Text) parseSingleNode(valueElement).get("text");
				if (textNode != null) {
					key.value = textNode.getData().trim();
				}
			}
		}

		java.util.ArrayList keys = (java.util.ArrayList) map.get("keys");
		if (keys != null) {
			for (java.util.Iterator iter = keys.iterator(); iter.hasNext();) {
				org.w3c.dom.Element subKeyElement = (org.w3c.dom.Element) iter.next();
				if (subKeyElement != null) {
					if (key.subKeys == null) {
						key.subKeys = new java.util.HashMap();
					}
					Key subKey = loadKey(subKeyElement);
					if (subKey != null && subKey.name != null) {
						key.subKeys.put(subKey.name, subKey);
					}
				}
			}
		}

		return key;
	}

	/**
	 * maps:
	 * 
	 * "name" -> <name> Element "value" -> <value> Element "list" -> <list>
	 * Element "items" -> ArrayList of <item> Elements "keys" -> ArrayList of
	 * <key> Elements "text" -> last Text Node encountered
	 */
	private static java.util.HashMap parseSingleNode(org.w3c.dom.Node node) {
		// TODO: check for efficiency problems with creating this HashMap for
		// each Node...
		java.util.HashMap map = new java.util.HashMap();

		org.w3c.dom.NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			org.w3c.dom.Node childNode = childNodes.item(i);

			if (childNode instanceof org.w3c.dom.Element) {
				org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
				String tagName = childElement.getTagName();
				if (tagName.equals("name")) {
					map.put("name", childElement);
				} else if (tagName.equals("value")) {
					map.put("value", childElement);
				} else if (tagName.equals("list")) {
					map.put("list", childElement);
				} else if (tagName.equals("item")) {
					java.util.ArrayList list = (java.util.ArrayList) map.get("items");
					if (list == null) {
						list = new java.util.ArrayList();
						map.put("items", list);
					}
					list.add(childElement);
				} else if (tagName.equals("key")) {
					java.util.ArrayList list = (java.util.ArrayList) map.get("keys");
					if (list == null) {
						list = new java.util.ArrayList();
						map.put("keys", list);
					}
					list.add(childElement);
				}
			} else if (childNode instanceof org.w3c.dom.Text) {
				map.put("text", childNode);
			}
		}

		return map;
	}

	private static org.w3c.dom.Element getChildElementNamed(String name, org.w3c.dom.Node node) {
		org.w3c.dom.NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			org.w3c.dom.Node childNode = childNodes.item(i);
			if (childNode instanceof org.w3c.dom.Element) {
				org.w3c.dom.Element childElement = (org.w3c.dom.Element) childNode;
				String tagName = childElement.getTagName();
				if (tagName.equals(name)) {
					return childElement;
				}
			}
		}
		return null;
	}

	public static void storeConfig() throws java.io.IOException {
		if (configLocation.getParentFile().exists() && configLocation.getParentFile().canWrite()) {
			if (configLocation.exists()) {
				if (configLocation.canWrite()) {
					storeConfig(configLocation);
				}
			} else {
				storeConfig(configLocation);
			}
		}
	}

	private static void storeConfig(java.io.File file) throws java.io.IOException {
		java.io.BufferedOutputStream bos = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));
		storeConfig(bos);
		bos.flush();
		bos.close();
	}

	private static void storeConfig(java.io.OutputStream os) throws java.io.IOException {
		javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		try {
			javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
			org.w3c.dom.Document document = builder.newDocument();

			org.w3c.dom.Element rootElement = document.createElement("configuration");
			document.appendChild(rootElement);

			if (root.subKeys != null) {
				for (java.util.Iterator iter = root.subKeys.values().iterator(); iter.hasNext();) {
					Key key = (Key) iter.next();
					rootElement.appendChild(makeKeyElement(document, key));
				}
			}

			document.getDocumentElement().normalize();

			edu.cmu.cs.stage3.xml.Encoder.write(document, os);
			// try {
			// Class documentClass = document.getClass();
			// java.lang.reflect.Method writeMethod = documentClass.getMethod(
			// "write", new Class[] { java.io.OutputStream.class } );
			// writeMethod.invoke( document, new Object[] { os } );
			// } catch( Exception e ) {
			// edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog(
			// "Unable to store preferences.  Cannot invoke 'write' method.", e
			// );
			// }
			// ((org.apache.crimson.tree.XmlDocument)document).write( os );
			// ((com.sun.xml.tree.XmlDocument)document).write( os );
		} catch (javax.xml.parsers.ParserConfigurationException pce) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog("Error parsing preferences file.", pce);
		}
	}

	private static org.w3c.dom.Element makeKeyElement(org.w3c.dom.Document document, Key key) {
		org.w3c.dom.Element keyElement = document.createElement("key");
		if ((key.visibility & VIS_OPEN) > 0) {
			keyElement.setAttribute("visibility", "open");
		} else if ((key.visibility & VIS_ADVANCED) > 0) {
			keyElement.setAttribute("visibility", "advanced");
		} else if ((key.visibility & VIS_HIDDEN) > 0) {
			keyElement.setAttribute("visibility", "hidden");
		} else {
			keyElement.setAttribute("visibility", "open");
		}

		org.w3c.dom.Element nameElement = document.createElement("name");
		nameElement.appendChild(document.createTextNode(key.name));
		keyElement.appendChild(nameElement);

		if (key.value != null) {
			org.w3c.dom.Element valueElement = document.createElement("value");
			valueElement.appendChild(document.createTextNode(key.value));
			keyElement.appendChild(valueElement);
		} else if (key.valueList != null) {
			org.w3c.dom.Element valueElement = document.createElement("value");
			org.w3c.dom.Element listElement = document.createElement("list");
			for (java.util.Iterator iter = key.valueList.iterator(); iter.hasNext();) {
				org.w3c.dom.Element itemElement = document.createElement("item");
				itemElement.appendChild(document.createTextNode((String) iter.next()));
				listElement.appendChild(itemElement);
			}
			valueElement.appendChild(listElement);
			keyElement.appendChild(valueElement);
		}

		if (key.subKeys != null) {
			for (java.util.Iterator iter = key.subKeys.values().iterator(); iter.hasNext();) {
				Key subKey = (Key) iter.next();
				keyElement.appendChild(makeKeyElement(document, subKey));
			}
		}

		return keyElement;
	}

	// Listening
	protected static java.util.HashSet listeners = new java.util.HashSet();

	public static void addConfigurationListener(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener listener) {
		listeners.add(listener);
	}

	public static void removeConfigurationListener(edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener listener) {
		listeners.remove(listener);
	}

	protected static void fireChanging(String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
		// keyName, isList, oldValue, newValue, oldValueList, newValueList );
		// for( java.util.Iterator iter = listeners.iterator(); iter.hasNext();
		// ) {
		// ((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener)iter.next()).changing(
		// ev );
		// }
		edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(keyName, isList, oldValue, newValue, oldValueList, newValueList);
		Object[] listenerArray = listeners.toArray();
		for (Object element : listenerArray) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener) element).changing(ev);
		}
	}

	protected static void fireChanged(String keyName, boolean isList, String oldValue, String newValue, String[] oldValueList, String[] newValueList) {
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev = new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(
		// keyName, isList, oldValue, newValue, oldValueList, newValueList );
		// for( java.util.Iterator iter = listeners.iterator(); iter.hasNext();
		// ) {
		// ((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener)iter.next()).changed(
		// ev );
		// }
		edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent ev = new edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent(keyName, isList, oldValue, newValue, oldValueList, newValueList);
		Object[] listenerArray = listeners.toArray();
		for (Object element : listenerArray) {
			((edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener) element).changed(ev);
		}
	}
}