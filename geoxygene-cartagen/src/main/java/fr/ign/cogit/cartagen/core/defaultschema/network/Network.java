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

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Noeuds de réseau
 * Author: J. Renard Date: 18/09/2009
 */

public class Network extends GeneObjDefault implements INetwork {

  private Reseau geoxObj;

  private CarteTopo carteTopo;

  private IFeatureCollection<INetworkSection> sections;
  private IFeatureCollection<INetworkNode> nodes;
  private IFeatureCollection<INetworkFace> faces;

  public Network(Reseau res) {
    super();
    this.geoxObj = res;
    this.setInitialGeom(this.geoxObj.getGeom());
    this.setEliminated(false);
    this.sections = new FT_FeatureCollection<INetworkSection>();
    this.nodes = new FT_FeatureCollection<INetworkNode>();
    this.faces = new FT_FeatureCollection<INetworkFace>();
  }

  public Network() {
    super();
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public CarteTopo getCarteTopo() {
    return this.carteTopo;
  }

  @Override
  public void setCarteTopo(CarteTopo carteTopo) {
    this.carteTopo = carteTopo;
  }

  @Override
  public IFeatureCollection<INetworkSection> getSections() {
    return this.sections;
  }

  /**
   * Returns the non deleted sections of a network
   * @return
   */
  @Override
  public IFeatureCollection<INetworkSection> getNonDeletedSections() {
    Population<INetworkSection> tronconsNonSup = new Population<INetworkSection>();
    for (INetworkSection tr : this.getSections()) {
      if (!tr.isDeleted()) {
        tronconsNonSup.add(tr);
      }
    }
    return tronconsNonSup;
  }

  @Override
  public void setSections(IFeatureCollection<INetworkSection> sections) {
    this.sections = sections;
  }

  @Override
  public void addSection(INetworkSection section) {
    this.sections.add(section);
  }

  @Override
  public void removeSection(INetworkSection section) {
    this.sections.remove(section);
  }

  @Override
  public IFeatureCollection<INetworkNode> getNodes() {
    return this.nodes;
  }

  @Override
  public void setNodes(IFeatureCollection<INetworkNode> nodes) {
    this.nodes = nodes;
  }

  @Override
  public void addNode(INetworkNode node) {
    this.nodes.add(node);
  }

  @Override
  public void removeNode(INetworkNode node) {
    this.nodes.remove(node);
  }

  @Override
  public IFeatureCollection<INetworkFace> getFaces() {
    return this.faces;
  }

  @Override
  public void setFaces(IFeatureCollection<INetworkFace> faces) {
    this.faces = faces;
  }

  @Override
  public void addFace(INetworkFace face) {
    this.faces.add(face);
  }

  @Override
  public void removeFace(INetworkFace face) {
    this.faces.remove(face);
  }

  @Override
  public void removeAllNodes() {
    Set<INetworkNode> copySet = new HashSet<INetworkNode>(this.getNodes());
    for (INetworkNode node : copySet)
      this.removeNode(node);
  }

}
