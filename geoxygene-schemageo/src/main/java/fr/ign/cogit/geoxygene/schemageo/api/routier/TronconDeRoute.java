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

import java.util.Date;

import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * troncon de transport routier (route, chemin, GR, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface TronconDeRoute extends ArcReseau {

  /**
   * @return le nombre de voies
   */
  public int getNombreDeVoies();

  public void setNombreDeVoies(int nombreDeVoies);

  /**
   * @return le date de mise en service
   */
  public Date getDateMiseEnService();

  public void setDateMiseEnService(Date dateMiseEnService);

  /**
   * @return l'etat physique
   */
  public String getEtatPhysique();

  public void setEtatPhysique(String etatPhysique);

  /**
   * @return l'acces
   */
  public String getAcces();

  public void setAcces(String acces);

  /**
   * @return l'altitude initiale
   */
  public double getzIni();

  public void setzIni(double zIni);

  /**
   * @return l'altitude finale
   */
  public double getzFin();

  public void setzFin(double zFin);

  /**
   * @return le nom de l'itineraire eventuel
   */
  public String getNomItineraire();

  public void setNomItineraire(String nomItineraire);
}
