package fr.ign.parameters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "param")
public class Parameter extends ParameterComponent {
  
  @XmlAttribute(name = "key")
  private String key;
  
  @XmlAttribute(name = "value")
  private String value;
  
  public Parameter() {
  }
  
  public Parameter(String k, String v) {
    this.key = k;
    this.value = v;
  }
  
  /* public Param(Map.Entry<String, Object> e) {
    key = e.getKey();
    value = e.getValue().toString();
  } */
  
  public String getKey() {
    return key;
  }
  
  public String getValue() {
    return value;
  }
  
}
