/**
 * 
 */
package fr.ign.cogit.geoxygene.style.colorimetry;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * @author CHoarau
 *
 */
public class ColorimetricColorTest {

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getSlice(fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem)}.
   */
  @Test
  public final void testGetSlice() {
    ColorReferenceSystem COGITcrs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResource(
            "/color/ColorReferenceSystem.xml").getPath()); //$NON-NLS-1$
    assertEquals("JAUNE", COGITcrs.getAllColors().get(1).getSlice(COGITcrs).getHue()); //$NON-NLS-1$
    
    ColorimetricColor c = new ColorimetricColor(1);
    assertEquals("JAUNE", c.getSlice(COGITcrs).getHue()); //$NON-NLS-1$
    
    List<ColorimetricColor> slice = COGITcrs.getSlice(0, 1);
    
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getWheel(fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem)}.
   */
  @Test
  public final void testGetWheel() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Void Constructor test.
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor()}.
   */
  @Test
  public final void testColorimetricColor() {
    ColorimetricColor c = new ColorimetricColor();
    assertEquals(null, c.getHue());
    assertEquals(0, c.getLightness());
    assertEquals(null, c.getUsualName());
    assertEquals(null, c.getCleCoul());
    assertEquals(0, c.getRedRGB());
    assertEquals(0, c.getGreenRGB());
    assertEquals(0, c.getBlueRGB());
    assertEquals(0, c.getXScreen());
    assertEquals(0, c.getYScreen());
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int)}.
   */
  @Test
  public final void testColorimetricColorInt() {
    //Test with an existing color
    ColorimetricColor c = new ColorimetricColor(1);
    assertEquals("JAUNE", c.getHue()); //$NON-NLS-1$
    assertEquals(1, c.getLightness());
    assertEquals("JAUNE TRES CLAIR", c.getUsualName()); //$NON-NLS-1$
    assertEquals("J1", c.getCleCoul()); //$NON-NLS-1$
    assertEquals(255, c.getRedRGB());
    assertEquals(255, c.getGreenRGB());
    assertEquals(204, c.getBlueRGB());
    assertEquals(236, c.getXScreen());
    assertEquals(164, c.getYScreen());
    
    //Test with a non existing color
    c = new ColorimetricColor(0);
    assertEquals(null, c.getHue());
    assertEquals(0, c.getLightness());
    assertEquals(null, c.getUsualName());
    assertEquals(null, c.getCleCoul());
    assertEquals(0, c.getRedRGB());
    assertEquals(0, c.getGreenRGB());
    assertEquals(0, c.getBlueRGB());
    assertEquals(0, c.getXScreen());
    assertEquals(0, c.getYScreen());
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.lang.String, int)}.
   */
  @Test
  public final void testColorimetricColorStringInt() {
    //First Test of the setting of all the attributes
    ColorimetricColor c = new ColorimetricColor("Rouge", 0); //$NON-NLS-1$
    assertEquals("ROUGE", c.getHue()); //$NON-NLS-1$
    assertEquals(1, c.getLightness());
    assertEquals("ROUGE TRES CLAIR", c.getUsualName()); //$NON-NLS-1$
    assertEquals("R1", c.getCleCoul()); //$NON-NLS-1$
    assertEquals(254, c.getRedRGB());
    assertEquals(230, c.getGreenRGB());
    assertEquals(219, c.getBlueRGB());
    assertEquals(164, c.getXScreen());
    assertEquals(164, c.getYScreen());
    
    //Then, Test of the different combination of hue and lightness
    //In the three first wheels where lightness range from 1 to 7
    //0 and 8 are allowed and setted respectively as 1 and 7
    c = new ColorimetricColor("Rouge", 1); //$NON-NLS-1$
    assertEquals("R1", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Rouge", 5); //$NON-NLS-1$
    assertEquals("R5", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Rouge", 7); //$NON-NLS-1$
    assertEquals("R7", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Rouge", 8); //$NON-NLS-1$
    assertEquals("R7", c.getCleCoul()); //$NON-NLS-1$
    
    //Finally, test of the different allowed hue for white, grey and black.
    c = new ColorimetricColor("Gris", 0); //$NON-NLS-1$
    assertEquals("Bl0", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Gris", 1); //$NON-NLS-1$
    assertEquals("G1", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Gris", 5); //$NON-NLS-1$
    assertEquals("G5", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Gris", 7); //$NON-NLS-1$
    assertEquals("G7", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Gris", 8); //$NON-NLS-1$
    assertEquals("N8", c.getCleCoul()); //$NON-NLS-1$

    c = new ColorimetricColor("Noir", 0); //$NON-NLS-1$
    assertEquals("Bl0", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Noir", 1); //$NON-NLS-1$
    assertEquals("G1", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Noir", 7); //$NON-NLS-1$
    assertEquals("G7", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Noir", 8); //$NON-NLS-1$
    assertEquals("N8", c.getCleCoul()); //$NON-NLS-1$

    c = new ColorimetricColor("Blanc", 0); //$NON-NLS-1$
    assertEquals("Bl0", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Blanc", 1); //$NON-NLS-1$
    assertEquals("G1", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Blanc", 7); //$NON-NLS-1$
    assertEquals("G7", c.getCleCoul()); //$NON-NLS-1$
    c = new ColorimetricColor("Blanc", 8); //$NON-NLS-1$
    assertEquals("N8", c.getCleCoul()); //$NON-NLS-1$
    
    c = new ColorimetricColor("Rouge", 9); //$NON-NLS-1$
    assertEquals(null, c.getHue());
    assertEquals(0, c.getLightness());
    assertEquals(null, c.getUsualName());
    assertEquals(null, c.getCleCoul());
    assertEquals(0, c.getRedRGB());
    assertEquals(0, c.getGreenRGB());
    assertEquals(0, c.getBlueRGB());
    assertEquals(0, c.getXScreen());
    assertEquals(0, c.getYScreen());
    
    c = new ColorimetricColor("Mauve", 5); //$NON-NLS-1$
    assertEquals(null, c.getHue());
    assertEquals(0, c.getLightness());
    assertEquals(null, c.getUsualName());
    assertEquals(null, c.getCleCoul());
    assertEquals(0, c.getRedRGB());
    assertEquals(0, c.getGreenRGB());
    assertEquals(0, c.getBlueRGB());
    assertEquals(0, c.getXScreen());
    assertEquals(0, c.getYScreen());
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.lang.String)}.
   */
  @Test
  public final void testColorimetricColorString() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int, int, int)}.
   */
  @Test
  public final void testColorimetricColorIntIntInt() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(int, int, int, boolean)}.
   */
  @Test
  public final void testColorimetricColorIntIntIntBoolean() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.awt.Color)}.
   */
  @Test
  public final void testColorimetricColorColor() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(java.awt.Color, boolean)}.
   */
  @Test
  public final void testColorimetricColorColorBoolean() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toColor()}.
   */
  @Test
  public final void testToColor() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toXYZ()}.
   */
  @Test
  public final void testToXYZ() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toXYZ(float[])}.
   */
  @Test
  public final void testToXYZFloatArray() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getLab()}.
   */
  @Test
  public final void testGetLab() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getCIEChroma()}.
   */
  @Test
  public final void testGetCIEChroma() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#getCIEHue()}.
   */
  @Test
  public final void testGetCIEHue() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(float, float, float)}.
   */
  @Test
  public final void testColorimetricColorFloatFloatFloat() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#ColorimetricColor(float[])}.
   */
  @Test
  public final void testColorimetricColorFloatArray() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#labExistance(float, float, float)}.
   */
  @Test
  public final void testLabExistance() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setL(float)}.
   */
  @Test
  public final void testSetL() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setA(float)}.
   */
  @Test
  public final void testSetA() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#setB(float)}.
   */
  @Test
  public final void testSetB() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#equals(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testEqualsColorimetricColor() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#maxRGB()}.
   */
  @Test
  public final void testMaxRGB() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#distanceCIElab(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testDistanceCIElab() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#distanceRVB(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testDistanceRVB() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELab_Lightness_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testCIELab_Lightness_Difference() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELCh_Chroma_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testCIELCh_Chroma_Difference() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#CIELCh_Hue_Difference(fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor, fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor)}.
   */
  @Test
  public final void testCIELCh_Hue_Difference() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#toString()}.
   */
  @Test
  public final void testToString() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

  /**
   * Test method for {@link fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor#basicColorsComponents()}.
   */
  @Test
  public final void testBasicColorsComponents() {
    //fail("Not yet implemented"); // TODO //$NON-NLS-1$
  }

}
