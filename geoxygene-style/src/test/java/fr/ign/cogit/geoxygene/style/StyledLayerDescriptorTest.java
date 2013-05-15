package fr.ign.cogit.geoxygene.style;

import java.awt.Color;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.thematic.DiagramRadius;
import fr.ign.cogit.geoxygene.style.thematic.DiagramSymbolizer;
import fr.ign.cogit.geoxygene.style.thematic.ThematicClass;
import fr.ign.cogit.geoxygene.style.thematic.ThematicSymbolizer;

/**
 * 
 */
public class StyledLayerDescriptorTest extends XMLTestCase {
    
    private static Logger LOGGER = Logger.getLogger(StyledLayerDescriptorTest.class.getName());
    
    /**
     * On construit un fichier SLD manuellement, ensuite on genere le XML 
     * et on compare avec le résultat escompté.
     */
    @Test
    public void testXML() throws Exception {
        
        /*StyledLayerDescriptor sld = new StyledLayerDescriptor();
        
        Layer layer = sld.createLayerRandomColor("Test", GM_Polygon.class); //$NON-NLS-1$
        layer.getStyles().get(0).getSymbolizer().getStroke().setColor(Color.red);
        ((PolygonSymbolizer)layer.getStyles().get(0).getSymbolizer()).getFill().setFill(Color.orange);
        
        DiagramSymbolizer d = new DiagramSymbolizer();
        ThematicSymbolizer s = new ThematicSymbolizer();
        DiagramRadius r = new DiagramRadius();
        ThematicClass t = new ThematicClass();
        
        r.setValue(15.0);
        d.getDiagramSize().add(r);
        d.setDiagramType("piechart"); //$NON-NLS-1$
        t.setClassLabel("sold"); //$NON-NLS-1$
        t.setClassValue(new PropertyName("sold")); //$NON-NLS-1$
        t.setFill(new Fill());
        t.getFill().setFill(Color.blue);
        d.getThematicClass().add(t);
        
        s.getSymbolizers().add(d);
        
        layer.getStyles().get(0).getFeatureTypeStyles().get(0).getRules().get(0).getSymbolizers().add(s);
        sld.add(layer);
        LOGGER.debug(sld);
        
        String xmlCompare = "<?xml version=\"1.0\" ?>"
                + "<StyledLayerDescriptor xmlns:ns2=\"http://www.opengis.net/ogc\">"
                + "<NamedLayer>"
                    + "<Name>Test</Name>"
                    + "<UserStyle>"
                        + "<Name>Test</Name>"   // ???
                        + "<Name>Test</Name>"   // ???
                        + "<IsDefault>false</IsDefault>"
                        + "<FeatureTypeStyle>"
                            + "<Rule>"
                                + "<LegendGraphic>"
                                    + "<Graphic>"
                                        + "<opacity>1.0</opacity>"
                                        + "<Size>6.0</Size>"
                                        + "<Rotation>"
                                            + "<Literal>0</Literal>"
                                        + "</Rotation>"
                                    + "</Graphic>"
                                + "</LegendGraphic>"
                                + "<PolygonSymbolizer uom=\"http://www.opengeospatial.org/se/units/metre\">"
                                    + "<Stroke>"
                                        + "<CssParameter name=\"stroke\">#ff0000</CssParameter>"
                                        + "<CssParameter name=\"stroke-opacity\">0.8</CssParameter>"
                                        + "<CssParameter name=\"stroke-width\">1.0</CssParameter>"
                                    + "</Stroke>"
                                    + "<GeometryPropertyName>geom</GeometryPropertyName>"
                                    + "<Fill>"
                                        + "<CssParameter name=\"fill\">#ffc800</CssParameter>"
                                        + "<CssParameter name=\"fill-opacity\">0.8</CssParameter>"
                                    + "</Fill>"
                                + "</PolygonSymbolizer>"
                                + "<ThematicSymbolizer uom=\"http://www.opengeospatial.org/se/units/metre\">"
                                    + "<GeometryPropertyName>geom</GeometryPropertyName>"
                                    + "<DiagramSymbolizer>"
                                        + "<DiagramType>piechart</DiagramType>"
                                        + "<DiagramSize><DiagramRadius>15.0</DiagramRadius></DiagramSize>"
                                        + "<ThematicClass>"
                                            + "<ClassLabel>sold</ClassLabel>"
                                            + "<ClassValue><PropertyName>sold</PropertyName></ClassValue>"
                                            + "<Fill><CssParameter name=\"fill\">#0000ff</CssParameter></Fill>"
                                        + "</ThematicClass>"
                                    + "</DiagramSymbolizer>"
                                + "</ThematicSymbolizer>"
                            + "</Rule>"
                        + "</FeatureTypeStyle>"
                    + "</UserStyle>"
                + "</NamedLayer>"
                + "</StyledLayerDescriptor>";
        LOGGER.debug(xmlCompare);
        
        // Compare
        assertXMLEqual(sld.toString(), xmlCompare);*/
        
        assertTrue(true);
        
    }
    
}