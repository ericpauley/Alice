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
	edu.cmu.cs.stage3.alice.core.question.visualization.model.Item : "the value of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.model.SetItem : "let <<<subject>>> = <item>",

	edu.cmu.cs.stage3.alice.core.question.visualization.array.ItemAtIndex : "the value at <<<subject>>>[ <index> ]",
	edu.cmu.cs.stage3.alice.core.response.visualization.array.SetItemAtIndex : "let <<<subject>>>[ <index> ] = <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.array.Size : "<<<subject>>>'s size",

	edu.cmu.cs.stage3.alice.core.question.visualization.list.Size : "<<<subject>>>'s size",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.Contains : "<<<subject>>> contains <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.IsEmpty : "<<<subject>>> is empty",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.FirstIndexOfItem : "<<<subject>>>'s first index of <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.LastIndexOfItem : "<<<subject>>>'s last index of <item>",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtBeginning : "<<<subject>>>'s item at beginning",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtEnd : "<<<subject>>>'s item at end",
	edu.cmu.cs.stage3.alice.core.question.visualization.list.ItemAtIndex : "<<<subject>>>'s item at index <index>",
	
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtBeginning : "insert <item> at beginning of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtEnd : "insert <item> at end of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.InsertItemAtIndex : "insert <item> at <index> of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromBeginning : "remove item from beginning of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromEnd : "remove item from end of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.RemoveItemFromIndex : "remove item from <index> of <<<subject>>>",
	edu.cmu.cs.stage3.alice.core.response.visualization.list.Clear : "clear <<<subject>>>",

	edu.cmu.cs.stage3.alice.core.response.MoveAnimation : "<<<subject>>> move <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.MoveTowardAnimation : "<<<subject>>> move <<amount>> toward <<target>>",
	edu.cmu.cs.stage3.alice.core.response.MoveAwayFromAnimation : "<<<subject>>> move <<amount>> away from <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAnimation : "<<<subject>>> turn <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.RollAnimation : "<<<subject>>> roll <<direction>><<amount>>",
	edu.cmu.cs.stage3.alice.core.response.MoveAtSpeed : "<<<subject>>> move at speed <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAtSpeed : "<<<subject>>> turn at speed <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.RollAtSpeed : "<<<subject>>> roll at speed <<direction>><<speed>>",
	edu.cmu.cs.stage3.alice.core.response.ResizeAnimation : "<<<subject>>> resize <<amount>>",
	edu.cmu.cs.stage3.alice.core.response.PointAtAnimation : "<<<subject>>> point at <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceAnimation : "<<<subject>>> turn to face <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromAnimation : "<<<subject>>> turn away from <<target>>",
	edu.cmu.cs.stage3.alice.core.response.PointAtConstraint : "<<<subject>>> constrain to point at <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnToFaceConstraint : "<<<subject>>> constrain to face <<target>>",
	edu.cmu.cs.stage3.alice.core.response.TurnAwayFromConstraint : "<<<subject>>> constrain to face away from <<target>>",
	edu.cmu.cs.stage3.alice.core.response.GetAGoodLookAtAnimation : "<<<subject>>> get a good look at <<target>>",
	edu.cmu.cs.stage3.alice.core.response.StandUpAnimation : "<<<subject>>> stand up",
	edu.cmu.cs.stage3.alice.core.response.PositionAnimation : "<<<subject>>> move to <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PlaceAnimation : "<<<subject>>> caitlin move to <<amount>><<spatialRelation>><<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation : "<<<subject>>> orient to <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation : "<<<subject>>> set point of view to <<asSeenBy>>",
	edu.cmu.cs.stage3.alice.core.response.PropertyAnimation : "<<<element>>> set <propertyName> to <<value>>",
	edu.cmu.cs.stage3.alice.core.response.SoundResponse : "<<<subject>>> play sound <<sound>>",
	edu.cmu.cs.stage3.alice.core.response.Wait : "Wait <<duration>>",
	edu.cmu.cs.stage3.alice.core.response.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.response.Print : "print <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.response.CallToUserDefinedResponse : "<userDefinedResponse><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.response.ScriptResponse : "Script <<script>>",
	edu.cmu.cs.stage3.alice.core.response.ScriptDefinedResponse : "Script-Defined Response <<script>>",
	edu.cmu.cs.stage3.alice.core.response.SayAnimation : "<<<subject>>> say <<what>>",
	edu.cmu.cs.stage3.alice.core.response.ThinkAnimation : "<<<subject>>> think <<what>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse : "position keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse : "orientation keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse : "scale keyframe animation <<subject>>",
	edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse : "keyframe animation <<subject>>",
	edu.cmu.cs.stage3.alice.core.response.PoseAnimation : "<<<subject>>> set pose <<pose>>",
	edu.cmu.cs.stage3.alice.core.response.Increment : "increment <<<variable>>> by 1",
	edu.cmu.cs.stage3.alice.core.response.Decrement : "decrement <<<variable>>> by 1",

	edu.cmu.cs.stage3.alice.core.response.VehiclePropertyAnimation : "<element> set <propertyName> to <value>",

	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtBeginning : "insert <item> at beginning of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtEnd : "insert <item> at end of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.InsertItemAtIndex : "insert <item> at position <index> of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromBeginning : "remove item from beginning of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromEnd : "remove item from end of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.RemoveItemFromIndex : "remove item from position <index> of <<<list>>>",
	edu.cmu.cs.stage3.alice.core.response.list.Clear : "remove all items from <<<list>>>",

	edu.cmu.cs.stage3.alice.core.response.array.SetItemAtIndex : "set item <index> to <item> in <<<array>>>",

	edu.cmu.cs.stage3.alice.core.response.vector3.SetX : "set <<<vector3>>>'s distance right <<value>>",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetY : "set <<<vector3>>>'s distance up <<value>>",
	edu.cmu.cs.stage3.alice.core.response.vector3.SetZ : "set <<<vector3>>>'s distance forward <<value>>",

	edu.cmu.cs.stage3.alice.core.question.userdefined.CallToUserDefinedQuestion : "<userDefinedQuestion><requiredActualParameters>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Return : "Return <<value>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Comment : "// <<text>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.Print : "print <<text>> <<object>>",
	edu.cmu.cs.stage3.alice.core.question.userdefined.PropertyAssignment : "<element> set <propertyName> to <value>",

	edu.cmu.cs.stage3.alice.core.question.PartKeyed : "<<<owner>>>'s part named <key>",
	edu.cmu.cs.stage3.alice.core.question.VariableNamed : "<<<owner>>>'s variable named <variableName> of type <valueClass>",

	edu.cmu.cs.stage3.alice.core.question.Width : "<<<subject>>>'s width",
	edu.cmu.cs.stage3.alice.core.question.Height : "<<<subject>>>'s height",
	edu.cmu.cs.stage3.alice.core.question.Depth : "<<<subject>>>'s depth",
	edu.cmu.cs.stage3.alice.core.question.Quaternion : "<<<subject>>>'s quaternion",
	edu.cmu.cs.stage3.alice.core.question.Position : "<<<subject>>>'s position",
	edu.cmu.cs.stage3.alice.core.question.PointOfView : "<<<subject>>>'s point of view",

	edu.cmu.cs.stage3.alice.core.question.Not : "not <a>",
	edu.cmu.cs.stage3.alice.core.question.And : "both <a> and <b>",
	edu.cmu.cs.stage3.alice.core.question.Or : "either <a> or <b>, or both",

	edu.cmu.cs.stage3.alice.core.question.StringConcatQuestion : "<a> joined with <b>",
	edu.cmu.cs.stage3.alice.core.question.ToStringQuestion : "<what> as a string",

	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForNumber : "ask user for a number <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserYesNo : "ask user for yes or no <<question>>",
	edu.cmu.cs.stage3.alice.core.question.ask.AskUserForString : "ask user for a string <<question>>",

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

	edu.cmu.cs.stage3.alice.core.question.math.Min : "minimum of <a> and <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Max : "maximum of <a> and <b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Abs : "absolute value of <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sqrt : "square root of <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Floor : "floor <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Ceil : "ceiling <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Sin : "sin <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Cos : "cos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Tan : "tan <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ASin : "arcsin <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ACos : "arccos <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ATan : "arctan <a>",
	edu.cmu.cs.stage3.alice.core.question.math.ATan2 : "arctan2 <a><b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Pow : "<a> raised to the <b> power", 
	edu.cmu.cs.stage3.alice.core.question.math.Log : "natural log of <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.Exp : "e raised to the <a> power", 
	edu.cmu.cs.stage3.alice.core.question.math.IEEERemainder : "IEEERemainder of <a>/<b>", 
	edu.cmu.cs.stage3.alice.core.question.math.Round : "round <a>", 
	edu.cmu.cs.stage3.alice.core.question.math.ToDegrees : "<a> converted from radians to degrees", 
	edu.cmu.cs.stage3.alice.core.question.math.ToRadians : "<a> converted from degrees to radians", 
	edu.cmu.cs.stage3.alice.core.question.math.SuperSqrt : "the <b>th root of <a>",
	edu.cmu.cs.stage3.alice.core.question.math.Int : "int <a> as a String",

	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromLeftEdge : "mouse distance from left edge", 
	edu.cmu.cs.stage3.alice.core.question.mouse.DistanceFromTopEdge : "mouse distance from top edge", 

	edu.cmu.cs.stage3.alice.core.question.time.TimeElapsedSinceWorldStart : "time elapsed", 

	edu.cmu.cs.stage3.alice.core.question.time.Year : "year", 
	edu.cmu.cs.stage3.alice.core.question.time.MonthOfYear : "month of year", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfYear : "day of year", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfMonth : "day of month", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeek : "day of week", 
	edu.cmu.cs.stage3.alice.core.question.time.DayOfWeekInMonth : "day of week in month", 
	edu.cmu.cs.stage3.alice.core.question.time.IsAM : "is AM", 
	edu.cmu.cs.stage3.alice.core.question.time.IsPM : "is PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfAMOrPM : "hour of AM or PM", 
	edu.cmu.cs.stage3.alice.core.question.time.HourOfDay : "hour of day", 
	edu.cmu.cs.stage3.alice.core.question.time.MinuteOfHour : "minute of hour", 
	edu.cmu.cs.stage3.alice.core.question.time.SecondOfMinute : "second of minute", 

	edu.cmu.cs.stage3.alice.core.question.RandomBoolean : "choose true <probabilityOfTrue> of the time",
	edu.cmu.cs.stage3.alice.core.question.RandomNumber : "random number",

	edu.cmu.cs.stage3.alice.core.question.list.Contains : "<list> contains <item>",
	edu.cmu.cs.stage3.alice.core.question.list.FirstIndexOfItem : "first index of <item> from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.IsEmpty : "is <list> empty",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtBeginning : "first item from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtEnd : "last item from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtIndex : "item <index> from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.ItemAtRandomIndex : "random item from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.LastIndexOfItem : "last index of <item> from <list>",
	edu.cmu.cs.stage3.alice.core.question.list.Size : "size of <list>",

	edu.cmu.cs.stage3.alice.core.question.array.ItemAtIndex : "item <index> from <<<array>>>",
	edu.cmu.cs.stage3.alice.core.question.array.Size : "size of <<<array>>>",

	edu.cmu.cs.stage3.alice.core.question.IsAbove : "<<<subject>>> is above <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBehind : "<<<subject>>> is behind <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsBelow : "<<<subject>>> is below <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsInFrontOf : "<<<subject>>> is in front of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLeftOf : "<<<subject>>> is to the left of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsRightOf : "<<<subject>>> is to the right of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsSmallerThan : "<<<subject>>> is smaller than <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsLargerThan : "<<<subject>>> is larger than <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsNarrowerThan : "<<<subject>>> is narrower than <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsWiderThan : "<<<subject>>> is wider than <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsShorterThan : "<<<subject>>> is shorter than <<object>>",
	edu.cmu.cs.stage3.alice.core.question.IsTallerThan : "<<<subject>>> is taller than <<object>>",
 
	edu.cmu.cs.stage3.alice.core.question.IsCloseTo : "<<<subject>>> is within <threshold> of <object>",
	edu.cmu.cs.stage3.alice.core.question.IsFarFrom : "<<<subject>>> is at least <threshold> away from <object>",
	edu.cmu.cs.stage3.alice.core.question.DistanceTo : "<<<subject>>> distance to <<object>>",

	edu.cmu.cs.stage3.alice.core.question.DistanceToTheLeftOf : "<<<subject>>> distance to the left of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceToTheRightOf : "<<<subject>>> distance to the right of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceAbove : "<<<subject>>> distance above <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBelow : "<<<subject>>> distance below <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceInFrontOf : "<<<subject>>> distance in front of <<object>>",
	edu.cmu.cs.stage3.alice.core.question.DistanceBehind : "<<<subject>>> distance behind <<object>>",

	edu.cmu.cs.stage3.alice.core.question.vector3.X : "<<<vector3>>>'s distance right",
	edu.cmu.cs.stage3.alice.core.question.vector3.Y : "<<<vector3>>>'s distance up",
	edu.cmu.cs.stage3.alice.core.question.vector3.Z : "<<<vector3>>>'s distance forward",

	edu.cmu.cs.stage3.alice.core.question.PickQuestion : "object under mouse cursor",

	edu.cmu.cs.stage3.alice.core.question.RightUpForward : "<right>, <up>, <forward>",

	edu.cmu.cs.stage3.alice.core.question.Pose : "<<<subject>>>'s current pose",
	edu.cmu.cs.stage3.alice.core.question.PropertyValue : "<<<element>>>.<propertyName>",
}



##################
# Name Config
##################

nameMap = {
	"edu.cmu.cs.stage3.alice.core.response.DoInOrder" : "Do in order",
	"edu.cmu.cs.stage3.alice.core.response.DoTogether" : "Do together",
	"edu.cmu.cs.stage3.alice.core.response.IfElseInOrder" : "If/Else",
	"edu.cmu.cs.stage3.alice.core.response.LoopNInOrder" : "Loop",
	"edu.cmu.cs.stage3.alice.core.response.WhileLoopInOrder" : "While",
	"edu.cmu.cs.stage3.alice.core.response.ForEachInOrder" : "For all in order",
	"edu.cmu.cs.stage3.alice.core.response.ForEachTogether" : "For all together",
	"edu.cmu.cs.stage3.alice.core.response.Print" : "Print",
	"edu.cmu.cs.stage3.alice.core.response.QuaternionAnimation.quaternion" : "offset by",
	"edu.cmu.cs.stage3.alice.core.response.PointOfViewAnimation.pointOfView" : "point of view of",
	"edu.cmu.cs.stage3.alice.core.response.PositionAnimation.position" : "offset by",

	"edu.cmu.cs.stage3.alice.core.question.userdefined.Return" : "Return",

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

	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_GENTLY : "gently",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_GENTLY_AND_END_ABRUPTLY : "begin gently",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY : "end gently",
	edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.BEGIN_AND_END_ABRUPTLY : "abruptly",

	edu.cmu.cs.stage3.alice.core.Direction.LEFT : "left",
	edu.cmu.cs.stage3.alice.core.Direction.RIGHT : "right",
	edu.cmu.cs.stage3.alice.core.Direction.UP : "up",
	edu.cmu.cs.stage3.alice.core.Direction.DOWN : "down",
	edu.cmu.cs.stage3.alice.core.Direction.FORWARD : "forward",
	edu.cmu.cs.stage3.alice.core.Direction.BACKWARD : "backward",

	edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF : "left of",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF : "right of",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE : "above",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW : "below",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF : "in front of",
	edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND : "behind",

	edu.cmu.cs.stage3.alice.core.Dimension.ALL : "all",
	edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT : "left to right",
	edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM : "top to bottom",
	edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK : "front to back",

	edu.cmu.cs.stage3.alice.core.FogStyle.NONE : "no fog",
	edu.cmu.cs.stage3.alice.core.FogStyle.LINEAR : "distance",
	edu.cmu.cs.stage3.alice.core.FogStyle.EXPONENTIAL : "density",

	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID : "solid",
 	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME : "wireframe",
	edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS : "points",

	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE : "none",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT : "flat",
	edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH : "smooth",

	java.lang.Boolean : "Boolean",
	java.lang.Number : "Number",
	edu.cmu.cs.stage3.alice.core.Model : "Object",

	Boolean.TRUE : "true",
	Boolean.FALSE : "false",

	edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE : "no color",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLACK : "black",
	edu.cmu.cs.stage3.alice.scenegraph.Color.RED : "red",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GREEN : "green",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BLUE : "blue",
	edu.cmu.cs.stage3.alice.scenegraph.Color.YELLOW : "yellow",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PURPLE : "purple",
	edu.cmu.cs.stage3.alice.scenegraph.Color.ORANGE : "orange",
	edu.cmu.cs.stage3.alice.scenegraph.Color.PINK : "pink",
	edu.cmu.cs.stage3.alice.scenegraph.Color.BROWN : "brown",
	edu.cmu.cs.stage3.alice.scenegraph.Color.CYAN : "cyan",
	edu.cmu.cs.stage3.alice.scenegraph.Color.MAGENTA : "magenta",
	edu.cmu.cs.stage3.alice.scenegraph.Color.GRAY : "gray",
	edu.cmu.cs.stage3.alice.scenegraph.Color.LIGHT_GRAY : "light gray",
	edu.cmu.cs.stage3.alice.scenegraph.Color.DARK_GRAY : "dark gray",

	edu.cmu.cs.stage3.util.HowMuch.INSTANCE : "object only",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS : "object and parts",
	edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS : "object and all descendents",
}

htmlNameMap = {
	"edu.cmu.cs.stage3.alice.core.Transformable" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Model" : "[Obj]",
	"java.lang.Number" : "[123]",
	"java.lang.Boolean" : "[T/F]",
	"java.lang.String" : "[ABC]",
	"edu.cmu.cs.stage3.alice.scenegraph.Color" : "[Color]",
	"edu.cmu.cs.stage3.alice.core.TextureMap" : "[Texture]",
	"edu.cmu.cs.stage3.alice.core.Sound" : "[Sound]",
	"edu.cmu.cs.stage3.alice.core.Pose" : "[Pose]",
	"edu.cmu.cs.stage3.math.Vector3" : "[Pos]",
	"edu.cmu.cs.stage3.math.Quaternion" : "[Ori]",
	"edu.cmu.cs.stage3.math.Matrix44" : "[POV]",
	"edu.cmu.cs.stage3.alice.core.ReferenceFrame" : "[Obj]",
	"edu.cmu.cs.stage3.alice.core.Light" : "[Light]",
	"edu.cmu.cs.stage3.alice.core.Direction" : "[Direction]",
	"edu.cmu.cs.stage3.alice.core.Collection" : "]]]",
}


####################
# Color Config
####################

colorMap = {
	"disabledHTMLText": java.awt.Color( 200, 200, 200 ),
	"disabledHTML": java.awt.Color( 230, 230, 230 ),
	"DoInOrder" : java.awt.Color( 255, 255, 210 ),
	"DoTogether" : java.awt.Color( 238, 221, 255 ),
	"IfElseInOrder" : java.awt.Color( 204, 238, 221 ),
	"LoopNInOrder" : java.awt.Color( 221, 249, 249 ),
	"WhileLoopInOrder" : java.awt.Color( 204, 255, 221 ),
	"ForEach" : java.awt.Color( 255, 230, 230 ),
	"ForEachInOrder" : java.awt.Color( 255, 230, 230 ),
	"ForAllTogether" : java.awt.Color( 248, 221, 255 ),
	"Wait" : java.awt.Color( 255, 230, 180 ),
	"ScriptResponse" : java.awt.Color( 255, 230, 180 ),
	"ScriptDefinedResponse" : java.awt.Color( 255, 230, 180 ),
	"Print" : java.awt.Color( 255, 230, 180 ),
	"Comment" : java.awt.Color( 255, 255, 255 ),
	"Return" : java.awt.Color( 212, 204, 249 ),
	"PropertyAssignment" : java.awt.Color( 255, 230, 180 ),
	"accessibleMathTile" : java.awt.Color( 255, 230, 180 ),
	"dndHighlight" : java.awt.Color( 255, 255, 0 ),
	"dndHighlight2" : java.awt.Color( 0, 200, 0 ),
	"dndHighlight3" : java.awt.Color( 230, 0, 0 ),
	"propertyViewControllerBackground" : java.awt.Color( 240, 240, 255 ),
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
	"prototypeParameter" : java.awt.Color( 255, 255, 200 ),
	"elementDnDPanel" : java.awt.Color( 255, 230, 180 ),
	"elementPrototypeDnDPanel" : java.awt.Color( 255, 255, 255 ),
	"formattedElementViewController" : java.awt.Color( 255, 255, 255 ),
	"response" : java.awt.Color( 255, 230, 180 ),
	"question" : java.awt.Color( 212, 204, 249 ),
	"userDefinedResponse" : java.awt.Color( 255, 230, 180 ),
	"userDefinedQuestion" : java.awt.Color( 212, 204, 249 ),
	"userDefinedQuestionComponent" : java.awt.Color( 255, 230, 180 ),
	"commentForeground" : java.awt.Color( 0, 164, 0 ),
	"variableDnDPanel" : java.awt.Color( 255, 255, 200 ),
	"userDefinedQuestionEditor" : java.awt.Color( 225, 255, 195 ),
	"userDefinedResponseEditor" : java.awt.Color( 255, 255, 210 ),
	"editorHeaderColor" : java.awt.Color( 255, 255, 255 ),
	"behavior" : java.awt.Color( 203, 231, 236 ),
	"behaviorBackground" : java.awt.Color( 255, 255, 255 ),
	"makeSceneEditorBigBackground" : java.awt.Color( 0, 150, 0 ),
	"makeSceneEditorSmallBackground" : java.awt.Color( 0, 150, 0 ),
	"stdErrTextColor" : java.awt.Color( 52, 174, 32 ),
        "mainFontColor" : java.awt.Color(0,0,0),
        
}


#########################
# Experimental Features
#########################

experimental = 0



####################################
# transfer resource data to Alice
####################################

resourceTransferFile = java.io.File( JAlice.getAliceHomeDirectory(), "resources/common/ResourceTransfer.py" )
execfile( resourceTransferFile.getAbsolutePath() )
