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
 * 
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.api.support.reseau;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * (peut-etre inutile sous forme de classe; a voir a l'usage)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface PassePar extends ElementDuReseau {

  /**
   * @return niveau du passage
   */
  public int getNiveau();

  public void setNiveau(int niveau);

  @Override
  public IPoint getGeom();

  /**
   * @return l'arc concerne
   */
  public ArcReseau getArc();

  public void setArc(ArcReseau arc);

  /**
   * @return le franchissement concerne
   */
  public Franchissement getFranchissement();

  public void setFranchissement(Franchissement franchissement);

}
