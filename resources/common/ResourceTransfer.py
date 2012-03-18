
from edu.cmu.cs.stage3.alice.authoringtool import AuthoringToolResources
from java.util import Vector
from java.util import HashMap
from edu.cmu.cs.stage3.util import StringObjectPair
from jarray import array
import types


def dictionaryToHashMap( dict ):
	hash = HashMap()
	for key in dict.keys():
		hash.put( key, dict[key] )
	return hash

####################
# Property Config
####################

jPropertyStructure = Vector()
for chunk in propertyStructure:
	className = chunk.pop( 0 )
	groupVec = Vector()
	for group in chunk:
		groupName = group.pop( 0 )
		propVec = Vector()
		for propertyName in group:
			propVec.add( propertyName )
		groupVec.add( StringObjectPair( groupName, propVec ) )
	jPropertyStructure.add( StringObjectPair( className, groupVec ) )
AuthoringToolResources.setPropertyStructure( jPropertyStructure )

AuthoringToolResources.setClassesToOmitNoneFor( array( classesToOmitNoneFor, java.lang.Class ) )

pairs = []
for item in propertiesToOmitNoneFor:
	pairs.append( StringTypePair( item[0], java.lang.Class.forName( item[1] ) ) )
AuthoringToolResources.setPropertiesToOmitNoneFor( array( pairs, StringTypePair ) )

pairs = []
for item in propertiesToIncludeNoneFor:
	pairs.append( StringTypePair( item[0], java.lang.Class.forName( item[1] ) ) )
AuthoringToolResources.setPropertiesToIncludeNoneFor( array( pairs, StringTypePair ) )

pairs = []
for item in propertyNamesToOmit:
	pairs.append( StringTypePair( item[0], java.lang.Class.forName( item[1] ) ) )
AuthoringToolResources.setPropertyNamesToOmit( array( pairs, StringTypePair ) )

def makeValueStructure( group ):
	vec = Vector()
	for value in group:
		if( type( value ) == types.ListType ):
			label = value.pop( 0 )
			vec.add( StringObjectPair( label, makeValueStructure( value ) ) )
		else:
			vec.add( value )
	return vec

defaultValuesStructure = Vector()
for chunk in defaultPropertyValues:
	className = chunk.pop( 0 )
	propertyVec = Vector()
	for propertyGroup in chunk:
		propertyName = propertyGroup.pop( 0 )
		valueVec = makeValueStructure( propertyGroup )
		propertyVec.add( StringObjectPair( propertyName, valueVec ) )
	defaultValuesStructure.add( StringObjectPair( className, propertyVec ) )
AuthoringToolResources.setDefaultPropertyValuesStructure( defaultValuesStructure )

pairs = []
for item in propertiesToOmitScriptDefinedFor:
	pairs.append( StringTypePair( item[0], java.lang.Class.forName( item[1] ) ) )
AuthoringToolResources.setPropertiesToOmitScriptDefinedFor( array( pairs, StringTypePair ) )

worldTreeChildrenPropertiesStructure = Vector()
for chunk in worldTreeChildrenProperties:
	className = chunk.pop( 0 )
	properties = array( chunk, String )
	worldTreeChildrenPropertiesStructure.add( StringObjectPair( className, properties ) )
AuthoringToolResources.setWorldTreeChildrenPropertiesStructure( worldTreeChildrenPropertiesStructure )

AuthoringToolResources.setParameterizedPropertiesToOmit( array( parameterizedPropertiesToOmit, String ) )

for key in propertyValueFormatMap.keys():
	AuthoringToolResources.putPropertyValueFormatMap( key, dictionaryToHashMap( propertyValueFormatMap[key] ) )

for key in unitMap.keys():
	AuthoringToolResources.putUnitString( key, unitMap[key] )


######################
# Response Config
######################

jOneShotStructure = Vector()
for chunk in oneShotStructure:
	className = chunk.pop( 0 )
	groupVec = Vector()
	for group in chunk:
		groupName = group.pop( 0 )
		responseVec = makeValueStructure( group )
		groupVec.add( StringObjectPair( groupName, responseVec ) )
	jOneShotStructure.add( StringObjectPair( className, groupVec ) )
AuthoringToolResources.setOneShotStructure( jOneShotStructure )
AuthoringToolResources.setOneShotGroupsToInclude( array( oneShotGroupsToInclude, String ) )


######################
# Question Config
######################

jQuestionStructure = Vector()
for chunk in questionStructure:
	className = chunk.pop( 0 )
	groupVec = Vector()
	for group in chunk:
		groupName = group.pop( 0 )
		questionVec = Vector()
		for questionName in group:
			questionVec.add( questionName )
		groupVec.add( StringObjectPair( groupName, questionVec ) )
	jQuestionStructure.add( StringObjectPair( className, groupVec ) )
AuthoringToolResources.setQuestionStructure( jQuestionStructure )


######################
# Behavior Config
######################

AuthoringToolResources.setBehaviorClasses( array( behaviorList, java.lang.Class ) )

behaviorParameterPropertiesStructure = Vector()
for chunk in behaviorParameterProperties:
	className = chunk.pop( 0 )
	properties = array( chunk, String )
	behaviorParameterPropertiesStructure.add( StringObjectPair( className, properties ) )
AuthoringToolResources.setBehaviorParameterPropertiesStructure( behaviorParameterPropertiesStructure )

##################
# Name Config
##################

for key in nameMap.keys():
	AuthoringToolResources.putName( key, nameMap[key] )

for key in htmlNameMap.keys():
	AuthoringToolResources.putHTMLName( key, htmlNameMap[key] )


##################
# Name Config
##################

for key in formatMap.keys():
	AuthoringToolResources.putFormat( key, formatMap[key] )


####################
# Variable Config
####################

AuthoringToolResources.setDefaultVariableTypes( array( map( lambda pair: StringTypePair( pair[0], pair[1] ), defaultVariableTypes ), StringTypePair ) )


####################
# Color Config
####################

for key in colorMap.keys():
	AuthoringToolResources.putColor( key, colorMap[key] )


######################
# Importer Config
######################

AuthoringToolResources.setImporterClasses( array( importerList, java.lang.Class ) )


######################
# Editor Config
######################

AuthoringToolResources.setEditorClasses( array( editorList, java.lang.Class ) )


#########################
# Gallery
#########################

AuthoringToolResources.setMainWebGalleryURL( java.net.URL( mainWebGalleryURL ) )
AuthoringToolResources.setMainDiskGalleryDirectory( java.io.File( mainDiskGallery ).getAbsoluteFile() )
AuthoringToolResources.autodetectMainCDGalleryDirectory( mainCDGalleryName )


#########################
# Misc
#########################

AuthoringToolResources.setDefaultAspectRatios( defaultAspectRatios )

for key in miscMap.keys():
	AuthoringToolResources.putMiscItem( key, miscMap[key] )


#########################
# Experimental Features
#########################

AuthoringToolResources.setExperimentalFeaturesEnabled( experimental )
