package fr.ign.cogit.geoxygene.example;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.jdbc.postgis.PostgisReader;

/**
 * 
 * @author Marie-Dominique
 */
public class Test2Geom {
  
  public static void main(String args[]) {
    
    System.out.println("================================================");
    try {
      
      Map<String,String> params = new HashMap<String,String>();
      params.put("dbtype", "postgis");
      params.put("host", "localhost");
      params.put("port", "5433");
      params.put("database", "test");
      params.put("schema", "public");
      params.put("user", "test");
      params.put("passwd", "test");
      params.put("charset", "UTF-8");
      
      String tableName = "bati_indifferencie";
      
      IPopulation<IFeature> bati = PostgisReader.read(params, tableName, "bati", null, false, "geom"); // centre
      System.out.println("NB features = " + bati.size()); // 4054
      System.out.println(bati.getFeatureType().getGeometryType().getSimpleName()); // GM_MultiSurface  GM_Point
      
      for (int i = 0; i < bati.size(); i++) {
        DefaultFeature feature = (DefaultFeature) bati.get(i);
        //System.out.println(feature.getSchema().getFeatureType().getGeometryType());
        /*if (i < 10) {
          System.out.println(feature.getGeom().toString());
        }*/
        
        
        /*for (int j=0; j < feature.getSchema().getFeatureType().getFeatureAttributes().size(); j++) {
          AttributeType attr = feature.getSchema().getFeatureType().getFeatureAttributeI(j);
          System.out.println(attr.getNomField());
        }*/
        
        // System.out.println(feature.getAttributes().length);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    System.out.println("================================================");
  }

}
