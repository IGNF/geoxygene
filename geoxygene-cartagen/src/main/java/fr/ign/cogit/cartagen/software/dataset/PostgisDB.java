/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.software.dataset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

/**
 * @author JGaffuri
 */
public class PostgisDB {
  /**
     */
  private static PostgisDB postGISBD = null;
  static Logger logger = Logger.getLogger(PostgisDB.class.getName());

  public static PostgisDB get() {
    if (PostgisDB.postGISBD == null) {
      PostgisDB.postGISBD = new PostgisDB();
    }
    return PostgisDB.postGISBD;
  }

  public static PostgisDB get(String name, boolean create) {
    if (PostgisDB.postGISBD == null) {
      PostgisDB.postGISBD = new PostgisDB(name, create);
    } else if (!PostgisDB.dbName.equals(name)) {
      PostgisDB.postGISBD = new PostgisDB(name, create);
    }
    return PostgisDB.postGISBD;
  }

  private static String serverName = "localhost";

  /**
   * @return
   */
  public static String getServerName() {
    return PostgisDB.serverName;
  }

  private static String portNumber = "5432";

  /**
   * @return
   */
  public static String getPortNumber() {
    return PostgisDB.portNumber;
  }

  private static String dbName = "38";

  /**
   * @return
   */
  public static String getDbName() {
    return PostgisDB.dbName;
  }

  private static String userName = "postgres";

  /**
   * @return
   */
  public static String getUserName() {
    return PostgisDB.userName;
  }

  private static String motDePasse = "cartagen";

  // le SRID de la BD
  private static int srid = -1; // 27582;

  public static int getSRID() {
    return PostgisDB.srid;
  }

  // le lecteur de geometrie
  private static WKBReader lecteurWKB;

  /**
   * @return
   */
  public static WKBReader getLecteurWKB() {
    if (PostgisDB.lecteurWKB == null) {
      PostgisDB.lecteurWKB = new WKBReader();
    }
    return PostgisDB.lecteurWKB;
  }

  // l'ecrivain de geometrie
  private static WKBWriter ecrivainWKB;

  /**
   * @return
   */
  public static WKBWriter getEcrivainWKB() {
    if (PostgisDB.postGISBD == null) {
      PostgisDB.ecrivainWKB = new WKBWriter();
    }
    return PostgisDB.ecrivainWKB;
  }

  // la connexion a postgresql
  private static Connection connection = null;

  /**
   * @return
   */
  public static Connection getConnection() {
    return PostgisDB.connection;
  }

  private static String defaultConfigPath = "/hibernate_config/hibernate_cartagen.cfg.xml";

  public static String getDefaultConfigPath() {
    return defaultConfigPath;
  }

  /**
   * Builds the URL string of the DB from the serverName, the portNumber and the
   * dbName.
   * @return a String URL like "jdbc:postgresql://localhost:5432/dbXX"
   */
  public static String getUrl() {
    return "jdbc:postgresql://" + PostgisDB.serverName + ":"
        + PostgisDB.portNumber + "/" + PostgisDB.dbName;
  }

  // constructeur
  private PostgisDB() {
    if (PostgisDB.logger.isDebugEnabled()) {
      PostgisDB.logger.debug("Connection a postGIS");
    }

    // charge le pilote
    if (PostgisDB.logger.isInfoEnabled()) {
      PostgisDB.logger.info("chargement du pilote JDBC");
    }
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      PostgisDB.logger
          .error("Erreur lors du chargement du pilote de connexion a postGIS");
      e.printStackTrace();
    }

    if (PostgisDB.logger.isInfoEnabled()) {
      PostgisDB.logger.info("Tentative de connection: (" + PostgisDB.serverName
          + ", " + PostgisDB.portNumber + ", " + PostgisDB.dbName + ", "
          + PostgisDB.userName + ", " + PostgisDB.motDePasse + ")");
    }
    try {
      PostgisDB.connection = DriverManager.getConnection("jdbc:postgresql://"
          + PostgisDB.serverName + ":" + PostgisDB.portNumber + "/"
          + PostgisDB.dbName + "", "" + PostgisDB.userName + "", ""
          + PostgisDB.motDePasse + "");
      // DatabaseMetaData md=connexion.getMetaData();
      // System.out.println(md.getDatabaseProductName()+" "+md.getDatabaseProductVersion());
    } catch (SQLException e) {
      PostgisDB.logger.warn("Connexion postGIS impossible: " + e.getMessage());
      if (PostgisDB.logger.isDebugEnabled()) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Constructor with the name of the database to connect to.
   * @param name the PostGIS name of the database to connect to.
   * @param create if true, a new db is created is no exists with the name
   *          'name'
   */
  private PostgisDB(String name, boolean create) {
    PostgisDB.dbName = name.toLowerCase();
    if (PostgisDB.logger.isDebugEnabled()) {
      PostgisDB.logger.debug("Connection to postGIS");
    }

    // charge le pilote
    if (PostgisDB.logger.isInfoEnabled()) {
      PostgisDB.logger.info("chargement du pilote JDBC");
    }
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      PostgisDB.logger
          .error("Erreur lors du chargement du pilote de connexion a postGIS");
      e.printStackTrace();
    }

    if (PostgisDB.logger.isInfoEnabled()) {
      PostgisDB.logger.info("Tentative de connection: (" + PostgisDB.serverName
          + ", " + PostgisDB.portNumber + ", " + PostgisDB.dbName + ", "
          + PostgisDB.userName + ", " + PostgisDB.motDePasse + ")");
    }
    // try to connect to the database
    try {
      PostgisDB.connection = DriverManager.getConnection("jdbc:postgresql://"
          + PostgisDB.serverName + ":" + PostgisDB.portNumber + "/"
          + PostgisDB.dbName + "", "" + PostgisDB.userName + "", ""
          + PostgisDB.motDePasse + "");
      // DatabaseMetaData md=connexion.getMetaData();
      // System.out.println(md.getDatabaseProductName()+" "+md.getDatabaseProductVersion());
    } catch (SQLException e) {
      if (create && e.getMessage().contains("n'existe pas")) {
        PostgisDB.logger
            .warn("Connexion postGIS impossible: " + e.getMessage());
        try {
          // connect to the default DB of postgres to create a new one
          PostgisDB.connection = DriverManager.getConnection(
              "jdbc:postgresql://" + PostgisDB.serverName + ":"
                  + PostgisDB.portNumber + "/" + "postgres", ""
                  + PostgisDB.userName + "", "" + PostgisDB.motDePasse + "");
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        createPostGisDb();
      } else {
        PostgisDB.logger
            .warn("Connexion postGIS impossible: " + e.getMessage());
        if (PostgisDB.logger.isDebugEnabled()) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Execute une commande SQL. Cette commande ne doit pas renvoyer de resultat :
   * INSERT, UPDATE, DELETE, mais pas SELECT.
   */
  public static void exeSQL(String query) {
    try {
      if (logger.isDebugEnabled()) {
        logger.debug("exécution de la requête SQL : " + query);
      }
      Connection conn = getConnection();
      Statement stm = conn.createStatement();
      stm.executeUpdate(query);
      stm.close();
      conn.commit();
    } catch (Exception e) {
      logger.error("Erreur pendant l'exécution de la requête SQL : " + query);
      logger.error(e.getMessage());
    }
  }

  /**
   * Create a new database into PostGIS with the dbName, using SQL query.
   */
  public static void createPostGisDb() {
    String sql = "CREATE DATABASE " + dbName + " TEMPLATE=template_postgis";
    exeSQL(sql);
  }

  /**
   * effectue une requete qui ne renvoit aucun resultat
   * 
   * @param requete
   * @return
   */
  public static boolean query(String requete) {
    boolean res = false;
    try {
      res = PostgisDB.connection.createStatement().execute(requete);
    } catch (SQLException e) {
      System.out
          .println("Erreur lors de l'execution de la requete: " + requete);
      System.out.println(e.getMessage());
    }
    return res;
  }

  /**
   * effectue une requete qui renvoit un resultat
   * 
   * @param requete
   * @return
   */
  public static ResultSet requeteResultat(String requete) {
    ResultSet res = null;
    try {
      res = PostgisDB.connection.createStatement().executeQuery(requete);
    } catch (SQLException e) {
      System.out
          .println("Erreur lors de l'execution de la requete: " + requete);
      e.printStackTrace();
    }
    return res;
  }

  // selectionne toutes les geometrie d'une classe contenues dans un rectangle
  // donne
  public static ResultSet requete(String nomClasse,
      String nomAttributGeometrie, double xMin, double yMin, double xMax,
      double yMax) {
    ResultSet res = null;
    String requete = "SELECT ST_AsBinary(" + nomAttributGeometrie + ") FROM "
        + nomClasse + " WHERE " + nomAttributGeometrie + " && " + "'BOX3D("
        + xMin + " " + yMin + "," + xMax + " " + yMax + ")'::box3d";
    try {
      res = PostgisDB.connection.createStatement().executeQuery(requete);
    } catch (SQLException e) {
      System.out
          .println("Erreur lors de l'execution de la requete: " + requete);
      e.printStackTrace();
    }
    return res;
  }

  // selectionne toutes les geometrie d'une classe contenues dans un rectangle
  // donne, ainsi qu'un attribut donne
  public static ResultSet requete(String nomClasse,
      String nomAttributGeometrie, String nomAttribut, double xMin,
      double yMin, double xMax, double yMax) {
    ResultSet res = null;
    String requete = "SELECT ST_AsBinary(" + nomAttributGeometrie + "), "
        + nomAttribut + " FROM " + nomClasse + " WHERE " + nomAttributGeometrie
        + " && " + "'BOX3D(" + xMin + " " + yMin + "," + xMax + " " + yMax
        + ")'::box3d";
    try {
      res = PostgisDB.connection.createStatement().executeQuery(requete);
    } catch (SQLException e) {
      System.out
          .println("Erreur lors de l'execution de la requete: " + requete);
      e.printStackTrace();
    }
    return res;
  }

  // selectionne toutes les valeurs d'un attribut des objets d'une classe
  // contenus dans un rectangle donne
  public static ResultSet requeteAtt(String nomClasse,
      String nomAttributGeometrie, String nomAttribut, double xMin,
      double yMin, double xMax, double yMax) {
    ResultSet res = null;
    String requete = "SELECT " + nomAttribut + " FROM " + nomClasse + " WHERE "
        + nomAttributGeometrie + " && " + "'BOX3D(" + xMin + " " + yMin + ","
        + xMax + " " + yMax + ")'::box3d";
    try {
      res = PostgisDB.connection.createStatement().executeQuery(requete);
    } catch (SQLException e) {
      System.out
          .println("Erreur lors de l'execution de la requete: " + requete);
      e.printStackTrace();
    }
    return res;
  }

  public static void selectionnerTous(String nomClasse, String nomAttribut,
      Vector<String> valeurs) {
    ResultSet res = PostgisDB.requeteResultat("SELECT " + nomAttribut
        + " FROM " + nomClasse);
    try {
      int nb = res.getFetchSize();
      res.first();
      for (int i = 0; i < nb; i++) {
        String value = res.getString(1);
        valeurs.addElement(value);
        res.next();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // cree un index spatial sur une classe
  public static void creerIndexSpatial(String nomClasse,
      String nomAttributGeometrie) {
    PostgisDB.query("CREATE INDEX ind_" + nomClasse + "_"
        + nomAttributGeometrie + " ON " + nomClasse + " USING GIST ( "
        + nomAttributGeometrie + " );");
  }

  public static void creerIndexSpatial(String nomClasse) {
    PostgisDB.creerIndexSpatial(nomClasse, "the_geom");
  }

  public static void creerIndex(String nomClasse, String nomAttribut) {
    PostgisDB.query("CREATE INDEX ind_" + nomClasse + "_" + nomAttribut
        + " ON " + nomClasse + " ( " + nomAttribut + " );");
  }

}
