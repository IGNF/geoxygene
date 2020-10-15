/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * Default implementation of the action interface.
 * 
 * @author JGaffuri
 * 
 */
public abstract class ActionImpl implements Action {

  /**
   * An annotation to distinguish the fields used to define the action from the
   * other fields. Used by the Equals method (and the hashcode method). Two
   * actions are equals if all the fields annotated with ActionField are equals.
   * @author AMaudet
   * 
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  protected @interface ActionField {
  }

  Logger logger = LogManager.getLogger(ActionImpl.class.getName());

  /**
   * Build an action for an agent, proposed by a constraint, with a weight
   * 
   * @param agent
   * @param constraint
   * @param weight
   */
  public ActionImpl(IAgent agent, Constraint constraint, double weight) {
    this.setAgent(agent);
    this.setConstraint(constraint);
    this.setWeight(weight);
  }

  /**
   * Build an action for an agent, proposed by a constraint, with a weight of
   * 1.0
   * 
   * @param agent
   * @param constraint
   */
  public ActionImpl(IAgent agent, Constraint constraint) {
    this(agent, constraint, 1.0);
  }

  /**
   * the action's weight
   */
  @ActionField
  private double weight = 0.0;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#getWeight()
   */
  /**
   * @return
   */
  @Override
  public double getWeight() {
    return this.weight;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#setWeight(double)
   */
  /**
   * @param weight
   */
  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * the constraint proposing the action
   */
  @ActionField
  private Constraint constraint;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#getConstraint()
   */
  /**
   * @return
   */
  @Override
  public Constraint getConstraint() {
    return this.constraint;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.action.Action#setConstraint(fr.ign.cogit.
   * agentgeoxygene.constraint.Constraint)
   */
  /**
   * @param constraint
   */
  @Override
  public void setConstraint(Constraint constraint) {
    this.constraint = constraint;
  }

  /**
   * the agent
   */
  @ActionField
  private IAgent agent;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#getAgent()
   */
  /**
   * @return
   */
  @Override
  public IAgent getAgent() {
    return this.agent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#setAgent(fr.ign.cogit.
   * agentgeoxygene .agent.Agent)
   */
  /**
   * @param agent
   */
  @Override
  public void setAgent(IAgent agent) {
    this.agent = agent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#compute()
   */
  @Override
  public abstract ActionResult compute() throws InterruptedException;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.action.Action#clean()
   */
  @Override
  public void clean() {
    this.setAgent(null);
    this.setConstraint(null);
  }

  /**
   * the restriction of action application.
   */
  @ActionField
  private int restriction = -1;

  /**
   * @return
   */
  @Override
  public int getRestriction() {
    return this.restriction;
  }

  /**
   * @param restriction
   */
  @Override
  public void setRestriction(int restriction) {
    this.restriction = restriction;
  }

  /**
   * A modified equals method to consider the fact that two different Action
   * objects may be equals if they have some specific fields equals (this
   * specific fields are annotated by the @ActionField annotation {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {

    logger.trace("Test the equality of two actions..");
    // test simple equality
    if (this == obj) {
      logger.trace("Actions are the same object.");
      return true;
    }
    // no equality with a null object
    if (obj == null) {

      logger.trace("Object in parameter is null.");
      return false;
    }

    // the other action must be of same class
    if (this.getClass() != obj.getClass()) {
      logger.trace("Actions are from different classes.");
      return false;
    }
    // Now the other one is an ActionImpl
    // ActionImpl other = (ActionImpl) obj;
    // It should concern the same agent
    // if (!agent.equals(other.agent))
    // return false;
    // TODO: need to be tested on CartAcom actionjs.

    // Get the class of the action.
    Class<?> testedClass = this.getClass();
    logger.trace("Class of the action : " + testedClass);
    // Test each argument of the class and its subclasses.
    while (true) {
      logger.trace("List of field : " + testedClass.getDeclaredFields().length);

      // Get each field of the tested class.
      for (Field f : testedClass.getDeclaredFields()) {
        if (f.getAnnotation(ActionField.class) != null) {
          f.setAccessible(true);
          try {
            logger.trace("Testing Field " + f + " : " + f.get(this)
                + " is Equals " + f.get(obj));
            // if the field is not declared for this, it have to be not declared
            // for obj.
            if (f.get(this) == null) {
              if (f.get(obj) != null)
                return false;
            } else {
              // if the field is declared, it needs to be equals to the obj
              // one..
              if (!f.get(this).equals(f.get(obj)))
                return false;
            }
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
      }
      // we dont need to test for the superclasses of ActionImpl.
      if (testedClass == ActionImpl.class)
        break;
      testedClass = testedClass.getSuperclass();
      logger.trace("Tested Class : " + testedClass);
    }
    return true;
  }

  /**
   * A modified hashCode method to consider the fact that two different Action
   * objects may be equals if they have some specific fields equals (this
   * specific fields are annotated by the @ActionField annotation
   * {@inheritDoc} If to objects are equals, they need to have the same hashcode
   * value.
   */
  @Override
  public int hashCode() {
    // final int prime = 10;
    // Class<?> testedClass = this.getClass();
    // int result = testedClass.hashCode();
    // // Test each argument of the class and its subclasses.
    // while (true) {
    // // for each field, find a number
    // for (Field f : testedClass.getDeclaredFields()) {
    // // verify the annotation
    // if (f.getAnnotation(ActionField.class) != null) {
    // f.setAccessible(true);
    // try {
    // result = prime * result + f.get(this).hashCode();
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // if (testedClass == ActionImpl.class)
    // break;
    // testedClass = testedClass.getSuperclass();
    // }
    // logger.trace("HashCode for " + this + " is : " + result);
    // return result;
    return this.getClass().getSimpleName().hashCode();
  }

}
