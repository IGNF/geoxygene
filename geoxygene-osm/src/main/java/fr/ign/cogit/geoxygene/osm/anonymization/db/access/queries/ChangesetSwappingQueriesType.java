package fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries;

/**
 * Enumération indiquant les types de requêtes 
 * utilisées dans 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.algorithm.ChangesetSwapping}
 * et attendus dans en tant que clé dans 
 * {@link fr.ign.cogit.geoxygene.osm.anonymization.db.access.queries.ChangesetSwappingQueries}.
 * 
 * @author Matthieu Dufait
 */
public enum ChangesetSwappingQueriesType {
  GET_RANDOM_CHANGESET, 
  SWAP_CHANGESET,
  
}
