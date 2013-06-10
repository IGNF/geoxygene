package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;

public class MGCPPlusPlusDB extends PeaRepDB {

  private boolean loadSea = false;

  public boolean isLoadSea() {
    return loadSea;
  }

  public void setLoadSea(boolean loadSea) {
    this.loadSea = loadSea;
  }

  public MGCPPlusPlusDB(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    super(file);
    this.setGeneObjImpl(new GeneObjImplementation("mgcp++", MGCPFeature.class
        .getPackage(), MGCPFeature.class));
  }

  public MGCPPlusPlusDB(String name) {
    super(name);
    this.setGeneObjImpl(new GeneObjImplementation("mgcp++", MGCPFeature.class
        .getPackage(), MGCPFeature.class));
  }

  @Override
  public void populateDataset(int scale) {

    MGCPLoader loader = new MGCPLoader(loadSea);
    loader.setDataset(MGCPPlusPlusDB.this.getDataSet());
    try {
      loader.loadData(new File(this.getSystemPath()), this.getLoadedClasses());
    } catch (ShapefileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
