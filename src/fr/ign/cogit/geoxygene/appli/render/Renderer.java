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

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Julien Perret
 *
 */
public interface Renderer {

	/**
	 * True is the renderer is running, i.e. if its associated runnable is running, false otherwise.
	 * @return true is the renderer is running, false otherwise
	 * @see #createRunnable()
	 */
	public abstract boolean isRendering();
	public abstract boolean isRendered();


	/**
	 * Cancel the rendering. This method does not actually interrupt the thread but lets the thread know it should stop.
	 * @see Runnable
	 * @see Thread
	 */
	public abstract void cancel();

	/**
	 * Copy the rendered image the a 2D graphics
	 * @param graphics the 2D graphics to draw into
	 */
	public abstract void copyTo(Graphics2D graphics);

	/**
	 * Create a runnable for the renderer. A renderer create a new image to draw into. 
	 * If cancel() is called, the rendering stops as soon as possible. 
	 * When finished, set the variable rendering to false.
	 * @return a new runnable
	 * @see Runnable
	 * @see #cancel()
	 * @see #isRendering()
	 */
	public abstract Runnable createRunnable();

	/**
	 * Clear the image cache, i.e. delete the current image.
	 */
	public abstract void clearImageCache();

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public abstract void clearImageCache(int x, int y, int width, int height);

	/**
	 * @param feature
	 * @return a featureRunnable to draw the given Feature
	 */
	public abstract Runnable createFeatureRunnable(FT_Feature feature);
	/**
	 * @param geom
	 * @return a localRunnable to draw the given region
	 */
	public abstract Runnable createLocalRunnable(GM_Object geom);

}