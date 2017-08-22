package fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries;

import java.util.HashMap;
/**
 * Cette classe sert à stocker les requêtes SQL 
 * utilisée par {@link fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ChangesetSwapping}.
 * Il peut s'agir de requêtes préparées on non, on considère que les 
 * classes les utilisant savent comment les utiliser. Cette classe 
 * sert donc principalement d'externalisation des caractéristiques
 * des bases de données (structure, nom des tables et colonnes) 
 * plus que de classe destinée à être utilisée de façon 
 * très modulaire avec des traitements automatiques. 
 * 
 * La classe contient une HashMap qui utilise les 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.ChangesetSwappingQueriesType} 
 * comme clés et leur valeurs sont les String représentant les requêtes.
 * Elle peut être accédée avec les fonctions get et put. 
 * De plus la classe peut devenir immutable en utilisant {@link setImmutable() }
 * cela indique que la HashMap ne peut plus être modifiée et tenter d'utiliser
 * la fonction put lèvera une exception.
 * 
 * @author Matthieu Dufait
 */
public class ChangesetSwappingQueries {


  /**
   * Défini les requêtes pour l'analyse des bases de données 
   * les noms utilisés sont ceux issus de la pré-anonymisation
   * effectuée sur la base du nepal.
   */
  public static final ChangesetSwappingQueries SWAPPING_QUERIES;
  static {
    SWAPPING_QUERIES = new ChangesetSwappingQueries();
    
    SWAPPING_QUERIES.put(ChangesetSwappingQueriesType.GET_RANDOM_CHANGESET,
        "WITH nbNode AS (\n" + 
        "SELECT changeset.changeset, count(id) nodeCount \n" + 
        "FROM node RIGHT JOIN changeset \n" + 
        "ON (node.changeset = changeset.changeset)\n" + 
        "GROUP BY changeset.changeset), \n" + 
        "nbWay AS (\n" + 
        "SELECT changeset.changeset, count(id) wayCount \n" + 
        "FROM way RIGHT JOIN changeset \n" + 
        "ON (way.changeset = changeset.changeset)\n" + 
        "GROUP BY changeset.changeset),  \n" + 
        "nbRelation AS (\n" + 
        "SELECT changeset.changeset, count(id) relationCount \n" + 
        "FROM relation RIGHT JOIN changeset \n" + 
        "ON (relation.changeset = changeset.changeset)\n" + 
        "GROUP BY changeset.changeset)\n" + 
        "\n" + 
        "SELECT changeset.changeset, changeset.uid, \n" + 
        "nodeCount, wayCount, relationCount, \n" + 
        "tags?'comment' FROM changeset\n" + 
        "JOIN nbNode ON nbNode.changeset = changeset.changeset\n" + 
        "JOIN nbWay ON nbWay.changeset = changeset.changeset\n" + 
        "JOIN nbRelation ON nbRelation.changeset = changeset.changeset\n" + 
        "ORDER BY RANDOM() LIMIT 10000;");
    
    SWAPPING_QUERIES.put(ChangesetSwappingQueriesType.SWAP_CHANGESET,
        "UPDATE changeset SET uid = CASE changeset \n" +
        "WHEN ? THEN ? \n" + 
        "WHEN ? THEN ? \n" + 
        "END \n" + 
        "WHERE changeset IN (?,?);");
    
    
    if(SWAPPING_QUERIES.querylist.size() != ChangesetSwappingQueriesType.values().length)
      throw new IllegalStateException("Problème dans le remplissage des requêtes");
    
    SWAPPING_QUERIES.setImmutable();
  }
  
  /**
   * HashMap des requêtes
   */
  private HashMap<ChangesetSwappingQueriesType, String> querylist;
  /**
   * Indique si querylist est modifiable
   */
  private boolean mutable;
  
  /**
   * Initialise une HashMap vide et mutable à vrai
   */
  public ChangesetSwappingQueries() {
    this.querylist = new HashMap<ChangesetSwappingQueriesType, String>();
    mutable = true;
  }

  /**
   * Initialise une nouvelle HashMap remplie avec 
   * les éléments de querylist et mutable à vrai
   * @param querylist
   */
  public ChangesetSwappingQueries(HashMap<ChangesetSwappingQueriesType, String> querylist) {
    this(querylist, true);
  }

  /**
   * Initialise une nouvelle HashMap remplie avec les éléments de querylist
   * et mutable à la valeur du paramètre mutable
   * @param querylist
   * @param mutable
   */
  public ChangesetSwappingQueries(HashMap<ChangesetSwappingQueriesType, String> querylist,
      boolean mutable) {
    this();
    this.querylist.putAll(querylist);
    this.mutable = mutable;
  }
  
  /**
   * Même comportement que put de HashMap pour querylist
   * sauf si mutable est à false, renvoi une exception 
   * UnsupportedOperationException dans ce cas
   * @see java.util.HashMap#put(K, V)
   * @param key
   * @param value
   * @return
   */
  public String put(ChangesetSwappingQueriesType key, String value) {
    if(!mutable)
      throw new UnsupportedOperationException("Can not modify this instance any longer.");
    return querylist.put(key, value);
  }

  /**
   * Retourne la valeur liée à la clé 
   * donnée en paramètre
   * @param key
   * @return
   */
  public String get(ChangesetSwappingQueriesType key) {
    return querylist.get(key);
  }

  /**
   * Retourne la valeur de mutable
   * @return
   */
  public boolean isMutable() {
    return mutable;
  }

  /**
   * Fixe mutable à false
   */
  public void setImmutable() {
    this.mutable = false;
  }
}