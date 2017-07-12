package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import javax.vecmath.Color3f;

import org.citygml4j.model.citygml.appearance.AbstractTexture;
import org.citygml4j.model.citygml.appearance.GeoreferencedTexture;
import org.citygml4j.model.citygml.appearance.ParameterizedTexture;
import org.citygml4j.model.gml.basicTypes.Code;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_AbstractTexture extends CG_AbstractSurfaceData {

  protected String imageURI;
  protected Code mimeType;

  /*
   * +typical +specific +unknown
   */
  protected CG_TextureType textureType;

  /*
   * +none +wrap +mirror +clamp +border
   */
  protected CG_WrapMode wrapMode;

  protected Color3f borderColor;

  public CG_AbstractTexture(AbstractTexture aT) {
    super(aT);

    if (aT.isSetImageURI()) {
      this.imageURI = aT.getImageURI();
    }

    if (aT.isSetMimeType()) {
      this.mimeType = aT.getMimeType();
    }

    if (aT.isSetBorderColor()) {

      this.borderColor = new Color3f(aT.getBorderColor().getRed().floatValue(),
          aT.getBorderColor().getGreen().floatValue(), aT.getBorderColor()
              .getBlue().floatValue());

    }

    if (aT.isSetTextureType()) {
      this.textureType = new CG_TextureType(aT.getTextureType());
    }

    if (aT.isSetWrapMode()) {
      this.wrapMode = new CG_WrapMode(aT.getWrapMode());
    }

  }

  public static CG_AbstractSurfaceData generateAbstractTexture(
      AbstractTexture sD) {

    if (sD instanceof GeoreferencedTexture) {
      return new CG_GeoreferencedTexture((GeoreferencedTexture) sD);
    } else if (sD instanceof ParameterizedTexture) {
      return new CG_ParameterizedTexture((ParameterizedTexture) sD);
    }

    System.out
        .println("Classe non reconnue" + sD.getClass().getCanonicalName());

    // TODO GeoreferencedTextureType.class,
    // ParameterizedTextureType.class
    return null;
  }

  /**
   * Gets the value of the imageURI property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getImageURI() {
    return this.imageURI;
  }

  /**
   * Sets the value of the imageURI property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setImageURI(String value) {
    this.imageURI = value;
  }

  public boolean isSetImageURI() {
    return (this.imageURI != null);
  }

  /**
   * Gets the value of the mimeType property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public Code getMimeType() {
    return this.mimeType;
  }

  /**
   * Sets the value of the mimeType property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setMimeType(Code value) {
    this.mimeType = value;
  }

  public boolean isSetMimeType() {
    return (this.mimeType != null);
  }

  /**
   * Gets the value of the textureType property.
   * 
   * @return possible object is {@link CG_TextureType }
   * 
   */
  public CG_TextureType getTextureType() {
    return this.textureType;
  }

  /**
   * Sets the value of the textureType property.
   * 
   * @param value allowed object is {@link CG_TextureType }
   * 
   */
  public void setTextureType(CG_TextureType value) {
    this.textureType = value;
  }

  public boolean isSetTextureType() {
    return (this.textureType != null);
  }

  /**
   * Gets the value of the wrapMode property.
   * 
   * @return possible object is {@link CG_WrapMode }
   * 
   */
  public CG_WrapMode getWrapMode() {
    return this.wrapMode;
  }

  /**
   * Sets the value of the wrapMode property.
   * 
   * @param value allowed object is {@link CG_WrapMode }
   * 
   */
  public void setWrapMode(CG_WrapMode value) {
    this.wrapMode = value;
  }

  public boolean isSetWrapMode() {
    return (this.wrapMode != null);
  }

  public Color3f getBorderColor() {
    if (this.borderColor == null) {
      this.borderColor = new Color3f();
    }
    return this.borderColor;
  }

  public boolean isSetBorderColor() {
    return (this.borderColor != null);
  }

  public void unsetBorderColor() {
    this.borderColor = null;
  }

}
