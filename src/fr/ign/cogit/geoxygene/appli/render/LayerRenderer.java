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
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
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
  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger(LayerRenderer.class.getName());
  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code>.
   * @param l the <code>ActionListener</code> to be added
   */
  public void addActionListener(ActionListener l) {
    this.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = this.listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        // Lazily create the event:
        ((ActionListener) listeners[i + 1]).actionPerformed(event);
      }
    }
  }

  /**
   * Layer to render.
   */
  private Layer layer = null;

  public Layer getLayer() {
    return this.layer;
  }

  /**
   * Layer view panel.
   */
  private LayerViewPanel layerViewPanel = null;

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
   * Set the layer view panel.
   * @param theLayerViewPanel the layer view panel
   */
  public final void setLayerViewPanel(final LayerViewPanel theLayerViewPanel) {
    this.layerViewPanel = theLayerViewPanel;
  }

  /**
   * @return the layer view panel
   */
  public final LayerViewPanel getLayerViewPanel() {
    return this.layerViewPanel;
  }

  /**
   * True if rendering is finished.
   */
  private volatile boolean rendered = false;

  /**
   * @param isRendered True if rendering is finished, false otherwise
   */
  public final void setRendered(final boolean isRendered) {
    this.rendered = isRendered;
  }

  /**
   * @return true if rendering is finished, false otherwise
   */
  @Override
  public final boolean isRendered() {
    return this.rendered;
  }

  /**
   * True if rendering is cancelled.
   */
  private volatile boolean cancelled = false;

  /**
   * @param isCancelled True if rendering is cancelled, false otherwise
   */
  public final void setCancelled(final boolean isCancelled) {
    this.cancelled = isCancelled;
  }

  /**
   * @return True if rendering is cancelled, false otherwise.
   */
  public final boolean isCancelled() {
    return this.cancelled;
  }

  /**
   * The rendered image.
   */
  private BufferedImage image = null;

  /**
   * True if rendering is ongoing.
   */
  private volatile boolean rendering = false;

  /**
   * True is the renderer is running, i.e. if its associated runnable is
   * running, false otherwise.
   * @return true is the renderer is running, false otherwise
   * @see #createRunnable()
   * @see #renderHook(BufferedImage,GM_Envelope)
   */
  @Override
  public final boolean isRendering() {
    return this.rendering;
  }

  /**
   * @param isRendering True if rendering is ongoing
   */
  public final void setRendering(final boolean isRendering) {
    this.rendering = isRendering;
  }

  /**
   * Cancel the rendering. This method does not actually interrupt the thread
   * but lets the thread know it should stop.
   * @see Runnable
   * @see Thread
   */
  @Override
  public final void cancel() {
    this.setCancelled(true);
  }

  /**
   * Constructor of renderer using a {@link Layer} and a {@link LayerViewPanel}.
   * @param theLayer a layer to render
   * @param theLayerViewPanel the panel to draws into
   */
  public LayerRenderer(final Layer theLayer,
      final LayerViewPanel theLayerViewPanel) {
    this.layer = theLayer;
    this.setLayerViewPanel(theLayerViewPanel);
  }

  /**
   * Copy the rendered image the a 2D graphics.
   * @param graphics the 2D graphics to draw into
   */
  @Override
  public final void copyTo(final Graphics2D graphics) {
    if (this.getImage() != null) {
      /*
       * if (logger.isTraceEnabled()) { logger.trace("drawImage"); //$NON-NLS-1$
       * }
       */
      graphics.drawImage(this.getImage(), 0, 0, null);
    } else {
      if (LayerRenderer.logger.isTraceEnabled()) {
        LayerRenderer.logger.trace("this.getImage() = null"); //$NON-NLS-1$
      }
    }
  }

  /**
   * Create a runnable for the renderer. A renderer create a new image to draw
   * into. If cancel() is called, the rendering stops as soon as possible. When
   * finished, set the variable rendering to false.
   * @return a new runnable
   * @see Runnable
   * @see #cancel()
   * @see #isRendering()
   */
  @Override
  public final Runnable createRunnable() {
    if (this.getImage() != null) {
      return null; // No image, can't render
    }
    this.setCancelled(false);
    return new Runnable() {
      @Override
      public void run() {
        try {
          // now, we are rendering
          LayerRenderer.this.setRendering(true);
          // and it's not finished yet
          LayerRenderer.this.setRendered(false);
          // it the rendering is cancel, stop
          if (LayerRenderer.this.isCancelled()) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(LayerRenderer.this.getLayerViewPanel().getWidth(),
              LayerRenderer.this.getLayerViewPanel().getHeight()) <= 0) {
            return;
          }
          // create a new image
          LayerRenderer.this.setImage(new BufferedImage(LayerRenderer.this
              .getLayerViewPanel().getWidth(), LayerRenderer.this
              .getLayerViewPanel().getHeight(), BufferedImage.TYPE_INT_ARGB));
          // do the actual rendering
          try {
            LayerRenderer.this.renderHook(LayerRenderer.this.getImage(),
                LayerRenderer.this.getLayerViewPanel().getViewport()
                    .getEnvelopeInModelCoordinates());
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
        } finally {
          // the renderer is not rendering anymore
          // ( used by isRendering() )
          LayerRenderer.this.setRendering(false);
          LayerRenderer.this.setRendered(true);
          if (LayerRenderer.logger.isTraceEnabled()) {
            LayerRenderer.logger.trace("Renderer " //$NON-NLS-1$
                + LayerRenderer.this.getLayer().getName() + " finished"); //$NON-NLS-1$
          }
          LayerRenderer.this.getLayerViewPanel().getRenderingManager()
              .repaint();
        }
      }
    };
  }

  /**
   * Actually renders the layer in an image. Stop if cancelled is true.
   * @param theImage the image to draw into
   * @param envelope the envelope
   * @see #cancel()
   */
  final void renderHook(final BufferedImage theImage, final GM_Envelope envelope) {
    // if rendering has been cancelled or there is nothing to render, stop
    if (this.isCancelled() || (this.layer.getFeatureCollection() == null)
        || !this.layer.isVisible()) {
      return;
    }
    Collection<? extends FT_Feature> collection = this.layer
        .getFeatureCollection().select(envelope);
    int numberOfFeatureTypeStyle = 0;
    Collection<Style> activeStyles = this.layer.getStyles();
    if (!(this.layer.getActiveGroup() == null||this.layer.getActiveGroup().isEmpty())) {
      activeStyles = new ArrayList<Style>(0);
      for (Style style : this.layer.getStyles()) {
        if (style.getGroup() == null || style.getGroup().equalsIgnoreCase(this.layer.getActiveGroup())) {
          activeStyles.add(style);
        }
      }
    }
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
          Map<Rule, Set<FT_Feature>> filteredFeatures = new HashMap<Rule, Set<FT_Feature>>(0);
          for (Rule rule : featureTypeStyle.getRules()) {
            filteredFeatures.put(rule, new HashSet<FT_Feature>(0));
          }
          FT_Feature[] list = new FT_Feature[0];
          synchronized (collection) {
            list = collection.toArray(list);
          }
          for (FT_Feature feature : list) {
            for (Rule rule : featureTypeStyle.getRules()) {
              if ((rule.getFilter() == null)
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
            for (FT_Feature feature : filteredFeatures.get(rule)) {
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
  @SuppressWarnings( { "unused", "unchecked" })
  private void render(final Rule rule, final BufferedImage theImage,
      final GM_Envelope envelope) {
    if (this.isCancelled()) {
      return;
    }
    // logger.info("using rule "+rule.getName());
    Collection<? extends FT_Feature> collection = this.layer
        .getFeatureCollection().select(envelope);
    if (collection == null) {
      return;
    }
    Collection<FT_Feature> filteredCollection = (Collection<FT_Feature>) collection;
    if (rule.getFilter() != null) {
      if (this.isCancelled()) {
        return;
      }
      filteredCollection = new ArrayList<FT_Feature>();
      for (FT_Feature feature : collection) {
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
      final Collection<FT_Feature> featureCollection,
      final BufferedImage theImage) {
    FT_Feature[] collection = featureCollection.toArray(new FT_Feature[0]);
    for (FT_Feature feature : collection) {
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
  private void render(final Symbolizer symbolizer, final FT_Feature feature,
      final BufferedImage theImage) {
    if (theImage == null) {
      return;
    }
    Graphics2D graphics = theImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    symbolizer.paint(feature, this.getLayerViewPanel().getViewport(), graphics);
  }

  /**
   * Clear the image cache, i.e. delete the current image.
   */
  @Override
  public final void clearImageCache() {
    this.setImage(null);
  }

  @Override
  public final void clearImageCache(final int x, final int y, final int width,
      final int height) {
    if (this.isCancelled() || (this.getImage() == null)) {
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
  final void renderHook(final BufferedImage theImage, final FT_Feature feature) {
    if (this.isCancelled()) {
      return;
    }
    for (Style style : this.layer.getStyles()) {
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

  /**
   * Render hook.
   * @param theImage the image to render
   * @param geom the geometry to render
   */
  final void renderHook(final BufferedImage theImage, final GM_Object geom) {
    if (this.isCancelled() || geom == null || geom.isEmpty()) {
      return;
    }
    GM_Envelope envelope = geom.envelope();
    DirectPosition lowerCornerPosition = envelope.getLowerCorner();
    lowerCornerPosition.move(-1, -1);
    DirectPosition upperCornerPosition = envelope.getUpperCorner();
    upperCornerPosition.move(1, 1);
    envelope.setLowerCorner(lowerCornerPosition);
    envelope.setUpperCorner(upperCornerPosition);
    try {
      Point2D upperCorner = this.getLayerViewPanel().getViewport().toViewPoint(
          envelope.getUpperCorner());
      Point2D lowerCorner = this.getLayerViewPanel().getViewport().toViewPoint(
          envelope.getLowerCorner());
      this.clearImageCache((int) lowerCorner.getX(), (int) upperCorner.getY(),
          (int) (upperCorner.getX() - lowerCorner.getX() + 2),
          (int) (lowerCorner.getY() - upperCorner.getY() + 2));
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    Collection<? extends FT_Feature> visibleFeatures = this.layer
        .getFeatureCollection().select(envelope);
    for (Style style : this.layer.getStyles()) {
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
          Map<Rule, Set<FT_Feature>> filteredFeatures = new HashMap<Rule, Set<FT_Feature>>();
          for (Rule rule : featureTypeStyle.getRules()) {
            filteredFeatures.put(rule, new HashSet<FT_Feature>());
          }
          for (FT_Feature feature : visibleFeatures) {
            for (Rule rule : featureTypeStyle.getRules()) {
              if ((rule.getFilter() == null)
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
            for (FT_Feature feature : filteredFeatures.get(rule)) {
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
