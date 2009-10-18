/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewPanel}.
 * 
 * @author Julien Perret
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class LayerRenderer implements Renderer {
	static Logger logger=Logger.getLogger(LayerRenderer.class.getName());

	Layer layer = null;
	LayerViewPanel layerViewPanel = null;
	FT_FeatureCollection<? extends FT_Feature> features = null;
	protected volatile boolean cancelled = false;
	protected volatile boolean rendering = false;
	protected volatile boolean rendered = false;
	public boolean isRendered() {return this.rendered;}

	BufferedImage image = null;

	/**
	 * Constructor of renderer using a {@link Layer} and a {@link LayerViewPanel}
	 * @param theLayer a layer to render
	 * @param theFeatures the feature to render
	 * @param theLayerViewPanel the panel to draws into
	 */
	public LayerRenderer(Layer theLayer, FT_FeatureCollection<? extends FT_Feature> theFeatures, LayerViewPanel theLayerViewPanel) {
		this.layer=theLayer;
		this.layerViewPanel=theLayerViewPanel;
		this.features = theFeatures;
	}

	/**
	 * True is the renderer is running, i.e. if its associated runnable is running, false otherwise.
	 * @return true is the renderer is running, false otherwise
	 * @see #createRunnable()
	 * @see #renderHook(BufferedImage,GM_Envelope)
	 */
	public boolean isRendering() {return rendering;}

	/**
	 * Cancel the rendering. This method does not actually interrupt the thread but lets the thread know it should stop.
	 * @see Runnable
	 * @see Thread
	 */
	public void cancel() {cancelled = true;}

	/**
	 * Copy the rendered image the a 2D graphics
	 * @param graphics the 2D graphics to draw into
	 */
	public void copyTo(Graphics2D graphics) {if (image != null) graphics.drawImage(image, 0, 0, null);}

	/**
	 * Create a runnable for the renderer. A renderer create a new image to draw into. 
	 * If cancel() is called, the rendering stops as soon as possible. 
	 * When finished, set the variable rendering to false.
	 * @return a new runnable
	 * @see Runnable
	 * @see #cancel()
	 * @see #isRendering()
	 */
	public Runnable createRunnable() {
		if (image != null) {return null;}
		cancelled = false;
		return new Runnable() {
			public void run() {
				try {
					rendering = true;
					rendered = false;
					// it the rendering is cancel, stop
					if (cancelled) return;
					// if either the width or the height of the panel is lesser or equal to 0, stop
					if (Math.min(layerViewPanel.getWidth(),layerViewPanel.getHeight())<=0) return;
					// create a new image
					image = new BufferedImage(layerViewPanel.getWidth(),layerViewPanel.getHeight(),BufferedImage.TYPE_INT_ARGB);
				    ((Graphics2D) image.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					// do the actual rendering
					try {renderHook(image,layerViewPanel.getViewport().getEnvelopeInModelCoordinates());}
					catch (Throwable t) {
						// TODO WARN THE USER?
						t.printStackTrace(System.err);
						return;
					}
					// when time comes, repaint the panel
					SwingUtilities.invokeLater(new Runnable() {public void run() {layerViewPanel.superRepaint();}});
				} finally {
					// the renderer is not rendering anymore ( used by isRendering() )
					rendering = false;
					rendered = true;
				}
			}
		};
	}

	/**
	 * Actually renders the layer in an image. Stop if cancelled is true.
	 * @param theImage the image to draw into
	 * @param envelope 
	 * @see #cancel()
	 */
	private void renderHook(BufferedImage theImage, GM_Envelope envelope) {
		if (cancelled||(this.features==null)) return;
		FT_FeatureCollection<? extends FT_Feature> collection = this.features.select(envelope);
		for (Style style : layer.getStyles()) {
			if (cancelled) return;
			if (style.isUserStyle()) {
				UserStyle userStyle = (UserStyle) style;
				for (FeatureTypeStyle featureTypeStyle:userStyle.getFeatureTypeStyles()) {
					if (cancelled) return;
					Map<Rule,Set<FT_Feature>> filteredFeatures = new HashMap<Rule,Set<FT_Feature>>();
					for(Rule rule:featureTypeStyle.getRules()) filteredFeatures.put(rule, new HashSet<FT_Feature>());
					if (logger.isDebugEnabled()) logger.debug(collection.size()+" features");
					for (FT_Feature feature:collection) {
						for(Rule rule:featureTypeStyle.getRules()) {
							if ((rule.getFilter()==null)||rule.getFilter().evaluate(feature)) {
								if (logger.isDebugEnabled()) logger.debug(feature+" filtered in "+rule.getFilter());
								filteredFeatures.get(rule).add(feature);
								break;
							} else {
								if (logger.isDebugEnabled()) logger.debug(feature+" filtered out "+rule.getFilter());
							}
						}
					}
					for(int indexRule=featureTypeStyle.getRules().size()-1;indexRule>=0;indexRule--) {
						if (cancelled) return;
						Rule rule=featureTypeStyle.getRules().get(indexRule);
						if (logger.isDebugEnabled()) logger.debug(filteredFeatures.get(rule)+"  for rule "+rule);
						for (Symbolizer symbolizer:rule.getSymbolizers()) {
							if (logger.isDebugEnabled()) logger.debug("symbolizer =  "+symbolizer);
							if (logger.isDebugEnabled()) logger.debug("stroke =  "+symbolizer.getStroke());
							if (logger.isDebugEnabled()) logger.debug("awt stroke =  "+symbolizer.getStroke().toAwtStroke());
							if (logger.isDebugEnabled()) logger.debug("color =  "+symbolizer.getStroke().getColor());
							
						}
						for (FT_Feature feature:filteredFeatures.get(rule))
							for (Symbolizer symbolizer:rule.getSymbolizers()) {render(symbolizer,feature,theImage);}
					}
				}
			}
		}
	}

	/**
	 * Render a rule.
	 * @param rule
	 * @param theImage
	 * @param envelope 
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void render(Rule rule, BufferedImage theImage, GM_Envelope envelope) {
		if (cancelled) return;
		//logger.info("using rule "+rule.getName());
		FT_FeatureCollection<? extends FT_Feature> collection = this.features.select(envelope);
		if (collection==null) return;
		Collection<FT_Feature> filteredCollection = (Collection<FT_Feature>) collection.getElements();
		if (rule.getFilter()!=null) {
			if (cancelled) return;
			filteredCollection = new ArrayList<FT_Feature>();
			for (FT_Feature feature:collection) {
				if (cancelled) return;
				if (rule.getFilter().evaluate(feature)) filteredCollection.add(feature);
			}
		}
		for (Symbolizer symbolizer:rule.getSymbolizers()) render(symbolizer,filteredCollection,theImage);
	}

	/**
	 * Render a symbolizer in an image using the given features
	 * @param symbolizer
	 * @param featureCollection
	 * @param theImage
	 */
	private void render(Symbolizer symbolizer,Collection<FT_Feature> featureCollection,BufferedImage theImage) {
		FT_Feature[] collection = featureCollection.toArray(new FT_Feature[0]);
		for (FT_Feature feature:collection) {
			if (cancelled) return;
			// TODO here, we should use the geometry parameter
			if (feature.getGeom() != null && !feature.getGeom().isEmpty()) {
				render(symbolizer,feature,theImage);
			}
		}		
	}

	/**
	 * Render a feature into an image using the given symbolizer
	 * @param symbolizer
	 * @param feature
	 * @param theImage
	 */
	private void render(Symbolizer symbolizer, FT_Feature feature, BufferedImage theImage) {
		if (theImage==null) return;
		symbolizer.paint(feature,layerViewPanel.getViewport(),(Graphics2D) theImage.getGraphics());
	}

	/**
	 * Clear the image cache, i.e. delete the current image.
	 */
	public void clearImageCache() {image = null;}

	@Override
	public void clearImageCache(int x, int y, int width, int height) {
		if (cancelled||(image==null)) return;
		for(int i = Math.max(x, 0) ; i < Math.min(x+width, layerViewPanel.getWidth()) ; i++)
			for(int j = Math.max(y, 0) ; j < Math.min(y+height, layerViewPanel.getHeight()) ; j++) 
				synchronized(image) {
					if (image==null) return;
					image.setRGB(i, j, Color.TRANSLUCENT);
				}
	}

	@Override
	public Runnable createFeatureRunnable(final FT_Feature feature) {
		if (image == null) {return null;}
		cancelled = false;

		return new Runnable() {
			public void run() {
				try {
					rendering = true;
					// it the rendering is cancel, stop
					if (cancelled) return;
					// if either the width or the height of the panel is lesser or equal to 0, stop
					if (Math.min(layerViewPanel.getWidth(),layerViewPanel.getHeight())<=0) return;
					// do the actual rendering
					try {renderHook(image,feature);}
					catch (Throwable t) {
						// TODO WARN THE USER?
						t.printStackTrace(System.err);
						return;
					}
					// when time comes, repaint the panel
					SwingUtilities.invokeLater(new Runnable() {public void run() {layerViewPanel.superRepaint();}});
				} finally {
					// the renderer is not rendering anymore ( used by isRendering() )
					rendering = false;
				}
			}
		};
	}
	@Override
	public Runnable createLocalRunnable(final GM_Object geom) {
		if (image == null) {return null;}
		cancelled = false;

		return new Runnable() {
			public void run() {
				try {
					rendering = true;
					// it the rendering is cancel, stop
					if (cancelled) return;
					// if either the width or the height of the panel is lesser or equal to 0, stop
					if (Math.min(layerViewPanel.getWidth(),layerViewPanel.getHeight())<=0) return;
					// do the actual rendering
					try {renderHook(image,geom);}
					catch (Throwable t) {
						// TODO WARN THE USER?
						t.printStackTrace(System.err);
						return;
					}
					// when time comes, repaint the panel
					SwingUtilities.invokeLater(new Runnable() {public void run() {layerViewPanel.superRepaint();}});
				} finally {
					// the renderer is not rendering anymore ( used by isRendering() )
					rendering = false;
				}
			}
		};
	}

	/**
	 * @param theImage
	 * @param feature
	 */
	protected void renderHook(BufferedImage theImage, FT_Feature feature) {
		if (cancelled) return;
		for (Style style : layer.getStyles()) {
			if (cancelled) return;
			if (style.isUserStyle()) {
				UserStyle userStyle = (UserStyle) style;
				for (FeatureTypeStyle featureTypeStyle:userStyle.getFeatureTypeStyles()) {
					if (cancelled) return;
					for(int indexRule=featureTypeStyle.getRules().size()-1;indexRule>=0;indexRule--) {
						if (cancelled) return;
						Rule rule=featureTypeStyle.getRules().get(indexRule);
						for (Symbolizer symbolizer:rule.getSymbolizers()) {
							if ((rule.getFilter()==null)||rule.getFilter().evaluate(feature)) {
								render(symbolizer,feature,theImage);
							}
						}
					}
				}
			}
		}
	}
	private void renderHook(BufferedImage theImage, GM_Object geom) {
		if (cancelled||(geom ==null)) return;
		GM_Envelope envelope = geom.envelope();
		DirectPosition lowerCornerPosition = envelope.getLowerCorner();
		lowerCornerPosition.move(-1, -1);
		DirectPosition upperCornerPosition = envelope.getUpperCorner();
		upperCornerPosition.move(1, 1);
		envelope.setLowerCorner(lowerCornerPosition);
		envelope.setUpperCorner(upperCornerPosition);
		try {
			Point2D upperCorner = layerViewPanel.getViewport().toViewPoint(envelope.getUpperCorner());
			Point2D lowerCorner = layerViewPanel.getViewport().toViewPoint(envelope.getLowerCorner());
			this.clearImageCache((int)lowerCorner.getX(),(int)upperCorner.getY(),(int)(upperCorner.getX()-lowerCorner.getX()),(int)(lowerCorner.getY()-upperCorner.getY()));
		} catch (NoninvertibleTransformException e) {e.printStackTrace();}
		FT_FeatureCollection<? extends FT_Feature> visibleFeatures = this.features.select(envelope);
		for (Style style : layer.getStyles()) {
			if (cancelled) return;
			if (style.isUserStyle()) {
				UserStyle userStyle = (UserStyle) style;
				for (FeatureTypeStyle featureTypeStyle:userStyle.getFeatureTypeStyles()) {
					if (cancelled) return;
					Map<Rule,Set<FT_Feature>> filteredFeatures = new HashMap<Rule,Set<FT_Feature>>();
					for(Rule rule:featureTypeStyle.getRules()) filteredFeatures.put(rule, new HashSet<FT_Feature>());
					for (FT_Feature feature:visibleFeatures) {
						for(Rule rule:featureTypeStyle.getRules()) {
							if ((rule.getFilter()==null)||rule.getFilter().evaluate(feature)) {
								filteredFeatures.get(rule).add(feature);
								break;
							}
						}
					}
					for(int indexRule=featureTypeStyle.getRules().size()-1;indexRule>=0;indexRule--) {
						if (cancelled) return;
						Rule rule=featureTypeStyle.getRules().get(indexRule);
						for (FT_Feature feature:filteredFeatures.get(rule))
							for (Symbolizer symbolizer:rule.getSymbolizers()) {render(symbolizer,feature,theImage);}
					}
				}
			}
		}
	}
}