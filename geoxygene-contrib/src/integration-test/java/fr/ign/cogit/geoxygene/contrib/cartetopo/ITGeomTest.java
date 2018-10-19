package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Test;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

public class ITGeomTest {
  
  
  @Test
  public void test1() {
    
    Connection jdbcConnection = null; 
    try { 
      Class.forName("org.postgresql.Driver");
      jdbcConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/dbunit", "dbunit", "dbunit"); 
    
      PGConnection pgConn = (PGConnection) jdbcConnection;
      pgConn.addDataType("geometry", org.postgis.PGgeometry.class);
      
      PreparedStatement ps = null;
      
      // --------------------------------------------------------------------------------------------------------
      
      ps = jdbcConnection.prepareStatement("DELETE from troncon;");
      ps.execute();
      ps.close();
      
      // --------------------------------------------------------------------------------------------------------
      ps = jdbcConnection.prepareStatement(" INSERT INTO public.troncon (sens, the_geom) "
          + " values ( ? , ? ); ");
      ps.setString(1, "sens direct");
      // ps.setObject(2, "GeomFromText('SRID=4326;LineString((010200000002000000000000000000F03F000000000000000000000000000014400000000000000000))");
      ps.setObject(2, new PGgeometry("010200000002000000000000000000F03F000000000000000000000000000014400000000000000000"));
      
      
      int nb = ps.executeUpdate();
      ps.close();
      
      jdbcConnection.commit();
      
      System.out.println("Nb de lignes ajoutées = " + nb);
      
    } catch (Exception e) {
      System.out.println("Erreur = " + e.toString()); 
     } 
    
    
    
    Assert.assertTrue(true);
  }
  

}
