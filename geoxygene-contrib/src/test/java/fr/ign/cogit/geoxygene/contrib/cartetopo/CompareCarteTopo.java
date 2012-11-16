package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.dataset.Column;
import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.datatools.hibernate.HibernateUtil;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.data.Roads;

/**
 * Carte Topo dataset. <br/>
 * <ul>
 * <li>ct1 : schema construit à la main</li>
 * <li>ct2 : schema construit à partir d'un jeu de données en base.</li>
 * </ul>
 */
public class CompareCarteTopo extends DBTestCase {

  // Logger
  private static Logger logger = Logger.getLogger(CompareCarteTopo.class);

  public static final String TABLE_TRONCON_ROUTE = "contrib.troncon_route";
  public static final String TABLE_ROADS = "contrib.roads";

  private FlatXmlDataSet loadedDataSet;

  /**
   * Initialisation des paramètres pour accéder à la base de données.
   */
  public CompareCarteTopo() {
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS,
        "org.postgresql.Driver");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
        "jdbc:postgresql://del1109s019:5432/dbunit");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
        "dbunit");
    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
        "dbunit");
  }

  /**
   * Load the data which will be inserted for the test.
   */
  protected IDataSet getDataSet() throws Exception {
    logger.info("Chargement des données en base");
    loadedDataSet = new FlatXmlDataSet(this.getClass().getClassLoader()
        .getResourceAsStream("dbunit/contrib.xml"));
    return loadedDataSet;
  }

  /**
   * On charge une carte topo en base de données.
   * @throws Exception
   */
  public void testCheckTronconDataLoaded() throws Exception {

    // On vérifie que le dataset n'est pas null.
    assertNotNull(loadedDataSet);

    // On vérifie que les 61 lignes sont bien ajoutées
    int rowCount = loadedDataSet.getTable(TABLE_TRONCON_ROUTE).getRowCount();
    assertEquals(61, rowCount);

  }

  /**
   * Comparaison du calcul du chemin le plus court 26-39 : en base et dans GeOxygene.
   * Chargement des données en base se fait via DBUnit
   * Chargement des données dans Geoxygene se fait par Hibernate.
   * @throws Exception
   */
  public void testCheckRoad() throws Exception {

    // On vérifie que le dataset n'est pas null.
    assertNotNull(loadedDataSet);

    // On vérifie que les 61 lignes sont bien ajoutées
    int rowCount = loadedDataSet.getTable(TABLE_ROADS).getRowCount();
    assertEquals(61, rowCount);
    
    // -----------------------------------------------------------------------------------------
    // Calcul dans Geoxygene
    // 
    // On commence par charger le reseau dans Geoxygene
    GeodatabaseHibernate geoDB = new GeodatabaseHibernate();
    FT_FeatureCollection<Roads> roads = geoDB.loadAllFeatures(Roads.class);
    
    String attribute = "sens";
    Map<Object, Integer> orientationMap = new HashMap<Object, Integer>(2);
    orientationMap.put("Direct", new Integer(1));
    orientationMap.put("Inverse", new Integer(-1));
    orientationMap.put("Double", new Integer(2));
    orientationMap.put("NC", new Integer(2));
    String groundAttribute = "pos_sol";
    double tolerance = 0.1;
    
    CarteTopo networkMapTest = new CarteTopo("Network Map Test");
    /*Chargeur.importAsEdges(roads, networkMapTest, attribute,
        orientationMap, groundAttribute, null, groundAttribute, tolerance);*/
    
    
    // On calcule le plus court chemin
    
    // Calcul du chemin le plus court 26-39 en base
    /*String sqlGetShortestPath = " SELECT * "
        + " FROM shortest_path('SELECT id, source, target, x1, y1, x2, y2, cost, reverse_cost FROM contrib.roads', 26, 39, true, true); ";
    // vertex_id, edge_id, cost
    
    try {
      
      Class.forName("org.postgresql.Driver");
      Connection connection = DriverManager.getConnection(
          "jdbc:postgresql://del1109s019:5432/dbunit", "dbunit", "dbunit");
      
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery(sqlGetShortestPath);
      
      // On charge le resultat dans GeOxygene
      while (rs.next()) {
        System.out.println("vertex_id = " + rs.getString("vertex_id"));
      }
      
      rs.close();
      st.close();
      connection.close();
      
    } catch (Exception e) {
      fail("Erreur de chargement " + e.toString());
    }*/
    
    // On compare les 2 résultats

  }

}
