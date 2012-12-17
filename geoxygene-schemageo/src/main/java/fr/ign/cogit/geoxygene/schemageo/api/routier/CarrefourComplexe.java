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
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.api.routier;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.AgregatReseau;

/**
 * carrefour complexe du reseau routier (rond-point, echangeur, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface CarrefourComplexe extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * les tronçons de route situés à l'intérieur du carrefour complexe
   * @return
   */
  public HashSet<TronconDeRoute> getRoutesInternes();

  public void setRoutesInternes(HashSet<TronconDeRoute> routesInternes);

  /**
   * les tronçons de route qui connectent le carrefour complexe au reste du
   * réseau
   * @return
   */
  public HashSet<TronconDeRoute> getRoutesExternes();

  public void setRoutesExternes(HashSet<TronconDeRoute> routesExternes);
}
