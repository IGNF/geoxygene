package fr.ign.cogit.geoxygene.semio.legend.metadata;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;



public class SemanticRelationTest {
    
    @Test
    public void xmlTest() {
        
        // Load file
    	SemanticRelationDescriptor desc = SemanticRelationDescriptor.unmarshall(SemanticRelationTest.class.getClassLoader().getResource("SemanticRelationExample.xml").getPath());
        // Check nb 
    	Assert.assertEquals(desc.getNbRelations(), 20);
    	
    	// Relation 1
    	SemanticRelation rel1 = desc.getRelations().get(0);
    	Assert.assertEquals(rel1.getType(), 1);
    	Assert.assertEquals(rel1.isAssociation(), true);
    	Assert.assertEquals(rel1.isDifference(), false);
    	Assert.assertEquals(rel1.isOrder(), false);
    	Assert.assertEquals(rel1.getRelatedComponents().size(), 2);
    	LegendComponent comp1 = rel1.getRelatedComponents().get(0);
    	Assert.assertEquals(comp1.getName(), "Marshalling Area");
    	Assert.assertEquals(comp1.getRelations().size(), 0);
    	comp1.setRelations(desc.getRelations());
    	Assert.assertEquals(comp1.getRelations().size(), 20);
    }

}
