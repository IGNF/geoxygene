/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.sources;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.function.LinearFunction;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.punctual.EuclidianDist;
import fr.ign.cogit.geoxygene.matching.dst.sources.text.LevenshteinDist;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;



/**
 * A source expressing its belief on geometries.
 * @author Bertrand Dumenieu
 */
@XmlRootElement(name = "GeoSource")
public abstract class GeoSource implements Source<IFeature, GeomHypothesis> {
  
  @XmlTransient
  protected String name = this.getClass().getName().substring(
      this.getClass().getName().lastIndexOf('.') + 1);
  
  /** . */
  private Function1D[] masseAppCi;
  
  /** . */
  private Function1D[] masseAppPasCi;
  
  /** . */
  private Function1D[] masseIgnorance;

  /**
   * Default constructor.
   */
  public GeoSource() {
    super();
  }
  
  public void setMasseAppCi(Function1D... masseAppCi) {
    this.masseAppCi = masseAppCi;
  }
  
  @XmlElementWrapper(name = "MasseAppCi")
  @XmlAnyElement
  public Function1D[] getMasseAppCi() {
    return this.masseAppCi;
  }
  
  public void setMasseAppPasCi(Function1D... masseAppPasCi) {
    this.masseAppPasCi = masseAppPasCi;
  }
  
  @XmlElementWrapper
  @XmlAnyElement
  public Function1D[] getMasseAppPasCi() {
    return this.masseAppPasCi;
  }
  
  public void setMasseIgnorance(Function1D... masseIgnorance) {
    this.masseIgnorance = masseIgnorance;
  }
  
  @XmlElementWrapper
  @XmlAnyElement
  public Function1D[] getMasseIgnorance() {
    return this.masseIgnorance;
  }
  
  @Override
  public String toString() {
    return this.getName();
  }

  

  /**
   * Source name is used to identify hypothessi values.
   * @return
   */
  @Override
  public String getName() {
    return "Default Source";
  }

  /**
   * @param candidates
   * @param encoded
   */
  @Override
  public List<Pair<byte[], Float>> evaluate(IFeature reference, final List<GeomHypothesis> candidates,
      EvidenceCodec<GeomHypothesis> codec) {
    return null;
  }
  
  @Override
  public double[] evaluate(IFeature reference, final GeomHypothesis candidates) {
    return null;
  }
  
  public double[] getMasses(double distance) {
 
    double[] masses = new double[3];
    
    // Fonction EstApparie
    float masse1 = 0.0f;
    if (masseAppCi != null) {
      for (Function1D f : masseAppCi) {
        if (f.isBetween(distance)) {
          try {
            masse1 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[0] = masse1;

    // Fonction NonApparie
    float masse2 = 0.0f;
    if (masseAppPasCi != null) {
      for (Function1D f : masseAppPasCi) {
        if (f.isBetween(distance)) {
          try {
            masse2 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[1] = masse2;

    // Fonction PrononcePas
    float masse3 = 0.0f;
    if (masseIgnorance != null) {
      for (Function1D f : masseIgnorance) {
        if (f.isBetween(distance)) {
          try {
            masse3 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[2] = masse3;

    return masses;
  }
  
  public void marshall() {
    try {
      JAXBContext jc = JAXBContext.newInstance(GeoSource.class, EuclidianDist.class, LevenshteinDist.class,
          LinearFunction.class, ConstantFunction.class);
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.marshal(this, System.out);
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  public void marshall(String filename) {
    try {
      JAXBContext context = JAXBContext.newInstance(GeoSource.class, EuclidianDist.class, LevenshteinDist.class,
          LinearFunction.class, ConstantFunction.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this, new File(filename));
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  public void marshall(StringWriter w) {
    try {
      JAXBContext context = JAXBContext.newInstance(GeoSource.class, EuclidianDist.class, LevenshteinDist.class,
          LinearFunction.class, ConstantFunction.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(this, w);
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }
  
  public static GeoSource unmarshall(File file) throws Exception { 
    try {
      JAXBContext context = JAXBContext.newInstance(GeoSource.class, EuclidianDist.class, LevenshteinDist.class,
          LinearFunction.class, ConstantFunction.class);
      Unmarshaller unmarshaller = context.createUnmarshaller(); 
      GeoSource root = (GeoSource) unmarshaller.unmarshal(file); 
      return root; 
    } catch (Exception e1) { 
      e1.printStackTrace(); throw e1; 
    } 
  }
  
  public static GeoSource unmarshall(String inputXML) throws Exception { 
    try { 
      JAXBContext context = JAXBContext.newInstance(GeoSource.class, EuclidianDist.class, LevenshteinDist.class,
          LinearFunction.class, ConstantFunction.class);
      Unmarshaller msh = context.createUnmarshaller(); 
      StringReader reader = new StringReader(inputXML); 
      GeoSource root = (GeoSource)msh.unmarshal(reader); 
      return root; 
    } catch (Exception e1) {
      e1.printStackTrace(); 
      throw e1;
    }
  }
  
}
