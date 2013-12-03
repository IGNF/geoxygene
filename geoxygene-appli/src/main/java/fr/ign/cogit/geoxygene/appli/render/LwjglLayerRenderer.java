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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GLContext;

import com.google.common.io.Files;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.script.ScriptingPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.DensityFieldPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLPrimitivePointRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedConverterUtil;
import fr.ign.cogit.geoxygene.appli.render.primitive.PrimitiveRenderer;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewLwjgl1Panel}. It
 * draws directly the layer into the GL context contained into the
 * 
 * @author JeT
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public class LwjglLayerRenderer extends AbstractLayerRenderer {
    private static Logger logger = Logger.getLogger(LwjglLayerRenderer.class.getName()); // logger
    protected EventListenerList listenerList = new EventListenerList();
    private LayerViewGLPanel layerViewPanel = null; // Lwjgl layer view Panel
    private final Map<String, PrimitiveRenderer> renderers = new HashMap<String, PrimitiveRenderer>();
    private PrimitiveRenderer defaultRenderer = null;
    private final DensityFieldPrimitiveRenderer densityFieldPrimitiveRenderer = new DensityFieldPrimitiveRenderer();

    /**
     * Constructor of renderer using a {@link Layer} and a
     * {@link LayerViewPanel}.
     * 
     * @param theLayer
     *            a layer to render
     * @param theLayerViewPanel
     *            the panel to draws into
     */
    public LwjglLayerRenderer(final Layer theLayer, final LayerViewGLPanel theLayerViewPanel) {
        super(theLayer, theLayerViewPanel);
        this.setLayerViewPanel(theLayerViewPanel);
        this.initRenderers();
    }

    private void initRenderers() {
        this.defaultRenderer = new GLPrimitivePointRenderer();
    }

    /**
     * TODO: This method should not exist anymore when the SLD will describe
     * which renderer has to be used for which feature/symbolizer
     * 
     * @return the renderer
     */
    private static boolean onlyOneWarning = false;

    public PrimitiveRenderer getLayerRenderer() {
        String layerName = this.getLayer().getName();

        //        if (layerName.compareToIgnoreCase("SURFACE_ROUTE") == 0 || layerName.compareToIgnoreCase("SURFACE_EAU") == 0) {
        if (layerName.compareToIgnoreCase("mer") == 0 || layerName.compareToIgnoreCase("PISTE_AERODROME") == 0
                || layerName.compareToIgnoreCase("mer_sans_sable") == 0 || layerName.compareToIgnoreCase("sable_humide") == 0) {
            //            if (layerName.compareToIgnoreCase("mer") == 0 || layerName.compareToIgnoreCase("sable_humide") == 0) {
            if (!onlyOneWarning) {
                logger.warn("Special primitive renderer used for layer named " + layerName);
                onlyOneWarning = true;
            }
            return this.densityFieldPrimitiveRenderer;
        }

        if (this.renderers.containsKey(layerName)) {
            return this.renderers.get(layerName);
        }

        System.err.println("layer name = " + layerName);
        File defaultRenderingScriptFile = new File("src/main/resources/scripts/defaultRenderer.groovy");
        File layerRenderingScriptFile = new File("src/main/resources/scripts/" + layerName + "-renderer.groovy");
        if (!layerRenderingScriptFile.isFile()) {
            try {
                Files.copy(defaultRenderingScriptFile, layerRenderingScriptFile);
                logger.info("copy default script to " + layerRenderingScriptFile);
            } catch (IOException e) {
                logger.error("Cannot copy " + defaultRenderingScriptFile + " to " + layerRenderingScriptFile);
                e.printStackTrace();
                return null;
            }
        }
        try {
            ScriptingPrimitiveRenderer scriptingPrimitiveRenderer = new ScriptingPrimitiveRenderer(layerRenderingScriptFile);
            scriptingPrimitiveRenderer.initializeRendering();
            this.renderers.put(layerName, scriptingPrimitiveRenderer);
            return scriptingPrimitiveRenderer;
        } catch (Exception e) {
            logger.error("Cannot load groovy script file " + layerRenderingScriptFile);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the renderer associated with this symbolizer. It should be described
     * by the SLD
     * FIXME: use SLD to choose the right Renderer
     * 
     * @return the renderer
     */
    public PrimitiveRenderer getRenderer(final Symbolizer symbolizer) {
        PrimitiveRenderer symbolizerRenderer = this.getLayerRenderer();
        if (symbolizerRenderer != null) {
            return symbolizerRenderer;
        }
        logger.warn("No Primitive renderer associated with layer " + this.getLayer().getName());
        return this.defaultRenderer;
    }

    /**
     * Adds an <code>ActionListener</code>.
     * 
     * @param l
     *            the <code>ActionListener</code> to be added
     */
    @Override
    public void addActionListener(final ActionListener l) {
        this.listenerList.add(ActionListener.class, l);
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created.
     * 
     * @see EventListenerList
     */
    @Override
    protected void fireActionPerformed(final ActionEvent event) {
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
     * Set the layer view panel.
     * 
     * @param theLayerViewPanel
     *            the layer view Lwjgl panel
     */
    public final void setLayerViewPanel(final LayerViewGLPanel theLayerViewPanel) {
        this.layerViewPanel = theLayerViewPanel;
    }

    /**
     * Create a runnable for the renderer. A renderer create a new image to draw
     * into. If cancel() is called, the rendering stops as soon as possible.
     * When finished, set the variable rendering to false.
     * 
     * @return a new runnable
     * @see Runnable
     * @see #cancel()
     * @see #isRendering()
     */
    @Override
    public final Runnable createRunnable() {
        // this runnable is not dedicated to be launched into a thread.
        // It should be launched by a SyncRenderingManager which calls the run() method as a singular method

        //logger.debug("isCreated() = " + Display.isCreated());
        //logger.debug("isVisible() = " + Display.isVisible());

        //logger.debug("isCreated() = " + Display.isVisible());
        //    System.out.println("isActive " + Display.isActive() + " isCreated " + Display.isCreated() + " isVisible " + Display.isVisible());
        try {
            if (GLContext.getCapabilities() == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return new Runnable() {
            @Override
            public void run() {
                //        try {
                //            //            System.out.println("isCurrent = " + Display.isCurrent() + " " + Display.isActive() + " " + Display.isCreated() + " "
                //        } catch (LWJGLException e1) {
                //          Log.warn("GL display is not ready. Rendering is aborted. " + e1.getMessage());
                //          return;
                //        }
                LayerViewGLPanel vp = (LayerViewGLPanel) LwjglLayerRenderer.this.getLayerViewPanel();
                try {
                    LwjglLayerRenderer.this.setRendering(true); // now, we are rendering
                    LwjglLayerRenderer.this.setRendered(false); // and it's not finished yet
                    if (LwjglLayerRenderer.this.isCancelled()) { // it the rendering is cancel, stop
                        return;
                    }
                    // if either the width or the height of the panel is lesser than or equal to 0, stop
                    if (Math.min(vp.getWidth(), vp.getHeight()) <= 0) {
                        return;
                    }
                    // do the actual rendering
                    try {
                        LwjglLayerRenderer.this.renderHook(vp.getViewport().getEnvelopeInModelCoordinates());
                    } catch (Throwable t) {
                        logger.warn("LwJGL Rendering failed: " + t.getMessage() + " (" + t.getClass().getSimpleName() + ")");
                        t.printStackTrace();
                        return;
                    }
                } finally {
                    LwjglLayerRenderer.this.setRendering(false); // we are no more in rendering progress
                    // FIXME Is this operation really useful or is a patch?
                    vp.getRenderingManager().repaint();
                }
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.LayerRenderer#initializeRendering()
     */
    @Override
    public void initializeRendering() {
        System.err.println("initialize Layer before rendering");
        for (PrimitiveRenderer renderer : this.renderers.values()) {
            try {
                renderer.initializeRendering();
            } catch (RenderingException e) {
                e.printStackTrace();
            }
        }
        super.initializeRendering();
    }

    /**
     * Actually renders the layer in the open GL context. Stop if cancelled is
     * true.
     * 
     * @param theImage
     *            the image to draw into
     * @param envelope
     *            the envelope
     * @throws RenderingException
     * @see #cancel()
     */
    final void renderHook(final IEnvelope envelope) throws RenderingException {
        // if rendering has been cancelled or there is nothing to render, stop
        if (this.isCancelled() || this.getLayer().getFeatureCollection() == null || !this.getLayer().isVisible()) {
            return;
        }
        Collection<? extends IFeature> collection = this.getLayer().getFeatureCollection().select(envelope);
        int numberOfFeatureTypeStyle = 0;
        Collection<Style> activeStyles = this.getLayer().getActiveStyles();
        for (Style style : activeStyles) {
            if (style.isUserStyle()) {
                numberOfFeatureTypeStyle += ((UserStyle) style).getFeatureTypeStyles().size();
            }
        }
        // logger.info(numberOfFeatureTypeStyle + " fts");
        this.fireActionPerformed(new ActionEvent(this, 3, "Rendering start", numberOfFeatureTypeStyle * collection.size())); //$NON-NLS-1$
        int featureRenderIndex = 0;
        for (Style style : activeStyles) {
            if (this.isCancelled()) {
                return;
            }
            if (style.isUserStyle()) {
                UserStyle userStyle = (UserStyle) style;
                for (FeatureTypeStyle featureTypeStyle : userStyle.getFeatureTypeStyles()) {
                    if (this.isCancelled()) {
                        return;
                    }
                    // creating a map between each rule and the
                    // corresponding features (filtered in)
                    Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>(0);
                    if (featureTypeStyle.getRules().size() == 1 && featureTypeStyle.getRules().get(0).getFilter() == null) {
                        filteredFeatures.put(featureTypeStyle.getRules().get(0), new HashSet<IFeature>(collection));
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
                                if (rule.getFilter() == null || rule.getFilter().evaluate(feature)) {
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
                                this.render(symbolizer, feature);
                            }
                            this.fireActionPerformed(new ActionEvent(this, 4, "Rendering feature", 100 * featureRenderIndex++ //$NON-NLS-1$
                                    / (numberOfFeatureTypeStyle * collection.size())));
                        }
                    }
                }
            }
        }
        this.fireActionPerformed(new ActionEvent(this, 5, "Rendering finished", featureRenderIndex)); //$NON-NLS-1$
    }

    //  /**
    //   * Render a rule.
    //   * @param rule the rule
    //   * @param theImage the image
    //   * @param envelope the envelope
    //   * @throws RenderingException 
    //   */
    //  @SuppressWarnings({
    //      "unused", "unchecked"
    //  })
    //  private void render(final Rule rule, final BufferedImage theImage, final IEnvelope envelope) throws RenderingException {
    //    if (this.isCancelled()) {
    //      return;
    //    }
    //    // logger.info("using rule "+rule.getName());
    //    Collection<? extends IFeature> collection = this.layer.getFeatureCollection().select(envelope);
    //    if (collection == null) {
    //      return;
    //    }
    //    Collection<IFeature> filteredCollection = (Collection<IFeature>) collection;
    //    if (rule.getFilter() != null) {
    //      if (this.isCancelled()) {
    //        return;
    //      }
    //      filteredCollection = new ArrayList<IFeature>();
    //      for (IFeature feature : collection) {
    //        if (this.isCancelled()) {
    //          return;
    //        }
    //        if (rule.getFilter().evaluate(feature)) {
    //          filteredCollection.add(feature);
    //        }
    //      }
    //    }
    //    for (Symbolizer symbolizer : rule.getSymbolizers()) {
    //      this.render(symbolizer, filteredCollection);
    //    }
    //  }
    //
    //  /**
    //   * Render a symbolizer in a GL context using the given features.
    //   * @param symbolizer the symbolize
    //   * @param featureCollection the feature collection
    //   * @param theImage the image
    //   * @throws RenderingException 
    //   */
    //  private void render(final Symbolizer symbolizer, final Collection<IFeature> featureCollection) throws RenderingException {
    //    IFeature[] collection = featureCollection.toArray(new IFeature[0]);
    //    for (IFeature feature : collection) {
    //      if (this.isCancelled()) {
    //        return;
    //      }
    //      if (feature.getGeom() != null && !feature.getGeom().isEmpty()) {
    //        this.render(symbolizer, feature);
    //      }
    //    }
    //  }
    //
    /**
     * Render a feature into an image using the given symbolizer.
     * 
     * @param symbolizer
     *            the symbolizer
     * @param feature
     *            the feature
     * @param theImage
     *            the image
     */

    private void render(final Symbolizer symbolizer, final IFeature feature) throws RenderingException {
        Viewport viewport = this.getLayerViewPanel().getViewport();
        DrawingPrimitive primitive = ParameterizedConverterUtil.getConverter().convert(feature, symbolizer, viewport);
        PrimitiveRenderer symbolizerRenderer = this.getRenderer(symbolizer);
        //        WorldCoordinatesParameterizer parameterizer = new WorldCoordinatesParameterizer(viewport);
        //        primitive.generateParameterization(parameterizer);
        symbolizerRenderer.setPrimitive(primitive);
        symbolizerRenderer.setViewport(viewport);
        symbolizerRenderer.render();
    }

    //  /**
    //   * @param theImage the image
    //   * @param feature the feature
    //   * @throws RenderingException 
    //   */
    //  final void renderHook(final IFeature feature) throws RenderingException {
    //    if (this.isCancelled()) {
    //      return;
    //    }
    //    for (Style style : this.layer.getStyles()) {
    //      if (this.isCancelled()) {
    //        return;
    //      }
    //      if (style.isUserStyle()) {
    //        UserStyle userStyle = (UserStyle) style;
    //        for (FeatureTypeStyle featureTypeStyle : userStyle.getFeatureTypeStyles()) {
    //          if (this.isCancelled()) {
    //            return;
    //          }
    //          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
    //            if (this.isCancelled()) {
    //              return;
    //            }
    //            Rule rule = featureTypeStyle.getRules().get(indexRule);
    //            for (Symbolizer symbolizer : rule.getSymbolizers()) {
    //              if (rule.getFilter() == null || rule.getFilter().evaluate(feature)) {
    //                this.render(symbolizer, feature);
    //              }
    //            }
    //          }
    //        }
    //      }
    //    }
    //  }
    //
    //  /**
    //   * Render hook.
    //   * @param theImage the image to render
    //   * @param geom the geometry to render
    //   * @throws RenderingException 
    //   */
    //  final void renderHook(final IGeometry geom) throws RenderingException {
    //    if (this.isCancelled() || geom == null || geom.isEmpty()) {
    //      return;
    //    }
    //    IEnvelope envelope = geom.envelope();
    //    IDirectPosition lowerCornerPosition = envelope.getLowerCorner();
    //    lowerCornerPosition.move(-1, -1);
    //    IDirectPosition upperCornerPosition = envelope.getUpperCorner();
    //    upperCornerPosition.move(1, 1);
    //    envelope.setLowerCorner(lowerCornerPosition);
    //    envelope.setUpperCorner(upperCornerPosition);
    //    try {
    //      Point2D upperCorner = this.getLayerViewPanel().getViewport().toViewPoint(envelope.getUpperCorner());
    //      Point2D lowerCorner = this.getLayerViewPanel().getViewport().toViewPoint(envelope.getLowerCorner());
    //      this.clearImageCache((int) lowerCorner.getX(), (int) upperCorner.getY(), (int) (upperCorner.getX() - lowerCorner.getX() + 2),
    //          (int) (lowerCorner.getY() - upperCorner.getY() + 2));
    //    } catch (NoninvertibleTransformException e) {
    //      e.printStackTrace();
    //    }
    //    Collection<? extends IFeature> visibleFeatures = this.layer.getFeatureCollection().select(envelope);
    //    for (Style style : this.layer.getStyles()) {
    //      if (this.isCancelled()) {
    //        return;
    //      }
    //      if (style.isUserStyle()) {
    //        UserStyle userStyle = (UserStyle) style;
    //        for (FeatureTypeStyle featureTypeStyle : userStyle.getFeatureTypeStyles()) {
    //          if (this.isCancelled()) {
    //            return;
    //          }
    //          Map<Rule, Set<IFeature>> filteredFeatures = new HashMap<Rule, Set<IFeature>>();
    //          for (Rule rule : featureTypeStyle.getRules()) {
    //            filteredFeatures.put(rule, new HashSet<IFeature>());
    //          }
    //          for (IFeature feature : visibleFeatures) {
    //            for (Rule rule : featureTypeStyle.getRules()) {
    //              if (rule.getFilter() == null || rule.getFilter().evaluate(feature)) {
    //                filteredFeatures.get(rule).add(feature);
    //                break;
    //              }
    //            }
    //          }
    //          for (int indexRule = featureTypeStyle.getRules().size() - 1; indexRule >= 0; indexRule--) {
    //            if (this.isCancelled()) {
    //              return;
    //            }
    //            Rule rule = featureTypeStyle.getRules().get(indexRule);
    //            for (IFeature feature : filteredFeatures.get(rule)) {
    //              for (Symbolizer symbolizer : rule.getSymbolizers()) {
    //                this.render(symbolizer, feature);
    //              }
    //            }
    //          }
    //        }
    //      }
    //    }
    //  }
}
