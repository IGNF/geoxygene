/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.mgcp;

import fr.ign.cogit.cartagen.core.defaultschema.network.Network;
import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadNode;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;

public class MGCPSchemaFactory extends AbstractCreationFactory {

  @Override
  public IRoadNode createRoadNode(Noeud noeud) {
    return new MGCPRoadNode(noeud);
  }

  @Override
  public INetwork createNetwork() {
    return new Network();
  }

  @Override
  public INetwork createNetwork(Reseau res) {
    return new Network(res);
  }

}
