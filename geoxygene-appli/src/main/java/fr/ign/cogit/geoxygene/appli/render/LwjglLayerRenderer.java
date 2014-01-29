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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GLContext;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldTexture;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.primitive.BasicParameterizer;
import fr.ign.cogit.geoxygene.appli.render.primitive.FeatureRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GL4FeatureRenderer;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.style.gl.DistanceFieldTexturedPolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.gl.TexturedPolygonSymbolizer;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

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
    //    private final Map<String, PrimitiveRenderer> renderers = new HashMap<String, PrimitiveRenderer>();
    //    private PrimitiveRenderer defaultRenderer = null;
    //    private final DensityFieldPrimitiveRenderer densityFieldPrimitiveRenderer = new DensityFieldPrimitiveRenderer();
    private GL4FeatureRenderer gl4Renderer = null;

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

        this.initRenderers();
    }

    private final void initRenderers() {
        this.gl4Renderer = new GL4FeatureRenderer(this);
        //        this.defaultRenderer = new GLPrimitivePointRenderer();
    }

    /**
     * TODO: This method should not exist anymore when the SLD will describe
     * which renderer has to be used for which feature/symbolizer
     * 
     * @return the renderer
     */
    private static boolean onlyOneWarning = false;

    public FeatureRenderer getLayerRenderer() {
        return this.gl4Renderer; // FIXME: GL4 development purpose only
        //        String layerName = this.getLayer().getName();
        //        //        if (layerName.compareToIgnoreCase("SURFACE_ROUTE") == 0 || layerName.compareToIgnoreCase("SURFACE_EAU") == 0) {
        //        if (layerName.compareToIgnoreCase("mer") == 0 || layerName.compareToIgnoreCase("PISTE_AERODROME") == 0
        //                || layerName.compareToIgnoreCase("mer_sans_sable") == 0 || layerName.compareToIgnoreCase("sable_humide") == 0) {
        //            //            if (layerName.compareToIgnoreCase("mer") == 0 || layerName.compareToIgnoreCase("sable_humide") == 0) {
        //            if (!onlyOneWarning) {
        //                logger.warn("Special primitive renderer used for layer named " + layerName);
        //                onlyOneWarning = true;
        //            }
        //            return this.densityFieldPrimitiveRenderer;
        //        }
        //
        //        if (this.renderers.containsKey(layerName)) {
        //            return this.renderers.get(layerName);
        //        }
        //
        //        System.err.println("layer name = " + layerName);
        //        File defaultRenderingScriptFile = new File("src/main/resources/scripts/defaultRenderer.groovy");
        //        File layerRenderingScriptFile = new File("src/main/resources/scripts/" + layerName + "-renderer.groovy");
        //        if (!layerRenderingScriptFile.isFile()) {
        //            try {
        //                Files.copy(defaultRenderingScriptFile, layerRenderingScriptFile);
        //                logger.info("copy default script to " + layerRenderingScriptFile);
        //            } catch (IOException e) {
        //                logger.error("Cannot copy " + defaultRenderingScriptFile + " to " + layerRenderingScriptFile);
        //                e.printStackTrace();
        //                return null;
        //            }
        //        }
        //        try {
        //            ScriptingPrimitiveRenderer scriptingPrimitiveRenderer = new ScriptingPrimitiveRenderer(layerRenderingScriptFile);
        //            scriptingPrimitiveRenderer.initializeRendering();
        //            this.renderers.put(layerName, scriptingPrimitiveRenderer);
        //            return scriptingPrimitiveRenderer;
        //        } catch (Exception e) {
        //            logger.error("Cannot load groovy script file " + layerRenderingScriptFile);
        //            e.printStackTrace();
        //            return null;
        //        }
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
        //        System.err.println("initialize Layer before rendering");
        //        for (PrimitiveRenderer renderer : this.renderers.values()) {
        //            try {
        //                renderer.initializeRendering();
        //            } catch (RenderingException e) {
        //                e.printStackTrace();
        //            }
        //        }
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
                                /// ////////////////////////////////////////////////////////////////////////////////////////////
                                // FIXME: find a way to integrate/describe it into the SLD
                                // bypass given symbolizers for some layers
                                if (this.getLayer().getName().equals("PISTE_AERODROME")) {
                                    symbolizer = this.generateDistanceFieldTexturedPolygonSymbolizer(this.getLayerViewPanel().getViewport(), feature);
                                    //                                    symbolizer = this.generateTexturedPolygonSymbolizer(this.getLayerViewPanel().getViewport(), feature);
                                }
                                if (this.getLayer().getName().equals("mer_sans_sable")) {
                                    symbolizer = this.generateDistanceFieldTexturedPolygonSymbolizer(this.getLayerViewPanel().getViewport(), feature);
                                    //                                    symbolizer = this.generateTexturedPolygonSymbolizer(this.getLayerViewPanel().getViewport(), feature);
                                }
                                // FIXME: find a way to integrate/describe it into the SLD
                                // \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
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

    /**
     * @param feature
     * @param viewport
     * @return
     */
    private DistanceFieldTexturedPolygonSymbolizer generateDistanceFieldTexturedPolygonSymbolizer(Viewport viewport, IFeature feature) {
        DistanceFieldTexturedPolygonSymbolizer polygonSymbolizer = new DistanceFieldTexturedPolygonSymbolizer(feature, viewport);
        DistanceFieldTexture texture = new DistanceFieldTexture(viewport, feature);
        texture.setTextureToApply(new BasicTexture("./src/main/resources/textures/mer cassini.png"));
        texture.setUScale(10);
        texture.setVScale(10);
        polygonSymbolizer.setTexture(texture);
        return polygonSymbolizer;
    }

    /**
     * @param feature
     * @param viewport
     * @return
     */
    private TexturedPolygonSymbolizer generateTexturedPolygonSymbolizer(Viewport viewport, IFeature feature) {
        TexturedPolygonSymbolizer polygonSymbolizer = new TexturedPolygonSymbolizer(feature, viewport);
        BasicParameterizer parameterizer = new BasicParameterizer(feature);
        parameterizer.scaleX(10);
        parameterizer.scaleY(10);
        polygonSymbolizer.setParameterizer(parameterizer);
        BasicTexture texture = new BasicTexture();
        texture.setTextureFilename("./src/main/resources/textures/dense pine forest.jpg");
        polygonSymbolizer.setTexture(texture);
        return polygonSymbolizer;
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
     */

    private void render(final Symbolizer symbolizer, final IFeature feature) throws RenderingException {
        Viewport viewport = this.getLayerViewPanel().getViewport();
        FeatureRenderer renderer = this.getLayerRenderer();
        renderer.render(feature, symbolizer, viewport);
    }

    @Override
    public void reset() {
        this.gl4Renderer.reset();
    }

}
