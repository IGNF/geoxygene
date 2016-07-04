/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.foursquare;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;

public class FoursquareFeature extends AbstractFeature {

  private FoursquareVenue venue;

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return null;
  }

  public FoursquareFeature(FoursquareVenue venue) {
    super();
    this.setVenue(venue);
    this.setGeom(GeometryEngine.getFactory().createPoint(
        new DirectPosition(venue.location.lng, venue.location.lat)));
  }

  public FoursquareVenue getVenue() {
    return venue;
  }

  public void setVenue(FoursquareVenue venue) {
    this.venue = venue;
  }

}
