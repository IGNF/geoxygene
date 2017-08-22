package fr.ign.cogit.geoxygene.osm.anonymization.junk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;


/**
 * Classe permettant d'automatiser des tests
 * sur la base de données des changesets 
 * fournie sur le site planet d'OpenStreetMap.
 * 
 * La structure de la base est issue d'une exportation
 * vers une base PostgreSQL incluant l'extension PostGIS
 * effectuée avec ChangesetMD.
 * 
 * Cette classe contient la connexion à la base de données
 * avec les informations de connexion, ainsi qu'une 
 * fonction executant les tests sous forme de requêtes 
 * passés en paramètre à l'aide d'une interface fonctionnelle
 * DBTestFunction qui renvoie une List de ResultSet. Cette
 * liste peut ensuite être affichée, exportée ou traitée.
 * 
 * Necessite des modifications considérable avec l'utilisation de 
 * PostgresAccess donc déprécié pour conserver les deux versions.
 * Utilisez ChangesetDBAnalysis.
 * 
 * Déprécié le 22/05/2017
 * 
 * @author Matthieu Dufait
 */
@Deprecated
public class ChangesetDBTests {
  // les valeurs suivantes doivent être changées
  // en fonction des besoins et de la configuration
  // de la base de données.
  private static final String staticHost = "localhost";
  private static final String staticPort = "5432";
  private static final String staticName = "osmchangeset";
  private static final String staticUser = "postgres";
  private static final String staticPassword = "postgres";
  
  /**
   * Instance par défaut de la classe définie grâce 
   * aux constantes définies au-dessus.
   */
  private final static ChangesetDBTests instance =
      new ChangesetDBTests(staticHost, staticPort, staticName, 
          staticUser, staticPassword); 

  /**
   * Accesseur de l'instance de ChangesetDBTests
   * @return l'instance unique
   */
  public static ChangesetDBTests getInstance() {
    return instance;
  }
  
  /**
   * Contient l'hôte de la base de données
   */
  private String dbHost;  
  /**
   * Contient le port de connexion à la base de données
   */
  private String dbPort;  
  /**
   * Contient le nom de la base postgres
   */
  private String dbName;  
  /**
   * Contient le nom d'utilisateur qui peut accéder à la base
   */
  private String dbUser;  
  /**
   * Contient le mot de passe de l'utilisateur
   */
  private String dbPwd;
  
  /**
   * Contructeur définissant les champs 
   * avec les paramètres
   */
  public ChangesetDBTests(String dbHost, String dbPort, String dbName,
      String dbUser, String dbPassword) {
    super();
    this.dbHost = dbHost;
    this.dbPort = dbPort;
    this.dbName = dbName;
    this.dbUser = dbUser;
    this.dbPwd = dbPassword;
  }
  
  /**
   * Fonction exécutant le test donné en paramètre
   * sous forme de DBTestFunction et qui retourne un 
   * ResultSet qui peut ensuite être traité.
   * @param testFunc
   */
  public void execTest(DBTestFunction testFunc) {
    try {
      String url = "jdbc:postgresql://" + this.dbHost + ":" +
          this.dbPort + "/" + this.dbName;
      Connection dbConnection = DriverManager.getConnection(url, 
            this.dbUser, this.dbPwd);
      
      testFunc.test(dbConnection);
      
      dbConnection.close(); 
    } catch (SQLException e) {
      e.printStackTrace();
    }    
  }
}

/**
 * Interface fonctionnelle servant à passer 
 * une fonction préparant différentes requêtes 
 * procéduralement et traitant les résultats
 * @author Matthieu Dufait
 */
@FunctionalInterface
interface DBTestFunction {
  public void test(Connection dbConnection) throws SQLException;
}

/**
 * Implémentation dans une classe simple
 * du test sur le temps d'ouverture des changeset
 *  
 * @author Matthieu Dufait
 */
class DBTestTimeOpened implements DBTestFunction {
  @Override
  public void test(Connection dbConnection) throws SQLException {
    String query = "SELECT ?, percentile_cont(?) WITHIN GROUP " +
      "(ORDER BY closed_at - created_at) " +
      "FROM osm_changeset WHERE closed_at IS NOT NULL "
      + "AND (closed_at - created_at) >= '00:00:00'";
  ResultSet r = null;
  PreparedStatement statement = 
      dbConnection.prepareStatement(query);
  for(double d = 0.05; d < 1; d+=0.05)
  {
    statement.setString(1, "c"+new DecimalFormat(".00").
        format(d).substring(1));
    statement.setDouble(2, d);
    System.out.println("Traitement "+ new DecimalFormat("0.00").
        format(d));
    r = statement.executeQuery();
    r.next();
    System.out.println(r.getString(1) + " : " + r.getString(2));
    }
    if(r != null) r.close();
    statement.close();
  }    
}

/**
 * Implémentation dans une classe simple
 * du test sur nombre de changement 
 * effectué durant un changeset
 *  
 * @author Matthieu Dufait
 */
class DBTestNumChanges implements DBTestFunction {
  @Override
  public void test(Connection dbConnection) throws SQLException {
    String query = "SELECT ?, percentile_cont(?) WITHIN GROUP " +
      "(ORDER BY num_changes) " +
      "FROM osm_changeset";
  ResultSet r = null;
  PreparedStatement statement = 
      dbConnection.prepareStatement(query);
  for(double d = 0.05; d < 1; d+=0.05)
  {
    statement.setString(1, "c"+new DecimalFormat(".00").
        format(d).substring(1));
    statement.setDouble(2, d);
    System.out.println("Traitement "+ new DecimalFormat("0.00").
        format(d));
    r = statement.executeQuery();
    r.next();
    System.out.println(r.getString(1) + " : " + r.getString(2));
    }
  if(r != null) r.close();
  statement.close();  
  }    
}