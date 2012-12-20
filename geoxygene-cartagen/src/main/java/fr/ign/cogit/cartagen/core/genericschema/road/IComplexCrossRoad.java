/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.road;

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;

public interface IComplexCrossRoad extends IGeneObjSurf {

  public Set<IRoadLine> getInternalRoads();

  public void setInternalRoads(Set<IRoadLine> internalRoads);

  public Set<IRoadLine> getExternalRoads();

  public void setExternalRoads(Set<IRoadLine> externalRoads);

  public Set<INetworkNode> getSimples();

  public void setSimples(Set<INetworkNode> simples);
}
