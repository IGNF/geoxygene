/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * @author Julien Perret
 */
public class DempsterOp implements CombinationOp {
  Logger logger = Logger.getLogger(DempsterOp.class);
  public Float conflict;
  public boolean worldclosed = true;

  public DempsterOp(boolean isworldclosed) {
    this.worldclosed = isworldclosed;
  }

  @Override
  public List<Pair<byte[], Float>> combine(List<List<Pair<byte[], Float>>> masspotentials) {
//    logger.info("Combination using dempster operator");
    SmetsOp sop = new SmetsOp(this.worldclosed);
    List<Pair<byte[], Float>> mresult = sop.combine(masspotentials);
    // On force l'ensemble vide à 0 = on le supprime du noyau
    if (!mresult.isEmpty()) {
      if (Utils.isEmpty(mresult.get(0).getFirst())) {
        logger.info("Estimated conflict between mass information sources : "
            + mresult.get(0).getSecond());
        this.conflict = mresult.get(0).getSecond();
        mresult.remove(0);
      }
    }
    if (mresult.isEmpty()) {
      logger.info("TOTAL CONFLICT BETWEEN SOURCES, THERE IS NO SOLUTION");
      return null;
    }
    float sum = 0.0f;
    for (Pair<byte[], Float> hyp : mresult) {
      sum += hyp.getSecond();
    }
    if (logger.isDebugEnabled()) {
      if (sum < 0 || sum == 0) {
        logger.debug("Warning : normalization by " + sum
            + " may result in an invalid mass potential");
      }
    }
    for (Pair<byte[], Float> hyp : mresult) {
      hyp.setSecond(hyp.getSecond() / sum);
    }
//    if (logger.isDebugEnabled()) {
//      logger.debug("---Result of all masses combination using Dempster operator---");
//      for (Pair<byte[], Float> hyp : mresult) {
//        logger.debug("Value is " + hyp.getSecond() + " for combination "
//            + Arrays.toString(hyp.getFirst()));
//      }
//    }
    return mresult;
  }

  @Override
  public Float getConflict() {
    return this.conflict;
  }
}
