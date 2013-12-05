package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import net.jgeom.ext.mesh.boolop.AnySurfaceBoolOpd;
import net.jgeom.ext.mesh.boolop.BoolopTriangled;
import net.jgeom.ext.mesh.boolop.GeometryConverterd;
import net.jgeom.ext.mesh.boolop.J3DBoolOp;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.convert.java3d.ConversionJava3DGeOxygene;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * @version 1.5
 * 
 *
 * Classe permettant de calculer des Opérations booléennes entre 2 objets
 * volumiques Attention les objets doivent être justes mathématiquement Les
 * constantes INTERSECTION, UNION et DIFFERENCE permettent de choisir
 * l'Opérateur Utilisation de la librairie JGeom https://jgeom.dev.java.net/
 * Class for 3D boolean operation. Only for 3D objects in 3D. Use of
 * https://jgeom.dev.java.net/ library
 * 
 */
public class BooleanOperators {

  private final static Logger logger = Logger.getLogger(BooleanOperators.class
      .getName());

  public final static int INTERSECTION = J3DBoolOp.INTERSECTION;
  public final static int UNION = J3DBoolOp.UNION;
  public final static int DIFFERENCE = J3DBoolOp.DIFFERENCE;

  // On supprime les triangle avec des aires trop petites
  public final static double AREA_TO_REMOVE = 0.001;

  /**
   * Calcule une Opération booléeenne entre 2 solides (Geometries GM_Solid) La
   * geometrie Java3D est utilisee dans ce calcul et le solide doit etre
   * mathematiquement justes Ne fonctionne qu'avec des GM_Solid justes
   * 
   * @param feat1 Premier operande
   * @param feat2 Second operande
   * @param type Selon le type de constante utilisee INTERSECTION - Intersection
   *          UNION - Union DIFFERENCE - Différence
   * @return On effectue en fonction du type l'operation de feat1 sur feat2
   *         (Intersection entre feat1 et feat2 ou union entre feat1 et feat2 ou
   *         difference entre feat1 et feat2)
   */
  public static GM_Solid compute(IFeature feat1, IFeature feat2, int type) {

    // Vérifications d'usage sur les objets
    if (feat1 == null) {
      BooleanOperators.logger.error(Messages
          .getString("CalculOpBoolean.O1NotSolid"));
      return null;

    }

    // Vérifications d'usage sur les objets
    if (feat2 == null) {
      BooleanOperators.logger.error(Messages
          .getString("CalculOpBoolean.O2NotSolid"));
      return null;

    }

    // On vérifie le type de geometrie
    if (!(feat1.getGeom() instanceof GM_Solid)) {

      BooleanOperators.logger.error(Messages
          .getString("CalculOpBoolean.O1NotSolid"));
      return null;
    }

    if (!(feat2.getGeom() instanceof GM_Solid)) {

      BooleanOperators.logger.error(Messages
          .getString("CalculOpBoolean.O2NotSolid"));
      return null;
    }

    IDirectPosition directPositionIni = feat1.getGeom().coord().get(0);
    double x, y, z;
    x = directPositionIni.getX();
    y = directPositionIni.getY();
    z = directPositionIni.getZ();

    Calculation3D.translate(feat1.getGeom(), -x, -y, -z);
    Calculation3D.translate(feat2.getGeom(), -x, -y, -z);

    GeometryInfo inf = ConversionJava3DGeOxygene
        .fromOrientableSToTriangleArray(((GM_Solid) feat1.getGeom())
            .getFacesList());
    Shape3D sh = new Shape3D(inf.getGeometryArray(), new Appearance());

    // récupèration de la géométrie Java3D
    Geometry tmp = sh.getGeometry();

    tmp = GeometryConverterd.transformAndAddColor((TriangleArray) tmp,
        new Transform3D(), new Color3f(1.0f, 0, 0));
    tmp.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    tmp.setCapability(GeometryArray.ALLOW_NORMAL_READ);
    tmp.setCapability(GeometryArray.ALLOW_COUNT_READ);

    tmp.setCapability(Geometry.ALLOW_INTERSECT);

    tmp.setCapability(GeometryArray.ALLOW_COLOR_READ);

    GeometryInfo inf2 = ConversionJava3DGeOxygene
        .fromOrientableSToTriangleArray(((GM_Solid) feat2.getGeom())
            .getFacesList());
    Shape3D sh2 = new Shape3D(inf2.getGeometryArray(), new Appearance());

    // récupèration de la géométrie Java3D

    Geometry tmp2 = sh2.getGeometry();

    tmp2 = GeometryConverterd.transformAndAddColor((TriangleArray) tmp2,
        new Transform3D(), new Color3f(1.0f, 0, 0));
    tmp2.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    tmp2.setCapability(GeometryArray.ALLOW_NORMAL_READ);
    tmp2.setCapability(GeometryArray.ALLOW_COUNT_READ);

    tmp2.setCapability(Geometry.ALLOW_INTERSECT);

    tmp2.setCapability(GeometryArray.ALLOW_COLOR_READ);

    AnySurfaceBoolOpd boolop = new AnySurfaceBoolOpd();

    // Calcule la nouvelle surface
    boolop
        .combineSurface((TriangleArray) tmp, (TriangleArray) tmp2, type, null);

    // On recupere la liste des triangles
    List<?> t = boolop.getResultingTriangles();

    if (t == null) {
      return null;
    }

    int nbElem = t.size();

    ArrayList<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(
        nbElem);
    // On transforme le résultat en géométrie ISO
    for (int i = 0; i < nbElem; i++) {
      BoolopTriangled bopt = (BoolopTriangled) t.get(i);
      Point3d[] lPoints = bopt.getPoints();

      DirectPositionList dpl = new DirectPositionList();

      for (int j = 0; j < 3; j++) {

        Point3d p = lPoints[j];
        DirectPosition dp = new DirectPosition(p.x + x, p.y + y, p.z + z);
        dpl.add(dp);
      }

      dpl.add(dpl.get(0));

      GM_Triangle surf = new GM_Triangle(new GM_LineString(dpl));

      if (surf.area() < BooleanOperators.AREA_TO_REMOVE) {

        continue;
      }

      lOS.add(surf);

    }

    // on replace les objets à l'emplacement initial
    Calculation3D.translate(feat1.getGeom(), x, y, z);
    Calculation3D.translate(feat2.getGeom(), x, y, z);

    return new GM_Solid(lOS);

  }

}
