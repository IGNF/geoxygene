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

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import java.util.Stack;

public class QuadtreeClutterMethod {
  private BufferedImage image;

  public QuadtreeClutterMethod(BufferedImage image) {
    super();
    this.image = image;
  }

  /**
   * Transformer une image colorée en Niveau de gris.
   */
  public BufferedImage toGray() {
    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
    ColorConvertOp op = new ColorConvertOp(cs, null);

    return op.filter(image, null);
  }

  public void diviseFeuille(FeuilleQuadtree feuille,
      Stack<FeuilleQuadtree> pile, ArrayList<FeuilleQuadtree> liste) {
    int col;
    int lin;
    int seuil = 1;
    boolean sortir = false;

    // parcours de l'image, comparaison de la valeur du pixel par rapport à ses
    // 8 voisins

    for (col = feuille.column1 + 1; col < feuille.column1 + feuille.longueur
        - 1; col++) {
      for (lin = feuille.line1 + 1; lin < feuille.line1 + feuille.largeur - 1; lin++) {
        // si la différence est supérieure au seuil, on crée 4 feuilles enfants
        if (Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
            - (feuille.image.getRGB(col - 1, lin) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col, lin - 1) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col + 1, lin) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col, lin + 1) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col - 1, lin - 1) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col + 1, lin + 1) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col - 1, lin + 1) & 0xFF)) > seuil
            || Math.abs((feuille.image.getRGB(col, lin) & 0xFF)
                - (feuille.image.getRGB(col + 1, lin - 1) & 0xFF)) > seuil) {
          // création des nouvelles dimensions des feuilles créées
          feuille.longueur = feuille.longueur / 2;
          feuille.largeur = feuille.largeur / 2;
          // création des nouvelles feuilles
          FeuilleQuadtree feuille1 = new FeuilleQuadtree(feuille.line1,
              feuille.column1, feuille.longueur, feuille.largeur,
              feuille.image, feuille);
          FeuilleQuadtree feuille2 = new FeuilleQuadtree(feuille.line1,
              feuille.column1 + feuille.longueur, feuille.longueur,
              feuille.largeur, feuille.image, feuille);
          FeuilleQuadtree feuille3 = new FeuilleQuadtree(feuille.line1
              + feuille.largeur, feuille.column1, feuille.longueur,
              feuille.largeur, feuille.image, feuille);
          FeuilleQuadtree feuille4 = new FeuilleQuadtree(feuille.line1
              + feuille.largeur, feuille.column1 + feuille.longueur,
              feuille.longueur, feuille.largeur, feuille.image, feuille);
          // ajout des 4 enfants
          feuille.addChild(feuille1);
          feuille.addChild(feuille2);
          feuille.addChild(feuille3);
          feuille.addChild(feuille4);
          // ajout des feuilles enfants à la liste
          liste.add(feuille1);
          liste.add(feuille2);
          liste.add(feuille3);
          liste.add(feuille4);
          pile.push(feuille1);
          pile.push(feuille2);
          pile.push(feuille3);
          pile.push(feuille4);
          sortir = true;
          break;
        }
      }
      if (sortir)
        break;
    }
  }

  public void Traitement_feuilles(Stack<FeuilleQuadtree> pile,
      ArrayList<FeuilleQuadtree> liste) {
    while (!pile.empty()) {
      FeuilleQuadtree f;
      f = pile.pop();
      diviseFeuille(f, pile, liste);
    }
  }

}
