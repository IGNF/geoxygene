package fr.ign.cogit.geoxygene.schemageo.api.support.reseau;

/**
 * 
 * @author JTeulade-Denantes
 *
 */
public interface ArcReseauFlagPair {
  
  /**
   * 
   * @return l'arcReseau
   */
  public ArcReseau getArcReseau() ;
  
  /**
   * 
   * @param arcReseau
   */
  public void setArcReseau(ArcReseau arcReseau) ;
  
  /**
   * 
   * @return le flag
   */
  public boolean getFlag();
  
  /**
   * 
   * @param flag
   */
  public void setFlag(boolean flag);
  
}
