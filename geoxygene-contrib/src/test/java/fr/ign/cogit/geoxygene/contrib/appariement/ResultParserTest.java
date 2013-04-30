package fr.ign.cogit.geoxygene.contrib.appariement;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElementInterface;

/**
 * Tests sur le parsing des classes de résultats en XML. - Test 1 : quelques
 * chiffres quelconques - Test 2 : classe vide
 */
public class ResultParserTest extends XMLTestCase {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ResultParserTest.class.getName());

  @Test
  public void testResultNetworkStat1() throws Exception {

    JAXBContext context = JAXBContext.newInstance(ResultNetworkStat.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    ResultNetworkStat resnet = new ResultNetworkStat();
    // EDGES_OF_NETWORK_1
    ResultNetworkStatElement res = new ResultNetworkStatElement(
        ResultNetworkStatElementInterface.EDGES_OF_NETWORK_1);
    res.setTotalNetworkElementNumber(621);
    res.setTotalNetworkElementLength(5432.33);
    res.setCorrectMatchingNetworkElementNumber(463);
    res.setCorrectedMatchingNetworkElementLength(102.36);
    res.setNoMatchingNetworkElementNumber(149);
    res.setNoMatchingNetworkElementLength(40.002);
    res.setDoubtfulNetworkElementNumber(9);
    res.setDoubtfulNetworkElementLength(0.55);
    resnet.setStatsEdgesOfNetwork1(res);
    // EDGES_OF_NETWORK_2
    res = new ResultNetworkStatElement(
        ResultNetworkStatElementInterface.EDGES_OF_NETWORK_2);
    res.setTotalNetworkElementNumber(1621);
    res.setTotalNetworkElementLength(15432.33);
    res.setCorrectMatchingNetworkElementNumber(1463);
    res.setCorrectedMatchingNetworkElementLength(1102.36);
    res.setNoMatchingNetworkElementNumber(1149);
    res.setNoMatchingNetworkElementLength(140.002);
    res.setDoubtfulNetworkElementNumber(19);
    res.setDoubtfulNetworkElementLength(10.55);
    resnet.setStatsEdgesOfNetwork2(res);
    // NODES_OF_NETWORK_1
    res = new ResultNetworkStatElement(
        ResultNetworkStatElementInterface.NODES_OF_NETWORK_1);
    res.setTotalNetworkElementNumber(21);
    res.setCorrectMatchingNetworkElementNumber(63);
    res.setNoMatchingNetworkElementNumber(49);
    res.setDoubtfulNetworkElementNumber(0);
    resnet.setStatsNodesOfNetwork1(res);
    // NODES_OF_NETWORK_2
    res = new ResultNetworkStatElement(
        ResultNetworkStatElementInterface.NODES_OF_NETWORK_2);
    res.setTotalNetworkElementNumber(12);
    res.setCorrectMatchingNetworkElementNumber(24);
    res.setNoMatchingNetworkElementNumber(36);
    res.setDoubtfulNetworkElementNumber(48);
    resnet.setStatsNodesOfNetwork2(res);

    //
    java.io.StringWriter xmlResult = new StringWriter();
    m.marshal(resnet, xmlResult);
    LOGGER.debug(xmlResult.toString());

    String xmlATrouver = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<ResultStatNetwork>\n"
        + "    <StatsEdgesOfNetwork1 NetworkElement=\"EdgesOfNetwork1\">\n"
        + "        <TotalNetworkElementNumber>621</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>5432.33</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>463</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>102.36</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>149</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>40.002</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>9</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.55</DoubtfulNetworkElementLength>\n"
        + "    </StatsEdgesOfNetwork1>\n"
        + "    <StatsNodesOfNetwork1 NetworkElement=\"NodesOfNetwork1\">\n"
        + "        <TotalNetworkElementNumber>21</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>63</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>49</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>0</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsNodesOfNetwork1>\n"
        + "    <StatsEdgesOfNetwork2 NetworkElement=\"EdgesOfNetwork2\">\n"
        + "        <TotalNetworkElementNumber>1621</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>15432.33</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>1463</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>1102.36</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>1149</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>140.002</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>19</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>10.55</DoubtfulNetworkElementLength>\n"
        + "    </StatsEdgesOfNetwork2>\n"
        + "    <StatsNodesOfNetwork2 NetworkElement=\"NodesOfNetwork2\">\n"
        + "        <TotalNetworkElementNumber>12</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>24</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>36</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>48</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsNodesOfNetwork2>\n" + "</ResultStatNetwork>\n";
    LOGGER.debug(xmlATrouver);

    // Compare 2 xmls
    assertXMLEqual(xmlResult.toString(), xmlATrouver);

  }

  @Test
  public void testResultNetworkStat2() throws Exception {

    JAXBContext context = JAXBContext.newInstance(ResultNetworkStat.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    ResultNetworkStat resnet = new ResultNetworkStat();

    //
    java.io.StringWriter xmlResult = new StringWriter();
    m.marshal(resnet, xmlResult);
    LOGGER.debug(xmlResult.toString());

    String xmlATrouver = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<ResultStatNetwork>\n"
        + "    <StatsEdgesOfNetwork1 NetworkElement=\"EdgesOfNetwork1\">\n"
        + "        <TotalNetworkElementNumber>0</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>0</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>0</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>0</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsEdgesOfNetwork1>\n"
        + "    <StatsNodesOfNetwork1 NetworkElement=\"NodesOfNetwork1\">\n"
        + "        <TotalNetworkElementNumber>0</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>0</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>0</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>0</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsNodesOfNetwork1>\n"
        + "    <StatsEdgesOfNetwork2 NetworkElement=\"EdgesOfNetwork2\">\n"
        + "        <TotalNetworkElementNumber>0</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>0</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>0</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>0</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsEdgesOfNetwork2>\n"
        + "    <StatsNodesOfNetwork2 NetworkElement=\"NodesOfNetwork2\">\n"
        + "        <TotalNetworkElementNumber>0</TotalNetworkElementNumber>\n"
        + "        <TotalNetworkElementLength>0.0</TotalNetworkElementLength>\n"
        + "        <CorrectMatchingNetworkElementNumber>0</CorrectMatchingNetworkElementNumber>\n"
        + "        <CorrectedMatchingNetworkElementLength>0.0</CorrectedMatchingNetworkElementLength>\n"
        + "        <NoMatchingNetworkElementNumber>0</NoMatchingNetworkElementNumber>\n"
        + "        <NoMatchingNetworkElementLength>0.0</NoMatchingNetworkElementLength>\n"
        + "        <DoubtfulMatchingNumber>0</DoubtfulMatchingNumber>\n"
        + "        <DoubtfulNetworkElementLength>0.0</DoubtfulNetworkElementLength>\n"
        + "    </StatsNodesOfNetwork2>\n" + "</ResultStatNetwork>\n";
    LOGGER.debug(xmlATrouver);

    // Compare 2 xmls
    assertXMLEqual(xmlResult.toString(), xmlATrouver);

  }

  /*@Test
  public void testResultNetworkDataMatching1() throws Exception {

    JAXBContext context = JAXBContext.newInstance(ResultNetworkDataMatching.class);
    Marshaller m = context.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

    ResultNetworkDataMatching resnet = new ResultNetworkDataMatching();

    //
    java.io.StringWriter xmlResult = new StringWriter();
    m.marshal(resnet, xmlResult);
    LOGGER.debug(xmlResult.toString());
  }*/

}
