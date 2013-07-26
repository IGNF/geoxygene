/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * @copyright twak
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.landuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoFactory;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * Extract the spinal column of polygons
 * @author JFG
 * 
 */
public class LanduseSimplification {

  public static Logger logger = Logger.getLogger(LanduseSimplification.class
      .getName());

  /**
   * A basic method to simplify landuse classes. Each landuse class requires a
   * mimimum area to be conserved. Different methods are proposed to eliminate
   * holes according to their characteristics (inside the polygon, border of the
   * polygon, border of the area). If the set of landuse classes doesn't provide
   * a complete coverage of the area, an empty class is temporary created.
   * Generalisation is performed using the Douglas-Peucker filtering algorithm.
   * 
   * @param mapFtColIn : the list of landuse classes to generalise, with their
   *          minimum area required
   * @param dpFilter : douglas-peucker filtering applied to generalise landuse
   *          classes
   * @return
   */
  public static Map<IFeatureCollection<IFeature>, String> landuseSimplify(
      Map<IFeatureCollection<IFeature>, Map<String, Double>> mapFtColIn,
      double dpFilter) {

    List<String> listTypeOCS = new ArrayList<String>();
    Map<IFeatureCollection<IFeature>, String> mapFtColOutTmp = new HashMap<IFeatureCollection<IFeature>, String>();
    IFeatureCollection<IFeature> ftColPolyTotal = new FT_FeatureCollection<IFeature>();
    IFeatureCollection<IFeature> ftColPolyBigs = new FT_FeatureCollection<IFeature>();
    List<IPolygon> listPolyRemove = new ArrayList<IPolygon>();
    Iterator<IFeatureCollection<IFeature>> itFtCol;
    itFtCol = mapFtColIn.keySet().iterator();

    while (itFtCol.hasNext()) {
      IFeatureCollection<IFeature> ftCol = itFtCol.next();
      Map<String, Double> mapNameArea = mapFtColIn.get(ftCol);
      String nameFtCol = mapNameArea.keySet().iterator().next();

      // listTypeOCS.add(nameFtCol);
      double seuilSurface = mapNameArea.get(nameFtCol);

      // 1 - Union and split of polygons
      List<IPolygon> listPoly = new ArrayList<IPolygon>();
      for (IFeature ft : ftCol) {
        if (ft.getGeom().isMultiSurface()) {
          @SuppressWarnings("unchecked")
          IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) ft
              .getGeom();
          for (IPolygon polygon : multiPoly.getList()) {
            listPoly.add((IPolygon) polygon.buffer(0));
          }
        } else if (ft.getGeom().isPolygon()) {
          IPolygon polygon = (IPolygon) ft.getGeom();
          listPoly.add((IPolygon) polygon.buffer(0));
        }
      }

      List<IPolygon> listPolyUnion = LanduseSimplification
          .unionPolygones(listPoly);
      for (IPolygon poly : listPolyUnion) {
        ftColPolyTotal.add(new DefaultFeature(poly));
      }
      IFeatureCollection<IFeature> ftColPolyRemove = new FT_FeatureCollection<IFeature>();

      List<IPolygon> listPolyBigs = new ArrayList<IPolygon>();

      // remove small polygons
      for (IPolygon poly : listPolyUnion) {
        if (poly.area() < seuilSurface) {
          ftColPolyRemove.add(new DefaultFeature(poly));
          listPolyRemove.add(poly);
        } else {
          ftColPolyBigs.add(new DefaultFeature(poly));
          listPolyBigs.add(poly);
        }
      }

      IFeatureCollection<IFeature> ftColOut = new FT_FeatureCollection<IFeature>();
      // for (IPolygon poly : listPolyUnion) {
      for (IPolygon poly : listPolyBigs) {
        ftColOut.add(new DefaultFeature(poly));
      }
      if (ftColOut.size() > 0) {
        listTypeOCS.add(nameFtCol);//
        mapFtColOutTmp.put(ftColOut, nameFtCol);
      }
    }

    LanduseSimplification.logger.info("Gestion des zones vides");
    // Creation of an empty area (if the landcover is not complete)
    IFeatureCollection<IFeature> ftColEmptyZone = new FT_FeatureCollection<IFeature>();

    // Solution 1 : on utilise une envelope rectangulaire horizontale...
    // IPolygon polyEnveloppe = new GM_Polygon(ftColPolyTotal.getGeomAggregate()
    // .envelope());

    // Solution 2 : on utilise l'enveloppe convexe...
    IPolygon polyEnveloppe = new GM_Polygon(new GM_LineString(ftColPolyTotal
        .getGeomAggregate().convexHull().coord()));

    IGeometry geom = polyEnveloppe;
    for (IFeature ft : ftColPolyTotal) {
      IPolygon poly = (IPolygon) ft.getGeom().buffer(0);
      if (!(geom.difference(poly) == null)) {
        geom = geom.difference(poly);
      }
    }
    if (geom.isMultiSurface()) {
      if (!(geom.isEmpty())) {
        @SuppressWarnings("unchecked")
        IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) geom;
        for (IPolygon polygon : multiPoly.getList()) {
          ftColEmptyZone.add(new DefaultFeature(polygon));
        }
      }
    }
    if (geom.isPolygon()) {
      if (!(geom.isEmpty())) {
        ftColEmptyZone.add(new DefaultFeature(geom));
      }
    }
    mapFtColOutTmp.put(ftColEmptyZone, "Zone_Vide");
    ftColPolyBigs.addAll(ftColEmptyZone);

    LanduseSimplification.logger
        .info("Elimination des trous entièrement inclus");
    // Elimination of entire holes in the polygons
    List<IPolygon> listPolyUnionRemove = LanduseSimplification
        .unionPolygones(listPolyRemove);
    List<IRing> listRingRemoveUnion = new ArrayList<IRing>();
    for (IPolygon polygon : listPolyUnionRemove) {
      listRingRemoveUnion.add(polygon.getExterior());
    }
    IFeatureCollection<IFeature> ftColPolyConserve = new FT_FeatureCollection<IFeature>();
    IFeatureCollection<IFeature> ftColPolyEntireHoles = new FT_FeatureCollection<IFeature>();

    // Bouche les trous fusionnés
    for (IFeature ft : ftColPolyBigs) {
      IPolygon poly = (IPolygon) ft.getGeom().buffer(0);
      if (poly.getInterior().size() > 0) {
        IPolygon polyNoHoles = (IPolygon) poly.clone();
        for (IRing gmRing1 : poly.getInterior()) {
          for (IRing gmRing2 : listRingRemoveUnion) {
            // Grosse bidouille : utilise les rectangles englobants pour contrer
            // les problèmes d'égalité avec Jts entre Ring intérieur et Ring
            // extérieur
            IPolygon polyEnv1 = new GM_Polygon(gmRing1.envelope());
            IPolygon polyEnv2 = new GM_Polygon(gmRing2.envelope());
            if (polyEnv1.equals(polyEnv2)) {
              int idTrou = 0;
              for (int k = 0; k < polyNoHoles.getInterior().size(); k++) {
                IPolygon polyEnvNoHoles = new GM_Polygon(polyNoHoles
                    .getInterior(k).envelope());
                if (polyEnv1.equals(polyEnvNoHoles)) {
                  idTrou = k;
                  ftColPolyEntireHoles.add(new DefaultFeature(new GM_Polygon(
                      polyNoHoles.getInterior(k))));
                }
              }
              polyNoHoles.removeInterior(idTrou);
            }
          }
        }
        ftColPolyConserve.add(new DefaultFeature(polyNoHoles));
      } else {
        ftColPolyConserve.add(new DefaultFeature(poly));
      }
    }
    IFeatureCollection<IFeature> ftColPolyFrontierHoles = new FT_FeatureCollection<IFeature>();
    for (IPolygon polygon : listPolyUnionRemove) {
      if (!(polygon.within(ftColPolyEntireHoles.getGeomAggregate()))) {
        ftColPolyFrontierHoles.add(new DefaultFeature(polygon));
      }
    }

    LanduseSimplification.logger.info("Gestion des trous en bordure de zone");
    // On prend le parti-pris de les conserver entièrement car ils font
    // peut-être partie
    // d'un polygone plus grand...
    // A VALIDER !! On peut également faire le choix de ne pas les conserver
    IFeatureCollection<IFeature> ftColBorderHoles = new FT_FeatureCollection<IFeature>();
    Iterator<IFeature> itFeature;
    itFeature = ftColPolyFrontierHoles.iterator();
    while (itFeature.hasNext()) {
      IFeature ft = itFeature.next();
      // envelope rectangulaire horizontale ...
      // ILineString ls = new
      // GM_LineString(ftColPolyConserve.envelope().getGeom()
      // .coord());
      // envelope convexe...
      ILineString ls = new GM_LineString(ftColPolyConserve.getGeomAggregate()
          .convexHull().coord());

      if (ft.getGeom().intersects(ls)) {
        ftColBorderHoles.add(new DefaultFeature(ft.getGeom()));
        itFeature.remove();
      }
    }

    LanduseSimplification.logger.info("Division des trous frontaliers");
    // Les trous localisés à la frontière entre 2 polygones ou plus sont divisés
    // et chaque partie est affectée aux polygones voisins
    IFeatureCollection<IFeature> ftColLsConserve = new FT_FeatureCollection<IFeature>();
    for (IFeature ftFeature : ftColPolyConserve) {
      IPolygon poly = (IPolygon) ftFeature.getGeom().buffer(0);
      ILineString lsExt = new GM_LineString(poly.exteriorCoord());
      ftColLsConserve.add(new DefaultFeature(lsExt));
    }

    ftColLsConserve.add(new DefaultFeature(new GM_LineString(ftColPolyTotal
        .getGeomAggregate().convexHull().coord())));

    // ftColLsConserve.add(new DefaultFeature(new
    // GM_LineString(ftColPolyConserve
    // .envelope().getGeom().coord())));

    Iterator<IFeature> itLs;
    itLs = ftColLsConserve.iterator();
    while (itLs.hasNext()) {
      ILineString ls = (ILineString) itLs.next().getGeom();
      if (!ls.isValid() || ls.length() == 0) {
        itLs.remove();
      }
    }

    CarteTopo carteTopoLandCoverRaw = CarteTopoFactory.newCarteTopo("TopoMap",
        ftColLsConserve, 1.0, true);
    carteTopoLandCoverRaw.filtreNoeudsIsoles();
    carteTopoLandCoverRaw.filtreNoeudsSimples();
    // LanduseSimplification.elimineFaceInfinie(carteTopoLandCoverRaw);
    List<Arc> listArcsPCC = new ArrayList<Arc>();
    for (Face face : carteTopoLandCoverRaw.getPopFaces()) {
      if (face.getGeom().within(ftColPolyFrontierHoles.getGeomAggregate())) {
        IFeatureCollection<IFeature> ftColArcsSkeleton = new FT_FeatureCollection<IFeature>();
        IPopulation<Noeud> popNoeudsToConnect = new Population<Noeud>();

        for (Arc arc : face.arcs()) {
          ftColArcsSkeleton.add(arc);
        }
        for (Noeud noeud : face.noeuds()) {
          popNoeudsToConnect.add(noeud);
        }

        // Transforme la géométrie en points puis la triangule avec JTS
        // La triangulation est utilisée afin de diviser le polygone en
        // plusieurs parties
        IFeatureCollection<IFeature> ftcolPoints = new FT_FeatureCollection<IFeature>();
        for (IDirectPosition dp : face.getCoord()) {
          ftcolPoints.add(new DefaultFeature(dp.toGM_Point()));
        }
        if (face.getGeom().centroid().toGM_Point().within(face.getGeom())) {
          ftcolPoints.add(new DefaultFeature(face.getGeom().centroid()
              .toGM_Point()));
        }
        TriangulationJTS triangule = new TriangulationJTS("TriangulationJTS");
        triangule.importAsNodes(ftcolPoints);
        try {
          triangule.triangule("v");
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        IPopulation<Arc> popArcsTriangles = triangule.getPopArcs();
        for (Arc arc : popArcsTriangles) {
          if (arc.getGeom().within(face.getGeom())) {
            ftColArcsSkeleton.add(arc);
          }
        }
        CarteTopo carteTopoArcsSkeleton = CarteTopoFactory
            .newCarteTopo(ftColArcsSkeleton);
        for (Arc arc : carteTopoArcsSkeleton.getPopArcs()) {
          arc.setOrientation(2);
          arc.setPoids(arc.getGeometrie().length());
        }

        // Cas n°1: le trou est frontalier avec seulement 2 polygones
        // On calcule le chemin à travers la triangulation et le
        // contour entre les 2 noeuds à connecter
        if (popNoeudsToConnect.size() == 2) {
          Noeud noeud1 = popNoeudsToConnect.get(0);
          Noeud noeud2 = popNoeudsToConnect.get(1);
          if (!(noeud1.getCoord().equals(noeud2.getCoord()))) {
            Noeud noeudStart = null;
            Noeud noeudEnd = null;
            for (Noeud noeud : carteTopoArcsSkeleton.getPopNoeuds()) {
              if (noeud.getCoord().equals(noeud1.getCoord())) {
                noeudStart = noeud;
              }
              if (noeud.getCoord().equals(noeud2.getCoord())) {
                noeudEnd = noeud;
              }
            }
            if (noeudStart != null) {
              Groupe groupePCC = noeudStart.plusCourtChemin(noeudEnd, 0);
              listArcsPCC.addAll(groupePCC.getListeArcs());
            }
          }
        }

        // Cas n°2: le trou est frontalier avec plus de 2 polygones
        // On calcul le chemin à travers la triangulation et le
        // contours entre chaque noeud et le noeud le plus proche du centroide
        else if (popNoeudsToConnect.size() > 2) {
          IDirectPosition dpCentroid = face.getGeom().centroid();
          Noeud noeudCentroid = null;
          double distanceMin = Double.MAX_VALUE;
          for (Noeud noeud : carteTopoArcsSkeleton.getPopNoeuds()) {
            double distance = dpCentroid.distance2D(noeud.getCoord());
            if (distance < distanceMin) {
              distanceMin = distance;
              noeudCentroid = noeud;
            }
          }
          if (noeudCentroid == null) {
            continue;
          }
          for (Noeud noeud1 : popNoeudsToConnect) {
            Noeud noeudStart = null;
            Noeud noeudEnd = noeudCentroid;
            if (!(noeud1.getCoord().equals(noeudCentroid.getCoord()))) {
              for (Noeud noeud : carteTopoArcsSkeleton.getPopNoeuds()) {
                if (noeud.getCoord().equals(noeud1.getCoord())) {
                  noeudStart = noeud;
                }
              }
            }
            if (noeudStart != null) {
              Groupe groupePCC = noeudStart.plusCourtChemin(noeudEnd, 0);
              listArcsPCC.addAll(groupePCC.getListeArcs());
            }
          }
        }
      }
    }

    IFeatureCollection<IFeature> ftColLsConserveBorder = new FT_FeatureCollection<IFeature>();
    ftColLsConserveBorder.addAll(ftColLsConserve);
    ftColLsConserveBorder.addAll(listArcsPCC);
    CarteTopo carteTopoLandCoverBorder = CarteTopoFactory.newCarteTopo(
        "TopoMap", ftColLsConserveBorder, 1.0, true);
    carteTopoLandCoverBorder.filtreNoeudsIsoles();
    carteTopoLandCoverBorder.filtreNoeudsSimples();
    LanduseSimplification.elimineFaceInfinie(carteTopoLandCoverBorder);

    // on vire l'arc le plus grand entre chaque polygone du trous
    // (cad qu'on affecte la face au polygone avec lequel elle partage la plus
    // grande frontière)
    Population<Arc> popArcsToDelete = new Population<Arc>();
    for (IFeature ft : ftColPolyFrontierHoles) {
      for (Face face : carteTopoLandCoverBorder.getPopFaces()) {
        if (face.getGeom().within(ft.getGeom())) {
          double arcLongMax = 0;
          int IdArcMax = 0;
          for (int i = 0; i < face.arcs().size(); i++) {
            double lengthArc = face.arcs().get(i).getGeom().length();
            if (lengthArc > arcLongMax) {
              arcLongMax = lengthArc;
              IdArcMax = i;
            }
          }
          popArcsToDelete.add(face.arcs().get(IdArcMax));
        }
      }
    }

    Iterator<Arc> itArc;
    itArc = carteTopoLandCoverBorder.getPopArcs().iterator();
    while (itArc.hasNext()) {
      Arc arc = itArc.next();
      if (arc.getGeom().within(popArcsToDelete.getGeomAggregate())) {
        itArc.remove();
      }
    }

    // On obtient l'occupation du sol finale
    IFeatureCollection<IFeature> ftColLandCoverFinal = new FT_FeatureCollection<IFeature>();
    for (Arc arc : carteTopoLandCoverBorder.getPopArcs()) {
      ftColLandCoverFinal.add(arc);
    }
    CarteTopo carteTopoLandCoverFinal = CarteTopoFactory.newCarteTopo(
        "TopoMap", ftColLandCoverFinal, 1.0, true);
    carteTopoLandCoverFinal.filtreNoeudsIsoles();
    carteTopoLandCoverFinal.filtreNoeudsSimples();
    LanduseSimplification.elimineFaceInfinie(carteTopoLandCoverFinal);

    // Puis on la filtre avec Douglas-Peucker
    IFeatureCollection<IFeature> ftColLandCoverFinalFilter = new FT_FeatureCollection<IFeature>();
    for (Arc arc : carteTopoLandCoverFinal.getPopArcs()) {
      ILineString ls = new GM_LineString(arc.getCoord());
      ILineString lsFiltre = (ILineString) Filtering.DouglasPeucker(ls,
          dpFilter);
      ftColLandCoverFinalFilter.add(new DefaultFeature(lsFiltre));
    }
    CarteTopo carteTopoLandCoverFinalFilter = CarteTopoFactory.newCarteTopo(
        "TopoMap", ftColLandCoverFinalFilter, 5.0, true);
    // CarteTopo carteTopoLandCoverFinalFilter = CarteTopoFactory
    // .newCarteTopo(ftColLandCoverFinalFilter);
    carteTopoLandCoverFinalFilter.filtreNoeudsIsoles();
    carteTopoLandCoverFinalFilter.filtreNoeudsSimples();
    LanduseSimplification.elimineFaceInfinie(carteTopoLandCoverFinalFilter);

    LanduseSimplification.logger
        .info("Export des classes d'occupation du sol généralisées");
    // Export sous forme de Map comprenant la ftCol et le nom de la classe
    Map<Face, String> mapFaceParLandUseType = new HashMap<Face, String>();
    for (Face face : carteTopoLandCoverFinalFilter.getPopFaces()) {
      Iterator<IFeatureCollection<IFeature>> itFtColOut;
      itFtColOut = mapFtColOutTmp.keySet().iterator();
      Map<String, Double> mapSurfaceParFtCol = new HashMap<String, Double>();
      while (itFtColOut.hasNext()) {
        IFeatureCollection<IFeature> ftColOut = itFtColOut.next();
        String nomFtCol = mapFtColOutTmp.get(ftColOut);
        if (!(face.getGeom().intersects(ftColOut.getGeomAggregate()))) {
          continue;
        }
        if (face.getGeom().buffer(0).intersection(ftColOut.getGeomAggregate()) == null) {
          continue;
        }
        if (!(face.getGeom().isValid())) {
          continue;
        }

        double surface = face.getGeom().buffer(0)
            .intersection(ftColOut.getGeomAggregate()).area();
        mapSurfaceParFtCol.put(nomFtCol, surface);
      }
      Iterator<String> itNomFtCol;
      itNomFtCol = mapSurfaceParFtCol.keySet().iterator();

      Double surfaceMax = Double.MIN_VALUE;
      String nomSurfaceMax = null;
      while (itNomFtCol.hasNext()) {
        String ftColName = itNomFtCol.next();
        Double surface = mapSurfaceParFtCol.get(ftColName);
        if (surface > surfaceMax) {
          surfaceMax = surface;
          nomSurfaceMax = ftColName;
        }
      }
      mapFaceParLandUseType.put(face, nomSurfaceMax);
    }

    Map<IFeatureCollection<IFeature>, String> mapFtColOut = new HashMap<IFeatureCollection<IFeature>, String>();
    for (String typeOCS : listTypeOCS) {

      Iterator<Face> itFace;
      itFace = mapFaceParLandUseType.keySet().iterator();
      IFeatureCollection<IFeature> ftColExport = new FT_FeatureCollection<IFeature>();
      while (itFace.hasNext()) {
        Face face = itFace.next();
        String typeOCSFace = mapFaceParLandUseType.get(face);
        if (typeOCS.equals(typeOCSFace)) {
          ftColExport.add(face);
        }
      }
      if (!(ftColExport.isEmpty())) {
        mapFtColOut.put(ftColExport, typeOCS);
      }
    }

    return mapFtColOut;
  }

  /**
   * Union of adjacent polygons
   * 
   **/
  private static List<IPolygon> unionPolygones(List<IPolygon> listPoly) {

    List<IPolygon> listPolyUnion = new ArrayList<IPolygon>();
    List<Geometry> list = new ArrayList<Geometry>();
    for (IPolygon poly : listPoly) {
      poly = (IPolygon) poly.buffer(0);
      Geometry jtsGeom = null;
      try {
        jtsGeom = JtsGeOxygene.makeJtsGeom(poly);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (jtsGeom == null) {
        continue;
      }
      Polygon polygon = (Polygon) jtsGeom.buffer(0);
      if (polygon.isValid() && polygon.getArea() != 0) {
        list.add(jtsGeom.buffer(0));
      } else
        continue;

    }
    Geometry jtsUnion = JtsAlgorithms.union(list);
    IGeometry union = null;
    try {
      union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (union == null) {
      return listPolyUnion;
    }
    if (union.isMultiSurface()) {
      @SuppressWarnings("unchecked")
      IMultiSurface<IPolygon> multiPoly = (IMultiSurface<IPolygon>) union;
      for (IPolygon polygon : multiPoly.getList()) {
        listPolyUnion.add(polygon);
      }
    } else if (union.isPolygon()) {
      IPolygon polygon = (IPolygon) union;
      listPolyUnion.add(polygon);
    }
    return listPolyUnion;
  }

  /**
   * Eliminate the infinite face of the topologic Map.
   * 
   * @param carteTopo
   */
  private static void elimineFaceInfinie(CarteTopo carteTopo) {
    Face infiniteFace = null;
    for (Face face : carteTopo.getPopFaces()) {
      if (face.isInfinite()) {
        infiniteFace = face;
      }
    }
    List<Face> listFaceInfinite = new ArrayList<Face>();
    listFaceInfinite.add(infiniteFace);
    carteTopo.enleveFaces(listFaceInfinite);
  }

}
