package fr.ign.cogit.geoxygene.matching.dst.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;
import org.w3c.dom.Document;

import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.LinearFunction;
import fr.ign.cogit.geoxygene.matching.dst.sources.punctual.EuclidianDist;
import fr.ign.cogit.geoxygene.matching.dst.sources.text.LevenshteinDist;


public class XmlSourceParser extends XMLTestCase {
  
  @Test
  public void testMarshallEuclidianDistance() {
    
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
    
    source.marshall();
    source.marshall("target/test-classes/OneSourceBuild.xml");
    
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      
      File xml1 = new File(XmlSourceParser.class.getClassLoader().getResource("xml/sources/EuclidianDist.xml").getPath());
      Document doc1 = db.parse(xml1);
      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 7);
      
      File xml2 = new File("target/test-classes/OneSourceBuild.xml");
      Document doc2 = db.parse(xml2);
      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 7);
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  
  }
  
  @Test
  public void testMarshallLevensteinDistance() {
    LevenshteinDist levenshteinSource = new LevenshteinDist();
    double t = 0.7;
    
    Function1D[] list = new Function1D[2];
    LinearFunction f11 = new LinearFunction(-0.9/t, 1);
    f11.setDomainOfFunction(0., t, true, false);
    list[0] = f11;
    ConstantFunction f12 = new ConstantFunction(0.1);
    f12.setDomainOfFunction(t, 3, true, true);
    list[1] = f12;
    levenshteinSource.setMasseAppCi(list);
    
    // Fonction NonApparie
    list = new Function1D[2];
    LinearFunction f22 = new LinearFunction(0.5/t, 0);
    f22.setDomainOfFunction(0., t, true, false);
    list[0] = f22;
    ConstantFunction f23 = new ConstantFunction(0.5);
    f23.setDomainOfFunction(t, 3, true, true);
    list[1] = f23;
    levenshteinSource.setMasseAppPasCi(list);
    
    // Fonction PrononcePas
    list = new Function1D[2];
    LinearFunction f31 = new LinearFunction(0.4/t, 0.);
    f31.setDomainOfFunction(0., t, true, false);
    list[0] = f31;
    ConstantFunction fL32 = new ConstantFunction(0.4);
    fL32.setDomainOfFunction(t, 3, true, false);
    list[1] = fL32;
    levenshteinSource.setMasseIgnorance(list);
    
    levenshteinSource.marshall();
    levenshteinSource.marshall("target/test-classes/OneSourceBuild2.xml");
    
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      
      File xml1 = new File(XmlSourceParser.class.getClassLoader().getResource("xml/sources/LevenshteinDist.xml").getPath());
      Document doc1 = db.parse(xml1);
      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 7);
      
      File xml2 = new File("target/test-classes/OneSourceBuild2.xml");
      Document doc2 = db.parse(xml2);
      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 7);
    
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
