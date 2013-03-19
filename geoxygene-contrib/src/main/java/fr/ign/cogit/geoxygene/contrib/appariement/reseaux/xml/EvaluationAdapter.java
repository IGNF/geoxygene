package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkElementInterface;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkElement;

/**
 *
 */
public class EvaluationAdapter extends XmlAdapter<String, ResultNetworkElement> {
  
  /**
   * TODO : implementation
   */
  public ResultNetworkElement unmarshal(String toto) throws Exception {
    ResultNetworkElement res = new ResultNetworkElement(ResultNetworkElementInterface.NONE);
    res.setTotalNetworkElementNumber(-1);
    res.setCorrectMatchingNetworkElementNumber(-1);
    res.setNoMatchingNetworkElementNumber(-1);
    res.setDoubtfulNetworkElementNumber(-1);
    return res;
  }
  
  
  public String marshal(ResultNetworkElement res) throws Exception {
    String chaine_a_retourner = "toto s'en va en guerre";
    return chaine_a_retourner;
  }
}
