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
  public float[][][] cell = {};

  public ImageBis() {
  }

  public ImageBis(BufferedImage img) {

    len = img.getHeight();
    width = img.getWidth();
    cell = new float[len][width][3];

    // WritableRaster rast = img.getRaster();

    // int l=0;
    float[] pixel;
    for (int j = 0; j < width; j++) {
      for (int i = 0; i < len; i++) {

        // l++;
        pixel = img.getRaster().getPixel(j, i, new float[3]);
        // System.out.println(i +" "+ j+":" + pixel[0] + " - " + pixel[1] +
        // " - " + pixel[2] );
        cell[i][j][0] = pixel[0];
        cell[i][j][1] = pixel[1];
        cell[i][j][2] = pixel[2];

      }// for j
    }// for i
  }

}
