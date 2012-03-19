package edu.cmu.cs.stage3.alice.core.visualization;

import edu.cmu.cs.stage3.alice.core.Collection;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.TextureMap;
import edu.cmu.cs.stage3.alice.core.Variable;

public abstract class CollectionOfModelsVisualization extends edu.cmu.cs.stage3.alice.core.Visualization {
	private java.util.Vector m_bins = new java.util.Vector();

	@Override
	public void unhook(Model model) {
		int i = indexOf(model, 0);
		if (i != -1) {
			set(i, null);
		}
	}

	protected String getItemsName() {
		return "items";
	}
	private Variable m_itemsVariable = null;
	private Variable getItemsVariable() {
		if (m_itemsVariable == null) {
			m_itemsVariable = (Variable) getChildNamed(getItemsName());
		}
		return m_itemsVariable;
	}
	public Collection getItemsCollection() {
		return (Collection) getItemsVariable().value.getValue();
	}
	public Model[] getItems() {
		return (Model[]) getItemsCollection().values.getArrayValue();
	}
	public void setItems(Model[] items) {
		getItemsCollection().values.set(items);
	}

	private Model getPrototype() {
		return (Model) getChildNamed("BinPrototype");
	}
	private int getBinCount() {
		return m_bins.size();
	}
	private Model getBinAt(int i) {
		return (Model) m_bins.get(i);
	}
	private void setBinAt(int i, Model bin) {
		if (m_bins.size() == i) {
			m_bins.addElement(bin);
		} else {
			if (m_bins.size() < i) {
				m_bins.ensureCapacity(i + 1);
			}
			m_bins.set(i, bin);
		}
	}

	private static final java.awt.Font s_font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 32);
	private static TextureMap getEmptyTextureMap(Model bin) {
		return (TextureMap) bin.getChildNamed("EmptyTexture");
	}
	private static TextureMap getFilledTextureMap(Model bin) {
		return (TextureMap) bin.getChildNamed("FilledTexture");
	}

	private static void decorateTextureMap(TextureMap skin, int i) {
		if (skin != null) {
			java.awt.Image originalImage = skin.image.getImageValue();
			if (originalImage instanceof java.awt.image.BufferedImage) {
				java.awt.image.BufferedImage originalBufferedImage = (java.awt.image.BufferedImage) originalImage;
				java.awt.Image image = new java.awt.image.BufferedImage(originalBufferedImage.getWidth(), originalBufferedImage.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
				java.awt.Graphics g = image.getGraphics();
				g.drawImage(originalImage, 0, 0, null);
				g.setFont(s_font);
				String s = Integer.toString(i);
				java.awt.FontMetrics fm = g.getFontMetrics();
				java.awt.geom.Rectangle2D r = fm.getStringBounds(s, g);
				g.setColor(java.awt.Color.black);
				g.drawString(s, 80, (int) (20 - r.getX() + r.getHeight()));
				g.dispose();
				skin.image.set(image);
				skin.touchImage();
			}
		}
	}

	private void synchronize(Model[] curr) {
		int binCount = getBinCount();
		for (int i = binCount - 1; i >= curr.length; i--) {
			Model binI = getBinAt(i);
			binI.vehicle.set(null);
			// binI.removeFromParent();
			m_bins.remove(binI);
		}
		Model prototype = getPrototype();
		if (prototype != null) {
			for (int i = binCount; i < curr.length; i++) {
				Class[] share = {edu.cmu.cs.stage3.alice.core.Geometry.class};
				String name = "Sub" + i;
				Model binI = (Model) getChildNamed(name);
				if (binI == null) {
					binI = (Model) prototype.HACK_createCopy(name, this, -1, share, null);
					decorateTextureMap(getEmptyTextureMap(binI), i);
					decorateTextureMap(getFilledTextureMap(binI), i);
				}
				setBinAt(i, binI);
			}
			binCount = getBinCount();
			for (int i = 0; i < binCount; i++) {
				Model binI = getBinAt(i);
				binI.vehicle.set(this);
				binI.setPositionRightNow(-(prototype.getWidth() * i), 0, 0);
				if (curr[i] != null) {
					curr[i].vehicle.set(binI);
					curr[i].visualization.set(this);
					curr[i].setTransformationRightNow(getTransformationFor(curr[i], i), this);
					binI.diffuseColorMap.set(getFilledTextureMap(binI));
				} else {
					binI.diffuseColorMap.set(getEmptyTextureMap(binI));
				}
			}
			Model rightBracket = (Model) getChildNamed("RightBracket");
			if (rightBracket != null) {
				rightBracket.setPositionRightNow(-(prototype.getWidth() * (binCount - 0.5)), 0, 0);
			}
		}
	}

	public Model get(int i) {
		return (Model) getItemsCollection().values.get(i);
	}
	public void set(int i, Model model) {
		getItemsCollection().values.set(i, model);
	}
	public int indexOf(Model model, int from) {
		return getItemsCollection().values.indexOf(model, from);
	}
	public int lastIndexOf(Model model, int from) {
		return getItemsCollection().values.lastIndexOf(model, from);
	}
	public boolean contains(Model model) {
		return getItemsCollection().values.contains(model);
	}
	public int size() {
		return getItemsCollection().values.size();
	}
	public boolean isEmpty() {
		return getItemsCollection().values.isEmpty();
	}

	@Override
	protected void loadCompleted() {
		super.loadCompleted();
		Collection collection = getItemsCollection();
		if (collection != null) {
			collection.values.addPropertyListener(new edu.cmu.cs.stage3.alice.core.event.PropertyListener() {
				@Override
				public void propertyChanging(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
				}
				@Override
				public void propertyChanged(edu.cmu.cs.stage3.alice.core.event.PropertyEvent propertyEvent) {
					CollectionOfModelsVisualization.this.synchronize((Model[]) propertyEvent.getValue());
				}
			});
			synchronize(getItems());
		} else {
			System.err.println("WARNING: collection is null " + this);
		}
	}
	public javax.vecmath.Matrix4d getTransformationFor(edu.cmu.cs.stage3.alice.core.Model model, int i) {
		Model prototype = getPrototype();

		javax.vecmath.Matrix4d m = new javax.vecmath.Matrix4d();
		m.setIdentity();
		if (model != null) {
			edu.cmu.cs.stage3.math.Box box = model.getBoundingBox();
			javax.vecmath.Vector3d v = box.getCenterOfBottomFace();
			if (v != null) {
				v.negate();
				m.m30 = v.x;
				m.m31 = v.y;
				m.m32 = v.z;
			}
		}
		m.m30 -= prototype.getWidth() * i;
		return m;
	}
}
