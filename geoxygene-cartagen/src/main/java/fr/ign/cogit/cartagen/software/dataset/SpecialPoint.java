/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

/**
 * @author KJaara for storing and visualising the punctual thematic objects or
 *         the characteristic objects
 */
import java.awt.Color;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * to put any type of special points like accidents, intersectionriverRoad,
 * roundabout centers..
 */
public class SpecialPoint extends DefaultFeature {

  SpecialPointType pointType;
  // a boolean to distinge the initial from the final special point
  boolean isInitial = true;

  // use one of the used color to make a difference between initial and final
  // symbolpoint
  Color symbolColor;

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

  public static final String FEAT_TYPE_NAME = "SpecialPoint";

  public SpecialPoint(IPoint point, SpecialPointType pointType,
      Color symbolColor) {
    this.setGeom(point);
    this.pointType = pointType;
    this.symbolColor = symbolColor;
  }

  public void setColors(Color colorInitial) {
    this.symbolColor = colorInitial;

  }

  public SpecialPointType getPointType() {
    return this.pointType;
  }

  public boolean isInitial() {
    return this.isInitial;
  }

  public Color getSymbolColor() {
    return this.symbolColor;
  }

}
