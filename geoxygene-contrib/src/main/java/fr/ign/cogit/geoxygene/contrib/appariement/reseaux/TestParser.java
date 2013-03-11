package fr.ign.cogit.geoxygene.contrib.appariement.reseaux;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.NetworkElementInterface;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkElement;

public class TestParser {
  
  public static void main(String[] args) {
    
    try {
      
      JAXBContext context = JAXBContext.newInstance(ResultNetwork.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    
      ResultNetwork resnet = new ResultNetwork();
      ResultNetworkElement res = new ResultNetworkElement(NetworkElementInterface.NONE);
      res.setTotalNetworkElementNumber(-10);
      res.setCorrectMatchingNetworkElementNumber(-100);
      res.setNoMatchingNetworkElementNumber(-1000);
      res.setDoubtfulNetworkElementNumber(-10000);
      resnet.setEdgesEvaluationComp(res);
      
      m.marshal(resnet, System.out);
    
    } catch (JAXBException ex) {
      ex.printStackTrace();
    }
  }

}
