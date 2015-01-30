package fr.ign.cogit.geoxygene.style.interpolation;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Reader;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.hamcrest.core.IsInstanceOf;

import fr.ign.cogit.geoxygene.style.AbstractSymbolizer;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;

public class SLDMixer {
  protected static Logger logger = Logger.getLogger(AbstractSymbolizer.class
    .getName());
  public static StyledLayerDescriptor mix(StyledLayerDescriptor sld1, StyledLayerDescriptor sld2){
    
    StyledLayerDescriptor sldout = null;    
    CharArrayWriter writer = new CharArrayWriter();
    sld2.marshall(writer);
    Reader reader = new CharArrayReader(writer.toCharArray());
    
    try {
      sldout = StyledLayerDescriptor.unmarshall(reader);
    } catch (JAXBException e) {
        e.printStackTrace();
    }
    
    sldout.setDataSet(sld1.getDataSet());
    
    for (Layer layer1 : sld1.getLayers()) {
        String layerName = layer1.getName();
      
        // find if a similar layer exist in the other SLD
        Layer layer2 = null;
        for (Layer layer2tmp : sld2.getLayers()) {
            if (layer2tmp.getName().compareTo(layerName) == 0){
                layer2 = layer2tmp;
                break;
            }
        }
        
        if (layer2 != null){
            logger.info("Found a match for layer " + layerName);
            
            int nbStyles = Math.min(layer1.getStyles().size(), layer2.getStyles().size());
            
            for(int si = 0; si != nbStyles; ++si){
                Style style1 = layer1.getStyles().get(si);
                Style style2 = layer2.getStyles().get(si);
                
                int nbFeatures = Math.min(style1.getFeatureTypeStyles().size(), style2.getFeatureTypeStyles().size());
                
                for(int fi = 0; fi != nbFeatures; ++fi ){
                    FeatureTypeStyle fts1 = style1.getFeatureTypeStyles().get(fi);
                    FeatureTypeStyle fts2 = style2.getFeatureTypeStyles().get(fi);
  
                    int nbRules = Math.min(fts1.getRules().size(), fts2.getRules().size());
  
                    for(int ri = 0; ri != nbRules; ++ri ){
                        Rule rule1 = fts1.getRules().get(ri);
                        Rule rule2 = fts2.getRules().get(ri);
                        
                        int nbSymbolizers = Math.min(rule1.getSymbolizers().size(), rule2.getSymbolizers().size());

                        for(int i = 0; i != nbSymbolizers; ++i ){
                            Symbolizer symbolizer1 = rule1.getSymbolizers().get(i);
                            Symbolizer symbolizer2 = rule2.getSymbolizers().get(i);
                            
                            Class <? extends Symbolizer> cl = symbolizer1.getClass(); 
                            
                            if (cl == symbolizer2.getClass()) {
                                logger.info("Found " + cl.toString());

                                Symbolizer interpolated = null;
                                if (cl == PolygonSymbolizer.class){
                                    interpolated = new PolygonInterpolationSymbolizer();
                                    ((PolygonInterpolationSymbolizer) interpolated)
                                        .setFirstSymbolizer((PolygonSymbolizer) symbolizer1);
                                    ((PolygonInterpolationSymbolizer) interpolated)
                                        .setSecondSymbolizer((PolygonSymbolizer) symbolizer2);                                    
                                } else if (cl == LineSymbolizer.class){
                                    interpolated = new LineInterpolationSymbolizer();
                                    ((LineInterpolationSymbolizer) interpolated)
                                        .setFirstSymbolizer((LineSymbolizer) symbolizer1);
                                    ((LineInterpolationSymbolizer) interpolated)
                                        .setSecondSymbolizer((LineSymbolizer) symbolizer2);                                    
                                } else
                                    interpolated = symbolizer2;
                                
                                sldout.getLayer(layerName)
                                .getStyles().get(si)
                                .getFeatureTypeStyles().get(fi)
                                .getRules().get(ri)
                                .getSymbolizers().set(i, interpolated);
                                
                            }
                            else
                                logger.warn("Conflict between " 
                            + symbolizer1.getClass().toString()
                            + " and "
                            + symbolizer2.getClass().toString());
                            
                           
                        }
                    }
                }
            }
        }
        
      
  }
    
    return sldout;    
  }
}
