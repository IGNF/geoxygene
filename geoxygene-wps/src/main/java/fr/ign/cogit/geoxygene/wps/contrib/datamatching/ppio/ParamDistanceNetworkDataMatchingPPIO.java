package fr.ign.cogit.geoxygene.wps.contrib.datamatching.ppio;

import java.io.InputStream;
import org.apache.log4j.Logger;

import org.geoserver.wps.ppio.XStreamPPIO;
import org.xml.sax.ContentHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.SaxWriter;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;

/**
 * 
 *
 */
public class ParamDistanceNetworkDataMatchingPPIO extends XStreamPPIO {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ParamDistanceNetworkDataMatchingPPIO.class.getName());

    /**
     * Default constructor.
     */
    protected ParamDistanceNetworkDataMatchingPPIO() {
        super(ParamDistanceNetworkDataMatching.class);
    }
    
    
    public void encode(Object object, ContentHandler handler) throws Exception {
        
        LOGGER.info("------------------------------------------------------------------------");
        LOGGER.info("Start encoding the result for output");
        
        SaxWriter writer = new SaxWriter();
        writer.setContentHandler(handler);
        XStream xstream = new XStream();
        xstream.marshal((ParamDistanceNetworkDataMatching)object, writer);
        
        // throw new UnsupportedOperationException("Unsupported Operation.");
    }
    
    
    public Object decode(InputStream input) throws Exception {
        
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Start decoding the parameter for input.");
        
        // System.out.println("Valeur de l'input = " + input.toString());
        
        ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
        return paramDistance;
    }
    
}
