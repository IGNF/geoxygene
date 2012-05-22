/*
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
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArcString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;

/**
 * NON IMPLEMENTE. Arc de cercle. Similaire à GM_LineString, sauf que
 * l'interpolation est circulaire.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
class GM_ArcString extends GM_CurveSegment implements IArcString {

  @Override
  public IDirectPositionList coord() {
    return null;
  }

  @Override
  public ICurveSegment reverse() {
    return null;
  }

  private int numArc = 0;

  @Override
  public int getNumArc() {
    return this.numArc;
  }

  @Override
  public String getInterpolation() {
    return "circularArc3points"; //$NON-NLS-1$
  }
}
