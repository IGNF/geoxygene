package fr.ign.cogit.geoxygene.osm.anonymization.analysis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ContributorData;
import fr.ign.cogit.geoxygene.osm.anonymization.analysis.collections.DiscreetRangeMap;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.PostgresAccess;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.DatabaseAnalysisQueries;
import fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.DatabaseAnalysisQueriesType;

/**
 * Cette classe permet de traiter une base 
 * de données issues d'OpenStreetMap ayant 
 * subit un processus de Pré-Anonymisation 
 * afin d'en retirer les statistiques utilisées 
 * lors du processus d'anonymisation.
 * 
 * Différentes fonctions permettent d'obtenir 
 * indépendamment chaque caractériques des statistiques
 * cependant le moyen le plus simple d'utiliser
 * cette classe est d'utiliser la fonction getStats()
 * qui retourne une nouvelle DatabaseStatistics 
 * remplie de toutes les caractéristiques.
 * 
 * @author Matthieu Dufait
 */
public class DatabaseAnalysis {
  
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
  private DatabaseAnalysisQueries queries;
  
  /**
   * Table qui contient la taille des différents intervalles
   * utilisées pour les DiscreetRangeMap représentant les 
   * répartitions d'éléments. 
   */
  private static final DiscreetRangeMap<Integer> repartitionLimits;
  static {
    repartitionLimits = new DiscreetRangeMap<>();
    // génération de l'ensemble des limites pour chaque intervalle
    repartitionLimits.insert(0l, 1l, 1);
    for(int i = 0; i < 9; i++) {
      long power = (long) Math.pow(10, i);
      repartitionLimits.insert(power, power*10, (int) power);
    }
  }
  
  /**
   * Retourne la taille de l'intervalle dans 
   * lequel se trouve le paramètre nb dans les 
   * tables de répartitions de contributions 
   * par utilisateur.
   * @param nb
   * @return
   */
  public static long getRepartitionLimit(int nb) {
    return repartitionLimits.getValue((long) nb);
  }
  
  /**
   * Constructeur initialisant la classe avec les
   * paramètres.
   * @param dbAccess
   * @param queries
   */
  public DatabaseAnalysis(PostgresAccess dbAccess, DatabaseAnalysisQueries queries) {
    this.dbAccess = dbAccess;
    this.queries = queries;
  }
  
  /**
   * Fonction destinée à executer la requête donnée
   * en paramètre correspondant à une requête pour 
   * compter les éléments présents dans la base de 
   * données comme compter les nodes. 
   * Pour cela la fonction exécute la requête et
   * retourne la valeur dans la première ligne de
   * la première colonne retournée. Aucune vérification 
   * n'est faite.
   * @param query requête de compte d'élément
   * @return nombre d'élément (en fonction de la requête)
   */
  private long getNbContribQuery(String query) {
    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;
    long nb = -1;
    
    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      results = stmt.executeQuery(query);
      
      results.next();
      nb = results.getLong(1);
      
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if(results != null) 
        try{ results.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    return nb;
  }
  
  /**
   * Fonction retournant le nombre de contributeurs
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de contributeurs dans la base.
   */
  public long getNbContributeurs() {
    return getNbContribQuery(queries.get(
        DatabaseAnalysisQueriesType.SELECT_NB_CONTRIBUTEURS));
  }

  /**
   * Fonction retournant le nombre de nodes
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de nodes dans la base.
   */
  public long getNbNodes() {
    return getNbContribQuery(queries.get(
        DatabaseAnalysisQueriesType.SELECT_NB_NODES));
  }

  /**
   * Fonction retournant le nombre de ways
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de ways dans la base.
   */
  public long getNbWays() {
    return getNbContribQuery(queries.get(
        DatabaseAnalysisQueriesType.SELECT_NB_WAYS));
  }

  /**
   * Fonction retournant le nombre de relations
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de relations dans la base.
   */
  public long getNbRelations() {
    return getNbContribQuery(queries.get(
        DatabaseAnalysisQueriesType.SELECT_NB_RELATIONS));
  }

  /**
   * Fonction retournant le nombre de changesets
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de changesets dans la base.
   */
  public long getNbContributions() {
      return getNbContribQuery(queries.get(
          DatabaseAnalysisQueriesType.SELECT_NB_CONTRIBUTIONS));
  }

  /**
   * Fonction retournant le nombre de tags comment
   * dans la base de données courante avec un 
   * appelle à la base qui compte le nombre d'éléments.
   * @return le nombre de changesets dans la base.
   */
  public long getNbTagsComment() {
      return getNbContribQuery(queries.get(
          DatabaseAnalysisQueriesType.SELECT_NB_TAGS_COMMENT));
  }
  
  /**
   * Fonction qui génère une table d'intervalles pour
   * représenter une répartition 
   * avec lecture des données de la base. 
   * 
   * On considère que les fonctions appelantes 
   * connaissent le format demandé pour les requetes,
   * c'est à dire une première colonne pour le compte
   * d'élément qui donne les intervalles de la répartition
   * et une seconde colonne pour la fréquence d'apparition
   * de ce compte.
   * 
   * Un nombre de contribution n se trouve dans l'invervalle
   * entre floor(n/10^floor(log10(n)))*floor(log10(n) (inclus)
   * et floor(n/10^floor(log10(n)))*floor(log10(n)+floor(log10(n) (exclus)
   * soit par exemple 150 se trouve sur l'invervalle 100(inclus)-200(exclus).
   * @param query la requête qui donne la répartition
   * @return
   */
  private DiscreetRangeMap<Integer> generateRepartition(String query)  {

    DiscreetRangeMap<Integer> repartition = new DiscreetRangeMap<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;

    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      results = stmt.executeQuery(query);
      
      long limit = 0;
      Integer tailleIntervalle;
      // on remplie la table de répartition avec les intervalles défini avec 0
      while((tailleIntervalle = repartitionLimits.getValue((long) limit)) != null) {
        // comme on rempli les intervalles dans l'ordre, 
        // on peut mettre la limite supérieure à Long.MAX_VALUE
        // les intervalles suivants écriront par dessus les autres
        repartition.insert(limit, Long.MAX_VALUE, 0);
        limit += tailleIntervalle;
      }
      
      // parcours des résultats
      while(results.next()) {
        // ajout des valeurs à la répartition
        repartition.setValue(results.getLong(1), 
            repartition.getValue(results.getLong(1))+results.getInt(2));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      if(results != null) 
        try{ results.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    
    return repartition;
  }
  
  /**
   * Génère et retourne les données sur la répartition des changesets.
   * @return une nouvelle DiscreetRangeMap<Integer> 
   *    définissant la répartition des changesets.
   */
  public DiscreetRangeMap<Integer> generateRepartitionChangesetContributions() {
    return generateRepartition(queries.get(
          DatabaseAnalysisQueriesType.SELECT_REPARTITION_CHANGESET_CONTRIBUTIONS));
  }
  
  /**
   * Génère et retourne les données sur la répartition des nodes.
   * @return une nouvelle DiscreetRangeMap<Integer> 
   *    définissant la répartition des nodes.
   */
  public DiscreetRangeMap<Integer> generateRepartitionNodeContributions() {
    return generateRepartition(queries.get(
          DatabaseAnalysisQueriesType.SELECT_REPARTITION_NODE_CONTRIBUTIONS));
  }
  
  /**
   * Génère et retourne les données sur la répartition des ways.
   * @return une nouvelle DiscreetRangeMap<Integer> 
   *    définissant la répartition des ways.
   */
  public DiscreetRangeMap<Integer> generateRepartitionWayContributions() {
    return generateRepartition(queries.get(
          DatabaseAnalysisQueriesType.SELECT_REPARTITION_WAY_CONTRIBUTIONS));
  }
  
  /**
   * Génère et retourne les données sur la répartition des relations.
   * @return une nouvelle DiscreetRangeMap<Integer> 
   *    définissant la répartition des relations.
   */
  public DiscreetRangeMap<Integer> generateRepartitionRelationContributions() {
    return generateRepartition(queries.get(
          DatabaseAnalysisQueriesType.SELECT_REPARTITION_RELATION_CONTRIBUTIONS));
  }
  
  /**
   * Génère et retourne les données sur la répartition des tags comment.
   * @return une nouvelle DiscreetRangeMap<Integer> 
   *    définissant la répartition des tags comment.
   */
  public DiscreetRangeMap<Integer> generateRepartitionTagComment() {
    return generateRepartition(queries.get(
          DatabaseAnalysisQueriesType.SELECT_REPARTITION_TAG_COMMENT));
  }
  
  /**
   * Génère et retourne une table de hashage contenant
   * les informations sur l'ensembles des contributeurs ainsi
   * que le nombre de contributions de chaque types 
   * qu'ils ont effectués. Pour cela la fonction 
   * effectue un appel vers la base de données qui 
   * retrourne l'ensembles de données nécessaires pour 
   * construire la table.
   * @return une nouvelle HashMap avec les données sur les contributeurs.
   */
  public HashMap<Long, ContributorData> generateContributorMap() {
    HashMap<Long, ContributorData>  map = new HashMap<>();
    Connection conn = null;
    Statement stmt = null;
    ResultSet results = null;

    try {
      conn = dbAccess.getConnection();
      stmt = conn.createStatement();
      results = stmt.executeQuery(queries.get(
          DatabaseAnalysisQueriesType.SELECT_CONTRIBUTORS_DATA));
      
      // parcours des résultats
      while(results.next()) {
        map.put(results.getLong(1),
            new ContributorData(
                results.getLong(1),
                results.getLong(2),
                results.getLong(3),
                results.getLong(4),
                results.getLong(5)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      if(results != null) 
        try{ results.close(); } catch (Exception e) { }
      if(stmt != null) 
        try{ stmt.close(); } catch (Exception e) { }
      if(conn != null) 
        try{ conn.close(); } catch (Exception e) { }
    }
    
    return map;
  }
  
  /**
   * Initialise une nouvelle DatabaseStatistics 
   * et remplit les valeurs des ses attributs avec 
   * des appels aux fonction d'analyses afin 
   * d'avoir des statistiques complètes.
   * @return une nouvelle DatabaseStatistics complète.
   */
  public DatabaseStatistics getStats() {
    DatabaseStatistics statsRetour = new DatabaseStatistics();
    statsRetour.setRepartitionChangesetContributions(this.generateRepartitionChangesetContributions());
    statsRetour.setRepartitionWayContributions(this.generateRepartitionWayContributions());
    statsRetour.setRepartitionNodeContributions(this.generateRepartitionNodeContributions());
    statsRetour.setRepartitionRelationContributions(this.generateRepartitionRelationContributions());
    statsRetour.setRepartitionTagComment(this.generateRepartitionTagComment());
    statsRetour.setNbUsers(this.getNbContributeurs());
    statsRetour.setNbContributions(this.getNbContributions());
    statsRetour.setNbNodes(this.getNbNodes());
    statsRetour.setNbWays(this.getNbWays());
    statsRetour.setNbRelations(this.getNbRelations());
    statsRetour.setNbTagsComment(this.getNbTagsComment());
    statsRetour.setContributorMap(this.generateContributorMap());
    return statsRetour;
  }
}
