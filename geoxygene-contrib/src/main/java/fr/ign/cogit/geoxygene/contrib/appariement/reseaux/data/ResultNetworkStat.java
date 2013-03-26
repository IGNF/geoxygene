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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml.EvaluationAdapter;

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
    "edgesStatNetwork1",
    "nodesEvaluationRef",
    "edgesEvaluationComp",
    "nodesEvaluationComp"
})
@XmlRootElement(name = "ResultStatNetwork")
public class ResultNetworkStat {
  
  /** Edges evaluation of the less detailed network. */
  // @XmlJavaTypeAdapter(EvaluationAdapter.class)
  @XmlElement(name = "EdgesStatNetwork1")
  ResultNetworkStatElement edgesStatNetwork1;
  
  /** Nodes evaluation of the less detailed network. */
  @XmlElement(name = "NodesEvaluationRef", required = true)
  ResultNetworkStatElement nodesEvaluationRef;
  
  /** Edges evaluation of the comparison network. */
  @XmlElement(name = "EdgesEvaluationComp", required = true)
  ResultNetworkStatElement edgesEvaluationComp;
  
  /** Nodes evaluation of the comparison network. */
  @XmlElement(name = "NodesEvaluationComp", required = true)
  ResultNetworkStatElement nodesEvaluationComp;
  
  /**
   * Default constructor.
   */
  public ResultNetworkStat () {
    edgesStatNetwork1 = new ResultNetworkStatElement(ResultNetworkStatElementInterface.EDGES_LESS_DETAILED_NETWORK);
    nodesEvaluationRef = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NODES_LESS_DETAILED_NETWORK);
    edgesEvaluationComp = new ResultNetworkStatElement(ResultNetworkStatElementInterface.EDGES_COMPARISON_NETWORK);
    nodesEvaluationComp = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NODES_COMPARISON_NETWORK);
  }
  
  /**
   * Return edges evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getEdgesStatNetwork1() {
    return edgesStatNetwork1;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the less detailed network to set.
   */
  public void setEdgesStatNetwork1(ResultNetworkStatElement rsea) {
    edgesStatNetwork1 = rsea;
  }
  
  /**
   * Return nodes evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getNodesEvaluationRef() {
    return nodesEvaluationRef;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the less detailed network to set.
   */
  public void setNodesEvaluationRef(ResultNetworkStatElement rsea) {
    nodesEvaluationRef = rsea;
  }
  
  /**
   * Return edges evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getEdgesEvaluationComp() {
    return edgesEvaluationComp;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the comparison network to set.
   */
  public void setEdgesEvaluationComp(ResultNetworkStatElement rsea) {
    edgesEvaluationComp = rsea;
  }
  
  /**
   * Return nodes evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultNetworkStatElement getNodesEvaluationComp() {
    return nodesEvaluationComp;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the comparison network to set.
   */
  public void setNodesEvaluationComp(ResultNetworkStatElement rsea) {
    nodesEvaluationComp = rsea;
  }

}
