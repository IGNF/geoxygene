package fr.ign.parameters;

import java.io.File;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.validation.Schema;

@XmlType(name = "ParameterComponent")
public class ParameterComponent {

  public void marshall() {
    try {
      JAXBContext jc = JAXBContext.newInstance(Parameters.class, Parameter.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(this, System.out);
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }

  public void marshall(String filename) {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class, Parameter.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this, new File(filename));
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * Unmarshal XML data from the specified Parameters file.
   * @param XML data file
   * @return the resulting content tree in Parameters
   * @throws Exception
   */
  public static Parameters unmarshall(File file) throws Exception { 
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class, Parameter.class);
      Unmarshaller unmarshaller = context.createUnmarshaller(); 
      Parameters root = (Parameters) unmarshaller.unmarshal(file); 
      return root; 
    } catch (Exception e1) { 
      e1.printStackTrace(); throw e1; 
    } 
  }
   

  /**
   * Validate and Unmarshal XML data from the specified Parameters file.
   * @param XML data file
   * @param xsdSchema
   * @return the resulting content tree in Parameters
   * @throws Exception
   */
  public static Parameters unmarshall(File file, Schema xsdSchema) throws Exception { 
    try { 
      JAXBContext context = JAXBContext.newInstance(Parameters.class, Parameter.class); 
      Unmarshaller unmarshaller = context.createUnmarshaller(); 
      // Validation : setting a schema on the marshaller instance to activate validation against given XML schema
      unmarshaller.setSchema(xsdSchema); Parameters root = (Parameters)
          unmarshaller.unmarshal(file); 
      return root; 
    } catch (Exception e1) {
      e1.printStackTrace(); 
      throw e1;
    }
  }
  

  /**
   * Unmarshal data from XML text.
   * @param inputXML
   * @return the resulting content tree in Parameters
   * @throws Exception
   */
  public static Parameters unmarshall(String inputXML) throws Exception { 
    try { 
      JAXBContext context = JAXBContext.newInstance(Parameters.class, Parameter.class);
      Unmarshaller msh = context.createUnmarshaller(); 
      StringReader reader = new StringReader(inputXML); 
      Parameters root = (Parameters)msh.unmarshal(reader); 
      return root; 
    } catch (Exception e1) {
      e1.printStackTrace(); 
      throw e1;
    }
  }
   

  /**
   * Validate and Unmarshal data from XML text.
   * @param inputXML
   * @param xsdSchema
   * @return the resulting content tree in Parameters
   * @throws Exception
   */
  public static Parameters unmarshall(String inputXML, Schema xsdSchema) throws Exception { 
    try { 
      JAXBContext context = JAXBContext.newInstance(Parameters.class, Parameter.class); 
      Unmarshaller msh = context.createUnmarshaller(); 
      // Validation : setting a schema on the marshaller instance to activate validation against given XML schema
      msh.setSchema(xsdSchema); 
      StringReader reader = new StringReader(inputXML);
      Parameters root = (Parameters) msh.unmarshal(reader); 
      return root; 
    } catch (Exception e1) { 
      throw e1; 
    }
  }
    
   

  /**
   * Parse une requête XML de paramètres.
   * 
   * @param inputXML valeurToParse Le contenu du XML à parser
   * @param inputXSD document Le document de validation
   * @return Les paramètres
   * @throws Exception
   */
  /*
   * public static Parameters parseXML(String inputXML, InputStream inputXSD)
   * throws Exception { try {
   * 
   * Source schemaSource = new StreamSource(inputXSD); SchemaFactory
   * schemaFactory =
   * SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); Schema
   * parameterSchema = schemaFactory.newSchema(schemaSource);
   * 
   * Parameters param = Parameters.unmarshall(inputXML, parameterSchema); return
   * param;
   * 
   * } catch (Exception e) { throw e; } }
   */

}
