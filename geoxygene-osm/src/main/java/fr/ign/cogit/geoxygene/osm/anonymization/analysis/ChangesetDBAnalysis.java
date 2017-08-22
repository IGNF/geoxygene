package fr.ign.cogit.geoxygene.osm.anonymization.analysis;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;

import fr.ign.cogit.geoxygene.osm.anonymization.db.access.PostgresAccess;

/**
 * Classe permettant d'automatiser des traitements
 * analytiques sur la base de données des changeset
 * d'OpenStreetMap.
 * 
 * 
 * @author Matthieu Dufait
 */
public class ChangesetDBAnalysis {
  
  private static final String staticHost = "localhost";
  private static final String staticPort = "5432";
  private static final String staticName = "osmchangeset";
  private static final String staticUser = "postgres";
  private static final String staticPassword = "postgres";
  
  private PostgresAccess dbAccess;
  
  public static ChangesetDBAnalysis instance = new ChangesetDBAnalysis();
  
  private ChangesetDBAnalysis() {
    this(staticHost, staticPort, staticName, 
        staticUser, staticPassword); 
  }
  
  public ChangesetDBAnalysis(String host, String port, 
      String name, String user, String pass) {
    this.dbAccess = new PostgresAccess(host, port, 
        name, user, pass); 
  }

  /**
   * 
   * @param precision
   * @param start
   * @param end
   * @param filterNull
   * @return
   */
  public int[] centileNumChanges(double precision, double start, double end, boolean filterNull) {
    if(precision <= 0.0 || precision > 1.0)
      throw new IllegalArgumentException("precision must be between 0 and 1, found: "+precision);
    if(start < 0.0 || start >= end)
      throw new IllegalArgumentException("start must be positive, not zero and less than end, found: "+start);
    if(end > 1.0)
      throw new IllegalArgumentException("end must be less than 1, found: "+end);
    
    int[] retour = null;
    
    String query = "SELECT percentile_disc(?) WITHIN GROUP " +
        "(ORDER BY num_changes) AS perc " +
        "FROM osm_changeset";
    if(filterNull)
      query += " WHERE num_changes IS NOT NULL "
      + "AND num_changes > 0";

    Double[] centiles = generateCentiles(precision, start, end);
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet r = null;
    try {
      conn = dbAccess.getConnection();
      stmt = conn.prepareStatement(query);
      
      Array arr =  conn.createArrayOf("numeric", (Object[]) centiles);
      stmt.setArray(1, arr);
      
      r = stmt.executeQuery();
      r.next();
      arr = r.getArray("perc");
      // conversion du tableau obtenu en primitive
      retour = ArrayUtils.toPrimitive((Integer[]) arr.getArray());
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(r != null)
        try { r.close(); } catch (SQLException e) { }
      if(stmt != null)
        try { stmt.close(); } catch (SQLException e) { } 
      if(conn != null)
        try { conn.close(); } catch (SQLException e) { } 
    }
    
    return retour;
  }
  
  public int[] centileNumChanges(double precision, boolean filterNull) {
      
    return centileNumChanges(precision, 0.00, 1.0, filterNull);
  }

  public int[] centileNumChanges(double precision, double start, double end) {
    
    return centileNumChanges(precision, start, end, true);
  }
  
  public int[] centileNumChanges(double precision) {
    
    return centileNumChanges(precision, true);
  }

  public int[] centileNumChanges() {
    
    return centileNumChanges(0.05);
  }
  
  public double[] centileTimeOpened(double precision, double start, double end, boolean filterNull) {
    if(precision <= 0.0 || precision > 1.0)
      throw new IllegalArgumentException("precision must be between 0 and 1, found: "+precision);
    if(start < 0.0 || start >= end)
      throw new IllegalArgumentException("start must be positive, not zero and less than end, found: "+start);
    if(end > 1.0)
      throw new IllegalArgumentException("end must be less than 1, found: "+end);
    
    
    String query = "SELECT percentile_disc(?) WITHIN GROUP " +
      "(ORDER BY EXTRACT(EPOCH FROM (closed_at - created_at))) AS perc " +
      "FROM osm_changeset";
     
    if(filterNull)
      query += " WHERE closed_at IS NOT NULL "
      + "AND (closed_at - created_at) >= '00:00:00'";
    
    
    double[] retour = null;
    // calcul du nombre de centiles à calculer    
    Double[] centiles = generateCentiles(precision, start, end);
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet r = null;
    try {
      conn = dbAccess.getConnection();
      stmt = conn.prepareStatement(query);
      
      Array arr =  conn.createArrayOf("numeric", (Object[]) centiles);
      stmt.setArray(1, arr);
      
      r = stmt.executeQuery();
      r.next();
      arr = r.getArray("perc");
      
      retour = ArrayUtils.toPrimitive((Double[]) arr.getArray());
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(r != null)
        try { r.close(); } catch (SQLException e) { }
      if(stmt != null)
        try { stmt.close(); } catch (SQLException e) { } 
      if(conn != null)
        try { conn.close(); } catch (SQLException e) { } 
    }
    
    return retour;
  }
  
  public static Double[] generateCentiles(double precision, double start, double end) {

    int nbCentile = (int) ((end - start)/precision+1);
    
    Double[] centiles = new Double[nbCentile];
    for(int cpt = 0; cpt < nbCentile; cpt++)
      centiles[cpt] = start + cpt*precision;
    return centiles;
  }
  
  public double[] centileTimeOpened(double precision, boolean filterNull) {
      
    return centileTimeOpened(precision, 0.00, 1.0, filterNull);
  }

  public double[] centileTimeOpened(double precision, double start, double end) {
    
    return centileTimeOpened(precision, start, end, true);
  }
  
  public double[] centileTimeOpened(double precision) {
    
    return centileTimeOpened(precision, true);
  }

  public double[] centileTimeOpened() {
    
    return centileTimeOpened(0.05);
  }
}
