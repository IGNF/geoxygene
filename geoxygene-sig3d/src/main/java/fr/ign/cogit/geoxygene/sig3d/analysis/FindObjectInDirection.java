package fr.ign.cogit.geoxygene.sig3d.analysis;

import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.util.MathConstant;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 * @author mickael brasebin
 *
 */
public class FindObjectInDirection {

	private final static double smallVectorSize = 0.01;

	private final static Logger logger = Logger.getLogger(FindObjectInDirection.class.getName());

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
			logger.warning(
					FindObjectInDirection.class + " TRANSLATION IS IN PARCEL IN BOTH DIRECTION " + oppositeDirection);
		}

		if ((!isInPolygonDep) && (!isInPolygonDepNeg)) {
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
