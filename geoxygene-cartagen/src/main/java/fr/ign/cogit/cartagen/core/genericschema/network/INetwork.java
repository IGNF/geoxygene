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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;

/*
 * ###### IGN / CartAGen ###### Title: Network Description: Réseau Author: J.
 * Renard Date: 02/01/2012
 */

public interface INetwork extends IGeneObj {

  /**
   * @return Topological map of the network
   */
  public CarteTopo getCarteTopo();

  public void setCarteTopo(CarteTopo carteTopo);

  /**
   * @return Sections of the network
   */
  public IFeatureCollection<INetworkSection> getSections();

  public IFeatureCollection<INetworkSection> getNonDeletedSections();

  public void setSections(IFeatureCollection<INetworkSection> sections);

  public void addSection(INetworkSection section);

  public void removeSection(INetworkSection section);

  /**
   * @return Nodes of the network
   */
  public IFeatureCollection<INetworkNode> getNodes();

  public void setNodes(IFeatureCollection<INetworkNode> nodes);

  public void addNode(INetworkNode node);

  public void removeNode(INetworkNode node);

  public void removeAllNodes();

  /**
   * @return Faces of the network
   */
  public IFeatureCollection<INetworkFace> getFaces();

  public void setFaces(IFeatureCollection<INetworkFace> faces);

  public void addFace(INetworkFace face);

  public void removeFace(INetworkFace face);

  public static final String FEAT_TYPE_NAME = "Network"; //$NON-NLS-1$

}
