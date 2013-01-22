package fr.ign.cogit.geoxygene.style.colorimetry;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.log4j.Logger;

/**
 * The ColorReferenceSystem class represent a specific organization of colors. A
 * ColorReferenceSystem is made up of {@link ColorWheel}s, themselves are made
 * up of {@link ColorSlice}s, themselves are made up of
 * {@link ColorimetricColor}s. The COGIT Color Reference System has been
 * elaborated by Elisabeth Chesneau in her PhD thesis. It is adapted to the
 * representation of hazard maps. It can be displayed using the GeOxygene
 * Application.
 * 
 * @author Charlotte Hoarau
 * 
 */
@XmlRootElement(name = "ColorReferenceSystem")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorReferenceSystem", propOrder = { "wheels" })
public class ColorReferenceSystem {

  static Logger logger = Logger.getLogger(ColorReferenceSystem.class.getName());

  public final static ColorReferenceSystem COGITcrs = ColorReferenceSystem
      .unmarshall(ColorReferenceSystem.class.getResourceAsStream(
          "ColorReferenceSystem.xml")); //$NON-NLS-1$
  /**
   * List of the Color Wheels of the COGIT Reference System.
   */
  @XmlElement(name = "Wheel")
  protected List<ColorWheel> wheels;

  /**
   * Return the list of the Color Wheels of the COGIT Reference System.
   * @return The list of the Color Wheels of the COGIT Reference System.
   */
  public List<ColorWheel> getWheels() {
    if (this.wheels == null) {
      this.wheels = new ArrayList<ColorWheel>();
    }
    return this.wheels;
  }

  /**
   * Set the list of the Color Wheels of the COGIT Reference System.
   * @param wheels The list of the Color Wheels of the COGIT Reference System.
   */
  public void setWheels(List<ColorWheel> wheels) {
    this.wheels = wheels;
  }

  /**
   * Methods to marshall and unmarshall the ColorReferenceSystems
   */

  /**
   * Load the CRS (Color Reference System) described in the input file. If the
   * file does'nt exist, a new empty CRS is created.
   * @param stream Input file describing the CRS to load.
   * @return The CRS described in the input file or a new empty CRS if the file
   *         does'nt exist.
   */
  public static ColorReferenceSystem unmarshall(InputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(ColorReferenceSystem.class);
      Unmarshaller m = context.createUnmarshaller();
      ColorReferenceSystem crs = (ColorReferenceSystem) m.unmarshal(stream);
      return crs;
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new ColorReferenceSystem();
  }

  /**
   * Load the CRS (Color Reference System) described in the XML file. If the
   * file does'nt exist, a new empty CRS is created.
   * @param fileName XML file describing the CRS to load.
   * @return The CRS described in the XML file or a new empty CRS if the file
   *         does'nt exist.
   */
  public static ColorReferenceSystem unmarshall(String fileName) {
    try {
      return ColorReferenceSystem.unmarshall(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      ColorReferenceSystem.logger.error("File " + fileName //$NON-NLS-1$
          + " could not be read"); //$NON-NLS-1$
      return new ColorReferenceSystem();
    }
  }

  /**
   * Write the CRS (Color Reference System).
   * @param writer The writer to write the CRS.
   */
  public void marshall(Writer writer) {
    try {
      JAXBContext context = JAXBContext.newInstance(ColorReferenceSystem.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, writer);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Save the CRS (Color Reference System) in the given file.
   * @param stream The file to save the CRS.
   */
  public void marshall(OutputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(ColorReferenceSystem.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(this, stream);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
  }

  /**
   * Save the CRS (Color Reference System) in the given file.
   * @param fileName The name of the file to save the CRS.
   */
  public void marshall(String fileName) {
    try {
      this.marshall(new FileOutputStream(fileName));
    } catch (FileNotFoundException e) {
      ColorReferenceSystem.logger.error("File " + fileName //$NON-NLS-1$
          + " could not be written to"); //$NON-NLS-1$
    }
  }

  /**
   * This method return a list of the slices of this ColorReferenceSystem.
   * @return A list of the slices of this ColorReferenceSystem.
   */
  public List<ColorSlice> getSlices() {
    List<ColorSlice> sliceList = new ArrayList<ColorSlice>();
    for (ColorWheel wheel : this.getWheels()) {
      for (ColorSlice colorSlice : wheel.getSlices()) {
        sliceList.add(colorSlice);
      }
    }
    return sliceList;
  }

  /**
   * This method return a list of the colors of a slice.
   * @param saturationWheel Saturation level of the wheel of the slice.
   * @param hueSlice Hue of the slice.
   * @return The list of the colors of the slice of this Color Reference System.
   */
  public List<ColorimetricColor> getSliceColors(int saturationWheel,
      String hueSlice) {
    List<ColorSlice> slices = this.getWheels().get(saturationWheel).getSlices();
    List<ColorimetricColor> colors = new ArrayList<ColorimetricColor>();
    for (ColorSlice slice : slices) {
      if (slice.getHue().equalsIgnoreCase(hueSlice)) {
        colors = slice.getColors();
      }
    }
    return colors;
  }

  /**
   * This method return a list of the colors of a slice of a given Color
   * Reference System.
   * @param saturationWheel Saturation level of the wheel of the slice.
   * @param nbSlice Number of the slice in the List of slices of the wheel.
   * @return The list of the colors of the slice of this Color Reference System.
   */
  public List<ColorimetricColor> getSlice(int saturationWheel, int nbSlice) {
    return this.getWheels().get(saturationWheel).getSlices().get(nbSlice)
        .getColors();
  }

  /**
   * This method return a list of all the color of a given Color Reference
   * System.
   * @return The list of all the colors of this Color Reference System.
   */
  public List<ColorimetricColor> getAllColors() {
    List<ColorimetricColor> allColors = new ArrayList<ColorimetricColor>();

    List<ColorWheel> allWheels = this.getWheels();
    for (ColorWheel wheel : allWheels) {
      List<ColorSlice> allSlices = wheel.getSlices();
      for (ColorSlice slice : allSlices) {
        List<ColorimetricColor> colors = slice.getColors();
        for (ColorimetricColor color : colors) {
          allColors.add(color);
        }
      }
    }
    return allColors;
  }

  /**
   * This method searches and return the 2 neighbor colors regarding the hue.
   * @param c The initial color.
   * @return The list of the 2 neighbor colors regarding the hue.
   */
  public List<ColorimetricColor> getHueNeighborColors(ColorimetricColor c) {
    List<ColorimetricColor> neighborColors = new ArrayList<ColorimetricColor>();

    ColorSlice cSlice = c.getSlice(this);
    ColorWheel cWheel = c.getWheel(this);
    int cLightness = c.getLightness();

    List<ColorSlice> cWheelSlices = cWheel.getSlices();
    for (int i = 0; i < cWheelSlices.size(); i++) {
      if (cWheelSlices.get(i).getHue() == cSlice.getHue()) {
        // Case of the first slice
        if (i == 0) {
          neighborColors.add(new ColorimetricColor(cWheelSlices.get(
              cWheelSlices.size() - 1).getHue(), cLightness));
          neighborColors.add(new ColorimetricColor(cWheelSlices.get(i + 1)
              .getHue(), cLightness));
          // Case of the last slice
        } else if (i == cWheelSlices.size() - 1) {
          neighborColors.add(new ColorimetricColor(cWheelSlices.get(i - 1)
              .getHue(), cLightness));
          neighborColors.add(new ColorimetricColor(
              cWheelSlices.get(0).getHue(), cLightness));
          // Generic case for all the other slices
        } else {
          neighborColors.add(new ColorimetricColor(cWheelSlices.get(i - 1)
              .getHue(), cLightness));
          neighborColors.add(new ColorimetricColor(cWheelSlices.get(i + 1)
              .getHue(), cLightness));
        }
      }
    }
    return neighborColors;
  }

  /**
   * Tests different methods dealing with colors. TODO : Create a unit testing
   * method to do that
   * @param args
   */
  public static void main(String[] args) {
    // Test of the method getHueNeighborColors
    ColorimetricColor c = new ColorimetricColor("JAUNE ORANGE", 5); //$NON-NLS-1$
    System.out.println("Couleur Origine : " + c.usualName); //$NON-NLS-1$
    System.out.println();

    ColorReferenceSystem crs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResourceAsStream(
            "ColorReferenceSystem.xml")); //$NON-NLS-1$
    List<ColorimetricColor> voisins = crs.getHueNeighborColors(c);

    System.out.println("Liste des voisins : "); //$NON-NLS-1$
    System.out.println(voisins.get(0).usualName);
    System.out.println(voisins.get(1).usualName);
    System.out.println();

    // Test of the method searchColor
    Color cJava = Color.yellow;
    System.out.println("Couleur Origine : " + cJava.toString()); //$NON-NLS-1$
    System.out.println();

    // Test of the method getSliceColors
    List<ColorimetricColor> sliceColors = crs.getSliceColors(0, "BLEU"); //$NON-NLS-1$
    System.out.println(sliceColors);
  }

  /**
   * This method return the list of all the colors of the COGIT
   * {@link ColorReferenceSystem}.
   * @return The list of all the colors of the COGIT
   *         {@link ColorReferenceSystem}.
   */
  public static List<ColorimetricColor> getCOGITColors() {
    List<ColorimetricColor> listCouleurs = new ArrayList<ColorimetricColor>();
    ColorReferenceSystem crs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResourceAsStream(
            "ColorReferenceSystem.xml")); //$NON-NLS-1$
    listCouleurs = crs.getAllColors();

    return listCouleurs;
  }

  /**
   * This method find a Java Color in the COGIT {@link ColorReferenceSystem}.
   * When the color exist in the color reference system, the method return it.
   * When the color doesn't exist in the color wheel, the method return the
   * nearest color regarding the CIELab Euclidean distance between 2 colors.
   * @param cTest The Java Color to find.
   * @return The corresponding COGIT Reference Color or the nearest COGIT
   *         Reference Color according to the Euclidean Distance in the CIELab
   *         System.
   */
  public static ColorimetricColor searchColor(Color cTest) {
    ColorimetricColor couleur = null;
    ColorimetricColor c = new ColorimetricColor(cTest);
    List<ColorimetricColor> cogitColors = ColorReferenceSystem.getCOGITColors();

    float distMin = 10000f;
    ColorimetricColor cNear = null;

    for (int i = 0; i < cogitColors.size(); i++) {
      if (c.equals(cogitColors.get(i))) {
        couleur = cogitColors.get(i);
      } else if (ColorimetricColor.distanceCIElab(c, cogitColors.get(i)) < distMin) {
        cNear = cogitColors.get(i);
        distMin = ColorimetricColor.distanceCIElab(c, cogitColors.get(i));
      }
    }
    if (couleur == null) {
      couleur = cNear;
    }
    return couleur;
  }

  /**
   * This method indicates whether some {@link ColorimetricColor} exists in the
   * COGIT {@link ColorReferenceSystem}.
   * @param c The color to test.
   * @return True if it's a COGIT color, false otherwise.
   */
  public static boolean isCOGITColor(ColorimetricColor c) {
    boolean exist = false;

    List<ColorimetricColor> cogitColors = ColorReferenceSystem.getCOGITColors();

    for (int i = 0; i < cogitColors.size(); i++) {
      if (c.equals(cogitColors.get(i))) {
        exist = true;
      }
    }

    return exist;
  }

  /**
   * This method return the pure colors of the COGIT
   * {@link ColorReferenceSystem}. The pure Colors are described in the
   * E.Chesneau's PhD thesis.
   * @return A list with the pure colors of the COGIT
   *         {@link ColorReferenceSystem}.
   */
  public static List<ColorimetricColor> getPureColors() {
    List<ColorimetricColor> pureColors = new ArrayList<ColorimetricColor>();
    ColorReferenceSystem crs = ColorReferenceSystem
        .unmarshall(ColorReferenceSystem.class.getResourceAsStream(
            "ColorReferenceSystem.xml")); //$NON-NLS-1$
    for (int j = 0; j < 12; j++) {

      List<ColorimetricColor> sliceCOGIT = crs.getSlice(0, j);

      if (j == 0 || j == 1 || j == 2 || j == 3 || j == 11) {
        pureColors.add(sliceCOGIT.get(sliceCOGIT.size() - 3));
      } else if (j == 4 || j == 9 || j == 10) {
        pureColors.add(sliceCOGIT.get(sliceCOGIT.size() - 2));
      } else {
        pureColors.add(sliceCOGIT.get(sliceCOGIT.size() - 1));
      }
    }
    return pureColors;
  }
  
    public static ColorReferenceSystem defaultColorRS() {
        return ColorReferenceSystem.unmarshall(ColorReferenceSystem.class
                .getResourceAsStream("ColorReferenceSystem.xml")); //$NON-NLS-1$

    }

}
