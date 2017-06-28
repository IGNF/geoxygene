package fr.ign.cogit.geoxygene.contrib.agents.constraint;

/**
 * The constraint interface
 * @author JGaffuri
 */
public interface Constraint {

  /**
   * @return The constraint's importance
   */
  double getImportance();

  /**
   * @param importance
   */
  void setImportance(double importance);

  /**
   * Print info on the constraint in the console
   */
  void printInfosConsole();

}
