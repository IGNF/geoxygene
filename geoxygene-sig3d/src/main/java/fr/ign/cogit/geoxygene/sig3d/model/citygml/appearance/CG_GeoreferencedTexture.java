package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.GeoreferencedTexture;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_GeoreferencedTexture extends CG_AbstractTexture {

  protected Boolean preferWorldFile;
  protected GM_Point referencePoint;
  protected double[][] orientation = new double[2][2];
  protected List<String> target;

  public CG_GeoreferencedTexture(GeoreferencedTexture sD) {

    super(sD);

    if (sD.isSetPreferWorldFile()) {
      this.setPreferWorldFile(sD.getPreferWorldFile());
    }

    if (sD.isSetReferencePoint()) {

      this.referencePoint = ConvertyCityGMLGeometry.convertGMLPoint(sD
          .getReferencePoint().getPoint());
    }

    if (sD.isSetOrientation()) {
      this.orientation[0][0] = sD.getOrientation().getMatrix().get(0, 0);
      this.orientation[1][0] = sD.getOrientation().getMatrix().get(1, 0);
      this.orientation[0][1] = sD.getOrientation().getMatrix().get(0, 1);
      this.orientation[1][1] = sD.getOrientation().getMatrix().get(1, 1);

    }

    if (sD.isSetTarget()) {
      this.getTarget().addAll(sD.getTarget());
    }
  }

  /**
   * Gets the value of the preferWorldFile property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isPreferWorldFile() {
    return this.preferWorldFile;
  }

  /**
   * Sets the value of the preferWorldFile property.
   * 
   * @param value allowed object is {@link Boolean }
   * 
   */
  public void setPreferWorldFile(Boolean value) {
    this.preferWorldFile = value;
  }

  public boolean isSetPreferWorldFile() {
    return (this.preferWorldFile != null);
  }

  /**
   * Gets the value of the referencePoint property.
   * 
   * @return possible object is {@link GM_Point }
   * 
   */
  public GM_Point getReferencePoint() {
    return this.referencePoint;
  }

  /**
   * Sets the value of the referencePoint property.
   * 
   * @param value allowed object is {@link GM_Point }
   * 
   */
  public void setReferencePoint(GM_Point value) {
    this.referencePoint = value;
  }

  public boolean isSetReferencePoint() {
    return (this.referencePoint != null);
  }

  /**
   * Gets the value of the orientation property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the orientation property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getOrientation().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link Double }
   * 
   * 
   */
  public double[][] getOrientation() {
    if (this.orientation == null) {
      this.orientation = new double[2][2];
    }
    return this.orientation;
  }

  public boolean isSetOrientation() {
    return (this.orientation != null);
  }

  public void unsetOrientation() {
    this.orientation = null;
  }

  /**
   * Gets the value of the target property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the target property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getTarget().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   * 
   * 
   */
  public List<String> getTarget() {
    if (this.target == null) {
      this.target = new ArrayList<String>();
    }
    return this.target;
  }

  public boolean isSetTarget() {
    return ((this.target != null) && (!this.target.isEmpty()));
  }

  public void unsetTarget() {
    this.target = null;
  }

}
