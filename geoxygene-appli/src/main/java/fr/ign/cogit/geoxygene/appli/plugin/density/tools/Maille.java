/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * Objet Maille crée pour la classe Grid
 * 
 * @see Grid
 * @author Simon
 *
 */
public class Maille extends Vector<IDirectPosition>{
  
  private static final long serialVersionUID = 1L;
  
  private int row;
  private int col;
  
  /**
   * Construit l'objet à partir des numeros de ligne et de colonne
   * @param row - numero de la ligne
   * @param col - numero de la colonne
   */
  public Maille(int row, int col) {
    super();
    this.row = row;
    this.col = col;
  }

  /**
   * @return le numero de la ligne
   */
  public int getRow() {
    return row;
  }
  
  /**
   * @return le numero de la colonne
   */
  public int getCol() {
    return col;
  }
  
  @Override
  public synchronized String toString() {
    return "row: "+row+" col: "+col+" n: "+size();
  }
  
  
}
