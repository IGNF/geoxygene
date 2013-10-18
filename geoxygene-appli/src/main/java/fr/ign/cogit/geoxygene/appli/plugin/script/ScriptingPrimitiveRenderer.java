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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventType;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventType.GeOxygeneEventKey;
import fr.ign.cogit.geoxygene.appli.api.GeOxygeneInterlocutor;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.render.primitive.AbstractPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLPrimitiveRenderer;
import fr.ign.cogit.geoxygene.appli.render.primitive.PrimitiveRenderer;
import fr.ign.cogit.geoxygene.scripting.TextTransformException;
import fr.ign.cogit.geoxygene.scripting.TextTransformer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @author JeT This renderer uses groovy scripting language to do the rendering
 */
public class ScriptingPrimitiveRenderer extends AbstractPrimitiveRenderer
    implements GeOxygeneInterlocutor, TextTransformer {

  private static Logger logger = Logger
      .getLogger(ScriptingPrimitiveRenderer.class.getName()); // logger
  private final static String NEWLINE = System.getProperty("line.separator");

  private final String id; // this renderer ID (used to get script updates from
                           // groovy editor)
  // groovy file containing the renderer description in groovy code
  private File initialScriptingFile = null;
  private String preprocessedScriptingFileContent = null; // preprocessed
                                                          // scripting file
                                                          // (some replacement
                                                          // in initial
                                                          // scriptingFile)
  // private MyGroovyScriptEngine gse = null; // script engine associated with
  // the groovy file
  // private Class<?> executableClass = null;
  private Script groovyScript = null;
  private GroovyShell groovySh = null;
  private final Object[] emptyArgs = {};

  private Binding binding = null; // binding associated with the script engine
  // available renderers (for groovy access)
  private final Map<String, PrimitiveRenderer> availableRenderers = new HashMap<String, PrimitiveRenderer>();
  // keep track and count of errors
  private final Map<Class<? extends Exception>, int[]> errors = new HashMap<Class<? extends Exception>, int[]>();
  // key/value global map passed to script used to keep values between
  // executions exceptions
  private final HashMap<Object, Object> globals = new HashMap<Object, Object>();

  // private ScriptingConsole console = null;

  // private Collection<ParameterizedPolygons> polygons = null;
  // private Collection<PrimitivePoints> points = null;

  /**
   * Constructor
   * @param scriptingFilename
   * @throws IOException
   */
  public ScriptingPrimitiveRenderer(final String scriptingFilename)
      throws RenderingException {
    this(new File(scriptingFilename));

  }

  /**
   * Constructor
   * @param scriptingFile
   * @throws IOException
   * @throws RenderingException
   */
  public ScriptingPrimitiveRenderer(final File scriptingFile)
      throws RenderingException {
    super();
    this.id = scriptingFile.getName() + "-"
        + String.valueOf(new Date().getTime());
    this.initializeAvailableRenderers();
    this.setScriptingFile(scriptingFile);

  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * Fill a collection of know renderers that can be used within the script
   */
  private void initializeAvailableRenderers() {
    this.addRenderer("glRenderer", new GLPrimitiveRenderer());

  }

  /**
   * Add a renderer is the collection of available renderers
   * @param variableName name of the renderer instance that can be used in the
   *          script
   * @param primitiveRenderer instance of the renderer
   */
  private void addRenderer(final String variableName,
      final PrimitiveRenderer primitiveRenderer) {
    this.availableRenderers.put(variableName, primitiveRenderer);
  }

  /**
   * @param scriptingFile the groovyFile to set
   * @throws IOException
   * @throws RenderingException
   * @throws CompilationFailedException
   */
  public final void setScriptingFile(final File scriptingFile)
      throws RenderingException {
    this.invalidateInputFile();
    this.initialScriptingFile = scriptingFile;
    this.preprocessedScriptingFileContent = null;
    if (!scriptingFile.isFile() || !scriptingFile.canRead()) {
      throw new RenderingException("Groovy scripting file " + scriptingFile
          + " is not a real file or not readable");
    }

    // String[] roots = new String[] {
    // scriptingFile.getAbsolutePath()
    // };

    // GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    // GroovyCodeSource gcs = new GroovyCodeSource(this.getScriptingFile());
    // this.executableClass = groovyClassLoader.parseClass(gcs, true);
    // System.err.println("executable class = " +
    // this.executableClass.getClass().getName() + " / " +
    // this.executableClass);

    // this.gse = new
    // MyGroovyScriptEngine(this.scriptingFile.getAbsolutePath());
    // // add some default imports to the script
    // ImportCustomizer defaultImports = new ImportCustomizer();
    // defaultImports.addStarImports("fr.ign.cogit.geoxygene.appli.render.primitive");
    // defaultImports.addStarImports("fr.ign.cogit.geoxygene.appli.render.gl");
    // defaultImports.addStarImports("fr.ign.cogit.geoxygene.appli.render");
    // defaultImports.addStaticStars("org.lwjgl.opengl.GL11");
    // defaultImports.addImports("javax.vecmath.Point2d");
    // defaultImports.addImports("java.awt.Color");
    // CompilerConfiguration config = new CompilerConfiguration();
    // config.addCompilationCustomizers(defaultImports);
    // this.gse.setConfig(config);

    HashMap<GeOxygeneEventKey, Object> params = new HashMap<GeOxygeneEventType.GeOxygeneEventKey, Object>();
    params.put(GeOxygeneEventKey.GROOVY_SCRIPT_ID, this.getId());
    params.put(GeOxygeneEventKey.GROOVY_SCRIPT_FILE, this.initialScriptingFile);
    params.put(GeOxygeneEventKey.GROOVY_SCRIPT_PREPROCESSOR, this);
    params.put(GeOxygeneEventKey.GROOVY_SCRIPT_SHELL, this.getGroovyShell());
    // GeOxygeneEventManager.fire(GeOxygeneEventType.SHOW_GROOVY_CONSOLE);

    GeOxygeneEventManager.fire(GeOxygeneEventType.NEW_GROOVY_SCRIPT, params);
    // GeOxygeneEventManager.fire(GeOxygeneEventType.HIDE_GROOVY_CONSOLE);

    // add this as a geoxygene interlocutor in order to be notified if the
    // script is changed by the groovy plugin
    GeOxygeneEventManager.getInstance().addInterlocutor(this);
  }

  private void invalidateInputFile() {
    this.preprocessedScriptingFileContent = null;
    this.groovyScript = null;
  }

  private Script getGroovyScript() throws RenderingException {
    if (this.groovyScript == null) {

      this.groovyScript = this.getGroovyShell().parse(
          this.getPreprocessedScriptingFileContent());
      this.groovyScript.run();
      // TODO: check if script contains a render() and an initialize() method
    }
    return this.groovyScript;

  }

  private GroovyShell getGroovyShell() {
    if (this.groovySh == null) {
      // add some default imports to the script
      ImportCustomizer defaultImports = new ImportCustomizer();
      defaultImports
          .addStarImports("fr.ign.cogit.geoxygene.appli.render.primitive");
      defaultImports
          .addStarImports("fr.ign.cogit.geoxygene.appli.render.operator");
      defaultImports.addStarImports("fr.ign.cogit.geoxygene.appli.render.gl");
      defaultImports.addStarImports("fr.ign.cogit.geoxygene.appli.render");
      defaultImports.addStarImports("fr.ign.cogit.geoxygene.function");
      defaultImports.addStaticStars("org.lwjgl.opengl.GL11");
      defaultImports.addStaticStars("java.lang.Math");
      defaultImports.addStarImports("javax.vecmath");
      defaultImports.addStarImports("java.awt");
      final CompilerConfiguration config = new CompilerConfiguration();
      config.addCompilationCustomizers(defaultImports);
      final Binding binding = this.getBinding();

      this.groovySh = new GroovyShell(binding, config);
    }
    return this.groovySh;
  }

  private String getPreprocessedScriptingFileContent()
      throws RenderingException {
    if (this.preprocessedScriptingFileContent == null) {
      this.preprocessedScriptingFileContent = preprocessScriptingFile(this.initialScriptingFile);
      // System.err.println("**************************************************************************************************");
      // System.err.println(this.preprocessedScriptingFileContent);
      // System.err.println("**************************************************************************************************");
    }
    return this.preprocessedScriptingFileContent;
  }

  /**
   * Read the 'initialScriptingFile' file and do some process on it: - replace
   * include 'YYY' by the content of YYY file
   * @return the preprocessed file content
   */
  private static String preprocessScriptingFile(final File file)
      throws RenderingException {
    if (file == null) {
      return null;
    }
    try {
      String fileContent = preprocessScriptingHeader(file.getAbsolutePath())
          + FileUtils.readFileToString(file)
          + preprocessScriptingFooter(file.getAbsolutePath());
      return preprocessScriptingFile(file.getAbsolutePath(), fileContent);
    } catch (IOException e) {
      throw new RenderingException(e);
    }
  }

  /**
   * Header added when preprocessing a file
   * @param filename in preprocessing file name
   * @return preprocess header
   */
  private static String preprocessScriptingHeader(final String filename) {
    return NEWLINE + "//     vvvvvvv BOF '" + filename + "' vvvvvvv" + NEWLINE;
  }

  /**
   * Footer added when preprocessing a file
   * @param filename in preprocessing file name
   * @return preprocess footer
   */
  private static String preprocessScriptingFooter(final String filename) {
    return "//     ^^^^^^^ EOF '" + filename + "' ^^^^^^^" + NEWLINE + NEWLINE;
  }

  private static String preprocessScriptingFile(final String filename,
      final String fileContent) throws RenderingException {
    if (fileContent == null) {
      return null;
    }
    String includedContent = preprocessInclude(filename, fileContent);
    return includedContent;
  }

  /**
   * Replace all 'include "YYY"' lines by the YYY file content
   * @param fileContent file content where to replace includes contents
   * @return preprocessed content
   * @throws RenderingException
   */
  private static String preprocessInclude(final String filename,
      final String fileContent) throws RenderingException {
    if (fileContent == null) {
      return null;
    }
    try {
      Pattern includePattern = Pattern.compile(
          "include\\s+[\\\"\\\'](.*)[\\\"\\\']", Pattern.MULTILINE);
      // Pattern includePattern = Pattern.compile("^\\s*include",
      // Pattern.MULTILINE);
      Matcher m = includePattern.matcher(fileContent);

      if (m.find() && m.groupCount() == 1) {
        return preprocessInclude(
            filename,
            fileContent.replace(m.group(0),
                preprocessScriptingFile(new File(m.group(1)))));
      }
    } catch (PatternSyntaxException pse) {
      throw new RenderingException(pse);
    }
    return fileContent;
  }

  /**
   * Globals is a generic key/value map. It can be used by script to keep values
   * between two executions or to store data generated at initialization
   * @return the globals variable map
   */
  public HashMap<Object, Object> getGlobals() {
    return this.globals;
  }

  /**
   * generate the binding for execution
   * @return new binding
   */
  private Binding getBinding() {
    this.binding = new Binding();
    this.binding.setVariable("viewport", this.getViewport());
    this.binding.setVariable("primitives", this.getPrimitives());
    this.binding.setVariable("global", this.getGlobals());
    // this.binding.setVariable("groovy", this.groovySh);
    // // bind all renderers
    // for (Map.Entry<String, PrimitiveRenderer> entry :
    // this.availableRenderers.entrySet()) {
    // this.binding.setVariable(entry.getKey(), entry.getValue());
    // }
    // this.binding.setVariable("polygons", this.getPolygonsPrimitives());
    return this.binding;
  }

  @Override
  public void initializeRendering() {
    try {
      // this.getGroovyScript().setBinding(this.getBinding());

      this.getGroovyScript().invokeMethod("initialize", this.emptyArgs);

      // this.getGroovyScript().run();
      // System.err.println(this.executableClass.getSimpleName());
      // this.gse.run(this.scriptingFile.getName(), binding);

    } catch (Exception e) {
      logger.error(e);
      e.printStackTrace();
    }

  }

  @Override
  public void finalizeRendering() throws RenderingException {
    // nothing to finalize
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#render()
   */
  @Override
  public void render() throws RenderingException {
    if (this.getViewport() == null) {
      throw new RenderingException("viewport is not set");
    }
    // TODO: may be we should not send the binding at each rendering !!!
    GeOxygeneEventManager.fire(GeOxygeneEventType.NEW_GROOVY_BINDING,
        GeOxygeneEventKey.GROOVY_SCRIPT_ID, this.getId(),
        GeOxygeneEventKey.GROOVY_SCRIPT_BINDING, this.getBinding());
    try {
      this.getGroovyScript().setBinding(this.getBinding());

      this.getGroovyScript().invokeMethod("render", this.emptyArgs);

      // this.getGroovyScript().run();
      // System.err.println(this.executableClass.getSimpleName());
      // this.gse.run(this.scriptingFile.getName(), binding);

    } catch (Exception e) {
      logger.error("An error occured executing script ");
      e.printStackTrace();
      throw new RenderingException(e);
      // int[] eCount = this.errors.get(e.getClass());
      // if (eCount == null) {
      // // throw an error only the first time it appears
      // this.errors.put(e.getClass(), new int[] {
      // 0
      // });
      // throw new RenderingException(e);
      // } else {
      // // increment the error counter
      // eCount[0]++;
      // if ( eCount[0] % 10000 == 0) {
      // logger.error("exception " + e.getClass().getSimpleName() + " occurs " +
      // eCount[0] + " times");
      //
      // }
      // }
    }

  }

  @Override
  public void onGeOxygeneEvent(final GeOxygeneEventType type,
      final Map<GeOxygeneEventKey, Object> params) {

    if (type == GeOxygeneEventType.GROOVY_SCRIPT_UPDATED) {
      // check if it's our script that has been updated
      String scriptId = (String) params.get(GeOxygeneEventKey.GROOVY_SCRIPT_ID);
      if (scriptId.equals(this.getId())) {
        // String scriptContent = (String)
        // params.get(GeOxygeneEventKey.GROOVY_SCRIPT_CONTENT);
        this.invalidateInputFile();
        this.initializeRendering();
        GeOxygeneEventManager.fire(GeOxygeneEventType.UPDATE_RENDERING);
      }
    }

  }

  // @Override
  // public String getId() {
  // return String.valueOf(this.hashCode());
  // }
  //
  // @Override
  // public InputStream getScriptContent() {
  // try {
  // return new
  // ByteArrayInputStream(this.getPreprocessedScriptingFileContent().getBytes());
  // } catch (RenderingException e) {
  // logger.error(e.getMessage());
  // return null;
  // }
  // }

  @Override
  public String transform(final String text) throws TextTransformException {
    try {
      return preprocessScriptingFile(this.initialScriptingFile);
    } catch (RenderingException e) {
      throw new TextTransformException(e);
    }
  }

}
