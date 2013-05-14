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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
// import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *  Statistics results. Like : <ul>
 *  <li>- Edges evaluation of the less detailed network </li>
 *  <li>- Nodes evaluation of the less detailed network</li>
 *  <li>- Edges evaluation of the comparison network</li>
 *  <li>- Nodes evaluation of the comparison network</li>
 *  </ul>
 * 
 * @author M.-D. Van Damme
 * @version 1.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "statsEdgesOfNetwork1",
    "statsNodesOfNetwork1",
    "statsEdgesOfNetwork2",
    "statsNodesOfNetwork2"
})
@XmlRootElement(name = "ResultStatNetwork")
public class ResultNetworkStat {
  
  /** Edges evaluation of the less detailed network. */
  @XmlElement(name = "StatsEdgesOfNetwork1")
  ResultNetworkStatElement statsEdgesOfNetwork1;
  
  /** Nodes evaluation of the less detailed network. */
  @XmlElement(name = "StatsNodesOfNetwork1", required = true)
  ResultNetworkStatElement statsNodesOfNetwork1;
  
  /** Edges evaluation of the comparison network. */
  @XmlElement(name = "StatsEdgesOfNetwork2", required = true)
  ResultNetworkStatElement statsEdgesOfNetwork2;
  
  /** Nodes evaluation of the comparison network. */
  @XmlElement(name = "StatsNodesOfNetwork2", required = true)
  ResultNetworkStatElement statsNodesOfNetwork2;
  
  /**
   * Default constructor.
   */
  public ResultNetworkStat () {
    statsEdgesOfNetwork1 = new ResultNetworkStatElement(ResultNetworkStatElementInterface.EDGES_OF_NETWORK_1);
    statsNodesOfNetwork1 = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NODES_OF_NETWORK_1);
    statsEdgesOfNetwork2 = new ResultNetworkStatElement(ResultNetworkStatElementInterface.EDGES_OF_NETWORK_2);
    statsNodesOfNetwork2 = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NODES_OF_NETWORK_2);
  }
  
  /**
   * Return edges evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getStatsEdgesOfNetwork1() {
    return statsEdgesOfNetwork1;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the less detailed network to set.
   */
  public void setStatsEdgesOfNetwork1(ResultNetworkStatElement rsea) {
    statsEdgesOfNetwork1 = rsea;
  }
  
  /**
   * Return nodes evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getStatsNodesOfNetwork1() {
    return statsNodesOfNetwork1;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the less detailed network to set.
   */
  public void setStatsNodesOfNetwork1(ResultNetworkStatElement rsea) {
    statsNodesOfNetwork1 = rsea;
  }
  
  /**
   * Return edges evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getStatsEdgesOfNetwork2() {
    return statsEdgesOfNetwork2;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the comparison network to set.
   */
  public void setStatsEdgesOfNetwork2(ResultNetworkStatElement rsea) {
    statsEdgesOfNetwork2 = rsea;
  }
  
  /**
   * Return nodes evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getStatsNodesOfNetwork2() {
    return statsNodesOfNetwork2;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the comparison network to set.
   */
  public void setStatsNodesOfNetwork2(ResultNetworkStatElement rsea) {
    statsNodesOfNetwork2 = rsea;
  }

}
