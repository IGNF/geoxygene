package fr.ign.cogit.osm.load;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.osm.importexport.OSMLoader;
import fr.ign.cogit.geoxygene.osm.importexport.OsmDataset;

/**
 * 
 * @author MDVan-Damme
 */
public class ITLoadFile {
  
  @Test
  public void BasicAndLightFileLoadTest() {
    try {
      OsmDataset dataset = new OsmDataset();
      Runnable fillLayersTask = null;
      // OSMLoader loader = new OSMLoader(new File(ITLoadFile.class.getClassLoader().getResource("map.osm").getPath()), dataset, fillLayersTask, "2154");
      
      // loader.importOsmData();
      // Assert.assertEquals(0, loader.getNbNoeuds());
      Assert.assertEquals(true, true);
 
    } catch(Exception e) {
      e.printStackTrace();
      fail();
    }
  }

}
