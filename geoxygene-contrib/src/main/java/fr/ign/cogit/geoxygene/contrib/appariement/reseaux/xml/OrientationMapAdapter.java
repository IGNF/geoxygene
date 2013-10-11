package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * 
 *
 */
public class OrientationMapAdapter extends XmlAdapter<OrientationElement[], Map<Integer, String>> {
  
  /**
   * 
   */
  @Override
  public OrientationElement[] marshal(Map<Integer, String> arg0) throws Exception {
    
    if (arg0 != null) {
      OrientationElement[] mapElements = new OrientationElement[arg0.size()];
   
      int i = 0;
      for (Map.Entry<Integer, String> entry : arg0.entrySet()) {
        // System.out.println("map element = " + entry.getKey() + " : " + entry.getValue());
        OrientationElement element = new OrientationElement(entry.getKey(), entry.getValue());
        mapElements[i] = element;
        i++;
      }
   
      return mapElements;
    
    } else {
      return null;
    }

  }
 
  
  /**
   * 
   */
  @Override
  public Map<Integer, String> unmarshal(OrientationElement[] arg0) throws Exception  {
    
    if (arg0 != null) {
      Map<Integer, String> r = new HashMap<Integer, String>();
      for(OrientationElement mapElement : arg0) {
        r.put(mapElement.sens, mapElement.attribut);
      }
      return r;
    } 
    
    return null;
  }

}

/**
 * 
 * <OrientationElement SENS="1">direct</OrientationElement>
 *
 */
@XmlRootElement(name = "OrientationElement")
class OrientationElement {
  
  @XmlAttribute(name = "SENS") 
  public int sens;
  
  @XmlElement(name = "ValeurAttribut")
  public String attribut;
  
  // Required by JAXB
  private OrientationElement() {} 
 
  //
  public OrientationElement(int key, String value) {
    this.sens   = key;
    this.attribut = value;
  }

}
