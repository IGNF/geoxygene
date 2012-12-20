/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.relief;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.ITriangleFace;

/*
 * ###### IGN / CartAGen ###### Title: reliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public interface IReliefTriangle extends IGeneObjSurf, ITriangleFace {

  /**
   * gets the positions of the 3 points of the triangle
   * @return
   */
  public INode getPt1();

  public INode getPt2();

  public INode getPt3();

  public static final String FEAT_TYPE_NAME = "ReliefTriangle"; //$NON-NLS-1$
}
