package fr.ign.cogit.geoxygene.contrib.agents.relation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraintImpl;

/**
 * 
 * A relational constraint default implementation.
 * 
 * @author JGaffuri
 * 
 */
public abstract class RelationalConstraintImpl extends GeographicConstraintImpl
    implements RelationalConstraint {
  private static Logger logger = LogManager
      .getLogger(RelationalConstraintImpl.class.getName());

  /**
   * the relation considered by the constraint
   */
  protected Relation relation;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.relation.RelationnalConstraint#getRelation()
   */
  /**
   * @return
   */
  @Override
  public Relation getRelation() {
    return this.relation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.relation.RelationnalConstraint#setRelation(
   * fr.ign.cogit.agentgeoxygene.relation.Relation)
   */
  /**
   * @param relation
   */
  @Override
  public void setRelation(Relation relation) {
    this.relation = relation;
  }

  /**
   * Build a relational constraint on a relation and one of the agents involved
   * in the relation.
   * 
   * @param agent
   * @param relation
   * @param importance
   */
  public RelationalConstraintImpl(IGeographicAgent agent, Relation relation,
      double importance) {
    super(agent, importance);

    // check the agent is one of the relation's ones
    if (relation.getAgentGeo1() != agent && relation.getAgentGeo2() != agent) {
      RelationalConstraintImpl.logger
          .error("Error in relation construction: the relation " + relation
              + " does not involve " + agent + " but " + relation.getAgentGeo1()
              + " and " + relation.getAgentGeo2());
    }

    this.relation = relation;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.ConstraintImpl#getImportance()
   */
  @Override
  public double getImportance() {
    // according to [Duchene 2003], the relational constraint importance is the
    // relation's one
    return this.getRelation().getImportance();
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.constraint.GeographicConstraintImpl#
   * computeCurrentValue()
   */
  @Override
  public void computeCurrentValue() {
    // according to [Duchene 2003], the relational constraint value is the
    // relation's one
    this.getRelation().computeCurrentValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.constraint.GeographicConstraintImpl#
   * computeGoalValue()
   */
  @Override
  public void computeGoalValue() {
    // according to [Duchene 2003], the relational constraint value is the
    // relation's one
    this.getRelation().computeGoalValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraintImpl#
   * getSatisfaction ()
   */
  @Override
  public double getSatisfaction() {
    // according to [Duchene 2003], the relational constraint satisfaction is
    // the relation's one
    return this.getRelation().getSatisfaction();
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.constraint.GeographicConstraintImpl#
   * computeSatisfaction()
   */
  @Override
  public void computeSatisfaction() {
    // according to [Duchene 2003], the relational constraint satisfaction is
    // the relation's one
    this.getRelation().computeSatisfaction();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGeographicAgent getAgentSharingConstraint() {
    if (this.getAgent().equals(this.getRelation().getAgentGeo1())) {
      return this.getRelation().getAgentGeo2();
    } else if (this.getAgent().equals(this.getRelation().getAgentGeo2())) {
      return this.getRelation().getAgentGeo1();
    } else {
      RelationalConstraintImpl.logger
          .error("Problem to retrieve other agent sharing the constraint "
              + this.toString());
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    return false;
    /*
     * if (this.getClass() != obj.getClass()) return false;
     * RelationnalConstraint relConstraint = (RelationnalConstraint) obj; if
     * (!this.getRelation().equals(relConstraint.getRelation())) return false;
     * return true;
     */
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
