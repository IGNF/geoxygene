package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.WrapMode;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_WrapMode {

  public final static String NONE = "none";
  public final static String WRAP = "wrap";
  public final static String MIRROR = "mirror";
  public final static String CLAMP = "clamp";
  public final static String BORDER = "border";

  private String value;

  public CG_WrapMode(WrapMode w) {
    this.value = CG_WrapMode.fromValue(w.getValue());
  }

  public String value() {
    return this.value;
  }

  public static String fromValue(String v) {

    if (v.equalsIgnoreCase(CG_WrapMode.NONE)) {
      return CG_WrapMode.NONE;
    }

    if (v.equalsIgnoreCase(CG_WrapMode.WRAP)) {
      return CG_WrapMode.WRAP;
    }

    if (v.equalsIgnoreCase(CG_WrapMode.MIRROR)) {
      return CG_WrapMode.MIRROR;
    }

    if (v.equalsIgnoreCase(CG_WrapMode.CLAMP)) {
      return CG_WrapMode.CLAMP;
    }

    if (v.equalsIgnoreCase(CG_WrapMode.BORDER)) {
      return CG_WrapMode.BORDER;
    }

    System.out.println("VALEUR INCONNUE : " + v);

    return "";
  }

}
