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

package edu.cmu.cs.stage3.alice.authoringtool.importers.mocap;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Stage3</p>
 * @author Ben Buchwald
 * @version 1.0
 */

import java.io.InputStream;
import java.io.StreamTokenizer;
import java.util.Map;

import edu.cmu.cs.stage3.alice.authoringtool.AbstractImporter;
import edu.cmu.cs.stage3.alice.core.Element;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.io.EStreamTokenizer;

public class ASFImporter extends AbstractImporter {
	protected ASFSkeleton skel;
	private static final edu.cmu.cs.stage3.alice.core.World scene = new edu.cmu.cs.stage3.alice.core.World();
	// protected Elmenent applyTo = null;

	public ASFImporter() {
		skel = new ASFSkeleton();
	}

	@Override
	public Map getExtensionMap() {
		java.util.HashMap map = new java.util.HashMap();
		map.put("ASF", "Acclaim Skeleton File");
		return map;
	}

	private Model parseASF(InputStream is) throws java.io.IOException {

		EStreamTokenizer tokenizer;

		// to convert to rotations and meters
		skel.anglescale = 1.0;
		skel.lengthscale = .0254;

		ASFBone bone;

		// load a skeleton
		edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream bcfis = new edu.cmu.cs.stage3.alice.authoringtool.util.BackslashConverterFilterInputStream(is);
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(bcfis));
		tokenizer = new EStreamTokenizer(br);

		tokenizer.commentChar('#');
		tokenizer.eolIsSignificant(true);
		tokenizer.lowerCaseMode(true);
		tokenizer.parseNumbers();
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars(':', ':');

		// find the units section or root section if there is no unit section
		// (assume default units)
		while (tokenizer.ttype != StreamTokenizer.TT_WORD || !(tokenizer.sval.equals(":units") || tokenizer.sval.equals(":root"))) {
			tokenizer.nextToken();
		}

		// if there is a units section...
		if (tokenizer.sval.equals(":units")) {
			tokenizer.nextToken();
			// **************************************
			// ** Parse the :units section **
			// **************************************
			// System.out.println("Parsing :units ...");
			while (tokenizer.ttype != StreamTokenizer.TT_WORD || tokenizer.sval.charAt(0) != ':') {
				if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
					tokenizer.nextToken();
					continue;
				}
				// adjust the length conversion
				if (tokenizer.sval.equals("length")) {
					tokenizer.nextToken();
					skel.lengthscale /= tokenizer.nval;
					// adjust the angle conversion
				} else if (tokenizer.sval.equals("angle")) {
					tokenizer.nextToken();
					if (tokenizer.sval.equals("deg")) {
						skel.anglescale = Math.PI / 180; // 1/360;
					}
				}
				tokenizer.nextToken();
			}

			// now find the root section
			while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals(":root")) {
				tokenizer.nextToken();
			}
			tokenizer.nextToken();
		}
		// **************************************
		// ** Parse the :root section **
		// **************************************
		// System.out.println("Parsing :root ...");

		skel.bones.addElement(new ASFBone("root"));
		skel.bones_dict.put("root", skel.bones.lastElement());
		bone = skel.getRoot();

		while (tokenizer.ttype != StreamTokenizer.TT_WORD || tokenizer.sval.charAt(0) != ':') {
			if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
				tokenizer.nextToken();
				continue;
			}

			if (tokenizer.sval.equals("position")) {
				tokenizer.nextToken();
				bone.base_position.x = tokenizer.nval * skel.lengthscale;
				tokenizer.nextToken();
				bone.base_position.y = tokenizer.nval * skel.lengthscale;
				tokenizer.nextToken();
				bone.base_position.z = -tokenizer.nval * skel.lengthscale;
			} else if (tokenizer.sval.equals("orientation")) {
				tokenizer.nextToken();
				bone.base_axis.rotateX(-tokenizer.nval * skel.anglescale);
				tokenizer.nextToken();
				bone.base_axis.rotateY(-tokenizer.nval * skel.anglescale);
				tokenizer.nextToken();
				bone.base_axis.rotateZ(tokenizer.nval * skel.anglescale);
			} else if (tokenizer.sval.equals("order")) {
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
					if (tokenizer.sval.equals("tx")) {
						bone.dof.addElement(ASFBone.DOF_TX);
					} else if (tokenizer.sval.equals("ty")) {
						bone.dof.addElement(ASFBone.DOF_TY);
					} else if (tokenizer.sval.equals("tz")) {
						bone.dof.addElement(ASFBone.DOF_TZ);
					} else if (tokenizer.sval.equals("rx")) {
						bone.dof.addElement(ASFBone.DOF_RX);
					} else if (tokenizer.sval.equals("ry")) {
						bone.dof.addElement(ASFBone.DOF_RY);
					} else if (tokenizer.sval.equals("rz")) {
						bone.dof.addElement(ASFBone.DOF_RZ);
					}
				}
			}
			tokenizer.nextToken();
		}

		// find the :bonedata section
		while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals(":bonedata")) {
			tokenizer.nextToken();
		}
		tokenizer.nextToken();

		// **************************************
		// ** Parse the :bonedata section **
		// **************************************
		// System.out.println("Parsing :bonedata ...");

		while (tokenizer.ttype != StreamTokenizer.TT_WORD || tokenizer.sval.charAt(0) != ':') {
			// find a bone
			while (tokenizer.ttype != StreamTokenizer.TT_WORD || tokenizer.sval.charAt(0) != ':' && !tokenizer.sval.equals("begin")) {
				tokenizer.nextToken();
			}

			if (tokenizer.sval.charAt(0) == ':') {
				break;
			}
			tokenizer.nextToken();

			// System.out.print("Parsing Bone ");
			// parse the bone
			bone = new ASFBone();

			while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("end")) {
				if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
					tokenizer.nextToken();
					continue;
				}

				if (tokenizer.sval.equals("axis")) {
					tokenizer.nextToken();
					bone.base_axis.rotateX(-tokenizer.nval * skel.anglescale);
					tokenizer.nextToken();
					bone.base_axis.rotateY(-tokenizer.nval * skel.anglescale);
					tokenizer.nextToken();
					bone.base_axis.rotateZ(tokenizer.nval * skel.anglescale);
				} else if (tokenizer.sval.equals("direction")) {
					tokenizer.nextToken();
					bone.direction.x = tokenizer.nval;
					tokenizer.nextToken();
					bone.direction.y = tokenizer.nval;
					tokenizer.nextToken();
					bone.direction.z = -tokenizer.nval;
				} else if (tokenizer.sval.equals("length")) {
					tokenizer.nextToken();
					bone.length = tokenizer.nval * skel.lengthscale;
				} else if (tokenizer.sval.equals("name")) {
					tokenizer.nextToken();
					bone.name = tokenizer.sval;
					// System.out.println(bone.name);
				} else if (tokenizer.sval.equals("dof")) {
					while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
						if (tokenizer.sval.equals("tx")) {
							bone.dof.addElement(ASFBone.DOF_TX);
						} else if (tokenizer.sval.equals("ty")) {
							bone.dof.addElement(ASFBone.DOF_TY);
						} else if (tokenizer.sval.equals("tz")) {
							bone.dof.addElement(ASFBone.DOF_TZ);
						} else if (tokenizer.sval.equals("rx")) {
							bone.dof.addElement(ASFBone.DOF_RX);
						} else if (tokenizer.sval.equals("ry")) {
							bone.dof.addElement(ASFBone.DOF_RY);
						} else if (tokenizer.sval.equals("rz")) {
							bone.dof.addElement(ASFBone.DOF_RZ);
						} else if (tokenizer.sval.equals("l")) {
							bone.dof.addElement(ASFBone.DOF_L);
						}
					}
				}
				tokenizer.nextToken();

			}
			tokenizer.nextToken();

			skel.bones.addElement(bone);
			skel.bones_dict.put(bone.name, bone);
		}

		// find the :hierarchy section
		while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals(":hierarchy")) {
			tokenizer.nextToken();
		}
		tokenizer.nextToken();

		while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("begin")) {
			tokenizer.nextToken();
		}
		tokenizer.nextToken();

		// **************************************
		// ** Parse the :hierarchy section **
		// **************************************

		while (tokenizer.ttype != StreamTokenizer.TT_WORD || !tokenizer.sval.equals("end")) {
			if (tokenizer.ttype != StreamTokenizer.TT_WORD) {
				tokenizer.nextToken();
				continue;
			}
			bone = (ASFBone) skel.bones_dict.get(tokenizer.sval);

			while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
				bone.children.addElement(skel.bones_dict.get(tokenizer.sval));
			}
		}

		return skel.buildBones();

	}

	@Override
	protected Element load(InputStream is, String ext) throws java.io.IOException {
		return parseASF(is);
	}

	public ASFSkeleton loadSkeleton(InputStream is) throws java.io.IOException {
		parseASF(is);
		return skel;
	}

	public ASFSkeleton getSkeleton() {
		return skel;
	}
}