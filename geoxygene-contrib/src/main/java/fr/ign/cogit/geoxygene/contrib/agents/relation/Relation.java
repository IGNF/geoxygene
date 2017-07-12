package fr.ign.cogit.geoxygene.contrib.agents.relation;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * An explicit relation object between two geographic agents. (see [Duchene
 * 2003])
 * @author julien Gaffuri
 */
public interface Relation {

  /**
   * The first geographic agent involved in the relation
   */
  IGeographicAgent getAgentGeo1();

  /**
   * @param ag1
   */
  void setAgentGeo1(IGeographicAgent ag1);

  /**
   * The second geographic agent involved in the relation
   */
  IGeographicAgent getAgentGeo2();

  /**
   * @param ag2
   */
  void setAgentGeo2(IGeographicAgent ag2);

  /**
   * The relation's importance
   */
  double getImportance();

  /**
   * Compute the current value
   */
  void computeCurrentValue();

  /**
   * @return The relation's satisfaction value
   */
  double getSatisfaction();

  /**
   * @param satisfaction
   */
  void setSatisfaction(double satisfaction);

  /**
   * Compute the relation's satisfaction value
   */
  void computeSatisfaction();

  /**
   * Compute the relation's initial value
   */
  void computeInitialValue();

  /**
   * Compute the relation's goal value
   */
  void computeGoalValue();

}
