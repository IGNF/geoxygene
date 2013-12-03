package fr.ign.cogit.geoxygene.style.texture;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorJaxbAdaptor;

@XmlAccessorType(XmlAccessType.FIELD)
public class PerlinNoiseTexture extends Texture {

  public PerlinNoiseTexture() {
    super();
  }

  public PerlinNoiseTexture(float scale, float amount, float angle,
      float stretch, Color color1, Color color2) {
    super();
    this.scale = scale;
    this.amount = amount;
    this.angle = angle;
    this.stretch = stretch;
    this.color1 = color1;
    this.color2 = color2;
  }

  @XmlElement(name = "Scale")
  private float scale = 10f;

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  @XmlElement(name = "Amount")
  private float amount = 0.5f;

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  @XmlElement(name = "Angle")
  private float angle = (float) Math.PI;

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  @XmlElement(name = "Stretch")
  private float stretch = 1f;

  public float getStretch() {
    return stretch;
  }

  public void setStretch(float stretch) {
    this.stretch = stretch;
  }

  @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
  @XmlElement(name = "Color1")
  private Color color1 = Color.YELLOW;

  public Color getColor1() {
    return color1;
  }

  public void setColor1(Color color1) {
    this.color1 = color1;
  }

  @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
  @XmlElement(name = "Color2")
  private Color color2 = Color.RED;

  public Color getColor2() {
    return color2;
  }

  public void setColor2(Color color2) {
    this.color2 = color2;
  }

}
