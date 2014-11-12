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
package fr.ign.cogit.geoxygene.function;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

/** 
 *
 */
public abstract class AbstractFunction1D implements Function1D {
  
  /** The lower bound of the domain of the function. */
  @XmlAttribute(name = "lowerBoundDF")
  private double lowerBoundDF;
  
  /** The upper bound of the domain of the function. */
  @XmlAttribute(name = "upperBoundDF")
  private double upperBoundDF;
  
  /** If lower bound is accepted in domain of the function. */
  @XmlAttribute(name = "withMatchLowerBound")
  private boolean withMatchLowerBound;
  
  /** If upper bound is accepted in domain of the function. */
  @XmlAttribute(name = "withMatchUpperBound")
  private boolean withMatchUpperBound;

  /**
   * Default constructor.
   */
  public AbstractFunction1D() {
  }

  @Override
  public abstract String help();

  /**
   * Set Domain of Function.
   * 
   * @param binf : borne inferieur
   * @param bsup
   * @param leftEq
   * @param rightEq
   */
  public void setDomainOfFunction(Double binf, double bsup, boolean minf, boolean msup) {
    this.lowerBoundDF = binf;
    this.upperBoundDF = bsup;
    this.withMatchLowerBound = minf;
    this.withMatchUpperBound = msup;
  }
  
  public double getLowerBoundDF() {
    return this.lowerBoundDF;
  }

  public double getUpperBoundDF() {
    return this.upperBoundDF;
  }
  
  public boolean getWithMatchLowerBound() {
    return this.withMatchLowerBound;
  }
  
  public boolean getWithMatchUpperBound() {
    return this.withMatchUpperBound;
  }

  /**
   * TODO : il y a surement mieux.
   */ 
  @Override
  public boolean isBetween(double d) {
    if (withMatchLowerBound) {
      if (withMatchUpperBound) {
        return (this.lowerBoundDF <= d && d <= this.upperBoundDF);
      } else {
        return (this.lowerBoundDF <= d && d < this.upperBoundDF);
      }
    } else {
      if (withMatchUpperBound) {
        return (this.lowerBoundDF < d && d <= this.upperBoundDF);
      } else {
        return (this.lowerBoundDF < d && d < this.upperBoundDF);
      }
    } 
  }
  
  public void marshall() {
    try {
      JAXBContext jc = JAXBContext.newInstance(ConstantFunction.class,
          LinearFunction.class, IdentityFunction.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(this, System.out);
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  public void marshall(String filename) {
    try {
      JAXBContext context = JAXBContext.newInstance(ConstantFunction.class,
          LinearFunction.class, IdentityFunction.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this, new File(filename));
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  public void marshall(StringWriter w) {
    try {
      JAXBContext context = JAXBContext.newInstance(ConstantFunction.class,
          LinearFunction.class, IdentityFunction.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this, w);
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  /**
   * Unmarshal XML data from the specified Functions file.
   * @param XML data file
   * @return the resulting content tree in Functions List
   * @throws Exception
   */
  public static AbstractFunction1D unmarshall(File file) throws Exception { 
    try {
      JAXBContext context = JAXBContext.newInstance(ConstantFunction.class,
          LinearFunction.class, IdentityFunction.class);
      Unmarshaller unmarshaller = context.createUnmarshaller(); 
      AbstractFunction1D root = (AbstractFunction1D) unmarshaller.unmarshal(file); 
      return root; 
    } catch (Exception e1) { 
      e1.printStackTrace(); throw e1; 
    } 
  }
  
  /**
   * Unmarshal data from XML text.
   * @param inputXML
   * @return the resulting content tree in Functions List
   * @throws Exception
   */
  public static AbstractFunction1D unmarshall(String inputXML) throws Exception { 
    try { 
      JAXBContext context = JAXBContext.newInstance(ConstantFunction.class,
          LinearFunction.class, IdentityFunction.class);
      Unmarshaller msh = context.createUnmarshaller(); 
      StringReader reader = new StringReader(inputXML); 
      AbstractFunction1D root = (AbstractFunction1D)msh.unmarshal(reader); 
      return root; 
    } catch (Exception e1) {
      e1.printStackTrace(); 
      throw e1;
    }
  }

}
