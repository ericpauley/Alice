package edu.cmu.cs.stage3.alice.gallery.modeleditor;

import edu.cmu.cs.stage3.alice.core.Pose;
import edu.cmu.cs.stage3.alice.core.reference.PropertyReference;

public class ModelEditor extends javax.swing.JFrame {
	/*
	 * public static void main(String[] args) { ModelEditor modelEditor = new
	 * ModelEditor(); modelEditor.setLocation( 0, 0 ); modelEditor.setSize(
	 * 1280, 1000 );
	 * edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory
	 * drtf = new
	 * edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory();
	 * modelEditor.init( drtf ); if( args.length > 0 ) { modelEditor.onFileOpen(
	 * new java.io.File( args[ 0 ] ) ); } else { modelEditor.onFileOpen(); }
	 * modelEditor.setVisible( true );
	 * 
	 * modelEditor.m_tree.requestFocus(); }
	 */

	private edu.cmu.cs.stage3.alice.core.World m_world;
	private edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera m_camera;
	private edu.cmu.cs.stage3.alice.core.RenderTarget m_renderTarget;
	private edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator m_pivotDecorator;

	private ElementTree m_tree;
	private ElementTreeModel m_treeModel;
	private int m_treeMouseEventModifiers = 0;

	private javax.swing.JButton m_prev;
	private javax.swing.JButton m_next;
	private javax.swing.JTextField m_modeledBy;
	private javax.swing.JTextField m_paintedBy;
	private javax.swing.JButton m_revert;
	private javax.swing.JCheckBox m_forceWireframe;

	private javax.swing.JFileChooser m_fileChooser;
	private java.io.File m_file;
	private boolean m_isDirty;

	public ModelEditor() {
		addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				onFileExit();
			}
		});

		javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

		javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
		fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);

		javax.swing.JMenuItem fileOpenMenuItem = new javax.swing.JMenuItem("Open...");
		fileOpenMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_O);
		fileOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onFileOpen();
			}
		});

		javax.swing.JMenuItem fileSaveMenuItem = new javax.swing.JMenuItem("Save");
		fileSaveMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_S);
		fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onFileSave();
			}
		});

		// javax.swing.JMenuItem fileSaveAsMenuItem = new javax.swing.JMenuItem(
		// "Save As..." );
		// fileSaveAsMenuItem.setMnemonic( java.awt.event.KeyEvent.VK_A );
		// fileSaveAsMenuItem.addActionListener(new
		// java.awt.event.ActionListener() {
		// public void actionPerformed( java.awt.event.ActionEvent e) {
		// onFileSaveAs();
		// }
		// });

		javax.swing.JMenuItem fileExitMenuItem = new javax.swing.JMenuItem("Exit...");
		fileExitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onFileExit();
			}
		});

		javax.swing.JMenu actionMenu = new javax.swing.JMenu("Action");
		actionMenu.setMnemonic(java.awt.event.KeyEvent.VK_A);

		javax.swing.JMenuItem actionNextMenuItem = new javax.swing.JMenuItem("Next");
		actionNextMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_N);
		// actionNextMenuItem.setAccelerator(
		// javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_RIGHT,
		// java.awt.event.ActionEvent.CTRL_MASK ) );
		actionNextMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onNext();
			}
		});

		javax.swing.JMenuItem actionPrevMenuItem = new javax.swing.JMenuItem("Previous");
		actionPrevMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_P);
		// actionPrevMenuItem.setAccelerator(
		// javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_LEFT,
		// java.awt.event.ActionEvent.CTRL_MASK ) );
		actionPrevMenuItem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onPrev();
			}
		});

		setJMenuBar(menuBar);

		menuBar.add(fileMenu);
		fileMenu.add(fileOpenMenuItem);
		fileMenu.add(fileSaveMenuItem);
		// fileMenu.add( fileSaveAsMenuItem );
		fileMenu.add(fileExitMenuItem);

		menuBar.add(actionMenu);
		actionMenu.add(actionNextMenuItem);
		actionMenu.add(actionPrevMenuItem);

		m_treeModel = new ElementTreeModel();
		m_treeModel.setRoot(new edu.cmu.cs.stage3.alice.core.Model() {
		});

		m_tree = new ElementTree(m_treeModel);
		final ElementTreeCellRenderer cellRenderer = new ElementTreeCellRenderer();
		final ElementTreeCellEditor cellEditor = new ElementTreeCellEditor(m_tree, cellRenderer);
		m_tree.setCellRenderer(cellRenderer);
		m_tree.setCellEditor(cellEditor);
		m_tree.setEditable(true);
		m_tree.setScrollsOnExpand(true);

		javax.swing.ToolTipManager toolTipManager = javax.swing.ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(m_tree);
		toolTipManager.setLightWeightPopupEnabled(false);

		cellEditor.addCellEditorListener(new javax.swing.event.CellEditorListener() {
			@Override
			public void editingStopped(javax.swing.event.ChangeEvent e) {
				edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element) m_tree.getLastSelectedPathComponent();
				String nameValue = (String) cellEditor.getCellEditorValue();
				if (element.name.getStringValue().equals(nameValue)) {
					// pass
				} else {
					element.name.set(nameValue);
					setIsDirty(true);
				}
			}
			@Override
			public void editingCanceled(javax.swing.event.ChangeEvent e) {
			}
		});

		m_tree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
			@Override
			public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
				javax.swing.tree.TreePath treePath = e.getPath();
				onSelect((edu.cmu.cs.stage3.alice.core.Element) m_tree.getLastSelectedPathComponent());
			}
		});

		m_tree.addMouseListener(new java.awt.event.MouseAdapter() {
			private javax.swing.JPopupMenu m_popupMenu;
			private edu.cmu.cs.stage3.alice.core.Element m_element;
			private void handlePopup(java.awt.event.MouseEvent e) {
				if (m_popupMenu == null) {
					m_popupMenu = new javax.swing.JPopupMenu();
					javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem("Delete");
					menuItem.addActionListener(new java.awt.event.ActionListener() {
						@Override
						public void actionPerformed(java.awt.event.ActionEvent e) {
							edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = m_world.getPropertyReferencesTo(m_element);
							if (propertyReferences.length > 0) {
								StringBuffer sb = new StringBuffer();
								sb.append("Cannot delete " + m_element.getTrimmedKey() + ".  The following properties reference it:\n");
								for (PropertyReference propertyReference : propertyReferences) {
									edu.cmu.cs.stage3.alice.core.Property property = propertyReference.getProperty();
									sb.append("    ");
									sb.append(property.getOwner().getTrimmedKey());
									sb.append('[');
									sb.append(property.getName());
									sb.append("] = ");
									Object value = property.getValue();
									if (value instanceof edu.cmu.cs.stage3.alice.core.Element) {
										sb.append(((edu.cmu.cs.stage3.alice.core.Element) value).getTrimmedKey());
									} else {
										sb.append(value);
									}
									sb.append('\n');
								}
								javax.swing.JOptionPane.showMessageDialog(ModelEditor.this, sb.toString());
							} else {
								int result = javax.swing.JOptionPane.showConfirmDialog(ModelEditor.this, "Would you like to delete: " + m_element.getTrimmedKey());
								if (result == javax.swing.JOptionPane.YES_OPTION) {
									m_treeModel.removeDescendant(m_element);
									setIsDirty(true);
								}
							}
							m_element = null;
						}
					});
					m_popupMenu.add(menuItem);
				}
				javax.swing.tree.TreePath path = m_tree.getClosestPathForLocation(e.getX(), e.getY());
				m_element = (edu.cmu.cs.stage3.alice.core.Element) path.getLastPathComponent();
				if (m_element != null) {
					m_popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					handlePopup(e);
				}
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				m_treeMouseEventModifiers = e.getModifiers();
				if (e.isPopupTrigger()) {
					handlePopup(e);
				}
				onSelect((edu.cmu.cs.stage3.alice.core.Element) m_tree.getLastSelectedPathComponent());
			}
		});

		m_tree.setShowsRootHandles(true);
		m_tree.setToggleClickCount(0);

		m_prev = new javax.swing.JButton();
		m_prev.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onPrev();
			}
		});
		m_next = new javax.swing.JButton();
		m_next.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onNext();
			}
		});

		m_modeledBy = new javax.swing.JTextField();
		m_modeledBy.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				onModeledByChange();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				onModeledByChange();
			}
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				onModeledByChange();
			}
		});
		m_paintedBy = new javax.swing.JTextField();
		m_paintedBy.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				onPaintedByChange();
			}
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				onPaintedByChange();
			}
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				onPaintedByChange();
			}
		});

		m_revert = new javax.swing.JButton("Revert");
		m_revert.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onRevert();
			}
		});
		m_revert.setEnabled(false);

		m_forceWireframe = new javax.swing.JCheckBox("Force Wireframe");
		m_forceWireframe.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				onForceWireframe(m_forceWireframe.isSelected());
			}
		});

		m_fileChooser = new javax.swing.JFileChooser();
	}
	public void init(edu.cmu.cs.stage3.alice.scenegraph.renderer.RenderTargetFactory renderTargetFactory) {
		m_world = new edu.cmu.cs.stage3.alice.core.World();
		m_world.atmosphereColor.set(new edu.cmu.cs.stage3.alice.scenegraph.Color(0.75, 0.75, 1));
		m_world.ambientLightBrightness.set(new Double(0.2));

		m_renderTarget = new edu.cmu.cs.stage3.alice.core.RenderTarget();
		m_renderTarget.commit(renderTargetFactory);
		class CameraOrbiter implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
			private int m_prevX;
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				final int x = e.getX();
				final int y = e.getY();
				new Thread() {

					@Override
					public void run() {
						edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = m_renderTarget.pick(x, y, false, true);
						if (pickInfo.getCount() > 0) {
							System.err.println(pickInfo.getVisualAt(0).getBonus());
						} else {
							System.err.println("null");
						}
					}
					// }.start();
				}.run();
			}
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				m_prevX = e.getX();
			}
			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
			}
			@Override
			public void mouseDragged(java.awt.event.MouseEvent e) {
				int x = e.getX();
				m_camera.rotateRightNow(edu.cmu.cs.stage3.math.MathUtilities.getYAxis(), 0.001 * (x - m_prevX), getModel());
				m_prevX = x;
			}
		};
		CameraOrbiter cameraOrbiter = new CameraOrbiter();
		m_renderTarget.addMouseListener(cameraOrbiter);
		m_renderTarget.addMouseMotionListener(cameraOrbiter);

		edu.cmu.cs.stage3.alice.core.light.DirectionalLight sun = new edu.cmu.cs.stage3.alice.core.light.DirectionalLight();
		sun.vehicle.set(m_world);
		m_world.addChild(sun);
		sun.setOrientationRightNow(0, -1, 0, 0, 0, 1);

		m_camera = new edu.cmu.cs.stage3.alice.core.camera.SymmetricPerspectiveCamera();
		m_camera.verticalViewingAngle.set(new Double(0.5));
		m_camera.vehicle.set(m_world);
		m_camera.renderTarget.set(m_renderTarget);
		m_camera.name.set("Camera");
		m_world.addChild(m_camera);

		edu.cmu.cs.stage3.alice.core.Model ground = new edu.cmu.cs.stage3.alice.core.Model();
		ground.name.set("Ground");
		m_world.addChild(ground);

		m_pivotDecorator = new edu.cmu.cs.stage3.alice.core.decorator.PivotDecorator();

		javax.swing.JPanel westPanel = new javax.swing.JPanel();

		westPanel.setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();

		gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gbc.fill = java.awt.GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;

		westPanel.add(m_prev, gbc);
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		westPanel.add(m_next, gbc);

		gbc.weighty = 1.0;
		javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(m_tree, javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		westPanel.add(scrollPane, gbc);

		gbc.weighty = 0.0;

		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		westPanel.add(new javax.swing.JLabel("modeled by: "), gbc);
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		westPanel.add(m_modeledBy, gbc);
		gbc.gridwidth = java.awt.GridBagConstraints.RELATIVE;
		westPanel.add(new javax.swing.JLabel("painted by: "), gbc);
		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		westPanel.add(m_paintedBy, gbc);

		gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		westPanel.add(m_revert, gbc);
		westPanel.add(m_forceWireframe, gbc);

		javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, westPanel, m_renderTarget.getAWTComponent());
		getContentPane().setLayout(new java.awt.GridLayout(1, 1));
		getContentPane().add(splitPane);
	}

	public void setIsDirty(boolean isDirty) {
		if (m_isDirty != isDirty) {
			m_isDirty = isDirty;
			m_revert.setEnabled(m_isDirty);
			updateTitle();
		}
	}
	public edu.cmu.cs.stage3.alice.core.Model getModel() {
		return (edu.cmu.cs.stage3.alice.core.Model) m_treeModel.getRoot();
	}
	private void expandTree() {
		for (int i = 0; i < m_tree.getRowCount(); i++) {
			m_tree.expandRow(i);
		}
		m_tree.invalidate();
	}
	private void releasePreviousModel() {
		edu.cmu.cs.stage3.alice.core.Model prevModel = getModel();
		if (prevModel != null) {
			// prevModel.release();
			// System.err.println( prevModel.vehicle.get() );
			// System.err.println( prevModel.getParent() );
			prevModel.vehicle.set(null);
			prevModel.setParent(null);
		}
	}
	public void setModel(edu.cmu.cs.stage3.alice.core.Model model) {
		m_treeMouseEventModifiers = 0;
		m_treeModel.setRoot(model);
		expandTree();
		m_tree.setSelectionInterval(0, 0);
		m_tree.requestFocus();
		if (model != null) {
			model.setParent(m_world);
			// try {
			// edu.cmu.cs.stage3.alice.scenegraph.io.OBJ.store( new
			// java.io.FileOutputStream( "c:/fighter.obj" ),
			// model.getSceneGraphTransformable() );
			// } catch( Throwable t ) {
			// t.printStackTrace();
			// }
			model.vehicle.set(m_world);
			m_camera.getAGoodLookAtRightNow(model);
			edu.cmu.cs.stage3.math.Sphere sphere = model.getBoundingSphere();
			double radius = sphere.getRadius();
			m_camera.nearClippingPlaneDistance.set(new Double(0.1));
			m_camera.farClippingPlaneDistance.set(new Double(radius * 2 + m_camera.getDistanceTo(model)));
			m_camera.moveRightNow(0, 0, -m_camera.nearClippingPlaneDistance.doubleValue());
			String modeledBy = (String) model.data.get("modeled by");
			if (modeledBy != null) {
				m_modeledBy.setText(modeledBy);
			} else {
				m_modeledBy.setText("");
			}
			String paintedBy = (String) model.data.get("painted by");
			if (paintedBy != null) {
				m_paintedBy.setText(paintedBy);
			} else {
				m_paintedBy.setText("");
			}
			onForceWireframe(m_forceWireframe.isSelected());
		} else {
			m_modeledBy.setText("");
			m_paintedBy.setText("");
		}
	}

	private boolean isContinueAppropriateAfterCheckingForSave() {
		if (m_isDirty) {
			int option = javax.swing.JOptionPane.showConfirmDialog(this, "Changes have been made.  Would you like to save before continuing?", "Check for save", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);
			switch (option) {
				case javax.swing.JOptionPane.YES_OPTION :
					onFileSave();
					return true;
				case javax.swing.JOptionPane.NO_OPTION :
					return true;
				case javax.swing.JOptionPane.CANCEL_OPTION :
				case javax.swing.JOptionPane.CLOSED_OPTION :
					return false;
				default :
					throw new Error();
			}
		}
		return true;
	}
	private void updateTitle() {
		StringBuffer sb = new StringBuffer("Model editor: ");
		if (m_isDirty) {
			sb.append("*");
		}
		sb.append(m_file.getPath());
		setTitle(sb.toString());
	}
	private java.io.File[] getSiblingFiles() {
		java.io.File directory = m_file.getParentFile();
		return directory.listFiles(new java.io.FilenameFilter() {
			@Override
			public boolean accept(java.io.File dir, String name) {
				return name.endsWith(".a2c");
			}
		});
	}
	private void setFile(java.io.File file) {
		m_file = file;
		updateTitle();
		java.io.File[] siblingFiles = getSiblingFiles();
		int n = siblingFiles.length;
		for (int i = 0; i < n; i++) {
			if (siblingFiles[i].equals(m_file)) {
				if (i == 0) {
					m_prev.setEnabled(false);
					m_prev.setText("<< { None }");
				} else {
					m_prev.setEnabled(true);
					m_prev.setText("<< " + siblingFiles[i - 1].getName());
					m_prev.setActionCommand(siblingFiles[i - 1].getPath());
				}
				if (i == n - 1) {
					m_next.setEnabled(false);
					m_next.setText("{ None } >>");
				} else {
					m_next.setEnabled(true);
					m_next.setText(siblingFiles[i + 1].getName() + " >>");
					m_next.setActionCommand(siblingFiles[i + 1].getPath());
				}
			}
		}
	}
	private void onPrev() {
		if (isContinueAppropriateAfterCheckingForSave()) {
			open(new java.io.File(m_prev.getActionCommand()));
		}
	}
	private void onNext() {
		if (isContinueAppropriateAfterCheckingForSave()) {
			open(new java.io.File(m_next.getActionCommand()));
		}
	}
	private void onRevert() {
		open(m_file);
	}

	private void onModeledByChange() {
		getModel().data.put("modeled by", m_modeledBy.getText());
		setIsDirty(true);
	}
	private void onPaintedByChange() {
		getModel().data.put("painted by", m_paintedBy.getText());
		setIsDirty(true);
	}

	private void negativeAppearance(edu.cmu.cs.stage3.alice.core.Model model) {
		edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
		// sgAppearance.setFillingStyle(
		// edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME );
		sgAppearance.setShadingStyle(edu.cmu.cs.stage3.alice.scenegraph.ShadingStyle.SMOOTH);
		sgAppearance.setOpacity(0.25);
		for (int i = 0; i < model.parts.size(); i++) {
			negativeAppearance((edu.cmu.cs.stage3.alice.core.Model) model.parts.get(i));
		}
	}
	private void positiveAppearance(edu.cmu.cs.stage3.alice.core.Model model) {
		edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
		edu.cmu.cs.stage3.alice.scenegraph.TextureMap sgTextureMap = null;
		if (model.diffuseColorMap.getTextureMapValue() != null) {
			sgTextureMap = model.diffuseColorMap.getTextureMapValue().getSceneGraphTextureMap();
		}
		sgAppearance.setDiffuseColorMap(sgTextureMap);
		// sgAppearance.setFillingStyle(
		// model.fillingStyle.getFillingStyleValue() );
		sgAppearance.setShadingStyle(model.shadingStyle.getShadingStyleValue());
		sgAppearance.setOpacity(model.opacity.doubleValue());
		for (int i = 0; i < model.parts.size(); i++) {
			positiveAppearance((edu.cmu.cs.stage3.alice.core.Model) model.parts.get(i));
		}
	}

	private void textureMapAppearance(edu.cmu.cs.stage3.alice.core.Model model, edu.cmu.cs.stage3.alice.core.TextureMap textureMap) {
		edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
		if ((m_treeMouseEventModifiers & java.awt.event.InputEvent.CTRL_MASK) != 0) {
			sgAppearance.setDiffuseColorMap(textureMap.getSceneGraphTextureMap());
		} else {
			if (model.diffuseColorMap.get() == textureMap) {
				// pass
			} else {
				sgAppearance.setDiffuseColorMap(null);
			}
		}
		for (int i = 0; i < model.parts.size(); i++) {
			textureMapAppearance((edu.cmu.cs.stage3.alice.core.Model) model.parts.get(i), textureMap);
		}
	}
	private void onSelect(edu.cmu.cs.stage3.alice.core.Element element) {
		positiveAppearance(getModel());
		if (element instanceof edu.cmu.cs.stage3.alice.core.Model) {
			if (getModel() != element) {
				negativeAppearance(getModel());
				positiveAppearance((edu.cmu.cs.stage3.alice.core.Model) element);
			}
			m_pivotDecorator.setTransformable((edu.cmu.cs.stage3.alice.core.Model) element);
		} else if (element instanceof edu.cmu.cs.stage3.alice.core.TextureMap) {
			textureMapAppearance(getModel(), (edu.cmu.cs.stage3.alice.core.TextureMap) element);
		}
		m_pivotDecorator.setIsShowing(element instanceof edu.cmu.cs.stage3.alice.core.Model);
	}

	private void onForceWireframe(edu.cmu.cs.stage3.alice.core.Model model, boolean forceWireframe) {
		edu.cmu.cs.stage3.alice.scenegraph.Appearance sgAppearance = model.getSceneGraphAppearance();
		edu.cmu.cs.stage3.alice.scenegraph.FillingStyle fillingStyle;
		if (forceWireframe) {
			fillingStyle = edu.cmu.cs.stage3.alice.scenegraph.FillingStyle.WIREFRAME;
		} else {
			fillingStyle = model.fillingStyle.getFillingStyleValue();
		}
		sgAppearance.setFillingStyle(fillingStyle);
		for (int i = 0; i < model.parts.size(); i++) {
			onForceWireframe((edu.cmu.cs.stage3.alice.core.Model) model.parts.get(i), forceWireframe);
		}
	}
	private void onForceWireframe(boolean forceWireframe) {
		onForceWireframe(getModel(), forceWireframe);
	}

	private void open(java.io.File file) {
		setFile(file);
		try {
			releasePreviousModel();
			edu.cmu.cs.stage3.alice.core.Model model = (edu.cmu.cs.stage3.alice.core.Model) edu.cmu.cs.stage3.alice.core.Element.load(file, m_world);
			setModel(model);
			hardenPoses();
			setIsDirty(false);
		} catch (edu.cmu.cs.stage3.alice.core.UnresolvablePropertyReferencesException upre) {
			edu.cmu.cs.stage3.alice.core.reference.PropertyReference[] propertyReferences = upre.getPropertyReferences();
			for (PropertyReference propertyReference : propertyReferences) {
				System.err.println(propertyReference);
			}
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
	}
	private void onFileOpen() {
		onFileOpen(null);
	}
	private void onFileOpen(java.io.File file) {
		if (file == null) {
			if (m_file != null) {
				m_fileChooser.setCurrentDirectory(m_file.getParentFile());
			}
		} else {
			if (file.isDirectory()) {
				m_fileChooser.setCurrentDirectory(file);
			} else {
				open(file);
				return;
			}
		}
		m_fileChooser.setDialogType(javax.swing.JFileChooser.OPEN_DIALOG);
		m_fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		m_fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {

			@Override
			public boolean accept(java.io.File file) {
				return file.isDirectory() || file.getName().endsWith(".a2c");
			}

			@Override
			public String getDescription() {
				return "Alice Character (*.a2c)";
			}
		});

		m_fileChooser.setPreferredSize(new java.awt.Dimension(500, 300));
		m_fileChooser.rescanCurrentDirectory();
		if (m_fileChooser.showDialog(this, null) == javax.swing.JFileChooser.APPROVE_OPTION) {
			open(m_fileChooser.getSelectedFile());
		}
		m_tree.requestFocus();
	}

	private void onFileSave() {
		try {
			softenPoses();
			getModel().store(m_file);
			setIsDirty(false);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// private void onFileSaveAs() {
	// }

	private void onFileExit() {
		if (isContinueAppropriateAfterCheckingForSave()) {
			System.exit(0);
		}
	}

	private void hardenPoses() {
		edu.cmu.cs.stage3.alice.core.Pose[] poses = (edu.cmu.cs.stage3.alice.core.Pose[]) getModel().getDescendants(edu.cmu.cs.stage3.alice.core.Pose.class);
		for (Pose pose : poses) {
			pose.HACK_harden();
		}
	}
	private void softenPoses() {
		edu.cmu.cs.stage3.alice.core.Pose[] poses = (edu.cmu.cs.stage3.alice.core.Pose[]) getModel().getDescendants(edu.cmu.cs.stage3.alice.core.Pose.class);
		for (Pose pose : poses) {
			pose.HACK_soften();
		}
	}

}
