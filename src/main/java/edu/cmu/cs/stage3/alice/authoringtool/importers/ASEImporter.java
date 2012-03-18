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

package edu.cmu.cs.stage3.alice.authoringtool.importers;

/**
 * @author Jason Pratt
 */
public class ASEImporter extends edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter {
	protected java.io.StreamTokenizer tokenizer;
	protected java.util.HashMap modelsToParentStrings;
	protected java.util.HashMap namesToModels;
	protected java.util.HashMap namesToMaterials;
	protected java.util.HashMap modelsToMaterialIndices;
	protected java.util.HashMap modelsToKeyframeAnims;
	protected java.util.ArrayList models;
	protected Material[] materials = null;

	protected int firstFrame;
	protected int lastFrame;
	protected int frameSpeed;
	protected int ticksPerFrame;
	protected double timeScaleFactor; // = (1.0/ticksPerFrame) * (1.0/frameSpeed)

	protected String currentObject = "<none>";
	protected String currentlyLoading = "<none>";
	protected int currentProgress = 0;
	//protected ASEOptionsDialog optionsDialog = new ASEOptionsDialog();
	protected ProgressDialog progressDialog;

	protected static edu.cmu.cs.stage3.alice.authoringtool.util.Configuration importersConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration( ASEImporter.class.getPackage() );

	// config init
	static {
		if( importersConfig.getValue( "aseImporter.useSpecular" ) == null ) {
			importersConfig.setValue( "aseImporter.useSpecular", "false" );
		}
		if( importersConfig.getValue( "aseImporter.colorToWhiteWhenTextured" ) == null ) {
			importersConfig.setValue( "aseImporter.colorToWhiteWhenTextured", "true" );
		}
		if( importersConfig.getValue( "aseImporter.groupMultipleRootObjects" ) == null ) {
			importersConfig.setValue( "aseImporter.groupMultipleRootObjects", "true" );
		}
		if( importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ) == null ) {
			importersConfig.setValue( "aseImporter.createNormalsIfNoneExist", "true" );
		}
		if( importersConfig.getValue( "aseImporter.createUVsIfNoneExist" ) == null ) {
			importersConfig.setValue( "aseImporter.createUVsIfNoneExist", "true" );
		}
	}

	public java.util.Map getExtensionMap() {
		java.util.HashMap map = new java.util.HashMap();
		map.put( "ASE", "3D Studio ascii export" );
		return map;
	}

	protected edu.cmu.cs.stage3.alice.core.Element load( java.io.InputStream is, String ext ) throws java.io.IOException {
		edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream bcfis = new edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream( is );
		java.io.BufferedReader br = new java.io.BufferedReader( new java.io.InputStreamReader( bcfis ) );
		tokenizer = new java.io.StreamTokenizer( br );

		tokenizer.eolIsSignificant( false );
		tokenizer.lowerCaseMode( false );
		tokenizer.parseNumbers();
		tokenizer.wordChars( '*', '*' );
		tokenizer.wordChars( '_', '_' );
		tokenizer.wordChars( ':', ':' );

		modelsToParentStrings = new java.util.HashMap();
		namesToModels = new java.util.HashMap();
		namesToMaterials = new java.util.HashMap();
		modelsToMaterialIndices = new java.util.HashMap();
		modelsToKeyframeAnims = new java.util.HashMap();
		models = new java.util.ArrayList();

		//optionsDialog.show();

		progressDialog = new ProgressDialog();
		progressDialog.start();

		try {
			while( true ) {
				tokenizer.nextToken();

				if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE" ) ) {
					currentObject = "scene info";
					parseSceneInfo();
					currentObject = "<none>";
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*GEOMOBJECT" ) ) {
					currentObject = "unnamed object";
					parseGeomObject();
					currentObject = "<none>";
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*HELPEROBJECT" ) ) {
					currentObject = "unnamed helper object";
					parseHelperObject();
					currentObject = "<none>";
				} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_LIST" ) ) {
					currentObject = "material list";
					parseMaterialList();
					currentObject = "<none>";
				} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
					break;
				}
			}
		} catch( java.io.IOException e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error parsing ASE: IOException caught at line " + tokenizer.lineno(), e );
			return null;
		} catch( InvalidFormatError e ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error parsing ASE: Invalid Format: " + e.getMessage() + "; at line " + tokenizer.lineno(), e, false );
			return null;
		}

		edu.cmu.cs.stage3.alice.core.Element element = null;
		try {
			java.util.ArrayList rootModels = new java.util.ArrayList();
			for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				String parentString = (String)modelsToParentStrings.get( model );
				if( parentString == null ) {
					rootModels.add( model );
					model.isFirstClass.set( Boolean.TRUE );
				} else {
					edu.cmu.cs.stage3.alice.core.Transformable parent = (edu.cmu.cs.stage3.alice.core.Transformable)namesToModels.get( parentString );
					if( parent == null ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( model.name.getValue() + "'s parent (" + parentString + ") does not exist;  putting it at the top level...", null );
						rootModels.add( model );
						model.isFirstClass.set( Boolean.TRUE );
					} else {
						parent.addChild( model );
						parent.parts.add( model );
						model.vehicle.set( parent );
						model.isFirstClass.set( Boolean.FALSE );
					}
				}
			}

			if( rootModels.size() == 1 ) {
				element = (edu.cmu.cs.stage3.alice.core.Transformable)rootModels.get( 0 );
			} else if( rootModels.size() > 1 ) {
				if( importersConfig.getValue( "aseImporter.groupMultipleRootObjects" ).equalsIgnoreCase( "true" ) ) {
					element = new edu.cmu.cs.stage3.alice.core.Model();
					element.name.set( null );
					element.isFirstClass.set( Boolean.TRUE );
					for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
						edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
						element.addChild( model );
						((edu.cmu.cs.stage3.alice.core.Model)element).parts.add( model );
						model.vehicle.set( (edu.cmu.cs.stage3.alice.core.Model)element );
						model.isFirstClass.set( Boolean.FALSE );
					}
				} else {
					element = new edu.cmu.cs.stage3.alice.core.Module();
					element.name.set( null );
					for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
						edu.cmu.cs.stage3.alice.core.Transformable model = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
						element.addChild( model );
						model.isFirstClass.set( Boolean.TRUE );
					}
				}
			} else if( rootModels.size() < 1 ) {
				return null;
			}

			String currentName = (String)element.name.getValue();
			if( currentName == null ) {
				element.name.set( plainName );
			} else if( ! currentName.equalsIgnoreCase( plainName ) ) {
				element.name.set( plainName + "_" + currentName );
			}

			edu.cmu.cs.stage3.alice.core.Transformable dummyScene = new edu.cmu.cs.stage3.alice.core.Transformable();
			if( element instanceof edu.cmu.cs.stage3.alice.core.Model ) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)element;
				trans.vehicle.set( dummyScene );
				currentObject = (String)trans.name.getValue();
				currentlyLoading = "fixing transformations";
				fixTransformations( trans, dummyScene );
				currentObject = (String)trans.name.getValue();
				currentlyLoading = "fixing vertices";
				fixVertices( trans );
				currentlyLoading = "<none>";
				trans.vehicle.set( null );
				trans.localTransformation.set( edu.cmu.cs.stage3.math.MathUtilities.createIdentityMatrix4d() );
			} else {
				edu.cmu.cs.stage3.alice.core.Element[] children = element.getChildren();
				for( int i = 0; i < children.length; i++ ) {
					edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)children[i];
					trans.vehicle.set( dummyScene );
					currentObject = (String)trans.name.getValue();
					currentlyLoading = "fixing transformations";
					fixTransformations( trans, dummyScene );
					currentObject = (String)trans.name.getValue();
					currentlyLoading = "fixing vertices";
					fixVertices( trans );
					currentlyLoading = "<none>";
					trans.vehicle.set( null );
				}
			}

			for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				if( trans instanceof edu.cmu.cs.stage3.alice.core.Model ) {
					edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model)trans;
					//TODO make better
					int materialIndex;
					try {
						materialIndex = ((Integer)modelsToMaterialIndices.get( model )).intValue();
					} catch( NullPointerException e ) {
						materialIndex = -1;
					}
					if( (materialIndex >= 0) && (materialIndex < materials.length) ) {
						Material material = materials[materialIndex];
						if( material != null ) {
//							edu.cmu.cs.stage3.alice.scenegraph.Visual sgVisual = model.getSceneGraphVisual();
							if( (material.diffuseTexture != null) && importersConfig.getValue( "aseImporter.colorToWhiteWhenTextured" ).equalsIgnoreCase( "true" ) ) {
								model.ambientColor.set( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
								model.color.set( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
//								sgVisual.getFrontFacingAppearance().setAmbientColor( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
//								sgVisual.getFrontFacingAppearance().setDiffuseColor( edu.cmu.cs.stage3.alice.scenegraph.Color.WHITE );
							} else {
								model.ambientColor.set( material.ambient );
								model.color.set( material.diffuse );
//								sgVisual.getFrontFacingAppearance().setAmbientColor( material.ambient );
//								sgVisual.getFrontFacingAppearance().setDiffuseColor( material.diffuse );
							}
							if( importersConfig.getValue( "aseImporter.useSpecular" ).equalsIgnoreCase( "true" ) ) {
								model.specularHighlightColor.set( material.specular );
								model.specularHighlightExponent.set( new Double( material.shine + 1 ) ); // this is a kludge to get roughly the right shininess.  I don't have the MAX lighting equations...
							}
							model.opacity.set( new Double( 1.0 - material.transparency ) );
							model.diffuseColorMap.set( material.diffuseTexture );
							model.opacityMap.set( material.opacityTexture );
							model.specularHighlightColorMap.set( material.shineTexture );
							model.bumpMap.set( material.bumpTexture );
						}
					} else if( materialIndex != -1 ) {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( model.name.getValue() + " referenced a material index out of range.  no material properties assigned.", null );
					}
				}
			}

			for( int i = 0; i < materials.length; i++ ) {
				if( materials[i] != null ) {
					edu.cmu.cs.stage3.alice.core.Transformable materialOwner = null;
					if( element instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
						materialOwner = (edu.cmu.cs.stage3.alice.core.Transformable)element;
					} else {
						//TODO: if texture is applied to multiple root models, it should be attached to the world,
						//      or something better.  In the implementation below, it just uses the root model of the first
						//      model that uses the material.
						for( java.util.Iterator iter = models.iterator(); iter.hasNext(); ) {
							edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
							try {
								int materialIndex = ((Integer)modelsToMaterialIndices.get( trans )).intValue();
								if( materialIndex == i ) {
									materialOwner = getRootModel( trans );
									break;
								}
							} catch( NullPointerException e ) {
								continue;
							}
						}
					}

					if( materialOwner != null ) {
						if( materials[i].diffuseTexture != null ) {
							materialOwner.addChild( materials[i].diffuseTexture );
							materialOwner.textureMaps.add( materials[i].diffuseTexture );
						}
						if( materials[i].opacityTexture != null ) {
							materialOwner.addChild( materials[i].opacityTexture );
							materialOwner.textureMaps.add( materials[i].diffuseTexture );
						}
						if( materials[i].shineTexture != null ) {
							materialOwner.addChild( materials[i].shineTexture );
							materialOwner.textureMaps.add( materials[i].shineTexture );
						}
						if( materials[i].bumpTexture != null ) {
							materialOwner.addChild( materials[i].bumpTexture );
							materialOwner.textureMaps.add( materials[i].bumpTexture );
						}
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "ASEImporter Error: no materialOwner to attach textures to.", null );
					}
				}
			}

			// Keyframe Animation

			for( java.util.Iterator iter = rootModels.iterator(); iter.hasNext(); ) {
				edu.cmu.cs.stage3.alice.core.Transformable root = (edu.cmu.cs.stage3.alice.core.Transformable)iter.next();
				//System.out.println( "root: " + root );
				edu.cmu.cs.stage3.alice.core.response.DoTogether rootAnim = new edu.cmu.cs.stage3.alice.core.response.DoTogether();
				rootAnim.name.set( "keyframeAnimation" );
				for( java.util.Iterator jter = models.iterator(); jter.hasNext(); ) {
					edu.cmu.cs.stage3.alice.core.Transformable trans = (edu.cmu.cs.stage3.alice.core.Transformable)jter.next();
					//System.out.println( "trans: " + trans );
					if( trans.isDescendantOf( root ) || trans.equals( root ) ) {
						java.util.ArrayList anims = (java.util.ArrayList)modelsToKeyframeAnims.get( trans );
						if( anims != null ) {
							//System.out.println( trans + " has anims" );
							String prefix = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getReprForValue( trans );
							prefix = prefix.replace( '.', '_' );
							for( java.util.Iterator kter = anims.iterator(); kter.hasNext(); ) {
								edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse anim = (edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse)kter.next();
								anim.duration.set( null );
								String baseName = anim.name.getStringValue();
								anim.name.set( prefix + "_" + baseName );
								anim.subject.set( trans );
								rootAnim.addChild( anim );
								rootAnim.componentResponses.add( anim );
								//System.out.println( "adding " + anim );
							}
						}
					}
				}
				if( ! rootAnim.componentResponses.isEmpty() ) {
					root.addChild( rootAnim );
					root.responses.add( rootAnim );
				}
			}
		} catch( Throwable t ) {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "An unexpected error occured while loading an ASE.", t );
		}

		progressDialog.stop();
		progressDialog.setVisible( false );
		progressDialog.dispose();

		tokenizer = null;
		modelsToParentStrings = null;
		namesToModels = null;
		namesToMaterials = null;
		modelsToMaterialIndices = null;
		modelsToKeyframeAnims = null;
		models = null;
		materials = null;

		return element;
	}

	protected edu.cmu.cs.stage3.alice.core.Transformable getRootModel( edu.cmu.cs.stage3.alice.core.Transformable trans ) {
		edu.cmu.cs.stage3.alice.core.Element parent = trans.getParent();
		if( ! (parent instanceof edu.cmu.cs.stage3.alice.core.Transformable) ) {
			return trans;
		} else {
			return getRootModel( (edu.cmu.cs.stage3.alice.core.Transformable)parent );
		}
	}

	// all coordinates in ASEs are in world space, so everything has to be fixed once we have the hierarchy...
	protected void fixTransformations( edu.cmu.cs.stage3.alice.core.Transformable root, edu.cmu.cs.stage3.alice.core.Transformable scene ) {
		root.setTransformationRightNow( root.getLocalTransformation(), scene );
		/*
		root.setLocalTransformation(
			edu.cmu.cs.stage3.math.Matrix44.multiply(
				((edu.cmu.cs.stage3.alice.core.ReferenceFrame)root.vehicle.getValue()).getSceneGraphReferenceFrame().getInverseAbsoluteTransformation(),
				(edu.cmu.cs.stage3.math.Matrix44)root.localTransformation.getValue()
			)
		);
		*/

		edu.cmu.cs.stage3.alice.core.Element[] children = root.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				fixTransformations( (edu.cmu.cs.stage3.alice.core.Transformable)children[i], scene );
			}
		}
	}

	protected void fixVertices( edu.cmu.cs.stage3.alice.core.Transformable root ) {
		currentObject = (String)root.name.getValue();
		if( root instanceof edu.cmu.cs.stage3.alice.core.Model ) {
			if( ((edu.cmu.cs.stage3.alice.core.Model)root).geometry.getValue() instanceof edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray ) {
				edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geom = (edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray)((edu.cmu.cs.stage3.alice.core.Model)root).geometry.getValue();
				edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] vertices = (edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[])geom.vertices.getValue();
				if( vertices != null ) {
					progressDialog.setMax( vertices.length - 1 );
					currentProgress = 0;
					for( int i = 0; i < vertices.length; i++ ) {
						currentProgress = i;
						edu.cmu.cs.stage3.math.Vector4 v = new edu.cmu.cs.stage3.math.Vector4( vertices[i].position.x, vertices[i].position.y, vertices[i].position.z, 1.0 );
						edu.cmu.cs.stage3.math.Vector4 vprime = edu.cmu.cs.stage3.math.Vector4.multiply( v, root.getSceneGraphReferenceFrame().getInverseAbsoluteTransformation() );
						vertices[i].position.set( vprime.x, vprime.y, vprime.z );
					}
					geom.vertices.set( vertices );
				}
			}
		}

		edu.cmu.cs.stage3.alice.core.Element[] children = root.getChildren();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i] instanceof edu.cmu.cs.stage3.alice.core.Transformable ) {
				fixVertices( (edu.cmu.cs.stage3.alice.core.Transformable)children[i] );
			}
		}
	}

	protected String parseString() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == '"' ) {
			return tokenizer.sval;
		} else {
			throw new InvalidFormatError( "String value expected" );
		}
	}

	protected int parseInt() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return (int)tokenizer.nval;
		} else {
			throw new InvalidFormatError( "int value expected" );
		}
	}

	protected double parseDouble() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return tokenizer.nval;
		} else {
			throw new InvalidFormatError( "double value expected" );
		}
	}

	protected float parseFloat() throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();

		if( tokenizer.ttype == java.io.StreamTokenizer.TT_NUMBER ) {
			return (float)tokenizer.nval;
		} else {
			throw new InvalidFormatError( "double value expected" );
		}
	}

	protected void parseUnknownBlock() throws InvalidFormatError, java.io.IOException {
		while( true ) {
			tokenizer.nextToken();

			if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished block" );
			}
		}
	}

	protected void parseSceneInfo() throws java.io.IOException {
		firstFrame = 0;
		lastFrame = 0;
		frameSpeed = 0;
		ticksPerFrame = 0;
		timeScaleFactor = 1.0;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *SCENE" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_FIRSTFRAME" ) ) {
				firstFrame = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_LASTFRAME" ) ) {
				lastFrame = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_FRAMESPEED" ) ) {
				frameSpeed = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*SCENE_TICKSPERFRAME" ) ) {
				ticksPerFrame = parseInt();
			} else if( tokenizer.ttype == '}' ) {
				try {
					timeScaleFactor = (1.0/(double)ticksPerFrame) * (1.0/(double)frameSpeed);
				} catch( Exception e ) {
					timeScaleFactor = 1.0;
				}
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *SCENE" );
			}
		}
	}

	protected void parseMaterialList() throws InvalidFormatError, java.io.IOException {
		int count = 0;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MATERIAL_LIST" );
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_COUNT" ) ) {
				count = parseInt();
				materials = new Material[count];
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL" ) ) {
				if( count < 1 ) {
					throw new InvalidFormatError( "material declared before number of materials defined" );
				}

				parseMaterial();
				currentObject = "<none>";
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_VERTEX_LIST" );
			}
		}
	}

	protected void parseMaterial() throws InvalidFormatError, java.io.IOException {
		Material material = new Material();

		int index = parseInt();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MATERIAL <n>" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_NAME" ) ) {
				material.name = tokenizer.sval;
				namesToMaterials.put( material.name, material );
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_AMBIENT" ) ) {
				material.ambient = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_DIFFUSE" ) ) {
				material.diffuse = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SPECULAR" ) ) {
				material.specular = new edu.cmu.cs.stage3.alice.scenegraph.Color( parseDouble(), parseDouble(), parseDouble() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SHINE" ) ) {
				material.shine = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_SHINESTRENGTH" ) ) {
				material.shinestrength = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_TRANSPARENCY" ) ) {
				material.transparency = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_AMBIENT" ) ) {
				material.ambientTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_DIFFUSE" ) ) {
				material.diffuseTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SHINE" ) ) {
				material.shineTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SHINESTRENGTH" ) ) {
				material.shineStrengthTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_SELFILLUM" ) ) {
				material.selfIllumTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_OPACITY" ) ) {
				material.opacityTexture = parseMap();
				currentObject = material.name;
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MAP_BUMP" ) ) {
				material.bumpTexture = parseMap();
				currentObject = material.name;
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				materials[index] = material;
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MATERIAL" );
			}
		}
	}

	protected edu.cmu.cs.stage3.alice.core.TextureMap parseMap() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.alice.core.TextureMap texture = null;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MAP_<map type>" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*BITMAP" ) ) {
				String filename = parseString();
				currentObject = filename;

				java.io.File imageFile = new java.io.File( filename );
				String justName = imageFile.getName();
				String extension = justName.substring( justName.lastIndexOf( '.' ) + 1 );
				java.io.BufferedInputStream bis = null;

				if( imageFile.exists() ) {
					if( imageFile.canRead() ) {
						bis = new java.io.BufferedInputStream( new java.io.FileInputStream( imageFile ) );
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Cannot read from file \"" + filename + "\" specified on line " + tokenizer.lineno(), null, false );
						continue;
					}
				} else {
					Object location = getLocation();
					if( location instanceof java.io.File ) {
						imageFile = new java.io.File( (java.io.File)location, filename );
						if( ! imageFile.exists() ) {
							imageFile = new java.io.File( (java.io.File)location, justName );
						}
						if( imageFile.exists() ) {
							if( imageFile.canRead() ) {
								bis = new java.io.BufferedInputStream( new java.io.FileInputStream( imageFile ) );
							} else {
								edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Cannot read from file \"" + filename + "\" specified on line " + tokenizer.lineno(), null, false );
								continue;
							}
						} else {
							edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Unable to find file \"" + filename + "\" specified on line " + tokenizer.lineno(), null, false );
							continue;
						}
					} else if( location instanceof java.net.URL ) {
						// escape necessary characters
						StringBuffer name = new StringBuffer();
						char[] chars = new char[justName.length()];
						justName.getChars( 0, justName.length(), chars, 0 );
						for( int i = 0; i < chars.length; i++ ) {
							char c = chars[i];
							if( c == ' ' ) {
								name.append( "%20" );
							} else if( c == '#' ) {
								name.append( "%23" );
							} else if( c == ';' ) {
								name.append( "%3B" );
							} else if( c == '@' ) {
								name.append( "%40" );
							} else if( c == '&' ) {
								name.append( "%26" );
							} else if( c == '=' ) {
								name.append( "%3D" );
							} else if( c == '+' ) {
								name.append( "%2B" );
							} else if( c == '$' ) {
								name.append( "%24" );
							} else if( c == ',' ) {
								name.append( "%2C" );
							} else if( c == '%' ) {
								name.append( "%25" );
							} else if( c == '"' ) {
								name.append( "%22" );
							} else if( c == '{' ) {
								name.append( "%7B" );
							} else if( c == '}' ) {
								name.append( "%7D" );
							} else if( c == '^' ) {
								name.append( "%5E" );
							} else if( c == '[' ) {
								name.append( "%5B" );
							} else if( c == ']' ) {
								name.append( "%5D" );
							} else if( c == '`' ) {
								name.append( "%60" );
							} else {
								name.append( c );
							}
						}
						java.net.URL url = (java.net.URL)location;
						url = new java.net.URL( url.toExternalForm() + name.toString() );
						bis = new java.io.BufferedInputStream( url.openStream() );
					} else {
						edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "location is not a File or URL: " + location, null, false );
					}
				}
				if( bis == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "BufferedInputStream is null for " + filename, null );
					continue;
				}

				String codec = edu.cmu.cs.stage3.image.ImageIO.mapExtensionToCodecName( extension );
				if( codec == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Can't find appropriate codec for " + filename, null );
					continue;
				}

				java.awt.Image image = edu.cmu.cs.stage3.image.ImageIO.load( codec, bis );
				if( image == null ) {
					edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Image loaded is null for " + filename, null );
					continue;
				}

				String textureName = justName.substring( 0, justName.indexOf( '.' ) );
				texture = new edu.cmu.cs.stage3.alice.core.TextureMap();

				if( image instanceof java.awt.image.BufferedImage ) {
					java.awt.image.BufferedImage bi = (java.awt.image.BufferedImage)image;
					if( bi.getColorModel().hasAlpha() ) {
						texture.format.set( new Integer( edu.cmu.cs.stage3.alice.scenegraph.TextureMap.RGBA ) );
					}
				}

				texture.name.set( textureName );
				texture.image.set( image );
			} else if( tokenizer.ttype == '}' ) {
				return texture;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MAP_DIFFUSE" );
			}
		}
	}

	protected void parseHelperObject() throws java.io.IOException {
		edu.cmu.cs.stage3.alice.core.Model helper = new edu.cmu.cs.stage3.alice.core.Model();
		helper.isFirstClass.set( Boolean.FALSE );
		models.add( helper );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *HELPEROBJECT" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_NAME" ) ) {
				helper.name.set( parseString() );
				namesToModels.put( helper.name.getValue(), helper );
				currentObject = (String)helper.name.getValue();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_PARENT" ) ) {
				modelsToParentStrings.put( helper, parseString() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_TM" ) ) {
				currentlyLoading = "transformation";
				currentProgress = 0;
				helper.localTransformation.set( parseTransformation() );
				currentProgress = 0;
				currentlyLoading = "<none>";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ANIMATION" ) ) {
				java.util.ArrayList anims = parseAnimationNode();
				modelsToKeyframeAnims.put( helper, anims );
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *HELPEROBJECT" );
			}
		}
	}

	protected void parseGeomObject() throws java.io.IOException {
		edu.cmu.cs.stage3.alice.core.Model model = new edu.cmu.cs.stage3.alice.core.Model();
		model.isFirstClass.set( Boolean.FALSE );
		models.add( model );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *GEOMOBJECT" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_NAME" ) ) {
				model.name.set( parseString() );
				namesToModels.put( model.name.getValue(), model );
				currentObject = (String)model.name.getValue();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_PARENT" ) ) {
				modelsToParentStrings.put( model, parseString() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*NODE_TM" ) ) {
				currentlyLoading = "transformation";
				currentProgress = 0;
				model.localTransformation.set( parseTransformation() );
				currentProgress = 0;
				currentlyLoading = "<none>";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH" ) ) {
				currentlyLoading = "mesh";
				currentProgress = 0;
				model.geometry.set( parseMesh() );
				model.geometry.getElementValue().setParent( model );
				model.geometry.getElementValue().name.set( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( "__ita__", model ) );
				currentProgress = 0;
				currentlyLoading = "<none>";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*PROP_CASTSHADOW" ) ) {
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*PROP_RECVSHADOW" ) ) {
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MATERIAL_REF" ) ) {
				modelsToMaterialIndices.put( model, new Integer( parseInt() ) );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ANIMATION" ) ) {
				java.util.ArrayList anims = parseAnimationNode();
				modelsToKeyframeAnims.put( model, anims );
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *GEOMOBJECT" );
			}
		}
	}

	protected edu.cmu.cs.stage3.math.Matrix44 parseTransformation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.math.Matrix44 m = new edu.cmu.cs.stage3.math.Matrix44();
		edu.cmu.cs.stage3.math.Matrix33 rot = new edu.cmu.cs.stage3.math.Matrix33();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *NODE_TM" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW0" ) ) {
				rot.m00 = parseDouble();
				rot.m01 = parseDouble();
				rot.m02 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW1" ) ) {
				rot.m10 = parseDouble();
				rot.m11 = parseDouble();
				rot.m12 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW2" ) ) {
				rot.m20 = parseDouble();
				rot.m21 = parseDouble();
				rot.m22 = parseDouble();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*TM_ROW3" ) ) {
				// X = -X, Y = Z, Z = -Y
				m.m30 = -parseDouble();
				m.m32 = -parseDouble();
				m.m31 = parseDouble();
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				/**
				 * Transforming a rotation matrix from MAX space (right-handed, x-left, y-back, z-up)
				 * to Alice space (left-handed, x-right, y-up, z-forward):
				 *
				 * [ a  b  c ]       [-a -b -c ]       [ a -c  b ]
				 * [ d  e  f ]  -->  [ g  h  i ]  -->  [-g  i -h ]
				 * [ g  h  i ]       [-d -e -f ]       [ d -f  e ]
				 */


				m.m00 =  rot.m00;
				m.m01 = -rot.m02;
				m.m02 =  rot.m01;
				m.m10 = -rot.m20;
				m.m11 =  rot.m22;
				m.m12 = -rot.m21;
				m.m20 =  rot.m10;
				m.m21 = -rot.m12;
				m.m22 =  rot.m11;

				//m.makeAffine();  // should NOT be needed
				return m;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *NODE_TM" );
			}
		}
	}

	protected edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray parseMesh() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray geometry = new edu.cmu.cs.stage3.alice.core.geometry.IndexedTriangleArray();
		edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[] verts = null;
		int[] coordIndices = null;
		int[] uvIndices = null;
		double[] coordinates = null;
		double[] normals = null;
		float[] uvs = null;
		double[] colors = null;
		int numVerts = -1;
		int numUVs = -1;
		int numFaces = -1;

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMVERTEX" ) ) {
				numVerts = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMFACES" ) ) {
				numFaces = parseInt();
				coordIndices = new int[numFaces*3];
				uvIndices = new int[numFaces*3];    // assuming same number of texture faces for now
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NUMTVERTEX" ) ) {
				numUVs = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEX_LIST" ) ) {
				if( numVerts < 0 ) {
					throw new InvalidFormatError( "illegal number of vertices defined or coordinates declared before number of vertices defined" );
				}
				coordinates = new double[numVerts*3];

				currentlyLoading = "coordinates";
				progressDialog.setMax( numVerts );
				parseVertexList( coordinates );
				currentlyLoading = "mesh";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TVERTLIST" ) ) {
				if( numUVs < 0 ) {
					throw new InvalidFormatError( "uvs declared before number of texture vertices defined" );
				}
				uvs = new float[numUVs*2];

				currentlyLoading = "uvs";
				progressDialog.setMax( numUVs );
				parseUVList( uvs );
				currentlyLoading = "mesh";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_NORMALS" ) ) {
				if( numVerts < 0 ) {
					throw new InvalidFormatError( "normals declared before number of vertices defined" );
				}
				normals = new double[numFaces*3*3];

				currentlyLoading = "normals";
				progressDialog.setMax( numFaces );
				parseNormals( normals );
				currentlyLoading = "mesh";
			//TODO vertex colors
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE_LIST" ) ) {
				if( coordIndices == null ) {
					throw new InvalidFormatError( "faces declared before number of faces defined" );
				}

				currentlyLoading = "coordinate indices";
				progressDialog.setMax( coordIndices.length/3 );
				parseFaceList( coordIndices );
				currentlyLoading = "mesh";
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TFACELIST" ) ) {
				if( uvIndices == null ) {
					throw new InvalidFormatError( "texture faces declared before number of faces defined" );
				}

				currentlyLoading = "texture indices";
				progressDialog.setMax( uvIndices.length/3 );
				parseUVFaceList( uvIndices );
				currentlyLoading = "mesh";
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				if( (numVerts > 0) && (coordIndices != null) ) {
					int vertexFormat = 0;
					if( coordinates != null ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_POSITION;
					}
					if( (normals != null) || importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ).equalsIgnoreCase( "true" ) ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_NORMAL;
					}
					if( (uvs != null) || importersConfig.getValue( "aseImporter.createUVsIfNoneExist" ).equalsIgnoreCase( "true" ) ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_TEXTURE_COORDINATE_0;
					}
					if( colors != null ) {
						vertexFormat |= edu.cmu.cs.stage3.alice.scenegraph.Vertex3d.FORMAT_DIFFUSE_COLOR;
					}

					verts = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d[numFaces*3];
					for( int i = 0; i < numFaces*3; i++ ) {
						verts[i] = new edu.cmu.cs.stage3.alice.scenegraph.Vertex3d( vertexFormat );
					}

					int[] indices = new int[numFaces*3];
					for( int i = 0; i < numFaces*3; i++ ) {
						indices[i] = i;
					}

					for( int i = 0; i < numFaces; i++ ) {
						for( int j = 0; j < 3; j++ ) {
							if( coordinates != null ) {
								verts[i*3 + j].position.x = coordinates[coordIndices[i*3 + j]*3 + 0];
								verts[i*3 + j].position.y = coordinates[coordIndices[i*3 + j]*3 + 1];
								verts[i*3 + j].position.z = coordinates[coordIndices[i*3 + j]*3 + 2];
							}
							if( normals != null ) {
								verts[i*3 + j].normal.x = normals[i*9 + j*3 + 0];
								verts[i*3 + j].normal.y = normals[i*9 + j*3 + 1];
								verts[i*3 + j].normal.z = normals[i*9 + j*3 + 2];
							}
							if( uvs != null ) {
								verts[i*3 + j].textureCoordinate0.x = uvs[uvIndices[i*3 + j]*2 + 0];
								verts[i*3 + j].textureCoordinate0.y = uvs[uvIndices[i*3 + j]*2 + 1];
							}
							//TODO colors
						}
					}

					geometry.vertices.set( verts );
					geometry.indices.set( indices );

					if( (normals == null) && importersConfig.getValue( "aseImporter.createNormalsIfNoneExist" ).equalsIgnoreCase( "true" ) ) {
						edu.cmu.cs.stage3.alice.gallery.ModelFixer.calculateNormals( geometry );
					}
				}

				return geometry;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH" );
			}
		}
	}

	protected void parseVertexList( double[] coordinates ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH_VERTEX_LIST" );
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEX" ) ) {
				int index = parseInt();
				// X = -X, Y = Z, Z = -Y
				coordinates[index*3 + 0] = -parseDouble();
				coordinates[index*3 + 2] = -parseDouble();
				coordinates[index*3 + 1] = parseDouble();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_VERTEX_LIST" );
			}
		}
	}

	protected void parseUVList( float[] uvs ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH_TVERTLIST" );
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TVERT" ) ) {
				int index = parseInt();
				uvs[index*2 + 0] = parseFloat();
				uvs[index*2 + 1] = parseFloat();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_TVERTLIST" );
			}
		}
	}

	protected void parseNormals( double[] normals ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH_NORMALS" );
		}

		// this may not be exactly correct...
		int face = 0;
		int v = 0;
		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_VERTEXNORMAL" ) ) {
				int index = parseInt();
				int realv = v;
				// reverse face order
				if( v == 1 ) realv = 2;
				if( v == 2 ) realv = 1;
				// X = -X, Y = Z, Z = -Y
				normals[face*9 + realv*3 + 0] = -parseDouble();
				normals[face*9 + realv*3 + 2] = -parseDouble();
				normals[face*9 + realv*3 + 1] = parseDouble();
				v++;
				if( v == 3 ) {
					v = 0;
					face++;
				}
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_NORMALS" );
			}
		}
	}

	protected void parseVertexColors( double[] colors ) {
		//TODO
	}

	protected void parseFaceList( int[] indices ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH_FACE_LIST" );
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE" ) ) {
				parseMeshFace( indices );
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_FACE_LIST" );
			}
		}
	}

	protected void parseMeshFace( int[] indices ) throws InvalidFormatError, java.io.IOException {
		int index = parseInt();

		while( true ) {
			tokenizer.nextToken();

			// reverse face order
			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "A:" ) ) {
				indices[index*3 + 0] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "B:" ) ) {
				indices[index*3 + 2] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "C:" ) ) {
				indices[index*3 + 1] = parseInt();
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_FACE" ) ) {
				tokenizer.pushBack();
				return;
			} else if( tokenizer.ttype == '}' ) {
				tokenizer.pushBack();
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_FACE" );
			}
		}
	}

	protected void parseUVFaceList( int[] indices ) throws InvalidFormatError, java.io.IOException {
		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *MESH_TFACELIST" );
		}

		currentProgress = 0;
		while( true ) {
			tokenizer.nextToken();

			// reverse face order
			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*MESH_TFACE" ) ) {
				int index = parseInt();
				indices[index*3 + 0] = parseInt();
				indices[index*3 + 2] = parseInt();
				indices[index*3 + 1] = parseInt();
				currentProgress++;
			} else if( tokenizer.ttype == '}' ) {
				return;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *MESH_TFACELIST" );
			}
		}
	}

	protected java.util.ArrayList parseAnimationNode() throws InvalidFormatError, java.io.IOException {
		java.util.ArrayList anims = new java.util.ArrayList();

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *TM_ANIMATION" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_LINEAR" ) ) {
				anims.add( parseLinearPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_BEZIER" ) ) {
				anims.add( parseBezierPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_TCB" ) ) {
				anims.add( parseTCBPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_LINEAR" ) ) { // to the best of my knowledge, rotation animations are all handled by quaternion slerping
				anims.add( parseLinearQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_BEZIER" ) ) {
				anims.add( parseBezierQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_TCB" ) ) {
				anims.add( parseTCBQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_LINEAR" ) ) {
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_BEZIER" ) ) {
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_TCB" ) ) {
				//TODO
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_TRACK" ) ) { // sampled animations create CatmullRom based keyframe animations
				anims.add( parseSampledPositionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_TRACK" ) ) {
				anims.add( parseSampledQuaternionAnimation() );
			} else if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_TRACK" ) ) {
				//TODO
			} else if( tokenizer.ttype == '{' ) {
				parseUnknownBlock();
			} else if( tokenizer.ttype == '}' ) {
				return anims;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *TM_ANIMATION" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "linearPositionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_POS_LINEAR" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey( time, new javax.vecmath.Vector3d( x, y, z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_POS_LINEAR" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "bezierPositionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_POS_BEZIER" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_BEZIER_POS_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				double intan_x = -parseDouble();
				double intan_z = -parseDouble();
				double intan_y = parseDouble();
				double outtan_x = -parseDouble();
				double outtan_z = -parseDouble();
				double outtan_y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3BezierKey( time, new javax.vecmath.Vector3d( x, y, z ), new javax.vecmath.Vector3d( intan_x, intan_y, intan_z ), new javax.vecmath.Vector3d( outtan_x, outtan_y, outtan_z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.convertMAXTangentsToBezierTangents( timeScaleFactor );
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_POS_BEZIER" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "tcbPositionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_POS_TCB" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_POS_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				double tension = parseDouble();
				double continuity = parseDouble();
				double bias = parseDouble();
				double easeIn = parseDouble();  // NOT USED AT THE MOMENT
				double easeOut = parseDouble(); // NOT USED AT THE MOMENT
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3TCBKey( time, new javax.vecmath.Vector3d( x, y, z ), tension, continuity, bias ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_POS_TCB" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "quaternionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_ROT_LINEAR" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_ROT_LINEAR" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "quaternionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_ROT_BEZIER" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_ROT_BEZIER" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "tcbQuaternionKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_ROT_TCB" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_ROT_KEY" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				double tension = parseDouble();     // NOT USED IN QUATERNION ANIMATION
				double continuity = parseDouble();  // NOT USED IN QUATERNION ANIMATION
				double bias = parseDouble();        // NOT USED IN QUATERNION ANIMATION
				double easeIn = parseDouble();  // NOT USED AT THE MOMENT
				double easeOut = parseDouble(); // NOT USED AT THE MOMENT
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionTCBKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ), tension, continuity, bias ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.correctForMAXRelativeKeys();
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_ROT_TCB" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseLinearScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "linearScaleKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.LinearSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_SCALE_LINEAR" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_SCALE_KEY" ) ) {
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_SCALE_LINEAR" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseBezierScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "bezierScaleKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.BezierSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_SCALE_BEZIER" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_BEZIER_SCALE_KEY" ) ) {
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				spline.convertMAXTangentsToBezierTangents( timeScaleFactor );
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_SCALE_BEZIER" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseTCBScaleAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.ScaleKeyframeResponse();
		keyframeResponse.name.set( "tcbScaleKeyframeAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.TCBSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_SCALE_TCB" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_TCB_SCALE_KEY" ) ) {
				//TODO
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_SCALE_TCB" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseSampledPositionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.PositionKeyframeResponse();
		keyframeResponse.name.set( "sampledPositionAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.CatmullRomSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_POS_TRACK" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_POS_SAMPLE" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double x = -parseDouble();
				double z = -parseDouble();
				double y = parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.Vector3SimpleKey( time, new javax.vecmath.Vector3d( x, y, z ) ) );
			} else if( tokenizer.ttype == '}' ) {
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_POS_TRACK" );
			}
		}
	}

	protected edu.cmu.cs.stage3.pratt.maxkeyframing.KeyframeResponse parseSampledQuaternionAnimation() throws InvalidFormatError, java.io.IOException {
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse keyframeResponse = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKeyframeResponse();
		keyframeResponse.name.set( "sampledQuaternionAnim" );
		edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline spline = new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionSlerpSpline();
		keyframeResponse.spline.set( spline );

		tokenizer.nextToken();
		if( tokenizer.ttype != '{' ) {
			throw new InvalidFormatError( "Block expected after *CONTROL_ROT_TRACK" );
		}

		while( true ) {
			tokenizer.nextToken();

			if( (tokenizer.sval != null) && tokenizer.sval.equalsIgnoreCase( "*CONTROL_ROT_SAMPLE" ) ) {
				double time = parseDouble()*timeScaleFactor;
				// don't forget to convert MAX's coordinate system...
				// X = -X, Y = Z, Z = -Y
				double axis_x = -parseDouble();
				double axis_z = -parseDouble();
				double axis_y = parseDouble();
				double angle = -parseDouble();
				spline.addKey( new edu.cmu.cs.stage3.pratt.maxkeyframing.QuaternionKey( time, new edu.cmu.cs.stage3.math.Quaternion( new edu.cmu.cs.stage3.math.AxisAngle( axis_x, axis_y, axis_z, angle ) ) ) );
			} else if( tokenizer.ttype == '}' ) {
				spline.correctForMAXRelativeKeys();
				return keyframeResponse;
			} else if( tokenizer.ttype == java.io.StreamTokenizer.TT_EOF ) {
				throw new InvalidFormatError( "unfinished *CONTROL_ROT_TRACK" );
			}
		}
	}

	class InvalidFormatError extends Error {
		public InvalidFormatError( String s ) {
			super( s );
		}
	}

	class ProgressDialog extends javax.swing.JDialog {
		protected javax.swing.JLabel linesLabel = new javax.swing.JLabel( "Lines read: 0" );
		protected javax.swing.JLabel objectLabel = new javax.swing.JLabel( "Object: <none>" );
		protected javax.swing.JLabel progressLabel = new javax.swing.JLabel( "Loading <none>: " );
		protected javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
		protected javax.swing.JPanel progressPanel = new javax.swing.JPanel();
		protected javax.swing.Timer timer = null;

		public ProgressDialog() {
			super( (java.awt.Frame)null, "Importing...", false );

			progressBar.setMinimum( 0 );
			linesLabel.setAlignmentX( 0.0f );
			objectLabel.setAlignmentX( 0.0f );
			progressPanel.setAlignmentX( 0.0f );

			progressPanel.setLayout( new java.awt.BorderLayout() );
			progressPanel.add( java.awt.BorderLayout.WEST, progressLabel );
			progressPanel.add( java.awt.BorderLayout.CENTER, progressBar );

			getContentPane().setLayout( new javax.swing.BoxLayout( getContentPane(), javax.swing.BoxLayout.Y_AXIS ) );
			getContentPane().add( linesLabel );
			getContentPane().add( objectLabel );
			getContentPane().add( progressPanel );

			setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );

			timer = new javax.swing.Timer( 100,
				new java.awt.event.ActionListener() {
					public void actionPerformed( java.awt.event.ActionEvent ev ) {
						linesLabel.setText( "Lines read: " + ASEImporter.this.tokenizer.lineno() );
						objectLabel.setText( "Object: " + ASEImporter.this.currentObject );
						progressLabel.setText( "Loading " + ASEImporter.this.currentlyLoading + ": " );
						progressBar.setValue( ASEImporter.this.currentProgress );
					}
				}
			);

			pack();
			setVisible( true );
		}

		public void start() {
			timer.start();
		}

		public void stop() {
			timer.stop();
		}

		public void setMax( final int max ) {
			progressBar.setMaximum( max );
		}
	}

	private class Material {
		public String name;
		public edu.cmu.cs.stage3.alice.scenegraph.Color ambient;
		public edu.cmu.cs.stage3.alice.scenegraph.Color diffuse;
		public edu.cmu.cs.stage3.alice.scenegraph.Color specular;
		public double shine;
		public double shinestrength;
		public double transparency;
		public edu.cmu.cs.stage3.alice.core.TextureMap ambientTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap diffuseTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap shineTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap shineStrengthTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap selfIllumTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap opacityTexture = null;
		public edu.cmu.cs.stage3.alice.core.TextureMap bumpTexture = null;
	}

	/*
	private class ASEOptionsDialog extends javax.swing.JDialog {
		javax.swing.JCheckBox useSpecularCheckBox = new javax.swing.JCheckBox( "Use specular highlighting information", useSpecular );
		javax.swing.JCheckBox diffuseToWhiteCheckBox = new javax.swing.JCheckBox( "Set diffuse color to white for textured objects", diffuseToWhite );
		javax.swing.JButton okayButton = new javax.swing.JButton( "Okay" );

		public ASEOptionsDialog() {
			super( (javax.swing.JFrame)null );

			setTitle( "ASE importing options" );
			setModal( true );
			setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );

			java.awt.event.ItemListener checkBoxListener = new java.awt.event.ItemListener() {
				public void itemStateChanged( java.awt.event.ItemEvent ev ) {
					javax.swing.JCheckBox source = (javax.swing.JCheckBox)ev.getSource();
					if( source == useSpecularCheckBox ) {
						ASEImporter.this.useSpecular = useSpecularCheckBox.isSelected();
					} else if( source == diffuseToWhiteCheckBox ) {
						ASEImporter.this.diffuseToWhite = diffuseToWhiteCheckBox.isSelected();
					}
				}
			};
			useSpecularCheckBox.addItemListener( checkBoxListener );
			diffuseToWhiteCheckBox.addItemListener( checkBoxListener );
			useSpecularCheckBox.setAlignmentX( 0.0f );
			diffuseToWhiteCheckBox.setAlignmentX( 0.0f );

			java.awt.event.ActionListener okayListener = new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					hide();
				}
			};
			okayButton.addActionListener( okayListener );
			okayButton.setAlignmentX( 0.5f );

			javax.swing.border.Border padding = javax.swing.BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
			javax.swing.JPanel optionsPanel = new javax.swing.JPanel();
			optionsPanel.setAlignmentX( .5f );
			optionsPanel.setBorder( padding );
			optionsPanel.setLayout( new javax.swing.BoxLayout( optionsPanel, javax.swing.BoxLayout.Y_AXIS ) );
			optionsPanel.add( useSpecularCheckBox );
			optionsPanel.add( diffuseToWhiteCheckBox );
			javax.swing.JPanel mainPanel = new javax.swing.JPanel();
			mainPanel.setBorder( padding );
			mainPanel.setLayout( new javax.swing.BoxLayout( mainPanel, javax.swing.BoxLayout.Y_AXIS ) );
			mainPanel.add( optionsPanel );
			mainPanel.add( javax.swing.Box.createVerticalStrut( 5 ) );
			mainPanel.add( javax.swing.Box.createVerticalGlue() );
			mainPanel.add( okayButton );
			getContentPane().setLayout( new java.awt.BorderLayout() );
			getContentPane().add( mainPanel, java.awt.BorderLayout.CENTER );

			pack();
		}
	}
	*/
}

