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

import java.util.Collection;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/*
 * ###### IGN / CartAGen ###### Title: Node Description: Noeuds de réseaux
 * Author: J. Renard Date: 01/07/2010
 */
public interface INetworkNode extends IGeneObjPoint {

  public IDirectPosition getPosition();

  public double getWidth();

  /**
   * @return les arcs entrant sur le noeud
   */
  public Collection<INetworkSection> getInSections();

  public void setInSections(Collection<INetworkSection> inSections);

  /**
   * @return les arcs sortant du noeud
   */
  public Collection<INetworkSection> getOutSections();

  public void setOutSections(Collection<INetworkSection> outSections);

  /**
   * @return le degre du noeud
   */
  public int getDegree();

  /**
   * @return the maximum importance of all sections connected to the node
   */
  public int getSectionsMaxImportance();

  /**
   * @return the max width of the symbol
   */
  public SymbolShape getMaxWidthSymbol();

  public static final String FEAT_TYPE_NAME = "NetworkNode"; //$NON-NLS-1$

}
