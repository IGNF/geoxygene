package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Color4f;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.conversion.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.conversion.Extrusion3DObject;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Classe abstraite contenant les informations minimales permettant
 *          d'implémenter de nouvelles classes de MNT Pour que la classe soit
 *          fonctionnelle : - Utiliser le constructeur par défaut - Renseigner
 *          les informations accessibles par le getter (un des constructeurs le
 *          fait) en fonction du type de MNT - Implémenter les méthodes :
 *          --processSurfacicGrid --getGeometryAt --castCoordinate
 *          --get3DEnvelope
 * 
 *          Abstract class that gives minimal information to implement new DMT
 *          classes In order to implement it, you need to : - Use one of the
 *          default constructors - Fill information available from getters (one
 *          of the constructors does it) according to DTM type -Implement the
 *          following methods : --processSurfacicGrid --getGeometryAt
 *          --castCoordinate --get3DEnvelope
 */
public abstract class AbstractDTM extends DefaultLayer {

  // Emprise 3D du MNT
  protected Box3D emprise = null;

  // Exageration
  protected int exageration;

  // Chemin du MNT
  protected String path;

  // Chemin de l'image
  protected String imagePath = "";

  // Dégradés
  protected Color[] colorShade = null;

  // Paramètres de représentation
  protected IEnvelope imageEnvelope;

  // Indique que le MNT est rempli
  protected boolean isFilled;

  // Si on applique un dégradé de couleur
  protected Color4f[] color4fShade = null;

  protected AbstractDTM() {
    super();
  }

  private static GeometryFactory fac = new GeometryFactory();

  /**
   * 
   * @param path
   * @param colorShade
   * @param imagePath
   * @param imageEnvelope
   * @param exageration
   * @param isFilled
   */
  public AbstractDTM(String layerName, String path, Color[] colorShade,
      String imagePath, IEnvelope imageEnvelope, int exageration,
      boolean isFilled) {
    super();
    this.exageration = exageration;
    this.path = path;
    this.imagePath = imagePath;
    this.colorShade = colorShade;
    this.imageEnvelope = imageEnvelope;
    this.isFilled = isFilled;
    this.layerName = layerName;
  }

  /**
   * Renvoie sur une emprise donnée un multi polygone correspondant aux
   * géométries JTS des géométries 3D du MNT se trouvant dans cette zone
   * @param xmin
   * @param xmax
   * @param ymin
   * @param ymax
   * @return
   */
  abstract public MultiPolygon processSurfacicGrid(double xmin, double xmax,
      double ymin, double ymax);

  /**
   * Permet de renvoyer la géométrie du modèle se trouvant aux coordonnées x,y
   * @param x
   * @param y
   * @return
   */
  abstract public IGeometry getGeometryAt(double x, double y);

  /**
   * Renvoie le z du MNT en X,Y augmenté du offsetting (renvoie donc x+y +
   * offsetting)
   * 
   * 
   * 
   * @param x
   * @param y
   * @param offsetting
   * @return
   */
  abstract public Coordinate castCoordinate(double x, double y);

  public Coordinate castCoordinate(double x, double y, double offsetting) {

    Coordinate c = castCoordinate(x, y);
    c.z = c.z + offsetting;
    return c;

  }

  /**
   * Renvoie la boite 3D du MNT
   */
  @Override
  abstract public Box3D get3DEnvelope();

  /**
   * Permet de plaquer un shapefile sur le MNT sans extrusion.
   * 
   * @param file le chemin du ShapeFile à plaquer
   * @param isSurSampled indique si l'on souhaite suréchantillonner ou non.
   *          (Ré-échantillonnage en fonction des mailles du MNT)
   * @return Une collection d'entité avec des géométries 3D adapdées au MNT
   * @throws Exception
   */
  public IFeatureCollection<IFeature> mapFeatureCollection(
      IFeatureCollection<? extends IFeature> featColl, boolean isSurSampled)
      throws Exception {

    // Nombre d'élements
    int nb = featColl.size();
    IFeatureCollection<IFeature> lObjFinal = new FT_FeatureCollection<IFeature>();

    // Pour chaque géométrie, on affecte un Z
    for (int i = 0; i < nb; i++) {
      // On récupère l'instance de la géométrie et l'on plaque en
      // fonction de son type
      IFeature feat = featColl.get(i).cloneGeom();
      IGeometry geom = this.mapGeom(feat.getGeom(), 0, true, isSurSampled);
      feat.setGeom(geom);
      lObjFinal.add(feat);

    }
    return lObjFinal;

  }

  /**
   * Permet de plaquer un shapefile sur le MNT sans extrusion.
   * 
   * @param file le chemin du ShapeFile à plaquer
   * @param isSurSampled indique si l'on souhaite suréchantillonner ou non.
   *          (Ré-échantillonnage en fonction des mailles du MNT)
   * @return Une collection d'entité avec des géométries 3D adapdées au MNT
   */
  public IFeatureCollection<IFeature> mapShapeFile(String file,
      boolean isSurSampled) {

    try {

      IFeatureCollection<IFeature> ftColl = ShapefileReader.read(file);

      return this.mapFeatureCollection(ftColl, isSurSampled);

    } catch (Exception e) {

      e.printStackTrace();
    }

    return null;
  }

  /**
   * Plaque une géométrie sur le MNT en fonction de paramètres d'extrusion
   * 
   * @param geom la géométrie que l'on souhaite plaquer
   * @param altMax le paramètre d'altitude maximal (soit une hauteur max soit
   *          une altitude max à atteindre)
   * @param isHeigth - si true alors on extrude de altMax - si false, on extrude
   *          jusqu'à altMax
   * @param isSurSampled indique si l'on suréchantillonne la géométrie en
   *          fonction des mailles du MNT ou si l'on plaque 1 à 1 chacun des
   *          points initiaux de la géométrie
   * @return une géométrie 3D plaquée
   * @throws Exception
   */
  public IGeometry mapGeom(IGeometry geom, double altMax, boolean isHeigth,
      boolean isSurSampled) throws Exception {

    com.vividsolutions.jts.geom.Geometry jtsGeom = JtsGeOxygene
        .makeJtsGeom(geom);
    return this.mapGeom(jtsGeom, altMax, isHeigth, isSurSampled);

  }

  /**
   * @param geom
   * @param altMax
   * @param isHeigth
   * @param isSursampled
   * @return
   * @throws Exception
   */
  public IGeometry mapGeom(com.vividsolutions.jts.geom.Geometry geom,
      double altMax, boolean isHeigth, boolean isSurSampled) throws Exception {

    // On adpate en fonction de la classe de la géométrie, la méthode à
    // appliquer

    // GM_Polygon
    if (geom instanceof Polygon) {
      Polygon poly = (Polygon) geom;

      com.vividsolutions.jts.geom.Geometry geometryJTS = this.mapSurface(poly,
          altMax, isHeigth, isSurSampled && (isHeigth && altMax == 0));
      IGeometry geomTemp = JtsGeOxygene.makeGeOxygeneGeom(geometryJTS);
      return postProcessingSurface(geomTemp, isSurSampled, isHeigth, altMax);
    }

    // GM_MultiSurface
    if (geom instanceof MultiPolygon) {
      MultiPolygon poly = (MultiPolygon) geom;

      int nbElem = poly.getNumGeometries();

      IMultiSurface<IOrientableSurface> resultMS = new GM_MultiSurface<IOrientableSurface>();
      IMultiSolid<ISolid> resultMSolid = new GM_MultiSolid<ISolid>();

      for (int i = 0; i < nbElem; i++) {
        Polygon polyTemp = (Polygon) poly.getGeometryN(i);

        com.vividsolutions.jts.geom.Geometry geometryJTS = this.mapSurface(
            polyTemp, altMax, isHeigth, isSurSampled
                && (isHeigth && altMax == 0));

        IGeometry geomTemp = JtsGeOxygene.makeGeOxygeneGeom(geometryJTS);
        IGeometry geomTransformed = postProcessingSurface(geomTemp,
            isSurSampled, isHeigth, altMax);

        if (geomTransformed == null) {
          continue;
        }

        if (geomTemp instanceof ISolid) {

          resultMSolid.add((ISolid) geomTemp);

        } else if (geomTemp instanceof IMultiSurface<?>) {
          resultMS.addAll((IMultiSurface<?>) geomTemp);
        }else if (geomTemp instanceof IPolygon){
          resultMS.add((IPolygon) geomTemp);
        }

      }

      if (resultMS.size() == 1 && resultMSolid.size() == 0) {
        return resultMS.get(0);
      }

      if (resultMS.size() == 0 && resultMSolid.size() == 1) {
        return resultMSolid.get(0);
      }

      if (resultMS.size() == 0 && resultMSolid.size() > 1) {
        return resultMSolid;
      }

      if (resultMS.size() > 1 && resultMSolid.size() == 0) {
        return resultMS;
      }

      IAggregate<IGeometry> agg = new GM_Aggregate<IGeometry>();
      agg.addAll(resultMSolid);
      agg.addAll(resultMS);
      return agg;

    }

    // GM_Curve
    if (geom instanceof LineString) {
      LineString curve = (LineString) geom;

      LineString gls = this.mapCurve(curve, altMax, isHeigth, isSurSampled);

      if (gls != null) {
        return JtsGeOxygene.makeGeOxygeneGeom(gls);
      }

      return new GM_LineString();
    }

    // GM_MultiCurve
    if (geom instanceof MultiLineString) {

      MultiLineString multiCurve = (MultiLineString) geom;

      int nbElem = multiCurve.getNumGeometries();

      GM_MultiCurve<GM_Curve> resultMCurve = new GM_MultiCurve<GM_Curve>();
      GM_MultiSurface<GM_OrientableSurface> resultMPolygon = new GM_MultiSurface<GM_OrientableSurface>();

      for (int i = 0; i < nbElem; i++) {
        LineString curveTemp = (LineString) multiCurve.getGeometryN(i);

        LineString gls = this.mapCurve(curveTemp, altMax, isHeigth,
            isSurSampled);

        if (gls == null) {
          continue;
        }

        IGeometry geomTemp = JtsGeOxygene.makeGeOxygeneGeom(gls);

        if (geomTemp instanceof GM_OrientableSurface) {
          resultMPolygon.add((GM_OrientableSurface) geomTemp);

        } else {
          resultMCurve.add((GM_Curve) geomTemp);
        }

      }

      if (resultMPolygon.size() == 0) {

        return resultMCurve;
      } else if (resultMCurve.size() == 0) {

        return resultMPolygon;
      } else {
        GM_Aggregate<GM_Object> agg = new GM_Aggregate<GM_Object>();
        agg.addAll(resultMPolygon);
        agg.addAll(resultMCurve);

        return agg;
      }

    }

    // GM_Point
    if (geom instanceof Point) {
      Point point = (Point) geom;
      Coordinate coor = this.castCoordinate(point.getX(), point.getY(), altMax);

      DirectPosition dp = new DirectPosition(coor.x, coor.y, coor.z);
      if (isHeigth) {
        return Extrusion2DObject.convertFromPoint(new GM_Point(dp), dp.getZ(),
            dp.getZ() + altMax);
      } else {

        return Extrusion2DObject.convertFromPoint(new GM_Point(dp), dp.getZ(),
            altMax);
      }

    }

    // GM_MultiPoint
    if (geom instanceof MultiPoint) {
      MultiPoint multiPoint = (MultiPoint) geom;

      int nbElem = multiPoint.getNumGeometries();

      GM_MultiCurve<GM_Curve> resultMCurve = new GM_MultiCurve<GM_Curve>();
      GM_MultiPoint resultMPoints = new GM_MultiPoint();

      for (int i = 0; i < nbElem; i++) {
        Point point = (Point) multiPoint.getGeometryN(i);

        Coordinate coor = this.castCoordinate(point.getX(), point.getY(),
            altMax);

        DirectPosition dp = new DirectPosition(coor.x, coor.y, coor.z);
        IGeometry geomResult = null;
        if (isHeigth) {
          geomResult = Extrusion2DObject.convertFromPoint(new GM_Point(dp),
              dp.getZ(), dp.getZ() + altMax);
        } else {

          geomResult = Extrusion2DObject.convertFromPoint(new GM_Point(dp),
              dp.getZ(), altMax);
        }

        if (geomResult instanceof GM_Curve) {
          resultMCurve.add((GM_Curve) geomResult);

        } else {
          resultMPoints.add((GM_Point) geomResult);
        }

      }

      if (resultMPoints.size() == 0) {

        return resultMCurve;
      } else if (resultMCurve.size() == 0) {

        return resultMPoints;
      } else {
        GM_Aggregate<IGeometry> agg = new GM_Aggregate<IGeometry>();
        agg.addAll(resultMPoints);
        agg.addAll(resultMCurve);

        return agg;
      }

    }
    return null;

  }

  /**
   * Gère l'extrusion des objets de type surfacique après plaquage
   * @param geomTemp
   * @param isSurSampled
   * @param isHeigth
   * @param altMax
   * @return
   */
  private static IGeometry postProcessingSurface(IGeometry geomTemp,
      boolean isSurSampled, boolean isHeigth, double altMax) {

    if (!isSurSampled) {

      if (geomTemp == null || geomTemp.coord() == null) {
        return null;

      }

      Box3D b = new Box3D(geomTemp);
      double zmin = b.getLLDP().getZ();

      if (isHeigth) {
        // On renvoie des solides qui s'extrudent à partir de
        // zmin

        geomTemp = Extrusion2DObject.convertFromGeometry(geomTemp, zmin, zmin
            + altMax);
      } else {
        // On renvoie des solides qui s'extrudent à partir de
        // zmin
        geomTemp = Extrusion2DObject
            .convertFromGeometry(geomTemp, zmin, altMax);
      }

    } else {

      if (isHeigth) {
        // On renvoie des solides qui s'extrudent à partir de
        // zmin

        geomTemp = Extrusion3DObject.conversionFromGeom(geomTemp, altMax);
      } else {

        Box3D b = new Box3D(geomTemp);
        double zmin = b.getLLDP().getZ();

        geomTemp = Extrusion3DObject
            .conversionFromGeom(geomTemp, altMax - zmin);
      }

    }

    return geomTemp;

  }

  /**
   * Fonction plaquant une géométrie de type GM_Polygon sur un MNT
   * 
   * @param poly le polygone à plauqer
   * @param altMax le paramètre d'altitude maximal (soit une hauteur max soit
   *          une altitude max à atteindre)
   * @param isHeigth - si true alors on extrude de altMax - si false, on extrude
   *          jusqu'à altMax
   * @param isSursampled indique si l'on suréchantillonne la géométrie en
   *          fonction des mailles du MNT ou si l'on plaque 1 à 1 chacun des
   *          points initiaux de la géométrie
   * @return un objet de type GM_Polygon, GM_Solid voire GM_MultiSurface (cas du
   *         sur-échantillonnage)
   * @throws Exception
   */
  public IGeometry mapSurface(IPolygon poly, double altMax, boolean isHeigth,
      boolean isSurSampled) throws Exception {

    com.vividsolutions.jts.geom.Geometry geom = JtsGeOxygene.makeJtsGeom(poly);

    return this.mapGeom(geom, altMax, isHeigth, isSurSampled);

  }

  /**
   * Fonction plaquant une géométrie de type Polygon sur un MNT
   * 
   * @param poly le polygone à plauqer
   * @param altMax le paramètre d'altitude maximal (soit une hauteur max soit
   *          une altitude max à atteindre)
   * @param isHeigth - si true alors on extrude de altMax - si false, on extrude
   *          jusqu'à altMax
   * @param isSursampled indique si l'on suréchantillonne la géométrie en
   *          fonction des mailles du MNT ou si l'on plaque 1 à 1 chacun des
   *          points initiaux de la géométrie
   * @return un objet de type GM_Polygon, GM_Solid voire GM_MultiSurface (cas du
   *         sur-échantillonnage)
   * 
   * @Fixme : cas avec une extrusion non gérée
   * @throws Exception
   */
  public com.vividsolutions.jts.geom.Geometry mapSurface(Polygon poly,
      double altMax, boolean isHeigth, boolean isSursampled) {

    // On surechantillonne ou non
    if (!isSursampled) {

      // On ne sur échantillonne pas, on se contente de plaquer les
      // bordures du polygones
      LinearRing gls = fac.createLinearRing(this.mapCurve(
          poly.getExteriorRing(), 0, true, false).getCoordinates());

      Coordinate[] dpl = gls.getCoordinates();

      int nbPoints = dpl.length;

      double zmin = Double.POSITIVE_INFINITY;

      for (int i = 0; i < nbPoints; i++) {

        double ztemp = dpl[i].z;

        if (zmin > ztemp) {

          zmin = ztemp;

        }
      }


      int nbInterior = poly.getNumInteriorRing();

      LinearRing[] holes = new LinearRing[nbInterior];
      for (int i = 0; i < nbInterior; i++) {

        LinearRing gls2 = fac.createLinearRing(this.mapCurve(
            poly.getInteriorRingN(i), 0, true, false).getCoordinates());
        holes[i] = gls2;

      }
      return fac.createPolygon(gls, holes);


    }
    // Cas sur échantillonné

    // On récupère les géométries sur lesquelles il faudra découper le
    // polygone

    List<Polygon> lPoly = new ArrayList<Polygon>();

    MultiPolygon multiP = null;

    if (isSursampled) {

      Coordinate[] coordEnv = poly.getEnvelope().getCoordinates();
      double xmin = coordEnv[0].x;
      double xmax = coordEnv[2].x;

      double ymin = coordEnv[0].y;
      double ymax = coordEnv[2].y;

      multiP = this.processSurfacicGrid(xmin, xmax, ymin, ymax);

    }

    int nbPoly = multiP.getNumGeometries();
    // On crée une géométrie géoxygne pour chacune de ces mailles
    // (2 triangles par maille)
    for (int i = 0; i < nbPoly; i++) {

      if (isSursampled) {

        com.vividsolutions.jts.geom.Geometry ob = multiP.getGeometryN(i)
            .intersection(poly);

        List<Polygon> p1 = this.returnIntersectionFromPolygon(ob);

        int nbElem = p1.size();

        for (int k = 0; k < nbElem; k++) {

          LineString ls = this.mapCurve(p1.get(k).getExteriorRing(), altMax,
              isHeigth, false);

          lPoly.add(fac.createPolygon(
              fac.createLinearRing(ls.getCoordinates()), null));
        }

      } else {

      }

    }

    if (lPoly.size() == 0) {
      return null;
    }

    Polygon[] polygons = lPoly.toArray(new Polygon[0]);

    return fac.createMultiPolygon(polygons);

  }

 

  private List<Polygon> returnIntersectionFromPolygon(
      com.vividsolutions.jts.geom.Geometry geom) {

    List<Polygon> polys = new ArrayList<Polygon>();

    if (geom == null) {
      return null;
    }

    if (geom instanceof Polygon) {

      polys.add((Polygon) geom);
      return polys;

    } else if (geom instanceof MultiPolygon) {

      MultiPolygon multiP = (MultiPolygon) geom;
      int nbElem = multiP.getNumGeometries();

      for (int i = 0; i < nbElem; i++) {
        polys.add((Polygon) multiP.getGeometryN(i));

      }

      return polys;
    } else if (geom instanceof GeometryCollection) {

      GeometryCollection coll = (GeometryCollection) geom;

      int nbGeom = coll.getNumGeometries();

      for (int i = 0; i < nbGeom; i++) {

        polys.addAll(this.returnIntersectionFromPolygon(coll.getGeometryN(i)));
      }
      return polys;
    }

    return polys;
  }

  /**
   * Fonction plaquant une géométrie de type GM_Curve sur un MNT
   * 
   * @param ls la polyligne à plauqer
   * @param altMax le paramètre d'altitude maximal (soit une hauteur max soit
   *          une altitude max à atteindre)
   * @param isHeigth - si true alors on extrude de altMax - si false, on extrude
   *          jusqu'à altMax
   * @param isSurSampled indique si l'on suréchantillonne la géométrie en
   *          fonction des mailles du MNT ou si l'on plaque 1 à 1 chacun des
   *          points initiaux de la géométrie
   * @return un objet de type GM_Polygon, GM_Curve voire GM_MultiCurve (cas du
   *         sur-échantillonnage)
   * @throws Exception
   */
  public ICurve mapCurve(ICurve ls, double altMax, boolean isHeigth,
      boolean isSurSampled) throws Exception {

    com.vividsolutions.jts.geom.Geometry geom = JtsGeOxygene.makeJtsGeom(ls);

    return (GM_LineString) this.mapGeom(geom, altMax, isHeigth, isSurSampled);

  }

  /**
   * Fonction plaquant une géométrie de type LineString sur un MNT
   * 
   * @param ls la polyligne à plauqer
   * @param altMax le paramètre d'altitude maximal (soit une hauteur max soit
   *          une altitude max à atteindre)
   * @param isHeigth - si true alors on extrude de altMax - si false, on extrude
   *          jusqu'à altMax
   * @param isSurSampled indique si l'on suréchantillonne la géométrie en
   *          fonction des mailles du MNT ou si l'on plaque 1 à 1 chacun des
   *          points initiaux de la géométrie
   * @return un objet de type GM_Polygon, GM_Curve voire GM_MultiCurve (cas du
   *         sur-échantillonnage)
   * @throws Exception
   */
  public LineString mapCurve(LineString ls, double altMax, boolean isHeigth,
      boolean isSursampled) {

    Coordinate[] lDPFinal;
    if (isSursampled) {
      // On sur échantillonne donc on découpe les lignes en fonctions des
      // lignes des mailles du MNT
      com.vividsolutions.jts.geom.Geometry env = ls.getEnvelope();

      Coordinate[] coordEnv = env.getCoordinates();

      MultiLineString gml;

      if (coordEnv.length < 3) {

        return null;
      } else {
        gml = this.processLinearGrid(coordEnv[0].x, coordEnv[0].y,
            coordEnv[2].x, coordEnv[2].y);

      }

      // On récupère les points d'intersections
      com.vividsolutions.jts.geom.Geometry obj = gml.intersection(ls);

      // On réordonne les points obtenus et ceux de la ligne initiale
      lDPFinal = this.fusion(ls, obj.getCoordinates()).getCoordinates();
    } else {

      // On ne suréchantillonne pas le ligne finale garde les mêmes points
      // que les initiaux
      lDPFinal = ls.getCoordinates();

    }

    int nbPoints = lDPFinal.length;
    Coordinate[] coords = new Coordinate[nbPoints];
    // On extrude ou plaque les différents points de la polyligne finale en
    // fonction des cas
    for (int i = 0; i < nbPoints; i++) {
      if (isHeigth) {

        coords[i] = this.castCoordinate(lDPFinal[i].x, lDPFinal[i].y, altMax);
      } else {
        coords[i] = this.castCoordinate(lDPFinal[i].x, lDPFinal[i].y, altMax
            - lDPFinal[i].z);

      }

    }

    return fac.createLineString(coords);

  }

  /**
   * Renforce et réordonne les points dpl dans la GM_Curve gls
   * 
   * @param gls GM_Curve initiale
   * @param dpl liste des points à ajouter
   * @return renvoie un GM_LineString correspondant à l'enrichissent de gls par
   *         les sommets dpl
   */
  private LineString fusion(LineString gls, Coordinate[] dpl) {

    // On recupere les points de la ligne initiale
    Coordinate[] dplIni = gls.getCoordinates();
    Coordinate dpPred = dplIni[0];

    int nbelem = dplIni.length;
    // Points servant pour la ligne finale
    List<Coordinate> dplFin = new ArrayList<Coordinate>(nbelem);

    Coordinate dpSuiv = null;

    for (int i = 1; i < nbelem; i++) {
      // On procede par couples de points dans le GM lineString
      dpSuiv = dplIni[i];

      // On regarde le max des distances, on ne recuperera aucun point
      // intermédiaire plus loin
      double max = dpSuiv.distance(dpPred);
      dplFin.add(dpPred);

      Vecteur vIni = new Vecteur(dpSuiv.x - dpPred.x, dpSuiv.y - dpPred.y, 0);

      int nbPRestants = dpl.length;

      List<Coordinate> dplTemp = new ArrayList<Coordinate>();

      ArrayList<Double> tabDist = new ArrayList<Double>();

      for (int j = 0; j < nbPRestants; j++) {
        Vecteur vTemp = new Vecteur(dpl[j].x - dpPred.x, dpl[j].y - dpPred.y, 0);

        // Les points sont alignes

        if (vTemp.prodScalaire(vIni) > 0) {

          if (Math.abs(vTemp.prodVectoriel(vIni).norme()) < 0.00001) {

            dplTemp.add(dpl[j]);
            tabDist.add(vTemp.norme());

          }

        }

      }

      // On ordonne les points et on ajoute a dplFin

      for (int j = 0; j < tabDist.size(); j++) {
        double d = Double.POSITIVE_INFINITY;
        int ind = 0;
        int n = tabDist.size();
        for (int k = 0; k < n; k++) {

          if (tabDist.get(k) < d) {
            ind = k;
            d = tabDist.get(k);

          }

        }
        if (d > max) {
          break;
        }

        dplFin.add(dplTemp.get(ind));
        tabDist.set(ind, Double.POSITIVE_INFINITY);

      }

      dpPred = dpSuiv;
    }

    dplFin.add(dpSuiv);

    int nbP = dplFin.size();
    Coordinate[] coordF = new Coordinate[nbP];
    for (int i = 0; i < nbP; i++) {
      coordF[i] = dplFin.get(i);
    }

    return fac.createLineString(coordF);

  }

  /**
   * Cette fonction permet de projeter un point sur un MNT en lui ajoutant un
   * altitude offsetting
   * 
   * @param dp le point à projeter
   * @param offsetting l'altitude que l'on rajoute au point final
   * @return un point 3D ayant comme altitude Z du MNT + offesting
   */
  public Coordinate castCoordinate(DirectPosition dp, double offsetting) {

    return this.castCoordinate(dp.getX(), dp.getY(), offsetting);
  }

  /**
   * Plaque un directPosition sur le terrain
   * @param dp
   * @return
   */
  public IDirectPosition cast(IDirectPosition dp) {

    IDirectPosition dp2 = (IDirectPosition) dp.clone();
    dp2.setZ(this.castCoordinate(dp.getX(), dp.getY(), 0.0).z);

    return dp2;
  }

  /**
   * Plaque un directPosition sur le terrain
   * @param dp
   * @return
   */
  public IDirectPosition cast(IDirectPosition dp, double offsetting) {

    IDirectPosition dp2 = (IDirectPosition) dp.clone();
    dp2.setZ(this.castCoordinate(dp.getX(), dp.getY(), offsetting).z);

    return dp2;
  }

  /**
   * Renvoie sur une emprise donnée un multi polygone correspondant aux
   * géométries JTS des géométries 3D du MNT se trouvant dans cette zone
   * 
   * 
   * @param dpMin point inférieur gauche
   * @param dpMax point supérieur droit
   * @return une liste des géométries du MNT se trouvant dans cette emprise sous
   *         forme linéaire
   */
  public MultiLineString processLinearGrid(double xmin, double ymin,
      double xmax, double ymax) {
    MultiPolygon mPoly = this.processSurfacicGrid(xmin, xmax, ymin, ymax);

    List<LineString> lS = new ArrayList<LineString>();

    int nbElem = mPoly.getNumGeometries();

    for (int i = 0; i < nbElem; i++) {

      Polygon p = (Polygon) mPoly.getGeometryN(i);

      List<LineString> lsTemp = new ArrayList<LineString>();
      lsTemp.add(p.getExteriorRing());

      int nInt = p.getNumInteriorRing();

      for (int j = 0; j < nInt; j++) {
        lsTemp.add(p.getInteriorRingN(j));
      }

      int nbRing = lsTemp.size();

      for (int j = 0; j < nbRing; j++) {

        LineString r = lsTemp.get(j);
        Coordinate[] coord = r.getCoordinates();

        int nbPoints = coord.length;

        for (int k = 1; k < nbPoints; k++) {

          Coordinate c0 = coord[k - 1];
          Coordinate c1 = coord[k];

          if (this.checkRingInList(c0, c1, lS)) {

            Coordinate[] cTemp = new Coordinate[2];
            cTemp[0] = c0;
            cTemp[1] = c1;

            lS.add(fac.createLinearRing(cTemp));

          }

        }

      }

    }

    return fac.createMultiLineString((LineString[]) lS.toArray());

  }

  private boolean checkRingInList(Coordinate c1, Coordinate c2,
      List<LineString> ls) {
    int nbElem = ls.size();

    for (int i = 0; i < nbElem; i++) {

      LineString lsTemp = ls.get(i);
      Coordinate[] coord = lsTemp.getCoordinates();

      if (coord[0].equals2D(c1) && coord[1].equals2D(c2)) {
        return false;
      }

      if (coord[0].equals2D(c2) && coord[1].equals2D(c1)) {
        return false;
      }

    }

    return true;

  }

  @Override
  public String getLayerName() {
    return this.layerName;
  }

  /**
   * @return Exaggération appliquee au MNT
   */
  public int getExageration() {
    return this.exageration;
  }

  /**
   * @return Chemin du MNT
   */
  public String getPath() {

    return this.path;
  }

  /**
   * @return Chemin de l'image a plaquer
   */
  public String getImagePath() {
    return this.imagePath;
  }

  /**
   * @return renvoie (si il est défini) le dégradé de couleur utilisé pour la
   *         représentation du MNT
   */
  public Color[] getColorShade() {
    return this.colorShade;
  }

  /**
   * @return the imageEnvelope
   */
  public IEnvelope getImageEnvelope() {
    return this.imageEnvelope;
  }

  /**
   * @return indique si le MNT est représenté en mode filaire ou rempli
   */
  public boolean isFilled() {
    return this.isFilled;
  }

}
