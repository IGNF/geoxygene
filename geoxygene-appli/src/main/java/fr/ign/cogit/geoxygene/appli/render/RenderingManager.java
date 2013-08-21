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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * A rendering manager responsible for rendering layers in a
 * {@link LayerViewPanel}.
 * @author Julien Perret
 */
public class RenderingManager {
  /**
   * The logger.
   */
  private static final Logger LOGGER = Logger.getLogger(RenderingManager.class
      .getName());

  /**
   * @return The logger
   */
  public static Logger getLogger() {
    return RenderingManager.LOGGER;
  }

  /**
   * The managed {@link LayerViewPanel} panel.
   */
  private LayerViewPanel layerViewPanel = null;

  /**
   * Set the managed {@link LayerViewPanel} panel.
   * @param aLayerViewPanel the managed {@link LayerViewPanel} panel.
   */
  public final void setLayerViewPanel(final LayerViewPanel aLayerViewPanel) {
    this.layerViewPanel = aLayerViewPanel;
  }

  /**
   * @return The managed {@link LayerViewPanel} panel.
   */
  public final LayerViewPanel getLayerViewPanel() {
    return this.layerViewPanel;
  }

  /**
   * Insertion-ordered map between a layer and its renderer.
   */
  private LinkedHashMap<Layer, LayerRenderer> rendererMap = new LinkedHashMap<Layer, LayerRenderer>();
  /**
   * The selection renderer used to render the selected features.
   */
  private SelectionRenderer selectionRenderer = null;

  /**
   * @return The selection renderer used to render the selected features
   */
  public SelectionRenderer getSelectionRenderer() {
    return this.selectionRenderer;
  }

  /**
   * The current daemon.
   */
  private Thread daemon = null;
  /**
   * Maximum time a daemon waits for a runnable to be added to the queue.
   */
  public static final long DAEMON_MAXIMUM_WAITING_TIME = 5000L;
  /**
   * Queue containg the runnables, one for each layer.
   */
  private LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();

  /**
   * @return The queue containg the runnables, one for each layer.
   */
  public final LinkedBlockingQueue<Runnable> getRunnableQueue() {
    return this.runnableQueue;
  }

  /**
   * Create a new daemon.
   * @return the new daemon
   * @see Thread
   */
  private Thread createDaemon() {
    Thread newDaemon = new Thread() {
      @Override
      public void run() {
        try {
          for (;;) {
            Runnable runnable;
            synchronized (RenderingManager.this.getRunnableQueue()) {
              if (RenderingManager.this.getRunnableQueue().isEmpty()) {
                try {
                  RenderingManager.this.getRunnableQueue().wait(
                      RenderingManager.DAEMON_MAXIMUM_WAITING_TIME);
                } catch (InterruptedException ie) {
                  if (RenderingManager.getLogger().isDebugEnabled()) {
                    RenderingManager.getLogger().debug(ie.getMessage());
                    // ie.printStackTrace();
                  }
                }
              }
              runnable = RenderingManager.this.getRunnableQueue().poll();
              if (RenderingManager.getLogger().isDebugEnabled()) {
                RenderingManager.getLogger().debug(
                    RenderingManager.this.getRunnableQueue().size()
                        + " runnables in the queue" //$NON-NLS-1$
                );
              }
              if (runnable == null) {
                return;
              }
            }
            try {
              runnable.run();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        } finally {
          if (RenderingManager.getLogger().isDebugEnabled()) {
            RenderingManager.getLogger().debug("Deamon thread finished"); //$NON-NLS-1$
          }
          // RenderingManager.this.getLayerViewPanel().superRepaint();
        }
      }
    };
    newDaemon.setDaemon(true);
    return newDaemon;
  }

  /**
   * Constructor of Rendering manager.
   * @param theLayerViewPanel the panel the rendering manager draws into
   */
  public RenderingManager(final LayerViewPanel theLayerViewPanel) {
    this.setLayerViewPanel(theLayerViewPanel);
    this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
  }

  /**
   * Return the collection of managed renderers.
   * @return the collection of managed renderers
   * @see LayerRenderer
   */
  public final Collection<LayerRenderer> getRenderers() {
    return this.rendererMap.values();
  }

  /**
   * Render all managed layers.
   */
  public final void renderAll() {
    // if the daemon is still alive, interrupt it
    if ((this.daemon != null) && this.daemon.isAlive()) {
      synchronized (this.daemon) {
        this.daemon.interrupt();
      }
    }
    // create a new daemon
    this.daemon = this.createDaemon();
    // clear the queue of runnables
    this.getRunnableQueue().clear();
    // start the new daemon
    this.daemon.start();
    // render all layers
    for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld()
        .getLayers()) {
      if (layer.isVisible()) {
        this.render(this.rendererMap.get(layer));
      }
    }
    this.render(this.selectionRenderer);
  }

  /**
   * Add a new layer to the manager and create the corresponding renderer.
   * @param layer the new layer to manage and render
   * @see Layer
   * @see LayerRenderer
   */
  public final void addLayer(final Layer layer) {
    if (this.rendererMap.get(layer) == null) {
      LayerRenderer renderer = new LayerRenderer(layer,
          this.getLayerViewPanel());
      this.rendererMap.put(layer, renderer);
      // Adding the layer legend panel to the listeners of the renderer
      renderer.addActionListener(this.getLayerViewPanel().getProjectFrame()
          .getLayerLegendPanel());
    }
  }

  /**
   * Remove a layer from the manager.
   * @param layer the layer to remove
   * @see Layer
   * @see LayerRenderer
   */
  public final void removeLayer(final Layer layer) {
    if (this.rendererMap.get(layer) == null) {
      return;
    }
    this.rendererMap.remove(layer);
  }

  /**
   * Render a layer using the given renderer.
   * @param renderer the renderer to run
   * @see LayerRenderer
   */
  public void render(final Renderer renderer) {
    // if the renderer is already rendering, interrupt the current
    // rendering to start a new one
    if (renderer == null) {
      return;
    }
    if (renderer.isRendering()) {
      renderer.cancel();
    }
    // clear the image cache
    renderer.clearImageCache();
    // create a new runnable for the rendering
    Runnable runnable = renderer.createRunnable();
    if (runnable != null) {
      synchronized (this.getRunnableQueue()) {
        // add it to the queue
        this.getRunnableQueue().add(runnable);
        // notify the queue which should wake the daemon up as it
        // should be waiting on it
        this.getRunnableQueue().notify();
      }
    }
  }

  /**
   * Render a layer.
   * @param layerName the name of the layer to run
   * @see LayerRenderer
   */
  public void render(final String layerName) {
    for (Layer layer : this.getLayers()) {
      if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
        this.render(this.getRenderer(layer));
        return;
      }
    }
  }

  /**
   * Return the collection of managed layers in the same order they were added.
   * @return the collection of managed layers
   * @see Layer
   */
  public final Collection<Layer> getLayers() {
    return this.rendererMap.keySet();
  }

  public final Renderer getRenderer(Layer layer) {
    return this.rendererMap.get(layer);
  }

  /**
   * Copy the rendered images to a 2D graphics in the same order the layers were
   * added to the manager.
   * @param destination a 2D graphics to copy the images to
   */
  public final void copyTo(final Graphics2D destination) {
    for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld()
        .getLayers()) {
      if (layer.getOpacity() > 0.0d && this.rendererMap.get(layer) != null) {
        this.rendererMap.get(layer).copyTo(destination);
      }
    }
    this.selectionRenderer.copyTo(destination);
  }

  /**
   * 
   * Dispose of the manager. Cleans up all threads, renderers, daemons, etc.
   */
  public final void dispose() {
    this.rendererMap.clear();
    this.getRunnableQueue().clear();
    if (this.daemon != null) {
      synchronized (this.daemon) {
        this.daemon.interrupt();
      }
    }
  }

  public void repaint() {
    if (RenderingManager.LOGGER.isDebugEnabled()) {
      RenderingManager.LOGGER.debug(this.getRenderers().size() + " renderers"); //$NON-NLS-1$
    }
    // we check if there is still something being rendererd
    // the fastest way is to check for renderers in the queue
    if (!this.getRunnableQueue().isEmpty()) {
      return;
    }
    // then we check if there is still a renderer working
    for (Renderer r : this.getRenderers()) {
      if (r.isRendering() || !r.isRendered()) {
        RenderingManager.LOGGER.debug("Renderer " + r.isRendering() + " - " //$NON-NLS-1$ //$NON-NLS-2$
            + r.isRendered());
        return;
      }
    }
    if (this.selectionRenderer != null
        && (this.selectionRenderer.isRendering() || !this.selectionRenderer
            .isRendered())) {
      if (RenderingManager.LOGGER.isDebugEnabled()) {
        RenderingManager.LOGGER.debug("Renderer " //$NON-NLS-1$
            + this.selectionRenderer.isRendering() + " - " //$NON-NLS-1$
            + this.selectionRenderer.isRendered());
      }
      return;
    }
    if (RenderingManager.LOGGER.isDebugEnabled()) {
      RenderingManager.LOGGER.debug("Repaint"); //$NON-NLS-1$
    }
    // nothing is being rendered, we can actually repaint the panel
    RenderingManager.this.getLayerViewPanel().superRepaint();
  }

  public boolean isRendering() {
    // we check if there is still something being rendererd
    // the fastest way is to check for renderers in the queue
    if (!this.getRunnableQueue().isEmpty()) {
      return true;
    }
    // then we check if there is still a renderer working
    for (Renderer r : this.getRenderers()) {
      if (r.isRendering() || !r.isRendered()) {
        return true;
      }
    }
    return false;
  }
}
