/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.agents;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * Object composed of an agent's list and able to manage their activations
 * @author JGaffuri 28 janv. 2009
 */
public class Scheduler implements Runnable {
  private static Logger logger = Logger.getLogger(Scheduler.class.getName());

  /**
   * The agent's list of the scheduler
   */
  private ArrayList<IAgent> list;

  /**
   * @return
   */
  public ArrayList<IAgent> getList() {
    if (this.list == null) {
      this.list = new ArrayList<IAgent>();
    }
    return this.list;
  }

  /**
   * Add an agent to the scheduler list
   * @param agent
   */
  public void add(IAgent agent) {
    this.getList().add(agent);
  }

  /**
   * Add agent to the beginning of the list.
   * 
   * @param agent
   * @author amaudet
   */
  public void addToTheTop(IAgent agent) {
    this.getList().add(0, agent);
  }

  /**
   * Remove an agent from the scheduler list
   * @param agent
   */
  public void remove(IAgent agent) {
    this.getList().remove(agent);
  }

  /**
   * Waiting time between two successives activation os agents (negative or null
   * if no pause is required)
   */
  private static long WAIT_TIME = -1;

  public long getWaitTime() {
    return Scheduler.WAIT_TIME;
  }

  /**
   * The scheduler's thread
   */
  protected Thread thread = null;

  /**
   * @return
   */
  public Thread getThread() {
    return this.thread;
  }

  /**
   * Activate the scheduler
   */
  public synchronized void activate() {
    if (this.thread != null) {
      return;
    }
    this.thread = new Thread(this);
    this.thread.start();
  }

  /**
   * Wirth {@code true} if the scheduler has to be stopped
   */
  protected boolean stop = false;

  /**
   * Deactivate the scheduler
   */
  public void deactivate() {
    this.stop = true;
  }

  /**
   * Test if the scheduler has to be stopped
   * @throws InterruptedException
   */
  public void testStop() throws InterruptedException {
    if (this.stop) {
      throw new InterruptedException();
    }
  }

  private boolean random = false;

  public boolean isRandom() {
    return random;
  }

  public void setRandom(boolean random) {
    this.random = random;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public synchronized void run() {

    int nb = this.getList().size();
    Scheduler.logger
        .info("- Scheduler lanching (" + nb + " agents to activate)");

    int i = 1;
    try {
      // while the list is not empty, activate its elements
      while (!this.getList().isEmpty()) {

        int element = 0;
        if (random) {
          Random rand = new Random();
          element = rand.nextInt(this.getList().size());
        }

        // retrieve the first element
        IAgent agent = this.getList().get(element);

        if (Scheduler.logger.isInfoEnabled()) {
          Scheduler.logger
              .info("activation of " + agent + " (" + i++ + "/" + nb + ")");
        }
        agent.activate();
        this.remove(agent);

        // test if an interruption is required
        this.testStop();

        // pause, if required
        if (Scheduler.WAIT_TIME > 0) {
          try {
            Thread.sleep(this.getWaitTime());
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        this.testStop();
      }
      this.thread = null;
      Scheduler.logger.info("- End (the scheduler is empty)");
    } catch (InterruptedException e) {
      // an interuption has been required: stop the scheduler
      this.stop = false;
      this.thread = null;
    }
  }

}
