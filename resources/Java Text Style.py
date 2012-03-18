
from edu.cmu.cs.stage3.alice.authoringtool import JAlice
from edu.cmu.cs.stage3.util import StringTypePair
from java.lang import Boolean
from java.lang import Double
from java.lang import Integer
from java.lang import String
from edu.cmu.cs.stage3.math import Vector3
from edu.cmu.cs.stage3.math import Matrix44
import edu
import java
import javax
import string

# HACK: until os.path works
def os_path_join( *args ):
	return string.join( args, java.io.File.separator )

####################################
# load common resource data
####################################

standardResourcesFile = java.io.File( JAlice.getAliceHomeDirectory(), "resources/common/StandardResources.py" )
execfile( standardResourcesFile.getAbsolutePath() )


##################
# Format Config
##################

formatMap = {
	edu.cmu.cs.stage3.alice.core.response.MoveAnimation : "<<<subject>>>.move( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveTowardAnimation : "<<<subject>>>.moveToward( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveAwayFromAnimation : "<<<subject>>>.moveAwayFrom( <target>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAnimation : "<<<subject>>>.turn( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.RollAnimation : "<<<subject>>>.roll( <direction>, <amount> );",
	edu.cmu.cs.stage3.alice.core.response.MoveAtSpeed : "<<<subject>>>.moveAtSpeed( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAtSpeed : "<<<subject>>>.turnAtSpeed( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.RollAtSpeed : "<<<subject>>>.rollAtSpeed( <direction>, <speed> );",
	edu.cmu.cs.stage3.alice.core.response.ResizeAnimation : "<<<subject>>>.resize( <amount> );",
	edu.cmu.cs.stage3.alice.core.response.PointAtAnimation : "<<<subject>>>.pointAt( <target> );",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceAnimation : "<<<subject>>>.turnToFace( <target> );",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromAnimation : "<<<subject>>>.turnAwayFrom( <target> );",
	edu.cmu.cs.stage3.alice.core.response.PointAtConstraint : "<<<subject>>>.constrainToPointAt( <target> );",
	edu.cmu.cs.stage3.alice.core.response.GetAGoodLookAtAnimation : "<<<subject>>>.getAGoodLookAt( <target> );",
	edu.cmu.cs.stage3.alice.core.response.StandUpAnimation : "<<<subject>>>.standUp();",
	edu.cmu.cs.stage3.alice.core.response.PositionAnimation : "<<<subject>>>.moveTo( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PlaceAnimation : "<<<subject>>>.place( <amount>, <spatialRelation>, <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation : "<<<subject>>>.orientTo( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation : "<<<subject>>>.setPointOfView( <asSeenBy> );",
	edu.cmu.cs.stage3.alice.core.response.PropertyAnimation : "<element>.set( <propertyName>, <value> );",
	edu.cmu.cs.stage3.alice.core.response.SoundResponse : "<<<subject>>>.playSound( <sound> );",
	edu.cmu.cs.stage3.alice.core.response.Wait : "wait( <duration> );",
	edu.cmu.cs.stage3.alice.core.response.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.response.Print : "print( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse : "<userDefinedResponse>( <requiredActualParameters> );",
	edu.cmu.cs.stage3.alice.core.response.ScriptResponse : "script( <script> );",
	edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse : "scriptDefinedResponse( <script> );",
	edu.cmu.cs.stage3.alice.core.response.SayAnimation : "<<<subject>>>.say( <what> );",
	edu.cmu.cs.stage3.alice.core.response.ThinkAnimation : "<<<subject>>>.think( <what> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "runPositionKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "runOrientationKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "runScaleKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "runKeyframeAnimationOn( <subject> );",
	edu.cmu.cs.stage3.alice.core.response.PoseAnimation : "<<<subject>>>.setPose( <pose> );",
	edu.cmu.cs.stage3.alice.core.response.Increment : "<<<variable>>>++",
	edu.cmu.cs.stage3.alice.core.response.Decrement : "<<<variable>>>--",

	edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation : "<element>.set( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtBeginning : "<<<list>>>.add( 0, <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtEnd : "<<<list>>>.add( <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtIndex : "<<<list>>>.add( <index>, <item> );",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromBeginning : "<<<list>>>.remove( 0 );",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromEnd : "<<<list>>>.removeLast();", 
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromIndex : "<<<list>>>.remove( <index> );",
	edu.cmu.cs.stage3.alice.core.response.list.Clear : "<<<list>>>.clear();",

	edu.cmu.cs.stage3.alice.core.response.array.SetItemAtIndex : "<<<array>>>[<index>] = <item>;",

	edu.cmu.cs.stage3.alice.core.response.vector3.SetX : "<<<vector3>>>.setDistanceRight( <value> )",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetY : "<<<vector3>>>.setDistanceUp( <value> )",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetZ : "<<<vector3>>>.setDistanceForward( <value> )",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion>( <requiredActualParameters> )",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "return <<value>>;",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "print( <text>, <object> );",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element>.set( <propertyName>, <value> );",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>.partNamed( <key> )",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>.getWidth()",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>.getHeight()",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>.getDepth()",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>.getQuaternion()",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>.getPosition()",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>.getPointOfView()",

	edu.cmu.cs.stage3.alice.core.question.Not : "!<a>",
	edu.cmu.cs.stage3.alice.core.question.And : "(<a>&&<b>)",
	edu.cmu.cs.stage3.alice.core.question.Or : "(<a>||<b>)",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a>+<b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what>.toString()",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "NumberDialog(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "BooleanDialog(<question>)",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "StringDialog(<question>)",

	edu.cmu.cs.stage3.alice.core.question.IsEqualTo : "<a>==<b>",
	edu.cmu.cs.stage3.alice.core.question.IsNotEqualTo : "<a>!=<b>",

	edu.cmu.cs.stage3.alice.core.question.NumberIsEqualTo : "<a>==<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsNotEqualTo : "<a>!=<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThan : "<a>><b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsGreaterThanOrEqualTo : "<a>>=<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsLessThan : "<a>&lt;<b>",
	edu.cmu.cs.stage3.alice.core.question.NumberIsLessThanOrEqualTo : "<a>&lt;=<b>",

	edu.cmu.cs.stage3.alice.core.question.NumberAddition : "(<a>+<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberSubtraction : "(<a>-<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberMultiplication : "(<a>*<b>)", 
	edu.cmu.cs.stage3.alice.core.question.NumberDivision : "(<a>/<b>)",

	edu.cmu.cs.stage3.alice.core.question.math.Min : "Math.min( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "Math.max( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "Math.abs( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "Math.sqrt( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "Math.floor( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "Math.ceil( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "Math.sin( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "Math.cos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "Math.tan( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "Math.asin( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "Math.acos( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "Math.atan( <a> )",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "Math.atan2( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "Math.pow( <a>, <b> )",
	edu.cmu.cs.stage3.alice.core.question.math.Log : "Math.natural log of <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "Math.exp( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "Math.IEEERemainder( <a>, <b> )", 
	edu.cmu.cs.stage3.alice.core.question.math.Round : "Math.round( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "Math.toDegrees( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "Math.toRadians( <a> )", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "superSquareRoot( <a>, <b> )", 

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "Mouse.getDistanceFromLeftEdge()", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "Mouse.getDistanceFromTopEdge()", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "getTimeElapsedSinceWorldStart()", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "getYear()", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "getMonthOfYear()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "getDayOfYear()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "getDayOfMonth()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "getDayOfWeek()", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "getDayOfWeekInMonth()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "isAM()", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "isPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "getHourOfAMOrPM()", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "getHourOfDay()", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "getMinuteOfHour()", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "getSecondOfMinute()", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "Random.nextBoolean()",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "Random.nextDouble()",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list>.contains( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "<list>.indexOf( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "<list>.isEmpty()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "<list>[0]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "<list>.getLastItem()",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "<list>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "<list>.getRandomItem()",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "<list>.lastIndexOf( <item> )",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "<list>.size()",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "<<<array>>>[<index>]",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "<<<array>>>.length",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>>.isAbove( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>>.isBehind( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>>.isBelow( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>>.isInFrontOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>>.isToTheLeftOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>>.isToTheRightOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>>.isSmallerThan( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>>.isLargerThan( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>>.isNarrowerThan( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>>.isWiderThan( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>>.isShorterThan( <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>>.isTallerThan( <object> )",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>>.isCloseTo( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>>.isFarFrom( <threshold>, <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>>.distanceTo( <object> )",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>>.distanceToTheLeftOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>>.distanceToTheRightOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>>.distanceAbove( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>>.distanceBelow( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>>.distanceInFrontOf( <object> )",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>>.distanceBehind( <object> )",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>.getDistanceRight()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>.getDistanceUp()",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>.getDistanceForward()",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "whatWasPicked()",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "getVector( <right>, <up>, <forward> )",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>.getCurrentPose()",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.response.DoInOrder" : "doInOrder",
	"edu.cmu.cs.stage3.alice.core.response.DoTogether" : "doTogether",
	"edu.cmu.cs.stage3.alice.core.response.IfElseInOrder" : "if",
	"edu.cmu.cs.stage3.alice.core.response.LoopNInOrder" : "loop",
	"edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder" : "while",
	"edu.cmu.cs.stage3.alice.core.response.ForEachInOrder" : "forAllInOrder",
	"edu.cmu.cs.stage3.alice.core.response.ForEachTogether" : "forAllTogether",
	"edu.cmu.cs.stage3.alice.core.response.Print" : "print",
	"edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation.quaternion" : "orientation of",
	"edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation.pointOfView" : "point of view of",
	"edu.cmu.cs.stage3.alice.core.response.PositionAnimation.position" : "position of",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "return",

	"edu.cmu.cs.stage3.alice.core.behavior.WorldStartBehavior" : "When the world starts",
	"edu.cmu.cs.stage3.alice.core.behavior.WorldIsRunningBehavior" : "While the world is running",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyClickBehavior" : "When <keyCode> is typed",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyIsPressedBehavior" : "While <keyCode> is pressed",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior" : "When <mouse> is clicked on <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior" : "While <mouse> is pressed on <onWhat>",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalBehavior" : "While <condition> is true",
	"edu.cmu.cs.stage3.alice.core.behavior.ConditionalTriggerBehavior" : "When <condition> becomes true",
	"edu.cmu.cs.stage3.alice.core.behavior.VariableChangeBehavior" : "When <variable> changes",
	"edu.cmu.cs.stage3.alice.core.behavior.MessageReceivedBehavior" : "When a message is received by <toWhom> from <fromWho>", 
	"edu.cmu.cs.stage3.alice.core.behavior.DefaultMouseInteractionBehavior" : "Let <mouse> move <objects>",
	"edu.cmu.cs.stage3.alice.core.behavior.KeyboardNavigationBehavior" : "Let <arrowKeys> move <subject>",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseNavigationBehavior" : "Let <mouse> move the camera",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseLookingBehavior" : "Let <mouse> orient the camera",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundMarkerPassedBehavior" : "When the sound marker <marker> is played",
	"edu.cmu.cs.stage3.alice.core.behavior.SoundLevelBehavior" : "When the sound recording level is >= <level>",

	"edu.cmu.cs.stage3.alice.core.Model.opacity" : "opacity",
	"edu.cmu.cs.stage3.alice.core.Model.diffuseColorMap" : "skin texture",
	"diffuseColorMap" : "skin texture",
	"edu.cmu.cs.stage3.alice.core.Transformable.localTransformation" : "pointOfView",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonClickBehavior.onWhat" : "onWhat",
	"edu.cmu.cs.stage3.alice.core.behavior.MouseButtonIsPressedBehavior.onWhat" : "onWhat",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.threshold" : "is within",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.threshold" : "is at least",
	"edu.cmu.cs.stage3.alice.core.question.IsCloseTo.object" : "of",
	"edu.cmu.cs.stage3.alice.core.question.IsFarFrom.object" : "away from",

	"edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer" : "DirectX 7",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.openglrenderer.Renderer" : "OpenGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.java3drenderer.Renderer" : "Java3D",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.Renderer" : "JOGL",
	"edu.cmu.cs.stage3.alice.scenegraph.renderer.nullrenderer.Renderer" : "None",

	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "BEGIN_AND_END_GENTLY",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "BEGIN_GENTLY_AND_END_ABRUPTLY",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "BEGIN_ABRUPTLY_AND_END_GENTLY",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "BEGIN_AND_END_ABRUPTLY",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "LEFT",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "RIGHT",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "UP",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "DOWN",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "FORWARD",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "BACKWARD",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "LEFT_OF",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "RIGHT_OF",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "ABOVE",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "BELOW",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "IN_FRONT_OF",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "BEHIND",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "ALL",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "LEFT_TO_RIGHT",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "TOP_TO_BOTTOM",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "FRONT_TO_BACK",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "NONE",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "LINEAR",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "EXPONENTIAL",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "SOLID",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "WIREFRAME",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "POINTS",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "NONE",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "FLAT",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "SMOOTH",

	Boolean.TRUE : "true",
	Boolean.FALSE : "false",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "WHITE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "BLACK",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "RED",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "GREEN",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "BLUE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "YELLOW",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "PURPLE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "ORANGE",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "PINK",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "BROWN",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "CYAN",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "MAGENTA",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "GRAY",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "LIGHT_GRAY",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "DARK_GRAY",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "INSTANCE",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "INSTANCE_AND_PARTS",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "INSTANCE_AND_ALL_DESCENDANTS",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "Object",
	"edu.cmu.cs.stage3.alice.core.Model" : "Object",
	"java.lang.Number" : "Number",
	"java.lang.Boolean" : "Boolean",
	"java.lang.String" : "String",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "Color",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "Texture",
	"edu.cmu.cs.stage3.alice.core.Sound" : "Sound",
	"edu.cmu.cs.stage3.alice.core.Pose" : "Pose",
	"edu.cmu.cs.stage3.math.Vector3" : "Posistion",
	"edu.cmu.cs.stage3.math.Quaternion" : "Orientation",
	"edu.cmu.cs.stage3.math.Matrix44" : "PointOfView",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "Object",
	"edu.cmu.cs.stage3.alice.core.Light" : "Light",
	"edu.cmu.cs.stage3.alice.core.Direction" : "Direction",
	"edu.cmu.cs.stage3.alice.core.Collection" : "[]",
}


####################
# Color Config
####################

colorMap = {
	"disabledHTMLText": java.awt.Color( 200, 200, 200 ),
	"disabledHTML": java.awt.Color( 230, 230, 230 ),
	"DoInOrder" : java.awt.Color( 255, 255, 255 ),
	"DoTogether" : java.awt.Color( 255, 255, 255 ),
	"IfElseInOrder" : java.awt.Color( 255, 255, 255 ),
	"LoopNInOrder" : java.awt.Color( 255, 255, 255 ),
	"WhileLoopInOrder" : java.awt.Color( 255, 255, 255 ),
	"ForEach" : java.awt.Color( 255, 255, 255 ),
	"ForEachInOrder" : java.awt.Color( 255, 255, 255 ),
	"ForAllTogether" : java.awt.Color( 255, 255, 255 ),
	"Wait" : java.awt.Color( 255, 255, 255 ),
	"ScriptResponse" : java.awt.Color( 255, 255, 255 ),
	"ScriptDefinedResponse" : java.awt.Color( 255, 255, 255 ),
	"Print" : java.awt.Color( 255, 255, 255 ),
	"Comment" : java.awt.Color( 255, 255, 255 ),
	"Return" : java.awt.Color( 255, 255, 255 ),
	"PropertyAssignment" : java.awt.Color( 255, 255, 255 ),
	"accessibleMathTile" : java.awt.Color( 255, 255, 255 ),
	"dndHighlight" : java.awt.Color( 255, 255, 255 ),
	"dndHighlight2" : java.awt.Color( 0, 200, 0 ),
	"dndHighlight3" : java.awt.Color( 230, 0, 0 ),
	"propertyViewControllerBackground" : java.awt.Color( 255, 255, 255 ),
	"objectTreeSelected" : java.awt.Color( 96, 32, 200 ),
	"objectTreeBackground" : java.awt.Color( 240, 233, 207 ),
	"objectTreeDisabled" : java.awt.Color( 220, 220, 220 ),
	"objectTreeText" : java.awt.Color( 0, 0, 0 ),
	"objectTreeDisabledText" : java.awt.Color( 150, 150, 150 ),
	"objectTreeSelectedText" : java.awt.Color( 240, 240, 240 ),
	"guiEffectsDisabledBackground" : java.awt.Color( 245, 245, 245, 100 ),
	"guiEffectsDisabledLine" : java.awt.Color( 128, 128, 128, 120 ),
	"guiEffectsShadow" : java.awt.Color( 0, 0, 0, 96 ),
	"guiEffectsEdge" : java.awt.Color( 0, 0, 0, 0 ),
	"guiEffectsTroughHighlight" : java.awt.Color( 255, 255, 255 ),
	"guiEffectsTroughShadow" : java.awt.Color( 96, 96, 96 ),
	"propertyDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"prototypeParameter" : java.awt.Color( 255, 255, 255 ),
	"elementDnDPanel" : java.awt.Color( 255, 230, 180 ),
	"elementPrototypeDnDPanel" : java.awt.Color( 255, 255, 255 ),
	"formattedElementViewController" : java.awt.Color( 255, 255, 255 ),
	"response" : java.awt.Color( 255, 255, 255 ),
	"question" : java.awt.Color( 255, 255, 255 ),
	"userDefinedResponse" : java.awt.Color( 255, 255, 255 ),
	"userDefinedQuestion" : java.awt.Color( 255, 255, 255 ),
	"userDefinedQuestionComponent" : java.awt.Color( 255, 255, 255 ),
	"commentForeground" : java.awt.Color( 0, 164, 0 ),
	"variableDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"userDefinedQuestionEditor" : java.awt.Color( 255, 255, 255 ),
	"userDefinedResponseEditor" : java.awt.Color( 255, 255, 255 ),
	"editorHeaderColor" : java.awt.Color( 255, 255, 255 ),
	"behavior" : java.awt.Color( 203, 231, 236 ),
	"behaviorBackground" : java.awt.Color( 255, 255, 255 ),
	"makeSceneEditorBigBackground" : java.awt.Color( 0, 150, 0 ),
	"makeSceneEditorSmallBackground" : java.awt.Color( 0, 150, 0 ),
	"stdErrTextColor" : java.awt.Color( 138, 212, 101 ),
        "mainFontColor" : java.awt.Color(0,0,0),
}


#########################
# Experimental Features
#########################

experimental = 0


#########################
# Misc
#########################

miscMap["javaLikeSyntax"] = "true"


####################################
# transfer resource data to Alice
####################################

resourceTransferFile = java.io.File( JAlice.getAliceHomeDirectory(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile.getAbsolutePath() )
