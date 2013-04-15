package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paramFilenameNetwork1",
    "paramFilenameNetwork2",
    "paramNetworkDataMatching"
})
@XmlRootElement(name = "ParamPluginNetworkDataMatching")
public class ParamPluginNetworkDataMatching {
  
  /** Fichiers qui contiennent les shapes du réseau 1. */
  @XmlElement(name = "ParamFilenameNetwork1")
  private ParamFilenameNetworkDataMatching paramFilenameNetwork1 = null;
  
  /** Fichiers qui contiennent les shapes du réseau 2. */
  @XmlElement(name = "ParamFilenameNetwork2")
  private ParamFilenameNetworkDataMatching paramFilenameNetwork2 = null;
  
  /** Paramètres pour l'appariement. */
  @XmlElement(name = "ParamNetworkDataMatching")
  private ParamNetworkDataMatching paramNetworkDataMatching = null;
  
  /**
   * Default constructor.
   */
  public ParamPluginNetworkDataMatching() {
    paramFilenameNetwork1 = new ParamFilenameNetworkDataMatching();
    paramFilenameNetwork2 = new ParamFilenameNetworkDataMatching();
    paramNetworkDataMatching = new ParamNetworkDataMatching();
  }
  
  public ParamFilenameNetworkDataMatching getParamFilenameNetwork1() {
    return paramFilenameNetwork1;
  }
  
  public ParamFilenameNetworkDataMatching getParamFilenameNetwork2() {
    return paramFilenameNetwork2;
  }
  
  public ParamNetworkDataMatching getParamNetworkDataMatching() {
    return paramNetworkDataMatching;
  }
  
  public void setParamFilenameNetwork1(ParamFilenameNetworkDataMatching pf) {
    paramFilenameNetwork1 = pf;
  }
  
  public void setParamFilenameNetwork2(ParamFilenameNetworkDataMatching pf) {
    paramFilenameNetwork2 = pf;
  }
  
  public void setParamNetworkDataMatching(ParamNetworkDataMatching p) {
    paramNetworkDataMatching = p;
  }

}
