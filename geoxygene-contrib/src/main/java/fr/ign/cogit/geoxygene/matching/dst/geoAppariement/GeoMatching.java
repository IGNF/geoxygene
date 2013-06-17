/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.geoAppariement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.DefaultCodec;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Hypothesis;
import fr.ign.cogit.geoxygene.matching.dst.evidence.MatchingProcess;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.util.Combinations;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * @author Bertrand Dumenieu
 * 
 */
public class GeoMatching {

  List<Hypothesis> candidates;
  private static GeoMatching singleton;
  private IFeature reference;

  public GeoMatching() {
  }

  public EvidenceResult run(Collection<Source<Hypothesis>> criteria, IFeature reference,
      List<IFeature> candidates) throws Exception {
    
      //Création des hypothèses d'appariement.
      LinkedList<List<IFeature>> combinations = Combinations.enumerate(candidates);
      List<Hypothesis> hypotheses = new ArrayList<Hypothesis>();
      for (List<IFeature> l : combinations) {
        
        if (l.size() == 1) {
          hypotheses.add(new SimpleGeomHypothesis(l.get(0)));
        } else if (l.size() > 1) {
  
          IFeature[] featarray = new IFeature[l.size()];
          hypotheses.add(new ComplexGeomHypothesis(l.toArray(featarray)));
        }
      }
      MatchingProcess matchingProcess = new MatchingProcess(criteria, hypotheses, new DefaultCodec(hypotheses), false);
      List<Pair<byte[], Float>> result = matchingProcess.combinationProcess();
      return null;
      
  }
//    AppriouMapper mapper = new AppriouMapper();
//    List<List<Pair<byte[], Float>>> mapped = new ArrayList<List<Pair<byte[], Float>>>();
//    for (Hypothesis candidate : candidates) {
//      // Création des hypothèses focalisées A et !A. L'Hypothèse inconnue étant
//      // Teta, elle sera directement évaluée par les sources focalisées en tant
//      // qu'ensemble de 2Teta.
//      this.reference = reference;
//      List<Hypothesis> hyps = new ArrayList<Hypothesis>();
//      Hypothesis hyp = new AppriouHyp(AppriouType.A, candidate);
//      Hypothesis nothyp = new AppriouHyp(AppriouType.NOTA, candidate);
//      hyps.add(hyp);
//      hyps.add(nothyp);
//      DefaultCodec codec = new DefaultCodec(hyps);
//      MatchingProcess mp = new MatchingProcess(focalisedSources, hyps, codec,
//          false);
//      List<Pair<byte[], Float>> result = mp.combinationProcess();
//      List<Pair<byte[], Float>> localresult = mapper.mapFocalisedToGlobal(
//          candidates.indexOf(candidate), candidates.size(), result);
//      CombinationAlgos.sortKernel(localresult);
//      CombinationAlgos.deleteDoubles(localresult);
//      mapped.add(localresult);
//    }
//    DempsterOp op = new DempsterOp(true);
//    List<Pair<byte[], Float>> result = op.combine(mapped);
//    DecisionOp decisionOp = new DecisionOp(result, op.conflict,
//        ChoiceType.CREDIBILITY, new DefaultCodec(candidates), true);
//    return decisionOp.resolve();
  //}

  public static GeoMatching getInstance() {
    if (singleton == null) {
      singleton = new GeoMatching();
    }
    return singleton;
  }

  public IFeature getReference() {
    return this.reference;
  }

}
