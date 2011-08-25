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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Renderer interface.
 * @author Julien Perret
 * 
 */
public interface Renderer {
  /**
   * True is the renderer is running, i.e. if its associated runnable is
   * running, false otherwise.
   * @return true is the renderer is running, false otherwise
   * @see #createRunnable()
   */
  boolean isRendering();

  /**
   * @return true if rendering is finished, false otherwise
   */
  boolean isRendered();

  /**
   * Cancel the rendering. This method does not actually interrupt the thread
   * but lets the thread know it should stop.
   * @see Runnable
   * @see Thread
   */
  void cancel();

  /**
   * Copy the rendered image the a 2D graphics.
   * @param graphics the 2D graphics to draw into
   */
  void copyTo(Graphics2D graphics);

  /**
   * Create a runnable for the renderer. A renderer create a new image to draw
   * into. If cancel() is called, the rendering stops as soon as possible. When
   * finished, set the variable rendering to false.
   * @return a new runnable
   * @see Runnable
   * @see #cancel()
   * @see #isRendering()
   */
  Runnable createRunnable();

  /**
   * Clear the image cache, i.e. delete the current image.
   */
  void clearImageCache();

  /**
   * @param x the X coordinate of the upper left pixel of the region to clear
   * @param y the y coordinate of the upper left pixel of the region to clear
   * @param width width of the region to clear
   * @param height height of the region to clear
   */
  void clearImageCache(int x, int y, int width, int height);
}
