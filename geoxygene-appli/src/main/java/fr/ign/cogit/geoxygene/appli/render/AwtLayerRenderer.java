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
import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
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
public class AwtLayerRenderer extends AbstractLayerRenderer {
  
  /** The logger. */
  private static Logger LOGGER = Logger.getLogger(AwtLayerRenderer.class.getName());
  private BufferedImage image = null;

  /**
   * Set the image.
   * @param theImage the image to render
   */
  public final void setImage(final BufferedImage theImage) {
    this.image = theImage;
  }

  /**
   * Get the image.
   * @return the rendered image
   */
  public final BufferedImage getImage() {
    return this.image;
  }

  /**
   * Constructor of renderer using a {@link Layer} and a {@link LayerViewPanel}.
   * @param theLayer a layer to render
   * @param theLayerViewPanel the panel to draws into
   */
  public AwtLayerRenderer(final Layer theLayer,
      final LayerViewPanel theLayerViewPanel) {
    super(theLayer, theLayerViewPanel);
  }

  /**
   * Copy the rendered image the a 2D graphics.
   * @param graphics the 2D graphics to draw into
   */
  public void copyTo(final Graphics2D graphics) {
    if (this.getImage() != null) {
      /*
       * if (logger.isTraceEnabled()) { logger.trace("drawImage"); //$NON-NLS-1$
       * }
       */
      graphics.drawImage(this.getImage(), 0, 0, null);
    } else {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("this.getImage() = null"); //$NON-NLS-1$
      }
    }
  }

  /**
   * Create a runnable for the renderer. A renderer creates a new image to draw
   * into. If cancel() is called, the rendering stops as soon as possible. When
   * finished, set the variable rendering to false.
   * @return a new runnable
   * @see Runnable
   * @see #cancel()
   * @see #isRendering()
   */
  @Override
  public Runnable createRunnable() {
    if (this.getImage() == null) {
      return null; // No image, can't render
    }
    this.setCancelled(false);
    return new Runnable() {
      @Override
      public void run() {
        try {
          // now, we are rendering
          AwtLayerRenderer.this.setRendering(true);
          // and it's not finished yet
          AwtLayerRenderer.this.setRendered(false);
          // it the rendering is cancel, stop
          if (AwtLayerRenderer.this.isCancelled()) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(AwtLayerRenderer.this.getLayerViewPanel().getWidth(),
              AwtLayerRenderer.this.getLayerViewPanel().getHeight()) <= 0) {
            return;
          }
          // do the actual rendering
          try {
            AwtLayerRenderer.this.initializeRendering();
            AwtLayerRenderer.this.renderHook(AwtLayerRenderer.this.getImage(),
                AwtLayerRenderer.this.getLayerViewPanel().getViewport()
                    .getEnvelopeInModelCoordinates());
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
        } finally {
          // the renderer is not rendering anymore
          // ( used by isRendering() )
          AwtLayerRenderer.this.setRendering(false);
          AwtLayerRenderer.this.setRendered(true);
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Renderer " //$NON-NLS-1$
                + AwtLayerRenderer.this.getLayer().getName() + " finished"); //$NON-NLS-1$
          }
          // FIXME Is this operation really useful or is a patch?
          AwtLayerRenderer.this.getLayerViewPanel().getRenderingManager()
              .repaint();
        }
      }
    };
  }

  /**
   * Method called before each rendering
   */
  @Override
  public void initializeRendering() {
    super.initializeRendering();
    this.clearImageCache();
    this.setImage(new BufferedImage(AwtLayerRenderer.this.getLayerViewPanel()
        .getWidth(), AwtLayerRenderer.this.getLayerViewPanel().getHeight(),
        BufferedImage.TYPE_INT_ARGB));
  }

  /**
   * Actually renders the layer in an image. Stop if cancelled is true.
   * @param theImage the image to draw into
   * @param envelope the envelope
   * @see #cancel()
   */
  final void renderHook(final BufferedImage theImage, final IEnvelope envelope) {
    // if rendering has been cancelled or there is nothing to render, stop
    if (this.isCancelled() || this.getLayer().getFeatureCollection() == null
        || !this.getLayer().isVisible()) {
      return;
    }
    Collection<? extends IFeature> collection = this.getLayer()
        .getFeatureCollection().select(envelope);
    int numberOfFeatureTypeStyle = 0;
    Collection<Style> activeStyles = this.getLayer().getActiveStyles();
    for (Style style : activeStyles) {
      if (style.isUserStyle()) {
        numberOfFeatureTypeStyle += ((UserStyle) style).getFeatureTypeStyles()
            .size();
      }
    }
    // logger.info(numberOfFeatureTypeStyle + " fts");
    this.fireActionPerformed(new ActionEvent(this, 3,
        "Rendering start", numberOfFeatureTypeStyle * collection.size())); //$NON-NLS-1$
    int featureRenderIndex = 0;
    for (Style style : activeStyles) {
      if (this.isCancelled()) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.isCancelled()) {
            return;
          }
          // creating a map between each rule and the
          // corresponding features (filtered in)
          Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>(
              0);
          if (featureTypeStyle.getRules().size() == 1
              && featureTypeStyle.getRules().get(0).getFilter() == null) {
            filteredFeatures.put(featureTypeStyle.getRules().get(0),
                new HashSet<IFeature>(collection));
          } else {
            for (Rule rule : featureTypeStyle.getRules()) {
              filteredFeatures.put(rule, new HashSet<IFeature>(0));
            }
            IFeature[] list = new IFeature[0];
            synchronized (collection) {
              list = collection.toArray(list);
            }
            for (IFeature feature : list) {
              for (Rule rule : featureTypeStyle.getRules()) {
                if (rule.getFilter() == null
                    || rule.getFilter().evaluate(feature)) {
                  filteredFeatures.get(rule).add(feature);
                  break;
                }
              }
            }
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.isCancelled()) {
              return;
            }
            Rule rule = featureTypeStyle.getRules().get(indexRule);
            for (IFeature feature : filteredFeatures.get(rule)) {
              if (this.isCancelled()) {
                return;
              }
              for (Symbolizer symbolizer : rule.getSymbolizers()) {
                if (this.isCancelled()) {
                  return;
                }
                this.render(symbolizer, feature, theImage);
              }
              this.fireActionPerformed(new ActionEvent(this, 4,
                  "Rendering feature", 100 * featureRenderIndex++ //$NON-NLS-1$
                      / (numberOfFeatureTypeStyle * collection.size())));
            }
          }
        }
      }
    }
    this.fireActionPerformed(new ActionEvent(this, 5,
        "Rendering finished", featureRenderIndex)); //$NON-NLS-1$
  }

  /**
   * Render a rule.
   * @param rule the rule
   * @param theImage the image
   * @param envelope the envelope
   */
  @SuppressWarnings({ "unused", "unchecked" })
  private void render(final Rule rule, final BufferedImage theImage,
      final IEnvelope envelope) {
    if (this.isCancelled()) {
      return;
    }
    // logger.info("using rule "+rule.getName());
    Collection<? extends IFeature> collection = this.getLayer()
        .getFeatureCollection().select(envelope);
    if (collection == null) {
      return;
    }
    Collection<IFeature> filteredCollection = (Collection<IFeature>) collection;
    if (rule.getFilter() != null) {
      if (this.isCancelled()) {
        return;
      }
      filteredCollection = new ArrayList<IFeature>();
      for (IFeature feature : collection) {
        if (this.isCancelled()) {
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
   * Render a symbolizer in an image using the given features.
   * @param symbolizer the symbolize
   * @param featureCollection the feature collection
   * @param theImage the image
   */
  private void render(final Symbolizer symbolizer,
      final Collection<IFeature> featureCollection, final BufferedImage theImage) {
    IFeature[] collection = featureCollection.toArray(new IFeature[0]);
    for (IFeature feature : collection) {
      if (this.isCancelled()) {
        return;
      }
      // TODO here, we should use the geometry parameter
      if (feature.getGeom() != null && !feature.getGeom().isEmpty()) {
        this.render(symbolizer, feature, theImage);
      }
    }
  }

  /**
   * Render a feature into an image using the given symbolizer.
   * @param symbolizer the symbolizer
   * @param feature the feature
   * @param theImage the image
   */
  private void render(final Symbolizer symbolizer, final IFeature feature,
      final BufferedImage theImage) {
    if (theImage == null) {
      return;
    }
    Graphics2D graphics = theImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    RenderUtil.paint(symbolizer, feature, this.getLayerViewPanel()
        .getViewport(), graphics, this.getLayer().getOpacity(), null);// FIXME
                                                                      // // FIX
  }

  /** Clear the image cache, i.e. delete the current image. */
  public void clearImageCache() {
    this.setImage(null);
  }

  public void clearImageCache(final int x, final int y, final int width,
      final int height) {
    if (this.isCancelled() || this.getImage() == null) {
      return;
    }
    for (int i = Math.max(x, 0); i < Math.min(x + width, this
        .getLayerViewPanel().getWidth()); i++) {
      for (int j = Math.max(y, 0); j < Math.min(y + height, this
          .getLayerViewPanel().getHeight()); j++) {
        if (this.getImage() == null) {
          return;
        }
        synchronized (this.getImage()) {
          if (this.getImage() == null) {
            return;
          }
          this.getImage().setRGB(i, j, Transparency.TRANSLUCENT);
        }
      }
    }
  }

  /**
   * @param theImage the image
   * @param feature the feature
   */
  final void renderHook(final BufferedImage theImage, final IFeature feature) {
    if (this.isCancelled()) {
      return;
    }
    for (Style style : this.getLayer().getStyles()) {
      if (this.isCancelled()) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.isCancelled()) {
            return;
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.isCancelled()) {
              return;
            }
            Rule rule = featureTypeStyle.getRules().get(indexRule);
            for (Symbolizer symbolizer : rule.getSymbolizers()) {
              if (rule.getFilter() == null
                  || rule.getFilter().evaluate(feature)) {
                this.render(symbolizer, feature, theImage);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Render hook.
   * @param theImage the image to render
   * @param geom the geometry to render
   */
  final void renderHook(final BufferedImage theImage, final IGeometry geom) {
    if (this.isCancelled() || geom == null || geom.isEmpty()) {
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
      Point2D upperCorner = this.getLayerViewPanel().getViewport()
          .toViewPoint(envelope.getUpperCorner());
      Point2D lowerCorner = this.getLayerViewPanel().getViewport()
          .toViewPoint(envelope.getLowerCorner());
      this.clearImageCache((int) lowerCorner.getX(), (int) upperCorner.getY(),
          (int) (upperCorner.getX() - lowerCorner.getX() + 2),
          (int) (lowerCorner.getY() - upperCorner.getY() + 2));
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    Collection<? extends IFeature> visibleFeatures = this.getLayer()
        .getFeatureCollection().select(envelope);
    for (Style style : this.getLayer().getStyles()) {
      if (this.isCancelled()) {
        return;
      }
      if (style.isUserStyle()) {
        UserStyle userStyle = (UserStyle) style;
        for (FeatureTypeStyle featureTypeStyle : userStyle
            .getFeatureTypeStyles()) {
          if (this.isCancelled()) {
            return;
          }
          Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>();
          for (Rule rule : featureTypeStyle.getRules()) {
            filteredFeatures.put(rule, new HashSet<IFeature>());
          }
          for (IFeature feature : visibleFeatures) {
            for (Rule rule : featureTypeStyle.getRules()) {
              if (rule.getFilter() == null
                  || rule.getFilter().evaluate(feature)) {
                filteredFeatures.get(rule).add(feature);
                break;
              }
            }
          }
          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
            if (this.isCancelled()) {
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
