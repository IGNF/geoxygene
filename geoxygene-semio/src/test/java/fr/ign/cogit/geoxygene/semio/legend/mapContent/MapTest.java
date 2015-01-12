package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.UserLayerFactory;

public class MapTest extends TestCase {
	
	private Map carte;
	  
	  
	  
	  private static Population<DefaultFeature> getPopCarre() {
	    
	    Population<DefaultFeature> popCarre = new Population<DefaultFeature>("Carre");
	    
	    DefaultFeature carre1 = new DefaultFeature(new GM_Polygon(new GM_Envelope(10, 20, 10, 40)));
	    DefaultFeature carre2 = new DefaultFeature(new GM_Polygon(new GM_Envelope(30, 40, 10, 18)));
	    DefaultFeature carre3 = new DefaultFeature(new GM_Polygon(new GM_Envelope(30, 40, 22, 40)));
	    
	    FeatureType carreFeatureType = new FeatureType();
	    carreFeatureType.setTypeName("nature");
	    carreFeatureType.setGeometryType(IPolygon.class);
	    
	    AttributeType idTextNature = new AttributeType("nature", "String");
	    carreFeatureType.addFeatureAttribute(idTextNature);
	    
	    // Création d'un schéma associé au featureType
	    SchemaDefaultFeature schema = new SchemaDefaultFeature();
	    schema.setFeatureType(carreFeatureType);
	    carreFeatureType.setSchema(schema);
	    
	    java.util.Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
	    attLookup.put(new Integer(0), new String[] { "nature", "nature" });
	    schema.setAttLookup(attLookup);
	    
	    popCarre.setFeatureType(carreFeatureType);
	    
	    Object[] attributes = new Object[] { "carre1" };
	    carre1.setSchema(schema);
	    carre1.setAttributes(attributes);
	    attributes = new Object[] { "carre2" };
	    carre2.setSchema(schema);
	    carre2.setAttributes(attributes);
	    attributes = new Object[] { "carre2" };
	    carre3.setSchema(schema);
	    carre3.setAttributes(attributes);
	    
	    popCarre.add(carre1);
	    popCarre.add(carre2);
	    popCarre.add(carre3);
	    
	    return popCarre;
	    
	  }
	  
	  @Before
	  public void setUp() {
	    
	    // System.out.println("oo");
	    UserLayerFactory factory = new UserLayerFactory();
	    StyledLayerDescriptor sld;
	    try {
	        sld = StyledLayerDescriptor
	                .unmarshall(StyledLayerDescriptor.class
	                        .getClassLoader().getResourceAsStream(
	                                "sld/BasicStyles.xml"));
	        factory.setModel(sld);
	        factory.setName("");
	    } catch (JAXBException e1) {
	        
	        e1.printStackTrace();
	    } 
	    
	    Legend legendCarre = Legend.unmarshall(this.getClass().getClassLoader().getResource("LegendCarre.xml").getPath());
	    
	    

	    Population<DefaultFeature> collection = getPopCarre();
	    
	    factory.setGeometryType(collection.getFeatureType().getGeometryType());
	    factory.setCollection(collection);
	    Layer layerCarre = factory.createLayer();

	    List<Layer> layers = new ArrayList<Layer>();
	    layers.add(layerCarre);
	   
	    double metersPerPixel = 0.02540005 / 120;
	    carte = new Map(layers, legendCarre, metersPerPixel);
	    carte.setName("Carte des carrés");
	    
	    
	    
	  }
	  
	  @Test
	  public void test1() {
		  Assert.assertEquals(carte.getSymbolisedFeatureCollections().size(), 1);
	  }

}
