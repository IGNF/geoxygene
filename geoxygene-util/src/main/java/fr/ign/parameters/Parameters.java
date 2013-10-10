package fr.ign.parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * A simple and generic parameter class to handle the parameters of different processes using
 * hashmaps.
 * <p>
 * Parameters can be set at runtime, but also loaded from or saved to XML files.
 * @author Julien Perret
 */
@XmlRootElement(name = "parameter-configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters {
  @XmlJavaTypeAdapter(MapAdapter.class)
  @XmlElement(name = "parameters")
  Map<String, Object> map;

  public Parameters() {
    this.map = new HashMap<String, Object>();
  }

  public Object get(String key) {
    return this.map.get(key);
  }

  public void set(String key, Object c) {
    this.map.put(key, c);
  }

  public boolean getBoolean(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return false;
    }
    return Boolean.parseBoolean(value.toString());
  }

  public double getDouble(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return 0;
    }
    return Double.parseDouble(value.toString());
  }

  public int getInteger(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return 0;
    }
    return Integer.parseInt(value.toString());
  }

  public String getString(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return "";
    }
    return value.toString();
  }

  public float getFloat(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return 0;
    }
    return Float.parseFloat(value.toString());
  }

  public static Parameters unmarshall(String file) {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Unmarshaller m = context.createUnmarshaller();
      Parameters root = (Parameters) m.unmarshal(new File(file));
      return root;
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
    return null;
  }

  public void marshall(String file) {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, new File(file));
    } catch (JAXBException e1) {
      e1.printStackTrace();
    }
  }

  public static class MapAdapter extends XmlAdapter<MyHashMapType, Map<String, Object>> {
    public MapAdapter() {
      super();
    }

    @Override
    public MyHashMapType marshal(Map<String, Object> v) throws Exception {
      MyHashMapType result = new MyHashMapType(v);
      return result;
    }

    @Override
    public Map<String, Object> unmarshal(MyHashMapType v) throws Exception {
      Map<String, Object> result = new HashMap<String, Object>();
      for (MyHashMapEntryType entry : v.entry) {
        result.put(entry.key, entry.value);
      }
      return result;
    }
  };

  public static class MyHashMapType {
    @XmlElement(name = "param")
    public List<MyHashMapEntryType> entry = new ArrayList<MyHashMapEntryType>();

    public MyHashMapType(Map<String, Object> map) {
      for (Map.Entry<String, Object> e : map.entrySet())
        entry.add(new MyHashMapEntryType(e));
    }

    public MyHashMapType() {
    }
  }

  public static class MyHashMapEntryType {
    @XmlAttribute
    public String key;

    @XmlAttribute
    public String value;

    public MyHashMapEntryType() {
    }

    public MyHashMapEntryType(Map.Entry<String, Object> e) {
      key = e.getKey();
      value = e.getValue().toString();
    }
  };
}
