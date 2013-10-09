package fr.ign.cogit.geoxygene.matching;

import java.util.HashMap;
import java.util.Map;

public class Parameters {
  Map<String, Object> map = new HashMap<String, Object>();

  public void set(String name, Object value) {
    this.map.put(name, value);
  }

  public boolean getBoolean(String name) {
    Object value = this.map.get(name);
    if (value == null || !(value instanceof Boolean)) {
      return false;
    }
    return ((Boolean) value).booleanValue();
  }

  public Object get(String name) {
    return this.map.get(name);
  }

  public double getDouble(String name) {
    Object value = this.map.get(name);
    if (value == null || !(value instanceof Double)) {
      return 0;
    }
    return ((Double) value).doubleValue();
  }
  public int getInteger(String name) {
    Object value = this.map.get(name);
    if (value == null || !(value instanceof Integer)) {
      return 0;
    }
    return ((Integer) value).intValue();
  }
  public String getString(String name) {
    Object value = this.map.get(name);
    if (value == null) {
      return "";
    }
    return value.toString();
  }
}
