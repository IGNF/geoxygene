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

package fr.ign.cogit.geoxygene.contrib.graphe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Méthodes statiques pour la création d'un ARM (Arbre de Recouvrement Minimal,
 * Minimal Spanning Tree)
 * 
 * @author Mustiere - IGN / Laboratoire COGIT version 1.0
 * 
 * @version 1.7  
 * @author R. Cuissard 
 * add creeARMPondere(points, reseau)
 */
public class ARM {
  
  private static Logger LOGGER = LogManager.getLogger(ARM.class.getName());

  /**
   * Création d'un ARM à partir d'un ensemble de points
   * 
   * Cette méthode est très brutale: adaptée pour quelques points seulement. On
   * fait des calculs de distance beaucoup trop souvent. L'ARM étant un
   * sous-graphe de Delaunay, cela peut être grandement optimisé en effectuant
   * un Delaunay d'abord.
   * 
   * @param points Liste d'objets en entrée: ils doivent avoir une géométrie de
   *          type point
   * 
   * @return Une carte topo contenant un noeud pour chaque point, et un arc pour
   *         chaque tronçon du ARM ("correspondant" est instancié pour relier
   *         les noeuds et les points).
   */
  public static CarteTopo creeARM(Collection<IFeature> points) {
    
    // CarteTopo to return 
    CarteTopo arm = new CarteTopo("Minimum Spanning Tree");
    
    Noeud noeud, nouveauNoeud;
    Arc arc;
    IFeature point;
    double dist, distMin;
    int i, j, imin = 0, jmin = 0;
    GM_LineString trait;
    List<IFeature> pointsCopie = new ArrayList<IFeature>(points);
    
    // If no point, return null
    if (pointsCopie.isEmpty()) {
      return null;
    }
    
    // Amorce, on prend un point au hasard: le premier
    point = pointsCopie.get(0);
    if (!(point.getGeom() instanceof GM_Point)) {
      LOGGER.debug("An object is not a point, returning Null");
      return null;
    }
    pointsCopie.remove(point);
    nouveauNoeud = arm.getPopNoeuds().nouvelElement();
    nouveauNoeud.setGeom(point.getGeom());
    nouveauNoeud.addCorrespondant(point);
    // Ajout des points un à un
    while (true) {
      if (pointsCopie.isEmpty()) {
        break; // ça y est, on a relié tous les points
      }
      // on cherche le couple noeud-point le plus proche (TRES bourrin)
      distMin = Double.MAX_VALUE;
      for (i = 0; i < pointsCopie.size(); i++) {
        point = pointsCopie.get(i);
        System.out.print("Point initial = " + point.getId() + " ("+arm.getPopNoeuds().size()+") ");
        if (!(point.getGeom() instanceof GM_Point)) {
          LOGGER.debug("An object is not a point, returning Null");
          return null;
        }
        for (j = 0; j < arm.getPopNoeuds().size(); j++) {
          noeud = arm.getPopNoeuds().get(j);
          dist = noeud.getGeom().distance(point.getGeom());
          // System.out.println("Distance entre " + noeud.getId() + " et " + point.getId() + " = " + dist);
          if (dist < distMin) {
            distMin = dist;
            imin = i;
            jmin = j;
          }
        }
      }
      point = pointsCopie.get(imin);
      noeud = arm.getPopNoeuds().get(jmin);
      // on remplit l'ARM
      pointsCopie.remove(point);
      nouveauNoeud = arm.getPopNoeuds().nouvelElement();
      nouveauNoeud.setGeom(point.getGeom());
      nouveauNoeud.addCorrespondant(point);
      arc = arm.getPopArcs().nouvelElement();
      arc.setNoeudIni(noeud);
      arc.setNoeudFin(nouveauNoeud);
      trait = new GM_LineString(arc.getNoeudIni().getGeometrie().getPosition(), arc.getNoeudFin().getGeometrie()
          .getPosition());
      arc.setGeometrie(trait);
    }
    return arm;

  }

  public static CarteTopo creeARMPondere(Collection<IFeature> points, CarteTopo reseau) {
    Noeud noeud, nouveauNoeud;
    Arc arc;
    IFeature point;
    double dist, distMin;
    CarteTopo arm = new CarteTopo("Minimum Spanning Tree");
    int i, j, imin = 0, jmin = 0;
    GM_LineString trait;
    List<IFeature> pointsCopie = new ArrayList<IFeature>(points);
    if (pointsCopie.isEmpty()) {
      return null;
    }
    // Amorce, on prend un point au hasard: le premier
    point = pointsCopie.get(0);
    if (!(point.getGeom() instanceof GM_Point)) {
      LOGGER.debug("An object is not a point, returning Null");
      return null;
    }
    pointsCopie.remove(point);
    nouveauNoeud = arm.getPopNoeuds().nouvelElement();
    nouveauNoeud.setGeom(point.getGeom());
    nouveauNoeud.addCorrespondant(point);
    // Ajout des points un à un
    while (true) {
      if (pointsCopie.isEmpty()) {
        break; // ça y est, on a relié tous les points
      }
      // on cherche le couple noeud-point le pus proche (TRES bourrin)
      distMin = Double.MAX_VALUE;
      for (i = 0; i < pointsCopie.size(); i++) {
        point = pointsCopie.get(i);
        if (!(point.getGeom() instanceof GM_Point)) {
          LOGGER.debug("An object is not a point, returning Null");
          return null;
        }
        for (j = 0; j < arm.getPopNoeuds().size(); j++) {
          noeud = arm.getPopNoeuds().get(j);
          // dist = noeud.getGeom().distance(point.getGeom());
          dist = ARM.distanceReseau((GM_Point) noeud.getGeom(), (GM_Point) point.getGeom(), reseau);
          if (dist < distMin) {
            distMin = dist;
            imin = i;
            jmin = j;
          }
        }
      }
      point = pointsCopie.get(imin);
      noeud = arm.getPopNoeuds().get(jmin);
      // on remplit l'ARM
      pointsCopie.remove(point);
      nouveauNoeud = arm.getPopNoeuds().nouvelElement();
      nouveauNoeud.setGeom(point.getGeom());
      nouveauNoeud.addCorrespondant(point);
      arc = arm.getPopArcs().nouvelElement();
      arc.setNoeudIni(noeud);
      arc.setNoeudFin(nouveauNoeud);
      trait = new GM_LineString(arc.getNoeudIni().getGeometrie().getPosition(), arc.getNoeudFin().getGeometrie()
          .getPosition());
      arc.setGeometrie(trait);
    }
    return arm;
  }

  /**
   * 
   * @param pt1
   * @param pt2
   * @param reseau
   * @return
   */
  private static double distanceReseau(GM_Point pt1, GM_Point pt2, CarteTopo reseau) {
    // recupére le noeud le plus proche de p1
    Noeud noeud1 = null;
    Collection<Noeud> ptsProches = reseau.getPopNoeuds().select(pt1.coord().get(0), 500); 
                                                                                          
    Iterator<Noeud> itPtsProches = ptsProches.iterator();
    double distmin = 1000;

    while (itPtsProches.hasNext()) {
      Noeud noeud = (Noeud) itPtsProches.next();
      if (pt1.distance(noeud.getGeom()) < distmin) {
        distmin = pt1.distance(noeud.getGeom());
        noeud1 = noeud;
      }
    }
    if (noeud1 == null) {
      return 100000;
    }

    // recupére le noeud le plus proche de p2
    Noeud noeud2 = null;
    ptsProches = reseau.getPopNoeuds().select(pt2.coord().get(0), 110);
    itPtsProches = ptsProches.iterator();
    distmin = 1000;

    while (itPtsProches.hasNext()) {
      Noeud noeud = (Noeud) itPtsProches.next();
      if (pt2.distance(noeud.getGeom()) < distmin) {
        distmin = pt2.distance(noeud.getGeom());
        noeud2 = noeud;
      }
    }
    if (noeud2 == null)
      return Double.POSITIVE_INFINITY;

    // calcul le plus court chemin1
    Groupe pcc = noeud1.plusCourtChemin(noeud2, 5000);
    if (pcc != null)
      return pcc.longueur();
    else
      return Double.POSITIVE_INFINITY;
  }

  /**
   * Methode pour créer un ARM à partir des centroides d'un ensemble d'objets.
   * 
   * @param objets Liste d'objets en entrée: ils doivent avoir une géométrie
   *          quelconque
   * 
   * @return Une carte topo contenant un noeud pour chaque point, et un arc pour
   *         chaque tronçon du ARM ("correspondant" est instancié pour relier
   *         les noeuds et les points).
   * 
   */
  public static CarteTopo creeARMsurObjetsQuelconques(Collection<IFeature> objets) {
    Collection<IFeature> points = new HashSet<IFeature>();

    Iterator<IFeature> itObjets = objets.iterator();
    while (itObjets.hasNext()) {
      IFeature objet = itObjets.next();
      if (objet.getGeom() == null) {
        LOGGER.debug("An object has no geometry, returning Null");
        return null;
      }
      Noeud objet2 = new Noeud();
      objet2.setGeom(new GM_Point(objet.getGeom().centroid()));
      points.add(objet2);
    }
    return ARM.creeARM(points);
  }

}
