/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.svg.SVGDocument;

/**
 * @author Julien Perret
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalGraphic {

  /** LOGGER. */
  private final static Logger LOGGER = LogManager
      .getLogger(ExternalGraphic.class.getName());
  private static Proxy proxy;

  static {
    // load the Proxy information if necessary
    boolean required = Boolean
        .valueOf(ResourceBundle.getBundle("proxy").getString("required"));
    if (required) {
      String host = ResourceBundle.getBundle("proxy").getString("host");
      int port = Integer
          .valueOf(ResourceBundle.getBundle("proxy").getString("port"));
      proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }
  }

  @XmlElement(name = "href")
  private String href;

  /**
   * Renvoie la valeur de l'attribut href.
   * 
   * @return la valeur de l'attribut href
   */
  public String getHref() {
    return this.href;
  }

  /**
   * Affecte la valeur de l'attribut href.
   * 
   * @param href l'attribut href à affecter
   */
  public void setHref(String href) {
    this.href = href;
  }

  @XmlElement(name = "Format")
  private String format;

  /**
   * Renvoie la valeur de l'attribut format.
   * 
   * @return la valeur de l'attribut format
   */
  public String getFormat() {
    return this.format;
  }

  /**
   * Affecte la valeur de l'attribut format.
   * 
   * @param format l'attribut format à affecter
   */
  public void setFormat(String format) {
    this.format = format;
  }

  @XmlTransient
  private Image onlineResource = null;

  /**
   * Renvoie la valeur de l'attribut onlineResource.
   * 
   * @return la valeur de l'attribut onlineResource
   */
  public Image getOnlineResource() {
    if (this.onlineResource == null) {
      try {
        URL url = ExternalGraphic.class.getResource(this.href);
        if (url == null) {
          url = new URL(this.href);
        }
        if (url.getProtocol().equals("http")
            || url.getProtocol().equals("https")) {
          // Connection
          URLConnection urlConn;
          if (proxy != null)
            urlConn = (URLConnection) url.openConnection(proxy);
          else
            urlConn = (URLConnection) url.openConnection();

          // Get connection inputstream
          InputStream is = urlConn.getInputStream();
          LOGGER.trace("try to read '" + url + "'");
          this.onlineResource = ImageIO.read(is);
        } else {
          LOGGER.trace("try to read '" + url + "'");
          this.onlineResource = ImageIO.read(url);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return this.onlineResource;
  }

  public GraphicsNode getGraphicsNode() {

    SVGDocument doc = this.getSVGDocument();
    if (doc == null)
      return null;
    UserAgent userAgent = new UserAgentAdapter();
    DocumentLoader loader = new DocumentLoader(userAgent);
    BridgeContext ctx = new BridgeContext(userAgent, loader);
    ctx.setDynamicState(BridgeContext.DYNAMIC);
    GVTBuilder builder = new GVTBuilder();
    GraphicsNode rootGN = builder.build(ctx, doc);
    return rootGN;
  }

  public SVGDocument getSVGDocument() {
    String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
    SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
    SVGDocument doc = null;
    try {
      if (this.href != null) {
        if (!this.href.substring(0, 4).equalsIgnoreCase("file")) {

          URL res = ExternalGraphic.class.getResource(this.href);
          if (res != null) {
            doc = df.createSVGDocument(
                ExternalGraphic.class.getResource(this.href).toString());
          }
        } else {
          doc = df.createSVGDocument(this.href);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return doc;
  }

  /**
   * Affecte la valeur de l'attribut onlineResource.
   * 
   * @param onlineResource l'attribut onlineResource à affecter
   */
  public void setOnlineResource(Image onlineResource) {
    this.onlineResource = onlineResource;
  }
}
