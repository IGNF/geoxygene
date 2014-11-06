package fr.ign.cogit.geoxygene.function;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/** 
 *
 */
@XmlRootElement(name = "Function1D")
@XmlType(name = "Function1D")
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
      JAXBContext jc = JAXBContext.newInstance(AbstractFunction1D.class, ConstantFunction.class,
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
      JAXBContext context = JAXBContext.newInstance(AbstractFunction1D.class, ConstantFunction.class,
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
      JAXBContext context = JAXBContext.newInstance(AbstractFunction1D.class, ConstantFunction.class,
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
      JAXBContext context = JAXBContext.newInstance(AbstractFunction1D.class, ConstantFunction.class,
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
      JAXBContext context = JAXBContext.newInstance(AbstractFunction1D.class, ConstantFunction.class,
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
