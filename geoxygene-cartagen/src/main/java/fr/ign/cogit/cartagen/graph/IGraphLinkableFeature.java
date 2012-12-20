/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import java.util.ArrayList;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author JGaffuri
 */
public interface IGraphLinkableFeature {

  public abstract INode getReferentGraphNode();

  /**
   * @param agentPointReferant
   */
  public abstract void setReferentGraphNode(INode referentGraphNode);

  public abstract ArrayList<IEdge> getProximitySegments();

  public void clean();

  public IGeneObj getFeature();

  public IGeometry getSymbolGeom();

  public double getSymbolArea();

}
