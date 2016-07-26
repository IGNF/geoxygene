package fr.ign.cogit;

import fr.ign.cogit.calculation.TestBooleanOperators;
import fr.ign.cogit.sample.TestSample;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

//--------------------------------------------------------------------------------
// Class containing main function to launch test suite
// Date : 28/01/2014
// Yann MENEROUX (yann.meneroux@ign.fr)
//--------------------------------------------------------------------------------

public class MainTest {

	public static Test suite() {

		TestSuite suite = new TestSuite("All 3D tests");

		suite.addTestSuite(TestBooleanOperators.class);
		suite.addTestSuite(TestSample.class);

		return suite;

	}

	public static void main(final String[] args) {

		TestRunner.run(suite());

	}

}