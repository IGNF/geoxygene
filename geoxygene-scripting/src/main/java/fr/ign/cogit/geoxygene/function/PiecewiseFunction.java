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
package fr.ign.cogit.geoxygene.function;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JeT
 * function composed of other function on different ranges
 * parameterized by a and b
 */
public class PiecewiseFunction implements Function1D {

  private static class Piece {
    public double xMin, xMax;
    public Function1D f;

    /**
     * Constructor
     * @param xMin
     * @param xMax
     * @param f
     */
    public Piece(final double xMin, final double xMax, final Function1D f) {
      super();
      this.xMin = xMin;
      this.xMax = xMax;
      this.f = f;
    }

  }

  private List<Piece> pieces = new ArrayList<Piece>();

  /**
   * Constructor
   */
  public PiecewiseFunction() {
    super();
  }

  public void addPiece(final double xMin, final double xMax, final Function1D f) {
    this.pieces.add(new Piece(xMin, xMax, f));
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.function.Function1D#help()
   */
  @Override
  public String help() {
    return "f(x)=f1(x), x C [a,b] | f2(x), x C [b,c] | ...";
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.gl.GeoDisplacementFunction1D#displacement(double)
   */
  @Override
  public Double evaluate(final double x) throws FunctionEvaluationException {
    for (Piece piece : this.pieces) {
      if (x >= piece.xMin && x <= piece.xMax) {
        return piece.f.evaluate(x);
      }
    }
    return null;
  }

}
