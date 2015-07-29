package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.LinearFunction;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;
import fr.ign.cogit.geoxygene.matching.dst.sources.punctual.EuclidianDist;
import fr.ign.cogit.geoxygene.matching.dst.sources.semantic.WuPalmerDistance;
import fr.ign.cogit.geoxygene.matching.dst.sources.text.LevenshteinDist;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class EscarpuTest {
  
  private final static Logger LOGGER = Logger.getLogger(EscarpuTest.class);
  
  @Test
  public void testAppriou1Critere() throws Exception {
    
    List<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
    LOGGER.info("====================================================================================");
    LOGGER.info("Appariement Esparpu - 1 critère (géométrique)");
    
    // Distance euclidienne
    EuclidianDist source = new EuclidianDist();
    
    // Fonction EstApparie
    Function1D[] listFEA = new Function1D[2];
    LinearFunction f11 = new LinearFunction(-0.9/800, 1);
    f11.setDomainOfFunction(0., 800., true, false);
    listFEA[0] = f11;
    ConstantFunction f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(800., 1500., true, true);
    listFEA[1] = f12;
    source.setMasseAppCi(listFEA);
    
    // Fonction NonApparie
    Function1D[] listFNA = new Function1D[3];
    ConstantFunction f21 = new ConstantFunction(0.);
    f21.setDomainOfFunction(0., 400., true, false);
    listFNA[0] = f21;
    LinearFunction f22 = new LinearFunction(0.8/400, -0.8);
    f22.setDomainOfFunction(400., 800., true, false);
    listFNA[1] = f22;
    ConstantFunction f23 = new ConstantFunction(0.8);
    f23.setDomainOfFunction(800., 1500., true, true);
    listFNA[2] = f23;
    source.setMasseAppPasCi(listFNA);
    
    // Fonction PrononcePas
    Function1D[] listFPP = new Function1D[3];
    LinearFunction f31 = new LinearFunction(0.45/400, 0.);
    f31.setDomainOfFunction(0., 400., true, false);
    listFPP[0] = f31;
    LinearFunction f32 = new LinearFunction(-0.35/400, 0.8);
    f32.setDomainOfFunction(400., 800., true, false);
    listFPP[1] = f32;
    ConstantFunction f33 = new ConstantFunction(0.1);
    f33.setDomainOfFunction(800., 1500., true, true);
    listFPP[2] = f33;
    source.setMasseIgnorance(listFPP);
    
    criteria.add(source);
    
    // ========================================================================
    
    IPopulation<IFeature> oronymePop = ShapefileReader.read("./data/pic-escarpu/oronyme_pic_escarpu.shp", "Oronyme", null, true);
    IPopulation<IFeature> pointReliefPop = ShapefileReader.read("./data/pic-escarpu/point_remarq_relief_pic_escarpu.shp", "Relief", null, true);
    
    Assert.assertEquals(1, pointReliefPop.size());
    Assert.assertEquals(3, oronymePop.size());
    
    boolean closed = true;
    GeoMatching matching = new GeoMatching();
    
    IFeature point = pointReliefPop.get(0);
    IPopulation<IFeature> candidat = oronymePop;
    
    EvidenceResult<GeomHypothesis> result = matching.runAppriou(criteria, point, candidat.getElements(),
        ChoiceType.PIGNISTIC, closed);
    
    Assert.assertEquals(0.6198158, result.getValue(), 0.0001);
    Assert.assertEquals(1, result.getHypothesis().size());
    Assert.assertEquals("l'escarpu ou pic de sesques", result.getHypothesis().get(0).getAttribute("NOM").toString().trim());
    
  }
  
  @Test
  public void testAppriou2Criteres() throws Exception {
    
    List<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
    LOGGER.info("====================================================================================");
    LOGGER.info("Appariement Esparpu - 2 critères (géométrique + toponyme)");
    
    // Distance euclidienne
    EuclidianDist source = new EuclidianDist();
    
    // Fonction EstApparie
    Function1D[] listFEA = new Function1D[2];
    LinearFunction f11 = new LinearFunction(-0.9/800, 1);
    f11.setDomainOfFunction(0., 800., true, false);
    listFEA[0] = f11;
    ConstantFunction f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(800., 1500., true, true);
    listFEA[1] = f12;
    source.setMasseAppCi(listFEA);
    
    // Fonction NonApparie
    Function1D[] listFNA = new Function1D[3];
    ConstantFunction f21 = new ConstantFunction(0.);
    f21.setDomainOfFunction(0., 400., true, false);
    listFNA[0] = f21;
    LinearFunction f22 = new LinearFunction(0.8/400, -0.8);
    f22.setDomainOfFunction(400., 800., true, false);
    listFNA[1] = f22;
    ConstantFunction f23 = new ConstantFunction(0.8);
    f23.setDomainOfFunction(800., 1500., true, true);
    listFNA[2] = f23;
    source.setMasseAppPasCi(listFNA);
    
    // Fonction PrononcePas
    Function1D[] listFPP = new Function1D[3];
    LinearFunction f31 = new LinearFunction(0.45/400, 0.);
    f31.setDomainOfFunction(0., 400., true, false);
    listFPP[0] = f31;
    LinearFunction f32 = new LinearFunction(-0.35/400, 0.8);
    f32.setDomainOfFunction(400., 800., true, false);
    listFPP[1] = f32;
    ConstantFunction f33 = new ConstantFunction(0.1);
    f33.setDomainOfFunction(800., 1500., true, true);
    listFPP[2] = f33;
    source.setMasseIgnorance(listFPP);
    
    criteria.add(source);
    
    // ==================================================================================== 
    
    LevenshteinDist levenshteinSource = new LevenshteinDist("toponyme", "NOM");
    double t = 0.7;

    // Fonction EstApparie
    listFEA = new Function1D[2];
    f11 = new LinearFunction(-0.9/t, 1);
    f11.setDomainOfFunction(0., t, true, false);
    listFEA[0] = f11;
    f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(t, 3, true, true);
    listFEA[1] = f12;
    levenshteinSource.setMasseAppCi(listFEA);
    
    // Fonction NonApparie
    listFNA = new Function1D[2];
    f22 = new LinearFunction(0.5/t, 0);
    f22.setDomainOfFunction(0., t, true, false);
    listFNA[0] = f22;
    f23 = new ConstantFunction(0.5);
    f23.setDomainOfFunction(t, 3, true, true);
    listFNA[1] = f23;
    levenshteinSource.setMasseAppPasCi(listFNA);
    
    // Fonction PrononcePas
    listFPP = new Function1D[2];
    f31 = new LinearFunction(0.4/t, 0.);
    f31.setDomainOfFunction(0., t, true, false);
    listFPP[0] = f31;
    ConstantFunction fL32 = new ConstantFunction(0.4);
    fL32.setDomainOfFunction(t, 3, true, false);
    listFPP[1] = fL32;
    levenshteinSource.setMasseIgnorance(listFPP);
    
    criteria.add(levenshteinSource);

    // ========================================================================
    
    IPopulation<IFeature> oronymePop = ShapefileReader.read("./data/pic-escarpu/oronyme_pic_escarpu.shp", "Oronyme", null, true);
    IPopulation<IFeature> pointReliefPop = ShapefileReader.read("./data/pic-escarpu/point_remarq_relief_pic_escarpu.shp", "Relief", null, true);
    
    Assert.assertEquals(1, pointReliefPop.size());
    Assert.assertEquals(3, oronymePop.size());
    
    boolean closed = false;
    GeoMatching matching = new GeoMatching();
    
    IFeature point = pointReliefPop.get(0);
    IPopulation<IFeature> candidat = oronymePop;
    
    EvidenceResult<GeomHypothesis> result = matching.runAppriou(criteria, point, candidat.getElements(),
        ChoiceType.PIGNISTIC, closed);
    
    Assert.assertEquals(0.621839, result.getValue(), 0.0001);
    Assert.assertEquals(1, result.getHypothesis().size());
    Assert.assertEquals("l'escarpu ou pic de sesques", result.getHypothesis().get(0).getAttribute("NOM").toString().trim());
    
  }
  
  @Test
  public void testAppriou3Criteres() throws Exception {
    
    List<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
    LOGGER.info("====================================================================================");
    LOGGER.info("Appariement Esparpu - 3 critères (géométrique + toponyme + wp)");
    
    // Distance euclidienne
    EuclidianDist source = new EuclidianDist();
    
    // Fonction EstApparie
    Function1D[] listFEA = new Function1D[2];
    LinearFunction f11 = new LinearFunction(-0.9/800, 1);
    f11.setDomainOfFunction(0., 800., true, false);
    listFEA[0] = f11;
    ConstantFunction f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(800., 1500., true, true);
    listFEA[1] = f12;
    source.setMasseAppCi(listFEA);
    
    // Fonction NonApparie
    Function1D[] listFNA = new Function1D[3];
    ConstantFunction f21 = new ConstantFunction(0.);
    f21.setDomainOfFunction(0., 400., true, false);
    listFNA[0] = f21;
    LinearFunction f22 = new LinearFunction(0.8/400, -0.8);
    f22.setDomainOfFunction(400., 800., true, false);
    listFNA[1] = f22;
    ConstantFunction f23 = new ConstantFunction(0.8);
    f23.setDomainOfFunction(800., 1500., true, true);
    listFNA[2] = f23;
    source.setMasseAppPasCi(listFNA);
    
    // Fonction PrononcePas
    Function1D[] listFPP = new Function1D[3];
    LinearFunction f31 = new LinearFunction(0.45/400, 0.);
    f31.setDomainOfFunction(0., 400., true, false);
    listFPP[0] = f31;
    LinearFunction f32 = new LinearFunction(-0.35/400, 0.8);
    f32.setDomainOfFunction(400., 800., true, false);
    listFPP[1] = f32;
    ConstantFunction f33 = new ConstantFunction(0.1);
    f33.setDomainOfFunction(800., 1500., true, true);
    listFPP[2] = f33;
    source.setMasseIgnorance(listFPP);
    
    criteria.add(source);
    
    // ==================================================================================== 
    
    LevenshteinDist levenshteinSource = new LevenshteinDist("toponyme", "NOM");
    double t = 0.7;

    // Fonction EstApparie
    listFEA = new Function1D[2];
    f11 = new LinearFunction(-0.9/t, 1);
    f11.setDomainOfFunction(0., t, true, false);
    listFEA[0] = f11;
    f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(t, 3, true, true);
    listFEA[1] = f12;
    levenshteinSource.setMasseAppCi(listFEA);
    
    // Fonction NonApparie
    listFNA = new Function1D[2];
    f22 = new LinearFunction(0.5/t, 0);
    f22.setDomainOfFunction(0., t, true, false);
    listFNA[0] = f22;
    f23 = new ConstantFunction(0.5);
    f23.setDomainOfFunction(t, 3, true, true);
    listFNA[1] = f23;
    levenshteinSource.setMasseAppPasCi(listFNA);
    
    // Fonction PrononcePas
    listFPP = new Function1D[2];
    f31 = new LinearFunction(0.4/t, 0.);
    f31.setDomainOfFunction(0., t, true, false);
    listFPP[0] = f31;
    ConstantFunction fL32 = new ConstantFunction(0.4);
    fL32.setDomainOfFunction(t, 3, true, false);
    listFPP[1] = fL32;
    levenshteinSource.setMasseIgnorance(listFPP);
    
    criteria.add(levenshteinSource);

    // ========================================================================
  
    WuPalmerDistance wuPalmerDistance = new WuPalmerDistance("./data/onto/FusionTopoCarto2.owl", "nature", "NATURE");
    t = 0.6;

    // Fonction EstApparie
    listFEA = new Function1D[2];
    f11 = new LinearFunction(-0.4 / t, 0.5);
    f11.setDomainOfFunction(0., t, true, false);
    listFEA[0] = f11;
    f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(t, 3, true, true);
    listFEA[1] = f12;
    wuPalmerDistance.setMasseAppCi(listFEA);

    // Fonction NonApparie
    listFNA = new Function1D[2];
    f22 = new LinearFunction(0.8 / t, 0);
    f22.setDomainOfFunction(0., t, true, false);
    listFNA[0] = f22;
    f23 = new ConstantFunction(0.8);
    f23.setDomainOfFunction(t, 3, true, true);
    listFNA[1] = f23;
    wuPalmerDistance.setMasseAppPasCi(listFNA);

    // Fonction PrononcePas
    listFPP = new Function1D[2];
    f31 = new LinearFunction(-0.4 / t, 0.5);
    f31.setDomainOfFunction(0., t, true, false);
    listFPP[0] = f31;
    fL32 = new ConstantFunction(0.1);
    fL32.setDomainOfFunction(t, 3, true, false);
    listFPP[1] = fL32;
    wuPalmerDistance.setMasseIgnorance(listFPP);

    criteria.add(wuPalmerDistance);
  
    //==================================================================================== 
    
    IPopulation<IFeature> oronymePop = ShapefileReader.read("./data/pic-escarpu/oronyme_pic_escarpu.shp", "Oronyme", null, true);
    IPopulation<IFeature> pointReliefPop = ShapefileReader.read("./data/pic-escarpu/point_remarq_relief_pic_escarpu.shp", "Relief", null, true);
    
    Assert.assertEquals(1, pointReliefPop.size());
    Assert.assertEquals(3, oronymePop.size());
    
    boolean closed = false;
    GeoMatching matching = new GeoMatching();
    
    IFeature point = pointReliefPop.get(0);
    IPopulation<IFeature> candidat = oronymePop;
    
    EvidenceResult<GeomHypothesis> result = matching.runAppriou(criteria, point, candidat.getElements(),
        ChoiceType.PIGNISTIC, closed);
    
    Assert.assertEquals(0.726618, result.getValue(), 0.0001);
    Assert.assertEquals(1, result.getHypothesis().size());
    Assert.assertEquals("l'escarpu ou pic de sesques", result.getHypothesis().get(0).getAttribute("NOM").toString().trim());
    
    System.out.println("----------------------------------------------------------------------");
  
  }

}
