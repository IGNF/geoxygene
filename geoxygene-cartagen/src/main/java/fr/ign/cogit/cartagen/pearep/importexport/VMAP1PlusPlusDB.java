package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFeature;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPSchemaFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;

public class VMAP1PlusPlusDB extends PeaRepDB {

  public VMAP1PlusPlusDB(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    super(file);
    this.setGeneObjImpl(new GeneObjImplementation("vmap1++",
        VMAP1PPFeature.class.getPackage(), VMAP1PPFeature.class,
        new VMAP1PPSchemaFactory()));
  }

  public VMAP1PlusPlusDB(String name) {
    super(name);
    this.setGeneObjImpl(new GeneObjImplementation("vmap1++",
        VMAP1PPFeature.class.getPackage(), VMAP1PPFeature.class,
        new VMAP1PPSchemaFactory()));
  }

  @Override
  public void populateDataset(int scale) {
    CartagenApplication.getInstance().setCreationFactory(
        this.getGeneObjImpl().getCreationFactory());
    VMAP1PlusPlusLoader loader = new VMAP1PlusPlusLoader();
    loader.setDataset(VMAP1PlusPlusDB.this.getDataSet());
    try {
      loader.loadData(new File(this.getSystemPath()), this.getLoadedClasses());
    } catch (ShapefileException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
