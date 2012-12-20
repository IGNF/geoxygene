/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This class contains static methods to ease the handling of XML files.
 * @author GTouya
 * 
 */
public class XMLUtil {

  private static Logger logger = Logger.getLogger(XMLUtil.class.getName());

  /**
   * Writes a DOM document into an XML file.
   * @param doc
   * @param fileName
   * @throws TransformerException
   * @throws IOException
   */
  public static void writeDocumentToXml(Document doc, String fileName)
      throws TransformerException, IOException {
    File file = new File(fileName);
    if (!file.exists())
      file.createNewFile();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(fileName);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

    // Output to console for testing
    if (logger.isLoggable(Level.FINE))
      logger.fine(result.toString());

    transformer.transform(source, result);
  }

  /**
   * Writes a DOM document into an XML file.
   * @param doc
   * @param file
   * @throws TransformerException
   * @throws IOException
   */
  public static void writeDocumentToXml(Document doc, File file)
      throws TransformerException, IOException {
    if (!file.exists())
      file.createNewFile();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(file.toURI().getPath());
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

    // Output to console for testing
    if (logger.isLoggable(Level.FINE))
      logger.fine(result.toString());

    transformer.transform(source, result);
  }
}
