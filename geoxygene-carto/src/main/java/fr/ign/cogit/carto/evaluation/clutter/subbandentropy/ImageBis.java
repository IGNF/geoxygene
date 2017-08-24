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

import java.awt.image.BufferedImage;

/*
 * definition de la classe imageBis, type particulier d image
 */
public class ImageBis {

  public int len = 0;
  public int width = 0;
  public int nbColorCanal = 0;
  public float[][][] cell = {};

  public ImageBis() {
  }

  public ImageBis(BufferedImage img) {

    len = img.getHeight();
    width = img.getWidth();
    nbColorCanal = 3;
    cell = new float[len][width][3];
    if (img.getType() == 6) {
      cell = new float[len][width][4];
      nbColorCanal = 4;
    } else if (img.getType() == 10) {
      cell = new float[len][width][1];
      nbColorCanal = 1;
    }

    // WritableRaster rast = img.getRaster();

    // int l=0;
    float[] pixel;
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < len; i++) {

        // l++;
        if (img.getType() == 5) {
          pixel = img.getRaster().getPixel(j, i, new float[3]);
          // System.out.println(i +" "+ j+":" + pixel[0] + " - " + pixel[1] +
          // " - " + pixel[2] );
          cell[i][j][0] = pixel[0];
          cell[i][j][1] = pixel[1];
          cell[i][j][2] = pixel[2];
        } else if (img.getType() == 6) {
          pixel = img.getRaster().getPixel(j, i, new float[4]);
          cell[i][j][0] = pixel[0];
          cell[i][j][1] = pixel[1];
          cell[i][j][2] = pixel[2];
          cell[i][j][3] = pixel[3];
        } else if (img.getType() == 10) {
          // case for 1 byte gray images
          pixel = img.getRaster().getPixel(j, i, new float[1]);
          cell[i][j][0] = pixel[0];
        }
      } // for j
    } // for i
  }

}
