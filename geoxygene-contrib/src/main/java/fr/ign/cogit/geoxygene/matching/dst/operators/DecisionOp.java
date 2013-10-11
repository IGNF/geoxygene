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

package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Hypothesis;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * @author Bertrand Dumenieu
 */
public class DecisionOp<H extends Hypothesis> {
  List<Pair<byte[], Float>> masspotentials;
  ChoiceType choice;
  boolean isChoiceOnSinglesOnly = true;
  private Logger logger = Logger.getLogger(DecisionOp.class);
  private float conflict = 0f;
  private EvidenceCodec<H> decoder;

  /**
   * DecisionOp constructor.
   * @param masspotentials
   *        masses
   * @param conflict
   *        conflict
   * @param choice
   *        type of choice
   * @param decoder
   *        a codec to decode the hypothesis
   * @param onsingles
   *        do we only want to choose between singles?
   */
  public DecisionOp(List<Pair<byte[], Float>> masspotentials, float conflict, ChoiceType choice,
      EvidenceCodec<H> decoder, boolean onsingles) {
    this.masspotentials = masspotentials;
    this.choice = choice;
    this.isChoiceOnSinglesOnly = onsingles;
    this.conflict = conflict;
    this.decoder = decoder;

  }

  /**
   * Make a decision and return it.
   * @return an evidence result.
   */
  public EvidenceResult<H> resolve() {
    List<H> decoded = null;
    switch (choice) {
      case CREDIBILITY:
        Pair<byte[], Float> maxbel = this.maxBelief(masspotentials, this.isChoiceOnSinglesOnly);
        logger.info("MAX CREDIBLE HYPOTHESIS : " + maxbel.getSecond() + " for "
            + Arrays.toString(maxbel.getFirst()));
        decoded = decoder.decode(maxbel.getFirst());
        return new EvidenceResult<H>(choice, conflict, decoded, maxbel.getSecond());
      case PLAUSIBILITY:
        Pair<byte[], Float> maxpl = this.maxPlausible(masspotentials, this.isChoiceOnSinglesOnly);
        logger.info("MAX PLAUSIBLE HYPOTHESIS : " + maxpl.getSecond() + " for "
            + Arrays.toString(maxpl.getFirst()));
        decoded = decoder.decode(maxpl.getFirst());
        return new EvidenceResult<H>(choice, conflict, decoded, maxpl.getSecond());

      case PIGNISTIC:
        Pair<byte[], Float> maxpig = this.maxPignistic(masspotentials, this.isChoiceOnSinglesOnly);
        logger.info("MAX PIGNISTIC HYPOTHESIS : " + maxpig.getSecond() + " for "
            + Arrays.toString(maxpig.getFirst()));
        decoded = decoder.decode(maxpig.getFirst());
        return new EvidenceResult<H>(choice, conflict, decoded, maxpig.getSecond());
    }
    return null;
  }

  /**
   * @param hyp
   * @param masspotentials
   * @return
   */
  private float credibility(byte[] hyp, List<Pair<byte[], Float>> masspotentials) {
    float credibility = 0.0f;
    for (Pair<byte[], Float> value : masspotentials) {
      if (!Utils.isEmpty(value.getFirst())
          && Arrays.equals(Utils.byteUnion(hyp, value.getFirst()), hyp)) {
        credibility += value.getSecond();
      }
    }
    logger.debug("Credibility : " + credibility + " for hypothesis" + Arrays.toString(hyp));
    return credibility;
  }

  /**
   * @param hyp
   * @param masspotentials
   * @return
   */
  private float plausibility(byte[] hyp, List<Pair<byte[], Float>> masspotentials) {
    float plausibility = 0.0f;
    for (Pair<byte[], Float> value : masspotentials) {
      if (!Utils.isEmpty(Utils.byteIntersection(hyp, value.getFirst()))) {
        plausibility += value.getSecond();
      }
    }
    logger.debug("Plausibility : " + plausibility + " for hypothesis" + Arrays.toString(hyp));
    return plausibility;
  }

  /**
   * @param hyp
   * @param masspotentials
   * @return
   */
  private float pignistic(byte[] hyp, List<Pair<byte[], Float>> masspotentials) {
    float pignistic = 0.0f;
    float mvoid = masspotentials.get(0).getSecond();
    if (!Utils.isEmpty(masspotentials.get(0).getFirst())) {
      mvoid = 0.0f;
    }
    for (Pair<byte[], Float> value : masspotentials) {
      if (!Utils.isEmpty(Utils.byteIntersection(hyp, value.getFirst()))) {
        int cardinal = 0;
        for (byte b : value.getFirst()) {
          if (b == (byte) 1) {
            cardinal++;
          }
        }
        pignistic += value.getSecond() / (cardinal * (1 - mvoid));
      }
    }
    logger.debug("Pignistic value : " + pignistic + " for hypothesis" + Arrays.toString(hyp));
    return pignistic;
  }

  /**
   * @param masspotentials
   * @param onsingles
   * @return
   */
  private Pair<byte[], Float> maxBelief(List<Pair<byte[], Float>> masspotentials, boolean onsingles) {
    float maxbelief = 0.0f;
    byte[] maxcredible = null;
    for (Pair<byte[], Float> hyp : masspotentials) {
      if (onsingles) {
        int cardinal = 0;
        for (byte b : hyp.getFirst()) {
          if (b == (byte) 1) {
            cardinal++;
          }
        }
        if (cardinal > 1)
          continue;
      }
      float bel = this.credibility(hyp.getFirst(), masspotentials);
      if (bel > maxbelief) {
        maxbelief = bel;
        maxcredible = hyp.getFirst();
      }
    }
    return new Pair<byte[], Float>(maxcredible, maxbelief);
  }

  /**
   * @param masspotentials
   * @param onsingles
   * @return
   */
  private Pair<byte[], Float> maxPlausible(List<Pair<byte[], Float>> masspotentials,
      boolean onsingles) {
    float maxpl = 0.0f;
    byte[] maxplausible = null;
    for (Pair<byte[], Float> hyp : masspotentials) {
      if (onsingles) {
        int cardinal = 0;
        for (byte b : hyp.getFirst()) {
          if (b == (byte) 1) {
            cardinal++;
          }
        }
        if (cardinal > 1)
          continue;
      }
      float bel = this.plausibility(hyp.getFirst(), masspotentials);
      if (bel > maxpl) {
        maxpl = bel;
        maxplausible = hyp.getFirst();
      }
    }
    return new Pair<byte[], Float>(maxplausible, maxpl);
  }

  /**
   * @param masspotentials
   * @param onsingles
   * @return
   */
  private Pair<byte[], Float> maxPignistic(List<Pair<byte[], Float>> masspotentials,
      boolean onsingles) {
    float maxpig = 0.0f;
    byte[] maxpignistic = null;
    for (Pair<byte[], Float> hyp : masspotentials) {
      if (onsingles) {
        int cardinal = 0;
        for (byte b : hyp.getFirst()) {
          if (b == (byte) 1) {
            cardinal++;
          }
        }
        if (cardinal > 1)
          continue;
      }
      float bel = this.pignistic(hyp.getFirst(), masspotentials);
      if (bel > maxpig) {
        maxpig = bel;
        maxpignistic = hyp.getFirst();
      }
    }
    return new Pair<byte[], Float>(maxpignistic, maxpig);
  }
}
