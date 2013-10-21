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

import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * A rendering manager responsible for rendering layers in a
 * {@link LayerViewPanel}.
 * @author Julien Perret
 */
public class MultithreadedRenderingManager implements RenderingManager {

  private static final Logger LOGGER = Logger
      .getLogger(MultithreadedRenderingManager.class.getName()); // logger

  private LayerViewPanel layerViewPanel = null; // managed LayerViewPanel

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#
   * setLayerViewPanel(fr.ign.cogit.geoxygene.appli.layerview.LayerViewPanel)
   */
  @Override
  public final void setLayerViewPanel(final LayerViewPanel aLayerViewPanel) {
    this.layerViewPanel = aLayerViewPanel;
  }

  /** @return The managed {@link LayerViewPanel} panel. */
  public final LayerViewPanel getLayerViewPanel() {
    return this.layerViewPanel;
  }

  /** Insertion-ordered map between a layer and its renderer. */
  private final LinkedHashMap<Layer, AwtLayerRenderer> rendererMap = new LinkedHashMap<Layer, AwtLayerRenderer>();
  /** The selection renderer used to render the selected features. */
  private SelectionRenderer selectionRenderer = null;

  /** @return The selection renderer used to render the selected features */
  @Override
  public SelectionRenderer getSelectionRenderer() {
    return this.selectionRenderer;
  }

  /** The current daemon. */
  private Thread daemon = null;
  /** Maximum time a daemon waits for a runnable to be added to the queue. */
  public static final long DAEMON_MAXIMUM_WAITING_TIME = 5000L;
  /** Queue containg the runnables, one for each layer. */
  private final LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();

  /** @return The queue containg the runnables, one for each layer. */
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
            synchronized (MultithreadedRenderingManager.this.getRunnableQueue()) {
              if (MultithreadedRenderingManager.this.getRunnableQueue()
                  .isEmpty()) {
                try {
                  MultithreadedRenderingManager.this
                      .getRunnableQueue()
                      .wait(
                          MultithreadedRenderingManager.DAEMON_MAXIMUM_WAITING_TIME);
                } catch (InterruptedException ie) {
                  if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(ie.getMessage());
                    // ie.printStackTrace();
                  }
                }
              }
              runnable = MultithreadedRenderingManager.this.getRunnableQueue()
                  .poll();
              if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(MultithreadedRenderingManager.this
                        .getRunnableQueue().size() + " runnables in the queue" //$NON-NLS-1$
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
          if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Deamon thread finished"); //$NON-NLS-1$
          }
          // MultithreadedRenderingManager.this.getLayerViewPanel().superRepaint();
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
  public MultithreadedRenderingManager(final LayerViewPanel theLayerViewPanel) {
    this.setLayerViewPanel(theLayerViewPanel);
    this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getRenderers
   * ()
   */
  @Override
  public final Collection<AwtLayerRenderer> getRenderers() {
    return this.rendererMap.values();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#renderAll
   * ()
   */
  @Override
  public final void renderAll() {
    
    LOGGER.debug("RenderAll()");
    
    // if the daemon is still alive, interrupt it
    if (this.daemon != null && this.daemon.isAlive()) {
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

    synchronized (this.getLayerViewPanel().getProjectFrame().getSld()) {
      // render all layers
      for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld()
          .getLayers()) {
        if (layer.isVisible()) {
          this.render(this.rendererMap.get(layer));
        }
      }
    }
    this.render(this.selectionRenderer);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#addLayer
   * (fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final void addLayer(final Layer layer) {
    synchronized (rendererMap) {
      if (this.rendererMap.get(layer) == null) {
        AwtLayerRenderer renderer = new AwtLayerRenderer(layer,
            this.getLayerViewPanel());
        this.rendererMap.put(layer, renderer);
        // Adding the layer legend panel to the listeners of the renderer
        renderer.addActionListener(this.getLayerViewPanel().getProjectFrame()
            .getLayerLegendPanel());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#removeLayer
   * (fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final void removeLayer(final Layer layer) {
    synchronized (rendererMap) {
      if (this.rendererMap.get(layer) == null) {
        return;
      }
      this.rendererMap.remove(layer);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render
   * (fr.ign.cogit.geoxygene.appli.render.Renderer)
   */
  @Override
  public void render(final LayerRenderer renderer) {
    
    // if the renderer is already rendering, interrupt the current
    // rendering to start a new one
    if (renderer == null) {
      LOGGER.warn("Rendering process requested but no renderer has been set");
      return;
    }
    LOGGER.trace("rendering process for " + renderer.getClass());
    
    if (renderer.isRendering()) {
      renderer.cancel();
    }
    
    // clear the image cache
    renderer.initializeRendering();
    
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#render
   * (java.lang.String)
   */
  @Override
  public void render(final String layerName) {
    for (Layer layer : this.getLayers()) {
      if (layer.getName().equalsIgnoreCase(layerName.toLowerCase())) {
        this.render(this.getRenderer(layer));
        return;
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getLayers
   * ()
   */
  @Override
  public final Collection<Layer> getLayers() {
    return this.rendererMap.keySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#getRenderer
   * (fr.ign.cogit.geoxygene.style.Layer)
   */
  @Override
  public final LayerRenderer getRenderer(final Layer layer) {
    return this.rendererMap.get(layer);
  }

  /**
   * Copy the rendered images to a 2D graphics in the same order the layers were
   * added to the manager.
   * @param destination a 2D graphics to copy the images to
   */
  public final void copyTo(final Graphics2D destination) {
    synchronized (this.getLayerViewPanel().getProjectFrame().getSld()) {
      for (Layer layer : this.getLayerViewPanel().getProjectFrame().getSld()
          .getLayers()) {
        AwtLayerRenderer layerRenderer = this.rendererMap.get(layer);
        if (layer.getOpacity() > 0.0d && layerRenderer != null) {
          layerRenderer.copyTo(destination);
        }
      }
      this.selectionRenderer.copyTo(destination);
    }
  }

  /** Dispose of the manager. Cleans up all threads, renderers, daemons, etc. */
  @Override
  public final void dispose() {
    synchronized (rendererMap) {
      this.rendererMap.clear();
    }
    this.getRunnableQueue().clear();
    if (this.daemon != null) {
      synchronized (this.daemon) {
        this.daemon.interrupt();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#repaint()
   */
  @Override
  public void repaint() {
    // we check if there is still something being rendered
    // the fastest way is to check for renderers in the queue
    if (!this.getRunnableQueue().isEmpty()) {
      return;
    }
    // then we check if there is still a renderer working
    for (LayerRenderer r : this.getRenderers()) {
      if (r.isRendering() || !r.isRendered()) {
        return;
      }
    }
    if (this.selectionRenderer != null
        && (this.selectionRenderer.isRendering() || !this.selectionRenderer
            .isRendered())) {
      return;
    }
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Repaint"); //$NON-NLS-1$
    }
    // nothing is being rendered, we can actually repaint the panel
    MultithreadedRenderingManager.this.getLayerViewPanel().superRepaint();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager#isRendering
   * ()
   */
  @Override
  public boolean isRendering() {
    // we check if there is still something being rendered
    // the fastest way is to check for renderers in the queue
    if (!this.getRunnableQueue().isEmpty()) {
      return true;
    }
    // then we check if there is still a renderer working
    for (LayerRenderer r : this.getRenderers()) {
      if (r.isRendering() || !r.isRendered()) {
        return true;
      }
    }
    return false;
  }
}
