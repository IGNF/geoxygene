package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.gml.geometry.AbstractGeometry;
import org.citygml4j.model.gml.geometry.GeometryProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurve;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurveProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiPoint;
import org.citygml4j.model.gml.geometry.aggregates.MultiPointProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiSolid;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurfaceProperty;
import org.citygml4j.model.gml.geometry.complexes.CompositeCurve;
import org.citygml4j.model.gml.geometry.complexes.CompositeSolid;
import org.citygml4j.model.gml.geometry.complexes.CompositeSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractCurve;
import org.citygml4j.model.gml.geometry.primitives.AbstractRingProperty;
import org.citygml4j.model.gml.geometry.primitives.AbstractSolid;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurface;
import org.citygml4j.model.gml.geometry.primitives.AbstractSurfacePatch;
import org.citygml4j.model.gml.geometry.primitives.CurveProperty;
import org.citygml4j.model.gml.geometry.primitives.LineString;
import org.citygml4j.model.gml.geometry.primitives.LineStringSegment;
import org.citygml4j.model.gml.geometry.primitives.LineStringSegmentArrayProperty;
import org.citygml4j.model.gml.geometry.primitives.LinearRing;
import org.citygml4j.model.gml.geometry.primitives.OrientableSurface;
import org.citygml4j.model.gml.geometry.primitives.Point;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import org.citygml4j.model.gml.geometry.primitives.PosOrPointPropertyOrPointRep;
import org.citygml4j.model.gml.geometry.primitives.Rectangle;
import org.citygml4j.model.gml.geometry.primitives.Solid;
import org.citygml4j.model.gml.geometry.primitives.SolidProperty;
import org.citygml4j.model.gml.geometry.primitives.Surface;
import org.citygml4j.model.gml.geometry.primitives.SurfaceProperty;
import org.citygml4j.model.gml.geometry.primitives.Tin;
import org.citygml4j.model.gml.geometry.primitives.Triangle;
import org.citygml4j.model.gml.geometry.primitives.TriangulatedSurface;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ITriangle;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSolid;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolid;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Tin;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_TriangulatedSurface;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeCurve;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSolid;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

/**
 * 
 * @author MBrasebin
 */
public class ConvertCityGMLtoGeometry {
	// La translation que l'on appliquera
	public static double coordXIni = 0;
	public static double coordYIni = 0;
	public static double coordZIni = 0;

	public static double xMin = Double.POSITIVE_INFINITY;
	public static double yMin = Double.POSITIVE_INFINITY;
	public static double zMin = Double.POSITIVE_INFINITY;

	public static double xMax = Double.NEGATIVE_INFINITY;
	public static double yMax = Double.NEGATIVE_INFINITY;
	public static double zMax = Double.NEGATIVE_INFINITY;

	public static IGeometry convertGMLGeometry(GeometryProperty<?> geom) {
		if (geom == null) {
			return null;
		}

		return ConvertCityGMLtoGeometry.convertGMLGeometry(geom.getGeometry());

	}

	/**
	 * Convertir n'importe quelle géométrie CityGML en géométrie géoxygene
	 * 
	 * @param geom
	 *            une géométrie GML de la librairie CityGML4j
	 * @return une géométrie GeOxygene issue de la conversion de la géométrie
	 *         paramètre
	 */
	public static IGeometry convertGMLGeometry(AbstractGeometry geom) {

		if (geom instanceof Solid) {

			return ConvertCityGMLtoGeometry.convertGMLSolid((Solid) geom);

		} else if (geom instanceof CompositeSolid) {

			return ConvertCityGMLtoGeometry.convertGMLCompositeSolid((CompositeSolid) geom);

		} else if (geom instanceof MultiSolid) {

			return ConvertCityGMLtoGeometry.convertGMLMultiSolid((MultiSolid) geom);

		} else if (geom instanceof Polygon) {

			return ConvertCityGMLtoGeometry.convertGMLPolygon((Polygon) geom);

			/*
			 * } else if (geom instanceof Rectangle) {
			 * 
			 * return ConvertyCityGMLGeometry.convertGMLRectangle((Rectangle)
			 * geom);
			 * 
			 * } else if (geom instanceof Triangle) {
			 * 
			 * return ConvertyCityGMLGeometry.convertGMLTriangle((Triangle)
			 * geom);
			 */

		} else if (geom instanceof MultiSurface) {

			return ConvertCityGMLtoGeometry.convertGMLMultiSurface((MultiSurface) geom);

		} else if (geom instanceof Tin) {

			return ConvertCityGMLtoGeometry.convertGMLTin((Tin) geom);

		} else if (geom instanceof TriangulatedSurface) {

			return ConvertCityGMLtoGeometry.convertGMLTriangulatedSurface((TriangulatedSurface) geom);

		} else if (geom instanceof OrientableSurface) {

			List<IOrientableSurface> lOS = ConvertCityGMLtoGeometry
					.convertGMLOrientableSurface((OrientableSurface) geom);
			if (lOS.size() == 1) {
				return lOS.get(0);
			} else {

				return new GM_MultiSurface<IOrientableSurface>(lOS);
			}

		} else if (geom instanceof CompositeSurface) {

			ConvertCityGMLtoGeometry.convertGMLCompositeSurface((CompositeSurface) geom);

		} else if (geom instanceof Surface) {
			List<IOrientableSurface> lOS = ConvertCityGMLtoGeometry.convertGMLSurface((Surface) geom);
			if (lOS.size() == 1) {
				return lOS.get(0);
			} else {

				return new GM_MultiSurface<IOrientableSurface>(lOS);
			}

		} else if (geom instanceof LineString) {

			return ConvertCityGMLtoGeometry.convertGMLLineString((LineString) geom);

		} else if (geom instanceof MultiCurve) {
			return ConvertCityGMLtoGeometry.convertGMLMultiCurve((MultiCurve) geom);

		} else if (geom instanceof CompositeCurve) {
			return ConvertCityGMLtoGeometry.convertGMLCompositeCurve((CompositeCurve) geom);

		} else if (geom instanceof MultiPoint) {
			return ConvertCityGMLtoGeometry.convertGMLMultiPoint((MultiPoint) geom);

		} else if (geom instanceof Point) {
			return ConvertCityGMLtoGeometry.convertGMLPoint((Point) geom);
		}
		// Type de géométrie non reconnu
		if (geom != null) {
			System.out.println(geom.getClass());
		}

		return null;

	}

	// /////////////////////////////Les
	// primitives//////////////////////////////////////

	public static GM_TriangulatedSurface convertGMLTriangulatedSurface(TriangulatedSurface geom) {

		List<ITriangle> lTri = new ArrayList<ITriangle>();

		if (geom.isSetTrianglePatches()) {

			int nbPatchs = geom.getTrianglePatches().getTriangle().size();

			for (int i = 0; i < nbPatchs; i++) {

				lTri.add(convertGMLTriangle(geom.getTrianglePatches().getTriangle().get(i)));

			}

		}

		return new GM_TriangulatedSurface(lTri);
	}

	public static GM_Tin convertGMLTin(Tin geom) {

		List<ILineString> lSBL = new ArrayList<ILineString>();

		// Break line
		if (geom.isSetBreakLines()) {

			List<LineStringSegmentArrayProperty> lGMLBL = geom.getBreakLines();
			int nbElem = lGMLBL.size();

			for (int i = 0; i < nbElem; i++) {

				List<LineStringSegment> lSS = lGMLBL.get(i).getLineStringSegment();

				int nbLSS = lSS.size();
				for (int j = 0; j < nbLSS; j++) {

					LineStringSegment lS = lSS.get(j);

					lSBL.add(new GM_LineString(convertGMLDirectPositionList(lS.getPosList())));

				}

			}

		}
		// Stop Line
		List<ILineString> lSSL = new ArrayList<ILineString>();

		if (geom.isSetStopLines()) {

			List<LineStringSegmentArrayProperty> lGMLBL = geom.getStopLines();
			int nbElem = lGMLBL.size();

			for (int i = 0; i < nbElem; i++) {

				List<LineStringSegment> lSS = lGMLBL.get(i).getLineStringSegment();

				int nbLSS = lSS.size();
				for (int j = 0; j < nbLSS; j++) {

					LineStringSegment lS = lSS.get(j);

					lSSL.add(new GM_LineString(convertGMLDirectPositionList(lS.getPosList())));

				}

			}

		}
		// ControlPoint

		IDirectPositionList iDPL = new DirectPositionList();

		if (geom.isSetControlPoint()) {
			iDPL.addAll(convertGMLDirectPositionList(geom.getControlPoint().getPosList()));

		}

		double maxL = Double.NaN;

		if (geom.isSetMaxLength()) {
			maxL = geom.getMaxLength().getValue();
		}

		// TODO Auto-generated method stub
		return new GM_Tin(iDPL, lSSL, lSBL, (float) maxL);
	}

	/**
	 * Convertit un DirectPosition GML en DirectPosition GeOxygene
	 * 
	 * @param dp
	 *            le DirectPosition GML que l'on souhaite convertir
	 * @return un DirectPosition de GeOxygene
	 */
	public static DirectPosition convertGMLDirectPosition(
			org.citygml4j.model.gml.geometry.primitives.DirectPosition dp) {

		List<Double> lD = dp.getValue();

		ConvertCityGMLtoGeometry.xMin = Math.min(ConvertCityGMLtoGeometry.xMin, lD.get(0));
		ConvertCityGMLtoGeometry.yMin = Math.min(ConvertCityGMLtoGeometry.yMin, lD.get(1));
		ConvertCityGMLtoGeometry.zMin = Math.min(ConvertCityGMLtoGeometry.zMin, lD.get(2));

		ConvertCityGMLtoGeometry.xMax = Math.max(ConvertCityGMLtoGeometry.xMax, lD.get(0));
		ConvertCityGMLtoGeometry.yMax = Math.max(ConvertCityGMLtoGeometry.yMax, lD.get(1));
		ConvertCityGMLtoGeometry.zMax = Math.max(ConvertCityGMLtoGeometry.zMax, lD.get(2));

		return new DirectPosition(lD.get(0) - ConvertCityGMLtoGeometry.coordXIni,
				lD.get(1) - ConvertCityGMLtoGeometry.coordYIni, lD.get(2) - ConvertCityGMLtoGeometry.coordZIni);
	}

	/**
	 * Convertit un DirectPositionList de CityGML4j en DirectPositionList
	 * Geoxyene
	 * 
	 * @param dplGML
	 *            un DirectPositionList GML à convertir
	 * @return un DirectPositionList GeOxygene
	 */
	public static DirectPositionList convertGMLDirectPositionList(
			org.citygml4j.model.gml.geometry.primitives.DirectPositionList dplGML) {

		DirectPositionList dplFinal = new DirectPositionList();

		List<Double> lD = dplGML.getValue();
		int nbElem = lD.size();

		for (int i = 0; i < nbElem / 3; i++) {

			dplFinal.add(new DirectPosition(lD.get(3 * i) - ConvertCityGMLtoGeometry.coordXIni,
					lD.get(3 * i + 1) - ConvertCityGMLtoGeometry.coordYIni,
					lD.get(3 * i + 2) - ConvertCityGMLtoGeometry.coordZIni));

			ConvertCityGMLtoGeometry.xMin = Math.min(ConvertCityGMLtoGeometry.xMin, lD.get(3 * i));
			ConvertCityGMLtoGeometry.yMin = Math.min(ConvertCityGMLtoGeometry.yMin, lD.get(3 * i + 1));
			ConvertCityGMLtoGeometry.zMin = Math.min(ConvertCityGMLtoGeometry.zMin, lD.get(3 * i + 2));

			ConvertCityGMLtoGeometry.xMax = Math.max(ConvertCityGMLtoGeometry.xMax, lD.get(3 * i));
			ConvertCityGMLtoGeometry.yMax = Math.max(ConvertCityGMLtoGeometry.yMax, lD.get(3 * i + 1));
			ConvertCityGMLtoGeometry.zMax = Math.max(ConvertCityGMLtoGeometry.zMax, lD.get(3 * i + 2));

		}

		return dplFinal;
	}

	/**
	 * Convertit en DirectPositionList les points properties
	 * 
	 * @param lPOPPOPR
	 *            une liste de PosOrPointPropertyOrPointRep de CityGML4j
	 * @return un objet DirectPositionList de GeOxygene correspondant à la
	 *         conversion de l'objet paramètre
	 */
	public static DirectPositionList convertPosOrPointPropertyOrPointRep(List<PosOrPointPropertyOrPointRep> lPOPPOPR) {

		int nbPOPPOPR = lPOPPOPR.size();

		DirectPositionList dplFinal = new DirectPositionList();

		for (int i = 0; i < nbPOPPOPR; i++) {
			DirectPosition dp = null;

			if (lPOPPOPR.get(i).getPointProperty() != null) {
				Point p = lPOPPOPR.get(i).getPointProperty().getPoint();
				dp = ConvertCityGMLtoGeometry.convertGMLDirectPosition(p.getPos());

			} else if (lPOPPOPR.get(i).getPointRep() != null) {
				Point p = lPOPPOPR.get(i).getPointRep().getPoint();
				dp = ConvertCityGMLtoGeometry.convertGMLDirectPosition(p.getPos());

			} else {
				dp = ConvertCityGMLtoGeometry.convertGMLDirectPosition(lPOPPOPR.get(i).getPos());

			}

			dplFinal.add(dp);
		}

		return dplFinal;

	}

	/**
	 * Convertit un point de cityGML en point GeOxygene
	 * 
	 * @param p
	 *            le point GML que l'on souhaite convertir
	 * @return un GM_Point de GeOxygene
	 */
	public static GM_Point convertGMLPoint(Point p) {

		return new GM_Point(ConvertCityGMLtoGeometry.convertGMLDirectPosition(p.getPos()));
	}

	/**
	 * Convertit un LineString CityGML en LineString GeOxygene
	 * 
	 * @param ls
	 *            un LineString que l'on souhaite convertir
	 * @return un GM_LineString de GeOxygene
	 */
	public static GM_LineString convertGMLLineString(LineString ls) {

		DirectPositionList dpl = ConvertCityGMLtoGeometry.convertGMLDirectPositionList(ls.getPosList());

		return new GM_LineString(dpl);

	}

	/**
	 * Convertit un polygon de cityGML en polygon GeOxygene
	 * 
	 * @param pol
	 *            un polygone GML que l'on souhaite convertir
	 * @return un GM_Polygon de GeOxygene
	 */
	public static GML_Polygon convertGMLPolygon(Polygon pol) {

		AbstractRingProperty ringExterior = pol.getExterior();
		LinearRing linearRing = ((LinearRing) ringExterior.getRing());

		DirectPositionList dplExt;

		if (linearRing.getPosList() != null) {

			dplExt = ConvertCityGMLtoGeometry.convertGMLDirectPositionList(linearRing.getPosList());

		} else {

			dplExt = ConvertCityGMLtoGeometry
					.convertPosOrPointPropertyOrPointRep(linearRing.getPosOrPointPropertyOrPointRep());
		}

		GML_Polygon poly = new GML_Polygon();
		GML_Ring ring = new GML_Ring(new GM_LineString(dplExt));

		if (linearRing.isSetId()) {
			ring.setID(linearRing.getId());
		}

		
		if(ring.coord().size() < 4){
			return null;
		}
		poly.setExterior(ring);

		if (pol.isSetId()) {
			poly.setID(pol.getId());
		}

		List<AbstractRingProperty> lRing = pol.getInterior();
		int nbInterior = lRing.size();

		if (poly.getID().equals("PolyID46_93_731494_37481")) {
			System.out.println();
		}

		for (int i = 0; i < nbInterior; i++) {

			linearRing = (LinearRing) lRing.get(i).getRing();

			if (linearRing.getPosList() != null) {

				dplExt = ConvertCityGMLtoGeometry.convertGMLDirectPositionList(linearRing.getPosList());

			} else {

				dplExt = ConvertCityGMLtoGeometry
						.convertPosOrPointPropertyOrPointRep(linearRing.getPosOrPointPropertyOrPointRep());
			}

			GML_Ring ringInt = new GML_Ring(new GM_LineString(dplExt));
			if (lRing.get(i).getRing().isSetId()) {
				ringInt.setID(lRing.get(i).getRing().getId());
			}

			poly.addInterior(ringInt);
		}

		return poly;

	}

	/**
	 * Convertit un OrientableSurface CityGML en List de Surfaces GeOxygene
	 * 
	 * @param os
	 *            l'OrientableSurface GML à convertir
	 * @return une liste de GM_OrientableSurface issue de la surface initiale
	 */
	public static List<IOrientableSurface> convertGMLOrientableSurface(OrientableSurface os) {
		AbstractSurface as = os.getBaseSurface().getSurface();
		return ConvertCityGMLtoGeometry.convertGMLOrientableSurface(as);

	}

	public static List<IOrientableSurface> convertGMLOrientableSurface(AbstractSurface as) {

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

		if (as instanceof OrientableSurface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLOrientableSurface((OrientableSurface) as));
		} else if (as instanceof Polygon) {
			
			GML_Polygon pol = ConvertCityGMLtoGeometry.convertGMLPolygon((Polygon) as);
			if(pol != null){
				lOS.add(pol);
			}

			

			/*
			 * } else if (as instanceof MultiSurface) {
			 * 
			 * lOS.addAll(ConvertyCityGMLGeometry
			 * .convertGMLMultiSurface((MultiSurface) as));
			 */
		} else if (as instanceof Surface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLSurface((Surface) as));
		} else if (as instanceof CompositeSurface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSurface((CompositeSurface) as).getGenerator());

		} else {

			System.out.println("OS non reconnu" + as.getClass());
		}

		if (lOS.size() == 0) {
			return null;
		}

		return lOS;

	}

	public static ISolid convertGMLSolid(SolidProperty sol) {

		if (sol == null) {
			return null;
		}

		return ConvertCityGMLtoGeometry.convertGMLSolid((Solid) sol.getSolid());

	}

	/**
	 * Convertit un solide GML en GM_Solid GeOxygene
	 * 
	 * @param sol
	 *            le Solid GML que l'on souhaite convertir
	 * @return un GM_Solid Geoxygene
	 */
	public static ISolid convertGMLSolid(Solid sol) {

		AbstractSurface as = sol.getExterior().getSurface();

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>();

		if (as instanceof OrientableSurface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLOrientableSurface((OrientableSurface) as));
		} else if (as instanceof Polygon) {

			GML_Polygon pol = ConvertCityGMLtoGeometry.convertGMLPolygon((Polygon) as);
			if(pol != null){
				lOS.add(pol);
			}

			/*
			 * } else if (as instanceof MultiSurface) {
			 * 
			 * lOS.addAll(ConvertyCityGMLGeometry
			 * .convertGMLMultiSurface((MultiSurface) as));
			 */
		} else if (as instanceof Surface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLSurface((Surface) as));
		} else if (as instanceof CompositeSurface) {

			lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSurface((CompositeSurface) as).getGenerator());

		} else {

			System.out.println("Solid non reconnu" + as.getClass());
		}

		if (lOS.size() == 0) {
			return null;
		}
		return new GM_Solid(lOS);

	}

	// /////////////////////////////Les Multis Géométries
	// /////////////////////////////////////////

	public static GM_MultiPoint convertGMLMultiPoint(MultiPointProperty multiP) {
		return ConvertCityGMLtoGeometry.convertGMLMultiPoint(multiP.getMultiPoint());

	}

	/**
	 * Conversion de multiPoints cityGML en multiPoints GeOxygene
	 * 
	 * @param multiP
	 *            le multiPoints GML que l'on souhaite convertir
	 * @return un GM_MultiPointGeoxygene
	 */
	public static GM_MultiPoint convertGMLMultiPoint(MultiPoint multiP) {
		List<Point> lP = multiP.getPointMembers().getPoint();
		DirectPositionList dpl = new DirectPositionList();

		int nbPoints = lP.size();

		for (int i = 0; i < nbPoints; i++) {

			dpl.add(ConvertCityGMLtoGeometry.convertGMLDirectPosition(lP.get(i).getPos()));
		}

		return new GM_MultiPoint(dpl);
	}

	/**
	 * Convertit les multiCurves CityGML en multiCurve GeOxygene
	 * 
	 * @param multiC
	 *            un MultiCurve GML à convertir
	 * @return un GM_MultiCurve GeOxygene
	 */
	public static GM_MultiCurve<IOrientableCurve> convertGMLMultiCurve(MultiCurve multiC) {

		List<CurveProperty> multiCurves = multiC.getCurveMember();
		int nbCurves = multiCurves.size();

		List<IOrientableCurve> lCurves = new ArrayList<IOrientableCurve>(nbCurves);

		for (int i = 0; i < nbCurves; i++) {

			AbstractCurve c = multiCurves.get(i).getCurve();

			if (c instanceof LineString) {

				lCurves.add(ConvertCityGMLtoGeometry.convertGMLLineString((LineString) c));

			} else if (c instanceof CompositeCurve) {

				lCurves.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeCurve((CompositeCurve) c).getGenerator());
			} else {

				System.out.println("MS non reconnu" + c.getClass());
			}

		}

		if (lCurves.size() == 0) {
			return null;
		}

		return new GM_MultiCurve<IOrientableCurve>(lCurves);

	}

	public static GM_MultiCurve<IOrientableCurve> convertGMLMultiCurve(MultiCurveProperty multiC) {

		return ConvertCityGMLtoGeometry.convertGMLMultiCurve(multiC.getMultiCurve());

	}

	public static IMultiSurface<IOrientableSurface> convertGMLMultiSurface(MultiSurfaceProperty multiS) {

		if (multiS == null) {
			return null;
		}

		return ConvertCityGMLtoGeometry.convertGMLMultiSurface(multiS.getMultiSurface());

	}

	/**
	 * Convertit une multisurface GML en GM_MultiSurface de GeOxygene
	 * 
	 * @param multiS
	 *            multiSurface GML
	 * @return GM_MultiSurface de GeOxygene
	 */
	public static IMultiSurface<IOrientableSurface> convertGMLMultiSurface(MultiSurface multiS) {
		List<SurfaceProperty> multiSurfaces = multiS.getSurfaceMember();
		int nbSurfaces = multiSurfaces.size();

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

		for (int i = 0; i < nbSurfaces; i++) {
			AbstractSurface as = multiSurfaces.get(i).getSurface();

			if (as instanceof OrientableSurface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLOrientableSurface((OrientableSurface) as));
			} else if (as instanceof Polygon) {

				GML_Polygon pol = ConvertCityGMLtoGeometry.convertGMLPolygon((Polygon) as);
				if(pol != null){
					lOS.add(pol);
				}
				

				/*
				 * } else if (as instanceof MultiSurface) {
				 * 
				 * lOS.addAll(ConvertyCityGMLGeometry
				 * .convertGMLMultiSurface((MultiSurface) as));
				 */
			} else if (as instanceof Surface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLSurface((Surface) as));
			} else if (as instanceof CompositeSurface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSurface((CompositeSurface) as).getGenerator());

			} else {

				System.out.println("Surface non reconnu" + as.getClass());
			}

		}

		return new GM_MultiSurface<IOrientableSurface>(lOS);
	}

	/**
	 * Convertit un MultiSolid GML
	 * 
	 * @param mS
	 *            MultiSolid GML à convertir
	 * @return un MultiSolid GeOxygene
	 */
	public static IMultiSolid<ISolid> convertGMLMultiSolid(MultiSolid mS) {

		List<? extends AbstractSolid> lAS = mS.getSolidMembers().getSolid();
		int nbSolid = lAS.size();

		List<ISolid> lOS = new ArrayList<ISolid>();

		for (int i = 0; i < nbSolid; i++) {
			AbstractSolid as = lAS.get(i);

			if (as instanceof Solid) {
				lOS.add(ConvertCityGMLtoGeometry.convertGMLSolid((Solid) as));

			} else if (as instanceof CompositeSolid) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSolid((CompositeSolid) as).getGenerator());
			} else {

				if (as != null) {
					System.out.println("as non reconnu" + as.getClass());
				} else {
					System.out.println("Convert CityGML abstract Solid null");
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
	 * @param compositeC
	 *            le CompositeCurve GML à convertir
	 * @return un GM_CompositeCurve GeOxygene
	 */
	public static ICompositeCurve convertGMLCompositeCurve(CompositeCurve compositeC) {

		List<CurveProperty> lCP = compositeC.getCurveMember();
		int nbCurves = lCP.size();

		List<IOrientableCurve> lCurves = new ArrayList<IOrientableCurve>(nbCurves);

		for (int i = 0; i < nbCurves; i++) {
			AbstractCurve c = lCP.get(i).getCurve();

			if (c instanceof LineString) {

				lCurves.add(ConvertCityGMLtoGeometry.convertGMLLineString((LineString) c));

			} else if (c instanceof CompositeCurve) {

				lCurves.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeCurve((CompositeCurve) c).getGenerator());
			} else {
				System.out.println("c non reconnu" + c.getClass());
			}

		}

		GM_CompositeCurve cC = new GM_CompositeCurve();
		cC.getGenerator().addAll(lCurves);

		return cC;

	}

	/**
	 * Convertit un CompositeSurface de GML en GM_CompositeSurface GeOxygene
	 * 
	 * @param compositeS
	 *            CompositeSurface GML à convertir
	 * @return GM_CompositeSurface issu de la conversion
	 */
	public static GM_CompositeSurface convertGMLCompositeSurface(CompositeSurface compositeS) {

		List<SurfaceProperty> multiSurfaces = compositeS.getSurfaceMember();
		int nbSurfaces = multiSurfaces.size();

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

		for (int i = 0; i < nbSurfaces; i++) {
			AbstractSurface as = multiSurfaces.get(i).getSurface();

			if (as instanceof OrientableSurface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLOrientableSurface((OrientableSurface) as));
			} else if (as instanceof Polygon) {

				GML_Polygon pol = ConvertCityGMLtoGeometry.convertGMLPolygon((Polygon) as);
				if(pol != null)
				{
					lOS.add(pol);
				}
				

				/*
				 * } else if (as instanceof MultiSurface) {
				 * 
				 * lOS.addAll(ConvertyCityGMLGeometry
				 * .convertGMLMultiSurface((MultiSurface) as));
				 */
			} else if (as instanceof Surface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLSurface((Surface) as));
			} else if (as instanceof CompositeSurface) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSurface((CompositeSurface) as).getGenerator());

			} else {
				if (as != null) {
					System.out.println("as non reconnu" + as.getClass());
				} else {
					System.out.println("ConvertCityGML : abstract solid null");
				}

			}

		}

		GM_CompositeSurface compS = new GM_CompositeSurface();
		compS.getGenerator().addAll(lOS);
		// compS.getElement().addAll(lOS);

		return compS;
	}

	/**
	 * Convertit un CompositeSolid GML en GM_CompositeSolid GeOxygene
	 * 
	 * @param cS
	 *            le CompositeSolid GML à convertir
	 * @return un GM_CompositeSolid issu de la conversion
	 */
	public static GM_CompositeSolid convertGMLCompositeSolid(CompositeSolid cS) {
		List<SolidProperty> lSP = cS.getSolidMember();

		int nbSolid = lSP.size();

		List<ISolid> lOS = new ArrayList<ISolid>(nbSolid);

		for (int i = 0; i < nbSolid; i++) {

			AbstractSolid as = lSP.get(i).getSolid();
			if (as instanceof Solid) {
				lOS.add(ConvertCityGMLtoGeometry.convertGMLSolid((Solid) as));

			} else if (as instanceof CompositeSolid) {

				lOS.addAll(ConvertCityGMLtoGeometry.convertGMLCompositeSolid((CompositeSolid) as).getGenerator());
			} else {

				System.out.println("Solid non reconnu" + as.getClass());
			}

		}

		GM_CompositeSolid cs = new GM_CompositeSolid();
		cs.getGenerator().addAll(lOS);

		return cs;
	}

	// /////////////////////////////////////Les objets autres //
	// //////////////////////////////////

	/**
	 * Convertit un objet Surface de GML en une liste de GM_OrientableSurface
	 * GeOxygene
	 * 
	 * @param sur
	 *            la surface que l'on souhaite convertir
	 * @return une liste de GM_OrientableSurface issue de la conversion de
	 *         l'objet paramètre
	 */
	public static List<IOrientableSurface> convertGMLSurface(Surface sur) {
		List<? extends AbstractSurfacePatch> lASP = sur.getPatches().getSurfacePatch();

		int nbSurfaces = lASP.size();

		List<IOrientableSurface> lOS = new ArrayList<IOrientableSurface>(nbSurfaces);

		for (int i = 0; i < nbSurfaces; i++) {
			AbstractSurfacePatch as = lASP.get(i);

			if (as instanceof Triangle) {

				lOS.add(ConvertCityGMLtoGeometry.convertGMLTriangle((Triangle) as));

			} else if (as instanceof Rectangle) {
				lOS.add(ConvertCityGMLtoGeometry.convertGMLRectangle((Rectangle) as));
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
	 * @param r
	 *            le rectangle que l'on souhaite convertir
	 * @return un GM_Polygon issu de la conversion du rectangle
	 */
	public static GML_Polygon convertGMLRectangle(Rectangle r) {

		LinearRing linearRing = (LinearRing) r.getExterior().getRing();

		DirectPositionList dplExt = null;

		if (linearRing.getPosList() != null) {

			dplExt = ConvertCityGMLtoGeometry.convertGMLDirectPositionList(linearRing.getPosList());

		} else {

			dplExt = ConvertCityGMLtoGeometry
					.convertPosOrPointPropertyOrPointRep(linearRing.getPosOrPointPropertyOrPointRep());
		}
		GML_Polygon pol = new GML_Polygon(new GML_Ring(new GM_LineString(dplExt)));

		return pol;
	}

	/**
	 * Convertit un triangle GML en GM_Triangle (utilisé lors de la conversion
	 * de TIN)
	 * 
	 * @param t
	 *            le triangle que l'on souhaite convertir
	 * @return un GM_Triangle issu de l'objet initial
	 */
	public static GM_Triangle convertGMLTriangle(Triangle t) {

		LinearRing linearRing = (LinearRing) t.getExterior().getRing();

		DirectPositionList dplExt = null;

		if (linearRing.getPosList() != null) {

			dplExt = ConvertCityGMLtoGeometry.convertGMLDirectPositionList(linearRing.getPosList());

		} else {

			dplExt = ConvertCityGMLtoGeometry
					.convertPosOrPointPropertyOrPointRep(linearRing.getPosOrPointPropertyOrPointRep());
		}

		GM_Triangle tri = new GM_Triangle(dplExt.get(0), dplExt.get(1), dplExt.get(2));

		return tri;
	}
}
