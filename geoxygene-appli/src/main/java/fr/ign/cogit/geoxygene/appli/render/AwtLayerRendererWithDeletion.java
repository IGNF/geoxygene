package fr.ign.cogit.geoxygene.appli.render;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class AwtLayerRendererWithDeletion extends AwtLayerRenderer {

  /** The logger. */
  private static Logger LOGGER = Logger
      .getLogger(AwtLayerRendererWithDeletion.class.getName());

  public AwtLayerRendererWithDeletion(Layer theLayer,
      LayerViewPanel theLayerViewPanel) {
    super(theLayer, theLayerViewPanel);
  }

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
          AwtLayerRendererWithDeletion.this.setRendering(true);
          // and it's not finished yet
          AwtLayerRendererWithDeletion.this.setRendered(false);
          // it the rendering is cancel, stop
          if (AwtLayerRendererWithDeletion.this.isCancelled()) {
            return;
          }
          // if either the width or the height of the panel is lesser
          // or equal to 0, stop
          if (Math.min(AwtLayerRendererWithDeletion.this.getLayerViewPanel()
              .getWidth(), AwtLayerRendererWithDeletion.this
              .getLayerViewPanel().getHeight()) <= 0) {
            return;
          }
          // do the actual rendering
          try {
            AwtLayerRendererWithDeletion.this.initializeRendering();
            AwtLayerRendererWithDeletion.this.renderDeletion(
                AwtLayerRendererWithDeletion.this.getImage(),
                AwtLayerRendererWithDeletion.this.getLayerViewPanel()
                    .getViewport().getEnvelopeInModelCoordinates());
          } catch (Throwable t) {
            // TODO WARN THE USER?
            t.printStackTrace(System.err);
            return;
          }
        } finally {
          // the renderer is not rendering anymore
          // ( used by isRendering() )
          AwtLayerRendererWithDeletion.this.setRendering(false);
          AwtLayerRendererWithDeletion.this.setRendered(true);
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Renderer " //$NON-NLS-1$
                + AwtLayerRendererWithDeletion.this.getLayer().getName()
                + " finished"); //$NON-NLS-1$
          }
          // FIXME Is this operation really useful or is a patch?
          AwtLayerRendererWithDeletion.this.getLayerViewPanel()
              .getRenderingManager().repaint();
        }
      }
    };
  }

  /**
   * Actually renders the layer in an image. Stop if cancelled is true. Does not
   * render the deleted features.
   * @param theImage the image to draw into
   * @param envelope the envelope
   * @see #cancel()
   */
  final void renderDeletion(final BufferedImage theImage,
      final IEnvelope envelope) {
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
              if (feature.isDeleted())
                continue;
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
              if (feature.isDeleted())
                continue;
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

}
