package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.jdesktop.swingx.JXColorSelectionButton;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.mrdb.REPPointOfView;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterEnrichment;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterXMLParser;
import fr.ign.cogit.cartagen.pearep.PeaRepGeneralisation;
import fr.ign.cogit.cartagen.pearep.derivation.XMLParser;
import fr.ign.cogit.cartagen.pearep.enrichment.ScaleMasterPreProcess;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPSchemaFactory;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPFeature;
import fr.ign.cogit.cartagen.pearep.vmap1PlusPlus.VMAP1PPSchemaFactory;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.ScaleRulerPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.Interval;
import fr.ign.cogit.cartagen.util.XMLUtil;
import fr.ign.cogit.cartagen.util.ontologies.OntologyUtil;
import fr.ign.cogit.geoxygene.filter.BinaryComparisonOpsType;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;

public class EditScaleMasterFrame extends JFrame implements ActionListener,
    ChangeListener, PropertyChangeListener, ItemListener, MouseListener {

  /****/
  private static final long serialVersionUID = 4975395667603478507L;
  public static Logger logger = Logger.getLogger(EditScaleMasterFrame.class
      .getName());

  private ScaleMaster current;
  private ScaleLine selectedLine;
  private OWLOntology ontology;
  private Set<ScaleMasterTheme> existingThemes = new HashSet<ScaleMasterTheme>();
  private Map<String, Color> dbHues;
  private Set<ScaleMasterEnrichment> enrichProcs;
  private List<ScaleMasterGeneProcess> genProcs;
  private Set<ScaleMasterPreProcess> preProcs;

  private JButton btnOk, btnCancel, btnAddLine, btnAddElement, btnEditElement;
  private JButton btnUp, btnDown, btnTop, btnBottom;
  private ImageIcon iconUp, iconDown, iconTop, iconBottom;
  private JTextField txtName;
  private JSpinner spMin, spMax;
  private JComboBox cbPtOfView, cbDbs;
  private JXColorSelectionButton colorBtn;
  private JPanel pDisplay;
  private ScaleRulerPanel ruler;
  private Map<String, ScaleLineDisplayPanel> linePanels = new HashMap<String, ScaleLineDisplayPanel>();
  private Set<GeneObjImplementation> implementations;

  // internationalisation labels
  private String frameTitle, lblCancel, lblOk, lblAddLine, lblAddElement,
      lblEditElement, lblName, lblPtOfView, lblMin, lblMax, lblDbs;

  public EditScaleMasterFrame() throws OWLOntologyCreationException,
      ParserConfigurationException, SAXException, IOException,
      ClassNotFoundException {
    super();
    this.internationalisation();
    this.setTitle(this.frameTitle);
    this.setIconImage(new ImageIcon(this.getClass().getClassLoader()
        .getResource("resources/images/icons/logo.jpg").getPath()
        .replaceAll("%20", " ")).getImage());
    this.setSize(600, 500);
    this.setMaximumSize(new Dimension(700, 600));
    this.setOntology(OntologyUtil
        .getOntologyFromName("MapGeneralisationProcesses"));
    this.setGeoClasses();
    this.current = new ScaleMaster();
    initThemes();

    buildFrameComponents();
  }

  public EditScaleMasterFrame(File themeFile, boolean jar)
      throws OWLOntologyCreationException, ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {
    super();
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.internationalisation();
    this.setTitle(this.frameTitle);
    this.setIconImage(new ImageIcon(EditScaleMasterFrame.class.getClassLoader()
        .getResource("images/icons/logo.jpg")).getImage());
    this.setSize(600, 500);
    this.setMaximumSize(new Dimension(700, 600));
    if (jar)
      try {
        this.setGeoClassesJar();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }
    else
      this.setGeoClasses();
    this.current = new ScaleMaster();
    initThemes(themeFile);
    this.iconUp = new ImageIcon(EditScaleMasterFrame.class.getClassLoader()
        .getResource("images/icons/16x16/chevron_haut.png"));
    this.iconTop = new ImageIcon(EditScaleMasterFrame.class.getClassLoader()
        .getResource("images/icons/16x16/chevron_double_haut.png"));
    this.iconDown = new ImageIcon(EditScaleMasterFrame.class.getClassLoader()
        .getResource("images/icons/16x16/chevron_bas.png"));
    this.iconBottom = new ImageIcon(EditScaleMasterFrame.class.getClassLoader()
        .getResource("images/icons/16x16/chevron_double_bas.png"));
    buildFrameComponents();
  }

  private void buildFrameComponents() {
    this.implementations = new HashSet<GeneObjImplementation>();
    this.dbHues = new HashMap<String, Color>();
    DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
    this.dbHues.put(SourceDLM.MGCPPlusPlus.name(), Color.RED);
    cbModel.addElement(SourceDLM.MGCPPlusPlus.name());
    implementations.add(new GeneObjImplementation(
        SourceDLM.MGCPPlusPlus.name(), MGCPFeature.class.getPackage(),
        MGCPFeature.class, new MGCPSchemaFactory()));
    this.dbHues.put(SourceDLM.VMAP1PlusPlus.name(), Color.BLUE);
    cbModel.addElement(SourceDLM.VMAP1PlusPlus.name());
    implementations.add(new GeneObjImplementation(SourceDLM.VMAP1PlusPlus
        .name(), VMAP1PPFeature.class.getPackage(), VMAP1PPFeature.class,
        new VMAP1PPSchemaFactory()));

    // a panel to define the scale master
    JPanel pDefinition = new JPanel();
    this.txtName = new JTextField();
    this.txtName.setPreferredSize(new Dimension(80, 20));
    this.txtName.setMinimumSize(new Dimension(80, 20));
    this.txtName.setMaximumSize(new Dimension(80, 20));
    this.cbPtOfView = new JComboBox(REPPointOfView.values());
    this.cbPtOfView.setPreferredSize(new Dimension(80, 20));
    this.cbPtOfView.setMinimumSize(new Dimension(80, 20));
    this.cbPtOfView.setMaximumSize(new Dimension(80, 20));
    SpinnerModel modelMin = new SpinnerNumberModel(10000, 0, 10000000, 5000);
    this.spMin = new JSpinner(modelMin);
    this.spMin.setPreferredSize(new Dimension(80, 20));
    this.spMin.setMinimumSize(new Dimension(80, 20));
    this.spMin.setMaximumSize(new Dimension(80, 20));
    this.spMin.addChangeListener(this);
    SpinnerModel modelMax = new SpinnerNumberModel(5000000, 0, 10000000, 5000);
    this.spMax = new JSpinner(modelMax);
    this.spMax.setPreferredSize(new Dimension(80, 20));
    this.spMax.setMinimumSize(new Dimension(80, 20));
    this.spMax.setMaximumSize(new Dimension(80, 20));
    this.spMax.addChangeListener(this);
    this.cbDbs = new JComboBox(cbModel);
    this.cbDbs.setPreferredSize(new Dimension(120, 20));
    this.cbDbs.setMinimumSize(new Dimension(120, 20));
    this.cbDbs.setMaximumSize(new Dimension(120, 20));
    this.cbDbs.addItemListener(this);
    this.colorBtn = new JXColorSelectionButton();
    this.colorBtn.setPreferredSize(new Dimension(25, 25));
    this.colorBtn.setMaximumSize(new Dimension(25, 25));
    this.colorBtn.setMinimumSize(new Dimension(25, 25));
    this.colorBtn.setBackground(this.dbHues.get(this.cbDbs.getSelectedItem()
        .toString()));
    this.colorBtn.addPropertyChangeListener(this);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(this.lblName + " : "));
    pDefinition.add(this.txtName);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(this.lblPtOfView + " : "));
    pDefinition.add(this.cbPtOfView);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(this.lblMin + " : "));
    pDefinition.add(this.spMin);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(this.lblMax + " : "));
    pDefinition.add(this.spMax);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.add(new JLabel(this.lblDbs + " : "));
    pDefinition.add(this.cbDbs);
    pDefinition.add(this.colorBtn);
    pDefinition.add(Box.createHorizontalGlue());
    pDefinition.setLayout(new BoxLayout(pDefinition, BoxLayout.X_AXIS));

    // a panel to edit the scale master
    JPanel pEdition = new JPanel();
    this.btnAddLine = new JButton(this.lblAddLine);
    this.btnAddLine.addActionListener(this);
    this.btnAddLine.setActionCommand("add-line");
    this.btnAddElement = new JButton(this.lblAddElement);
    this.btnAddElement.addActionListener(this);
    this.btnAddElement.setActionCommand("add-elt");
    this.btnEditElement = new JButton(this.lblEditElement);
    this.btnEditElement.addActionListener(this);
    this.btnEditElement.setActionCommand("edit");
    this.btnUp = new JButton(iconUp);
    this.btnUp.addActionListener(this);
    this.btnUp.setActionCommand("up");
    this.btnUp.setEnabled(false);
    this.btnDown = new JButton(iconDown);
    this.btnDown.addActionListener(this);
    this.btnDown.setActionCommand("down");
    this.btnDown.setEnabled(false);
    this.btnTop = new JButton(iconTop);
    this.btnTop.addActionListener(this);
    this.btnTop.setActionCommand("top");
    this.btnTop.setEnabled(false);
    this.btnBottom = new JButton(iconBottom);
    this.btnBottom.addActionListener(this);
    this.btnBottom.setActionCommand("bottom");
    this.btnBottom.setEnabled(false);
    pEdition.add(this.btnAddLine);
    pEdition.add(this.btnAddElement);
    pEdition.add(this.btnEditElement);
    pEdition.add(this.btnUp);
    pEdition.add(this.btnDown);
    pEdition.add(this.btnTop);
    pEdition.add(this.btnBottom);
    pEdition.setLayout(new BoxLayout(pEdition, BoxLayout.X_AXIS));

    // a panel to display the scale master
    this.pDisplay = new JPanel();
    this.pDisplay.setPreferredSize(new Dimension(1200, 800));
    this.pDisplay.setMinimumSize(new Dimension(1200, 600));
    this.ruler = new ScaleRulerPanel(5);
    this.ruler.addMouseListener(this);
    JPanel pRuler = new JPanel();
    pRuler.add(Box.createHorizontalStrut(100), 0);
    pRuler.add(this.ruler);
    pRuler.setLayout(new BoxLayout(pRuler, BoxLayout.X_AXIS));
    this.pDisplay.add(pRuler);
    // no line added as the current scale master is null at the frame
    // construction
    this.pDisplay.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.pDisplay.setLayout(new BoxLayout(this.pDisplay, BoxLayout.Y_AXIS));
    JScrollPane scrollDisplay = new JScrollPane(this.pDisplay);
    scrollDisplay.setPreferredSize(new Dimension(1200, 400));

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    this.btnOk = new JButton(this.lblOk);
    this.btnOk.addActionListener(this);
    this.btnOk.setActionCommand("ok");
    this.btnCancel = new JButton(this.lblCancel);
    this.btnCancel.addActionListener(this);
    this.btnCancel.setActionCommand("cancel");
    pButtons.add(this.btnOk);
    pButtons.add(this.btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // the frame menu
    JMenuBar menuBar = new JMenuBar();
    // a File menu
    JMenu menuFichier = new JMenu(I18N.getString("MainLabels.lblFile"));
    JMenuItem load = new JMenuItem(I18N.getString("MainLabels.lblLoad"));
    load.setActionCommand("load");
    load.addActionListener(this);
    ImageIcon iconeAide = new ImageIcon(EditScaleMasterFrame.class
        .getClassLoader().getResource("images/icons/16x16/help.png").getPath()
        .replaceAll("%20", " "));
    JMenuItem aide = new JMenuItem(I18N.getString("MainLabels.lblHelp"),
        iconeAide);
    aide.setActionCommand("help");
    aide.addActionListener(this);
    menuFichier.add(load);
    menuFichier.add(aide);
    // a ScaleMasterParameters menu
    JMenu menuParams = new JMenu("ScaleMasterParameters");
    JMenuItem paramsMenuItem = new JMenuItem("Edition");
    paramsMenuItem.setActionCommand("params");
    paramsMenuItem.addActionListener(this);
    JMenuItem paramsLoadMenuItem = new JMenuItem(
        I18N.getString("MainLabels.lblLoad"));
    paramsLoadMenuItem.setActionCommand("load-params");
    paramsLoadMenuItem.addActionListener(this);
    menuParams.add(paramsMenuItem);
    menuParams.add(paramsLoadMenuItem);
    // the menu bar
    menuBar.add(menuFichier);
    menuBar.add(menuParams);
    menuBar.setAlignmentX(Component.LEFT_ALIGNMENT);

    this.setJMenuBar(menuBar);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDefinition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pEdition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(scrollDisplay);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("ok")) {
      // apply and export to xml the current ScaleMaster
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File xmlFile = fc.getSelectedFile();
      try {
        this.saveToXml(xmlFile);
      } catch (TransformerException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      this.setVisible(false);
    } else if (e.getActionCommand().equals("params")) {
      EditPeaRepParamsFrame frame = new EditPeaRepParamsFrame();
      frame.setVisible(true);
    } else if (e.getActionCommand().equals("load-params")) {
      // load a file previously stored in xml
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File xmlFile = fc.getSelectedFile();
      EditPeaRepParamsFrame frame = new EditPeaRepParamsFrame(xmlFile);
      frame.setVisible(true);
    } else if (e.getActionCommand().equals("edit")) {
      // edit a ScaleMasterElement in the selected line
      if (this.selectedLine == null) {
        return;
      }
      SelectElementFrame frame = new SelectElementFrame(this);
      frame.setVisible(true);
      this.pack();
    } else if (e.getActionCommand().equals("add-line")) {
      AddScaleLineFrame frame = new AddScaleLineFrame(this);
      frame.setVisible(true);
    } else if (e.getActionCommand().equals("add-elt")) {
      if (this.selectedLine == null) {
        return;
      }

      AddScaleMasterEltFrame frame = new AddScaleMasterEltFrame(this,
          selectedLine);
      frame.setVisible(true);
      this.pack();
    } else if (e.getActionCommand().equals("help")) {
      // launch the frame documentation
      if (Desktop.isDesktopSupported()) {
        // On récupère l'instance du desktop :
        Desktop desktop = Desktop.getDesktop();

        // On vérifie que la fonction open est bien supportée :
        if (desktop.isSupported(Desktop.Action.OPEN)) {

          // Et on lance l'application associé au fichier pour
          // l'ouvrir :
          try {
            desktop.open(new File("help/aide_module_IGN_v2.chm"));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }

    } else if (e.getActionCommand().equals("load")) {
      // load a ScaleMaster previously stored in xml
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      fc.setName("Choose the ScaleMaster2.0 XML file to open");
      int returnVal = fc.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File xmlFile = fc.getSelectedFile();
      try {
        this.current = new ScaleMasterXMLParser(xmlFile)
            .parseScaleMaster(existingThemes);

        // now update the frame components
        this.txtName.setText(this.current.getName());
        this.spMin.setValue(this.current.getGlobalRange().getMinimum());
        this.spMax.setValue(this.current.getGlobalRange().getMaximum());
        this.cbPtOfView.setSelectedItem(this.current.getPointOfView());
        this.linePanels.clear();
        for (int i = 1; i < this.getDisplayPanel().getComponentCount(); i++)
          this.getDisplayPanel().remove(i);
        for (ScaleLine line : this.current.getScaleLines()) {
          List<ScaleMasterElement> elements = new ArrayList<ScaleMasterElement>();
          for (List<ScaleMasterElement> list : line.getLine().values())
            elements.addAll(list);
          line.getLine().clear();
          ScaleLineDisplayPanel linePanel = new ScaleLineDisplayPanel(line,
              ruler, this);
          linePanels.put(line.getTheme().getName(), linePanel);
          JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
          jp.add(linePanel);
          this.getDisplayPanel().add(jp);
          for (ScaleMasterElement elem : elements) {
            if (!CartAGenDoc.getInstance().containsSourceDLM(
                SourceDLM.valueOf(elem.getDbName()))) {
              if (!this.dbHues.containsKey(elem.getDbName())) {
                this.dbHues.put(elem.getDbName(), Color.BLUE);
                ((DefaultComboBoxModel) cbDbs.getModel()).addElement(elem
                    .getDbName());
              }
            }
            line.addElement(elem);
            linePanel.updateElements();
            this.pack();
          }
        }
        this.pack();
      } catch (DOMException e1) {
        e1.printStackTrace();
      } catch (ParserConfigurationException e1) {
        e1.printStackTrace();
      } catch (SAXException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      } catch (ClassNotFoundException e1) {
        e1.printStackTrace();
      }
    } else if (e.getActionCommand().equals("up")) {
      int index = this.current.getScaleLines().indexOf(selectedLine);
      this.current.getScaleLines().remove(index);
      this.current.getScaleLines().add(index - 1, selectedLine);
      Component comp = this.getDisplayPanel().getComponent(index + 1);
      this.getDisplayPanel().remove(comp);
      this.getDisplayPanel().add(comp, index);
      this.pack();
    } else if (e.getActionCommand().equals("down")) {
      int index = this.current.getScaleLines().indexOf(selectedLine);
      this.current.getScaleLines().remove(index);
      this.current.getScaleLines().add(index + 1, selectedLine);
      Component comp = this.getDisplayPanel().getComponent(index + 1);
      this.getDisplayPanel().remove(comp);
      this.getDisplayPanel().add(comp, index + 2);
      this.pack();
    } else if (e.getActionCommand().equals("top")) {
      int index = this.current.getScaleLines().indexOf(selectedLine);
      this.current.getScaleLines().remove(index);
      this.current.getScaleLines().add(0, selectedLine);
      Component comp = this.getDisplayPanel().getComponent(index + 1);
      this.getDisplayPanel().remove(comp);
      this.getDisplayPanel().add(comp, 1);
      this.pack();
    } else if (e.getActionCommand().equals("bottom")) {
      int index = this.current.getScaleLines().indexOf(selectedLine);
      this.current.getScaleLines().remove(index);
      this.current.getScaleLines().add(this.current.getScaleLines().size(),
          selectedLine);
      Component comp = this.getDisplayPanel().getComponent(index + 1);
      this.getDisplayPanel().remove(comp);
      this.getDisplayPanel().add(comp,
          this.getDisplayPanel().getComponentCount());
      this.pack();
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (e.getSource().equals(this.spMin)) {
      this.ruler.updateMinBound((Integer) this.spMin.getValue());
      this.pack();
    } else if (e.getSource().equals(this.spMax)) {
      this.ruler.updateMaxBound((Integer) this.spMax.getValue());
      this.pack();
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("background")) {
      this.dbHues.put((String) this.cbDbs.getSelectedItem(),
          (Color) evt.getNewValue());
    }
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource().equals(this.cbDbs)) {
      this.colorBtn.setBackground(this.dbHues.get(this.cbDbs.getSelectedItem()
          .toString()));
    }
  }

  public void setSelectedLine(ScaleLine selectedLine) {
    this.selectedLine = selectedLine;
  }

  public ScaleLine getSelectedLine() {
    return this.selectedLine;
  }

  public void setExistingThemes(Set<ScaleMasterTheme> existingThemes) {
    this.existingThemes = existingThemes;
  }

  public Set<ScaleMasterTheme> getExistingThemes() {
    return this.existingThemes;
  }

  public JPanel getDisplayPanel() {
    return this.pDisplay;
  }

  public ScaleRulerPanel getRuler() {
    return this.ruler;
  }

  public Map<String, Color> getDbHues() {
    return this.dbHues;
  }

  public void setDbHues(Map<String, Color> dbHues) {
    this.dbHues = dbHues;
  }

  public void setOntology(OWLOntology ontology) {
    this.ontology = ontology;
  }

  public OWLOntology getOntology() {
    return this.ontology;
  }

  public void setLinePanels(Map<String, ScaleLineDisplayPanel> linePanels) {
    this.linePanels = linePanels;
  }

  public Map<String, ScaleLineDisplayPanel> getLinePanels() {
    return this.linePanels;
  }

  public List<ScaleMasterGeneProcess> getGenProcs() {
    return genProcs;
  }

  public void setGenProcs(List<ScaleMasterGeneProcess> genProcs) {
    this.genProcs = genProcs;
  }

  /**
   * Reset all the selected toggle buttons of the scale lines except the new
   * selected one.
   * 
   * @param selected
   */
  void resetAllToggles(ScaleLineDisplayPanel selected) {
    for (String name : this.linePanels.keySet()) {
      if (name.equals(selected.getName())) {
        continue;
      }
      ScaleLineDisplayPanel panel = this.linePanels.get(name);
      panel.getToggle().setSelected(false);
      panel.setOpaque(false);
      panel.repaint();
    }
  }

  private void setGeoClassesJar() throws URISyntaxException {
    this.enrichProcs = new HashSet<ScaleMasterEnrichment>();
    this.genProcs = new ArrayList<ScaleMasterGeneProcess>();
    this.preProcs = new HashSet<ScaleMasterPreProcess>();

    String jarPath = PeaRepGeneralisation.class.getProtectionDomain()
        .getCodeSource().getLocation().toURI().getPath().substring(1);
    String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
    try {
      JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
      JarEntry jarEntry;
      while (true) {
        jarEntry = jarFile.getNextJarEntry();
        if (jarEntry == null) {
          break;
        }

        if (!(jarEntry.getName().endsWith(".class")))
          continue;
        if (!jarEntry.getName().contains("cartagen"))
          continue;
        if (jarEntry.getName().substring(0, jarEntry.getName().length() - 6)
            .equals("GothicObjectDiffusion")) {
          continue;
        }
        // Try to create an instance of the object
        Class<?> classObj = Class.forName(FileUtil
            .changeFileNameToClassName(jarEntry.getName()));

        if (classObj.isInterface()) {
          continue;
        }
        if (classObj.isLocalClass()) {
          continue;
        }
        if (classObj.isMemberClass()) {
          continue;
        }
        if (classObj.isEnum()) {
          continue;
        }
        if (Modifier.isAbstract(classObj.getModifiers())) {
          continue;
        }
        // test if it's an available generalisation process class
        if (ScaleMasterGeneProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterGeneProcess instance = (ScaleMasterGeneProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.genProcs.add(instance);
          continue;
        }

        // test if it's an enrichment process class
        if (ScaleMasterEnrichment.class.isAssignableFrom(classObj)) {
          ScaleMasterEnrichment instance = (ScaleMasterEnrichment) classObj
              .getMethod("getInstance").invoke(null);
          this.enrichProcs.add(instance);
          continue;
        }

        // test if it's a pre-process class
        if (ScaleMasterPreProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterPreProcess instance = (ScaleMasterPreProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.preProcs.add(instance);
          continue;
        }
      }
      jarFile.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    Collections.sort(this.genProcs);
  }

  /**
   * Get all the classes inheriting from IGeneObj in the project and set them as
   * the geo classes to be queried.
   */
  private void setGeoClasses() {
    this.enrichProcs = new HashSet<ScaleMasterEnrichment>();
    this.genProcs = new ArrayList<ScaleMasterGeneProcess>();
    this.preProcs = new HashSet<ScaleMasterPreProcess>();
    // get the directory of the package of this class
    Package pack = this.getClass().getPackage();
    String name = pack.getName();
    name = name.replace('.', '/');
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    URL pathName = this.getClass().getResource(name);
    File directory = new File(pathName.getFile());
    // get the parent directories to get fr.ign.cogit.cartagen package
    while (!directory.getName().equals("cartagen")) {
      directory = directory.getParentFile();
    }
    Collection<String> excluded = new HashSet<String>();
    excluded.add("gestionK");
    excluded.add("software");
    excluded.add("agentGeneralisation");
    excluded.add("internships");
    List<File> files = FileUtil.getAllFilesInDir(directory, excluded);
    for (File file : files) {
      if (!file.getName().endsWith(".class")) {
        continue;
      }
      if (file.getName().substring(0, file.getName().length() - 6)
          .equals("GothicObjectDiffusion")) {
        continue;
      }
      String path = file.getPath().substring(file.getPath().indexOf("fr"));
      String classname = FileUtil.changeFileNameToClassName(path);
      try {
        // Try to create an instance of the object
        Class<?> classObj = Class.forName(classname);
        if (classObj.isInterface()) {
          continue;
        }
        if (classObj.isLocalClass()) {
          continue;
        }
        if (classObj.isMemberClass()) {
          continue;
        }
        if (classObj.isEnum()) {
          continue;
        }
        if (Modifier.isAbstract(classObj.getModifiers())) {
          continue;
        }
        // test if it's an available generalisation process class
        if (ScaleMasterGeneProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterGeneProcess instance = (ScaleMasterGeneProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.genProcs.add(instance);
          continue;
        }

        // test if it's an enrichment process class
        if (ScaleMasterEnrichment.class.isAssignableFrom(classObj)) {
          ScaleMasterEnrichment instance = (ScaleMasterEnrichment) classObj
              .getMethod("getInstance").invoke(null);
          this.enrichProcs.add(instance);
          continue;
        }

        // test if it's a pre-process class
        if (ScaleMasterPreProcess.class.isAssignableFrom(classObj)) {
          ScaleMasterPreProcess instance = (ScaleMasterPreProcess) classObj
              .getMethod("getInstance").invoke(null);
          this.preProcs.add(instance);
          continue;
        }

      } catch (ClassNotFoundException cnfex) {
        cnfex.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      }
    }

    Collections.sort(this.genProcs);
  }

  /**
   * This methods internationalise all labels of the panel.
   */
  private void internationalisation() {
    this.frameTitle = I18N.getString("EditScaleMasterFrame.frameTitle");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblOk = I18N.getString("MainLabels.lblOk");
    this.lblAddLine = I18N.getString("EditScaleMasterFrame.lblAddLine");
    this.lblAddElement = I18N.getString("EditScaleMasterFrame.lblAddElement");
    this.lblEditElement = I18N.getString("EditScaleMasterFrame.lblEditElement");
    this.lblName = I18N.getString("EditScaleMasterFrame.lblName");
    this.lblPtOfView = I18N.getString("EditScaleMasterFrame.lblPtOfView");
    this.lblMin = I18N.getString("EditScaleMasterFrame.lblMin");
    this.lblMax = I18N.getString("EditScaleMasterFrame.lblMax");
    this.lblDbs = I18N.getString("EditScaleMasterFrame.lblDbs");
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    System.out.println(this.ruler.getX() + e.getX());
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  public void setCurrent(ScaleMaster current) {
    this.current = current;
  }

  public ScaleMaster getCurrent() {
    return this.current;
  }

  public JSpinner getSpMin() {
    return spMin;
  }

  public void setSpMin(JSpinner spMin) {
    this.spMin = spMin;
  }

  public JSpinner getSpMax() {
    return spMax;
  }

  public void setSpMax(JSpinner spMax) {
    this.spMax = spMax;
  }

  public Set<GeneObjImplementation> getImplementations() {
    return implementations;
  }

  public void setImplementations(Set<GeneObjImplementation> implementations) {
    this.implementations = implementations;
  }

  /**
   * Put the available enrichment processes in a combo box model.
   * 
   * @return
   */
  public ComboBoxModel getEnrichComboModel() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (ScaleMasterEnrichment enrich : this.enrichProcs) {
      model.addElement(enrich);
    }
    return model;
  }

  /**
   * Put the available generalisation processes in a combo box model.
   * 
   * @return
   */
  public ComboBoxModel getProcComboModel() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (ScaleMasterGeneProcess enrich : this.genProcs) {
      model.addElement(enrich);
    }
    return model;
  }

  /**
   * Initialise the {@link ScaleMasterTheme} objects known by {@code this}.
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private void initThemes() throws ParserConfigurationException, SAXException,
      IOException, ClassNotFoundException {
    // load a xml file in which existing themes have been stored
    JFileChooser fc = new JFileChooser();
    fc.setFileFilter(new XMLFileFilter());
    int returnVal = fc.showDialog(this, "Choose the themes XML file to open");
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File xmlFile = fc.getSelectedFile();
    // now parse the xml file
    initThemes(xmlFile);
  }

  /**
   * Initialise the {@link ScaleMasterTheme} objects known by {@code this}.
   * 
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  private void initThemes(File themesFile) throws ParserConfigurationException,
      SAXException, IOException, ClassNotFoundException {

    // now parse the xml file
    XMLParser parser = new XMLParser(themesFile);
    this.existingThemes.addAll(parser.parseExistingThemes());
  }

  /**
   * Save the current ScaleMaster described in {@code this} frame to XML.
   * 
   * @param file
   * @throws IOException
   * @throws TransformerException
   */
  private void saveToXml(File file) throws TransformerException, IOException {
    Node n = null;
    // ********************************************
    // CREATION DU DOCUMENT XML
    // Document (Xerces implementation only).
    DocumentImpl xmlDoc = new DocumentImpl();
    // Root element.
    Element root = xmlDoc.createElement("pearep-scalemaster");

    // WRITE GLOBAL INFO
    Element nameElem = xmlDoc.createElement("name");
    n = xmlDoc.createTextNode(this.txtName.getText());
    nameElem.appendChild(n);
    root.appendChild(nameElem);
    Element povElem = xmlDoc.createElement("point-of-view");
    n = xmlDoc.createTextNode(this.cbPtOfView.getSelectedItem().toString());
    povElem.appendChild(n);
    root.appendChild(povElem);
    Element globalElem = xmlDoc.createElement("global-range");
    root.appendChild(globalElem);
    Element globalMinElem = xmlDoc.createElement("interval-min");
    n = xmlDoc.createTextNode(String.valueOf(this.spMin.getValue()));
    globalMinElem.appendChild(n);
    globalElem.appendChild(globalMinElem);
    Element globalMaxElem = xmlDoc.createElement("interval-max");
    n = xmlDoc.createTextNode(String.valueOf(this.spMax.getValue()));
    globalMaxElem.appendChild(n);
    globalElem.appendChild(globalMaxElem);

    // WRITE A LINE
    for (ScaleLine line : current.getScaleLines()) {
      Element lineElem = xmlDoc.createElement("scale-line");
      lineElem.setAttribute("theme", line.getTheme().getName());
      root.appendChild(lineElem);
      List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>(line
          .getLine().keySet());
      Collections.sort(intervals);
      for (Interval<Integer> interval : intervals) {
        Element intervalElem = xmlDoc.createElement("scale-interval");
        lineElem.appendChild(intervalElem);
        Element minElem = xmlDoc.createElement("interval-min");
        n = xmlDoc.createTextNode(String.valueOf(interval.getMinimum()));
        minElem.appendChild(n);
        intervalElem.appendChild(minElem);
        Element maxElem = xmlDoc.createElement("interval-max");
        n = xmlDoc.createTextNode(String.valueOf(interval.getMaximum()));
        maxElem.appendChild(n);
        intervalElem.appendChild(maxElem);
        // get the element
        ScaleMasterElement elem = line.getLine().get(interval).get(0);
        Element dbNameElem = xmlDoc.createElement("db-name");
        n = xmlDoc.createTextNode(elem.getDbName());
        dbNameElem.appendChild(n);
        intervalElem.appendChild(dbNameElem);

        // the OGC filter for attribute selection
        if (elem.getOgcFilter() != null) {
          Element attrElem = xmlDoc.createElement("attribute-selection");
          attrElem.setAttribute("priority",
              String.valueOf(elem.getFilterPriority().ordinal()));
          intervalElem.appendChild(attrElem);
          if (elem.getOgcFilter() instanceof BinaryComparisonOpsType) {
            // simple case
            ScaleMasterXMLParser
                .writeSimpleFilter(
                    (BinaryComparisonOpsType) elem.getOgcFilter(), attrElem,
                    xmlDoc);
          } else if (elem.getOgcFilter() instanceof BinaryLogicOpsType) {
            // case with multiple queries
            ScaleMasterXMLParser.writeComplexFilter(
                (BinaryLogicOpsType) elem.getOgcFilter(), attrElem, xmlDoc);
          }
        }

        // the generalisation processes if exist
        if (elem.getProcessesToApply().size() > 0) {
          Element procsElem = xmlDoc.createElement("generalisation-processes");
          intervalElem.appendChild(procsElem);
          for (int i = 0; i < elem.getProcessesToApply().size(); i++) {
            String proc = elem.getProcessesToApply().get(i);
            Element procElem = xmlDoc.createElement("process");
            procElem.setAttribute("priority",
                String.valueOf(elem.getProcessPriorities().get(i).ordinal()));
            procsElem.appendChild(procElem);
            Element procNameElem = xmlDoc.createElement("name");
            n = xmlDoc.createTextNode(proc);
            procNameElem.appendChild(n);
            procElem.appendChild(procNameElem);
            if (elem.getParameters().get(i) != null) {
              Element paramsElem = xmlDoc.createElement("params");
              procElem.appendChild(paramsElem);
              for (String param : elem.getParameters().get(i).keySet()) {
                Element paramElem = xmlDoc.createElement("parameter");
                paramElem.setAttribute("name", param);
                Object value = elem.getParameters().get(i).get(param);
                paramElem
                    .setAttribute("type", value.getClass().getSimpleName());
                n = xmlDoc.createTextNode(value.toString());
                paramElem.appendChild(n);
                paramsElem.appendChild(paramElem);
              }
            }
          }
        }

        // the enrichments if exist
        if (elem.getEnrichments().size() > 0) {
          for (ScaleMasterEnrichment enrich : elem.getEnrichments()) {
            Element enrichElem = xmlDoc.createElement("enrichment");
            n = xmlDoc.createTextNode(enrich.getEnrichmentName());
            enrichElem.appendChild(n);
            intervalElem.appendChild(enrichElem);
          }
        }
      }
    }

    // ECRITURE DU FICHIER
    xmlDoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmlDoc, file);
  }

  public GeneObjImplementation getImplFromName(String name) {
    for (GeneObjImplementation impl : this.implementations) {
      if (impl.getName().equals(name))
        return impl;
    }
    return null;
  }

  public JButton getBtnUp() {
    return btnUp;
  }

  public JButton getBtnDown() {
    return btnDown;
  }

  public JButton getBtnTop() {
    return btnTop;
  }

  public JButton getBtnBottom() {
    return btnBottom;
  }

}
