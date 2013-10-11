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

package fr.ign.cogit.geoxygene.appli.plugin.script;

import java.awt.Window;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.scripting.ConsoleEvent;
import fr.ign.cogit.geoxygene.scripting.ConsoleEventType;
import fr.ign.cogit.geoxygene.scripting.ConsoleListener;
import fr.ign.cogit.geoxygene.scripting.GroovyConsoleTab;
import fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI;
import fr.ign.cogit.geoxygene.scripting.TextTransformer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author JeT
 *
 */
public class GroovyScriptingConsole implements ScriptingConsole, ConsoleListener {

  private GroovyConsoleUI groovyConsole = null;
  private String name = null;

  /**
   * Constructor
   */
  public GroovyScriptingConsole(final Window parent, final String name) {
    this.groovyConsole = new GroovyConsoleUI(parent, name);
    this.groovyConsole.getMainFrame().setSize(parent.getWidth() / 2, parent.getHeight());
    this.groovyConsole.getMainFrame().setLocation(parent.getLocation());
    this.groovyConsole.addConsoleListener(this);
    this.name = name;
  }

  /**
   * @return the groovyConsole
   */
  public GroovyConsoleUI getGroovyConsole() {
    return this.groovyConsole;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.plugin.script.ScriptingConsole#getName()
   */
  @Override
  public String getName() {
    return this.name;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.plugin.script.ScriptingConsole#setVisible(boolean)
   */
  @Override
  public void setVisible(final boolean b) {
    if (b) {
      this.getGroovyConsole().getMainFrame().setVisible(true);
    } else {
      this.getGroovyConsole().getMainFrame().dispose();
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.plugin.script.ScriptingConsole#isVisible()
   */
  @Override
  public boolean isVisible() {
    return this.getGroovyConsole().getMainFrame().isVisible();
  }

  /**
   * @param title
   * @param groovyShell
   * @return
   * @see fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI#addConsoleTab(java.lang.String, groovy.lang.GroovyShell)
   */
  public String addGroovyScript(final String id, final File scriptFile, final TextTransformer groovyPreprocess, final GroovyShell groovyShell) {
    String title = scriptFile.getName();
    GroovyConsoleTab consoleTab = this.groovyConsole.addConsoleTab(id, title, scriptFile, groovyPreprocess, groovyShell);
    // change content
    //    this.groovyConsole.setScriptText(id, content);
    consoleTab.addHiddenExceptions(RuntimeException.class);
    return id;
  }

  /**
   * Set a binding for the script with given id 
   * @param id script id
   * @param binding script binding to set
   */
  public void setBinding(final String id, final Binding binding) {
    this.groovyConsole.setBinding(id, binding);
  }

  @Override
  public String getCurrentId() {
    return this.groovyConsole.getCurrentId();
  }

  /**
   * @return
   * @see fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI#getScriptText()
   */
  @Override
  public String getScriptText() {
    return this.groovyConsole.getScriptText();
  }

  /**
   * @return
   * @see fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI#getScriptText()
   */
  @Override
  public String getScriptText(final String id) {
    return this.groovyConsole.getScriptText(id);
  }

  //  /**
  //   * @return
  //   * @see fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI#getScriptText()
  //   */
  //  @Override
  //  public void setScriptText(final String script) {
  //    this.groovyConsole.setScriptText(script);
  //  }
  //
  //  /**
  //   * @return
  //   * @see fr.ign.cogit.geoxygene.scripting.GroovyConsoleUI#getScriptText()
  //   */
  //  @Override
  //  public void setScriptText(final String id, final String script) {
  //    this.groovyConsole.setScriptText(id, script);
  //  }

  /********************************************************************
   *  Event Management
   */
  private Set<ConsoleListener> listeners = new HashSet<ConsoleListener>();

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#addConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  @Override
  public void addConsoleListener(final ConsoleListener listener) {
    synchronized (this.listeners) {
      this.listeners.add(listener);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#removeConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  @Override
  public void removeConsoleListener(final ConsoleListener listener) {
    synchronized (this.listeners) {
      this.listeners.remove(listener);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.scripting.ScriptingConsole#removeConsoleListener(fr.ign.cogit.geoxygene.scripting.ConsoleListener)
   */
  @Override
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

  /**
   * action received from Groovy console. Take care that there is an easy confusion
   * between the groovy console which is an existing component and this GroovyScriptingConsole
   * which is a wrapper for GeoXygene architecture. Events are just forwarded from 
   * GroovyConsole to GroovyScriptingConsole listeners
   */
  @Override
  public void onScriptEvent(final ConsoleEvent event) {
    this.fireEvent(event);
  }

}
