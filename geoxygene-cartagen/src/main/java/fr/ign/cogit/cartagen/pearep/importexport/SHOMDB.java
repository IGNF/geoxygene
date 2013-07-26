package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.shom.SHOMFeature;
import fr.ign.cogit.cartagen.pearep.shom.SHOMSchemaFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;

public class SHOMDB extends PeaRepDB {

  private boolean loadLand = false;

  public boolean isLoadSea() {
    return loadLand;
  }

  public void setLoadSea(boolean loadSea) {
    this.loadLand = loadSea;
  }

  public SHOMDB(File file) throws ParserConfigurationException, SAXException,
      IOException, ClassNotFoundException {
    super(file);
    this.setGeneObjImpl(new GeneObjImplementation("shom", SHOMFeature.class
        .getPackage(), SHOMFeature.class, new SHOMSchemaFactory()));
  }

  public SHOMDB(String name) {
    super(name);
    this.setGeneObjImpl(new GeneObjImplementation("shom", SHOMFeature.class
        .getPackage(), SHOMFeature.class, new SHOMSchemaFactory()));
  }

  @Override
  public void populateDataset(int scale) {
    CartagenApplication.getInstance().setCreationFactory(
        this.getGeneObjImpl().getCreationFactory());
    SHOMLoader loader = new SHOMLoader(loadLand);
    loader.setDataset(SHOMDB.this.getDataSet());
    try {
      loader.loadData(new File(this.getSystemPath()), this.getLoadedClasses());
    } catch (ShapefileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
