/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL_V2-fr.txt
 *        see Licence_CeCILL_V2-en.txt
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
 * 
 * 
 * @copyright IGN
 *
 */
package fr.ign.cogit.geoxygene.client.service;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.http.SimpleHttpClient;


/**
 * 
 *
 */
public class DemonstrateurServiceAppariement {
  
  // The thread running this service 
  protected Thread thread = null;
  
  /**
   * The logger used to log the errors or several information.
   * 
   * @see org.apache.log4j.Logger
   */
  protected final Logger logger = Logger.getLogger(this.getClass());

   /**
    * Constructeur par défaut.
    */
   public DemonstrateurServiceAppariement() {
     super();
   }

   /**
    * Constructeur.
    * @param t : 
    */
   public DemonstrateurServiceAppariement(Thread t) {
     super();
     thread = t;
   }
   
   /**
    * 
    * @param d
    */
   public void doCalcul(String d) { 
     
     StringBuffer result = new StringBuffer();
     
     result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
     result.append("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">");   
     
     result.append("<ows:Identifier>gs:ImportAppariement</ows:Identifier>");
     result.append("<wps:DataInputs>");
     
     result.append("<wps:Input>");
     result.append("<ows:Identifier>userId</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>8</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
     
     result.append("<wps:Input>");
     result.append("<ows:Identifier>datasetIdApparie</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>504</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
     
     result.append("<wps:Input>");
     result.append("<ows:Identifier>datasetIdOrigine</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>845</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
     
     result.append("<wps:Input>");
     result.append("<ows:Identifier>layerIdAppariement</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>TEST05</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
         
     result.append("<wps:Input>");
     result.append("<ows:Identifier>resultAppariement</ows:Identifier>");
     result.append("<wps:Reference mimeType=\"text/xml\" xlink:href=\"http://geoserver/wps\" method=\"POST\">");
     result.append("<wps:Body>");
     result.append("<wps:Execute version=\"1.0.0\" service=\"WPS\">");
     result.append("<ows:Identifier>gs:AppariementReseau</ows:Identifier>");
     result.append("<wps:DataInputs>");
     
     result.append("<wps:Input>");
     result.append("<ows:Identifier>popRef</ows:Identifier>");
     result.append("<wps:Reference mimeType=\"text/xml\" xlink:href=\"http://geoserver/wps\" method=\"POST\">");
     result.append("<wps:Body>");
     result.append("<wps:Execute version=\"1.0.0\" service=\"WPS\">");
     result.append("<ows:Identifier>gs:Reproject</ows:Identifier>");
     result.append("<wps:DataInputs>");
     result.append("<wps:Input>");
     result.append("<ows:Identifier>features</ows:Identifier>");
     result.append("<wps:Reference mimeType=\"text/xml\" xlink:href=\"http://geoserver/wfs\" method=\"POST\">");
     result.append("<wps:Body>");
     result.append("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\" xmlns:WS_User-8=\"http://WS_User-8\">");
     result.append("<wfs:Query typeName=\"WS_User-8:i-f62579c0-824d-11e2-b63d-00199968a6ba\"/>");
     result.append("</wfs:GetFeature>");
     result.append("</wps:Body>");
     result.append("</wps:Reference>");
     result.append("</wps:Input>");
                 
     result.append("<wps:Input>");
     result.append("<ows:Identifier>forcedCRS</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>EPSG:3857</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
                   
     result.append("<wps:Input>");
     result.append("<ows:Identifier>targetCRS</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>EPSG:2154</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
               
     result.append("</wps:DataInputs>");
     
     result.append("<wps:ResponseForm>");
     result.append("<wps:RawDataOutput mimeType=\"text/xml; subtype=wfs-collection/1.0\">");
     result.append("<ows:Identifier>result</ows:Identifier>");
     result.append("</wps:RawDataOutput>");
     result.append("</wps:ResponseForm>");
               
     result.append("</wps:Execute>");
     result.append("</wps:Body>");
     result.append("</wps:Reference>");
     result.append("</wps:Input>");
           
     result.append("<wps:Input>");
     result.append("<ows:Identifier>distanceNoeudsMax</ows:Identifier>");
     result.append("<wps:Data>");
     result.append("<wps:LiteralData>30</wps:LiteralData>");
     result.append("</wps:Data>");
     result.append("</wps:Input>");
     
     result.append("</wps:DataInputs>");
     result.append("<wps:ResponseForm>");
     result.append("<wps:RawDataOutput mimeType=\"text/xml\">");
     result.append("<ows:Identifier>popApp</ows:Identifier>");
     result.append("</wps:RawDataOutput>");
     result.append("</wps:ResponseForm>");
     result.append("</wps:Execute>");
     result.append("</wps:Body>");
     result.append("</wps:Reference>");
     result.append("</wps:Input>");
     result.append("</wps:DataInputs>");
     result.append("<wps:ResponseForm>");
     result.append("<wps:RawDataOutput>");
     result.append("<ows:Identifier>userLayerApp</ows:Identifier>");
     result.append("</wps:RawDataOutput>");
     result.append("</wps:ResponseForm>");
     result.append("</wps:Execute>");
     
     String xml = result.toString();
     
     String serviceAppariement = "http://hinano.ign.fr:8081/geoserver/wps/";
     
     try {
     
       // On se connecte
       logger.info("Connexion a geoserver : " + serviceAppariement);
       SimpleHttpClient client = new SimpleHttpClient(serviceAppariement);
       client.connect("POST");
       
       // On envoie
       logger.debug(xml);
       client.post(xml);
       
       // On receptionne
       String response = client.getResponse();
       logger.debug(response);
       
       // On se deconnecte
       logger.info("Deconnexion a geoserver.");
       client.disconnect();
     
     } catch (Exception e) {
       logger.error("Erreur dans le service d'appariement.");
       e.printStackTrace();
     }
   
   }

}
