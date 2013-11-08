package fr.ign.parameters;


import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;


/**
 * 
 * 
 * @author MDVan-Damme
 */
public class ParametersParser {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ParametersParser.class.getName());

  public static Parameters parseXML(String inputXML) throws Exception {

    LOGGER.debug("====================================================================");
    LOGGER.debug("Parser");
    try {
      
      InputStream input = ParametersParser.class.getResourceAsStream("/schema/Parameters.xsd");
      Source schemaSource = new StreamSource(input);
      
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema parameterSchema = schemaFactory.newSchema(schemaSource);
     
      Parameters param = Parameters.unmarshall(inputXML, parameterSchema);
      System.out.println(param);

      // Return Parameter object
      return param;

    } catch (Exception e) {
      throw e;
    }

  }

}
