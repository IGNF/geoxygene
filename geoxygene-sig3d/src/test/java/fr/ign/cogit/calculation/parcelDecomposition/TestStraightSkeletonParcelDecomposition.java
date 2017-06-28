package fr.ign.cogit.calculation.parcelDecomposition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition.StraightSkeletonParcelDecomposition;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class TestStraightSkeletonParcelDecomposition {

	@Test
	public void test1() {

		IFeatureCollection<IFeature> featColl = ShapefileReader
				.read(getClass().getClassLoader().getResource("parcelDecomposition/parcelle.shp").toString());

		IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(featColl.get(0).getGeom()).get(0);

		IFeatureCollection<IFeature> featCollRoads = new FT_FeatureCollection<>();

		double maxDepth = 20;
		double maxDistanceToNearestRoad = 10;
		RandomGenerator rng = new MersenneTwister(42);
		double minimalArea = 750;
		double minWidth = 10;
		double maxWidth = 50;
		double noiseParameter = 10;
		StraightSkeletonParcelDecomposition.DEBUG = false;

		IFeatureCollection<IFeature> featCOut = StraightSkeletonParcelDecomposition.runStraightSkeleton(pol,
				featCollRoads, maxDepth, maxDistanceToNearestRoad, minimalArea, minWidth, maxWidth, noiseParameter,
				rng);

		assertNotNull(featCOut);
		assertTrue(! featCOut.isEmpty());

	}

}
