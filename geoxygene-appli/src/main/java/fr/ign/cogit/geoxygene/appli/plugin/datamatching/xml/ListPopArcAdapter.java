package fr.ign.cogit.geoxygene.appli.plugin.datamatching.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import fr.ign.cogit.geoxygene.appli.plugin.datamatching.data.ParamFilenameNetworkDataMatching;



/**
 * 
 *
 */
public class ListPopArcAdapter extends XmlAdapter<String, ParamFilenameNetworkDataMatching> {

  /**
   * TODO : à implementer.
   */
  public ParamFilenameNetworkDataMatching unmarshal(String toto) throws Exception {
    
    ParamFilenameNetworkDataMatching res = new ParamFilenameNetworkDataMatching();
    /*ResultNetworkStatElement res = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NONE);
    res.setTotalNetworkElementNumber(-1);
    res.setCorrectMatchingNetworkElementNumber(-1);
    res.setNoMatchingNetworkElementNumber(-1);
    res.setDoubtfulNetworkElementNumber(-1);*/
    return res;
  }
  
  
  public String marshal(ParamFilenameNetworkDataMatching res) throws Exception {
    String chaine_a_retourner = "toto s'en va en guerre";
    return chaine_a_retourner;
  }
}
