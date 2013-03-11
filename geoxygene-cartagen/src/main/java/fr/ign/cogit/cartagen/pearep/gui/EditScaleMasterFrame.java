package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXColorSelectionButton;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.REPPointOfView;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterEnrichment;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.ScaleRulerPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.ClassComparator;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.cartagen.util.ontologies.OntologyUtil;

public class EditScaleMasterFrame extends JFrame implements ActionListener,
    ChangeListener, PropertyChangeListener, ItemListener, MouseListener {

  /****/
  private static final long serialVersionUID = 4975395667603478507L;

  private ScaleMaster current;
  private ScaleLine selectedLine;
  private OWLOntology ontology;
  private Set<ScaleMasterTheme> existingThemes = new HashSet<ScaleMasterTheme>();
  private List<Class<?>> geoClasses;
  private Map<String, Color> dbHues;
  private Set<ScaleMasterEnrichment> enrichProcs;
  private Set<ScaleMasterGeneProcess> genProcs;

  private JButton btnOk, btnCancel, btnApply, btnAddLine, btnAddElement,
      btnEditElement;
  private JTextField txtName;
  private JSpinner spMin, spMax;
  private JComboBox cbPtOfView, cbDbs;
  private JXColorSelectionButton colorBtn;
  private JPanel pDisplay;
  private ScaleRulerPanel ruler;
  private Map<String, ScaleLineDisplayPanel> linePanels = new HashMap<String, ScaleLineDisplayPanel>();

  // internationalisation labels
  private String frameTitle, lblCancel, lblOk, lblApply, lblAddLine,
      lblAddElement, lblEditElement, lblName, lblPtOfView, lblMin, lblMax,
      lblDbs;

  public EditScaleMasterFrame() throws OWLOntologyCreationException {
    super();
    this.internationalisation();
    this.setTitle(this.frameTitle);
    this.setIconImage(new ImageIcon(this.getClass().getResource(
        "/images/icons/logo.jpg")).getImage());
    this.setSize(600, 500);
    this.setMaximumSize(new Dimension(700, 600));
    this.setOntology(OntologyUtil
        .getOntologyFromName("MapGeneralisationProcesses"));
    this.setGeoClasses();
    this.dbHues = new HashMap<String, Color>();
    for (CartAGenDB db : CartAGenDoc.getInstance().getDatabases().values()) {
      this.dbHues.put(db.getName(), Color.RED);
    }
    this.current = new ScaleMaster();

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
    this.cbDbs = new JComboBox(CartAGenDoc.getInstance().getDatabases()
        .values().toArray());
    this.cbDbs.setPreferredSize(new Dimension(80, 20));
    this.cbDbs.setMinimumSize(new Dimension(80, 20));
    this.cbDbs.setMaximumSize(new Dimension(80, 20));
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
    pEdition.add(this.btnAddLine);
    pEdition.add(this.btnAddElement);
    pEdition.add(this.btnEditElement);
    pEdition.setLayout(new BoxLayout(pEdition, BoxLayout.X_AXIS));

    // a panel to display the scale master
    this.pDisplay = new JPanel();
    this.pDisplay.setPreferredSize(new Dimension(1200, 200));
    this.pDisplay.setMinimumSize(new Dimension(1200, 200));
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

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    this.btnOk = new JButton(this.lblOk);
    this.btnOk.addActionListener(this);
    this.btnOk.setActionCommand("ok");
    this.btnCancel = new JButton(this.lblCancel);
    this.btnCancel.addActionListener(this);
    this.btnCancel.setActionCommand("cancel");
    this.btnApply = new JButton(this.lblApply);
    this.btnApply.addActionListener(this);
    this.btnApply.setActionCommand("apply");
    pButtons.add(this.btnOk);
    pButtons.add(this.btnApply);
    pButtons.add(this.btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDefinition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pEdition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(new JScrollPane(this.pDisplay));
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
      System.out.println(this.getRuler().getPixelAlign(75000));
      // TODO
      this.setVisible(false);
    } else if (e.getActionCommand().equals("apply")) {
      // TODO
    } else if (e.getActionCommand().equals("edit")) {
      // TODO
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
      this.dbHues.put(((CartAGenDB) this.cbDbs.getSelectedItem()).getName(),
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

  public Set<ScaleMasterGeneProcess> getGenProcs() {
    return genProcs;
  }

  public void setGenProcs(Set<ScaleMasterGeneProcess> genProcs) {
    this.genProcs = genProcs;
  }

  /**
   * Reset all the selected toggle buttons of the scale lines except the new
   * selected one.
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
    }
  }

  DefaultListModel getGeoClassesModel() {
    DefaultListModel model = new DefaultListModel();
    for (Class<?> classObj : this.geoClasses) {
      model.addElement(classObj);
    }
    return model;
  }

  DefaultComboBoxModel getGeoClassesComboModel() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (Class<?> classObj : this.geoClasses) {
      model.addElement(classObj);
    }
    return model;
  }

  /**
   * Get all the classes inheriting from IGeneObj in the project and set them as
   * the geo classes to be queried.
   */
  private void setGeoClasses() {
    this.enrichProcs = new HashSet<ScaleMasterEnrichment>();
    this.genProcs = new HashSet<ScaleMasterGeneProcess>();
    this.geoClasses = new ArrayList<Class<?>>();
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
        }

        // test if it's an enrichment process class
        if (ScaleMasterEnrichment.class.isAssignableFrom(classObj)) {
          ScaleMasterEnrichment instance = (ScaleMasterEnrichment) classObj
              .getMethod("getInstance").invoke(null);
          this.enrichProcs.add(instance);
        }

        // test if the class inherits from IGeneObj
        if (!IGeneObj.class.isAssignableFrom(classObj))
          continue;

        // test if it is an implementation used in one of the opened DBs
        boolean implementation = false;
        for (CartAGenDB db : CartAGenDoc.getInstance().getDatabases().values()) {
          if (db.getGeneObjImpl().containsClass(classObj)) {
            implementation = true;
            break;
          }
        }
        if (!implementation)
          continue;

        this.geoClasses.add(classObj);

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
    Collections.sort(this.geoClasses, new ClassComparator());
  }

  /**
   * This methods internationalise all labels of the panel.
   */
  private void internationalisation() {
    this.frameTitle = I18N.getString("EditScaleMasterFrame.frameTitle");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblOk = I18N.getString("MainLabels.lblOk");
    this.lblApply = I18N.getString("MainLabels.lblApply");
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

  /**
   * Put the available enrichment processes in a combo box model.
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
   * @return
   */
  public ComboBoxModel getProcComboModel() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (ScaleMasterGeneProcess enrich : this.genProcs) {
      model.addElement(enrich);
    }
    return model;
  }

}
