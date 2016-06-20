package fr.ign.cogit.geoxygene.io;

import java.text.SimpleDateFormat;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import junit.framework.TestCase;

public class TestShapeReader extends TestCase {
    
	// helper to test dates
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Test
    public void testPoint() {
    	IPopulation<IFeature> population = ShapefileReader.read(getClass().getClassLoader().getResource("shp/shp_point.shp").toString());
    	assertEquals(2, population.size());
    	
    	{
	    	IFeature first = population.get(0);
	    	assertEquals( 0, first.getAttribute("id") );
	    	assertEquals( "texte1", first.getAttribute("a_text") );
	    	assertEquals( "1", first.getAttribute("a_integer").toString() ); // TODO fix integer??
	
	    	assertEquals( "2015-01-01" , dateFormat.format( first.getAttribute("a_date") ) ); 
	    	assertEquals( "POINT (-1.935329 0.269461 0.0)", first.getGeom().toString() ) ;
    	}	
    	{
	    	IFeature second = population.get(1);
	    	assertEquals( 1, second.getAttribute("id") );
	    	assertEquals( "texte2", second.getAttribute("a_text") );
	    	assertEquals( "2", second.getAttribute("a_integer").toString() ); // TODO fix integer??
	
	    	assertEquals( "2015-01-02" , dateFormat.format( second.getAttribute("a_date") ) ); 
	    	assertEquals( "POINT (-2.067066 0.099401 0.0)", second.getGeom().toString() ) ;
    	}
    }
	
	@Test
    public void testNoGeom() {
    	IPopulation<IFeature> population = ShapefileReader.read(getClass().getClassLoader().getResource("shp/DOC_URBA.shp").toString());
    	assertEquals(11, population.size());
    	
    	{
	    	IFeature first = population.get(0);
	    	assertEquals( "4400120120223", first.getAttribute("IDURBA") );
	    	assertEquals( "PLU", first.getAttribute("TYPEDOC") );
	    	assertEquals( "", first.getAttribute("URLPLAN") ); // empty (not null)
    	}	
    }

}
