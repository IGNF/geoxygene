package fr.ign.cogit.process.geoxygene.cartetopo.ppio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;

/**
 * 
 * @author MDVan-Damme
 */
public class CarteTopoResultPPIO extends CDataPPIO {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(CarteTopoResultPPIO.class.getName());
    
    /**
     * Default constructor.
     */
    protected CarteTopoResultPPIO() {
        super(CarteTopoResult.class, CarteTopoResult.class, "text/xml");
    }
    
    @Override
    public void encode(Object value, OutputStream os) throws IOException {
        System.out.println("ENCODE");
        System.out.println("NB ARC = " + ((CarteTopoResult)value).getPopArc().size());
    }
    
    @Override
    public Object decode(InputStream input) throws Exception {
        throw new UnsupportedOperationException("Unsupported decode(InputStream) operation.");
    }
    
    @Override
    public Object decode(String input) throws Exception {
        throw new UnsupportedOperationException("Unsupported decode(String) operation.");
    }
    
    
    @Override
    public String getFileExtension() {
        return "xml";
    }

}
