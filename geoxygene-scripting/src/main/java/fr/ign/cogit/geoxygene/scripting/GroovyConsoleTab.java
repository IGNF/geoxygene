package fr.ign.cogit.geoxygene.scripting;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MetaMethod;
import groovy.lang.Script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
// JAVA7:
//import java.nio.charset.Charset;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import jsyntaxpane.DefaultSyntaxKit;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.tools.shell.util.Preferences;

/**
 * @author JeT
 * Tab for Groovy Console UI. It contains an groovy text editor and a result text pane
 * Some key binding are hardcoded:
 * Ctrl + s : save
 * Ctrl + Shift + s : save as
 * Ctrl + n : new file
 * Ctrl + o : open file
 */
public class GroovyConsoleTab extends JPanel {

  private static final long serialVersionUID = 6257410289869320772L; // Serializable UID
  private static Logger logger = Logger.getLogger(GroovyConsoleTab.class.getName());
  private static final Border BORDERS = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
  private static final String NEWLINE = System.getProperty("line.separator");
  private GroovyConsoleUI console = null;
  private GroovyShell groovyShell = null;
  private String title = "untitled";
  private String id = String.valueOf(new Date().getTime());
  private File file = null; // edited file or null 
  private TextTransformer textTransformer = null; // text transformation applied before compilation (or null)
  private boolean sourceView = true; // tell if we are viewing the sources or the expanded preprocessed file

  // UI elements  
  private JPanel resultPanel = null;
  private JTextPane resultTextPane = null;
  private JEditorPane editor = null;
  private JPanel editionPanel = null;
  private SimpleAttributeSet resultAttr = null;
  private SimpleAttributeSet criticAttr = null;
  private SimpleAttributeSet errorAttr = null;
  private SimpleAttributeSet bindingAttr = null;
  private Color resultBackgroundColor = new Color(250, 250, 220);
  private Color defaultBackground = this.resultBackgroundColor;
  private Color defaultForeground = Color.black;
  private Color errorBackground = this.resultBackgroundColor;
  private Color errorForeground = Color.red;
  private Color criticBackground = Color.red;
  private Color criticForeground = Color.white;
  private Color bindingBackground = this.resultBackgroundColor;
  private Color bindingForeground = new Color(10, 80, 20);
  private Set<Class<? extends Exception>> hiddenExceptions = new HashSet<Class<? extends Exception>>();

  //  private final DisplayConfigurationAction displayConfigurationAction = new DisplayConfigurationAction();
  //  private final RunScriptAction runScriptAction = new RunScriptAction();
  //  private final SaveScriptAction saveScriptAction = new SaveScriptAction();

  /**
   * Constructor
   */
  public GroovyConsoleTab(final String id, final String title, final File file, final TextTransformer tt, final GroovyConsoleUI console,
      final GroovyShell groovyShell) {
    this.console = console;
    this.id = id;
    this.textTransformer = tt;
    this.setFile(file);
    this.setTitle(title);
    this.setGroovyShell(groovyShell);
    this.resultAttr = new SimpleAttributeSet();
    StyleConstants.setForeground(this.resultAttr, this.defaultForeground);
    StyleConstants.setBackground(this.resultAttr, this.defaultBackground);
    StyleConstants.setBold(this.resultAttr, true);
    this.errorAttr = new SimpleAttributeSet();
    StyleConstants.setForeground(this.errorAttr, this.errorForeground);
    StyleConstants.setBackground(this.errorAttr, this.errorBackground);
    StyleConstants.setBold(this.errorAttr, false);
    this.criticAttr = new SimpleAttributeSet();
    StyleConstants.setBackground(this.criticAttr, this.criticBackground);
    StyleConstants.setForeground(this.criticAttr, this.criticForeground);
    StyleConstants.setBold(this.criticAttr, false);
    this.bindingAttr = new SimpleAttributeSet();
    StyleConstants.setBackground(this.bindingAttr, this.bindingBackground);
    StyleConstants.setForeground(this.bindingAttr, this.bindingForeground);
    StyleConstants.setBold(this.bindingAttr, false);
    this.initUI();
  }

  private void initUI() {
    this.setLayout(new BorderLayout());
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.getEditionPanel(), this.getResultPanel());
    this.add(splitPane, BorderLayout.CENTER);
    splitPane.setDividerLocation(0.5d);
    splitPane.setResizeWeight(0.5d);
    splitPane.setDividerSize(3);
  }

  private JPanel getResultPanel() {
    if (this.resultPanel == null) {
      this.resultPanel = new JPanel(new BorderLayout());
      this.resultPanel.setBorder(BORDERS);
      this.resultPanel.add(new JScrollPane(this.getResultTextPane()), BorderLayout.CENTER);
      this.resultPanel.setPreferredSize(new Dimension(400, 300));
    }
    return this.resultPanel;
  }

  private JTextPane getResultTextPane() {
    if (this.resultTextPane == null) {
      this.resultTextPane = new JTextPane();
      this.resultTextPane.setEditable(false);
      this.resultTextPane.setBackground(this.resultBackgroundColor);
      this.resultTextPane.setForeground(Color.black);
    }
    return this.resultTextPane;
  }

  private StyledDocument getResultDocument() {
    return this.getResultTextPane().getStyledDocument();
  }

  private JPanel getEditionPanel() {
    if (this.editionPanel == null) {
      this.editionPanel = new JPanel(new BorderLayout());
      this.editionPanel.setPreferredSize(new Dimension(400, 300));
      this.editionPanel.add(new JScrollPane(this.getEditor()), BorderLayout.CENTER);

      // HACK due to a bug in JEditorPane highlighting. Content type has to be set AFTER the editor is added to the GUI
      // and text is destroyed when setting the language :(
      DefaultSyntaxKit.initKit();
      String text = this.getEditor().getText();
      this.getEditor().setContentType("text/groovy");
      this.getEditor().setText(text);
    }
    return this.editionPanel;
  }

  JEditorPane getEditor() {
    if (this.editor == null) {
      this.editor = new JEditorPane();

      // hardcoded key mapping

      this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "saveAction");
      this.editor.getActionMap().put("saveAction", new AbstractAction() {

        @Override
        public void actionPerformed(final ActionEvent actionevent) {
          GroovyConsoleTab.this.console.saveScript(false);

        }

      });
      this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "saveAsAction");
      this.editor.getActionMap().put("saveAsAction", new AbstractAction() {

        @Override
        public void actionPerformed(final ActionEvent actionevent) {
          GroovyConsoleTab.this.console.saveScript(true);

        }

      });
      this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "newAction");
      this.editor.getActionMap().put("newAction", new AbstractAction() {

        @Override
        public void actionPerformed(final ActionEvent actionevent) {
          GroovyConsoleTab.this.console.newFile();

        }

      });
      this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "openAction");
      this.editor.getActionMap().put("openAction", new AbstractAction() {

        @Override
        public void actionPerformed(final ActionEvent actionevent) {
          GroovyConsoleTab.this.console.openFile();

        }

      });

      //      this.editor = new RSyntaxTextArea(40, 60);
      //      this.editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
      //      this.editor.setCodeFoldingEnabled(true);
      //      this.editor.setAntiAliasingEnabled(true);
    }
    return this.editor;
  }

  /**
   * @return the hiddenExceptions
   */
  public Set<Class<? extends Exception>> getHiddenExceptions() {
    return this.hiddenExceptions;
  }

  /**
   * @param hiddenException the hiddenException to add
   */
  public void addHiddenExceptions(final Class<? extends Exception> hiddenException) {
    this.hiddenExceptions.add(hiddenException);
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @param title the title to set
   */
  final public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param file the file to set
   */
  public void setFile(final File file) {
    this.file = file;
    if (this.file != null) {
      try {
        this.setScriptText(readFile(file.getAbsolutePath()));
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }
  }

  // JAVA7:
  //  /**
  //   * read a file into a string
  //   * @param path
  //   * @param encoding
  //   * @return
  //   * @throws IOException
  //   */
  //  static String readFile(final String path, final Charset encoding) throws IOException {
  //    byte[] encoded = Files.readAllBytes(Paths.get(path));
  //    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
  //  }
  //
  //  /**
  //   * read a file into a string
  //   * @param path
  //   * @return
  //   * @throws IOException
  //   */
  //  static String readFile(final String path) throws IOException {
  //    return readFile(path, Charset.defaultCharset());
  //  }

  /**
   * read a file into a string
   * @param path
   * @return
   * @throws IOException
   */
  static String readFile(final String path) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    String line = null;
    StringBuilder stringBuilder = new StringBuilder();
    String ls = System.getProperty("line.separator");

    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line);
      stringBuilder.append(ls);
    }
    reader.close();
    return stringBuilder.toString();
  }

  /**
   * @return the file
   */
  public File getFile() {
    return this.file;
  }

  /**
   * @return the console
   */
  public GroovyConsoleUI getConsole() {
    return this.console;
  }

  /**
   * @return the groovyShell
   */
  public GroovyShell getGroovyShell() {
    return this.groovyShell;
  }

  /**
   * @param groovyShell the groovyShell to set
   */
  public void setGroovyShell(final GroovyShell groovyShell) {
    if (groovyShell != null) {
      this.groovyShell = new GroovyShell(groovyShell.getClassLoader(), groovyShell.getContext(), (CompilerConfiguration) groovyShell
          .getProperty("config"));

      this.groovyShell.setProperty("out", new PrintStream(new ConsoleOutputStream(this.getResultDocument(), Color.blue), true));
      this.groovyShell.setProperty("err", new PrintStream(new ConsoleOutputStream(this.getResultDocument(), Color.red), true));
    }
  }

  private String formatStackTraceElement(final StackTraceElement ste) {
    return ste.toString();
  }

  private void setScriptText(final String text) {
    this.getEditor().setText(text);

  }

  public String getScriptText() {
    return this.getEditor().getText();
  }

  public void setBinding(final Binding binding) {
    if (this.groovyShell != null && binding != null) {
      // there is no setContext() or setBinding(), even using setProperty("config", binding) does not work. so recreate a full shell :( 
      this.groovyShell = new GroovyShell(binding, (CompilerConfiguration) this.groovyShell.getProperty("config"));
    }
  }

  /**
   * Toggle between source view and preprocessed view
   * @param selected
   */
  public void toggleSourceView(final boolean selected) {
    if (this.sourceView == selected) {
      return;
    }
    if (selected && this.getFile() != null) {
      try {
        this.setScriptText(readFile(this.file.getAbsolutePath()));
        this.getEditor().setEditable(true);
        this.console.displayNotification("display initial script");
        this.sourceView = true;
        this.console.getSourceButton().setSelected(true);
      } catch (IOException e) {
        logger.error(e.getMessage());
        this.console.displayNotification("an error occurred displaying initial script");
      }
    }
    if (!selected && this.textTransformer != null) {
      try {
        this.saveScript(false);
        this.setScriptText(this.textTransformer.transform(readFile(this.file.getAbsolutePath())));
        this.getEditor().setEditable(false);
        this.sourceView = false;
        this.console.getSourceButton().setSelected(false);
      } catch (TextTransformException e) {
        logger.error(e.getMessage());
        this.console.displayNotification("an error occurred displaying preprocessed script");
      } catch (IOException e) {
        logger.error(e.getMessage());
        this.console.displayNotification("an error occurred displaying preprocessed script");
      }
      this.console.displayNotification("display preprocessed script");
    }
  }

  /**
   * display the current binding into the resultPane
   */
  public void displayConfiguration() {
    StyledDocument doc = GroovyConsoleTab.this.getResultDocument();
    CompilerConfiguration config = (CompilerConfiguration) GroovyConsoleTab.this.groovyShell.getProperty("config");
    StringBuilder str = new StringBuilder();
    str.append("----------------------------- Configuration:" + NEWLINE);

    // class path
    List<String> classpath = config.getClasspath();
    str.append("  classpath = ");
    for (String cpEntry : classpath) {
      str.append(cpEntry + ":");
    }
    str.append(NEWLINE);
    // debug mode
    str.append("  debug = " + config.getDebug() + NEWLINE);
    // customizers
    List<CompilationCustomizer> customizers = config.getCompilationCustomizers();
    for (CompilationCustomizer customizer : customizers) {
      str.append("  customizer : " + customizer.toString() + NEWLINE);
    }
    // default script extension
    str.append("  default script extension = " + config.getDefaultScriptExtension() + NEWLINE);
    // optimization options
    Map<String, Boolean> options = config.getOptimizationOptions();
    for (Map.Entry<String, Boolean> opt : options.entrySet()) {
      str.append("  compiler option = " + opt.getKey() + " : " + opt.getValue() + NEWLINE);
    }
    str.append("  source encoding = " + config.getSourceEncoding() + NEWLINE);
    // class path
    List<String> disabledAST = config.getClasspath();
    str.append("  disabled AST = ");
    for (String astEntry : disabledAST) {
      str.append(astEntry + ":");
    }
    str.append(NEWLINE);
    // tolerance
    str.append("  tolerance = " + config.getTolerance() + NEWLINE);
    // default script extension
    str.append("  warning level = " + config.getWarningLevel() + NEWLINE);
    // default script extension
    str.append("  verbose = " + config.getVerbose() + NEWLINE);

    str.append("--------------------------------------- " + NEWLINE);
    try {
      doc.insertString(doc.getLength(), str.toString(), GroovyConsoleTab.this.bindingAttr);
    } catch (BadLocationException e) {
      logger.error(e.getMessage());
    }

  }

  /**
   * display the current binding into the resultPane
   */
  public void displayBinding() {
    StyledDocument doc = GroovyConsoleTab.this.getResultDocument();
    Binding binding = GroovyConsoleTab.this.groovyShell.getContext();
    StringBuilder str = new StringBuilder();
    str.append("----------------------------- Binding:" + NEWLINE);
    for (Map.Entry<String, Object> variable : (Set<Map.Entry<String, Object>>) binding.getVariables().entrySet()) {
      str.append("  " + variable.getKey() + ":" + variable.getValue()
          + (variable.getValue() == null ? "" : " (" + variable.getValue().getClass().getSimpleName() + ")") + NEWLINE);
    }
    str.append("--------------------------------------- " + NEWLINE);
    try {
      doc.insertString(doc.getLength(), str.toString(), GroovyConsoleTab.this.bindingAttr);
    } catch (BadLocationException e) {
      logger.error(e.getMessage());
    }

  }

  boolean saveScript(final boolean saveAs) {
    this.toggleSourceView(true);
    if (Preferences.get(GroovyConsoleUI.LAST_DIRECTORY) != null) {
      GroovyConsoleUI.fileChooser.setCurrentDirectory(new File(Preferences.get(GroovyConsoleUI.LAST_DIRECTORY)));
    }

    File f = GroovyConsoleTab.this.getFile();
    if (saveAs || f == null) {
      int returnVal = GroovyConsoleUI.fileChooser.showSaveDialog(GroovyConsoleTab.this.console.getMainFrame());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        f = GroovyConsoleUI.fileChooser.getSelectedFile();
        if (f.exists()) {
          f.renameTo(new File(f.getAbsolutePath() + ".svg"));
        }
      }
    }
    if (f != null) {
      try {
        BufferedWriter out = new BufferedWriter(new FileWriter(f));
        out.write(GroovyConsoleTab.this.getScriptText());
        out.close();
        GroovyConsoleTab.this.setFile(f);
        Preferences.put(GroovyConsoleUI.LAST_DIRECTORY, f.getAbsolutePath());
        return true;
      } catch (IOException e) {
        String errorMessage = "Cannot write file " + f.getAbsolutePath() + " : " + e.getMessage();
        logger.error(errorMessage);
        JOptionPane.showMessageDialog(GroovyConsoleTab.this.console.getMainFrame(), errorMessage, "save error", JOptionPane.ERROR_MESSAGE);
      }
    }
    return false;
  }

  /**
   * run the script using groovy interpreter and display result in the result textpane
   * It runs the main method and all zero-arguments methods
   */

  boolean runScript() {
    Object resultObject = null;
    StyledDocument doc = GroovyConsoleTab.this.getResultDocument();
    String scriptText = GroovyConsoleTab.this.getScriptText();
    if (this.textTransformer != null) {
      try {
        scriptText = this.textTransformer.transform(scriptText);
      } catch (TextTransformException e) {
        logger.error("Bad transformation exception " + e.getMessage());
        e.printStackTrace();
      }
    }
    GroovyShell sh = GroovyConsoleTab.this.getGroovyShell();
    Script script = sh.parse(scriptText);

    try {

      // run all methods with no args which are not in "Object" methods
      List<MetaMethod> methods = script.getMetaClass().getMethods();
      List<MetaMethod> metaMethods = script.getMetaClass().getMetaMethods();
      Method[] objectMethods = Object.class.getMethods();
      for (MetaMethod method : methods) {
        if (method.getParameterTypes().length == 0) {
          if (!this.inMethodList(method, metaMethods) && !this.inMethods(method, objectMethods)) {
            try {
              doc.insertString(doc.getLength(), "-------------------  run method " + method.getName() + "\n", GroovyConsoleTab.this.resultAttr);
              Object resultValue = script.invokeMethod(method.getName(), null);
              try {
                doc.insertString(doc.getLength(), "result value = '" + resultValue + "'" + NEWLINE, GroovyConsoleTab.this.resultAttr);
              } catch (BadLocationException e) {
                logger.error("Cannot display result value in Console. Result = " + resultValue);
                e.printStackTrace();
              }

            } catch (Exception e) {
              if (GroovyConsoleTab.this.hiddenExceptions.contains(e.getClass())) {
                doc.insertString(doc.getLength(), "Exception " + e.getClass().getSimpleName() + " occurred but is hidden" + NEWLINE,
                    GroovyConsoleTab.this.resultAttr);
                doc.insertString(doc.getLength(), "  message was " + e.getMessage() + NEWLINE, GroovyConsoleTab.this.errorAttr);
                return true;
              } else {
                doc.insertString(doc.getLength(), e.getMessage() + "\n", GroovyConsoleTab.this.criticAttr);
                StackTraceElement[] stackTrace = e.getStackTrace();
                StringBuilder str = new StringBuilder();
                for (StackTraceElement ste : stackTrace) {
                  str.append(GroovyConsoleTab.this.formatStackTraceElement(ste) + NEWLINE);
                }
                doc.insertString(doc.getLength(), str.toString(), GroovyConsoleTab.this.errorAttr);
                return false;
              }

            }
          }
        }
      }
    } catch (Exception e) {
      this.displayException(doc, e);
      return false;
    }
    try {
      // run the main 
      doc.insertString(doc.getLength(), "-------------------  run script\n", GroovyConsoleTab.this.resultAttr);
      resultObject = script.run();
      doc.insertString(doc.getLength(), "-------------------  end of compilation\n", GroovyConsoleTab.this.resultAttr);
    } catch (Exception e) {
      this.displayException(doc, e);
      return false;
    }
    try {
      doc.insertString(doc.getLength(), "result value = '" + resultObject + "'" + NEWLINE, GroovyConsoleTab.this.resultAttr);
    } catch (BadLocationException e) {
      logger.error("Cannot display result value in Console. Result = " + resultObject);
      e.printStackTrace();
    }
    GroovyConsoleTab.this.console.newResults(GroovyConsoleTab.this.getId());

    return true;

  }

  private void displayException(final StyledDocument doc, final Exception e) {
    try {
      if (GroovyConsoleTab.this.hiddenExceptions.contains(e.getClass())) {
        doc.insertString(doc.getLength(), "Exception " + e.getClass().getSimpleName() + " occurred but is hidden" + NEWLINE,
            GroovyConsoleTab.this.resultAttr);
        doc.insertString(doc.getLength(), "  message was " + e.getMessage() + NEWLINE, GroovyConsoleTab.this.errorAttr);
      } else {
        doc.insertString(doc.getLength(), e.getMessage() + "\n", GroovyConsoleTab.this.criticAttr);
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder str = new StringBuilder();
        for (StackTraceElement ste : stackTrace) {
          str.append(GroovyConsoleTab.this.formatStackTraceElement(ste) + NEWLINE);
        }
        doc.insertString(doc.getLength(), str.toString(), GroovyConsoleTab.this.errorAttr);
      }
    } catch (BadLocationException e1) {
      logger.error("displaying exception throws an exception... :/");
      logger.error(e1.getMessage());
      e1.printStackTrace();
    }
  }

  private boolean inMethods(final MetaMethod method, final Method[] methods) {
    for (Method objectMethod : methods) {
      if (objectMethod.getName().equals(method.getName())) {
        return true;
      }
    }
    return false;
  }

  private boolean inMethodList(final MetaMethod method, final List<MetaMethod> metaMethods) {
    for (MetaMethod metaMethod : metaMethods) {
      if (metaMethod.getName().equals(method.getName())) {
        return true;
      }
    }
    return false;
  }

  public class RunScriptAction extends AbstractAction {
    private static final long serialVersionUID = 2333910810710297243L;

    @Override
    public void actionPerformed(final ActionEvent e) {
      GroovyConsoleTab.this.runScript();
    }

  }

  public class DisplayConfigurationAction extends AbstractAction {
    private static final long serialVersionUID = 2179584965585579711L;

    @Override
    public void actionPerformed(final ActionEvent arg0) {
      GroovyConsoleTab.this.displayBinding();
      GroovyConsoleTab.this.displayConfiguration();
    }
  }

  public class SaveScriptAction extends AbstractAction {

    private static final long serialVersionUID = -5184936851777894654L;

    @Override
    public void actionPerformed(final ActionEvent e) {
      GroovyConsoleTab.this.saveScript(false);
    }

  }

  public class SaveAsScriptAction extends AbstractAction {

    private static final long serialVersionUID = -5184936851787894654L;

    @Override
    public void actionPerformed(final ActionEvent e) {
      GroovyConsoleTab.this.saveScript(true);
    }

  }

}
