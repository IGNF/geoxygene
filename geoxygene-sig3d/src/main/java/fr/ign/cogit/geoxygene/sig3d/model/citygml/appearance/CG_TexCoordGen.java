package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.TexCoordGen;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TexCoordGen extends CG_AbstractTextureParameterization {

  protected CG_WorldToTexture worldToTexture;

  public CG_TexCoordGen(TexCoordGen tPT) {
    if (tPT.isSetWorldToTexture()) {
      this.worldToTexture = new CG_WorldToTexture(tPT.getWorldToTexture());
    }
  }

  /**
   * Gets the value of the worldToTexture property.
   * 
   * @return possible object is {@link CG_TexCoordGen.WorldToTexture }
   * 
   */
  public CG_WorldToTexture getWorldToTexture() {
    return this.worldToTexture;
  }

  /**
   * Sets the value of the worldToTexture property.
   * 
   * @param value allowed object is {@link CG_TexCoordGen.WorldToTexture }
   * 
   */
  public void setWorldToTexture(CG_WorldToTexture value) {
    this.worldToTexture = value;
  }

  public boolean isSetWorldToTexture() {
    return (this.worldToTexture != null);
  }

}
