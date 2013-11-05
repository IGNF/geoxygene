package fr.ign.cogit.osm.importexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.GeographicClass;

public class OpenStreetMapDb extends CartAGenDB {

  public OpenStreetMapDb(String name) {
    super();
    this.setName(name);
    this.setClasses(new ArrayList<GeographicClass>());
    // TODO
  }

  @Override
  public void openFromXml(File file) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public void saveToXml(File file) throws IOException, TransformerException {
    // TODO Auto-generated method stub

  }

  @Override
  public void populateDataset(int scale) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void load(GeographicClass geoClass, int scale) {
    // TODO Auto-generated method stub

  }

  @Override
  public void overwrite(GeographicClass geoClass) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void triggerEnrichments() {
    // TODO Auto-generated method stub

  }

  @Override
  public void addCartagenId() {
    // Do nothing as OSM id are preserved.
  }

}
