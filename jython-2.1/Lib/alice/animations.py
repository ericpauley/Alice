import java
import edu
import org
import types
import traceback

from constants import *

class ApplyResponse( edu.cmu.cs.stage3.alice.core.Response ):
	class ApplyRuntimeResponse( edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse ):
		def __init__( self, response ):
			edu.cmu.cs.stage3.alice.core.Response.RuntimeResponse.__init__( self, response )
			self._response = response
		def update( self, t ):
			apply( self._response._fcn, self._response._args, self._response._kws )
	def __init__( self, fcn, args, kws ):
		edu.cmu.cs.stage3.alice.core.Response.__init__( self )
		self._fcn = fcn
		self._args = args
		self._kws = kws
		self.duration.set( 0 )

	def manufactureRuntimeResponse( self ):
		return self.ApplyRuntimeResponse( self )

class ApplyQuestion( edu.cmu.cs.stage3.alice.core.Question, edu.cmu.cs.stage3.alice.core.event.ScheduleListener ):
	def __init__( self, fcn, args=(), kws={}, cls=java.lang.Object ):
		edu.cmu.cs.stage3.alice.core.Question.__init__( self )
		self._fcn = fcn
		self._args = args
		self._kws = kws
		self._cls = cls
	def getValue( self ):
		return apply( self._fcn, self._args, self._kws )
	def getValueClass( self ):
		return self._cls
	#def scheduled( self, e ):
	#	self.onExpressionChange()
	#def started( self, world ):
	#	edu.cmu.cs.stage3.alice.core.Question.started( self, world )
	#	self._scheduler = world.getScheduler()
	#	if self._scheduler:
	#		self._scheduler.addScheduleListener( self )
	#def stopped( self, world ):
	#	edu.cmu.cs.stage3.alice.core.Question.stopped( self, world )
	#	if self._scheduler:
	#		self._scheduler.removeScheduleListener( self )

def _WaitIfNecessary( a, waitDuration ):
	if waitDuration:
		return SequentialAnimation( WaitAction( waitDuration ), a )
	else:
		return a

def ScriptAction( fcn, args=(), kws={} ):
	if type(args) != types.TupleType:
		args = (args,)
	if type( fcn )==types.StringType:
		if args:
			print "warning: unexpected arguments ", args
		if kws:
			print "warning: unexpected keywords ", kws
		scriptResponse =  edu.cmu.cs.stage3.alice.core.response.ScriptResponse()
		scriptResponse.script.set( fcn )
		scriptResponse.setWorld( __world__ )
		return scriptResponse
	else:
		return ApplyResponse( fcn, args, kws )

def WaitAction( duration ):
	a = edu.cmu.cs.stage3.alice.core.response.Wait()
	a.duration.set( duration )
	return a

def _AddComponentResponses( a, args ):
	for child in args:
		if child is None:
			print "WARNING: SequentialAnimation passed None as a child "
			for arg in args:
				print '\t'+`arg`
			child = ScriptAction( "print 'animation is None'" )
		a.componentResponses.add( child )

def DoInOrder( *args, **kws ):
	a = edu.cmu.cs.stage3.alice.core.response.DoInOrder()
	_AddComponentResponses( a, args )
	try:
		wait = kws["wait"]
	except:
		wait = 0
	return _WaitIfNecessary( a, wait )

def DoTogether( *args, **kws ):
	a = edu.cmu.cs.stage3.alice.core.response.DoTogether()
	_AddComponentResponses( a, args )
	try:
		wait = kws["wait"]
	except:
		wait = 0
	return _WaitIfNecessary( a, wait )

def _GetClass( list ):
	item0 = list[0]
	type0 = type( item0 )
	if type0 in [ types.IntType, types.FloatType, types.LongType ]:
		return java.lang.Number
	else:
		cls = list[0].getClass()
		while true:
			found = true
			for item in list:
				if cls.isAssignableFrom( item.getClass() ):
					pass
				else:
					found = false
					break 
			if found:
				return cls
			else:
				try:
					cls = cls.getSuperclass()
				except:
					return None

def ManufactureEach( list ):
	each = edu.cmu.cs.stage3.alice.core.Variable()
	each.valueClass.set( _GetClass( list ) )
	return each

def ForEachInOrder( each, list, *args, **kws ):
	a = edu.cmu.cs.stage3.alice.core.response.ForEachInOrder()
	_AddComponentResponses( a, args )
	
	a.each.set( each )

	if type( list ) == types.ListType:
		l = edu.cmu.cs.stage3.alice.core.List()
		for item in list:
			l.append( item )
		l.valueClass.set( _GetClass( list ) )
		list = l
	a.list.set( list )

	try:
		wait = kws["wait"]
	except:
		wait = 0
	return _WaitIfNecessary( a, wait )
	
def ForEachTogether( each, list, *args, **kws ):
	a = edu.cmu.cs.stage3.alice.core.response.ForEachTogether()
	_AddComponentResponses( a, args )
	a.each.set( each )
	a.list.set( list )
	try:
		wait = kws["wait"]
	except:
		wait = 0
	return _WaitIfNecessary( a, wait )

def IfElseInOrder( condition, ifAnimation, elseAnimation=None ):
	if callable( condition ):
		condition = ApplyQuestion( condition, cls=java.lang.Boolean )

	a = edu.cmu.cs.stage3.alice.core.response.IfElseInOrder()
	a.addComponentResponse( ifAnimation )
	#todo
	try:
		wait = kws["wait"]
	except:
		wait = 0
	return _WaitIfNecessary( a, wait )

def WhileLoopInOrder( condition, *args ):
	anim = edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder()
	_AddComponentResponses( anim, args )

	if callable( condition ):
		condition = ApplyQuestion( condition, cls=java.lang.Boolean )
	anim.condition.set( condition )
	return anim

def LoopNInOrder( *args ):
	return None

def MoveAnimation( subject, direction=forward, amount=1, duration=1, asSeenBy=None, style=gently, isScaledBySize=false, wait=0 ):
	anim = edu.cmu.cs.stage3.alice.core.response.MoveAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( direction ):
		direction = ApplyQuestion( direction, cls=anim.direction.getValueClass() )
	anim.direction.set( direction )

	if callable( amount ):
		amount = ApplyQuestion( amount, cls=anim.amount.getValueClass() )
	anim.amount.set( amount )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	if callable( isScaledBySize ):
		isScaledBySize = ApplyQuestion( isScaledBySize, cls=anim.isScaledBySize.getValueClass() )
	anim.isScaledBySize.set( isScaledBySize )

	return _WaitIfNecessary( anim, wait )

def TurnAnimation( subject, direction=forward, amount=0.25, duration=1, asSeenBy=None, style=gently, wait=0 ):
	if ( direction is up ) or ( direction is down ):
		print 
		print 'WARNING: one cannot turn up or down (is ambiguous depending on orientation).  please use backward or forward instead.'
		print 'feedback is welcome on this and other issues: bvw-help@cs.cmu.edu'
		print 
		return 

	anim = edu.cmu.cs.stage3.alice.core.response.TurnAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( direction ):
		direction = ApplyQuestion( direction, cls=anim.direction.getValueClass() )
	anim.direction.set( direction )

	if callable( amount ):
		amount = ApplyQuestion( amount, cls=anim.amount.getValueClass() )
	anim.amount.set( amount )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def RollAnimation( subject, direction=forward, amount=0.25, duration=1, asSeenBy=None, style=gently, wait=0 ):
	if ( direction is up ) or ( direction is down ) or ( direction is forward ) or ( direction is backward ):
		print 
		print 'WARNING: one cannot roll up or down or forward or backward.  Please use turn instead.'
		print 
		return 
	anim = edu.cmu.cs.stage3.alice.core.response.RollAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( direction ):
		direction = ApplyQuestion( direction, cls=anim.direction.getValueClass() )
	anim.direction.set( direction )

	if callable( amount ):
		amount = ApplyQuestion( amount, cls=anim.amount.getValueClass() )
	anim.amount.set( amount )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def ResizeAnimation( subject, dimension=all, amount=2, likeRubber=false, duration=1, asSeenBy=None, style=gently, wait=0 ):
	anim = edu.cmu.cs.stage3.alice.core.response.ResizeAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( dimension ):
		dimension = ApplyQuestion( dimension, cls=anim.dimension.getValueClass() )
	anim.dimension.set( dimension )

	if callable( amount ):
		amount = ApplyQuestion( amount, cls=anim.amount.getValueClass() )
	anim.amount.set( amount )

	if callable( likeRubber ):
		likeRubber = ApplyQuestion( likeRubber, cls=anim.likeRubber.getValueClass() )
	anim.likeRubber.set( likeRubber )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def PositionAnimation( subject, position=(0,0,0), duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( position ) == types.TupleType:
		x, y, z = position
		position = edu.cmu.cs.stage3.math.Vector3( x, y, z )

	anim = edu.cmu.cs.stage3.alice.core.response.PositionAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( position ):
		position = ApplyQuestion( position, cls=anim.position.getValueClass() )
	anim.position.set( position )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def ForwardVectorAnimation( subject, forward=(1,0,0), upGuide=None, duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( forward ) == types.TupleType:
		x, y, z = forward
		forward = Vector3( x, y, z )
	if type( upGuide ) == types.TupleType:
		x, y, z = upGuide
		upGuide = Vector3( x, y, z )
	
	anim = edu.cmu.cs.stage3.alice.core.response.ForwardVectorAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( forward ):
		forward = ApplyQuestion( forward, cls=anim.forward.getValueClass() )
	anim.forward.set( forward )

	if callable( upGuide ):
		upGuide = ApplyQuestion( upGuide, cls=anim.upGuide.getValueClass() )
	anim.upGuide.set( upGuide )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def QuaternionAnimation( subject, quaternion=(0,0,0,1), duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( quaternion ) == types.TupleType:
		x,y,z,w = quaternion
		quaternion = edu.cmu.cs.stage3.math.Quaternion( x,y,z,w )

	anim = edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( quaternion ):
		quaternion = ApplyQuestion( quaternion, cls=anim.quaternion.getValueClass() )
	anim.quaternion.set( quaternion )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )


def EulerAnglesAnimation( subject, pitchYawRoll=(0,0,0), duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( pitchYawRoll ) == types.TupleType:
		pitch, yaw, roll = pitchYawRoll
		pitchYawRoll = edu.cmu.cs.stage3.math.EulerAngles( pitch, yaw, roll )

	anim = edu.cmu.cs.stage3.alice.core.response.EulerAnglesAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( pitchYawRoll ):
		pitchYawRoll = ApplyQuestion( pitchYawRoll, cls=anim.eulerAngles.getValueClass() )
	anim.eulerAngles.set( pitchYawRoll )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )


def SizeAnimation( subject, size=Vector3(0,0,0), duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( size ) == types.TupleType:
		x, y, z = size
		size = edu.cmu.cs.stage3.math.Vector3( x, y, z )

	anim = edu.cmu.cs.stage3.alice.core.response.SizeAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( size ):
		size = ApplyQuestion( size, cls=anim.size.getValueClass() )
	anim.size.set( size )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def PointAtAnimation( subject, target, offset=None, upGuide=None, onlyAffectYaw=false, duration=1, asSeenBy=None, style=gently, wait=0 ):
	if type( offset ) == types.TupleType:
		x, y, z = offset
		offset = edu.cmu.cs.stage3.math.Vector3( x, y, z )
	if type( upGuide ) == types.TupleType:
		x, y, z = upGuide
		upGuide = edu.cmu.cs.stage3.math.Vector3( x, y, z )

	anim = edu.cmu.cs.stage3.alice.core.response.PointAtAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( target ):
		target = ApplyQuestion( target, cls=anim.target.getValueClass() )
	anim.target.set( target )

	if callable( offset ):
		offset = ApplyQuestion( offset, cls=anim.offset.getValueClass() )
	anim.offset.set( offset )

	if callable( upGuide ):
		upGuide = ApplyQuestion( upGuide, cls=anim.upGuide.getValueClass() )
	anim.upGuide.set( upGuide )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( onlyAffectYaw ):
		onlyAffectYaw = ApplyQuestion( onlyAffectYaw, cls=anim.onlyAffectYaw.getValueClass() )
	anim.onlyAffectYaw.set( onlyAffectYaw )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def PointOfViewAnimation( subject, pointOfView, affectPosition=true, affectQuaternion=true, followHermiteCubic=false, duration=1, asSeenBy=None, style=gently, wait=0 ):
	anim = edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( pointOfView ):
		pointOfView = ApplyQuestion( pointOfView, cls=anim.pointOfView.getValueClass() )
	anim.pointOfView.set( pointOfView )

	if callable( affectPosition ):
		affectPosition = ApplyQuestion( affectPosition, cls=anim.affectPosition.getValueClass() )
	anim.affectPosition.set( affectPosition )

	if callable( affectQuaternion ):
		affectQuaternion = ApplyQuestion( affectQuaternion, cls=anim.affectQuaternion.getValueClass() )
	anim.affectQuaternion.set( affectQuaternion )

	if callable( followHermiteCubic ):
		followHermiteCubic = ApplyQuestion( followHermiteCubic, cls=anim.followHermiteCubic.getValueClass() )
	anim.followHermiteCubic.set( followHermiteCubic )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def StandUpAnimation( subject, duration=1, asSeenBy=None, style=gently, wait=0 ):
	anim = edu.cmu.cs.stage3.alice.core.response.StandUpAnimation()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def PathAnimation( subject, pointOfViews, times=None, duration=None, asSeenBy=None, style=gently, wait=0 ):
	if isinstance( pointOfViews, edu.cmu.cs.stage3.alice.core.group.VariableGroup ):
		enum = pointOfViews.getChildren()
		pointOfViews = []
		while enum.hasMoreElements():
			pointOfViews.append( enum.nextElement() )

	anim = edu.cmu.cs.stage3.alice.core.response.PathAnimation()
	
	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( pointOfViews ):
		pointOfViews = ApplyQuestion( pointOfViews, cls=anim.pointOfViews.getValueClass() )
	anim.pointOfViews.set( pointOfViews )

	if callable( times ):
		times = ApplyQuestion( times, cls=anim.times.getValueClass() )
	anim.times.set( times )

	if callable( asSeenBy ):
		asSeenBy = ApplyQuestion( asSeenBy, cls=anim.asSeenBy.getValueClass() )
	anim.asSeenBy.set( asSeenBy )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	return _WaitIfNecessary( anim, wait )

def TextureMapAnimation( subject, textureMaps, framesPerSecond=24, setDiffuseColorMap=true, setOpacityMap=false, howMuch=instanceAndParts, wait=0 ):
	if isinstance( textureMaps, edu.cmu.cs.stage3.alice.core.group.TextureMapGroup ):
		enum = textureMaps.getChildren()
		textureMaps = []
		while enum.hasMoreElements():
			textureMaps.append( enum.nextElement() )

	anim = edu.cmu.cs.stage3.alice.core.response.TextureMapMovie()

	if callable( subject ):
		subject = ApplyQuestion( subject, cls=anim.subject.getValueClass() )
	anim.subject.set( subject )

	if callable( textureMaps ):
		textureMaps = ApplyQuestion( textureMaps, cls=anim.textureMaps.getValueClass() )
	anim.textureMaps.set( textureMaps )

	if callable( framesPerSecond ):
		framesPerSecond = ApplyQuestion( framesPerSecond, cls=anim.framesPerSecond.getValueClass() )
	anim.framesPerSecond.set( framesPerSecond )

	if callable( setDiffuseColorMap ):
		setDiffuseColorMap = ApplyQuestion( setDiffuseColorMap, cls=anim.setDiffuseColorMap.getValueClass() )
	anim.setDiffuseColorMap.set( setDiffuseColorMap )

	if callable( setOpacityMap ):
		setOpacityMap = ApplyQuestion( setOpacityMap, cls=anim.setOpacityMap.getValueClass() )
	anim.setOpacityMap.set( setOpacityMap )

	if callable( howMuch ):
		howMuch = ApplyQuestion( howMuch, cls=anim.howMuch.getValueClass() )
	anim.howMuch.set( howMuch )

	return _WaitIfNecessary( anim, wait )

def PropertyAnimation( element, propertyName, value, duration=1, style=gently, howMuch=None, wait=0 ):
	property = element.getPropertyNamed( propertyName )
	if property.acceptsHowMuch():
		if howMuch is None:
			howMuch = instanceAndParts

	anim = edu.cmu.cs.stage3.alice.core.response.PropertyAnimation()

	anim.element.set( element )
	anim.propertyName.set( propertyName )


	if callable( value ):
		value = ApplyQuestion( value, cls=anim.value.getValueClass() )
	anim.value.set( value )

	if callable( style ):
		style = ApplyQuestion( style, cls=anim.style.getValueClass() )
	anim.style.set( style )

	if callable( duration ):
		duration = ApplyQuestion( duration, cls=anim.duration.getValueClass() )
	anim.duration.set( duration )

	if callable( howMuch ):
		howMuch = ApplyQuestion( howMuch, cls=anim.howMuch.getValueClass() )
	anim.howMuch.set( howMuch )

	return _WaitIfNecessary( anim, wait )

def VehiclePropertyAnimation( element, vehicle ):
	anim = edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation()
	anim.element.set( element )
	anim.vehicle.set( vehicle )
	anim.duration = 0
	return anim

def SoundAction( sound, volumeLevel=1, wait=0 ):
	anim = edu.cmu.cs.stage3.alice.core.response.SoundResponse()

	if callable( sound ):
		sound = ApplyQuestion( sound, cls=anim.sound.getValueClass() )
	anim.sound.set( sound )

	if callable( volumeLevel ):
		volumeLevel = ApplyQuestion( volumeLevel, cls=anim.volumeLevel.getValueClass() )
	anim.volumeLevel.set( volumeLevel )

	return _WaitIfNecessary( anim, wait )

def ShowAction( element, howMuch=instanceAndParts, wait=0 ):
	return PropertyAnimation( element, "isShowing", true, duration=0, howMuch=howMuch, wait=wait )

def HideAction( element, howMuch=instanceAndParts, wait=0 ):
	return PropertyAnimation( element, "isShowing", true, duration=0, howMuch=howMuch, wait=wait )

