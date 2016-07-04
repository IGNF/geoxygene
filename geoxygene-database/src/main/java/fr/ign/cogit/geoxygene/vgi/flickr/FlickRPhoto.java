/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.flickr;

public class FlickRPhoto {

  private String flickrId, owner, secret, server, farm, title, ispublic,
      isfriend, isfamily;

  public FlickRPhoto(String flickrId, String owner, String secret,
      String server, String farm, String title, String ispublic,
      String isfriend, String isfamily) {
    super();
    this.flickrId = flickrId;
    this.owner = owner;
    this.secret = secret;
    this.server = server;
    this.farm = farm;
    this.title = title;
    this.ispublic = ispublic;
    this.isfriend = isfriend;
    this.isfamily = isfamily;
  }

  public String getFlickRId() {
    return flickrId;
  }

  public void setId(String id) {
    this.flickrId = id;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getFarm() {
    return farm;
  }

  public void setFarm(String farm) {
    this.farm = farm;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getIspublic() {
    return ispublic;
  }

  public void setIspublic(String ispublic) {
    this.ispublic = ispublic;
  }

  public String getIsfriend() {
    return isfriend;
  }

  public void setIsfriend(String isfriend) {
    this.isfriend = isfriend;
  }

  public String getIsfamily() {
    return isfamily;
  }

  public void setIsfamily(String isfamily) {
    this.isfamily = isfamily;
  }

}
