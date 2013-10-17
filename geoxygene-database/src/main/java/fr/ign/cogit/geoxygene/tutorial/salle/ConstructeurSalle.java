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

package fr.ign.cogit.geoxygene.tutorial.salle;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Méthodes possibles de construction de salles
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class ConstructeurSalle {

  static Logger logger = Logger.getLogger(ConstructeurSalle.class);

  public static void main(String[] args) {
    ConstructeurSalle.CreationSalles(10, "Réunion");
  }

  /**
   * Construction de salles
   * 
   * @param nbSalles
   * @param nomGenerique
   */
  public static void CreationSalles(int nbSalles, String nomGenerique) {
    // Création d'une connexion à la base

    // Ouverture d'une transaction pour la base définie

    // Création de 10 salles avec une géométrie (polygone carré)
    // for...
    // Création d'un nouvel objet "salle"

    // L'objet "salle" est rendu persistant dans la base

    // fin for

    // Commit de la transaction (sauvegarde les objets créés et ferme la
    // connexion)
  }

  /**
   * Création de la géométrie d'une salle en fonction de son numéro
   * @param numeroSalle
   * @return un polygone
   */
  public static GM_Polygon creationGeometrie(int numeroSalle) {

    // Création des points de la salle

    // Ajout des points de manière ordonnée dans une liste de points

    // Construction d'une ligne fermée (premier point = dernier) à partir d'une
    // liste de points

    // Retourne un polygone construit à partir de la ligne fermée
    return new GM_Polygon();
  }

}
