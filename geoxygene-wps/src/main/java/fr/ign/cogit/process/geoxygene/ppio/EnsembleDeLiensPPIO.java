package fr.ign.cogit.process.geoxygene.ppio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;

import fr.ign.cogit.geoxygene.contrib.data.EnsembleDeLiensData;


/**
 * 
 * 
 * @author MDVan-Damme
 * 
 */
public class EnsembleDeLiensPPIO extends CDataPPIO {
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(EnsembleDeLiensPPIO.class.getName());

  /**
   * Default constructor.
   */
  protected EnsembleDeLiensPPIO() {
    super(EnsembleDeLiensData.class, EnsembleDeLiensData.class, "text/xml");
  }

  @Override
  public void encode(Object value, OutputStream os) throws IOException {
    String result;
    try {
      result = EnsembleDeLiensData.generateXMLResponse((EnsembleDeLiensData)value);
      os.write(result.getBytes(Charset.forName("UTF-8")));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public Object decode(InputStream input) throws Exception {
    throw new UnsupportedOperationException("Unsupported decode(InputStream) operation.");
  }

  @Override
  public Object decode(String inputXML) throws Exception {
    throw new UnsupportedOperationException("Unsupported decode(String) operation.");
  }

  @Override
  public String getFileExtension() {
    return "xml";
  }

}
