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

package fr.ign.cogit.geoxygene.contrib.appariement.surfaces;

/**
 * Paramètres de l'appariement de surfaces.
 * 
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */
public class ParametresAppSurfaces implements Cloneable {

  /**
   * 2 surfaces, pour être appariées, doivent s'intersecter et l'intersection
   * doit faire au moins la taille fixée par ce seuil. A l'extrême, ce paramètre
   * peut être nul. Valeur par défaut : 1 m2
   */
  public double surface_min_intersection = 1;

  /**
   * 2 surfaces, pour être appariées, doivent s'intersecter et l'intersection
   * doit faire au moins la taille d'une des surfaces multipliée par ce
   * paramètre. A l'extrême, ce paramètre peut être nul. Valeur par défaut : 0.2
   */
  public double pourcentage_min_intersection = 0.2;

  /**
   * Si 2 surfaces s'intersectent d'au moins la taille d'une des surfaces
   * multipliée par ce paramètre, on garde ce lien à coup sûr. Valeur par défaut
   * : 0.8
   */
  public double pourcentage_intersection_sur = 0.5;

  /**
   * Mesure de ressemblance entre surfaces (ou groupes de surfaces) à optimiser
   * lors du choix final de l'appariement. 2 possibilités :
   * <ul>
   * <li>TRUE : minimise la distance surfacique (conseillé par Atef en cas de données avec des
   * niveaux de détail similaires).
   * <li>FALSE : minimise la somme Exactitude + Complétude.
   * </ul>
   * Remarque :
   * <ul>
   * <li>- Complétude = surf(Sref inter Scomp) / Sref
   * <li>- Exactitude = surf(Sref inter Scomp) / Scomp
   * </ul>
   */
  public boolean minimiseDistanceSurfacique = false;

  /**
   * Si on utilise le critère de Distance Surfacique (cf. parametre
   * minimiseDistanceSurfacique): On n'accepte que les appariements finaux pour
   * lesquels distance surfacique est inférieure à ce seuil.
   */
  public double distSurfMaxFinal = 0.6;

  /**
   * Si on utilise le critère d'exactitude/complétude : (cf. parametre
   * minimiseDistanceSurfacique): On n'accepte que les appariements finaux pour
   * lesquels la somme exactitude + complétude est supérieur à ce seuil.
   */
  public double completudeExactitudeMinFinal = 0.3;

  /**
   * Paramètre indiquant si on souhaite faire un regroupement optimal des liens
   */
  public boolean regroupementOptimal = true;

  /**
   * Paramètre indiquant si on souhaite faire un filtrage final des liens sur
   * des critères de distance surfacique ou de complétude
   */
  public boolean filtrageFinal = true;

  /**
   * Paramètre indiquant si on souhaite faire un raffinement en essayant
   * d'aparier les petites surfaces non appariées
   */
  public boolean ajoutPetitesSurfaces = false;

  /**
   * On ne rajout que les petites surface de taille inférieure à ce paramètre *
   * taille de la surface à laquelle on le rajoute
   */
  public double seuilPourcentageTaillePetitesSurfaces = 0.1;

  /**
   * Paramètre indiquant si les liens fianux sont redus persistant ou non. Par
   * défaut: false
   */
  public boolean persistant = false;

  /**
   * Uniquement pour des problèmes de robustesse du code si les surfaces en
   * entrée ne sont pas propres (existence de mini-boucles). Si JTS plante à
   * cause de ces surfaces bizarres, celles-ci seront filtrées avec
   * DouglasPeucker. On essaiera plusieurs forces de filtrage : entre
   * resolutionMin et resolutionMax.
   */
  public double resolutionMin = 1;

  /**
   * Uniquement pour des problèmes de robustesse du code si les surfaces en
   * entrée ne sont pas propres (existence de mini-boucles). Si JTS plante à
   * cause de ces surfaces bizarres, celles-ci seront filtrées avec
   * DouglasPeucker. On essaiera plusieurs forces de filtrage : entre
   * resolutionMin et resolutionMax.
   */
  public double resolutionMax = 11;

  public boolean useSemanticSimilarity = false;
  public String semanticSimilarityAttributeRef = "";
  public String semanticSimilarityAttributeComp = "";
  public double semanticWeight = 0.5;
  
  /** Clone l'objet. */
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
