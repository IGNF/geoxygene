package fr.ign.cogit.geoxygene.osm.anonymization.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.AnonymizedDate;
import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.InternetAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.PostgresAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.PreAnonymizationQueries;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.PreAnonymizationQueriesType;

/**
 * Cette classe permet de convertir une base de données 
 * OpenStreetMap avec une orientation éléments vers une 
 * base de données orientée changeset et utilisateur avec
 * les premières étapes de l'anonymisation et l'ajout
 * des informations à utiliser pour l'anonymisation.
 * 
 * Cette classe contient plusieurs méthodes permettant 
 * d'effectuer les différentes étapes de la conversion :
 * 
 * - création des tables changeset et user
 * - ajout des données des tables d'éléments dans les tables changeset et user
 * 
 * - suppression des colonnes superflues
 * - anonymisation des dates dans les changesets
 * 
 * - récupération des données sur les changeset via l'api
 * - récupération des données sur la visibilité des éléments via l'api
 * - ajout du numéro de version de la relation dans la référence des membres
 * 
 * - remplacement des identifiants numériques d'origine par des nouveaux
 * 
 * - possible extraction des tags et des nodes des ways dans leurs propres tables
 * - possible renommage des colonnes et des tables.
 * 
 * @author Matthieu Dufait
 */
public class SQLDBPreAnonymization {
  private Logger logger = Logger.getLogger(SQLDBPreAnonymization.class.getName());
  
  /**
   * Chaine de caractère contenant la base de l'URL utilisée pour charger
   * des données sur les changesets.
   */
  private static final String OSM_API_URL = "http://www.openstreetmap.org/api/0.6/";
  private static final String OSM_HISTORY_SUFFIX_URL = "/history";

  private static final String OSM_CHANGESET_API_URL = OSM_API_URL + "changeset/";
  
  private static Proxy proxyIGN = InternetAccess.getIGNProxy();

  /**
   * La classe PostgresAccess gère la connexion à la base de donnée
   */
  private PostgresAccess dbAccess;
  /**
   * Classe contenant les données sur les requêtes qui 
   * sont utilisées dans les fonctions de cette classe.
   * La gestion de cet objet est à la totale discrétion 
   * de son utilisateur et peut provoquer de graves problèmes
   * si elle est mal utilisée. En particulier risque de pertes 
   * ou de fuites de données de la base de données.
   */
  private PreAnonymizationQueries queries;
  /**
   * Booléen indiquant aux fonctions si elles doivent afficher 
   * leur progression.
   */
  private boolean displayOn;
  
  /**
   * Attributs permettant d'enregistrer les identifiants 
   * sur lesquels les requêtes n'ont pas données de résultat
   */
  private HashMap<OSMPrimitiveType, ArrayList<String>> listElementIdFailed;
  private ArrayList<String> listChangesetIdFailed;
  
  /**
   * Constructeur initialisant une instance à partir des objets fournits 
   * en paramètre et displayProgression à false
   * @param dbAccess
   * @param queries
   */
  public SQLDBPreAnonymization(PostgresAccess dbAccess, PreAnonymizationQueries queries) {
    this(dbAccess, queries, false);
  }
  
  /**
   * Constructeur initialisant une instance à partir des paramètres
   * @param dbAccess
   * @param queries
   * @param displayProgression
   */
  public SQLDBPreAnonymization(PostgresAccess dbAccess, PreAnonymizationQueries queries, boolean displayProgression) {
    this.dbAccess = dbAccess;
    this.queries = queries;
    this.displayOn = displayProgression;
    
    listElementIdFailed = new HashMap<>();
    for(OSMPrimitiveType pt : OSMPrimitiveType.values())
      listElementIdFailed.put(pt, new ArrayList<>());
    listChangesetIdFailed = new ArrayList<>();
  }
  
  /**
   * Retourne displayProgression
   *    true indique que les messages doivent être affichés ou mis en log
   *    false indique que non
   * @return displayProgression
   */
  public boolean isDisplayOn() {
    return displayOn;
  }

  /**
   * Modifie displayProgression en fonction du paramètre
   *    true indique que les messages doivent être affichés ou mis en log
   *    false indique que non
   * @param displayProgression
   */
  public void setDisplayOn(boolean displayProgression) {
    this.displayOn = displayProgression;
  }

  /**
   * <b>Dépendance :</b> Aucune
   * 
   * Cette fonction appelle successivement six requêtes pour 
   * transformer la base d'une base OpenStreetMap avec un modèle
   * orienté vers les éléments OSM (node, way et relation) vers 
   * une base orienté vers les contributeurs et changeset OSM.
   * 
   * Pour cela un requête créer la table changeset avec les colonnes
   * changeset (qui indique l'identifiant OSM du changeset),
   * uid (qui indique l'identifiant du contibuteur) et username. 
   * Ces colonnes sont remplies directement par la lecture des 
   * données issues des tables des éléments.
   * Une requête effectue la même chose pour la table contributor
   * qui ne contient que la colonne uid. 
   * 
   * Quatres requêtes sont ensuite appelées pour supprimer les 
   * données sur le contributeur les tables éléments (uid et 
   * username).
   * 
   * <b>Précaution :</b>
   *    1/ Cette fonction doit être appelée avant les autres car 
   *    c'est elle qui défini le modèles sur lequel les autres 
   *    fonctions se basent pour effectuer leur propres requêtes.
   *    
   *    2/ Cette fonction modifie la base de données en ajoutant 
   *    deux tables, en supprimant des colonnes dans les tables 
   *    éléments et en déplaçant le contenu de ces colonnes dans 
   *    les nouvelles tables.
   * @throws SQLException
   */
  public void alterDatabaseToChangeset()  {
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      stmt.execute(queries.get(PreAnonymizationQueriesType.CREATE_CHANGESET_TABLE));
      stmt.execute(queries.get(PreAnonymizationQueriesType.CREATE_USER_TABLE));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_COLUMNS_NODE));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_COLUMNS_WAY));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_COLUMNS_RELATION));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_COLUMNS_MEMBER));
    } catch (SQLException e) {
      e.printStackTrace();
      // on ferme les ressource dans tous les cas
    } finally {
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
  }

  /**
   * <b>Dépendance :</b> alterDatabaseToChangeset
   * Cette fonction a besoin que la table des changeset soit définie par
   * la fonction alterDatabaseToChangeset. 
   * 
   * Cette fonction permet d'effectuer deux modifcations distinctes sur 
   * la table des changesets de la base de données issue d'OpenStreetMap :
   * 
   *  - ajout des colonnes suivantes dans la table changeset
   *        created_at timestamp, 
   *        time_opened interval, 
   *        open boolean, 
   *        comments_count integer, 
   *        tags hstore
   *        
   *  - ajout des données des quatres premières colonnes ci-dessus 
   *    avec la lecture des documents XML par changeset issus de 
   *    l'API public d'OSM. 
   *    
   *  Pour cela cette fonction considère que la table changeset existe 
   *  et qu'elle possède au moins la colonne changeset qui indique 
   *  l'identifiant réel du changeset d'OSM pour effectuer le lien 
   *  entre la base courante et l'API OSM. 
   *  
   *  L'ajout des colonnes à la table ne vérifie volontairement pas 
   *  que les colonnes n'existent pas déjà dans la table. Un appel 
   *  à cette fonction alors que les colonnes existent lèvera donc 
   *  une exception. Ce comportement est fait de façon à ne pas réécrire
   *  sur des données existantes (si on supprimait les colonnes dans la fonction)
   *  ou ne pas écrire sur des colonnes dont le type n'est pas attendu.
   *  
   *  Pour des raisons de simplicité d'utilisation cette fonction peut ne pas 
   *  effectuer l'étape de création des colonnes si le booléen createColumns 
   *  passé en paramètre est false. La focntion ne fera alors que la mise à jour 
   *  des données avec la lecture de l'API.
   *  
   *  Enfin cette fonction possède un affichage de la progression qui peut être
   *  activée ou desactivée en fonction de la valeur de l'attibut de classe 
   *  booléen displayProgression. Cela est utile car les traitements effectués
   *  par cette fonction peuvent être longs.
   *  
   * <b>Précaution :</b>
   *    1/ Vérifiez les dépendances de la fonctione, en particulier, 
   *    le modèle de données doit contenir la table changeset non 
   *    modifiée après création dans alterDatabaseToChangeset.
   *    
   *    2/ Cette fonction modifie la base de données en ajoutant 
   *    des colonnes à la table changeset et en la remplissant de 
   *    données externes disponible via l'API OSM.
   *    
   *    3/ Cette fonction peut renvoyer une exception si (entre
   *    autres) les colonnes que la requête crée existent déjà.
   *  
   *  @param 
   *    createColumns indique si oui ou non la fonction effectue 
   *    l'étape de création des colonnes lors du traitement.   
   * 
   * @throws SQLException 
   *    renvoie une exception si les fonctions liées à SQL ont des problèmes
   */
  public void updateChangesetAttributesData(boolean createColumns) {

    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet listChangesetId = null;
    
    try {
      // récupération d'une connexion à la base de données
      conn = dbAccess.getConnection();
      
      // création d'une Statement, on fait en sorte de pourvoir retourner en arrière 
      // dans le parcours du ResultSet pour calculer le nombre d'éléments à traiter
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
      // en fonction du paramètre d'entrée on execute la requête permettant 
      // de rajouter les colonnes nécessaires sur la table changeset
      if(createColumns)
        stmt.execute(queries.get(PreAnonymizationQueriesType.ADD_COLUMNS_CHANGESET));
      
      // on prépare la requête permettant de mettre à jour une entrée sur la table des changeset
      pstmt = 
          conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_DATA_CHANGESET));
      
      // on exécute la requête permettant de sélectionner l'ensemble des identifiants
      // des changeset de la base de données afin de pouvoir itérer dessus
      listChangesetId = stmt.executeQuery(queries.get(PreAnonymizationQueriesType.SELECT_CHANGESET_ID));
      
      // création des variables servant à afficher la progression
      int total = 0, current = 0;
      // initialisation du total de lignes à traiter en accédant au numéro
      // de la dernière ligne
      if (listChangesetId.last()) {
        total = listChangesetId.getRow();
        // remise au début
        listChangesetId.beforeFirst();
      }
      
      // affiche le nombre de lignes à traiter en fonction si displayProgression est vrai
      if(displayOn)
        logger.log(Level.INFO, "Nombre de lignes à traiter : "+total);
      
      // on parcours l'ensemble des changeset
      conn.setAutoCommit(false);
      while(listChangesetId.next()) {
        // affichage de la progression du traitement
        if(displayOn && total > 30 && ++current % (total/10) == 0) 
          logger.log(Level.INFO, "Traitement "+current+" sur "+total);
        
        // mise à jour des données
        updateChangesetData(pstmt, listChangesetId.getString(1), current);
        
        if(current % 1000 != 0)
          pstmt.executeBatch();
      }
      conn.setAutoCommit(true);
      
    } catch (SQLException e) {
      e.printStackTrace();
      // on ferme les resources dans tous les cas
    } finally {
      if(listChangesetId != null) 
        try{ listChangesetId.close(); } catch (Exception e) { }
      if(pstmt != null) 
        try{ pstmt.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    
  }

  /**
   * Met à jour les données sur un changeset dans la base
   * à en récupérant les données de l'API OSM.
   * @param pstmt
   * @param current 
   * @param listChangesetId
   * @throws SQLException
   */
  private void updateChangesetData(PreparedStatement pstmt,
      String changesetId, int current) throws SQLException {
    // on récupère les données sur le changeset via une récupération issue de 
    // l'API public OpenStreetMap
    // on récupère ici uniquement les attributs du changeset sous forme de NamedNodeMap
    ChangesetAttributesAndTags caat = getChangesetDataFromAPI(changesetId);
    
    NamedNodeMap nnm = caat.getAttributes();
    
    // affectation des valeurs issues de la base dans la requête
    pstmt.setString(1, nnm.getNamedItem("created_at").getNodeValue());
    pstmt.setString(2, nnm.getNamedItem("closed_at").getNodeValue());
    pstmt.setString(3, nnm.getNamedItem("created_at").getNodeValue());
    pstmt.setBoolean(4, Boolean.parseBoolean(nnm.getNamedItem("open").getNodeValue()));
    pstmt.setInt(5, Integer.parseInt(nnm.getNamedItem("comments_count").getNodeValue()));
    pstmt.setString(6, caat.getTags());
    pstmt.setInt(7, Integer.parseInt(nnm.getNamedItem("id").getNodeValue())); 
 
    // execution de la requête de mise à jour
    pstmt.addBatch();
    
    if(current % 1000 == 0) 
      pstmt.executeBatch();
  }
  
  /**
   * Cette fonction charge les données XML d'un changeset
   * avec la lecture de l'API OSM et l'analyse syntaxique 
   * du document XML fourni. La fonction renvoi une
   * NamedNodeMap représentant les attributs du changeset.
   * Les données sont chargés avec l'identiant du changeset
   * donné en paramètre.
   * 
   * @param idChangeset identifiant du changeset à charger
   * @return tableau associatif des attributs du changeset
   */
  public ChangesetAttributesAndTags getChangesetDataFromAPI(String idChangeset) {
    // génération de l'url de reception des données
    String urlString = OSM_CHANGESET_API_URL + idChangeset;
    Document doc = getDataFromAPI(urlString);
    
    if(doc == null) {
      listChangesetIdFailed.add(idChangeset);
      return null;
    }
    
    // récupération des attributs
    ChangesetAttributesAndTags changesetAttributes = getChangesetAttributesFromXML(doc);
    return changesetAttributes;
  }
  
  /**
   * Accès la la fonction getDataFromAPI(String, boolean)
   * 
   * @see SQLDBPreAnonymization#getDataFromAPI(String, boolean)
   */
  public static Document getDataFromAPI(String urlString) {
    return getDataFromAPI(urlString, 0, false);
  }

  /**
   * Fonction permettant de se connecter à une adresse donnée
   * en paramètre qui doit être un document XML. 
   * 
   * Par défaut cette fonction se connecte en HTTP en 
   * utilisant le proxy IGN.
   * 
   * Retourne un nouvel objet document créé par lecture de la
   * source
   * 
   * Permet de gérer en cas d'exception issue d'un problème pour
   * récupérer les données à la source, la fonction se
   * relance à nouveau avec retry à true permettant de
   * reessayer de récupérer les données après avoir temporisé
   * pendant 50ms. Si on recontre à nouveau une exception, 
   * on abandonne en affichant l'exception et revoyant null.
   * 
   * La fonction devrait par défaut être appelé avec retry 
   * à false pour éviter la temporisation
   * 
   * @param urlString url à lire
   * @param retry booléen indiquant si la fonction vient d'être 
   * tenter à nouveau.
   * @return un nouveau Document
   */
  private static Document getDataFromAPI(String urlString, int retryCount, boolean connectException) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    // si on rencontre une connectException on fait une longue pause
    if(connectException) {
      try {
        Thread.sleep(300*1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    // si on rencontre une autre erreur http on reessaye 5 fois
    else if(retryCount > 0) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      
      // connexion à l'API avec les informations sur le proxy
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection(proxyIGN);  
      
      // lecture des données et analyse du document XML
      InputStream s = connection.getInputStream();  
      return db.parse(s);
      
    } catch (SAXException | IOException | ParserConfigurationException e) {
      if(e instanceof IOException) {
        // si on rencrontre une ConnectException on prévoie une pause
        if(e instanceof ConnectException && retryCount <= 10)
          return getDataFromAPI(urlString, retryCount+1, true);
        else if(retryCount <= 5)
          return getDataFromAPI(urlString, retryCount+1, false);
        else 
          System.out.println(e);
      }
      else
        e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Cette fonction prend un Document XML qui est généré
   * par la lecture de l'API OpenStreetMap et qui contient
   * les informations sur un changeset et en retire un 
   * tableau associatif des différents attributs du changeset.
   * 
   * Le tableau est retourné sous forme de {@link NamedNodeMap }
   * qui est la structure utilisée par l'API org.w3c.dom.
   * 
   * Cette fonction suppose que la structure du document
   * XML suit la structure exsitante dans l'API version 0.6
   * au 31/05/2017 qui est sous la forme:
   *    <code><osm> <changeset /> </osm></code>
   * 
   * @throws IllegalStateException si les données sur 
   *    le changeset ne sont pas trouvé.
   * @param doc Document issus de l'API OSM représentant 
   *    les données d'un changeset.
   * @return  le tableau associatif des attributs du changeset
   */
  public static ChangesetAttributesAndTags getChangesetAttributesFromXML(Document doc) {
    
    // on obtient le noeud du changeset que l'on souhaite traiter
    // le format étant normalement <osm> <changeset /> </osm>
    Node osm = doc.getFirstChild();
    Node changeset = osm.getFirstChild();
    Node tags = null;
    
    // s'il existe des sauts de ligne dans le document XML ils seront considérés comme du 
    // texte et donc auront leur propre noeud enfant. Nous ne souhaitons pas traiter cela
    // donc on parcours les différents enfants afin de trouver celui qui correspond 
    // aux données du changeset
    while(!changeset.getNodeName().equalsIgnoreCase("changeset") && changeset != null) {
      changeset = changeset.getNextSibling();
    }
    
    // si changeset est null ici, le traitement n'a pas trouvé de changeset
    // dans le document et donc retourne une exception.
    if(changeset == null)
      throw new IllegalStateException("No changeset found");
    
    StringBuilder tagsString = new StringBuilder("");
    
    // recherche des node tags enfants
    if(changeset.hasChildNodes()) {
      tags = changeset.getFirstChild();
      do {
        // vérification que le note est un tag
        if(tags.getNodeName().equalsIgnoreCase("tag")) {
          NamedNodeMap nnm = tags.getAttributes();
          // construction du string
          tagsString.append("\""+
          nnm.getNamedItem("k").getNodeValue().replaceAll("\"", "\\\\\"")
          +"\"=>" + "\""+
          nnm.getNamedItem("v").getNodeValue().replaceAll("\"", "\\\\\"")
          +"\",");
          
        }
      } while((tags = tags.getNextSibling()) != null);
    }
    if(tagsString.length() > 0)
      tagsString.deleteCharAt(tagsString.length()-1);
    
    // objet retourné
    ChangesetAttributesAndTags retour = 
        new ChangesetAttributesAndTags(changeset.getAttributes(), tagsString.toString());
    
    // retourne le tableau associatif des attributs et les tags du changeset
    return retour;
  }
  
  /**
   * <b>Dépendance :</b> alterDatabaseToChangeset
   *    Cette fonction n'a pas nécessairement besoin que 
   *    alterDatabaseToChangeset soit appelé pour fonctionner
   *    mais en cas de pré-anonymisation intégral, il est 
   *    préférable de d'abord changer la structure de la base. 
   *    
   *    
   * Fonction permettant de mettre à jour les données 
   * sur les éléments par lecture de l'API.
   * 
   * En particulier cette fonction ajoute les 
   * attributs de visibilité des éléments et ajoute
   * l'attribut de la version de la relation dans 
   * un membre.
   * 
   * @param createVisible 
   *    indique si la fonction doit créer 
   *    les colonnes de visiblité pour les éléments
   * @param createVrel 
   *    indique si la fonction doit créer 
   *    la colonne pour la version de la relation dans le membre.
   */
  public void updateElementsDataAttributes(boolean createVisible, boolean createVrel) {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null, pstmtMember = null;
    ResultSet listId = null;
    
    try {
      // récupération d'une connexion à la base de données
      conn = dbAccess.getConnection();
      
      // création d'une Statement, on fait en sorte de pourvoir retourner en arrière 
      // dans le parcours du ResultSet pour calculer le nombre d'éléments à traiter
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        
      // en fonction du paramètre d'entrée on execute la requête permettant 
      // de rajouter la colonne visible sur les tables des éléments
      if(createVisible)
        updateVisibleIntoElementsAddColumn(conn);

      // en fonction du paramètre d'entrée on execute la requête permettant 
      // de rajouter la colonne vrel sur les tables des relationmember
      if(createVrel)
        stmt.execute(queries.get(PreAnonymizationQueriesType.ADD_VERSION_MEMBER));
      
      
      for(OSMPrimitiveType type : OSMPrimitiveType.values()) {
        // préparation des requêtes en fonction du type
        // les requêtes de mise à jour de visibilité
        // la requête de mise à jour de membre pour relation
        // execution des requêtes de récupération des id
        switch (type) {
          case node:
            pstmt = 
              conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_VISIBLE_NODE));
            listId = 
              stmt.executeQuery(queries.get(PreAnonymizationQueriesType.SELECT_ID_NODE));
            
          break;
          case way:
            pstmt = 
              conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_VISIBLE_WAY));
            listId = 
                stmt.executeQuery(queries.get(PreAnonymizationQueriesType.SELECT_ID_WAY));
          break;
          case relation:
            pstmt = 
              conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_VISIBLE_RELATION));
            pstmtMember = 
                conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_VERSION_MEMBER));
            listId = 
                stmt.executeQuery(queries.get(PreAnonymizationQueriesType.SELECT_ID_RELATION));
          break;
          default:
            throw new IllegalStateException("Unexpected OSMPrimitiveType");
        }
        
        // création des variables servant à afficher la progression
        int total = 0, current = 0;
        // initialisation du total de lignes à traiter en accédant au numéro
        // de la dernière ligne
        if (listId.last()) {
          total = listId.getRow();
          // remise au début
          listId.beforeFirst();
        }
        
        // affiche le nombre de lignes à traiter en fonction si displayProgression est vrai
        if(displayOn)
          logger.log(Level.INFO, "Nombre de "+type+"s à traiter : "+total);
        
        // on parcours l'ensemble des changeset
        conn.setAutoCommit(false);
        while(listId.next()) {
          // affichage de la progression du traitement
          if(displayOn && total > 30 && ++current % (total/10) == 0) 
            logger.log(Level.INFO, "Traitement "+current+" sur "+total+" "+type+"s");
          
          // mise à jour des données
          updateElementData(type, pstmt, pstmtMember, listId.getString(1), current);
        }
        if(current % 1000 != 0)
          pstmt.executeBatch();

        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      // on ferme les resources dans tous les cas
    } finally {
      if(listId != null) 
        try{ listId.close(); } catch (Exception e) { }
      if(pstmt != null) 
        try{ pstmt.close(); } catch (Exception e) { }
      if(pstmtMember != null) 
        try{ pstmtMember.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
  }
  
  /**
   * Cette fonction charge les données historiques 
   * d'un élément d'un type donné en paramètre par 
   * lecture de l'API d'OSM. 
   * 
   * L'identifiant de l'élément recherché est donné
   * en paramètre, de même que son type afin de 
   * pouvoir construire l'URL pour charger les données.
   * 
   * Deux requêtes sont aussi passée en paramètre qui 
   * vont être executées pour mettre à jour les données
   * en base.
   * 
   * @param type le type de l'élément recherché
   * @param pstmt la requête permettant de mettre à 
   *    jour les attribut de visibilité des éléments
   * @param pstmtMember la requête permettant de mettre
   *    à jour les attributs de l'id et de la version 
   *    de la relation référencé dans un relationmember
   * @param idElmt identifiant de l'élément recherché
   * @param current 
   * @throws SQLException peut envoyer une exception
   *    sur l'exécution des requêtes pour sur l'affectation
   *    des valeurs aux requêtes. 
   */
  private void updateElementData(OSMPrimitiveType type, PreparedStatement pstmt,
      PreparedStatement pstmtMember, String idElmt, int current) throws SQLException {
    
    String url = OSM_API_URL + type + "/" + idElmt + OSM_HISTORY_SUFFIX_URL;
    
    Document doc = getDataFromAPI(url);
    
    if(doc == null) {
      listElementIdFailed.get(type).add(idElmt);
      return;
    }
    
    // affectation des valeurs de visibilité
    for(Node xmlNode : getElementsAttributesFromXML(doc, type)) {
      NamedNodeMap nnm = xmlNode.getAttributes();
      String id = nnm.getNamedItem("id").getNodeValue();
      String version = nnm.getNamedItem("version").getNodeValue();
      // affectation des valeurs issues de la base dans la requête
      pstmt.setBoolean(1, Boolean.parseBoolean(nnm.getNamedItem("visible").getNodeValue()));
      pstmt.setLong(2, Long.parseLong(id)); 
      pstmt.setInt(3, Integer.parseInt(version));
      
      pstmt.addBatch();
      
      // affectation des valeurs d'idrel et vrel dans les membres
      if(type == OSMPrimitiveType.relation) {
        for(NamedNodeMap nnmMember : getMembersAttributesFromXML(xmlNode)) {
          
          pstmtMember.setLong(1, Long.parseLong(id)); 
          pstmtMember.setInt(2, Integer.parseInt(version));
          pstmtMember.setLong(3, Long.parseLong(id+version));
          pstmtMember.setLong(4, Long.parseLong(nnmMember.getNamedItem("ref").getNodeValue()));
          pstmtMember.setString(5, nnmMember.getNamedItem("type").getNodeValue());
          
          pstmtMember.addBatch();
        }
        pstmtMember.executeBatch();
      }
      
      // execution de la requête de mise à jour
      if(current % 1000 == 0)
        pstmt.executeBatch();
    }
  }
  
  /**
   * Cette fonction prend un Document XML qui est généré
   * par la lecture de l'API OpenStreetMap et qui contient
   * les informations sur un des éléments et en retire une liste de
   * d'objets XML représentant les éléments.
   * 
   * Les objets sont retournés sous forme de {@link Node }
   * qui est la structure utilisée par l'API org.w3c.dom.
   * 
   * Cette fonction suppose que la structure du document
   * XML suit la structure existante dans l'API version 0.6
   * au 31/05/2017 qui est sous la forme:
   *    <code><osm> <node /> </osm></code>
   * ou
   *    <code><osm> <way|relation > <way|relation /> </osm></code>
   *    
   * Cette fonction recherche un type d'élément donné en paramètre.
   * 
   * @throws IllegalStateException si les données sur 
   *    les elements ne sont pas trouvées.
   * @param doc Document issus de l'API OSM représentant 
   *    les données d'un changeset.
   * @param type Type primitif d'OSM recherché
   *    
   * @return la liste des objets trouvés sous forme de Node
   */
  public static ArrayList<Node> getElementsAttributesFromXML(Document doc,
      OSMPrimitiveType type) {
    ArrayList<Node> retour = new ArrayList<>();
    // on obtient le noeud de l'élément que l'on souhaite traiter
    // l'élément étant normalement encadré par un objet osm
    Node osm = doc.getFirstChild();
    Node element = osm.getFirstChild();
    
    do {
      if(element.getNodeName().equalsIgnoreCase(type.name()))
          retour.add(element);
    } while((element = element.getNextSibling()) != null);
    
    // si la liste est vide, le traitement n'a pas trouvé d'élément
    // dans le document et donc retourne une exception.
    // Deprecated piece of code
    //if(retour.size() == 0)
    //  throw new IllegalStateException("No elements found");
    
    // retourne la liste
    return retour;
  }
  

  /**
   * Cette fonction prend un Document XML qui est généré
   * par la lecture de l'API OpenStreetMap et qui contient
   * les informations sur des relations et en retire une liste de
   * tableaux associatifs des différents attributs des member 
   * de ces relations.
   * 
   * Les tableaux est retourné sous forme de {@link NamedNodeMap }
   * qui est la structure utilisée par l'API org.w3c.dom.
   * 
   * Cette fonction suppose que la structure du document
   * XML suit la structure exsitante dans l'API version 0.6
   * au 31/05/2017 qui est sous la forme:
   *    <code><osm> <node /> </osm></code>
   * ou
   *    <code><osm> <way|relation > <way|relation /> </osm></code>
   * 
   * @throws IllegalStateException aucune relation
   *    n'a été trouvé.
   * @param doc Document issus de l'API OSM représentant 
   *    les données d'un changeset.
   *    
   * @return  le tableau associatif des attributs des membres
   */
  private static ArrayList<NamedNodeMap> getMembersAttributesFromXML(Node relation) {
    ArrayList<NamedNodeMap> retour = new ArrayList<>();
    // on obtient le noeud de l'élément que l'on souhaite traiter
    // l'élément étant normalement encadré par un objet osm
    
    // Deprecated piece of code
    //boolean throwException = true;
    Node member;
    do {
      if(relation.getNodeName().equalsIgnoreCase("relation")) {
        // Deprecated piece of code
        //throwException = false;
        if(relation.hasChildNodes()) {
          member = relation.getFirstChild();
          do {
            if(member.getNodeName().equalsIgnoreCase("member"))
              retour.add(member.getAttributes());
          } while((member = member.getNextSibling()) != null);
        }
      }
    } while((relation = relation.getNextSibling()) != null);
    
    // si on a pas trouvé de relation
    // dans le document on retourne une exception.
    // Deprecated piece of code
    //if(throwException)
    //  throw new IllegalStateException("No relation found");
    
    // retourne la liste
    return retour;
  }
  
  
  
  /**
   * Fonction permettant d'ajouter les colonnes du
   * booléen visible dans les table node, way et relation
   * ne vérifie pas que les colonnes n'existent pas déjà.
   * 
   * @param conn un objet Connection qui donne une connexion
   *    vers la base à modifier
   * @throws SQLException sur la création ou les exécutions de la requête.
   */
  private void updateVisibleIntoElementsAddColumn(Connection conn) throws SQLException {
    Statement stmt = conn.createStatement();

    // ajout de la colonne dans node
    stmt.execute(queries.get(PreAnonymizationQueriesType.ADD_VISIBLE_NODE));
    // ajout de la colonne dans way
    stmt.execute(queries.get(PreAnonymizationQueriesType.ADD_VISIBLE_WAY));
    // ajout de la colonne dans relation
    stmt.execute(queries.get(PreAnonymizationQueriesType.ADD_VISIBLE_RELATION));
  }
  
  /**
   * <b>Dépendance :</b> Aucune
   * Cette fonction ne dépend d'aucune autre et peut être
   * exécutée très tot si on ne souhaite pas mettre à jour 
   * les dates définies sur un changeset avec les dates 
   * définies sur chaque éléments. 
   * 
   * Fonction qui supprime les colonnes des éléments
   * correspondant à la dernière date de modification 
   * de l'élément.
   */
  public void removeDatesFromElements() {
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_DATE_NODE));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_DATE_WAY));
      stmt.execute(queries.get(PreAnonymizationQueriesType.DROP_DATE_RELATION));
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
  }
  
  /**
   * <b>Dépendance :</b> updateChangesetAttributesData
   * Cette fonction agit sur les colonnes created_at et time_opened  
   * ainsi updateChangesetAttributesData est nécessaire pour créer 
   * ces colonnes et les remplir avec les données issues de l'API OSM.
   * 
   * Cette fonction utilise le processus d'anonymisation de 
   * AnonymizedDate sur les dates récupérées de la base de données
   * puis met à jour les valeurs anonymisées dans la base. 
   * 
   * On utilise ici l'anonymisation incluant une valeur de temps
   * d'ouverture afin de préciser la généralisation.
   * 
   * Cette fonction devrait être appelée après la dernière modification 
   * des dates qui récupère les dates d'origine d'OSM, cependant elle 
   * ne supprime aucune données et donc peut être appelée plusieurs fois.
   */
  public void datesAnonymization() {
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet results = null;
    
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      pstmt = conn.prepareStatement(queries.get(PreAnonymizationQueriesType.UPDATE_DATE_CHANGESET));
      int current = 0;
      // selection des dates présentes dans les changeset
      results = stmt.executeQuery(queries.get(PreAnonymizationQueriesType.SELECT_DATE_CHANGESET));

      conn.setAutoCommit(false);
      while(results.next()) {
        current++;
        // récupération de la durée d'ouverture
        Date time_opened = new Date((long)(results.getDouble(3)*1000));
        // anonymisation des dates
        AnonymizedDate date = new AnonymizedDate(results.getTimestamp(2).getTime(), time_opened);
        pstmt.setString(1, date.toString());
        pstmt.setInt(2, results.getInt(1));
        
        pstmt.addBatch();
        if(current % 1000 == 0)
          pstmt.executeBatch();
      }
      
      if(current % 1000 != 0)
        pstmt.executeBatch();

      conn.setAutoCommit(true);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(results != null) 
        try{ results.close(); } catch (Exception e) { }
      if(pstmt != null) 
        try{ pstmt.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
  }
  
  /**
   * Fonction applicant l'ensemble des traitement permettant de conclure la 
   * pré-anonymisation ce qui inclut pricipalement le mélange des identifiants.
   * 
   * La fonction mélange d'abord les identifiants des contributors et les 
   * remplace dans les changeset.
   * Puis les identifiants des changesets.
   * Puis les identifiants des éléments.
   * Pour pouvoir mettre à jour les données sur les nodes qui composent les 
   * ways, elles sont extraites et placées dans une nouvelle table way_nodes
   * 
   * Enfin les colonnes contenant des informations issues des anciens identifiants
   * sont supprimés et ensuite les clés primaires sont redéfinies. 
   */
  public void shuffleId() {
    Connection conn = null;
    Statement stmt = null;
    
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      
      // ID CONTRIBUTOR
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_ANON_CONTRIBUTOR)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_CHANGESET_UID)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_UID_CONTRIBUTOR)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.REPLACE_UID_CONTRIBUTOR)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_TABLE_CONTRIBUTOR)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.REPLACE_TABLE_CONTRIBUTOR)); 
      
      // ID CHANGESET
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_ANON_CHANGESET)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_CHANGESET_IN_RELATION)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_CHANGESET_ID)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.REPLACE_CHANGESET_ID)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_TABLE_CHANGESET)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.REPLACE_TABLE_CHANGESET)); 

      // ID ELEMENTS
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_ANON_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_ANON_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_ANON_RELATION)); 
        // WAY_NODES
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.CREATE_WAY_NODES)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_WAY_NODES_IDS)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_COMPOSEDOF_WAY)); 
        // MEMBERS
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_REL_REF)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_RELATION)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_RELATIONMEMBER_MB_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_ID_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_ID_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.UPDATE_ID_RELATION)); 
      //stmt.executeUpdate(queries.get(AnonymizationQueriesType.DROP_ANON_NODE)); 
      //stmt.executeUpdate(queries.get(AnonymizationQueriesType.DROP_ANON_WAY)); 
      //stmt.executeUpdate(queries.get(AnonymizationQueriesType.DROP_ANON_RELATION)); 
      // BAD IDS
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_BAD_ID_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_BAD_ID_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_BAD_ID_RELATION)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.DROP_BAD_ID_RELATIONMEMBER)); 
      
      // PRIMARY KEYS
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_CONTRIBUTOR)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_WAY_NODES)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_CHANGESET)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_NODE)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_WAY)); 
      stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_RELATION)); 
      // peut ne pas fonctionner, une relation peut avoir deux fois un élément 
      // en tant que membre
      //stmt.executeUpdate(queries.get(PreAnonymizationQueriesType.ADD_PRIMARY_RELATIONMEMBER));
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
  }
  
  /**
   * Fonction effectuant l'ensemble du processus de 
   * Pré-Anonymisation sur une base non modifiée précédemment.
   * 
   * Les fonctions sont appelées de cette façon :
   * alterDatabaseToChangeset()
   * updateChangesetAttributesData(true)
   * updateElementsDataAttributes(true, true)
   * datesAnonymization()
   * removeDatesFromElements()
   * 
   * Ici on affiche s'il y en a les identifiants 
   * pour lesquels des données n'ont pas pu être 
   * récupérées. 
   * 
   * shuffleId()
   * 
   * Etant donné que shuffleId() modifie les 
   * identifiants, les modifications sont 
   * permannente. Les indentifiants ayant 
   * rencontrés un problème pourraient être
   * modifiés à la main cependant.
   */
  public void fullProcess() {
    if(displayOn)
      logger.log(Level.INFO, "Démarrage du processus d'anonymisation");
    alterDatabaseToChangeset();
    updateChangesetAttributesData(true);
    updateElementsDataAttributes(true, true);
    if(displayOn)
      logger.log(Level.INFO, "Mise à jour des éléments terminée");
    datesAnonymization();
    removeDatesFromElements();
    
    if(!listChangesetIdFailed.isEmpty()) {
      System.out.println("Liste de changesets non réussis :");
      System.out.println(listChangesetIdFailed);
    }
    for(OSMPrimitiveType pt : OSMPrimitiveType.values()) {
      if(!listElementIdFailed.get(pt).isEmpty()) {
        System.out.println("Liste de "+pt+"s non réussis :");
        System.out.println(listElementIdFailed.get(pt));
      }
    }
    if(displayOn)
      logger.log(Level.INFO, "Anonymisation des dates terminée");
    
    shuffleId();
    if(displayOn)
      logger.log(Level.INFO, "Processus d'anonymisation terminé");
  }
}

/**
 * Classe contenant toutes les données relatives 
 * au changeset, c'est-à-dire les tags et les
 * attributs. 
 * @author Matthieu Dufait
 */
class ChangesetAttributesAndTags {
  private NamedNodeMap attributes;
  private String tags;
  
  public ChangesetAttributesAndTags(NamedNodeMap attributes, String tags) {
    super();
    this.attributes = attributes;
    this.tags = tags;
  }

  public NamedNodeMap getAttributes() {
    return attributes;
  }

  public String getTags() {
    return tags;
  }
}