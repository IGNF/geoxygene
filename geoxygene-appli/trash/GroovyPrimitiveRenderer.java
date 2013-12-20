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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author JeT
 * This renderer uses groovy scripting language to do the rendering.
 * Groovy script must define an instance of a PrimitiveRenderer in 'renderer' variable
 * Classic usage is to define a new Class extending AbstractPrimitiveRenderer and 
 * implementing render() method. It must return a new Instance of this class
 * In groovy script conventions, it is simply the last line of the script
 * 
 * example of simple rendering delegating rendering to GLPrimitiveRenderer
 * -----------------------
 * import fr.ign.cogit.geoxygene.appli.render.AbstractPrimitiveRenderer;
 * import fr.ign.cogit.geoxygene.appli.render.GLPrimitiveRenderer;
 * import fr.ign.cogit.geoxygene.appli.render.RenderingException;
 * 
 *   
 * class MyRenderer extends AbstractPrimitiveRenderer {
 * 
 *   GLPrimitiveRenderer gl = new GLPrimitiveRenderer();
 *   
 *   void setViewport( Viewport viewport ) {
 *     super.setViewport( viewport );
 *     gl.setViewport( viewport );
 *   }
 *   
 *   void render() throws RenderingException {
 *     
 *     gl.removeAllPrimitives();
 *     gl.addPrimitives( this.getPrimitives() );
 *     gl.render();
 *   }
 * 
 * }
 * 
 * renderer = new MyRenderer();
 * -----------------------
 */
public class GroovyPrimitiveRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(GroovyPrimitiveRenderer.class.getName()); // logger
  // groovy file containing the renderer description in groovy code
  private File groovyFile = null;
  private GroovyScriptEngine gse = null; // script engine associated with the groovy file
  private Binding binding = null; // binding associated with the script engine
  private PrimitiveRenderer groovyRenderer = null; // primitive renderer written in groovy
  private final Map<Class<? extends Exception>, int[]> errors = new HashMap<Class<? extends Exception>, int[]>();

  /**
   * Constructor
   * @param groovyFilename
   */
  public GroovyPrimitiveRenderer(final String groovyFilename) {
    super();
    this.setGroovyFile(new File(groovyFilename));
  }

  /**
   * Constructor
   * @param groovyFile
   */
  public GroovyPrimitiveRenderer(final File groovyFile) {
    super();
    this.setGroovyFile(groovyFile);
  }

  /**
   * @param groovyFile the groovyFile to set
   */
  public final void setGroovyFile(final File groovyFile) {
    this.groovyFile = groovyFile;
    this.invalidateGroovyScriptEngine();
  }

  private void invalidateGroovyScriptEngine() {
    this.gse = null;

  }

  /**
   * @return the groovyFile
   */
  public final File getGroovyFile() {
    return this.groovyFile;
  }

  /**
   * Lazy getter of the script engine. use invalidateGroovyScriptEngine() method to regenerate it
   * @return new or existing script engine
   * @throws IOException 
   */
  public GroovyScriptEngine getGroovyScriptEngine() throws IOException {
    File groovyFile = this.getGroovyFile();
    if (groovyFile == null) {
      return null;
    }
    if (this.gse == null) {
      if (!groovyFile.isFile() || !groovyFile.canRead()) {
        throw new IOException("Groovy script file " + groovyFile + " is not a real file or not readable");
      }
      String[] roots = new String[] {
        groovyFile.getAbsolutePath()
      };

      this.gse = new GroovyScriptEngine(roots);
      this.invalidateBinding();
    }
    return this.gse;

  }

  /**
   * remove the current generated binding
   */
  private void invalidateBinding() {
    this.binding = null;
  }

  /**
   * Lazy getter of the script engine binding. use invalidateBinding() method to regenerate it
   * @return new or existing binding
   */
  private Binding getBinding() {
    if (this.binding == null) {
      this.binding = new Binding();
      this.binding.setVariable("viewport", this.getViewport());
    }
    return this.binding;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#render()
   */
  @Override
  public void render() throws RenderingException {
    if (this.getViewport() == null) {
      throw new RenderingException("viewport is not set");
    }
    try {
      this.getGroovyRenderer().setViewport(this.getViewport());
      this.getGroovyRenderer().removeAllPrimitives();
      this.getGroovyRenderer().addPrimitives(this.getPrimitives());
      this.getGroovyRenderer().render();
    } catch (Exception e) {
      int[] eCount = this.errors.get(e.getClass());
      if (eCount == null) {
        // throw an error only the first time it appears
        this.errors.put(e.getClass(), new int[] {
          1
        });
        throw new RenderingException(e);
      } else {
        // just increment the error counter
        eCount[0]++;
        if (eCount[0] % 10000 == 0) {
          logger.error("exception " + e.getClass().getSimpleName() + " occurs " + eCount[0] + " times");
        }
      }
    }

  }

  private PrimitiveRenderer getGroovyRenderer() throws IOException, ResourceException, ScriptException {
    if (this.groovyRenderer == null) {
      GroovyScriptEngine gse = this.getGroovyScriptEngine();
      Binding binding = this.getBinding();
      gse.run(this.groovyFile.getName(), binding);
      this.groovyRenderer = (PrimitiveRenderer) binding.getVariable("renderer");
    }
    return this.groovyRenderer;
  }

  public void invalidateRenderer() {
    this.groovyRenderer = null;
  }

  @Override
  public void initializeRendering() throws RenderingException {
    // nothing to initialize

  }

  @Override
  public void finalizeRendering() throws RenderingException {
    // nothing to finalize
  }

}
