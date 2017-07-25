package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.builder.copy.CopyBuilder;
import org.citygml4j.factory.DimensionMismatchException;
import org.citygml4j.factory.GMLGeometryFactory;
import org.citygml4j.model.gml.basicTypes.Coordinates;
import org.citygml4j.model.gml.geometry.GeometryProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurve;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurveProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractCurve;
import org.citygml4j.model.gml.geometry.primitives.AbstractRing;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurfacePatch;
import org.citygml4j.model.gml.geometry.primitives.CurveProperty;
import org.citygml4j.model.gml.geometry.primitives.LineString;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.Ring;
import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.Surface;
import org.citygml4j.model.gml.geometry.primitives.SurfacePatchArrayProperty;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.Triangle;

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

	public static GeometryProperty<?> convertGeometryProperty(IGeometry iGeom) {

		if (iGeom instanceof ISolid) {

			return convertSolidProperty((ISolid) iGeom);
		} else if (iGeom instanceof IMultiSurface<?>) {
			return convertMultiSurfaceProperty((IMultiSurface<?>) iGeom);
		} else if (iGeom instanceof IMultiCurve<?>) {
			return convertMultiCurveProperty((IMultiCurve<?>) iGeom);
		} else if (iGeom instanceof IOrientableCurve) {
			return convertCurveProperty((IOrientableCurve) iGeom);
		}

		System.out.println("Classe non gérée : " + iGeom.getClass());

		return null;

	}

	public static SolidProperty convertSolidProperty(ISolid sol) {

		SolidProperty sp = new SolidProperty();

		sp.setSolid(convertSolid(sol));

		return sp;

	}

	public static Solid convertSolid(ISolid sol) {

		Solid solOut = new Solid();

		SurfaceProperty sp = new SurfaceProperty();
		sp.setSurface(convertSuface(sol.getFacesList()));

		solOut.setExterior(sp);

		return solOut;

	}

	public static MultiSurfaceProperty convertMultiSurfaceProperty(IMultiSurface<?> iMS) {
		MultiSurfaceProperty mSP = new MultiSurfaceProperty();

		mSP.setMultiSurface(convertMultiSurface(iMS));

		return mSP;

	}

	public static MultiSurface convertMultiSurface(IMultiSurface<?> iMS) {

		MultiSurface mS = new MultiSurface();

		int nbElem = iMS.size();

		List<SurfaceProperty> lSPT = new ArrayList<SurfaceProperty>();

		for (int i = 0; i < nbElem; i++) {

			AbstractSurface aS = convertPolygon((GM_Polygon) iMS.get(i));
			SurfaceProperty sP = new SurfaceProperty();

			sP.setSurface(aS);
			lSPT.add(sP);

		}

		mS.setSurfaceMember(lSPT);

		return mS;

	}

	public static MultiCurveProperty convertMultiCurveProperty(IMultiCurve<?> iMC) {

		MultiCurveProperty mCP = new MultiCurveProperty();
		mCP.setMultiCurve(convertMultiCurve(iMC));

		return mCP;

	}

	public static MultiCurve convertMultiCurve(IMultiCurve<?> iMC) {

		List<CurveProperty> lCP = convertRingCurve(iMC.getList());

		MultiCurve mC = new MultiCurve();
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
		 * Polygon polOut = new Polygon(); polOut.setId("Str" + Math.random());
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
		 * List<AbstractRingProperty> lARP = new
		 * ArrayList<AbstractRingProperty>();
		 * 
		 * for (int i = 0; i < nbInt; i++) { Ring rInt =
		 * convertRing(poly.getInterior(i));
		 * 
		 * AbstractRingProperty rPTIn = new AbstractRingPropertyImpl() {
		 * 
		 * @Override public Object copy(CopyBuilder arg0) { // TODO
		 * Auto-generated method stub return super.copyTo(this, arg0);
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

			lASP.add(convertPatch(((GM_Polygon) lOS.get(i)).getExterior()));
		}

		SurfacePatchArrayProperty sAP = new SurfacePatchArrayProperty();

		sAP.setSurfacePatch(lASP);

		Surface as = new Surface();
		as.setPatches(sAP);

		return as;

	}

	public static AbstractSurfacePatch convertPatch(IRing iRing) {

		Triangle tri = new Triangle();

		Ring r = convertRing(iRing);
		AbstractRingProperty rPT = new AbstractRingProperty() {

			@Override
			public Object copy(CopyBuilder arg0) {
				// TODO Auto-generated method stub
				return super.copyTo(this, arg0);

			}

			@Override
			public Class<AbstractRing> getAssociableClass() {
				// TODO Auto-generated method stub
				return AbstractRing.class;
			}
		};

		rPT.setRing(convertRing(iRing));

		tri.setExterior(rPT);
		rPT.setRing(r);

		return tri;
	}

	public static Ring convertRing(IRing r) {
		Ring rOut = new Ring();

		rOut.setCurveMember(convertRingCurve(r.getGenerator()));
		return rOut;

	}

	private static List<CurveProperty> convertRingCurve(List<? extends IOrientableCurve> lOC) {

		int nbCurve = lOC.size();

		List<CurveProperty> lCP = new ArrayList<CurveProperty>(nbCurve);

		for (int i = 0; i < nbCurve; i++) {

			lCP.add(convertCurveProperty(lOC.get(i)));

		}

		return lCP;

	}

	private static CurveProperty convertCurveProperty(IOrientableCurve iOC) {

		CurveProperty cP = new CurveProperty();

		cP.setCurve(convertCurve(iOC));

		return cP;

	}

	public static AbstractCurve convertCurve(IOrientableCurve iOC) {

		LineString lS = new LineString();
		lS.setCoordinates(convertCoordinates(iOC.coord()));

		return lS;

	}

	public static Coordinates convertCoordinates(IDirectPositionList dpl) {
		Coordinates coord = new Coordinates();

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
