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

/**
 * @author JeT
 * Type of events exchanged through GeOxygeneEventManager between application, GUI and GeOxygeneInterlocutors
 */
public enum GeOxygeneEventType {
  SHOW_GROOVY_CONSOLE, // Ask to display the groovy console (no param)
  HIDE_GROOVY_CONSOLE, // Ask to hide the groovy console (no param)

  SHOW_FUNCTION_GRAPH, // Ask to display the function graph (no param)
  HIDE_FUNCTION_GRAPH, // Ask to hide the function graph (no param)

  NEW_GROOVY_SCRIPT,  // A groovy script is in use (param: GROOVY_SCRIPT_SHELL, GROOVY_SCRIPT_ID, GROOVY_SCRIPT_FILE, GROOVY_SCRIPT_PREPROCESSOR) 
  NEW_GROOVY_BINDING,  // A groovy binding is in use (param: GROOVY_SCRIPT_ID, GROOVY_SCRIPT_BINDING) 
  GROOVY_SCRIPT_UPDATED, // a groovy script has been updated (param: GROOVY_SCRIPT_ID)

  UPDATE_RENDERING, // ask the application GUI to update the render area (no param)
  ;

  public static enum GeOxygeneEventKey {
    GROOVY_SCRIPT_SHELL, // Groovy shell instance for NEW_GROOVY_SCRIPT (type: GroovyShell) 
    GROOVY_SCRIPT_ID, // Groovy script id for NEW_GROOVY_SCRIPT, GROOVY_SCRIPT_UPDATED, NEW_GROOVY_BINDING (type: String) 
    GROOVY_SCRIPT_FILE, // Groovy script file for NEW_GROOVY_SCRIPT, GROOVY_SCRIPT_UPDATED (type: String) 
    GROOVY_SCRIPT_PREPROCESSOR, // Groovy script content for NEW_GROOVY_SCRIPT (type: TextTransformer) 
    GROOVY_SCRIPT_BINDING, // Groovy script content for NEW_GROOVY_BINDING (type: Binding) 
    ;
  }

}
