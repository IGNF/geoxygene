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

public class FoursquareCompleteVenue extends FoursquareVenue {
  @SerializedName("reasons")
  public String reasons;
  @SerializedName("flags")
  public String flags;
  @SerializedName("roles")
  public String roles;
  @SerializedName("restricted")
  public String restricted;
  @SerializedName("pageUpdates")
  public String pageUpdates;
  @SerializedName("phrases")
  public String phrases;
  @SerializedName("attributes")
  public String attributes;
  @SerializedName("storeId")
  public String storeId;
  @SerializedName("description")
  public String description;
  @SerializedName("createdAt")
  public Long createdAt;
  @SerializedName("mayor")
  public Mayor mayor;
  @SerializedName("tips")
  public Tips tips;
  @SerializedName("listed")
  public ListGroups listed;
  @SerializedName("tags")
  public String[] tags;
  @SerializedName("beenHere")
  public Count beenHere;
  @SerializedName("shortUrl")
  public String shortUrl;
  @SerializedName("canonicalUrl")
  public String canonicalUrl;
  @SerializedName("specialsNearby")
  public SpecialGroup specialsNearby;
  @SerializedName("photos")
  public Photos photos;
  @SerializedName("likes")
  public UserGroups likes;
  @SerializedName("like")
  public Boolean like;
  @SerializedName("dislike")
  public Boolean dislike;
  @SerializedName("timeZone")
  public String timeZone;

  public class Count {
    @SerializedName("count")
    public Long count;
  }

  public class Tips extends Count {
    @SerializedName("groups")
    public TipGroup[] groups;
  }

  public class TipGroup extends Count {
    @SerializedName("items")
    public CompleteTip[] items;
  }

  public class CompactTip {
    @SerializedName("id")
    public String id;
    @SerializedName("text")
    public String text;
    @SerializedName("createdAt")
    public Long createdAt;
    @SerializedName("status")
    public String status;
    @SerializedName("photo")
    public Photo photo;
    @SerializedName("photourl")
    public String photourl;
    @SerializedName("url")
    public String url;
    @SerializedName("user")
    public FoursquareUser user;
    @SerializedName("venue")
    public FoursquareVenue venue;
  }

  public class Photo {
    @SerializedName("id")
    public String id;
    @SerializedName("createdAt")
    public Long createdAt;
    @SerializedName("url")
    public String url;
    @SerializedName("sizes")
    public SizeGroup sizes;
    @SerializedName("source")
    public Source source;
    @SerializedName("user")
    public FoursquareUser user;
    @SerializedName("venue")
    public FoursquareVenue venue;
    @SerializedName("tip")
    public CompleteTip tip;
    @SerializedName("checkin")
    public FoursquareCheckin checkin;
    @SerializedName("height")
    public Integer height;
    @SerializedName("width")
    public Integer width;
    @SerializedName("visibility")
    public String visibility;
    @SerializedName("prefix")
    public String prefix;
    @SerializedName("suffix")
    public String suffix;

  }

  public class Photos extends Count {
    @SerializedName("groups")
    public PhotoGroup[] groups;
  }

  public class PhotoGroup extends Count {
    @SerializedName("items")
    public Photo[] items;
  }

  public class Mayor extends Count {
    @SerializedName("user")
    public FoursquareUser user;
  }

  public class CompleteTip extends CompactTip {
    @SerializedName("todo")
    public UserGroups todo;
    @SerializedName("done")
    public UserGroups done;
    @SerializedName("canonicalUrl")
    public String canonicalUrl;
    @SerializedName("likes")
    public String likes;
    @SerializedName("like")
    public Boolean like;
    @SerializedName("logView")
    public Boolean logView;
  }

  public class Source {
    @SerializedName("name")
    public String name;
    @SerializedName("url")
    public String url;
  }

  public class UserGroups extends Count {
    @SerializedName("summary")
    public String summary;
    @SerializedName("groups")
    public UserGroup[] groups;
  }

  public class UserGroup extends Count {
    @SerializedName("items")
    public FoursquareUser items;
  }

  public class SizeGroup extends Count {
    @SerializedName("items")
    public Size[] items;
  }

  public class Size {
    @SerializedName("url")
    public String url;
    @SerializedName("width")
    public Integer width;
    @SerializedName("height")
    public Integer height;
  }

  public class ListGroups extends Count {
    @SerializedName("groups")
    public ListGroup[] groups;
  }

  public class ListGroup extends Count {
    @SerializedName("items")
    public FoursquareList[] items;
  }

  public class FoursquareList {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("type")
    public String type;
    @SerializedName("user")
    public String user;
    @SerializedName("following")
    public String following;
    @SerializedName("followers")
    public String followers;
    @SerializedName("editable")
    public String editable;
    @SerializedName("collaborative")
    public String collaborative;
    @SerializedName("collaborators")
    public String collaborators;
    @SerializedName("url")
    public String url;
    @SerializedName("canonicalUrl")
    public String canonicalUrl;
    @SerializedName("photo")
    public String photo;
    @SerializedName("venueCount")
    public String venueCount;
    @SerializedName("visitedCount")
    public String visitedCount;
    @SerializedName("listItems")
    public String listItems;
    @SerializedName("createdAt")
    public String createdAt;
    @SerializedName("updatedAt")
    public String updatedAt;
    @SerializedName("entities")
    public String entities;
    @SerializedName("isPublic")
    public Boolean isPublic;
    @SerializedName("logView")
    public Boolean logView;
  }

}
