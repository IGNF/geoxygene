/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

/**
 * Cette classe fournit un chronometre que l'on peut démarrer et mettre pause
 * facilement. This class provides chronometers (or stopwatches) that can be
 * started or stopped easily. TODO use threads to improve the getTime() method
 * when running.
 * @author GTouya
 * 
 */
public class Chronometer {

  /**
   * Is the chronometer running?
   */
  private boolean running;
  /**
   * The cumulated time between last start and last stop.
   */
  private long cumulTime;
  /**
   * The time {@code this} was lastly started and stopped.
   */
  private long timeIni, timeFin;

  public boolean isRunning() {
    return this.running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public Chronometer() {
    this.running = false;
    this.reset();
  }

  public void start() {
    this.cumulTime += (this.timeFin - this.timeIni);
    this.timeIni = System.currentTimeMillis();
    this.timeFin = 0;
    this.running = true;
  }

  public void pause() {
    this.timeFin = System.currentTimeMillis();
    this.running = false;
  }

  public void stop() {
    this.timeFin = System.currentTimeMillis();
    this.cumulTime += (this.timeFin - this.timeIni);
    this.running = false;
    this.reset();
  }

  public void reset() {
    this.cumulTime = 0;
    this.timeIni = 0;
    this.timeFin = 0;
  }

  public long getTime() {
    if (this.isRunning()) {
      this.pause();
      this.start();
      return this.cumulTime;
    }
    return this.cumulTime + (this.timeFin - this.timeIni);
  }

  @Override
  public String toString() {
    return String.valueOf(this.getTime());
  }
}
