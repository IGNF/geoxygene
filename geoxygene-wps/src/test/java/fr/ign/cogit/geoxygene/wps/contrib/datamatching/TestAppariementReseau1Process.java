package fr.ign.cogit.geoxygene.wps.contrib.datamatching;

import org.junit.Test;
import org.junit.Assert;

import fr.ign.cogit.geoxygene.http.SimpleHttpClient;

/**
 * 
 * 
 *
 */
public class TestAppariementReseau1Process {
    
    @Test
    public void TestJeu1() {
        
        StringBuffer result = new StringBuffer();
        
        result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        result.append("<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">");
        
        result.append("<ows:Identifier>gs:AppariementReseau1</ows:Identifier>");
        result.append("<wps:DataInputs>");
        
        result.append("<wps:Input>");
        result.append("<ows:Identifier>param1</ows:Identifier>");
        result.append("<wps:Data>");
        result.append("<wps:ComplexData mimeType=\"text/xml\">");
        String xmlCompare1 = "<![CDATA[" 
                // + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching>"
                + "<DistanceNoeudsMax>150.0</DistanceNoeudsMax>"
                + "<DistanceNoeudsImpassesMax>-1.0</DistanceNoeudsImpassesMax>"
                + "<DistanceArcsMax>100.0</DistanceArcsMax>"
                + "<DistanceArcsMin>30.0</DistanceArcsMin>"
                + "</fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching>"
                + "]]>";
        result.append(xmlCompare1);
        result.append("</wps:ComplexData>");
        result.append("</wps:Data>");
        result.append("</wps:Input>");
        
        result.append("<wps:Input>");
        result.append("<ows:Identifier>reseau1</ows:Identifier>");
        result.append("<wps:Reference mimeType=\"text/xml\" xlink:href=\"http://geoserver/wfs\" method=\"POST\">");
        result.append("<wps:Body>");
        result.append("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\" xmlns:WS_User-1=\"WS_User-1\">");
        result.append("<wfs:Query typeName=\"WS_User-1:bdcarto_route\"/>");
        result.append("</wfs:GetFeature>");
        result.append("</wps:Body>");
        result.append("</wps:Reference>");
        result.append("</wps:Input>");
        
        result.append("<wps:Input>");
        result.append("<ows:Identifier>reseau2</ows:Identifier>");
        result.append("<wps:Reference mimeType=\"text/xml\" xlink:href=\"http://geoserver/wfs\" method=\"POST\">");
        result.append("<wps:Body>");
        result.append("<wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\" xmlns:WS_User-1=\"WS_User-1\">");
        result.append("<wfs:Query typeName=\"WS_User-1:bdtopo_route\"/>");
        result.append("</wfs:GetFeature>");
        result.append("</wps:Body>");
        result.append("</wps:Reference>");
        result.append("</wps:Input>");
        
        result.append("</wps:DataInputs>");
          
        result.append("<wps:ResponseForm>");
        result.append("<wps:RawDataOutput>");
        result.append("<ows:Identifier>popApp</ows:Identifier>");
        result.append("</wps:RawDataOutput>");
        result.append("</wps:ResponseForm>");
        
        result.append("</wps:Execute>");
        
        try {
            
            String serviceAppariement = "http://127.0.0.1:8094/geoserver/wps/";
             
            // On se connecte
            System.out.println("Connexion a geoserver : " + serviceAppariement);
            SimpleHttpClient client = new SimpleHttpClient(serviceAppariement);
            client.connect("POST");
            
            // On envoie
            String xml = result.toString();
            // System.out.println(xml);
            client.post(xml);
            
            // On receptionne
            String response = client.getResponse();
            System.out.println(response);
            
            // On se deconnecte
            System.out.println("Deconnexion a geoserver.");
            client.disconnect();
            
        } catch (Exception e) {
            e.printStackTrace();
            // throw e;
        }
        
        Assert.assertTrue(true);
    }

}
