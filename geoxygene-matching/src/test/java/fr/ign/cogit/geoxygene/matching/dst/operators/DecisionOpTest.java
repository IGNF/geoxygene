package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.DefaultCodec;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.SimpleGeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


public class DecisionOpTest {
  
  private static final Logger LOGGER = Logger.getLogger(DecisionOpTest.class);
  
  @Test
  public void test1() {
    
    List<Pair<byte[], Float>> masspotentials = new ArrayList<Pair<byte[], Float>>();
    float conflict;
    ChoiceType choice;
    EvidenceCodec<GeomHypothesis> decoder;
    boolean onsingles;
    
    // ------------------------------------------------------------------------------
    //    Initialize
    //
    byte[] C1    = new byte[] { 1, 0 };
    byte[] NC1   = new byte[] { 0, 1 };
    byte[] OME   = new byte[] { 1, 1 };
    
    masspotentials.add(new Pair<byte[], Float>(C1,  0.45f));
    masspotentials.add(new Pair<byte[], Float>(NC1, 0.45f));
    masspotentials.add(new Pair<byte[], Float>(OME, 0.10f));
    
    conflict = 10f;
    choice = ChoiceType.PIGNISTIC;
    onsingles = false;
    
    DefaultFeature p1 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    SimpleGeomHypothesis hypothese1 = new SimpleGeomHypothesis(p1);
    DefaultFeature p2 = new DefaultFeature(new GM_Point(new DirectPosition(10, 0)));
    SimpleGeomHypothesis hypothese2 = new SimpleGeomHypothesis(p2);
    DefaultFeature p3 = new DefaultFeature(new GM_Point(new DirectPosition(10, 10)));
    SimpleGeomHypothesis hypothese3 = new SimpleGeomHypothesis(p3);
    
    List<GeomHypothesis> hypotheses = new ArrayList<GeomHypothesis>();
    hypotheses.add(hypothese1);
    hypotheses.add(hypothese2);
    hypotheses.add(hypothese3);
    decoder = new DefaultCodec<GeomHypothesis>(hypotheses);
    
    // ------------------------------------------------------------------------------
    //     Combinaison des candidats
    LOGGER.info("----------------------------------------------------------------- ");
    DecisionOp<GeomHypothesis> op = new DecisionOp<GeomHypothesis>(masspotentials, conflict, choice, decoder, onsingles);
    EvidenceResult<GeomHypothesis> result = op.resolve();
    
    LOGGER.info("result = " + result);
    LOGGER.info("nb hyp final = " + result.getHypothesis().size());
    LOGGER.info("value = " + result.getValue());
    LOGGER.info("conflict = " + result.getConflict());
    LOGGER.info("with " + result.getHypothesis().size());
    
    Assert.assertTrue(true);
  }

}
