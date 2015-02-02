package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.Appearance;

import fr.ign.cogit.geoxygene.feature.FT_Feature;

public class CG_Appearance extends FT_Feature {

  protected String theme;

  public CG_Appearance(Appearance appearance) {
    super();

    if (appearance.isSetTheme()) {
      this.theme = appearance.getTheme();
    }

    if (appearance.isSetSurfaceDataMember()) {
      int nbElem = appearance.getSurfaceDataMember().size();

      for (int i = 0; i < nbElem; i++) {
        this.getSurfaceDataMember().add(
            CG_AbstractSurfaceData.generateAbstractSurfaceData(appearance
                .getSurfaceDataMember().get(i).getSurfaceData()));
      }
    }

  }

  /**
   * Gets the value of the theme property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getTheme() {
    return this.theme;
  }

  /**
   * Sets the value of the theme property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setTheme(String value) {
    this.theme = value;
  }

  public boolean isSetTheme() {
    return (this.theme != null);
  }

  List<CG_AbstractSurfaceData> surfaceDataMember;

  public List<CG_AbstractSurfaceData> getSurfaceDataMember() {
    if (this.surfaceDataMember == null) {
      this.surfaceDataMember = new ArrayList<CG_AbstractSurfaceData>();
    }
    return this.surfaceDataMember;
  }

  public boolean isSetSurfaceDataMember() {
    return ((this.surfaceDataMember != null) && (!this.surfaceDataMember
        .isEmpty()));
  }

  public void unsetSurfaceDataMember() {
    this.surfaceDataMember = null;
  }

}
