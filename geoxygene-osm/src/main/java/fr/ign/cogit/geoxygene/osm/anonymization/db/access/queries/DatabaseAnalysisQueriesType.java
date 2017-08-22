package fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries;

/**
 * Enumération indiquant les types de requêtes 
 * utilisées dans 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.analysis.DatabaseAnalysis}
 * et attendus dans en tant que clé dans 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.DatabaseAnalysisQueries}.
 * 
 * @author Matthieu Dufait
 */
public enum DatabaseAnalysisQueriesType {
  SELECT_REPARTITION_CHANGESET_CONTRIBUTIONS, 
  SELECT_REPARTITION_NODE_CONTRIBUTIONS,
  SELECT_REPARTITION_WAY_CONTRIBUTIONS,
  SELECT_REPARTITION_RELATION_CONTRIBUTIONS,
  SELECT_REPARTITION_TAG_COMMENT, 
  SELECT_NB_CONTRIBUTEURS, 
  SELECT_NB_CONTRIBUTIONS, 
  SELECT_NB_NODES, 
  SELECT_NB_WAYS, 
  SELECT_NB_RELATIONS, 
  SELECT_CONTRIBUTORS_DATA, 
  SELECT_NB_TAGS_COMMENT,
}
