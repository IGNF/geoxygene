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
public class ResultatStatAppariement {
  
  /** Edges evaluation of the less detailed network. */
  ResultatStatEvaluationAppariement edgesEvaluationRef;
  
  /** Nodes evaluation of the less detailed network. */
  ResultatStatEvaluationAppariement nodesEvaluationRef;
  
  /** Edges evaluation of the comparison network. */
  ResultatStatEvaluationAppariement edgesEvaluationComp;
  
  /** Nodes evaluation of the comparison network. */
  ResultatStatEvaluationAppariement nodesEvaluationComp;
  
  /**
   * Default constructor.
   */
  public ResultatStatAppariement () {
    edgesEvaluationRef = new ResultatStatEvaluationAppariement();
    nodesEvaluationRef = new ResultatStatEvaluationAppariement();
    edgesEvaluationComp = new ResultatStatEvaluationAppariement();
    nodesEvaluationComp = new ResultatStatEvaluationAppariement();
  }
  
  /**
   * Return edges evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultatStatEvaluationAppariement getEdgesEvaluationRef() {
    return edgesEvaluationRef;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the less detailed network to set.
   */
  public void setEdgesEvaluationRef(ResultatStatEvaluationAppariement rsea) {
    edgesEvaluationRef = rsea;
  }
  
  /**
   * Return nodes evaluation of the less detailed network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultatStatEvaluationAppariement getNodesEvaluationRef() {
    return nodesEvaluationRef;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the less detailed network to set.
   */
  public void setNodesEvaluationRef(ResultatStatEvaluationAppariement rsea) {
    nodesEvaluationRef = rsea;
  }
  
  /**
   * Return edges evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultatStatEvaluationAppariement getEdgesEvaluationComp() {
    return edgesEvaluationComp;
  }
  
  /**
   * @param rsea
   *          Edges evaluation of the comparison network to set.
   */
  public void setEdgesEvaluationComp(ResultatStatEvaluationAppariement rsea) {
    edgesEvaluationComp = rsea;
  }
  
  /**
   * Return nodes evaluation of the comparison network.
   * @return ResultatStatEvaluationAppariement
   */
  public ResultatStatEvaluationAppariement getNodesEvaluationComp() {
    return nodesEvaluationComp;
  }
  
  /**
   * @param rsea
   *          Nodes evaluation of the comparison network to set.
   */
  public void setNodesEvaluationComp(ResultatStatEvaluationAppariement rsea) {
    nodesEvaluationComp = rsea;
  }

}
