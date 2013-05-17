package fr.ign.cogit.geoxygene.semio.legend.legendContent;

import org.junit.Assert;
import org.junit.Test;


public class LegendTest {
    
    @Test
    public void xmlTest() {
        
        // Load file
        Legend legend = Legend.unmarshall(LegendTest.class.getClassLoader().getResource("LegendExample.xml").getPath());
        
        // Check 
        Assert.assertTrue("Number of leaves : " + legend.getLeaves().size(), legend.getLeaves().size() == 21);
    }

}
