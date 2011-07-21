package edu.cmu.cs.stage3.math;

public class GoldenRatio {
//	private static int s_windowBorderWidth;
//	private static int s_windowBorderHeight;
//	static {
//		try {
//			java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
//			String propNames[] = (String[])toolkit.getDesktopProperty( "win.propNames" );
//			if( propNames.length > 0 ) {
//				Integer dragWidth = (Integer)toolkit.getDesktopProperty( "win.drag.width" );
//				Integer dragHeight = (Integer)toolkit.getDesktopProperty( "win.drag.height" );
//				Integer captionHeight = (Integer)toolkit.getDesktopProperty( "win.frame.captionHeight" );
//
//				s_windowBorderWidth = dragWidth.intValue();
//				s_windowBorderHeight = captionHeight.intValue() + dragHeight.intValue();
//			}
//		} catch( Throwable t ) {
//			t.printStackTrace();
//			s_windowBorderWidth = 0;
//			s_windowBorderHeight = 0;
//		}
//	}
	public static double PHI = 1.6180339887;
	public static int getShorterSideLength( int longerSideLength ) {
		return (int)(longerSideLength/PHI);
	}
	public static int getLongerSideLength( int shorterSideLength ) {
		return (int)(shorterSideLength*PHI);
	}
	
//	public static int getLongerWidthAccountingForWindowBorder( int shorterHeight ) {
//		return getLongerSideLength( shorterHeight + s_windowBorderHeight ) - s_windowBorderWidth;
//	}
//	public static int getShorterWidthAccountingForWindowBorder( int longerHeight ) {
//		return getShorterSideLength( longerHeight + s_windowBorderHeight ) - s_windowBorderWidth;
//	}
//
//	public static int getLongerHeightAccountingForWindowBorder( int shorterWidth ) {
//		return getLongerSideLength( shorterWidth + s_windowBorderWidth ) - s_windowBorderHeight;
//	}
//	public static int getShorterHeightAccountingForWindowBorder( int longerWidth ) {
//		return getShorterSideLength( longerWidth + s_windowBorderWidth ) - s_windowBorderHeight;
//	}
}
