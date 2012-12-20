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

import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkFace;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;

/*
 * ###### IGN / CartAGen ###### Title: WaterPoint Description: Faces de réseau
 * Author: J. Renard Date: 18/09/2009
 */

public class NetworkFace extends GeneObjSurfDefault implements INetworkFace {

  private Face geoxObj;
  private Collection<INetworkSection> sections;

  /**
   * Constructor
   */
  public NetworkFace(Face geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
    this.sections = new HashSet<INetworkSection>();
  }

  public NetworkFace(IPolygon poly) {
    super();
    this.geoxObj = new Face();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
    this.sections = new HashSet<INetworkSection>();
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public Collection<INetworkSection> getSections() {
    return this.sections;
  }

  public void setSections(Collection<INetworkSection> sections) {
    this.sections = sections;
  }

  public void addToSections(INetworkSection section) {
    this.sections.add(section);
  }

}
