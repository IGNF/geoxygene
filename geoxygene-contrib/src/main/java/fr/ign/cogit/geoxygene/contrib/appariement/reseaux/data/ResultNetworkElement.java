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
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Detailed results of a "network element".<br/>
 * Network element may be : nodes of the less detailed network, edges of the comparison network, ...
 * <ul>
 * <li> - Total network element number.</li>
 * <li> - Correct matching network element number.</li>
 * <li> - No matching network element number</li>
 * <li> - Doubtful matching network element number</li>
 * </ul>
 * 
 * @version 1.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "totalNetworkElementNumber",
    "correctMatchingNetworkElementNumber",
    "noMatchingNetworkElementNumber",
    "doubtfulNetworkElementNumber"
})
@XmlRootElement(name = "ResultNetworkElement")
public class ResultNetworkElement {
  
  /** Total network element number. */
  @XmlElement(name = "TotalNetworkElementNumber")
  private int totalNetworkElementNumber;
  
  /** Correct matching network element number. */ 
  @XmlElement(name = "CorrectMatchingNetworkElementNumber")
  private int correctMatchingNetworkElementNumber;
  
  /** No matching network element number. */
  @XmlElement(name = "NoMatchingNetworkElementNumber")
  private int noMatchingNetworkElementNumber;
  
  /** Doubtful matching network element number. */
  @XmlElement(name = "DoubtfulMatchingNumber")
  private int doubtfulNetworkElementNumber;
  
  /** network element. Like NodeRef, EdgeRef, ...*/
  @XmlAttribute(name = "NetworkElement", required = true)
  private String networkElement;
  
  /**
   * Default constructor.
   */
  public ResultNetworkElement() {
    totalNetworkElementNumber = 0;
    correctMatchingNetworkElementNumber = 0;
    noMatchingNetworkElementNumber = 0;
    doubtfulNetworkElementNumber = 0;
    networkElement = NetworkElementInterface.NONE;
  }
  
  /**
   * Default constructor.
   */
  public ResultNetworkElement(String networkElt) {
    totalNetworkElementNumber = 0;
    correctMatchingNetworkElementNumber = 0;
    noMatchingNetworkElementNumber = 0;
    doubtfulNetworkElementNumber = 0;
    networkElement = networkElt;
  }
  
  /**
   * Return total network element number.
   * @return int
   */
  public int getTotalNetworkElementNumber() {
    return totalNetworkElementNumber;
  }
  
  /**
   * @param n
   *      Total network element number to set.
   */
  public void setTotalNetworkElementNumber(int n) {
    totalNetworkElementNumber = n;
  }
  
  /**
   * Return correct matching network element number.
   * @return int
   */
  public int getCorrectMatchingNetworkElementNumber() {
    return correctMatchingNetworkElementNumber;
  }
  
  /**
   * @param n
   *      Correct matching network element number to set.
   */
  public void setCorrectMatchingNetworkElementNumber(int n) {
    correctMatchingNetworkElementNumber = n;
  }
  
  /**
   * Return no matching network element number.
   * @return int
   */
  public int getNoMatchingNetworkElementNumber() {
    return noMatchingNetworkElementNumber;
  }
  
  /**
   * @param n
   *      No matching network element number to set.
   */
  public void setNoMatchingNetworkElementNumber(int n) {
    noMatchingNetworkElementNumber = n;
  }
  
  /**
   * Return doubtful matching network element number.
   * @return int
   */
  public int getDoubtfulNetworkElementNumber() {
    return doubtfulNetworkElementNumber;
  }
  
  /**
   * @param n
   *      Doubtful matching network element number to set.
   */
  public void setDoubtfulNetworkElementNumber(int n) {
    doubtfulNetworkElementNumber = n;
  }
  
  /**
   * Return network element.
   * @return String
   */
  public String getNetworkElement() {
    return networkElement;
  }
  
  /**
   * @param element
   *      Network element to set.
   */
  public void setNetworkElement(String element) {
    networkElement = element;
  }
  
  /**
   * Returns a string representation of the statistic evaluation.
   * @return String
   */
  public String toString() {
    return "Stat " + networkElement + " = [" + "Total : " + totalNetworkElementNumber + ", "
        + "Appariés : " + correctMatchingNetworkElementNumber + ", "
        + "Non appariés : " + noMatchingNetworkElementNumber + ", "
        + "Incertains : " + doubtfulNetworkElementNumber
        + "]";
  }
  
}
