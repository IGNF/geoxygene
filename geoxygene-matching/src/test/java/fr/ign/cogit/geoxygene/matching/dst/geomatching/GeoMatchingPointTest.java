package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.priv.garshol.duke.comparators.JaroWinkler;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.sources.punctual.EuclidianDist;
import fr.ign.cogit.geoxygene.matching.dst.sources.text.LevenshteinDist;
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
		//candidates = new Population<IFeature>("Point");
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
	    // SchemaDefaultFeature schema = new SchemaDefaultFeature();
	    // schema.setFeatureType(pointFeatureType);
	    // pointFeatureType.setSchema(schema);
	    
	    // Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
	    // attLookup.put(new Integer(0), new String[] { "nature", "nature" });
	    // schema.setAttLookup(attLookup);
	    
	    Object[] attributes = new Object[] { "Saint-Paul" };
	    p1.setFeatureType(pointFeatureType);
	    // p1.setSchema(schema);
	    p1.setAttributes(attributes);
	    
	    // p2.setSchema(schema);
	    p2.setFeatureType(pointFeatureType);
	    p2.setAttributes(attributes);
	    
	    attributes = new Object[] { "Saint-Jean" };
	    // p3.setSchema(schema);
	    p3.setFeatureType(pointFeatureType);
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
		
		Collection<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
		criteria.add(new EuclidianDist());
		criteria.add(new LevenshteinDist());
		
		boolean closed = true;
		GeoMatching matching = new GeoMatching();
		EvidenceResult<GeomHypothesis> result = matching.run(criteria, reference, candidates,
		        ChoiceType.PIGNISTIC, closed);
		
		LOGGER.info("result = " + result);
		LOGGER.trace("reference = " + reference.getGeom());
		LOGGER.trace("value = " + result.getValue());
		LOGGER.trace("conflict = " + result.getConflict());
		LOGGER.trace("with " + result.getHypothesis().size());
		for (int i = 0; i < result.getHypothesis().size(); i++) {
			LOGGER.trace("\tobj " + i + " = " + result.getHypothesis().get(i));
		}
		  
		Assert.assertTrue(true);
		  
		LOGGER.trace("\n====== Fin du test ======");
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
