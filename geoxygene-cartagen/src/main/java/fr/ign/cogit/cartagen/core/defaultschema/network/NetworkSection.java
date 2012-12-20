/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.network;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.network.NetworkSectionType;

/*
 * ###### IGN / CartAGen ###### Title: WaterLine Description: Tronçons de
 * réseau: routier, ferré, hydro ou électrique Author: J. Renard Date:
 * 18/09/2009
 */

public abstract class NetworkSection extends GeneObjLinDefault implements
    INetworkSection {

  private int importance;

  @Override
  public int getImportance() {
    return this.importance;
  }

  @Override
  public void setImportance(int importance) {
    this.importance = importance;
  }

  @Override
  public boolean isAnalog(INetworkSection at) {
    return this.importance == at.getImportance();
  }

  private INetworkFace leftFace;

  @Override
  public INetworkFace getLeftFace() {
    return this.leftFace;
  }

  public void setLeftFace(INetworkFace leftFace) {
    this.leftFace = leftFace;
  }

  private INetworkFace rightFace;

  @Override
  public INetworkFace getRightFace() {
    return this.rightFace;
  }

  public void setRightFace(INetworkFace rightFace) {
    this.rightFace = rightFace;
  }

  private NetworkSectionType networkSectionType = NetworkSectionType.UNKNOWN;

  @Override
  public NetworkSectionType getNetworkSectionType() {
    return this.networkSectionType;
  }

  @Override
  public void setNetworkSectionType(NetworkSectionType type) {
    this.networkSectionType = type;
  }

}
