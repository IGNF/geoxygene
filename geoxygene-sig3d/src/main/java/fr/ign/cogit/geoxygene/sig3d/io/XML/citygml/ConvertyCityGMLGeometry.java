package fr.ign.cogit.geoxygene.sig3d.io.XML.citygml;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.gml.AbstractCurve;
import org.citygml4j.model.gml.AbstractGeometry;
import org.citygml4j.model.gml.AbstractRingProperty;
import org.citygml4j.model.gml.AbstractSolid;
import org.citygml4j.model.gml.AbstractSurface;
import org.citygml4j.model.gml.AbstractSurfacePatch;
import org.citygml4j.model.gml.CompositeCurve;
import org.citygml4j.model.gml.CompositeSolid;
import org.citygml4j.model.gml.CompositeSurface;
import org.citygml4j.model.gml.CurveProperty;
import org.citygml4j.model.gml.LineString;
import org.citygml4j.model.gml.LinearRing;
import org.citygml4j.model.gml.MultiCurve;
import org.citygml4j.model.gml.MultiPoint;
import org.citygml4j.model.gml.MultiSolid;
import org.citygml4j.model.gml.MultiSurface;
import org.citygml4j.model.gml.OrientableSurface;
import org.citygml4j.model.gml.Point;
import org.citygml4j.model.gml.Polygon;
import org.citygml4j.model.gml.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.Rectangle;
import org.citygml4j.model.gml.Solid;
import org.citygml4j.model.gml.SolidProperty;
import org.citygml4j.model.gml.Surface;
import org.citygml4j.model.gml.SurfaceProperty;
import org.citygml4j.model.gml.Triangle;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
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
 * @version 0.1
 * 
 * Classe utilisée pour convertir les géométries GML issues du parsing d'un
 * fichier CityGML dans les classes de la librairie CityGML4j. A moins que les
 * attributs coordXIni, coordYIni et coordZIni soient renseignés à 0,0,0 une
 * translation sera appliquée à tous les objets du jeu à convertir afin de
 * permettre une meilleure visualisation des données. (Translation vers 0,0,0)
 * Class used to convert GML geometries from a parsing of a CityGML file in
 * classes of CityGML4j librairie. 0,0,0 muste be assigne to coordXIni,
 * coordYIni and coordZIni unless a translation will be applyed on the loading
 * objects to bring them back near 0,0,0 coordinates for a better vizualisation
 * 
 * 
 */
public class ConvertyCityGMLGeometry {
  // La translation que l'on appliquera
  protected static double coordXIni = Double.NaN;
  protected static double coordYIni = Double.NaN;
  protected static double coordZIni = Double.NaN;

  // Les identifiants des Ring rencontrés (utile pour le plaquage de textures)
  private List<String> lRingID = new ArrayList<String>();

  public List<String> getlRingID() {
    return this.lRingID;
  }

  public ConvertyCityGMLGeometry() {
    super();
  }

  /**
   * Convertir n'importe quelle géométrie CityGML en géométrie géoxygene
   * 
   * @param geom une géométrie GML de la librairie CityGML4j
   * @return une géométrie GeOxygene issue de la conversion de la géométrie
   *         paramètre
   */
  public IGeometry convertGMLGeometry(AbstractGeometry geom) {

    if (geom instanceof Solid) {

      return this.convertGMLSolid((Solid) geom);

    } else if (geom instanceof CompositeSolid) {

      return this.convertGMLCompositeSolid((CompositeSolid) geom);

    } else if (geom instanceof MultiSolid) {

      return this.convertGMLMultiSolid((MultiSolid) geom);

    } else if (geom instanceof Polygon) {

      return this.convertGMLPolygon((Polygon) geom);

    } else if (geom instanceof Rectangle) {

      return this.convertGMLRectangle((Rectangle) geom);

    } else if (geom instanceof Triangle) {

      return this.convertGMLTriangle((Triangle) geom);

    } else if (geom instanceof MultiSurface) {

      return this.convertGMLMultiSurface((MultiSurface) geom);

    } else if (geom instanceof OrientableSurface) {

      List<IOrientableSurface> lOS = this
          .convertGMLOrientableSurface((OrientableSurface) geom);
      if (lOS.size() == 1) {
        return lOS.get(0);
      } else {

        return new GM_MultiSurface<IOrientableSurface>(lOS);
      }

    } else if (geom instanceof CompositeSurface) {

      return this.convertGMLCompositeSurface((CompositeSurface) geom);

    } else if (geom instanceof Surface) {
      List<IOrientableSurface> lOS = this.convertGMLSurface((Surface) geom);
      if (lOS.size() == 1) {
        return lOS.get(0);
      } else {

        return new GM_MultiSurface<IOrientableSurface>(lOS);
      }

    } else if (geom instanceof LineString) {

      return this.convertGMLLineString((LineString) geom);

    } else if (geom instanceof MultiCurve) {
      return this.convertGMLMultiCurve((MultiCurve) geom);

    } else if (geom instanceof CompositeCurve) {
      return this.convertGMLCompositeCurve((CompositeCurve) geom);

    } else if (geom instanceof MultiPoint) {
      return this.convertGMLMultiPoint((MultiPoint) geom);

    } else if (geom instanceof Point) {
      return this.convertGMLPoint((Point) geom);
    }
    // Type de géométrie non reconnu
    System.out.println(geom.getClass());
    return null;

  }

  // /////////////////////////////Les
  // primitives//////////////////////////////////////

  /**
   * Convertit un DirectPosition GML en DirectPosition GeOxygene
   * 
   * @param dp le DirectPosition GML que l'on souhaite convertir
   * @return un DirectPosition de GeOxygene
   */
  public IDirectPosition convertGMLDirectPosition(
      org.citygml4j.model.gml.DirectPosition dp) {

    List<Double> lD = dp.getValue();

    if (Double.isNaN(ConvertyCityGMLGeometry.coordXIni)) {
      ConvertyCityGMLGeometry.coordXIni = lD.get(0);
      ConvertyCityGMLGeometry.coordYIni = lD.get(1);
      ConvertyCityGMLGeometry.coordZIni = lD.get(2);

    }

    return new DirectPosition(lD.get(0) - ConvertyCityGMLGeometry.coordXIni,
        lD.get(1) - ConvertyCityGMLGeometry.coordYIni, lD.get(2)
            - ConvertyCityGMLGeometry.coordZIni);
  }

  /**
   * Convertit un DirectPositionList de CityGML4j en DirectPositionList Geoxyene
   * 
   * @param dplGML un DirectPositionList GML à convertir
   * @return un DirectPositionList GeOxygene
   */
  public IDirectPositionList convertGMLDirectPositionList(
      org.citygml4j.model.gml.DirectPositionList dplGML) {

    IDirectPositionList dplFinal = new DirectPositionList();

    List<Double> lD = dplGML.getValue();
    int nbElem = lD.size();

    if (Double.isNaN(ConvertyCityGMLGeometry.coordXIni)) {
      ConvertyCityGMLGeometry.coordXIni = lD.get(0);
      ConvertyCityGMLGeometry.coordYIni = lD.get(1);
      ConvertyCityGMLGeometry.coordZIni = lD.get(2);

    }

    for (int i = 0; i < nbElem / 3; i++) {

      dplFinal.add(new DirectPosition(lD.get(3 * i)
          - ConvertyCityGMLGeometry.coordXIni, lD.get(3 * i + 1)
          - ConvertyCityGMLGeometry.coordYIni, lD.get(3 * i + 2)
          - ConvertyCityGMLGeometry.coordZIni));

    }

    return dplFinal;
  }

  /**
   * Convertit en DirectPositionList les points properties
   * 
   * @param lPOPPOPR une liste de PosOrPointPropertyOrPointRep de CityGML4j
   * @return un objet DirectPositionList de GeOxygene correspondant à la
   *         conversion de l'objet paramètre
   */
  public IDirectPositionList convertPosOrPointPropertyOrPointRep(
      List<PosOrPointPropertyOrPointRep> lPOPPOPR) {

    int nbPOPPOPR = lPOPPOPR.size();

    IDirectPositionList dplFinal = new DirectPositionList();

    for (int i = 0; i < nbPOPPOPR; i++) {
      IDirectPosition dp = null;

      if (lPOPPOPR.get(i).getPointProperty() != null) {
        Point p = lPOPPOPR.get(i).getPointProperty().getPoint();
        dp = this.convertGMLDirectPosition(p.getPos());

      } else if (lPOPPOPR.get(i).getPointRep() != null) {
        Point p = lPOPPOPR.get(i).getPointRep().getPoint();
        dp = this.convertGMLDirectPosition(p.getPos());

      } else {
        dp = this.convertGMLDirectPosition(lPOPPOPR.get(i).getPos());

      }

      dplFinal.add(dp);
    }

    return dplFinal;

  }

  /**
   * Convertit un point de cityGML en point GeOxygene
   * 
   * @param p le point GML que l'on souhaite convertir
   * @return un GM_Point de GeOxygene
   */
  public IPoint convertGMLPoint(Point p) {

    return new GM_Point(this.convertGMLDirectPosition(p.getPos()));
  }

  /**
   * Convertit un LineString CityGML en LineString GeOxygene
   * 
   * @param ls un LineString que l'on souhaite convertir
   * @return un GM_LineString de GeOxygene
   */
  public ILineString convertGMLLineString(LineString ls) {

    IDirectPositionList dpl = this
        .convertGMLDirectPositionList(ls.getPosList());

    return new GM_LineString(dpl);

  }

  /**
   * Convertit un polygon de cityGML en polygon GeOxygene
   * 
   * @param pol un polygone GML que l'on souhaite convertir
   * @return un GM_Polygon de GeOxygene
   */
  public IPolygon convertGMLPolygon(Polygon pol) {

    AbstractRingProperty ringExterior = pol.getExterior();
    LinearRing linearRing = ((LinearRing) ringExterior.getRing());

    this.lRingID.add(pol.getId());

    IDirectPositionList dplExt;

    if (linearRing.getPosList() != null) {

      dplExt = this.convertGMLDirectPositionList(linearRing.getPosList());

    } else {

      dplExt = this.convertPosOrPointPropertyOrPointRep(linearRing
          .getPosOrPointPropertyOrPointRep());
    }

    GM_Polygon poly = new GM_Polygon(new GM_LineString(dplExt));

    List<AbstractRingProperty> lRing = pol.getInterior();
    int nbInterior = lRing.size();

    for (int i = 0; i < nbInterior; i++) {

      linearRing = (LinearRing) lRing.get(i).getRing();

      if (linearRing.getPosList() != null) {

        dplExt = this.convertGMLDirectPositionList(linearRing.getPosList());

      } else {

        dplExt = this.convertPosOrPointPropertyOrPointRep(linearRing
            .getPosOrPointPropertyOrPointRep());
      }

      poly.addInterior(new GM_Ring(new GM_LineString(dplExt)));
    }

    return poly;

  }

  /**
   * Convertit un OrientableSurface CityGML en List de Surfaces GeOxygene
   * 
   * @param os l'OrientableSurface GML à convertir
   * @return une liste de GM_OrientableSurface issue de la surface initiale
   */
  public List<IOrientableSurface> convertGMLOrientableSurface(
      OrientableSurface os) {

    AbstractSurface as = os.getBaseSurface().getSurface();
    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    if (as instanceof OrientableSurface) {

      lOS.addAll(this.convertGMLOrientableSurface((OrientableSurface) as));
    } else if (as instanceof Polygon) {

      lOS.add(this.convertGMLPolygon((Polygon) as));

    } else if (as instanceof MultiSurface) {

      lOS.addAll(this.convertGMLMultiSurface((MultiSurface) as));
    } else if (as instanceof Surface) {

      lOS.addAll(this.convertGMLSurface((Surface) as));
    } else if (as instanceof CompositeSurface) {

      lOS.addAll(this.convertGMLCompositeSurface((CompositeSurface) as)
          .getGenerator());

    } else {

      System.out.println("OS non reconnu" + as.getClass());
    }

    return lOS;

  }

  /**
   * Convertit un solide GML en GM_Solid GeOxygene
   * 
   * @param sol le Solid GML que l'on souhaite convertir
   * @return un GM_Solid Geoxygene
   */
  public ISolid convertGMLSolid(Solid sol) {

    AbstractSurface as = sol.getExterior().getSurface();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

    if (as instanceof OrientableSurface) {

      lOS.addAll(this.convertGMLOrientableSurface((OrientableSurface) as));
    } else if (as instanceof Polygon) {

      lOS.add(this.convertGMLPolygon((Polygon) as));

    } else if (as instanceof MultiSurface) {

      lOS.addAll(this.convertGMLMultiSurface((MultiSurface) as));
    } else if (as instanceof Surface) {

      lOS.addAll(this.convertGMLSurface((Surface) as));
    } else if (as instanceof CompositeSurface) {

      lOS.addAll(this.convertGMLCompositeSurface((CompositeSurface) as)
          .getGenerator());

    } else {

      System.out.println("Solid non reconnu" + as.getClass());
    }

    return new GM_Solid(lOS);

  }

  // /////////////////////////////Les Multis Géométries
  // /////////////////////////////////////////

  /**
   * Conversion de multiPoints cityGML en multiPoints GeOxygene
   * 
   * @param multiP le multiPoints GML que l'on souhaite convertir
   * @return un GM_MultiPointGeoxygene
   */
  public IMultiPoint convertGMLMultiPoint(MultiPoint multiP) {
    List<Point> lP = multiP.getPointMembers().getPoint();
    IDirectPositionList dpl = new DirectPositionList();

    int nbPoints = lP.size();

    for (int i = 0; i < nbPoints; i++) {

      dpl.add(this.convertGMLDirectPosition(lP.get(i).getPos()));
    }

    return new GM_MultiPoint(dpl);
  }

  /**
   * Convertit les multiCurves CityGML en multiCurve GeOxygene
   * 
   * @param multiC un MultiCurve GML à convertir
   * @return un GM_MultiCurve GeOxygene
   */
  public IMultiCurve<IOrientableCurve> convertGMLMultiCurve(MultiCurve multiC) {

    List<CurveProperty> multiCurves = multiC.getCurveMember();
    int nbCurves = multiCurves.size();

    List<IOrientableCurve> lCurves = new ArrayList<IOrientableCurve>(nbCurves);

    for (int i = 0; i < nbCurves; i++) {

      AbstractCurve c = multiCurves.get(i).getCurve();

      if (c instanceof LineString) {

        lCurves.add(this.convertGMLLineString((LineString) c));

      } else if (c instanceof CompositeCurve) {

        lCurves.addAll(this.convertGMLCompositeCurve((CompositeCurve) c)
            .getGenerator());
      } else {

        System.out.println("MS non reconnu" + c.getClass());
      }

    }

    return new GM_MultiCurve<IOrientableCurve>(lCurves);

  }

  /**
   * Convertit une multisurface GML en GM_MultiSurface de GeOxygene
   * 
   * @param multiS multiSurface GML
   * @return GM_MultiSurface de GeOxygene
   */
  public IMultiSurface<IOrientableSurface> convertGMLMultiSurface(
      MultiSurface multiS) {
    List<SurfaceProperty> multiSurfaces = multiS.getSurfaceMember();
    int nbSurfaces = multiSurfaces.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

    for (int i = 0; i < nbSurfaces; i++) {
      AbstractSurface as = multiSurfaces.get(i).getSurface();

      if (as instanceof OrientableSurface) {

        lOS.addAll(this.convertGMLOrientableSurface((OrientableSurface) as));
      } else if (as instanceof Polygon) {

        lOS.add(this.convertGMLPolygon((Polygon) as));

      } else if (as instanceof MultiSurface) {

        lOS.addAll(this.convertGMLMultiSurface((MultiSurface) as));
      } else if (as instanceof Surface) {

        lOS.addAll(this.convertGMLSurface((Surface) as));
      } else if (as instanceof CompositeSurface) {

        lOS.addAll(this.convertGMLCompositeSurface((CompositeSurface) as)
            .getGenerator());

      } else {

        System.out.println("Surface non reconnu" + as.getClass());
      }

    }

    return new GM_MultiSurface<IOrientableSurface>(lOS);
  }

  /**
   * Convertit un MultiSolid GML
   * 
   * @param mS MultiSolid GML à convertir
   * @return un MultiSolid GeOxygene
   */
  public IMultiSolid<ISolid> convertGMLMultiSolid(MultiSolid mS) {

    List<AbstractSolid> lAS = mS.getSolidMembers().getSolid();
    int nbSolid = lAS.size();

    List<ISolid> lOS = new ArrayList<ISolid>();

    for (int i = 0; i < nbSolid; i++) {
      AbstractSolid as = lAS.get(i);

      if (as instanceof Solid) {
        lOS.add(this.convertGMLSolid((Solid) as));

      } else if (as instanceof CompositeSolid) {

        lOS.addAll(this.convertGMLCompositeSolid((CompositeSolid) as)
            .getGenerator());
      } else {

        if (as != null) {
          System.out.println("as non reconnu" + as.getClass());
        } else {
          System.out.println("as nulle");
        }
      }

    }

    if (lOS.size() == 0) {
      return null;
    }

    return new GM_MultiSolid<ISolid>(lOS);
  }

  // /////////////////////////////////Les objets
  // composites//////////////////////////////////////

  /**
   * Transforme les composites CurveCityGML en composites GeOxygene
   * 
   * @param compositeC le CompositeCurve GML à convertir
   * @return un GM_CompositeCurve GeOxygene
   */
  public ICompositeCurve convertGMLCompositeCurve(CompositeCurve compositeC) {

    List<CurveProperty> lCP = compositeC.getCurveMember();
    int nbCurves = lCP.size();

    List<IOrientableCurve> lCurves = new ArrayList<IOrientableCurve>(nbCurves);

    for (int i = 0; i < nbCurves; i++) {
      AbstractCurve c = lCP.get(i).getCurve();

      if (c instanceof LineString) {

        lCurves.add(this.convertGMLLineString((LineString) c));

      } else if (c instanceof CompositeCurve) {

        lCurves.addAll(this.convertGMLCompositeCurve((CompositeCurve) c)
            .getGenerator());
      } else {
        System.out.println("c non reconnu" + c.getClass());
      }

    }

    ICompositeCurve cC = new GM_CompositeCurve();
    cC.getGenerator().addAll(lCurves);

    return cC;

  }

  /**
   * Convertit un CompositeSurface de GML en GM_CompositeSurface GeOxygene
   * 
   * @param compositeS CompositeSurface GML à convertir
   * @return GM_CompositeSurface issu de la conversion
   */
  public ICompositeSurface convertGMLCompositeSurface(
      CompositeSurface compositeS) {

    List<SurfaceProperty> multiSurfaces = compositeS.getSurfaceMember();
    int nbSurfaces = multiSurfaces.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

    for (int i = 0; i < nbSurfaces; i++) {
      AbstractSurface as = multiSurfaces.get(i).getSurface();

      if (as instanceof OrientableSurface) {

        lOS.addAll(this.convertGMLOrientableSurface((OrientableSurface) as));
      } else if (as instanceof Polygon) {

        lOS.add(this.convertGMLPolygon((Polygon) as));

      } else if (as instanceof MultiSurface) {

        lOS.addAll(this.convertGMLMultiSurface((MultiSurface) as));
      } else if (as instanceof Surface) {

        lOS.addAll(this.convertGMLSurface((Surface) as));
      } else if (as instanceof CompositeSurface) {

        lOS.addAll(this.convertGMLCompositeSurface((CompositeSurface) as)
            .getGenerator());

      } else {
        if (as != null) {
          System.out.println("as non reconnu" + as.getClass());
        } else {
          System.out.println("as nulle");
        }

      }

    }

    ICompositeSurface compS = new GM_CompositeSurface();
    compS.getGenerator().addAll(lOS);
   // compS.getElement().addAll(lOS);

    return compS;
  }

  /**
   * Convertit un CompositeSolid GML en GM_CompositeSolid GeOxygene
   * 
   * @param cS le CompositeSolid GML à convertir
   * @return un GM_CompositeSolid issu de la conversion
   */
  public ICompositeSolid convertGMLCompositeSolid(CompositeSolid cS) {
    List<SolidProperty> lSP = cS.getSolidMember();

    int nbSolid = lSP.size();

    List<ISolid> lOS = new ArrayList<ISolid>(nbSolid);

    for (int i = 0; i < nbSolid; i++) {

      AbstractSolid as = lSP.get(i).getSolid();
      if (as instanceof Solid) {
        lOS.add(this.convertGMLSolid((Solid) as));

      } else if (as instanceof CompositeSolid) {

        lOS.addAll(this.convertGMLCompositeSolid((CompositeSolid) as)
            .getGenerator());
      } else {

        System.out.println("Solid non reconnu" + as.getClass());
      }

    }

    ICompositeSolid cs = new GM_CompositeSolid();
    cs.getGenerator().addAll(lOS);

    return cs;
  }

  // /////////////////////////////////////Les objets autres //
  // //////////////////////////////////

  /**
   * Convertit un objet Surface de GML en une liste de GM_OrientableSurface
   * GeOxygene
   * 
   * @param sur la surface que l'on souhaite convertir
   * @return une liste de GM_OrientableSurface issue de la conversion de l'objet
   *         paramètre
   */
  public List<IOrientableSurface> convertGMLSurface(Surface sur) {
    List<? extends AbstractSurfacePatch> lASP = sur.getPatches()
        .getSurfacePatch();

    int nbSurfaces = lASP.size();

    List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

    for (int i = 0; i < nbSurfaces; i++) {
      AbstractSurfacePatch as = lASP.get(i);

      if (as instanceof Triangle) {

        lOS.add(this.convertGMLTriangle((Triangle) as));

      } else if (as instanceof Rectangle) {
        lOS.add(this.convertGMLRectangle((Rectangle) as));
      } else {

        System.out.println("Patch non reconnu" + as.getClass());
      }
    }

    return lOS;
  }

  /**
   * Convertit un rectangle GML en GM_Polygon GeOxygene (utilisé pour la
   * conversion de MNT)
   * 
   * @param r le rectangle que l'on souhaite convertir
   * @return un GM_Polygon issu de la conversion du rectangle
   */
  public IPolygon convertGMLRectangle(Rectangle r) {

    LinearRing linearRing = (LinearRing) r.getExterior().getRing();

    this.lRingID.add(linearRing.getId());

    IDirectPositionList dplExt = null;

    if (linearRing.getPosList() != null) {

      dplExt = this.convertGMLDirectPositionList(linearRing.getPosList());

    } else {

      dplExt = this.convertPosOrPointPropertyOrPointRep(linearRing
          .getPosOrPointPropertyOrPointRep());
    }

    return new GM_Polygon(new GM_Ring(new GM_LineString(dplExt)));
  }

  /**
   * Convertit un triangle GML en GM_Triangle (utilisé lors de la conversion de
   * TIN)
   * 
   * @param t le triangle que l'on souhaite convertir
   * @return un GM_Triangle issu de l'objet initial
   */
  public ITriangle convertGMLTriangle(Triangle t) {

    LinearRing linearRing = (LinearRing) t.getExterior().getRing();

    this.lRingID.add(linearRing.getId());

    IDirectPositionList dplExt = null;

    if (linearRing.getPosList() != null) {

      dplExt = this.convertGMLDirectPositionList(linearRing.getPosList());

    } else {

      dplExt = this.convertPosOrPointPropertyOrPointRep(linearRing
          .getPosOrPointPropertyOrPointRep());
    }

    GM_Triangle tri = new GM_Triangle(dplExt.get(0), dplExt.get(1),
        dplExt.get(2));

    return tri;
  }
}
