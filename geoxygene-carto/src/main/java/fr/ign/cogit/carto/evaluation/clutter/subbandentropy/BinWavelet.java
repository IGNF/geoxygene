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

/*
 * creation de l histogramme des images
 */
public class BinWavelet {

  private static final Logger logger = Logger.getLogger(BinWavelet.class
      .getName());

  public BinWavelet() {
  }

  /*
   * cette fonction permet la creation des histogrammes des images prend une
   * image en entree, ressort un tableau histo : tableau de 256 lignes et 3
   * colonnes; une colone par canal
   */

  public int[][] makeHisto(ImageBis imag) {
    logger.debug("calcul de l histo...");

    int histo[][] = new int[256][3];

    // canal rouge
    for (int i = 0; i < imag.len; i++) {
      for (int j = 0; j < imag.width; j++) {
        float pos = imag.cell[i][j][0];

        histo[(int) pos][0]++;

      }// for j
    }// for i

    // System.out.println(histo[40][0]);

    // canal vert
    for (int i = 0; i < imag.len; i++) {
      for (int j = 0; j < imag.width; j++) {
        float pos = imag.cell[i][j][1];
        histo[(int) pos][1]++;
      }// for j
    }// for i

    // System.out.println(histo[40][1]);

    // canal bleu
    for (int i = 0; i < imag.len; i++) {
      for (int j = 0; j < imag.width; j++) {
        float pos = imag.cell[i][j][2];
        histo[(int) pos][2]++;
      }// for j
    }// for i

    // System.out.println(histo[40][2]);

    logger.debug("histo ok");

    return histo;
  }// makeHisto

  /*
   * calcul l entropie de shannon pour une image dont on a l histogramme
   */
  public double shanonEntro(ImageBis imag, int[][] hist) {
    logger.debug("calcul entropie de shanon...");

    double shanonr = 0;
    double shanong = 0;
    double shanonb = 0;
    double shanon = 0;

    int nbPixel = imag.len * imag.width;
    // System.out.println(nbPixel);

    // pour le canal rouge
    for (int i = 0; i < 255; i++) {
      double num = (double) hist[i][0];
      double denom = (double) nbPixel;
      if ((denom != 0) && ((num / denom) > 0.000000001)) {
        shanonr = shanonr + (-(hist[i][0] / denom) * Math.log10((num / denom)));
        System.out.println(hist[i][0]);

      } else if ((denom != 0) && ((num / denom) < 0.000000001)) {
        continue;

      } else {
        logger.error("division par zero !!! ");
      }

    }// for i

    // pour le canal vert
    for (int i = 0; i < 255; i++) {
      double num = (double) hist[i][1];
      double denom = (double) nbPixel;
      if ((denom != 0) && ((num / denom) > 0.000000001)) {
        shanong = shanong
            + (-(hist[i][0] / nbPixel) * Math.log10((num / denom)));

      } else if ((denom != 0) && ((num / denom) < 0.000000001)) {
        continue;

      } else {
        logger.error("division par zero !!! ");
      }

    }// for i

    // pour le canal bleu
    for (int i = 0; i < 255; i++) {
      double num = (double) hist[i][0];
      double denom = (double) nbPixel;
      if ((denom != 0) && ((num / denom) > 0.000000001)) {
        shanonb = shanonb
            + (-(hist[i][0] / nbPixel) * Math.log10((num / denom)));
      } else if ((denom != 0) && ((num / denom) < 0.000000001)) {
        continue;

      } else {
        logger.error("division par zero !!! ");
      }

    }// for i

    shanon = Math.abs(shanonr + shanong + shanonb);

    logger.debug("entropie ok");

    return shanon;
  }// shanonEntro

}// class BinWavelet
