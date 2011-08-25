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

package fr.ign.cogit.geoxygene.tutorial.exemple.cartetopo;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.tutorial.data.BdTopoTrRoute;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * Exemple d'utilisation de la carte topologique : Utilisation de noeud valué
 * pour réaliser une cartographie de leur valuation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TestCarteTopoNoeudValue {

  /**
   * Main method.
   * @param args arguments
   */
  public static void main(String[] args) {

    // Initialisation de la connexion à la base de données
    Geodatabase geodb = GeodatabaseOjbFactory.newInstance();

    // Chargement des données

    // Données BDTopo
    IFeatureCollection<BdTopoTrRoute> tronconsBDT = geodb
        .loadAllFeatures(BdTopoTrRoute.class);

    // Création de la carte topologique
    CarteTopo carteTopo = CarteTopoFactory.creeCarteTopoEtendue(tronconsBDT);

    // Affichage
    // Initiatlisation du visualisateur GeOxygene
    ObjectViewer viewer = new ObjectViewer();
    viewer.addFeatureCollection(tronconsBDT, "Tronçon routier"); //$NON-NLS-1$
    viewer.addFeatureCollection(carteTopo.getPopNoeuds(), "Noeuds valués"); //$NON-NLS-1$
  }
}
