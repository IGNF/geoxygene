package fr.ign.cogit.geoxygene.example;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

public class TestShapefileReader {

    /**
     * @param args
     */
    public static void main(String[] args) {
	ObjectViewer vwr = new ObjectViewer();
	Population<DefaultFeature> pop = ShapefileReader.read("/home/jperret/Data/GeOpenSim/Zone4/batiment_Z4/batiment_2002_Z4.shp");
	vwr.addFeatureCollection(pop, "fichier");
    }

}
