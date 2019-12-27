package fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries;

import java.util.HashMap;

import org.geolatte.geom.V;

/**
 * Cette classe sert à stocker les requêtes SQL 
 * utilisée par {@link fr.ign.cogit.geoxygene.osm.anonymization.analysis.DatabaseAnalysis}.
 * Il peut s'agir de requêtes préparées on non, on considère que les 
 * classes les utilisant savent comment les utiliser. Cette classe 
 * sert donc principalement d'externalisation des caractéristiques
 * des bases de données (structure, nom des tables et colonnes) 
 * plus que de classe destinée à être utilisée de façon 
 * très modulaire avec des traitements automatiques. 
 * 
 * La classe contient une HashMap qui utilise les 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.DatabaseAnalysisQueriesType} 
 * comme clés et leur valeurs sont les String représentant les requêtes.
 * Elle peut être accédée avec les fonctions get et put. 
 * De plus la classe peut devenir immutable en utilisant {@link setImmutable() }
 * cela indique que la HashMap ne peut plus être modifiée et tenter d'utiliser
 * la fonction put lèvera une exception.
 * 
 * @author Matthieu Dufait
 */
public class DatabaseAnalysisQueries {
  /**
   * Défini les requêtes pour l'analyse des bases de données 
   * les noms utilisés sont ceux issus de la pré-anonymisation
   * effectuée sur la base du nepal.
   */
  public static final DatabaseAnalysisQueries ANALYSIS_QUERIES;
  static {
    ANALYSIS_QUERIES = new DatabaseAnalysisQueries();
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_REPARTITION_CHANGESET_CONTRIBUTIONS,
        "SELECT contribCount, count(contribCount) frequencyCount FROM ( " + 
        "SELECT count(changeset) contribCount FROM changeset GROUP BY uid) t " + 
        "GROUP BY contribCount ORDER BY contribCount ASC;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_REPARTITION_NODE_CONTRIBUTIONS,
        "SELECT contribCount, count(contribCount) frequencyCount FROM " + 
        "(SELECT count(id) contribCount FROM node RIGHT JOIN changeset " + 
        "ON (node.changeset = changeset.changeset) " + 
        "GROUP BY uid) t " + 
        "GROUP BY contribCount ORDER BY contribCount ASC;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_REPARTITION_WAY_CONTRIBUTIONS,
        "SELECT contribCount, count(contribCount) frequencyCount FROM " + 
        "(SELECT count(id) contribCount FROM way RIGHT JOIN changeset " + 
        "ON (way.changeset = changeset.changeset) " + 
        "GROUP BY uid) t " + 
        "GROUP BY contribCount ORDER BY contribCount ASC;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_REPARTITION_RELATION_CONTRIBUTIONS,
        "SELECT contribCount, count(contribCount) frequencyCount FROM " + 
        "(SELECT count(id) contribCount FROM relation RIGHT JOIN changeset " + 
        "ON (relation.changeset = changeset.changeset) " + 
        "GROUP BY uid) t " + 
        "GROUP BY contribCount ORDER BY contribCount ASC;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_REPARTITION_TAG_COMMENT,
        "SELECT commentCount, count(commentCount) frequencyCount FROM \n" + 
        "(SELECT sum(cast(tags?'comment' as int)) commentCount\n" + 
        "FROM changeset GROUP BY uid) t \n" + 
        "GROUP BY commentCount ORDER BY commentCount ASC;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_CONTRIBUTEURS,
        "SELECT count(uid) FROM contributor;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_CONTRIBUTIONS,
        "SELECT count(changeset) FROM changeset;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_NODES,
        "SELECT count(*) FROM node;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_WAYS,
        "SELECT count(*) FROM way;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_RELATIONS,
        "SELECT count(*) FROM relation;");
    
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_NB_TAGS_COMMENT,
        "SELECT count(changeset) FROM changeset WHERE tags?'comment';");
    
    /* 
     * Requête permettant de récupérer 
     * pour tous les contributeurs de la base :
     * - son identifiant
     * - le nombre de nodes contribués
     * - le nombre de ways contribués
     * - le nombre de relations contribuées.
     * - le nombre de tags comment ajoutés
     */
    ANALYSIS_QUERIES.put(DatabaseAnalysisQueriesType.SELECT_CONTRIBUTORS_DATA,
        "WITH nbNode AS (\n" + 
        "SELECT uid, count(id) nodeCount \n" + 
        "FROM node RIGHT JOIN changeset \n" + 
        "ON (node.changeset = changeset.changeset)\n" + 
        "GROUP BY uid), \n" + 
        "nbWay AS (\n" + 
        "SELECT uid, count(id) wayCount \n" + 
        "FROM way RIGHT JOIN changeset \n" + 
        "ON (way.changeset = changeset.changeset)\n" + 
        "GROUP BY uid),  \n" + 
        "nbRelation AS (\n" + 
        "SELECT uid, count(id) relationCount \n" + 
        "FROM relation RIGHT JOIN changeset \n" + 
        "ON (relation.changeset = changeset.changeset)\n" + 
        "GROUP BY uid),\n" + 
        "nbComment AS (\n" + 
        "SELECT uid, sum(cast(tags?'comment' as int)) commentCount\n" + 
        "FROM changeset GROUP BY uid) \n" + 
        "\n" + 
        "SELECT contributor.uid, nbNode.nodeCount, \n" + 
        "nbWay.wayCount, nbRelation.relationCount, \n" + 
        "nbComment.commentCount\n" + 
        "FROM contributor\n" + 
        "JOIN nbNode ON nbNode.uid = contributor.uid\n" + 
        "JOIN nbWay ON nbWay.uid = contributor.uid\n" + 
        "JOIN nbRelation ON nbRelation.uid = contributor.uid\n" + 
        "JOIN nbComment ON nbComment.uid = contributor.uid;");
    
    
    
    if(ANALYSIS_QUERIES.querylist.size() != DatabaseAnalysisQueriesType.values().length)
      throw new IllegalStateException("Problème dans le remplissage des requêtes");
    
    ANALYSIS_QUERIES.setImmutable();
  }
  /**
   * HashMap des requêtes.
   */
  private HashMap<DatabaseAnalysisQueriesType, String> querylist;
  /**
   * Indique si querylist est modifiable.
   */
  private boolean mutable;
  
  /**
   * Initialise une HashMap vide et mutable à vrai.
   */
  public DatabaseAnalysisQueries() {
    this.querylist = new HashMap<DatabaseAnalysisQueriesType, String>();
    mutable = true;
  }

  /**
   * Initialise une nouvelle HashMap remplie avec les éléments de querylist
   * et mutable à vrai.
   * @param querylist
   */
  public DatabaseAnalysisQueries(HashMap<DatabaseAnalysisQueriesType, String> querylist) {
    this(querylist, true);
  }

  /**
   * Initialise une nouvelle HashMap remplie avec les éléments de querylist
   * et mutable à la valeur du paramètre mutable.
   * @param querylist
   * @param mutable
   */
  public DatabaseAnalysisQueries(HashMap<DatabaseAnalysisQueriesType, String> querylist,
      boolean mutable) {
    this();
    this.querylist.putAll(querylist);
    this.mutable = mutable;
  }
  
  /**
   * Même comportement que put de HashMap pour querylist
   * sauf si mutable est à false, renvoi une exception 
   * UnsupportedOperationException dans ce cas.
   * @see java.util.HashMap#put(K, V)
   * @param key
   * @param value
   * @return
   */
  public String put(DatabaseAnalysisQueriesType key, String value) {
    if(!mutable)
      throw new UnsupportedOperationException("Can not modify this instance any longer.");
    return querylist.put(key, value);
  }

  /**
   * Retourne la valeur liée à la clé 
   * donnée en paramètre.
   * @param key
   * @return
   */
  public String get(DatabaseAnalysisQueriesType key) {
    return querylist.get(key);
  }

  /**
   * Retourne la valeur de mutable.
   * @return
   */
  public boolean isMutable() {
    return mutable;
  }

  /**
   * Fixe mutable à false.
   * Rend ainsi l'instance de la classe 
   * non modifiable.
   */
  public void setImmutable() {
    this.mutable = false;
  }
}
