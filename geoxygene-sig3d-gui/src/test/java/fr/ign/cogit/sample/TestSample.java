package fr.ign.cogit.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.sig3d.sample.AddMenu;
import fr.ign.cogit.geoxygene.sig3d.sample.Calcul3DArea;
import fr.ign.cogit.geoxygene.sig3d.sample.DTMDisplay;
import fr.ign.cogit.geoxygene.sig3d.sample.DisplayData;
import fr.ign.cogit.geoxygene.sig3d.sample.RGE;
import fr.ign.cogit.geoxygene.sig3d.sample.Symbology;
import fr.ign.cogit.geoxygene.sig3d.sample.Toponym;
import junit.framework.TestCase;
;


public class TestSample extends TestCase {
	
	
	private static Logger log = LogManager.getLogger(TestSample.class);

	// ---------------------------------- PREPROCESS ----------------------------------

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// ------------------------------------ TESTS -------------------------------------


	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class AddMenu
	// --------------------------------------------------------------------------------
	public void testSample1() {
		
		log.info("Test for sample class AddMenu");

		new AddMenu();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class Calcul3DArea
	// --------------------------------------------------------------------------------
	public void testSample2() {
		
		log.info("Test for sample class Calcul3DArea");

	 new Calcul3DArea();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class DisplayData
	// --------------------------------------------------------------------------------
	public void testSample3() {
		
		log.info("Test for sample class DisplayData");
		new DisplayData();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class DTMDisplay
	// --------------------------------------------------------------------------------
	public void testSample4() {
		
		log.info("Test for sample class DTMDisplay");

		@SuppressWarnings("unused")
		DTMDisplay dtmd = new DTMDisplay();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class DTMDisplay
	// --------------------------------------------------------------------------------
	public void testSample5() {
		
		log.info("Test for sample class DTMDisplay");

		@SuppressWarnings("unused")
		DTMDisplay dtmd = new DTMDisplay();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class RGE
	// --------------------------------------------------------------------------------
	public void testSample6() {
		
		log.info("Test for sample class RGE");
		new RGE();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class Symbology
	// --------------------------------------------------------------------------------
	public void testSample7() {
		
		log.info("Test for sample class Symbology");

		new Symbology();

	}
	
	@Test
	// --------------------------------------------------------------------------------
	// Test for sample class Toponym
	// --------------------------------------------------------------------------------
	public void testSample8() {
		
		log.info("Test for sample class Toponym");

		new Toponym();

	}

}

