package fr.ign.parameters;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "param")
public class Parameter extends ParameterComponent {
  
  @XmlAttribute(name = "key")
  private String key;
  
  @XmlJavaTypeAdapter(ValueMapAdapter.class)
  @XmlAttribute(name = "value")
  private Object value;
  
  public Parameter() {
  }
  
  public Parameter(String k, Object v) {
    this.key = k;
    this.value = v;
  }
  
  public String getKey() {
    return key;
  }
  
  public Object getValue() {
    return value;
  }
  
}
