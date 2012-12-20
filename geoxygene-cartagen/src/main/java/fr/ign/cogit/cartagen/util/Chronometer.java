/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
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
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public Chronometer() {
    running = false;
    reset();
  }

  public void start() {
    cumulTime += (timeFin - timeIni);
    timeIni = System.currentTimeMillis();
    timeFin = 0;
    running = true;
  }

  public void pause() {
    timeFin = System.currentTimeMillis();
    running = false;
  }

  public void stop() {
    timeFin = System.currentTimeMillis();
    cumulTime += (timeFin - timeIni);
    running = false;
    reset();
  }

  public void reset() {
    cumulTime = 0;
    timeIni = 0;
    timeFin = 0;
  }

  public long getTime() {
    if (isRunning()) {
      pause();
      start();
      return cumulTime;
    }
    return cumulTime + (timeFin - timeIni);
  }

  public String toString() {
    return String.valueOf(getTime());
  }
}
