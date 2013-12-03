package fr.ign.cogit.geoxygene.style.colorimetry;

import java.awt.Color;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ColorJaxbAdaptor extends XmlAdapter<String, Color> {

  @Override
  public Color unmarshal(String s) {

    // The tint color
    if (s.length() == 9) {
      String sRGB = "#" + s.substring(2, s.length());

      return Color.decode(sRGB);
    } else {
      return Color.decode(s);
    }

  }

  @Override
  public String marshal(Color c) {
    if (c.getAlpha() == 0) {
      return "#" + Integer.toHexString(c.getRGB());
    } else {
      return "#" + Integer.toHexString(c.getRGB()).substring(2);
    }
  }
}
