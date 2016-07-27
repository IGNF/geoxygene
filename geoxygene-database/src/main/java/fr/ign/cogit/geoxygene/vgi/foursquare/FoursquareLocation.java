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

import com.google.gson.annotations.SerializedName;

public class FoursquareLocation {

  @SerializedName("address")
  public String address;
  @SerializedName("crossStreet")
  public String crossStreet;
  @SerializedName("city")
  public String city;
  @SerializedName("state")
  public String state;
  @SerializedName("postalCode")
  public String postalCode;
  @SerializedName("country")
  public String country;
  @SerializedName("name")
  public String name;
  @SerializedName("cc")
  public String cc;
  @SerializedName("lat")
  public Double lat;
  @SerializedName("lng")
  public Double lng;
  @SerializedName("distance")
  public Double distance;
}
