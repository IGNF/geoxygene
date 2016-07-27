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

import fr.ign.cogit.geoxygene.vgi.foursquare.FoursquareCompleteVenue.PhotoGroup;
import fr.ign.cogit.geoxygene.vgi.foursquare.FoursquareCompleteVenue.Source;
import fr.ign.cogit.geoxygene.vgi.foursquare.FoursquareVenue.CheckinGroup;

public class FoursquareCheckin {
  @SerializedName("id")
  public String id;
  @SerializedName("type")
  public String type;
  @SerializedName("isPrivate")
  public Boolean isPrivate;
  @SerializedName("user")
  public FoursquareUser user;
  @SerializedName("isMayor")
  public Boolean isMayor;
  @SerializedName("timeZone")
  public String timeZone;
  @SerializedName("venue")
  public FoursquareCompleteVenue venue;
  @SerializedName("location")
  public FoursquareLocation location;
  @SerializedName("shout")
  public String shout;
  @SerializedName("createdAt")
  public Long createdAt;
  @SerializedName("source")
  public Source source;
  @SerializedName("photos")
  public PhotoGroup photos;
  @SerializedName("comments")
  public CommentGroup comments;
  @SerializedName("overlaps")
  public CheckinGroup overlaps;

  public class CommentGroup {
    @SerializedName("count")
    public Long count;
    @SerializedName("items")
    public Comment[] items;
  }

  public class Comment {
    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public Long createdAt;
    @SerializedName("user")
    public FoursquareUser user;
    @SerializedName("text")
    public String text;
  }
}
