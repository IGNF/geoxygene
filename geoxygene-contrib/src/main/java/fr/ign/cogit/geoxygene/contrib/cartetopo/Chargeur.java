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

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.parameters.Parameters;


/**
 * Chargeur permettant de créer une carte topo à partir de classes de
 * "FT_Feature".
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 */

public class Chargeur {
  
  /** Logger. */
  static Logger logger = LogManager.getLogger(Chargeur.class.getName());

  /**
   * Charge en mémoire les éléments de la classe 'nomClasseGeo' et remplit la
   * carte topo 'carte' avec des correspondants de ces éléments.
   * @param nomClasseGeo class name
   * @param carte topological map
   */
  public static void importClasseGeo(final String nomClasseGeo,
      final CarteTopo carte) {
    Class<?> clGeo;

    try {
      clGeo = Class.forName(nomClasseGeo);
    } catch (Exception e) {
      Chargeur.logger.warn(I18N.getString("Chargeur.ClassWarning") //$NON-NLS-1$
          + nomClasseGeo + I18N.getString("Chargeur.DoesNotExist")); //$NON-NLS-1$
      Chargeur.logger.warn(I18N.getString("Chargeur.ImportImpossible")); //$NON-NLS-1$
      e.printStackTrace();
      return;
    }

    IFeatureCollection<?> listeFeatures = DataSet.db.loadAllFeatures(clGeo);
    Chargeur.importClasseGeo(listeFeatures, carte);
  }

  /**
   * Remplit la carte topo 'carte' avec des correspondants des éléments de
   * 'listeFeature'.
   * @param listeFeatures éléments
   * @param carte carte topo
   */
  public static void importClasseGeo(final IFeatureCollection<?> listeFeatures,
      final CarteTopo carte) {
    Chargeur.importClasseGeo(listeFeatures, carte, false);
  }

  /**
   * Remplit la carte topo 'carte' avec des correspondants des éléments de
   * 'listeFeature'.
   * @param listeFeatures éléments
   * @param carte carte topo
   * @param convert2d si vrai, alors convertir les géométries en 2d
   */
  public static void importClasseGeo(final IFeatureCollection<?> listeFeatures,
      final CarteTopo carte, final boolean convert2d) {
    if (listeFeatures.isEmpty()) {
      Chargeur.logger.warn(I18N.getString("Chargeur.NothingImported")); //$NON-NLS-1$
      return;
    }
    if (listeFeatures.get(0).getGeom() instanceof GM_Point) {
      int nbElements = Chargeur.importClasseGeo(listeFeatures,
          carte.getPopNoeuds(), convert2d);
      if (Chargeur.logger.isDebugEnabled()) {
        Chargeur.logger
            .debug(I18N.getString("Chargeur.NumberOfImportedNodes") + nbElements); //$NON-NLS-1$
      }
      return;
    }
    if ((listeFeatures.get(0).getGeom() instanceof GM_LineString)
        || (listeFeatures.get(0).getGeom() instanceof GM_MultiCurve<?>)) {
      int nbElements = Chargeur.importClasseGeo(listeFeatures,
          carte.getPopArcs(), convert2d);
      if (Chargeur.logger.isDebugEnabled()) {
        Chargeur.logger
            .debug(I18N.getString("Chargeur.NumberOfImportedEdges") + nbElements); //$NON-NLS-1$
      }
      return;
    }
    if ((listeFeatures.get(0).getGeom() instanceof GM_Polygon)
        || (listeFeatures.get(0).getGeom() instanceof GM_MultiSurface<?>)) {
      int nbElements = Chargeur.importClasseGeo(listeFeatures,
          carte.getPopFaces(), convert2d);
      if (Chargeur.logger.isDebugEnabled()) {
        Chargeur.logger
            .debug(I18N.getString("Chargeur.NumberOfImportedFaces") + nbElements); //$NON-NLS-1$
      }
      return;
    }
    Chargeur.logger.warn(I18N.getString("Chargeur.WarningNothingImported") //$NON-NLS-1$
        + listeFeatures.get(0).getClass().getName());
  }

  /**
   * Remplit la carte topo 'carte' avec des correspondants des éléments de
   * 'listeFeature'.
   * @param listeFeatures éléments
   * @param population the population to import
   * @param convert2d si vrai, alors convertir les géométries en 2d
   */
  @SuppressWarnings("unchecked")
  private static int importClasseGeo(final IFeatureCollection<?> listeFeatures,
      final IPopulation<?> population, final boolean convert2d) {
    int nbElements = 0;
    for (IFeature feature : listeFeatures) {
      if (feature.getGeom() == null) {
        continue;
      }
      if (feature.getGeom() instanceof GM_Primitive) {
        Chargeur.creeElement(feature, feature.getGeom(), population, convert2d);
        nbElements++;
      } else {
        for (IGeometry geom : ((GM_Aggregate<IGeometry>) feature.getGeom())) {
          try {
            Chargeur.creeElement(feature, geom, population, convert2d);
            nbElements++;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return nbElements;
  }

  /**
   * crée un élément de la carte topo comme correspondant de l'objet feature et
   * la géométrie geom.
   * @param geom géométrie du nouvel élément
   * @param population population à laquelle ajout le nouvel élément
   * @param convert2d si vrai alors la géométrie du nouvel élément est convertie
   *          en 2d
   */
  private static void creeElement(final IFeature feature, final IGeometry geom,
      final IPopulation<?> population, final boolean convert2d) {
    IFeature nouvelElement;
    try {
      nouvelElement = population.nouvelElement(convert2d ? AdapterFactory
          .to2DGM_Object(geom) : geom);
      nouvelElement.addCorrespondant(feature);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Seuls les points des éléments sont importés comme noeuds de la carte.
   * @param listeFeatures
   * @param carteTopo
   */
  public static void importAsNodes(
      Collection<? extends IFeature> listeFeatures, CarteTopo carteTopo) {
    
    Class<Noeud> nodeClass = carteTopo.getPopNoeuds().getClasse();
    try {
      Constructor<Noeud> constructor = nodeClass
          .getConstructor(IDirectPosition.class);
      for (IFeature f : listeFeatures) {
        for (IDirectPosition p : f.getGeom().coord()) {
          try {
            Noeud n = constructor.newInstance(p);
            carteTopo.getPopNoeuds().add(n);
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (InstantiationException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }

  /**
   * Seuls les points des éléments sont importés comme noeuds de la carte.
   * @param feature
   * @param carteTopo
   */
  public static void importAsNodes(IFeature feature, CarteTopo carteTopo) {
    Class<Noeud> nodeClass = carteTopo.getPopNoeuds().getClasse();
    try {
      Constructor<Noeud> constructor = nodeClass
          .getConstructor(IDirectPosition.class);
      for (IDirectPosition p : feature.getGeom().coord()) {
        try {
          Noeud n = constructor.newInstance(p);
          carteTopo.getPopNoeuds().add(n);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
  
  public static void importAsEdges(Collection<? extends IFeature> edges,
      CarteTopo map, Parameters param) {
    
    // test validation param file here ????
    
    // Initialize parameters from global parameters
    String orientationAttribute = param.getString("orientationAttribute");
    String groundPositionAttribute = param.getString("groundPositionAttribute");
    double tolerance = param.getDouble("tolerance");
    Map<Object, Integer> orientationMap = new HashMap<Object, Integer>(2);
    // orientationMap.put(key, value);
    
    Chargeur.importAsEdges(edges, map, orientationAttribute, orientationMap, 
        "", null, groundPositionAttribute, tolerance);
  }

  /**
   * @param edges
   * @param map
   * @param orientationAttribute
   * @param orientationMap
   * @param groundPositionAttribute
   * @param tolerance
   */
  public static void importAsEdges(Collection<? extends IFeature> edges,
      CarteTopo map, String orientationAttribute,
      Map<Object, Integer> orientationMap, String filterAttribute,
      Map<Object, Boolean> filterMap, String groundPositionAttribute,
      double tolerance) {
    
      // import des arcs
      for (IFeature element : edges) {
          boolean filter = true;
          if (filterMap != null) {
              Object value = element.getAttribute(filterAttribute);
              if (value != null) {
                  Boolean filterValue = filterMap.get(value);
                  if (filterValue != null) {
                      filter = filterValue.booleanValue();
                  } else {
                      filter = false;
                  }
              }
          }
      
          if (filter) {
              Arc arc = map.getPopArcs().nouvelElement();
              ILineString ligne = new GM_LineString((IDirectPositionList) element.getGeom().coord().clone());
              arc.setGeometrie(ligne);
              if (orientationAttribute == null || orientationAttribute.isEmpty()) {
                  arc.setOrientation(2);
              } else {
                  Object value = element.getAttribute(orientationAttribute);
                  if (orientationMap != null) {
                      Integer orientation = orientationMap.get(value);
                      if (orientation != null) {
                          // LOGGER.debug(value + " -> " + orientation);
                          arc.setOrientation(orientation.intValue());
                      } else {
                          Chargeur.logger.error(value + "(" + value.getClass() + ")" + " not found in map for attribute "
                                  + orientationAttribute + ", element = " + element.getGeom());
                      }
                  } else {
                      if (value instanceof Number) {
                          Number v = (Number) value;
                          arc.setOrientation(v.intValue());
                      } else {
                          if (value instanceof String) {
                              String v = (String) value;
                              try {
                                  arc.setOrientation(Integer.parseInt(v));
                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          } else {
                              Chargeur.logger.error("Attribute "
                                      + orientationAttribute
                                      + " is neither Number nor String. It can't be used as an orientation");
                          }
                      }
                  }
              }
              arc.addCorrespondant(element);
              arc.setPoids(arc.getGeometrie().length());
          }
      } // fin de la boucle sur edges
    
      // initialisation de l'index au besoin
      // si on peut, on prend les mêmes paramètres que le dallage des arcs
      if (!map.getPopNoeuds().hasSpatialIndex()) {
          if (map.getPopArcs().hasSpatialIndex()) {
              map.getPopNoeuds().initSpatialIndex(map.getPopArcs().getSpatialIndex());
              map.getPopNoeuds().getSpatialIndex().setAutomaticUpdate(true);
          } else {
              IEnvelope enveloppe = map.getPopArcs().envelope();
              int nb = (int) Math.sqrt(map.getPopArcs().size() / 20);
              if (nb == 0) {
                  nb = 1;
              }
              map.getPopNoeuds().initSpatialIndex(Tiling.class, true, enveloppe, nb);
          }
      }
      
      //
      for (Arc arc : map.getPopArcs()) {
          IDirectPosition p1 = arc.getGeometrie().getControlPoint(0);
          IDirectPosition p2 = arc.getGeometrie().getControlPoint(
                  arc.getGeometrie().sizeControlPoint() - 1);
          int posSol = 0;
          if (groundPositionAttribute != null) {
              posSol = Integer.parseInt(arc.getCorrespondant(0)
                      .getAttribute(groundPositionAttribute).toString());
          }
          Collection<Noeud> candidates = map.getPopNoeuds().select(p1, tolerance);
          if (candidates.isEmpty()) {
              Noeud n1 = map.getPopNoeuds().nouvelElement(p1.toGM_Point());
              arc.setNoeudIni(n1);
              n1.setDistance(posSol);
          } else {
              for (Noeud n : candidates) {
                  if (n.getDistance() == posSol) {
                      arc.setNoeudIni(n);
                      break;
                  }
              }
              if (arc.getNoeudIni() == null) {
                  Noeud n1 = map.getPopNoeuds().nouvelElement(p1.toGM_Point());
                  arc.setNoeudIni(n1);
                  n1.setDistance(posSol);
              }
          }
          candidates = map.getPopNoeuds().select(p2, tolerance);
      if (candidates.isEmpty()) {
        Noeud n1 = map.getPopNoeuds().nouvelElement(p2.toGM_Point());
        arc.setNoeudFin(n1);
        n1.setDistance(posSol);
      } else {
        for (Noeud n : candidates) {
          if (n.getDistance() == posSol) {
            arc.setNoeudFin(n);
            break;
          }
        }
        if (arc.getNoeudFin() == null) {
          Noeud n1 = map.getPopNoeuds().nouvelElement(p2.toGM_Point());
          arc.setNoeudFin(n1);
          n1.setDistance(posSol);
        }
      }
    }
    List<Noeud> toRemove = new ArrayList<Noeud>(0);
    // connect the single nodes
    for (Noeud n : map.getPopNoeuds()) {
      if (n.arcs().size() == 1) {
        Collection<Noeud> candidates = map.getPopNoeuds().select(n.getCoord(),
            tolerance);
        candidates.remove(n);
        candidates.removeAll(toRemove);
        if (candidates.size() == 1) {
          Noeud candidate = candidates.iterator().next();
          // LOGGER.info("connecting node " + n + " (" + n.getDistance() +
          // ") to node " + candidate + " (" + candidate.getDistance() + ")");
          for (Arc a : new ArrayList<Arc>(n.getEntrants())) {
            candidate.addEntrant(a);
          }
          for (Arc a : new ArrayList<Arc>(n.getSortants())) {
            candidate.addSortant(a);
          }
          toRemove.add(n);
        }
      }
    }
    map.enleveNoeuds(toRemove);
  }

}
