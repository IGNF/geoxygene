package fr.ign.cogit.geoxygene.osm.anonymization.db.access;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

import fr.ign.cogit.geoxygene.jdbc.postgis.ConnectionParam;

/**
 * Classe permettant de gérer la connexion à 
 * une base de données PostgreSQL. 
 * Cette classe utilise des connections 
 * en pool, pour accéder à la base de 
 * l'extérieur il suffit d'appeler 
 * getConnection()
 * 
 * Important : les classes utilisant cette 
 * classe doivent gérer la fermeture de 
 * la connexion après utilisation.
 *  
 * @author Matthieu Dufait
 */
public class PostgresAccess {
  /**
   * Défini les paramètres de connexion à la base 
   * de donnée.
   */
  private BasicDataSource dataSource;
  
  /**
   * Constructeur initialisant la source de données
   * à partir d'un tableau associatif
   * @param params
   */
  public PostgresAccess(Map<String, String> params) {
    this(params.get("host"), params.get("port"), 
        params.get("database"), params.get("user"), 
        params.get("passwd"));
  }
  
  /**
   * Constructeur à partir un objet ConnectionParam
   * @param params
   */
  public PostgresAccess(ConnectionParam params) {
    this(params.getHost(), params.getPort(), 
        params.getDatabase(), params.getUser(),
        params.getPasswd());
  }
  
  /**
   * Constructeur à partir des paramètres de 
   * connexion directement.
   * @param host
   * @param port
   * @param database
   * @param user
   * @param passwd
   */
  public PostgresAccess(String host, String port, String database, 
      String user, String passwd) {
    super();
    this.dataSource = new BasicDataSource();
    // l'accès est vers une base postgres
    this.dataSource.setDriverClassName("org.postgresql.Driver");
    
    this.dataSource.setUrl("jdbc:postgresql://" + host + ":" +
        port + "/" + database);

    this.dataSource.setUsername(user);
    this.dataSource.setPassword(passwd);
  }
  
  /**
   * Retourne une connexion à la base de données
   * @return
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    return this.dataSource.getConnection();
  }
}
