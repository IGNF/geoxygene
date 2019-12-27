package fr.ign.cogit.geoxygene.osm.importexport.postgis;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;

public class RelationMemberDataRetrieve {
    public String host;
    public String port;
    public String dbName;
    public String dbUser;
    public String dbPwd;
    
    private static final String OVERPASS_API = "http://www.overpass-api.de/api/interpreter";
	private static final String OPENSTREETMAP_API_06 = "http://www.openstreetmap.org/api/0.6/";
    
    public static void main(String[] args) throws Exception {
    	RelationMemberDataRetrieve retriever = new RelationMemberDataRetrieve("localhost", "5432",
                "nepal2", "postgres", "postgres");
//    	retriever.missingNodeMembers();
    	System.setProperty("proxy.ign.fr", "3128");
    	URL url = new URL("http://java.sun.com");
    	
    	System.out.println("Connexion à l'URL...");
    	HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
    	connexion.setAllowUserInteraction(true);
    	DataInputStream in = new DataInputStream(connexion.getInputStream());
    	if (connexion.getResponseCode()!= HttpURLConnection.HTTP_OK){
			System.out.println(connexion.getResponseMessage());
		} else {
			while(true){
				System.out.print((char) in.readUnsignedByte());
			}
		}
    	
    	
    	
    }
       
    public RelationMemberDataRetrieve(String host, String port, String dbName,
            String dbUser, String dbPwd) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPwd = dbPwd;
        
    }
	
	public void missingNodeMembers() throws Exception {
		System.setProperty("proxy.ign.fr", "3128");
		String myQuery = "SELECT * FROM relationmember WHERE typemb = 'Node' AND mb_exists IS FALSE;";
//		ResultSet missingNodes = selectFromDB(myQuery);
		/* Calling OSM API to get the history of all missing nodes */
		selectFromDB(myQuery);
//		while (missingNodes.next()){
//			updateNodeTable(missingNodes.getString("idmb"));
//		}
//		
	}
	public static void updateNodeTable(String nodeId) throws IOException, ParserConfigurationException, SAXException{
		String string = "http://www.openstreetmap.org/api/0.6/node/" + nodeId;
		URL osm = new URL(string);
		HttpURLConnection connexion = (HttpURLConnection) osm.openConnection();
		connexion.setAllowUserInteraction(true);
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		if (connexion.getResponseCode()!= HttpURLConnection.HTTP_OK){
			System.out.println(connexion.getResponseMessage());
		} else {
			while(true){
//				System.out.print((char) in.readUnsignedByte());
			}
		}
				
		Document document = docBuilder.parse(connexion.getInputStream());
		
	}
	public static List<OSMNode> getNodes(Document xmlDocument) {
		System.out.println("getNodes(Document xmlDocument);");
		List<OSMNode> osmNodes = new ArrayList<OSMNode>();

		// Document xml = getXML(8.32, 49.001);
		Node osmRoot = xmlDocument.getFirstChild();
		NodeList osmXMLNodes = osmRoot.getChildNodes();
		for (int i = 1; i < osmXMLNodes.getLength(); i++) {
			Node item = osmXMLNodes.item(i);
			if (item.getNodeName().equals("node")) {
				NamedNodeMap attributes = item.getAttributes();
				NodeList tagXMLNodes = item.getChildNodes();
				Map<String, String> tags = new HashMap<String, String>();
				for (int j = 1; j < tagXMLNodes.getLength(); j++) {
					System.out.println("for (int j = 1; j < tagXMLNodes.getLength(); j++) {");
					Node tagItem = tagXMLNodes.item(j);
					NamedNodeMap tagAttributes = tagItem.getAttributes();
					if (tagAttributes != null) {
						tags.put(tagAttributes.getNamedItem("k").getNodeValue(), tagAttributes.getNamedItem("v")
								.getNodeValue());
					}
				}
				Node namedItemID = attributes.getNamedItem("id");
				Node namedItemLat = attributes.getNamedItem("lat");
				Node namedItemLon = attributes.getNamedItem("lon");
				Node namedItemVersion = attributes.getNamedItem("version");

				String id = namedItemID.getNodeValue();
				String latitude = namedItemLat.getNodeValue();
				String longitude = namedItemLon.getNodeValue();
				String version = "0";
				if (namedItemVersion != null) {
					version = namedItemVersion.getNodeValue();
				}

//				osmNodes.add(new OSMNode(id, latitude, longitude, version, tags));
				System.out.println("id : "+ id +"\n(longitude,latitude): ("+ longitude+","+latitude+")\nversion: "+version);
			}

		}
		return osmNodes;
	}

	public void missingWayMembers() throws Exception {
		String myQuery = "SELECT * FROM relationmember WHERE typemb = 'Way' AND mb_exists IS FALSE;";
//		ResultSet missingWays =selectFromDB(myQuery);
		/* Calling OSM API to get the history of all missing ways */
		
	}
	public void missingRelationMembers() throws Exception {
		String myQuery = "SELECT * FROM relationmember WHERE typemb = 'Relation' AND mb_exists IS FALSE;";
//		ResultSet missingRel =selectFromDB(myQuery);
		
	}
    public void selectFromDB(String query)
            throws Exception {
        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + this.host + ":" + this.port
                    + "/" + this.dbName;
            conn = DriverManager.getConnection(url, this.dbUser, this.dbPwd);
            Statement s = conn.createStatement();
            System.out.println(" Statement s = conn.createStatement();");
            ResultSet r = s.executeQuery(query);
            System.out.println("ResultSet r = s.executeQuery(query);");

//            return r;
//            System.out.println("------- Query Executed -------");
    		while (r.next()){
    			updateNodeTable(r.getString("idmb"));
    		}
            s.close();
            conn.close();
            
        } catch (Exception e) {
            throw e;
        }
    }

	public static String getOverpassApi() {
		return OVERPASS_API;
	}

	public static String getOpenstreetmapApi06() {
		return OPENSTREETMAP_API_06;
	}	
}