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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventType;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventType.GeOxygeneEventKey;
import fr.ign.cogit.geoxygene.appli.GeOxygeneInterlocutor;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.function.ui.FunctionGraphPanel;
import fr.ign.cogit.geoxygene.scripting.ConsoleEvent;
import fr.ign.cogit.geoxygene.scripting.ConsoleListener;
import fr.ign.cogit.geoxygene.scripting.TextTransformer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * @author JeT
 * Plugin allowing to manage groovy consoles
 */
public class GroovyConsolePlugin implements GeOxygeneApplicationPlugin, GeOxygeneInterlocutor, ConsoleListener {

  private static Logger logger = Logger.getLogger(GroovyConsolePlugin.class.getName());
  private static GroovyScriptingConsole console = null;
  private static JMenu scriptMenu = null;
  private static JMenuItem groovyConsoleMenuItem = null;
  private static JMenuItem groovyGraphMenuItem = null;
  private static JDialog functionGraphDialog = null;

  /**
   * Static initializer
   */
  private static void staticInitialize(final GeOxygeneApplication application) {
    console = new GroovyScriptingConsole(GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getGui(), "Groovy Console");
    scriptMenu = new JMenu("Script");
    groovyConsoleMenuItem = new JMenuItem("Show Groovy Console");
    scriptMenu.add(groovyConsoleMenuItem);
    groovyConsoleMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        GeOxygeneEventManager.fire(GeOxygeneEventType.SHOW_GROOVY_CONSOLE);
      }
    });
    scriptMenu.addSeparator();
    groovyGraphMenuItem = new JMenuItem("Groovy Functions");
    scriptMenu.add(groovyGraphMenuItem);
    groovyGraphMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        GeOxygeneEventManager.fire(GeOxygeneEventType.SHOW_FUNCTION_GRAPH);
      }
    });

    application.getMainFrame().getMenuBar().add(scriptMenu, application.getMainFrame().getMenuBar().getComponentCount() - 1);
  }

  @Override
  public void initialize(final GeOxygeneApplication application) {
    GroovyConsolePlugin.staticInitialize(application);
    // make the static class listen to event by forwarding event reception from all instances
    GeOxygeneEventManager.getInstance().addInterlocutor(this);
    console.getGroovyConsole().addConsoleListener(this);
  }

  @Override
  public void onGeOxygeneEvent(final GeOxygeneEventType type, final Map<GeOxygeneEventKey, Object> params) {
    switch (type) {
    case SHOW_FUNCTION_GRAPH:
      GroovyConsolePlugin.showGraph();
      break;
    case HIDE_FUNCTION_GRAPH:
      GroovyConsolePlugin.hideGraph();
      break;
    case SHOW_GROOVY_CONSOLE:
      showConsole();
      break;
    case HIDE_GROOVY_CONSOLE:
      hideConsole();
      break;
    case NEW_GROOVY_SCRIPT:
      String id = (String) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_ID);
      File scriptFile = (File) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_FILE);
      TextTransformer groovyPreprocess = (TextTransformer) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_PREPROCESSOR);
      GroovyShell groovyShell = (GroovyShell) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_SHELL);
      this.addScript(id, scriptFile, groovyPreprocess, groovyShell);
    case NEW_GROOVY_BINDING:
      String id1 = (String) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_ID);
      Binding binding = (Binding) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_BINDING);
      this.setBinding(id1, binding);
      break;
    case UPDATE_RENDERING:
      // this update event should be listened by the GUI. but as EventManagement has been added
      // and is not still validated, this plugin is the only one which is listening to events...
      GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
      break;
    default:
      // not interesting events
      break;
    }

  }

  /**
   * set a new binding into a groovy console
   * @param id script id
   * @param binding new script binding to inject
   */
  private void setBinding(final String id, final Binding binding) {
    GroovyConsolePlugin.console.setBinding(id, binding);
  }

  /**
   * add a script to be managed by this console
   * @param id
   * @param name
   * @param content
   * @param groovyShell
   */
  private void addScript(final String id, final File file, final TextTransformer groovyPreprocess, final GroovyShell groovyShell) {
    GroovyConsolePlugin.console.addGroovyScript(id, file, groovyPreprocess, groovyShell);
  }

  private static void showGraph() {
    getFunctionGraphDialog().setVisible(true);
  }

  private static JDialog getFunctionGraphDialog() {
    if (functionGraphDialog == null) {
      JFrame parent = GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getGui();
      functionGraphDialog = new JDialog(parent, "Function Graph");
      functionGraphDialog.getContentPane().add(new FunctionGraphPanel());
      functionGraphDialog.setSize(parent.getWidth() / 2, parent.getHeight() - 10);
      functionGraphDialog.setLocation(parent.getLocation().x + parent.getWidth() / 2, parent.getLocation().y);
    }
    return functionGraphDialog;
  }

  private static void hideGraph() {
    getFunctionGraphDialog().setVisible(false);
  }

  /**
   * Ask to show the groovy console.
   */
  private static void showConsole() {
    if (console.isVisible()) {
      return;
    }
    console.setVisible(true);
    groovyConsoleMenuItem.setText("hide Groovy Console");
    ActionListener[] actionListeners = groovyConsoleMenuItem.getActionListeners();
    if (actionListeners != null && actionListeners.length > 0) {
      groovyConsoleMenuItem.removeActionListener(actionListeners[0]);
    }
    groovyConsoleMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        GeOxygeneEventManager.fire(GeOxygeneEventType.HIDE_GROOVY_CONSOLE);
      }
    });
  }

  /**
   * Ask to hide the groovy console
   * @param console console to add
   */
  private static void hideConsole() {
    if (!console.isVisible()) {
      return;
    }
    console.setVisible(false);
    groovyConsoleMenuItem.setText("show Groovy Console");
    ActionListener[] actionListeners = groovyConsoleMenuItem.getActionListeners();
    if (actionListeners != null && actionListeners.length > 0) {
      groovyConsoleMenuItem.removeActionListener(actionListeners[0]);
    }
    groovyConsoleMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        GeOxygeneEventManager.fire(GeOxygeneEventType.SHOW_GROOVY_CONSOLE);
      }
    });
  }

  @Override
  public void onScriptEvent(final ConsoleEvent event) {
    switch (event.getType()) {
    case CONSOLE_HIDE:
      hideConsole();
      break;
    case CONSOLE_SHOW:
      showConsole();
      break;
    case APPLY:
    case SAVE:
      String currentId = console.getCurrentId();
      if (currentId != null) {
        this.applyScriptChanges(currentId, console.getScriptText(currentId));
      } else {
        logger.error("current Id is null");
      }
      break;
    default:
      // unused events
    }

  }

  private void applyScriptChanges(final String currentId, final String scriptText) {
    HashMap<GeOxygeneEventKey, Object> params = new HashMap<GeOxygeneEventType.GeOxygeneEventKey, Object>();
    params.put(GeOxygeneEventKey.GROOVY_SCRIPT_ID, currentId);
    GeOxygeneEventManager.fire(GeOxygeneEventType.GROOVY_SCRIPT_UPDATED, params);
  }

}
