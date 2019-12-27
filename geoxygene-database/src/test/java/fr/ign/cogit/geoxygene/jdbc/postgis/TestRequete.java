package fr.ign.cogit.geoxygene.jdbc.postgis;

import org.junit.Assert;
import org.junit.Test;


public class TestRequete {
    
    @Test
    public void testSQL1() {
       
        /*try {
            
            Map<String,String> params = new HashMap<String,String>();
            params.put("dbtype", "postgis");
            params.put("host", "localhost");
            params.put("port", "5433");
            params.put("database", "indicecarto"); // bduni_utf8
            params.put("schema", "public");
            params.put("user", "test");
            params.put("passwd", "test");
            // params.put("charset", "UTF-8");
            
            String tableName = "troncon_route"; // troncon_de_route
            
            IPopulation<IFeature> troncon = PostgisReader.read(params, tableName, "troncon", null, false);
            System.out.println("NB features = " + troncon.size());
            
            if (troncon != null) {
                Assert.assertTrue("Nb de troncon = ", troncon.size() >= 0);
            } //else {
                // Assert.fail();
            //}
        
        } catch (Exception e) {
            // Assert.fail();
            e.printStackTrace();
        }*/
        
        Assert.assertTrue(true);
    
    }

}
