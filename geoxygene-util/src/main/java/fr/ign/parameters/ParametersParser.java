package fr.ign.parameters;


import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;


/**
 * Parser for the Parameters object.
 * @author MDVan-Damme
 */
public class ParametersParser {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ParametersParser.class.getName());

  /**
   * Parse une requête XML de paramètres.
   * 
   * @param inputXML valeurToParse
     *            Le contenu du XML à parser
   * @param inputXSD document
   *              Le document de validation
   * @return Les paramètres
   * @throws Exception
   */
  public static Parameters parseXML(String inputXML, InputStream inputXSD) throws Exception {

    LOGGER.debug("====================================================================");
    LOGGER.debug("Parser");
    try {
      
      Source schemaSource = new StreamSource(inputXSD);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema parameterSchema = schemaFactory.newSchema(schemaSource);
     
      Parameters param = Parameters.unmarshall(inputXML, parameterSchema);
      LOGGER.trace(param);

      // Return Parameter 
      return param;

    } catch (Exception e) {
      throw e;
    }

  }

}
