package fr.ign.cogit.geoxygene.contrib.agents.relation;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A default implementation of the Relation interface
 * 
 * @author julien Gaffuri
 * 
 */
public abstract class RelationImpl implements Relation {

  /**
   * Constructor for implementation.
   */
  protected RelationImpl() {
  }

  /**
   * Build a relation between 2 geographic agents, from the agents. Note that
   * the importance of the relation is not instantiated (=> valid for GAEL
   * apparently, but surely not for CartACom)
   * 
   * @param ag1
   * @param ag2
   */
  public RelationImpl(IGeographicAgent ag1, IGeographicAgent ag2) {
    this.setAgentGeo1(ag1);
    this.setAgentGeo2(ag2);
  }

  /**
   * Build a relation between 2 geographic agents, from the agents and the
   * importance of the relation.
   * 
   * @param ag1
   * @param ag2
   */
  public RelationImpl(IGeographicAgent ag1, IGeographicAgent ag2,
      double importance) {
    this.setAgentGeo1(ag1);
    this.setAgentGeo2(ag2);
    this.setImportance(importance);
  }

  /**
   * The first geographic agent involved in the relation
   */
  private IGeographicAgent ag1;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.relation.Relation#getAgentGeo1()
   */
  @Override
  public IGeographicAgent getAgentGeo1() {
    return this.ag1;
  }

  @Override
  public void setAgentGeo1(IGeographicAgent ag1) {
    this.ag1 = ag1;
  }

  /**
   * The second geographic agent involved in the relation
   */
  private IGeographicAgent ag2;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.relation.Relation#getAgentGeo2()
   */
  @Override
  public IGeographicAgent getAgentGeo2() {
    return this.ag2;
  }

  @Override
  public void setAgentGeo2(IGeographicAgent ag2) {
    this.ag2 = ag2;
  }

  /**
     */
  private double importance;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.relation.Relation#getImportance()
   */
  /**
   * @return
   */
  @Override
  public double getImportance() {
    return this.importance;
  }

  /**
   * Sets the importance of the relation
   * @param importance
   */
  protected void setImportance(double importance) {
    this.importance = importance;
  }

  /**
   * The relation's satisfaction
   */
  private double satisfaction;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.relation.IRelation#getSatisfaction()
   */
  /**
   * @return
   */
  @Override
  public double getSatisfaction() {
    return this.satisfaction;
  }

  /**
   * @param satisfaction
   */
  @Override
  public void setSatisfaction(double satisfaction) {
    this.satisfaction = satisfaction;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public boolean equals(Object obj) {

    if (this.getClass() != obj.getClass()) {
      return false;
    }
    Relation rel = (Relation) obj;
    if (((this.getAgentGeo1().equals(rel.getAgentGeo1()))
        && (this.getAgentGeo2().equals(rel.getAgentGeo2())))
        || ((this.getAgentGeo1().equals(rel.getAgentGeo2()))
            && (this.getAgentGeo2().equals(rel.getAgentGeo1())))) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return super.hashCode();
  }

}
