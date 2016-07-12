package fr.ign.cogit.geoxygene.semio.color;

import java.awt.Color;

import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Conversion tests between the Geoxygene CIELab, CIELach and CIEXYZ
 * colorspaces. All expected results come from <a href =
 * "http://www.brucelindbloom.com" > http://www.brucelindbloom.com</a>.
 */
public class ColorSpaceConversion {

  /**
   * Delta value set empirically. Seems to be the minimum precision of the
   * sRGB/XYZ matrices.
   */
  private static final float eqDelta = 0.024f;

  static final Color sRGBBlack = Color.BLACK; // 0 0 0
  static final Color sRGBBlue = Color.BLUE; // 0 0 255
  static final Color sRGBGreen = Color.GREEN; // 0 255 0
  static final Color sRGBRed = Color.RED; // 255 0 0
  static final Color sRGBCyan = Color.CYAN; // 0 255 255
  static final Color sRGBMagenta = Color.MAGENTA; // 255 0 255
  static final Color sRGBYellow = Color.YELLOW; // 255 255 0
  static final Color sRGBWhite = Color.WHITE; // 255 255 255

  private final Log4JLogger logger = new Log4JLogger("Default");

  @Test
  public void convert_sRGB_CIELab() {

    logger.info("### convert_sRGB_CIELab() ###");
    logger.info("### Convert forth and back between sRGB and CIE L*a*b*");
    CIELabColorSpace csLAB = new CIELabColorSpace(false);

    /**
     * Expected results taken from http://www.brucelindbloom.com/ (D50 sRGB
     * Bradford-adapted). Lambda = 0, Gamma = sRGB
     */
    float[] expLabBlack = new float[] { 0f, 0f, 0f };
    float[] expLabBlue = new float[] { 29.5676f, 68.2986f, -112.0294f };
    float[] expLabGreen = new float[] { 87.8181f, -79.2873f, 80.9902f };
    float[] expLabRed = new float[] { 54.2917f, 80.8125f, 69.8851f };
    float[] expLabCyan = new float[] { 90.6655f, -50.6654f, -14.9620f };
    float[] expLabMagenta = new float[] { 60.1697f, 93.5500f, -60.4986f };
    float[] expLabYellow = new float[] { 97.6071f, -15.7529f, 93.3885f };
    float[] expLabWhite = new float[] { 100f, 0f, 0f };

    /** Convert forth... */
    float[] labBlack = csLAB.fromRGB(sRGBBlack.getColorComponents(null));
    float[] labBlue = csLAB.fromRGB(sRGBBlue.getColorComponents(null));
    float[] labGreen = csLAB.fromRGB(sRGBGreen.getColorComponents(null));
    float[] labRed = csLAB.fromRGB(sRGBRed.getColorComponents(null));
    float[] labCyan = csLAB.fromRGB(sRGBCyan.getColorComponents(null));
    float[] labMagenta = csLAB.fromRGB(sRGBMagenta.getColorComponents(null));
    float[] labYellow = csLAB.fromRGB(sRGBYellow.getColorComponents(null));
    float[] labWhite = csLAB.fromRGB(sRGBWhite.getColorComponents(null));

    Assert.assertArrayEquals(expLabBlack, labBlack, eqDelta);
    Assert.assertArrayEquals(expLabBlue, labBlue, eqDelta);
    Assert.assertArrayEquals(expLabGreen, labGreen, eqDelta);
    Assert.assertArrayEquals(expLabRed, labRed, eqDelta);
    Assert.assertArrayEquals(expLabCyan, labCyan, eqDelta);
    Assert.assertArrayEquals(expLabMagenta, labMagenta, eqDelta);
    Assert.assertArrayEquals(expLabYellow, labYellow, eqDelta);
    Assert.assertArrayEquals(expLabWhite, labWhite, eqDelta);

    /** ...and back */
    float[] bsRGBBlack = csLAB.toRGB(labBlack);
    float[] bsRGBBlue = csLAB.toRGB(labBlue);
    float[] bsRGBGreen = csLAB.toRGB(labGreen);
    float[] bsRGBRed = csLAB.toRGB(labRed);
    float[] bsRGBCyan = csLAB.toRGB(labCyan);
    float[] bsRGBMagenta = csLAB.toRGB(labMagenta);
    float[] bsRGBYellow = csLAB.toRGB(labYellow);
    float[] bsRGBWhite = csLAB.toRGB(labWhite);

    Assert.assertArrayEquals(sRGBBlack.getColorComponents(null), bsRGBBlack,
        eqDelta);
    Assert.assertArrayEquals(sRGBBlue.getColorComponents(null), bsRGBBlue,
        eqDelta);
    Assert.assertArrayEquals(sRGBGreen.getColorComponents(null), bsRGBGreen,
        eqDelta);
    Assert.assertArrayEquals(sRGBRed.getColorComponents(null), bsRGBRed,
        eqDelta);
    Assert.assertArrayEquals(sRGBCyan.getColorComponents(null), bsRGBCyan,
        eqDelta);
    Assert.assertArrayEquals(sRGBMagenta.getColorComponents(null),
        bsRGBMagenta, eqDelta);
    Assert.assertArrayEquals(sRGBYellow.getColorComponents(null), bsRGBYellow,
        eqDelta);
    Assert.assertArrayEquals(sRGBWhite.getColorComponents(null), bsRGBWhite,
        eqDelta);
  }

  @Test
  public void convert_sRGB_CIELch() {

    logger.info("### convert_sRGB_CIELch() ###");
    logger.info("### Convert forth and back between sRGB and CIE L*c*h*");
    CIELchColorSpace csLCH = new CIELchColorSpace(false);

    /**
     * Expected results taken from http://www.brucelindbloom.com/ (D50 sRGB
     * Bradford-adapted). Lambda = 0, Gamma = sRGB
     */
    float[] expLchBlack = new float[] { 0f, 0f, 0f };
    float[] expLchBlue = new float[] { 29.5676f, 131.2070f, 301.3685f };
    float[] expLchGreen = new float[] { 87.8181f, 113.3397f, 134.3912f };
    float[] expLchRed = new float[] { 54.2917f, 106.8390f, 40.8526f };
    float[] expLchCyan = new float[] { 90.6655f, 52.8285f, 196.4524f };
    float[] expLchMagenta = new float[] { 60.1697f, 111.4077f, 327.1094f };
    float[] expLchYellow = new float[] { 97.6071f, 94.7078f, 99.5746f };
    /**
     * Note : for he LCH models, the hue angle (H) becomes undefined as the
     * chroma value (C) approaches zero. Consequently we don't test the H value
     * for the sRGB white.
     */
    float[] expLchWhite = new float[] { 100f, 0f };

    /** Convert forth... */
    float[] lchBlack = csLCH.fromRGB(sRGBBlack.getColorComponents(null));
    float[] lchBlue = csLCH.fromRGB(sRGBBlue.getColorComponents(null));
    float[] lchGreen = csLCH.fromRGB(sRGBGreen.getColorComponents(null));
    float[] lchRed = csLCH.fromRGB(sRGBRed.getColorComponents(null));
    float[] lchCyan = csLCH.fromRGB(sRGBCyan.getColorComponents(null));
    float[] lchMagenta = csLCH.fromRGB(sRGBMagenta.getColorComponents(null));
    float[] lchYellow = csLCH.fromRGB(sRGBYellow.getColorComponents(null));
    float[] lchWhite = csLCH.fromRGB(sRGBWhite.getColorComponents(null));

    Assert.assertArrayEquals(expLchBlack, lchBlack, eqDelta);
    Assert.assertArrayEquals(expLchBlue, lchBlue, eqDelta);
    Assert.assertArrayEquals(expLchGreen, lchGreen, eqDelta);
    Assert.assertArrayEquals(expLchRed, lchRed, eqDelta);
    Assert.assertArrayEquals(expLchCyan, lchCyan, eqDelta);
    Assert.assertArrayEquals(expLchMagenta, lchMagenta, eqDelta);
    Assert.assertArrayEquals(expLchYellow, lchYellow, eqDelta);
    Assert.assertArrayEquals(expLchWhite, new float[] { lchWhite[0],
        lchWhite[1] }, eqDelta);

    /** ...and back */
    float[] bsRGBBlack = csLCH.toRGB(lchBlack);
    float[] bsRGBBlue = csLCH.toRGB(lchBlue);
    float[] bsRGBGreen = csLCH.toRGB(lchGreen);
    float[] bsRGBRed = csLCH.toRGB(lchRed);
    float[] bsRGBCyan = csLCH.toRGB(lchCyan);
    float[] bsRGBMagenta = csLCH.toRGB(lchMagenta);
    float[] bsRGBYellow = csLCH.toRGB(lchYellow);
    float[] bsRGBWhite = csLCH.toRGB(lchWhite);

    Assert.assertArrayEquals(sRGBBlack.getColorComponents(null), bsRGBBlack,
        eqDelta);
    Assert.assertArrayEquals(sRGBBlue.getColorComponents(null), bsRGBBlue,
        eqDelta);
    Assert.assertArrayEquals(sRGBGreen.getColorComponents(null), bsRGBGreen,
        eqDelta);
    Assert.assertArrayEquals(sRGBRed.getColorComponents(null), bsRGBRed,
        eqDelta);
    Assert.assertArrayEquals(sRGBCyan.getColorComponents(null), bsRGBCyan,
        eqDelta);
    Assert.assertArrayEquals(sRGBMagenta.getColorComponents(null),
        bsRGBMagenta, eqDelta);
    Assert.assertArrayEquals(sRGBYellow.getColorComponents(null), bsRGBYellow,
        eqDelta);
    Assert.assertArrayEquals(sRGBWhite.getColorComponents(null), bsRGBWhite,
        eqDelta);
  }
}
