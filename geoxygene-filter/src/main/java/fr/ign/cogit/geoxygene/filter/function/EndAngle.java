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

package fr.ign.cogit.geoxygene.filter.function;

import javax.xml.bind.annotation.XmlRootElement;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;

/**
 * @author Julien Perret
 */
@XmlRootElement(name = "Function", namespace = "")
public class EndAngle extends FunctionImpl {

  public EndAngle() {
    this.name = "EndAngle";} //$NON-NLS-1$

  @Override
  public Object evaluate(Object object) {
    double angle = 0;
    IGeometry g = ((IFeature) object).getGeom();
    if (g.numPoints() < 2) {
      return angle;
    }
    IDirectPositionList l = g.coord();
    IDirectPosition p1 = l.get(g.numPoints() - 2);
    IDirectPosition p2 = l.get(g.numPoints() - 1);
    Angle a = new Angle(p1, p2);
    // convert to degrees
    angle = a.getValeur() * 180 / Math.PI;
    return angle;
  }
}
