/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.vgi.panoramio;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PanoramioPhoto {

  private long photoId;
  private int ownerId, width, height;
  private String title, url, fileUrl, ownerName, ownerUrl;
  private Date uploadDate;

  public PanoramioPhoto(long photoId, int ownerId, int width, int height,
      String title, String url, String fileUrl, String ownerName,
      String ownerUrl, String uploadDate) throws ParseException {
    super();
    this.photoId = photoId;
    this.ownerId = ownerId;
    this.width = width;
    this.height = height;
    this.title = title;
    this.url = url;
    this.fileUrl = fileUrl;
    this.ownerName = ownerName;
    this.ownerUrl = ownerUrl;
    this.uploadDate = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        .parse(uploadDate);
  }

  public long getPhotoId() {
    return photoId;
  }

  public void setPhotoId(long photoId) {
    this.photoId = photoId;
  }

  public int getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(int ownerId) {
    this.ownerId = ownerId;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFileUrl() {
    return fileUrl;
  }

  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getOwnerUrl() {
    return ownerUrl;
  }

  public void setOwnerUrl(String ownerUrl) {
    this.ownerUrl = ownerUrl;
  }

  public Date getUploadDate() {
    return uploadDate;
  }

  public void setUploadDate(Date uploadDate) {
    this.uploadDate = uploadDate;
  }

}
