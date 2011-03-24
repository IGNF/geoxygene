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

package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * @author bonin
 * @version 1.0
 */

@SuppressWarnings("unchecked")
public class ChargeurTriangulation extends Chargeur {
  static Logger logger = Logger
      .getLogger(ChargeurTriangulation.class.getName());

  public ChargeurTriangulation() {
  }

  public static void importLigneEnPoints(String nomClasseGeo,
      AbstractTriangulation carte) throws Exception {
    FT_Feature objGeo;
    Class<?> clGeo = Class.forName(nomClasseGeo);
    NoeudDelaunay noeud;
    DirectPositionList listePoints;
    int i, j;

    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportLinesAsPoints")); //$NON-NLS-1$
    }
    FT_FeatureCollection<?> listeFeatures = DataSet.db.loadAllFeatures(clGeo);
    for (i = 0; i < listeFeatures.size(); i++) {
      if (ChargeurTriangulation.logger.isDebugEnabled()) {
        ChargeurTriangulation.logger.debug(I18N
            .getString("ChargeurTriangulation.NumberOfImportedLines") + i); //$NON-NLS-1$
      }
      objGeo = listeFeatures.get(i);

      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString) {
        listePoints = ((GM_LineString) objGeo.getGeom()).getControlPoint();
        for (j = 0; j < listePoints.size(); j++) {
          if ((j % 100) == 0) {
            if (ChargeurTriangulation.logger.isDebugEnabled()) {
              ChargeurTriangulation.logger
                  .debug(I18N
                      .getString("ChargeurTriangulation.NumberOfPointsCreated") + j); //$NON-NLS-1$
            }
          }
          noeud = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));

        }
      }
    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportedLinesAsPoints")); //$NON-NLS-1$
    }
  }

  public static void importLigneEnPoints(FT_FeatureCollection<?> listeFeatures,
      AbstractTriangulation carte) throws Exception {
    FT_Feature objGeo;
    NoeudDelaunay noeud;
    DirectPositionList listePoints;
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportLinesAsPoints")); //$NON-NLS-1$
    }
    for (int i = 0; i < listeFeatures.size(); i++) {
      if (ChargeurTriangulation.logger.isDebugEnabled()) {
        ChargeurTriangulation.logger.debug(I18N
            .getString("ChargeurTriangulation.NumberOfImportedLines") + i); //$NON-NLS-1$
      }
      objGeo = listeFeatures.get(i);

      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString) {
        listePoints = ((GM_LineString) objGeo.getGeom()).getControlPoint();
        for (int j = 0; j < listePoints.size(); j++) {
          noeud = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));
        }
        if (ChargeurTriangulation.logger.isDebugEnabled()) {
          ChargeurTriangulation.logger
              .debug(I18N
                  .getString("ChargeurTriangulation.NumberOfPointsCreated") + listePoints.size()); //$NON-NLS-1$
        }
      } else if (objGeo.getGeom() instanceof GM_MultiCurve<?>) {
        listePoints = ((GM_MultiCurve<?>) objGeo.getGeom()).coord();
        for (int j = 0; j < listePoints.size(); j++) {
          noeud = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));
        }
        if (ChargeurTriangulation.logger.isDebugEnabled()) {
          ChargeurTriangulation.logger
              .debug(I18N
                  .getString("ChargeurTriangulation.NumberOfPointsCreated") + listePoints.size()); //$NON-NLS-1$
        }
      }

    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportedLinesAsPoints")); //$NON-NLS-1$
    }
  }

  public static void importSegments(String nomClasseGeo, AbstractTriangulation carte)
      throws Exception {
    FT_Feature objGeo;
    Class<?> clGeo = Class.forName(nomClasseGeo);

    NoeudDelaunay noeud1;
    ArcDelaunay arc;
    DirectPositionList listePoints;
    DirectPosition dp;
    ArrayList<NoeudDelaunay> listeTemp, listeNoeuds, listeNoeudsEffaces = null;
    DirectPositionList tableau = null;
    Iterator<?> it, itEntrants, itSortants = null;
    int i, j;

    FT_FeatureCollection<?> listeFeatures = DataSet.db.loadAllFeatures(clGeo);
    Class<?>[] signaturea = { carte.getPopNoeuds().getClasse(),
        carte.getPopNoeuds().getClasse() };
    Object[] parama = new Object[2];

    for (i = 0; i < listeFeatures.size(); i++) {
      if (ChargeurTriangulation.logger.isDebugEnabled()) {
        ChargeurTriangulation.logger.debug(I18N
            .getString("ChargeurTriangulation.NumberOfImportedLines") + i); //$NON-NLS-1$
      }
      objGeo = listeFeatures.get(i);

      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString) {
        listePoints = ((GM_LineString) objGeo.getGeom()).getControlPoint();
        listeTemp = new ArrayList<NoeudDelaunay>();
        for (j = 0; j < listePoints.size(); j++) {
          if ((j % 100) == 0) {
            if (ChargeurTriangulation.logger.isDebugEnabled()) {
              ChargeurTriangulation.logger
                  .debug(I18N
                      .getString("ChargeurTriangulation.NumberOfPointsCreated") + j); //$NON-NLS-1$
            }
          }
          noeud1 = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement();
          noeud1.setCoord(listePoints.get(j));
          listeTemp.add(noeud1);

        }
        for (j = 0; j < listeTemp.size() - 1; j++) {
          parama[0] = listeTemp.get(j);
          parama[1] = listeTemp.get(j + 1);
          arc = (ArcDelaunay) carte.getPopArcs().nouvelElement(signaturea,
              parama);
        }
      }
    }

    // Filtrage des noeuds en double et correction de la topologie
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.DoubleNodesFiltering")); //$NON-NLS-1$
    }
    listeNoeuds = new ArrayList(carte.getListeNoeuds());
    it = carte.getListeNoeuds().iterator();
    tableau = new DirectPositionList();
    listeNoeudsEffaces = new ArrayList();
    while (it.hasNext()) {
      noeud1 = (NoeudDelaunay) it.next();
      dp = noeud1.getCoord();
      if (Operateurs.indice2D(tableau, dp) != -1) {
        if (ChargeurTriangulation.logger.isDebugEnabled()) {
          ChargeurTriangulation.logger.debug(I18N
              .getString("ChargeurTriangulation.DoubleNodeRemoval")); //$NON-NLS-1$
        }
        itEntrants = noeud1.getEntrants().iterator();
        while (itEntrants.hasNext()) {
          arc = (ArcDelaunay) itEntrants.next();
          arc.setNoeudFin(listeNoeuds.get(Operateurs.indice2D(tableau, dp)));
        }
        itSortants = noeud1.getSortants().iterator();
        while (itSortants.hasNext()) {
          arc = (ArcDelaunay) itSortants.next();
          arc.setNoeudIni(listeNoeuds.get(Operateurs.indice2D(tableau, dp)));
        }
      }
      tableau.add(dp);
    }
    it = listeNoeudsEffaces.iterator();
    while (it.hasNext()) {
      noeud1 = (NoeudDelaunay) it.next();
      carte.getPopNoeuds().remove(noeud1); // pour la bidirection
    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportedSegments")); //$NON-NLS-1$
    }
  }

  public static void importSegments(Collection<? extends FT_Feature> listeFeatures,
      AbstractTriangulation carte) throws Exception {
    Class[] signaturea = { carte.getPopNoeuds().getClasse(),
        carte.getPopNoeuds().getClasse() };
    Object[] parama = new Object[2];

    int i = 0;
    for (FT_Feature objGeo : listeFeatures) {
      if (ChargeurTriangulation.logger.isDebugEnabled()) {
        ChargeurTriangulation.logger.debug(I18N
            .getString("ChargeurTriangulation.NumberOfImportedLines") + (i++)); //$NON-NLS-1$
      }
      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString) {
        DirectPositionList listePoints = ((GM_LineString) objGeo.getGeom())
            .coord();
        List<Noeud> listeTemp = new ArrayList<Noeud>();
        for (int j = 0; j < listePoints.size(); j++) {
          if ((j % 100) == 0) {
            if (ChargeurTriangulation.logger.isDebugEnabled()) {
              ChargeurTriangulation.logger
                  .debug(I18N
                      .getString("ChargeurTriangulation.NumberOfPointsCreated") + j); //$NON-NLS-1$
            }
          }
          Noeud noeud = carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));
          listeTemp.add(noeud);
        }
        for (int j = 0; j < listeTemp.size() - 1; j++) {
          parama[0] = listeTemp.get(j);
          parama[1] = listeTemp.get(j + 1);
          carte.getPopArcs().nouvelElement(signaturea, parama);
        }
        parama[0] = listeTemp.get(listeTemp.size() - 1);
        parama[1] = listeTemp.get(0);
        carte.getPopArcs().nouvelElement(signaturea, parama);

      }

      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon) {
        DirectPositionList listePoints = ((GM_Polygon) objGeo.getGeom())
            .coord();
        if (ChargeurTriangulation.logger.isDebugEnabled()) {
          ChargeurTriangulation.logger
              .debug(I18N.getString("ChargeurTriangulation.Polygon") + i + " " + listePoints); //$NON-NLS-1$ //$NON-NLS-2$
        }
        List<Noeud> listeTemp = new ArrayList<Noeud>();
        for (int j = 0; j < listePoints.size(); j++) {
          if ((j % 100) == 0) {
            if (ChargeurTriangulation.logger.isDebugEnabled()) {
              ChargeurTriangulation.logger
                  .debug(I18N
                      .getString("ChargeurTriangulation.NumberOfPointsCreated") + j); //$NON-NLS-1$
            }
          }
          Noeud noeud = carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));
          listeTemp.add(noeud);
        }
        for (int j = 0; j < listeTemp.size() - 1; j++) {
          parama[0] = listeTemp.get(j);
          parama[1] = listeTemp.get(j + 1);
          carte.getPopArcs().nouvelElement(signaturea, parama);
        }
      }
    }

    // Filtrage des noeuds en double et correction de la topologie
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.DoubleNodesFiltering")); //$NON-NLS-1$
    }
    Noeud[] listeNoeuds = carte.getListeNoeuds().toArray(new Noeud[0]);
    DirectPositionList tableau = new DirectPositionList();
    for (Noeud noeud : listeNoeuds) {
      DirectPosition dp = noeud.getCoord();
      if (Operateurs.indice2D(tableau, dp) != -1) {
        // if (logger.isDebugEnabled())
        // logger.debug("Elimination d'un doublon");
        for (Arc arc : noeud.getEntrants().toArray(new Arc[0])) {
          arc.setNoeudFin(carte.getListeNoeuds().get(
              Operateurs.indice2D(tableau, dp)));
        }
        for (Arc arc : noeud.getSortants().toArray(new Arc[0])) {
          arc.setNoeudIni(carte.getListeNoeuds().get(
              Operateurs.indice2D(tableau, dp)));
        }
      }
      tableau.add(dp);
    }
    // FIXME delete unnecessary nodes
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportedSegments")); //$NON-NLS-1$
    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger
          .debug(I18N.getString("ChargeurTriangulation.NodeList") + carte.getListeNoeuds().size()); //$NON-NLS-1$
    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger
          .debug(I18N.getString("ChargeurTriangulation.EdgeList") + carte.getListeArcs().size()); //$NON-NLS-1$
    }
  }

  /**
   * @param listeFeatures
   * @param carte
   * @throws Exception
   */
  public static void importPolygoneEnPoints(FT_FeatureCollection listeFeatures,
      AbstractTriangulation carte) throws Exception {
    FT_Feature objGeo;
    NoeudDelaunay noeud;
    DirectPositionList listePoints;
    int i, j;

    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportPolygonsAsPoints")); //$NON-NLS-1$
    }
    for (i = 0; i < listeFeatures.size(); i++) {
      if (ChargeurTriangulation.logger.isDebugEnabled()) {
        ChargeurTriangulation.logger.debug(I18N
            .getString("ChargeurTriangulation.NumberOfImportedPolygons") + i); //$NON-NLS-1$
      }
      objGeo = listeFeatures.get(i);

      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon) {
        listePoints = ((GM_Polygon) objGeo.getGeom()).coord();
        for (j = 0; j < listePoints.size(); j++) {
          if ((j % 100) == 0) {
            if (ChargeurTriangulation.logger.isDebugEnabled()) {
              ChargeurTriangulation.logger
                  .debug(I18N
                      .getString("ChargeurTriangulation.NumberOfPointsCreated") + j); //$NON-NLS-1$
            }
          }
          noeud = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement();
          noeud.setCoord(listePoints.get(j));
        }
      }
    }
    if (ChargeurTriangulation.logger.isDebugEnabled()) {
      ChargeurTriangulation.logger.debug(I18N
          .getString("ChargeurTriangulation.ImportedPolygonsAsPoints")); //$NON-NLS-1$
    }
  }

  /**
   * @param features
   * @param carte
   * @throws Exception
   */
  public static void importCentroidesPolygones(
      Collection<? extends FT_Feature> features, AbstractTriangulation carte)
      throws Exception {
    NoeudDelaunay noeud;
    if (ChargeurTriangulation.logger.isTraceEnabled()) {
      ChargeurTriangulation.logger.trace(I18N
          .getString("ChargeurTriangulation.ImportPolygonCentroids")); //$NON-NLS-1$
    }
    for (FT_Feature objGeo : features) {
      if (objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon) {
        GM_Point p = new GM_Point(objGeo.getGeom().centroid());
        noeud = (NoeudDelaunay) carte.getPopNoeuds().nouvelElement(p);
        noeud.addCorrespondant(objGeo);
        if (ChargeurTriangulation.logger.isTraceEnabled()) {
          ChargeurTriangulation.logger.trace(I18N
              .getString("ChargeurTriangulation.ImportedCentroid") + p); //$NON-NLS-1$
        }
      }
    }
    if (ChargeurTriangulation.logger.isTraceEnabled()) {
      ChargeurTriangulation.logger.trace(I18N
          .getString("ChargeurTriangulation.ImportedPolygonCentroids")); //$NON-NLS-1$
    }
  }
}
