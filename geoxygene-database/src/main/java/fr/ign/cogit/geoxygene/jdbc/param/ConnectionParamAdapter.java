package fr.ign.cogit.geoxygene.jdbc.param;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 *
 */
public class ConnectionParamAdapter {
   
    /**
     * 
     */
    /*public ConnectionParam[] marshal(Map<String, String> arg0) throws Exception {
        
        if (arg0 != null) {
        
            ConnectionParam[] mapElements = new ConnectionParam[arg0.size()];
       
          int i = 0;
          for (Map.Entry<Integer, String> entry : arg0.entrySet()) {
            // System.out.println("map element = " + entry.getKey() + " : " + entry.getValue());
              ConnectionParam element = new ConnectionParam(entry.getKey(), entry.getValue());
            mapElements[i] = element;
            i++;
          }
       
          return mapElements;
        
        } else {
          return null;
        }

      }*/
    
    /**
     * 
     */
    /*public Map<String, String> unmarshal(ConnectionParam[] arg0) throws Exception  {
      
      if (arg0 != null) {
        Map<Integer, String> r = new HashMap<Integer, String>();
        for(ConnectionParam mapElement : arg0) {
          r.put(mapElement.sens, mapElement.attribut);
        }
        return r;
      } 
      
      return null;
    }*/

}

/**
 * 
 * <OrientationElement SENS="1">direct</OrientationElement>
 *
 */
/*@XmlRootElement(name = "ConnectionParam")
class ConnectionParam {
  
    @XmlElement(name = "dbtype") 
    public String dbtype;
  
    @XmlElement(name = "host") 
    public String host;
    
    @XmlElement(name = "port") 
    public String port;
    
    @XmlElement(name = "database") 
    public String database;
    
    @XmlElement(name = "schema") 
    public String schema;
    
    @XmlElement(name = "user") 
    public String user;
    
    @XmlElement(name = "passwd") 
    public String passwd;
  
    // Required by JAXB
    private ConnectionParam() {} 
 
    //
    //public ConnectionParam(int key, String value) {
        //this.sens   = key;
        //this.attribut = value;
    //}

}*/
