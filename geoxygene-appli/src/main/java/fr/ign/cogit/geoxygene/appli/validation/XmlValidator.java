/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.validation;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class XmlValidator {

  protected Logger logger = Logger.getLogger(XmlValidator.class);

  protected InputStream stream = null;
  private String schema_path = null;
  ValidationEventHandler eventHandler;
  protected Object content;
  protected LocationListener ll;

  private Class[] contextclasses;

  public XmlValidator(InputStream xml, Class... jaxbcontextclasses) {
    this.stream = xml;
    this.contextclasses = jaxbcontextclasses;

  }

  public boolean validate() {
    return this.validateSchemaAndParse() & this.validateContent();
  }

  private boolean validateSchemaAndParse() {

    JAXBContext jc;
    try {
      jc = JAXBContext.newInstance(contextclasses);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      if (schema_path != null) {
        // TODO to replace with the javax.xml constant W3C_XML_SCHEMA_NS_URI
        SchemaFactory sf = SchemaFactory
            .newInstance("http://www.w3.org/2001/XMLSchema");
        String fpath = XmlValidator.class.getClassLoader()
            .getResource(schema_path).getFile();
        File f = new File(fpath);
        if (f.exists() && f.isFile()) {
          Schema schema = sf.newSchema(f);
          unmarshaller.setSchema(schema);
        } else {
          logger.fatal("Cannot validate the XML File: the XSD schema "
              + f.getName() + " was not found in the resources directory.");
          return false;
        }
      }
      if (this.eventHandler != null)
        unmarshaller.setEventHandler(this.eventHandler);

      XMLInputFactory xif = XMLInputFactory.newInstance();
      XMLStreamReader xsr = xif.createXMLStreamReader(this.stream);
      this.ll = new LocationListener(xsr);
      unmarshaller.setListener(ll);
      content = unmarshaller.unmarshal(xsr);
    } catch (JAXBException e) {
      e.printStackTrace();
    } catch (SAXNotRecognizedException e1) {
      e1.printStackTrace();
    } catch (SAXNotSupportedException e2) {
      e2.printStackTrace();
    } catch (SAXException e3) {
      e3.printStackTrace();
    } catch (XMLStreamException e) {
      e.printStackTrace();
    }
    return true;
  }

  protected abstract boolean validateContent();

}
