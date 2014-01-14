package fr.ign.parameters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "parameters")
public class Parameters extends ParameterComponent {
  
  @XmlElementRef(name = "parameter")
  public List<ParameterComponent> entry = new ArrayList<ParameterComponent>();
  
  @XmlAttribute(name = "description")
  public String description;
  
  /**
   * Constructor.
   */
  public Parameters() {
    this.description = "";
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void add(ParameterComponent p) {
    entry.add(p);
  }
  
  public void set(String key, Object c) {
    Parameter p = new Parameter(key, c);
    entry.add(p);
  }
  
  public Object get(String key) {
    // System.out.println("key = " + key);
    if (entry != null) {
      for (ParameterComponent paramComp : entry) {
        if (paramComp instanceof Parameter) {
          //System.out.println("parameter");
          if (((Parameter)paramComp).getKey().equals(key)) {
            //System.out.println("--" + ((Parameter)paramComp).getValue());
            return ((Parameter)paramComp).getValue();
          } else {
            //System.out.println(((Parameter)paramComp).getKey());
          }
        } else if (paramComp instanceof Parameters) {
          //System.out.println("parameters");
          Object retour = ((Parameters) paramComp).get(key);
          if (retour != null) {
            return retour;
          }
          // return ((Parameters) paramComp).get(key);
        }
      }
    }
    return null;
  }
  
  /**
   * On cherche dans les param de parametres qui a la description, celui qui a key
   * 
   * @param description
   * @param key
   * @return
   */
  public Object get(String desc, String key) {
    // System.out.println("key = " + key);
    if (entry != null) {
      for (ParameterComponent paramComp : entry) {
        if (paramComp instanceof Parameters) {
          if (((Parameters) paramComp).description.equals(desc)) {
            return ((Parameters) paramComp).get(key);
          }
        }
      }
    }
    return null;
  }
  
  public Parameters getParameters(String desc) {
    // System.out.println("key = " + key);
    if (entry != null) {
      for (ParameterComponent paramComp : entry) {
        if (paramComp instanceof Parameters) {
          if (((Parameters) paramComp).description.equals(desc)) {
            return (Parameters) paramComp;
          }
        }
      }
    }
    return null;
  }
  
  public String getString(String name) {
    Object value = this.get(name);
    if (value == null) {
      return "";
    }
    return value.toString();
  }
  
  public String getString(String desc, String name) {
    Object value = this.get(desc, name);
    if (value == null) {
      return "";
    }
    return value.toString();
  }
  
  public boolean getBoolean(String name) {
    Object value = this.get(name);
    if (value == null) {
      return false;
    }
    return Boolean.parseBoolean(value.toString());
  }
  
  public boolean getBoolean(String desc, String name) {
    Object value = this.get(desc, name);
    if (value == null) {
      return false;
    }
    return Boolean.parseBoolean(value.toString());
  }

  public double getDouble(String name) {
    Object value = this.get(name);
    if (value == null) {
      return 0;
    }
    return Double.parseDouble(value.toString());
  }

  public int getInteger(String name) {
    Object value = this.get(name);
    if (value == null) {
      return 0;
    }
    return Integer.parseInt(value.toString());
  }

  public float getFloat(String name) {
    Object value = this.get(name);
    if (value == null) {
      return 0;
    }
    return Float.parseFloat(value.toString());
  } 

}
