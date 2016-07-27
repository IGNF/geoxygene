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

import com.flickr4java.flickr.stats.Stats;
import com.google.gson.annotations.SerializedName;

public class FoursquareVenue {
  @SerializedName("id")
  public String id;
  @SerializedName("name")
  public String name;
  @SerializedName("contact")
  public FoursquareContact contact;
  @SerializedName("location")
  public FoursquareLocation location;
  @SerializedName("categories")
  public Category[] categories;
  @SerializedName("verified")
  public Boolean verified;
  @SerializedName("stats")
  public Stats stats;
  @SerializedName("url")
  public String url;
  @SerializedName("hours")
  public Hours hours;
  @SerializedName("popular")
  public Hours popular;
  @SerializedName("menu")
  public Menu menu;
  @SerializedName("price")
  public Price price;
  @SerializedName("rating")
  public Integer rating;
  @SerializedName("specials")
  public SpecialGroup specials;
  @SerializedName("hereNow")
  public HereNow hereNow;

  public class FoursquareContact {
    @SerializedName("email")
    public String email;
    @SerializedName("facebook")
    public String facebook;
    @SerializedName("twitter")
    public String twitter;
    @SerializedName("phone")
    public String phone;
    @SerializedName("formattedPhone")
    public String formattedPhone;
  }

  public class Hours {
    @SerializedName("status")
    public String status;
    @SerializedName("isOpen")
    public Boolean isOpen;
    @SerializedName("timeframes")
    public Timeframe[] timeframes;
  }

  public class Timeframe {
    @SerializedName("days")
    public String days;
    @SerializedName("includesToday")
    public Boolean includesToday;
    @SerializedName("open")
    public String[] open;
    @SerializedName("segments")
    public String[] segments;
  }

  public class Price {
    @SerializedName("message")
    public String message;
    @SerializedName("tier")
    public Integer tier;
  }

  public class Menu {
    @SerializedName("type")
    public String type;
    @SerializedName("label")
    public String label;
    @SerializedName("anchor")
    public String anchor;
    @SerializedName("url")
    public String url;
    @SerializedName("mobileUrl")
    public String mobileUrl;
  }

  public class HereNow {
    @SerializedName("summary")
    public String summary;
    @SerializedName("count")
    public Integer count;
    @SerializedName("groups")
    public CheckinGroup[] groups;
  }

  public class CheckinGroup {
    @SerializedName("name")
    public String name;
    @SerializedName("type")
    public String type;
    @SerializedName("count")
    public Integer count;
    @SerializedName("items")
    public FoursquareCheckin[] items;
  }

  public class SpecialGroup {
    @SerializedName("items")
    public CompleteSpecial[] items;
  }

  public class CompleteSpecial {
    @SerializedName("id")
    public String id;
    @SerializedName("type")
    public String type;
    @SerializedName("message")
    public String message;
    @SerializedName("finePrint")
    public String finePrint;
    @SerializedName("description")
    public String description;
    @SerializedName("unlocked")
    public Boolean unlocked;
    @SerializedName("icon")
    public String icon;
    @SerializedName("title")
    public String title;
    @SerializedName("state")
    public String state;
    @SerializedName("provider")
    public String provider;
    @SerializedName("redemption")
    public String redemption;
    @SerializedName("venue")
    public FoursquareVenue venue;
  }

  public class Category {
    @SerializedName("id")
    public String id;
    @SerializedName("name")
    public String name;
    @SerializedName("pluralName")
    public String pluralName;
    @SerializedName("icon")
    public Icon icon;
    @SerializedName("parents")
    public String[] parents;
    @SerializedName("primary")
    public Boolean primary;
    @SerializedName("categories")
    public Category[] categories;
  }

  public class Icon {
    @SerializedName("prefix")
    public String prefix;
    @SerializedName("suffix")
    public String suffix;
  }
}
