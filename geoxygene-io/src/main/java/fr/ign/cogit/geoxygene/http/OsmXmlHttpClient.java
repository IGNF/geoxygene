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
package fr.ign.cogit.geoxygene.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Get XML OSM data from Overpass API.
 * The Overpass API (or OSM3S) is a read-only API that serves up custom selected parts of the OSM map data.
 * 
 * @author MDVan-Damme
 *
 */
public class OsmXmlHttpClient {
  
  /** OverPass API URL. */
  private static final String URL_OVERPASS_API = "http://overpass-api.de/api/interpreter";
  
  /** Proxy IGN. */
  private final static String PROXY_IGN_HOST = "proxy.ign.fr";
  private final static int PROXY_IGN_PORT = 3128;
  
  /**
   * @see http://wiki.openstreetmap.org/wiki/Overpass_API/Language_Guide
   * 
   * Example : way(50.746,7.154,50.748,7.157);out body;
   * 
   * @param data : 
   *    param request in UTF-8 to send.
   * @param proxyIGN
   *    true if IGN proxy have to be set.
   * @return 
   */
  public static String getOsmXML(String data, boolean proxyIGN) {

    StringBuffer response = new StringBuffer();
    try {
      
      URL url = new URL(URL_OVERPASS_API + "?data=" + URLEncoder.encode(data, "UTF-8"));
      URLConnection urlConn; 
      
      // Connection
      if (proxyIGN) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_IGN_HOST, PROXY_IGN_PORT));
        urlConn = (URLConnection) url.openConnection(proxy);
      } else {
        urlConn = url.openConnection();
      }

      // Get connection inputstream
      InputStream is = urlConn.getInputStream(); 
      BufferedReader s = new BufferedReader(new InputStreamReader(is));
      String line = s.readLine();
      while (line != null) {
        response.append(line);
        line = s.readLine();
      }
      s.close();
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return response.toString();

  }
  
}
