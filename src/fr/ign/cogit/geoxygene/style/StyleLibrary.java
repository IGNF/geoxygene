/**
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.style;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

/**
 * A Library to store Styles (UserStyle or NamedStyle).
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "StyleLibrary")
public class StyleLibrary {
  private static Logger logger = Logger.getLogger(StyleLibrary.class.getName());
  /**
   * Name of the library.
   */
  @XmlElement(name = "Name", required = true)
  private String name;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Description of the library.
   */
  @XmlElement(name = "Description", required = false)
  private String description;

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String d) {
    this.description = d;
  }

  /**
   * The styles the library holds.
   */
  @XmlElements( { @XmlElement(name = "UserStyle", type = UserStyle.class),
      @XmlElement(name = "NamedStyle", type = NamedStyle.class) })
  List<Style> styles = new ArrayList<Style>();

  public List<Style> getStyles() {
    return this.styles;
  }

  public void setStyles(List<Style> styles) {
    this.styles = styles;
  }

  /**
   * Load a library from the given stream.
   * @param stream stream to load the library from
   * @return the library described in the file
   */
  public static StyleLibrary unmarshall(InputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(StyleLibrary.class,
          NamedStyle.class);
      Unmarshaller m = context.createUnmarshaller();
      StyleLibrary library = (StyleLibrary) m.unmarshal(stream);
      return library;
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new StyleLibrary();
  }

  /**
   * Load a library from the given file.
   * @param fileName name of the file to load the library from
   * @return the library described in the file
   */
  public static StyleLibrary unmarshall(String fileName) {
    try {
      return StyleLibrary.unmarshall(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      StyleLibrary.logger.error("File " + fileName + " could not be read"); //$NON-NLS-1$//$NON-NLS-2$
      return new StyleLibrary();
    }
  }

  /**
   * Save the library using the given writer.
   * @param writer writer used to save the library
   */
  public void marshall(Writer writer) {
    try {
      JAXBContext context = JAXBContext.newInstance(StyleLibrary.class,
          NamedStyle.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, writer);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Save the library using the given stream.
   * @param stream stream used to save the library
   */
  public void marshall(OutputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(StyleLibrary.class,
          NamedStyle.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, stream);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Save the library in the given file.
   * @param fileName name of the file to save the library into
   */
  public void marshall(String fileName) {
    try {
      this.marshall(new FileOutputStream(fileName));
    } catch (FileNotFoundException e) {
      StyleLibrary.logger
          .error("File " + fileName + " could not be written to"); //$NON-NLS-1$//$NON-NLS-2$
    }
  }

  public static void main(String[] args) {
    StyleLibrary library = StyleLibrary.unmarshall(StyleLibrary.class
        .getResourceAsStream("/styleLibrary.xml")); //$NON-NLS-1$
    System.out.println(library);
  }

  @Override
  public String toString() {
    String result = "Library " + this.getName() //$NON-NLS-1$
        + " (" + this.getDescription() + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$
    String line = "----------------------\n"; //$NON-NLS-1$
    result += line;
    for (Style s : this.getStyles()) {
      result += s.toString() + "\n"; //$NON-NLS-1$
      result += line;
    }
    return result;
  }
}
