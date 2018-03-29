package fr.ign.cogit.geoxygene.sig3d.sample;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.DynamicVectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Sample to represent the use of DynamicLayer i.e. a layer with moving objects
 * 
 * @author mbrasebin
 *
 */
public class DynamicVectorLayerSimpleTest {

	public static void main(String[] args) throws Exception {

		// Premier exemple
		DynamicVectorLayerSimpleTest.sample1();

	}

	public static void sample1() {

		// Time stamp List
		List<List<Double>> lT = new ArrayList<List<Double>>();

		//Collection containing moving features
		FT_FeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();

		// Generating false trajectories
		// Number of vertices
		int nbElem = 30;
		//Radius of random zone
		int radius = 1000;
		// Number of trajectories
		int nbLayer = 2;

		// Random geenration

		for (int l = 0; l < nbLayer; l++) {
			//For each feaeture, creation of a time stamp list
			List<Double> d = new ArrayList<Double>(nbElem);
			//Creation of a list of position pionts
			DirectPositionList dpl = new DirectPositionList();
			for (int i = 0; i < nbElem; i++) {
				//Adding a random point position
				dpl.add(new DirectPosition(Math.random() * radius, Math.random() * radius, Math.random() * radius));

				//Add time with regular step
				d.add(((double) i) / nbElem);


			}
			lT.add(d);
			//Adding geometry
			featColl.add(new DefaultFeature(new GM_LineString(dpl)));
		}

		//Rendering dynamic layer

		MainWindow main = new MainWindow();
		main.setVisible(true);

		//Getting path to a 3D model
		String path = DynamicVectorLayerSimpleTest.class.getResource(
                "/demo3d/3dmodel/CatN030213.3DS").getPath();

		//Adding the Dynamic Layer to the map
		main.getInterfaceMap3D().getCurrent3DMap()
				.addLayer(new DynamicVectorLayer("DynamicTest", featColl, lT, 30,
						path, 1, 0,
						Math.PI / 2, Math.PI / 2));

	}

}
