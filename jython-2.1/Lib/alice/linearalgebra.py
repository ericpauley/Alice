import math

class Vec3:
	def __init__( self, x, y, z ):
		self.x = x
		self.y = y
		self.z = z
	def __add__( self, other ):
		return Vec3( self.x+other.x, self.y+other.y, self.z+other.z )
	def __sub__( self, other ):
		return Vec3( self.x-other.x, self.y-other.y, self.z-other.z )
	def __mul__( self, other ):
		return Vec3( self.x*other, self.y*other, self.z*other )
	def __rmul__( self, other ):
		return Vec3( self.x*other, self.y*other, self.z*other )
	def getLengthSquared( self ):
		return self.x*self.x + self.y*self.y + self.z*self.z
	def getLength( self ):
		return math.sqrt( self.getLengthSquared() )
	def __repr__( self ):
		return "Vec3[ " + `self.x` + ", " + `self.y` + ", " + `self.z` + " ]"

def dot( a, b ):
	return a.x*b.x + a.y*b.y + a.z*b.z
def cross( a, b ):
	return Vec3( a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x )

