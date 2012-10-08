package fr.ign.cogit.geoxygene.style.colorimetry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

/**
 * Common class with comparative methods.
 *
 */
public class ColorimetryTest {
  
  /**
   * Compare 2 ColorimetricColor (compare each attributes, one by one).
   * @param ColorimetricColor result, i.e. calculated
   * @param ColorimetricColor expected 
   */
  protected void compareColorimetricColor(ColorimetricColor result, ColorimetricColor expected) {
    
    // logger.trace("            Result  : " + result.toString());
    // logger.trace("          Expected  : " + expected.toString());
    
    assertEquals("Compare IdColor. ", expected.getIdColor(), result.getIdColor());
    assertEquals("Compare Hue. ", expected.getHue(), result.getHue());
    assertEquals("Compare Lightness. ", expected.getLightness(), result.getLightness());
    assertEquals("Compare UsualName. ", expected.getUsualName(), result.getUsualName());
    assertEquals("Compare CleCoul. ", expected.getCleCoul(), result.getCleCoul());
    
    assertEquals("Compare RedRGB. ", expected.getRedRGB(), result.getRedRGB());
    assertEquals("Compare GreenRGB. ", expected.getGreenRGB(), result.getGreenRGB());
    assertEquals("Compare BlueRGB. ", expected.getBlueRGB(), result.getBlueRGB());
    
    assertEquals("Compare XScreen. ", expected.getXScreen(), result.getXScreen());
    assertEquals("Compare YScreen. ", expected.getYScreen(), result.getYScreen());
  }
  
  /**
   * Compare 2 ColorimetricColor (compare each attributes excepted colorId, one by one).
   * @param ColorimetricColor result, i.e. calculated
   * @param ColorimetricColor expected 
   */
  protected void compareColorimetricColorWithoutId(ColorimetricColor result, ColorimetricColor expected) {
    
    // logger.trace("            Result  : " + result.toString());
    // logger.trace("          Expected  : " + expected.toString());
    
    assertEquals("Compare Hue. ", expected.getHue(), result.getHue());
    assertEquals("Compare Lightness. ", expected.getLightness(), result.getLightness());
    assertEquals("Compare UsualName. ", expected.getUsualName(), result.getUsualName());
    assertEquals("Compare CleCoul. ", expected.getCleCoul(), result.getCleCoul());
    
    assertEquals("Compare RedRGB. ", expected.getRedRGB(), result.getRedRGB());
    assertEquals("Compare GreenRGB. ", expected.getGreenRGB(), result.getGreenRGB());
    assertEquals("Compare BlueRGB. ", expected.getBlueRGB(), result.getBlueRGB());
    
    assertEquals("Compare XScreen. ", expected.getXScreen(), result.getXScreen());
    assertEquals("Compare YScreen. ", expected.getYScreen(), result.getYScreen());
  }
  
  /**
   * Compare 2 ColorimetricColor list (compare each ColorimetricColor, one by one).
   * @param expectedList
   * @param resultList
   */
  protected void compareColorimetricColorList(List<ColorimetricColor> expectedList, List<ColorimetricColor> resultList) {
    
    assertTrue("The result is empty ", resultList.size() > 0);

    assertEquals("Not find the same number of ColorimetricColor ", expectedList.size(), resultList.size());
    
    // We suppose keys are identical 
    for (int i = 0; i < resultList.size(); i++) {
      ColorimetricColor cResult = resultList.get(i);
      ColorimetricColor cExpected = expectedList.get(i);
      // compare ColorimetricColor object, excepted colorId
      compareColorimetricColorWithoutId(cResult, cExpected);
    }
    
  }

}
