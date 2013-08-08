package fr.ign.cogit.geoxygene.contrib.appariement;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.junit.Test;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopologyTreatmentNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;

/**
 * 
 * 
 *
 */
public class ParamParserTest extends XMLTestCase {
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ParamParserTest.class.getName());
  
  /**
   * 
   * @throws Exception
   */
  @Test
  public void testParamTopologyTreatmentNetwork() throws Exception {

    JAXBContext context = JAXBContext.newInstance(ParamTopologyTreatmentNetwork.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    StringWriter xmlResult = new StringWriter();
    
    LOGGER.debug("-------------------------------------------------------------");
    LOGGER.debug("ParamTopologyTreatmentNetwork ");
    
    // Test 1 : default param
    LOGGER.debug("-------------------------------------------------------------");
    LOGGER.debug("    ParamTopologyTreatmentNetwork - default");
    ParamTopologyTreatmentNetwork paramTopo1 = new ParamTopologyTreatmentNetwork();
    m.marshal(paramTopo1, xmlResult);
    LOGGER.debug(xmlResult.toString());
    String xmlCompare1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<ParamTopoTreatmentNetworkDataMatching>\n"
        + "    <SeuilFusionNoeuds>-1.0</SeuilFusionNoeuds>\n"
        + "    <ElimineNoeudsAvecDeuxArcs>false</ElimineNoeudsAvecDeuxArcs>\n"
        + "    <GraphePlanaire>false</GraphePlanaire>\n"
        + "    <FusionArcsDoubles>false</FusionArcsDoubles>\n"
        + "</ParamTopoTreatmentNetworkDataMatching>\n";
    LOGGER.debug(xmlCompare1);
    // Compare 2 xmls
    assertXMLEqual(xmlResult.toString(), xmlCompare1);
    
    // Test 2 : 
    LOGGER.debug("-------------------------------------------------------------");
    LOGGER.debug("    ParamTopologyTreatmentNetwork - (10, true, true, true)");
    xmlResult = new StringWriter();
    ParamTopologyTreatmentNetwork paramTopo2 = new ParamTopologyTreatmentNetwork();
    paramTopo2.setSeuilFusionNoeuds(10);
    paramTopo2.setGraphePlanaire(true);
    paramTopo2.setElimineNoeudsAvecDeuxArcs(true);
    paramTopo2.setFusionArcsDoubles(true);
    m.marshal(paramTopo2, xmlResult);
    LOGGER.debug(xmlResult.toString());
    String xmlCompare2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<ParamTopoTreatmentNetworkDataMatching>\n"
        + "    <SeuilFusionNoeuds>10.0</SeuilFusionNoeuds>\n"
        + "    <ElimineNoeudsAvecDeuxArcs>true</ElimineNoeudsAvecDeuxArcs>\n"
        + "    <GraphePlanaire>true</GraphePlanaire>\n"
        + "    <FusionArcsDoubles>true</FusionArcsDoubles>\n"
        + "</ParamTopoTreatmentNetworkDataMatching>\n";
    LOGGER.debug(xmlCompare2);
    // Compare 2 xmls
    assertXMLEqual(xmlResult.toString(), xmlCompare2);
    
  }
  
  /**
   * 
   * @throws Exception
   */
  @Test
  public void testParamDistanceNetworkDataMatching() throws Exception {
      
      JAXBContext context = JAXBContext.newInstance(ParamDistanceNetworkDataMatching.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      StringWriter xmlResult = new StringWriter();
      
      LOGGER.debug("-------------------------------------------------------------");
      LOGGER.debug("ParamDistanceNetworkDataMatching ");
      
      // Test 1 : default param
      LOGGER.debug("-------------------------------------------------------------");
      LOGGER.debug("    ParamDistanceNetworkDataMatching - default");
      ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
      m.marshal(paramDistance, xmlResult);
      LOGGER.debug(xmlResult.toString());
      String xmlCompare1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
          + "<ParamDistanceNetworkDataMatching>\n"
          + "    <DistanceNoeudsMax>150.0</DistanceNoeudsMax>\n"
          + "    <DistanceArcsMax>100.0</DistanceArcsMax>\n"
          + "    <DistanceArcsMin>30.0</DistanceArcsMin>\n"
          + "    <DistanceNoeudsImpassesMax>-1.0</DistanceNoeudsImpassesMax>\n"
          + "</ParamDistanceNetworkDataMatching>\n";
      LOGGER.debug(xmlCompare1);
      // Compare 2 xmls
      assertXMLEqual(xmlResult.toString(), xmlCompare1);
      
  }

}
