package fr.ign.parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.CDataPPIO;


/**
 * 
 * @author MDVan-Damme
 */
public class ParametersPPIO extends CDataPPIO {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ParametersPPIO.class.getName());

  /**
   * Default constructor.
   */
  protected ParametersPPIO() {
    super(Parameters.class, Parameters.class, "text/xml");
  }

  @Override
  public void encode(Object value, OutputStream os) throws IOException {
    throw new UnsupportedOperationException("Unsupported encode(Object, OutputStream) operation.");
  }

  @Override
  public Object decode(InputStream input) throws Exception {
    throw new UnsupportedOperationException("Unsupported decode(InputStream) operation.");
  }

  @Override
  public Object decode(String input) throws Exception {
    return ParametersParser.parseXML(input);
  }

  @Override
  public String getFileExtension() {
    return "xml";
  }

}
