/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

/*
 * ###### IGN / CartAGen ###### Title: IEdge Description: An edge of a Graph
 * object. Author: J. Renard Version: 1.0 Changes: 1.0 (13/01/12) : creation
 */

public interface ITriangleFace extends IFace {

  public INode getNode1();

  public INode getNode2();

  public INode getNode3();

}
