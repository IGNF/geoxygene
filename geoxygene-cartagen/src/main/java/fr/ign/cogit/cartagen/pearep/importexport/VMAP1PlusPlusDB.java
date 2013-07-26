package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public boolean isDbFile(File file) {
    int indexPt = file.getName().lastIndexOf(".");
    String extension = file.getName().substring(indexPt + 1);
    if (!extension.equals("shp"))
      return false;
    String last = file.getName().substring(indexPt - 1, indexPt);
    Set<String> figuresSet = new HashSet<String>();
    figuresSet.add("0");
    figuresSet.add("1");
    figuresSet.add("2");
    figuresSet.add("3");
    figuresSet.add("4");
    figuresSet.add("5");
    figuresSet.add("6");
    figuresSet.add("7");
    figuresSet.add("8");
    figuresSet.add("9");
    if (figuresSet.contains(last))
      return false;
    return true;
  }
}
