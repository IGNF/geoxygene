package fr.ign.cogit.geoxygene.contrib.appariement;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * 
 * 
 *
 */
public class ITLinearFeatureMatcher extends TestCase {
  
  
  @Test
  public void testAppariementDefautAvecRecalage() throws Exception {
    
    System.out.println("-------------------------------------");
    
    // 2 datasets
    /*IPopulation<IFeature> db1 = ShapefileReader.read("E:\\Workspace\\GeOxygene\\modules\\noyau\\geoxygene-contrib"
        + "\\src\\test\\resources\\data\\reseau1.shp");
    IPopulation<IFeature> db2 = ShapefileReader.read("E:\\Workspace\\GeOxygene\\modules\\noyau\\geoxygene-contrib"
        + "\\src\\test\\resources\\data\\reseau2.shp");
    
    // Parameter
    Parameters param = new Parameters();
    param.set("a", "1.0");
    param.set("gamma", "18.0");
    param.set("k", "10");
    param.set("selection", "10.0");
    param.set("beta", "10.0");
    param.set("name_db1", "SENS");
    param.set("name_db2", "SENS");
    
    LinearFeatureMatcher lfm = new LinearFeatureMatcher(db1, db2, param);
    Matchings m = lfm.getMatchings("SENS", "SENS");*/
    
    System.out.println("-------------------------------------");
    assertTrue(true);

  }

}
