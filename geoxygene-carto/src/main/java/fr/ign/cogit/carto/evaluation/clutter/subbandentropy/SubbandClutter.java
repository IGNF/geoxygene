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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class SubbandClutter {

  private static final Logger logger = Logger.getLogger(ClutterBywavelet.class
      .getName());

  public SubbandClutter() {
  }

  public double appelParCanal(BufferedImage image) {

    // Creation de l image ImageBis
    ImageBis image1 = new ImageBis(image);

    // tableaux de stockage
    ImageBis tab1[];
    ImageBis tab2[];
    ImageBis tab3[];

    ImageBis f1 = new ImageBis();
    ImageBis fh = new ImageBis();
    ImageBis f11 = new ImageBis();
    ImageBis fhh = new ImageBis();
    ImageBis f1h = new ImageBis();
    ImageBis fh1 = new ImageBis();

    // creation d une classe ClutterBywavelet
    ClutterBywavelet clutts = new ClutterBywavelet();

    // ON APPLIQUE LA COMPRESSION SUIVANT LES X
    tab1 = clutts.WaveletCompressionX(image1);

    // Creation de l image f1 à partir de tab1[0]
    int nbLine = tab1[0].len;
    int nbColumn = tab1[0].width;
    f1.cell = new float[nbLine][nbColumn][3];
    f1.len = nbLine;
    f1.width = nbColumn;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine; i++) {
        for (int j = 0; j < nbColumn - 1; j++) {
          f1.cell[i][j][k] = tab1[0].cell[i][j][k];

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit f1b avec les couleurs de f1
    BufferedImage f1b = new BufferedImage(nbLine, nbColumn, 3);
    for (int i = 0; i < nbLine; i++) {
      for (int j = 0; j < nbColumn; j++) {

        Color col = new Color(f1.cell[i][j][0] / 255, f1.cell[i][j][1] / 255,
            f1.cell[i][j][2] / 255);
        int rgb = col.getRGB();
        f1b.setRGB(i, j, rgb);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfile = new File("saved1.png");

    try {
      ImageIO.write(f1b, "png", outputfile);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");
    }

    // ON APPLIQUE LA COMPRESSION SUIVANT LES Y
    tab1 = clutts.WaveletCompressionY(image1);

    // Creation de l image fh à partir de tab1[1]
    int nbLine1 = tab1[1].len;
    int nbColumn1 = tab1[1].width;
    fh.cell = new float[nbLine1][nbColumn1][3];
    fh.len = nbLine1;
    fh.width = nbColumn1;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine1; i++) {
        for (int j = 0; j < nbColumn1 - 1; j++) {
          fh.cell[i][j][k] = tab1[1].cell[i][j][k];
          // System.out.println(f1.cell[i][j][k]);

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit fhb avec les couleurs de fh
    BufferedImage fhb = new BufferedImage(nbLine1, nbColumn1, 3);
    for (int i = 0; i < nbLine1; i++) {
      for (int j = 0; j < nbColumn1; j++) {

        Color col = new Color(fh.cell[i][j][0] / 255, fh.cell[i][j][1] / 255,
            fh.cell[i][j][2] / 255);
        int rgb1 = col.getRGB();
        fhb.setRGB(i, j, rgb1);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfile1 = new File("savedh.png");

    try {
      ImageIO.write(fhb, "png", outputfile1);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");

    }

    // NIVEAU 2 : les gosses !!

    // enfants de f1
    // image f11
    tab2 = clutts.WaveletCompressionY(f1);

    // Creation de l image f11 à partir de tab2[0]
    int nbLine11 = tab2[0].len;
    int nbColumn11 = tab2[0].width;
    f11.cell = new float[nbLine11][nbColumn11][3];
    f11.len = nbLine11;
    f11.width = nbColumn11;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine11; i++) {
        for (int j = 0; j < nbColumn11 - 1; j++) {
          f11.cell[i][j][k] = tab2[0].cell[i][j][k];

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit fhb avec les couleurs de f11
    BufferedImage f11b = new BufferedImage(nbLine11, nbColumn11, 3);
    for (int i = 0; i < nbLine11; i++) {
      for (int j = 0; j < nbColumn11; j++) {

        Color col = new Color(f11.cell[i][j][0] / 255, f11.cell[i][j][1] / 255,
            f11.cell[i][j][2] / 255);
        int rgb11 = col.getRGB();
        f11b.setRGB(i, j, rgb11);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfile11 = new File("saved11.png");

    try {
      ImageIO.write(f11b, "png", outputfile11);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");

    }

    // image f1h
    // Creation de l image f1h à partir de tab2[1]
    int nbLine1h = tab2[1].len;
    int nbColumn1h = tab2[1].width;
    f1h.cell = new float[nbLine1h][nbColumn1h][3];
    f1h.len = nbLine1h;
    f1h.width = nbColumn1h;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLine1h; i++) {
        for (int j = 0; j < nbColumn1h - 1; j++) {
          f1h.cell[i][j][k] = tab2[1].cell[i][j][k];

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit fhb avec les couleurs de f1h
    BufferedImage f1hb = new BufferedImage(nbLine1h, nbColumn1h, 3);
    for (int i = 0; i < nbLine1h; i++) {
      for (int j = 0; j < nbColumn1h; j++) {

        Color col = new Color(f1h.cell[i][j][0] / 255, f1h.cell[i][j][1] / 255,
            f1h.cell[i][j][2] / 255);
        int rgb1h = col.getRGB();
        f1hb.setRGB(i, j, rgb1h);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfile1h = new File("saved1h.png");

    try {
      ImageIO.write(f1hb, "png", outputfile1h);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");

    }

    // enfants de fh
    tab3 = clutts.WaveletCompressionY(fh);

    // image fh1
    // Creation de l image f1h à partir de tab3[0]
    int nbLineh1 = tab3[0].len;
    int nbColumnh1 = tab3[0].width;
    fh1.cell = new float[nbLineh1][nbColumnh1][3];
    fh1.len = nbLineh1;
    fh1.width = nbColumnh1;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLineh1; i++) {
        for (int j = 0; j < nbColumnh1 - 1; j++) {
          fh1.cell[i][j][k] = tab3[0].cell[i][j][k];

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit fhb avec les couleurs de f1h
    BufferedImage fh1b = new BufferedImage(nbLineh1, nbColumnh1, 3);
    for (int i = 0; i < nbLineh1; i++) {
      for (int j = 0; j < nbColumnh1; j++) {

        Color col = new Color(fh1.cell[i][j][0] / 255, fh1.cell[i][j][1] / 255,
            fh1.cell[i][j][2] / 255);
        int rgbh1 = col.getRGB();
        fh1b.setRGB(i, j, rgbh1);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfileh1 = new File("savedh1.png");

    try {
      ImageIO.write(fh1b, "png", outputfileh1);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");

    }

    // image fhh
    // Creation de l image fhh à partir de tab3[1]
    int nbLinehh = tab3[1].len;
    int nbColumnhh = tab3[1].width;
    fhh.cell = new float[nbLinehh][nbColumnhh][3];
    fhh.len = nbLinehh;
    fhh.width = nbColumnhh;

    for (int k = 0; k < 3; k++) {
      for (int i = 0; i < nbLinehh; i++) {
        for (int j = 0; j < nbColumnhh - 1; j++) {
          fhh.cell[i][j][k] = tab3[1].cell[i][j][k];
          // System.out.println(f1.cell[i][j][k]);

        }// for j
      }// for i
    }// for les trois canaux

    // ECRITURE

    // on remplit fhb avec les couleurs de f1h
    BufferedImage fhhb = new BufferedImage(nbLinehh, nbColumnhh, 3);
    for (int i = 0; i < nbLinehh; i++) {
      for (int j = 0; j < nbColumnhh; j++) {

        Color col = new Color(fhh.cell[i][j][0] / 255, fhh.cell[i][j][1] / 255,
            fhh.cell[i][j][2] / 255);
        int rgbhh = col.getRGB();
        fhhb.setRGB(i, j, rgbhh);
      }
    }
    // il faut repasser de rgb à type arg rgb

    File outputfilehh = new File("savedhh.png");

    try {
      ImageIO.write(fhhb, "png", outputfilehh);
      logger.debug("succès écriture");

    } catch (IOException e) {
      e.printStackTrace();
      logger.debug("écriture pas possible");

    }

    // histogrammes
    BinWavelet wave = new BinWavelet();

    int[][] histof1 = wave.makeHisto(f1);
    int[][] histofh = wave.makeHisto(fh);
    int[][] histof1h = wave.makeHisto(f1h);
    int[][] histofh1 = wave.makeHisto(fh1);
    int[][] histof11 = wave.makeHisto(f11);
    int[][] histofhh = wave.makeHisto(fhh);

    // calcul des entropies de shannon
    double[] rendu = new double[6];

    double sha1 = wave.shanonEntro(f1, histof1);
    rendu[0] = sha1;
    logger.debug(rendu[0]);

    double shah = wave.shanonEntro(fh, histofh);
    rendu[1] = shah;
    logger.debug(rendu[1]);

    double sha1h = wave.shanonEntro(f1h, histof1h);
    rendu[2] = sha1h;
    logger.debug(rendu[2]);

    double shah1 = wave.shanonEntro(fh1, histofh1);
    rendu[3] = shah1;
    logger.debug(rendu[3]);

    double sha11 = wave.shanonEntro(f11, histof11);
    rendu[4] = sha11;
    logger.debug(rendu[4]);

    double shahh = wave.shanonEntro(fhh, histofhh);
    rendu[5] = shahh;
    logger.debug(rendu[5]);

    double sum = rendu[0] + rendu[1] + rendu[2] + rendu[3] + rendu[4]
        + rendu[5];

    logger.info("CLUTTER FINAL : " + sum);
    return sum;

  }

}
