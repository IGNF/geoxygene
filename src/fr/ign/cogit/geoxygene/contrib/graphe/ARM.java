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

import java.util.Iterator;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Méthodes statiques pour la création d'un ARM (Arbre de Recouvrement Minimal,
 * Minimal Spanning Tree)
 * 
 * @author Mustiere - IGN / Laboratoire COGIT version 1.0
 * 
 */
public class ARM {

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
  public static CarteTopo creeARM(IFeatureCollection<IFeature> points) {
    Noeud noeud, nouveauNoeud;
    Arc arc;
    IFeature point;
    double dist, distMin;
    CarteTopo arm = new CarteTopo(I18N.getString("ARM.MST")); //$NON-NLS-1$
    int i, j, imin = 0, jmin = 0;
    GM_LineString trait;
    FT_FeatureCollection<IFeature> pointsCopie = new FT_FeatureCollection<IFeature>(
        points);

    if (pointsCopie.size() == 0) {
      return null;
    }

    // Amorce, on prend un point au hasard: le premier
    point = pointsCopie.get(0);
    if (!(point.getGeom() instanceof GM_Point)) {
      System.out.println(I18N.getString("ARM.AnObjectIsNotAPoint")); //$NON-NLS-1$
      return null;
    }
    pointsCopie.remove(point);
    nouveauNoeud = arm.getPopNoeuds().nouvelElement();
    nouveauNoeud.setGeom(point.getGeom());
    nouveauNoeud.addCorrespondant(point);
    // Ajout des points un à un
    while (true) {
      if (pointsCopie.size() == 0) {
        break; // ça y est, on a relié tous les points
      }

      // on cherche le couple noeud-point le pus proche (TRES bourrin)
      distMin = Double.MAX_VALUE;
      for (i = 0; i < pointsCopie.size(); i++) {
        point = pointsCopie.get(i);
        if (!(point.getGeom() instanceof GM_Point)) {
          System.out.println(I18N.getString("ARM.AnObjectIsNotAPoint")); //$NON-NLS-1$
          return null;
        }

        for (j = 0; j < arm.getPopNoeuds().size(); j++) {
          noeud = arm.getPopNoeuds().get(j);
          dist = noeud.getGeom().distance(point.getGeom());
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
      trait = new GM_LineString();
      trait.addControlPoint(arc.getNoeudIni().getGeometrie().getPosition());
      trait.addControlPoint(arc.getNoeudFin().getGeometrie().getPosition());
      arc.setGeometrie(trait);
    }

    return arm;

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
  public static CarteTopo creeARMsurObjetsQuelconques(
      IFeatureCollection<IFeature> objets) {
    FT_FeatureCollection<IFeature> points = new FT_FeatureCollection<IFeature>();

    Iterator<IFeature> itObjets = objets.getElements().iterator();
    while (itObjets.hasNext()) {
      IFeature objet = itObjets.next();
      if (objet.getGeom() == null) {
        System.out.println(I18N.getString("ARM.AnObjectHasNoGeometry")); //$NON-NLS-1$
        return null;
      }
      Noeud objet2 = new Noeud();
      objet2.setGeom(new GM_Point(objet.getGeom().centroid()));
      points.add(objet2);
    }
    return ARM.creeARM(points);
  }

}
