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

/**
 * @author Jason Pratt
 */
public class JAlice {
	// version information
	private static String version = "Unknown version";
	private static String backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(127.0 / 255.0, 138.0 / 255.0, 209.0 / 255.0).toString();
	private static String directory = null;
	static {
		try {
			java.io.File versionFile = new java.io.File(getAliceHomeDirectory(), "etc/version.txt").getAbsoluteFile();
			if (versionFile.exists()) {
				if (versionFile.canRead()) {
					java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(versionFile));
					String versionString = br.readLine();
					String colorString = br.readLine();
					directory = br.readLine();
					br.close();
					if (colorString != null) {
						colorString = colorString.trim();
						if (colorString.length() > 0) {
							try {
								if (colorString.startsWith("0x") == false) {
									String[] color = colorString.split(",");
									double red = Integer.decode(color[0]).doubleValue() / 255.0;
									double green = Integer.decode(color[1]).doubleValue() / 255.0;
									double blue = Integer.decode(color[2]).doubleValue() / 255.0;
									backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(red, green, blue).toString();
								} else {
									java.awt.Color newColor = java.awt.Color.decode(colorString);
									backgroundColor = new edu.cmu.cs.stage3.alice.scenegraph.Color(newColor).toString();
								}
							} catch (Throwable colorT) {
								colorT.printStackTrace();
							}
						}
					}
					if (versionString != null) {
						versionString = versionString.trim();
						if (versionString.length() > 0) {
							version = versionString;
						} else {
							version = "Unknown version [first line of version.txt empty]";
						}
					} else {
						version = "Unknown version [version.txt is empty]";
					}
				} else {
					version = "Unknown version [cannot read version.txt]";
				}
			} else {
				version = "Unknown version [version.txt does not exist]";
			}
		} catch (Throwable t) {
			t.printStackTrace();
			version = "Unknown version [error while reading version.txt]";
		}
	}

	public static String getVersion() {
		return version;
	}

	static java.io.File aliceHomeDirectory = null;
	static java.io.File aliceUserDirectory = null;

	static edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen splashScreen;
	static java.io.File defaultWorld;
	static java.io.File worldToLoad = null;
	static boolean stdOutToConsole = false;
	static boolean stdErrToConsole = false;
	static String defaultRendererClassname = null;
	static AuthoringTool authoringTool;

	static boolean mainHasFinished = false;

	// ////////////////////
	// main
	// ////////////////////

	public static void main(String[] args) {
		try {
			String[] mp3args = new String[0];
			// System.out.println( "attempting to register mp3 capability... "
			// );
			com.sun.media.codec.audio.mp3.JavaDecoder.main(mp3args);
		} catch (Throwable t) {
			// System.out.println( "FAILED." );
			t.printStackTrace(System.out);
		}

		try {
			boolean useJavaBasedSplashScreen = true;
			String useSplashScreenString = System.getProperty("alice.useJavaBasedSplashScreen");
			if (useSplashScreenString != null && !useSplashScreenString.equalsIgnoreCase("true")) {
				useJavaBasedSplashScreen = false;
			}
			parseCommandLineArgs(args);
			if (useJavaBasedSplashScreen) {
				splashScreen = initSplashScreen();
				splashScreen.showSplash();
			}
			defaultWorld = new java.io.File(getAliceHomeDirectory(), "etc/default.a2w").getAbsoluteFile();
			Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.Configuration");
			configInit();
			try {
				java.io.File aliceHasNotExitedFile = new java.io.File(edu.cmu.cs.stage3.alice.authoringtool.JAlice.getAliceUserDirectory(), "aliceHasNotExited.txt");
				if (aliceHasNotExitedFile.exists()) {
					aliceHasNotExitedFile.delete();
				}
				aliceHasNotExitedFile.createNewFile();
				java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(new java.io.FileOutputStream(aliceHasNotExitedFile));
				writer.write("Alice has not exited propertly yet.");
				writer.flush();
				writer.close();
			} catch (Exception e) {}
			Class.forName("edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources");
			Class.forName("edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities");
			authoringTool = new AuthoringTool(defaultWorld, worldToLoad, stdOutToConsole, stdErrToConsole);
			if (useJavaBasedSplashScreen) {
				splashScreen.hideSplash();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			// System.exit( 1 );
		}

		mainHasFinished = true;
	}

	private static edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen initSplashScreen() {
		java.io.File splashFile = new java.io.File(getAliceHomeDirectory(), "etc/AliceSplash.jpg").getAbsoluteFile();
		java.awt.Image splashImage = java.awt.Toolkit.getDefaultToolkit().getImage(splashFile.getAbsolutePath());
		return new edu.cmu.cs.stage3.alice.authoringtool.util.SplashScreen(splashImage);
	}

	private static void configInit() {
		final edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringtoolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(JAlice.class.getPackage());
		// System.out.println(backgroundColor);
		// System.out.println(new edu.cmu.cs.stage3.alice.scenegraph.Color(
		// 127.0/255.0, 138.0/255.0, 209.0/255.0 ).toString());
		authoringtoolConfig.setValue("backgroundColor", backgroundColor);
		if (authoringtoolConfig.getValue("recentWorlds.maxWorlds") == null) {
			authoringtoolConfig.setValue("recentWorlds.maxWorlds", Integer.toString(8));
		}
		if (authoringtoolConfig.getValueList("recentWorlds.worlds") == null) {
			authoringtoolConfig.setValueList("recentWorlds.worlds", new String[]{});
		}

		if (authoringtoolConfig.getValue("enableHighContrastMode") == null) {
			authoringtoolConfig.setValue("enableHighContrastMode", "false");
		}

		if (authoringtoolConfig.getValue("fontSize") == null) {
			authoringtoolConfig.setValue("fontSize", Integer.toString(12));
		}

		if (authoringtoolConfig.getValue("showObjectLoadFeedback") == null) {
			authoringtoolConfig.setValue("showObjectLoadFeedback", "true");
		}

		if (authoringtoolConfig.getValue("maximumWorldBackupCount") == null) {
			authoringtoolConfig.setValue("maximumWorldBackupCount", Integer.toString(5));
		}

		if (authoringtoolConfig.getValue("maxRecentlyUsedValues") == null) {
			authoringtoolConfig.setValue("maxRecentlyUsedValues", Integer.toString(5));
		}

		if (authoringtoolConfig.getValue("numberOfClipboards") == null) {
			authoringtoolConfig.setValue("numberOfClipboards", Integer.toString(1));
		}

		if (authoringtoolConfig.getValue("showWorldStats") == null) {
			authoringtoolConfig.setValue("showWorldStats", "false");
		}

		if (authoringtoolConfig.getValue("enableScripting") == null) {
			authoringtoolConfig.setValue("enableScripting", "false");
		}

		if (authoringtoolConfig.getValue("promptToSaveInterval") == null) {
			authoringtoolConfig.setValue("promptToSaveInterval", Integer.toString(15));
		}

		if (authoringtoolConfig.getValue("doNotShowUnhookedMethodWarning") == null) {
			authoringtoolConfig.setValue("doNotShowUnhookedMethodWarning", "false");
		}

		// if( authoringtoolConfig.getValue( "backgroundColor" ) == null ) {
		// authoringtoolConfig.setValue( "backgroundColor", new
		// edu.cmu.cs.stage3.alice.scenegraph.Color( 127.0/255.0, 138.0/255.0,
		// 209.0/255.0 ).toString() );
		// }

		if (authoringtoolConfig.getValue("clearStdOutOnRun") == null) {
			authoringtoolConfig.setValue("clearStdOutOnRun", "true");
		}

		if (authoringtoolConfig.getValue("resourceFile") == null) {
			authoringtoolConfig.setValue("resourceFile", "Alice Style.py");
		}

		if (authoringtoolConfig.getValue("watcherPanelEnabled") == null) {
			authoringtoolConfig.setValue("watcherPanelEnabled", "false");
		}

		if (authoringtoolConfig.getValue("showStartUpDialog") == null) {
			authoringtoolConfig.setValue("showStartUpDialog", "true");
		}

		if (authoringtoolConfig.getValue("showWebWarningDialog") == null) {
			authoringtoolConfig.setValue("showWebWarningDialog", "true");
		}

		if (authoringtoolConfig.getValue("showStartUpDialog_OpenTab") == null) {
			authoringtoolConfig.setValue("showStartUpDialog_OpenTab", Integer.toString(edu.cmu.cs.stage3.alice.authoringtool.dialog.StartUpContentPane.TUTORIAL_TAB_ID));
		}

		if (authoringtoolConfig.getValue("loadSavedTabs") == null) {
			authoringtoolConfig.setValue("loadSavedTabs", "false");
		}

		if (authoringtoolConfig.getValue("saveThumbnailWithWorld") == null) {
			authoringtoolConfig.setValue("saveThumbnailWithWorld", "true");
		}

		// if( authoringtoolConfig.getValue( "useJavaSyntax" ) == null ) {
		// authoringtoolConfig.setValue( "useJavaSyntax", "false" );
		// } else if( authoringtoolConfig.getValue( "useJavaSyntax"
		// ).equalsIgnoreCase( "true" ) ) {
		// AuthoringToolResources.setSyntaxMode( "java" );
		// }
		// edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.addConfigurationListener(
		// new
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationListener()
		// {
		// public void changing(
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev ) {
		// if( ev.getKeyName().equals(
		// "edu.cmu.cs.stage3.alice.authoringtool.useJavaSyntax" ) ) {
		// if( ev.getNewValue().equalsIgnoreCase( "true" ) ) {
		// AuthoringToolResources.setSyntaxMode( "java" );
		// } else {
		// AuthoringToolResources.setSyntaxMode( "default" );
		// }
		// }
		// }
		// public void changed(
		// edu.cmu.cs.stage3.alice.authoringtool.util.event.ConfigurationEvent
		// ev ) {}
		// }
		// );

		if (authoringtoolConfig.getValue("mainWindowBounds") == null) {
			int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int x = 0;
			int y = 0;
			int height = screenHeight - 30;
			authoringtoolConfig.setValue("mainWindowBounds", x + 80 + ", " + y + ", " + (screenWidth - 80) + ", " + height);
		}

		if (authoringtoolConfig.getValueList("rendering.orderedRendererList") == null) {
			Class[] rendererClasses = edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory.getPotentialRendererClasses();
			String[] list = new String[rendererClasses.length];
			for (int i = 0; i < rendererClasses.length; i++) {
				list[i] = rendererClasses[i].getName();
			}
			authoringtoolConfig.setValueList("rendering.orderedRendererList", list);
		}

		if (authoringtoolConfig.getValue("rendering.showFPS") == null) {
			authoringtoolConfig.setValue("rendering.showFPS", "false");
		}

		if (authoringtoolConfig.getValue("rendering.forceSoftwareRendering") == null) {
			authoringtoolConfig.setValue("rendering.forceSoftwareRendering", "false");
		}

		if (authoringtoolConfig.getValue("rendering.deleteFiles") == null) {
			authoringtoolConfig.setValue("rendering.deleteFiles", "true");
		}

		if (authoringtoolConfig.getValue("rendering.renderWindowMatchesSceneEditor") == null) {
			authoringtoolConfig.setValue("rendering.renderWindowMatchesSceneEditor", "true");
		}

		if (authoringtoolConfig.getValue("rendering.ensureRenderDialogIsOnScreen") == null) {
			authoringtoolConfig.setValue("rendering.ensureRenderDialogIsOnScreen", "true");
		}

		if (authoringtoolConfig.getValue("rendering.renderWindowBounds") == null) {
			int screenWidth = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int screenHeight = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			int width = 480; // (int)(screenWidth*.5);
			int height = 360; // (int)Math.round(
								// ((double)width)/(screenWidth/screenHeight) );
			int x = (screenWidth - width) / 2;
			int y = (screenHeight - height) / 2;

			authoringtoolConfig.setValue("rendering.renderWindowBounds", x + ", " + y + ", " + width + ", " + height);
		}

		if (authoringtoolConfig.getValue("rendering.runtimeScratchPadEnabled") == null) {
			authoringtoolConfig.setValue("rendering.runtimeScratchPadEnabled", "false");
		}

		if (authoringtoolConfig.getValue("rendering.runtimeScratchPadHeight") == null) {
			authoringtoolConfig.setValue("rendering.runtimeScratchPadHeight", "300");
		}

		if (authoringtoolConfig.getValue("rendering.useBorderlessWindow") == null) {
			authoringtoolConfig.setValue("rendering.useBorderlessWindow", "false");
		}

		if (authoringtoolConfig.getValue("rendering.constrainRenderDialogAspectRatio") == null) {
			authoringtoolConfig.setValue("rendering.constrainRenderDialogAspectRatio", "true");
		}

		// if( authoringtoolConfig.getValue( "printing.scaleFactor" ) == null )
		// {
		// authoringtoolConfig.setValue( "printing.scaleFactor", "1.0" );
		// }
		//
		// if( authoringtoolConfig.getValue( "printing.fillBackground" ) == null
		// ) {
		// authoringtoolConfig.setValue( "printing.fillBackground", "true" );
		// }

		if (authoringtoolConfig.getValue("gui.pickUpTiles") == null) {
			authoringtoolConfig.setValue("gui.pickUpTiles", "true");
		}

		if (authoringtoolConfig.getValue("gui.useAlphaTiles") == null) {
			authoringtoolConfig.setValue("gui.useAlphaTiles", "false");
		}

		if (authoringtoolConfig.getValue("useSingleFileLoadStore") == null) {
			authoringtoolConfig.setValue("useSingleFileLoadStore", "true");
		}

		if (authoringtoolConfig.getValue("directories.worldsDirectory") == null) {
			// TODO: be more cross-platform aware
			String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("directories.worldsDirectory", dir);
		}

		// if( authoringtoolConfig.getValue( "directories.galleryDirectory" ) ==
		// null ) {
		// if( new java.io.File( "gallery" ).getAbsoluteFile().exists() ) {
		// authoringtoolConfig.setValue( "directories.galleryDirectory",
		// "gallery" );
		// } else { // this is kind of silly
		// String dir = System.getProperty( "user.home" ) + System.getProperty(
		// "file.separator" ) + "Desktop";
		// authoringtoolConfig.setValue( "directories.galleryDirectory", dir );
		// }
		// }

		if (authoringtoolConfig.getValue("directories.importDirectory") == null) {
			// TODO: be more cross-platform aware
			String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("directories.importDirectory", dir);
		}

		if (authoringtoolConfig.getValue("directories.examplesDirectory") == null) {
			authoringtoolConfig.setValue("directories.examplesDirectory", "exampleWorlds");
		}

		if (authoringtoolConfig.getValue("directories.charactersDirectory") == null) {
			String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
			java.io.File captureDir = new java.io.File(dir);
			if (captureDir.canWrite()) {
				authoringtoolConfig.setValue("directories.charactersDirectory", dir);
			} else {
				authoringtoolConfig.setValue("directories.charactersDirectory", null);
			}
		}

		if (authoringtoolConfig.getValue("directories.templatesDirectory") == null) {
			authoringtoolConfig.setValue("directories.templatesDirectory", "templateWorlds");
		}

		if (authoringtoolConfig.getValue("directories.textbookExamplesDirectory") == null) {
			authoringtoolConfig.setValue("directories.textbookExamplesDirectory", "textbookExampleWorlds");
		}

		// if( authoringtoolConfig.getValue( "reloadWorldScriptOnRun" ) == null
		// ) {
		// authoringtoolConfig.setValue( "reloadWorldScriptOnRun", "false" );
		// }

		if (authoringtoolConfig.getValue("screenCapture.directory") == null) {
			String dir = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop";
			authoringtoolConfig.setValue("screenCapture.directory", dir);
		}
		if (authoringtoolConfig.getValue("screenCapture.baseName") == null) {
			authoringtoolConfig.setValue("screenCapture.baseName", "capture");
		}
		if (authoringtoolConfig.getValue("screenCapture.numDigits") == null) {
			authoringtoolConfig.setValue("screenCapture.numDigits", "2");
		}
		if (authoringtoolConfig.getValue("screenCapture.codec") == null) {
			authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
		}
		if (authoringtoolConfig.getValue("screenCapture.codec").equalsIgnoreCase("gif")) {
			authoringtoolConfig.setValue("screenCapture.codec", "jpeg");
		}
		if (authoringtoolConfig.getValue("screenCapture.informUser") == null) {
			authoringtoolConfig.setValue("screenCapture.informUser", "true");
		}

		if (authoringtoolConfig.getValue("saveInfiniteBackups") == null) {
			authoringtoolConfig.setValue("saveInfiniteBackups", "false");
		}

		if (authoringtoolConfig.getValue("doProfiling") == null) {
			authoringtoolConfig.setValue("doProfiling", "false");
		}
	}

	private static void parseCommandLineArgs(String[] args) {
		int c;
		// String arg;
		gnu.getopt.LongOpt[] options = {new gnu.getopt.LongOpt("stdOutToConsole", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'o'), new gnu.getopt.LongOpt("stdErrToConsole", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'e'), new gnu.getopt.LongOpt("defaultRenderer", gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 'r'),
				// new gnu.getopt.LongOpt("customStartupClass",
				// gnu.getopt.LongOpt.REQUIRED_ARGUMENT, null, 'c'),
		new gnu.getopt.LongOpt("help", gnu.getopt.LongOpt.NO_ARGUMENT, null, 'h'),};

		String helpMessage = "" + "\nUsage: JAlice <options> <world>\n" + "\n" + "options:\n" + "    --stdOutToConsole|-o:\n" + "        directs System.stdOut to the console instead of the output text area.\n" + "    --stdErrToConsole|-e:\n" + "        directs System.stdOut to the console instead of the output text area.\n" + "    --defaultRenderer|-r <classname>:\n" + "        the Renderer specified by <classname> will be used as the default Renderer\n" +
		// "    --customStartupClass|-c <classname>:\n" +
		// "        calls <classname>.customSetup( String [] args, <JAlice instance>,\n"
		// +
		// "                  <world instance> )\n" +
		// "        during system initialization\n" +
		"    --help|-h:\n" + "        prints this help message\n" + "\n" + "world:\n" + "    a pathname to a world on disk to be loaded at startup.\n";

		// for the options string:
		// --a lone character has no options
		// --a character preceded by a colon has a required argument
		// --a character preceded by two colons has a non-required argument
		// --if the whole string starts with a colon, then ':' is returned for
		// valid options that do not have their required argument
		gnu.getopt.Getopt g = new gnu.getopt.Getopt("JAlice", args, ":oeh", options);
		while ((c = g.getopt()) != -1) {
			switch (c) {
				case 'o' : // stdOut to console...
					stdOutToConsole = true;
					break;
				case 'e' : // stdErr to console...
					stdErrToConsole = true;
					break;
				case 'r' : // default Renderer Class...
					defaultRendererClassname = g.getOptarg();
					break;
				/*
				 * case 'c': //custom Startup class arg = g.getOptarg(); try {
				 * Class cls = Class.forName( arg ); Object [] argValues = {
				 * args, f, f.world }; Class [] argClasses = new
				 * Class[argValues.length]; for( int i=0; i<argClasses.length;
				 * i++ ) { argClasses[i] = argValues[i].getClass(); }
				 * java.lang.reflect.Method method = cls.getMethod(
				 * "customSetup", argClasses ); method.invoke( null, argValues
				 * ); } catch( Exception e ) { e.printStackTrace(); } break;
				 */
				case 'h' : // help
				case '?' :
					System.err.println(helpMessage);
					System.exit(0);
					break;
				default :
					System.err.println("ignoring " + c + " on the command line.");
					break;
			}
		}

		int i = g.getOptind();
		if (i >= 0 && i < args.length) {
			if (System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows")) {
				char ch = ':';
				String file = args[i].toString();
				file = file.substring(file.lastIndexOf(ch) - 1, file.length() - 1);
				worldToLoad = new java.io.File(file).getAbsoluteFile();
			} else {
				worldToLoad = new java.io.File(args[i]).getAbsoluteFile();
			}
		}
	}

	public static boolean isMainFinished() {
		return mainHasFinished;
	}

	public static void setAliceHomeDirectory(java.io.File file) {
		aliceHomeDirectory = file;
	}

	public static java.io.File getAliceHomeDirectory() {
		if (aliceHomeDirectory == null) {
			if (System.getProperty("alice.home") != null) {
				setAliceHomeDirectory(new java.io.File(System.getProperty("alice.home")).getAbsoluteFile());
			} else {
				setAliceHomeDirectory(new java.io.File(System.getProperty("user.dir")).getAbsoluteFile());
			}
		}

		return aliceHomeDirectory;
	}

	public static void setAliceUserDirectory(java.io.File file) {
		aliceUserDirectory = file;
	}

	public static java.io.File getAliceUserDirectory() {
		if (directory != null) {
			aliceUserDirectory = new java.io.File(directory);
		} else if (aliceUserDirectory == null) {
			java.io.File dirFromProperties = null;
			if (System.getProperty("alice.userDir") != null) {
				dirFromProperties = new java.io.File(System.getProperty("alice.userDir")).getAbsoluteFile();
			}
			java.io.File userHome = new java.io.File(System.getProperty("user.home")).getAbsoluteFile();
			java.io.File aliceHome = getAliceHomeDirectory();
			java.io.File aliceUser = null;
			if (dirFromProperties != null) {
				aliceUser = dirFromProperties;
			} else if (userHome.exists() && userHome.canRead() && userHome.canWrite()) {
				aliceUser = new java.io.File(userHome, ".alice2");
			} else if (aliceHome != null && aliceHome.exists() && aliceHome.canRead() && aliceHome.canWrite()) {
				aliceUser = new java.io.File(aliceHome, ".alice2");
			}

			if (aliceUser != null) {
				if (aliceUser.exists()) {
					setAliceUserDirectory(aliceUser);
				} else if (aliceUser.mkdir()) {
					setAliceUserDirectory(aliceUser);
				}
			}
		}
		return aliceUserDirectory;
	}
}
