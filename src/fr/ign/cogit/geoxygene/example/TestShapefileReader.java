package fr.ign.cogit.geoxygene.example;

import java.io.File;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIShapefileChoice;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

public class TestShapefileReader {

    /**
     * @param args
     */
    public static void main(String[] args) {
	ObjectViewer vwr = new ObjectViewer();
	GUIShapefileChoice chooser = new GUIShapefileChoice(true);
	File[] files = chooser.getSelectedFiles();
	chooser.dispose();
	if (files!=null) {
	    for (int i = 0 ; i < files.length ; i++) {
		System.out.println(files[i].getAbsolutePath());
		Population<DefaultFeature> pop = ShapefileReader.read(files[i].getAbsolutePath());
		vwr.addFeatureCollection(pop, files[i].getName().substring(0, files[i].getName().lastIndexOf(".")));
	    }
	}
    }

}
