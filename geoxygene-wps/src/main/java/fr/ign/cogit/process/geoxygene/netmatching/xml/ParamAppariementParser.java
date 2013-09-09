package fr.ign.cogit.process.geoxygene.netmatching.xml;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;

/**
 *
 */
public class ParamAppariementParser {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ParamAppariementParser.class.getName());

    
    public static ParamDistanceNetworkDataMatching parseXML(String inputXML) throws Exception {
        
        LOGGER.debug("====================================================================");
        LOGGER.debug("Parser");
        try {
            
            ParamDistanceNetworkDataMatching paramDistance = JAXB.unmarshal(new StringReader(inputXML), ParamDistanceNetworkDataMatching.class);
            LOGGER.debug(paramDistance);
            LOGGER.debug("====================================================================");
            
            //
            return paramDistance;
        
        } catch(Exception e) {
            throw e;
        }
        
    }
}
