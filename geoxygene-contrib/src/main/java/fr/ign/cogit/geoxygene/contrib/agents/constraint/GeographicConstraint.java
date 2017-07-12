package fr.ign.cogit.geoxygene.contrib.agents.constraint;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A geographic constraint according to [Ruas 99]. It can be simple (on a
 * geographic agent) or relationnal (on an object, and a relation)
 * @author JGaffuri
 */
public interface GeographicConstraint extends Constraint {

  // TODO Voir comment integrer une flexibilite "publique" sur les contraintes
  // pour qu'elle puisse être instanciée depuis des specs... pb ça force à typer
  // publiquement la current value des contraintes à un niveau générique, ce qui
  // avait été évité jusque là.

  /**
   * @return The geographic agent
   */
  IGeographicAgent getAgent();

  /**
   * compute the constraint current value
   */
  // FIXME Verifier que ca a bien un sens... theoriquement elle devrait être
  // privee
  // puisqu'elle n'est utilisee que par sa classe concrete.
  void computeCurrentValue();

  /**
   * compute the constraint goal value
   */
  void computeGoalValue();

  /**
   * @return the constraint satisfaction
   */
  double getSatisfaction();

  /**
   * compute the constraint satisfaction
   */
  void computeSatisfaction();

  /**
   * 0 is the max priority.
   * @return the constraint priority
   */
  double getPriority();

  /**
   * compute of the constraint priority.
   */
  void computePriority();

  /**
   * @return the actions set poposed by the constraint
   */
  Set<ActionProposal> getActions();

}
