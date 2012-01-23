package fr.ign.cogit.geoxygene.style.colorimetry;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.util.algo.MathUtil;

/**
 * The ColorimetricColor class represent a color by different colorimetric
 * systems. It aims to deal with the RGB, CIELab, CIEXYZ, Java and COGIT
 * reference systems. TODO : synchroniser les paramètres de couleurs! Modifier
 * la teinte devrait également modifier les codes RVB par exemple !!
 * 
 * @author Charlotte Hoarau
 * @author Elodie Buard
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorimetricColor", propOrder = { "idColor", "hue",
    "lightness", "usualName", "cleCoul", "redRGB", "greenRGB", "blueRGB",
    "xScreen", "yScreen" })
public class ColorimetricColor {
  static Logger logger = Logger.getLogger(ColorimetricColor.class);

  /**
   * Color identifier in the COGIT reference system.
   */
  @XmlElement(name = "IdColor")
  protected int idColor;

  /**
   * Return the color identifier in the COGIT reference system.
   * @return The color identifier in the COGIT reference system.
   */
  public int getIdColor() {
    return this.idColor;
  }

  /**
   * Set the identifier of the ColorimetricColor.
   * @param id The identifier of the ColorimetricColor.
   */
  public void setIdColor(int id) {
    this.idColor = id;
  }

  // ////////////////////////////////////////////////////////////////////////
  // ------------------- Cercles Chromatiques COGIT ---------------------//
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Common hue of the colors of a {@link ColorSlice}. It is expressed in
   * French. For example BLEU
   */
  @XmlElement(name = "Hue")
  protected String hue;

  /**
   * Return the common hue of the colors of a {@link ColorSlice}.
   * @return The common hue of the colors of a {@link ColorSlice}.
   */
  public String getHue() {
    return this.hue;
  }

  /**
   * Set the common hue of the colors of a {@link ColorSlice}.
   * @param hue The common hue of the colors of a {@link ColorSlice}.
   */
  public void setHue(String hue) {
    this.hue = hue;
  }

  /**
   * Return the corresponding {@link ColorSlice} in the
   * {@link ColorReferenceSystem} given in parameter.
   * @param crs The Color Reference System.
   * @return The corresponding {@link ColorSlice}
   */
  public ColorSlice getSlice(ColorReferenceSystem crs) {
    ColorSlice sliceColor = null;

    List<ColorWheel> wheels = crs.getWheels();
    for (ColorWheel wheel : wheels) {
      List<ColorSlice> slices = wheel.getSlices();
      for (ColorSlice slice : slices) {
        List<ColorimetricColor> colors = slice.getColors();
        for (ColorimetricColor color : colors) {
          if (this.equals(color)) {
            sliceColor = slice;
          }
        }
      }
    }

    return sliceColor;
  }

  /**
   * Return the corresponding {@link ColorWheel} in the
   * {@link ColorReferenceSystem} given in parameter.
   * @param crs The Color Reference System.
   * @return The corresponding {@link ColorWheel}
   */
  public ColorWheel getWheel(ColorReferenceSystem crs) {
    ColorWheel wheelColor = null;

    List<ColorWheel> wheels = crs.getWheels();
    for (ColorWheel wheel : wheels) {
      List<ColorSlice> slices = wheel.getSlices();
      for (ColorSlice slice : slices) {
        List<ColorimetricColor> colors = slice.getColors();
        for (ColorimetricColor color : colors) {
          if (this.equals(color)) {
            wheelColor = wheel;
          }
        }
      }
    }

    return wheelColor;
  }

  /**
   * Lightness level of the color. It range from 1 to 7. All the levels does not
   * exist for all the slices.
   */
  @XmlElement(name = "Lightness")
  protected int lightness;

  /**
   * Return the lightness level of the color.
   * @return The lightness level of the color.
   */
  public int getLightness() {
    return this.lightness;
  }

  /**
   * Set the lightness level of the color.
   * @param lightness The lightness level of the color.
   */
  public void setLightness(int lightness) {
    this.lightness = lightness;
  }

  /**
   * Usual name of the color. It is made up of the hue of the corresponding
   * {@link ColorSlice} and a literal transcription of the lightness level. It
   * is expressed in French. For example: BLEU TRES FONCE
   */
  @XmlElement(name = "UsualName")
  protected String usualName;

  /**
   * Return the usual name of the color.
   * @return The usual name of the color.
   */
  public String getUsualName() {
    return this.usualName;
  }

  /**
   * Set the usual name of the color.
   * @param usualName The usual name of the color.
   */
  public void setUsualName(String usualName) {
    this.usualName = usualName;
  }

  /**
   * Key of the color in the COGIT Reference System. It is an other identifier
   * of the color. It is made up of the initial of the hue of the corresponding
   * {@link ColorSlice} and the lightness level of the color ranged from 1 to 7.
   * For example: B6 for BLEU FONCE
   */
  @XmlElement(name = "CleCoul")
  protected String cleCoul;

  /**
   * Return the key of the color in the COGIT Reference System.
   * @return The key of the color in the COGIT Reference System.
   */
  public String getCleCoul() {
    return this.cleCoul;
  }

  /**
   * Set the key of the color in the COGIT Reference System.
   * @param cleCoul the key of the color in the COGIT Reference System.
   */
  public void setCleCoul(String cleCoul) {
    this.cleCoul = cleCoul;
  }

  // ////////////////////////////////////////////////////////////////////////
  // -------------------------- Système RVB -----------------------------//
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Red component in the RGB color system. It range from 0 to 255.
   */
  @XmlElement(name = "redRGB")
  protected int redRGB;

  /**
   * Return the Red component in the RGB color system.
   * @return The Red component in the RGB color system.
   */
  public int getRedRGB() {
    return this.redRGB;
  }

  /**
   * Set the Red component in the RGB color system.
   * @param redRGB The Red component in the RGB color system.
   */
  public void setRedRGB(int redRGB) {
    this.redRGB = redRGB;
  }

  /**
   * Green component in the RGB color system. It range from 0 to 255.
   */
  @XmlElement(name = "greenRGB")
  protected int greenRGB;

  /**
   * Return the Green component in the RGB color system.
   * @return The Green component in the RGB color system.
   */
  public int getGreenRGB() {
    return this.greenRGB;
  }

  /**
   * Set the Green component in the RGB color system.
   * @param greenRGB The Green component in the RGB color system.
   */
  public void setGreenRGB(int greenRGB) {
    this.greenRGB = greenRGB;
  }

  /**
   * Blue component in the RGB color system. It range from 0 to 255.
   */
  @XmlElement(name = "blueRGB")
  protected int blueRGB;

  /**
   * Return the Blue component in the RGB color system.
   * @return The Blue component in the RGB color system.
   */
  public int getBlueRGB() {
    return this.blueRGB;
  }

  /**
   * Set the Blue component in the RGB color system.
   * @param blueRGB The Blue component in the RGB color system.
   */
  public void setBlueRGB(int blueRGB) {
    this.blueRGB = blueRGB;
  }

  // ////////////////////////////////////////////////////////////////////////
  // ------ Coordinates on the COGITColorChooserPanel -------------------//
  // ////////////////////////////////////////////////////////////////////////
  /**
   * The X Coordinate of the center of this color on the COGITColorChooserPanel.
   */
  @XmlElement(name = "xScreen")
  protected int xScreen;

  /**
   * Return the X Coordinate of the center of this color on the
   * COGITColorChooserPanel.
   * @return The X Coordinate of the center of this color on the
   *         COGITColorChooserPanel.
   */
  public int getXScreen() {
    return this.xScreen;
  }

  /**
   * Set the X Coordinate of the center of this color on the
   * COGITColorChooserPanel.
   * @param x The X Coordinate of the center of this color on the
   *          COGITColorChooserPanel.
   */
  public void setXScreen(int x) {
    this.xScreen = x;
  }

  /**
   * The Y Coordinate of the center of this color on the COGITColorChooserPanel.
   */
  @XmlElement(name = "yScreen")
  protected int yScreen;

  /**
   * Return the Y Coordinate of the center of this color on the
   * COGITColorChooserPanel.
   * @return The Y Coordinate of the center of this color on the
   *         COGITColorChooserPanel.
   */
  public int getYScreen() {
    return this.yScreen;
  }

  /**
   * Set the Y Coordinate of the center of this color on the
   * COGITColorChooserPanel.
   * @param y The Y Coordinate of the center of this color on the
   *          COGITColorChooserPanel.
   */
  public void setYScreen(int y) {
    this.yScreen = y;
  }

  // ////////////////////////////////////////////////////////////////////////
  // ------------------------- Constructeurs ----------------------------//
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Default constructor.
   */
  public ColorimetricColor() {
  }

  /**
   * Constructor with the color identifier in the COGIT reference system.
   * @param idColor The color identifier in the COGIT reference system.
   */
  public ColorimetricColor(int idColor) {
    List<ColorimetricColor> cogitColors = ColorReferenceSystem.getCOGITColors();

    for (int i = 0; i < cogitColors.size(); i++) {
      if (cogitColors.get(i).getIdColor() == idColor) {
        this.redRGB = cogitColors.get(i).getRedRGB();
        this.greenRGB = cogitColors.get(i).getGreenRGB();
        this.blueRGB = cogitColors.get(i).getBlueRGB();
        this.idColor = cogitColors.get(i).getIdColor();
        this.cleCoul = cogitColors.get(i).getCleCoul();
        this.hue = cogitColors.get(i).getHue();
        this.lightness = cogitColors.get(i).getLightness();
        this.usualName = cogitColors.get(i).getUsualName();
        this.xScreen = cogitColors.get(i).getXScreen();
        this.yScreen = cogitColors.get(i).getYScreen();
      }
    }
  }

  /**
   * Constructor with the name of the hue and the lightness of the color
   * @param hue The name of the hue of the corresponding {@link ColorSlice}
   * @param lightness The lightness level of the color
   **/
  @SuppressWarnings("null")
  public ColorimetricColor(String hue, int lightness) {
    List<ColorimetricColor> cogitColors = ColorReferenceSystem.getCOGITColors();
    ColorimetricColor color = null;

    for (int i = 0; i < cogitColors.size(); i++) {
      if (cogitColors.get(i).getHue().equalsIgnoreCase(hue)
          && cogitColors.get(i).getLightness() == lightness) {
        color = cogitColors.get(i);
      }
    }
    if (color == null) {
      if (!hue.equalsIgnoreCase("GRIS") //$NON-NLS-1$
          && !hue.equalsIgnoreCase("NOIR") //$NON-NLS-1$
          && !hue.equalsIgnoreCase("BLANC")) { //$NON-NLS-1$

        if (lightness == 0) {
          color = new ColorimetricColor(hue.toUpperCase(), 1);
        } else if (lightness == 8) {
          color = new ColorimetricColor(hue.toUpperCase(), 7);
        }
      } else if (hue.equalsIgnoreCase("GRIS")) { //$NON-NLS-1$
        if (lightness == 0) {
          color = new ColorimetricColor(85);
        } else if (lightness == 8) {
          color = new ColorimetricColor(86);
        }
      } else if (hue.equalsIgnoreCase("NOIR")) { //$NON-NLS-1$
        if (lightness == 0) {
          color = new ColorimetricColor(85);
        } else {
          color = new ColorimetricColor("GRIS", lightness); //$NON-NLS-1$
        }
      } else if (hue.equalsIgnoreCase("BLANC")) { //$NON-NLS-1$
        if (lightness == 8) {
          color = new ColorimetricColor(86);
        } else {
          color = new ColorimetricColor("GRIS", lightness); //$NON-NLS-1$
        }
      }
    }
    this.redRGB = color.getRedRGB();
    this.greenRGB = color.getGreenRGB();
    this.blueRGB = color.getBlueRGB();
    this.idColor = color.getIdColor();
    this.cleCoul = color.getCleCoul();
    this.hue = color.getHue();
    this.lightness = color.getLightness();
    this.usualName = color.getUsualName();
    this.xScreen = color.getXScreen();
    this.yScreen = color.getYScreen();
  }

  public static void main(String[] args) {
    ColorimetricColor c = new ColorimetricColor("RoUge", 0); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("RoUge", 1); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Rouge", 7); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Rouge", 8); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    ColorimetricColor.logger.info(""); //$NON-NLS-1$

    c = new ColorimetricColor("Gris", 0); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Gris", 1); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Gris", 7); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Gris", 8); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    ColorimetricColor.logger.info(""); //$NON-NLS-1$

    c = new ColorimetricColor("Noir", 0); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Noir", 1); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Noir", 7); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Noir", 8); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    ColorimetricColor.logger.info(""); //$NON-NLS-1$

    c = new ColorimetricColor("Blanc", 0); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Blanc", 1); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Blanc", 7); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
    c = new ColorimetricColor("Blanc", 8); //$NON-NLS-1$
    ColorimetricColor.logger.info(c.getCleCoul());
  }

  /**
   * Constructor with the usual name of the color
   * @param usualName The usual name of the color in the COGIT
   *          ColorReferenceSystem
   **/
  public ColorimetricColor(String usualName) {
    List<ColorimetricColor> cogitColors = ColorReferenceSystem.getCOGITColors();

    for (int i = 0; i < cogitColors.size(); i++) {
      if (cogitColors.get(i).getUsualName().equalsIgnoreCase(usualName)) {
        this.redRGB = cogitColors.get(i).getRedRGB();
        this.greenRGB = cogitColors.get(i).getGreenRGB();
        this.blueRGB = cogitColors.get(i).getBlueRGB();
        this.idColor = cogitColors.get(i).getIdColor();
        this.cleCoul = cogitColors.get(i).getCleCoul();
        this.hue = cogitColors.get(i).getHue();
        this.lightness = cogitColors.get(i).getLightness();
        this.usualName = cogitColors.get(i).getUsualName();
        this.xScreen = cogitColors.get(i).getXScreen();
        this.yScreen = cogitColors.get(i).getYScreen();
      }
    }
  }

  /**
   * Constructor with the RGB color components
   * @param r The Red component in the RGB color system.
   * @param g The Green component in the RGB color system.
   * @param b The Blue component in the RGB color system.
   **/
  public ColorimetricColor(int r, int g, int b) {
    this.redRGB = r;
    this.greenRGB = g;
    this.blueRGB = b;
  }

  /**
   * Constructor with the RGB color component The boolean specify if the color
   * must be in the COGIT Color Reference System.
   * @param r The Red component in the RGB color system.
   * @param g The Green component in the RGB color system.
   * @param b The Blue component in the RGB color system.
   * @param reference true if the color must be in the COGIT Color Reference
   *          System, false else.
   **/
  public ColorimetricColor(int r, int g, int b, boolean reference) {
    if (reference) {
      ColorimetricColor nearestColor = ColorReferenceSystem
          .searchColor(new Color(r, g, b));
      this.redRGB = nearestColor.getRedRGB();
      this.greenRGB = nearestColor.getGreenRGB();
      this.blueRGB = nearestColor.getBlueRGB();
      this.idColor = nearestColor.getIdColor();
      this.cleCoul = nearestColor.getCleCoul();
      this.hue = nearestColor.getHue();
      this.lightness = nearestColor.getLightness();
      this.usualName = nearestColor.getUsualName();
      this.xScreen = nearestColor.getXScreen();
      this.yScreen = nearestColor.getYScreen();
    } else {
      this.redRGB = r;
      this.greenRGB = g;
      this.blueRGB = b;
    }
  }

  /**
   * Constructor with a java color
   * @param c Java Color
   **/
  public ColorimetricColor(Color c) {
    this.redRGB = c.getRed();
    this.greenRGB = c.getGreen();
    this.blueRGB = c.getBlue();
  }

  /**
   * Constructor with a java color. The boolean specify if the color must be in
   * the COGIT Color Reference System.
   * @param c Java Color.
   * @param reference true if the color must be in the COGIT Color Reference
   *          System, false else.
   */
  public ColorimetricColor(Color c, boolean reference) {
    if (reference) {
      ColorimetricColor nearestColor = ColorReferenceSystem.searchColor(c);
      this.redRGB = nearestColor.getRedRGB();
      this.greenRGB = nearestColor.getGreenRGB();
      this.blueRGB = nearestColor.getBlueRGB();
      this.idColor = nearestColor.getIdColor();
      this.cleCoul = nearestColor.getCleCoul();
      this.hue = nearestColor.getHue();
      this.lightness = nearestColor.getLightness();
      this.usualName = nearestColor.getUsualName();
      this.xScreen = nearestColor.getXScreen();
      this.yScreen = nearestColor.getYScreen();
    } else {
      this.redRGB = c.getRed();
      this.greenRGB = c.getGreen();
      this.blueRGB = c.getBlue();
    }
  }

  /**
   * Conversion of a COGIT reference color to a Java Color.
   * @return The corresponding Java Color.
   */
  public Color toColor() {
    return new Color(this.getRedRGB(), this.getGreenRGB(), this.getBlueRGB());
  }

  /**
   * Conversion of a color to a CIEXYZ Color. The conversion is made from the
   * RGB components.
   * @return The corresponding CIEXYZ color components.
   */
  public float[] toXYZ() {
    float[] xyz = this.toColor().getColorComponents(
        ColorSpace.getInstance(ColorSpace.CS_CIEXYZ), null);
    return xyz;
  }

  /**
   * Conversion of a color to a CIEXYZ Color. The conversion is made from the
   * CIELab components.
   * @param lab CIELab components.
   * @return The corresponding CIEXYZ color components.
   */
  public static float[] toXYZ(float[] lab) {

    float fy = (lab[0] + 16f) / 116f;
    float[] f = new float[3];
    f[0] = fy + lab[1] / 500f;
    f[1] = fy;
    f[2] = fy - lab[2] / 200f;

    float delta = 6f / 29f;
    float[] xyz = new float[3];

    for (int i = 0; i < 3; i++) {
      if (f[i] > delta) {
        xyz[i] = f[i] * f[i] * f[i];
      } else {
        xyz[i] = (f[i] - 16f / 116f) * delta * delta * 3f;
      }
    }

    return xyz;
  }

  /**
   * Return the CIELab color components.
   * @return The CIELab color components.
   */
  public float[] getLab() {

    float[] xyz = this.toXYZ();
    float x = xyz[0];
    float y = xyz[1];
    float z = xyz[2];
    float l = 116f * ColorimetricColor.f(y) - 16f;
    float a = 500f * (ColorimetricColor.f(x) - ColorimetricColor.f(y));
    float b = 200f * (ColorimetricColor.f(y) - ColorimetricColor.f(z));
    return new float[] { l, a, b };
  }

  /**
   * Colorimetric function of the CIE 1976 Lab color space.
   * Used to calculate the CIE 1976 Lab color space components from CIEXYZ components.
   * @param t a CIEXYZ component.
   * @return The result of the function
   */
  public static float f(float t) {
    final float delta = 6f / 29f;
    float result;
    if (t > delta * delta * delta) {
      result = (float) Math.pow(t, 1f / 3f);
    } else {
      result = t / (3f * delta * delta) + 4f / 29f;
    }
    return result;
  }

  /**
   * Return the lightness in the CIE 1976 Lab color space.
   * @return The lightness in the CIE 1976 Lab color space.
   */
  public float getCIELabL() {
    return this.getLab()[0];
  }

  /**
   * Return the red-green chromatic component in the CIE 1976 Lab color space.
   * @return The red-green chromatic component in the CIE 1976 Lab color space.
   */
  public float getCIELabA() {
    return this.getLab()[1];
  }

  /**
   * Return the yellow-blue chromatic component in the CIE 1976 Lab color space.
   * @return The yellow-blue chromatic component in the CIE 1976 Lab color space.
   */
  public float getCIELabB() {
    return this.getLab()[2];
  }
  
  /**
   * Return the chroma (saturation) in the CIE 1976 LCh color space.
   * @return The chroma (saturation) in the CIE 1976 LCh color space.
   */
  public double getCIEChroma(){
    double chroma = Math.sqrt(this.getCIELabA() * this.getCIELabA() + this.getCIELabB() * this.getCIELabB());
    return chroma;
  }
  
  /**
   * Return the hue angle in the CIE 1976 LCh color space.
   * @return The hue angle in the CIE 1976 LCh color space.
   */
  public double getCIEHue(){
    double hue = Math.atan(this.getCIELabB() / this.getCIELabA());
    return hue;
  }

  /**
   * Constructor with the CIELab component.
   * 
   * FIXME Warning ! This constructor must be use with an existence test if the
   * existence is not sure!! FRENCH : Cette méthode doit s'utiliser avec des
   * tests si on n'est pas sûrs de l'existance d'une couleur!!
   * 
   * TODO : Create a unit testing method to give an exemple
   * 
   * @param l The lightness in the CIELab color reference system.
   * @param a First chromatic component in the CIELab color reference system.
   * @param b Second chromatic component in the CIELab color reference system.
   */
  public ColorimetricColor(float l, float a, float b) {
    float[] lab = new float[] { l, a, b };
    float[] xyz = ColorimetricColor.toXYZ(lab);

    Color c = new Color(ColorSpace.getInstance(ColorSpace.CS_CIEXYZ), xyz, 1f);

    this.redRGB = c.getRed();
    this.greenRGB = c.getGreen();
    this.blueRGB = c.getBlue();
  }

  public ColorimetricColor(float[] lab) {
    ColorimetricColor c = new ColorimetricColor(lab[0], lab[1], lab[2]);
    this.redRGB = c.getRedRGB();
    this.greenRGB = c.getGreenRGB();
    this.blueRGB = c.getBlueRGB();
  }

  public ColorimetricColor(ColorimetricColor colorimetricColor) {
	  this.blueRGB = colorimetricColor.blueRGB;
	  this.cleCoul = colorimetricColor.cleCoul;
	  this.greenRGB = colorimetricColor.greenRGB;
	  this.hue = colorimetricColor.hue;
	  this.idColor = colorimetricColor.idColor;
	  this.lightness =colorimetricColor.lightness;
	  this.redRGB  = colorimetricColor.redRGB;
	  this.usualName = colorimetricColor.usualName;
	  this.xScreen = colorimetricColor.xScreen;
	  this.yScreen = colorimetricColor.yScreen;
  }

/**
   * This method test if this CIELab coordinates describe an existing color.
   * @param l The lightness in the CIELab color reference system.
   * @param a First chromatic component in the CIELab color reference system.
   * @param b Second chromatic component in the CIELab color reference system.
   * @return True if the color exists, false otherwise. FIXME This method should
   *         be tested once again.
   */
  public static boolean labExistance(float l, float a, float b) {
    float[] lab = new float[] { l, a, b };

    float fy = (lab[0] + 16f) / 116f;
    float[] f = new float[3];
    f[0] = fy + lab[1] / 500f;
    f[1] = fy;
    f[2] = fy - lab[2] / 200f;

    float delta = 6f / 29f;
    float[] xyz = new float[3];

    for (int i = 0; i < 3; i++) {
      if (f[i] > delta) {
        xyz[i] = f[i] * f[i] * f[i];
      } else {
        xyz[i] = (f[i] - 16f / 116f) * delta * delta * 3f;
      }
    }

    float[] rgb = new float[3];

    float[][] m = new float[][] { { 3.2404542f, -1.5371385f, -0.4985314f },
        { -0.9692660f, 1.8760108f, 0.0415560f },
        { 0.0556434f, -0.2040259f, 1.0572252f } };

    for (int i = 0; i < 3; i++) {
      rgb[i] = m[i][0] * xyz[i] + m[i][1] * xyz[i] + m[i][2] * xyz[i];

      if (rgb[i] > 0.0031308) {
        rgb[i] = 1.055f * (float) Math.pow(rgb[i], 1 / 2.4) - 0.055f;
      } else {
        rgb[i] = 12.92f * rgb[i];
      }
    }

    if (rgb[0] <= 0.99 && rgb[0] > 0.01 && rgb[1] <= 0.99 && rgb[1] > 0.01
        && rgb[2] <= 0.99 && rgb[2] > 0.01) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Set the lightness in the CIELab color reference system.
   * @param l The lightness in the CIELab color reference system.
   */
  public void setL(float l) {
    float[] lab = this.getLab();
    lab[0] = l;
    ColorimetricColor c = new ColorimetricColor(lab[0], lab[1], lab[2]);
    this.redRGB = c.getRedRGB();
    this.greenRGB = c.getGreenRGB();
    this.blueRGB = c.getBlueRGB();
  }

  /**
   * Set the first chromatic component in the CIELab color reference system.
   * @param a First chromatic component in the CIELab color reference system.
   */
  public void setA(float a) {
    float[] lab = this.getLab();
    lab[1] = a;
    ColorimetricColor c = new ColorimetricColor(lab[0], lab[1], lab[2]);

    this.redRGB = c.getRedRGB();
    this.greenRGB = c.getGreenRGB();
    this.blueRGB = c.getBlueRGB();
  }

  /**
   * Set the second chromatic component in the CIELab color reference system.
   * @param b The second chromatic component in the CIELab color reference
   *          system.
   */
  public void setB(float b) {
    float[] lab = this.getLab();
    lab[2] = b;
    ColorimetricColor c = new ColorimetricColor(lab[0], lab[1], lab[2]);

    this.redRGB = c.getRedRGB();
    this.greenRGB = c.getGreenRGB();
    this.blueRGB = c.getBlueRGB();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * @param c The color to be tested.
   * @return true if this object is the same as the c argument; false otherwise.
   */
  public boolean equals(ColorimetricColor c) {
    if (this.getRedRGB() == c.getRedRGB()
        && this.getGreenRGB() == c.getGreenRGB()
        && this.getBlueRGB() == c.getBlueRGB()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Return the maximal RGB color component of this color.
   * @return The maximal RGB color component of this color.
   */
  public float maxRGB() {
    List<Double> pComposantes = new ArrayList<Double>();
    pComposantes.add((double) this.getRedRGB());
    pComposantes.add((double) this.getGreenRGB());
    pComposantes.add((double) this.getBlueRGB());
    return Collections.max(pComposantes).intValue();
  }

  /**
   * Calculate an euclidien distance between 2 colors in the CIE 1976 color space.
   * Ie is usually called "deltaE" and refers to the total color difference.
   * @param c1 First {@link ColorimetricColor}.
   * @param c2 Second {@link ColorimetricColor}.
   * @return the distance between 2 colors in the CIE 1976 color space.
   */
  public static float distanceCIElab(ColorimetricColor c1, ColorimetricColor c2) {
    float[] lab1 = c1.getLab();
    float[] lab2 = c2.getLab();
    return MathUtil.distEucl(lab1, lab2);
  }

  /**
   * Calculate an euclidien distance between 2 colors in the RGB color space.
   * @param c1 First {@link ColorimetricColor}.
   * @param c2 Second {@link ColorimetricColor}.
   * @return the distance between 2 colors in the RGB color space.
   */
  public static float distanceRVB(ColorimetricColor c1, ColorimetricColor c2) {
    float[] rgb1 = { c1.getRedRGB(), c1.getGreenRGB(), c1.getBlueRGB() };
    float[] rgb2 = { c2.getRedRGB(), c2.getGreenRGB(), c2.getBlueRGB() };
    return MathUtil.distEucl(rgb1, rgb2);
  }

  /**
   * Calculate the lightness difference between 2 colors in the CIE 1976 color space.
   * @param c1 First {@link ColorimetricColor}.
   * @param c2 Second {@link ColorimetricColor}.
   * @return The lightness difference between 2 colors in the CIE 1976 color space.
   */
  public static float CIELab_Lightness_Difference(ColorimetricColor c1,
      ColorimetricColor c2) {
    float deltaL = c1.getCIELabL() - c2.getCIELabL();
    return deltaL;
  }
  
  /**
   * Calculate the chroma difference between 2 colors in the CIELab system.
   * @param c1 First {@link ColorimetricColor}.
   * @param c2 Second {@link ColorimetricColor}.
   * @return The chroma difference between 2 colors in the CIELab system.
   */
  public static double CIELCh_Chroma_Difference(ColorimetricColor c1,
      ColorimetricColor c2) {
    double deltaC = c1.getCIEChroma() - c2.getCIEChroma();
    return deltaC;
  }

  /**
   * Calculate the hue contrast between 2 colors in the CIELab system.
   * @param c1 First {@link ColorimetricColor}.
   * @param c2 Second {@link ColorimetricColor}.
   * @return The hue contrast between 2 colors in the CIELab system.
   */
  public static double CIELCh_Hue_Difference(ColorimetricColor c1,
      ColorimetricColor c2) {
    double deltaE = distanceCIElab(c1, c2);
    double deltaL = CIELab_Lightness_Difference(c1, c2);
    double deltaC = CIELCh_Chroma_Difference(c1, c2);
    
    double deltaH = Math.pow(
        (deltaE*deltaE) - (deltaL*deltaL) - (deltaC*deltaC)
        , 0.5);
    return deltaH;
  }

  @Override
  public String toString() {
    String txtColor = "Color : R=" + this.getRedRGB() + " V=" //$NON-NLS-1$ //$NON-NLS-2$
        + this.getGreenRGB() + " B=" + this.getBlueRGB() + " l=" //$NON-NLS-1$ //$NON-NLS-2$
        + this.getLab()[0] + " a=" + this.getLab()[1] + " b=" //$NON-NLS-1$ //$NON-NLS-2$
        + this.getLab()[2];

    if (this.getCleCoul() != null) {
      txtColor += " Key : " + this.getCleCoul(); //$NON-NLS-1$
    }

    return txtColor;
  }

  @SuppressWarnings("nls")
  // TODO : Create a unit testing method to do that
  public static void basicColorsComponents() {
    ColorimetricColor rouge = new ColorimetricColor(255, 0, 0);
    ColorimetricColor vert = new ColorimetricColor(0, 255, 0);
    ColorimetricColor bleu = new ColorimetricColor(0, 0, 255);
    ColorimetricColor jaune = new ColorimetricColor(255, 255, 0);
    ColorimetricColor cyan = new ColorimetricColor(0, 255, 255);
    ColorimetricColor magenta = new ColorimetricColor(255, 255, 0);
    ColorimetricColor blanc = new ColorimetricColor(255, 255, 255);
    ColorimetricColor noir = new ColorimetricColor(0, 0, 0);

    System.out.println("rouge\t" + rouge.getRedRGB() + "\t"
        + rouge.getGreenRGB() + "\t" + rouge.getBlueRGB() + "\t"
        + rouge.getCIELabL() + "\t" + rouge.getCIELabA() + "\t"
        + rouge.getCIELabB());
    System.out.println("vert\t" + vert.getRedRGB() + "\t" + vert.getGreenRGB()
        + "\t" + vert.getBlueRGB() + "\t" + vert.getCIELabL() + "\t"
        + vert.getCIELabA() + "\t" + vert.getCIELabB());
    System.out.println("bleu\t" + bleu.getRedRGB() + "\t" + bleu.getGreenRGB()
        + "\t" + bleu.getBlueRGB() + "\t" + bleu.getCIELabL() + "\t"
        + bleu.getCIELabA() + "\t" + bleu.getCIELabB());
    System.out.println("jaune\t" + jaune.getRedRGB() + "\t"
        + jaune.getGreenRGB() + "\t" + jaune.getBlueRGB() + "\t"
        + jaune.getCIELabL() + "\t" + jaune.getCIELabA() + "\t"
        + jaune.getCIELabB());
    System.out.println("cyan\t" + cyan.getRedRGB() + "\t" + cyan.getGreenRGB()
        + "\t" + cyan.getBlueRGB() + "\t" + cyan.getCIELabL() + "\t"
        + cyan.getCIELabA() + "\t" + cyan.getCIELabB());
    System.out.println("magenta\t" + magenta.getRedRGB() + "\t"
        + magenta.getGreenRGB() + "\t" + magenta.getBlueRGB() + "\t"
        + magenta.getCIELabL() + "\t" + magenta.getCIELabA() + "\t"
        + magenta.getCIELabB());
    System.out.println("blanc\t" + blanc.getRedRGB() + "\t"
        + blanc.getGreenRGB() + "\t" + blanc.getBlueRGB() + "\t"
        + blanc.getCIELabL() + "\t" + blanc.getCIELabA() + "\t"
        + blanc.getCIELabB() + "\t");
    System.out.println("noir\t" + noir.getRedRGB() + "\t" + noir.getGreenRGB()
        + "\t" + noir.getBlueRGB() + "\t" + noir.getCIELabL() + "\t"
        + noir.getCIELabA() + "\t" + noir.getCIELabB());
  }
}
