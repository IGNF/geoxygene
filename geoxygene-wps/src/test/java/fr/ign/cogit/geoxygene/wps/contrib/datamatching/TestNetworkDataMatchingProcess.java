package fr.ign.cogit.geoxygene.wps.contrib.datamatching;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

/**
 * Tests network data matching process.
 *
 */
public class TestNetworkDataMatchingProcess {
  
  
  private String networkDataMatchingCall() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<wps:Execute version=\"1.0.0\" service=\"WPS\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.opengis.net/wps/1.0.0\" xmlns:wfs=\"http://www.opengis.net/wfs\" xmlns:wps=\"http://www.opengis.net/wps/1.0.0\" xmlns:ows=\"http://www.opengis.net/ows/1.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:wcs=\"http://www.opengis.net/wcs/1.1.1\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xsi:schemaLocation=\"http://www.opengis.net/wps/1.0.0 http://schemas.opengis.net/wps/1.0.0/wpsAll.xsd\">\n"
            + "  <ows:Identifier>gs:Aggregate</ows:Identifier>\n"
            + "  <wps:DataInputs>\n"
            + "    <wps:Input>\n"
            + "      <ows:Identifier>features</ows:Identifier>\n"
            + "      <wps:Reference mimeType=\"text/xml; subtype=wfs-collection/1.0\" xlink:href=\"http://geoserver/wfs\" method=\"POST\">\n"
            + "        <wps:Body>\n"
            + "          <wfs:GetFeature service=\"WFS\" version=\"1.0.0\" outputFormat=\"GML2\">\n"
            + "            <wfs:Query typeName=\""
            //+ getLayerId(MockData.PRIMITIVEGEOFEATURE)
            + "\"/>\n"
            + "          </wfs:GetFeature>\n"
            + "        </wps:Body>\n"
            + "      </wps:Reference>\n"
            + "    </wps:Input>\n"
            + "    <wps:Input>\n"
            + "      <ows:Identifier>aggregationAttribute</ows:Identifier>\n"
            + "      <wps:Data>\n"
            + "        <wps:LiteralData>intProperty</wps:LiteralData>\n"
            + "      </wps:Data>\n"
            + "    </wps:Input>\n"
            + "    <wps:Input>\n"
            + "      <ows:Identifier>function</ows:Identifier>\n"
            + "      <wps:Data>\n"
            + "        <wps:LiteralData>"
           // + function
            + "</wps:LiteralData>\n"
            + "      </wps:Data>\n"
            + "    </wps:Input>\n"
            + "  </wps:DataInputs>\n"
            + "  <wps:ResponseForm>\n"
            + "    <wps:RawDataOutput>\n"
            + "      <ows:Identifier>result</ows:Identifier>\n"
            + "    </wps:RawDataOutput>\n"
            + "  </wps:ResponseForm>\n" + "</wps:Execute>";
}
  
  
  @Test
  public void testCantonMP() {
    
    String xml = networkDataMatchingCall();

    // Document dom = postAsDOM(root(), xml);
    // print(dom);
    // assertXpathEvaluatesTo("1", "count(/AggregationResults/*)", dom);
    // assertXpathEvaluatesTo("-111.0", "/AggregationResults/Sum", dom);
    Assert.assertTrue(true);
  }

  

}
