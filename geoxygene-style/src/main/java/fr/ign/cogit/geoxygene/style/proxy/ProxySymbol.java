package fr.ign.cogit.geoxygene.style.proxy;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

/**
 * The ProxySymbol defines pre-processed symbolization attributes (such as a
 * specific color of with for each feature) to be used for styling.
 * 
 * @author Charlotte Hoarau
 * 
 */
public class ProxySymbol {

  @SuppressWarnings("unused")
  private static final Logger logger = Logger.getLogger(ProxySymbol.class);

  @XmlElement(name = "ProxyColorPropertyName")
  String proxyColorPropertyName = null;

  @XmlTransient
  public String getProxyColorPropertyName() {
    return this.proxyColorPropertyName;
  }

  public void setProxyColorPropertyName(String proxyColorPropertyName) {
    this.proxyColorPropertyName = proxyColorPropertyName;
  }

}
