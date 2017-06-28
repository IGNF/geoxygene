package fr.ign.cogit.geoxygene.contrib.agents.relation;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;

/**
 * A relationnal constraint. Such constraint concern a relation and one of the
 * geographic agents involved in the relation. It is a geographic constraint.
 * (see [Duchene 2003])
 * @author JGaffuri
 */
public interface RelationalConstraint extends GeographicConstraint {

  /**
   * @return The relation
   */
  Relation getRelation();

  /**
   * @param relation
   */
  void setRelation(Relation relation);

  /**
   * Retrieves the other agent sharing the constraint with this agent
   * @return the other agent sharing the constraint
   */
  IGeographicAgent getAgentSharingConstraint();

}
