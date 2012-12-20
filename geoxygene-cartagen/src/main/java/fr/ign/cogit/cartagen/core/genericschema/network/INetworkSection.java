/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.network;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;

/*
 * ###### IGN / CartAGen ###### Title: Cable Description: Sections de réseau:
 * routier, ferré, hydro ou électrique Author: J. Renard Date: 01/07/2010
 */
public interface INetworkSection extends IGeneObjLin {

  /**
   * @return
   */
  public int getImportance();

  /**
   * @param importance
   */
  public void setImportance(int importance);

  /**
   * Width of the symbol on the map, in mm
   * @return
   */
  public double getWidth();

  public double getInternWidth();

  /**
   * 
   * @param at
   * @return
   */
  public boolean isAnalog(INetworkSection at);

  /**
   * @return la direction du troncon
   */
  public Direction getDirection();

  public void setDirection(Direction direction);

  /**
   * @return le noeud initial de l'arc
   */
  public INetworkNode getInitialNode();

  public void setInitialNode(INetworkNode node);

  /**
   * @return le noeud final de l'arc
   */
  public INetworkNode getFinalNode();

  public void setFinalNode(INetworkNode node);

  public boolean isDeadEnd();

  public void setDeadEnd(boolean deadEnd);

  /**
   * @return les faces autour de l'arc
   */
  public INetworkFace getLeftFace();

  public INetworkFace getRightFace();

  /**
   * Get the section type that classifies dead ends, bridges to dead ends,
   * isolated sections and normal sections.
   * 
   * @return
   * @author GTouya
   */
  public NetworkSectionType getNetworkSectionType();

  public void setNetworkSectionType(NetworkSectionType type);

  public static final String FEAT_TYPE_NAME = "NetworkSection"; //$NON-NLS-1$

}
