package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.AppearanceProperty;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_AppearanceProperty extends FT_Feature {

  public CG_AppearanceProperty(AppearanceProperty app) {

    if (app.isSetAppearance()) {
      this.appearance = new CG_Appearance(app.getAppearance());
    }

  }

  protected CG_Appearance appearance;

  /**
   * Gets the value of the appearance property.
   * 
   * @return possible object is {@link CG_Appearance }
   * 
   */
  public CG_Appearance getAppearance() {
    return this.appearance;
  }

  /**
   * Sets the value of the appearance property.
   * 
   * @param value allowed object is {@link CG_Appearance }
   * 
   */
  public void setAppearance(CG_Appearance value) {
    this.appearance = value;
  }

  public boolean isSetAppearance() {
    return (this.appearance != null);
  }

}
