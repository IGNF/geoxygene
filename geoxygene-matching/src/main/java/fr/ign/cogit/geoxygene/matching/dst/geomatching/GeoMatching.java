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

package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.MatchingProcess;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.DefaultCodec;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.DecisionOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.DempsterOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.SmetsOp;
import fr.ign.cogit.geoxygene.matching.dst.util.Combinations;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * @author Bertrand Dumenieu
 */
public class GeoMatching {
	
  private final static Logger LOGGER = Logger.getLogger(GeoMatching.class);
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");

  // List<Hypothesis> candidates;

  // private static GeoMatching singleton;
  // private IFeature reference;

  public GeoMatching() {
  }

  /**
   * 
   * @param criteria
   * @param reference
   * @param candidates
   * @param choice
   * @param closed
   * @return
   * @throws Exception
   */
  public EvidenceResult<GeomHypothesis> run(Collection<Source<IFeature, GeomHypothesis>> criteria,
      IFeature reference, List<IFeature> candidates, ChoiceType choice, boolean closed)
      throws Exception {
    
    // Création des hypothèses d'appariement.
    LOGGER.debug(candidates.size() + " candidates");
    LinkedList<List<IFeature>> combinations = Combinations.enumerate(candidates);
    LOGGER.debug(combinations.size() + " hypotheses");
    
    // 
    List<GeomHypothesis> hypotheses = new ArrayList<GeomHypothesis>();
    for (List<IFeature> l : combinations) {
      if (l.size() == 1) {
        hypotheses.add(new SimpleGeomHypothesis(l.get(0)));
      } else {
        if (l.size() > 1) {
          IFeature[] featarray = new IFeature[l.size()];
          hypotheses.add(new ComplexGeomHypothesis(l.toArray(featarray)));
        }
      }
    }
    
    EvidenceCodec<GeomHypothesis> codec = new DefaultCodec<GeomHypothesis>(hypotheses);
    MatchingProcess<IFeature, GeomHypothesis> matchingProcess = new MatchingProcess<IFeature, GeomHypothesis>(
        criteria, hypotheses, codec, closed);
    
    // Get Result
    List<Pair<byte[], Float>> result = matchingProcess.combinationProcess(reference);
    CombinationAlgos.sortKernel(result);
    CombinationAlgos.deleteDoubles(result);
    
    // Décision 
    DecisionOp<GeomHypothesis> decisionOp = new DecisionOp<GeomHypothesis>(result, 0f, choice,
        codec, false);
    return decisionOp.resolve();
  }

  /**
   * 
   * @param criteria
   * @param reference
   * @param candidates
   * @param weights
   * @param choice
   * @param closed
   * @return
   * @throws Exception
   */
  public EvidenceResult<GeomHypothesis> runAppriou(List<Source<IFeature, GeomHypothesis>> criteria,
      IFeature reference, List<IFeature> candidates, ChoiceType choice, boolean closed)
      throws Exception {
    
    DST_LOGGER.info("Appariement du toponyme " + reference.getAttribute("toponyme"));
    
    // Création des hypothèses d'appariement.
    LOGGER.debug(candidates.size() + " candidates");
    LinkedList<List<IFeature>> combinations = Combinations.enumerate(candidates);
    LOGGER.debug(combinations.size() + " hypotheses");
    
    // 
    List<GeomHypothesis> hypotheses = new ArrayList<GeomHypothesis>();
    for (List<IFeature> l : combinations) {
      if (l.size() == 1) {
        // System.out.println(l.get(0).getFeatureType().getFeatureAttributes().size());
        hypotheses.add(new SimpleGeomHypothesis(l.get(0)));
      } else
        if (l.size() > 1) {
          IFeature[] featarray = new IFeature[l.size()];
          hypotheses.add(new ComplexGeomHypothesis(l.toArray(featarray)));
        }
    }

    List<List<Pair<byte[], Float>>> beliefs = new ArrayList<List<Pair<byte[], Float>>>();
    DefaultCodec<GeomHypothesis> codec = new DefaultCodec<GeomHypothesis>(hypotheses);
    for (GeomHypothesis candidate : hypotheses) {
      for (int j = 0; j < criteria.size(); j++) {
        Source<IFeature, GeomHypothesis> source = criteria.get(j);
        DST_LOGGER.info("   " + source.getName());
        double[] mij = source.evaluate(reference, candidate);
        DST_LOGGER.info("        [" + mij[0] + ", " + mij[1] + ", " + mij[2] + "]");
        List<Pair<byte[], Float>> kernel = new ArrayList<Pair<byte[], Float>>();
        byte[] code = codec.encode(candidate);
        byte[] codeComplement = new byte[hypotheses.size()];
        Arrays.fill(codeComplement, (byte)1);
        for (int i = 0; i < code.length; i++) {
          codeComplement[i] -= code[i];
        }
        byte[] codeUnknown = new byte[hypotheses.size()];
        Arrays.fill(codeUnknown, (byte)1);
        kernel.add(new Pair<byte[], Float>(code, new Float(mij[0])));
        kernel.add(new Pair<byte[], Float>(codeComplement, new Float(mij[1])));
        kernel.add(new Pair<byte[], Float>(codeUnknown, new Float(mij[2])));
        beliefs.add(kernel);
      }
    }
    
    CombinationOp op = closed ? new DempsterOp(closed) : new SmetsOp(closed);
    List<Pair<byte[], Float>> result = op.combine(beliefs);
    
    DecisionOp<GeomHypothesis> decisionOp = new DecisionOp<GeomHypothesis>(result, op.getConflict(),
        choice, codec, true);
    return decisionOp.resolve();

  }
}
