package fr.ign.cogit.geoxygene.sig3d.analysis;

import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.convert.FromGeomToLineString;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 * @author mickael brasebin
 *
 */
public class FindObjectInDirection {

	private final static double smallVectorSize = 0.01;

	private final static Logger logger = Logger.getLogger(FindObjectInDirection.class.getName());

	public static void main(String[] args) throws ParseException {

		String polWKT = "POLYGON ((926132.266 6688555.264 0.0, 926132.26 6688555.27 0.0, 926132.27 6688555.28 0.0, 926134.07 6688557.65 0.0, 926134.08 6688557.65 0.0, 926134.09 6688557.66 0.0, 926134.1 6688557.65 0.0, 926134.11 6688557.65 0.0, 926134.16 6688557.61 0.0, 926134.18 6688557.6 0.0, 926161.83 6688535.07 0.0, 926161.91 6688535.01 0.0, 926162.0 6688534.97 0.0, 926162.1 6688534.96 0.0, 926162.2 6688534.96 0.0, 926162.3 6688534.98 0.0, 926162.39 6688535.02 0.0, 926162.47 6688535.07 0.0, 926162.54 6688535.14 0.0, 926202.64 6688585.96 0.0, 926202.66 6688585.97 0.0, 926202.68 6688585.96 0.0, 926212.03 6688578.5 0.0, 926230.4 6688564.17 0.0, 926230.42 6688564.16 0.0, 926213.98 6688541.91 0.0, 926196.959 6688518.829 0.0, 926189.46 6688508.67 0.0, 926189.45 6688508.68 0.0, 926174.611 6688520.768 0.0, 926160.411 6688532.339 0.0, 926160.39 6688532.36 0.0, 926160.378 6688532.366 0.0, 926132.27 6688555.27 0.0, 926132.266 6688555.264 0.0))";

		double maximumDistance = 50;

		IPolygon polygon = (IPolygon) WktGeOxygene.makeGeOxygene(polWKT);
		IFeature parcelle = new DefaultFeature(polygon);

		IDirectPositionList dpl = polygon.coord();
		int nbPoints = dpl.size();

		IFeatureCollection<IFeature> fT = new FT_FeatureCollection<>();
		fT.add(parcelle);

		for (int i = 1; i < nbPoints; i++) {
			IDirectPosition dpPred = dpl.get(0);
			IDirectPosition dpActu = dpl.get(1);
			IDirectPositionList dplTemp = new DirectPositionList();
			dplTemp.add(dpPred);
			dplTemp.add(dpActu);

			ILineString lS = new GM_LineString(dplTemp);

			FindObjectInDirection.find(new DefaultFeature(lS), parcelle, fT, maximumDistance);
		}

	}

	/**
	 * Find an object in the collection collectionToSelect in a direction
	 * perpendicular to bound (a LineString object) in a direction opposite to
	 * parcel
	 * 
	 * @param bound
	 * @param parcel
	 * @param collectionToSelect
	 * @param maximumDistance
	 * @return
	 */
	public static IFeature find(IFeature linestringFeature, IFeature oppositeDirectionFeature,
			IFeatureCollection<? extends IFeature> collectionToSelect, double maximumDistance) {

		return find(linestringFeature.getGeom(), oppositeDirectionFeature.getGeom(), collectionToSelect,
				maximumDistance);

	}

	public static IFeature find(IGeometry linestring, IGeometry oppositeDirection,
			IFeatureCollection<? extends IFeature> collectionToSelect, double maximumDistance) {

		if (collectionToSelect.isEmpty()) {
			return null;
		}

		if (!collectionToSelect.hasSpatialIndex()) {
			collectionToSelect.initSpatialIndex(Tiling.class, true);
		}
		ILineString ls = generateLineofSight(linestring, oppositeDirection, maximumDistance);

		if (ls == null) {
			return null;
		}

		double distance = Double.POSITIVE_INFINITY;
		IFeature bestcandidateParcel = null;

		for (IFeature boundaryTemp : collectionToSelect.select(ls)) {

			double distTemp = boundaryTemp.getGeom().distance(oppositeDirection);

			if (oppositeDirection.buffer(0.5).contains(boundaryTemp.getGeom())) {
				continue;
			}

			if (distTemp < distance) {
				distance = distTemp;
				bestcandidateParcel = boundaryTemp;
			}

		}

		return bestcandidateParcel;

	}

	private static ILineString generateLineofSight(IGeometry geom, IGeometry oppositeDirection,
			double maximumDistance) {
		IDirectPositionList dplBound = geom.coord();

		IDirectPosition dp1 = dplBound.get(0);
		IDirectPosition dp2 = dplBound.get(1);

		Vecteur vLine = new Vecteur(dp1, dp2);

		Vecteur vectOrth = vLine.prodVectoriel(MathConstant.vectZ).getNormalised();
		Vecteur vectOrthNeg = vectOrth.multConstante(-1).getNormalised();

		vectOrth = vectOrth.multConstante(smallVectorSize);
		vectOrthNeg = vectOrthNeg.multConstante(smallVectorSize);

		IDirectPosition lineCenter = geom.centroid();

		IDirectPosition dpDep = vectOrth.translate(lineCenter);
		IDirectPosition dpDepNeg = vectOrthNeg.translate(lineCenter);

		boolean isInPolygonDep = oppositeDirection.contains(new GM_Point(dpDep));
		boolean isInPolygonDepNeg = oppositeDirection.contains(new GM_Point(dpDepNeg));

		if (isInPolygonDep && isInPolygonDepNeg) {

			IMultiCurve<IOrientableCurve> iOC = FromGeomToLineString.convertMC(oppositeDirection);

			double distDep = iOC.distance(new GM_Point(dpDep));
			double distDepNeg = iOC.distance(new GM_Point(dpDepNeg));

			if (distDep < distDepNeg) {
				isInPolygonDepNeg = false;

			} else {
				isInPolygonDep = false;
			}

			logger.warning(
					FindObjectInDirection.class + " TRANSLATION IS IN PARCEL IN BOTH DIRECTION " + oppositeDirection);
		}

		if ((!isInPolygonDep) && (!isInPolygonDepNeg)) {

			IMultiCurve<IOrientableCurve> iOC = FromGeomToLineString.convertMC(oppositeDirection);

			double distDep = iOC.distance(new GM_Point(dpDep));
			double distDepNeg = iOC.distance(new GM_Point(dpDepNeg));

			if (distDep > distDepNeg) {
				isInPolygonDep = true;

			} else {
				isInPolygonDepNeg = true;
			}

			logger.warning(
					FindObjectInDirection.class + " TRANSLATION IS IN PARCEL IN NO DIRECTION " + oppositeDirection);
		}

		Vecteur rightVector = null;

		if (isInPolygonDep) {
			vectOrthNeg.normalise();
			rightVector = vectOrthNeg.multConstante(maximumDistance);

		}

		if (!isInPolygonDep) {
			vectOrth.normalise();
			rightVector = vectOrth.multConstante(maximumDistance);
		}

		if (rightVector == null) {
			return null;
		}

		IDirectPosition dpLine = rightVector.translate(lineCenter);

		IDirectPositionList dplLineString = new DirectPositionList();
		dplLineString.add(dpLine);
		dplLineString.add(lineCenter);

		return new GM_LineString(dplLineString);

	}

}
