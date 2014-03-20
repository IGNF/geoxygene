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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.util.Pair;

/**
 * A renderer to render a {@link Layer} into a {@link LayerViewPanel}.
 * 
 * @author Julien Perret
 * @see RenderingManager
 * @see Layer
 * @see LayerViewPanel
 */
public abstract class AbstractLayerRenderer implements LayerRenderer {
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Constructor of renderer using a {@link Layer} and a
     * {@link LayerViewPanel}.
     * 
     * @param theLayer
     *            a layer to render
     * @param theLayerViewPanel
     *            the panel to draws into
     */
    public AbstractLayerRenderer(final Layer theLayer, final LayerViewPanel theLayerViewPanel) {
        this.layer = theLayer;
        this.setLayerViewPanel(theLayerViewPanel);
    }

    /** Layer to render. */
    private Layer layer = null;

    /** @return the Layer to render. */
    @Override
    public Layer getLayer() {
        return this.layer;
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

    /** Layer view panel. */
    private LayerViewPanel layerViewPanel = null;

    /**
     * Set the layer view panel.
     * 
     * @param theLayerViewPanel
     *            the layer view panel
     */
    public final void setLayerViewPanel(final LayerViewPanel theLayerViewPanel) {
        this.layerViewPanel = theLayerViewPanel;
    }

    /** @return the layer view panel */
    public final LayerViewPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    /** True if rendering is finished. */
    private volatile boolean rendered = false;

    /**
     * @param isRendered
     *            True if rendering is finished, false otherwise
     */
    public final void setRendered(final boolean isRendered) {
        this.rendered = isRendered;
    }

    /** @return true if rendering is finished, false otherwise */
    @Override
    public final boolean isRendered() {
        return this.rendered;
    }

    /** True if rendering is cancelled. */
    private volatile boolean cancelled = false;

    /**
     * @param isCancelled
     *            True if rendering is cancelled, false otherwise
     */
    public final void setCancelled(final boolean isCancelled) {
        this.cancelled = isCancelled;
    }

    /** @return True if rendering is cancelled, false otherwise. */
    public final boolean isCancelled() {
        return this.cancelled;
    }

    /** True if rendering is ongoing. */
    private volatile boolean rendering = false;

    /**
     * True is the renderer is running, i.e. if its associated runnable is
     * running, false otherwise.
     * 
     * @return true is the renderer is running, false otherwise
     * @see #createRunnable()
     * @see #renderHook(BufferedImage,IEnvelope)
     */
    @Override
    public final boolean isRendering() {
        return this.rendering;
    }

    /**
     * @param isRendering
     *            True if rendering is ongoing
     */
    public final void setRendering(final boolean isRendering) {
        this.rendering = isRendering;
    }

    /**
     * Cancel the rendering. This method does not actually interrupt the thread
     * but lets the thread know it should stop.
     * 
     * @see Runnable
     * @see Thread
     */
    @Override
    public final void cancel() {
        this.setCancelled(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.Renderer#initializeRendering()
     */
    @Override
    public void initializeRendering() {
        // nothing special to initialize
    }

    /**
     * generate the list of features to render depending on the SLD and the
     * viewing are (envelope)
     * 
     * @return a collection of symbolizer associated with feature or null if
     *         cancelled
     */
    protected List<Pair<Symbolizer, IFeature>> generateFeaturesToRender(IEnvelope envelope) {
        List<Pair<Symbolizer, IFeature>> featuresToRender = new ArrayList<Pair<Symbolizer, IFeature>>();

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
                return null;
            }
            if (style.isUserStyle()) {
                UserStyle userStyle = (UserStyle) style;
                for (FeatureTypeStyle featureTypeStyle : userStyle.getFeatureTypeStyles()) {
                    if (this.isCancelled()) {
                        return null;
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
                            return null;
                        }
                        Rule rule = featureTypeStyle.getRules().get(indexRule);
                        for (IFeature feature : filteredFeatures.get(rule)) {
                            if (this.isCancelled()) {
                                return null;
                            }
                            for (Symbolizer symbolizer : rule.getSymbolizers()) {
                                if (this.isCancelled()) {
                                    return null;
                                }
                                featuresToRender.add(new Pair<Symbolizer, IFeature>(symbolizer, feature));
                            }
                            this.fireActionPerformed(new ActionEvent(this, 4, "Rendering feature", 100 * featureRenderIndex++ //$NON-NLS-1$
                                    / (numberOfFeatureTypeStyle * collection.size())));
                        }
                    }
                }
            }
        }
        return featuresToRender;
    }

}
