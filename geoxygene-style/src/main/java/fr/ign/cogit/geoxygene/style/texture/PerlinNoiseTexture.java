package fr.ign.cogit.geoxygene.style.texture;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class PerlinNoiseTexture extends Texture {

  private float scale = 10f;

  public float getScale() {
    return scale;
  }

  public void setScale(float scale) {
    this.scale = scale;
  }

  private float amount = 0.5f;

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  private float angle = (float) Math.PI;

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  private float stretch = 1f;

  public float getStretch() {
    return stretch;
  }

  public void setStretch(float stretch) {
    this.stretch = stretch;
  }

  private Color color1 = Color.YELLOW;

  public Color getColor1() {
    return color1;
  }

  public void setColor1(Color color1) {
    this.color1 = color1;
  }

  private Color color2 = Color.RED;

  public Color getColor2() {
    return color2;
  }

  public void setColor2(Color color2) {
    this.color2 = color2;
  }

}
