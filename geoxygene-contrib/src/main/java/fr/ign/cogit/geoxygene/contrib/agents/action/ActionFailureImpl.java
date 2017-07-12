/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.action;

/**
 * @author CDuchene
 * 
 */
public class ActionFailureImpl implements ActionFailure {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //
  /**
   * The failing action
   */
  private Action action;
  /**
   * the validity of this failure
   */
  private FailureValidity validity;
  /**
   * {@code true} if the other agent sharing the constraint handled by this
   * action should be informed when this failure will be removed, {@code false}
   * otherwise.
   */
  private boolean informOtherAgent;

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  /**
   * {@inheritDoc}
   */
  @Override
  public Action getAction() {
    return this.action;
  }

  /**
   * Constructs a failure from a failing action, a validity and indicating the
   * {@code #informOtherAgent} flag.
   * @param action the failing action
   * @param validity the validty of the failure
   * @param informOtherAgent a bollean indicating if the other agent sharing the
   *          constraint handled by the action should be informed when this
   *          failure is removed.
   */
  public ActionFailureImpl(Action action, FailureValidity validity,
      boolean informOtherAgent) {
    this.setAction(action);
    this.setValidity(validity);
    this.setInformOtherAgent(informOtherAgent);
  }

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // All getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * Setter for action.
   * @param action the action to set
   */
  protected void setAction(Action action) {
    this.action = action;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FailureValidity getValidity() {
    return this.validity;
  }

  /**
   * Setter for validity.
   * @param validity the validity to set
   */
  protected void setValidity(FailureValidity validity) {
    this.validity = validity;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getInformOtherAgent() {
    return this.informOtherAgent;
  }

  /**
   * Setter for informOtherAgent.
   * @param informOtherAgent the informOtherAgent to set
   */
  protected void setInformOtherAgent(boolean informOtherAgent) {
    this.informOtherAgent = informOtherAgent;
  }

  // /////////////////////////////////////////////
  // Other public methods //
  // /////////////////////////////////////////////
  @Override
  public String toString() {
    return "ActionFailureImpl [action=" + action + ", validity=" + validity
        + ", informOtherAgent=" + informOtherAgent + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((action == null) ? 0 : action.hashCode());
    result = prime * result + (informOtherAgent ? 1231 : 1237);
    result = prime * result + ((validity == null) ? 0 : validity.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ActionFailureImpl other = (ActionFailureImpl) obj;
    if (action == null) {
      if (other.action != null)
        return false;
    } else if (!action.equals(other.action))
      return false;
    if (informOtherAgent != other.informOtherAgent)
      return false;
    if (validity != other.validity)
      return false;
    return true;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
