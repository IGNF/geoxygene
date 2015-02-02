package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.builder.copy.CopyBuilder;
import org.citygml4j.factory.geometry.DimensionMismatchException;
import org.citygml4j.factory.geometry.GMLGeometryFactory;
import org.citygml4j.impl.gml.AbstractRingPropertyImpl;
import org.citygml4j.impl.gml.CoordinatesImpl;
import org.citygml4j.impl.gml.CurvePropertyImpl;
import org.citygml4j.impl.gml.LineStringImpl;
import org.citygml4j.impl.gml.MultiCurveImpl;
import org.citygml4j.impl.gml.MultiCurvePropertyImpl;
import org.citygml4j.impl.gml.MultiSurfaceImpl;
import org.citygml4j.impl.gml.MultiSurfacePropertyImpl;
import org.citygml4j.impl.gml.RingImpl;
import org.citygml4j.impl.gml.SolidImpl;
import org.citygml4j.impl.gml.SolidPropertyImpl;
import org.citygml4j.impl.gml.SurfaceImpl;
import org.citygml4j.impl.gml.SurfacePatchArrayPropertyImpl;
import org.citygml4j.impl.gml.SurfacePropertyImpl;
import org.citygml4j.impl.gml.TriangleImpl;
import org.citygml4j.model.gml.AbstractCurve;
import org.citygml4j.model.gml.AbstractRingProperty;
import org.citygml4j.model.gml.AbstractSurface;
import org.citygml4j.model.gml.AbstractSurfacePatch;
import org.citygml4j.model.gml.Coordinates;
import org.citygml4j.model.gml.CurveProperty;
import org.citygml4j.model.gml.GeometryProperty;
import org.citygml4j.model.gml.LineString;
import org.citygml4j.model.gml.MultiCurve;
import org.citygml4j.model.gml.MultiCurveProperty;
import org.citygml4j.model.gml.MultiSurface;
import org.citygml4j.model.gml.MultiSurfaceProperty;
import org.citygml4j.model.gml.Polygon;
import org.citygml4j.model.gml.Ring;
import org.citygml4j.model.gml.Solid;
import org.citygml4j.model.gml.SolidProperty;
import org.citygml4j.model.gml.Surface;
import org.citygml4j.model.gml.SurfacePatchArrayProperty;
import org.citygml4j.model.gml.SurfaceProperty;
import org.citygml4j.model.gml.Triangle;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class ConvertToCityGMLGeometry {

  @SuppressWarnings("unchecked")
  public static GeometryProperty convertGeometryProperty(IGeometry iGeom) {

    if (iGeom instanceof ISolid) {

      return (GeometryProperty) convertSolidProperty((ISolid) iGeom);
    } else if (iGeom instanceof IMultiSurface<?>) {
      return (GeometryProperty) convertMultiSurfaceProperty((IMultiSurface<IOrientableSurface>) iGeom);
    } else if (iGeom instanceof IMultiCurve<?>) {
      return (GeometryProperty) convertMultiCurveProperty((IMultiCurve<IOrientableCurve>) iGeom);
    } else if (iGeom instanceof IOrientableCurve) {
      return (GeometryProperty) convertCurveProperty((IOrientableCurve) iGeom);
    }

    System.out.println("Classe non gérée : " + iGeom.getClass());

    return null;

  }

  public static SolidProperty convertSolidProperty(ISolid sol) {

    SolidProperty sp = new SolidPropertyImpl();

    sp.setSolid(convertSolid(sol));

    return sp;

  }

  public static Solid convertSolid(ISolid sol) {

    Solid solOut = new SolidImpl();

    SurfaceProperty sp = new SurfacePropertyImpl();
    sp.setSurface(convertSuface(sol.getFacesList()));

    solOut.setExterior(sp);

    return solOut;

  }

  public static MultiSurfaceProperty convertMultiSurfaceProperty(
      IMultiSurface<IOrientableSurface> iMS) {
    MultiSurfaceProperty mSP = new MultiSurfacePropertyImpl();

    mSP.setMultiSurface(convertMultiSurface(iMS));

    return mSP;

  }

  public static MultiSurface convertMultiSurface(
      IMultiSurface<IOrientableSurface> iMS) {

    MultiSurface mS = new MultiSurfaceImpl();

    int nbElem = iMS.size();

    List<SurfaceProperty> lSPT = new ArrayList<SurfaceProperty>();

    for (int i = 0; i < nbElem; i++) {

      AbstractSurface aS = convertPolygon((GM_Polygon) iMS.get(i));
      SurfaceProperty sP = new SurfacePropertyImpl();

      sP.setSurface(aS);
      lSPT.add(sP);

    }

    mS.setSurfaceMember(lSPT);

    return mS;

  }

  public static MultiCurveProperty convertMultiCurveProperty(
      IMultiCurve<IOrientableCurve> iMC) {

    MultiCurveProperty mCP = new MultiCurvePropertyImpl();
    mCP.setMultiCurve(convertMultiCurve(iMC));

    return mCP;

  }

  public static MultiCurve convertMultiCurve(IMultiCurve<IOrientableCurve> iMC) {

    List<CurveProperty> lCP = convertRingCurve(iMC.getList());

    MultiCurve mC = new MultiCurveImpl();
    mC.setCurveMember(lCP);

    return mC;

  }

  public static AbstractSurface convertPolygon(IPolygon poly) {

    GMLGeometryFactory geom = new GMLGeometryFactory();

    Polygon p = null;
    try {
      p = geom.createLinearPolygon(poly.getExterior().coord().toArray3D(), 3);
    } catch (DimensionMismatchException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return p;

    /*
     * 
     * 
     * Polygon polOut = new PolygonImpl(); polOut.setId("Str" + Math.random());
     * 
     * Ring r = convertRing(poly.getExterior());
     * 
     * polOut.setExterior(new Ring);
     * 
     * 
     * 
     * rPT.setRing(r);
     * 
     * 
     * polOut.setExterior(rPT);
     * 
     * 
     * int nbInt = poly.getInterior().size();
     * 
     * List<AbstractRingProperty> lARP = new ArrayList<AbstractRingProperty>();
     * 
     * for (int i = 0; i < nbInt; i++) { Ring rInt =
     * convertRing(poly.getInterior(i));
     * 
     * AbstractRingProperty rPTIn = new AbstractRingPropertyImpl() {
     * 
     * @Override public Object copy(CopyBuilder arg0) { // TODO Auto-generated
     * method stub return super.copyTo(this, arg0);
     * 
     * } };
     * 
     * rPTIn.setRing(rInt); lARP.add(rPTIn);
     * 
     * }
     * 
     * polOut.setInterior(lARP); return polOut;
     */
  }

  public static AbstractSurface convertSuface(List<IOrientableSurface> lOS) {

    List<AbstractSurfacePatch> lASP = new ArrayList<AbstractSurfacePatch>();

    int nbPatchs = lOS.size();

    for (int i = 0; i < nbPatchs; i++) {

      lASP.add(convertPatch(((GM_Polygon) lASP.get(i)).getExterior()));
    }

    SurfacePatchArrayProperty sAP = new SurfacePatchArrayPropertyImpl();

    sAP.setSurfacePatch(lASP);

    Surface as = new SurfaceImpl();
    as.setPatches(sAP);

    return as;

  }

  public static AbstractSurfacePatch convertPatch(IRing iRing) {

    Triangle tri = new TriangleImpl();

    Ring r = convertRing(iRing);
    AbstractRingProperty rPT = new AbstractRingPropertyImpl() {

      @Override
      public Object copy(CopyBuilder arg0) {
        // TODO Auto-generated method stub
        return super.copyTo(this, arg0);

      }
    };

    rPT.setRing(convertRing(iRing));

    tri.setExterior(rPT);
    rPT.setRing(r);

    return tri;
  }

  public static Ring convertRing(IRing r) {
    Ring rOut = new RingImpl();

    rOut.setCurveMember(convertRingCurve(r.getGenerator()));
    return rOut;

  }

  private static List<CurveProperty> convertRingCurve(List<IOrientableCurve> lOC) {

    int nbCurve = lOC.size();

    List<CurveProperty> lCP = new ArrayList<CurveProperty>(nbCurve);

    for (int i = 0; i < nbCurve; i++) {

      lCP.add(convertCurveProperty(lOC.get(i)));

    }

    return lCP;

  }

  private static CurveProperty convertCurveProperty(IOrientableCurve iOC) {

    CurveProperty cP = new CurvePropertyImpl();

    cP.setCurve(convertCurve(iOC));

    return cP;

  }

  public static AbstractCurve convertCurve(IOrientableCurve iOC) {

    LineString lS = new LineStringImpl();
    lS.setCoordinates(convertCoordinates(iOC.coord()));

    return lS;

  }

  public static Coordinates convertCoordinates(IDirectPositionList dpl) {
    CoordinatesImpl coord = new CoordinatesImpl();

    StringBuffer sB = new StringBuffer();

    int nbP = dpl.size();

    for (int i = 0; i < nbP; i++) {

      IDirectPosition dp = dpl.get(i);
      sB.append(dp.getX() + ";" + dp.getY() + ";" + dp.getZ() + ";");

    }

    coord.setValue(sB.toString());
    return coord;
  }

}
