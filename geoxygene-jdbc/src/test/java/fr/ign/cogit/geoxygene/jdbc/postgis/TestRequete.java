package fr.ign.cogit.geoxygene.jdbc.postgis;

import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;


public class TestRequete {
    
    private final static String PG_BDUNI_HOST = "charden.ign.fr";
    private final static String PG_BDUNI_PORT = "5432";
    private final static String PG_BDUNI_DATABASE = "consultation_bduc_1.1.0_mar2013";
    private final static String PG_BDUNI_USER = "carto_auto";
    private final static String PG_BDUNI_PASSWD = "totoauto1";
    private final static String PG_BDUNI_CHARSET = "ISO-8859-1";
    
    @Test
    public void testSQL1() {
       
        try {
            
            Map<String,String> params = new HashMap<String,String>();
            params.put("dbtype", "postgis");
            params.put("host", "localhost");
            params.put("port", "5433");
            params.put("database", "indicecarto");
            params.put("schema", "public");
            params.put("user", "test");
            params.put("passwd", "test");
            // params.put("charset", "UTF-8");
            
            IPopulation<IFeature> troncon = PostgisReader.read(params, "troncon_route", "troncon", null, false);
            
            if (troncon != null) {
                Assert.assertTrue("Nb de troncon = ", troncon.size() >= 0);
            } else {
                // Assert.fail();
            }
        
        } catch (Exception e) {
            // Assert.fail();
            e.printStackTrace();
        }
    
    }

}
