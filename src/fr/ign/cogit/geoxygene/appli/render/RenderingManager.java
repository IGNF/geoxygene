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

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerViewPanel;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * A rendering manager responsible for rendering layers in a {@link LayerViewPanel}.
 * @author Julien Perret
 *
 */
public class RenderingManager {
	static Logger logger=Logger.getLogger(RenderingManager.class.getName());
	/**
	 * The managed {@link LayerViewPanel} panel
	 */
	LayerViewPanel layerViewPanel = null;
	/**
	 * Insertion-ordered map between a layer and its renderer.
	 */
	LinkedHashMap<Layer, LayerRenderer> rendererMap = new LinkedHashMap<Layer, LayerRenderer>();
	SelectionRenderer selectionRenderer = null;
	/**
	 * The current daemon
	 */
	private Thread daemon = null;
	/**
	 * Maximum time a daemon waits for a runnable to be added to the queue
	 */
	public static final long DAEMON_MAXIMUM_WAITING_TIME = 5000L;
	/**
	 * Time between 2 repaintings of the panel during the rendering
	 */
	private int repaintTimerDelay=400;
	/**
	 * Deque of layers
	 */
	/**
	 * Queue containg the runnables, one for each layer.
	 */
	LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();
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
						synchronized (RenderingManager.this.runnableQueue) {
							if (RenderingManager.this.runnableQueue.isEmpty()) {
								try {RenderingManager.this.runnableQueue.wait(DAEMON_MAXIMUM_WAITING_TIME);}
								catch (InterruptedException ie) {}
							}
							runnable = RenderingManager.this.runnableQueue.poll();
							if (logger.isDebugEnabled()) logger.debug(RenderingManager.this.runnableQueue.size()+" runnables in the queue"); //$NON-NLS-1$
							if (runnable == null) return;
						}
						try {runnable.run();}
						catch (Exception e) {e.printStackTrace();}
					}
				}
				finally {}
			}
		};
		newDaemon.setDaemon(true);
		return newDaemon;
	}

	/**
	 * The repaint timer used to repaint the panel during the rendering.
	 * It allows for a progressive rendering.
	 * @see Timer
	 */
	Timer repaintTimer = new Timer(this.repaintTimerDelay, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			for (Renderer renderer:getRenderers()) {
				// while a layer is being rendered, repaint the panel
				if (renderer.isRendering()) {
					RenderingManager.this.layerViewPanel.superRepaint();
					return;
				}
			}
			// when no more layer is being rendered, stop the timer
			RenderingManager.this.repaintTimer.stop();
			// repaint the panel
			RenderingManager.this.layerViewPanel.superRepaint();
		}
	});

	/**
	 * Constructor of Rendering manager.
	 * @param theLayerViewPanel the panel the rendering manager draws into
	 */
	public RenderingManager(LayerViewPanel theLayerViewPanel) {
		this.layerViewPanel = theLayerViewPanel;
		// set the repaint timer to coalesce multiple pending events in case the application does not keep up
		this.repaintTimer.setCoalesce(true);
		this.selectionRenderer = new SelectionRenderer(theLayerViewPanel);
	}

	/**
	 * Return the collection of managed renderers
	 * @return the collection of managed renderers
	 * @see LayerRenderer
	 */
	Collection<LayerRenderer> getRenderers() {return this.rendererMap.values();}

	/**
	 * Render all managed layers
	 */
	public void renderAll() {
		// if the daemon is still alive, interrupt it
		if ((this.daemon!=null)&&this.daemon.isAlive()) synchronized(this.daemon) {this.daemon.interrupt();}
		// create a new daemon 
		this.daemon = createDaemon();
		// clear the queue of runnables
		this.runnableQueue.clear();
		// start the new daemon
		this.daemon.start();
		// render all layers
		for(Renderer renderer:this.rendererMap.values()) this.render(renderer);
		this.render(this.selectionRenderer);
	}

	/**
	 * Add a new layer to the manager and create the corresponding renderer.
	 * @param layer the new layer to manage and render
	 * @see Layer
	 * @see LayerRenderer
	 */
	public void addLayer(Layer layer) {
		if (this.rendererMap.get(layer)==null) this.rendererMap.put(layer, new LayerRenderer(layer,layer.getFeatureCollection(),this.layerViewPanel));
	}

	/**
	 * Render a layer using the given renderer
	 * @param renderer the renderer to run
	 * @see LayerRenderer
	 */
	private void render(Renderer renderer) {
		// if the renderer is already rendering, interrupt the current rendering to start a new one
		if (renderer.isRendering()) renderer.cancel();
		// clear the image cache
		renderer.clearImageCache();
		// create a new runnable for the rendering
		Runnable runnable = renderer.createRunnable();
		if (runnable != null) {
			synchronized(this.runnableQueue) {
				// add it to the queue
				this.runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				this.runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!this.repaintTimer.isRunning()) {
			// repaint the panel
			this.layerViewPanel.superRepaint();
			// start the timer
			this.repaintTimer.start();
		}
	}

	/**
	 * Return the collection of managed layers in the same order they were added
	 * @return the collection of managed layers
	 * @see Layer
	 */
	public Collection<Layer> getLayers() {return this.rendererMap.keySet();}

	/**
	 * Copy the rendered images to a 2D graphics in the same order the layers were added to the manager
	 * @param destination a 2D graphics to copy the images to
	 */
	public void copyTo(Graphics2D destination) {
		for (Layer layer:this.rendererMap.keySet()) this.rendererMap.get(layer).copyTo(destination);
		this.selectionRenderer.copyTo(destination);
	}

	/**
	 * Dispose of the manager. Cleans up all threads, renderers, daemons, etc.
	 */
	public void dispose() {
		this.rendererMap.clear();
		this.repaintTimer.stop();
		this.runnableQueue.clear();
		if (this.daemon!=null) synchronized(this.daemon) {this.daemon.interrupt();}
	}

	/**
	 * @param layer
	 * @param feature
	 */
	public void render(Layer layer, FT_Feature feature) {
		Renderer renderer = this.rendererMap.get(layer);
		// if the renderer is not already finished, do nothing
		if (!renderer.isRendered()) return;
		// create a new runnable for the rendering
		Runnable runnable = renderer.createFeatureRunnable(feature);
		if (runnable != null) {
			synchronized(this.runnableQueue) {
				// add it to the queue
				this.runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				this.runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!this.repaintTimer.isRunning()) {
			// repaint the panel
			this.layerViewPanel.superRepaint();
			// start the timer
			this.repaintTimer.start();
		}
	}

	/**
	 * @param layer
	 * @param geom
	 */
	public void render(Layer layer, GM_Object geom) {
		Renderer renderer = this.rendererMap.get(layer);
		// if the renderer is not already finished, do nothing
		if (!renderer.isRendered()) return;
		// create a new runnable for the rendering
		Runnable runnable = renderer.createLocalRunnable(geom);
		if (runnable != null) {
			synchronized(this.runnableQueue) {
				// add it to the queue
				this.runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				this.runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!this.repaintTimer.isRunning()) {
			// repaint the panel
			this.layerViewPanel.superRepaint();
			// start the timer
			this.repaintTimer.start();
		}
	}
}
