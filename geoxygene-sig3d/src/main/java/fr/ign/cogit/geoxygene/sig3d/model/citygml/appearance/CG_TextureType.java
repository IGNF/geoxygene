package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.TextureType;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TextureType {

  public final static String SPECIFIC = "specific";
  public final static String TYPICAL = "typical";
  public final static String UNKNOWN = "unknown";

  private String value;

  public String value() {
    return this.value;
  }

  public static String fromValue(String v) {

    if (v.equalsIgnoreCase(CG_TextureType.SPECIFIC)) {
      return CG_TextureType.SPECIFIC;
    }

    if (v.equalsIgnoreCase(CG_TextureType.TYPICAL)) {
      return CG_TextureType.TYPICAL;
    }

    if (v.equalsIgnoreCase(CG_TextureType.UNKNOWN)) {
      return CG_TextureType.UNKNOWN;
    }

    System.out.println("Param inconnu :" + v);

    return "";

  }

  public CG_TextureType(TextureType textureType) {
    this.value = CG_TextureType.fromValue(textureType.getValue());
  }

}
