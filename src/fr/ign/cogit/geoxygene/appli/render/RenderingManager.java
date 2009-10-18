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
	private LayerViewPanel layerViewPanel = null;
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
	private LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();
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
						synchronized (runnableQueue) {
							if (runnableQueue.isEmpty()) {
								try {runnableQueue.wait(DAEMON_MAXIMUM_WAITING_TIME);}
								catch (InterruptedException ie) {}
							}
							runnable = runnableQueue.poll();
							if (logger.isDebugEnabled()) logger.debug(runnableQueue.size()+" runnables in the queue");
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
	private Timer repaintTimer = new Timer(repaintTimerDelay, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			for (Renderer renderer:getRenderers()) {
				// while a layer is being rendered, repaint the panel
				if (renderer.isRendering()) {
					layerViewPanel.superRepaint();
					return;
				}
			}
			// when no more layer is being rendered, stop the timer
			repaintTimer.stop();
			// repaint the panel
			layerViewPanel.superRepaint();
		}
	});

	/**
	 * Constructor of Rendering manager.
	 * @param theLayerViewPanel the panel the rendering manager draws into
	 */
	public RenderingManager(LayerViewPanel theLayerViewPanel) {
		this.layerViewPanel = theLayerViewPanel;
		// set the repaint timer to coalesce multiple pending events in case the application does not keep up
		repaintTimer.setCoalesce(true);
		selectionRenderer = new SelectionRenderer(theLayerViewPanel);
	}

	/**
	 * Return the collection of managed renderers
	 * @return the collection of managed renderers
	 * @see LayerRenderer
	 */
	private Collection<LayerRenderer> getRenderers() {return rendererMap.values();}

	/**
	 * Render all managed layers
	 */
	public void renderAll() {
		// if the daemon is still alive, interrupt it
		if ((daemon!=null)&&daemon.isAlive()) synchronized(daemon) {daemon.interrupt();}
		// create a new daemon 
		daemon = createDaemon();
		// clear the queue of runnables
		runnableQueue.clear();
		// start the new daemon
		daemon.start();
		// render all layers
		for(Renderer renderer:rendererMap.values()) this.render(renderer);
		this.render(selectionRenderer);
	}

	/**
	 * Add a new layer to the manager and create the corresponding renderer.
	 * @param layer the new layer to manage and render
	 * @see Layer
	 * @see LayerRenderer
	 */
	public void addLayer(Layer layer) {
		if (rendererMap.get(layer)==null) rendererMap.put(layer, new LayerRenderer(layer,layer.getFeatureCollection(),layerViewPanel));
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
			synchronized(runnableQueue) {
				// add it to the queue
				runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!repaintTimer.isRunning()) {
			// repaint the panel
			layerViewPanel.superRepaint();
			// start the timer
			repaintTimer.start();
		}
	}

	/**
	 * Return the collection of managed layers in the same order they were added
	 * @return the collection of managed layers
	 * @see Layer
	 */
	public Collection<Layer> getLayers() {return rendererMap.keySet();}

	/**
	 * Copy the rendered images to a 2D graphics in the same order the layers were added to the manager
	 * @param destination a 2D graphics to copy the images to
	 */
	public void copyTo(Graphics2D destination) {
		for (Layer layer:rendererMap.keySet()) rendererMap.get(layer).copyTo(destination);
		selectionRenderer.copyTo(destination);
	}

	/**
	 * Dispose of the manager. Cleans up all threads, renderers, daemons, etc.
	 */
	public void dispose() {
		rendererMap.clear();
		repaintTimer.stop();
		runnableQueue.clear();
		if (daemon!=null) synchronized(daemon) {daemon.interrupt();}
	}

	/**
	 * @param layer
	 * @param feature
	 */
	public void render(Layer layer, FT_Feature feature) {
		Renderer renderer = rendererMap.get(layer);
		// if the renderer is not already finished, do nothing
		if (!renderer.isRendered()) return;
		// create a new runnable for the rendering
		Runnable runnable = renderer.createFeatureRunnable(feature);
		if (runnable != null) {
			synchronized(runnableQueue) {
				// add it to the queue
				runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!repaintTimer.isRunning()) {
			// repaint the panel
			layerViewPanel.superRepaint();
			// start the timer
			repaintTimer.start();
		}
	}

	/**
	 * @param layer
	 * @param geom
	 */
	public void render(Layer layer, GM_Object geom) {
		Renderer renderer = rendererMap.get(layer);
		// if the renderer is not already finished, do nothing
		if (!renderer.isRendered()) return;
		// create a new runnable for the rendering
		Runnable runnable = renderer.createLocalRunnable(geom);
		if (runnable != null) {
			synchronized(runnableQueue) {
				// add it to the queue
				runnableQueue.add(runnable);
				// notify the queue which should wake the daemon up as it should be waiting on it
				runnableQueue.notify();
			}
		}
		// if the repaint timer is not running yet, start it
		if (!repaintTimer.isRunning()) {
			// repaint the panel
			layerViewPanel.superRepaint();
			// start the timer
			repaintTimer.start();
		}
	}
}
