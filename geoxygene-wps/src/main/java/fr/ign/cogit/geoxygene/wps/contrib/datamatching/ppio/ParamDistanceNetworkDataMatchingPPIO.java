package fr.ign.cogit.geoxygene.wps.contrib.datamatching.ppio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;

/**
 * 
 *
 */
public class ParamDistanceNetworkDataMatchingPPIO extends CDataPPIO {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ParamDistanceNetworkDataMatchingPPIO.class.getName());

    /**
     * Default constructor.
     */
    protected ParamDistanceNetworkDataMatchingPPIO() {
        super(ParamDistanceNetworkDataMatching.class, ParamDistanceNetworkDataMatching.class, "text/xml");
    }
    
    @Override
    public void encode(Object value, OutputStream os) throws IOException {
        
        LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LOGGER.debug("[ParamDistanceNetworkDataMatchingPPIO] ENCODE ");
        LOGGER.debug("Start encoding the result for output");
        
        /*SaxWriter writer = new SaxWriter();
        writer.setContentHandler(handler);
        XStream xstream = new XStream();
        xstream.marshal((ParamDistanceNetworkDataMatching)object, writer);*/
        
        throw new UnsupportedOperationException("Unsupported Operation.");
    }
    
    @Override
    public Object decode(InputStream input) throws Exception {
        
        LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LOGGER.debug("[ParamDistanceNetworkDataMatchingPPIO] DECODE InputStream ");
        LOGGER.debug("Start decoding the parameter for input.");
        
        ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
        return paramDistance;
    }
    
    @Override
    public Object decode(String input) throws Exception {
        
        LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        LOGGER.debug("[ParamDistanceNetworkDataMatchingPPIO] DECODE String ");
        LOGGER.debug(input);
        ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
        return paramDistance;
    }
    
    
    @Override
    public String getFileExtension() {
        return "xml";
    }
    
}

