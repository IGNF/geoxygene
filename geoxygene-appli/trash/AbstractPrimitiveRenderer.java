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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.appli.Viewport;

/**
 * @author JeT
 */
public abstract class AbstractPrimitiveRenderer implements PrimitiveRenderer {

  private Viewport viewport = null;
  private Set<DrawingPrimitive> primitives = new HashSet<DrawingPrimitive>();

  /**
   * Constructor
   */
  public AbstractPrimitiveRenderer() {
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#setViewport(fr.ign.cogit.geoxygene.appli.Viewport)
   */
  @Override
  public void setViewport(final Viewport viewport) {
    this.viewport = viewport;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#addPrimitive(fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitive)
   */
  @Override
  public void addPrimitive(final DrawingPrimitive primitive) {
    this.primitives.add(primitive);
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#addPrimitive(fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitive)
   */
  @Override
  public void setPrimitive(final DrawingPrimitive primitive) {
    this.primitives.clear();
    this.primitives.add(primitive);
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#addPrimitive(fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitive)
   */
  @Override
  public void removeAllPrimitives() {
    this.primitives.clear();
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#addPrimitives(java.util.Collection)
   */
  @Override
  public void addPrimitives(final Collection<? extends DrawingPrimitive> primitives) {
    this.primitives.addAll(primitives);
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#addPrimitives(java.util.Collection)
   */
  @Override
  public void setPrimitives(final Collection<? extends DrawingPrimitive> primitives) {
    this.primitives.clear();
    this.primitives.addAll(primitives);
  }

  /**
   * @return the viewport
   */
  @Override
  public Viewport getViewport() {
    return this.viewport;
  }

  /**
   * @return the primitives
   */
  @Override
  public Collection<DrawingPrimitive> getPrimitives() {
    return this.primitives;
  }

  //  /* (non-Javadoc)
  //   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#initializeRendering()
  //   */
  //  @Override
  //  public void initializeRendering() throws RenderingException {
  //    // default behavior is empty
  //  }
  //
  //  /* (non-Javadoc)
  //   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#finalizeRendering()
  //   */
  //  @Override
  //  public void finalizeRendering() throws RenderingException {
  //    // default behavior is empty
  //  }

}
