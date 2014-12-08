package fr.ign.cogit.geoxygene.matching.beeri;

import junit.framework.Assert;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class CalculDistanceTest {
  
  @Test
  public void testDeuxiemePlusProcheVoisin() {
    
    DefaultFeature p1 = new DefaultFeature(new GM_Point(new DirectPosition(0, 0)));
    
    // ============================================================================
    // Aucun
    IPopulation<IFeature> popPoint = new Population<IFeature>("Point");
    double d = CalculDistance.deuxiemePlusProcheVoisin(p1, popPoint, 100.0);
    Assert.assertEquals(1.0, d, 0);
    
    // ============================================================================
    //  Un seul point 
    popPoint = new Population<IFeature>("Point");
    DefaultFeature p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    popPoint.add(p2);
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, popPoint, 100.0);
    Assert.assertEquals(1.0, d);
    
    // ============================================================================
    //  le plus proche = deuxième plus proche
    popPoint = new Population<IFeature>("Point");
    p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    DefaultFeature p3 = new DefaultFeature(new GM_Point(new DirectPosition(10, 0)));
    DefaultFeature p4 = new DefaultFeature(new GM_Point(new DirectPosition(10, 10)));
    popPoint.add(p2);
    popPoint.add(p3);
    popPoint.add(p4);
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, popPoint, 100.0);
    Assert.assertEquals(10.0, d, 0);
    
    // ============================================================================
    //  deuxièeme plus proche à 10
    popPoint = new Population<IFeature>("Point");
    p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    p3 = new DefaultFeature(new GM_Point(new DirectPosition(10, 0)));
    popPoint.add(p2);
    popPoint.add(p3);
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, popPoint, 100.0);
    Assert.assertEquals(10.0, d, 0);
 
    // ============================================================================
    //  deuxièeme plus proche à 14.142
    popPoint = new Population<IFeature>("Point");
    p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    p3 = new DefaultFeature(new GM_Point(new DirectPosition(20, 20)));
    p4 = new DefaultFeature(new GM_Point(new DirectPosition(10, 10)));
    popPoint.add(p2);
    popPoint.add(p3);
    popPoint.add(p4);
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, popPoint, 100.0);
    Assert.assertEquals(14.142, d, 0.001);
    
  }
  
  @Test
  public void testDeuxiemePlusProcheVoisinSachantPPV() {
    
    DefaultFeature p1 = new DefaultFeature(new GM_Point(new DirectPosition(0, 0)));
    
    // ============================================================================
    // Aucun
    IPopulation<IFeature> popPoint = new Population<IFeature>("Point");
    DefaultFeature p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    popPoint.add(p2);
    double d = CalculDistance.deuxiemePlusProcheVoisin(p1, p2, popPoint, 100.0);
    Assert.assertEquals(Double.MAX_VALUE, d, 0);
    
    // ============================================================================
    // 10
    popPoint = new Population<IFeature>("Point");
    p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    DefaultFeature p3 = new DefaultFeature(new GM_Point(new DirectPosition(10, 0)));
    popPoint.add(p2);
    popPoint.add(p3);
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, p2, popPoint, 100.0);
    Assert.assertEquals(10.0, d, 0);
    
    // ============================================================================
    // Aucun
    popPoint = new Population<IFeature>("Point");
    p2 = new DefaultFeature(new GM_Point(new DirectPosition(0, 10)));
    p3 = new DefaultFeature(new GM_Point(new DirectPosition(10, 0)));
    d = CalculDistance.deuxiemePlusProcheVoisin(p1, p2, popPoint, 100.0);
    Assert.assertEquals(Double.MAX_VALUE, d, 0);
    
  }

}
