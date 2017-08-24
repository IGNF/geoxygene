package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.jdbc.postgis.PGReader;

/**
 * Only a test, no real use on this project
 * 
 * Deprecated the 22/05/2017
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public class DbReadingTest {
  static String[] keys = {"host", "port" , "database", 
      "schema", "user","passwd", "dbtype"}; 
  static String[] values = {"localhost", "5432", 
      "nepal1", "public", "postgres", "postgres", "postgis"};

  /**
   * Only a test
   */
  public static void mainDbReadingTest() { 
    if(keys.length != values.length)
      throw new IllegalStateException("parameters arrays must be of the same size");
    
    
    Map<String, String> params = new HashMap<String, String>();
    for(int cpt = 0; cpt < keys.length; cpt++)
      params.put(keys[cpt], values[cpt]);
    
    try {
      PGReader testReader = new PGReader(params, "way", null);
      
      System.out.println("Nb Fields : " + testReader.getNbFields());
      //testReader.
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
