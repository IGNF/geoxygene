package fr.ign.cogit.geoxygene.schemageo.impl.support.reseau;

import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseauFlagPair;

/**
 * @author JTeulade-Denantes
 * 
 * This class allows to have a flag related to an arcReseau
 * It's used in Stroke class to get arcReseau direction into the stroke 
 */
public class ArcReseauFlagPairImpl implements ArcReseauFlagPair {

  public ArcReseauFlagPairImpl(ArcReseau arcReseau, boolean flag) {
    super();
    this.arcReseau = arcReseau;
    this.flag = flag;
  }
  
  public ArcReseauFlagPairImpl(ArcReseau arcReseau) {
    super();
    this.arcReseau = arcReseau;
    this.flag = true;
  }

  private ArcReseau arcReseau;
  private boolean flag;
  
  public ArcReseau getArcReseau() {
    return arcReseau;
  }
  public void setArcReseau(ArcReseau arcReseau) {
    this.arcReseau = arcReseau;
  }
  public boolean getFlag() {
    return flag;
  }
  public void setFlag(boolean flag) {
    this.flag = flag;
  }
  
}
