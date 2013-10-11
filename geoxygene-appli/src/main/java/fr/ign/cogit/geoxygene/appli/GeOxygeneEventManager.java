/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.appli.GeOxygeneEventType.GeOxygeneEventKey;

/**
 * @author JeT
 * This class is a kind of patch to open a discussion channel between plugins, the GUI and the main application.
 * It is a singleton class, so it breaks the possibility to have multiple GeOxygene application in the same JVM
 * This class and concept has been introduced due to groovy script plugins which generates multiple GUI windows
 * managing each Groovy Renderer engine with the need to update the script which can be live edited...
 * Moreover, groovy editors are known/generated only when they are used by the rendering engine. It is not
 * possible to know what will be used before launching a rendering on the user chosen shape file. Plugin
 * initialize() method is really not enough for these needs.
 * 
 * This architecture has been chosen in order to modify as less as possible the existing code and architecture
 * It is very rough so, do not use this intensively for real time actions, use it only for non time consuming 
 * specific isolated actions
 */
public class GeOxygeneEventManager {

  private GeOxygeneApplication registeredApplication = null;
  private Set<GeOxygeneInterlocutor> interlocutors = new HashSet<GeOxygeneInterlocutor>();
  private static final GeOxygeneEventManager instance = new GeOxygeneEventManager();

  /**
   * Private Constructor
   */
  private GeOxygeneEventManager() {
    // private constructor
  }

  /**
   * @return the singleton instance
   */
  public static GeOxygeneEventManager getInstance() {
    return instance;
  }

  /**
   * @return the registeredApplication
   */
  public GeOxygeneApplication getApplication() {
    return this.registeredApplication;
  }

  /**
   * @param registeredApplication the registeredApplication to set
   */
  public void setApplication(final GeOxygeneApplication registeredApplication) {
    this.registeredApplication = registeredApplication;
  }

  /**
   * @return the interlocutors
   */
  private Set<GeOxygeneInterlocutor> getInterlocutors() {
    return this.interlocutors;
  }

  /**
   * Add an interlocutor
   * @param interlocutor the interlocutor to set
   * @return 
   */
  public boolean addInterlocutor(final GeOxygeneInterlocutor interlocutor) {
    return this.interlocutors.add(interlocutor);
  }

  /**
   * Remove an interlocutor
   * @param interlocutor the interlocutor to remove
   * @return 
   */
  public boolean removeInterlocutor(final GeOxygeneInterlocutor interlocutor) {
    return this.interlocutors.remove(interlocutor);
  }

  public void fireEvent(final GeOxygeneEventType type, final Map<GeOxygeneEventKey, Object> params) {
    for (GeOxygeneInterlocutor interlocutor : this.getInterlocutors()) {
      interlocutor.onGeOxygeneEvent(type, params);
    }
  }

  public void fireEvent(final GeOxygeneEventType type) {
    this.fireEvent(type, new HashMap<GeOxygeneEventKey, Object>());
  }

  public void fireEvent(final GeOxygeneEventType type, final GeOxygeneEventKey key, final Object value) {
    Map<GeOxygeneEventKey, Object> params = new HashMap<GeOxygeneEventKey, Object>();
    params.put(key, value);
    this.fireEvent(type, params);
  }

  public void fireEvent(final GeOxygeneEventType type, final GeOxygeneEventKey key1, final Object value1, final GeOxygeneEventKey key2,
      final Object value2) {
    Map<GeOxygeneEventKey, Object> params = new HashMap<GeOxygeneEventKey, Object>();
    params.put(key1, value1);
    params.put(key2, value2);
    this.fireEvent(type, params);
  }

  public static void fire(final GeOxygeneEventType type, final Map<GeOxygeneEventKey, Object> params) {
    getInstance().fireEvent(type, params);
  }

  public static void fire(final GeOxygeneEventType type) {
    getInstance().fireEvent(type, new HashMap<GeOxygeneEventKey, Object>());
  }

  public static void fire(final GeOxygeneEventType type, final GeOxygeneEventKey key, final Object value) {
    getInstance().fireEvent(type, key, value);
  }

  public static void fire(final GeOxygeneEventType type, final GeOxygeneEventKey key1, final Object value1, final GeOxygeneEventKey key2,
      final Object value2) {
    getInstance().fireEvent(type, key1, value1, key2, value2);
  }

}
