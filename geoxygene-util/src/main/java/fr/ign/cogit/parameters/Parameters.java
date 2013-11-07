package fr.ign.cogit.parameters;

import java.io.File;
import java.io.StringReader;
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
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;


/**
 * A simple and generic parameter class to handle the parameters of different processes using
 * hashmaps.
 * <p>
 * Parameters can be set at runtime, but also loaded from or saved to XML files.
 * </p>
 * 
 * @author Julien Perret
 */
@XmlRootElement(name = "ParameterConfig")
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

  public static Parameters unmarshall(File file) throws Exception {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      Parameters root = (Parameters) unmarshaller.unmarshal(file);
      return root;
    } catch (Exception e1) {
      //e1.printStackTrace();
      throw e1;
    }
  }
  
  public static Parameters unmarshall(File file, String xsd) throws Exception {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      // Validation : setting a schema on the marshaller instance to activate validation against given XML schema
      unmarshaller.setSchema(SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
          .newSchema(new File(Parameters.class.getClassLoader().getResource(xsd).getPath())));
      Parameters root = (Parameters) unmarshaller.unmarshal(file);
      return root;
    } catch (Exception e1) {
      //e1.printStackTrace();
      throw e1;
    }
  }
  
  public static Parameters unmarshall(String inputXML) throws Exception {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Unmarshaller msh = context.createUnmarshaller();
      StringReader reader = new StringReader(inputXML);
      Parameters root = (Parameters) msh.unmarshal(reader);
      return root;
    } catch (Exception e1) {
      //e1.printStackTrace();
      throw e1;
    }
  }
  
  public static Parameters unmarshall(String inputXML, Schema xsdSchema) throws Exception {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Unmarshaller msh = context.createUnmarshaller();
      // Validation : setting a schema on the marshaller instance to activate validation against given XML schema
      msh.setSchema(xsdSchema);
      StringReader reader = new StringReader(inputXML);
      Parameters root = (Parameters) msh.unmarshal(reader);
      return root;
    } catch (Exception e1) {
      //e1.printStackTrace();
      throw e1;
    }
  }

  public void marshall(String file) {
    try {
      JAXBContext context = JAXBContext.newInstance(Parameters.class);
      Marshaller msh = context.createMarshaller();
      // specifies that the XML output must be indented
      msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      msh.marshal(this, new File(file));
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
