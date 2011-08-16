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

package fr.ign.cogit.geoxygene.tutorial.exemple.cartetopo;

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * Algorithmie en relation avec une carte topologique
 * 
 * @author EGrosso - IGN / Laboratoire COGIT
 */
public class CarteTopoAlgorithmie {
  /**
   * Logger.
   */
  static Logger logger = Logger.getLogger(CarteTopoAlgorithmie.class);

  /**
   * Détection des faces circulaires
   * 
   * @param carteTopo
   * @return collection de faces
   */
  public static FT_FeatureCollection<Face> detectionFacesCirculaires(
      CarteTopo carteTopo) {

    // Récupération des faces de la carteTopo
    IPopulation<Face> faces = carteTopo.getPopFaces();

    if (faces.isEmpty()) {
      return new FT_FeatureCollection<Face>();
    }

    // Création d'une collection d'accueil pour sauver les faces circulaires
    FT_FeatureCollection<Face> facesCirculaires = new FT_FeatureCollection<Face>();

    // Itération sur les faces de la carteTopo
    Iterator<Face> it = faces.iterator();
    while (it.hasNext()) {
      Face face = it.next();

      // Calcul de l'indice de compacité
      double indiceCompacite = IndicesForme
          .indiceCompacite(face.getGeometrie());

      // Si l'indice est supérieur ou égal à 0.95, alors la face est
      // considérée comme circulaire et ajoutée à la collection
      if (indiceCompacite >= 0.95) {
        facesCirculaires.add(face);
        CarteTopoAlgorithmie.logger
            .info("Nouvelle face circulaire détectée (indice : "
                + indiceCompacite + ")");
      }
    }

    // Renvoi de la collection
    return facesCirculaires;
  }

}
