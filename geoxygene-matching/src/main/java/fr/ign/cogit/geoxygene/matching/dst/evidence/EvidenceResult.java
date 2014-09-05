/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.util.ArrayList;
import java.util.List;

/**
 * Evidence matching result. It can be an hypothesis of the union of hypotheses.
 * <p>
 * Résultat d'un appariement. Il s'agit éventuellement d'une union d'hypothèses.
 * @author Bertrand Dumenieu
 */
public class EvidenceResult<H extends Hypothesis> {

  private ChoiceType type;
  private float conflict;
  private List<H> finalhyps;
  private float finalvalue;

  /**
   * Creates a new result.
   * @param decision
   *        the decision type used
   * @param conflict
   *        the conflict
   * @param hypothesis
   *        the list of hypotheses
   * @param value
   *        the value
   */
  public EvidenceResult(ChoiceType decision, float conflict, List<H> hypothesis,
      float value) {
    this.type = decision;
    this.conflict = conflict;
    this.finalhyps = new ArrayList<H>(hypothesis);
    this.finalvalue = value;
  }

  /**
   * @return the type of decision used for this result.
   */
  public ChoiceType getDecisionType() {
    return this.type;
  }

  /**
   * @return the conflict.
   */
  public float getConflict() {
    return this.conflict;
  }

  /**
   * @return the hypotheses retained for this result.
   */
  public List<H> getHypothesis() {
    return this.finalhyps;
  }

  /**
   * @return the evaluation of this result
   */
  public float getValue() {
    return finalvalue;
  }

  @Override
  public String toString() {
    String result = "";
    for (Hypothesis h : this.finalhyps) {
      result += h;
    }
    return result;
  }
}
