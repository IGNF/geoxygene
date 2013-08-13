package fr.ign.cogit.geoxygene.jdbc.postgis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dbtype",
    "host",
    "port",
    "database",
    "schema",
    "user",
    "passwd"
})
@XmlRootElement(name = "ConnectionParam")
public class ConnectionParam {
    
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
    
    /**
     * Default constructor.
     */
    public ConnectionParam() {
        dbtype = "postgis";
        host = "localhost";
        port = "5432";
        database = "postgres";
        schema = "public";
        user = "test";
        passwd = "test";
    }
    
    public String getDbtype() {
        return dbtype;
    }
    
    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getDatabase() {
        return database;
    }

    public String getSchema() {
        return schema;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPasswd() {
        return passwd;
    }
}