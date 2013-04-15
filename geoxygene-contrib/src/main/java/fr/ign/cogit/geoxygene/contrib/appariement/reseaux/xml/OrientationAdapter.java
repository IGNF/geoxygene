package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * 
 *
 */
public class OrientationAdapter extends XmlAdapter<String, Map<Integer, String>> {
  
  
  public Map<Integer, String> unmarshal(String toto) throws Exception {
    // TODO : à implémenter
    return null;
  }
  
  public String marshal(Map<Integer, String> res) throws Exception {
    // TODO : à implémenter
    return "";
  }

}
