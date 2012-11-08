package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;

/**
 * Carte Topo dataset. <br/>
 * <ul>
 * <li>ct1 : schema construit à la main</li>
 * <li>ct2 : schema construit à partir d'un jeu de données en base.</li>
 * </ul>
 */
public class CarteTopoDataSet extends DBTestCase {

  // Logger
  private static Logger logger = Logger.getLogger(CarteTopoDataSet.class);

  public static final String TABLE_TRONCON_ROUTE = "contrib.troncon_route";
  private FlatXmlDataSet loadedDataSet;

  /**
   * Initialisation des paramètres pour accéder à la base de données.
   */
  public CarteTopoDataSet() {
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
        .getResourceAsStream("dbunit/troncon_route.xml"));
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
  
  
}
