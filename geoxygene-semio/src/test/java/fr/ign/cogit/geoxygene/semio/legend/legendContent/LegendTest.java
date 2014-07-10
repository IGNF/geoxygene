package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 */
public class LegendTest extends XMLTestCase {
    
    @Test
    public void testUnmarshallFileName() {
      // Load file
      Legend legend = Legend.unmarshall(LegendTest.class.getClassLoader().getResource("LegendExample.xml").getPath());
        
      // Check 
      Assert.assertTrue("Number of leaves : " + legend.getLeaves().size(), legend.getLeaves().size() == 21);
    }
    
    @Test
    public void testLevel2Legend()  {
      
    	LegendComposite racine = new LegendComposite();
		for (int i = 0; i < 4; i++) {
			LegendLeaf feuille = new LegendLeaf();
			feuille.setName("feuille " + i);
			feuille.setContainer(racine);
			// GraphicSymbol gs = new GraphicSymbol(layer, Viewport.getMETERS_PER_PIXEL());
			// feuille.setSymbol(gs);
			racine.getComponents().add(feuille);
		}
		Legend legende = new Legend();
		legende.setLegendRoot(racine);
		
		Assert.assertEquals(legende.getLegendRoot().getComponents().size(), 8);
        Assert.assertEquals(legende.getLegendRoot().getComponents().get(0).getName(), "feuille 0");
        Assert.assertEquals(legende.getLegendRoot().getComponents().get(1).getName(), "feuille 0");
        Assert.assertEquals(legende.getLegendRoot().getComponents().get(2).getName(), "feuille 1");
        Assert.assertEquals(legende.getLegendRoot().getComponents().get(3).getName(), "feuille 1");
    }
    
    @Test 
    public void testUnmarshallFile() {
      try {
        String path = Legend.class.getClassLoader().getResource("LegendExample.xml").getPath();
        Legend p = Legend.unmarshall(new File(path));
        
        assertEquals(p.getName(), "Legend");
        
        LegendComponent root = p.getLegendRoot();
        assertEquals(root.getName(), "LegendRoot");
        assertEquals(root.getComponents().size(), 7);
        
        LegendComponent themeTransport = root.getComponents().get(0);
        assertEquals(themeTransport.getName(), "Transport");
        assertEquals(themeTransport.getComponents().size(), 2);
        
        LegendComponent themeRailway = themeTransport.getComponents().get(1);
        assertEquals(themeRailway.getName(), "Railway");
        
        LegendComponent themeStation = themeRailway.getComponents().get(2);
        assertEquals(themeStation.getName(), "Station");
        
      } catch(Exception e) {
        e.printStackTrace();
        fail();
      }
    }
    
    @Test 
    public void testUnmarshallString() {
      StringBuffer xmlResult = new StringBuffer();
      xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
      xmlResult.append("<Legend>");
      xmlResult.append("<Name>Legend</Name>");
      xmlResult.append("<legendRoot>");
      xmlResult.append("<Name>LegendRoot</Name>");
      xmlResult.append("<Component>");
      xmlResult.append("<Name>Hydrography</Name>");
      xmlResult.append("<Component>");
      xmlResult.append("<Name>Water point</Name>");
      xmlResult.append("</Component>");
      xmlResult.append("<Component>");
      xmlResult.append("<Name>River</Name>");
      xmlResult.append("</Component>");
      xmlResult.append("<Component>");
      xmlResult.append("<Name>Sea</Name>");
      xmlResult.append("</Component>");
      xmlResult.append("</Component>");
      xmlResult.append("</legendRoot>");
      xmlResult.append("</Legend>");
      
      try {
        Legend legend = Legend.unmarshall(new StringReader(xmlResult.toString()));
        // System.out.println(((LegendComposite)legend.getLegendRoot().getComponents().get(0)).directLeaves().size());
        
        LegendComponent root = legend.getLegendRoot();
        LegendComponent hydro = root.getComponents().get(0);
        
        Assert.assertEquals(hydro.getComponents().size(), 3);
        Assert.assertEquals(hydro.getComponents().get(0).getName(), "Water point");
        Assert.assertEquals(hydro.getComponents().get(1).getName(), "River");
        Assert.assertEquals(hydro.getComponents().get(2).getName(), "Sea");
        
      } catch(Exception e) {
        e.printStackTrace();
        fail();
      }
      
      xmlResult = new StringBuffer();
      xmlResult.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
      xmlResult.append("<Legend>");
      xmlResult.append("<Name>Legend</Name>");
      xmlResult.append("<legendRoot>");
      xmlResult.append("<Name>LegendRoot</Name>");
      xmlResult.append("<Component>");
      xmlResult.append("<Name>Forest</Name>");
      xmlResult.append("</Component>");
      xmlResult.append("</legendRoot>");
      xmlResult.append("</Legend>");
      try {
        Legend legend = Legend.unmarshall(new StringReader(xmlResult.toString()));
      } catch(Exception e) {
        fail();
      }
    }
    
    @Test 
    public void testMarshall() {
      try {
	      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	      DocumentBuilder db;
	      db = dbf.newDocumentBuilder();
	      
	      Legend legend = new Legend();
	      legend.setName("Legend");
	      
	      LegendLeaf leaf = new LegendLeaf();
	      leaf.setName("Vegetation");
	      
	      LegendComposite root = new LegendComposite();
	      root.setName("LegendRoot");
	      root.addComponent(leaf);
	      
	      legend.setLegendRoot(root);
	      // legend.marshall("target/test-classes/LegendUltraSimple2.xml");
	      
	      /*File xml1 = new File("target/test-classes/LegendUltraSimple2.xml");
	      Document doc1 = db.parse(xml1);
	      assertEquals(doc1.getDocumentElement().getChildNodes().getLength(), 3);
	      
	      File xml2 = new File("target/test-classes/LegendUltraSimple.xml");
	      Document doc2 = db.parse(xml2);
	      assertEquals(doc2.getDocumentElement().getChildNodes().getLength(), 5);*/
	      
	      // assertXMLEqual(doc1, doc2);
      
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }

}
