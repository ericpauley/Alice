####################################################
# pinchgloves.py
# bstearns, 10/23
#
# Yeah, I know this should be called "touchgloves.py".
# It's George and Dennis' fault.
#
# Usage:
#
# Here's what your script should look like:
"""

import pinchgloves

def ack(time):
	print "ack:",time

def thppt():
	print "thppt"
	
def broken():
	print "broken"

pinchgloves.AddPinchResponse((pinchgloves.LEFT_THUMB,pinchgloves.LEFT_INDEX),ack,broken)
pinchgloves.AddPinchResponse((pinchgloves.LEFT_THUMB,pinchgloves.LEFT_MIDDLE),thppt)

#begin   ScriptAction( pinchgloves.Start )
#during  ScriptAction( pinchgloves._PinchHandler )
#end     ScriptAction( pinchgloves.Stop )


"""
# Note that the ack function accepts an optional parameter "time"
# which is the time in seconds since the last pinchgloves event.
# The pinch event handler trys to hand every registered function
# the time parameter; if the call fails, the handler will call
# the function without the time parameter.  So feel free to write
# your functions either way, accepting the time parameter or not.
#
# An event is any change in the state of the gloves, _including_
# the breaking of any combination of fingers.
#
# This module allows you to register functions to be
# called upon the connection and disconnection of any combination of
# fingers.  Currently this API only supports single
# combinations ie. you can't register a function to
# be triggered when both thumbs are touching _and_ both
# index fingers are touching (those are two separate
# finger combinations).
#
####################################################


THUMB  = 16
INDEX  = 8
MIDDLE = 4
RING   = 2
PINKIE = 1

LEFT   = "LEFT"
RIGHT  = "RIGHT"

####################################################
# These are valid fingers to use when specifying combinations:
LEFT_THUMB	= (LEFT,THUMB)
LEFT_INDEX	= (LEFT,INDEX)
LEFT_MIDDLE	= (LEFT,MIDDLE)
LEFT_RING	= (LEFT,RING)
LEFT_PINKIE	= (LEFT,PINKIE)
RIGHT_THUMB	= (RIGHT,THUMB)
RIGHT_INDEX	= (RIGHT,INDEX)
RIGHT_MIDDLE= (RIGHT,MIDDLE)
RIGHT_RING	= (RIGHT,RING)
RIGHT_PINKIE= (RIGHT,PINKIE)
####################################################

####################################################
# Don't touch these variables
_TICK_TIME	= 1.0/1200.0	# This constant should technically be read from the glove box
					# itself; must modify the .pyd to make this possible

_masterFingerList = [LEFT_THUMB, LEFT_INDEX, LEFT_MIDDLE, LEFT_RING, LEFT_PINKIE, 
			  RIGHT_THUMB, RIGHT_INDEX, RIGHT_MIDDLE, RIGHT_RING, RIGHT_PINKIE]

_pinchResponseDict = {}

_connectList = []

_pinchHandle = None

_port = "COM1"
####################################################

####################################################
# Functions available for the user
def SetDefaultPort(comPort):
	global _port
	if comPort not in ["COM1","COM2","COM3","COM4"]:
		raise RuntimeError,'Valid COM port values are: ["COM1","COM2","COM3","COM4"]'
	_port = comPort

def Start(comPort=None):
	global _pinchHandle
	if _pinchHandle:
		print "Pinchgloves already running."
		return
	if comPort:
		if comPort not in ["COM1","COM2","COM3","COM4"]:
			raise RuntimeError,'Valid COM port values are: ["COM1","COM2","COM3","COM4"]'
		_pinchHandle=edu.cmu.cs.stage3.io.glove.pinch.Pinch.open(comPort, 1)
		SetDefaultPort(comPort)
	else:
		_pinchHandle=edu.cmu.cs.stage3.io.glove.pinch.Pinch.open(_port, 1)
	if _pinchHandle == 0:
		raise RuntimeError,"Could not open pinch gloves!"
	edu.cmu.cs.stage3.io.glove.pinch.Pinch.enableTimeStamps(_pinchHandle)	# just making sure...
	
def Stop():
	global _pinchHandle
	edu.cmu.cs.stage3.io.glove.pinch.Pinch.close(_pinchHandle)
	_pinchHandle = None
	
def AddPinchResponse(pinchCombo,connectFunc,disconnectFunc=None):
	l = tuple(_SortFingerList(pinchCombo))
	if l in _pinchResponseDict.keys():
		_pinchResponseDict[l].append((connectFunc,disconnectFunc))
	else:
		_pinchResponseDict[l] = [(connectFunc,disconnectFunc)]
	
def RemovePinchResponse(pinchCombo,connectFunc,disconnectFunc=None):
	l = tuple(_SortFingerList(pinchCombo))
	if l in _pinchResponseDict.keys():
		if (connectFunc,disconnectFunc) in _pinchResponseDict[l]:
			if len(_pinchResponseDict[l]) <= 1:
				del(_pinchResponseDict[l])
			else:
				_pinchResponseDict[l].remove((connectFunc,disconnectFunc))
		else:
			raise RuntimeError,"Can't find this response for this finger combination."
	else:
		raise RuntimeError,"Can't find this finger combination."
	
def GetPinchResponses(pinchCombo=None):
	return _pinchResponseDict

def RemoveAllResponses():
	_pinchResponseDict = {}
####################################################

####################################################
# These functions and variables shouldn't ever be
# called externally.
def _SortFingerList(fingerList):
	l = []
	for finger in _masterFingerList:
		if finger in fingerList:
			l.append(finger)
	return l

def _InterpretTouches(touchData):
	# assumes that time stamps are on
	time = ((touchData[-2] << 7) + touchData[-3]) * _TICK_TIME
	if len(touchData) == 4:
		return ((),),time
	combos = []
	for i in range(int((len(touchData)-4)/2.0)):
		combo = []
		lh = touchData[i*2+1]
		rh = touchData[i*2+2]
		if (lh & THUMB) > 0:
			combo.append(LEFT_THUMB)
		if (lh & INDEX) > 0:
			combo.append(LEFT_INDEX)
		if (lh & MIDDLE) > 0:
			combo.append(LEFT_MIDDLE)
		if (lh & RING) > 0:
			combo.append(LEFT_RING)
		if (lh & PINKIE) > 0:
			combo.append(LEFT_PINKIE)
		if (rh & THUMB) > 0:
			combo.append(RIGHT_THUMB)
		if (rh & INDEX) > 0:
			combo.append(RIGHT_INDEX)
		if (rh & MIDDLE) > 0:
			combo.append(RIGHT_MIDDLE)
		if (rh & RING) > 0:
			combo.append(RIGHT_RING)
		if (rh & PINKIE) > 0:
			combo.append(RIGHT_PINKIE)
		combos.append(tuple(combo))
	return tuple(combos),time # should be sorted appropriately already



def _PinchHandler():
	touches = edu.cmu.cs.stage3.io.glove.pinch.Pinch.getRecord(_pinchHandle)
	if touches:
		combos,time = _InterpretTouches(touches)
		# assumes each combo is already sorted
		for combo in combos:
			if combo not in _connectList:
				_connectList.append(combo)
				if combo in _pinchResponseDict.keys():
					for pair in _pinchResponseDict[combo]:
						try:
							pair[0](time)
						except TypeError:
							pair[0]()
						except:
							print "Pinch response failed!"
							raise sys.exc_type,sys.exc_value
		for combo in _connectList:
			if combo not in combos:
				if combo in _pinchResponseDict.keys():
					for pair in _pinchResponseDict[combo]:
						if pair[1]:
							try:
								pair[1](time)
							except TypeError:
								pair[1]()				
							except:
								print "Pinch response failed!"
								raise sys.exc_type,sys.exc_value
				_connectList.remove(combo)


####################################################
