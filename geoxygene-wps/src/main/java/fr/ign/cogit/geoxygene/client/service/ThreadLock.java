/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL_V2-fr.txt
 *        see Licence_CeCILL_V2-en.txt
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
 * 
 * 
 * @copyright IGN
 *
 */
package fr.ign.cogit.geoxygene.client.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton lock used to store and lock the running process (CheckThread, UploadThread, ...).
 * 
 * @author BPESTY
 * @version 1.0
 */
public class ThreadLock {
  
  /**
   * The single instance of <code>ThreadLock</code>.
   */
  private static ThreadLock instance = new ThreadLock();

  /**
   * The <code>Map</code> containing the locks associed with the process Thread.
   */
  private final Map<String, Thread> submissionMap = new HashMap<String, Thread>();

  /**
   * Private constructor.
   * <p>
   * Used to force the call to the getInstance() method.
   */
  private ThreadLock() {
    // Do nothing
  }

  /**
   * Return the single instance of the submissionLock.
   * 
   * @return submissionLock
   */
  public static ThreadLock getInstance() {
    return instance;
  }

  /**
   * Set a lock for a given submission ID.
   * 
   * @param key
   *            the submission id
   * @param process
   *            the Thread we want to lock
   */
  public void lockProcess(String key, Thread process) {
    submissionMap.put(key, process);
  }

  /**
   * Release the lock on a given key.
   * 
   * @param key
   *            the key
   */
  public void releaseProcess(String key) {
    submissionMap.remove(key);
  }

  /**
   * Check if an submission ID is locked.
   * 
   * @param key
   *            the key
   * @return the lock value for the specified key
   */
  public boolean isLocked(String key) {
    return submissionMap.containsKey(key);
  }

  /**
   * Get a locked process for a given key.
   * 
   * @param key
   *            the key
   * @return the locked Thread.
   */
  public Thread getProcess(String key) {
    return submissionMap.get(key);
  }

}
