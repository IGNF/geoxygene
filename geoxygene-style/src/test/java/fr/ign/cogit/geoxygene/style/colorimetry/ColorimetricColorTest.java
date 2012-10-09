/**
 * 
 */
package fr.ign.cogit.geoxygene.style.colorimetry;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
// import org.junit.rules.Exception;


/**
 * 
 * @author CHoarau
 *
 */
public class ColorimetricColorTest extends ColorimetryAssert {
  
  private Logger logger = Logger.getLogger(ColorimetricColorTest.class);
  
  /** Default ColorimetricColor. */
  private ColorimetricColor defaultColorimetric;
  
  
  @Before
  public void setUp() throws Exception {
    // Initialize default ColorimetricColor.
    defaultColorimetric = new ColorimetricColor();
    defaultColorimetric.idColor = 0;
    defaultColorimetric.hue = null;
    defaultColorimetric.lightness = 0;
    defaultColorimetric.usualName = null;
    defaultColorimetric.cleCoul = null;
    defaultColorimetric.redRGB = 0;
    defaultColorimetric.greenRGB = 0;
    defaultColorimetric.blueRGB = 0;
    defaultColorimetric.xScreen = 0;
    defaultColorimetric.yScreen = 0;
  }
  
  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor()}.
   */
  @Test
  public final void testColorimetricColor() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor#ColorimetricColor()");
    
    logger.info("  Test 1 : default ColorimetricColor");
    // ColorimetricColor construite par defaut 
    ColorimetricColor cResult = new ColorimetricColor();
    
    // ColorimetricColor par defaut attendue
    ColorimetricColor cExpected = defaultColorimetric;
    
    // On compare
    compareColorimetricColor(cResult, cExpected);
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int)}.
   */
  @Test
  public final void testColorimetricColorInt() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor(int) ");
    
    // =====================================================
    logger.info("  Test 1 : existing color : 1");
    
    // Existing color : 1 
    ColorimetricColor cResult = new ColorimetricColor(1);
    
    // 
    ColorimetricColor cExpected = new ColorimetricColor();
    cExpected.idColor = 1;
    cExpected.hue = "JAUNE";
    cExpected.lightness = 1;
    cExpected.usualName = "JAUNE TRES CLAIR";
    cExpected.cleCoul = "J1";
    cExpected.redRGB = 255;
    cExpected.greenRGB = 255;
    cExpected.blueRGB = 204;
    cExpected.xScreen = 236;
    cExpected.yScreen = 164;
    
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    // Test with a non existing color 0
    logger.info("  Test 2 : with a non existing color : 0");
    cResult = new ColorimetricColor(0);
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    // Test with a non existing color 0
    logger.info("  Test 3 : with a non existing color : 164");
    cResult = new ColorimetricColor(164);
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
  }
  
  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.lang.String, int)}.
   */
  @Test
  public final void testColorimetricColorStringInt() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor(String, int)");
    
    // =====================================================
    logger.info("  Test 1 : setting of all the attributes");
    
    //
    ColorimetricColor cResult = new ColorimetricColor("Rouge", 0); //$NON-NLS-1$
    
    ColorimetricColor cExpected = new ColorimetricColor();
    cExpected.idColor = 57;
    cExpected.hue = "ROUGE";
    cExpected.lightness = 1;
    cExpected.usualName = "ROUGE TRES CLAIR";
    cExpected.cleCoul = "R1";
    cExpected.redRGB = 254;
    cExpected.greenRGB = 230;
    cExpected.blueRGB = 219;
    cExpected.xScreen = 164;
    cExpected.yScreen = 164;
    
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    logger.info("  Test 2 :  with a non existing color : abcdef ");
    cResult = new ColorimetricColor("abcdef", 1); //$NON-NLS-1$
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    logger.info("  Test 3 :  with a non existing lightness : 2012841");
    cResult = new ColorimetricColor("Rouge", 2012841); //$NON-NLS-1$
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    logger.info("  Test 4 : different existing combination of hue and lightness");
    
    // Then, Test of the different combination of hue and lightness
    // In the three first wheels where lightness range from 1 to 7
    // 0 and 8 are allowed and setted respectively as 1 and 7
    logger.info("  Test 4.1 :  Rouge, Lightness = 0 => R1");
    cResult = new ColorimetricColor("Rouge", 0); //$NON-NLS-1$
    assertEquals("R1", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.2 :  Rouge, Lightness = 1 => R1");
    cResult = new ColorimetricColor("Rouge", 1); //$NON-NLS-1$
    assertEquals("R1", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.3 :  Rouge, Lightness = 5 => R5");
    cResult = new ColorimetricColor("Rouge", 5); //$NON-NLS-1$
    assertEquals("R5", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.4 :  Rouge, Lightness = 7 => R7");
    cResult = new ColorimetricColor("Rouge", 7); //$NON-NLS-1$
    assertEquals("R7", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.5 :  Rouge, Lightness = 8 => R8");
    cResult = new ColorimetricColor("Rouge", 8); //$NON-NLS-1$
    assertEquals("R7", cResult.getCleCoul()); //$NON-NLS-1$
    
    // Finally, test of the different allowed hue for white, grey and black.
    logger.info("  Test 4.6 :  Gris, Lightness = 0 => Bl0");
    cResult = new ColorimetricColor("Gris", 0); //$NON-NLS-1$
    assertEquals("Bl0", cResult.getCleCoul()); //$NON-NLS-1$
    compareColorimetricColor(cResult, new ColorimetricColor(85));
    
    logger.info("  Test 4.7 :  Gris, Lightness = 1 => G1");
    cResult = new ColorimetricColor("Gris", 1); //$NON-NLS-1$
    assertEquals("G1", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.8 :  Gris, Lightness = 5 => G5");
    cResult = new ColorimetricColor("Gris", 5); //$NON-NLS-1$
    assertEquals("G5", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.9 :  Gris, Lightness = 7 => G7");
    cResult = new ColorimetricColor("Gris", 7); //$NON-NLS-1$
    assertEquals("G7", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.10 :  Gris, Lightness = 8 => N8");
    cResult = new ColorimetricColor("Gris", 8); //$NON-NLS-1$
    assertEquals("N8", cResult.getCleCoul()); //$NON-NLS-1$
    compareColorimetricColor(cResult, new ColorimetricColor(86));
    
    logger.info("  Test 4.11 :  Noir, Lightness = 0 => Bl0");
    cResult = new ColorimetricColor("Noir", 0); //$NON-NLS-1$
    assertEquals("Bl0", cResult.getCleCoul()); //$NON-NLS-1$
    compareColorimetricColor(cResult, new ColorimetricColor(85));
    
    logger.info("  Test 4.12 :  Noir, Lightness = 1 => G1");
    cResult = new ColorimetricColor("Noir", 1); //$NON-NLS-1$
    assertEquals("G1", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.13 :  Noir, Lightness = 7 => G7");
    cResult = new ColorimetricColor("Noir", 7); //$NON-NLS-1$
    assertEquals("G7", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.14 :  Noir, Lightness = 8 => N8");
    cResult = new ColorimetricColor("Noir", 8); //$NON-NLS-1$
    assertEquals("N8", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.15 :  Blanc, Lightness = 0 => Bl0");
    cResult = new ColorimetricColor("Blanc", 0); //$NON-NLS-1$
    assertEquals("Bl0", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.16 :  Blanc, Lightness = 1 => G1");
    cResult = new ColorimetricColor("Blanc", 1); //$NON-NLS-1$
    assertEquals("G1", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.17 :  Blanc, Lightness = 7 => G7");
    cResult = new ColorimetricColor("Blanc", 7); //$NON-NLS-1$
    assertEquals("G7", cResult.getCleCoul()); //$NON-NLS-1$
    
    logger.info("  Test 4.18 :  Blanc, Lightness = 8 => N8");
    cResult = new ColorimetricColor("Blanc", 8); //$NON-NLS-1$
    assertEquals("N8", cResult.getCleCoul()); //$NON-NLS-1$
    compareColorimetricColor(cResult, new ColorimetricColor(86));
    
    // =====================================================
    logger.info("  Test 5 : different non existing combination of hue and lightness");
    
    logger.info("  Test 5.1 :  Rouge, Lightness = 9 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Rouge", 9); //$NON-NLS-1$
    compareColorimetricColor(cResult, defaultColorimetric);
    
    logger.info("  Test 5.2 :  Gris, Lightness = 9 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Gris", 9); //$NON-NLS-1$
    compareColorimetricColor(cResult, defaultColorimetric);
    
    logger.info("  Test 5.3 :  Noir, Lightness = 88888 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Noir", 88888); //$NON-NLS-1$
    compareColorimetricColor(cResult, defaultColorimetric);
    
    logger.info("  Test 5.5 :  Blanc, Lightness = 9 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Blanc", 9); //$NON-NLS-1$
    compareColorimetricColor(cResult, defaultColorimetric);
    
    logger.info("  Test 5.6 :  Mauve, Lightness = 5 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Mauve", 5); //$NON-NLS-1$
    compareColorimetricColor(cResult, defaultColorimetric);
    
    // =====================================================
    logger.info("  Test 6 : 2 particular combination of hue and lightness (calling constructor twice)");
    
    logger.info("  Test 6.1 :  Noir, Lightness = 8 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Noir", 8); //$NON-NLS-1$
    cExpected = new ColorimetricColor("Gris", 8); //$NON-NLS-1$
    compareColorimetricColor(cResult, cExpected);
    compareColorimetricColor(cResult, new ColorimetricColor(86));
    
    logger.info("  Test 6.2 :  Blanc, Lightness = 0 => Default ColorimetricColor");
    cResult = new ColorimetricColor("Blanc", 0); //$NON-NLS-1$
    cExpected = new ColorimetricColor("Gris", 0); //$NON-NLS-1$
    compareColorimetricColor(cResult, cExpected);
    compareColorimetricColor(cResult, new ColorimetricColor(85));
    
    // =====================================================
    logger.info("  Test 7 :  with a non existing lightness : null");
    cResult = new ColorimetricColor(null, 2012841); //$NON-NLS-1$
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
  }
  
  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.lang.String)}.
   */
  @Test
  public final void testColorimetricColorString() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor(String)");
 
    // =====================================================
    logger.info("  Test 1 : existing color");
    
    logger.info("  Test 1.1 : Bleu violet pur");
    ColorimetricColor cResult = new ColorimetricColor("Bleu violet pur"); //$NON-NLS-1$
    ColorimetricColor cExpected = new ColorimetricColor();
    cExpected.idColor = 39;
    cExpected.hue = "BLEU VIOLET";
    cExpected.lightness = 4;
    cExpected.usualName = "BLEU VIOLET PUR";
    cExpected.cleCoul = "BVi4";
    cExpected.redRGB = 121;
    cExpected.greenRGB = 144;
    cExpected.blueRGB = 195;
    cExpected.xScreen = 176;
    cExpected.yScreen = 295;
    compareColorimetricColor(cResult, cExpected);
    
    logger.info("  Test 1.2 : GRIS POURPRE");
    cResult = new ColorimetricColor("GRIS POURPRE TRES CLAIR"); //$NON-NLS-1$
    cExpected = new ColorimetricColor("GRIS POURPRE", 1);
    compareColorimetricColor(cResult, cExpected);
    
    logger.info("  Test 1.3 : Bleu moyen fonce");
    cResult = new ColorimetricColor("Bleu moyen fonce"); //$NON-NLS-1$
    cExpected = new ColorimetricColor(33);
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    logger.info("  Test 2 : non existing color : ABCDE");
    cResult = new ColorimetricColor("ABCDE");
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
    // =====================================================
    logger.info("  Test 3 : non existing color : null");
    cResult = new ColorimetricColor((String)null); //$NON-NLS-1$
    cExpected = defaultColorimetric;
    compareColorimetricColor(cResult, cExpected);
    
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int, int, int)}.
   */
  @Test
  public final void testColorimetricColorIntIntInt() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor(int, int, int)");
 
    // =====================================================
    logger.info("  Test 1 : existing color");
    
    logger.info("  Test 1.1 : GRIS BLEU MOYEN CLAIR");
    ColorimetricColor cResult = new ColorimetricColor(182, 190, 191);
    assertEquals("Compare redRGB. ", 182, cResult.getRedRGB());
    assertEquals("Compare greenRGB. ", 190, cResult.getGreenRGB());
    assertEquals("Compare blueRGB. ", 191, cResult.getBlueRGB());
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    logger.info("  Test 1.2 : MARRON TRES CLAIR");
    cResult = new ColorimetricColor(249, 244, 217); //$NON-NLS-1$
    assertEquals("Compare redRGB. ", 249, cResult.redRGB);
    assertEquals("Compare greenRGB. ", 244, cResult.greenRGB);
    assertEquals("Compare blueRGB. ", 217, cResult.blueRGB);
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    // =====================================================
    logger.info("  Test 1.3 : test 250-277-999");
    cResult = new ColorimetricColor(250, 277, 999);
    
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    assertFalse("Compare redRGB. ", 249 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 0 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 277 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 999 == cResult.redRGB);
    
    assertFalse("Compare greenRGB. ", 250 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 999 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 0 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 278 == cResult.greenRGB);
    
    assertFalse("Compare blueRGB. ", 250 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 277 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 0 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 998 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 1000 == cResult.blueRGB);

  }
  
  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int, int, int, boolean)}.
   */
  @Test
  public final void testColorimetricColorIntIntIntBoolean() throws Exception {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method ColorimetricColor(int, int, int, boolean)");
    
    // =====================================================
    logger.info("  Test 1 : without reference");
    // On reprend les tests du test précedent
    
    logger.info("  Test 1.1 : GRIS BLEU MOYEN CLAIR");
    ColorimetricColor cResult = new ColorimetricColor(182, 190, 191, false);
    assertEquals("Compare redRGB. ", 182, cResult.getRedRGB());
    assertEquals("Compare greenRGB. ", 190, cResult.getGreenRGB());
    assertEquals("Compare blueRGB. ", 191, cResult.getBlueRGB());
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    logger.info("  Test 1.2 : MARRON TRES CLAIR");
    cResult = new ColorimetricColor(249, 244, 217, false); //$NON-NLS-1$
    assertEquals("Compare redRGB. ", 249, cResult.redRGB);
    assertEquals("Compare greenRGB. ", 244, cResult.greenRGB);
    assertEquals("Compare blueRGB. ", 217, cResult.blueRGB);
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    logger.info("  Test 1.3 : 250-277-999");
    cResult = new ColorimetricColor(250, 277, 999);
    assertEquals("Compare hue. ", null, cResult.getHue());
    assertEquals("Compare lightness. ", 0, cResult.getLightness());
    assertEquals("Compare usual name. ", null, cResult.getUsualName());
    assertEquals("Compare idColor. ", 0, cResult.getIdColor());
    assertEquals("Compare CleCoul. ", null, cResult.getCleCoul());
    assertEquals("Compare xScreen. ", 0, cResult.getXScreen());
    assertEquals("Compare yScreen. ", 0, cResult.getYScreen());
    
    assertFalse("Compare redRGB. ", 249 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 0 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 277 == cResult.redRGB);
    assertFalse("Compare redRGB. ", 999 == cResult.redRGB);
    
    assertFalse("Compare greenRGB. ", 250 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 999 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 0 == cResult.greenRGB);
    assertFalse("Compare greenRGB. ", 278 == cResult.greenRGB);
    
    assertFalse("Compare blueRGB. ", 250 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 277 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 0 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 998 == cResult.blueRGB);
    assertFalse("Compare blueRGB. ", 1000 == cResult.blueRGB);
    
    // =====================================================
    logger.info("  Test 2 : with reference");
    
    logger.info("  Test 2.1 : existing color (GRIS BLEU MOYEN CLAIR)");
    cResult = new ColorimetricColor(182, 190, 191, true);
    compareColorimetricColor(cResult, new ColorimetricColor("GRIS BLEU MOYEN CLAIR"));
    
    logger.info("  Test 2.2 : existing color (MARRON TRES CLAIR)");
    cResult = new ColorimetricColor(249, 244, 217, true);
    compareColorimetricColor(cResult, new ColorimetricColor("MARRON TRES CLAIR"));
    
    logger.info("  Test 2.3 : approximate existing color (MARRON TRES CLAIR)");
    cResult = new ColorimetricColor(249, 241, 216, true);
    compareColorimetricColor(cResult, new ColorimetricColor("MARRON TRES CLAIR"));
    
    logger.info("  Test 2.4 : approximate existing color (GRIS BLEU MOYEN CLAIR)");
    cResult = new ColorimetricColor(179, 192, 189, true);
    compareColorimetricColor(cResult, new ColorimetricColor("GRIS BLEU MOYEN CLAIR"));
    
    logger.info("  Test 2.5 : approximate existing color (BLEU VIOLET TRES FONCE)");
    cResult = new ColorimetricColor(0, 0, 80, true);
    compareColorimetricColor(cResult, new ColorimetricColor("BLEU VIOLET TRES FONCE"));
    
    // =====================================================
    logger.info("  Test 3 : non existing RGB");
    try {
      cResult = new ColorimetricColor(0, 999, 500, true);
      fail("Color(0, 999, 500) existing !");
    } catch (IllegalArgumentException eexpected) {
      // exception.expectMessage("Color parameter outside of expected range: Green Blue");
    }
    
  
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.awt.Color)}.
   */
  // @Test
  // public final void testColorimetricColorColor() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.awt.Color, boolean)}.
   */
  // @Test
  // public final void testColorimetricColorColorBoolean() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }
  

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getSlice(fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem)}.
   */
  @Test
  public final void testGetSlice() {
    logger.info("--------------------------------------------------------------------------------------------------------------");
    logger.info("Test method getSlice(ColorReferenceSystem)");
    
    // =====================================================
    logger.info("  Test 1.1 : ColorReferenceSystem is null. ");
    ColorimetricColor cResult = new ColorimetricColor();
    assertNull("ColorSlice is not null ", cResult.getSlice(null));
    
    logger.info("  Test 1.2 : ColorReferenceSystem exist but is empty. ");
    cResult = new ColorimetricColor();
    ColorReferenceSystem crsZeroWheels = new ColorReferenceSystem();
    assertNull("ColorSlice is not null ", cResult.getSlice(crsZeroWheels));
    
    logger.info("  Test 1.3 : ColorReferenceSystem without wheels");
    cResult = new ColorimetricColor();
    crsZeroWheels = new ColorReferenceSystem();
    List<ColorWheel> wheels = new ArrayList<ColorWheel>();
    crsZeroWheels.setWheels(wheels);
    assertNull("ColorSlice is not null ", cResult.getSlice(crsZeroWheels));
    
    logger.info("  Test 1.4 : ColorReferenceSystem : each wheels has not got slices");
    List<ColorSlice> slices = new ArrayList<ColorSlice>();
    ColorWheel colorWheel = new ColorWheel();
    colorWheel.setSlices(slices);
    wheels = new ArrayList<ColorWheel>();
    wheels.add(colorWheel);
    cResult = new ColorimetricColor();
    crsZeroWheels = new ColorReferenceSystem();
    crsZeroWheels.setWheels(wheels);
    assertNull("ColorSlice is not null ", cResult.getSlice(crsZeroWheels));
    
    logger.info("  Test 1.5 : ColorReferenceSystem : each slices of each wheels has not got colors");
    ColorSlice colorSlice = new ColorSlice();
    slices = new ArrayList<ColorSlice>();
    slices.add(colorSlice);
    colorWheel = new ColorWheel();
    colorWheel.setSlices(slices);
    wheels = new ArrayList<ColorWheel>();
    wheels.add(colorWheel);
    cResult = new ColorimetricColor();
    crsZeroWheels = new ColorReferenceSystem();
    crsZeroWheels.setWheels(wheels);
    assertNull("ColorSlice is not null ", cResult.getSlice(crsZeroWheels));
    
    // =====================================================
    logger.info("  Test 2 : ColorReferenceSystem with one color");
    colorSlice = new ColorSlice();
    colorSlice.setHue("ROUGE GRIS");
    colorSlice.setIdSaturationParentWheel(0);
    List<ColorimetricColor> justDefaultColorimetricList = new ArrayList<ColorimetricColor>();
    justDefaultColorimetricList.add(new ColorimetricColor());
    colorSlice.setColors(justDefaultColorimetricList);
    
    colorWheel = new ColorWheel();
    slices = new ArrayList<ColorSlice>();
    slices.add(colorSlice);
    colorWheel.setSlices(slices);
    colorWheel.setidSaturation(0);
    
    crsZeroWheels = new ColorReferenceSystem();
    wheels = new ArrayList<ColorWheel>();
    wheels.add(colorWheel);
    crsZeroWheels.setWheels(wheels);
    
    cResult = new ColorimetricColor();
    assertNotNull("ColorSlice is not null ", cResult.getSlice(crsZeroWheels));
    
    // =====================================================
    logger.info("  Test 3 : COGIT ColorReferenceSystem");
    // Loading COGIT Color reference system
    ColorReferenceSystem COGITcrs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResource(
            "/color/ColorReferenceSystem.xml").getPath());
    
    logger.info("  Test 3.1 : JAUNE");
    
    // Prepare list colors expected for the JAUNE ColorSlice
    List<ColorimetricColor> colorsExpected = new ArrayList<ColorimetricColor>();
    colorsExpected.add(new ColorimetricColor("JAUNE TRES CLAIR"));
    colorsExpected.add(new ColorimetricColor("JAUNE CLAIR"));
    colorsExpected.add(new ColorimetricColor("JAUNE MOYEN CLAIR"));
    colorsExpected.add(new ColorimetricColor("JAUNE PUR"));
    colorsExpected.add(new ColorimetricColor("JAUNE MOYEN FONCE"));
    colorsExpected.add(new ColorimetricColor("JAUNE FONCE"));
    colorsExpected.add(new ColorimetricColor("JAUNE TRES FONCE"));
    
    cResult = COGITcrs.getAllColors().get(1);
    assertEquals("Compare hue. ", "JAUNE", cResult.getSlice(COGITcrs).getHue());
    assertEquals("Compare idSaturationParentWheel. ", 0, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    compareColorimetricColorList(colorsExpected, cResult.getSlice(COGITcrs).getColors());
    
    cResult = new ColorimetricColor(1);
    assertEquals("Compare hue. ", "JAUNE", cResult.getSlice(COGITcrs).getHue());
    assertEquals("Compare idSaturationParentWheel. ", 0, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    compareColorimetricColorList(colorsExpected, cResult.getSlice(COGITcrs).getColors());
    
    cResult = new ColorimetricColor(2);
    assertEquals("Compare hue. ", "JAUNE", cResult.getSlice(COGITcrs).getHue());
    assertEquals("Compare idSaturationParentWheel. ", 0, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    compareColorimetricColorList(colorsExpected, cResult.getSlice(COGITcrs).getColors());
    
    cResult = new ColorimetricColor(7);
    assertEquals("Compare hue. ", "JAUNE", cResult.getSlice(COGITcrs).getHue());
    assertEquals("Compare idSaturationParentWheel. ", 0, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    compareColorimetricColorList(colorsExpected, cResult.getSlice(COGITcrs).getColors());
    
    cResult = new ColorimetricColor("JAUNE TRES FONCE");
    assertEquals("Compare hue. ", "JAUNE", cResult.getSlice(COGITcrs).getHue());
    assertEquals("Compare idSaturationParentWheel. ", 0, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    compareColorimetricColorList(colorsExpected, cResult.getSlice(COGITcrs).getColors());
    
    // =====================================================
    logger.info("  Test 3.2 : ORANGE GRIS");
    
    // Prepare list colors expected for the ORANGE GRIS ColorSlice
    colorsExpected = new ArrayList<ColorimetricColor>();
    colorsExpected.add(new ColorimetricColor("ORANGE GRIS TRES CLAIR"));
    colorsExpected.add(new ColorimetricColor("ORANGE GRIS MOYEN CLAIR"));
    colorsExpected.add(new ColorimetricColor("ORANGE GRIS MOYEN FONCE"));
    colorsExpected.add(new ColorimetricColor("ORANGE GRIS TRES FONCE"));
    
    cResult = new ColorimetricColor(129);
    assertEquals("", "ORANGE GRIS", cResult.getSlice(COGITcrs).getHue());
    assertEquals("", 1, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    
    cResult = new ColorimetricColor(130);
    assertEquals("", "ORANGE GRIS", cResult.getSlice(COGITcrs).getHue());
    assertEquals("", 1, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    
    cResult = new ColorimetricColor(131);
    assertEquals("", "ORANGE GRIS", cResult.getSlice(COGITcrs).getHue());
    assertEquals("", 1, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    
    cResult = new ColorimetricColor("ORANGE GRIS TRES CLAIR");
    assertEquals("", "ORANGE GRIS", cResult.getSlice(COGITcrs).getHue());
    assertEquals("", 1, cResult.getSlice(COGITcrs).getIdSaturationParentWheel());
    
    // ???
    // List<ColorimetricColor> slice = COGITcrs.getSlice(0, 1);
    
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getWheel(fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem)}.
   */
  // @Test
  // public final void testGetWheel() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toColor()}.
   */
  // @Test
  // public final void testToColor() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toXYZ()}.
   */
  // @Test
  // public final void testToXYZ() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toXYZ(float[])}.
   */
  // @Test
  // public final void testToXYZFloatArray() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getLab()}.
   */
  // @Test
  // public final void testGetLab() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getCIEChroma()}.
   */
  // @Test
  // public final void testGetCIEChroma() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getCIEHue()}.
   */
  // @Test
  // public final void testGetCIEHue() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(float, float, float)}.
   */
  // @Test
  // public final void testColorimetricColorFloatFloatFloat() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(float[])}.
   */
  // @Test
  // public final void testColorimetricColorFloatArray() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#labExistance(float, float, float)}.
   */
  // @Test
  // public final void testLabExistance() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setL(float)}.
   */
  // @Test
  // public final void testSetL() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setA(float)}.
   */
  // @Test
  // public final void testSetA() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setB(float)}.
   */
  // @Test
  // public final void testSetB() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#equals(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testEqualsColorimetricColor() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#maxRGB()}.
   */
  // @Test
  // public final void testMaxRGB() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#distanceCIElab(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testDistanceCIElab() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#distanceRVB(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testDistanceRVB() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELab_Lightness_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testCIELab_Lightness_Difference() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELCh_Chroma_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testCIELCh_Chroma_Difference() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELCh_Hue_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  // @Test
  // public final void testCIELCh_Hue_Difference() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toString()}.
   */
  // @Test
  // public final void testToString() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#basicColorsComponents()}.
   */
  // @Test
  // public final void testBasicColorsComponents() {
  // fail("Not yet implemented"); // TODO //$NON-NLS-1$
  // }
}
