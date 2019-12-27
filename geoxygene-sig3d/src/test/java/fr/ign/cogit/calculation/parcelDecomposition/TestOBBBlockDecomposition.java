package fr.ign.cogit.calculation.parcelDecomposition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition.OBBBlockDecomposition;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class TestOBBBlockDecomposition {
	
	
	
	public static void main(String[] args){
		(new TestOBBBlockDecomposition()).test1();
	}

	@Test
	public void test1() {

		IFeatureCollection<IFeature> featColl = ShapefileReader
				.read(getClass().getClassLoader().getResource("parcelDecomposition/parcelle.shp").toString());

		
		if(featColl == null) {
			System.out.println("------C'est nul");
		}
		
		

		// Maxmimal area for a parcel
		double maximalArea = 500;
		// MAximal with to the road
		double maximalWidth = 20;
		// Do we want noisy results
		double noise = 0;
		// Probability to get a cut perpendicular to the OBB
		double epsilon = 0;
		// Exterior from the UrbanBlock if necessary or null
		IMultiCurve<IOrientableCurve> imC = null;
		// Roads are created for this number of decomposition level
		int decompositionLevelWithRoad = 2;
		// Road width
		double roadWidth = 5.0;
		//Boolean forceRoadaccess
		boolean forceRoadAccess= false;
		
		
		
		IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(featColl.get(0).getGeom()).get(0);
		// We run the algorithm of decomposition
		OBBBlockDecomposition obb = new OBBBlockDecomposition(pol, maximalArea, maximalWidth,
				epsilon, null, 2, 5.0, forceRoadAccess);
	
		try {
			IFeatureCollection<IFeature> featCOut = obb.decompParcel(noise);
			assertNotNull(featCOut);
			assertTrue(! featCOut.isEmpty());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}

	}

}
