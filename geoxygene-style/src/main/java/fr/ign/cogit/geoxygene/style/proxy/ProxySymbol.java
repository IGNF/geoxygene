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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.proxyColorPropertyName == null) ? 0
                        : this.proxyColorPropertyName.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProxySymbol other = (ProxySymbol) obj;
        if (this.proxyColorPropertyName == null) {
            if (other.proxyColorPropertyName != null) {
                return false;
            }
        } else if (!this.proxyColorPropertyName
                .equals(other.proxyColorPropertyName)) {
            return false;
        }
        return true;
    }

}
