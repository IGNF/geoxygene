package fr.ign.cogit.process.geoxygene.netmatching.ppio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.process.geoxygene.netmatching.xml.ParamAppariementParser;

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
        throw new UnsupportedOperationException("Unsupported encode(Object, OutputStream) operation.");
    }
    
    @Override
    public Object decode(InputStream input) throws Exception {
        LOGGER.debug("====================================================================");
        LOGGER.debug("PPIO inputstream");
        LOGGER.debug(input);
        LOGGER.debug("====================================================================");
        throw new UnsupportedOperationException("Unsupported decode(InputStream) operation.");
    }
    
    @Override
    public Object decode(String input) throws Exception {
        LOGGER.debug("====================================================================");
        LOGGER.debug("PPIO input");
        LOGGER.debug(input);
        LOGGER.debug("====================================================================");
        return ParamAppariementParser.parseXML(input);
    }
    
    
    @Override
    public String getFileExtension() {
        return "xml";
    }
    
}

