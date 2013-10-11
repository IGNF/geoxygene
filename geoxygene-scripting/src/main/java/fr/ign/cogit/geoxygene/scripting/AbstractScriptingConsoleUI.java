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

package fr.ign.cogit.geoxygene.scripting;

import java.util.HashSet;
import java.util.Set;

/**
 * @author JeT
 * basic implementation of common functions
 */
public abstract class AbstractScriptingConsoleUI implements ScriptingConsoleUI {

  private String id = "no ID";
  private String title = "no title";
  private Set<ConsoleListener> listeners = new HashSet<ConsoleListener>();

  /**
   * Constructor
   * @param id
   */
  public AbstractScriptingConsoleUI(final String id, final String title) {
    super();
    this.id = id;
    this.setTitle(title);
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#getId()
   */
  public String getId() {
    return this.id;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#getTitle()
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param title the title to set
   */
  public final void setTitle(final String title) {
    this.title = title;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#addConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  public void addConsoleListener(final ConsoleListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#removeConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  public void removeConsoleListener(final ConsoleListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#removeConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  public void clearConsoleListeners() {
    synchronized (this.listeners) {
      this.listeners.clear();
    }
  }

  public void fireEvent(final ConsoleEvent event) {
    synchronized (this.listeners) {
      for (ConsoleListener listener : this.listeners) {
        listener.onScriptEvent(event);
      }
    }

  }

  public void fireEvent(final Object source, final ConsoleEventType type) {
    ConsoleEvent event = new ConsoleEvent(source, type);
    this.fireEvent(event);
  }

}
