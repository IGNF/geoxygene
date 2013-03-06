/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

/**
 * Detailed evaluation of the results of an object like edge or node.
 * <ul>
 * <li> - Total number of object </li>
 * <li> - Number of OK found object </li>
 * <li> - Number of KO found object </li>
 * <li> - Number of doubtful found object </li>
 * </ul>
 * 
 * @author M.-D. Van Damme
 * @version 1.6
 */
public class ResultatStatEvaluationAppariement {
  
  /** Total number of object. */
  private int totalNumber;
  
  /** Number of OK found object. */ 
  private int okNumber;
  
  /** Number of KO found object. */
  private int koNumber;
  
  /** Number of doubtful found object. */
  private int doubtfulNumber;
  
  /**
   * Default constructor.
   */
  public ResultatStatEvaluationAppariement() {
    totalNumber = 0;
    okNumber = 0;
    koNumber = 0;
    doubtfulNumber = 0;
  }
  
  /**
   * Return total number of object.
   * @return int
   */
  public int getTotalNumber() {
    return totalNumber;
  }
  
  /**
   * @param n
   *      Total number of object to set.
   */
  public void setTotalNumber(int n) {
    totalNumber = n;
  }
  
  /**
   * Return number of OK found object.
   * @return int
   */
  public int getOkNumber() {
    return okNumber;
  }
  
  /**
   * @param n
   *      Number of OK found object to set.
   */
  public void setOkNumber(int n) {
    okNumber = n;
  }
  
  /**
   * Return number of KO found object.
   * @return int
   */
  public int getKoNumber() {
    return koNumber;
  }
  
  /**
   * @param n
   *      Number of KO found object to set.
   */
  public void setKoNumber(int n) {
    koNumber = n;
  }
  
  /**
   * Return number of doubtful found object.
   * @return int
   */
  public int getDoubtfulNumber() {
    return doubtfulNumber;
  }
  
  /**
   * @param n
   *      Number of doubtful found object to set.
   */
  public void setDoubtfulNumber(int n) {
    doubtfulNumber = n;
  }
  
}
