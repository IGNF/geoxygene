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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import no.priv.garshol.duke.comparators.JaroWinkler;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;
import fr.ign.cogit.geoxygene.matching.dst.sources.punctual.EuclidianDist;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Test l'appariement d'un jeu de point suivant plusieurs critères.
 * 
 * @author MDVan-Damme
 */
public class GeoMatchingPointTest {
	
	private static final Logger LOGGER = Logger.getLogger(GeoMatchingPointTest.class);
	
	private List<IFeature> candidates;
	private DefaultFeature reference;
	
	@Before
	public void setUp() {
		// candidates = new Population<IFeature>("Point");
		candidates = new ArrayList<IFeature>();
	    
		DefaultFeature p1 = new DefaultFeature(new GM_Point(new DirectPosition(15, 15)));
		DefaultFeature p2 = new DefaultFeature(new GM_Point(new DirectPosition(10, -10)));
	    DefaultFeature p3 = new DefaultFeature(new GM_Point(new DirectPosition(-12, 10)));
		
	    FeatureType pointFeatureType = new FeatureType();
	    pointFeatureType.setTypeName("nature");
	    pointFeatureType.setGeometryType(IPoint.class);
	    
	    AttributeType idTextNature = new AttributeType("nature", "String");
	    pointFeatureType.addFeatureAttribute(idTextNature);
	    
	    // Création d'un schéma associé au featureType
	    SchemaDefaultFeature schema = new SchemaDefaultFeature();
	    schema.setFeatureType(pointFeatureType);
	    pointFeatureType.setSchema(schema);
	    
	    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
	    attLookup.put(new Integer(0), new String[] { "nature", "nature" });
	    schema.setAttLookup(attLookup);
	    
	    p1.setFeatureType(pointFeatureType);
	    p2.setFeatureType(pointFeatureType);
	    p3.setFeatureType(pointFeatureType);
	    
	    Object[] attributes = new Object[] { "boulevard du général de Gaulle" };
	    // p1.setSchema(schema);
	    p1.setAttributes(attributes);
	    
	    // p2.setSchema(schema);
	    p2.setAttributes(attributes);
	    
	    attributes = new Object[] { "bld du gal de Gaulle" };
	    // p3.setSchema(schema);
	    p3.setAttributes(attributes);
	    
	    candidates.add(p1);
	    candidates.add(p2);
	    candidates.add(p3);
	    
	    //
	    reference = new DefaultFeature(new GM_Point(new DirectPosition(0, 0)));
	    // reference.setSchema(schema);
	    reference.setAttributes(attributes);
	}
	
	@Test
	public void testPoint2Criteres() throws Exception {
	  
		List<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
		
		// Distance euclidienne
		EuclidianDist source = new EuclidianDist();
		
		// Fonction EstApparie
		Function1D[] listFEA = new Function1D[2];
		// LinearFunction f11 = new LinearFunction(-0.9/75, 1);
		ConstantFunction f11 = new ConstantFunction(0.8);
		f11.setDomainOfFunction(0., 75., true, false);
		listFEA[0] = f11;
		ConstantFunction f12 = new ConstantFunction(0.1);
        f12.setDomainOfFunction(75., 100., true, true);
        listFEA[1] = f12;
        source.setMasseAppCi(listFEA);
        
        // Fonction NonApparie
        Function1D[] listFNA = new Function1D[3];
        ConstantFunction f21 = new ConstantFunction(0.05);
        f21.setDomainOfFunction(0., 50., true, false);
        listFNA[0] = f21;
        // LinearFunction f22 = new LinearFunction(0.8/25, -1.6);
        ConstantFunction f22 = new ConstantFunction(0.1);
        f22.setDomainOfFunction(50., 75., true, false);
        listFNA[1] = f22;
        ConstantFunction f23 = new ConstantFunction(0.8);
        f23.setDomainOfFunction(75., 100., true, true);
        listFNA[2] = f23;
        source.setMasseAppPasCi(listFNA);
		
		// Fonction PrononcePas
        Function1D[] listFPP = new Function1D[3];
        // LinearFunction f31 = new LinearFunction(0.4/50, 0.);
        ConstantFunction f31 = new ConstantFunction(0.15);
        f31.setDomainOfFunction(0., 50., true, false);
        listFPP[0] = f31;
        // LinearFunction f32 = new LinearFunction(-0.3/25, 1.);
        ConstantFunction f32 = new ConstantFunction(0.1);
        f32.setDomainOfFunction(50., 75., true, false);
        listFPP[1] = f32;
        ConstantFunction f33 = new ConstantFunction(0.1);
        f33.setDomainOfFunction(75., 100., true, true);
        listFPP[2] = f33;
        source.setMasseIgnorance(listFPP);
	    
        //
		criteria.add(source);
		
		boolean closed = false;
		GeoMatching matching = new GeoMatching();
		EvidenceResult<GeomHypothesis> result = matching.runAppriou(criteria, reference, candidates,
		    ChoiceType.PIGNISTIC, closed);
		
		LOGGER.info("result = " + result);
		LOGGER.info("reference = " + reference.getGeom());
		LOGGER.info("value = " + result.getValue());
		LOGGER.info("conflict = " + result.getConflict());
		LOGGER.info("nb " + result.getHypothesis().size());
		for (int i = 0; i < result.getHypothesis().size(); i++) {
			LOGGER.info("obj " + i + " = " + result.getHypothesis().get(i).getId());
		}
		
		
		  
		Assert.assertTrue(true);
		  
		LOGGER.debug("====== Fin du test ======");
	}
	
	
	/*@Test
	public void testJaroWinckler() {
		// 0.961
		System.out.println("MARTHA & MARHTA (0.961) : "
				+ JaroWinkler.similarity("MARTHA", "MARHTA"));
		Assert.assertEquals(JaroWinkler.similarity("MARTHA", "MARHTA"), 0.961, 0.001);
		Assert.assertEquals(JaroWinkler.similarity("MARHTA", "MARTHA"), 0.961, 0.001);
		
		// 0.84
		System.out.println("DWAYNE & DUANE (0.84) : "
				+ JaroWinkler.similarity("DWAYNE", "DUANE"));
		Assert.assertEquals(JaroWinkler.similarity("DWAYNE", "DUANE"), 0.84, 0.01);
		Assert.assertEquals(JaroWinkler.similarity("DUANE", "DWAYNE"), 0.84, 0.01);

		// 0.813
		System.out.println("DIXON & DICKSONX (0.813) : "
				+ JaroWinkler.similarity("DIXON", "DICKSONX"));
		Assert.assertEquals(JaroWinkler.similarity("DIXON", "DICKSONX"), 0.813, 0.001);
		Assert.assertEquals(JaroWinkler.similarity("DICKSONX", "DIXON"), 0.813, 0.001);
		
		// 0.919
		System.out.println("Saint-Jean de la Ruelle & Saint Jean de la Ruelle : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint Jean de la Ruelle"));
		
		// 0.867
		System.out.println("Saint-Jean de la Ruelle & Saint-Jean : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint-Jean"));
		
		// 0.847
		System.out.println("Saint-Jean de la Ruelle & Saint Jean : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint Jean"));
		
		// 0.937
		System.out.println("Saint-Jean de la Ruelle & Saint-jean de la Ruelle : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint-jean de la Ruelle"));
				
		// 0.863
		System.out.println("Saint-Jean de la Ruelle & saint-Jean de la Ruelle : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "saint-jean de la Ruelle"));
		
		// 0.919
		System.out.println("Saint-Jean de la Ruelle & Saint-Paul de la Ruelle : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint-Paul de la Ruelle"));
				
		// 0.814
		System.out.println("Saint-Jean de la Ruelle & Saint-Paul : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint-Paul"));
				
		// 0.906
		System.out.println("Saint-Jean de la Ruelle & Saint-Pol de la Ruelle : "
				+ JaroWinkler.similarity("Saint-Jean de la Ruelle", "Saint-Pol de la Ruelle"));
			
	}*/

}
