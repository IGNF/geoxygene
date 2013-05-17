package fr.ign.cogit.geoxygene.semio.legend.symbol.color;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 *
 */
public class ContrastTest {
    
    @Test
    public void xmlTest() {
        
        // Load file
        ContrastCollection cogitContrasts = ContrastCollection.unmarshall(ContrastCollection.class.getClassLoader().getResource("symbol/color/Contrast.xml").getPath());
        
        // Check number of contrast
        Assert.assertTrue("Number of cogit contrast different of 24336 : " + cogitContrasts.getContrasts().size(), 
                cogitContrasts.getContrasts().size() == 24336);
    }

}
