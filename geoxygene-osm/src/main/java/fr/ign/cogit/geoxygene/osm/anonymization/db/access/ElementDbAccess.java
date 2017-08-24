package fr.ign.cogit.geoxygene.osm.anonymization.db.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Cette classe permet de gérer la lecture
 * des tables d'une base de données ayant 
 * la structure des bases de données 
 * PostGIS pour stocker les informations 
 * d'OSM comme la base nepal ou comores.
 * Il faut noter que les noms des 
 * tables et colonnes sont inscrits dans 
 * les requêtes.
 * 
 * @author Matthieu Dufait
 */
public class ElementDbAccess {  
  private static final String staticHost = "localhost";
  private static final String staticPort = "5432";
  private static final String staticName = "nepal1";
  //private static final String staticName = "comores1";
  private static final String staticUser = "postgres";
  private static final String staticPassword = "postgres";
  
  private static final String readNodesQuery =  
      "SELECT id, vnode, uid, changeset, "
      + "datemodif, lat, lon, hstore_to_array(tags) FROM node "
      + "ORDER BY id, vnode ASC LIMIT ? OFFSET ?";
  
  private static final String readWaysQuery =  
      "SELECT id, vway, uid, changeset, "
      + "datemodif, composedof, hstore_to_array(tags) FROM way "
      + "ORDER BY id, vway ASC LIMIT ? OFFSET ?";

  private static final String readRelationsQuery =  
      "SELECT id, vrel, uid, changeset, "
      + "datemodif, hstore_to_array(tags) FROM relation "
      + "ORDER BY id, vrel ASC LIMIT ? OFFSET ?";
  
  private static final String readNodesQueryNoTags =  
      "SELECT id, vnode, uid, changeset, datemodif, lat, lon, "
      + "string_to_array('','') hstore_to_array FROM node "
      + "ORDER BY id, vnode ASC LIMIT ? OFFSET ?";
  
  private static final String readWaysQueryNoTags =  
      "SELECT id, vway, uid, changeset, datemodif, composedof, "
      + "string_to_array('','') hstore_to_array FROM way "
      + "ORDER BY id, vway ASC LIMIT ? OFFSET ?";

  private static final String readRelationsQueryNoTags =  
      "SELECT id, vrel, uid, changeset, datemodif, "
      + "string_to_array('','') hstore_to_array FROM relation "
      + "ORDER BY id, vrel ASC LIMIT ? OFFSET ?";
  
  private static final String readMembersQuery =  
      "SELECT idrel, idmb, typemb, rolemb "
      + "FROM relationmember "
      + "ORDER BY idrel, idmb ASC LIMIT ? OFFSET ?";
  
  private PostgresAccess dbAccess;
  private Connection conn;
  private PreparedStatement pstmt;
  private ResultSet rslt;
  private boolean closed = true;
  private String lastQuery;
  private boolean readTags;
  
  /**
   * Instance par défaut défini à partir des 
   * constantes inscrites dans la classe.
   */
  public static ElementDbAccess instance = new ElementDbAccess();
  
  private ElementDbAccess() {
    this(staticHost, staticPort, staticName, 
        staticUser, staticPassword); 
  }
  
  public ElementDbAccess(String host, String port, 
      String name, String user, String pass) {
    this.dbAccess = new PostgresAccess(host, port, 
        name, user, pass); 
    readTags = true;
  }
  
  public boolean isReadTags() {
    return readTags;
  }

  public void setReadTags(boolean readTags) {
    this.readTags = readTags;
  }

  private ResultSet readElements(int limit, int offset, boolean autoClose,
      String query) throws SQLException {
    if(autoClose)
      this.close();
    
    if(closed || !query.equals(lastQuery)) {      
      conn = dbAccess.getConnection();
      pstmt = conn.prepareStatement(query);
      
      closed = false;
    }
    if(limit < 0)
      pstmt.setNull(1, Types.INTEGER);
    else
      pstmt.setInt(1, limit);
    pstmt.setInt(2, offset);
    rslt = pstmt.executeQuery();
    
    return rslt;
  }
  
  /**
   * La fonction readNodes permet de lire l'ensemble des nodes de la base 
   * des éléments et retourne un ResultSet.
   * @param limit
   * @param offset
   * @param autoClose
   * @return
   * @throws SQLException
   */
  public ResultSet readNodes(int limit, int offset, boolean autoClose) throws SQLException {
    if(readTags)
      return readElements(limit, offset, autoClose, readNodesQuery);
    else
      return readElements(limit, offset, autoClose, readNodesQueryNoTags);
  }
  
  public ResultSet readNodes(int limit) throws SQLException {
    return this.readNodes(limit, 0);
  }
  
  public ResultSet readNodes(int limit, int offset) throws SQLException {
    return this.readNodes(limit, offset, true);
  }
  
  public ResultSet readNodes() throws SQLException {
    return this.readNodes(true);
  }
  
  public ResultSet readNodes(int limit, boolean autoClose) throws SQLException {
    return this.readNodes(limit, 0, autoClose);
  }
  
  public ResultSet readNodes(boolean autoClose) throws SQLException {
    return this.readNodes(-1, autoClose);
  }
  
  public ResultSet readWays(int limit, int offset, boolean autoClose) throws SQLException {
    if(readTags)
      return readElements(limit, offset, autoClose, readWaysQuery);
    else
      return readElements(limit, offset, autoClose, readWaysQueryNoTags);
  }
  
  public ResultSet readWays(int limit) throws SQLException {
    return this.readWays(limit, 0);
  }
  
  public ResultSet readWays(int limit, int offset) throws SQLException {
    return this.readWays(limit, offset, true);
  }
  
  public ResultSet readWays() throws SQLException {
    return this.readWays(true);
  }
  
  public ResultSet readWays(int limit, boolean autoClose) throws SQLException {
    return this.readWays(limit, 0, autoClose);
  }
  
  public ResultSet readWays(boolean autoClose) throws SQLException {
    return this.readWays(-1, autoClose);
  }

  public ResultSet readRelations(int limit, int offset, boolean autoClose) throws SQLException {
    if(readTags)
      return readElements(limit, offset, autoClose, readRelationsQuery);
    else
      return readElements(limit, offset, autoClose, readRelationsQueryNoTags);
  }
  
  public ResultSet readRelations(int limit) throws SQLException {
    return this.readRelations(limit, 0);
  }
  
  public ResultSet readRelations(int limit, int offset) throws SQLException {
    return this.readRelations(limit, offset, true);
  }
  
  public ResultSet readRelations() throws SQLException {
    return this.readRelations(true);
  }
  
  public ResultSet readRelations(int limit, boolean autoClose) throws SQLException {
    return this.readRelations(limit, 0, autoClose);
  }
  
  public ResultSet readRelations(boolean autoClose) throws SQLException {
    return this.readRelations(-1, autoClose);
  }
  
  public ResultSet readMembers(int limit, int offset, boolean autoClose) throws SQLException {
    return readElements(limit, offset, autoClose, readMembersQuery);
  }
  
  public ResultSet readMembers(int limit) throws SQLException {
    return this.readMembers(limit, 0);
  }
  
  public ResultSet readMembers(int limit, int offset) throws SQLException {
    return this.readMembers(limit, offset, true);
  }
  
  public ResultSet readMembers() throws SQLException {
    return this.readMembers(true);
  }
  
  public ResultSet readMembers(int limit, boolean autoClose) throws SQLException {
    return this.readMembers(limit, 0, autoClose);
  }
  
  public ResultSet readMembers(boolean autoClose) throws SQLException {
    return this.readMembers(-1, autoClose);
  }
  
  
  /**
   * Fonction permettant de fermer les 
   * resources possiblement ouvertes
   */
  public void close() {
    if(rslt != null)
      try { rslt.close(); } catch (SQLException e) { }
    if(pstmt != null)
      try { pstmt.close(); } catch (SQLException e) { } 
    if(conn != null)
      try { conn.close(); } catch (SQLException e) { }
    closed = true;
  }
}
