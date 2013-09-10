package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.data.shapefile.shp.ShapefileException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSchemaFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
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
        .getPackage(), MGCPFeature.class, new MGCPSchemaFactory()));
  }

  public MGCPPlusPlusDB(String name) {
    super(name);
    this.setGeneObjImpl(new GeneObjImplementation("mgcp++", MGCPFeature.class
        .getPackage(), MGCPFeature.class, new MGCPSchemaFactory()));
  }

  @Override
  public void populateDataset(int scale) {
    CartagenApplication.getInstance().setCreationFactory(
        this.getGeneObjImpl().getCreationFactory());
    MGCPLoader loader = new MGCPLoader(loadSea);
    loader.setDataset(this.getDataSet());
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
    if (file.getName().length() != 10)
      return false;
    String figures = file.getName().substring(3, 6);
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
    if (!figuresSet.contains(figures.substring(0, 1)))
      return false;
    if (!figuresSet.contains(figures.substring(1, 2)))
      return false;
    if (!figuresSet.contains(figures.substring(2)))
      return false;
    return true;
  }
}
