package fr.ign.parameters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 *
 */
public class ValueMapAdapter extends XmlAdapter<String, Object> {
  
  public ValueMapAdapter() {
    super();
  }
  
  @Override
  public String marshal(Object parameter) throws Exception {
    return parameter.toString();
  }

  /**
   * 
   */
  @Override
  public Object unmarshal(String arg0) throws Exception {
    return arg0;
  }

}
