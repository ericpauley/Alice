import java
import edu

true = java.lang.Boolean.TRUE
false = java.lang.Boolean.FALSE

from linearalgebra import *

from edu.cmu.cs.stage3.alice.scenegraph import Color
red = Color.RED
pink = Color.PINK
orange = Color.ORANGE
yellow = Color.YELLOW
green = Color.GREEN
blue = Color.BLUE
purple = Color.PURPLE
brown = Color.BROWN
white = Color.WHITE
lightGray = Color.LIGHT_GRAY
gray = Color.GRAY
darkGray = Color.DARK_GRAY
black = Color.BLACK
cyan = Color.CYAN
magenta = Color.MAGENTA

left = edu.cmu.cs.stage3.alice.core.Direction.LEFT
right = edu.cmu.cs.stage3.alice.core.Direction.RIGHT
up = edu.cmu.cs.stage3.alice.core.Direction.UP
down = edu.cmu.cs.stage3.alice.core.Direction.DOWN
forward = edu.cmu.cs.stage3.alice.core.Direction.FORWARD
back = backward = edu.cmu.cs.stage3.alice.core.Direction.BACKWARD

leftToRight = edu.cmu.cs.stage3.alice.core.Dimension.LEFT_TO_RIGHT
topToBottom = edu.cmu.cs.stage3.alice.core.Dimension.TOP_TO_BOTTOM
frontToBack = edu.cmu.cs.stage3.alice.core.Dimension.FRONT_TO_BACK
all = edu.cmu.cs.stage3.alice.core.Dimension.ALL

instance = edu.cmu.cs.stage3.util.HowMuch.INSTANCE
instanceAndParts = edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_PARTS
instanceAndAllDescendants = edu.cmu.cs.stage3.util.HowMuch.INSTANCE_AND_ALL_DESCENDANTS

abruptly = edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.LINEAR
beginGently = edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.SLOW_IN
endGently = edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.SLOW_OUT
gently = edu.cmu.cs.stage3.alice.core.style.TraditionalAnimationStyle.SLOW_IN_OUT

solid = edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.SOLID
wireframe = edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME
points = edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.POINTS

# it is deemed too dangerous to have none, so we use noShading, flatShading, and smoothShading
noShading = edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.NONE
flatShading = edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.FLAT
smoothShading = edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH


#in = edu.cmu.cs.stage3.alice.core.SpatialRelation.IN
#on = edu.cmu.cs.stage3.alice.core.SpatialRelation.ON
#at = edu.cmu.cs.stage3.alice.core.SpatialRelation.AT
leftOf = edu.cmu.cs.stage3.alice.core.SpatialRelation.LEFT_OF
rightOf = edu.cmu.cs.stage3.alice.core.SpatialRelation.RIGHT_OF
above = edu.cmu.cs.stage3.alice.core.SpatialRelation.ABOVE
below = edu.cmu.cs.stage3.alice.core.SpatialRelation.BELOW
inFrontOf = edu.cmu.cs.stage3.alice.core.SpatialRelation.IN_FRONT_OF
behind = edu.cmu.cs.stage3.alice.core.SpatialRelation.BEHIND

from edu.cmu.cs.stage3.math import Distance, Angle, Matrix44, Vector3, Vector4

