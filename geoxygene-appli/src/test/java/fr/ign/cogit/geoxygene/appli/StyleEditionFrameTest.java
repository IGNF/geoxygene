package fr.ign.cogit.geoxygene.appli;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * 
 * 
 *
 */
public class StyleEditionFrameTest {
    
    public final static Logger LOGGER = Logger.getLogger(StyleEditionFrameTest.class.getName());

    /**
     * Test load and extract BasicStyles.xml.
     */
    @Test
    public void testBasicStyles() {
        
        LOGGER.info("Try to load BasicStyles.xml ");
        StyledLayerDescriptor sld = StyledLayerDescriptor.unmarshall(StyledLayerDescriptor.class.getClassLoader().getResourceAsStream("sld/BasicStyles.xml"));
        LOGGER.info("BasicStyles.xml loaded");

        Assert.assertTrue("Number of layers different to 5 : " + sld.getLayers().size(), 
                sld.getLayers().size() == 5);
        Assert.assertTrue("Number of polygon styles different to 1 : " + sld.getLayer("Polygon").getStyles().size(), 
                sld.getLayer("Polygon").getStyles().size() == 1);
        Assert.assertTrue("Number of point styles different to 1 : " + sld.getLayer("Point").getStyles().size(), 
                sld.getLayer("Point").getStyles().size() == 1);
        Assert.assertTrue("Number of 'Line Dasharray' styles different to 2 : " + sld.getLayer("Line Dasharray").getStyles().size(), 
                sld.getLayer("Line Dasharray").getStyles().size() == 2);
        
        Assert.assertTrue("Line with contour - Line fill - Red different 255",
                sld.getLayer("Line with contour").getStyles().get(1).getSymbolizer().getStroke().getColor().getRed() == 255);
        Assert.assertTrue("Line with contour - Line fill - Green different 255",
                sld.getLayer("Line with contour").getStyles().get(1).getSymbolizer().getStroke().getColor().getGreen() == 255);
        Assert.assertTrue("Line with contour - Line fill - Blue different 255",
                sld.getLayer("Line with contour").getStyles().get(1).getSymbolizer().getStroke().getColor().getBlue() == 0);
    }

}
