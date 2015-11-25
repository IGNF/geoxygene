package testmapping;


import java.util.Properties;

import fr.ign.cogit.mapping.webentity.spatial.Constants;



public class RTreePropertyFactory {
   public static Properties create() {
      Properties properties = new Properties();
      properties.put(Constants.GEOMETRY_INDEX_TYPE, Constants.GEOMETRY_INDEX_RTREE);

      return properties;
   }
}
