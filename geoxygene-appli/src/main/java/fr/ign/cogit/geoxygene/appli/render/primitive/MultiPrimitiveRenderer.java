/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;

/**
 * @author JeT
 * This renderer calls all registered renderer in the insertion order. It allows to draw
 * multiple layers with different renderers.
 * TODO: If inputs are added to this renderer, they are automatically added to stored renderers too
 * but as inputs are never cleared before rendering, It may grow quickly... Internal use of Set<> in
 * AbstractPrimitiveRenderer should avoid the problem, but another implementation won't work... To Be Checked
 */
public class MultiPrimitiveRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(MultiPrimitiveRenderer.class.getName());

  private final List<PrimitiveRenderer> renderers = new ArrayList<PrimitiveRenderer>();

  /**
   * Constructor
   */
  public MultiPrimitiveRenderer() {
  }

  /**
   * Add a renderer to the collection
   * @param primitiveRenderer
   */
  public void add(final PrimitiveRenderer primitiveRenderer) {
    this.renderers.add(primitiveRenderer);
    primitiveRenderer.setViewport(this.getViewport());
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.AbstractPrimitiveRenderer#setViewport(fr.ign.cogit.geoxygene.appli.Viewport)
   */
  @Override
  public void setViewport(final Viewport viewport) {
    super.setViewport(viewport);
    for (PrimitiveRenderer renderer : this.renderers) {
      renderer.setViewport(viewport);
    }
  }

  /**
   * empty the renderer collection
   */
  public void removeAll() {
    this.renderers.clear();
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#render()
   */
  /**
   * Loop over all registered renderers and call their rendering method.
   * Traversal is done in the insertion order
   * 
   * @throws RenderingException
   */
  @Override
  public void render() throws RenderingException {
    if (this.getViewport() == null) {
      throw new RenderingException("viewport is not set");
    }
    // add current inputs to all renderers inputs 
    for (PrimitiveRenderer renderer : this.renderers) {
      renderer.addPrimitives(this.getPrimitives());
      renderer.render();
    }
  }

  @Override
  public void initializeRendering() throws RenderingException {
    // nothing to initialize

  }

  @Override
  public void finalizeRendering() throws RenderingException {
    // nothing to finalize
  }

}
