import java
import edu
import org

from random import random

from constants import *
from linearalgebra import *
from animations import *

#need to use java.lang.Class.forName since "edu.cmu.cs.stage3.alice.core.media" is a package name
def copy( src, name=None, classesToShare=(edu.cmu.cs.stage3.alice.core.TextureMap,java.lang.Class.forName("edu.cmu.cs.stage3.alice.core.Media")) ):
	return src.createCopyNamed( name, classesToShare, None )

class _RenderTargetListener( edu.cmu.cs.stage3.alice.scenegraph.renderer.event.RenderTargetListener ):
	def __init__( self, callback ):
		self._callback = callback
	def cleared( self, renderEvent ):
		pass
	def rendered( self, renderEvent ):
		try:
			g = renderEvent.getSource().getOffscreenGraphics()
			try:
				self._callback( g )
			except:
				traceback.print_exc()
			g.dispose()
		except:
			traceback.print_exc()
	def swapped( self, renderEvent ):
		pass

def addOverlayCallback( renderTarget, callback ):
	rtl = _RenderTargetListener( callback )
	renderTarget.addRenderTargetListener( rtl )
	return rtl

def removeAllOverlayCallbacks( renderTarget ):
	rtls = renderTarget.getRenderTargetListeners()
	for rtl in rtls:
		if isinstance( rtl, _RenderTargetListener ):
			renderTarget.removeRenderTargetListener( rtl )

class Keyboard:
	def __init__( self ):
		self._map = {}
		for field in java.awt.event.KeyEvent.getDeclaredFields():
			name = field.getName()
			if name[:3]=="VK_":
				code = field.get( None )
				self.__dict__[name] = code
				self._map[java.awt.event.KeyEvent.getKeyText( code )] = code
	def getKeyNames( self ):
		names = keyboard._map.keys()
		names.sort()
		return names
	def isKeyPressed( self, keyCode ):
		if type( keyCode ) == types.IntType:
			pass
		else:
			try:
				keyCode = self._map[keyCode]
			except:
				pass
		return edu.cmu.cs.stage3.io.toolkit.Toolkit.isKeyPressed( keyCode )
keyboard = Keyboard()

class Mouse:
	def getLocation( self ):
		return edu.cmu.cs.stage3.io.toolkit.Toolkit.getCursorLocation()
mouse = Mouse()

class Clock:
	def getTime( self ):
		return java.lang.System.currentTimeMillis()*0.001
clock = Clock()


#todo
#import __builtin__
#import imp
#_save_import_module = __builtin__.__import__
#def __import__( name, globals={}, locals={}, fromlist=[] ):
#	try:
#		return _save_import_module( name, globals, locals, fromlist )
#	except:
#		print name, globals, locals, fromlist


def exportToObj( model, filename ):
	fos = java.io.FileOutputStream( filename )
	edu.cmu.cs.stage3.alice.scenegraph.io.OBJ.store( fos, model.getSceneGraphTransformable() )
	fos.close()