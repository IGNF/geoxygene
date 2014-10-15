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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Simple HTTP Client class.
 */
public class SimpleHttpClient {
  
  /**
   * The URL we want to connect
   */
  protected URL url;

  /**
   * The HTTP connection
   */
  protected HttpURLConnection server;
  
  /**
   * Constructor.
   * <p>
   * 
   * @param szUrl
   *            : The URL we want to connect.
   */
  public SimpleHttpClient(String szUrl) throws Exception {
      try {
          url = new URL(szUrl);
      } catch (Exception e) {
          throw new Exception("Invalid URL");
      }
  }
  
  /**
   * Connect the client to it's URL by using the specified method.
   * 
   * @param method
   *            : String object for client method (POST, GET,...)
   */
  public void connect(String method) throws Exception {
      try {
          server = (HttpURLConnection) url.openConnection();
          server.setDoInput(true);
          server.setDoOutput(true);
          server.setRequestMethod(method);
          // server.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
          server.setRequestProperty("Content-type", "text/xml");
          server.connect();
      } catch (Exception e) {
          throw new Exception("Connection failed");
      }
  }
  
  public void connectProxyIGN(String method) throws Exception {
    try {
      
        // Set IGN Proxy
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.ign.fr", 3128));
        
        server = (HttpURLConnection) url.openConnection(proxy);
        server.setDoInput(true);
        server.setDoOutput(true);
        server.setRequestMethod(method);
        // server.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        server.setRequestProperty("Content-type", "text/xml");
        server.connect();
    } catch (Exception e) {
        throw new Exception("Connection failed");
    }
  }
  
  

  
  /**
   * Disconnect the client.
   */
  public void disconnect() {
      server.disconnect();
  }
  
  /**
   * Read the response from the server.
   * 
   * @return The response.
   * @throws Exception
   */
  public String getResponse() throws Exception {
      StringBuffer response = new StringBuffer();

      try {
          BufferedReader s = new BufferedReader(new InputStreamReader(server.getInputStream()));
          String line = s.readLine();
          while (line != null) {
              response.append(line);
              line = s.readLine();
          }
          s.close();
      } catch (Exception e) {
          throw new Exception("Unable to read input stream");
      }
      return response.toString();
  }
  
  public void getResponseInFile(String filePath) throws Exception {
      try {
          
    	  InputStream instream = server.getInputStream();
    	  BufferedInputStream bis = new BufferedInputStream(instream);
    	  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
    	  int inByte;
    	  while ((inByte = bis.read()) != -1 ) {
    		  bos.write(inByte);
    	  }
    	  bis.close();
    	  bos.close();
    	  
      } catch (Exception e) {
          throw new Exception("Unable to read input stream");
      }
  }
  
  /**
   * Submit a POST to the server.
   * 
   * @param content
   *            The content of the POST message
   * @throws Exception
   */
  public void post(String content) throws Exception {
      try {
          BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
          bw.write(content, 0, content.length());
          bw.flush();
          bw.close();
      } catch (Exception e) {
          throw new Exception("Unable to write to output stream");
      }
  }

}
