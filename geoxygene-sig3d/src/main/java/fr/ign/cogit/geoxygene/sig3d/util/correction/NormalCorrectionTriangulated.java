package fr.ign.cogit.geoxygene.sig3d.util.correction;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.convert.geom.FromPolygonToTriangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Vertex;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 * 
 **/ 
public class NormalCorrectionTriangulated {

  public static IFeatureCollection<IFeature> DEBUG = new FT_FeatureCollection<IFeature>();

  public static IFeatureCollection<IFeature> correct(
      IFeatureCollection<IFeature> featColl) {

    IFeatureCollection<IFeature> featC = new FT_FeatureCollection<IFeature>();
    int nbElem = featColl.size();

    for (int i = 0; i < nbElem; i++) {

      try {
        IFeature feat = (IFeature) featColl.get(i).cloneGeom();

        IGeometry geomT = correct(feat.getGeom());

        if (geomT == null) {
          DEBUG.add((IFeature) featColl.get(i).cloneGeom());
          continue;
        }

        feat.setGeom(geomT);

        AttributeManager.addAttribute(feat, "ID", i, "Integer");
        featC.add(feat);

      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    return featC;

  }

  /**
   * Corrige les normales d'une géométrie, on assume que la géométrie en entrée
   * soit composée de triangles
   * @param geom
   * @return
   */
  public static IGeometry correct(IGeometry geom) {

    List<ITriangle> lTri = initGeom(geom);

    if (lTri == null) {
      return null;
    }

    List<IOrientableSurface> lOS = generateTopology(lTri);

    if (lOS == null) {
      return null;
    }

    return new GM_MultiSurface<ITriangle>(lOS);

  }

  /**
   * Génère des triangles à partir d'une géométrie
   * @param geom
   * @return
   */
  private static List<ITriangle> initGeom(IGeometry geom) {
    List<IOrientableSurface> lOSFaces = new ArrayList<IOrientableSurface>();

    if (geom instanceof IMultiSurface<?>) {

      IMultiSurface<?> multiS = (IMultiSurface<?>) geom;
      lOSFaces.addAll(multiS);

    }

    if (geom instanceof ISolid) {

      ISolid solid = (ISolid) geom;
      lOSFaces.addAll(solid.getFacesList());

    }

    List<ITriangle> lTriangle = FromPolygonToTriangle.convertAndTriangle(lOSFaces);

    if (lTriangle == null) {
      return null;
    }

    return lTriangle;
  }

  /**
   * 
   * @param lTri
   * @return
   */
  private static List<IOrientableSurface> generateTopology(List<ITriangle> lTri) {

    int nbTriangles = lTri.size();

    IDirectPositionList dplSommet = new DirectPositionList();

    double zMax = Double.NEGATIVE_INFINITY;

    // On ne garde qu'une version de chaque sommet
    for (int i = 0; i < nbTriangles; i++) {

      IDirectPositionList dplTemp = lTri.get(i).coord();

      for (int j = 0; j < 3; j++) {
        IDirectPosition dpTemp = dplTemp.get(j);

        if (!dplSommet.contains(dpTemp)) {
          zMax = Math.max(zMax, dpTemp.getZ());
          dplSommet.add(dpTemp);
        }

      }

    }

    // On génère les noeuds
    int nbNoeuds = dplSommet.size();

    List<Vertex> lV = new ArrayList<Vertex>();
    for (int i = 0; i < nbNoeuds; i++) {
      lV.add(new Vertex(dplSommet.get(i)));
    }

    // on génère les triangles
    List<Triangle> lTriNonTraite = new ArrayList<Triangle>();
    for (int i = 0; i < nbTriangles; i++) {

      ITriangle triActu = lTri.get(i);

      if (triActu.area() < 0.01) {
        i--;
        nbTriangles--;
        continue;
      }

      IDirectPositionList dplTemp = triActu.coord();
      IDirectPosition dp0 = dplTemp.get(0);
      IDirectPosition dp1 = dplTemp.get(1);
      IDirectPosition dp2 = dplTemp.get(2);

      Vertex v0 = lV.get(lV.indexOf(dp0));
      Vertex v1 = lV.get(lV.indexOf(dp1));
      Vertex v2 = lV.get(lV.indexOf(dp2));

      Triangle t = new Triangle(v0, v1, v2);
      v0.getLTRiRel().add(t);
      v1.getLTRiRel().add(t);
      v2.getLTRiRel().add(t);

      lTriNonTraite.add(t);

    }

    Vertex vertexActu = determineMaxVertex(lV, zMax);

    List<Triangle> lTriVActu = vertexActu.getLTRiRel();
    Triangle triActu = determineBestTriangle(lTriVActu);

    Vecteur vActu = triActu.getNormal();

    if (vActu.getZ() < 0) {

      triActu.reversePoints();

    }

    // Les triangles en sortie
    List<Triangle> lTriTraite = new ArrayList<Triangle>();

    // On ajoute le triangle actuel aux triangles traités
    // On l'enlève des triangles à traiter
    lTriTraite.add(triActu);
    lTriNonTraite.remove(triActu);

    List<Triangle> lTriangleToProcess = new ArrayList<Triangle>();
    lTriangleToProcess.add(triActu);

    while (lTriangleToProcess.size() != 0) {

      // C'est le triangles que l'on traite
      triActu = lTriangleToProcess.remove(0);

      // On tente de trouver les triangles voisins

      List<Edge> lE = triActu.calculEdge();

      int nbTrianglesNonTraites = lTriNonTraite.size();

      boucletriangle: for (int i = 0; i < nbTrianglesNonTraites; i++) {

        Triangle triNT = lTriNonTraite.get(i);

        for (int j = 0; j < 3; j++) {

          if (triNT.contientEdge(lE.get(j))) {

            triNT.reversePoints();

            i--;
            nbTrianglesNonTraites--;
            lTriNonTraite.remove(triNT);
            lTriangleToProcess.add(triNT);
            lTriTraite.add(triNT);

            continue boucletriangle;

          } else if (triNT.contientEdge(lE.get(j).inverse())) {

            i--;
            nbTrianglesNonTraites--;
            lTriNonTraite.remove(triNT);
            lTriangleToProcess.add(triNT);
            lTriTraite.add(triNT);

            continue boucletriangle;

          }

        }

      }

    }

    if (lTriNonTraite.size() != 0) {

      System.out.println("En plusieurs tours ?");

      lTriTraite.addAll(lTriNonTraite);
      return null;
    }

    List<IOrientableSurface> lOS = convert(lTriTraite);

    return lOS;

  }

  private static List<IOrientableSurface> convert(List<Triangle> lTriTraite) {

    int nbTriangle = lTriTraite.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    for (int i = 0; i < nbTriangle; i++) {
      lOS.add(lTriTraite.get(i).toGeoxygeneSurface());

    }

    return lOS;
  }

  private static Vertex determineMaxVertex(List<Vertex> lV, double zMax) {

    int nbVertex = lV.size();

    for (int i = 0; i < nbVertex; i++) {

      if (lV.get(i).getZ() == zMax) {
        return lV.get(i);

      }

    }

    return null;
  }

  private static Triangle determineBestTriangle(List<Triangle> lTri) {

    Triangle triout = lTri.get(0);
    double zMin = (new Box3D(triout)).getLLDP().getZ();

    int nbTri = lTri.size();

    for (int i = 1; i < nbTri; i++) {
      Triangle tri = lTri.get(i);

      double zMinActu = (new Box3D(tri)).getLLDP().getZ();

      if (zMinActu > zMin) {

        zMin = zMinActu;
        triout = tri;
      }

    }

    return triout;

  }

}
