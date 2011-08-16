/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.render;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
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
  static Logger logger = Logger.getLogger(LayerRenderer.class.getName());

  Layer layer = null;
  public Layer getLayer() {return layer;}

  LayerViewPanel layerViewPanel = null;
  IFeatureCollection<? extends IFeature> features = null;
  protected volatile boolean cancelled = false;
  protected volatile boolean rendering = false;
  protected volatile boolean rendered = false;

  @Override
  public boolean isRendered() {
    return this.rendered;
  }

  BufferedImage image = null;

  /**
   * Constructor of renderer using a {@link Layer} and a {@link LayerViewPanel}
   * 
   * @param theLayer a layer to render
   * @param theFeatures the feature to render
   * @param theLayerViewPanel the panel to draws into
   */
  public LayerRenderer(Layer theLayer,
      IFeatureCollection<? extends IFeature> theFeatures,
      LayerViewPanel theLayerViewPanel) {
    this.layer = theLayer;
    this.layerViewPanel = theLayerViewPanel;
    this.features = theFeatures;
  }

  /**
   * True is the renderer is running, i.e. if its associated runnable is
   * running, false otherwise.
   * 
   * @return true is the renderer is running, false otherwise
   * @see #createRunnable()
   * @see #renderHook(BufferedImage,IEnvelope)
   */
  @Override
  public boolean isRendering() {
    return this.rendering;
  }

  /**
   * Cancel the rendering. This method does not actually interrupt the thread
   * but lets the thread know it should stop.
   * 
   * @see Runnable
   * @see Thread
   */
  @Override
  public void cancel() {
    this.cancelled = true;
  }

  /**
   * Copy the rendered image the a 2D graphics
   * 
   * @param graphics the 2D graphics to draw into
   */
  @Override
  public void copyTo(Graphics2D graphics) {
    if (this.image != null) {
      graphics.drawImage(this.image, 0, 0, null);
    }
  }

  /**
   * Create a runnable for the renderer. A renderer create a new image to draw
   * into. If cancel() is called, the rendering stops as soon as possible. When
   * finished, set the variable rendering to false.
   * 
   * @return a new runnable
   * @see Runnable
   * @see #cancel()
   * @see #isRendering()
   */
  @Override
  public Runnable createRunnable() {
    if (this.image != null) {
      return null;
    }
    this.cancelled = false;
    return new Runnable() {
      @Override
      public void run() {
        try {
          LayerRenderer.this.rendering = true;
          LayerRenderer.this.rendered = false;
          // it the rendering is cancel, stop
          if (LayerRenderer.this.cancelled) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(LayerRenderer.this.layerViewPanel.getWidth(),
              LayerRenderer.this.layerViewPanel.getHeight()) <= 0) {
            return;
          }
          // create a new image
          LayerRenderer.this.image = new BufferedImage(
              LayerRenderer.this.layerViewPanel.getWidth(),
              LayerRenderer.this.layerViewPanel.getHeight(),
              BufferedImage.TYPE_INT_ARGB);
          // do the actual rendering
          try {
            LayerRenderer.this.renderHook(LayerRenderer.this.image,
                LayerRenderer.this.layerViewPanel.getViewport()
                    .getEnvelopeInModelCoordinates());
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
          // when time comes, repaint the panel
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              LayerRenderer.this.layerViewPanel.superRepaint();
            }
          });
        } finally {
          // the renderer is not rendering anymore ( used by
          // isRendering() )
          LayerRenderer.this.rendering = false;
          LayerRenderer.this.rendered = true;
        }
      }
    };
  }

  /**
   * Actually renders the layer in an image. Stop if cancelled is true.
   * 
   * @param theImage the image to draw into
   * @param envelope
   * @see #cancel()
   */
  private void renderHook(BufferedImage theImage, IEnvelope envelope) {
    if (this.cancelled || (this.features == null)) {
      return;
    }
    IFeatureCollection<? extends IFeature> collection = this.features
        .select(envelope);
    for (Style style : this.layer.getStyles()) {
      if (this.cancelled) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.cancelled) {
            return;
          }
          Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>();
          for (Rule rule : featureTypeStyle.getRules()) {
            filteredFeatures.put(rule, new HashSet<IFeature>());
          }
          if (LayerRenderer.logger.isDebugEnabled()) {
            LayerRenderer.logger.debug(collection.size() + " features"); //$NON-NLS-1$
          }
          for (IFeature feature : collection) {
            for (Rule rule : featureTypeStyle.getRules()) {
              if ((rule.getFilter() == null)
                  || rule.getFilter().evaluate(feature)) {
                if (LayerRenderer.logger.isDebugEnabled()) {
                  LayerRenderer.logger.debug(feature + " filtered in " //$NON-NLS-1$
                      + rule.getFilter());
                }
                filteredFeatures.get(rule).add(feature);
                break;
              } else {
                if (LayerRenderer.logger.isDebugEnabled()) {
                  LayerRenderer.logger.debug(feature + " filtered out " //$NON-NLS-1$
                      + rule.getFilter());
                }
              }
            }
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.cancelled) {
              return;
            }
            Rule rule = featureTypeStyle.getRules().get(indexRule);
            if (LayerRenderer.logger.isDebugEnabled()) {
              LayerRenderer.logger.debug(filteredFeatures.get(rule)
                  + "  for rule " + rule); //$NON-NLS-1$
            }
            if (LayerRenderer.logger.isDebugEnabled()) {
              for (Symbolizer symbolizer : rule.getSymbolizers()) {
                LayerRenderer.logger.debug("symbolizer =  " + symbolizer); //$NON-NLS-1$
                LayerRenderer.logger.debug("stroke =  " //$NON-NLS-1$
                    + symbolizer.getStroke());
                // logger.debug("awt stroke =  "+symbolizer.getStroke().toAwtStroke());
                // logger.debug("color =  "+symbolizer.getStroke().getColor());
              }
            }
            for (IFeature feature : filteredFeatures.get(rule)) {
              for (Symbolizer symbolizer : rule.getSymbolizers()) {
                this.render(symbolizer, feature, theImage);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Render a rule.
   * 
   * @param rule
   * @param theImage
   * @param envelope
   */
  @SuppressWarnings( { "unchecked", "unused" })
  private void render(Rule rule, BufferedImage theImage, IEnvelope envelope) {
    if (this.cancelled) {
      return;
    }
    // logger.info("using rule "+rule.getName());
    IFeatureCollection<? extends IFeature> collection = this.features
        .select(envelope);
    if (collection == null) {
      return;
    }
    Collection<IFeature> filteredCollection = (Collection<IFeature>) collection
        .getElements();
    if (rule.getFilter() != null) {
      if (this.cancelled) {
        return;
      }
      filteredCollection = new ArrayList<IFeature>();
      for (IFeature feature : collection) {
        if (this.cancelled) {
          return;
        }
        if (rule.getFilter().evaluate(feature)) {
          filteredCollection.add(feature);
        }
      }
    }
    for (Symbolizer symbolizer : rule.getSymbolizers()) {
      this.render(symbolizer, filteredCollection, theImage);
    }
  }

  /**
   * Render a symbolizer in an image using the given features
   * 
   * @param symbolizer
   * @param featureCollection
   * @param theImage
   */
  private void render(Symbolizer symbolizer,
      Collection<IFeature> featureCollection, BufferedImage theImage) {
    FT_Feature[] collection = featureCollection.toArray(new FT_Feature[0]);
    for (FT_Feature feature : collection) {
      if (this.cancelled) {
        return;
      }
      // TODO here, we should use the geometry parameter
      if (feature.getGeom() != null && !feature.getGeom().isEmpty()) {
        this.render(symbolizer, feature, theImage);
      }
    }
  }

  /**
   * Render a feature into an image using the given symbolizer
   * 
   * @param symbolizer
   * @param feature
   * @param theImage
   */
  private void render(Symbolizer symbolizer, IFeature feature,
      BufferedImage theImage) {
    if (theImage == null) {
      return;
    }
    Graphics2D graphics = theImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    symbolizer.paint(feature, this.layerViewPanel.getViewport(), graphics);
  }

  /**
   * Clear the image cache, i.e. delete the current image.
   */
  @Override
  public void clearImageCache() {
    this.image = null;
  }

  @Override
  public void clearImageCache(int x, int y, int width, int height) {
    if (this.cancelled || (this.image == null)) {
      return;
    }
    for (int i = Math.max(x, 0); i < Math.min(x + width, this.layerViewPanel
        .getWidth()); i++) {
      for (int j = Math.max(y, 0); j < Math.min(y + height, this.layerViewPanel
          .getHeight()); j++) {
        synchronized (this.image) {
          if (this.image == null) {
            return;
          }
          this.image.setRGB(i, j, Transparency.TRANSLUCENT);
        }
      }
    }
  }

  @Override
  public Runnable createFeatureRunnable(final IFeature feature) {
    if (this.image == null) {
      return null;
    }
    this.cancelled = false;

    return new Runnable() {
      @Override
      public void run() {
        try {
          LayerRenderer.this.rendering = true;
          // it the rendering is cancel, stop
          if (LayerRenderer.this.cancelled) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(LayerRenderer.this.layerViewPanel.getWidth(),
              LayerRenderer.this.layerViewPanel.getHeight()) <= 0) {
            return;
          }
          // do the actual rendering
          try {
            LayerRenderer.this.renderHook(LayerRenderer.this.image, feature);
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
          // when time comes, repaint the panel
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              LayerRenderer.this.layerViewPanel.superRepaint();
            }
          });
        } finally {
          // the renderer is not rendering anymore ( used by
          // isRendering() )
          LayerRenderer.this.rendering = false;
        }
      }
    };
  }

  @Override
  public Runnable createLocalRunnable(final IGeometry geom) {
    if (this.image == null) {
      return null;
    }
    this.cancelled = false;

    return new Runnable() {
      @Override
      public void run() {
        try {
          LayerRenderer.this.rendering = true;
          // it the rendering is cancel, stop
          if (LayerRenderer.this.cancelled) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(LayerRenderer.this.layerViewPanel.getWidth(),
              LayerRenderer.this.layerViewPanel.getHeight()) <= 0) {
            return;
          }
          // do the actual rendering
          try {
            LayerRenderer.this.renderHook(LayerRenderer.this.image, geom);
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
          // when time comes, repaint the panel
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              LayerRenderer.this.layerViewPanel.superRepaint();
            }
          });
        } finally {
          // the renderer is not rendering anymore ( used by
          // isRendering() )
          LayerRenderer.this.rendering = false;
        }
      }
    };
  }

  /**
   * @param theImage
   * @param feature
   */
  protected void renderHook(BufferedImage theImage, IFeature feature) {
    if (this.cancelled) {
      return;
    }
    for (Style style : this.layer.getStyles()) {
      if (this.cancelled) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.cancelled) {
            return;
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.cancelled) {
              return;
            }
            Rule rule = featureTypeStyle.getRules().get(indexRule);
            for (Symbolizer symbolizer : rule.getSymbolizers()) {
              if ((rule.getFilter() == null)
                  || rule.getFilter().evaluate(feature)) {
                this.render(symbolizer, feature, theImage);
              }
            }
          }
        }
      }
    }
  }

  private void renderHook(BufferedImage theImage, IGeometry geom) {
    if (this.cancelled || (geom == null)) {
      return;
    }
    IEnvelope envelope = geom.envelope();
    IDirectPosition lowerCornerPosition = envelope.getLowerCorner();
    lowerCornerPosition.move(-1, -1);
    IDirectPosition upperCornerPosition = envelope.getUpperCorner();
    upperCornerPosition.move(1, 1);
    envelope.setLowerCorner(lowerCornerPosition);
    envelope.setUpperCorner(upperCornerPosition);
    try {
      Point2D upperCorner = this.layerViewPanel.getViewport().toViewPoint(
          envelope.getUpperCorner());
      Point2D lowerCorner = this.layerViewPanel.getViewport().toViewPoint(
          envelope.getLowerCorner());
      this.clearImageCache((int) lowerCorner.getX(), (int) upperCorner.getY(),
          (int) (upperCorner.getX() - lowerCorner.getX()), (int) (lowerCorner
              .getY() - upperCorner.getY()));
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    IFeatureCollection<? extends IFeature> visibleFeatures = this.features
        .select(envelope);
    for (Style style : this.layer.getStyles()) {
      if (this.cancelled) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.cancelled) {
            return;
          }
          Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>();
          for (Rule rule : featureTypeStyle.getRules()) {
            filteredFeatures.put(rule, new HashSet<IFeature>());
          }
          for (IFeature feature : visibleFeatures) {
            for (Rule rule : featureTypeStyle.getRules()) {
              if ((rule.getFilter() == null)
                  || rule.getFilter().evaluate(feature)) {
                filteredFeatures.get(rule).add(feature);
                break;
              }
            }
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.cancelled) {
              return;
            }
            Rule rule = featureTypeStyle.getRules().get(indexRule);
            for (IFeature feature : filteredFeatures.get(rule)) {
              for (Symbolizer symbolizer : rule.getSymbolizers()) {
                this.render(symbolizer, feature, theImage);
              }
            }
          }
        }
      }
    }
  }
}
