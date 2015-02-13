/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.carto.evaluation.clutter.subbandentropy;

import org.apache.log4j.Logger;

public class ClutterBywavelet {

  private static final Logger logger = Logger.getLogger(ClutterBywavelet.class
      .getName());

  public ClutterBywavelet() {
  }

  /*
   * compression suivant l axe vertical
   */
  public ImageBis[] WaveletCompressionX(ImageBis img) {

    logger.debug("on entre dans la compression suivant les x");

    ImageBis tab[] = {};
    int nbLine = img.len;
    int nbColumn = img.width;

    // creation des deux images
    ImageBis moyenne = new ImageBis();
    ImageBis erreur = new ImageBis();

    moyenne.cell = new float[nbLine][nbColumn][3];
    moyenne.len = nbLine;
    moyenne.width = nbColumn;

    erreur.cell = new float[nbLine][nbColumn][3];
    erreur.len = nbLine;
    erreur.width = nbColumn;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine; i++) {
        for (int j = 0; j < nbColumn - 1; j++) {
          moyenne.cell[i][j][k] = (img.cell[i][j][k] + img.cell[i][j + 1][k]) / 2; // moyenne
          erreur.cell[i][j][k] = Math
              .abs((img.cell[i][j][k] - img.cell[i][j + 1][k]) / 2); // erreur

        }// for j
      }// for i
    }// for les trois canaux

    tab = new ImageBis[2];
    tab[0] = moyenne;

    tab[1] = erreur;

    logger.debug("on sort de la compression suivant les x");

    return tab;
  }// WaveletCompressionX

  /*
   * compression suivant l axe horizontal
   */
  public ImageBis[] WaveletCompressionY(ImageBis img) // where is string ??
  {

    logger.debug(img + "on entre dans la compression suivant les y");

    ImageBis tab[] = {};
    int nbLine = img.len;
    int nbColumn = img.width;

    // creation des deux images
    ImageBis moyenne = new ImageBis();
    ImageBis erreur = new ImageBis();

    moyenne.cell = new float[nbLine][nbColumn][3];
    moyenne.len = nbLine;
    moyenne.width = nbColumn;

    erreur.cell = new float[nbLine][nbColumn][3];
    erreur.len = nbLine;
    erreur.width = nbColumn;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine - 1; i++) {
        for (int j = 0; j < nbColumn; j++) {
          moyenne.cell[i][j][k] = (img.cell[i][j][k] + img.cell[i + 1][j][k]) / 2; // moyenne
          erreur.cell[i][j][k] = Math
              .abs((img.cell[i][j][k] - img.cell[i + 1][j][k])) / 2; // erreur
        }// for j
      }// for i
    }// for les trois bandes

    tab = new ImageBis[2];
    tab[0] = moyenne;
    tab[1] = erreur;

    logger.debug(img + "on sort de la compression suivant les y");

    return tab;
  }// WaveletCompressionY

}// ClutterBywavelet
