/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.carto.evaluation.clutter.quadtree;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public class FeuilleQuadtree {
  public FeuilleQuadtree(int line1, int column1, int longueur, int largeur,
      BufferedImage image, FeuilleQuadtree parent) {
    super();
    this.line1 = line1;
    this.column1 = column1;
    this.longueur = longueur;
    this.largeur = largeur;
    this.image = image;
    this.parent = parent;
    this.enfants = new ArrayList<>();
  }

  public int line1;
  public int column1;
  public int longueur;
  public int largeur;
  public BufferedImage image;
  public FeuilleQuadtree parent;
  public Collection<FeuilleQuadtree> enfants;

  public void addChild(FeuilleQuadtree enfant) {
    this.enfants.add(enfant);
  }

  public boolean hasChild() {
    if (enfants.size() == 0)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ligne : " + line1 + " colonne : " + column1 + " longueur : "
        + longueur + " largeur : " + largeur;
  }
}
