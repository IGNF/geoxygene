package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.DefaultCodec;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.SimpleGeomHypothesis;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class DefaultCodecTest {
  
  private EvidenceCodec<GeomHypothesis> codec;
  private SimpleGeomHypothesis h1;
  private SimpleGeomHypothesis h2;
  private SimpleGeomHypothesis h3;
  private SimpleGeomHypothesis h4;
  
  @Before
  public void setUp() {
    DefaultFeature p1 = new DefaultFeature(new GM_Point(new DirectPosition(10, 10)));
    p1.setId(10);
    h1 = new SimpleGeomHypothesis(p1);
    DefaultFeature p2 = new DefaultFeature(new GM_Point(new DirectPosition(-10, 10)));
    p2.setId(20);
    h2 = new SimpleGeomHypothesis(p2);
    DefaultFeature p3 = new DefaultFeature(new GM_Point(new DirectPosition(-10, -10)));
    p3.setId(30);
    h3 = new SimpleGeomHypothesis(p3);
    DefaultFeature p4 = new DefaultFeature(new GM_Point(new DirectPosition(10, -10)));
    p4.setId(40);
    h4 = new SimpleGeomHypothesis(p4);
    
    List<GeomHypothesis> hypotheses = new ArrayList<GeomHypothesis>();
    hypotheses.add(h1);
    hypotheses.add(h2);
    hypotheses.add(h3);
    hypotheses.add(h4);
    
    codec = new DefaultCodec<GeomHypothesis>(hypotheses);
  }
  
  @Test
  public void testEncode() {
    Assert.assertArrayEquals(new byte[] { 1 , 0 , 0 , 0 }, codec.encode(h1));
    Assert.assertArrayEquals(new byte[] { 0 , 1 , 0 , 0 }, codec.encode(h2));
    Assert.assertArrayEquals(new byte[] { 0 , 0 , 1 , 0 }, codec.encode(h3));
    Assert.assertArrayEquals(new byte[] { 0 , 0 , 0 , 1 }, codec.encode(h4));
  }
  
  @Test
  public void testDecode() {
    
    List<SimpleGeomHypothesis> listGeomHyp = new ArrayList<SimpleGeomHypothesis>();
    listGeomHyp.add(h1);
    Assert.assertEquals(codec.decode(new byte[] { 1 , 0 , 0 , 0 }), listGeomHyp);
    listGeomHyp.add(h2);
    Assert.assertEquals(codec.decode(new byte[] { 1 , 1 , 0 , 0 }), listGeomHyp);
    listGeomHyp.add(h4);
    Assert.assertEquals(codec.decode(new byte[] { 1 , 1 , 0 , 1 }), listGeomHyp);
    
    listGeomHyp = new ArrayList<SimpleGeomHypothesis>();
    listGeomHyp.add(h1);
    listGeomHyp.add(h2);
    listGeomHyp.add(h3);
    listGeomHyp.add(h4);
    Assert.assertEquals(codec.decode(new byte[] { 1 , 1 , 1 , 1 }), listGeomHyp);
    
    listGeomHyp = new ArrayList<SimpleGeomHypothesis>();
    listGeomHyp.add(h4);
    Assert.assertEquals(codec.decode(new byte[] { 0 , 0 , 0 , 1 }), listGeomHyp);
    
    listGeomHyp = new ArrayList<SimpleGeomHypothesis>();
    listGeomHyp.add(h3);
    listGeomHyp.add(h4);
    Assert.assertEquals(codec.decode(new byte[] { 0 , 0 , 1 , 1 }), listGeomHyp);
  }

}
